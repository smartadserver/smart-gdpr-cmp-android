package com.smartadserver.android.smartcmp;

/**
 * General constants for SmartCMP.
 */

public class Constants {

    // Generic information on the CMP sdk.
    public class CMPInfos {

        // '33' IS THE OFFICIAL CMP ID FOR SmartCMP.
        // You can use this ID as long as you don't change the source code of this project.
        // If you d'ont use it exactly as distributed in the official Smart AdServer repository,
        // you must get your own CMP ID by registering here: https://register.consensu.org/CMP
        public static final int ID = 33;
        public static final int VERSION = 2;
    }

    // IAB Keys for SharedPreferences storage.
    @SuppressWarnings("unused")
    public class IABConsentKeys {
        public static final String CMPPresent                     = "IABConsent_CMPPresent";
        public static final String SubjectToGDPR                  = "IABConsent_SubjectToGDPR";
        public static final String ConsentString                  = "IABConsent_ConsentString";
        public static final String ParsedPurposeConsent           = "IABConsent_ParsedPurposeConsent";
        public static final String ParsedVendorConsent            = "IABConsent_ParsedVendorConsent";
    }

    // The AdvertisingConsentStatus SharedPreferences key contains the current user consent for the advertising
    // purpose of the current vendor list.
    //
    // This status is only based on the answer of the user for the advertising purpose and does not take
    // vendors status into account. It should be used for third party advertisement SDK that are not
    // IAB TCF compliant.
    //
    // Note: this key is not part of the IAB TCF specifications.
    public class AdvertisingConsentStatus {
        public static final int PurposeId                         = 3;
        public static final String Key                            = "SmartCMP_advertisingConsentStatus";
    }

    // Vendor List configuration.
    public class VendorList {
        public static final String DefaultEndPoint                = "https://vendorlist.consensu.org/vendorlist.json";
        public static final String VersionedEndPoint              = "https://vendorlist.consensu.org/v-{version}/vendorlist.json";

        public static final String DefaultLocalizedEndPoint       = "https://vendorlist.consensu.org/purposes-{language}.json";
        public static final String VersionedLocalizedEndPoint     = "https://vendorlist.consensu.org/purposes-{language}-{version}.json";
    }
}
