package com.hayk.healthmanagerregistration;

import android.os.AsyncTask;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class NetworkUtils {

    public interface NetworkCheckListener {
        void onNetworkCheckCompleted(boolean isInternetAvailable);
    }

    public static void checkInternetConnectivity(NetworkCheckListener listener) {
        new InternetCheckTask(listener).execute();
    }

    private static class InternetCheckTask extends AsyncTask<Void, Void, Boolean> {
        private NetworkCheckListener listener;

        InternetCheckTask(NetworkCheckListener listener) {
            this.listener = listener;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                HttpURLConnection urlc = (HttpURLConnection) (new URL("https://www.google.com").openConnection());
                urlc.setRequestProperty("User-Agent", "Test");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1500);
                urlc.connect();
                return (urlc.getResponseCode() == 200);
            } catch (IOException e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            listener.onNetworkCheckCompleted(result);
        }
    }
}
