package com.example.boris.rijksapp;

import android.os.AsyncTask;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class obtainPaintingsTask extends AsyncTask<String, Void, String> {
     // Declaring variable for urlconnection
     private HttpURLConnection urlConnection;

    // This function retrieves information of the url-link
    @Override
    protected String doInBackground(String... params) {
        try {

            // Params[0] is the whole url String, pass this to the url
            URL urlOfArt = new URL(params[0]);

            try {
                // Makes a connection with the provided url-link
                urlConnection = (HttpURLConnection) urlOfArt.openConnection();

                // Reads from the source -> json object in this case
                BufferedReader readInfo = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder totalInformation = new StringBuilder();
                String line;

                // Add line per line from json to a string
                while ((line = readInfo.readLine()) != null) {
                    totalInformation.append(line + "\n"); //.append('\n');
                }

                readInfo.close();

                // This string will be passed to onpostexecute
                return totalInformation.toString();

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        // If catch occures, return null -> pass to onpostexecute
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        // If artworks are found, pass entire string(containing info) to MainActivity
        super.onPostExecute(result);
    }

    @Override
    protected void onPreExecute() {}

    @Override
    protected void onProgressUpdate(Void... values) {}
}

