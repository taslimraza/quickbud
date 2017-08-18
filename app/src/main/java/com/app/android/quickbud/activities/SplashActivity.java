package com.app.android.quickbud.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.multidex.MultiDex;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.app.android.quickbud.R;
import com.app.android.quickbud.network.VolleySingleton;
import com.app.android.quickbud.utils.Config;
import com.app.android.quickbud.utils.ConnectionDetector;
import com.app.android.quickbud.utils.LocationService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends AppCompatActivity {

    private ConnectionDetector connectionDetector;
    private final int REQUEST_CODE_SOME_FEATURES_PERMISSIONS = 12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        MultiDex.install(this);

        SharedPreferences sharedPreference = getSharedPreferences("user_info", MODE_PRIVATE);
        String userPhone = sharedPreference.getString("user_phone", null);
        Config.SESSION_TOKEN_ID = sharedPreference.getString("session_token_id", null);
//        if (userPhone != null){
//            new XMPPAsyncTask().execute();
//        }
        String deviceName = Build.MANUFACTURER;
        SharedPreferences sharedPreferences = getSharedPreferences("user_info", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("device_name", deviceName);
        editor.apply();

        SharedPreferences preferences = getSharedPreferences("chat_count_details", MODE_PRIVATE);
        SharedPreferences.Editor editor1 = preferences.edit();
        editor1.putString("chat_count", "0");
        editor1.apply();

        connectionDetector = new ConnectionDetector(this);
        if (connectionDetector.isConnectedToInternet()) {
            checkVersion();
        } else {
            connectionDetector.internetError();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("onResume", "=============================================================================");
//        SharedPreferences isRegistered = getSharedPreferences("Registration", MODE_PRIVATE);
//        boolean isReg = isRegistered.getBoolean("isFirstTime", false);
//        if (isReg){
//        connectionDetector = new ConnectionDetector(this);
//        if (connectionDetector.isConnectedToInternet()) {
//            checkVersion();
//        } else {
//            connectionDetector.internetError();
//        }
//        }else {
//            checkPermissionM();
//        }
    }

    private void splashScreenHandler() {
        connectionDetector = new ConnectionDetector(this);
        if (connectionDetector.isConnectedToInternet()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(SplashActivity.this, RegistrationActivity.class));
                    finish();
                }
            }, 2000);
        } else {
            connectionDetector.internetError();
        }
    }

    private void checkPermissionM() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            checkManifestPermissionSets();
        } else {
            splashScreenHandler();
        }
    }

    /**
     *
     */
    @TargetApi(23)
    private void checkManifestPermissionSets() {

        int cameraPermission = this.checkSelfPermission(
                Manifest.permission.CAMERA);

        int readExternalStoragePermission = this.checkSelfPermission(
                Manifest.permission.READ_EXTERNAL_STORAGE);

        int writeExternalStoragePermission = this.checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        int locationPermission = this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);

        int phonePermission = this.checkSelfPermission(Manifest.permission.CALL_PHONE);


        List<String> permissions = new ArrayList<String>();
        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (readExternalStoragePermission != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        if (writeExternalStoragePermission != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.CAMERA);
        }

        if (phonePermission != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.CALL_PHONE);
        }

        if (!permissions.isEmpty()) {
            requestPermissions(
                    permissions.toArray(new String[permissions.size()]),
                    REQUEST_CODE_SOME_FEATURES_PERMISSIONS);

        } else {
            splashScreenHandler();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {

            case REQUEST_CODE_SOME_FEATURES_PERMISSIONS: {
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        Log.d("Permissions", "Permission Granted: "
                                + permissions[i]);
                        splashScreenHandler();
                        break;
                    } else if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        Log.d("Permissions", "Permission Denied: " + permissions[i]);
                        Toast.makeText(this, "You've disabled the App required Permissions", Toast.LENGTH_SHORT).show();
                        break;

                    }
                }
            }
            break;

            default: {
                super.onRequestPermissionsResult(requestCode, permissions,
                        grantResults);
            }
        }
    }

    private void checkVersion() {
        String url = Config.APP_VERSION_URL;

        final JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.i("Version response", response.toString());
                        try {
                            JSONObject object = response.getJSONObject(0);
                            String versionNumber = object.getString("version_num");
                            String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;

                            if (versionName.compareTo(versionNumber) < 0) {

                                appUpdateDialog();

                            } else {
                                checkPermissionM();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error != null) {
                    if (error instanceof TimeoutError) {
                        Config.internetSlowError(SplashActivity.this);
                    } else if (error instanceof NoConnectionError) {
                        Config.internetSlowError(SplashActivity.this);
                    } else if (error instanceof ServerError) {
                        Config.serverError(SplashActivity.this);
                    }
                }
            }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(
                Config.TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        VolleySingleton.getInstance(this).addToRequestQueue(request);

    }

    private void appUpdateDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update required");
        builder.setMessage("Your QuickBud app is out of date, please download the new version to continue.");
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String packageName = getPackageName();
                try {
                    // if playstore installed
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    // else open in chrome
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)));
                }
                dialog.dismiss();
//                checkPermissionM();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
                finish();
            }
        });
    }
}