package com.fidzup.android.cmp.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.fidzup.android.cmp.Constants;

/**
 * Represents the URL to an editor.
 */

public class EditorURL {

    // The default URL used to build the URL
    private String defaultURL = Constants.Editor.EditorDefaultEndPoint;

    // The default URL used to build the URL
    private String defaultLocalizedURL = Constants.Editor.EditorDefaultLocalizedEndPoint;

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
        this.defaultURL = jsonURL;
        this.defaultLocalizedURL = localizedJsonURL;

        return this;
    }

    /**
     * Initialize an EditorURL object that represents the latest editor.
     *
     * @param language The language of the user if a localized URL has to be used.
     */
    public EditorURL(@Nullable Language language) {
//        URL = Constants.Editor.EditorDefaultEndPoint;
        URL = defaultURL;

        if (language != null) {
            localizedURL = defaultLocalizedURL.replace("{language}", language.toString());
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
