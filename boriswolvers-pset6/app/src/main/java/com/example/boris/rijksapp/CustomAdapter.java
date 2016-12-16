package com.example.boris.rijksapp;
/**
 * Created by Boris on 11-12-2016.
 * This is the adapter Class for the recyclerviewer. Main function of this class is to set the
 * cardviews in the recyclerviewer with the right image and description. It also contains a call to
 * an onclicklistener.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder>{

    // Declaration of variables
    private Context context;
    private List<ArtData> my_data;

    // Initialize adapter
    public CustomAdapter(Context context, List<ArtData> my_data) {
        this.context = context;
        this.my_data = my_data;
    }

    // Registrates when a cardview item is clicked -> activates onlicklistener in MainActivity
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item,parent,false);

        itemView.setOnClickListener(MainActivity.myOnClickListener);

        return new ViewHolder(itemView);
    }

    // Sets a CardViewItem with the right image and text from the list (containing ArtData objects)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.description.setText(my_data.get(position).getDescription());
        new showPictureTask((holder.imageView)).execute(my_data.get(position).getImage_url());
    }

    @Override
    public int getItemCount() {
        return my_data.size();
    }

    // FindViewById methods for obtaining textview and imageview of CardViewItem
    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView description;
        public ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            description = (TextView)itemView.findViewById(R.id.textCard);
            imageView = (ImageView)itemView.findViewById(R.id.imageCard);
        }
    }
}
