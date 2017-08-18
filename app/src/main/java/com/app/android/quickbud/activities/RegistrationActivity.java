package com.app.android.quickbud.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.app.android.quickbud.R;
import com.app.android.quickbud.fragments.RegistrationFragment;
import com.app.android.quickbud.fcmServices.RegistrationIntentService;
import com.app.android.quickbud.utils.Config;
import com.app.android.quickbud.utils.ConnectionDetector;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class RegistrationActivity extends AppCompatActivity {

    BroadcastReceiver broadcastReceiver;
//    private XMPPTCPConnection connection;
    public static boolean isAppKilled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ConnectionDetector connectionDetector = new ConnectionDetector(this);
        boolean isConnected = connectionDetector.isConnectedToInternet();
        if (!isConnected) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Not Connected to Internet");
            builder.setMessage("Press Connect to open Connection manager or Cancel to exit");
            builder.setPositiveButton("Connect", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.setCancelable(false);
            alertDialog.show();
        }

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences.getBoolean("sentTokenToServer", false);
                String token = sharedPreferences.getString("TokenId", "Not Found!");
                Log.i("FCM Token", token);


//                if(sentToken){
//                    textView.setText(getString(R.string.gcm_send_message));
//                    tokenID.setText("Token ID:- "+token);
//                }else{
//                    textView.setText(getString(R.string.token_error_message));
//                    tokenID.setText("No Token Found!");
//                }
            }
        };

        SharedPreferences isRegistered = getSharedPreferences("Registration", MODE_PRIVATE);
        SharedPreferences ewtInfo = getSharedPreferences("ewt_info", MODE_PRIVATE);
        boolean isReg = isRegistered.getBoolean("isFirstTime", false);

        if (!isReg) {
            if (checkPlayService()) {
                Intent intent = new Intent(this, RegistrationIntentService.class);
                startService(intent);
            }
            RegistrationFragment fragment = RegistrationFragment.newInstance();
            getSupportFragmentManager().beginTransaction().replace(R.id.main_activity, fragment).commit();
        } else {
            if (ewtInfo.getString("wait_time", null) != null) {
                Config.bookingStatus = true;
                isAppKilled = true;
                Intent intent = new Intent(this, OrderConfirmationActivity.class);
//                intent.putExtra("isDineIn", true);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            } else {
                isAppKilled = false;
//                startActivity(new Intent(this, ListOfRestaurantActivity.class));
                Intent intent = new Intent(this, MainMenuActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
            finish();
        }

//        registration();

    }

//    private void registration() {
//        SharedPreferences isRegistered = getSharedPreferences("Registration", MODE_PRIVATE);
//        SharedPreferences ewtInfo = getSharedPreferences("ewt_info", MODE_PRIVATE);
//        boolean isReg = isRegistered.getBoolean("isFirstTime", false);
//
//        if (!isReg) {
//            new XMPPAsyncTask().execute();
//            if (checkPlayService()) {
//                Intent intent = new Intent(this, RegistrationIntentService.class);
//                startService(intent);
//            }
//            RegistrationFragment fragment = RegistrationFragment.newInstance();
//            getSupportFragmentManager().beginTransaction().replace(R.id.main_activity, fragment).commit();
//        } else {
//            if (ewtInfo.getString("wait_time", null) != null) {
//                Config.bookingStatus = true;
//                Intent intent = new Intent(this, OrderConfirmationActivity.class);
////                intent.putExtra("isDineIn", true);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
//            } else {
//                startActivity(new Intent(this, ListOfRestaurantActivity.class));
//            }
//            finish();
//        }
//    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter("registrationComplete"));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        Log.d("Back Stack", "\t" + getSupportFragmentManager().getBackStackEntryCount());
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    private boolean checkPlayService() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        9000).show();
            } else {
                Log.i("MainActivity", "This device is not supported");
                finish();
            }
            return false;
        }
        return true;
    }

//    public void backgroundTask() {
//        new XMPPAsyncTask().execute();
//    }

//    public class XMPPAsyncTask extends AsyncTask<Void, Void, Void> {
//
//        @Override
//        protected Void doInBackground(Void... params) {
//
//            SharedPreferences sharedPreference = getSharedPreferences("user_info", MODE_PRIVATE);
//            String userPhone = sharedPreference.getString("user_phone", null);
//
//            try {
//                XMPPTCPConnectionConfiguration.Builder configBuilder = XMPPTCPConnectionConfiguration.builder();
//                configBuilder.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
//                configBuilder.setHost("159.203.88.161");
//                configBuilder.setPort(5222);
//                configBuilder.setServiceName("159.203.88.161");
//
//                connection = new XMPPTCPConnection(configBuilder.build());
//                connection.setPacketReplyTimeout(60 * 1000);
//                connection.connect();
//
//                Log.i("Login", "Try to login as " + userPhone);
////                Log.i("factualId", "FactualId " + SpecificMenuSingleton.getInstance().getClickedRestaurant().getPlaceID());
//                connection.login(userPhone, userPhone);
////                connection.login("917569550492", "917569550492");
//                Log.i("XMPPClient", "Logged in as " + connection.getUser());
//                Presence presence = new Presence(Presence.Type.available);
//                connection.sendPacket(presence);
////                manager = MultiUserChatManager.getInstanceFor(connection);
//
//                SpecificMenuSingleton menuSingleton = SpecificMenuSingleton.getInstance();
////                menuSingleton.setManager(manager);
//                menuSingleton.setConnection(connection);
//
//            } catch (XMPPException e) {
//                e.printStackTrace();
//            } catch (SmackException.NotConnectedException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            } catch (SmackException e) {
//                e.printStackTrace();
//            }
//
//            return null;
//        }
//    }
}
