package com.fidzup.android.cmp.util;

import android.support.annotation.NonNull;

import org.json.JSONObject;

/**
 * Listener of JSONAsyncTask.
 */

public interface JSONAsyncTaskListener {

    /**
     * Warns that JSONAsyncTask did succeed to download the JSON.
     *
     * @param JSON The JSONObject downloaded.
     */
    void JSONAsyncTaskDidSucceedDownloadingJSONObject(@NonNull JSONObject JSON);

    /**
     * Warns that JSONAsyncTask did fail to do download the JSON.
     */
    void JSONAsyncTaskDidFailDownloadingJSONObject();
}
