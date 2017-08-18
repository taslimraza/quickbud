package com.app.android.quickbud.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.app.android.quickbud.activities.SplashActivity;
import com.app.android.quickbud.network.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class LocationService extends Service implements LocationListener {

    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;
    LocationManager locationManager;
    Location gpsLocation, networkLocation;
    double latitude;
    double longitude;
    double gpsAccuracy, networkAccuracy;
    String status = null;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60;
    private Location finalLoc = null;
    private float accuracy;
    private Long sendingTime = 15 * 60 * 1000L;
    private static Long defaultToTime;
    private static Long defaultFromTime;

    public LocationService() {
//        this.mContext = mContext;
//        getLocation();
    }

    private Location getLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isGPSEnabled && !isNetworkEnabled) {

        } else {

            if (isGPSEnabled) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                if (locationManager != null) {
                    this.canGetLocation = true;
                    gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (gpsLocation != null) {
                        latitude = gpsLocation.getLatitude();
                        longitude = gpsLocation.getLongitude();
                        gpsAccuracy = gpsLocation.getAccuracy();
                        status = "GPS";
                        Log.i("GPS Data", "accuracy - " + gpsAccuracy);
                    }
                }
            }

            if (isNetworkEnabled) {
//                if (location == null) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                if (locationManager != null) {
                    this.canGetLocation = true;
                    networkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (networkLocation != null) {
                        latitude = networkLocation.getLatitude();
                        longitude = networkLocation.getLongitude();
                        networkAccuracy = networkLocation.getAccuracy();
                        status = "Network";

                        Log.i("Network Data", "accuracy - " + networkAccuracy);
//                        SharedPreferences sharedPreferences = mContext.getSharedPreferences("lastLocation",0);
//                        SharedPreferences.Editor editor = sharedPreferences.edit();
//                        editor.putFloat("last_known_latitude", (float) latitude);
//                        editor.putFloat("last_known_longitude", (float) longitude);
//                        editor.commit();
                    }
                }
//                }
            }
        }

        if (gpsLocation != null && networkLocation != null) {
            this.canGetLocation = true;
            if (gpsLocation.getAccuracy() <= networkLocation.getAccuracy())
                finalLoc = gpsLocation;
            else
                finalLoc = networkLocation;

            // I used this just to get an idea (if both avail, its upto you which you want to take as I taken location with more accuracy)

        } else {

            if (gpsLocation != null) {
                this.canGetLocation = true;
                finalLoc = gpsLocation;
            } else if (networkLocation != null) {
                this.canGetLocation = true;
                finalLoc = networkLocation;
            }
        }
        System.out.println("Location: " + latitude + ", " + longitude);

        return finalLoc;
    }

    public Location getFinalLocation() {
        return finalLoc;
    }

    public void stopUsingGPS() {
//        if(locationManager != null){
//            locationManager.removeUpdates(GPSTracker.this);
//        }
    }

    public double getLatitude() {
        if (finalLoc != null) {
            latitude = finalLoc.getLatitude();
        }
        return latitude;
    }

    public double getLongitude() {
        if (finalLoc != null) {
            longitude = finalLoc.getLongitude();
        }
        return longitude;
    }

    public double getAccuracy() {
        if (finalLoc != null) {
            accuracy = finalLoc.getAccuracy();
        }
        return accuracy;
    }

    public String getStatus() {
//        if(location != null) {
//            accuracy = location.getAccuracy();
//        }
        return status;
    }

    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public void startLocation() {
        getLocation();
    }

    public void stopLocation() {
        locationManager.removeUpdates(this);
    }


    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setTimer();
//        SharedPreferences preferences = getSharedPreferences("user_info", Context.MODE_PRIVATE);
//        boolean isTimerSet = preferences.getBoolean("set_location_timer", false);
//        Log.i("isTimerSet", ""+isTimerSet);
//        if (isTimerSet)
//            setTimer();
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {

        Log.i("onStartCommand", "fire");

        if (getLocation() != null) {
            Location mLocation = getLocation();
            double latitude = mLocation.getLatitude();
            double longitude = mLocation.getLongitude();
            sendBroadcast(latitude, longitude);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    onStartCommand(intent, flags, startId);
                }
            }, 5000);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void sendBroadcast(double latitude, double longitude) {
        Intent intent = new Intent("locationAvailable");
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);
        sendBroadcast(intent);
    }

    private void setTimer() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setTimer();
                if (getLocation() != null) {
                    Location mLocation = getLocation();
                    double latitude = mLocation.getLatitude();
                    double longitude = mLocation.getLongitude();
                    Log.i("Timer based Location - ", "lat-" + latitude + " long-" + longitude);
//                    SharedPreferences preferences = getSharedPreferences("user_info", Context.MODE_PRIVATE);
//                    SharedPreferences.Editor editor = preferences.edit();
//                    editor.putBoolean("set_location_timer", false);
//                    editor.apply();
//                    if (latitude != 0.0)
                    sendLatLong();
                }
            }
        }, sendingTime);
    }

    private void sendLatLong() {
        SharedPreferences sharedPreference = getSharedPreferences("user_info", MODE_PRIVATE);
        final String sessionToken = sharedPreference.getString("session_token_id", null);
        String tokenId = sharedPreference.getString("TokenId", "No Token Found!");

        String url = Config.QB_LOCATION_SEND_URL;

        JSONObject object = new JSONObject();
        try {
            object.put("latitude", latitude);
            object.put("longitude", longitude);
            object.put("token", tokenId);
            Log.i("Timer", object.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("Timer Location response", response.toString());

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("Timer Location response", error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("QTAuthorization", sessionToken);
                return headers;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                10 * 1000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }
}

