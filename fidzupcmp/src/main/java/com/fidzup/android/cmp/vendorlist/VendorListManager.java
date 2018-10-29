package com.fidzup.android.cmp.vendorlist;

import android.accounts.NetworkErrorException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import com.fidzup.android.cmp.model.Language;
import com.fidzup.android.cmp.model.VendorList;
import com.fidzup.android.cmp.model.VendorListURL;
import com.fidzup.android.cmp.util.JSONAsyncTask;
import com.fidzup.android.cmp.util.JSONAsyncTaskListener;

import org.json.JSONObject;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Retrieves and parses a vendor list from internet.
 */

@SuppressWarnings("WeakerAccess")
public class VendorListManager {

    // The main vendor list manager listener.
    @NonNull
    private VendorListManagerListener listener;

    // The interval between each refresh (in millisecond).
    private long refreshInterval;

    // the time interval to observe before retrying to download the vendor list after a failure at previous attempt
    private long retryInterval;

    // Representation of the vendor list URL.
    @NonNull
    VendorListURL vendorListURL;

    // Representation of the sub vendor list URL.
    @Nullable
    String subVendorListURL = null;

    // The timer used to schedule the automatic refresh.
    private Timer timer;

    // The Date of the last vendor list refresh.
    private Date lastRefreshDate;

    // flag to mark that a download attempt of the vendors list is currently in progress
    private boolean downloadingVendorsList = false;

    /**
     * Initialize a VendorListManager that will download only the latest version of the vendor list.
     *
     * @param listener        The vendor list manager listener to call when the vendor list is downloaded or failed to be downloaded.
     * @param refreshInterval Time between each refresh.
     * @param retryInterval   Time between each unsuccessful refresh.
     * @param language        The language wanted for the vendor list. Needs to be ISO-639-1.
     * @throws IllegalArgumentException if given language is not ISO 639-1.
     */
    public VendorListManager(@NonNull VendorListManagerListener listener, long refreshInterval, long retryInterval, @Nullable Language language) throws IllegalArgumentException {
        this(listener, refreshInterval, retryInterval, language, -1);
    }

    /**
     * Initialize a VendorListManager that will download only the given version number of the vendor list.
     *
     * @param listener          The vendor list manager listener to call when the vendor list is downloaded or failed to be downloaded.
     * @param refreshInterval   Time between each refresh.
     * @param retryInterval     Time between each unsuccessful refresh.
     * @param language          The language wanted for the vendor list. Needs to be ISO-639-1.
     * @param vendorListVersion The wanted version of the vendor list (or the latest if -1).
     * @throws IllegalArgumentException if given language is not ISO 639-1.
     */
    public VendorListManager(@NonNull VendorListManagerListener listener, long refreshInterval, long retryInterval, @Nullable Language language, int vendorListVersion) throws IllegalArgumentException {
        this.listener = listener;
        this.refreshInterval = refreshInterval;
        this.retryInterval = retryInterval;
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
     * Instantiate and return a new JSONAsyncTask used for the sub VendorList.
     * Explicitly defined for test purpose.
     *
     * @param listener The listener to set to the JSONAsyncTask.
     * @return a new JSONAsyncTask.
     */
    @VisibleForTesting
    protected JSONAsyncTask getNewJSONAsyncTaskForSubVendorList(@NonNull JSONAsyncTaskListener listener) {
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
                long delay = retryInterval;

                try {
                    Log.i("FidzupCMP","getJSONAsyncTaskListenerForMainVendorList");
                    // We succeed to retrieve the vendor list JSON.
                    // Now, we try to download the localized vendor list JSON.
                    JSONAsyncTask jsonAsyncTask = getNewJSONAsyncTaskForLocalizedVendorList(getJSONAsyncTaskListenerForLocalizedVendorList(vendorListJSON));

                    //noinspection unchecked
                    jsonAsyncTask.execute(vendorListURL.getLocalizedURL());

                    // Everything succeed, so we store the last vendor list refresh date.
                    lastRefreshDate = new Date();
                    delay = refreshInterval;
                } catch (Exception e) {
                    downloadingVendorsList = false;
                    listener.onVendorListUpdateFail(e);
                }

                scheduleTimerIfNeeded(delay);
            }

            @Override
            public void JSONAsyncTaskDidFailDownloadingJSONObject() {
                downloadingVendorsList = false;
                listener.onVendorListUpdateFail(new NetworkErrorException());
                scheduleTimerIfNeeded(retryInterval);
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

            public void rawJSONAsyncTaskDidSucceedDownloadingJSONObject(@NonNull JSONObject localizedVendorListJSON) {
                downloadingVendorsList = false;
                try {
                    listener.onVendorListUpdateSuccess(new VendorList(vendorListJSON, localizedVendorListJSON));
                } catch (Exception e) {
                    listener.onVendorListUpdateFail(e);
                }
            }

            public void setAsyncTaskSubVendor(@NonNull JSONObject vendorListJSON, @Nullable JSONObject localizedVendorListJSON) {
                long delay = retryInterval;

                try {
                    Log.i("FidzupCMP","getJSONAsyncTaskListenerForLocalizedVendorList Sub");
                    // We succeed to retrieve the localized vendor list JSON.
                    // Now, we try to download the sub vendor list JSON.
                    JSONAsyncTask jsonAsyncTask = getNewJSONAsyncTaskForSubVendorList(getJSONAsyncTaskListenerForSubVendorList(vendorListJSON,localizedVendorListJSON));

                    //noinspection unchecked
                    jsonAsyncTask.execute(subVendorListURL);

                    // Everything succeed, so we store the last vendor list refresh date.
                    lastRefreshDate = new Date();
                    delay = refreshInterval;
                } catch (Exception e) {
                    downloadingVendorsList = false;
                    listener.onVendorListUpdateFail(e);
                }

                scheduleTimerIfNeeded(delay);
            }

            @Override
            public void JSONAsyncTaskDidSucceedDownloadingJSONObject(@NonNull JSONObject localizedVendorListJSON) {
                Log.i("FidzupCMP","getJSONAsyncTaskListenerForLocalizedVendorList");
                if (subVendorListURL == null) {
                    rawJSONAsyncTaskDidSucceedDownloadingJSONObject(localizedVendorListJSON);
                } else {
                    setAsyncTaskSubVendor(vendorListJSON,localizedVendorListJSON);
                }
            }

            @Override
            public void JSONAsyncTaskDidFailDownloadingJSONObject() {
                //Special case, english purposes does not exists, because IAB push the english version directly in vendorlist
                //But we want the pubvendors.json file to be managed
                if (vendorListURL.getLanguage() != null && vendorListURL.getLanguage().toString().equals("en") && subVendorListURL != null) {
                    setAsyncTaskSubVendor(vendorListJSON,null);
                } else {
                    // We failed to get the localized vendor list.
                    downloadingVendorsList = false;
                    try {
                        listener.onVendorListUpdateSuccess(new VendorList(vendorListJSON));
                    } catch (Exception e) {
                        listener.onVendorListUpdateFail(e);
                    }
                }
            }
        };
    }

    /**
     * Instantiate and return a new JSONAsyncTaskListener used for the download of the sub vendor list, provided by the editor.
     *
     * @return a new JSONAsyncTaskListener.
     */
    private JSONAsyncTaskListener getJSONAsyncTaskListenerForSubVendorList(@NonNull final JSONObject vendorListJSON, @Nullable final JSONObject localizedVendorListJSON) {
        return new JSONAsyncTaskListener() {
            @Override
            public void JSONAsyncTaskDidSucceedDownloadingJSONObject(@NonNull JSONObject subVendorListJSON) {
                downloadingVendorsList = false;
                try {
                    listener.onVendorListUpdateSuccess(new VendorList(vendorListJSON, localizedVendorListJSON, subVendorListJSON));
                } catch (Exception e) {
                    listener.onVendorListUpdateFail(e);
                }
            }

            @Override
            public void JSONAsyncTaskDidFailDownloadingJSONObject() {
                // We failed to get the sub vendor list.
                downloadingVendorsList = false;
                try {
                    listener.onVendorListUpdateSuccess(new VendorList(vendorListJSON, localizedVendorListJSON));
                } catch (Exception e) {
                    Log.e("FidzupCMP", "Dowloading SubVendor failed [" + subVendorListURL + "]");
                    listener.onVendorListUpdateFail(e);
                }
            }
        };
    }

    public void loadInitialVendorListFromJson(String defaultVendorListJson) {
        try {
            listener.onVendorListUpdateSuccess(new VendorList(new JSONObject(defaultVendorListJson)));
        } catch (Exception e) {
            Log.e("FidzupCMP", "Parsing initial VendorList JSON failed [" + defaultVendorListJson + "]");
            listener.onVendorListUpdateFail(e);
        }
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

        // refresh the vendor list if needed.
        refreshVendorListIfNeeded();

    }

    /**
     * Disable the automatic refresh by cancelling the timer.
     */
    public void stopAutomaticRefresh() {
        if (timer != null) {
            timer.cancel();
        }
        downloadingVendorsList = false;
        timer = null;
    }

    /**
     * Reset the timer to refresh the vendor list sooner but not immediately.
     */
    public void resetTimer() {
        // Reset the timer
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();

        // reschedule the timer to refresh the vendor sooner.
        scheduleTimerIfNeeded(retryInterval);
    }

    /**
     * Refresh the vendor list from network only if needed.
     */
    private void refreshVendorListIfNeeded() {
        // Compute the time before the next needed refresh.
        long remainingTime = 0;
        if (lastRefreshDate != null) {
            remainingTime = lastRefreshDate.getTime() + refreshInterval - new Date().getTime();
        }

        //Need to refresh as we have reached the refresh date.
        if (remainingTime <= 0) {
            refreshVendorList();
        } else {
            scheduleTimerIfNeeded(remainingTime);
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
    private void scheduleTimerIfNeeded(long delay) {
        if (timer != null) {
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    refreshVendorListIfNeeded();
                }
            }, delay);
        }
    }

    /**
     * Set the Sub Vendor List URL. It cannot be static, as it needs to be hosted on Editor's side
     *
     * @return this
     */
    @SuppressWarnings("UnusedReturnValue")
    public VendorListManager setSubVendorListURL(String subVendorListURL) {
        this.subVendorListURL = subVendorListURL;
        return this;
    }

    /**
     *
     * @return String the sub vendor list URL
     */
    @SuppressWarnings("UnusedDeclaration") // We are in a library, and we want to expose method, even if we don't use it internally
    @Nullable
    public String getSubVendorListURL() {
        return this.subVendorListURL;
    }
}
