package com.app.android.quickbud.utils;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by mobi11 on 3/10/15.
 */
public class GPSTracker implements LocationListener {

    private final Context mContext;
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
    private static final long MIN_TIME_BW_UPDATES = 1000 * 30;
    private Location finalLoc = null;
    private float accuracy;

    public GPSTracker(Context mContext) {
        this.mContext = mContext;
//        getLocation();
    }

    private Location getLocation() {
        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isGPSEnabled && !isNetworkEnabled) {

        } else {

            if (isGPSEnabled) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                if (locationManager != null){
                    this.canGetLocation=true;
                    gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if(gpsLocation != null){
                        latitude = gpsLocation.getLatitude();
                        longitude = gpsLocation.getLongitude();
                        gpsAccuracy = gpsLocation.getAccuracy();
                        status = "GPS";
                        Log.i("GPS Data", "accuracy - " + gpsAccuracy);
                    }
                }
            }

            if(isNetworkEnabled){
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

    public Location getFinalLocation(){
        return finalLoc;
    }

    public void stopUsingGPS() {
//        if(locationManager != null){
//            locationManager.removeUpdates(GPSTracker.this);
//        }
    }

    public double getLatitude() {
        if(finalLoc != null) {
            latitude = finalLoc.getLatitude();
        }
        return latitude;
    }

    public double getLongitude() {
        if(finalLoc != null) {
            longitude = finalLoc.getLongitude();
        }
        return longitude;
    }

    public double getAccuracy() {
        if(finalLoc != null) {
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

}
