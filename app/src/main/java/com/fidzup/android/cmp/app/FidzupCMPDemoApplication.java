package com.fidzup.android.cmp.app;

import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.fidzup.android.cmp.consentstring.ConsentString;
import com.fidzup.android.cmp.manager.ConsentManager;
import com.fidzup.android.cmp.manager.ConsentManagerListener;
import com.fidzup.android.cmp.model.ConsentToolConfiguration;
import com.fidzup.android.cmp.model.Language;
import com.fidzup.android.cmp.model.VendorList;
import com.fidzup.android.cmp.model.Editor;

import java.util.Locale;


public class FidzupCMPDemoApplication extends MultiDexApplication implements ConsentManagerListener {
    @Override
    public void onCreate() {
        super.onCreate();
        // Find current language for CMP configuration.
        String rawLanguage = Locale.getDefault().getLanguage();

        // Become the listener of ConsentManager shared instance to know when the user should be asked for consent.
        //
        // This is not mandatory. If no listener is found, the ConsentManager will pop every time it is needed, whatever the user
        // is doing. Implementing this listener is useful if you want to control when the consent tool will be launched (for better
        // user experience) or if you want to provide your own UI instead of using FidzupCMP consent tool.
        ConsentManager.getSharedInstance().setConsentManagerListener(this);

        // Configure the ConsentManager shared instance.
        Language language;
        try {
            language = new Language(rawLanguage);
        } catch (IllegalArgumentException e) {
            language = Language.getDefaultLanguage();
        }
        ConsentManager.getSharedInstance().configure(this, language, generatedConsentToolConfiguration());
    }

    private ConsentToolConfiguration generatedConsentToolConfiguration() {
        ConsentToolConfiguration consentToolConfiguration = new ConsentToolConfiguration(this,
                R.drawable.logo_fidzup,
                R.string.cmp_home_screen_text,
                R.string.cmp_home_screen_manage_consent_button_title,
                R.string.cmp_home_screen_close_button_title,
                R.string.cmp_home_screen_close_refuse_button_title,
                R.string.cmp_consent_tool_preferences_appbar_subtitle,
                R.string.cmp_consent_tool_preferences_save_button_title,
                R.string.cmp_consent_tool_preferences_editor_section_header,
                R.string.cmp_consent_tool_preferences_purposes_section_header,
                R.string.cmp_consent_tool_preferences_vendors_section_header,
                R.string.cmp_consent_tool_preferences_vendor_list_access_cell_text,
                R.string.cmp_consent_tool_preferences_allowed_purpose_text,
                R.string.cmp_consent_tool_preferences_disallowed_purpose_text,
                R.string.cmp_purpose_detail_appbar_subtitle,
                R.string.cmp_purpose_detail_allowed_text,
                R.string.cmp_vendors_list_appbar_subtitle,
                R.string.cmp_vendor_detail_appbar_subtitle,
                R.string.cmp_vendor_detail_privacy_policy_button_title,
                R.string.cmp_vendor_detail_purposes_section_header,
                R.string.cmp_vendor_detail_features_section_header,
                R.string.cmp_privacy_policy_appbar_subtitle,
                R.string.cmp_alert_dialog_title,
                R.string.cmp_alert_dialog_description,
                R.string.cmp_alert_dialog_negative_button_title,
                R.string.cmp_alert_dialog_positive_button_title);
        consentToolConfiguration.setEditorConfiguration("https://www.fidzup.com/editor/editor.json","https://www.fidzup.com/editor/editor-{language}.json");
        consentToolConfiguration.setPubVendorConfiguration("https://www.fidzup.com/editor/pubvendor.json");
        String vendorListJson = "{\"vendorListVersion\":113,\"lastUpdated\":\"2018-10-22T16:00:19Z\",\"purposes\":[{\"id\":1,\"name\":\"Information storage and access\",\"description\":\"The storage of information, or access to information that is already stored, on your device such as advertising identifiers, device identifiers, cookies, and similar technologies.\"},{\"id\":2,\"name\":\"Personalisation\",\"description\":\"The collection and processing of information about your use of this service to subsequently personalise advertising and/or content for you in other contexts, such as on other websites or apps, over time. Typically, the content of the site or app is used to make inferences about your interests, which inform future selection of advertising and/or content.\"},{\"id\":3,\"name\":\"Ad selection, delivery, reporting\",\"description\":\"The collection of information, and combination with previously collected information, to select and deliver advertisements for you, and to measure the delivery and effectiveness of such advertisements. This includes using previously collected information about your interests to select ads, processing data about what advertisements were shown, how often they were shown, when and where they were shown, and whether you took any action related to the advertisement, including for example clicking an ad or making a purchase. This does not include personalisation, which is the collection and processing of information about your use of this service to subsequently personalise advertising and/or content for you in other contexts, such as websites or apps, over time.\"},{\"id\":4,\"name\":\"Content selection, delivery, reporting\",\"description\":\"The collection of information, and combination with previously collected information, to select and deliver content for you, and to measure the delivery and effectiveness of such content. This includes using previously collected information about your interests to select content, processing data about what content was shown, how often or how long it was shown, when and where it was shown, and whether the you took any action related to the content, including for example clicking on content. This does not include personalisation, which is the collection and processing of information about your use of this service to subsequently personalise content and/or advertising for you in other contexts, such as websites or apps, over time.\"},{\"id\":5,\"name\":\"Measurement\",\"description\":\"The collection of information about your use of the content, and combination with previously collected information, used to measure, understand, and report on your usage of the service. This does not include personalisation, the collection of information about your use of this service to subsequently personalise content and/or advertising for you in other contexts, i.e. on other service, such as websites or apps, over time.\"},{\"id\":6,\"name\":\"Geolocalized ads\",\"description\":\"We use your GPS, wifi or some bluetooth informations to target you and offer you better ads near your favorite shops\"}],\"features\":[{\"id\":1,\"name\":\"Matching Data to Offline Sources\",\"description\":\"Combining data from offline sources that were initially collected in other contexts.\"},{\"id\":2,\"name\":\"Linking Devices\",\"description\":\"Allow processing of a user's data to connect such user across multiple devices.\"},{\"id\":3,\"name\":\"Precise Geographic Location Data\",\"description\":\"Allow processing of a user's precise geographic location data in support of a purpose for which that certain third party has consent.\"}],\"vendors\":[{\"id\":45,\"name\":\"Smart Adserver\",\"policyUrl\":\"http://smartadserver.com/company/privacy-policy/\",\"purposeIds\":[1,2],\"legIntPurposeIds\":[3,5],\"featureIds\":[3]},{\"id\":529,\"name\":\"Fidzup\",\"policyUrl\":\"https://www.fidzup.com/en/privacy/\",\"purposeIds\":[1,2,3,4,5,6],\"legIntPurposeIds\":[],\"featureIds\":[1,2,3]}]}";
        consentToolConfiguration.setDefaultVendorListJson(vendorListJson);
        return consentToolConfiguration;
    }

    @Override
    public void onShowConsentToolRequest(ConsentString consentString, VendorList vendorList, Editor editor) {
        Log.i("FidzupCMPDemoAppli", "CMP requested ConsentTool display.");

        // You should display the consent tool UI, when user is ready…
        ConsentManager.getSharedInstance().showConsentTool();

        // Since the vendor list is provided in parameter of this listener callback, you can also build your own UI to ask for
        // user consent and simply save the resulting consent string in the relevant IAB keys (see the IAB specification for
        // more details about this).
        //
        // To generate a valid IAB consent string easily, you can use the ConsentString class.

        // ---------------------------------------------------------------------------------------------------------------------

        // Note: depending on the situation, you might also want to allow or revoke all purposes consents without showing
        // the consent tool. You can do it using the acceptAllPurposes() and revokeAllPurposes() methods.

        // Allow all purposes consents without prompting the user, for instance if the user is not subject to GDPR (when he
        // is living outside of the EU).
        // ConsentManager.getSharedInstance().allowAllPurposes();

        // Revoke all purposes consents without prompting the user, for instance if the user is under 16 years old (or younger
        // depending on the country where the user is located).
        // ConsentManager.getSharedInstance().revokeAllPurposes();
    }
}
