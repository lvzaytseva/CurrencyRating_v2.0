package com.example.currencyratingv20.utils;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

public class NetworkUtils {

    private static final String API_KEY = "3544413c-6d6c-4e43-a325-bf19f5a000c5";
    private static final String BASE_URL = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest";

    private static final String PARAMS_START = "start";
    private static final String PARAMS_LIMIT = "limit";
    private static final String PARAMS_CONVERT = "convert";

    private static final String CONVERT_VALUE = "USD";
    private static final int START_VALUE = 1;
    private static int limitValue = 5;

    public static void setLimitValue(int limitValue) {
        NetworkUtils.limitValue = limitValue;
    }

    public static int getLimitValue() {
        return limitValue;
    }

    public static URL buildURL() {
        URL result = null;
        Uri uri = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter(PARAMS_START, String.valueOf(START_VALUE))
                .appendQueryParameter(PARAMS_LIMIT, String.valueOf(limitValue))
                .appendQueryParameter(PARAMS_CONVERT,CONVERT_VALUE)
                .build();
        try {
            result = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static class JSONLoader extends AsyncTaskLoader<JSONObject> {
        private final Bundle bundle;
        private OnStartLoadingListener onStartLoadingListener;

        public interface OnStartLoadingListener {
            void onStartLoading();
        }

        public JSONLoader(@NonNull Context context, Bundle bundle) {
            super(context);
            this.bundle = bundle;
        }

        @Override
        protected void onStartLoading() {
            super.onStartLoading();
            if (onStartLoadingListener != null) {
                onStartLoadingListener.onStartLoading();
            }
            forceLoad();
        }

        @Nullable
        @Override
        public JSONObject loadInBackground() {
            if (bundle == null) {
                return null;
            }
            String urlAsString = bundle.getString("url");
            URL url = null;
            try {
                url = new URL(urlAsString);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            JSONObject result = null;
            if (url == null) {
                return null;
            }
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("X-CMC_PRO_API_KEY", API_KEY);
                try (InputStream inputStream = connection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(inputStreamReader)) {
                    StringBuilder builder = new StringBuilder();
                    String line = reader.readLine();
                    while (line != null) {
                        builder.append(line);
                        line = reader.readLine();
                    }
                    result = new JSONObject(builder.toString());
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return result;
        }
    }
}
