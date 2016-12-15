package com.example.boris.rijksapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

class showPictureTask extends AsyncTask<String, Void, Bitmap> {
    ImageView posterImage;

    public showPictureTask(ImageView bmImage) {
        this.posterImage = bmImage;
    }

    protected Bitmap doInBackground(String... urls) {

        // The passed url
        String stringurlOfImage = urls[0];
        URL urlOfImage = null;

        try {
            // Making new url using the passed string url
            urlOfImage = new URL(stringurlOfImage);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Bitmap bmpOfPoster = null;
        try {
            // Saving image url into end result
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();

            // Resize image, make it smaller
            bmOptions.inSampleSize = 10; // 1 = 100% if you write 10 means 1/10 = 10%
            bmpOfPoster = BitmapFactory.decodeStream(urlOfImage.openConnection().getInputStream(),
                    null, bmOptions);

            return bmpOfPoster;

        } catch (IOException e) {
            e.printStackTrace();
        }
        // If image could not be obtained
        return null;
    }

    protected void onPostExecute(Bitmap result) {

        // Setting the image to an imageview where showPictureTask() has been called
        posterImage.setImageBitmap(result);
    }
}
