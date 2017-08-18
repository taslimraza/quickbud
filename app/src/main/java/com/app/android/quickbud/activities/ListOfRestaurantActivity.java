package com.app.android.quickbud.activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.multidex.MultiDex;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextPaint;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.app.android.quickbud.R;
import com.app.android.quickbud.adapters.LocationListAdapter;
import com.app.android.quickbud.adapters.SortingSpinnerAdapter;
import com.app.android.quickbud.modelClasses.LocationDistanceModel;
import com.app.android.quickbud.modelClasses.LocationListModel;
import com.app.android.quickbud.modelClasses.RestaurantTimingsModel;
import com.app.android.quickbud.modelClasses.TenantModel;
import com.app.android.quickbud.network.VolleySingleton;
import com.app.android.quickbud.utils.ChatLocationSingleton;
import com.app.android.quickbud.utils.Config;
import com.app.android.quickbud.utils.ConnectionDetector;
import com.app.android.quickbud.utils.CustomListView;
import com.app.android.quickbud.utils.GPSTracker;
import com.app.android.quickbud.utils.Globals;
import com.app.android.quickbud.utils.HttpRequest;
import com.app.android.quickbud.utils.JsonParser;
import com.app.android.quickbud.utils.LocationService;
import com.app.android.quickbud.utils.SpecificMenuSingleton;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.yelp.clientlib.connection.YelpAPI;
import com.yelp.clientlib.connection.YelpAPIFactory;
import com.yelp.clientlib.entities.Business;
import com.yelp.clientlib.entities.SearchResponse;
import com.yelp.clientlib.entities.options.CoordinateOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;

//import com.factual.driver.Circle;
//import com.factual.driver.Factual;
//import com.factual.driver.FactualApiException;
//import com.factual.driver.Query;
//import com.factual.driver.ReadResponse;
//import com.google.common.collect.Lists;

public class ListOfRestaurantActivity extends AppCompatActivity implements OnMapReadyCallback, AdapterView.OnItemClickListener, View.OnClickListener, AbsListView.OnScrollListener {

    private boolean isInternetPresent = false;
    private ConnectionDetector connectionDetector;
    private Geocoder geocoder;
    private TextView userName, userLocation, menuTitle, searchRadius;
    private EditText searchRestaurant;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private CustomListView locationList;
    private ImageView homeBtn, locationSearch, searchClose, searchRest, imageList, qtLogo, userImage;
    private RelativeLayout cartContent, listOfRestaurantLayout, restaurantMapView, restaurantListView;
    private LinearLayout loadingImageLayout;
    private HttpRequest httpRequest;
    private JsonParser jsonParser;
    private String currentAddress1, currentAddress2, currentCity, currentState;
    private ArrayList<LocationListModel> locationListModels, qtRestaurantList, searchedRestaurantList;
    private ArrayList<LocationDistanceModel> locationDistanceModels;
    private final String GOOGLE_KEY = "AIzaSyBbU-VhGVEZQ0D8k7TIMyVSPoJq-oFWODY";
    private final String FACTUAL_API_KEY = "0X0TTqxibQSwZwGljem6AUJMvWJcrgxiADGqALzJ";
    private static double latitude;
    private static double longitude;
    private static double searchedLatitude;
    private static double searchedLongitude;
    private static boolean isSearchedLocation = false;
    private boolean isCurrentLocation = true;
    private boolean isSearchedRestaurant = false;
    private String[] googlePlaceUrl;
    private Spinner spinner;
    private String searchedLocation, restaurantListUrl;
    private LocationManager locationManager;
    private Location lastKnownGPSLocation, lastKnownNetworkLocation;
    private boolean isQTSorted = false;
    private SeekBar seekBar;
    private int distance = 8000; // 5 miles
    private int radius = 5, filteredItemCount = 0, secondFilteredItemCount = 0;
    private String firstPageToken, secondPageToken, tenantFirstPageToken, tenantSecondPageToken,
            searchedFirstPageToken, searchedSecondPageToken;
    private ProgressBar progressBar, loadMore;
    private LocationListAdapter locationListAdapter;
    private ArrayList<LocationListModel> newListModels, listModels;
    private boolean isLoading = false, isLoadingDone = false;
    private boolean mapViewShow = true, isDistanceSorted = false, isQuickChatSorted = false;
    private int sortByClickPosition;
    private String[] sortBy;
    private ArrayList<LocationListModel> filteredLocationList;
    private LocationListAdapter filteredLocationAdapter;
    private int pageCount = 0, searchedPageCount;
    private int restaurantListCount = 0;
    private final String FACTUAL_KEY = "J2DvoLSleXNKQD25SG39REtsimZ1eVCpq4v678H7";
    private final String FACTUAL_SECRET = "JRQJBLgXfZOimoRnqtA0q9TUperKjg89NWbZnW1C";
    //    Factual factual;
    private String[] cuisine = new String[]{"American", "Cafe", "Italian", "Diner", "Grill", "Chinese",
            "Seafood", "Traditional", "Asian", "Steak", "Pub Food", "Southwestern", "Barbecue", "Bistro", "Wings"};
    private String filterFastFood = "Carl's Jr., Church's Chicken, Popeyes Louisiana Kitchen, Sonic Drive-In, Quiznos, Jack In The Box, McDonald's, Burger King, Wendy's, Dairy Queen, Taco Bell,Subway, KFC, Moe's Southwest Grill, Rally's, Arby's, Firehouse Subs";
    private ImageView voteWinImage;
    private int yelpTotalList = 0;

    //    private XMPPTCPConnection connection;
//    private MultiUserChatManager manager;
    private RelativeLayout footerLayout;
    private GPSTracker gpsTracker;
    private String searchedRestaurantName = null;
    private String searchedCategory = null;
    private boolean isSearchByName = false;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

//Rockne's location - 41.126648,-81.607874
//https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=17.413828,78.439758&radius=5000&types=restaurant|cafe&key=AIzaSyDXZZEpRuN3AOrFF9lOYq4jTbCQGV6WFOo
//String googlePlaceUrl="https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + latitude + "," + longitude + "&radius=5000&types=restaurant|cafe&sensor=true&key=" + GOOGLE_KEY ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MultiDex.install(this);
        setContentView(R.layout.activity_location);

        ListOfRestaurantActivity.this.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
//        userName = (TextView) findViewById(R.id.user_name);
        userLocation = (TextView) findViewById(R.id.user_location);
        menuTitle = (TextView) findViewById(R.id.menu_title);
        locationSearch = (ImageView) findViewById(R.id.location_search);
        imageList = (ImageView) findViewById(R.id.imagelist);
//        drawerList = (ListView) findViewById(R.id.drawer_list);
//        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        final LinearLayout searchLocation = (LinearLayout) findViewById(R.id.search_container);
        locationList = (CustomListView) findViewById(R.id.listView);
        spinner = (Spinner) findViewById(R.id.sort_by);
//        searchRestaurant = (EditText) findViewById(R.id.search_rest);
//        searchClose = (ImageView) findViewById(R.id.search_close);
        loadingImageLayout = (LinearLayout) findViewById(R.id.loading_image_layout);
        listOfRestaurantLayout = (RelativeLayout) findViewById(R.id.list_of_restaurant_layout);
        restaurantMapView = (RelativeLayout) findViewById(R.id.map_view);
        restaurantListView = (RelativeLayout) findViewById(R.id.restaurant_list);
//        seekBar = (SeekBar) findViewById(R.id.seek_bar);
//        searchRadius = (TextView) findViewById(R.id.search_radius);
        progressBar = (ProgressBar) findViewById(R.id.loadMore);
//        userImage = (ImageView) findViewById(R.id.user_image);
//        footerLayout = (RelativeLayout) findViewById(R.id.footer);
//        voteWinImage = (ImageView) findViewById(R.id.fab_vote_win);
//        voteWinImage.setOnClickListener(this);


//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        toolbar.setBackgroundResource(R.mipmap.toolbar_image);
//        }

//        factual = new Factual(FACTUAL_KEY, FACTUAL_SECRET);

        httpRequest = new HttpRequest(this);
        jsonParser = new JsonParser();
        locationListModels = new ArrayList<>();
        filteredLocationList = new ArrayList<>();
        searchedRestaurantList = new ArrayList<LocationListModel>();
        locationDistanceModels = new ArrayList<>();
        loadMore = new ProgressBar(this);

        connectionDetector = new ConnectionDetector(this);
        isInternetPresent = connectionDetector.isConnectedToInternet();
        if (!isInternetPresent) {
            connectionDetector.internetError();
            return;
        }

//        qtLogo.setVisibility(View.VISIBLE);
//        toolbar.setNavigationIcon(R.mipmap.qt_icon);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        drawerList.setAdapter(new DrawerListAdapter(this));
        cartContent = (RelativeLayout) findViewById(R.id.cart_content);
        cartContent.setVisibility(View.GONE);
        homeBtn = (ImageView) findViewById(R.id.menu_button);
//        locationSearch.setVisibility(View.VISIBLE);

        searchedRestaurantName = getIntent().getStringExtra("searched_restaurant_name");
        searchedCategory = getIntent().getStringExtra("searched_category");
        isSearchByName = getIntent().getBooleanExtra("isSearchByName", false);
        searchedLatitude = getIntent().getDoubleExtra("searchedLatitude", 0);
        searchedLongitude = getIntent().getDoubleExtra("searchedLongitude", 0);

//        searchRadius.setText(" " + radius + "miles");

        locationSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListOfRestaurantActivity.this, SearchLocationActivity.class);
                intent.putExtra("location", currentAddress2);
                intent.putExtra("isSearchByName", isSearchByName);
                startActivityForResult(intent, 1);
            }
        });
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
//                drawerLayout.openDrawer(Gravity.LEFT);

                startActivity(new Intent(ListOfRestaurantActivity.this, MainMenuActivity.class));
                finish();
            }
        });

//        searchRestaurant.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
//                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
//                    if (searchRestaurant.getText().toString() != null && searchRestaurant.getText().toString().length() > 2) {
//                        isSearchedRestaurant = true;
////                    searchedPageCount = 1;
//                        listOfRestaurantLayout.setVisibility(View.GONE);
//                        loadingImageLayout.setVisibility(View.VISIBLE);
//
////                        Query query;
////                        if (isSearchedLocation) {
////                            query = new Query()
////                                    .within(new Circle(searchedLatitude, searchedLongitude, 25000))
//////                                    .field("category_ids").includesAny(348, 349, 352, 354, 357, 358, 359, 360, 364, 365)
////                                    .field("category_ids").includes(347)
//////                                    .field("category_ids").notIn(355)
//////                                    .field("cuisine").in(cuisine)
////                                    .search(searchRestaurant.getText().toString().trim())
//////                                .field("name").equal(searchRestaurant.getText().toString().trim())
////                                    .field("name").notIn(filterFastFood)
////                                    .sortAsc("$distance")
////                                    .only("name", "address", "factual_id", "latitude", "longitude", "locality", "region", "tel")
////                                    .limit(50);
////                        } else {
////                            query = new Query()
////                                    .within(new Circle(latitude, longitude, 25000))
//////                                    .field("category_ids").includesAny(348, 349, 352, 354, 357, 358, 359, 360, 364, 365)
////                                    .field("category_ids").includes(347)
//////                                    .field("category_ids").notIn(355)
//////                                    .field("cuisine").in(cuisine)
////                                    .search(searchRestaurant.getText().toString().trim())
//////                                .field("name").equal(searchRestaurant.getText().toString().trim())
////                                    .field("name").notIn(filterFastFood)
////                                    .sortAsc("$distance")
////                                    .only("name", "address", "factual_id", "latitude", "longitude", "locality", "region", "tel")
////                                    .limit(50);
////                        }
//
////                        new SearchedRestaurantList().execute(query);
//                    } else if (searchRestaurant.getText().toString().equals("")) {
//                        Toast.makeText(ListOfRestaurantActivity.this, "Empty Search Field!", Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(ListOfRestaurantActivity.this, "Please enter atleast 3 characters!", Toast.LENGTH_SHORT).show();
//                    }
//                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
//                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
//                    if (searchRestaurant.getText().toString() != null && searchRestaurant.getText().toString().length() > 2) {
//                        isSearchedRestaurant = true;
////                        searchedPageCount = 1;
//                        listOfRestaurantLayout.setVisibility(View.GONE);
//                        loadingImageLayout.setVisibility(View.VISIBLE);
//
//                        Query query;
//                        if (isSearchedLocation) {
//                            query = new Query()
//                                    .within(new Circle(searchedLatitude, searchedLongitude, 25000))
//                                    .field("category_labels").includes("Social, Food and Dining, Restaurants")
//                                    .field("cuisine").includesAny(cuisine)
//                                    .search(searchRestaurant.getText().toString().trim())
////                                    .field("name").equal(searchRestaurant.getText().toString().trim())
//                                    .sortAsc("$distance")
//                                    .only("name", "address", "factual_id", "latitude", "longitude", "locality", "region", "tel")
//                                    .limit(50);
//                        } else {
//                            query = new Query()
//                                    .within(new Circle(latitude, longitude, 25000))
//                                    .field("category_labels").includes("Social, Food and Dining, Restaurants")
//                                    .field("cuisine").includesAny(cuisine)
//                                    .search(searchRestaurant.getText().toString().trim())
////                                    .field("name").equal(searchRestaurant.getText().toString().trim())
//                                    .sortAsc("$distance")
//                                    .only("name", "address", "factual_id", "latitude", "longitude", "locality", "region", "tel")
//                                    .limit(50);
//                        }
//
//                        new SearchedRestaurantList().execute();
//                    } else if (searchRestaurant.getText().toString().equals("")) {
//                        Toast.makeText(ListOfRestaurantActivity.this, "Empty Search Field!", Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(ListOfRestaurantActivity.this, "Please enter atleast 3 characters!", Toast.LENGTH_SHORT).show();
//                    }
//                    return true;
//                }
//                return false;
//            }
//        });

//        searchClose.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
//                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
//                if (searchRestaurant.getText().toString() != null && searchRestaurant.getText().toString().length() > 2) {
//                    isSearchedRestaurant = true;
////                    searchedPageCount = 1;
//                    listOfRestaurantLayout.setVisibility(View.GONE);
//                    loadingImageLayout.setVisibility(View.VISIBLE);
//
////                    Query query;
////                    if (isSearchedLocation) {
////                        query = new Query()
////                                .within(new Circle(searchedLatitude, searchedLongitude, 25000))
////                                .field("category_ids").includes(347)
//////                                .field("category_ids").includesAny(348, 349, 352, 354, 357, 358, 359, 360, 364, 365)
//////                                .field("category_ids").notIn(355)
//////                                .field("cuisine").in(cuisine)
////                                .search(searchRestaurant.getText().toString().trim())
//////                                .field("name").equal(searchRestaurant.getText().toString().trim())
////                                .field("name").notIn(filterFastFood)
////                                .sortAsc("$distance")
////                                .only("name", "address", "factual_id", "latitude", "longitude", "locality", "region", "tel")
////                                .limit(50);
////                    } else {
////                        query = new Query()
////                                .within(new Circle(latitude, longitude, 25000))
////                                .field("category_ids").includes(347)
//////                                .field("category_ids").includesAny(348, 349, 352, 354, 357, 358, 359, 360, 364, 365)
//////                                .field("category_ids").notIn(355)
//////                                .field("cuisine").in(cuisine)
////                                .search(searchRestaurant.getText().toString().trim())
//////                                .field("name").equal(searchRestaurant.getText().toString().trim())
////                                .field("name").notIn(filterFastFood)
////                                .sortAsc("$distance")
////                                .only("name", "address", "factual_id", "latitude", "longitude", "locality", "region", "tel")
////                                .limit(50);
////                    }
////
////                    new SearchedRestaurantList().execute(query);
//                } else if (searchRestaurant.getText().toString().equals("")) {
//                    Toast.makeText(ListOfRestaurantActivity.this, "Empty Search Field!", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(ListOfRestaurantActivity.this, "Please enter atleast 3 characters!", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });

        imageList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapViewShow = !mapViewShow;
                if (!mapViewShow) {
                    imageList.setImageResource(R.mipmap.list);
                    restaurantListView.setVisibility(View.GONE);
                    restaurantMapView.setVisibility(View.VISIBLE);
                } else {
                    imageList.setImageResource(R.mipmap.map);
                    restaurantListView.setVisibility(View.VISIBLE);
                    restaurantMapView.setVisibility(View.GONE);
                }
            }


        });


//        searchRestaurant.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (s.toString().equals("") && (start > 0 || before > 0)) {
//                    if (locationListModels != null && locationListModels.size() > 0) {
//                        isSearchedRestaurant = false;
//                        filteredLocationList = initialSortedList();
//                        filteredLocationList = locationListFilter(distance);
//                        removeDuplicatedList();
//                        SpecificMenuSingleton menuSingleton = SpecificMenuSingleton.getInstance();
//                        menuSingleton.setRestaurantList(null);
//                        menuSingleton.setRestaurantList(filteredLocationList);
//                        locationList.setAdapter(new LocationListAdapter(ListOfRestaurantActivity.this, filteredLocationList));
////                        if(pageCount < 3){
////                            locationList.addFooterView(loadMore);
////                        }
//                        locationList.setOnItemClickListener(ListOfRestaurantActivity.this);
//                        if (firstPageToken != null && pageCount < 3 && (searchedPageCount > 2 || searchedFirstPageToken == null)) {
//                            locationList.addFooterView(loadMore);
//                            isLoading = false;
//                            isLoadingDone = false;
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//            }
//        });

        sortByClickPosition = 0;
        sortBy = getResources().getStringArray(R.array.sort_by);
        if (!Config.APP_NAME.equalsIgnoreCase("QuickTable")) {
            sortBy = Arrays.copyOf(sortBy, sortBy.length - 1);
        }
        final SortingSpinnerAdapter adapter = new SortingSpinnerAdapter(this, R.layout.restaurant_sorting_spinner, sortBy, sortByClickPosition);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 1:
                        sortByClickPosition = position;
                        final SortingSpinnerAdapter adapter = new SortingSpinnerAdapter(ListOfRestaurantActivity.this, R.layout.restaurant_sorting_spinner, sortBy, sortByClickPosition);
                        spinner.setAdapter(adapter);
                        if (filteredLocationList.size() > 0) {
                            //check for restaurant distance, if found then sort
                            sortByDistance();
                            isDistanceSorted = true;
                            isQuickChatSorted = false;
                        } else {
                            Toast.makeText(ListOfRestaurantActivity.this, "No Distance data to sort", Toast.LENGTH_SHORT).show();
                        }
                        removeDuplicatedList();
                        LocationListAdapter locationListAdapter = new LocationListAdapter(ListOfRestaurantActivity.this, filteredLocationList);
                        locationList.setAdapter(locationListAdapter);
                        break;

                    case 2:
                        sortByClickPosition = position;
                        final SortingSpinnerAdapter adapter2 = new SortingSpinnerAdapter(ListOfRestaurantActivity.this, R.layout.restaurant_sorting_spinner, sortBy, sortByClickPosition);
                        spinner.setAdapter(adapter2);
//                        qbSupportedRestaurant();
                        filteredLocationList = initialSortedList();
                        sortByQBSupported();
//                        if (Config.APP_NAME.equalsIgnoreCase("QuickTable")) {
//                            filteredLocationList = locationListFilter(distance);
//                        }
                        removeDuplicatedList();
                        if (!isQTSorted) {
                            Toast.makeText(ListOfRestaurantActivity.this, "No QB Supported restaurant in the list", Toast.LENGTH_SHORT).show();
                        }
                        LocationListAdapter qtLocationListAdapter = new LocationListAdapter(ListOfRestaurantActivity.this, filteredLocationList);
                        locationList.setAdapter(qtLocationListAdapter);
                        isDistanceSorted = false;
                        isQuickChatSorted = false;
                        break;

                    case 3:
                        sortByClickPosition = position;
                        final SortingSpinnerAdapter adapter1 = new SortingSpinnerAdapter(ListOfRestaurantActivity.this, R.layout.restaurant_sorting_spinner, sortBy, sortByClickPosition);
                        spinner.setAdapter(adapter1);
                        isDistanceSorted = false;
                        isQuickChatSorted = true;
//                        if (Config.APP_NAME.equalsIgnoreCase("QuickTable")) {
//                            filteredLocationList = locationListFilter(distance);
//                        }
                        removeDuplicatedList();
                        sortByRating();
                        locationList.setAdapter(new LocationListAdapter(ListOfRestaurantActivity.this, filteredLocationList));
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

//         FOR USER PROFILE OPTIONS
//        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                switch (position) {
//                    case 1:
//                        // User Profile
//                        startActivity(new Intent(ListOfRestaurantActivity.this, UserProfileActivity.class));
//                        drawerLayout.closeDrawers();
//                        break;
//                    case 2:
//                        // User Favorites Orders List
//                        startActivity(new Intent(ListOfRestaurantActivity.this, OrderHistoryActivity.class));
//                        drawerLayout.closeDrawers();
//                        break;
//                    case 3:
//                        // Order Booking Status - Show user estimated wait time
//                        startActivity(new Intent(ListOfRestaurantActivity.this, OrderConfirmationActivity.class));
//                        drawerLayout.closeDrawers();
//                        break;
//
//                    case 4:
//                        shareApp();
//                        break;
//
//                    case 5:
//                        startActivity(new Intent(ListOfRestaurantActivity.this, ChatImages.class));
//                        drawerLayout.closeDrawers();
//                        break;
//
//                    case 6:
//                        startActivity(new Intent(ListOfRestaurantActivity.this, AboutUsActivity.class));
//                        drawerLayout.closeDrawers();
//                        break;
//                }
//            }
//        });


        // seek bar listener

//        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
////                if (progress != 0) {
//                distance = (progress + 1) * 8000; // 10 miles * progress point
//                radius = 5;
//                radius = radius * (progress + 1);
//                searchRadius.setText(" " + radius + "miles");
////                } else {
////                    distance = 8000; // 5 miles by default
////                    radius = 5;
////                    searchRadius.setText(" " + radius + "miles");
////                }
//            }

//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }

//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//                if (isDistanceSorted) {
//                    filteredLocationList = locationListFilter(distance);
//                    removeDuplicatedList();
//                    sortByDistance();
//                    SpecificMenuSingleton menuSingleton = SpecificMenuSingleton.getInstance();
//                    menuSingleton.setRestaurantList(null);
//                    menuSingleton.setRestaurantList(filteredLocationList);
//                    filteredLocationAdapter = new LocationListAdapter(ListOfRestaurantActivity.this, filteredLocationList);
//                    locationList.setAdapter(filteredLocationAdapter);
//                    locationList.setOnItemClickListener(ListOfRestaurantActivity.this);
//                } else if (isQuickChatSorted) {
//                    filteredLocationList = locationListFilter(distance);
//                    removeDuplicatedList();
//                    sortByQuickChat();
//                    SpecificMenuSingleton menuSingleton = SpecificMenuSingleton.getInstance();
//                    menuSingleton.setRestaurantList(null);
//                    menuSingleton.setRestaurantList(filteredLocationList);
//                    filteredLocationAdapter = new LocationListAdapter(ListOfRestaurantActivity.this, filteredLocationList);
//                    locationList.setAdapter(filteredLocationAdapter);
//                    locationList.setOnItemClickListener(ListOfRestaurantActivity.this);
//                } else {
//                    filteredLocationList = locationListFilter(distance);
//                    removeDuplicatedList();
//                    SpecificMenuSingleton menuSingleton = SpecificMenuSingleton.getInstance();
//                    menuSingleton.setRestaurantList(null);
//                    menuSingleton.setRestaurantList(filteredLocationList);
//                    filteredLocationAdapter = new LocationListAdapter(ListOfRestaurantActivity.this, filteredLocationList);
//                    locationList.setAdapter(filteredLocationAdapter);
//                    locationList.setOnItemClickListener(ListOfRestaurantActivity.this);
//                }
////                if (!isSearchedLocation) {
////                    String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
////                            + latitude + "," + longitude + "&rankby=distance" + "&types=restaurant|cafe&key=" + GOOGLE_KEY;
////                    getListOfRestaurant(url);
////                    searchRadius.setText(" " + radius + "miles");
////                } else {
////                        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
////                                + searchedLatitude + "," + searchedLongitude + "&rankby=distance" + "&types=restaurant|cafe&key=" + GOOGLE_KEY;
////                        getListOfRestaurant(url);
////                }
//            }
//        });

//        if (SpecificMenuSingleton.getInstance().getConnection() == null) {
//            new XMPPAsyncTask().execute();
//        }

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("BroadCast", "Working");
            latitude = intent.getDoubleExtra("latitude", 0.0);
            longitude = intent.getDoubleExtra("longitude", 0.0);

            geocoder = new Geocoder(ListOfRestaurantActivity.this, Locale.getDefault());
            try {

                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                if (addresses.size() > 0) {
                    if (addresses.get(0).getAddressLine(0) != null) {
//                                currentAddress1 = addresses.get(0).getAddressLine(0);
                        currentAddress1 = addresses.get(0).getSubThoroughfare();
                    }
//                            currentAddress2 = addresses.get(0).getAddressLine(1);
                    currentAddress2 = addresses.get(0).getThoroughfare();
                    currentCity = addresses.get(0).getLocality();
                    currentState = addresses.get(0).getAdminArea();
                } else {

                }
            } catch (IOException e) {

            }

            SpecificMenuSingleton menuSingleton = SpecificMenuSingleton.getInstance();

            if (currentCity != null && currentState != null) {
                if (currentAddress2 == null && currentAddress1 == null) {
                    toolbar.setTitle(currentCity + " " + currentState);
                    menuSingleton.setLastAddress(null);
                } else if (currentAddress2 == null) {
                    toolbar.setTitle(currentAddress1 + " " + currentCity + " " + currentState);
                    menuSingleton.setLastAddress(currentAddress1);
                } else if (currentAddress1 == null) {
                    toolbar.setTitle(currentAddress2 + " " + currentCity + " " + currentState);
                    menuSingleton.setLastAddress(currentAddress2);
                } else {
                    toolbar.setTitle(currentAddress1 + " " + currentAddress2 + " " + currentCity + " " + currentState);
                    menuSingleton.setLastAddress(currentAddress1 + " " + currentAddress2);
                }
            }

            if (Config.APP_NAME.equalsIgnoreCase("QuickTable")) {
                if (searchedRestaurantName == null) {
                    getQBRestaurantList(true);
                } else {
                    getSearchedLocationLatLng(searchedRestaurantName);
                }
            } else {
                getRestaurantList();
            }

        }
    };

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

            showGpsEnabledAlert();


        } else {
            isLocation = true;
        }
        return isLocation;
    }

    @Override
    protected void onResume() {
        super.onResume();

        listOfRestaurantLayout.setVisibility(View.GONE);
        loadingImageLayout.setVisibility(View.VISIBLE);

        if (!isSearchedLocation) {
            if (locationCheck()) {
                Intent intent = new Intent(this, LocationService.class);
                startService(intent);
                registerReceiver(broadcastReceiver, new IntentFilter("locationAvailable"));
            }
        }

//        gpsTracker = new GPSTracker(this);
//        gpsTracker.startLocation();

//        sortByClickPosition = 0;
//        final SortingSpinnerAdapter adapter = new SortingSpinnerAdapter(this, R.layout.restaurant_sorting_spinner, sortBy, sortByClickPosition);
//        spinner.setAdapter(adapter);
//
//        if (filteredLocationList.size() > 0) {
//            locationListModels.clear();
//            filteredLocationList.clear();
//            searchedRestaurantList.clear();
//        }
//
//        if (isSearchedLocation) {
////            getSavedRestaurantList();
//        } else {
//            SharedPreferences sharedPreferences = getSharedPreferences("lastLocation", 0);
//            double lastKnownLatitude = sharedPreferences.getFloat("last_known_latitude", 0);
//            double lastKnownLongitude = sharedPreferences.getFloat("last_known_longitude", 0);
////            if (latitude == 0.00) {
//            Intent intent = getIntent();
//            listOfRestaurantLayout.setVisibility(View.GONE);
//            loadingImageLayout.setVisibility(View.VISIBLE);
////                gpsTracker = new GPSTracker(this);
//            if (gpsTracker.canGetLocation()) {
//                latitude = gpsTracker.getLatitude();
//                longitude = gpsTracker.getLongitude();
//                double accuracy = gpsTracker.getAccuracy();
//                System.out.println("latitude = " + latitude + "\t" + "longitude = " + longitude + "\t" + "accuracy = " + accuracy);
//
//                while (latitude == 0.0) {
////                        GPSTracker gpsTracker = new GPSTracker(this);
//                    latitude = gpsTracker.getLatitude();
//                    longitude = gpsTracker.getLongitude();
//                    System.out.println("latitude = " + latitude + "\t" + "longitude = " + longitude + "\t" + "accuracy = " + accuracy);
//                }
//
//                ChatLocationSingleton.getInstance().setCurrentLatitude(latitude);
//                ChatLocationSingleton.getInstance().setCurrentLongitude(longitude);
//
////                        if (locationListModels.size() == 0) {
//
//                // --------------- Code for White labelling--------------------------
//                if (Config.APP_NAME.equalsIgnoreCase("QuickTable")) {
////                        getTenantNames();
//                    if (searchedRestaurantName == null) {
//                        new FirstYelpRestaurantList().execute();
//                    } else {
//                        getSearchedLocationLatLng(searchedRestaurantName);
//                    }
//                } else {
//                    getRestaurantList();
//                }
//            } else {
//                showGpsEnabledAlert();
//            }
//        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isSearchedLocation = false;
        locationListModels.clear();
        if (gpsTracker != null) {
            gpsTracker.stopLocation();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        SpecificMenuSingleton menuSingleton = SpecificMenuSingleton.getInstance();
//        if (requestCode == 1) {
//            if (resultCode == RESULT_OK) {
//                isLoading = false;
//                spinner.setSelection(0);
//                sortByClickPosition = 0;
//                SortingSpinnerAdapter adapter = new SortingSpinnerAdapter(this, R.layout.restaurant_sorting_spinner, sortBy, sortByClickPosition);
//                spinner.setAdapter(adapter);
//                boolean currentLocation = data.getBooleanExtra("current_location", false);
//                locationListModels.clear();
//                searchedRestaurantList.clear();
//                menuSingleton.setRestaurantList(null);
//                isSearchedRestaurant = false;
//                firstPageToken = null;
//                secondPageToken = null;
//                tenantFirstPageToken = null;
//                tenantSecondPageToken = null;
//                if (currentLocation) {
//                    isSearchedLocation = false;
////                    gpsTracker = new GPSTracker(this);
////                    gpsTracker = new GPSTracker(this);
////                    gpsTracker.startLocation();
//                    if (gpsTracker.canGetLocation()) {
//                        latitude = gpsTracker.getLatitude();
//                        longitude = gpsTracker.getLongitude();
//
//                        if (locationList.getFooterViewsCount() > 0) {
//                            locationList.removeFooterView(loadMore);
//                        }
//
////                        if (locationListModels.size() == 0) {
//
//                        // --------------- Code for White labelling--------------------------
//                        if (Config.APP_NAME.equalsIgnoreCase("QuickTable")) {
////                            getTenantNames();
//                            new FirstYelpRestaurantList().execute();
//                        } else {
//                            getRestaurantList();
//                        }
//
////                        geocoder = new Geocoder(this, Locale.getDefault());
////                        try {
////
////                            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
////                            if (addresses.size() > 0) {
////                                if (addresses.get(0).getAddressLine(0) != null) {
////                                    currentAddress1 = addresses.get(0).getAddressLine(0);
////                                }
////                                currentAddress2 = addresses.get(0).getAddressLine(1);
////                                currentCity = addresses.get(0).getLocality();
////                                currentState = addresses.get(0).getAdminArea();
////                            } else {
////
////                            }
////                        } catch (IOException e) {
////
////                        }
////                        if (currentCity != null && currentState != null) {
////                            if (currentAddress2 == null && currentAddress1 == null) {
////                                toolbar.setTitle(currentCity + " " + currentState);
////                                menuSingleton.setLastAddress(null);
////                            } else if (currentAddress2 == null) {
////                                toolbar.setTitle(currentAddress1 + " " + currentCity + " " + currentState);
////                                menuSingleton.setLastAddress(currentAddress1);
////                            } else if (currentAddress1 == null) {
////                                toolbar.setTitle(currentAddress2 + " " + currentCity + " " + currentState);
////                                menuSingleton.setLastAddress(currentAddress2);
////                            } else {
////                                toolbar.setTitle(currentAddress1 + " " + currentAddress2 + " " + currentCity + " " + currentState);
////                                menuSingleton.setLastAddress(currentAddress1 + " " + currentAddress2);
////                            }
////                        }
//                    }
//
////                    }
//
//                } else {
//                    isSearchedLocation = true;
//                    searchedLocation = data.getStringExtra("search_location");
//                    String[] searchedString = searchedLocation.split(", ");
//                    StringBuilder builder = new StringBuilder();
//                    builder.append(searchedString[0]);
//                    for (int i = 1; i < searchedString.length; i++) {
//                        builder.append(", " + searchedString[i]);
//                    }
//                    toolbar.setTitle(builder.toString());
//
//                    if (locationList.getFooterViewsCount() > 0) {
//                        locationList.removeFooterView(loadMore);
//                    }
//
//                    menuSingleton.setLastAddress(builder.toString());
////                    getSearchedLocationLatLng();
//                }
//                seekBar.setProgress(0);
////                searchRestaurant.setText("");
//
//            }
//        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        setMap(googleMap, 10);
//        switch (radius) {
//            case 5:
//                setMap(googleMap, 14);
//                break;
//
//            case 10:
//                setMap(googleMap, 12);
//                break;
//
//            case 20:
//                setMap(googleMap, 11);
//                break;
//
//            case 30:
//                setMap(googleMap, 10);
//                break;
//
//            case 40:
//                setMap(googleMap, 9);
//                break;
//
//            case 50:
//                setMap(googleMap, 8);
//                break;
//        }
    }

    private void setMap(GoogleMap googleMap, int mapZoom) {
//        if (!isSearchedRestaurant) {
        for (int i = 0; i < filteredLocationList.size(); i++) {
            LatLng restaurant = new LatLng(filteredLocationList.get(i).getLatitude(), filteredLocationList.get(i).getLongitude());

            Bitmap restImage = BitmapFactory.decodeResource(getResources(), R.mipmap.restaurant_defaulticon_small);
            TextPaint paint = new TextPaint();
            Bitmap.Config conf = Bitmap.Config.ARGB_4444;
            Bitmap btm = Bitmap.createBitmap(200, 50, conf);
            Canvas canvas = new Canvas(btm);
            paint.setColor(Color.parseColor("#000000"));
            paint.setTextSize(20);
            paint.setTypeface(Globals.myraidProBold);
            canvas.drawBitmap(restImage, 0, 0, paint);
            if (filteredLocationList.size() < 100) {
                canvas.drawText(filteredLocationList.get(i).getRestaurantName(), 0, 50, paint);
                googleMap.addMarker(new MarkerOptions().position(restaurant).title(filteredLocationList.get(i).getRestaurantName())
                        .icon(BitmapDescriptorFactory.fromBitmap(btm)));
                googleMap.moveCamera(CameraUpdateFactory.
                        newLatLngZoom(new LatLng(filteredLocationList.get(0).getLatitude(), filteredLocationList.get(0).getLongitude())
                                , mapZoom));
            }
        }
        googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

//        } else {
//            googleMap.clear();
//            if (listModels.isEmpty()) {
//
//            } else {
//                for (int i = 0; i < listModels.size(); i++) {
//                    LatLng restaurant = new LatLng(listModels.get(i).getLatitude(), listModels.get(i).getLongitude());
//                    googleMap.addMarker(new MarkerOptions().position(restaurant).title(locationListModels.get(i).getRestaurantName())
//                            .icon(BitmapDescriptorFactory.fromBitmap(btm)));
//                }
//                if (listModels.size() == locationListModels.size()) {
//                    googleMap.moveCamera(CameraUpdateFactory.
//                            newLatLngZoom(new LatLng(listModels.get(0).getLatitude(), listModels.get(0).getLongitude())
//                                    , mapZoom));
//                } else {
//                    googleMap.moveCamera(CameraUpdateFactory.
//                            newLatLngZoom(new LatLng(listModels.get(0).getLatitude(), listModels.get(0).getLongitude())
//                                    , mapZoom));
//                }
//            }
//        }*/
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        try {
            SpecificMenuSingleton menuSingleton = SpecificMenuSingleton.getInstance();
            menuSingleton.setPlaceId(filteredLocationList.get(position).getPlaceID());

            ChatLocationSingleton.getInstance().setRestLatitude(filteredLocationList.get(position).getLatitude());
            ChatLocationSingleton.getInstance().setRestLongitude(filteredLocationList.get(position).getLongitude());

            LocationListModel listModel = filteredLocationList.get(position);

            menuSingleton.setClickedRestaurant(filteredLocationList.get(position));

//        if (!Config.APP_NAME.equalsIgnoreCase("QuickTable")) {
//            menuSingleton.getClickedRestaurant().setQtSupported(true);
//        }

            Intent intent = new Intent(this, RestaurantServices.class);
            intent.putExtra("RestName", filteredLocationList.get(position).getRestaurantName());
            intent.putExtra("RestAddress", filteredLocationList.get(position).getRestaurantAddress());
            intent.putExtra("RestDistance", filteredLocationList.get(position).getRestaurantDistance());
            intent.putExtra("RestChat", filteredLocationList.get(position).getRestaurantChatAvailable());
            intent.putExtra("RestEWT", filteredLocationList.get(position).getRestaurantEWT());
            intent.putExtra("RestPhone", filteredLocationList.get(position).getRestaurantPhone());
            intent.putExtra("LocationId", filteredLocationList.get(position).getLocationID());
            intent.putExtra("TenantId", filteredLocationList.get(position).getTenantID());
            intent.putExtra("RestPosition", position);
            intent.putExtra("RestaurantLatitude", filteredLocationList.get(position).getLatitude());
            intent.putExtra("RestaurantLongitude", filteredLocationList.get(position).getLongitude());
            intent.putExtra("RestaurantImage", filteredLocationList.get(position).getRestaurantImage());
            intent.putExtra("RestaurantMenuUrl", filteredLocationList.get(position).getRestaurantMenuUrl());
            SpecificMenuSingleton.getInstance().setQtSupported(filteredLocationList.get(position).isQtSupported());
            SpecificMenuSingleton.getInstance().setRestPosition(position);
            if (isSearchedLocation) {
                intent.putExtra("CurrentLatitude", searchedLatitude);
                intent.putExtra("CurrentLongitude", searchedLongitude);
            } else {
                intent.putExtra("CurrentLatitude", latitude);
                intent.putExtra("CurrentLongitude", longitude);
            }
            startActivity(intent);
        }catch (IndexOutOfBoundsException e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
        }
        return true;
    }

    private void getQTSupportedRestaurants(final ArrayList<LocationListModel> listModels) {
        final String qtRestaurantURL = Config.QT_SUPPORTED_URL;
//        final String qtRestaurantURL = "http://192.168.1.21:13000/qt/core/certloc/cmpplaceId/";
        final JSONObject params = new JSONObject();
        JSONArray array = new JSONArray();
        try {
            for (int i = 0; i < listModels.size(); i++) {
                JSONObject restObject = new JSONObject();
                restObject.put("name", listModels.get(i).getRestaurantName());
                restObject.put("placeId", listModels.get(i).getPlaceID());
                restObject.put("address", listModels.get(i).getRestaurantAddress());
                restObject.put("city", listModels.get(i).getRestaurantCity());
                restObject.put("state", listModels.get(i).getRestaurantState());
                array.put(restObject);
            }
            params.put("placeId", array);
            if (searchedCategory != null && !searchedCategory.isEmpty()) {
                String[] categoriesSplit = searchedCategory.split(", ");
                JSONArray categoriesArray = new JSONArray();
                for (int i = 0; i < categoriesSplit.length; i++) {
                    categoriesArray.put(categoriesSplit[i]);
                }
                params.put("searchkey", categoriesArray);
            } else {
                JSONArray categoriesArray = new JSONArray();
                categoriesArray.put("all");
                params.put("searchkey", categoriesArray);
            }
        } catch (JSONException e) {

        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, qtRestaurantURL, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("QBRestaurantList", response.toString());

//                        String factualId = null;
//                        if (SpecificMenuSingleton.getInstance().getClickedRestaurant() != null){
//                            factualId = SpecificMenuSingleton.getInstance().getClickedRestaurant().getPlaceID();
//                        }
                        filteredLocationList = new ArrayList<>();
                        try {
                            listOfRestaurantLayout.setVisibility(View.VISIBLE);
                            loadingImageLayout.setVisibility(View.GONE);
                            JSONArray restaurantList = response.getJSONArray("results");

                            for (int i = 0; i < restaurantList.length(); i++) {
                                JSONObject singleRest = restaurantList.getJSONObject(i);
                                String placeId = singleRest.getString("placeId");

                                for (int j = 0; j < listModels.size(); j++) {

                                    if (placeId.equals(listModels.get(j).getPlaceID())) {

                                        boolean qtSupported = singleRest.getBoolean("qt_supported");
                                        listModels.get(j).setQtSupported(qtSupported);
                                        listModels.get(j).setFavoriteRestaurant(singleRest.getBoolean("is_favorite"));
//                                        listModels.get(j).setRestaurantAccountName(singleRest.getString("account_type"));

                                        if (singleRest.isNull("chats")) {
                                            listModels.get(j).setRestaurantChatAvailable("0");
                                        } else {
                                            listModels.get(j).setRestaurantChatAvailable(singleRest.getString("chats"));
                                        }

                                        JSONArray accountTypes = singleRest.getJSONArray("account_type");
                                        JSONObject accountType = accountTypes.getJSONObject(0);
                                        listModels.get(j).setRestaurantAccountName(accountType.getString("account_name"));
                                        listModels.get(j).setCarryOut(accountType.getBoolean("carry_out"));
                                        listModels.get(j).setDisplayLogo(accountType.getBoolean("display_logo"));
                                        listModels.get(j).setGetInLine(accountType.getBoolean("get_in_line"));
                                        listModels.get(j).setPreOrder(accountType.getBoolean("pre_order"));
                                        listModels.get(j).setInteractiveMenu(accountType.getBoolean("interactive_menu"));
                                        listModels.get(j).setPickUpAvailable(accountType.getBoolean("pickup"));
                                        listModels.get(j).setDeliveryAvailable(accountType.getBoolean("delivery"));

                                        if (!qtSupported) {

                                        } else {
                                            listModels.get(j).setRestaurantPhone(singleRest.getString("phone_number"));
                                            if (singleRest.isNull("chats")) {
                                                listModels.get(j).setRestaurantChatAvailable("0");
                                            } else {
                                                listModels.get(j).setRestaurantChatAvailable(singleRest.getString("chats"));
                                            }

                                            if (singleRest.getString("status").equalsIgnoreCase("A")) {
                                                listModels.get(j).setHostessOnline(true);
                                            } else if (singleRest.getString("status").equalsIgnoreCase("I")) {
                                                listModels.get(j).setHostessOnline(false);
                                            }

                                            listModels.get(j).setRestaurantImage(singleRest.getString("url"));
                                            listModels.get(j).setRestaurantEWT(singleRest.getString("ewt"));
                                            listModels.get(j).setLocationID(singleRest.getInt("location_id"));
                                            listModels.get(j).setTenantID(singleRest.getInt("tenant_id"));
                                            listModels.get(j).setRestaurantOpenTiming(singleRest.getString("open_time"));
                                            listModels.get(j).setRestaurantCloseTiming(singleRest.getString("close_time"));
                                            listModels.get(j).setRestaurantMenuUrl(singleRest.getString("menu_url"));
                                            listModels.get(j).setTimezone(singleRest.getString("time_zone"));
                                            listModels.get(j).setAtm(singleRest.getBoolean("atm"));
                                            listModels.get(j).setMedical(singleRest.getBoolean("medical"));
                                            listModels.get(j).setRecreational(singleRest.getBoolean("recreational"));

                                            if (accountType.getString("account_name").equalsIgnoreCase("premium")) {
                                                listModels.get(j).setRestRating(5.0);
                                            } else {
                                                listModels.get(j).setRestRating(4.0);
                                            }

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
                                            listModels.get(j).setRestaurantTimings(timings);
//                                    listModels.get(i).setRestaurantName(singleRest.getString("name"));
//                                    StringBuilder stringBuilder = new StringBuilder();
//                                    stringBuilder.append(singleRest.getString("address_line"));
//                                    stringBuilder.append(", " + singleRest.getString("city"));
//                                    stringBuilder.append(", " + singleRest.getString("state"));
//                                    listModels.get(i).setRestaurantAddress(stringBuilder.toString());
                                        }

                                        SharedPreferences preferences = getSharedPreferences("chat_count_details", MODE_PRIVATE);
                                        String factualId = preferences.getString("factual_id", null);
                                        String chatCount = preferences.getString("chat_count", "0");
                                        if (factualId != null) {
                                            if (listModels.get(j).getPlaceID().equals(factualId)) {
                                                System.out.println("LOG: Modified chat count = " + chatCount);
                                                System.out.println("LOG: Actual chat count = " + listModels.get(j).getRestaurantChatAvailable());

                                                if (Integer.parseInt(chatCount) >= Integer.parseInt(listModels.get(j).getRestaurantChatAvailable())) {
                                                    listModels.get(j).setRestaurantChatAvailable(chatCount);
                                                } else {
                                                    listModels.get(j).setRestaurantChatAvailable(listModels.get(j).getRestaurantChatAvailable());
                                                }

//                                                if (!chatCount.equals(listModels.get(j).getRestaurantChatAvailable())) {
//                                                    listModels.get(j).setRestaurantChatAvailable(chatCount);
//                                                }
                                            }
                                        }
                                        break;
                                    }
                                }
                            }

                            if (isSearchedRestaurant) {
                                searchedRestaurantList.addAll(listModels);
                                searchedRestaurantList = initialSortedList();
                                filteredLocationList = searchedRestaurantList;
//                                filteredLocationList = locationListFilter(distance);
                                removeDuplicatedList();
                                filteredLocationAdapter = new LocationListAdapter(ListOfRestaurantActivity.this, filteredLocationList);
                            } else {
                                locationListModels.addAll(listModels);
                                locationListModels = initialSortedList();
                                filteredLocationList = locationListModels;
//                                filteredLocationList = locationListFilter(distance);
                                removeDuplicatedList();
                                filteredLocationAdapter = new LocationListAdapter(ListOfRestaurantActivity.this, filteredLocationList);
                            }

                            System.out.println("LOG: total rest count- " + locationListModels.size());

                            SpecificMenuSingleton menuSingleton = SpecificMenuSingleton.getInstance();
                            menuSingleton.setRestaurantList(null);
                            menuSingleton.setRestaurantList(filteredLocationList);
                            if (isSearchedLocation) {
                                menuSingleton.setLastLatitude(searchedLatitude);
                                menuSingleton.setLastLongitude(searchedLongitude);
                            } else {
                                menuSingleton.setLastLatitude(latitude);
                                menuSingleton.setLastLongitude(longitude);
                            }
//                        sortByQTSupported();

//                            filteredItemCount = filteredLocationAdapter.getCount();
//                            Log.i("FilteredList",""+filteredItemCount);
                            locationList.setAdapter(filteredLocationAdapter);
                            locationList.setOnItemClickListener(ListOfRestaurantActivity.this);
                            locationList.setOnScrollListener(ListOfRestaurantActivity.this);
                            locationList.addFooterView(loadMore);


//                            if (isSearchedRestaurant) {
//                                if (searchedPageCount == 2) {
//                                    locationList.removeFooterView(loadMore);
//                                    filteredLocationAdapter.notifyDataSetChanged();
//                                    locationList.setSelection(filteredLocationAdapter.getCount() - listModels.size());
//                                    locationList.addFooterView(loadMore);
//                                    isLoading = false;
//                                    isLoadingDone = false;
//                                } else if (searchedPageCount == 3) {
//                                    locationList.removeFooterView(loadMore);
//                                    filteredLocationAdapter.notifyDataSetChanged();
//                                    locationList.setSelection(filteredLocationAdapter.getCount() - listModels.size());
//                                }
//                            } else {
//                                if (pageCount == 2) {
//                                    locationList.removeFooterView(loadMore);
//                                    locationList.setSelection(filteredLocationAdapter.getCount() - listModels.size());
//                                    locationList.addFooterView(loadMore);
//                                    isLoading = false;
//                                    isLoadingDone = false;
//                                } else if (pageCount == 3) {
//                                    locationList.removeFooterView(loadMore);
//                                    locationList.setSelection(filteredLocationAdapter.getCount() - listModels.size());
//                                }
//                            }

                            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                            if (mapFragment != null) {
                                mapFragment.getMapAsync(ListOfRestaurantActivity.this);
                            }

//                            locationListAdapter = new LocationListAdapter(ListOfRestaurantActivity.this, locationListModels);
//                            locationList.setAdapter(new LocationListAdapter(ListOfRestaurantActivity.this, locationListModels));
                            spinner.setSelection(sortByClickPosition);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listOfRestaurantLayout.setVisibility(View.GONE);
                loadingImageLayout.setVisibility(View.GONE);
                if (error != null) {
                    if (error instanceof TimeoutError) {
                        Config.internetSlowError(ListOfRestaurantActivity.this);
                    } else if (error instanceof NoConnectionError) {
                        Config.internetSlowError(ListOfRestaurantActivity.this);
                    } else if (error instanceof ServerError) {
                        Config.serverError(ListOfRestaurantActivity.this);
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

    private void sortByDistance() {
        LocationListModel listModel = new LocationListModel();
        if (filteredLocationList.get(0).getRestaurantDistance() != null) {
            //if Distance is there, then only sort
            for (int i = 0; i < filteredLocationList.size() - 1; i++) {
                for (int j = 0; j < filteredLocationList.size() - 1 - i; j++) {
                    if (filteredLocationList.get(j).getRestaurantDistance() > (filteredLocationList.get(j + 1).getRestaurantDistance())) {
                        listModel = filteredLocationList.get(j);
                        filteredLocationList.set(j, filteredLocationList.get(j + 1));
                        filteredLocationList.set(j + 1, listModel);
                    }
                }
            }
        } else {
            Toast.makeText(this, "No Distance to sort!", Toast.LENGTH_SHORT).show();
        }
    }

    private ArrayList<LocationListModel> sortByDistance(ArrayList<LocationListModel> listModels) {
        LocationListModel listModel = new LocationListModel();
        if (listModels.size() > 0 && listModels.get(0).getRestaurantDistance() != null) { //if Distance is there, then only sort
            for (int i = 0; i < listModels.size() - 1; i++) {
                for (int j = 0; j < listModels.size() - 1 - i; j++) {
                    if (listModels.get(j).getRestaurantDistance() > (listModels.get(j + 1).getRestaurantDistance())) {
                        listModel = listModels.get(j);
                        listModels.set(j, listModels.get(j + 1));
                        listModels.set(j + 1, listModel);
                    }
                }
            }
        }
        return listModels;
    }

    private void sortByQuickChat() {
        LocationListModel listModel = new LocationListModel();

//        if (filteredLocationList.size() > 0) {

        for (int i = 0; i < filteredLocationList.size() - 1; i++) {
            for (int j = 0; j < filteredLocationList.size() - 1 - i; j++) {
                if (Integer.parseInt(filteredLocationList.get(j).getRestaurantChatAvailable()) < Integer.parseInt(filteredLocationList.get(j + 1).getRestaurantChatAvailable())) {
//                    if (filteredLocationList.get(j).getRestaurantChatAvailable().compareTo(filteredLocationList.get(j + 1).getRestaurantChatAvailable()) < 0) {
                    listModel = filteredLocationList.get(j);
                    filteredLocationList.set(j, filteredLocationList.get(j + 1));
                    filteredLocationList.set(j + 1, listModel);
                }
            }
        }

        ArrayList<LocationListModel> listModels = new ArrayList<>();
        listModels.addAll(filteredLocationList);
//        }
    }

    private void sortByRating() {
        LocationListModel listModel = new LocationListModel();
        for (int i = 0; i < filteredLocationList.size() - 1; i++) {
            for (int j = 0; j < filteredLocationList.size() - 1 - i; j++) {
                if (filteredLocationList.get(j).getRestRating() < (filteredLocationList.get(j + 1).getRestRating())) {
                    listModel = filteredLocationList.get(j);
                    filteredLocationList.set(j, filteredLocationList.get(j + 1));
                    filteredLocationList.set(j + 1, listModel);
                }
            }
        }
    }

    private void sortByQBSupported() {
        isQTSorted = false;
        int locationCount = 0;
        for (int i = 0; i < filteredLocationList.size(); i++) {
            if (filteredLocationList.get(i).getRestaurantAccountName().equalsIgnoreCase("premium")) {
                LocationListModel locationListModel = filteredLocationList.get(i);
                filteredLocationList.set(i, filteredLocationList.get(locationCount));
                filteredLocationList.set(locationCount, locationListModel);
                isQTSorted = true;
                locationCount++;
            }
        }

        for (int i = locationCount; i < filteredLocationList.size(); i++) {
            for (int j = locationCount; j < filteredLocationList.size() - 1 - i; j++) {
                if (filteredLocationList.get(j).getRestaurantDistance() > (filteredLocationList.get(j + 1).getRestaurantDistance())) {
                    LocationListModel listModel = filteredLocationList.get(j);
                    filteredLocationList.set(j, filteredLocationList.get(j + 1));
                    filteredLocationList.set(j + 1, listModel);
                }
            }
        }
    }

    private ArrayList<LocationListModel> initialSortedList() {
        ArrayList<LocationListModel> qtSortedList = new ArrayList<>();
        ArrayList<LocationListModel> distanceSortedList = new ArrayList<>();
        if (isSearchedRestaurant) {
            for (int i = 0; i < searchedRestaurantList.size(); i++) {
                if (searchedRestaurantList.get(i).getRestaurantEWT() != null) {
                    qtSortedList.add(searchedRestaurantList.get(i));
                    isQTSorted = true;
                } else {
                    distanceSortedList.add(searchedRestaurantList.get(i));
                }
            }
            qtSortedList = sortByDistance(qtSortedList);
            distanceSortedList = sortByDistance(distanceSortedList);
            searchedRestaurantList.clear();
            searchedRestaurantList.addAll(qtSortedList);
            searchedRestaurantList.addAll(distanceSortedList);
            return searchedRestaurantList;
        } else {
            for (int i = 0; i < locationListModels.size(); i++) {
                if (locationListModels.get(i).getRestaurantEWT() != null) {
                    qtSortedList.add(locationListModels.get(i));
                    isQTSorted = true;
                } else {
                    distanceSortedList.add(locationListModels.get(i));
                }
            }
            qtSortedList = sortByDistance(qtSortedList);
            distanceSortedList = sortByDistance(distanceSortedList);
            locationListModels.clear();
            locationListModels.addAll(qtSortedList);
            locationListModels.addAll(distanceSortedList);
            return locationListModels;
        }
    }

    private ArrayList<LocationListModel> qtInitialSortedList() {
        ArrayList<LocationListModel> qtSortedList = new ArrayList<>();
        ArrayList<LocationListModel> distanceSortedList = new ArrayList<>();
        for (int i = 0; i < qtRestaurantList.size(); i++) {
            if (qtRestaurantList.get(i).getRestaurantEWT() != null) {
                qtSortedList.add(qtRestaurantList.get(i));
            } else {
                distanceSortedList.add(qtRestaurantList.get(i));
            }
        }
        qtSortedList = sortByDistance(qtSortedList);
        distanceSortedList = sortByDistance(distanceSortedList);
        qtRestaurantList.clear();
        qtRestaurantList.addAll(qtSortedList);
        qtRestaurantList.addAll(distanceSortedList);
        return qtRestaurantList;
    }

    public ArrayList<LocationListModel> locationListFilter(int radius) {
        filteredItemCount = 0;
        filteredLocationList = new ArrayList<>();
        if (isSearchedRestaurant) {
            if (searchedRestaurantList.size() > 0 && searchedRestaurantList != null) {
                for (int i = 0; i < searchedRestaurantList.size(); i++) {
                    if (searchedRestaurantList.get(i).getRestaurantDistance() < radius) {
                        filteredLocationList.add(searchedRestaurantList.get(i));
                    }
                }
            }
        } else {
            if (locationListModels.size() > 0 && locationListModels != null) {
                for (int i = 0; i < locationListModels.size(); i++) {
                    if (locationListModels.get(i).getRestaurantDistance() < radius) {
                        filteredLocationList.add(locationListModels.get(i));
                    }
                }
            }
        }

        return filteredLocationList;
    }

    public void removeDuplicatedList() {

        for (int i = 0; i < filteredLocationList.size() - 1; i++) {
            for (int j = 0; j < filteredLocationList.size() - 1 - i; j++) {
                if (filteredLocationList.get(j).getPlaceID().equals(filteredLocationList.get(j + 1).getPlaceID())) {
                    filteredLocationList.remove(j);
                }
            }
        }
    }

    public ArrayList<LocationListModel> filterRestaurant(int radius) {
        filteredLocationList = new ArrayList<>();
        if (qtRestaurantList.size() > 0 && qtRestaurantList != null) {
            for (int i = 0; i < qtRestaurantList.size(); i++) {
                if (qtRestaurantList.get(i).getRestaurantDistance() < radius) {
                    filteredLocationList.add(qtRestaurantList.get(i));
                }
            }
        }
        return filteredLocationList;
    }

    public void getSearchedLocationLatLng(String searchedlocation) {

        isSearchedLocation = true;
        toolbar.setTitle(searchedRestaurantName);
        getQBRestaurantList(true);

//        listOfRestaurantLayout.setVisibility(View.GONE);
//        loadingImageLayout.setVisibility(View.VISIBLE);
//
//        String location = null;
//        try {
//            location = URLEncoder.encode(searchedlocation, "UTF-8");
//        } catch (UnsupportedEncodingException e) {
//        }
//
////        String geoCoderUrl = "http://maps.google.com/maps/api/geocode/json?sensor=true&address=" + location;
//        String geoCoderUrl = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=" + location + "&key=" + GOOGLE_KEY;
//
////        https://maps.googleapis.com/maps/api/place/textsearch/json?query=Rockne%27s+Pub%2C+Merriman+Road%2C+Akron%2C+OH%2C+United+States&key=AIzaSyADp-hM_UMCOeMN9HtAi4ycOPZLek-SqPQ
//
//        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, geoCoderUrl, null,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        Log.i("searchedlocation", response.toString());
//                        isSearchedLocation = true;
//                        try {
//                            JSONArray results = response.getJSONArray("results");
//                            JSONObject latLngObject = results.getJSONObject(0);
//
//                            JSONObject geometry = latLngObject.getJSONObject("geometry");
//                            JSONObject location = geometry.getJSONObject("location");
//
//                            toolbar.setTitle(latLngObject.getString("formatted_address"));
//
//                            searchedLatitude = location.getDouble("lat");
//                            searchedLongitude = location.getDouble("lng");
//
//                        } catch (JSONException e) {
//
//                        }
//
////                        searchedLatitude = address.getLatitude();
////                        searchedLongitude = address.getLongitude();
////                        String searchedUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
////                                + searchedLatitude + "," + searchedLongitude + "&rankby=distance&types=restaurant|cafe&key=" + GOOGLE_KEY;
////                        getListOfRestaurant(searchedUrl);
//
//                        // --------------- Code for White labelling--------------------------
//                        if (Config.APP_NAME.equalsIgnoreCase("QuickTable")) {
////                            getTenantNames();
//                            getQBRestaurantList(true);
//                        } else {
//                            getRestaurantList();
//                        }
//
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                if (error != null) {
//                    if (error instanceof TimeoutError) {
//                        Config.internetSlowError(ListOfRestaurantActivity.this);
//                    } else if (error instanceof NoConnectionError) {
//                        Config.internetSlowError(ListOfRestaurantActivity.this);
//                    } else if (error instanceof ServerError) {
//                        Config.serverError(ListOfRestaurantActivity.this);
//                    }
//                }
//            }
//        });
//
//        request.setRetryPolicy(new DefaultRetryPolicy(
//                Config.TIMEOUT,  // 25 sec - timeout
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
//        ));
//
//        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    public void getTenantNames() {

        listOfRestaurantLayout.setVisibility(View.GONE);
        loadingImageLayout.setVisibility(View.VISIBLE);

        if (!isSearchedLocation) {
            geocoder = new Geocoder(this, Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                if (addresses.size() > 0) {
                    if (addresses.get(0).getAddressLine(0) != null) {
                        currentAddress1 = addresses.get(0).getSubThoroughfare();
                    }
                    currentAddress2 = addresses.get(0).getThoroughfare();
                    currentCity = addresses.get(0).getLocality();
                    currentState = addresses.get(0).getAdminArea();
                } else {

                }
            } catch (IOException e) {

            }

            if (currentCity != null && currentState != null) {
                if (currentAddress2 == null && currentAddress1 == null) {
                    toolbar.setTitle(currentCity + " " + currentState);
                } else if (currentAddress2 == null) {
                    toolbar.setTitle(currentAddress1 + " " + currentCity + " " + currentState);
                } else if (currentAddress1 == null) {
                    toolbar.setTitle(currentAddress2 + " " + currentCity + " " + currentState);
                } else {
                    toolbar.setTitle(currentAddress1 + " " + currentAddress2 + " " + currentCity + " " + currentState);
                }
            }
        }

        final ArrayList<TenantModel> tenantModels = new ArrayList<>();
        String url = Config.QT_TENANT_URL;

        final JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.i("TenantNames", response.toString());

                        for (int i = 0; i < response.length(); i++) {
                            TenantModel tenantModel = new TenantModel();
                            try {
                                JSONObject tenantObj = response.getJSONObject(i);
                                tenantModel.setTenantName(tenantObj.getString("name"));
                                tenantModels.add(tenantModel);
                            } catch (JSONException e) {
                            }
                        }

                        StringBuilder stringBuilder = new StringBuilder();
                        if (tenantModels.size() > 0) {
                            stringBuilder.append(tenantModels.get(0).getTenantName());
                            for (int i = 1; i < tenantModels.size(); i++) {
                                stringBuilder.append(", " + tenantModels.get(i).getTenantName());
                            }
                        }

//                        Query query;
//                        if (isSearchedLocation) {
//                            query = new Query()
//                                    .within(new Circle(searchedLatitude, searchedLongitude, 25000))
//                                    .field("category_ids").includes(347)
////                                    .field("category_ids").includesAny(348, 349, 352, 354, 357, 358, 359, 360, 364, 365)
////                                    .field("category_ids").notIn(355)
////                                    .field("category_labels").includes("Social, Food and Dining, Restaurants")
////                                    .field("cuisine").in(cuisine)
//                                    .field("name").notIn(filterFastFood)
////                                .field("name").equal(stringBuilder.toString())
//                                    .search(stringBuilder.toString())
//                                    .sortAsc("$distance")
//                                    .only("name", "address", "factual_id", "latitude", "longitude", "locality", "region", "tel")
//                                    .includeRowCount(true)
//                                    .limit(50);
//                        } else {
//                            query = new Query()
//                                    .within(new Circle(latitude, longitude, 25000))
//                                    .field("category_ids").includes(347)
////                                    .field("category_ids").includesAny(348, 349, 352, 354, 357, 358, 359, 360, 364, 365)
////                                    .field("category_ids").notIn(355)
////                                    .field("category_labels").includes("Social, Food and Dining, Restaurants")
////                                    .field("cuisine").in(cuisine)
//                                    .field("name").notIn(filterFastFood)
////                                .field("name").equal(stringBuilder.toString())
//                                    .search(stringBuilder.toString())
//                                    .sortAsc("$distance")
//                                    .only("name", "address", "factual_id", "latitude", "longitude", "locality", "region", "tel")
//                                    .includeRowCount(true)
//                                    .limit(50);
//                        }
//
//                        FactualQTRestaurantListAsyncTask asyncTask = new FactualQTRestaurantListAsyncTask();
//                        asyncTask.execute(query);
//                        restaurantListUrl = getListOfTenantRestaurantURL(tenantModels);
//                        restaurantListUrl = getFactualQTRestaurantListUrl(tenantModels);
//                        getTenantRestaurantList();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("Tenants Name Error", error.toString());
                listOfRestaurantLayout.setVisibility(View.GONE);
                loadingImageLayout.setVisibility(View.GONE);
                if (error instanceof TimeoutError) {
                    Config.internetSlowError(ListOfRestaurantActivity.this);
                } else if (error instanceof NoConnectionError) {
                    Config.internetSlowError(ListOfRestaurantActivity.this);
                } else if (error instanceof ServerError) {
                    Config.serverError(ListOfRestaurantActivity.this);
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
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.fab_vote_win:
//                Intent chatIntent = new Intent(this, ChatImages.class);
//                startActivity(chatIntent);
//                break;
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        Log.i("Scroll Stage", String.valueOf(scrollState));
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (locationList.getAdapter() == null)
            return;

        if (locationList.getAdapter().getCount() == 0)
            return;

        if (visibleItemCount + firstVisibleItem >= totalItemCount && !isLoading) {
            isLoading = true;
            if (totalItemCount == filteredLocationList.size() + 1 && !isLoadingDone) {
                isLoadingDone = true;
                Log.i("Next Disp.", "Loading next 50 dispensary!");
                getQBRestaurantList(false);

//                if (isSearchedRestaurant) {
//                    searchedPageCount++;
//                    if (searchedPageCount == 2) {
//                        Log.i("searchedListView", "first listView ends here!!!");
////                        searchedSecondRestaurantList();
//                    } else if (searchedPageCount == 3) {
//                        Log.i("searchedListView", "Second listView ends here!!!");
////                        searchedThirdRestaurantList();
//                    }
//                } else {
//                    pageCount++;
//                    if (pageCount == 2) {
//                        Log.i("ListView", "first listView ends here!!!");
////                        getTenantSecondRestaurantList();
//                    } else if (pageCount == 3) {
//                        Log.i("ListView", "Second listView ends here!!!");
////                        getTenantThirdRestaurantList();
//                    }
//                }
            }
        }
    }

    private class SearchByNameRestaurantList extends AsyncTask<Void, String, Void> {

        @Override
        protected Void doInBackground(Void... param) {
            YelpAPIFactory apiFactory = new YelpAPIFactory(Config.YELP_CONSUMER_KEY, Config.YELP_CONSUMER_SECRET,
                    Config.YELP_TOKEN, Config.YELP_TOKEN_SECRET);
            YelpAPI yelpAPI = apiFactory.createAPI();
            Map<String, String> params = new HashMap<>();
            Call<SearchResponse> call = null;
            listModels = new ArrayList<>();
            newListModels = new ArrayList<>();

            String[] splitRestName = searchedRestaurantName.split(" ");
            String restName = splitRestName[0];

            if (searchedCategory != null && !searchedCategory.isEmpty()) {
                params.put("term", restName);
                if (searchedCategory.contains("all")) {
//                    params.put("category_filter", "");
                } else {
                    params.put("category_filter", searchedCategory);
                }
                params.put("sort", "1");
//                    params.put("radius_filter", "40000");
                CoordinateOptions coordinate = null;
                if (isSearchedLocation) {
                    coordinate = CoordinateOptions.builder()
                            .latitude(searchedLatitude)
                            .longitude(searchedLongitude).build();
                } else {
                    coordinate = CoordinateOptions.builder()
                            .latitude(latitude)
                            .longitude(longitude).build();
                }
                call = yelpAPI.search(coordinate, params);
            } else {
                params.put("term", restName);
//                params.put("category_filter", "newamerican,tradamerican");
                params.put("sort", "1");
//                    params.put("radius_filter", "40000");
                CoordinateOptions coordinate = null;
//                            .latitude(latitude)
//                            .longitude(longitude).build();
                if (isSearchedLocation) {
                    coordinate = CoordinateOptions.builder()
                            .latitude(searchedLatitude)
                            .longitude(searchedLongitude).build();
                } else {
                    coordinate = CoordinateOptions.builder()
                            .latitude(latitude)
                            .longitude(longitude).build();
                }
                call = yelpAPI.search(coordinate, params);
            }

            retrofit2.Response<SearchResponse> response = null;
            try {
                response = call.execute();
                System.out.println("LOG: Restaurant List - " + response.body());
//                filteredLocationList = new ArrayList<>();
                yelpTotalList = response.body().total();
                ArrayList<Business> businesses = response.body().businesses();
                if (businesses.size() > 0) {
                    LocationListModel locationListModel = new LocationListModel();
                    locationListModel.setRestaurantName(businesses.get(0).name());
                    if (businesses.get(0).location().address() != null && businesses.get(0).location().address().size() > 0) {
                        locationListModel.setRestaurantAddress(businesses.get(0).location().address().get(0));
                    }
                    locationListModel.setRestaurantCity(businesses.get(0).location().city());
                    locationListModel.setRestaurantState(businesses.get(0).location().stateCode());
                    locationListModel.setRestaurantDistance(businesses.get(0).distance());
                    locationListModel.setRestaurantPhone(businesses.get(0).displayPhone());
                    locationListModel.setRatingImage(businesses.get(0).ratingImgUrlLarge());
                    locationListModel.setPlaceID(businesses.get(0).id());
                    locationListModel.setLatitude(businesses.get(0).location().coordinate().latitude());
                    locationListModel.setLongitude(businesses.get(0).location().coordinate().longitude());
                    locationListModel.setRestRating(businesses.get(0).rating());
                    locationListModel.setRestaurantImage(businesses.get(0).imageUrl());
                    if (businesses.get(0).deals() != null && (businesses.get(0).deals().size() > 0)) {
                        locationListModel.setDealActive(true);
                    }
                    newListModels.add(locationListModel);
                }
                listModels.addAll(newListModels);

            } catch (IOException e) {
                e.printStackTrace();
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

            new FirstYelpRestaurantList().execute();

        }
    }

    private class FirstYelpRestaurantList extends AsyncTask<Void, String, Void> {

        @Override
        protected Void doInBackground(Void... param) {
            YelpAPIFactory apiFactory = new YelpAPIFactory(Config.YELP_CONSUMER_KEY, Config.YELP_CONSUMER_SECRET,
                    Config.YELP_TOKEN, Config.YELP_TOKEN_SECRET);
            YelpAPI yelpAPI = apiFactory.createAPI();
            Map<String, String> params = new HashMap<>();
            Call<SearchResponse> call = null;
//            listModels = new ArrayList<>();
            newListModels = new ArrayList<>();

            if (searchedCategory != null && !searchedCategory.isEmpty()) {
                params.put("term", "Dispensaries");
                if (searchedCategory.contains("all")) {
//                    params.put("category_filter", "cannabis_clinics,cannabisreferrals,cannabisdispensaries");
                } else {
                    params.put("category_filter", searchedCategory);
                }
                params.put("sort", "1");
//                    params.put("radius_filter", "40000");
                CoordinateOptions coordinate = null;
                if (isSearchedLocation) {
                    coordinate = CoordinateOptions.builder()
                            .latitude(searchedLatitude)
                            .longitude(searchedLongitude).build();
                } else {
                    coordinate = CoordinateOptions.builder()
                            .latitude(latitude)
                            .longitude(longitude).build();
                }
                call = yelpAPI.search(coordinate, params);
            } else {
                params.put("term", "Dispensaries");
//                params.put("category_filter", "newamerican,tradamerican");
                params.put("sort", "1");
//                    params.put("radius_filter", "40000");
                CoordinateOptions coordinate = null;
//                            .latitude(latitude)
//                            .longitude(longitude).build();
                if (isSearchedLocation) {
                    coordinate = CoordinateOptions.builder()
                            .latitude(searchedLatitude)
                            .longitude(searchedLongitude).build();
                } else {
                    coordinate = CoordinateOptions.builder()
                            .latitude(latitude)
                            .longitude(longitude).build();
                }
                call = yelpAPI.search(coordinate, params);
            }

            retrofit2.Response<SearchResponse> response = null;
            try {
                response = call.execute();
                System.out.println("LOG: Restaurant List - " + response.body());
//                filteredLocationList = new ArrayList<>();
                yelpTotalList = response.body().total();
                ArrayList<Business> businesses = response.body().businesses();
                for (int i = 0; i < businesses.size(); i++) {
                    LocationListModel locationListModel = new LocationListModel();
                    locationListModel.setRestaurantName(businesses.get(i).name());
                    if (businesses.get(i).location().address() != null && businesses.get(i).location().address().size() > 0) {
                        locationListModel.setRestaurantAddress(businesses.get(i).location().address().get(0));
                    }
                    locationListModel.setRestaurantCity(businesses.get(i).location().city());
                    locationListModel.setRestaurantState(businesses.get(i).location().stateCode());
                    locationListModel.setRestaurantDistance(businesses.get(i).distance());
                    locationListModel.setRestaurantPhone(businesses.get(i).displayPhone());
                    locationListModel.setRatingImage(businesses.get(i).ratingImgUrlLarge());
                    locationListModel.setPlaceID(businesses.get(i).id());
                    locationListModel.setLatitude(businesses.get(i).location().coordinate().latitude());
                    locationListModel.setLongitude(businesses.get(i).location().coordinate().longitude());
                    locationListModel.setRestRating(businesses.get(i).rating());
                    locationListModel.setRestaurantImage(businesses.get(i).imageUrl());
                    if (businesses.get(i).deals() != null && (businesses.get(i).deals().size() > 0)) {
                        locationListModel.setDealActive(true);
                    }
                    newListModels.add(locationListModel);
                }
                listModels.addAll(newListModels);

            } catch (IOException e) {
                e.printStackTrace();
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
            if (!isSearchedLocation) {
                geocoder = new Geocoder(ListOfRestaurantActivity.this, Locale.getDefault());
                try {
                    List<Address> addresses = null;
                    if (isSearchedLocation) {
                        addresses = geocoder.getFromLocation(searchedLatitude, searchedLongitude, 1);
                    } else {
                        addresses = geocoder.getFromLocation(latitude, longitude, 1);
                    }

                    if (addresses.size() > 0) {
                        if (addresses.get(0).getAddressLine(0) != null) {
//                                currentAddress1 = addresses.get(0).getAddressLine(0);
                            currentAddress1 = addresses.get(0).getSubThoroughfare();
                        }
//                            currentAddress2 = addresses.get(0).getAddressLine(1);
                        currentAddress2 = addresses.get(0).getThoroughfare();
                        currentCity = addresses.get(0).getLocality();
                        currentState = addresses.get(0).getAdminArea();
                    } else {

                    }
                } catch (IOException e) {

                }
                if (currentCity != null && currentState != null) {
                    if (currentAddress2 == null && currentAddress1 == null) {
                        toolbar.setTitle(currentCity + " " + currentState);
                    } else if (currentAddress2 == null) {
                        toolbar.setTitle(currentAddress1 + " " + currentCity + " " + currentState);
                    } else if (currentAddress1 == null) {
                        toolbar.setTitle(currentAddress2 + " " + currentCity + " " + currentState);
                    } else {
                        toolbar.setTitle(currentAddress1 + " " + currentAddress2 + " " + currentCity + " " + currentState);
                    }
                }
            }

            Log.i("Yelp Total List", "" + yelpTotalList);

            if (yelpTotalList > 20) {
                new SecondYelpRestaurantList().execute();
            } else {
                getQTSupportedRestaurants(listModels);
            }

        }
    }

//    private void showErrorMsg() {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                ConnectionDetector connectionDetector = new ConnectionDetector(ListOfRestaurantActivity.this);
//                connectionDetector.internetError();
//            }
//        });
//    }

    private class SecondYelpRestaurantList extends AsyncTask<Void, String, Void> {

        @Override
        protected Void doInBackground(Void... param) {
            YelpAPIFactory apiFactory = new YelpAPIFactory(Config.YELP_CONSUMER_KEY, Config.YELP_CONSUMER_SECRET,
                    Config.YELP_TOKEN, Config.YELP_TOKEN_SECRET);
            YelpAPI yelpAPI = apiFactory.createAPI();
            Map<String, String> params = new HashMap<>();
            Call<SearchResponse> call = null;
            newListModels = new ArrayList<>();

            if (searchedCategory != null && !searchedCategory.isEmpty()) {
                params.put("term", "Dispensaries");
                if (searchedCategory.contains("all")) {
//                    params.put("category_filter", "cannabis_clinics,cannabisreferrals,cannabisdispensaries");
                } else {
                    params.put("category_filter", searchedCategory);
                }
                params.put("sort", "1");
//                    params.put("radius_filter", "40000");
                params.put("offset", "20");
                CoordinateOptions coordinate = null;
//                            .latitude(latitude)
//                            .longitude(longitude).build();
                if (isSearchedLocation) {
                    coordinate = CoordinateOptions.builder()
                            .latitude(searchedLatitude)
                            .longitude(searchedLongitude).build();
                } else {
                    coordinate = CoordinateOptions.builder()
                            .latitude(latitude)
                            .longitude(longitude).build();
                }
                call = yelpAPI.search(coordinate, params);
            } else {
                params.put("term", "Dispensaries");
//                params.put("category_filter", "newamerican,tradamerican");
//                    params.put("radius_filter", "40000");
                params.put("sort", "1");
                params.put("offset", "20");
                CoordinateOptions coordinate = null;
//                            .latitude(latitude)
//                            .longitude(longitude).build();
                if (isSearchedLocation) {
                    coordinate = CoordinateOptions.builder()
                            .latitude(searchedLatitude)
                            .longitude(searchedLongitude).build();
                } else {
                    coordinate = CoordinateOptions.builder()
                            .latitude(latitude)
                            .longitude(longitude).build();
                }
                call = yelpAPI.search(coordinate, params);
            }

//            if ((searchedRestaurantName != null) || (searchedCategory != null && !searchedCategory.isEmpty())) {
//
//                if ((searchedCategory == null && searchedCategory.isEmpty()) && searchedRestaurantName != null) {
//                    // general params
//                    params.put("term", "Restaurants");
//                    params.put("category_filter", "newamerican,tradamerican");
//                    params.put("sort", "1");
////                    params.put("radius_filter", "40000");
//                    params.put("offset", "20");
//                    CoordinateOptions coordinate = null;
////                            .latitude(latitude)
////                            .longitude(longitude).build();
//                    if (isSearchedLocation){
//                        coordinate = CoordinateOptions.builder()
//                                .latitude(searchedLatitude)
//                                .longitude(searchedLongitude).build();
//                    }else {
//                        coordinate = CoordinateOptions.builder()
//                                .latitude(latitude)
//                                .longitude(longitude).build();
//                    }
//                    call = yelpAPI.search(coordinate, params);
//                } else if (searchedRestaurantName == null && (searchedCategory != null && !searchedCategory.isEmpty())) {
//                    // general params
//                    params.put("term", "Restaurants");
//                    if (searchedCategory.contains("all")){
//                        params.put("category_filter", "");
//                    }else {
//                        params.put("category_filter", searchedCategory);
//                    }
////                    params.put("radius_filter", "40000");
//                    params.put("sort", "1");
//                    params.put("offset", "20");
//                    CoordinateOptions coordinate = null;
////                            .latitude(latitude)
////                            .longitude(longitude).build();
//                    if (isSearchedLocation){
//                        coordinate = CoordinateOptions.builder()
//                                .latitude(searchedLatitude)
//                                .longitude(searchedLongitude).build();
//                    }else {
//                        coordinate = CoordinateOptions.builder()
//                                .latitude(latitude)
//                                .longitude(longitude).build();
//                    }
//                    call = yelpAPI.search(coordinate, params);
//                } else {
//                    // general params
//                    params.put("term", "Restaurants");
//                    if (searchedCategory.contains("all")){
//                        params.put("category_filter", "");
//                    }else {
//                        params.put("category_filter", searchedCategory);
//                    }
////                    params.put("radius_filter", "40000");
//                    params.put("sort", "1");
//                    params.put("offset", "20");
//                    CoordinateOptions coordinate = null;
////                            .latitude(latitude)
////                            .longitude(longitude).build();
//                    if (isSearchedLocation){
//                        coordinate = CoordinateOptions.builder()
//                                .latitude(searchedLatitude)
//                                .longitude(searchedLongitude).build();
//                    }else {
//                        coordinate = CoordinateOptions.builder()
//                                .latitude(latitude)
//                                .longitude(longitude).build();
//                    }
//                    call = yelpAPI.search(coordinate, params);
//                }
//
//            } else {
//                // general params
//                params.put("term", "Restaurants");
//                params.put("category_filter", "newamerican,tradamerican");
//                params.put("sort", "1");
//                params.put("offset", "20");
////                params.put("radius_filter", "40000");
//                CoordinateOptions coordinate = null;
////                            .latitude(latitude)
////                            .longitude(longitude).build();
//                if (isSearchedLocation){
//                    coordinate = CoordinateOptions.builder()
//                            .latitude(searchedLatitude)
//                            .longitude(searchedLongitude).build();
//                }else {
//                    coordinate = CoordinateOptions.builder()
//                            .latitude(latitude)
//                            .longitude(longitude).build();
//                }
//                call = yelpAPI.search(coordinate, params);
//            }

            retrofit2.Response<SearchResponse> response = null;
            try {
                response = call.execute();
                System.out.println("LOG: Restaurant List - " + response.body());
//                filteredLocationList = new ArrayList<>();
                ArrayList<Business> businesses = response.body().businesses();
                for (int i = 0; i < businesses.size(); i++) {
                    LocationListModel locationListModel = new LocationListModel();
                    locationListModel.setRestaurantName(businesses.get(i).name());
                    if (businesses.get(i).location().address() != null && businesses.get(i).location().address().size() > 0) {
                        locationListModel.setRestaurantAddress(businesses.get(i).location().address().get(0));
                    }
                    locationListModel.setRestaurantCity(businesses.get(i).location().city());
                    locationListModel.setRestaurantState(businesses.get(i).location().stateCode());
                    locationListModel.setRestaurantDistance(businesses.get(i).distance());
                    locationListModel.setRestaurantPhone(businesses.get(i).displayPhone());
                    locationListModel.setRatingImage(businesses.get(i).ratingImgUrl());
                    locationListModel.setPlaceID(businesses.get(i).id());
                    if (businesses.get(i).location().coordinate() != null) {
                        locationListModel.setLatitude(businesses.get(i).location().coordinate().latitude());
                        locationListModel.setLongitude(businesses.get(i).location().coordinate().longitude());
                    }
                    locationListModel.setRestRating(businesses.get(i).rating());
                    locationListModel.setRestaurantImage(businesses.get(i).imageUrl());
                    if (businesses.get(i).deals() != null && (businesses.get(i).deals().size() > 0)) {
                        locationListModel.setDealActive(true);
                    }

                    newListModels.add(locationListModel);
                }
                listModels.addAll(newListModels);
            } catch (IOException e) {
                e.printStackTrace();
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

            getQTSupportedRestaurants(listModels);

        }
    }

//    private class SearchedRestaurantList extends AsyncTask<Query, Void, List<ReadResponse>> {
//
//        @Override
//        protected List<ReadResponse> doInBackground(Query... params) {
//            List<ReadResponse> list = Lists.newArrayList();
//            try {
//                for (Query q : params) {
//                    list.add(factual.fetch("restaurants-us", q));
//                }
//            } catch (FactualApiException e) {
//                Log.i("FactualException", "Exception Occurred - " + e.toString());
//            }
//            return list;
//        }
//
//        @Override
//        protected void onPostExecute(List<ReadResponse> readResponses) {
//            Log.i("SearchedRestList", readResponses.toString());
//            listModels = new ArrayList<>();
//            newListModels = new ArrayList<>();
//            jsonParser = new JsonParser();
//            newListModels = jsonParser.getFactualRestaurantList(readResponses.toString());
//
//            listModels.addAll(newListModels);
//
//            if (searchedRestaurantList.size() > 0) {
//                searchedRestaurantList.clear();
//            }
//
//            getQTSupportedRestaurants(listModels);
//        }
//    }

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

    private void getRestaurantList() {

        footerLayout.setVisibility(View.GONE);

        String url = Config.QT_WHITE_LABEL_URL;

        JSONObject object = new JSONObject();
        try {
            if (isSearchedLocation) {
                object.put("latitude", searchedLatitude);
                object.put("longitude", searchedLongitude);
                object.put("tenant_id", Config.APP_TENANT_ID);
            } else {
                object.put("latitude", latitude);
                object.put("longitude", longitude);
                object.put("tenant_id", Config.APP_TENANT_ID);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        System.out.println("Post Object = " + object);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("LOG: White Label Rest List response = " + response);

                        JsonParser parser = new JsonParser();
                        filteredLocationList = new ArrayList<>();
                        filteredLocationList = parser.getWhiteLabelledList(response);

                        listOfRestaurantLayout.setVisibility(View.VISIBLE);
                        loadingImageLayout.setVisibility(View.GONE);

                        filteredLocationList = initialSortedList();
                        if (isQuickChatSorted) {
                            sortByQuickChat();
                        }
                        LocationListAdapter adapter = new LocationListAdapter(ListOfRestaurantActivity.this, filteredLocationList);
                        locationList.setAdapter(adapter);

                        locationList.setOnItemClickListener(ListOfRestaurantActivity.this);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listOfRestaurantLayout.setVisibility(View.GONE);
                loadingImageLayout.setVisibility(View.GONE);
                if (error != null) {
                    if (error instanceof TimeoutError) {
                        Config.internetSlowError(ListOfRestaurantActivity.this);
                    } else if (error instanceof NoConnectionError) {
                        Config.internetSlowError(ListOfRestaurantActivity.this);
                    } else if (error instanceof ServerError) {
                        Config.internetSlowError(ListOfRestaurantActivity.this);
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

    private void getQBRestaurantList(final boolean isFirst) {

        String url = Config.QB_REST_LIST;

        JSONObject postObj = new JSONObject();

        try {
            if (searchedCategory != null && !searchedCategory.isEmpty()) {
                String[] categoriesSplit = searchedCategory.split(", ");
                JSONArray categoriesArray = new JSONArray();
                for (int i = 0; i < categoriesSplit.length; i++) {
                    categoriesArray.put(categoriesSplit[i]);
                }
                postObj.put("searchkey", categoriesArray);
            } else {
                JSONArray categoriesArray = new JSONArray();
                categoriesArray.put("all");
                postObj.put("searchkey", categoriesArray);
            }

            JSONArray placeIdArray = new JSONArray();
            if (!isFirst) {
                for (int i = 0; i < filteredLocationList.size(); i++) {
                    placeIdArray.put(filteredLocationList.get(i).getPlaceID());
                }
            }
            postObj.put("placeId", placeIdArray);

            if (isSearchedLocation) {
                postObj.put("latitude", searchedLatitude);
                postObj.put("longitude", searchedLongitude);
            } else {
                postObj.put("latitude", latitude);
                postObj.put("longitude", longitude);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.i("Dispensary Post", String.valueOf(postObj));

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, postObj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("QBRestaurantList", response.toString());
                        listOfRestaurantLayout.setVisibility(View.VISIBLE);
                        loadingImageLayout.setVisibility(View.GONE);

//                        listModels = new ArrayList<>();
                        newListModels = new ArrayList<>();

                        try {
                            JSONArray responseArray = response.getJSONArray("result");

                            for (int i = 0; i < responseArray.length(); i++) {
                                JSONObject responseObj = responseArray.getJSONObject(i);

                                double dispensaryLat = responseObj.getDouble("latitude");
                                double dispensaryLong = responseObj.getDouble("longitude");
                                Location currentLocation = new Location("currentLocation");
                                if (isSearchedLocation) {
                                    currentLocation.setLatitude(searchedLatitude);
                                    currentLocation.setLongitude(searchedLongitude);
                                } else {
                                    currentLocation.setLatitude(latitude);
                                    currentLocation.setLongitude(longitude);
                                }
                                Location dispensaryLocation = new Location("dispensaryLocation");
                                dispensaryLocation.setLatitude(dispensaryLat);
                                dispensaryLocation.setLongitude(dispensaryLong);

                                double distance = currentLocation.distanceTo(dispensaryLocation);

                                LocationListModel locationListModel = new LocationListModel();

                                locationListModel.setRestaurantName(responseObj.getString("name"));
                                locationListModel.setRestaurantAddress(responseObj.getString("address_line"));
                                locationListModel.setRestaurantCity(responseObj.getString("city"));
                                locationListModel.setRestaurantState(responseObj.getString("state"));
//                                locationListModel.setRestaurantDistance(distance);
                                locationListModel.setRestaurantDistance(responseObj.getDouble("distance"));
                                locationListModel.setPlaceID(responseObj.getString("placeId"));
                                locationListModel.setLatitude(responseObj.getDouble("latitude"));
                                locationListModel.setLongitude(responseObj.getDouble("longitude"));
                                locationListModel.setRestaurantImage(responseObj.getString("url"));
                                locationListModel.setRestaurantPhone(responseObj.getString("phone_number"));
                                locationListModel.setRestaurantAccountName(responseObj.getString("account_type"));
                                if (responseObj.has("menu_exists")){
                                    locationListModel.setMenuAvailable(responseObj.getBoolean("menu_exists"));
                                }
                                locationListModel.setQtSupported(true);

                                if (locationListModel.getRestaurantAccountName().equalsIgnoreCase("premium")) {
                                    locationListModel.setRestRating(5.0);
                                } else {
                                    locationListModel.setRestRating(4.0);
                                }

                                newListModels.add(locationListModel);
                            }

//                            listModels.addAll(newListModels);

//                            getQTSupportedRestaurants(listModels);

                            if (isSearchedRestaurant) {
                                searchedRestaurantList.addAll(newListModels);
//                                searchedRestaurantList = initialSortedList();
                                filteredLocationList = searchedRestaurantList;
//                                filteredLocationList = locationListFilter(distance);
//                                removeDuplicatedList();
                                filteredLocationAdapter = new LocationListAdapter(ListOfRestaurantActivity.this, filteredLocationList);
                            } else {
                                locationListModels.addAll(newListModels);
//                                locationListModels = initialSortedList();
                                filteredLocationList = locationListModels;
//                                filteredLocationList = locationListFilter(distance);
//                                removeDuplicatedList();
                                filteredLocationAdapter = new LocationListAdapter(ListOfRestaurantActivity.this, filteredLocationList);
                            }

                            SpecificMenuSingleton menuSingleton = SpecificMenuSingleton.getInstance();
                            menuSingleton.setRestaurantList(null);
                            menuSingleton.setRestaurantList(filteredLocationList);
                            if (isSearchedLocation) {
                                menuSingleton.setLastLatitude(searchedLatitude);
                                menuSingleton.setLastLongitude(searchedLongitude);
                            } else {
                                menuSingleton.setLastLatitude(latitude);
                                menuSingleton.setLastLongitude(longitude);
                            }
//                        sortByQTSupported();

//                            filteredItemCount = filteredLocationAdapter.getCount();
//                            Log.i("FilteredList",""+filteredItemCount);
                            if (isFirst) {
                                locationList.setAdapter(filteredLocationAdapter);
                                locationList.setOnItemClickListener(ListOfRestaurantActivity.this);
                                locationList.setOnScrollListener(ListOfRestaurantActivity.this);

                            } else {
                                filteredLocationAdapter.notifyDataSetChanged();
                                locationList.removeFooterView(loadMore);
//                                if(responseArray.length() != 0){
//                                    locationList.addFooterView(loadMore);
//                                    isLoading = false;
//                                    isLoadingDone = false;
//                                }else {
//                                    locationList.removeFooterView(loadMore);
//                                    isLoading = true;
//                                    isLoadingDone = true;
//                                }
//                                isLoading = false;
//                                isLoadingDone = false;
                            }

                            if (responseArray.length() != 0) {
                                locationList.addFooterView(loadMore);
                                isLoading = false;
                                isLoadingDone = false;
                            } else {
                                locationList.removeFooterView(loadMore);
                                isLoading = true;
                                isLoadingDone = true;
                            }


//                            if (isSearchedRestaurant) {
//                                if (searchedPageCount == 2) {
//                                    locationList.removeFooterView(loadMore);
//                                    filteredLocationAdapter.notifyDataSetChanged();
//                                    locationList.setSelection(filteredLocationAdapter.getCount() - listModels.size());
//                                    locationList.addFooterView(loadMore);
//                                    isLoading = false;
//                                    isLoadingDone = false;
//                                } else if (searchedPageCount == 3) {
//                                    locationList.removeFooterView(loadMore);
//                                    filteredLocationAdapter.notifyDataSetChanged();
//                                    locationList.setSelection(filteredLocationAdapter.getCount() - listModels.size());
//                                }
//                            } else {
//                                if (pageCount == 2) {
//                                    locationList.removeFooterView(loadMore);
//                                    locationList.setSelection(filteredLocationAdapter.getCount() - listModels.size());
//                                    locationList.addFooterView(loadMore);
//                                    isLoading = false;
//                                    isLoadingDone = false;
//                                } else if (pageCount == 3) {
//                                    locationList.removeFooterView(loadMore);
//                                    locationList.setSelection(filteredLocationAdapter.getCount() - listModels.size());
//                                }
//                            }

                            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                            if (mapFragment != null) {
                                mapFragment.getMapAsync(ListOfRestaurantActivity.this);
                            }

//                            locationListAdapter = new LocationListAdapter(ListOfRestaurantActivity.this, locationListModels);
//                            locationList.setAdapter(new LocationListAdapter(ListOfRestaurantActivity.this, locationListModels));
                            spinner.setSelection(sortByClickPosition);

//                            if (isSearchByName){
//                                new SearchByNameRestaurantList().execute();
//                            }else {
//                                new FirstYelpRestaurantList().execute();
//                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error != null) {
                    if (error instanceof TimeoutError) {
                        Config.internetSlowError(ListOfRestaurantActivity.this);
                    } else if (error instanceof NoConnectionError) {
                        Config.internetSlowError(ListOfRestaurantActivity.this);
                    } else if (error instanceof ServerError) {
                        Config.serverError(ListOfRestaurantActivity.this);
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

        VolleySingleton.getInstance(this).addToRequestQueue(request);

    }

    private void shareApp() {
        final SharedPreferences sharedPreferences = getSharedPreferences("user_info", Context.MODE_PRIVATE);
        String userName = sharedPreferences.getString("user_name", null);
//        Uri imageUri = Uri.parse("android.resource://" + getPackageName()+ "/mipmap/" + "icon_qt_small");
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

        // Add data to the intent, the receiving app will decide
        // what to do with it.
//        share.putExtra(Intent.EXTRA_STREAM, imageUri);
        share.putExtra(Intent.EXTRA_TEXT, "Hey ! " + userName.toUpperCase() + " has personally invited you to download the new QuickTable app…it's like all your favorite restaurant apps rolled into one, and you will love the QuickChat feature!  Just click the link below or go to the app store and search for QuickTable (all one word).\n" +
                "\n" +
                "Have a great day!\n\n" + "https://play.google.com/store/apps/details?id=com.app.mobi.quicktabledemo&hl=en");

        startActivity(Intent.createChooser(share, "Share link!"));
    }

    private void getYelpKeys() {

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("");
        dialog.show();

        String url = Config.YELP_KEYS_URL;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("YelpKeyResponse", response.toString());

                        dialog.dismiss();

                        try {

                            Config.YELP_CONSUMER_KEY = response.getString("consumer_key");
                            Config.YELP_CONSUMER_SECRET = response.getString("consumer_secret");
                            Config.YELP_TOKEN = response.getString("token");
                            Config.YELP_TOKEN_SECRET = response.getString("token_secret");

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


}
