package com.fidzup.android.cmp;

/**
 * General constants for FidzupCMP.
 */

public class Constants {

    // Generic information on the CMP sdk.
    public class CMPInfos {

        // '190' IS THE OFFICIAL CMP ID FOR FidzupCMP.
        // You can use this ID as long as you don't change the source code of this project.
        // If you don't use it exactly as distributed in the official Fidzup repository,
        // you must get your own CMP ID by registering here: https://register.consensu.org/CMP
        public static final int ID = 190;
        public static final int VERSION = 7;
    }

    // IAB Keys for SharedPreferences storage.
    @SuppressWarnings("unused")
    public class IABConsentKeys {
        public static final String CMPPresent                     = "IABConsent_CMPPresent";
        public static final String SubjectToGDPR                  = "IABConsent_SubjectToGDPR";
        public static final String ConsentString                  = "IABConsent_ConsentString";
        public static final String ParsedPurposeConsent           = "IABConsent_ParsedPurposeConsents";
        public static final String ParsedVendorConsent            = "IABConsent_ParsedVendorConsents";
    }

    // The FidzupCMPConsentKeys SharedPreferences key contains the current user consent for the advertising
    // purpose of the current editor & vendor list.
    //
    // The AdvertisingConsentStatus status is only based on the answer of the user for the advertising purpose and does not take
    // vendors status into account. It should be used for third party advertisement SDK that are not
    // IAB TCF compliant.
    //
    // Note: all these keys are not part of the IAB TCF specifications.
    public class FidzupCMPConsentKeys {
        public static final int    PurposeId                  = 3;
        public static final String AdvertisingConsentStatus   = "FidzupCMP_advertisingConsentStatus";
        public static final String ConsentString              = "FidzupCMP_ConsentString";
        public static final String ParsedEditorPurposeConsent = "FidzupCMP_ParsedEditorPurposeConsents";
    }

    // Vendor List configuration.
    // The files hosted on vendorlist.fidzup.mgr.consensu.org, are a copy of the ones hosted on vendorlist.consensu.org
    // With the addition of a 6th purpose (Geolocalized ads), mandatory for the french regulator (CNIL)
    public class VendorList {
        public static final String DefaultEndPoint                = "https://vendorlist.fidzup.mgr.consensu.org/vendorlist.json";
        public static final String VersionedEndPoint              = "https://vendorlist.fidzup.mgr.consensu.org/v-{version}/vendorlist.json";

        public static final String DefaultLocalizedEndPoint       = "https://vendorlist.fidzup.mgr.consensu.org/purposes-{language}.json";
        public static final String VersionedLocalizedEndPoint     = "https://vendorlist.fidzup.mgr.consensu.org/v-{version}/purposes-{language}.json";
    }
}
