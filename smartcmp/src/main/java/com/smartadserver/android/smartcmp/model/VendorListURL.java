package com.smartadserver.android.smartcmp.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.smartadserver.android.smartcmp.Constants;

/**
 * Reprensents the URL to a vendor list.
 */

public class VendorListURL {

    // The actual URL of the vendor list.
    @NonNull
    private String URL;

    // The actual URL of the localized vendor list.
    @Nullable
    private String localizedURL;

    /**
     * Initialize a VendorListURL object that represents the latest vendor list.
     *
     * @param language The language of the user if a localized URL has to be used.
     */
    public VendorListURL(@Nullable Language language) {
        URL = Constants.VendorList.DefaultEndPoint;

        if (language != null) {
            localizedURL = Constants.VendorList.DefaultLocalizedEndPoint.replace("{language}", language.toString());
        }
    }

    /**
     * Initialize a VendorListURL object that represents the vendor list for a given version.
     *
     * @param version  The vendor list version that should be fetched.
     * @param language The language of the user if a localized URL has to be used.
     */
    public VendorListURL(int version, @Nullable Language language) throws IllegalArgumentException {
        if (version < 1) {
            Log.e("SmartCMP", "VendorListURL can not be configured. The version must be greater than 0.");
            throw new IllegalArgumentException("Version can not be lower than 1");
        }

        URL = Constants.VendorList.VersionedEndPoint.replace("{version}", "" + version);

        if (language != null) {
            localizedURL = Constants.VendorList.VersionedLocalizedEndPoint.replace("{language}", language.toString())
                    .replace("{version}", "" + version);
        }
    }

    /**
     * @return The URL of the vendor list.
     */
    @NonNull
    public String getURL() {
        return URL;
    }

    /**
     * @return The localized URL of the vendor list.
     */
    @Nullable
    public String getLocalizedURL() {
        return localizedURL;
    }
}
