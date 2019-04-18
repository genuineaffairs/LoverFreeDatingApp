package com.brl.loverfreedatingapp.Settings;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.gmail.samehadar.iosdialog.IOSDialog;
import com.brl.loverfreedatingapp.Accounts.Login_A;
import com.brl.loverfreedatingapp.CodeClasses.Variables;
import com.brl.loverfreedatingapp.Main_Menu.MainMenuActivity;
import com.brl.loverfreedatingapp.R;
import com.zhouyou.view.seekbar.SignSeekBar;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 */



// All the setting values will be used when we will get the Nearby user then we pass
    // that setting value as a parameter to the api


public class Setting_F extends Fragment {


    SwitchCompat men_switch,women_switch,show_me_on_binder;


    SignSeekBar distance_bar,age_seekbar;
    TextView age_range_txt,distance_txt;

    TextView privacy_policy_txt;

    View view;
    Context context;

    ImageButton back_btn;


    LinearLayout logout_btn,delete_account_btn;


    IOSDialog loadingDialog;

    public Setting_F() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_setting, container, false);
        context=getContext();


        loadingDialog = new IOSDialog.Builder(context)
                .setCancelable(false)
                .setSpinnerClockwise(false)
                .setMessageContentGravity(Gravity.END)
                .build();

        back_btn=view.findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        men_switch=view.findViewById(R.id.men_switch);
        women_switch=view.findViewById(R.id.women_switch);

        if(MainMenuActivity.sharedPreferences.getString(Variables.show_me,"all").equals("all")){
            men_switch.setChecked(true);
            women_switch.setChecked(true);
        }
        else if(MainMenuActivity.sharedPreferences.getString(Variables.show_me,"all").equals("male")){
            men_switch.setChecked(true);
        }
        else {
            women_switch.setChecked(true);
        }


        men_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                Variables.is_reload_users =true;

                if(!isChecked && !women_switch.isChecked()){
                    women_switch.setChecked(true);
                }else {
                    set_Show_me_data();
                }
            }
        });


        women_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                Variables.is_reload_users =true;

                if(!isChecked && !men_switch.isChecked()){
                    men_switch.setChecked(true);
                }else {
                    set_Show_me_data();
                }
            }
        });






        distance_bar=view.findViewById(R.id.distance_bar);
        distance_txt=view.findViewById(R.id.distance_txt);
        distance_bar.setProgress(MainMenuActivity.sharedPreferences.getInt(Variables.max_distance,Variables.default_distance));
        distance_txt.setText(MainMenuActivity.sharedPreferences.getInt(Variables.max_distance,Variables.default_distance)+" miles");

        distance_bar.setOnProgressChangedListener(new SignSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(SignSeekBar signSeekBar, int progress, float progressFloat, boolean fromUser) {
                distance_txt.setText(progress+" miles");
            }

            @Override
            public void getProgressOnActionUp(SignSeekBar signSeekBar, int progress, float progressFloat) {
                Variables.is_reload_users =true;
                MainMenuActivity.sharedPreferences.edit().putInt(Variables.max_distance,progress).commit();
            }

            @Override
            public void getProgressOnFinally(SignSeekBar signSeekBar, int progress, float progressFloat, boolean fromUser) {

                }
        });



       // this is the age seek bar. when we change the progress of seek bar it will be locally save
        age_seekbar=view.findViewById(R.id.age_seekbar);
        age_range_txt=view.findViewById(R.id.age_range_txt);
        age_seekbar.setProgress(MainMenuActivity.sharedPreferences.getInt(Variables.max_age,Variables.default_age));
        age_range_txt.setText(MainMenuActivity.sharedPreferences.getInt(Variables.max_age,Variables.default_age)+" Years");
        age_seekbar.setOnProgressChangedListener(new SignSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(SignSeekBar signSeekBar, int progress, float progressFloat, boolean fromUser) {
                age_range_txt.setText(progress+" Years");
            }

            @Override
            public void getProgressOnActionUp(SignSeekBar signSeekBar, int progress, float progressFloat) {
                Variables.is_reload_users =true;
                MainMenuActivity.sharedPreferences.edit().putInt(Variables.max_age,progress).commit();
            }

            @Override
            public void getProgressOnFinally(SignSeekBar signSeekBar, int progress, float progressFloat, boolean fromUser) {

            }
        });








        show_me_on_binder=view.findViewById(R.id.show_me_on_binder);
        show_me_on_binder.setChecked(MainMenuActivity.sharedPreferences.getBoolean(Variables.show_me_on_binder,true));
        show_me_on_binder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                MainMenuActivity.sharedPreferences.edit().putBoolean(Variables.show_me_on_binder,isChecked).commit();
                Variables.is_reload_users =true;
                if(isChecked){
                    Call_Api_For_show_on_binder_or_not("0");
                }else {
                    Call_Api_For_show_on_binder_or_not("1");
                }
            }
        });




        privacy_policy_txt=view.findViewById(R.id.privacy_policy_txt);
        privacy_policy_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://newgen-bd.com/turzo/dating_backend/privacy_policy.html"));
                startActivity(browserIntent);

            }
        });


        // on logout button click it will delete the user session and go to the login screen
        logout_btn=view.findViewById(R.id.logout_btn);
        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // on press logout btn we will chat the local value and move the user to login screen
                Logout_User();
            }
        });



        delete_account_btn=view.findViewById(R.id.delete_account_btn);
        delete_account_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Show_delete_account_alert();
            }
        });


        return view;

    }



    // below two methods are related with "save the value in database" that he is want to show
    // as a public or keep it private
    public void set_Show_me_data(){

        if(men_switch.isChecked() && women_switch.isChecked()){
            MainMenuActivity.sharedPreferences.edit().putString(Variables.show_me,"all").commit();

        }else if(!men_switch.isChecked() && women_switch.isChecked()){
            MainMenuActivity.sharedPreferences.edit().putString(Variables.show_me,"female").commit();
        }else if(men_switch.isChecked() && !women_switch.isChecked()){
            MainMenuActivity.sharedPreferences.edit().putString(Variables.show_me,"male").commit();
        }
    }

    private void Call_Api_For_show_on_binder_or_not(String status) {
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("fb_id", MainMenuActivity.user_id);
            parameters.put("status",status);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestQueue rq = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Variables.show_or_hide_profile, parameters, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        String respo=response.toString();

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Log.d("respo",error.toString());
                    }
                });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.getCache().clear();
        rq.add(jsonObjectRequest);
    }


    public void Show_delete_account_alert(){
        AlertDialog.Builder alert=new AlertDialog.Builder(context,R.style.DialogStyle);
        alert.setTitle("Are you Sure?")
                .setMessage("Delete an Account will Erase all of your data Completely")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Call_api_to_Delete_Account();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        alert.setCancelable(true);
        alert.show();
    }



    // below two method is used get the user pictures and about text from our server
    private void Call_api_to_Delete_Account() {
        loadingDialog.show();

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("fb_id", MainMenuActivity.user_id);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestQueue rq = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Variables.deleteAccount, parameters, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loadingDialog.cancel();
                        String respo=response.toString();
                        Log.d("responce",respo);

                        try {
                            JSONObject jsonObject=new JSONObject(respo);
                            String code=jsonObject.optString("code");
                            if(code.equals("200")){

                                Logout_User();

                            }
                        } catch (JSONException e) {

                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Log.d("respo",error.toString());
                        loadingDialog.cancel();
                    }
                });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.getCache().clear();
        rq.add(jsonObjectRequest);
    }




    public  void Logout_User(){
        SharedPreferences.Editor editor=MainMenuActivity.sharedPreferences.edit();

        editor.putBoolean(Variables.islogin,false);
        editor.putString(Variables.f_name,"").clear();
        editor.putString(Variables.l_name,"").clear();
        editor.putString(Variables.birth_day,"").clear();
        editor.putString(Variables.u_pic,"").clear();

        editor.commit();
        startActivity(new Intent(getActivity(), Login_A.class));
        getActivity().overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
        getActivity().finish();
    }


}
