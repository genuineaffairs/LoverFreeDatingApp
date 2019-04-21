package com.brl.loverfreedatingapp.getwholikedme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.brl.loverfreedatingapp.Accounts.Login_A;
import com.brl.loverfreedatingapp.CodeClasses.Functions;
import com.brl.loverfreedatingapp.CodeClasses.Variables;
import com.brl.loverfreedatingapp.Inbox.Inbox_F;
import com.brl.loverfreedatingapp.Main_Menu.MainMenuActivity;
import com.brl.loverfreedatingapp.Main_Menu.MainMenuFragment;
import com.brl.loverfreedatingapp.R;
import com.brl.loverfreedatingapp.Splash_A;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class GetWhoLikedMe extends AppCompatActivity {

    private String TAG = "GetWhoLikedMe";

    private ListView lv;
    private TextView noDataAvailable;
    private WhoLikedMeAdapter adapter;

    private ArrayList<String> masterID = new ArrayList<String>();
    private ArrayList<String> slaveID = new ArrayList<String>();
    private ArrayList<String> masterName = new ArrayList<String>();
    private ArrayList<String> slaveName = new ArrayList<String>();
    private ArrayList<String> slaveUrl = new ArrayList<String>();

    private MainMenuFragment mainMenuFragment;

    public static boolean getWhoFlag = false;
    DatabaseReference rootref;

    private int dataReturn = 0;
    public int tryNo = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_wholikedme);

        lv = (ListView)findViewById(R.id.listview);
        noDataAvailable = (TextView)findViewById(R.id.nodataavailable);

        rootref= FirebaseDatabase.getInstance().getReference();

        generatelistView();

        getWhoFlag = true;

    }

    public void generatelistView(){


        getWhoLikedMeList(MainMenuActivity.user_id);


    }


    public int likeButtonPressed(final String masterID, final String slaveID, final String masterName, final String slaveName, final int position) {


        //Toast.makeText(GetWhoLikedMe.this,masterName+" liked "+slaveName,Toast.LENGTH_LONG).show();

        //--


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

                    //Toast.makeText(context,"Added successfully!",Toast.LENGTH_LONG).show();
                    dataReturn = 200;

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

                   //--

                    Query query1=rootref.child("Match").child(slaveID).child(masterID);
                    query1.keepSynced(true);

                    query1.addListenerForSingleValueEvent(new ValueEventListener() {
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

                                //Toast.makeText(context,"Added successfully!",Toast.LENGTH_LONG).show();
                                dataReturn = 200;

                            }else {

                                dataReturn = 201;

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                            dataReturn = 201;

                        }
                    });



                    //----------------------


                    //Toast.makeText(context,"Something is wrong!",Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                dataReturn = 201;

            }
        });

        return dataReturn;

        //------------



    }

    public void unlikeButtonPressed(String masterID, String slaveID, String masterName, String slaveName,int position){


        Toast.makeText(GetWhoLikedMe.this,masterName+" unliked "+slaveName,Toast.LENGTH_LONG).show();


    }

    public void getWhoLikedMeList(String masterID){

        //--
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("fb_id", masterID);



        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(TAG,parameters.toString());

        RequestQueue rq = Volley.newRequestQueue(GetWhoLikedMe.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Variables.getWhoLikedMe, parameters, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        String respo=response.toString();
                        Log.d(TAG,respo);
                        //iosDialog.cancel();
                        //Parse_signup_data(respo);

                        String codeS = getCode(response);

                        if(codeS.equalsIgnoreCase("200")){


                            //lv.setVisibility(View.VISIBLE);
                            //noDataAvailable.setVisibility(View.GONE);
                            parse_whole_data(response);


                        }else if(codeS.equalsIgnoreCase("201")){

                            lv.setVisibility(View.GONE);
                            noDataAvailable.setVisibility(View.VISIBLE);


                        }else {

                            lv.setVisibility(View.GONE);
                            noDataAvailable.setVisibility(View.VISIBLE);


                        }



                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        //iosDialog.cancel();
                        //Toast.makeText(Login_A.this, "Something wrong with Api", Toast.LENGTH_SHORT).show();
                        //rgs.onHandleCodeFromModel(404);
                        //gt.onFetchDataFailure(1);
                        lv.setVisibility(View.GONE);
                        noDataAvailable.setVisibility(View.VISIBLE);
                        Log.d(TAG,error.toString());
                    }
                });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.getCache().clear();
        rq.add(jsonObjectRequest);


    }

    private String getCode(JSONObject job){

        String tempS = "";
        JSONObject jsonObject= job;
        try {

            tempS = jsonObject.getString("code");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return  tempS;

    }

    private void parse_whole_data(JSONObject job){

        masterID.clear();
        slaveID.clear();
        masterName.clear();
        slaveName.clear();
        slaveUrl.clear();





        try {
            JSONObject jsonObject= job;
            //String code=jsonObject.optString("code");

            JSONArray jsonArray=jsonObject.getJSONArray("msg");

            int temp = Integer.parseInt(jsonObject.getString("count"));
            Log.d(TAG,String.valueOf(temp)+"--------------------");
            for (int i=0; i<temp; i++){

                JSONObject userdata = jsonArray.getJSONObject(i);
                masterID.add(MainMenuActivity.user_id);
                slaveID.add(userdata.getString("fb_id"));
                masterName.add(MainMenuActivity.user_name);
                slaveName.add(userdata.getString("full_name"));
                slaveUrl.add(userdata.getString("image"));

                Log.d(TAG,slaveName.get(i));


            }

            adapter=new WhoLikedMeAdapter(this,masterID,slaveID,masterName,slaveName,slaveUrl);
            lv.setAdapter(adapter);

            lv.setVisibility(View.VISIBLE);
            noDataAvailable.setVisibility(View.GONE);

        } catch (JSONException e) {
            //iosDialog.cancel();
            e.printStackTrace();
        }




    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //Intent stI = new Intent(GetWhoLikedMe.this,MainMenuActivity.class);
        //startActivity(stI);
        finish();


    }


}