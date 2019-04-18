package com.brl.loverfreedatingapp.Accounts;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.brl.loverfreedatingapp.GetCountry;
import com.brl.loverfreedatingapp.Main_Menu.MainMenuActivity;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.gmail.samehadar.iosdialog.IOSDialog;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.brl.loverfreedatingapp.CodeClasses.Variables;
import com.brl.loverfreedatingapp.CustomProgressBar;
import com.brl.loverfreedatingapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;


public class Login_A extends AppCompatActivity {


    DatabaseReference rootref;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    IOSDialog iosDialog;

    SharedPreferences sharedPreferences;

    RelativeLayout gmail_login_layout,phone_login_layout;

    //-- ad purpose
    private InterstitialAd mInterstitialAd;
    private boolean showAdd = true;
    private int buttonpressed = 0;

    public CustomProgressBar customProgressBar;
    private boolean showLoadingBar = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        rootref= FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        firebaseUser=mAuth.getCurrentUser();

        // if the user is already login trought facebook then we will logout the user automatically
        LoginManager.getInstance().logOut();

        iosDialog = new IOSDialog.Builder(this)
                .setCancelable(false)
                .setSpinnerClockwise(false)
                .setMessageContentGravity(Gravity.END)
                .build();

        sharedPreferences=getSharedPreferences(Variables.pref_name,MODE_PRIVATE);

        init();

        printKeyHash();

        //--custom loading bar area

        customProgressBar = new CustomProgressBar(Login_A.this);
        customProgressBar.show();
        stopLoadingBar();


        //---------
        String applicationID = "";
        String addID = "";
        /*
        GetCountry gc = new GetCountry();
        String locale = gc.getUserCountry(Login_A.this);
        Log.d("Locality",locale);


        //Toast.makeText(Login_A.this,locale,Toast.LENGTH_LONG).show();

        if(locale.equalsIgnoreCase("bd")){

            //-- Show test id
            applicationID = "ca-app-pub-3940256099942544~3347511713";
            addID = "ca-app-pub-3940256099942544/1033173712";


        }else {

            applicationID = "ca-app-pub-1891195426382339~9539042271";
            addID = "ca-app-pub-1891195426382339/7010039715";

        }
        */

        applicationID = "ca-app-pub-1891195426382339~9539042271";
        addID = "ca-app-pub-1891195426382339/7010039715";

        //--
        //--ad purpose
        MobileAds.initialize(Login_A.this, applicationID);
        mInterstitialAd = new InterstitialAd(Login_A.this);
        mInterstitialAd.setAdUnitId(addID);
        mInterstitialAd.loadAd(new AdRequest.Builder()
                .addTestDevice("547BABAE3AB2FE105C08D5339A13F684")
                .addTestDevice("8B85BEED0CE3521958318466B0AC1946")
                //turzo
                .build());

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {

            }

            @Override
            public void onAdFailedToLoad(int errorCode) {


            }

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {



                //---------
                //-- admob ads
                if(buttonpressed == 1){

                    /*
         if have an permision from facebook developer console to get the user_birthday
         and user_gender then you will use the below line
        */

/*
        LoginManager.getInstance().
         logInWithReadPermissions(Login_A.this,
                 Arrays.asList("public_profile","email","user_birthday","user_gender"));
*/

                    /*othetwise you will use the below line*/

                    LoginManager.getInstance()
                            .logInWithReadPermissions(Login_A.this,
                                    Arrays.asList("public_profile","email"));


                    Loginwith_FB();


                }else if(buttonpressed == 2){

                    Sign_in_with_gmail();


                }else if(buttonpressed == 3){

                    startActivity(new Intent(Login_A.this, Login_Phone_A.class));
                    overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);


                }





                //------------------------



            }
        });


        //------------------


        //---------

    }



    private ViewPager mPager;
    private void init() {

        // here is the code of slider which is move in login screen
        // in Account folder the Sliding_Adapter is also belong to this code

        mPager = (ViewPager) findViewById(R.id.pager);

        mPager.setAdapter(new Sliding_adapter(Login_A.this));

        TabLayout indicator = (TabLayout) findViewById(R.id.indicator);

        indicator.setupWithViewPager(mPager, true);

        indicator.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        //-- turzo
        gmail_login_layout=findViewById(R.id.gmail_login_layout);
        gmail_login_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //--
                if (mInterstitialAd.isLoaded() && showAdd) {

                    buttonpressed = 2;
                    mInterstitialAd.show();
                }else {

                    Log.d("TAG", "The interstitial wasn't loaded yet.");

                    Sign_in_with_gmail();

                }
                //-----

            }
        });


        // this is the login with phone btn and by press this button we will open the Login_phone_A Activity
        phone_login_layout=findViewById(R.id.phone_login_layout);
        phone_login_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //--
                if (mInterstitialAd.isLoaded() && showAdd) {

                    buttonpressed = 3;
                    mInterstitialAd.show();
                }else {

                    Log.d("TAG", "The interstitial wasn't loaded yet.");

                    startActivity(new Intent(Login_A.this, Login_Phone_A.class));
                    overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);

                }
                //-----



            }
        });




    }




    public void Login(View view) {





        //-- turzo 2

        if (mInterstitialAd.isLoaded() && showAdd) {

            buttonpressed = 1;
            mInterstitialAd.show();
        }else {

            Log.d("TAG", "The interstitial wasn't loaded yet.");

                    /*
         if have an permision from facebook developer console to get the user_birthday
         and user_gender then you will use the below line
        */

/*
        LoginManager.getInstance().
         logInWithReadPermissions(Login_A.this,
                 Arrays.asList("public_profile","email","user_birthday","user_gender"));
*/

            /*othetwise you will use the below line*/

            LoginManager.getInstance()
                    .logInWithReadPermissions(Login_A.this,
                            Arrays.asList("public_profile","email"));


            Loginwith_FB();

        }



    }

    private CallbackManager mCallbackManager;
    //facebook implimentation
    public void Loginwith_FB(){

        // initialze the facebook sdk and request to facebook for login
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>()  {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                // App code
                Toast.makeText(Login_A.this, "Login Cancel", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("resp",""+error.toString());
                Toast.makeText(Login_A.this, "Login Error"+error.toString(), Toast.LENGTH_SHORT).show();
            }

        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Pass the activity result back to the Facebook SDK
        if(requestCode==123){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
        else if(mCallbackManager!=null)
        mCallbackManager.onActivityResult(requestCode, resultCode, data);

    }



    private void handleFacebookAccessToken(final AccessToken token) {
        // if user is login then this method will call and
        // facebook will return us a token which will user for get the info of user
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            iosDialog.show();
                            final FirebaseUser user_firebase = mAuth.getCurrentUser();
                            final String firebase_userid=user_firebase.getUid();
                            final String id = Profile.getCurrentProfile().getId();
                            GraphRequest request = GraphRequest.newMeRequest(token, new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(JSONObject user, GraphResponse graphResponse) {

                                    Log.d("resp",user.toString());
                                    //after get the info of user we will pass to function which will store the info in our server
                                    Call_Api_For_Signup(""+id,""+user.optString("first_name")
                                    ,""+user.optString("last_name"),""+user.optString("birthday")
                                            ,""+user.optString("gender"),
                                    "https://graph.facebook.com/"+id+"/picture?width=500&width=500");

                                }
                            });

                            // here is the request to facebook sdk for which type of info we have required
                            Bundle parameters = new Bundle();
                            parameters.putString("fields", "last_name,first_name,email,birthday,gender");
                            request.setParameters(parameters);
                            request.executeAsync();
                        } else {

                            Toast.makeText(Login_A.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }




    //google Implimentation
    GoogleSignInClient mGoogleSignInClient;
    public void Sign_in_with_gmail(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(Login_A.this);
        if (account != null) {
            String id=account.getId();
            String f_name=account.getGivenName();
            String l_name=account.getFamilyName();

            if(account.getPhotoUrl()!=null) {
               String pic_url = account.getPhotoUrl().toString();
                Call_Api_For_Signup(id,f_name,l_name,"","" ,pic_url);
            }
            else {
               Get_User_info(id,f_name,l_name);
            }

        }
        else {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, 123);
        }

    }

    //Relate to google login
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            if (account != null) {
                String id=account.getId();
                String f_name=account.getGivenName();
                String l_name=account.getFamilyName();

                // if we do not get the picture of user then we will use default profile picture

                if(account.getPhotoUrl()!=null) {
                    String pic_url = account.getPhotoUrl().toString();
                    Call_Api_For_Signup(id,f_name, l_name,"","" ,pic_url);
                }else {

                    Get_User_info(id,f_name,l_name);
                }

                
            }
        } catch (ApiException e) {
            Log.w("Error message", "signInResult:failed code=" + e.getStatusCode());
        }

    }



    // this method will store the info of user to  database
    private void Call_Api_For_Signup(String user_id,
                                     String f_name,String l_name,
                                     String birthday,String gender,String picture) {

        iosDialog.show();

        f_name=f_name.replaceAll("\\W+","");
        l_name=l_name.replaceAll("\\W+","");

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("fb_id", user_id);
            parameters.put("first_name",f_name);
            parameters.put("last_name", l_name);
            parameters.put("birthday", birthday);
            parameters.put("gender", gender);
            parameters.put("image1",picture);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("resp",parameters.toString());

        RequestQueue rq = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Variables.SignUp, parameters, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        String respo=response.toString();
                        Log.d("responce",respo);
                        iosDialog.cancel();
                        Parse_signup_data(respo);

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        iosDialog.cancel();
                        Toast.makeText(Login_A.this, "Something wrong with Api", Toast.LENGTH_SHORT).show();
                        Log.d("respo",error.toString());
                    }
                });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.getCache().clear();
        rq.add(jsonObjectRequest);
    }



    private void Get_User_info(final String user_id,final String f_name,final String l_name) {
        iosDialog.show();
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("fb_id", user_id);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue rq = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, Variables.getUserInfo, parameters, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String respo=response.toString();
                        Log.d("responce",respo);

                        iosDialog.cancel();
                        try {
                            JSONObject jsonObject=new JSONObject(respo);
                            String code=jsonObject.optString("code");
                            if(code.equals("200")){

                                // if user is already logedin then we will save the user data and go to the enable location screen
                                JSONArray msg=jsonObject.getJSONArray("msg");
                                JSONObject userdata=msg.getJSONObject(0);

                                SharedPreferences.Editor editor=sharedPreferences.edit();
                                editor.putString(Variables.uid,user_id);
                                editor.putString(Variables.f_name,userdata.optString("first_name"));
                                editor.putString(Variables.l_name,userdata.optString("last_name"));
                                editor.putString(Variables.birth_day,userdata.optString("age"));
                                editor.putString(Variables.gender,userdata.optString("gender"));
                                editor.putString(Variables.u_pic,userdata.optString("image1"));
                                editor.putBoolean(Variables.islogin,true);
                                editor.commit();

                                // after all things done we will move the user to enable location screen
                                enable_location();


                            }else {
                                // if user is first time login then we will get the usser picture and name
                                Intent intent=new Intent(Login_A.this, Get_User_Info_A.class);
                                intent.putExtra("id",user_id);
                                intent.putExtra("fname",f_name);
                                intent.putExtra("lname",l_name);
                                startActivity(intent);
                                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                                finish();

                            }

                        }catch (JSONException e) {

                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Log.d("respo",error.toString());
                        iosDialog.cancel();
                    }
                });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        rq.getCache().clear();

        rq.add(jsonObjectRequest);
    }



    // if the signup successfull then this method will call and it store the user info in local
    public void Parse_signup_data(String loginData){
        try {
            JSONObject jsonObject=new JSONObject(loginData);
            String code=jsonObject.optString("code");
            if(code.equals("200")){
            JSONArray jsonArray=jsonObject.getJSONArray("msg");
                JSONObject userdata = jsonArray.getJSONObject(0);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putString(Variables.uid,userdata.optString("fb_id"));
                editor.putString(Variables.f_name,userdata.optString("first_name"));
                editor.putString(Variables.l_name,userdata.optString("last_name"));
                editor.putString(Variables.birth_day,userdata.optString("age"));
                editor.putString(Variables.gender,userdata.optString("gender"));
                editor.putString(Variables.u_pic,userdata.optString("image1"));
                editor.putBoolean(Variables.islogin,true);
                editor.commit();

                // after all things done we will move the user to enable location screen
                enable_location();
            }else {
                Toast.makeText(this, ""+jsonObject.optString("msg"), Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            iosDialog.cancel();
            e.printStackTrace();
        }

    }



    private void enable_location() {
        // will move the user for enable location screen
        Enable_location_F enable_location_f = new Enable_location_F();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left,R.anim.in_from_left,R.anim.out_to_right);
        getSupportFragmentManager().popBackStackImmediate();
        transaction.replace(R.id.Login_A, enable_location_f).addToBackStack(null).commit();
    }



    public void printKeyHash()  {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName() , PackageManager.GET_SIGNATURES);
            for(Signature signature:info.signatures)
            {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.i("keyhash" , Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private void stopLoadingBar(){

        if(!showLoadingBar){

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    customProgressBar.hide();
                    //Toast.makeText(Matched_Activity.this, "No match found!", Toast.LENGTH_LONG).show();

                }
            }, 5000);

            showLoadingBar = true;
        }

    }




 }
