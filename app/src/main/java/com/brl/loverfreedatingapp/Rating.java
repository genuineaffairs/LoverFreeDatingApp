package com.brl.loverfreedatingapp;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

public class Rating {


    private Context rateContext;

    public Rating(Context ct){

        rateContext = ct;
    }

    //Rating_purpose
    private boolean MyStartActivity(Intent aIntent) {
        try
        {
            rateContext.startActivity(aIntent);
            return true;
        }
        catch (ActivityNotFoundException e)
        {
            return false;
        }
    }

    public  void OnRateButtonClick() {


        Intent intent = new Intent(Intent.ACTION_VIEW);
        //Try Google play
        intent.setData(Uri.parse("market://details?id=com.brl.loverfreedatingapp"));
        if (!MyStartActivity(intent)) {
            //Market (Google play) app seems not installed, let's try to open a webbrowser
            intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.brl.loverfreedatingapp"));
            if (!MyStartActivity(intent)) {
                //Well if this also fails, we have run out of options, inform the user.

                Toast.makeText(rateContext, "Could not open Play Store, please install the Google play app.", Toast.LENGTH_LONG).show();
            }
        }

        //-------------------

        /*

        new AlertDialog.Builder(this)
                .setIcon(R.drawable.starsolo)
                .setTitle("রেটিং দিন")
                .setMessage("অ্যাপটি যদি আপনার ভাল লেগে থাকে তাহলে প্লে-ষ্টোরে এই অ্যাপটিকে রেটিং দিয়ে আমাদের উৎসাহিত করুন!")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //--

                    }

                })
                .setNegativeButton("Close", null)
                .show();

                */



    }

    public void rateAlert() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                rateContext);

        // set title
        //  alertDialogBuilder.setTitle("Exit");

        // set dialog message
        alertDialogBuilder
                .setTitle("Rate this app!")
                .setMessage("If you like this app then please rate it to encourage us.")
                .setCancelable(false)
                .setPositiveButton("Rate App", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        //   mp.stop();
                        //   mp.release();

                        OnRateButtonClick();
                        dialog.dismiss();


                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing

                        dialog.dismiss();
                    }
                })
                .create().
                show();


        // create alert dialog
        //AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        // alertDialog.show();
    }
}
