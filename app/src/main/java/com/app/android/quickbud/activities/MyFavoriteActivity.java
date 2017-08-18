package com.app.android.quickbud.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.app.android.quickbud.R;
import com.app.android.quickbud.adapters.FavoriteRestListAdapter;
import com.app.android.quickbud.modelClasses.FavoriteRestListModel;
import com.app.android.quickbud.modelClasses.LocationListModel;
import com.app.android.quickbud.modelClasses.RestaurantTimingsModel;
import com.app.android.quickbud.network.VolleySingleton;
import com.app.android.quickbud.utils.Config;
import com.app.android.quickbud.utils.GPSTracker;
import com.app.android.quickbud.utils.SpecificMenuSingleton;
import com.yelp.clientlib.connection.YelpAPIFactory;
import com.yelp.clientlib.entities.Business;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;

public class MyFavoriteActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ListView favoriteRestListView;
    private ImageView loadingImage;
    private ArrayList<FavoriteRestListModel> favoriteRestListModels;
    private double latitude;
    private double longitude;
    private TextView noFavoriteListTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_favorite);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        RelativeLayout cartContent = (RelativeLayout) findViewById(R.id.cart_content);
        ImageView homeBtn = (ImageView) findViewById(R.id.menu_button);
        favoriteRestListView = (ListView) findViewById(R.id.favorite_rest_list);
        loadingImage = (ImageView) findViewById(R.id.loading_image);
        noFavoriteListTxt = (TextView) findViewById(R.id.no_favorite_list);

        cartContent.setVisibility(View.GONE);

        toolbar.setTitle("My Favorites");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.primary_color));
//        }

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyFavoriteActivity.this, MainMenuActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();

            }
        });

        favoriteRestListView.setOnItemClickListener(this);

        getFavoriteLocation();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        GPSTracker gpsTracker = new GPSTracker(this);
        gpsTracker.startLocation();
        if (gpsTracker.canGetLocation()) {
            latitude = gpsTracker.getLatitude();
            longitude = gpsTracker.getLongitude();
        } else {
            showGpsEnabledAlert();
        }
    }

    private void getFavoriteLocation() {

        loadingImage.setVisibility(View.VISIBLE);
        favoriteRestListView.setVisibility(View.GONE);
        favoriteRestListModels = new ArrayList<>();

        String url = Config.FAVORITE_REST_LIST;

        final JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        loadingImage.setVisibility(View.GONE);


                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject restDetails = response.getJSONObject(i);
                                FavoriteRestListModel favoriteRestListModel = new FavoriteRestListModel();
                                favoriteRestListModel.setRestName(restDetails.getString("name"));
//                                String address = restDetails.getString("address_line");
//                                String city = restDetails.getString("city");
//                                String state = restDetails.getString("state");
                                favoriteRestListModel.setRestAddressLine(restDetails.getString("address_line"));
                                favoriteRestListModel.setRestCity(restDetails.getString("city"));
                                favoriteRestListModel.setRestState(restDetails.getString("state"));
                                favoriteRestListModel.setPlaceId(restDetails.getString("placeId"));
                                favoriteRestListModels.add(favoriteRestListModel);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (favoriteRestListModels.size()>0){
                            favoriteRestListView.setVisibility(View.VISIBLE);
                            noFavoriteListTxt.setVisibility(View.GONE);
                        }else {
                            noFavoriteListTxt.setVisibility(View.VISIBLE);
                            favoriteRestListView.setVisibility(View.GONE);
                        }

                        FavoriteRestListAdapter adapter = new FavoriteRestListAdapter(MyFavoriteActivity.this, favoriteRestListModels);
                        favoriteRestListView.setAdapter(adapter);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                loadingImage.setVisibility(View.GONE);

                if (error != null) {
                    if (error instanceof TimeoutError) {
                        Config.internetSlowError(MyFavoriteActivity.this);
                    } else if (error instanceof NoConnectionError) {
                        Config.internetSlowError(MyFavoriteActivity.this);
                    } else if (error instanceof ServerError) {
                        Config.serverError(MyFavoriteActivity.this);
                    }
                }

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("QTAuthorization", Config.SESSION_TOKEN_ID);
                return headers;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                Config.TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

        showOptionsDialog(favoriteRestListModels.get(position));
//        ImageView favoriteImage = (ImageView) view.findViewById(R.id.favorite_star);
//        ImageView shareFavorite = (ImageView) view.findViewById(R.id.share_location);
//
//        favoriteImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                removeFavoriteLocation(favoriteRestListModels.get(position).getPlaceId());
//            }
//        });
//
//        shareFavorite.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                shareFavoriteLocation();
//            }
//        });
//
//        new YelpRestaurantDetails().execute(favoriteRestListModels.get(position).getPlaceId());

    }

    private void removeFavoriteLocation(String placeId) {

        loadingImage.setVisibility(View.VISIBLE);
        favoriteRestListView.setVisibility(View.GONE);

        String url = Config.FAVORITE_REST_LIST + placeId + "/delete";
        Log.i("RemoveFavorite", url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        loadingImage.setVisibility(View.GONE);
                        favoriteRestListView.setVisibility(View.VISIBLE);
                        getFavoriteLocation();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                loadingImage.setVisibility(View.GONE);

                if (error != null) {
                    if (error instanceof TimeoutError) {
                        Config.internetSlowError(MyFavoriteActivity.this);
                    } else if (error instanceof NoConnectionError) {
                        Config.internetSlowError(MyFavoriteActivity.this);
                    } else if (error instanceof ServerError) {
                        Config.serverError(MyFavoriteActivity.this);
                    }
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("QTAuthorization", Config.SESSION_TOKEN_ID);
                return headers;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                Config.TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    private class YelpRestaurantDetails extends AsyncTask<String, String, Void> {
        LocationListModel locationListModel;
        ProgressDialog dialog = new ProgressDialog(MyFavoriteActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Fetching info...");
            dialog.show();
        }

        @Override
        protected Void doInBackground(String... param) {
            YelpAPIFactory apiFactory = new YelpAPIFactory(Config.YELP_CONSUMER_KEY, Config.YELP_CONSUMER_SECRET,
                    Config.YELP_TOKEN, Config.YELP_TOKEN_SECRET);
            com.yelp.clientlib.connection.YelpAPI yelpAPI = apiFactory.createAPI();
            Call<Business> call = yelpAPI.getBusiness(param[0]);
            retrofit2.Response<Business> response = null;
            try {
                response = call.execute();
                System.out.println("LOG: Restaurant List - " + response.body());

                locationListModel = new LocationListModel();
                locationListModel.setRestaurantName(response.body().name());
                locationListModel.setRestaurantAddress(response.body().location().address().get(0));
                locationListModel.setRestaurantCity(response.body().location().city());
                locationListModel.setRestaurantState(response.body().location().stateCode());
                locationListModel.setRestaurantDistance(response.body().distance());
                locationListModel.setRestaurantPhone(response.body().displayPhone());
                locationListModel.setRatingImage(response.body().ratingImgUrl());
                locationListModel.setPlaceID(response.body().id());
                locationListModel.setLatitude(response.body().location().coordinate().latitude());
                locationListModel.setLongitude(response.body().location().coordinate().longitude());
                locationListModel.setRestRating(response.body().rating());

            } catch (IOException e) {
                e.printStackTrace();

//                qtOffersArrayList = new ArrayList<>();
//                getSpecialOffer();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
//            getQTSupportedRestaurants(locationListModel);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }

    private void getQTSupportedRestaurants(final LocationListModel listModels) {
        final ProgressDialog dialog = new ProgressDialog(MyFavoriteActivity.this);
        dialog.setMessage("Fetching info...");
        dialog.show();
        final String qtRestaurantURL = Config.QT_SUPPORTED_URL;
//        final String qtRestaurantURL = "http://192.168.1.21:13000/qt/core/certloc/cmpplaceId/";
        final JSONObject params = new JSONObject();
        JSONArray array = new JSONArray();
        try {
            JSONObject restObject = new JSONObject();
            restObject.put("name", listModels.getRestaurantName());
            restObject.put("placeId", listModels.getPlaceID());
            restObject.put("address", listModels.getRestaurantAddress());
            restObject.put("city", listModels.getRestaurantCity());
            restObject.put("state", listModels.getRestaurantState());
            array.put(restObject);
            params.put("placeId", array);
        } catch (JSONException e) {

        }

        Log.i("QTSupportedPostRequest", params.toString());

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, qtRestaurantURL, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }

                        Log.i("QtSupported", qtRestaurantURL);
                        Log.i("QTRestaurantList", response.toString());
                        Log.i("QTRestaurantList", params.toString());

                        try {
                            JSONArray restaurantList = response.getJSONArray("results");
                            JSONObject restaurantData = restaurantList.getJSONObject(0);

                            LocationListModel locationListModel = new LocationListModel();
                            locationListModel.setRestaurantName(listModels.getRestaurantName());
                            locationListModel.setRestaurantAddress(listModels.getRestaurantAddress());
                            locationListModel.setRestaurantCity(listModels.getRestaurantCity());
                            locationListModel.setRestaurantState(listModels.getRestaurantState());
                            locationListModel.setRestaurantDistance(listModels.getRestaurantDistance());
                            locationListModel.setRatingImage(listModels.getRatingImage());
                            locationListModel.setPlaceID(listModels.getPlaceID());
                            locationListModel.setLatitude(restaurantData.getDouble("latitude"));
                            locationListModel.setLongitude(restaurantData.getDouble("longitude"));
                            locationListModel.setRestRating(listModels.getRestRating());
                            locationListModel.setQtSupported(restaurantData.getBoolean("qt_supported"));
                            locationListModel.setRestaurantAccountName(restaurantData.getString("account_type"));
                            locationListModel.setFavoriteRestaurant(restaurantData.getBoolean("is_favorite"));

                            if (restaurantData.isNull("chats")) {
                                locationListModel.setRestaurantChatAvailable("0");
                            } else {
                                locationListModel.setRestaurantChatAvailable(restaurantData.getString("chats"));
                            }

                            JSONArray accountTypes = restaurantData.getJSONArray("account_type");
                            JSONObject accountType = accountTypes.getJSONObject(0);
                            locationListModel.setRestaurantAccountName(accountType.getString("account_name"));
                            locationListModel.setCarryOut(accountType.getBoolean("carry_out"));
                            locationListModel.setDisplayLogo(accountType.getBoolean("display_logo"));
                            locationListModel.setGetInLine(accountType.getBoolean("get_in_line"));
                            locationListModel.setPreOrder(accountType.getBoolean("pre_order"));
                            locationListModel.setInteractiveMenu(accountType.getBoolean("interactive_menu"));
                            locationListModel.setPickUpAvailable(accountType.getBoolean("pickup"));
                            locationListModel.setDeliveryAvailable(accountType.getBoolean("delivery"));
                            locationListModel.setAtm(restaurantData.getBoolean("atm"));
                            locationListModel.setMedical(restaurantData.getBoolean("medical"));
                            locationListModel.setRecreational(restaurantData.getBoolean("recreational"));

                            if (restaurantData.getBoolean("qt_supported")) {
                                locationListModel.setRestaurantPhone(restaurantData.getString("phone_number"));

                                if (restaurantData.getString("status").equalsIgnoreCase("A")) {
                                    locationListModel.setHostessOnline(true);
                                } else if (restaurantData.getString("status").equalsIgnoreCase("I")) {
                                    locationListModel.setHostessOnline(false);
                                }

                                locationListModel.setRestaurantImage(restaurantData.getString("url"));
                                locationListModel.setRestaurantEWT(restaurantData.getString("ewt"));
                                locationListModel.setLocationID(restaurantData.getInt("location_id"));
                                locationListModel.setTenantID(restaurantData.getInt("tenant_id"));
                                locationListModel.setRestaurantOpenTiming(restaurantData.getString("open_time"));
                                locationListModel.setRestaurantCloseTiming(restaurantData.getString("close_time"));
                                locationListModel.setRestaurantMenuUrl(restaurantData.getString("menu_url"));
                                locationListModel.setTimezone(restaurantData.getString("time_zone"));

                                ArrayList<RestaurantTimingsModel> timings = new ArrayList<>();
                                JSONArray hoursArray = restaurantData.getJSONArray("hours");
                                for (int k=0; k<hoursArray.length(); k++){
                                    RestaurantTimingsModel restaurantTimingsModel = new RestaurantTimingsModel();
                                    JSONObject hoursObj = hoursArray.getJSONObject(k);
                                    restaurantTimingsModel.setDay(hoursObj.getString("day"));
                                    if (hoursObj.getBoolean("opened")){
                                        String openTime = hoursObj.getString("open_time");
                                        String closeTime = hoursObj.getString("close_time");
                                        String[] openTimeSplit = openTime.split(":");
                                        if (Integer.parseInt(openTimeSplit[0]) > 12) {
                                            openTime = String.valueOf(Integer.parseInt(openTimeSplit[0]) - 12) + ":" + openTimeSplit[1] + " PM";
                                        }else if (Integer.parseInt(openTimeSplit[0]) == 12){
                                            openTime = String.valueOf(Integer.parseInt(openTimeSplit[0])) + ":" + openTimeSplit[1] + " PM";
                                        }else {
                                            openTime = openTimeSplit[0] + ":" + openTimeSplit[1] + " AM";
                                        }
                                        String[] closeTimeSplit = closeTime.split(":");
                                        if (Integer.parseInt(closeTimeSplit[0]) > 12) {
                                            closeTime = String.valueOf(Integer.parseInt(closeTimeSplit[0]) - 12) + ":" + closeTimeSplit[1] + " PM";
                                        }else if (Integer.parseInt(closeTimeSplit[0]) == 12){
                                            closeTime = String.valueOf(Integer.parseInt(closeTimeSplit[0])) + ":" + closeTimeSplit[1] + " PM";
                                        }else {
                                            closeTime = openTimeSplit[0] + ":" + openTimeSplit[1] + " AM";
                                        }
                                        restaurantTimingsModel.setHours(openTime + " - " + closeTime);
                                    }else {
                                        restaurantTimingsModel.setHours("Closed");
                                    }
                                    timings.add(restaurantTimingsModel);
                                }
                                locationListModel.setRestaurantTimings(timings);
                            }

                            SpecificMenuSingleton.getInstance().setClickedRestaurant(locationListModel);

                            Intent intent = new Intent(MyFavoriteActivity.this, RestaurantServices.class);
                            intent.putExtra("RestName", locationListModel.getRestaurantName());
                            intent.putExtra("RestAddress", locationListModel.getRestaurantAddress());
                            intent.putExtra("RestDistance", locationListModel.getRestaurantDistance());
                            intent.putExtra("RestChat", locationListModel.getRestaurantChatAvailable());
                            intent.putExtra("RestEWT", locationListModel.getRestaurantEWT());
                            intent.putExtra("RestPhone", locationListModel.getRestaurantPhone());
                            intent.putExtra("LocationId", locationListModel.getLocationID());
                            intent.putExtra("TenantId", locationListModel.getTenantID());
                            intent.putExtra("RestaurantLatitude", locationListModel.getLatitude());
                            intent.putExtra("RestaurantLongitude", locationListModel.getLongitude());
                            intent.putExtra("RestaurantImage", locationListModel.getRestaurantImage());
                            intent.putExtra("RestaurantMenuUrl", locationListModel.getRestaurantMenuUrl());
                            SpecificMenuSingleton.getInstance().setQtSupported(locationListModel.isQtSupported());
                            intent.putExtra("CurrentLatitude", latitude);
                            intent.putExtra("CurrentLongitude", longitude);
                            startActivity(intent);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error != null) {
                    if (error instanceof TimeoutError) {
                        Config.internetSlowError(MyFavoriteActivity.this);
                    } else if (error instanceof NoConnectionError) {
                        Config.internetSlowError(MyFavoriteActivity.this);
                    } else if (error instanceof ServerError) {
                        Config.serverError(MyFavoriteActivity.this);
                    }
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("QTAuthorization", Config.SESSION_TOKEN_ID);
                return headers;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                Config.TIMEOUT,  // 25 sec - timeout
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        VolleySingleton.getInstance(this).addToRequestQueue(request);

    }

    private void showGpsEnabledAlert() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Location Service Disabled");
        builder.setMessage("Please enable location services.");
        builder.setPositiveButton("Enable", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }

    private void showOptionsDialog(final FavoriteRestListModel favoriteRestListModel) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(MyFavoriteActivity.this);
        builderSingle.setTitle(favoriteRestListModel.getRestName());

        final LocationListModel locationListModel = new LocationListModel();
        locationListModel.setPlaceID(favoriteRestListModel.getPlaceId());
        locationListModel.setRestaurantAddress(favoriteRestListModel.getRestAddressLine());
        locationListModel.setRestaurantState(favoriteRestListModel.getRestState());
        locationListModel.setRestaurantCity(favoriteRestListModel.getRestCity());
        locationListModel.setRestaurantName(favoriteRestListModel.getRestName());

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                MyFavoriteActivity.this,
                R.layout.favorite_options);
        arrayAdapter.add("Open Dispensary");
        arrayAdapter.add("Share");
        arrayAdapter.add("Remove as favorite");
        arrayAdapter.add("Cancel");

//        builderSingle.setNegativeButton(
//                "cancel",
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                });

        builderSingle.setAdapter(
                arrayAdapter,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                getQTSupportedRestaurants(locationListModel);
                                break;
                            case 1:
                                shareFavoriteLocation(favoriteRestListModel.getRestName());
                                break;
                            case 2:
                                removeFavoriteLocation(favoriteRestListModel.getPlaceId());
                                break;
                            case 3:
                                dialog.dismiss();
                                break;
                        }
                    }
                });
        builderSingle.show();

    }

    private void shareFavoriteLocation(String restName) {
        final SharedPreferences sharedPreferences = getSharedPreferences("user_info", Context.MODE_PRIVATE);
        String userName = sharedPreferences.getString("user_name", null);
//        Uri imageUri = Uri.parse("android.resource://" + getPackageName()+ "/mipmap/" + "icon_qt_small");
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

        // Add data to the intent, the receiving app will decide
        // what to do with it.
//        share.putExtra(Intent.EXTRA_STREAM, imageUri);
        share.putExtra(Intent.EXTRA_TEXT, userName.toUpperCase() + " is sharing " + restName + " with you as a favorite restaurant in QuickTable.  " +
                "Click the restaurant name to save it to your favorites, or click on QuickTable to download the app and get connected" +
                "\n" +
                "Have a great day!\n\n" + "https://play.google.com/store/apps/details?id=com.app.android.quickbud&hl=en");

        startActivity(Intent.createChooser(share, "Share link!"));
    }

}
