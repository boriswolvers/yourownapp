package com.example.boris.rijksapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Boris on 11-12-2016.
 */
public class CustomAdapterFirebase extends RecyclerView.ViewHolder {
    Context mcontext;
    View mview;

    public CustomAdapterFirebase(View itemView) {
        super(itemView);
        mview = itemView;
        mcontext = itemView.getContext();

    }

    public void onBindViewHolder(ArtData artData) {

        TextView description = (TextView)itemView.findViewById(R.id.textCardFavs);
        ImageView imageView = (ImageView)itemView.findViewById(R.id.imageCardFavs);

        description.setText(artData.getDescription());
        new showPictureTask((imageView)).execute(artData.getImage_url());
    }
}
