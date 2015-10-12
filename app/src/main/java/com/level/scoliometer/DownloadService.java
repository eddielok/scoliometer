package com.level.scoliometer;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.level.scoliometer.models.MSKITEM;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadService extends IntentService {

    public static final int STATUS_RUNNING = 0;
    public static final int STATUS_FINISHED = 1;
    public static final int STATUS_ERROR = 2;

    private static final String TAG = "DownloadService";

    public DownloadService() {
        super(DownloadService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.d(TAG, "Service Started!");

        final ResultReceiver receiver = intent.getParcelableExtra("receiver");
        String url = intent.getStringExtra("url");
        String PREFS_KEY = intent.getStringExtra("PREFS_KEY");
        String PREFS_NAME = intent.getStringExtra("PREFS_NAME");
        Bundle bundle = new Bundle();

        if (!TextUtils.isEmpty(url)) {
            /* Update UI: Download Service is Running */
            receiver.send(STATUS_RUNNING, Bundle.EMPTY);

            try {

                String[] results = downloadData(url, PREFS_KEY, PREFS_NAME);
                /* Sending result back to activity */
                if (null != results && results.length > 0) {
                    bundle.putStringArray("result", results);
                    receiver.send(STATUS_FINISHED, bundle);
                }
            } catch (Exception e) {

                /* Sending error message back to activity */
                bundle.putString(Intent.EXTRA_TEXT, e.toString());
                receiver.send(STATUS_ERROR, bundle);
            }
        }
        Log.d(TAG, "Service Stopping!");
        this.stopSelf();
    }

    private String[] downloadData(String requestUrl, String PREFS_KEY, String PREFS_NAME) throws IOException, DownloadException {
        InputStream inputStream = null;

        HttpURLConnection urlConnection = null;

        /* forming th java.net.URL object */
        URL url = new URL(requestUrl);

        urlConnection = (HttpURLConnection) url.openConnection();

        /* optional request header */
        urlConnection.setRequestProperty("Content-Type", "application/json");

        /* optional request header */
        urlConnection.setRequestProperty("Accept", "application/json");

        /* for Get request */
        urlConnection.setRequestMethod("GET");

        int statusCode = urlConnection.getResponseCode();

        /* 200 represents HTTP OK */
        if (statusCode == 200) {
            inputStream = new BufferedInputStream(urlConnection.getInputStream());

            String response = convertInputStreamToString(inputStream);
            MSKITEM mskitem = null;
            if ( response != null) mskitem = parseResult(response);
            String[] results = new String[4];
            if (mskitem == null) {
                results[0] = "0";
                results[1] = "0";
            }
            else {
                results[0] = mskitem.getMean();
                results[1] = mskitem.getStandard_Deviation();
            }
                results[2] = PREFS_KEY;
                results[3] = PREFS_NAME;

            return results;
        } else {
           // throw new DownloadException("Failed to fetch data!!");
            String[] results = new String[4];
            results[0] = "0";
            results[1] = "0";
            results[2] = PREFS_KEY;
            results[3] = PREFS_NAME;
            return results;
        }
    }

    private String convertInputStreamToString(InputStream inputStream) throws IOException {

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";

        while ((line = bufferedReader.readLine()) != null) {
            result += line;
        }

            /* Close Stream */
        if (null != inputStream) {
            inputStream.close();
        }

        return result;
    }

    private MSKITEM parseResult(String jsonSB) {
        MSKITEM mskitem = null;
        String json = jsonSB.toString();
        Gson gson = new GsonBuilder().create();
        try {
            mskitem = gson.fromJson(json, MSKITEM.class);
            return mskitem;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public class DownloadException extends Exception {

        public DownloadException(String message) {
            super(message);
        }

        public DownloadException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}