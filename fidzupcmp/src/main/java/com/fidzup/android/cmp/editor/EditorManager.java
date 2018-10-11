package com.fidzup.android.cmp.editor;

import android.accounts.NetworkErrorException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import com.fidzup.android.cmp.model.Language;
import com.fidzup.android.cmp.model.Editor;
import com.fidzup.android.cmp.model.EditorURL;
import com.fidzup.android.cmp.util.JSONAsyncTask;
import com.fidzup.android.cmp.util.JSONAsyncTaskListener;

import org.json.JSONObject;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Retrieves and parses an editor from internet.
 */

@SuppressWarnings("WeakerAccess")
public class EditorManager {

    // The editor manager listener.
    @NonNull
    private EditorManagerListener listener;

    // The interval between each refresh (in millisecond).
    private long refreshInterval;

    // the time interval to observe before retrying to download the vendor list after a failure at previous attempt
    private long retryInterval;

    // Representation of the vendor list URL.
    @Nullable
    EditorURL editorURL;

    // The JSON String of the editor, if not refreshed by timer with URL.
    String editorJson;

    //
    Language wantedLanguage = null;

    // The timer used to schedule the automatic refresh.
    private Timer timer;

    // The Date of the last vendor list refresh.
    private Date lastRefreshDate;

    // flag to mark that a download attempt of the editor is currently in progress
    private boolean downloadingEditor = false;

    /**
     * Initialize a EditorManager that will download only the latest version of the vendor list.
     *
     * @param listener        The editor manager listener to call when the vendor list is downloaded or failed to be downloaded.
     * @param refreshInterval Time between each refresh.
     * @param retryInterval   Time between each unsuccessful refresh.
     * @param language        The language wanted for the editor. Needs to be ISO-639-1.
     * @throws IllegalArgumentException if given language is not ISO 639-1.
     */
    public EditorManager(@NonNull EditorManagerListener listener, long refreshInterval, long retryInterval, @Nullable Language language) throws IllegalArgumentException {
        this(listener, refreshInterval, retryInterval, language, -1);
    }

    /**
     * Initialize a EditorManager that will download only the given version number of the editor.
     *
     * @param listener          The editor manager listener to call when the vendor list is downloaded or failed to be downloaded.
     * @param refreshInterval   Time between each refresh.
     * @param retryInterval     Time between each unsuccessful refresh.
     * @param language          The language wanted for the vendor list. Needs to be ISO-639-1.
     * @param editorVersion     The wanted version of the editor (or the latest if -1).
     * @throws IllegalArgumentException if given language is not ISO 639-1.
     */
    @SuppressWarnings("UnusedParameters") // editorVersion is not used for now, but we have to in the future
    public EditorManager(@NonNull EditorManagerListener listener, long refreshInterval, long retryInterval, @Nullable Language language, int editorVersion) throws IllegalArgumentException {
        this.listener = listener;
        this.refreshInterval = refreshInterval;
        this.retryInterval = retryInterval;
        this.wantedLanguage = language;
//        this.editorURL = new EditorURL(language);
    }

    @SuppressWarnings("UnusedReturnValue")
    @NonNull
    public EditorManager setEditorURLs(String jsonURL, String localizedJsonURL) {
        this.editorURL = new EditorURL(jsonURL,localizedJsonURL,this.wantedLanguage);
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    @NonNull
    public EditorManager setEditorJSON(String json) {
        this.editorJson = json;
        return this;
    }

    /**
     * @return the refresh interval.
     */
    @SuppressWarnings("unused")
    public long getRefreshInterval() {
        return refreshInterval;
    }

    /**
     * Set the refresh interval.
     *
     * @param refreshInterval the refresh interval (ms).
     */
    @SuppressWarnings("unused")
    public void setRefreshInterval(long refreshInterval) {
        this.refreshInterval = refreshInterval;
    }

    /**
     * Instantiate and return a new JSONAsyncTask used for the Editor.
     * Explicitly defined for test purpose.
     *
     * @param listener The listener to set to the JSONAsyncTask.
     * @return a new JSONAsyncTask.
     */
    @VisibleForTesting
    protected JSONAsyncTask getNewJSONAsyncTaskForEditor(@NonNull JSONAsyncTaskListener listener) {
        return new JSONAsyncTask(listener);
    }

    /**
     * Instantiate and return a new JSONAsyncTask used for the localized editor.
     * Explicitly defined for test purpose.
     *
     * @param listener The listener to set to the JSONAsyncTask.
     * @return a new JSONAsyncTask.
     */
    @VisibleForTesting
    protected JSONAsyncTask getNewJSONAsyncTaskForLocalizedEditor(@NonNull JSONAsyncTaskListener listener) {
        return new JSONAsyncTask(listener);
    }

    /**
     * Instantiate and return a new JSONAsyncTaskListener used for the download of the editor.
     *
     * @return a new JSONAsyncTaskListener.
     */
    private JSONAsyncTaskListener getJSONAsyncTaskListenerForEditor() {
        return new JSONAsyncTaskListener() {
            @Override
            public void JSONAsyncTaskDidSucceedDownloadingJSONObject(@NonNull JSONObject editorJSON) {
                if (editorURL != null) {
                    long delay = retryInterval;

                    try {
                        // We succeed to retrieve the editor JSON.
                        // Now, we try to download the localized editor JSON.
                        JSONAsyncTask jsonAsyncTask = getNewJSONAsyncTaskForLocalizedEditor(getJSONAsyncTaskListenerForLocalizedEditor(editorJSON));

                        //noinspection unchecked
                        jsonAsyncTask.execute(editorURL.getLocalizedURL());

                        // Everything succeed, so we store the last vendor list refresh date.
                        lastRefreshDate = new Date();
                        delay = refreshInterval;
                    } catch (Exception e) {
                        downloadingEditor = false;
                        listener.onEditorUpdateFail(e);
                    }

                    scheduleTimerIfNeeded(delay);
                }
            }

            @Override
            public void JSONAsyncTaskDidFailDownloadingJSONObject() {
                downloadingEditor = false;
                listener.onEditorUpdateFail(new NetworkErrorException());
                scheduleTimerIfNeeded(retryInterval);
            }
        };
    }

    /**
     * Instantiate and return a new JSONAsyncTaskListener used for the download of the localized vendor list.
     *
     * @return a new JSONAsyncTaskListener.
     */
    private JSONAsyncTaskListener getJSONAsyncTaskListenerForLocalizedEditor(@NonNull final JSONObject editorJSON) {
        return new JSONAsyncTaskListener() {
            @Override
            public void JSONAsyncTaskDidSucceedDownloadingJSONObject(@NonNull JSONObject localizedEditorJSON) {
                downloadingEditor = false;
                try {
                    listener.onEditorUpdateSuccess(new Editor(editorJSON, localizedEditorJSON));
                } catch (Exception e) {
                    listener.onEditorUpdateFail(e);
                }
            }

            @Override
            public void JSONAsyncTaskDidFailDownloadingJSONObject() {
                // We failed to get the localized editor.
                downloadingEditor = false;
                try {
                    listener.onEditorUpdateSuccess(new Editor(editorJSON));
                } catch (Exception e) {
                    listener.onEditorUpdateFail(e);
                }
            }
        };
    }

    /**
     * Enable the automatic refresh.
     */
    public void startAutomaticRefresh(boolean forceFirstRefresh) {

        timer = new Timer();

        // to force refresh, simply erase the last refresh date
        if (forceFirstRefresh) {
            lastRefreshDate = null;
        }

        // refresh the editor if needed.
        refreshEditorIfNeeded();

    }

    /**
     * Disable the automatic refresh by cancelling the timer.
     */
    public void stopAutomaticRefresh() {
        if (timer != null) {
            timer.cancel();
        }
        downloadingEditor = false;
        timer = null;
    }

    /**
     * Reset the timer to refresh the editor sooner but not immediately.
     */
    public void resetTimer() {
        // Reset the timer
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();

        // reschedule the timer to refresh the editor sooner.
        scheduleTimerIfNeeded(retryInterval);
    }

    /**
     * Refresh the editor from network only if needed.
     */
    private void refreshEditorIfNeeded() {
        // Compute the time before the next needed refresh.
        long remainingTime = 0;
        if (lastRefreshDate != null) {
            remainingTime = lastRefreshDate.getTime() + refreshInterval - new Date().getTime();
        }

        //Need to refresh as we have reached the refresh date.
        if (remainingTime <= 0) {
            refreshEditor();
        } else {
            scheduleTimerIfNeeded(remainingTime);
        }
    }

    /**
     * Refresh the editor from the network.
     */
    @SuppressWarnings("unchecked")
    public void refreshEditor() {
        if (!downloadingEditor && editorURL != null) {
            downloadingEditor = true;
            JSONAsyncTask jsonAsyncTask = getNewJSONAsyncTaskForEditor(getJSONAsyncTaskListenerForEditor());
            jsonAsyncTask.execute(editorURL.getURL());
        }
    }

    /**
     * Refresh the editor from local json string.
     */
    @SuppressWarnings("unchecked")
    public void refreshEditorFromJson() {
        try {
            JSONObject JSON = new JSONObject(this.editorJson);
            listener.onEditorUpdateSuccess(new Editor(JSON));
        } catch (Exception e) {
            listener.onEditorUpdateFail(e);
        }
    }

    /**
     * Get the vendor list with the given vendor list version.
     *
     * @param editorVersion The editor version that must be downloaded.
     * @param listener          The listener that must be called.
     */
    @SuppressWarnings({"UnusedParameters", "unchecked"}) // editorVersion is not used for now. But we had to in the future
    public void getEditor(int editorVersion, @NonNull final EditorManagerListener listener) {
        JSONAsyncTask jsonAsyncTask = getNewJSONAsyncTaskForEditor(new JSONAsyncTaskListener() {
            @Override
            public void JSONAsyncTaskDidSucceedDownloadingJSONObject(@NonNull JSONObject JSON) {
                try {
                    listener.onEditorUpdateSuccess(new Editor(JSON));
                } catch (Exception e) {
                    listener.onEditorUpdateFail(e);
                }
            }

            @Override
            public void JSONAsyncTaskDidFailDownloadingJSONObject() {
                listener.onEditorUpdateFail(new NetworkErrorException());
            }
        });

        jsonAsyncTask.execute(editorURL.getURL());

    }

    /**
     * Schedule the timer only if automatic refresh is enable.
     */
    private void scheduleTimerIfNeeded(long delay) {
        if (timer != null) {
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    refreshEditorIfNeeded();
                }
            }, delay);
        }
    }
}
