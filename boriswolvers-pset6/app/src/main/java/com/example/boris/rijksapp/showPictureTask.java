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

        // the passed url
        String stringurlOfImage = urls[0];
        URL urlOfImage = null;

        try {
            // making new url using the passed string url
            urlOfImage = new URL(stringurlOfImage);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Bitmap bmpOfPoster = null;
        try {
            // saving image url into end result

            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inSampleSize = 10; // 1 = 100% if you write 4 means 1/4 = 25%
            bmpOfPoster = BitmapFactory.decodeStream(urlOfImage.openConnection().getInputStream(),
                    null, bmOptions);

            return bmpOfPoster;

        } catch (IOException e) {
            e.printStackTrace();
        }
        // if image could not be obtained
        return null;
    }

    protected void onPostExecute(Bitmap result) {

        // setting the image to an imageview where getPosterImage() is called
        posterImage.setImageBitmap(result);
    }
}
