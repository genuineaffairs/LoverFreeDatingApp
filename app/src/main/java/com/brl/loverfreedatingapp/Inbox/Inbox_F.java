package com.brl.loverfreedatingapp.Inbox;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.brl.loverfreedatingapp.Chat.Chat_Activity;
import com.brl.loverfreedatingapp.CodeClasses.Functions;
import com.brl.loverfreedatingapp.CodeClasses.Variables;
import com.brl.loverfreedatingapp.Main_Menu.MainMenuActivity;
import com.brl.loverfreedatingapp.Main_Menu.RelateToFragment_OnBack.RootFragment;
import com.brl.loverfreedatingapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class Inbox_F extends RootFragment {


    View view;
    Context context;

    RecyclerView inbox_list,match_list;

    ArrayList<Inbox_Get_Set> inbox_arraylist;

    ArrayList<Match_Get_Set>  matchs_users_list;

    DatabaseReference root_ref;

    Matches_Adapter matches_adapter;
    Inbox_Adapter inbox_adapter;

    boolean isview_created=false;

    private Button see_who_liked;
    DatabaseReference rootref;

    public Inbox_F() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_inbox, container, false);
        context=getContext();

        root_ref= FirebaseDatabase.getInstance().getReference();


        inbox_list=view.findViewById(R.id.inboxlist);
        match_list=view.findViewById(R.id.match_list);
        see_who_liked = (Button)view.findViewById(R.id.who_liked_button);

        // intialize the arraylist and and inboxlist
        inbox_arraylist=new ArrayList<>();

        inbox_list = (RecyclerView) view.findViewById(R.id.inboxlist);
        LinearLayoutManager layout = new LinearLayoutManager(context);
        inbox_list.setLayoutManager(layout);
        inbox_list.setHasFixedSize(false);
        inbox_adapter=new Inbox_Adapter(context, inbox_arraylist, new Inbox_Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(Inbox_Get_Set item) {

                // if user allow the stroage permission then we open the chat view
                if(check_ReadStoragepermission())
                chatFragment(MainMenuActivity.user_id,item.getId(),item.getName(),item.getPicture(),false);


            }
        }, new Inbox_Adapter.OnLongItemClickListener() {
            @Override
            public void onLongItemClick(Inbox_Get_Set item) {

            }
        });

        inbox_list.setAdapter(inbox_adapter);




        // intialize the arraylist and and upper Match list
        match_list=view.findViewById(R.id.match_list);
        matchs_users_list=new ArrayList<>();

        match_list.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false));

        matches_adapter=new Matches_Adapter(context, matchs_users_list, new Matches_Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(Match_Get_Set item) {

                // first check the storage permission then open the chat fragment
                if(check_ReadStoragepermission())
                chatFragment(MainMenuActivity.user_id,item.getU_id(),item.getUsername(),item.getPicture(),true);


            }
        }, new Matches_Adapter.OnLongItemClickListener() {
            @Override
            public void onLongItemClick(Match_Get_Set item) {

            }
        });


        match_list.setAdapter(matches_adapter);



        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Functions.hideSoftKeyboard(getActivity());
            }
        });

        //--
        see_who_liked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Toast.makeText(context,"Working",Toast.LENGTH_LONG).show();
                setForceMatch(MainMenuActivity.user_id,"8801834261758",MainMenuActivity.user_name,"Turzo");
            }
        });

        rootref= FirebaseDatabase.getInstance().getReference();


        //--------------------



        isview_created=true;

        return view;
    }


    // whenever there is focus in the third tab we will get the match list
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser && isview_created){
            get_match_data();
        }
    }



    // on start we will get the Inbox Message of user  which is show in bottom list of third tab
    ValueEventListener eventListener2;

    Query inbox_query;

    @Override
    public void onStart() {
        super.onStart();
        inbox_query=root_ref.child("Inbox").child(MainMenuActivity.user_id).orderByChild("date");
        eventListener2=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                inbox_arraylist.clear();

                for (DataSnapshot ds:dataSnapshot.getChildren()) {
                    Inbox_Get_Set model = new Inbox_Get_Set();
                    model.setId(ds.getKey());
                    model.setName(ds.child("name").getValue().toString());
                    model.setMessage(ds.child("msg").getValue().toString());
                    model.setTimestamp(ds.child("date").getValue().toString());
                    model.setStatus(ds.child("status").getValue().toString());
                    model.setPicture(ds.child("pic").getValue().toString());
                    inbox_arraylist.add(model);
                }
                Collections.reverse(inbox_arraylist);
                inbox_adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        inbox_query.addValueEventListener(eventListener2);


    }



    // on stop we will remove the listener
    @Override
    public void onStop() {
        super.onStop();
        if(inbox_query!=null)
        inbox_query.removeEventListener(eventListener2);
    }


    //open the chat fragment and on item click and pass your id and the other person id in which
    //you want to chat with them and this parameter is that is we move from match list or inbox list
    public void chatFragment(String senderid,String receiverid,String name,String picture,boolean is_match_exits){
        Chat_Activity chat_activity = new Chat_Activity();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
        Bundle args = new Bundle();
        args.putString("Sender_Id",senderid);
        args.putString("Receiver_Id",receiverid);
        args.putString("picture",picture);
        args.putString("name",name);
        args.putBoolean("is_match_exits",is_match_exits);
        chat_activity.setArguments(args);
        transaction.addToBackStack(null);
        transaction.replace(R.id.MainMenuFragment, chat_activity).commit();
    }



    //this method will check there is a storage permission given or not
    private boolean check_ReadStoragepermission(){
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        else {
            try {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        Variables.permission_Read_data );
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }
        return false;
    }



    // below two method will get all the  new user that is nearby of us  and parse the data in dataset
    // in that case which has a name of Nearby get set
    public void get_match_data() {

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("fb_id", MainMenuActivity.user_id);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("resp",parameters.toString());

        RequestQueue rq = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Variables.myMatch, parameters, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        String respo=response.toString();
                        Log.d("responce",respo);
                        Parse_user_info(respo);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.d("respo",error.toString());
                    }
                });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.getCache().clear();
        rq.add(jsonObjectRequest);
    }


    public void Parse_user_info(String loginData){
        try {
            JSONObject jsonObject=new JSONObject(loginData);
            String code=jsonObject.optString("code");
            if(code.equals("200")){
                matchs_users_list.clear();
                JSONArray msg=jsonObject.getJSONArray("msg");
                for (int i=0; i<msg.length();i++){
                    JSONObject userdata=msg.getJSONObject(i);
                    Match_Get_Set model = new Match_Get_Set();
                    model.setU_id(userdata.optString("effect_profile"));
                    JSONObject username_obj=userdata.getJSONObject("effect_profile_name");
                    model.setPicture(username_obj.optString("image1"));
                    model.setUsername(username_obj.optString("first_name")+" "+username_obj.optString("last_name"));
                    matchs_users_list.add(model);
                }
                matches_adapter.notifyDataSetChanged();

                if(matchs_users_list.isEmpty()){
                    view.findViewById(R.id.no_match_txt).setVisibility(View.VISIBLE);
                }else {
                    view.findViewById(R.id.no_match_txt).setVisibility(View.GONE);
                }

            }
        } catch (JSONException e) {

            e.printStackTrace();
        }


    }

    //--
    private void setForceMatch(final String masterID, final String slaveID, final String masterName, final String slaveName){

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("hh");
        final String formattedDate = df.format(c);

        Query query=rootref.child("Match").child(slaveID).child(masterID);
        query.keepSynced(true);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Map mymap=new HashMap<>();
                    mymap.put("match","true");
                    mymap.put("type","like");
                    mymap.put("status","0");
                    mymap.put("time",formattedDate);
                    mymap.put("name",slaveName);

                    Map othermap=new HashMap<>();
                    othermap.put("match","true");
                    othermap.put("type","like");
                    othermap.put("status","0");
                    othermap.put("time",formattedDate);
                    othermap.put("name",masterName);

                    rootref.child("Match").child(masterID+"/"+slaveID).updateChildren(mymap);
                    rootref.child("Match").child(slaveID+"/"+masterID).updateChildren(othermap);

                    Toast.makeText(context,"Added successfully!",Toast.LENGTH_LONG).show();

                }else {

                    Map mymap=new HashMap<>();
                    mymap.put("match","false");
                    mymap.put("type","like");
                    mymap.put("status","0");
                    mymap.put("time",formattedDate);
                    mymap.put("name",slaveName);
                    mymap.put("effect","true");

                    Map othermap=new HashMap<>();
                    othermap.put("match","false");
                    othermap.put("type","like");
                    othermap.put("status","0");
                    othermap.put("time",formattedDate);
                    othermap.put("name",masterName);
                    othermap.put("effect","false");

                    rootref.child("Match").child(masterID+"/"+slaveID).setValue(mymap);
                    rootref.child("Match").child(slaveID+"/"+masterID).setValue(othermap);

                    Toast.makeText(context,"Something is wrong!",Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    //-----------------



}
