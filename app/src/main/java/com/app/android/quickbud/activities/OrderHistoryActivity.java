package com.app.android.quickbud.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.app.android.quickbud.R;
import com.app.android.quickbud.adapters.OffersAdapter;
import com.app.android.quickbud.modelClasses.LocationListModel;
import com.app.android.quickbud.modelClasses.QTOffers;
import com.app.android.quickbud.network.VolleySingleton;
import com.app.android.quickbud.utils.Config;
import com.app.android.quickbud.utils.GPSTracker;
import com.app.android.quickbud.utils.SpecificMenuSingleton;
import com.yelp.clientlib.connection.YelpAPIFactory;
import com.yelp.clientlib.entities.Business;
import com.yelp.clientlib.entities.Deal;
import com.yelp.clientlib.entities.SearchResponse;
import com.yelp.clientlib.entities.options.CoordinateOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;

public class OrderHistoryActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private Toolbar toolbar;
    private ListView offerList;
    private TextView noOffersTxt;
    private ImageView loadingImage;
    private double latitude;
    private double longitude;
    private ArrayList<QTOffers> qtOffersArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);


        toolbar = (Toolbar) findViewById(R.id.app_bar);
        RelativeLayout cartContent = (RelativeLayout) findViewById(R.id.cart_content);
        ImageView drawerMenu = (ImageView) findViewById(R.id.menu_button);
        noOffersTxt = (TextView) findViewById(R.id.empty_offer_txt);
        loadingImage = (ImageView) findViewById(R.id.loading_image);
        offerList = (ListView) findViewById(R.id.offer_list);

        toolbar.setTitle("Special Offers");
        setSupportActionBar(toolbar);
        cartContent.setVisibility(View.GONE);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            toolbar.setBackgroundResource(R.mipmap.toolbar_image);
        }

        loadingImage.setVisibility(View.VISIBLE);
        offerList.setVisibility(View.GONE);

        drawerMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderHistoryActivity.this, MainMenuActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();

            }
        });

//        getPushMessages();

//        ArrayList<QTOffers> qtOffersArrayList = new ArrayList<>();
//        SharedPreferences preferences = getSharedPreferences("qt_offers", MODE_PRIVATE);
//        String offers = preferences.getString("offers", null);
//        if (offers != null){
//            try {
//                JSONArray jsonArray = new JSONArray(offers);
//
//                for (int i=0; i<jsonArray.length(); i++){
//                    JSONObject object = jsonArray.getJSONObject(i);
//                    QTOffers qtOffers = new QTOffers();
//                    qtOffers.setMsg(object.getString("message"));
//                    qtOffers.setRestaurantName(object.getString("rest_name"));
//                    qtOffers.setRestaurantAddress(object.getString("rest_address"));
//                    qtOffers.setRestaurantCity(object.getString("rest_city"));
//                    qtOffers.setRestaurantState(object.getString("rest_state"));
//                    qtOffers.setRestaurantPhone(object.getString("rest_phone"));
//                    qtOffers.setRestaurantImage(object.getString("rest_image"));
//                    qtOffers.setRestaurantZip(object.getString("rest_zip"));
//                    qtOffers.setOfferImage(object.getString("offer_image"));
//                    qtOffers.setTime(object.getString("time"));
//                    qtOffersArrayList.add(qtOffers);
//                }
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//            if (qtOffersArrayList.size() > 50){
//                for (int i=50; i<qtOffersArrayList.size(); i++){
//                    qtOffersArrayList.remove(i);
//                }
//            }
//
//            if (qtOffersArrayList.size() > 0){
//                offerList.setVisibility(View.VISIBLE);
//                noOffersTxt.setVisibility(View.GONE);
//                OffersAdapter offersAdapter = new OffersAdapter(this, qtOffersArrayList);
//                offerList.setAdapter(offersAdapter);
//            }else {
//                offerList.setVisibility(View.GONE);
//                noOffersTxt.setVisibility(View.VISIBLE);
//            }
//        }

//        FavoriteListFragment favoriteListFragment = new FavoriteListFragment();
//        getSupportFragmentManager().beginTransaction().replace(R.id.favorite_content, favoriteListFragment)
//                    .commit();

    }

//    @Override
//    public void onBackPressed() {
//
//        if(getSupportFragmentManager().getBackStackEntryCount() > 0){
//            getSupportFragmentManager().popBackStack();
//        }else {
//            super.onBackPressed();
//        }
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
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
            getPushMessages();
//            if (getIntent().getBooleanExtra("is_from_rest_list", false)) {
//                new YelpRestaurantDeals().execute();
//            } else {
//                new YelpRestaurantList().execute();
//            }
        } else {
            showGpsEnabledAlert();
        }
    }

    private void getPushMessages() {

        loadingImage.setVisibility(View.VISIBLE);
        offerList.setVisibility(View.GONE);

        SharedPreferences sharedPreferences = getSharedPreferences("user_info", Context.MODE_PRIVATE);
        String patronId = sharedPreferences.getString("patron_id", null);

        String url = Config.QT_OFFERS_URL + "?patron_id=" + patronId;

        System.out.println("url = " + url);

        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        System.out.println("response = " + response);

                        loadingImage.setVisibility(View.GONE);
                        offerList.setVisibility(View.VISIBLE);

                        qtOffersArrayList = new ArrayList<>();

                        try {
                            JSONArray pushArray = response.getJSONArray("results");

                            for (int i = 0; i < pushArray.length(); i++) {

                                JSONObject pushObj = pushArray.getJSONObject(i);

                                JSONObject object = pushObj.getJSONObject("extra_json");

//                                double restLatitude = object.getDouble("latitude");
//                                double restLongitude = object.getDouble("longitude");

//                                double distance = getAirDistance(restLatitude, restLongitude);

//                                if (distance < 40000) {
                                    QTOffers qtOffers = new QTOffers();
                                    if (object.has("placeId")) {
                                        qtOffers.setRestaurantName(object.getString("name"));
                                        qtOffers.setRestaurantAddress(object.getString("address_line"));
                                        qtOffers.setRestaurantCity(object.getString("city"));
                                        qtOffers.setRestaurantState(object.getString("state"));
                                        qtOffers.setRestaurantPhone(object.getString("phone_number"));
                                        qtOffers.setRestaurantImage(object.getString("client_url"));
                                        qtOffers.setRestaurantZip(object.getString("zip"));
                                        qtOffers.setPlaceId(object.getString("placeId"));
                                        qtOffers.setLatitude(object.getDouble("latitude"));
                                        qtOffers.setLongitude(object.getDouble("longitude"));
                                    }
                                    qtOffers.setYelp(false);
                                    if (!object.isNull("url")) {
                                        qtOffers.setOfferImage(object.getString("url"));
                                    }
                                    String date = pushObj.getString("created_on");

                                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yy");
                                    Date newDate = format.parse(date);
                                    qtOffers.setTime(dateFormat.format(newDate));
                                    qtOffers.setMsg(pushObj.getString("message"));

                                    qtOffersArrayList.add(qtOffers);
//                                }

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        if (qtOffersArrayList.size() > 0) {
                            offerList.setVisibility(View.VISIBLE);
                            noOffersTxt.setVisibility(View.GONE);
                            OffersAdapter offersAdapter = new OffersAdapter(OrderHistoryActivity.this, qtOffersArrayList);
                            offerList.setAdapter(offersAdapter);
                            offerList.setOnItemClickListener(OrderHistoryActivity.this);
                        } else {
                            offerList.setVisibility(View.GONE);
                            noOffersTxt.setVisibility(View.VISIBLE);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingImage.setVisibility(View.GONE);
                offerList.setVisibility(View.VISIBLE);
                if (error instanceof TimeoutError) {
                    Config.internetSlowError(OrderHistoryActivity.this);
                } else if (error instanceof NoConnectionError) {
                    Config.internetSlowError(OrderHistoryActivity.this);
                } else if (error instanceof ServerError) {
                    Config.serverError(OrderHistoryActivity.this);
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

//        if (qtOffersArrayList.get(position).isYelp()) {
        if (qtOffersArrayList.get(position).getPlaceId() != null){
            getQTSupportedRestaurants(qtOffersArrayList.get(position));
        }
//        } else {
//            new YelpRestaurantDetails().execute(qtOffersArrayList.get(position).getPlaceId());
//        }

    }

    private class YelpRestaurantList extends AsyncTask<Void, String, Void> {

        @Override
        protected Void doInBackground(Void... param) {
            YelpAPIFactory apiFactory = new YelpAPIFactory(Config.YELP_CONSUMER_KEY, Config.YELP_CONSUMER_SECRET,
                    Config.YELP_TOKEN, Config.YELP_TOKEN_SECRET);
            com.yelp.clientlib.connection.YelpAPI yelpAPI = apiFactory.createAPI();
            Map<String, String> params = new HashMap<>();
            // general params
            params.put("term", "Dispensaries");
//            params.put("radius_filter", "40000");
//            params.put("category_filter", "cannabis_clinics, cannabisreferrals, cannabisdispensaries");
            params.put("deals_filter", "true");
            CoordinateOptions coordinate = null;
            coordinate = CoordinateOptions.builder()
                    .latitude(latitude)
                    .longitude(longitude).build();
            Call<SearchResponse> call = yelpAPI.search(coordinate, params);
            retrofit2.Response<SearchResponse> response = null;
            try {
                response = call.execute();
                System.out.println("LOG: Restaurant List - " + response.body());

                qtOffersArrayList = new ArrayList<>();

                ArrayList<Business> businesses = response.body().businesses();

                for (int j = 0; j < businesses.size(); j++) {
                    ArrayList<Deal> dealsList = businesses.get(j).deals();

                    for (int k = 0; k < dealsList.size(); k++) {
                        QTOffers qtOffers = new QTOffers();
                        qtOffers.setMsg(dealsList.get(k).title());
                        qtOffers.setRestaurantName(businesses.get(j).name());
                        String restaurantAddress = businesses.get(j).location().address().toString();
                        String address = restaurantAddress.replace("[", "");
                        String finalAddress = address.replace("]", "");
                        qtOffers.setRestaurantAddress(finalAddress);
                        qtOffers.setRestaurantCity(businesses.get(j).location().city());
                        qtOffers.setRestaurantState(businesses.get(j).location().stateCode());
                        qtOffers.setRestaurantPhone(businesses.get(j).displayPhone());
                        qtOffers.setRestaurantImage(businesses.get(j).imageUrl());
                        qtOffers.setRestaurantZip(" ");
                        qtOffers.setPlaceId(businesses.get(j).id());
                        qtOffers.setYelp(true);
                        qtOffers.setOfferImage(dealsList.get(k).imageUrl());
                        qtOffers.setRestaurantDistance(businesses.get(j).distance());
                        qtOffers.setLatitude(businesses.get(j).location().coordinate().latitude());
                        qtOffers.setLongitude(businesses.get(j).location().coordinate().longitude());
                        qtOffers.setRatingImageUrl(businesses.get(j).ratingImgUrlLarge());
                        qtOffers.setRating(businesses.get(j).rating());
//                        Log.i("Offer date", ""+dealsList.get(k).timeStart());
//                        Long offerTime = dealsList.get(k).timeStart();
//                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yy");
//                        Date date = new Date(offerTime);
//                        qtOffers.setTime(dateFormat.format(date));

                        qtOffersArrayList.add(qtOffers);
                    }
                }

//                try {
//                    JSONArray pushArray = response.getJSONArray("results");
//
//                    for (int i = 0; i < pushArray.length(); i++) {
//
//                        JSONObject pushObj = pushArray.getJSONObject(i);
//
//                        JSONObject object = pushObj.getJSONObject("extra_json");
//
//                        QTOffers qtOffers = new QTOffers();
//                        qtOffers.setMsg(object.getString("message"));
//                        qtOffers.setRestaurantName(object.getString("name"));
//                        qtOffers.setRestaurantAddress(object.getString("address_line"));
//                        qtOffers.setRestaurantCity(object.getString("city"));
//                        qtOffers.setRestaurantState(object.getString("state"));
//                        qtOffers.setRestaurantPhone(object.getString("phone_number"));
//                        qtOffers.setRestaurantImage(object.getString("client_url"));
//                        qtOffers.setRestaurantZip(object.getString("zip"));
//                        if (!object.isNull("url")) {
//                            qtOffers.setOfferImage(object.getString("url"));
//                        }
//                        String date = pushObj.getString("created_on");
//
//                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
//                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yy");
//                        Date newDate = format.parse(date);
//                        qtOffers.setTime(dateFormat.format(newDate));
//
//                        qtOffersArrayList.add(qtOffers);
//
//                    }
//
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
            } catch (IOException e) {
                e.printStackTrace();


//                getPushMessages();
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(ListOfRestaurantActivity.this, "This service is not available in your location!", Toast.LENGTH_SHORT).show();
//                    }
//                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (!(qtOffersArrayList != null && qtOffersArrayList.size()>0)) {
                qtOffersArrayList = new ArrayList<>();
            }
            getPushMessages();
        }
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

    private class YelpRestaurantDeals extends AsyncTask<Void, String, Void> {

        @Override
        protected Void doInBackground(Void... param) {
            LocationListModel listModel = SpecificMenuSingleton.getInstance().getClickedRestaurant();
            YelpAPIFactory apiFactory = new YelpAPIFactory(Config.YELP_CONSUMER_KEY, Config.YELP_CONSUMER_SECRET,
                    Config.YELP_TOKEN, Config.YELP_TOKEN_SECRET);
            com.yelp.clientlib.connection.YelpAPI yelpAPI = apiFactory.createAPI();
            Call<Business> call = yelpAPI.getBusiness(listModel.getPlaceID());
            retrofit2.Response<Business> response = null;
            try {
                response = call.execute();

                qtOffersArrayList = new ArrayList<>();

                ArrayList<Deal> dealsList = response.body().deals();

                if (dealsList != null) {
                    for (int k = 0; k < dealsList.size(); k++) {
                        QTOffers qtOffers = new QTOffers();
                        qtOffers.setMsg(dealsList.get(k).title());
                        qtOffers.setRestaurantName(listModel.getRestaurantName());
                        qtOffers.setRestaurantAddress(listModel.getRestaurantAddress());
                        qtOffers.setRestaurantCity(listModel.getRestaurantCity());
                        qtOffers.setRestaurantState(listModel.getRestaurantState());
                        qtOffers.setRestaurantPhone(listModel.getRestaurantPhone());
                        qtOffers.setRestaurantImage(listModel.getRestaurantImage());
                        qtOffers.setRestaurantZip(" ");
                        qtOffers.setYelp(true);
                        qtOffers.setOfferImage(dealsList.get(k).imageUrl());
                        qtOffersArrayList.add(qtOffers);
                    }
                }

                getSpecialOffer();

            } catch (IOException e) {
                e.printStackTrace();

                qtOffersArrayList = new ArrayList<>();
                getSpecialOffer();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    private void getSpecialOffer() {

        loadingImage.setVisibility(View.VISIBLE);
        offerList.setVisibility(View.GONE);

        SharedPreferences sharedPreferences = getSharedPreferences("user_info", Context.MODE_PRIVATE);
        String patronId = sharedPreferences.getString("patron_id", null);

        String url = Config.QT_OFFERS_URL + "?patron_id=" + patronId;

        System.out.println("url = " + url);

        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        String placeId = SpecificMenuSingleton.getInstance().getClickedRestaurant().getPlaceID();

                        System.out.println("response = " + response);

                        loadingImage.setVisibility(View.GONE);
                        offerList.setVisibility(View.VISIBLE);

//                        ArrayList<QTOffers> qtOffersArrayList = new ArrayList<>();

                        try {
                            JSONArray pushArray = response.getJSONArray("results");

                            for (int i = 0; i < pushArray.length(); i++) {

                                JSONObject pushObj = pushArray.getJSONObject(i);

                                JSONObject object = pushObj.getJSONObject("extra_json");

                                if (placeId.equalsIgnoreCase(object.getString("placeId"))) {
                                    QTOffers qtOffers = new QTOffers();
                                    qtOffers.setMsg(object.getString("message"));
                                    qtOffers.setRestaurantName(object.getString("name"));
                                    qtOffers.setRestaurantAddress(object.getString("address_line"));
                                    qtOffers.setRestaurantCity(object.getString("city"));
                                    qtOffers.setRestaurantState(object.getString("state"));
                                    qtOffers.setRestaurantPhone(object.getString("phone_number"));
                                    qtOffers.setRestaurantImage(object.getString("client_url"));
                                    qtOffers.setRestaurantZip(object.getString("zip"));
                                    qtOffers.setYelp(false);
                                    if (!object.isNull("url")) {
                                        qtOffers.setOfferImage(object.getString("url"));
                                    }
                                    String date = pushObj.getString("created_on");

                                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yy");
                                    Date newDate = format.parse(date);
                                    qtOffers.setTime(dateFormat.format(newDate));

                                    qtOffersArrayList.add(qtOffers);
                                }

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        if (qtOffersArrayList.size() > 0) {
                            offerList.setVisibility(View.VISIBLE);
                            noOffersTxt.setVisibility(View.GONE);
                            OffersAdapter offersAdapter = new OffersAdapter(OrderHistoryActivity.this, qtOffersArrayList);
                            offerList.setAdapter(offersAdapter);
                        } else {
                            offerList.setVisibility(View.GONE);
                            noOffersTxt.setVisibility(View.VISIBLE);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingImage.setVisibility(View.GONE);
                offerList.setVisibility(View.VISIBLE);
                if (error instanceof TimeoutError) {
                    Config.internetSlowError(OrderHistoryActivity.this);
                } else if (error instanceof NoConnectionError) {
                    Config.internetSlowError(OrderHistoryActivity.this);
                } else if (error instanceof ServerError) {
                    Config.serverError(OrderHistoryActivity.this);
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

    private void getQTSupportedRestaurants(final QTOffers listModels) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Fetching info...");
        dialog.show();
        final String qtRestaurantURL = Config.QT_SUPPORTED_URL;
//        final String qtRestaurantURL = "http://192.168.1.21:13000/qt/core/certloc/cmpplaceId/";
        final JSONObject params = new JSONObject();
        JSONArray array = new JSONArray();
        try {
            JSONObject restObject = new JSONObject();
            restObject.put("name", listModels.getRestaurantName());
            restObject.put("placeId", listModels.getPlaceId());
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

                        try {
                            JSONArray restaurantList = response.getJSONArray("results");
                            JSONObject restaurantData = restaurantList.getJSONObject(0);

                            LocationListModel locationListModel = new LocationListModel();
                            locationListModel.setRestaurantName(listModels.getRestaurantName());
                            locationListModel.setRestaurantAddress(listModels.getRestaurantAddress());
                            locationListModel.setRestaurantCity(listModels.getRestaurantCity());
                            locationListModel.setRestaurantState(listModels.getRestaurantState());
                            locationListModel.setRestaurantDistance(listModels.getRestaurantDistance());
                            locationListModel.setRestaurantPhone(listModels.getRestaurantPhone());
                            locationListModel.setRatingImage(listModels.getRatingImageUrl());
                            locationListModel.setPlaceID(listModels.getPlaceId());
                            locationListModel.setLatitude(listModels.getLatitude());
                            locationListModel.setLongitude(listModels.getLongitude());
                            locationListModel.setRestRating(listModels.getRating());
                            locationListModel.setQtSupported(restaurantData.getBoolean("qt_supported"));
                            locationListModel.setRestaurantAccountName(restaurantData.getString("account_type"));
                            locationListModel.setFavoriteRestaurant(restaurantData.getBoolean("is_favorite"));
                            locationListModel.setDealActive(true);

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
                            }

                            SpecificMenuSingleton.getInstance().setClickedRestaurant(locationListModel);

                            Intent intent = new Intent(OrderHistoryActivity.this, RestaurantServices.class);
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
                        Config.internetSlowError(OrderHistoryActivity.this);
                    } else if (error instanceof NoConnectionError) {
                        Config.internetSlowError(OrderHistoryActivity.this);
                    } else if (error instanceof ServerError) {
                        Config.serverError(OrderHistoryActivity.this);
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

    private class YelpRestaurantDetails extends AsyncTask<String, String, Void> {
        LocationListModel locationListModel;
        ProgressDialog dialog = new ProgressDialog(OrderHistoryActivity.this);

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
            if (locationListModel != null) {
                getQTSupportedRestaurants(locationListModel);
            }
            if (dialog.isShowing()){
                dialog.dismiss();
            }
        }
    }

    private void getQTSupportedRestaurants(final LocationListModel listModels) {
        final ProgressDialog dialog = new ProgressDialog(OrderHistoryActivity.this);
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
                            locationListModel.setRestaurantPhone(listModels.getRestaurantPhone());
                            locationListModel.setRatingImage(listModels.getRatingImage());
                            locationListModel.setPlaceID(listModels.getPlaceID());
                            locationListModel.setLatitude(listModels.getLatitude());
                            locationListModel.setLongitude(listModels.getLongitude());
                            locationListModel.setRestRating(listModels.getRestRating());
                            locationListModel.setQtSupported(restaurantData.getBoolean("qt_supported"));
                            locationListModel.setRestaurantAccountName(restaurantData.getString("account_type"));
                            locationListModel.setFavoriteRestaurant(restaurantData.getBoolean("is_favorite"));
                            locationListModel.setDealActive(true);

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
                            }

                            SpecificMenuSingleton.getInstance().setClickedRestaurant(locationListModel);

                            Intent intent = new Intent(OrderHistoryActivity.this, RestaurantServices.class);
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
                        Config.internetSlowError(OrderHistoryActivity.this);
                    } else if (error instanceof NoConnectionError) {
                        Config.internetSlowError(OrderHistoryActivity.this);
                    } else if (error instanceof ServerError) {
                        Config.serverError(OrderHistoryActivity.this);
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

    private double getAirDistance(double restLatitude, double restLongitude){

        Location currentLocation = new Location("currentLocation");
        currentLocation.setLatitude(latitude);
        currentLocation.setLongitude(longitude);

        Location restLocation = new Location("restLocation");
        restLocation.setLatitude(restLatitude);
        restLocation.setLongitude(restLongitude);

        return currentLocation.distanceTo(restLocation);
    }
}
