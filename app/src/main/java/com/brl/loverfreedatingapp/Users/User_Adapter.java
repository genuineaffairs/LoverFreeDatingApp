package com.brl.loverfreedatingapp.Users;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.brl.loverfreedatingapp.R;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

/**
 * Created by AQEEL on 10/15/2018.
 */

public class User_Adapter  extends ArrayAdapter<Nearby_User_Get_Set> {

    public User_Adapter(Context context) {
        super(context, 0);
    }

    @Override
    public View getView(int position, View contentView, ViewGroup parent) {
        ViewHolder holder;

        if (contentView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            contentView = inflater.inflate(R.layout.item_user_layout, parent, false);
            holder = new ViewHolder(contentView);
            contentView.setTag(holder);
        } else {
            holder = (ViewHolder) contentView.getTag();
        }

        Nearby_User_Get_Set spot = getItem(position);

        //holder.distance_txt.setText(spot.getLocation());
        holder.distance_txt.setText("");
        holder.name.setText(spot.name);

        if(spot.getImagesurl().get(0).equals("")){
            Picasso.with(getContext())
                    .load(R.drawable.image_placeholder)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .placeholder(R.drawable.image_placeholder)
                    .into(holder.image);
        }else {
            Picasso.with(getContext())
                    .load(spot.getImagesurl().get(0))
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .placeholder(R.drawable.image_placeholder)
                    .into(holder.image);
        }
        return contentView;
    }

    private static class ViewHolder {
        public TextView name,age,distance_txt;
        public ImageView image;

        public ViewHolder(View view) {
            name=view.findViewById(R.id.username);
            image=view.findViewById(R.id.image);
            distance_txt=view.findViewById(R.id.distance_txt);

        }
    }

}
