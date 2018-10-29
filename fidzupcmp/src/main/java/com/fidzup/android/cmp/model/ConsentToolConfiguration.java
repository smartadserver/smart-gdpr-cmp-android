package com.fidzup.android.cmp.model;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

/**
 * The configuration class of ConsentTool UI.
 */

@SuppressWarnings("WeakerAccess")
public class ConsentToolConfiguration {

    // The application context. Needed to retrieve the strings.
    @NonNull
    private Context context;

    /////////////////////////////////////
    //// ConsentToolActivity strings ////
    /////////////////////////////////////

    // Drawable to display on the home screen.
    private int homeScreenLogoDrawableRes;

    // Text that will be displayed on the first controller of the consent tool.
    //
    // Eg: "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."
    private int homeScreenTextStringRes;

    // Text of the button to open consent management controller.
    //
    // Eg: "MANAGE MY CHOICES"
    private int homeScreenManageConsentButtonTitleStringRes;

    // Text of the button to close the consent tool directly.
    //
    // Eg: "GOT IT, THANKS!"
    private int homeScreenCloseButtonTitleStringRes;

    // Text of the button to close the consent tool directly.
    //
    // Eg: "GOT IT, BUT NO THANKS!"
    private int homeScreenCloseRefuseButtonTitleStringRes;

    ////////////////////////////////////////////////
    //// ConsentToolPreferencesActivity strings ////
    ////////////////////////////////////////////////

    // Text of the preferences app bar subtitle.
    //
    // Eg: "Privacy preferences".
    private int consentManagementScreenTitleStringRes;

    // Text of the button that saves consent choice.
    //
    // Eg: "Save"
    private int consentManagementSaveButtonTitleStringRes;

    // Text of the editor section header.
    //
    // Eg: "Editor"
    private int consentManagementScreenEditorSectionHeaderTextStringRes;

    // Text of the vendors section header.
    //
    // Eg: "Vendors"
    private int consentManagementScreenVendorsSectionHeaderTextStringRes;

    // Text of the purposes section header.
    //
    // Eg: "Purposes"
    private int consentManagementScreenPurposesSectionHeaderTextStringRes;

    // Text to access the full vendor list.
    //
    // Eg: "Authorized vendors"
    private int consentManagementVendorsActivityAccessTextStringRes;

    // Text to display when a purpose is activated.
    //
    // Eg: "yes"
    private int consentManagementActivatedTextStringRes;

    // Text to display when a purpose is deactivated.
    //
    // Eg: "no"
    private int consentManagementDeactivatedTextStringRes;

    /////////////////////////////////
    //// PurposeActivity strings ////
    /////////////////////////////////

    // Text of the purpose details app bar subtitle.
    //
    // Eg: "Purpose"
    private int consentManagementPurposeDetailTitleStringRes;

    // Text displayed next to the switch to allow (or disallow) a purpose.
    //
    // Eg: "Allowed"
    private int consentManagerPurposeDetailAllowTextStringRes;

    ////////////////////////////////////
    //// VendorListActivity strings ////
    ////////////////////////////////////

    // Text of the vendor list app bar subtitle.
    //
    // Eg: "Vendors list"
    private int consentManagementVendorsListTitleStringRes;

    ////////////////////////////////
    //// VendorActivity strings ////
    ////////////////////////////////

    // Text of the vendor detail app bar subtitle.
    //
    // Eg: "Vendor"
    private int consentManagementVendorDetailTitleStringRes;

    // Text of button that open the privacy policy.
    //
    // Eg: "VIEW PRIVACY POLICY"
    private int consentManagementVendorDetailViewPrivacyButtonTitleStringRes;

    // Text of the purposes section header.
    //
    // Eg: "Purposes"
    private int consentManagementVendorDetailPurposesSectionHeaderTextStringRes;

    // Text of the features section header.
    //
    // Eg: "Features"
    private int consentManagementVendorDetailFeaturesSectionHeaderTextStringRes;

    ///////////////////////////////////////
    //// PrivacyPolicyActivity strings ////
    ///////////////////////////////////////

    // Text of the privacy policy app bar subtitle.
    //
    // Eg: "Privacy policy"
    private int consentManagementPrivacyPolicyTitleStringRes;

    /////////////////////////////
    //// AlertDialog strings ////
    /////////////////////////////

    // Text of the alert dialog title.
    //
    // Eg: "Warning"
    private int consentManagementAlertDialogTitleStringRes;

    // Text of the alert dialog description.
    //
    // Eg: "By exiting the Consent Manager Provider you accept to give your personal data to all new purposes and vendors."
    private int consentManagementAlertDialogTextStringRes;

    // Text of the button that cancel the alert dialog.
    //
    // Eg: "Cancel"
    private int consentManagementAlertDialogNegativeButtonTitleStringRes;

    // Text of the button that accept the alert dialog consequences.
    //
    // Eg: "I accept"
    private int consentManagementAlertDialogPositiveButtonTitleStringRes;

    private String consentManagementDefaultEditorJsonURL;

    private String consentManagementLocalizedEditorJsonURL;

    private boolean isEditorConfigured = false;

    private boolean isEditorConfiguredWithURL;

    private String consentManagementEditorJson;

    private String defaultVendorListJson = null;

    private boolean isPubVendorConfigured = false;

    private String consentManagementDefaultPubVendorJsonURL;

    /**
     * Initialize a new instance of ConsentToolConfiguration.
     *
     * @param context                                                         The application context. Needed to retrieve the strings.
     * @param homeScreenLogoDrawableRes                                       Drawable to display on the home screen.
     * @param homeScreenTextStringRes                                         Text that will be displayed on the first controller of the consent tool.
     * @param homeScreenManageConsentButtonTitleStringRes                     Text of the button to open consent management controller.
     * @param homeScreenCloseButtonTitleStringRes                             Text of the button to accept and close the consent tool directly.
     * @param homeScreenCloseRefuseButtonTitleStringRes                       Text of the button to refuse and close the consent tool directly.
     * @param consentManagementScreenTitleStringRes                           Text of the preferences app bar subtitle.
     * @param consentManagementSaveButtonTitleStringRes                       Text of the button that saves consent choice.
     * @param consentManagementScreenEditorSectionHeaderTextStringRes         Text of the editor section header.
     * @param consentManagementScreenPurposesSectionHeaderTextStringRes       Text of the purposes section header.
     * @param consentManagementScreenVendorsSectionHeaderTextStringRes        Text of the vendors section header.
     * @param consentManagementVendorsActivityAccessTextStringRes             Text to access the full vendor list.
     * @param consentManagementActivatedTextStringRes                         Text to display when a purpose is activated.
     * @param consentManagementDeactivatedTextStringRes                       Text to display when a purpose is deactivated.
     * @param consentManagementPurposeDetailTitleStringRes                    Text of the purpose details app bar subtitle.
     * @param consentManagerPurposeDetailAllowTextStringRes                   Text displayed next to the switch to allow (or disallow) a purpose.
     * @param consentManagementVendorsListTitleStringRes                      Text of the vendor list app bar subtitle.
     * @param consentManagementVendorDetailTitleStringRes                     Text of the vendor detail app bar subtitle.
     * @param consentManagementVendorDetailViewPrivacyButtonTitleStringRes    Text of button that open the privacy policy.
     * @param consentManagementVendorDetailPurposesSectionHeaderTextStringRes Text of the purposes section header.
     * @param consentManagementVendorDetailFeaturesSectionHeaderTextStringRes Text of the features section header.
     * @param consentManagementPrivacyPolicyTitleStringRes                    Text of the privacy policy app bar subtitle.
     * @param consentManagementAlertDialogTitleStringRes                      Text of the alert dialog title.
     * @param consentManagementAlertDialogTextStringRes                       Text of the alert dialog description.
     * @param consentManagementAlertDialogNegativeButtonTitleStringRes        Text of the button that cancel the alert dialog.
     * @param consentManagementAlertDialogPositiveButtonTitleStringRes        Text of the button that accept the alert dialog consequences.
     */
    public ConsentToolConfiguration(@NonNull Context context,
                                    @DrawableRes int homeScreenLogoDrawableRes,
                                    @StringRes int homeScreenTextStringRes,
                                    @StringRes int homeScreenManageConsentButtonTitleStringRes,
                                    @StringRes int homeScreenCloseButtonTitleStringRes,
                                    @StringRes int homeScreenCloseRefuseButtonTitleStringRes,
                                    @StringRes int consentManagementScreenTitleStringRes,
                                    @StringRes int consentManagementSaveButtonTitleStringRes,
                                    @StringRes int consentManagementScreenEditorSectionHeaderTextStringRes,
                                    @StringRes int consentManagementScreenPurposesSectionHeaderTextStringRes,
                                    @StringRes int consentManagementScreenVendorsSectionHeaderTextStringRes,
                                    @StringRes int consentManagementVendorsActivityAccessTextStringRes,
                                    @StringRes int consentManagementActivatedTextStringRes,
                                    @StringRes int consentManagementDeactivatedTextStringRes,
                                    @StringRes int consentManagementPurposeDetailTitleStringRes,
                                    @StringRes int consentManagerPurposeDetailAllowTextStringRes,
                                    @StringRes int consentManagementVendorsListTitleStringRes,
                                    @StringRes int consentManagementVendorDetailTitleStringRes,
                                    @StringRes int consentManagementVendorDetailViewPrivacyButtonTitleStringRes,
                                    @StringRes int consentManagementVendorDetailPurposesSectionHeaderTextStringRes,
                                    @StringRes int consentManagementVendorDetailFeaturesSectionHeaderTextStringRes,
                                    @StringRes int consentManagementPrivacyPolicyTitleStringRes,
                                    @StringRes int consentManagementAlertDialogTitleStringRes,
                                    @StringRes int consentManagementAlertDialogTextStringRes,
                                    @StringRes int consentManagementAlertDialogNegativeButtonTitleStringRes,
                                    @StringRes int consentManagementAlertDialogPositiveButtonTitleStringRes) {

        this.context = context;
        this.homeScreenLogoDrawableRes = homeScreenLogoDrawableRes;
        this.homeScreenTextStringRes = homeScreenTextStringRes;
        this.homeScreenManageConsentButtonTitleStringRes = homeScreenManageConsentButtonTitleStringRes;
        this.homeScreenCloseButtonTitleStringRes = homeScreenCloseButtonTitleStringRes;
        this.homeScreenCloseRefuseButtonTitleStringRes = homeScreenCloseRefuseButtonTitleStringRes;
        this.consentManagementScreenTitleStringRes = consentManagementScreenTitleStringRes;
        this.consentManagementSaveButtonTitleStringRes = consentManagementSaveButtonTitleStringRes;
        this.consentManagementScreenEditorSectionHeaderTextStringRes = consentManagementScreenEditorSectionHeaderTextStringRes;
        this.consentManagementScreenPurposesSectionHeaderTextStringRes = consentManagementScreenPurposesSectionHeaderTextStringRes;
        this.consentManagementScreenVendorsSectionHeaderTextStringRes = consentManagementScreenVendorsSectionHeaderTextStringRes;
        this.consentManagementVendorsActivityAccessTextStringRes = consentManagementVendorsActivityAccessTextStringRes;
        this.consentManagementActivatedTextStringRes = consentManagementActivatedTextStringRes;
        this.consentManagementDeactivatedTextStringRes = consentManagementDeactivatedTextStringRes;
        this.consentManagementPurposeDetailTitleStringRes = consentManagementPurposeDetailTitleStringRes;
        this.consentManagerPurposeDetailAllowTextStringRes = consentManagerPurposeDetailAllowTextStringRes;
        this.consentManagementVendorsListTitleStringRes = consentManagementVendorsListTitleStringRes;
        this.consentManagementVendorDetailTitleStringRes = consentManagementVendorDetailTitleStringRes;
        this.consentManagementVendorDetailViewPrivacyButtonTitleStringRes = consentManagementVendorDetailViewPrivacyButtonTitleStringRes;
        this.consentManagementVendorDetailPurposesSectionHeaderTextStringRes = consentManagementVendorDetailPurposesSectionHeaderTextStringRes;
        this.consentManagementVendorDetailFeaturesSectionHeaderTextStringRes = consentManagementVendorDetailFeaturesSectionHeaderTextStringRes;
        this.consentManagementPrivacyPolicyTitleStringRes = consentManagementPrivacyPolicyTitleStringRes;
        this.consentManagementAlertDialogTitleStringRes = consentManagementAlertDialogTitleStringRes;
        this.consentManagementAlertDialogTextStringRes = consentManagementAlertDialogTextStringRes;
        this.consentManagementAlertDialogNegativeButtonTitleStringRes = consentManagementAlertDialogNegativeButtonTitleStringRes;
        this.consentManagementAlertDialogPositiveButtonTitleStringRes = consentManagementAlertDialogPositiveButtonTitleStringRes;
    }

    public ConsentToolConfiguration setEditorConfiguration(String consentManagementDefaultEditorJsonURL,
                                                           String consentManagementLocalizedEditorJsonURL) {
        this.isEditorConfigured = true;
        this.isEditorConfiguredWithURL = true;
        this.consentManagementDefaultEditorJsonURL   = consentManagementDefaultEditorJsonURL;
        this.consentManagementLocalizedEditorJsonURL = consentManagementLocalizedEditorJsonURL;

        return this;
    }

    public ConsentToolConfiguration setPubVendorConfiguration(String consentManagementDefaultPubVendorJsonURL) {
        this.isPubVendorConfigured = true;
        this.consentManagementDefaultPubVendorJsonURL   = consentManagementDefaultPubVendorJsonURL;

        return this;
    }

    public ConsentToolConfiguration setEditorConfiguration(String consentManagementEditorJson) {
        this.isEditorConfigured = true;
        this.isEditorConfiguredWithURL = false;
        this.consentManagementEditorJson = consentManagementEditorJson;

        return this;
    }

    public ConsentToolConfiguration setDefaultVendorListJson(String defaultVendorListJson) {
        this.defaultVendorListJson = defaultVendorListJson;
        return this;
    }

    /**
     * @return DrawableRes of the drawable to display on the home screen.
     */
    public @DrawableRes
    int getHomeScreenLogoDrawableRes() {
        return homeScreenLogoDrawableRes;
    }

    /**
     * @return Text that will be displayed on the first controller of the consent tool.
     */
    public String getHomeScreenText() {
        return context.getString(homeScreenTextStringRes);
    }

    /**
     * @return Text of the button to open consent management controller.
     */
    public String getHomeScreenManageConsentButtonTitle() {
        return context.getString(homeScreenManageConsentButtonTitleStringRes);
    }

    /**
     * @return Text of the button to accept and close the consent tool directly.
     */
    public String getHomeScreenCloseButtonTitle() {
        return context.getString(homeScreenCloseButtonTitleStringRes);
    }

    /**
     * @return Text of the button to refuse and close the consent tool directly.
     */
    public String getHomeScreenCloseRefuseButtonTitle() {
        return context.getString(homeScreenCloseRefuseButtonTitleStringRes);
    }

    /**
     * @return Text of the preferences app bar subtitle.
     */
    public String getConsentManagementScreenTitle() {
        return context.getString(consentManagementScreenTitleStringRes);
    }

    /**
     * @return Text of the button that saves consent choice.
     */
    public String getConsentManagementSaveButtonTitle() {
        return context.getString(consentManagementSaveButtonTitleStringRes);
    }

    /**
     * @return Text of the editor section header.
     */
    public String getConsentManagementScreenEditorSectionHeaderText() {
        return context.getString(consentManagementScreenEditorSectionHeaderTextStringRes);
    }

    /**
     * @return Text of the vendors section header.
     */
    public String getConsentManagementScreenVendorsSectionHeaderText() {
        return context.getString(consentManagementScreenVendorsSectionHeaderTextStringRes);
    }

    /**
     * @return Text of the purposes section header.
     */
    public String getConsentManagementScreenPurposesSectionHeaderText() {
        return context.getString(consentManagementScreenPurposesSectionHeaderTextStringRes);
    }

    /**
     * @return Text to access the full vendor list.
     */
    public String getConsentManagementVendorListActivityAccessText() {
        return context.getString(consentManagementVendorsActivityAccessTextStringRes);
    }

    /**
     * @return Text to display when a purpose is activated.
     */
    public String getConsentManagementActivatedText() {
        return context.getString(consentManagementActivatedTextStringRes);
    }

    /**
     * @return Text to display when a purpose is deactivated.
     */
    public String getConsentManagementDeactivatedText() {
        return context.getString(consentManagementDeactivatedTextStringRes);
    }

    /**
     * @return Text of the purpose details app bar subtitle.
     */
    public String getConsentManagementPurposeDetailTitle() {
        return context.getString(consentManagementPurposeDetailTitleStringRes);
    }

    /**
     * @return Text displayed next to the switch to allow (or disallow) a purpose.
     */
    public String getConsentManagerPurposeDetailAllowText() {
        return context.getString(consentManagerPurposeDetailAllowTextStringRes);
    }

    /**
     * @return Text of the vendor list app bar subtitle.
     */
    public String getConsentManagementVendorsListTitle() {
        return context.getString(consentManagementVendorsListTitleStringRes);
    }

    /**
     * @return Text of the vendor detail app bar subtitle.
     */
    public String getConsentManagementVendorDetailTitle() {
        return context.getString(consentManagementVendorDetailTitleStringRes);
    }

    /**
     * @return Text of button that open the privacy policy.
     */
    public String getConsentManagementVendorDetailViewPrivacyButtonTitle() {
        return context.getString(consentManagementVendorDetailViewPrivacyButtonTitleStringRes);
    }

    /**
     * @return Text of the purposes section header.
     */
    public String getConsentManagementVendorDetailPurposesSectionHeaderText() {
        return context.getString(consentManagementVendorDetailPurposesSectionHeaderTextStringRes);
    }

    /**
     * @return Text of the features section header.
     */
    public String getConsentManagementVendorDetailFeaturesSectionHeaderText() {
        return context.getString(consentManagementVendorDetailFeaturesSectionHeaderTextStringRes);
    }

    /**
     * @return Text of the privacy policy app bar subtitle.
     */
    public String getConsentManagementPrivacyPolicyTitle() {
        return context.getString(consentManagementPrivacyPolicyTitleStringRes);
    }

    /**
     * @return Text of the alert dialog title.
     */
    public String getConsentManagementAlertDialogTitle() {
        return context.getString(consentManagementAlertDialogTitleStringRes);
    }

    /**
     * @return Text of the alert dialog description.
     */
    public String getConsentManagementAlertDialogText() {
        return context.getString(consentManagementAlertDialogTextStringRes);
    }

    /**
     * @return Text of the button that cancel the alert dialog.
     */
    public String getConsentManagementAlertDialogNegativeButtonTitle() {
        return context.getString(consentManagementAlertDialogNegativeButtonTitleStringRes);
    }

    /**
     * @return Text of the button that accept the alert dialog consequences.
     */
    public String getConsentManagementAlertDialogPositiveButtonTitle() {
        return context.getString(consentManagementAlertDialogPositiveButtonTitleStringRes);
    }

    /**
     * @return The base json editor url, where to fetch the editor json configuration
     */
    public String getConsentManagementDefaultEditorJsonURL() {
        return this.consentManagementDefaultEditorJsonURL;
    }

    /**
     * @return true if editor si configured, false otherwise
     */
    public boolean isEditorConfigured() { return this.isEditorConfigured; }

    /**
     * @return true if editor URL are configured, false otherwise
     */
    public boolean isEditorConfiguredWithURL() { return this.isEditorConfiguredWithURL; }

    /**
     * @return The localized json editor url, where to fetch the localized editor json configuration
     */
    public String getConsentManagementLocalizedEditorJsonURL() {
        return this.consentManagementLocalizedEditorJsonURL;
    }

    /**
     * @return The json editor string
     */
    public String getConsentManagementEditorJson() {
        return this.consentManagementEditorJson;
    }

    /**
     * @return The defaut vendor list json string
     */
    public String getDefaultVendorListJson() {
        return this.defaultVendorListJson;
    }

    /**
     * @return true if pubvendor.json management si configured, false otherwise
     */
    public boolean isPubVendorConfigured() { return this.isPubVendorConfigured; }

    /**
     * @return The pubvendor json url string
     */
    public String getConsentManagementDefaultPubVendorJsonURL() {
        return this.consentManagementDefaultPubVendorJsonURL;
    }
}
