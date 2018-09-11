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
     * @param jsonURL the base URL where to fetch editor.json
     * @param localizedJsonURL the localized URL where to fetch localized editor-{language}.json
     * @return this
     */
    public EditorURL setDefaultURLs(String jsonURL, String localizedJsonURL) {
        this.URL = jsonURL;
        this.localizedURL = localizedJsonURL;

        return this;
    }

    /**
     * Initialize an EditorURL object that represents the latest editor.
     *
     * @param language The language of the user if a localized URL has to be used.
     */
    public EditorURL(String jsonURL, String localizedJsonURL, @Nullable Language language) {
        URL = jsonURL;

        if (language != null) {
            localizedURL = localizedJsonURL.replace("{language}", language.toString());
        }
    }


    /**
     * Initialize an EditorURL object that represents the latest editor.
     *
     * @param language The language of the user if a localized URL has to be used.
     */
    public EditorURL(@Nullable Language language) {
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
