package com.example.boris.rijksapp;
/**
 * Created by Boris on 11-12-2016.
 * This is the adapter Class for the recyclerviewer of Firebase. Main function of this class is to
 * set the cardviews in the Firebase recyclerviewer with the right image and description.
 */
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomAdapterFirebase extends RecyclerView.ViewHolder {

    // Declaration of variables
    Context mcontext;
    View mview;

    // Initialize adapter
    public CustomAdapterFirebase(View itemView) {
        super(itemView);
        mview = itemView;
        mcontext = itemView.getContext();

    }

    // Every change in the realtime database will trigger this function to updates CardViewItem
    public void onBindViewHolder(ArtData artData) {

        TextView description = (TextView)itemView.findViewById(R.id.textCardFavs);
        ImageView imageView = (ImageView)itemView.findViewById(R.id.imageCardFavs);

        description.setText(artData.getDescription());
        new showPictureTask((imageView)).execute(artData.getImage_url());
    }
}
