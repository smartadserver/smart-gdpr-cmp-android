package com.smartadserver.android.smartcmp.vendorlist;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import com.smartadserver.android.smartcmp.Constants;
import com.smartadserver.android.smartcmp.model.Language;
import com.smartadserver.android.smartcmp.model.VendorList;
import com.smartadserver.android.smartcmp.model.VendorListURL;
import com.smartadserver.android.smartcmp.util.JSONAsyncTask;
import com.smartadserver.android.smartcmp.util.JSONAsyncTaskListener;

import org.json.JSONObject;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Retrieves and parses a vendor list from internet.
 */

@SuppressWarnings("WeakerAccess")
public class VendorListManager {

    // The application context.
    @NonNull
    private Context context;

    // The main vendor list manager listener.
    @NonNull
    private VendorListManagerListener listener;

    // The interval between each refresh (in millisecond).
    private long refreshInterval;

    // The time interval between two refresh attempts.
    private long pollInterval;

    // Representation of the vendor list URL.
    @NonNull
    VendorListURL vendorListURL;

    // The timer used to schedule the automatic refresh.
    private Timer timer;

    // flag to mark that a download attempt of the vendors list is currently in progress
    private boolean downloadingVendorsList = false;

    /**
     * Initialize a VendorListManager that will download only the latest version of the vendor list.
     *
     * @param context         The application context.
     * @param listener        The vendor list manager listener to call when the vendor list is downloaded or failed to be downloaded.
     * @param refreshInterval Time between each refresh.
     * @param pollInterval    Time between each refresh attempt.
     * @param language        The language wanted for the vendor list. Needs to be ISO-639-1.
     * @throws IllegalArgumentException if given language is not ISO 639-1.
     */
    public VendorListManager(@NonNull Context context, @NonNull VendorListManagerListener listener, long refreshInterval, long pollInterval, @Nullable Language language) throws IllegalArgumentException {
        this(context, listener, refreshInterval, pollInterval, language, -1);
    }

    /**
     * Initialize a VendorListManager that will download only the given version number of the vendor list.
     *
     * @param context           The application context.
     * @param listener          The vendor list manager listener to call when the vendor list is downloaded or failed to be downloaded.
     * @param refreshInterval   Time between each refresh.
     * @param pollInterval      Time between each refresh attempt.
     * @param language          The language wanted for the vendor list. Needs to be ISO-639-1.
     * @param vendorListVersion The wanted version of the vendor list (or the latest if -1).
     * @throws IllegalArgumentException if given language is not ISO 639-1.
     */
    public VendorListManager(@NonNull Context context, @NonNull VendorListManagerListener listener, long refreshInterval, long pollInterval, @Nullable Language language, int vendorListVersion) throws IllegalArgumentException {
        this.context = context;
        this.listener = listener;
        this.refreshInterval = refreshInterval;
        this.pollInterval = pollInterval;
        vendorListURL = vendorListVersion == -1 ? new VendorListURL(language) : new VendorListURL(vendorListVersion, language);
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
     * Instantiate and return a new JSONAsyncTask used for the VendorList.
     * Explicitly defined for test purpose.
     *
     * @param listener The listener to set to the JSONAsyncTask.
     * @return a new JSONAsyncTask.
     */
    @VisibleForTesting
    protected JSONAsyncTask getNewJSONAsyncTaskForVendorList(@NonNull JSONAsyncTaskListener listener) {
        return new JSONAsyncTask(listener);
    }

    /**
     * Instantiate and return a new JSONAsyncTask used for the localized VendorList.
     * Explicitly defined for test purpose.
     *
     * @param listener The listener to set to the JSONAsyncTask.
     * @return a new JSONAsyncTask.
     */
    @VisibleForTesting
    protected JSONAsyncTask getNewJSONAsyncTaskForLocalizedVendorList(@NonNull JSONAsyncTaskListener listener) {
        return new JSONAsyncTask(listener);
    }

    /**
     * Instantiate and return a new JSONAsyncTaskListener used for the download of the main vendor list.
     *
     * @return a new JSONAsyncTaskListener.
     */
    private JSONAsyncTaskListener getJSONAsyncTaskListenerForMainVendorList() {
        return new JSONAsyncTaskListener() {
            @Override
            public void JSONAsyncTaskDidSucceedDownloadingJSONObject(@NonNull JSONObject vendorListJSON) {
                try {
                    // We succeed to retrieve the vendor list JSON.
                    // Now, we try to download the localized vendor list JSON.
                    JSONAsyncTask jsonAsyncTask = getNewJSONAsyncTaskForLocalizedVendorList(getJSONAsyncTaskListenerForLocalizedVendorList(vendorListJSON));

                    //noinspection unchecked
                    jsonAsyncTask.execute(vendorListURL.getLocalizedURL());

                    // Everything succeed, so we setup the next refresh date to the shared preferences.
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putLong(Constants.VendorList.NextRefreshDate, new Date().getTime() + refreshInterval);
                    editor.apply();

                } catch (Exception e) {
                    downloadingVendorsList = false;
                    listener.onVendorListUpdateFail(e);
                }

                scheduleTimerIfNeeded();
            }

            @Override
            public void JSONAsyncTaskDidFailDownloadingJSONObject() {
                downloadingVendorsList = false;
                listener.onVendorListUpdateFail(new NetworkErrorException());
                scheduleTimerIfNeeded();
            }
        };
    }

    /**
     * Instantiate and return a new JSONAsyncTaskListener used for the download of the localized vendor list.
     *
     * @return a new JSONAsyncTaskListener.
     */
    private JSONAsyncTaskListener getJSONAsyncTaskListenerForLocalizedVendorList(@NonNull final JSONObject vendorListJSON) {
        return new JSONAsyncTaskListener() {
            @Override
            public void JSONAsyncTaskDidSucceedDownloadingJSONObject(@NonNull JSONObject localizedVendorListJSON) {
                downloadingVendorsList = false;
                try {
                    listener.onVendorListUpdateSuccess(new VendorList(vendorListJSON, localizedVendorListJSON));
                } catch (Exception e) {
                    listener.onVendorListUpdateFail(e);
                }
            }

            @Override
            public void JSONAsyncTaskDidFailDownloadingJSONObject() {
                // We failed to get the localized vendor list.
                downloadingVendorsList = false;
                try {
                    listener.onVendorListUpdateSuccess(new VendorList(vendorListJSON));
                } catch (Exception e) {
                    listener.onVendorListUpdateFail(e);
                }
            }
        };
    }

    /**
     * Enable the automatic refresh.
     */
    public void startAutomaticRefresh(boolean forceFirstRefresh) {

        timer = new Timer();

        if (forceFirstRefresh) {
            // force refresh of the vendor list.
            refreshVendorList();
        } else {
            // refresh the vendor list if needed.
            refreshVendorListIfNeeded();
        }
    }

    /**
     * Disable the automatic refresh by cancelling the timer.
     */
    public void stopAutomaticRefresh() {
        timer.cancel();
        downloadingVendorsList = false;
        timer = null;
    }

    /**
     * Reset the timer to refresh the vendor list sooner but not immediately.
     */
    public void resetTimer() {
        // Remove the nextRefreshDate from the SharedPreferences to force the refresh on the next poll attempts.
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(Constants.VendorList.NextRefreshDate);
        editor.apply();
    }

    /**
     * Refresh the vendor list from network only if needed.
     */
    private void refreshVendorListIfNeeded() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Date currentDate = new Date();
        long nextRefreshDate = prefs.getLong(Constants.VendorList.NextRefreshDate, 0);

        // If the nextRefreshDate is reached we refresh the vendor list
        if (currentDate.getTime() > nextRefreshDate) {
            refreshVendorList();
        } else {
            scheduleTimerIfNeeded();
        }
    }

    /**
     * Refresh the vendor list from the network.
     */
    @SuppressWarnings("unchecked")
    public void refreshVendorList() {
        if (!downloadingVendorsList) {
            downloadingVendorsList = true;
            JSONAsyncTask jsonAsyncTask = getNewJSONAsyncTaskForVendorList(getJSONAsyncTaskListenerForMainVendorList());
            jsonAsyncTask.execute(vendorListURL.getURL());
        }
    }

    /**
     * Get the vendor list with the given vendor list version.
     *
     * @param vendorListVersion The vendor list version that must be downloaded.
     * @param listener          The listener that must be called.
     */
    @SuppressWarnings("unchecked")
    public void getVendorList(int vendorListVersion, @NonNull final VendorListManagerListener listener) {
        JSONAsyncTask jsonAsyncTask = getNewJSONAsyncTaskForVendorList(new JSONAsyncTaskListener() {
            @Override
            public void JSONAsyncTaskDidSucceedDownloadingJSONObject(@NonNull JSONObject JSON) {
                try {
                    listener.onVendorListUpdateSuccess(new VendorList(JSON));
                } catch (Exception e) {
                    listener.onVendorListUpdateFail(e);
                }
            }

            @Override
            public void JSONAsyncTaskDidFailDownloadingJSONObject() {
                listener.onVendorListUpdateFail(new NetworkErrorException());
            }
        });

        jsonAsyncTask.execute(new VendorListURL(vendorListVersion, null).getURL());

    }

    /**
     * Schedule the timer only if automatic refresh is enable.
     */
    private void scheduleTimerIfNeeded() {
        if (timer != null) {
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    refreshVendorListIfNeeded();
                }
            }, pollInterval);
        }
    }
}
