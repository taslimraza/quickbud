package com.app.android.quickbud.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

/**
 * Created by mobi11 on 22/1/16.
 */
public class InternetConnectionError extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            Log.d("NetworkCheckReceiver", "NetworkCheckReceiver invoked...");

            boolean noConnectivity = intent.getBooleanExtra(
                    ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);

            if (!noConnectivity) {
                Log.d("NetworkCheckReceiver", "connected");
            } else {
                Log.d("NetworkCheckReceiver", "disconnected");
                ConnectionDetector connectionDetector = new ConnectionDetector(context);
                connectionDetector.internetError();
            }
        }
    }
}
