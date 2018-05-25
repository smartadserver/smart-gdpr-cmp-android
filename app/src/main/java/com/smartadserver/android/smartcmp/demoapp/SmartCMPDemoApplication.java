package com.smartadserver.android.smartcmp.demoapp;

import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.smartadserver.android.smartcmp.consentstring.ConsentString;
import com.smartadserver.android.smartcmp.manager.ConsentManager;
import com.smartadserver.android.smartcmp.manager.ConsentManagerListener;
import com.smartadserver.android.smartcmp.model.ConsentToolConfiguration;
import com.smartadserver.android.smartcmp.model.Language;
import com.smartadserver.android.smartcmp.model.VendorList;

import java.util.Locale;


public class SmartCMPDemoApplication extends MultiDexApplication implements ConsentManagerListener {
    @Override
    public void onCreate() {
        super.onCreate();
        // Find current language for CMP configuration.
        String rawLanguage = Locale.getDefault().getLanguage();

        // Become the listener of ConsentManager shared instance to know when the user should be asked for consent.
        //
        // This is not mandatory. If no listener is found, the ConsentManager will pop every time it is needed, whatever the user
        // is doing. Implementing this listener is useful if you want to control when the consent tool will be launched (for better
        // user experience) or if you want to provide your own UI instead of using SmartCMP consent tool.
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
        return new ConsentToolConfiguration(this,
                R.drawable.logo_smart,
                R.string.cmp_home_screen_text,
                R.string.cmp_home_screen_manage_consent_button_title,
                R.string.cmp_home_screen_close_button_title,
                R.string.cmp_consent_tool_preferences_appbar_subtitle,
                R.string.cmp_consent_tool_preferences_cancel_button_title,
                R.string.cmp_consent_tool_preferences_save_button_title,
                R.string.cmp_consent_tool_preferences_vendors_section_header,
                R.string.cmp_consent_tool_preferences_purposes_section_header,
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
    }

    @Override
    public void onShowConsentToolRequest(ConsentString consentString, VendorList vendorList) {
        Log.i("SmartCMPDemoApp", "CMP requested ConsentTool display.");

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
