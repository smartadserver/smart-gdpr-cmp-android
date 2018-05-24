# SmartCMP for Android

## Introduction

_SmartCMP for Android_ is an Android SDK allowing you to retrieve and store the user's consent for data usage in your Android apps.

The purposes & vendors retrieval as well as the consent storage is compliant with [IAB Transparency and Consent Framework specifications](https://github.com/InteractiveAdvertisingBureau/GDPR-Transparency-and-Consent-Framework).

Retrieving user consent is mandatory in EU starting May 25th due to the _General Data Protection Regulation (GDPR)_.

<p align="center">
  <img src="images/android-consent-tool.gif" alt="Consent tool on Android"/>
</p>

## Usage

### Installation

#### Using Gradle (recommended)

1. In the main `build.gradle` of your project, declare the Smart repository
    
        allprojects {
            repositories {
                // add the Smart repository
                maven { url 'https://packagecloud.io/smartadserver/android/maven2' }

                // …
            }
        }

2. In the `build.gradle` file corresponding to your application module, you can now import the `SmartCMP` by declaring it in the _dependencies_ section

        dependencies {
            // …

            // add SmartCMP
            implementation 'com.smartadserver.android:smartcmp:3@aar'
        }

#### From the Git repository

Download this repository then add the `SmartCMP` module to your project through the _Project Structure_ menu.

### Integration

You must setup the CMP before using it. Start by creating a configuration object that will define how the first screen of the consent tool will look like:

    ConsentToolConfiguration consentToolConfiguration = new ConsentToolConfiguration(getApplicationContext(),
                R.drawable.logo_smart,
                R.string.cmp_home_screen_text,
                R.string.cmp_home_screen_manage_consent_button_title,
                R.string.cmp_home_screen_close_button_title,
                R.string.cmp_consent_tool_preferences_appbar_subtitle,
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

Call the `configure()` method on `ConsentManager.getSharedInstance()` to start the CMP. A good place to configure the _SmartCMP_ is in the `Application` class of your app, in the `onCreate` method. Indeed, your `Application` instance is needed by the CMP: it will listen to your application entering background or foreground to avoid unnecessary network calls.

    public class MyApplication extends Application {
        @Override
        public void onCreate() {
            super.onCreate();

            ConsentToolConfiguration consentToolConfiguration = // create your own ConsentToolConfiguration

            // Configure the SmartCMP
            ConsentManager.getSharedInstance().configure(this, language, consentToolConfiguration);
        }
    }

When the CMP is started, it will automatically fetch the most recent vendors list _(vendors.json)_ and prompt the user for consent if necessary, saving the resulting consent string in Android _SharedPreferences_ (according to the IAB specifications).

You might want to control when the user will be prompted for consent for better user experience. In this case, define a listener for the ```ConsentManager```:

    ConsentManager.getSharedInstance().setConsentManagerListener(this);

When retrieval of the user consent is required, the `onShowConsentToolRequest(ConsentString consentString, VendorList vendorList)`
 method will be called on the listener and it will be the app's responsability to display the consent tool UI when appropriate.

    @Override
    public void onShowConsentToolRequest(ConsentString consentString, VendorList vendorList) {
        // It is necessary to update the user consent using the consent tool or your own UI
    }

Showing the consent tool is done using the method `showConsentTool()`. Note that this method can be used not only when it has been requested by the consent manager, but also anytime you want to provide a way for the user to change its consent options.

    ConsentManager.getSharedInstance().showConsentTool();

## 'Limited Ad Tracking' behavior

On Android, the user can opt out for any tracking related to advertisement by enabling 'Limited Ad Tracking' in the OS settings. By default, the CMP does not handle this option and let the app developer choose how he wants to proceed if it has been enabled by the user. **Please note that not handling the 'Limited Ad Tracking' option properly is a violation of Google Play Store's terms & conditions. Your app might be removed from the store.**

However, if you configure the CMP with the parameter `showConsentToolWhenLimitedAdTracking` set to _false_, it will handle the 'Limited Ad Tracking' option automatically. In case of limited ad tracking the CMP will not display the consent tool and will not call the delegate, but will instead store a consent string with no consent given for any purposes or vendors.

## Known limitations

The current version of _SmartCMP_ has the following limitations:

* The consent tool UI is not customizable (except for static texts). You can however build your own UI and display it in the `consentManagerRequestsToShowConsentTool` listener method using the `vendorList` and the `consentString` parameters.
* _AndroidTV_ apps are not supported.
* The IAB specification allows publishers to display only a subset of purposes & vendors using a _pubvendors.json_ file, stored on their own infrastructure. _SmartCMP_ does not implement this feature at this time.
* No static texts are provided by default (you must provide them to `ConsentToolConfiguration`). The `homeScreenText` should be validated by your legal department.
* _SmartCMP_ does not have any logic to know if GDPR applies or not based on user's location / age at this time. For the moment it is the publisher's responsibility to determine whether or not GDPR applies and if the consent tool UI should be shown to the user, as well as requesting permission to fetch location or other including / excluding criteria.

## License

### Code source licensing

This software is distributed under the _Creative Commons Legal Code, Attribution 3.0 Unported_ license.

Check the [LICENSE file](LICENSE) for more details.

### Reusing SmartCMP ID

The CMP ID _'33'_ used for consent string encoding is the CMP ID of _Smart AdServer_.

You can use this CMP ID as long as you don't alter the source code of _SmartCMP_. If you do modify the source code, **YOU MUST REGISTER YOUR FORK AS A NEW CMP and change the CMP ID** in `Constants.CMPInfos.ID`. You can register your forked CMP and obtain your own ID here: https://register.consensu.org/CMP
