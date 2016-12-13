package com.example.boris.rijksapp;

import android.os.AsyncTask;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class obtainPaintingsTask extends AsyncTask<String, Void, String> {
     // declaring variable for urlconnection
     private HttpURLConnection urlConnection;

    // this function retrieves information of the url-link
    @Override
    protected String doInBackground(String... params) {
        try {

            // params[0] is the title art user entered, pass this to the url
            URL urlOfArt = new URL(params[0]);

            try {
                // makes a connection with the provided url-link
                urlConnection = (HttpURLConnection) urlOfArt.openConnection();

                // reads from the source -> json object in this case
                BufferedReader readInfo = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder totalInformation = new StringBuilder();
                String line;

                // add line per line from json to a string
                while ((line = readInfo.readLine()) != null) {
                    totalInformation.append(line + "\n"); //.append('\n');
                }

                readInfo.close();

                // this string will be passed to onpostexecute
                return totalInformation.toString();

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        // if catch occures, return null -> pass to onpostexecute
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        // if the movie is found, pass entire string(containing info) to showmovieInfo-activity
        super.onPostExecute(result);
    }

    @Override
    protected void onPreExecute() {}

    @Override
    protected void onProgressUpdate(Void... values) {}
}

