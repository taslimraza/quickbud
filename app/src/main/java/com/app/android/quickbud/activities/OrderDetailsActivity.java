package com.app.android.quickbud.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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
import com.app.android.quickbud.adapters.ItemListAdapter;
import com.app.android.quickbud.fragments.MenuItemFragment;
import com.app.android.quickbud.fragments.OrderFavoriteFragment;
import com.app.android.quickbud.modelClasses.CartItemModel;
import com.app.android.quickbud.modelClasses.MenuChoicesModel;
import com.app.android.quickbud.network.VolleySingleton;
import com.app.android.quickbud.utils.CartSingleton;
import com.app.android.quickbud.utils.Config;
import com.app.android.quickbud.utils.ConnectionDetector;
import com.app.android.quickbud.utils.GPSTracker;
import com.app.android.quickbud.utils.Globals;
import com.app.android.quickbud.utils.SpecificMenuSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class OrderDetailsActivity extends AppCompatActivity {

    Toolbar toolbar;
    Button checkOut, favoriteItem;
    TextView userName, userLocation, clearCart, restaurantName, restaurantAddress;
    EditText couponCode, specialRequest;
    private static TextView itemTotalQuantity, itemTotalCost, billTotalCost, grandTotalCost;
    DrawerLayout drawerLayout;
    ListView drawerList, itemList;
    ImageView homeBtn, favoriteOrder, loadingImage, userImage;
    RelativeLayout cartContent;
    CartItemModel cartItemModel;
    ArrayList<CartItemModel> cartItemModels;
    RelativeLayout orderDetailsLayout;
    RelativeLayout emptyCartLayout;
    ArrayList<String> menuOptionsName;
    Map<String, ArrayList<MenuChoicesModel>> menuChoices;
    private ItemListAdapter itemListAdapter;
    private static CartSingleton cartSingleton;
    private boolean isFavoriteOrder = false;
    private int orderId;
    private ConnectionDetector connectionDetector;
    private boolean isPickUpChecked = false;
    private boolean isDeliveryChecked = false;
    private boolean shouldCheckForPermission = true;
    private GPSTracker gpsTracker;
    private double latitude;
    private double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modified_order);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
//        favoriteItem = (Button) findViewById(R.id.add_more_items);
        checkOut = (Button) findViewById(R.id.checkout);
//        drawerList = (ListView) findViewById(R.id.drawer_list);
//        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        homeBtn = (ImageView) findViewById(R.id.menu_button);
        favoriteOrder = (ImageView) findViewById(R.id.order_favorite);
//        userName = (TextView) findViewById(R.id.user_name);
        userLocation = (TextView) findViewById(R.id.user_location);
        cartContent = (RelativeLayout) findViewById(R.id.cart_content);
        itemList = (ListView) findViewById(R.id.cart_list);
        orderDetailsLayout = (RelativeLayout) findViewById(R.id.order_details);
        emptyCartLayout = (RelativeLayout) findViewById(R.id.empty_cart);
        clearCart = (TextView) findViewById(R.id.clear_cart);
        loadingImage = (ImageView) findViewById(R.id.loading_image);
        itemTotalQuantity = (TextView) findViewById(R.id.item_total_quantity);
        itemTotalCost = (TextView) findViewById(R.id.item_total_price);
//        billTotalCost = (TextView) findViewById(R.id.bill_total_cost);
        grandTotalCost = (TextView) findViewById(R.id.grand_total_cost);
        restaurantName = (TextView) findViewById(R.id.restaurant_name);
        restaurantAddress = (TextView) findViewById(R.id.restaurant_address);
//        userImage = (ImageView) findViewById(R.id.user_image);
        couponCode = (EditText) findViewById(R.id.coupon_code);
        specialRequest = (EditText) findViewById(R.id.special_description);
        RelativeLayout pickUpLayout = (RelativeLayout) findViewById(R.id.pick_up_layout);
        RelativeLayout deliveryLayout = (RelativeLayout) findViewById(R.id.delivery_layout);
        final CheckBox pickUpCheckBox = (CheckBox) findViewById(R.id.pick_up_order);
        final CheckBox deliveryCheckBox = (CheckBox) findViewById(R.id.delivery_order);

        couponCode.setFilters(new InputFilter[] {new InputFilter.AllCaps()});

        // for android v6.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            checkOut.setBackgroundColor(ContextCompat.getColor(this, R.color.primary_color));
            toolbar.setBackgroundResource(R.mipmap.toolbar_image);
        }

        setSupportActionBar(toolbar);
        ActionBar mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setTitle("Order Details");
            mActionBar.setDisplayHomeAsUpEnabled(true);
        }
        connectionDetector = new ConnectionDetector(this);

        SpecificMenuSingleton menuSingleton = SpecificMenuSingleton.getInstance();
        restaurantName.setText(menuSingleton.getClickedRestaurant().getRestaurantName());
        StringBuilder builder = new StringBuilder();
        builder.append(menuSingleton.getClickedRestaurant().getRestaurantAddress());
        builder.append(", " + menuSingleton.getClickedRestaurant().getRestaurantCity());
        builder.append(", " + menuSingleton.getClickedRestaurant().getRestaurantState());
        restaurantAddress.setText(builder.toString());

        cartContent.setVisibility(View.GONE);
        clearCart.setVisibility(View.VISIBLE);

//        drawerList.setAdapter(new DrawerListAdapter(this));

        cartItemModel = new CartItemModel();
        cartSingleton = CartSingleton.getInstance();
        cartItemModels = cartSingleton.getCartItem();
        itemTotalQuantity.setText(cartSingleton.getTotalItemQuantity().toString());
        String cost = String.format("%.2f", cartSingleton.getItemTotalCost());
//        itemTotalCost.setText("$ " + cost);
//        billTotalCost.setText("$ " + cost);
        grandTotalCost.setText("$ " + cost);

        if (cartItemModels.size() > 0) {
            itemListAdapter = new ItemListAdapter(this, cartItemModels, orderDetailsLayout, emptyCartLayout);
            itemList.setAdapter(itemListAdapter);
        } else {
            orderDetailsLayout.setVisibility(View.GONE);
            emptyCartLayout.setVisibility(View.VISIBLE);
            clearCart.setVisibility(View.GONE);
            checkOut.setVisibility(View.GONE);
        }

        registerForContextMenu(itemList);

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
//                drawerLayout.openDrawer(Gravity.LEFT);

                Intent intent = new Intent(OrderDetailsActivity.this, RegistrationActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();

            }
        });

//        favoriteItem.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                postCartItems(1);
//            }
//        });

        checkOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (connectionDetector.isConnectedToInternet()) {
                    if (SpecificMenuSingleton.getInstance().getClickedRestaurant().getRestaurantAccountName().equalsIgnoreCase("Premium")){
                        if (isPickUpChecked || isDeliveryChecked){
                            Location currentLocation = new Location("currentLocation");
                            Location dispensaryLocation = new Location("dispensaryLocation");
                            currentLocation.setLatitude(latitude);
                            currentLocation.setLongitude(longitude);
                            dispensaryLocation.setLatitude(SpecificMenuSingleton.getInstance().getClickedRestaurant().getLatitude());
                            dispensaryLocation.setLongitude(SpecificMenuSingleton.getInstance().getClickedRestaurant().getLongitude());

                            if (currentLocation.distanceTo(dispensaryLocation) < 80470){
                                showAlert();
                            }else {
                                Toast.makeText(OrderDetailsActivity.this, "User needs to be within 50 miles to access this features!", Toast.LENGTH_SHORT).show();
                            }

                        }else {
                            Toast.makeText(OrderDetailsActivity.this, "Please select Pickup/Delivery services!", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(OrderDetailsActivity.this, "This dispensary does not support this feature yet!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    connectionDetector.internetError();
                }
            }
        });

        clearCart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    AlertDialog.Builder builder = new AlertDialog.Builder(OrderDetailsActivity.this);
                    builder.setTitle("Alert");
                    builder.setMessage("Are you sure you want to clear the cart ?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            cartItemModels.clear();
                            MenuItemFragment.updateCartCount();
                            orderDetailsLayout.setVisibility(View.GONE);
                            emptyCartLayout.setVisibility(View.VISIBLE);
                            clearCart.setVisibility(View.GONE);
                            checkOut.setVisibility(View.GONE);
                            dialog.dismiss();
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                }


            }


        });

        if (!SpecificMenuSingleton.getInstance().getClickedRestaurant().isPickUpAvailable()){
            pickUpLayout.setVisibility(View.GONE);
            deliveryCheckBox.setChecked(true);
            isDeliveryChecked = true;
            isPickUpChecked = false;
        }

        if (!SpecificMenuSingleton.getInstance().getClickedRestaurant().isDeliveryAvailable()){
            deliveryLayout.setVisibility(View.GONE);
            pickUpCheckBox.setChecked(true);
            isDeliveryChecked = false;
            isPickUpChecked = true;
        }

        pickUpLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPickUpChecked){
                    pickUpCheckBox.setChecked(false);
                    isPickUpChecked = false;
                }else {
                    pickUpCheckBox.setChecked(true);
                    isPickUpChecked = true;
                }
                deliveryCheckBox.setChecked(false);
                isDeliveryChecked = false;
            }
        });

        deliveryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickUpCheckBox.setChecked(false);
                isPickUpChecked = false;
                if (isDeliveryChecked){
                    deliveryCheckBox.setChecked(false);
                    isDeliveryChecked = false;
                }else {
                    deliveryCheckBox.setChecked(true);
                    isDeliveryChecked = true;
                }
            }
        });

        pickUpCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPickUpChecked) {
                    pickUpCheckBox.setChecked(false);
                    isPickUpChecked = false;
                }else {
                    pickUpCheckBox.setChecked(true);
                    isPickUpChecked = true;
                }

                deliveryCheckBox.setChecked(false);
                isDeliveryChecked = false;
            }
        });

        deliveryCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isDeliveryChecked) {
                    deliveryCheckBox.setChecked(false);
                    isDeliveryChecked = false;
                }else{
                    deliveryCheckBox.setChecked(true);
                    isDeliveryChecked = true;
                }

                pickUpCheckBox.setChecked(false);
                isPickUpChecked = false;
            }
        });

//         FOR USER PROFILE OPTIONS
//        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                switch (position) {
//                    case 1:
//                        // User Profile
//                        startActivity(new Intent(OrderDetailsActivity.this, UserProfileActivity.class));
//                        drawerLayout.closeDrawers();
//                        break;
//                    case 2:
//                        // User Favorites Orders List
//                        startActivity(new Intent(OrderDetailsActivity.this, OrderHistoryActivity.class));
//                        drawerLayout.closeDrawers();
//                        break;
//                    case 3:
//                        // Order Booking Status - Show user estimated wait time
////                        Config.bookingStatus = false;
//                        startActivity(new Intent(OrderDetailsActivity.this, OrderConfirmationActivity.class));
//                        drawerLayout.closeDrawers();
//                        break;
//                    case 4:
//                        shareApp();
//                        break;
//                    case 5:
//                        startActivity(new Intent(OrderDetailsActivity.this, ChatImages.class));
//                        drawerLayout.closeDrawers();
//                        break;
//                    case 6:
//                        startActivity(new Intent(OrderDetailsActivity.this, AboutUsActivity.class));
//                        drawerLayout.closeDrawers();
//                        break;
//                }
//            }
//        });
    }

    @Override
    protected void onResume() {
        super.onResume();
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

    private void setUpLocationData() {
        gpsTracker = new GPSTracker(this);
        gpsTracker.startLocation();

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_modified_order, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
                onBackPressed();
        }
        return true;
    }

    private void initializeData(int i) {
        menuOptionsName = new ArrayList<>();
        menuChoices = cartItemModels.get(i).getMenuChoicesHashMap();
        Set<String> menuOptions = menuChoices.keySet();
        for (String name : menuOptions) {
            menuOptionsName.add(name);
        }
    }

    private JSONObject cartPostData(int isFavorite) {
        SpecificMenuSingleton menuSingleton = SpecificMenuSingleton.getInstance();
        final SharedPreferences sharedPreferences = getSharedPreferences("user_info", Context.MODE_PRIVATE);
        String patronId = sharedPreferences.getString("patron_id", null);

        JSONObject cartItems = new JSONObject();
        try {
            JSONArray itemsDetailsArray = new JSONArray();
            for (int i = 0; i < cartItemModels.size(); i++) {
                JSONObject itemDetail = new JSONObject();
                itemDetail.put("menu_item_id", cartItemModels.get(i).getItemId());
                itemDetail.put("quantity", cartItemModels.get(i).getItemQuantity());
                itemDetail.put("special_request", cartItemModels.get(i).getSpecialRequest());

                if (cartItemModels.get(i).getMenuChoicesHashMap() != null) {  // if menu options are there
                    initializeData(i);
                    JSONArray itemOptions = new JSONArray();
                    for (int j = 0; j < cartItemModels.get(i).getMenuChoicesHashMap().size(); j++) {

                        for (int k = 0; k < cartItemModels.get(i).getMenuChoicesHashMap().get(menuOptionsName.get(j)).size(); k++) {
                            JSONObject itemOption = new JSONObject();
                            JSONArray itemChoices = new JSONArray();
                            if (cartItemModels.get(i).getMenuChoicesHashMap().get(menuOptionsName.get(j)).get(k).isCheckBoxChecked()
                                    || cartItemModels.get(i).getMenuChoicesHashMap().get(menuOptionsName.get(j)).get(k).isRadioButtonChecked()) {
                                JSONObject itemChoice = new JSONObject();
                                itemChoice.put("menu_choice_id", cartItemModels.get(i).getMenuChoicesHashMap().get(menuOptionsName.get(j)).get(k).getMenuChoiceId());
                                itemChoice.put("quantity", 1);
                                itemChoice.put("action", "A");
//                                menuOptionId = cartItemModels.get(i).getMenuChoicesHashMap().get(menuOptionsName.get(j)).get(k).getMenuOptionId();
                                itemOption.put("menu_option_id", cartItemModels.get(i).getMenuChoicesHashMap().get(menuOptionsName.get(j)).get(k).getMenuOptionId());
                                itemChoices.put(itemChoice);
                            } else {
                                JSONObject itemChoice = new JSONObject();
                                itemChoice.put("menu_choice_id", cartItemModels.get(i).getMenuChoicesHashMap().get(menuOptionsName.get(j)).get(k).getMenuChoiceId());
                                itemChoice.put("quantity", 1);
                                itemChoice.put("action", "R");
//                                menuOptionId = cartItemModels.get(i).getMenuChoicesHashMap().get(menuOptionsName.get(j)).get(k).getMenuOptionId();
                                itemOption.put("menu_option_id", cartItemModels.get(i).getMenuChoicesHashMap().get(menuOptionsName.get(j)).get(k).getMenuOptionId());
                                itemChoices.put(itemChoice);
                            }
                            itemOption.put("menu_choice", itemChoices);
                            itemOptions.put(itemOption);
                        }
//                        itemOption.put("menu_option_id", cartItemModels.get(i).getMenuOptionModels().get(j).getMenuOptionId());
                    }
                    itemDetail.put("menu_option", itemOptions);
                }
                itemsDetailsArray.put(itemDetail);
            }
            cartItems.put("is_favorite", isFavorite);
            cartItems.put("cart_items", itemsDetailsArray);
            cartItems.put("location_id", menuSingleton.getLocationId());
            cartItems.put("tenant_id", menuSingleton.getTenantId());
            cartItems.put("visit_id", menuSingleton.getVisitId());
            cartItems.put("patron_id", patronId);
        } catch (JSONException e) {

        }
        return cartItems;
    }

    private JSONObject cartPost(int isFavorite, int addressId) {
        SpecificMenuSingleton menuSingleton = SpecificMenuSingleton.getInstance();
        final SharedPreferences sharedPreferences = getSharedPreferences("user_info", Context.MODE_PRIVATE);
        String patronId = sharedPreferences.getString("patron_id", null);

        JSONObject cartItems = new JSONObject();
        try {
            JSONArray itemsDetailsArray = new JSONArray();
            for (int i = 0; i < cartItemModels.size(); i++) {
                JSONObject itemDetail = new JSONObject();
                itemDetail.put("menu_item_id", cartItemModels.get(i).getItemId());
                itemDetail.put("quantity", cartItemModels.get(i).getItemQuantity());
                itemDetail.put("special_request", cartItemModels.get(i).getSpecialRequest());

                if (cartItemModels.get(i).getMenuChoicesHashMap() != null) {  // if menu options are there
                    initializeData(i);
                    JSONArray itemOptions = new JSONArray();
                    for (int j = 0; j < cartItemModels.get(i).getMenuChoicesHashMap().size(); j++) {

                        for (int k = 0; k < cartItemModels.get(i).getMenuChoicesHashMap().get(menuOptionsName.get(j)).size(); k++) {
                            JSONObject itemOption = new JSONObject();
                            JSONArray itemChoices = new JSONArray();

                            if (cartItemModels.get(i).getMenuOptionModels().get(j).getIsMultiQuantity() == 1) {
                                // if multi quantity enabled

                                if (cartItemModels.get(i).getMenuChoicesHashMap().get(menuOptionsName.get(j)).get(k).isChoiceDefault() > 0) {

                                    if (cartItemModels.get(i).getMenuChoicesHashMap().get(menuOptionsName.get(j)).get(k).getMenuChoiceQuantity() >
                                            cartItemModels.get(i).getMenuChoicesHashMap().get(menuOptionsName.get(j)).get(k).isChoiceDefault()) {
                                        JSONObject itemChoice = new JSONObject();
                                        itemChoice.put("menu_choice_id", cartItemModels.get(i).getMenuChoicesHashMap().get(menuOptionsName.get(j)).get(k).getMenuChoiceId());
//                                    itemChoice.put("quantity", 1);
                                        itemChoice.put("quantity", cartItemModels.get(i).getMenuChoicesHashMap().get(menuOptionsName.get(j)).get(k).getMenuChoiceQuantity());
                                        itemChoice.put("action", "A");
//                                menuOptionId = cartItemModels.get(i).getMenuChoicesHashMap().get(menuOptionsName.get(j)).get(k).getMenuOptionId();
                                        itemOption.put("menu_option_id", cartItemModels.get(i).getMenuChoicesHashMap().get(menuOptionsName.get(j)).get(k).getMenuOptionId());
                                        itemChoices.put(itemChoice);
                                    }else if (cartItemModels.get(i).getMenuChoicesHashMap().get(menuOptionsName.get(j)).get(k).getMenuChoiceQuantity() == 0){
                                        if (cartItemModels.get(i).getMenuOptionModels().get(j).isMultiSelect() == 1){
                                            JSONObject itemChoice = new JSONObject();
                                            itemChoice.put("menu_choice_id", cartItemModels.get(i).getMenuChoicesHashMap().get(menuOptionsName.get(j)).get(k).getMenuChoiceId());
                                            itemChoice.put("quantity", 1);
                                            itemChoice.put("action", "R");
                                            itemOption.put("menu_option_id", cartItemModels.get(i).getMenuChoicesHashMap().get(menuOptionsName.get(j)).get(k).getMenuOptionId());
                                            itemChoices.put(itemChoice);
                                        }
                                    }

                                }else {
                                    if (cartItemModels.get(i).getMenuChoicesHashMap().get(menuOptionsName.get(j)).get(k).getMenuChoiceQuantity() > 0) {
                                        JSONObject itemChoice = new JSONObject();
                                        itemChoice.put("menu_choice_id", cartItemModels.get(i).getMenuChoicesHashMap().get(menuOptionsName.get(j)).get(k).getMenuChoiceId());
//                                    itemChoice.put("quantity", 1);
                                        itemChoice.put("quantity", cartItemModels.get(i).getMenuChoicesHashMap().get(menuOptionsName.get(j)).get(k).getMenuChoiceQuantity());
                                        itemChoice.put("action", "A");
//                                menuOptionId = cartItemModels.get(i).getMenuChoicesHashMap().get(menuOptionsName.get(j)).get(k).getMenuOptionId();
                                        itemOption.put("menu_option_id", cartItemModels.get(i).getMenuChoicesHashMap().get(menuOptionsName.get(j)).get(k).getMenuOptionId());
                                        itemChoices.put(itemChoice);
                                    }
                                }

//                                if (cartItemModels.get(i).getMenuChoicesHashMap().get(menuOptionsName.get(j)).get(k).getMenuChoiceQuantity() > 0) {
//                                    JSONObject itemChoice = new JSONObject();
//                                    itemChoice.put("menu_choice_id", cartItemModels.get(i).getMenuChoicesHashMap().get(menuOptionsName.get(j)).get(k).getMenuChoiceId());
////                                    itemChoice.put("quantity", 1);
//                                    itemChoice.put("quantity", cartItemModels.get(i).getMenuChoicesHashMap().get(menuOptionsName.get(j)).get(k).getMenuChoiceQuantity());
//                                    itemChoice.put("action", "A");
////                                menuOptionId = cartItemModels.get(i).getMenuChoicesHashMap().get(menuOptionsName.get(j)).get(k).getMenuOptionId();
//                                    itemOption.put("menu_option_id", cartItemModels.get(i).getMenuChoicesHashMap().get(menuOptionsName.get(j)).get(k).getMenuOptionId());
//                                    itemChoices.put(itemChoice);
//                                }else {
//                                    if (cartItemModels.get(i).getMenuChoicesHashMap().get(menuOptionsName.get(j)).get(k).isChoiceDefault() == 1) {
//                                        if (cartItemModels.get(i).getMenuOptionModels().get(j).isMultiSelect() == 1){
//                                            JSONObject itemChoice = new JSONObject();
//                                            itemChoice.put("menu_choice_id", cartItemModels.get(i).getMenuChoicesHashMap().get(menuOptionsName.get(j)).get(k).getMenuChoiceId());
//                                            itemChoice.put("quantity", 1);
//                                            itemChoice.put("action", "R");
//                                            itemOption.put("menu_option_id", cartItemModels.get(i).getMenuChoicesHashMap().get(menuOptionsName.get(j)).get(k).getMenuOptionId());
//                                            itemChoices.put(itemChoice);
//                                        }
//                                    }
//                                }

                            }else {
                                // if multi quantity disabled
                                if (cartItemModels.get(i).getMenuChoicesHashMap().get(menuOptionsName.get(j)).get(k).isChoiceDefault() == 1) {
                                    if (cartItemModels.get(i).getMenuChoicesHashMap().get(menuOptionsName.get(j)).get(k).isDefault() != 1) {
                                        if (cartItemModels.get(i).getMenuOptionModels().get(j).isMultiSelect() == 1){
                                            JSONObject itemChoice = new JSONObject();
                                            itemChoice.put("menu_choice_id", cartItemModels.get(i).getMenuChoicesHashMap().get(menuOptionsName.get(j)).get(k).getMenuChoiceId());
                                            itemChoice.put("quantity", 1);
                                            itemChoice.put("action", "R");
//                                menuOptionId = cartItemModels.get(i).getMenuChoicesHashMap().get(menuOptionsName.get(j)).get(k).getMenuOptionId();
                                            itemOption.put("menu_option_id", cartItemModels.get(i).getMenuChoicesHashMap().get(menuOptionsName.get(j)).get(k).getMenuOptionId());
                                            itemChoices.put(itemChoice);
                                        }
                                    }
                                    if (cartItemModels.get(i).getMenuChoicesHashMap().get(menuOptionsName.get(j)).get(k).isCheckBoxChecked() ||
                                            cartItemModels.get(i).getMenuChoicesHashMap().get(menuOptionsName.get(j)).get(k).isRadioButtonChecked()){
                                        JSONObject itemChoice = new JSONObject();
                                        itemChoice.put("menu_choice_id", cartItemModels.get(i).getMenuChoicesHashMap().get(menuOptionsName.get(j)).get(k).getMenuChoiceId());
                                        itemChoice.put("quantity", 1);
                                        itemChoice.put("action", "A");
//                                menuOptionId = cartItemModels.get(i).getMenuChoicesHashMap().get(menuOptionsName.get(j)).get(k).getMenuOptionId();
                                        itemOption.put("menu_option_id", cartItemModels.get(i).getMenuChoicesHashMap().get(menuOptionsName.get(j)).get(k).getMenuOptionId());
                                        itemChoices.put(itemChoice);
                                    }
                                } else {
                                    if (cartItemModels.get(i).getMenuChoicesHashMap().get(menuOptionsName.get(j)).get(k).isDefault() == 1) {
                                        JSONObject itemChoice = new JSONObject();
                                        itemChoice.put("menu_choice_id", cartItemModels.get(i).getMenuChoicesHashMap().get(menuOptionsName.get(j)).get(k).getMenuChoiceId());
//                                    itemChoice.put("quantity", 1);
                                        itemChoice.put("quantity", cartItemModels.get(i).getMenuChoicesHashMap().get(menuOptionsName.get(j)).get(k).getMenuChoiceQuantity());
                                        itemChoice.put("action", "A");
//                                menuOptionId = cartItemModels.get(i).getMenuChoicesHashMap().get(menuOptionsName.get(j)).get(k).getMenuOptionId();
                                        itemOption.put("menu_option_id", cartItemModels.get(i).getMenuChoicesHashMap().get(menuOptionsName.get(j)).get(k).getMenuOptionId());
                                        itemChoices.put(itemChoice);
                                    }
                                }
                            }


                            itemOption.put("menu_choice", itemChoices);
                            itemOptions.put(itemOption);
                        }
//                        itemOption.put("menu_option_id", cartItemModels.get(i).getMenuOptionModels().get(j).getMenuOptionId());
                    }
                    itemDetail.put("menu_option", itemOptions);
                }
                itemsDetailsArray.put(itemDetail);
            }
            cartItems.put("is_favorite", isFavorite);
            cartItems.put("cart_items", itemsDetailsArray);
            cartItems.put("location_id", menuSingleton.getClickedRestaurant().getLocationID());
            cartItems.put("tenant_id", menuSingleton.getClickedRestaurant().getTenantID());
            cartItems.put("visit_id", menuSingleton.getVisitId());
            cartItems.put("patron_id", patronId);
            cartItems.put("coupon_code", couponCode.getText().toString());
            cartItems.put("special_request", specialRequest.getText().toString());
            cartItems.put("address_id", addressId);
            if (isPickUpChecked) cartItems.put("pickup", true);
            if (isDeliveryChecked) cartItems.put("delivery", true);
        } catch (JSONException e) {

        }
        Log.i("Order Details", cartItems.toString());
        return cartItems;
    }

    private void postCartItems(final int isFavorite, int addressId) {
        orderDetailsLayout.setVisibility(View.GONE);
        loadingImage.setVisibility(View.VISIBLE);
        checkOut.setVisibility(View.GONE);

        final String url = Config.QT_POST_CART_URL;

        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, cartPost(isFavorite, addressId),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("Verification", response.toString());

                        orderDetailsLayout.setVisibility(View.VISIBLE);
                        loadingImage.setVisibility(View.GONE);
                        checkOut.setVisibility(View.VISIBLE);

                        try {
                            // Getting Order Id and setting it in singleton object

                            if (response.has("error_msg")){
                                Toast.makeText(OrderDetailsActivity.this, R.string.hostess_offline_msg, Toast.LENGTH_SHORT).show();
                                return;
                            }

                            orderId = response.getInt("order_id");
                            if (orderId != 0) {
                                cartItemModels.clear();
                                MenuItemFragment.updateCartCount();
                                SharedPreferences preferences = getSharedPreferences("ewt_info", Context.MODE_PRIVATE);
                                if (preferences.getBoolean("isDineInOnly", false)){
                                    // if pre-order
                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putBoolean("isDineInOnly", false);
                                    editor.putBoolean("isTakeAway", false);
                                    editor.commit();
                                }else {
                                    // else carry-out
                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putBoolean("isDineInOnly", false);
                                    editor.putBoolean("isTakeAway", true);
                                    editor.commit();
                                }
                                startActivity(new Intent(OrderDetailsActivity.this, OrderConfirmationActivity.class));
                                finish();
                            }
                        } catch (JSONException e) {

                        }
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                orderDetailsLayout.setVisibility(View.VISIBLE);
                loadingImage.setVisibility(View.GONE);
                checkOut.setVisibility(View.VISIBLE);
                if (error != null) {
                    if (error instanceof TimeoutError) {
                        Config.internetSlowError(OrderDetailsActivity.this);
                    } else if (error instanceof NoConnectionError) {
                        Config.internetSlowError(OrderDetailsActivity.this);
                    } else if (error instanceof ServerError) {
                        Config.serverError(OrderDetailsActivity.this);
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

    private void showAlert() {
        final SpecificMenuSingleton menuSingleton = SpecificMenuSingleton.getInstance();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Order Verification");
        builder.setMessage("Once you place your order it will be sent directly to the dispensary and no further changes may be made. Are you ready to place your order?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Config.bookingStatus = true;
                getUserAddress();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

//        SharedPreferences preferences = getSharedPreferences("ewt_info", MODE_PRIVATE);
//        if (!preferences.getBoolean("isDineInOnly", false)) {
//
//            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    Config.bookingStatus = true;
//                    takeAwayPost(0, addressId);
//                    dialog.dismiss();
//                }
//            });
//            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.dismiss();
//                }
//            });
//        } else {
//            builder.setMessage("Your order will be placed once you are seated");
//            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    postCartItems(0, addressId);
//                    dialog.dismiss();
//                }
//            });
//        }
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static void updateData() {
        Integer updatedCount = cartSingleton.getItemCount();
        itemTotalQuantity.setText(updatedCount.toString());
        String cost = String.format("%.2f", cartSingleton.getCalculatedTotalCost());
//        itemTotalCost.setText("$ " + cost);
//        billTotalCost.setText("$ " + cost);
        grandTotalCost.setText("$ " + cost);
    }

    private void showServerError(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alert");
        builder.setMessage("Oops! some server issue is there, please try it later!");
        builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent(OrderDetailsActivity.this, MenuActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void takeAwayPost(final int isFavorite, final int addressId) {
        orderDetailsLayout.setVisibility(View.GONE);
        loadingImage.setVisibility(View.VISIBLE);
        checkOut.setVisibility(View.GONE);
        final String url = Config.QT_VISIT_URL;
        final SharedPreferences sharedPreferences = getSharedPreferences("user_info", Context.MODE_PRIVATE);
        String patronId = sharedPreferences.getString("patron_id", null);
        SpecificMenuSingleton specificMenuSingleton = SpecificMenuSingleton.getInstance();
        JSONObject params = new JSONObject();
        try {
            params.put("tenant_id", specificMenuSingleton.getClickedRestaurant().getTenantID());
            params.put("location_id", specificMenuSingleton.getClickedRestaurant().getLocationID());
            params.put("visit_type", "T");
            params.put("patron_id", patronId);

            if (Config.APP_NAME.equalsIgnoreCase("QuickTable")){
                params.put("from_qt", true);
            }else {
                params.put("from_qt", false);
            }

        } catch (JSONException e) {
        }

        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("Verification", response.toString());
                        SpecificMenuSingleton menuSingleton = SpecificMenuSingleton.getInstance();
                        try {
                            if (response.has("error_msg")){
                                orderDetailsLayout.setVisibility(View.VISIBLE);
                                loadingImage.setVisibility(View.GONE);
                                checkOut.setVisibility(View.VISIBLE);
                                Toast.makeText(OrderDetailsActivity.this, R.string.hostess_offline_msg, Toast.LENGTH_SHORT).show();
                                return;
                            }else {
                                menuSingleton.setVisitId(response.getInt("id"));
                                menuSingleton.setEwt(response.getString("ewt"));
                                menuSingleton.setPosition(response.getInt("position"));

                                postCartItems(isFavorite, addressId);
                            }
                        } catch (JSONException e) {

                        }
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                orderDetailsLayout.setVisibility(View.VISIBLE);
                loadingImage.setVisibility(View.GONE);
                checkOut.setVisibility(View.VISIBLE);
                if (error != null) {
                    if (error instanceof TimeoutError) {
                        Config.internetSlowError(OrderDetailsActivity.this);
                    } else if (error instanceof NoConnectionError) {
                        Config.internetSlowError(OrderDetailsActivity.this);
                    } else if (error instanceof ServerError) {
                        Config.serverError(OrderDetailsActivity.this);
                    }
                }

            }
        }){
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
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.cart_list) {
            MenuInflater menuInflater = getMenuInflater();
            menuInflater.inflate(R.menu.menu_selected_category, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position;
        switch (item.getItemId()) {
            case R.id.remove_menu_item:
//                cartItemModels.remove(position);
//                itemListAdapter.notifyDataSetChanged();
                CartSingleton singleton = CartSingleton.getInstance();
                singleton.removeFromCart(position);
                OrderDetailsActivity.updateData();
                MenuItemFragment.updateCartCount();
                OrderFavoriteFragment.update();
                if (singleton.getItemCount() > 0) {
                    itemListAdapter = new ItemListAdapter(this, cartItemModels, orderDetailsLayout, emptyCartLayout);
                    itemList.setAdapter(itemListAdapter);
                } else {
                    orderDetailsLayout.setVisibility(View.GONE);
                    emptyCartLayout.setVisibility(View.VISIBLE);
                    clearCart.setVisibility(View.GONE);
                    checkOut.setVisibility(View.GONE);
                }
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void getUserAddress() {

        orderDetailsLayout.setVisibility(View.GONE);
        loadingImage.setVisibility(View.VISIBLE);
        checkOut.setVisibility(View.GONE);

        SharedPreferences sharedPreferences = getSharedPreferences("user_info", MODE_PRIVATE);
        String patronId = sharedPreferences.getString("patron_id", null);

        String url = Config.QT_PATRON_UPDATE + patronId;


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("UserProfile", response.toString());

                        try {
                            JSONArray addressArray = response.getJSONArray("address");
                            if (addressArray != null && addressArray.length()>0){
                                JSONObject addressObj = addressArray.getJSONObject(0);
                                int addressId = addressObj.getInt("id");
                                takeAwayPost(0, addressId);
                            }else {
                                orderDetailsLayout.setVisibility(View.VISIBLE);
                                loadingImage.setVisibility(View.GONE);
                                checkOut.setVisibility(View.VISIBLE);
                                startActivity(new Intent(OrderDetailsActivity.this, UserProfileActivity.class));
//                                Toast.makeText(OrderDetailsActivity.this, "Please enter your address before proceeding further!", Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error != null){
                    if (error instanceof TimeoutError) {
                        Config.internetSlowError(OrderDetailsActivity.this);
                    } else if (error instanceof NoConnectionError) {
                        Config.internetSlowError(OrderDetailsActivity.this);
                    } else if (error instanceof ServerError) {
                        Config.serverError(OrderDetailsActivity.this);
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

    private void shareApp() {
        final SharedPreferences sharedPreferences = getSharedPreferences("user_info", Context.MODE_PRIVATE);
        String userName = sharedPreferences.getString("user_name", null);
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

        // Add data to the intent, the receiving app will decide
        // what to do with it.
//        share.putExtra(Intent.EXTRA_SUBJECT, "QuickTable sharing!");
        share.putExtra(Intent.EXTRA_TEXT, "Hey ! " + userName.toUpperCase() + " has personally invited you to download the new QuickTable app…it's like all your favorite restaurant apps rolled into one, and you will love the QuickChat feature!  Just click the link below or go to the app store and search for QuickTable (all one word).\n" +
                "\n" +
                "Have a great day!\n\n" + "https://play.google.com/store/apps/details?id=com.app.mobi.quicktabledemo&hl=en");

        startActivity(Intent.createChooser(share, "Share link!"));
    }
}
