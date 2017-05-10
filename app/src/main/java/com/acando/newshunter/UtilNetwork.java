package com.acando.newshunter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class UtilNetwork {

    public static InputStream downloadUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.connect();
        return conn.getInputStream();
    }

    public static Bitmap getImage(String url) throws IOException, OutOfMemoryError {
        InputStream input = null;
        HttpsURLConnection connection = null;
        Bitmap bitmap = null;

        try {
            connection = (HttpsURLConnection) (new URL(url).openConnection());
            connection.setRequestMethod("GET");
            connection.setDoInput(true);

            connection.setConnectTimeout(10000);
            connection.setReadTimeout(15000);
            connection.connect();

            int responseCode = connection.getResponseCode();
            if (responseCode != HttpsURLConnection.HTTP_OK) {
                throw new IOException("HTTP error code: " + responseCode);
            }

            input = connection.getInputStream();
            if (input != null) {
                bitmap = BitmapFactory.decodeStream(input);
                input.close();
                connection.disconnect();
            }
        } finally {
            if (input != null) input.close();
            if (connection != null) connection.disconnect();
        }
        return bitmap;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }
}
