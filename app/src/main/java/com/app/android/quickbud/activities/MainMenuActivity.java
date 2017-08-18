package com.app.android.quickbud.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.app.android.quickbud.R;
import com.app.android.quickbud.adapters.ChatRestaurantListAdapter;
import com.app.android.quickbud.adapters.DrawerListAdapter;
import com.app.android.quickbud.adapters.MainMenuListAdapter;
import com.app.android.quickbud.interfaces.UserActionInterface;
import com.app.android.quickbud.modelClasses.LocationListModel;
import com.app.android.quickbud.modelClasses.MainMenuListModel;
import com.app.android.quickbud.network.VolleySingleton;
import com.app.android.quickbud.utils.Config;
import com.app.android.quickbud.utils.GPSTracker;
import com.app.android.quickbud.utils.Globals;
import com.app.android.quickbud.utils.SpecificMenuSingleton;
import com.bumptech.glide.Glide;
import com.yelp.clientlib.connection.YelpAPIFactory;
import com.yelp.clientlib.entities.Business;
import com.yelp.clientlib.entities.SearchResponse;
import com.yelp.clientlib.entities.options.CoordinateOptions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;

public class MainMenuActivity extends AppCompatActivity
        implements AdapterView.OnItemClickListener, View.OnClickListener, UserActionInterface {

    private ListView mainMenuList, restaurantListView, drawerList;
    private ArrayList<MainMenuListModel> mainMenuListModels;
    private int[] mainMenuImages = {R.mipmap.find_dispensaries, R.mipmap.special_offers, R.mipmap.profile_info,
            R.mipmap.bud_gallery, R.mipmap.top_buds, R.mipmap.bud_news};
    private String[] mainMenuTitles = {"Find Dispensaries", "Special Offers", "Profile Info",
            "Bud Gallery", "Top Buds"};
    private String[] mainMenuDescriptions = {"Search, See Ratings, View Details", "See Discount Offers", "See Favorites, Share with Friends",
            "View, Post, Like and Share", "Users Favorite Buds"};
    private GPSTracker gpsTracker;
    private double latitude;
    private double longitude;
    private String[] restaurantNamesList;
    private String[] restaurantIds;
    private int selectedRestaurant;
    private ArrayList<LocationListModel> restaurantList = new ArrayList<>();
    private RelativeLayout chatMsgLayout, postChatMsgBtn;
    private TextView chatMsgTxt, userName;
    private boolean isQuickChatClicked = false;
    private ChatRestaurantListAdapter adapter;
//    private XMPPTCPConnection connection;
    private DrawerLayout drawerLayout;
    private ImageView userImage;
    private boolean shouldCheckForPermission = true;
    private MainMenuListAdapter mainMenuListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        Globals.setCustomTypeface(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        RelativeLayout cartContent = (RelativeLayout) findViewById(R.id.cart_content);
        ImageView homeBtn = (ImageView) findViewById(R.id.menu_button);
        mainMenuList = (ListView) findViewById(R.id.main_menu_list);
        chatMsgLayout = (RelativeLayout) findViewById(R.id.chat_screen_layout);
        chatMsgTxt = (TextView) findViewById(R.id.quickchat_msg);
        restaurantListView = (ListView) findViewById(R.id.list_of_restaurant);
        postChatMsgBtn = (RelativeLayout) findViewById(R.id.post_chat_layout);
        drawerList = (ListView) findViewById(R.id.drawer_list);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        userName = (TextView) findViewById(R.id.user_name);
        userImage = (ImageView) findViewById(R.id.user_image);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            toolbar.setBackgroundResource(R.mipmap.toolbar_image);
        }

        SharedPreferences sharedPreference = getSharedPreferences("user_info", MODE_PRIVATE);
        Config.SESSION_TOKEN_ID = sharedPreference.getString("session_token_id", null);

//        toolbar.setTitle("Main Menu");
        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        cartContent.setVisibility(View.GONE);
        homeBtn.setVisibility(View.GONE);

        setMainMenuData();
        postChatMsgBtn.setOnClickListener(this);

        mainMenuListAdapter = new MainMenuListAdapter(this, mainMenuListModels);
        mainMenuList.setAdapter(mainMenuListAdapter);
        mainMenuList.setOnItemClickListener(this);

        drawerList.setAdapter(new DrawerListAdapter(this));

//        if (SpecificMenuSingleton.getInstance().getConnection() == null) {
//            new XMPPAsyncTask().execute();
//        }

        //         FOR USER PROFILE OPTIONS
        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 1:
                        // User Profile
                        startActivity(new Intent(MainMenuActivity.this, UserProfileActivity.class));
                        drawerLayout.closeDrawers();
                        break;
                    case 2:
                        // User Favorites Orders List
                        if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
                            drawerLayout.closeDrawers();
                        }
                        startActivity(new Intent(MainMenuActivity.this, MyFavoriteActivity.class));
                        break;

                    case 3:
                        if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
                            drawerLayout.closeDrawers();
                        }
                        shareApp();
                        break;

                    case 4:
                        startActivity(new Intent(MainMenuActivity.this, AboutUsActivity.class));
                        drawerLayout.closeDrawers();
                        break;
                }
            }
        });

    }

    private void setMainMenuData() {
        mainMenuListModels = new ArrayList<>();

        for (int i = 0; i < mainMenuImages.length; i++) {
            MainMenuListModel mainMenuListModel = new MainMenuListModel();
            mainMenuListModel.setMainMenuImage(mainMenuImages[i]);
//            mainMenuListModel.setMainMenuTitle(mainMenuTitles[i]);
//            mainMenuListModel.setMainMenuDescription(mainMenuDescriptions[i]);
            mainMenuListModels.add(mainMenuListModel);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            drawerLayout.closeDrawers();
        }else {
            super.onBackPressed();
        }
//        if (isQuickChatClicked) {
//            isQuickChatClicked = false;
//            mainMenuList.setVisibility(View.VISIBLE);
//            chatMsgLayout.setVisibility(View.GONE);
//            if (getSupportActionBar() != null) {
//                getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(false);
//                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
//            }
//            if (restaurantList != null && restaurantList.size() > 0) {
//                restaurantList.clear();
//            }
//        } else {
//            super.onBackPressed();
//        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        for(int i=0; i<mainMenuListModels.size(); i++){
            if (position == i){
                mainMenuListModels.get(i).setSelected(true);
            }else {
                mainMenuListModels.get(i).setSelected(false);
            }
        }
        mainMenuListAdapter.notifyDataSetChanged();
        switch (parent.getId()) {
            case R.id.main_menu_list:
                switch (position) {
                    case 0:
                        startActivity(new Intent(this, FindRestaurantActivity.class));
                        break;

                    case 1:
                        startActivity(new Intent(this, OrderHistoryActivity.class));
                        break;

                    case 2:
                        drawerLayout.openDrawer(Gravity.LEFT);
//                        startActivity(new Intent(this, UserProfileActivity.class));
                        break;

                    case 3:
                        Intent intent = new Intent(this, ChatImagesActivity.class);
                        intent.putExtra("isFromMainMenu", true);
                        startActivity(intent);
                        break;

                    case 4:
//                        setUpLocationData();
                        Intent topBudIntent = new Intent(this, TopBudsActivity.class);
                        topBudIntent.putExtra("isFromMainMenu", true);
                        topBudIntent.putExtra("currentLatitude", latitude);
                        topBudIntent.putExtra("currentLongitude", longitude);
                        startActivity(topBudIntent);
                        break;

                    case 5:
                        startActivity(new Intent(this, BudNewsActivity.class));
                        break;
                }
                break;

            case R.id.list_of_restaurant:
                view.setSelected(true);
                selectedRestaurant = position;
                openChatScreen(restaurantList.get(position));
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getUserProfile();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && shouldCheckForPermission) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
                } else {
                    Globals.showMissingPermissionDialog(this, null, getString(R.string.missing_permission_title),
                            getString(R.string.location_missing_message), 1000);
                }
            }
        } else {
            shouldCheckForPermission = false;
            setUpLocationData();
        }
    }

    @Override
    public void onDialogConfirmed(int actionType) {
        switch (actionType) {
            case 1000:
                finish();
                break;
        }
    }

    private void setUpLocationData() {
        gpsTracker = new GPSTracker(this);
        gpsTracker.startLocation();

        SharedPreferences sharedPreference = getSharedPreferences("user_info", MODE_PRIVATE);
        String profileName = sharedPreference.getString("user_name", null);
        String userAddress = sharedPreference.getString("user_address", null);
        String userImageUrl = sharedPreference.getString("user_image", null);
        userName.setText(profileName);
//        userLocation.setText(userAddress);
        userName.setTypeface(Globals.robotoBold);
//        userLocation.setTypeface(Globals.robotoRegular);
        Glide.with(MainMenuActivity.this).load(Config.QUICK_CHAT_IMAGE + userImageUrl)
                .asBitmap().placeholder(R.mipmap.default_profile_pic)
                .into(userImage);

//        Glide.with(this).load("https://s3-us-west-2.amazonaws.com/stagingquicktable/profile/" + userImageUrl)
//                .asBitmap().placeholder(R.mipmap.default_profile_pic)
//                .into(userImage);

        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        if (gpsTracker.canGetLocation()) {
            latitude = gpsTracker.getLatitude();
            longitude = gpsTracker.getLongitude();
//            getYelpKeys();
        } else {
            showGpsEnabledAlert();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 100:
                shouldCheckForPermission = !(grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_DENIED);
                if (grantResults[0] == PackageManager.PERMISSION_DENIED){
                    finish();
                }
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        shouldCheckForPermission = true;
        if (gpsTracker != null) {
            gpsTracker.stopLocation();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.post_chat_layout:
                if (restaurantList != null && restaurantList.size() > 0) {
                    if (restaurantListView.getChildAt(selectedRestaurant).isSelected()) {
                        openChatScreen(restaurantList.get(selectedRestaurant));
                    }
                }
                break;
        }
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

    class YelpRestaurantList extends AsyncTask<Void, String, Void> {
        ArrayList<LocationListModel> filteredLocationList = new ArrayList<>();
        ProgressDialog dialog = new ProgressDialog(MainMenuActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Fetching dispensaries data...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... param) {
            YelpAPIFactory apiFactory = new YelpAPIFactory(Config.YELP_CONSUMER_KEY, Config.YELP_CONSUMER_SECRET,
                    Config.YELP_TOKEN, Config.YELP_TOKEN_SECRET);
            com.yelp.clientlib.connection.YelpAPI yelpAPI = apiFactory.createAPI();
            Map<String, String> params = new HashMap<>();
            // general params
            params.put("term", "Dispensaries");
            params.put("radius_filter", "320");
            CoordinateOptions coordinate = CoordinateOptions.builder()
                    .latitude(latitude)
                    .longitude(longitude).build();

            Call<SearchResponse> call = yelpAPI.search(coordinate, params);
            retrofit2.Response<SearchResponse> response = null;
            try {
                response = call.execute();
                System.out.println("LOG: Restaurant List - " + response.body());
                ArrayList<Business> businesses = response.body().businesses();
                for (int i = 0; i < businesses.size(); i++) {
                    LocationListModel locationListModel = new LocationListModel();
                    locationListModel.setRestaurantName(businesses.get(i).name());
                    if (businesses.get(i).location().address().size() > 0) {
                        locationListModel.setRestaurantAddress(businesses.get(i).location().address().get(0));
                    }
                    locationListModel.setRestaurantCity(businesses.get(i).location().city());
                    locationListModel.setRestaurantState(businesses.get(i).location().stateCode());
                    locationListModel.setRestaurantDistance(businesses.get(i).distance());
                    locationListModel.setRestaurantPhone(businesses.get(i).displayPhone());
                    locationListModel.setRatingImage(businesses.get(i).ratingImgUrl());
                    locationListModel.setPlaceID(businesses.get(i).id());
                    locationListModel.setLatitude(businesses.get(i).location().coordinate().latitude());
                    locationListModel.setLongitude(businesses.get(i).location().coordinate().longitude());
                    filteredLocationList.add(locationListModel);
                }
            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        Toast.makeText(ListOfRestaurantActivity.this, "This service is not available in your location!", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            restaurantList.addAll(filteredLocationList);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            if (filteredLocationList.size() > 0) {
                if (filteredLocationList.size() == 1) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            openChatScreen(filteredLocationList.get(0));
//                            Toast.makeText(MainMenuActivity.this, "Fetching restaurant data!", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            chatMsgTxt.setText(getResources().getText(R.string.in_geofence));
                            chatMsgLayout.setVisibility(View.VISIBLE);
                            mainMenuList.setVisibility(View.GONE);
                            restaurantListView.setVisibility(View.VISIBLE);
                            restaurantNamesList = new String[filteredLocationList.size()];
                            restaurantIds = new String[filteredLocationList.size()];
                            for (int i = 0; i < filteredLocationList.size(); i++) {
                                restaurantNamesList[i] = filteredLocationList.get(i).getRestaurantName();
                                restaurantIds[i] = filteredLocationList.get(i).getPlaceID();
                            }
                            showChatRestaurantList();
//                            Toast.makeText(MainMenuActivity.this, "Fetching multiple restaurant data!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        chatMsgTxt.setText(getResources().getText(R.string.not_in_geofence));
                        chatMsgLayout.setVisibility(View.VISIBLE);
                        mainMenuList.setVisibility(View.GONE);
                        restaurantListView.setVisibility(View.GONE);
//                        Toast.makeText(MainMenuActivity.this, "You are not in a restaurant now!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    private void showChatRestaurantList() {

        adapter = new ChatRestaurantListAdapter(this, restaurantNamesList);
        restaurantListView.setAdapter(adapter);
        restaurantListView.setOnItemClickListener(this);

    }

    private void openChatScreen(LocationListModel locationListModels) {

        SpecificMenuSingleton.getInstance().setClickedRestaurant(locationListModels);
//        startActivity(new Intent(this, ChatActivity.class));

    }

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
//                configBuilder.setHost(Config.QUICK_CHAT_URL);
//                configBuilder.setPort(5222);
//                configBuilder.setServiceName(Config.QUICK_CHAT_URL);
//
//                connection = new XMPPTCPConnection(configBuilder.build());
//                connection.setPacketReplyTimeout(60000);
//                connection.connect();
//
//                Log.i("Login", "Try to login as " + userPhone);
////                Log.i("factualId", "FactualId " + SpecificMenuSingleton.getInstance().getClickedRestaurant().getPlaceID());
//                connection.login(userPhone, userPhone);
////                connection.login("917569550492", "917569550492");
//                Log.i("XMPPClient", "Logged in as " + connection.getUser());
////                Presence presence = new Presence(Presence.Type.available);
////                connection.sendPacket(presence);
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

    private void shareApp() {
        final SharedPreferences sharedPreferences = getSharedPreferences("user_info", Context.MODE_PRIVATE);
        String userName = sharedPreferences.getString("user_name", null);
//        Uri imageUri = Uri.parse("android.resource://" + getPackageName()+ "/mipmap/" + "icon_qt_small");
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

        // Add data to the intent, the receiving app will decide
        // what to do with it.
//        share.putExtra(Intent.EXTRA_STREAM, imageUri);
        share.putExtra(Intent.EXTRA_TEXT, "Hey ! " + userName.toUpperCase() + " has personally invited you to download the new QuickBud app.  Locate dispensaries, view menus, post pictures, and read the latest bud news.  Click the link below to download it now!\n" +
                "\n" +
                "Have a great day!\n\n" + "https://play.google.com/store/apps/details?id=com.app.android.quickbud&hl=en");

        startActivity(Intent.createChooser(share, "Share link!"));
    }

    private void getYelpKeys(){

//        final ProgressDialog dialog = new ProgressDialog(this);
//        dialog.setMessage("");
//        dialog.show();

        String url = Config.YELP_KEYS_URL;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.i("YelpKeyResponse", response.toString());

//                        dialog.dismiss();

                        try {
                            if (response.length() > 0){
                                JSONObject responseObj = response.getJSONObject(0);
                                Config.YELP_CONSUMER_KEY = responseObj.getString("consumer_key");
                                Config.YELP_CONSUMER_SECRET = responseObj.getString("consumer_secret");
                                Config.YELP_TOKEN = responseObj.getString("token");
                                Config.YELP_TOKEN_SECRET = responseObj.getString("token_secret");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

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

    private void getUserProfile() {

        SharedPreferences sharedPreferences = getSharedPreferences("user_info", MODE_PRIVATE);
        String patronId = sharedPreferences.getString("patron_id", null);

        String url = Config.QT_PATRON_UPDATE + patronId;


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            String imageUrl = response.getString("url");

                            if (imageUrl != null){
                                Glide.with(MainMenuActivity.this).load(Config.QUICK_CHAT_IMAGE + imageUrl)
                                        .asBitmap().fitCenter()
                                        .placeholder(R.mipmap.default_profile_pic)
                                        .into(userImage);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                loadingImage.setVisibility(View.GONE);
//                rootLayout.setVisibility(View.VISIBLE);
//                if (error != null){
//                    if (error instanceof TimeoutError) {
//                        Config.internetSlowError(UserProfileActivity.this);
//                    } else if (error instanceof NoConnectionError) {
//                        Config.internetSlowError(UserProfileActivity.this);
//                    } else if (error instanceof ServerError) {
//                        Config.serverError(UserProfileActivity.this);
//                    }
//                }
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
}
