package com.brl.loverfreedatingapp.getwholikedme;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.brl.loverfreedatingapp.R;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class WhoLikedMeAdapter extends BaseAdapter {
    private Context context; //context
    private ArrayList<String> masterID = new ArrayList<String>();
    private ArrayList<String> slaveID = new ArrayList<String>();
    private ArrayList<String> masterName = new ArrayList<String>();
    private ArrayList<String> slaveName = new ArrayList<String>();
    private ArrayList<String> slaveUrl = new ArrayList<String>();

    public WhoLikedMeAdapter(Context context, ArrayList<String> masterID, ArrayList<String> slaveID, ArrayList<String> masterName, ArrayList<String> slaveName, ArrayList<String> slaveUrl) {
        this.context = context;
        this.masterID = masterID;
        this.slaveID = slaveID;
        this.masterName = masterName;
        this.slaveName = slaveName;
        this.slaveUrl = slaveUrl;
    }

    @Override
    public int getCount() {
        return masterID.size(); //returns total of items in the list
    }

    @Override
    public Object getItem(int position) {
        return masterID.get(position); //returns list item at the specified position
    }

    @Override
    public long getItemId(int position) {
        return position;
    }



    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // inflate the layout for each list row
        if (convertView == null) {
            convertView = LayoutInflater.from(context).
                    inflate(R.layout.wholikeme_row, parent, false);
        }




        final CircleImageView profile_image = (CircleImageView) convertView.findViewById(R.id.profile_image);
        TextView name = (TextView)convertView.findViewById(R.id.name);
        ImageButton unlike = (ImageButton)convertView.findViewById(R.id.unlike);
        ImageButton like = (ImageButton)convertView.findViewById(R.id.like);

        name.setText(slaveName.get(position));

        //--

        if(slaveUrl.get(position).equals("")){
            Picasso.with(context)
                    .load(R.drawable.image_placeholder)
                    //.memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .placeholder(R.drawable.image_placeholder)
                    .into(profile_image);
        }else {
            Picasso.with(context)
                    .load(slaveUrl.get(position))
                    //.memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .placeholder(R.drawable.image_placeholder)
                    .into(profile_image);
        }



        //-------------

        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



               int tempInt =  ((GetWhoLikedMe)context).likeButtonPressed(masterID.get(position),slaveID.get(position),masterName.get(position),slaveName.get(position),position);
               if(tempInt == 200){

                   masterID.remove(position);
                   slaveID.remove(position);
                   masterName.remove(position);
                   slaveName.remove(position);
                   slaveUrl.remove(position);
                   notifyDataSetChanged();
                   Toast.makeText(context, "You & " + slaveName.get(position) + " liked each other, Go to inbox & start chatting!", Toast.LENGTH_LONG).show();


               }else {

                   Toast.makeText(context, "Something is wrong! Please check your internet connection.", Toast.LENGTH_LONG).show();
               }

                GetWhoLikedMe gwm = new GetWhoLikedMe();
               gwm.tryNo = 0;




            }
        });

        unlike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ((GetWhoLikedMe)context).unlikeButtonPressed(masterID.get(position),slaveID.get(position),masterName.get(position),slaveName.get(position),position);
                masterID.remove(position);
                slaveID.remove(position);
                masterName.remove(position);
                slaveName.remove(position);
                slaveUrl.remove(position);
                notifyDataSetChanged();


            }
        });

        // returns the view for the current row
        return convertView;
    }
}
