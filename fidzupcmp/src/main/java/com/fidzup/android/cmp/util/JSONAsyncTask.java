package com.fidzup.android.cmp.util;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Subclass of AsyncTask to make it easy to download a JSON file.
 */

public class JSONAsyncTask extends AsyncTask {

    static private final int TIMEOUT = 30000;

    @NonNull
    protected JSONAsyncTaskListener listener;

    public JSONAsyncTask(@NonNull JSONAsyncTaskListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onPostExecute(Object o) {
        String jsonString = (String) o;
        if (jsonString != null) {
            try {
                listener.JSONAsyncTaskDidSucceedDownloadingJSONObject(new JSONObject(jsonString));
                return;
            } catch (JSONException ignored) {
            }
        }

        listener.JSONAsyncTaskDidFailDownloadingJSONObject();
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        URL JSONURL = null;

        if (objects.length > 0) {
            // Retrieve the URL given in parameters
            String rawJSONURL = (String) objects[0];
            try {
                // Android 4.4 device not up 2 date, cannot use TLSv1.2 or TLSV1.2, so fallback to http.
                if (android.os.Build.VERSION.SDK_INT == 19) {
                    rawJSONURL = rawJSONURL.replaceFirst("^(https://)","http://");
                }
                JSONURL = new URL(rawJSONURL);
            } catch (MalformedURLException ignored) {
            }
        }

        if (JSONURL == null) {
            return null;
        }

        InputStream inputStream = null;
        HttpURLConnection connection = null;

        try {
            connection = (HttpURLConnection) JSONURL.openConnection();
            connection.setConnectTimeout(TIMEOUT);
            connection.setUseCaches(false);

            // if connection succeeded, download response body
            if (connection.getResponseCode() == 200) {
                inputStream = connection.getInputStream();
                BufferedInputStream bInputStream = new BufferedInputStream(inputStream);

                BufferedReader reader = new BufferedReader(new InputStreamReader(bInputStream));
                StringBuilder stringBuilder = new StringBuilder();

                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }

                return stringBuilder.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // close open connections
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (connection != null) {
                    connection.disconnect();
                }
            } catch (IOException ignored) {
            }
        }
        return null;
    }
}
