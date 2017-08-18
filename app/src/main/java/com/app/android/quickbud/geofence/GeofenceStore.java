package com.app.android.quickbud.geofence;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mobi11 on 24/12/15.
 */
public class GeofenceStore implements ConnectionCallbacks,
            OnConnectionFailedListener,ResultCallback<Status>, LocationListener{

    private Context context;
    private GoogleApiClient googleApiClient;
    private PendingIntent pendingIntent;
    private GeofencingRequest geofencingRequest;
    private LocationRequest locationRequest;
    private Geofence geofence;

    public GeofenceStore(Context context, Geofence geofence) {
        this.context = context;
        this.geofence = geofence;
        pendingIntent = null;

        googleApiClient = new GoogleApiClient.Builder(context)
                            .addApi(LocationServices.API).addConnectionCallbacks(this)
                            .addOnConnectionFailedListener(this).build();
        locationRequest = new LocationRequest();
        // location update at every 10 sec
        locationRequest.setInterval(30*1000);
        locationRequest.setFastestInterval(60*1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        googleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {

        geofencingRequest = new GeofencingRequest.Builder().addGeofence(geofence).build();

        pendingIntent = createRequestPendingIntent();

//        patronArrivedPost();

        LocationServices.FusedLocationApi.requestLocationUpdates(
                googleApiClient, locationRequest, this);

        PendingResult<Status> pendingResult = LocationServices.GeofencingApi
                        .addGeofences(googleApiClient, geofencingRequest, pendingIntent);

        pendingResult.setResultCallback(this);
    }

//    private void patronArrivedPost() {
//        SpecificMenuSingleton menuSingleton = SpecificMenuSingleton.getInstance();
//        String url = "http://159.203.88.161/qt/api/visit/enroute/?tenant_id=" + menuSingleton.getTenantId() + "&visit_id=" + menuSingleton.getVisitId() ;
//
//        Log.i("patron_arrived_post", url);
//        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        Log.i("patron_arrived_response", response.toString());
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//            }
//        });
//
//        VolleySingleton.getInstance(context).addToRequestQueue(request);
//
//    }

    private PendingIntent createRequestPendingIntent() {
        if(pendingIntent == null){
            Intent intent = new Intent(context, GeofenceTransitionIntentService.class);
            pendingIntent = PendingIntent.getService(context, 0, intent,
                                        PendingIntent.FLAG_UPDATE_CURRENT);
        }
        return pendingIntent;
    }

    public void removeRequestPendingIntent() {
        LocationServices.GeofencingApi.removeGeofences(googleApiClient,
                                    createRequestPendingIntent()).setResultCallback(this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onResult(Status status) {

    }

    public void removeGeofence(){
        List<String> geofenceIDs = new ArrayList<>();
        geofenceIDs.add(geofence.getRequestId());
        LocationServices.GeofencingApi.removeGeofences(googleApiClient, geofenceIDs);
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
    }
}
