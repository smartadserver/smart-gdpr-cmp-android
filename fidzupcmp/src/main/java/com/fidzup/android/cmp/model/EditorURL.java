package com.fidzup.android.cmp.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.fidzup.android.cmp.Constants;

/**
 * Represents the URL to an editor.
 */

public class EditorURL {

    // The actual URL of the editor.
    @NonNull
    private String URL;

    // The actual URL of the localized editor.
    @Nullable
    private String localizedURL;

    /**
     * Initialize an EditorURL object that represents the latest editor.
     *
     * @param language The language of the user if a localized URL has to be used.
     */
    public EditorURL(@Nullable Language language) {
        URL = Constants.Editor.EditorDefaultEndPoint;

        if (language != null) {
            localizedURL = Constants.Editor.EditorDefaultLocalizedEndPoint.replace("{language}", language.toString());
        }
    }

    /**
     * Initialize an EditorURL object that represents the editor for a given version.
     *
     * @param version  The editor version that should be fetched.
     * @param language The language of the user if a localized URL has to be used.
     */
    public EditorURL(int version, @Nullable Language language) throws IllegalArgumentException {
        if (version < 1) {
            Log.e("FidzupCMP", "VendorListURL can not be configured. The version must be greater than 0.");
            throw new IllegalArgumentException("Version can not be lower than 1");
        }

        URL = Constants.Editor.EditorVersionedEndPoint.replace("{version}", "" + version);

        if (language != null) {
            localizedURL = Constants.Editor.EditorVersionedLocalizedEndPoint.replace("{language}", language.toString())
                    .replace("{version}", "" + version);
        }
    }

    /**
     * @return The URL of the editor.
     */
    @NonNull
    public String getURL() {
        return URL;
    }

    /**
     * @return The localized URL of the editor.
     */
    @Nullable
    public String getLocalizedURL() {
        return localizedURL;
    }
}
