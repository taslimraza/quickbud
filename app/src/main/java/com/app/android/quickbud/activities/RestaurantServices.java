package com.app.android.quickbud.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.app.android.quickbud.adapters.RestaurantServicesAdapter;
import com.app.android.quickbud.fragments.MenuItemFragment;
import com.app.android.quickbud.geofence.GeofenceStore;
import com.app.android.quickbud.modelClasses.LocationListModel;
import com.app.android.quickbud.modelClasses.RestaurantServicesModel;
import com.app.android.quickbud.modelClasses.RestaurantTimingsModel;
import com.app.android.quickbud.network.VolleySingleton;
import com.app.android.quickbud.utils.CartSingleton;
import com.app.android.quickbud.utils.Config;
import com.app.android.quickbud.utils.GPSTracker;
import com.app.android.quickbud.utils.SpecificMenuSingleton;
import com.bumptech.glide.Glide;
import com.google.android.gms.location.Geofence;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class RestaurantServices extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    TextView userName, userLocation, restChats, recreational, atm, medical;
    DrawerLayout drawerLayout;
    ListView drawerList;
    ImageView homeBtn, userImage, favoriteLocationImg, ratingImage;
    Button callUs, getDirection, restaurantHours;
    RelativeLayout cartContent;
    private ArrayList<LocationListModel> locationListModels;
    private boolean isQTSupported = false;
    private String restName;
    private int locationId, tenantId, clickPosition;
    private SpecificMenuSingleton menuSingleton;
    private double sourceLatitude, sourceLongitude, destinationLatitude, destinationLongitude;
    private LocationListModel currentRest;
    private String restType;
    private Geofence geofence;
    private GeofenceStore geofenceStore;
    private String restMenuUrl = null;
    private String currentTime = null, openTime = null, closeTime = null;
    private boolean isRestFav = false;
    private GPSTracker gpsTracker;
    private RestaurantServicesAdapter adapter;

    private String[] titleList = {"View Menu & Specials", "Our Top Buds", "Our Bud Gallery"};
    private String[] subTitleList = {"Pre-Order for Pickup or Delivery",
            "Users Favorite Buds", "View, Post, Like and share"};
    private int[] imageList= {R.mipmap.view_menus,
            R.mipmap.our_top_buds,
            R.mipmap.our_bud_gallery};
    List<RestaurantServicesModel> restaurantServicesModels;

    //    public RestaurantServices(ArrayList<LocationListModel> locationListModels){
//        this.locationListModels = locationListModels;
//    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        RestaurantServices.this.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        TextView restaurantName = (TextView) findViewById(R.id.rest_name);
        TextView restaurantAddress = (TextView) findViewById(R.id.rest_address);
//        TextView restaurantDistance = (TextView) findViewById(R.id.rest_distance);
//        restChats = (TextView) findViewById(R.id.chats);
//        TextView restEWT = (TextView) findViewById(R.id.rest_ewt);
        getDirection = (Button) findViewById(R.id.get_direction);
//        drawerList = (ListView) findViewById(R.id.drawer_list);
//        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        callUs = (Button) findViewById(R.id.call_us);
        restaurantHours = (Button) findViewById(R.id.hours);
//        userName = (TextView) findViewById(R.id.user_name);
        userLocation = (TextView) findViewById(R.id.user_location);
        cartContent = (RelativeLayout) findViewById(R.id.cart_content);
//        userImage = (ImageView) findViewById(R.id.user_image);
        ratingImage = (ImageView) findViewById(R.id.rating_image);
        favoriteLocationImg = (ImageView) findViewById(R.id.location_favorite);
        ImageView dispensaryLogo = (ImageView) findViewById(R.id.dispensary_logo);
        recreational = (TextView) findViewById(R.id.recreational);
        atm = (TextView) findViewById(R.id.atm);
        medical = (TextView) findViewById(R.id.medical);

        favoriteLocationImg.setOnClickListener(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            toolbar.setBackgroundColor(Color.TRANSPARENT);
        }

        toolbar.setTitle(" ");
//        toolbar.getBackground().setAlpha(0);
        setSupportActionBar(toolbar);

        cartContent.setVisibility(View.GONE);

//        drawerList.setAdapter(new DrawerListAdapter(this));
        homeBtn = (ImageView) findViewById(R.id.menu_button);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        restaurantServicesModels = new ArrayList<>();

        Intent intent = getIntent();
        restName = intent.getStringExtra("RestName");
        String restAddress = intent.getStringExtra("RestAddress");
        Double restDistance = intent.getDoubleExtra("RestDistance", 0.0);
        String restChat = intent.getStringExtra("RestChat");
        String restEwt = intent.getStringExtra("RestEWT");
        final String restPhone = intent.getStringExtra("RestPhone");
        int restPosition = intent.getIntExtra("RestPosition", 0);
        locationId = intent.getIntExtra("LocationId", 0);
        tenantId = intent.getIntExtra("TenantId", 0);
        sourceLatitude = intent.getDoubleExtra("CurrentLatitude", 0);
        sourceLongitude = intent.getDoubleExtra("CurrentLongitude", 0);
        destinationLatitude = intent.getDoubleExtra("RestaurantLatitude", 0);
        destinationLongitude = intent.getDoubleExtra("RestaurantLongitude", 0);
        String restImage = intent.getStringExtra("RestaurantImage");
        restMenuUrl = intent.getStringExtra("RestaurantMenuUrl");
        restaurantName.setText(restName);

//        String distance;
//        if (Config.APP_NAME.equalsIgnoreCase("QuickTable")) {
//            distance = String.format("%.2fmi", restDistance / 1609.344);
//        } else {
//            distance = String.format("%.2fmi", restDistance);
//        }
//        restaurantDistance.setText(distance);

        menuSingleton = SpecificMenuSingleton.getInstance();
        menuSingleton.setLatitude(destinationLatitude);
        menuSingleton.setLongitude(destinationLongitude);
        menuSingleton.setImageUrl(restImage);

        StringBuilder builder = new StringBuilder();
        if (menuSingleton.getClickedRestaurant().getRestaurantAddress() != null) {
            builder.append(menuSingleton.getClickedRestaurant().getRestaurantAddress());
            builder.append("\n" + menuSingleton.getClickedRestaurant().getRestaurantCity());
            builder.append(", " + menuSingleton.getClickedRestaurant().getRestaurantState());
            restaurantAddress.setText(builder);
        }

//        if (restEwt != null) {
//            String[] timeArray = restEwt.split(":");
//            if (timeArray[0].equals("00") && timeArray[1].equals("00")) {
//                restEWT.setText("Wait-" + "0" + " mins");
//            } else if (!timeArray[1].equals("00")) {
//                restEWT.setText("Wait-" + timeArray[1] + " mins");
//            } else {
//                restEWT.setText("Wait-" + timeArray[0] + "hr " + timeArray[1] + "mins");
//            }
//        } else {
//            restEWT.setText("Wait-N/A");
//        }


        // to get location specific menu
        menuSingleton.setLocationId(locationId);
        menuSingleton.setTenantId(tenantId);
        menuSingleton.setRestaurantAddress(restAddress);
        menuSingleton.setRestaurantName(restName);

        currentRest = menuSingleton.getClickedRestaurant();
//        restType = currentRest.getRestaurantType();

//        if (restChat != null) {
//            restChats.setText(restChat + " Posts");
//        }

//        if (menuSingleton.getClickedRestaurant().isQtSupported()) {
        isQTSupported = true;
//        } else {
//            isQTSupported = false;
//        }

        getDispensaryDetails(menuSingleton.getClickedRestaurant());

        Glide.with(this).load(Config.QUICK_CHAT_IMAGE + SpecificMenuSingleton.getInstance().getClickedRestaurant().getRestaurantImage())
                .placeholder(R.mipmap.restaurant_defaulticon)
                .into(dispensaryLogo);

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                drawerLayout.openDrawer(Gravity.LEFT);
//                return true;

                Intent intent = new Intent(RestaurantServices.this, MainMenuActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();

            }
        });

        callUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VolleySingleton.getInstance(RestaurantServices.this).postEventRequest("CallUs", SpecificMenuSingleton.getInstance().getClickedRestaurant());
//                if (isQTSupported) {
                if (SpecificMenuSingleton.getInstance().getClickedRestaurant().getRestaurantPhone() != null) {
                    startActivity(new Intent(Intent.ACTION_DIAL).setData(Uri.parse("tel:" + restPhone)));
                } else {
                    Toast.makeText(RestaurantServices.this, "Phone number is not available!", Toast.LENGTH_SHORT).show();
                }
//                } else {
//                    showNotQTSupportedMessage();
//                }
            }
        });

        getDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VolleySingleton.getInstance(RestaurantServices.this).postEventRequest("GetDirection", SpecificMenuSingleton.getInstance().getClickedRestaurant());
//                if (isQTSupported) {
                String getDirectionUrl = String.format("http://maps.google.com/maps?saddr=%f,%f+&daddr=%f,%f",
                        sourceLatitude, sourceLongitude, destinationLatitude, destinationLongitude);
                Log.i("GetDirection", getDirectionUrl);
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(getDirectionUrl));
                startActivity(intent);
//                } else {
//                    showNotQTSupportedMessage();
//                }
            }
        });

        restaurantHours.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isQTSupported) {
                    if (SpecificMenuSingleton.getInstance().getClickedRestaurant().getRestaurantTimings() != null &&
                            SpecificMenuSingleton.getInstance().getClickedRestaurant().getRestaurantTimings().size() > 0) {
                        showRestaurantHoursDialog();
                    } else {
                        Toast.makeText(RestaurantServices.this, "No hours data available!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    showNotQTSupportedMessage();
                }
            }
        });

//         FOR USER PROFILE OPTIONS
//        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                switch (position) {
//                    case 1:
//                        // User Profile
//                        startActivity(new Intent(RestaurantServices.this, UserProfileActivity.class));
//                        drawerLayout.closeDrawers();
//                        break;
//                    case 2:
//                        // User Favorites Orders List
//                        startActivity(new Intent(RestaurantServices.this, OrderHistoryActivity.class));
//                        drawerLayout.closeDrawers();
//                        break;
//                    case 3:
//                        // Order Booking Status - Show user estimated wait time
//                        startActivity(new Intent(RestaurantServices.this, OrderConfirmationActivity.class));
//                        drawerLayout.closeDrawers();
//                        break;
//
//                    case 4:
//                        shareApp();
//                        break;
//
//                    case 5:
//                        startActivity(new Intent(RestaurantServices.this, ChatImagesActivity.class));
//                        drawerLayout.closeDrawers();
//                        break;
//
//                    case 6:
//                        startActivity(new Intent(RestaurantServices.this, AboutUsActivity.class));
//                        drawerLayout.closeDrawers();
//                        break;
//                }
//            }
//        });
//        Glide.with(this).load(SpecificMenuSingleton.getInstance().getClickedRestaurant().getRatingImage())
//                .placeholder(R.mipmap.rating_bg)
//                .into(ratingImage);

    }

    @Override
    protected void onResume() {
        super.onResume();
//        SharedPreferences sharedPreference = getSharedPreferences("user_info", MODE_PRIVATE);
//        String profileName = sharedPreference.getString("user_name", null);
//        String userAddress = sharedPreference.getString("user_address", null);
//        String userImageUrl = sharedPreference.getString("user_image", null);
//        userName.setText(profileName);
////        userLocation.setText(userAddress);
//        userName.setTypeface(Globals.robotoBold);
////        userLocation.setTypeface(Globals.robotoRegular);
//        Glide.with(this).load(Config.QUICK_CHAT_IMAGE + userImageUrl)
//                .asBitmap().placeholder(R.mipmap.default_profile_pic)
//                .into(userImage);
////        Glide.with(this).load("https://s3-us-west-2.amazonaws.com/stagingquicktable/profile/" + userImageUrl)
////                .asBitmap().placeholder(R.mipmap.default_profile_pic)
////                .into(userImage);
//
//        userImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });

//        if (SpecificMenuSingleton.getInstance().getClickedRestaurant().getRestaurantChatAvailable() != null) {
//            restChats.setText(SpecificMenuSingleton.getInstance().getClickedRestaurant()
//                    .getRestaurantChatAvailable() + " Posts");
//        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
        }
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        clickPosition = position;
        for(int i=0; i<restaurantServicesModels.size(); i++){
            if (position == i){
                restaurantServicesModels.get(i).setSelected(true);
            }else {
                restaurantServicesModels.get(i).setSelected(false);
            }
        }
        adapter.notifyDataSetChanged();
        String restAccountType = SpecificMenuSingleton.getInstance().getClickedRestaurant().getRestaurantAccountName();
        LocationListModel restDetails = SpecificMenuSingleton.getInstance().getClickedRestaurant();
        switch (position) {

//            case 0:
////                if (Config.APP_NAME.equalsIgnoreCase("QuickTable")){
//                VolleySingleton.getInstance(this).postEventRequest("ViewMenus", restDetails);
//                if (isQTSupported) {
//                    if (SpecificMenuSingleton.getInstance().getClickedRestaurant().getRestaurantAccountName().equalsIgnoreCase("custom")){
//                        if (SpecificMenuSingleton.getInstance().getClickedRestaurant().isInteractiveMenu()){
//                            Config.viewMenu = true;
//                            Config.takeAway = false;
//                            Intent intent = new Intent(this, MenuActivity.class);
//                            intent.putExtra("RestaurantMenuUrl", restMenuUrl);
//                            startActivity(intent);
//                        }else {
//                            showNotQTSupportedMessage();
//                        }
//                    }else {
//                        Config.viewMenu = true;
//                        Config.takeAway = false;
//                        Intent intent = new Intent(this, MenuActivity.class);
//                        intent.putExtra("RestaurantMenuUrl", restMenuUrl);
//                        startActivity(intent);
//                    }
//                } else {
//                    showNotQTSupportedMessage();
//                }
////                }else {
////                    Config.viewMenu = true;
////                    Intent intent = new Intent(this, MenuActivity.class);
////                    intent.putExtra("RestaurantMenuUrl", restMenuUrl);
////                    startActivity(intent);
////                }
//                break;

//            case 1:
//                if (restDetails.isDealActive() || isQTSupported){
//                    Intent intent = new Intent(this, OrderHistoryActivity.class);
//                    intent.putExtra("is_from_rest_list", true);
//                    startActivity(intent);
//                }else {
//                    noOffersDialog();
//                }
//                break;

//            case 3:
////                if (Config.APP_NAME.equalsIgnoreCase("QuickTable")){
//                if (isQTSupported) {
//                    setTimeZone();
//                    switch (restAccountType) {
//                        case "BRONZE":
//                            showNotQTSupportedMessage();
//                            VolleySingleton.getInstance(this).postEventRequest("GetInLine", restDetails);
//                            break;
//                        case "SILVER":
//                            if (currentTime.compareToIgnoreCase(openTime) >= 0 && currentTime.compareToIgnoreCase(closeTime) <= 0) {
//                                if (SpecificMenuSingleton.getInstance().getClickedRestaurant().isHostessOnline()) {
//                                    Config.takeAway = false;
//                                    Config.viewMenu = false;
//                                    OrderConfirmationActivity.currentTime = null;
//                                    startActivity(new Intent(this, BookYourTableActivity.class));
//                                } else {
//                                    Log.i("Hostess Offline", "true");
//                                    Toast.makeText(this, R.string.hostess_offline_msg, Toast.LENGTH_SHORT).show();
//                                }
//                            } else {
//                                Log.i("Business time", "true");
//                                Toast.makeText(this, R.string.hostess_offline_msg, Toast.LENGTH_SHORT).show();
//                            }
//                            break;
//
//                        case "GOLD":
//                            if (currentTime.compareToIgnoreCase(openTime) >= 0 && currentTime.compareToIgnoreCase(closeTime) <= 0) {
//                                if (SpecificMenuSingleton.getInstance().getClickedRestaurant().isHostessOnline()) {
//                                    Config.takeAway = false;
//                                    Config.viewMenu = false;
//                                    OrderConfirmationActivity.currentTime = null;
//                                    startActivity(new Intent(this, BookYourTableActivity.class));
//                                } else {
//                                    Log.i("Hostess Offline", "true");
//                                    Toast.makeText(this, R.string.hostess_offline_msg, Toast.LENGTH_SHORT).show();
//                                }
//                            } else {
//                                Log.i("Business time", "true");
//                                Toast.makeText(this, R.string.hostess_offline_msg, Toast.LENGTH_SHORT).show();
//                            }
//                            break;
//
//                        case "PLATINUM":
//                            if (currentTime.compareToIgnoreCase(openTime) >= 0 && currentTime.compareToIgnoreCase(closeTime) <= 0) {
//                                if (SpecificMenuSingleton.getInstance().getClickedRestaurant().isHostessOnline()) {
//                                    Config.takeAway = false;
//                                    Config.viewMenu = false;
//                                    OrderConfirmationActivity.currentTime = null;
//                                    startActivity(new Intent(this, BookYourTableActivity.class));
//                                } else {
//                                    Log.i("Hostess Offline", "true");
//                                    Toast.makeText(this, R.string.hostess_offline_msg, Toast.LENGTH_SHORT).show();
//                                }
//                            } else {
//                                Log.i("Business time", "true");
//                                Toast.makeText(this, R.string.hostess_offline_msg, Toast.LENGTH_SHORT).show();
//                            }
//                            break;
//
//                        case "CUSTOM":
//                            if (restDetails.isGetInLine()) {
//                                if (currentTime.compareToIgnoreCase(openTime) >= 0 && currentTime.compareToIgnoreCase(closeTime) <= 0) {
//                                    if (SpecificMenuSingleton.getInstance().getClickedRestaurant().isHostessOnline()) {
//                                        Config.takeAway = false;
//                                        Config.viewMenu = false;
//                                        OrderConfirmationActivity.currentTime = null;
//                                        startActivity(new Intent(this, BookYourTableActivity.class));
//                                    } else {
//                                        Log.i("Hostess Offline", "true");
//                                        Toast.makeText(this, R.string.hostess_offline_msg, Toast.LENGTH_SHORT).show();
//                                    }
//                                } else {
//                                    Log.i("Business time", "true");
//                                    Toast.makeText(this, R.string.hostess_offline_msg, Toast.LENGTH_SHORT).show();
//                                }
//                            } else {
//                                showNotQTSupportedMessage();
//                            }
//                    }
//                } else {
//                    showNotQTSupportedMessage();
//                    VolleySingleton.getInstance(this).postEventRequest("GetInLine", restDetails);
//                }
//                break;

            case 0:
                if (locationCheck()) {
                    if (checkUserState()) {
                        SharedPreferences preferences = getSharedPreferences("ewt_info", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
//                if (Config.APP_NAME.equalsIgnoreCase("QuickTable")){
//                VolleySingleton.getInstance(this).postEventRequest("Pre-order", restDetails);
//                        if (isQTSupported) {
                        setTimeZone();
                        switch (restAccountType) {
                            case "FREE":
                                if (CartSingleton.getInstance().getCartItem() != null && CartSingleton.getInstance().getCartItem().size() > 0) {
                                    if (CartSingleton.getInstance().getCartItem().get(0).getTenantId() == SpecificMenuSingleton.getInstance().getClickedRestaurant().getTenantID()
                                            && CartSingleton.getInstance().getCartItem().get(0).getLocationId() == SpecificMenuSingleton.getInstance().getClickedRestaurant().getLocationID()) {
                                        Config.viewMenu = false;
                                        Config.takeAway = true;
                                        editor.putBoolean("isDineInOnly", false);
                                        editor.putBoolean("is_patron_seated", false);
                                        editor.commit();
                                        startActivity(new Intent(this, MenuActivity.class));
                                    } else {
                                        showRestaurantVaryError();
                                    }
                                } else {
                                    Config.viewMenu = false;
                                    Config.takeAway = true;
                                    editor.putBoolean("isDineInOnly", false);
                                    editor.putBoolean("is_patron_seated", false);
                                    editor.commit();
                                    startActivity(new Intent(this, MenuActivity.class));
                                }
                                break;

                            case "PLATINUM":
//                            if (currentTime.compareToIgnoreCase(openTime) >= 0 && currentTime.compareToIgnoreCase(closeTime) <= 0) {
//                                if (SpecificMenuSingleton.getInstance().getClickedRestaurant().isHostessOnline()) {
                                if (CartSingleton.getInstance().getCartItem() != null && CartSingleton.getInstance().getCartItem().size() > 0) {
                                    if (CartSingleton.getInstance().getCartItem().get(0).getTenantId() == SpecificMenuSingleton.getInstance().getClickedRestaurant().getTenantID()
                                            && CartSingleton.getInstance().getCartItem().get(0).getLocationId() == SpecificMenuSingleton.getInstance().getClickedRestaurant().getLocationID()) {
                                        Config.viewMenu = false;
                                        Config.takeAway = true;
                                        editor.putBoolean("isDineInOnly", false);
                                        editor.putBoolean("is_patron_seated", false);
                                        editor.commit();
                                        startActivity(new Intent(this, MenuActivity.class));
                                    } else {
                                        showRestaurantVaryError();
                                    }
                                } else {
                                    Config.viewMenu = false;
                                    Config.takeAway = true;
                                    editor.putBoolean("isDineInOnly", false);
                                    editor.putBoolean("is_patron_seated", false);
                                    editor.commit();
                                    startActivity(new Intent(this, MenuActivity.class));
                                }
//                                } else {
//                                    Log.i("Hostess Offline", "true");
//                                    Toast.makeText(this, R.string.hostess_offline_msg, Toast.LENGTH_SHORT).show();
//                                }
//                            } else {
//                                Log.i("Business time", "true");
//                                Toast.makeText(this, R.string.hostess_offline_msg, Toast.LENGTH_SHORT).show();
//                            }
                                break;

                            case "PREMIUM":
//                            if (restDetails.isCarryOut()) {
//                                if (currentTime.compareToIgnoreCase(openTime) >= 0 && currentTime.compareToIgnoreCase(closeTime) <= 0) {
//                                    if (SpecificMenuSingleton.getInstance().getClickedRestaurant().isHostessOnline()) {
                                if (CartSingleton.getInstance().getCartItem() != null && CartSingleton.getInstance().getCartItem().size() > 0) {
                                    if (CartSingleton.getInstance().getCartItem().get(0).getTenantId() == SpecificMenuSingleton.getInstance().getClickedRestaurant().getTenantID()
                                            && CartSingleton.getInstance().getCartItem().get(0).getLocationId() == SpecificMenuSingleton.getInstance().getClickedRestaurant().getLocationID()) {
                                        Config.viewMenu = false;
                                        Config.takeAway = true;
                                        editor.putBoolean("isDineInOnly", false);
                                        editor.commit();
                                        startActivity(new Intent(this, MenuActivity.class));
                                    } else {
                                        showRestaurantVaryError();
                                    }
                                } else {
                                    Config.viewMenu = false;
                                    Config.takeAway = true;
                                    editor.putBoolean("isDineInOnly", false);
                                    editor.commit();
                                    startActivity(new Intent(this, MenuActivity.class));
                                }
//                                    } else {
//                                        Log.i("Hostess Offline", "true");
//                                        Toast.makeText(this, R.string.hostess_offline_msg, Toast.LENGTH_SHORT).show();
//                                    }
//                                } else {
//                                    Log.i("Business time", "true");
//                                    Toast.makeText(this, R.string.hostess_offline_msg, Toast.LENGTH_SHORT).show();
//                                }
//                            } else {
//                                showNotQTSupportedMessage();
//                            }
                        }
//                        } else {
//                            showNotQTSupportedMessage();
//                        }
//                }else {
//                    if (CartSingleton.getInstance().getCartItem() != null && CartSingleton.getInstance().getCartItem().size() > 0){
//                        if (CartSingleton.getInstance().getCartItem().get(0).getTenantId() == SpecificMenuSingleton.getInstance().getClickedRestaurant().getTenantID()
//                                && CartSingleton.getInstance().getCartItem().get(0).getLocationId() == SpecificMenuSingleton.getInstance().getClickedRestaurant().getLocationID()) {
//                            Config.viewMenu = false;
//                            Config.takeAway = true;
//                            editor.putBoolean("isDineInOnly", false);
//                            editor.commit();
//                            startActivity(new Intent(this, MenuActivity.class));
//                        }else {
//                            showRestaurantVaryError();
//                        }
//                    }else {
//                        Config.viewMenu = false;
//                        Config.takeAway = true;
//                        editor.putBoolean("isDineInOnly", false);
//                        editor.commit();
//                        startActivity(new Intent(this, MenuActivity.class));
//                    }
//                }

                    } else {
                        Config.viewMenu = true;
                        Config.takeAway = false;
//                        editor.putBoolean("isDineInOnly", false);
//                        editor.putBoolean("is_patron_seated", false);
//                        editor.commit();
                        startActivity(new Intent(this, MenuActivity.class));
//                        Toast.makeText(this, "Sorry, you must be in the same State as the dispensary to use this feature!", Toast.LENGTH_LONG).show();
                    }
                } else {
                    showGpsEnabledAlert();
                }
                break;

            case 1:
                startActivity(new Intent(this, TopBudsActivity.class));
                break;

            case 2:
                startActivity(new Intent(this, ChatImagesActivity.class));
                break;
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (gpsTracker != null) {
            gpsTracker.stopLocation();
        }
    }

    private void showNotQTSupportedMessage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alert");
        builder.setMessage(restName + " doesn’t support this feature yet...would you like us to tell them to add this feature?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void noOffersDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alert");
        builder.setMessage("We are sorry, but there are no special offers at this restaurant today.");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void postRestaurantData() {

        String url = "";

        final SharedPreferences sharedPreferences = getSharedPreferences("user_info", Context.MODE_PRIVATE);
        String patronName = sharedPreferences.getString("user_name", null);

        JSONObject postData = new JSONObject();
        try {
            postData.put("", restName);
            postData.put("", patronName);
            postData.put("", clickPosition);
        } catch (JSONException e) {
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, postData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        VolleySingleton.getInstance(this).addToRequestQueue(request);

    }

    private void showRestaurantVaryError() {
        SharedPreferences preferences = getSharedPreferences("ewt_info", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = preferences.edit();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Items already in cart!");
        builder.setMessage("Your cart contains items from other restaurant. Would you like to reset your cart before browsing this restaurant?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                CartSingleton.getInstance().clearCart();
                MenuItemFragment.updateCartCount();
                Config.viewMenu = false;
                Config.takeAway = true;
                editor.putBoolean("isDineInOnly", false);
                editor.commit();
                startActivity(new Intent(RestaurantServices.this, MenuActivity.class));
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void setTimeZone() {
        SpecificMenuSingleton menuSingleton = SpecificMenuSingleton.getInstance();
        SimpleDateFormat f = new SimpleDateFormat("HH:mm:ss");
        f.setTimeZone(TimeZone.getTimeZone(menuSingleton.getClickedRestaurant().getTimezone()));
        currentTime = f.format(new Date());
        openTime = menuSingleton.getClickedRestaurant().getRestaurantOpenTiming();
        closeTime = menuSingleton.getClickedRestaurant().getRestaurantCloseTiming();
        int time1 = currentTime.compareToIgnoreCase(openTime);
        int time2 = currentTime.compareToIgnoreCase(closeTime);
        Log.i("Timings", "CurrentTime= " + currentTime + " OpenTime= " + openTime + " CloseTime= " + closeTime + " timestatus= " + time1 + " timestatus= " + time2);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.location_favorite:
                if (isRestFav) {
                    removeFavoriteLocation();
                } else {
                    addFavoriteLocation();
                }
                break;
        }
    }

    private void addFavoriteLocation() {

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Adding dispensary to your favorite list.");
        dialog.show();

        String url = Config.FAVORITE_REST_LIST;

        JSONObject params = new JSONObject();
        try {
            params.put("placeId", SpecificMenuSingleton.getInstance().getClickedRestaurant().getPlaceID());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        isRestFav = true;
                        favoriteLocationImg.setImageResource(R.mipmap.ic_favorite_selected);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (dialog.isShowing()) {
                    dialog.dismiss();
                }

                if (error != null) {
                    if (error instanceof TimeoutError) {
                        Config.internetSlowError(RestaurantServices.this);
                    } else if (error instanceof NoConnectionError) {
                        Config.internetSlowError(RestaurantServices.this);
                    } else if (error instanceof ServerError) {
                        Config.serverError(RestaurantServices.this);
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

    private void removeFavoriteLocation() {

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Removing dispensary from your favorite list.");
        dialog.show();

        String url = Config.FAVORITE_REST_LIST + SpecificMenuSingleton.getInstance().getClickedRestaurant().getPlaceID() + "/delete";
        Log.i("RemoveFavorite", url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }

                        isRestFav = false;
                        favoriteLocationImg.setImageResource(R.mipmap.ic_favorite_unselected);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (dialog.isShowing()) {
                    dialog.dismiss();
                }

                if (error != null) {
                    if (error instanceof TimeoutError) {
                        Config.internetSlowError(RestaurantServices.this);
                    } else if (error instanceof NoConnectionError) {
                        Config.internetSlowError(RestaurantServices.this);
                    } else if (error instanceof ServerError) {
                        Config.serverError(RestaurantServices.this);
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

    private void showRestaurantHoursDialog() {

        String[] days = {"Mo", "Tu", "We", "Th", "Fr", "Sa", "Su"};
        ArrayList<RestaurantTimingsModel> timings = SpecificMenuSingleton.getInstance().getClickedRestaurant().getRestaurantTimings();
        ArrayList<RestaurantTimingsModel> sortedTimings = new ArrayList<>();
        StringBuilder restaurantDays = new StringBuilder();
        StringBuilder restaurantTiming = new StringBuilder();

        for (int i = 0; i < days.length; i++) {
            RestaurantTimingsModel restaurantTimingsModel = new RestaurantTimingsModel();
            switch (days[i]) {
                case "Mo":
                    for (int j = 0; j < timings.size(); j++) {
                        if (timings.get(j).getDay().equalsIgnoreCase("Mo")) {
                            restaurantTimingsModel.setDay("Monday");
                            restaurantTimingsModel.setHours(timings.get(j).getHours());
                            sortedTimings.add(restaurantTimingsModel);
                            break;
                        }
                    }
                    break;
                case "Tu":
                    for (int j = 0; j < timings.size(); j++) {
                        if (timings.get(j).getDay().equalsIgnoreCase("Tu")) {
                            restaurantTimingsModel.setDay("Tuesday");
                            restaurantTimingsModel.setHours(timings.get(j).getHours());
                            sortedTimings.add(restaurantTimingsModel);
                            break;
                        }
                    }
                    break;
                case "We":
                    for (int j = 0; j < timings.size(); j++) {
                        if (timings.get(j).getDay().equalsIgnoreCase("We")) {
                            restaurantTimingsModel.setDay("Wednesday");
                            restaurantTimingsModel.setHours(timings.get(j).getHours());
                            sortedTimings.add(restaurantTimingsModel);
                            break;
                        }
                    }
                    break;
                case "Th":
                    for (int j = 0; j < timings.size(); j++) {
                        if (timings.get(j).getDay().equalsIgnoreCase("Th")) {
                            restaurantTimingsModel.setDay("Thursday");
                            restaurantTimingsModel.setHours(timings.get(j).getHours());
                            sortedTimings.add(restaurantTimingsModel);
                            break;
                        }
                    }
                    break;
                case "Fr":
                    for (int j = 0; j < timings.size(); j++) {
                        if (timings.get(j).getDay().equalsIgnoreCase("Fr")) {
                            restaurantTimingsModel.setDay("Friday");
                            restaurantTimingsModel.setHours(timings.get(j).getHours());
                            sortedTimings.add(restaurantTimingsModel);
                            break;
                        }
                    }
                    break;
                case "Sa":
                    for (int j = 0; j < timings.size(); j++) {
                        if (timings.get(j).getDay().equalsIgnoreCase("Sa")) {
                            restaurantTimingsModel.setDay("Saturday");
                            restaurantTimingsModel.setHours(timings.get(j).getHours());
                            sortedTimings.add(restaurantTimingsModel);
                            break;
                        }
                    }
                    break;
                case "Su":
                    for (int j = 0; j < timings.size(); j++) {
                        if (timings.get(j).getDay().equalsIgnoreCase("Su")) {
                            restaurantTimingsModel.setDay("Sunday");
                            restaurantTimingsModel.setHours(timings.get(j).getHours());
                            sortedTimings.add(restaurantTimingsModel);
                            break;
                        }
                    }
                    break;
            }
        }

        for (int i = 0; i < sortedTimings.size(); i++) {
            restaurantDays.append(sortedTimings.get(i).getDay() + "\n");
            restaurantTiming.append(sortedTimings.get(i).getHours() + "\n");
        }

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.restaurant_hours_dialog);
        dialog.setCancelable(false);

        ImageView closeBtn = (ImageView) dialog.findViewById(R.id.hours_close_btn);
        TextView restaurantHoursDays = (TextView) dialog.findViewById(R.id.restaurant_hours_day);
        TextView restaurantHoursTiming = (TextView) dialog.findViewById(R.id.restaurant_hours_timing);

        restaurantHoursDays.setText(restaurantDays);
        restaurantHoursTiming.setText(restaurantTiming);

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private boolean checkUserState() {

        if (locationCheck()) {
            gpsTracker = new GPSTracker(this);
            gpsTracker.startLocation();

            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            ;
            try {
                List<Address> addresses = geocoder.getFromLocation(gpsTracker.getLatitude(), gpsTracker.getLongitude(), 1);

                if (addresses != null && addresses.size() > 0) {
                    String currentState = addresses.get(0).getAdminArea();
                    if (currentState.equalsIgnoreCase(SpecificMenuSingleton.getInstance().getClickedRestaurant().getRestaurantState())) {
                        if (SpecificMenuSingleton.getInstance().getClickedRestaurant().getRestaurantAccountName().equalsIgnoreCase("Premium") &&
                                SpecificMenuSingleton.getInstance().getClickedRestaurant().isPickUpAvailable())
                        return true;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private boolean locationCheck() {
        boolean isLocation = false;
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);

// lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, SampleMapFragment.this);

        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled && !network_enabled) {
            isLocation = false;
        } else {
            isLocation = true;
        }
        return isLocation;
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

    private void getDispensaryDetails(final LocationListModel listModel) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Getting Dispensary Details...");
        dialog.show();
        final String qtRestaurantURL = Config.QT_SUPPORTED_URL;
//        final String qtRestaurantURL = "http://192.168.1.21:13000/qt/core/certloc/cmpplaceId/";
        final JSONObject params = new JSONObject();
        JSONArray array = new JSONArray();
        try {
            JSONObject restObject = new JSONObject();
            restObject.put("name", listModel.getRestaurantName());
            restObject.put("placeId", listModel.getPlaceID());
            restObject.put("address", listModel.getRestaurantAddress());
            restObject.put("city", listModel.getRestaurantCity());
            restObject.put("state", listModel.getRestaurantState());
            array.put(restObject);
            params.put("placeId", array);
//            if (searchedCategory != null && !searchedCategory.isEmpty()) {
//                String[] categoriesSplit = searchedCategory.split(", ");
//                JSONArray categoriesArray = new JSONArray();
//                for (int i = 0; i < categoriesSplit.length; i++) {
//                    categoriesArray.put(categoriesSplit[i]);
//                }
//                params.put("searchkey", categoriesArray);
//            } else {
//                JSONArray categoriesArray = new JSONArray();
//                categoriesArray.put("all");
//                params.put("searchkey", categoriesArray);
//            }
        } catch (JSONException e) {

        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, qtRestaurantURL, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("QBRestaurantList", response.toString());
                        if (dialog.isShowing()){
                            dialog.dismiss();
                        }

                        try {
                            JSONArray restaurantList = response.getJSONArray("results");

                            for (int i = 0; i < restaurantList.length(); i++) {
                                JSONObject singleRest = restaurantList.getJSONObject(i);
                                String placeId = singleRest.getString("placeId");
                                listModel.setPlaceID(placeId);
                                listModel.setQtSupported(true);
                                listModel.setFavoriteRestaurant(singleRest.getBoolean("is_favorite"));

                                JSONArray accountTypes = singleRest.getJSONArray("account_type");
                                JSONObject accountType = accountTypes.getJSONObject(0);
                                listModel.setRestaurantAccountName(accountType.getString("account_name"));
                                listModel.setCarryOut(accountType.getBoolean("carry_out"));
                                listModel.setDisplayLogo(accountType.getBoolean("display_logo"));
                                listModel.setGetInLine(accountType.getBoolean("get_in_line"));
                                listModel.setPreOrder(accountType.getBoolean("pre_order"));
                                listModel.setInteractiveMenu(accountType.getBoolean("interactive_menu"));
                                listModel.setPickUpAvailable(accountType.getBoolean("pickup"));
                                listModel.setDeliveryAvailable(accountType.getBoolean("delivery"));
                                listModel.setRestaurantPhone(singleRest.getString("phone_number"));
                                if (singleRest.getString("status").equalsIgnoreCase("A")) {
                                    listModel.setHostessOnline(true);
                                } else if (singleRest.getString("status").equalsIgnoreCase("I")) {
                                    listModel.setHostessOnline(false);
                                }

                                listModel.setRestaurantImage(singleRest.getString("url"));
                                listModel.setRestaurantEWT(singleRest.getString("ewt"));
                                listModel.setLocationID(singleRest.getInt("location_id"));
                                listModel.setTenantID(singleRest.getInt("tenant_id"));
                                listModel.setRestaurantOpenTiming(singleRest.getString("open_time"));
                                listModel.setRestaurantCloseTiming(singleRest.getString("close_time"));
                                listModel.setRestaurantMenuUrl(singleRest.getString("menu_url"));
                                listModel.setTimezone(singleRest.getString("time_zone"));
                                listModel.setAtm(singleRest.getBoolean("atm"));
                                listModel.setMedical(singleRest.getBoolean("medical"));
                                listModel.setRecreational(singleRest.getBoolean("recreational"));

                                ArrayList<RestaurantTimingsModel> timings = new ArrayList<>();
                                JSONArray hoursArray = singleRest.getJSONArray("hours");
                                for (int k = 0; k < hoursArray.length(); k++) {
                                    RestaurantTimingsModel restaurantTimingsModel = new RestaurantTimingsModel();
                                    JSONObject hoursObj = hoursArray.getJSONObject(k);
                                    restaurantTimingsModel.setDay(hoursObj.getString("day"));
                                    if (hoursObj.getBoolean("opened")) {
                                        String openTime = hoursObj.getString("open_time");
                                        String closeTime = hoursObj.getString("close_time");
                                        String[] openTimeSplit = openTime.split(":");
                                        if (Integer.parseInt(openTimeSplit[0]) > 12) {
                                            openTime = String.valueOf(Integer.parseInt(openTimeSplit[0]) - 12) + ":" + openTimeSplit[1] + " PM";
                                        } else if (Integer.parseInt(openTimeSplit[0]) == 12) {
                                            openTime = String.valueOf(Integer.parseInt(openTimeSplit[0])) + ":" + openTimeSplit[1] + " PM";
                                        } else {
                                            openTime = openTimeSplit[0] + ":" + openTimeSplit[1] + " AM";
                                        }
                                        String[] closeTimeSplit = closeTime.split(":");
                                        if (Integer.parseInt(closeTimeSplit[0]) > 12) {
                                            closeTime = String.valueOf(Integer.parseInt(closeTimeSplit[0]) - 12) + ":" + closeTimeSplit[1] + " PM";
                                        } else if (Integer.parseInt(closeTimeSplit[0]) == 12) {
                                            closeTime = String.valueOf(Integer.parseInt(closeTimeSplit[0])) + ":" + closeTimeSplit[1] + " PM";
                                        } else {
                                            closeTime = closeTimeSplit[0] + ":" + closeTimeSplit[1] + " AM";
                                        }
                                        restaurantTimingsModel.setHours(openTime + " - " + closeTime);
                                    } else {
                                        restaurantTimingsModel.setHours("Closed");
                                    }
                                    timings.add(restaurantTimingsModel);
                                }
                                listModel.setRestaurantTimings(timings);
                                SpecificMenuSingleton.getInstance().setClickedRestaurant(listModel);
                            }

                            isRestFav = SpecificMenuSingleton.getInstance().getClickedRestaurant().isFavoriteRestaurant();

                            if (isRestFav) {
                                favoriteLocationImg.setImageResource(R.mipmap.ic_favorite_selected);
                            } else {
                                favoriteLocationImg.setImageResource(R.mipmap.ic_favorite_unselected);
                            }

                            if (SpecificMenuSingleton.getInstance().getClickedRestaurant().isRecreational()) {
                                recreational.setVisibility(View.VISIBLE);
                            }

                            if (SpecificMenuSingleton.getInstance().getClickedRestaurant().isAtm()) {
                                atm.setVisibility(View.VISIBLE);
                            }

                            if (SpecificMenuSingleton.getInstance().getClickedRestaurant().isMedical()) {
                                medical.setVisibility(View.VISIBLE);
                            }

                            if (SpecificMenuSingleton.getInstance().getClickedRestaurant().getRestRating() == 5.0) {
                                ratingImage.setImageResource(R.mipmap.rating_5_star);
                            } else {
                                ratingImage.setImageResource(R.mipmap.rating_4_star);
                            }

                            for (int i=0; i<titleList.length; i++){
                                RestaurantServicesModel model = new RestaurantServicesModel();
                                model.setImage(imageList[i]);
                                model.setTitleList(titleList[i]);
                                model.setSubTitleList(subTitleList[i]);
                                model.setSelected(false);
                                restaurantServicesModels.add(model);
                            }

                            ListView listView = (ListView) findViewById(R.id.homeScreenListView);
                            adapter = new RestaurantServicesAdapter(RestaurantServices.this, restaurantServicesModels);
                            listView.setAdapter(adapter);
                            listView.setOnItemClickListener(RestaurantServices.this);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error != null) {
                    if (error instanceof TimeoutError) {
                        Config.internetSlowError(RestaurantServices.this);
                    } else if (error instanceof NoConnectionError) {
                        Config.internetSlowError(RestaurantServices.this);
                    } else if (error instanceof ServerError) {
                        Config.serverError(RestaurantServices.this);
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
}
