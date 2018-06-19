package com.smartadserver.android.smartcmp.manager;

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

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.smartadserver.android.smartcmp.Constants;
import com.smartadserver.android.smartcmp.activity.ConsentToolActivity;
import com.smartadserver.android.smartcmp.consentstring.ConsentString;
import com.smartadserver.android.smartcmp.exception.UnknownVersionNumberException;
import com.smartadserver.android.smartcmp.model.ConsentToolConfiguration;
import com.smartadserver.android.smartcmp.model.Language;
import com.smartadserver.android.smartcmp.model.VendorList;
import com.smartadserver.android.smartcmp.vendorlist.VendorListManager;
import com.smartadserver.android.smartcmp.vendorlist.VendorListManagerListener;

import java.io.IOException;

/**
 * Singleton class that manages the GDPR user consent for the current device
 */

@SuppressWarnings("WeakerAccess")
public class ConsentManager implements VendorListManagerListener {

    // Default refresh interval in milliseconds (24 hours).
    static private final long DEFAULT_REFRESH_INTERVAL = 86400000;

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

    // The vendor list manager.
    private VendorListManager vendorListManager;

    // The last parsed vendor list.
    private VendorList lastVendorList;

    // The Language representation of the current device's language.
    private Language language;

    // Whether or not the consent tool should show if user has limited ad tracking from his device's settings.
    // If false and LAT is On, no consent will be given for any purpose or vendors.
    private boolean showConsentToolIfLAT;

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

            // if coming from a background state, we need to enable vendors list periodical refreshes
            if (wasBackground) {
                if (vendorListManager != null) {
                    vendorListManager.startAutomaticRefresh(false);
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
        configure(application, language, consentToolConfiguration, DEFAULT_LAT_VALUE, DEFAULT_REFRESH_INTERVAL);
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
        configure(application, language, consentToolConfiguration, showConsentToolWhenLimitedAdTracking, DEFAULT_REFRESH_INTERVAL);
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
     * @param refreshingInterval                   The interval in milliseconds te refresh the vendor list.
     */
    public void configure(@NonNull Application application, @NonNull Language language, @NonNull ConsentToolConfiguration consentToolConfiguration, boolean showConsentToolWhenLimitedAdTracking, long refreshingInterval) {
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

        // Check in preferences for already existing consent string.
        String rawConsentString = readStringFromSharedPreferences(Constants.IABConsentKeys.ConsentString, null);
        if (rawConsentString != null) {
            try {
                consentString = ConsentString.fromBase64String(rawConsentString);
            } catch (Exception ignored) {
            }
        }

        // Instantiate the VendorListManager and immediately trigger the automatic refresh.
        vendorListManager = new VendorListManager(this, refreshingInterval, DEFAULT_RETRY_INTERVAL, language);
        vendorListManager.startAutomaticRefresh(true);
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
     * @return The consent string.
     */
    @SuppressWarnings("unused")
    public ConsentString getConsentString() {
        return consentString;
    }

    /**
     * @return the last vendor list fetched.
     */
    public @Nullable VendorList getVendorList() {
        return lastVendorList;
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
     * Internal method to update the consent string. Will automatically store the consent string in the SharedPreferences.
     * Note: package private for test purpose.
     *
     * @param consentString The new consent string.
     */
    void setConsentString(ConsentString consentString) {
        this.consentString = consentString;

        if (consentString == null) {
            return;
        }

        // Store the consent string in the SharedPreferences.
        saveStringInSharedPreferences(Constants.IABConsentKeys.ConsentString, consentString.getConsentString());
        saveStringInSharedPreferences(Constants.IABConsentKeys.ParsedPurposeConsent, consentString.parsedPurposeConsents());
        saveStringInSharedPreferences(Constants.IABConsentKeys.ParsedVendorConsent, consentString.parsedVendorConsents());

        // Save the advertising consent status in the SharedPreferences.
        saveStringInSharedPreferences(Constants.AdvertisingConsentStatus.Key, consentString.isPurposeAllowed(Constants.AdvertisingConsentStatus.PurposeId) ? "1" : "0");
    }

    /**
     * Adds all purposes consents for the current consent string if it is already defined, create a new consent string with full consent otherwise.
     *
     * @return true if all purposes have been added correctly, false otherwise.
     */
    @SuppressWarnings("unused")
    public boolean allowAllPurposes() {
        if (lastVendorList == null || !isConfigured) {
            logErrorMessage("The ConsentManager must be configured and the vendor list downloaded before adding all purposes. Please try again later.");
            return false;
        }

        ConsentString consentString;

        if (this.consentString != null) {
            // The consent string is already set.
            consentString = ConsentString.consentStringByAddingAllPurposeConsents(lastVendorList, this.consentString);
        } else {
            // The consent string is not set yet, so we create a consent string with full consent.
            consentString = ConsentString.consentStringWithFullConsent(0, language, lastVendorList);
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
    @SuppressWarnings("unused")
    public boolean revokeAllPurposes() {
        if (lastVendorList == null || !isConfigured) {
            logErrorMessage("The ConsentManager must be configured and the vendor list downloaded before revoking all purposes. Please try again later.");
            return false;
        }

        ConsentString consentString;

        if (this.consentString != null) {
            // The consent string is already set.
            consentString = ConsentString.consentStringByRemovingAllPurposeConsents(lastVendorList, this.consentString);
        } else {
            // The consent string is not set yet.

            // First, create a consent string with full consent.
            consentString = ConsentString.consentStringWithFullConsent(0, language, lastVendorList);

            // Then remove all purpose consents.
            consentString = ConsentString.consentStringByRemovingAllPurposeConsents(lastVendorList, consentString);
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
     * Present the consent tool UI.
     *
     * @return Whether the consent tool UI has been displayed or not.
     */
    public boolean showConsentTool() {
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

        consentToolIsShown = true;

        // Start ConsentToolActivity
        Intent intent = new Intent(context, ConsentToolActivity.class);

        ConsentString consentString = this.consentString == null ? ConsentString.consentStringWithFullConsent(0, language, lastVendorList) : this.consentString;

        intent.putExtra("consent_string", consentString);
        intent.putExtra("vendor_list", lastVendorList);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

        return true;
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
     * Handle the reception of a new vendor list. Calling this method will either:
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
                    if (listener != null) {
                        // The listener is called so the publisher can ask for user's consent.
                        listener.onShowConsentToolRequest(consentString, lastVendorList);
                    } else {
                        // There is no listener so the CMP asked for user's consent automatically.
                        showConsentTool();
                    }
                } else {
                    // If 'Limited Ad Tracking' is enabled and the publisher doesn't want to handle it itself, a consent string with no
                    // consent (for all vendors / purposes) is generated and stored.
                    setConsentString(ConsentString.consentStringWithNoConsent(0, language, lastVendorList));
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
        Log.e("SmartCMP", message);
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
     * Save a string in the ShredPreferences.
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

                // Fetching the old vendor list to migrate the consent string.
                // Old purposes & vendors must keep their values, new one will be considered as accepted by default.
                vendorListManager.getVendorList(consentString.getVendorListVersion(), new VendorListManagerListener() {
                    @Override
                    public void onVendorListUpdateSuccess(@NonNull VendorList lastVendorList) {
                        consentString = ConsentString.consentStringFromUpdatedVendorList(vendorList, lastVendorList, consentString);
                        handleVendorListChanged();
                    }

                    @Override
                    public void onVendorListUpdateFail(@NonNull Exception e) {
                        // Unable to retrieve old vendor list version.
                        // Remove next refresh date to force the refresh in the next poll refresh.
                        vendorListManager.resetTimer();
                    }
                });
            }
        } else { // Consent string does not exist, ask for consent tool display.
            handleVendorListChanged();
        }
    }

    @Override
    public void onVendorListUpdateFail(@NonNull Exception e) {
        logErrorMessage("ConsentManager cannot retrieve vendors list because of an error \"" + e.getMessage() + "\". A new attempt will be made later.");
    }

}