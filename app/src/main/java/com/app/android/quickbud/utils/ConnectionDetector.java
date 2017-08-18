package com.app.android.quickbud.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;

import com.app.android.quickbud.R;

/**
 * Created by mobi11 on 3/10/15.
 */

public class ConnectionDetector {

    Context context;
    AlertDialog alertDialog;

    public ConnectionDetector(Context context) {
        this.context = context;
    }

    public boolean isConnectedToInternet() {
        if(context != null){
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager != null) {
                NetworkInfo ntw = connectivityManager.getActiveNetworkInfo();
                if (ntw != null && ntw.isConnectedOrConnecting()) {
                    return true;
                }
            }
        }
        return false;
    }

    public void internetError() {
        if(context != null){
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("NO NETWORK");
            builder.setMessage("Please check your Internet connection!");
            builder.setIcon(R.mipmap.no_network_icon);
            builder.setPositiveButton("Open Settings", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    context.startActivity(new Intent(Settings.ACTION_SETTINGS));

                }
            });
            builder.setNegativeButton("", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
//                ((Activity) context).finish();
                }
            });

            alertDialog = builder.create();
            alertDialog.setCancelable(true);
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();

            alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    dialog.dismiss();
                    ((Activity) context).finish();
                }
            });
        }
    }

    public void clearDialog(){
        if(alertDialog != null){
            if(alertDialog.isShowing()){
                alertDialog.dismiss();
            }
        }
    }
}
