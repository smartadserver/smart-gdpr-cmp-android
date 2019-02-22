package com.fidzup.android.cmp.manager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.fidzup.android.cmp.activity.ConsentActivity;
import com.fidzup.android.cmp.activity.main.MainConsentActivity;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.fidzup.android.cmp.Constants;
import com.fidzup.android.cmp.consentstring.ConsentString;
import com.fidzup.android.cmp.model.ConsentToolConfiguration;
import com.fidzup.android.cmp.model.Language;
import com.fidzup.android.cmp.model.VendorList;
import com.fidzup.android.cmp.vendorlist.VendorListManager;
import com.fidzup.android.cmp.vendorlist.VendorListManagerListener;
import com.fidzup.android.cmp.model.Editor;
import com.fidzup.android.cmp.editor.EditorManager;
import com.fidzup.android.cmp.editor.EditorManagerListener;

import java.io.IOException;
import java.util.Date;

/**
 * Singleton class that manages the GDPR user consent for the current device
 */

@SuppressWarnings("WeakerAccess")
public class ConsentManager implements VendorListManagerListener,EditorManagerListener {

    // The key used to store the next ui display date in the Shared Preferences.
    static private final String LAST_UI_DISPLAY_DATE_KEY = "FidzupCMP_LastUiDisplayDate";

    // Default interval between each consent tool UI automatic display in milliseconds (7 days).
    static private final long DEFAULT_UI_DISPLAY_INTERVAL = 604800000;

    // Default refresh interval in milliseconds (1 hour).
    static private final long DEFAULT_REFRESH_INTERVAL = 36000000;

    // Default retry interval (needed after an unsuccessful refresh) in milliseconds (1 minute).
    static private final long DEFAULT_RETRY_INTERVAL = 60000;

    // The default behavior if LAT (Limited Ad Tracking) is enabled.
    static private final boolean DEFAULT_LAT_VALUE = true;

    // The instance of the CMPManager singleton.
    @SuppressLint("StaticFieldLeak")
    static private final ConsentManager sharedInstance = new ConsentManager();

    // Whether or not the ConsentManager has been configured by the publisher.
    private boolean isConfigured = false;

    // The application context.
    private Context context;

    // The consent tool configuration needed for all strings used in the UI.
    private ConsentToolConfiguration consentToolConfiguration;

    // The ConsentManager listener.
    private ConsentManagerListener listener;

    // Boolean that define if the user is subject to GDPR or not. That attribute must be define as
    // soon as the publisher knows whether of not the user is subject to GDPR law, for example after looking up
    // the user's location, or if the publisher himself is subject to this regulation.
    // False by default.
    private boolean subjectToGDPR = false;

    // The consent string.
    private ConsentString consentString;

    // The editor manager.
    private EditorManager editorManager;

    // The editor used by the current consent string.
    private Editor usedEditor;

    // The last parsed editor.
    private Editor lastEditor;

    // The vendor list manager.
    private VendorListManager vendorListManager;

    // The vendor list used by the current consent string.
    private VendorList usedVendorList;

    // The last parsed vendor list.
    private VendorList lastVendorList;

    // The Language representation of the current device's language.
    private Language language;

    // Whether or not the consent tool should show if user has limited ad tracking from his device's settings.
    // If false and LAT is On, no consent will be given for any purpose or vendors.
    private boolean showConsentToolIfLAT;

    // Interval (in milliseconds) between each consent tool UI display.
    private long uiDisplayInterval;

    // Whether or not the consent tool is shown.
    private boolean consentToolIsShown = false;

    /**
     * Internal class to detect the Application state in order to stop watching/downloading task
     * when the Application is in background
     */
    private class ApplicationLifecycleListener implements Application.ActivityLifecycleCallbacks {

        // delay on activity lifecycle callbacks before actually checking background state
        public static final long BACKGROUND_DETECTION_DELAY = 1000;

        // if the application is in background
        private boolean isBackground = false;

        // flag to mark that the latest callback was from a paused activity
        private boolean activityWasPaused = false;

        // the Runnable to perform the background check
        private Runnable checkIfBackgroundRunnable = null;

        // a Handler to execute the above Runnable
        private Handler handler = new Handler();

        @Override
        public void onActivityResumed(Activity activity) {

            // update flags
            activityWasPaused = false;
            boolean wasBackground = isBackground;
            isBackground = false;

            // as application is not in background anymore, cancel any pending background check
            if (checkIfBackgroundRunnable != null) {
                handler.removeCallbacks(checkIfBackgroundRunnable);
            }

            // if coming from a background state, we need to enable vendors list & editor periodical refreshes
            if (wasBackground) {
                if (vendorListManager != null) {
                    vendorListManager.startAutomaticRefresh(false);
                }
                if (editorManager != null && consentToolConfiguration.isEditorConfiguredWithURL()) {
                    editorManager.startAutomaticRefresh(false);
                }
            }
        }

        @Override
        public void onActivityPaused(Activity activity) {

            // update flag
            activityWasPaused = true;

            // need to schedule another check, so cancel any pending Runnable
            if (checkIfBackgroundRunnable != null) {
                handler.removeCallbacks(checkIfBackgroundRunnable);
            }

            // instantiate new check Runnable
            checkIfBackgroundRunnable = new Runnable() {
                @Override
                public void run() {
                    if (!isBackground && activityWasPaused) {
                        // this means that the application really is paused (no active Activity since this check was fired
                        isBackground = true;
                        // suspend vendor list refreshes
                        if (vendorListManager != null) {
                            vendorListManager.stopAutomaticRefresh();
                        }
                        // suspend editor refreshes
                        if (editorManager != null && consentToolConfiguration.isEditorConfiguredWithURL()) {
                            editorManager.stopAutomaticRefresh();
                        }
                    }
                }
            };

            handler.postDelayed(checkIfBackgroundRunnable, BACKGROUND_DETECTION_DELAY);
        }

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        }

        @Override
        public void onActivityStarted(Activity activity) {
        }

        @Override
        public void onActivityStopped(Activity activity) {
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
        }
    }

    /**
     * Private constructor to enforce singleton.
     */
    private ConsentManager() {

    }

    /**
     * @return the singleton instance.
     */
    public static ConsentManager getSharedInstance() {
        return sharedInstance;
    }

    /**
     * Configure the ConsentManager. This method should be called only once per session.
     *
     * @param application              The instance of the application.
     * @param language                 An instance of Language reflecting the device's current language.
     * @param consentToolConfiguration An instance of ConsentToolConfiguration containing all the strings needed by the UI.
     */
    @SuppressWarnings("SameParameterValue")
    public void configure(@NonNull Application application, @NonNull Language language, @NonNull ConsentToolConfiguration consentToolConfiguration) {
        configure(application, language, consentToolConfiguration, DEFAULT_LAT_VALUE, DEFAULT_UI_DISPLAY_INTERVAL);
    }

    /**
     * Configure the ConsentManager. This method should be called only once per session.
     * <p>
     * Note: if you set 'showConsentToolWhenLimitedAdTracking' to true, you will be able to ask for user consent even if
     * 'Limited Ad Tracking' has been enabled on the device. In this case, remember that you still have to comply to Google's Play store
     * Terms and Conditions regarding 'Limited Ad Tracking'.
     *
     * @param application                          The instance of the application.
     * @param language                             An instance of Language reflecting the device's current language.
     * @param consentToolConfiguration             An instance of ConsentToolConfiguration containing all the strings needed by the UI.
     * @param showConsentToolWhenLimitedAdTracking Whether or not the consent tool UI should be shown if the user has checked Limit Ad Tracking in his device's preferences. If false, the UI will never be shown if user checked LAT and consent string will be formatted has "user does not give consent".
     */
    @SuppressWarnings("unused")
    public void configure(@NonNull Application application, @NonNull Language language, @NonNull ConsentToolConfiguration consentToolConfiguration, boolean showConsentToolWhenLimitedAdTracking) {
        configure(application, language, consentToolConfiguration, showConsentToolWhenLimitedAdTracking, DEFAULT_UI_DISPLAY_INTERVAL);
    }

    /**
     * Configure the ConsentManager. This method should be called only once per session.
     * <p>
     * Note: if you set 'showConsentToolWhenLimitedAdTracking' to true, you will be able to ask for user consent even if
     * 'Limited Ad Tracking' has been enabled on the device. In this case, remember that you still have to comply to Google's Play store
     * Terms and Conditions regarding 'Limited Ad Tracking'.
     *
     * @param application                          The instance of the application.
     * @param language                             An instance of Language reflecting the device's current language.
     * @param consentToolConfiguration             An instance of ConsentToolConfiguration containing all the strings needed by the UI.
     * @param showConsentToolWhenLimitedAdTracking Whether or not the consent tool UI should be shown if the user has checked Limit Ad Tracking in his device's preferences. If false, the UI will never be shown if user checked LAT and consent string will be formatted has "user does not give consent".
     * @param uiDisplayInterval                    The interval in milliseconds between each CMP UI display.
     */
    public void configure(@NonNull Application application, @NonNull Language language, @NonNull ConsentToolConfiguration consentToolConfiguration, boolean showConsentToolWhenLimitedAdTracking, long uiDisplayInterval) {
        if (isConfigured) {
            logErrorMessage("ConsentManager is already configured for this session. You cannot reconfigure.");
            return;
        }

        isConfigured = true;

        this.context = application.getApplicationContext();

        // register an ActivityLifecycleCallbacks on the application
        application.registerActivityLifecycleCallbacks(new ApplicationLifecycleListener());

        this.consentToolConfiguration = consentToolConfiguration;

        this.language = language;
        this.showConsentToolIfLAT = showConsentToolWhenLimitedAdTracking;
        this.uiDisplayInterval = uiDisplayInterval;

        // Check in preferences for already existing consent string.
        String rawConsentString = readStringFromSharedPreferences(Constants.FidzupCMPConsentKeys.ConsentString, null);
        if (rawConsentString != null) {
            try {
                consentString = ConsentString.fromBase64String(rawConsentString);
            } catch (Exception ignored) {
            }
        }

        // Instantiate the VendorListManager and immediately trigger the automatic refresh.
        vendorListManager = new VendorListManager(this, DEFAULT_REFRESH_INTERVAL, DEFAULT_RETRY_INTERVAL, language);
        if (this.consentToolConfiguration.getDefaultVendorListJson() != null) {
            // Force the first list load from the default json
            vendorListManager.loadInitialVendorListFromJson(this.consentToolConfiguration.getDefaultVendorListJson());
        }
        if (this.consentToolConfiguration.isPubVendorConfigured()) {
            vendorListManager.setSubVendorListURL(this.consentToolConfiguration.getConsentManagementDefaultPubVendorJsonURL());
        }
        vendorListManager.startAutomaticRefresh(true);
        if (this.consentToolConfiguration.isEditorConfigured()) {
            editorManager = new EditorManager(this, DEFAULT_REFRESH_INTERVAL, DEFAULT_RETRY_INTERVAL, language);
            if (this.consentToolConfiguration.isEditorConfiguredWithURL()) {
                // Instantiate the EditorManager and immediately trigger the automatic refresh.
                editorManager.setEditorURLs(this.consentToolConfiguration.getConsentManagementDefaultEditorJsonURL(), this.consentToolConfiguration.getConsentManagementLocalizedEditorJsonURL());
                editorManager.startAutomaticRefresh(true);
            } else {
                editorManager.setEditorJSON(this.consentToolConfiguration.getConsentManagementEditorJson());
                editorManager.refreshEditorFromJson();
            }
        }
    }

    /**
     * @return the consent tool configuration.
     */
    public ConsentToolConfiguration getConsentToolConfiguration() {
        return consentToolConfiguration;
    }

    /**
     * Set a new ConsentManagerListener.
     */
    public void setConsentManagerListener(ConsentManagerListener listener) {
        this.listener = listener;
    }

    /**
     * Set a new Context. This method is package private for test purpose.
     *
     * @param context the new context to set.
     */
    void setContext(Context context) {
        this.context = context;
    }

    /**
     * @return Whether the user is subject to GDPR.
     */
    @SuppressWarnings("unused")
    public boolean isSubjectToGDPR() {
        return subjectToGDPR;
    }

    /**
     * Set a new subjectToGDPR value. Will automatically store this value in the SharedPreferences.
     *
     * @param subjectToGDPR Whether or not the user is subject to GDPR.
     */
    @SuppressWarnings({"unused", "SameParameterValue"})
    public void setSubjectToGDPR(boolean subjectToGDPR) {
        this.subjectToGDPR = subjectToGDPR;

        // Save subjectToGDPR status to SharedPreferences
        saveStringInSharedPreferences(Constants.IABConsentKeys.SubjectToGDPR, subjectToGDPR ? "1" : "0");
    }

    /**
     * Note: Package private for test purpose.
     * @return The consent string.
     */
    @SuppressWarnings("unused")
    ConsentString getConsentString() {
        return consentString;
    }

    /**
     * @return the last vendor list fetched.
     */
    public @Nullable VendorList getVendorList() {
        return lastVendorList;
    }


    /**
     * @return the last editor fetched.
     */
    public @Nullable Editor getEditor() {
        return lastEditor;
    }

    /**
     * Check if the consent tool can be presented.
     *
     * Note: the consent tool cannot be displayed if:
     *  - you haven't called the configure() method first
     *  - the consent tool is already displayed
     *  - the vendor list has not been retrieved yet (or can't be retrieved for the moment)
     *
     * @return true if presenting the consent tool with the showConsentTool() method will be successful, false otherwise.
     */
    @SuppressWarnings("unused")
    public boolean canShowConsentTool() {
        return isConfigured && !consentToolIsShown && lastVendorList != null;
    }

    /**
     * Update the ConsentString using the given Base64URL encoded consent string.
     *
     * @param base64URLEncodedConsentString The base64URL encoded consent string.
     */
    public void setConsentString(@NonNull String base64URLEncodedConsentString) {
        try {
            setConsentString(ConsentString.fromBase64String(base64URLEncodedConsentString));
        } catch (Exception ignored) {
        }
    }

    /**
     * Method to update the consent string. Will automatically store the consent string in the SharedPreferences.
     *
     * @param consentString The new consent string.
     */
    public void setConsentString(ConsentString consentString) {
        this.consentString = consentString;

        if (consentString == null) {
            return;
        }

        //Confirm that an IAB CMP is present
        saveIntegerInSharedPreferences(Constants.IABConsentKeys.CMPPresent, 1);

        // Store the consent string in the SharedPreferences.
        saveStringInSharedPreferences(Constants.IABConsentKeys.ConsentString, consentString.getIABConsentString());
        saveStringInSharedPreferences(Constants.IABConsentKeys.ParsedPurposeConsent, consentString.parsedPurposeConsents());
        saveStringInSharedPreferences(Constants.IABConsentKeys.ParsedVendorConsent, consentString.parsedVendorConsents());

        // Store the consent string in the SharedPreferences.
        saveStringInSharedPreferences(Constants.FidzupCMPConsentKeys.ConsentString, consentString.getConsentString());

        // Store the editor purposes consent string in the SharedPreferences.
        saveStringInSharedPreferences(Constants.FidzupCMPConsentKeys.ParsedEditorPurposeConsent, consentString.parsedEditorPurposeConsents());

        // Save the advertising consent status in the SharedPreferences.
        saveStringInSharedPreferences(Constants.FidzupCMPConsentKeys.AdvertisingConsentStatus, consentString.isPurposeAllowed(Constants.FidzupCMPConsentKeys.PurposeId) ? "1" : "0");
    }

    /**
     * Adds all purposes consents for the current consent string if it is already defined, create a new consent string with full consent otherwise.
     *
     * @return true if all purposes have been added correctly, false otherwise.
     */
    @SuppressWarnings("UnusedReturnValue")
    public boolean allowAllPurposes() {
        if (lastVendorList == null || !isConfigured) {
            logErrorMessage("The ConsentManager must be configured and the vendor list downloaded before adding all purposes. Please try again later.");
            return false;
        }

        ConsentString consentString;

        if (this.consentString != null) {
            // The consent string is already set.
            consentString = ConsentString.consentStringByAddingAllPurposeConsents(lastVendorList, lastEditor, this.consentString);
        } else {
            // The consent string is not set yet, so we create a consent string with full consent.
            consentString = ConsentString.consentStringWithFullConsent(0, language, lastEditor, lastVendorList);
        }

        if (consentString == null) {
            // something went wrong, return false without saving anything.
            return false;
        }

        // Set the new consent string.
        setConsentString(consentString);

        return true;
    }

    /**
     * Removes all purposes consents for the current consent string if it is already defined, create a new consent string with full vendors consent and no purposes consent otherwise.
     *
     * @return true if all purposes have been removed correctly, false otherwise.
     */
    @SuppressWarnings("UnusedReturnValue")
    public boolean revokeAllPurposes() {
        if (lastVendorList == null || !isConfigured) {
            logErrorMessage("The ConsentManager must be configured and the vendor list downloaded before revoking all purposes. Please try again later.");
            return false;
        }

        ConsentString consentString;

        if (this.consentString != null) {
            // The consent string is already set.
            consentString = ConsentString.consentStringByRemovingAllPurposeConsents(lastVendorList, lastEditor, this.consentString);
        } else {
            // The consent string is not set yet.

            // First, create a consent string with full consent.
            consentString = ConsentString.consentStringWithFullConsent(0, language, lastEditor, lastVendorList);

            // Then remove all purpose consents.
            consentString = ConsentString.consentStringByRemovingAllPurposeConsents(lastVendorList, lastEditor, consentString);
        }

        if (consentString == null) {
            // something went wrong, return false without saving anything.
            return false;
        }

        // set the new consent string.
        setConsentString(consentString);

        return true;
    }

    /**
     * Force an immediate refresh of the vendor list.
     */
    @SuppressWarnings("unused")
    public void refreshVendorList() {
        if (!isConfigured) {
            logErrorMessage("ConsentManager is not configured for this session. Please call ConsentManager.getSharedInstance().configure() first.");
            return;
        }

        vendorListManager.refreshVendorList();
    }

    /**
     * Force an immediate refresh of the editor.
     */
    @SuppressWarnings("unused")
    public void refreshEditor() {
        if (!isConfigured) {
            logErrorMessage("ConsentManager is not configured for this session. Please call ConsentManager.getSharedInstance().configure() first.");
            return;
        }

        editorManager.refreshEditor();
    }

    /**
     * Present the consent tool UI (settings page).
     * Deprecated : use showConsentTool()
     *
     * @return Whether the consent tool UI has been displayed or not.
     */
    @Deprecated
    public boolean showConsentToolSettings() {
        return showConsentTool();
    }

    /**
     * Present the consent tool UI (main page).
     *
     * @return Whether the consent tool UI has been displayed or not.
     */
    public boolean showConsentTool() {
        return _showConsentTool(MainConsentActivity.class);
    }

    private boolean _showConsentTool(Class<? extends ConsentActivity> actcivityClass) {
        if (!isConfigured) {
            logErrorMessage("ConsentManager is not configured for this session. Please call ConsentManager.getSharedInstance().configure() first.");
            return false;
        }

        if (consentToolIsShown) {
            logErrorMessage("ConsentManager is already showing the consent tool UI.");
            return false;
        }

        if (lastVendorList == null) {
            logErrorMessage("ConsentManager cannot show consent tool as no vendor list is available. Please wait.");
            return false;
        }

        if (lastEditor == null && consentToolConfiguration.isEditorConfigured()) {
            logErrorMessage("ConsentManager cannot show consent tool as no editor is available. Please wait.");
            return false;
        }

        consentToolIsShown = true;

        migrateConsentStringIfNeeded();
        migrateConsentStringForEditorIfNeeded();
        ConsentString consentString = this.consentString == null ? ConsentString.consentStringWithFullConsent(0, language, lastEditor, lastVendorList) : this.consentString;

        Intent intent = ConsentActivity.getIntentForConsentActivity(context,
                actcivityClass,
                consentString,
                lastVendorList,
                lastEditor);

        intent.putExtra(ConsentActivity.EXTRA_ISROOTCONSENTACTIVITY, true);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

        return true;
    }

    /**
     * Called by the consent UI when it close.
     */
    public void consentToolClosed() {
        consentToolIsShown = false;
    }

    /**
     * Called by the consent tool UI when it close.
     *
     * @param consentString The Base64URL encoded consent string to store.
     */
    public void consentToolClosedWithConsentString(@NonNull String consentString) {
        consentToolIsShown = false;
        setConsentString(consentString);
    }

    /**
     * Migrate the consent string from the used vendor list to the last vendor list.
     */
    private void migrateConsentStringIfNeeded() {
        if (consentString != null && usedVendorList != null && lastVendorList != null
                && consentString.getVendorListVersion() != lastVendorList.getVersion()
                && usedVendorList.getVersion() == consentString.getVendorListVersion()) {
            // Update the consent string with the last vendor list.
            consentString = ConsentString.consentStringFromUpdatedVendorList(lastVendorList, usedVendorList, lastEditor, consentString);
        }
    }

    /**
     * Migrate the consent string from the used editor to the last editor.
     */
    private void migrateConsentStringForEditorIfNeeded() {
        if (consentString != null && usedEditor != null && lastEditor != null
                && consentString.getEditorVersion() != lastEditor.getVersion()
                && usedEditor.getVersion() == consentString.getEditorVersion()) {
            // Update the consent string with the last vendor list.
            consentString = ConsentString.consentStringFromUpdatedEditor(lastVendorList, usedEditor, lastEditor, consentString);
        }
    }

    /**
     * Handle the reception of a new editor. Calling this method will either:
     * - show the consent tool manager UI (if we don't have any listener set).
     * - call the listener with the new editor.
     * - generate a consent string without any consent if 'limited ad tracking' is enabled and the CMP is configured to handle it itself.
     */
    private void handleEditorChanged() {

        // Fetching the 'Limited Ad Tracking' status must be done in a background thread. Making it in the main
        // thread will lead to an IllegalStateException.
        new Thread(new Runnable() {
            @Override
            public void run() {

                boolean isLATEnable;

                try {
                    // Checking the 'Limited Ad Tracking' status of the device.
                    AdvertisingIdClient.Info adsInfo = AdvertisingIdClient.getAdvertisingIdInfo(context);
                    isLATEnable = adsInfo.isLimitAdTrackingEnabled();

                } catch (GooglePlayServicesNotAvailableException e) {
                    // Google play services are not available, so the device does not have those services. Therefore we can consider the 'Limited Ad Tracking' is disabled.
                    isLATEnable = false;

                } catch (GooglePlayServicesRepairableException e) {
                    // Google play services are not reachable. Therefore we can consider the 'Limited Ad Tracking' is disabled.
                    isLATEnable = false;

                } catch (IOException e) {
                    // Unable to retrieve the Google Play Service. Cancel the refresh timer to retry as soon as possible.
                    editorManager.resetTimer();
                    return;
                }


                // If the 'Limited Ad Tracking' is disable on the device, or if the 'Limited Ad Tracking' is enable but the publisher
                // wants to handle the display himself...
                if (!isLATEnable || showConsentToolIfLAT) {

                    migrateConsentStringForEditorIfNeeded();

                    if (listener != null) {
                        // The listener is called so the publisher can ask for user's consent.
                        listener.onShowConsentToolRequest(consentString, lastVendorList, lastEditor);
                    } else {
                        // There is no listener so the CMP asked for user's consent automatically.
                        showConsentTool();
                    }

                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                    SharedPreferences.Editor leditor = prefs.edit();
                    leditor.putLong(LAST_UI_DISPLAY_DATE_KEY, new Date().getTime());
                    leditor.apply();

                } else {
                    // If 'Limited Ad Tracking' is enabled and the publisher doesn't want to handle it itself, a consent string with no
                    // consent (for all vendors / purposes) is generated and stored.
                    setConsentString(ConsentString.consentStringWithNoConsent(0, language, lastEditor, lastVendorList));
                }
            }
        }).start();
    }

    /**
     * Handle the reception of a new vendor list or editor. Calling this method will either:
     * - show the consent tool manager UI (if we don't have any listener set).
     * - call the listener with the new vendor list.
     * - generate a consent string without any consent if 'limited ad tracking' is enabled and the CMP is configured to handle it itself.
     */
    private void handleVendorListChanged() {

        // Fetching the 'Limited Ad Tracking' status must be done in a background thread. Making it in the main
        // thread will lead to an IllegalStateException.
        new Thread(new Runnable() {
            @Override
            public void run() {

                boolean isLATEnable;

                try {
                    // Checking the 'Limited Ad Tracking' status of the device.
                    AdvertisingIdClient.Info adsInfo = AdvertisingIdClient.getAdvertisingIdInfo(context);
                    isLATEnable = adsInfo.isLimitAdTrackingEnabled();

                } catch (GooglePlayServicesNotAvailableException e) {
                    // Google play services are not available, so the device does not have those services. Therefore we can consider the 'Limited Ad Tracking' is disabled.
                    isLATEnable = false;

                } catch (GooglePlayServicesRepairableException e) {
                    // Google play services are not reachable. Therefore we can consider the 'Limited Ad Tracking' is disabled.
                    isLATEnable = false;

                } catch (IOException e) {
                    // Unable to retrieve the Google Play Service. Cancel the refresh timer to retry as soon as possible.
                    vendorListManager.resetTimer();
                    return;
                }


                // If the 'Limited Ad Tracking' is disable on the device, or if the 'Limited Ad Tracking' is enable but the publisher
                // wants to handle the display himself...
                if (!isLATEnable || showConsentToolIfLAT) {

                    migrateConsentStringIfNeeded();

                    if (listener != null) {
                        // The listener is called so the publisher can ask for user's consent.
                        listener.onShowConsentToolRequest(consentString, lastVendorList, lastEditor);
                    } else {
                        // There is no listener so the CMP asked for user's consent automatically.
                        showConsentTool();
                    }

                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putLong(LAST_UI_DISPLAY_DATE_KEY, new Date().getTime());
                    editor.apply();

                } else {
                    // If 'Limited Ad Tracking' is enabled and the publisher doesn't want to handle it itself, a consent string with no
                    // consent (for all vendors / purposes) is generated and stored.
                    setConsentString(ConsentString.consentStringWithNoConsent(0, language, lastEditor, lastVendorList));
                }
            }
        }).start();
    }

    /**
     * Log an error message in console.
     *
     * @param message The message that will be logged.
     */
    private void logErrorMessage(String message) {
        Log.e("FidzupCMP", message);
    }

    /**
     * Read a string from the SharedPreferences.
     *
     * @param key          The key in SharedPreferences where the string is possibly saved.
     * @param defaultValue The default value if the string is not retrieved.
     * @return the key stored with the given key, or the default value if no string has been found.
     */
    @SuppressWarnings("SameParameterValue")
    private String readStringFromSharedPreferences(@NonNull String key, String defaultValue) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(key, defaultValue);
    }

    /**
     * Save a string in the SharedPreferences.
     *
     * @param key    The key in SharedPreferences where the string will be saved.
     * @param string The string that needs to be saved.
     */
    private void saveStringInSharedPreferences(@NonNull String key, @NonNull String string) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, string);
        editor.apply();
    }

    /**
     * Save an integer in the SharedPreferences.
     *
     * @param key    The key in SharedPreferences where the string will be saved.
     * @param integer The integer that needs to be saved.
     */
    private void saveIntegerInSharedPreferences(@NonNull String key, @NonNull Integer integer) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(key, integer);
        editor.apply();
    }

    ///////////////////////////////////////////
    //// VendorListListener implementation ////
    ///////////////////////////////////////////

    @Override
    public void onVendorListUpdateSuccess(@NonNull final VendorList vendorList) {
        lastVendorList = vendorList;

        // If consent string exist
        if (consentString != null) {

            // If consent string has a different version than vendor list, ask for consent tool display
            if (consentString.getVendorListVersion() != lastVendorList.getVersion()) {

                // Retrieve the lastDisplayUIDate from the shared preferences.
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                long nextUIDisplayDate = prefs.getLong(LAST_UI_DISPLAY_DATE_KEY, 0) + uiDisplayInterval;
                Date currentDate = new Date();

                // If the nextUIDisplayDate is reached, then we show to consent tool or call the listener.
                final boolean shouldShowConsentTool = currentDate.getTime() > nextUIDisplayDate;

                // If we do not have the vendor list used by the current consent string, we try to download it
                if (usedVendorList == null || consentString.getVendorListVersion() != usedVendorList.getVersion()) {

                    // Fetching the old vendor list to migrate the consent string.
                    // Old purposes & vendors must keep their values, new one will be considered as accepted by default.
                    vendorListManager.getVendorList(consentString.getVendorListVersion(), new VendorListManagerListener() {
                        @Override
                        public void onVendorListUpdateSuccess(@NonNull VendorList previousVendorList) {
                            usedVendorList = previousVendorList;

                            if (shouldShowConsentTool) {
                                handleVendorListChanged();
                            }
                        }

                        @Override
                        public void onVendorListUpdateFail(@NonNull Exception e) {
                            // Unable to retrieve old vendor list version.
                            // Remove next refresh date to force the refresh in the next poll refresh.
                            vendorListManager.resetTimer();
                        }
                    });
                } else {

                    // We already have the vendor list used by the consent string, so we show the consent tool if needed.
                    if (shouldShowConsentTool) {
                        handleVendorListChanged();
                    }
                }
            }
        } else { // Consent string does not exist, ask for consent tool display.
            handleVendorListChanged();
        }
    }

    @Override
    public void onVendorListUpdateFail(@NonNull Exception e) {
        logErrorMessage("ConsentManager cannot retrieve vendors list because of an error \"" + e.getMessage() + "\". A new attempt will be made later.");
    }

    ///////////////////////////////////////
    //// EditorListener implementation ////
    ///////////////////////////////////////

    @Override
    public void onEditorUpdateSuccess(@NonNull final Editor editor) {
        lastEditor = editor;

        // If consent string exist
        if (consentString != null) {

            // If consent string has a different version than editor, ask for consent tool display
            if (consentString.getEditorVersion() != lastEditor.getVersion()) {

                // Retrieve the lastDisplayUIDate from the shared preferences.
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                long nextUIDisplayDate = prefs.getLong(LAST_UI_DISPLAY_DATE_KEY, 0) + uiDisplayInterval;
                Date currentDate = new Date();

                // If the nextUIDisplayDate is reached, then we show to consent tool or call the listener.
                final boolean shouldShowConsentTool = currentDate.getTime() > nextUIDisplayDate;

                // If we do not have the vendor list used by the current consent string, we try to download it
                if (usedEditor == null || consentString.getEditorVersion() != usedEditor.getVersion()) {

                    // Fetching the old vendor list to migrate the consent string.
                    // Old purposes & vendors must keep their values, new one will be considered as accepted by default.
                    editorManager.getEditor(consentString.getEditorVersion(), new EditorManagerListener() {
                        @Override
                        public void onEditorUpdateSuccess(@NonNull Editor previousEditor) {
                            usedEditor = previousEditor;

                            if (shouldShowConsentTool) {
                                handleEditorChanged();
                            }
                        }

                        @Override
                        public void onEditorUpdateFail(@NonNull Exception e) {
                            // Unable to retrieve old editor version.
                            // Remove next refresh date to force the refresh in the next poll refresh.
                            editorManager.resetTimer();
                        }
                    });
                } else {

                    // We already have the vendor list used by the consent string, so we show the consent tool if needed.
                    if (shouldShowConsentTool) {
                        handleEditorChanged();
                    }
                }
            }
        } else { // Consent string does not exist, ask for consent tool display.
            handleEditorChanged();
        }
    }

    @Override
    public void onEditorUpdateFail(@NonNull Exception e) {
        logErrorMessage("ConsentManager cannot retrieve editor because of an error \"" + e.getMessage() + "\". A new attempt will be made later.");
    }

}
