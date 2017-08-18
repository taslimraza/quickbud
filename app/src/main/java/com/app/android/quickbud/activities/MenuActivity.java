package com.app.android.quickbud.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.android.quickbud.R;
import com.app.android.quickbud.fragments.MenuSectionFragment;
import com.app.android.quickbud.utils.Config;
import com.app.android.quickbud.utils.InternetConnectionError;
import com.app.android.quickbud.utils.SpecificMenuSingleton;
import com.bumptech.glide.Glide;

public class MenuActivity extends AppCompatActivity {

    TextView userName, userLocation;
    DrawerLayout drawerLayout;
    ListView drawerList;
    ImageView homeBtn, userImage, loadingImage, menuImageView;
    RelativeLayout cartContent;
    private int locationId, tenantId;
    private InternetConnectionError internetConnectionError;
    private FrameLayout menuScreen;
    private WebView menuWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
//        drawerList = (ListView) findViewById(R.id.drawer_list);
//        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
//        userName = (TextView) findViewById(R.id.user_name);
        userLocation = (TextView) findViewById(R.id.user_location);
        cartContent = (RelativeLayout) findViewById(R.id.cart_content);
//        userImage = (ImageView) findViewById(R.id.user_image);
        menuScreen = (FrameLayout) findViewById(R.id.menu_container);
        loadingImage = (ImageView) findViewById(R.id.loading_image);
        menuWebView = (WebView) findViewById(R.id.menu_webview);
        menuImageView = (ImageView) findViewById(R.id.menu_imageview);

//        internetConnectionError = new InternetConnectionError();

        // for android v6.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            toolbar.setBackgroundResource(R.mipmap.toolbar_image);
        }

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Menu");
        }

        Intent intent = getIntent();
        locationId = intent.getIntExtra("locationId", 0);
        tenantId = intent.getIntExtra("tenantId", 0);
        setResult(RESULT_OK, intent);
        String menuUrl = intent.getStringExtra("RestaurantMenuUrl");

//        drawerList.setAdapter(new DrawerListAdapter(this));

        homeBtn = (ImageView) findViewById(R.id.menu_button);

//        SharedPreferences sharedPreferences = getSharedPreferences("ewt_info", Context.MODE_PRIVATE);
//        boolean isDineInOnly = sharedPreferences.getBoolean("isDineInOnly", false);
//        boolean isTakeAway = sharedPreferences.getBoolean("isTakeAway", false);
//        boolean isPatronSeated = sharedPreferences.getBoolean("is_patron_seated", false);
//
//        if (isDineInOnly || isTakeAway || isPatronSeated) {
//            homeBtn.setVisibility(View.INVISIBLE);
//        }

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                drawerLayout.openDrawer(Gravity.LEFT);

                Intent intent = new Intent(MenuActivity.this, RegistrationActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();

            }
        });

        if (Config.viewMenu) {
            cartContent.setVisibility(View.GONE);
        } else {
            cartContent.setVisibility(View.VISIBLE);
        }
        cartContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuActivity.this, OrderDetailsActivity.class));
            }
        });


//         FOR USER PROFILE OPTIONS
//        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                switch (position) {
//                    case 1:
//                        // User Profile
//                        startActivity(new Intent(MenuActivity.this, UserProfileActivity.class));
//                        drawerLayout.closeDrawers();
//                        break;
//                    case 2:
//                        // User Favorites Orders List
//                        startActivity(new Intent(MenuActivity.this, OrderHistoryActivity.class));
//                        drawerLayout.closeDrawers();
//                        break;
//                    case 3:
//                        // Order Booking Status - Show user estimated wait time
//                        startActivity(new Intent(MenuActivity.this, OrderConfirmationActivity.class));
//                        drawerLayout.closeDrawers();
//                        break;
//
//                    case 4:
//                        shareApp();
//                        break;
//
//                    case 5:
//                        startActivity(new Intent(MenuActivity.this, ChatImagesActivity.class));
//                        drawerLayout.closeDrawers();
//                        break;
//
//                    case 6:
//                        startActivity(new Intent(MenuActivity.this, AboutUsActivity.class));
//                        drawerLayout.closeDrawers();
//                        break;
//                }
//            }
//        });

//        if (Config.APP_NAME.equalsIgnoreCase("QuickTable")){
        if (SpecificMenuSingleton.getInstance().getClickedRestaurant().getRestaurantAccountName().equals("SILVER") ||
                SpecificMenuSingleton.getInstance().getClickedRestaurant().getRestaurantAccountName().equals("BRONZE")) {
            if (menuUrl == null || menuUrl.equalsIgnoreCase("")){
                Toast.makeText(this,"Menu Coming Soon", Toast.LENGTH_SHORT).show();
                finish();
            }else {
                getMenuWebView(menuUrl);
            }
        } else {
            MenuSectionFragment menuSectionFragment = new MenuSectionFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.menu_container, menuSectionFragment)
                    .commit();
//            if (Config.viewMenu) {
//                if (SpecificMenuSingleton.getInstance().getClickedRestaurant().isInteractiveMenu()) {
//                    MenuSectionFragment menuSectionFragment = new MenuSectionFragment();
//                    getSupportFragmentManager().beginTransaction().add(R.id.menu_container, menuSectionFragment)
//                            .commit();
//                } else {
//                    if (menuUrl == null || menuUrl.equalsIgnoreCase("")){
//                        Toast.makeText(this,"No menu to display!", Toast.LENGTH_SHORT).show();
//                        finish();
//                    }else {
//                        getMenuWebView(menuUrl);
//                    }
//                }
//            } else {
//                MenuSectionFragment menuSectionFragment = new MenuSectionFragment();
//                getSupportFragmentManager().beginTransaction().add(R.id.menu_container, menuSectionFragment)
//                        .commit();
//            }
        }
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

//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
//// intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
//        registerReceiver(internetConnectionError, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        unregisterReceiver(internetConnectionError);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
    public void onBackPressed() {

        if (getSupportFragmentManager().getBackStackEntryCount() == 2) {
            if (Config.isMenuOptionClicked) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Alert");
                builder.setMessage("Do you really want to discard your changes?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getSupportFragmentManager().popBackStack();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                getSupportFragmentManager().popBackStack();
            }
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        } else if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    private void getMenuWebView(String menuUrl) {

        String googleDocs = "http://docs.google.com/viewer?embedded=true&url=";

        String baseUrl = Config.QUICK_CHAT_IMAGE;

//        String url = "http://www.androidexample.com/media/webview/details.html";
//        String url = "http://www.rocknes.com/Rocknes-Menu.pdf";
        String url = baseUrl + menuUrl;

        Log.i("menuUrl", googleDocs + url);

        loadingImage.setVisibility(View.VISIBLE);
        menuWebView.setVisibility(View.GONE);
        menuScreen.setVisibility(View.GONE);
        menuWebView.getSettings().setJavaScriptEnabled(true);
        menuWebView.getSettings().setBuiltInZoomControls(true);
        menuWebView.getSettings().setSupportZoom(true);
//        menuWebView.getSettings().setUseWideViewPort(true);
//        menuWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
//        menuWebView.setScrollbarFadingEnabled(false);
//        menuWebView.getSettings().setAllowFileAccess(true);

        if (menuUrl.contains(".pdf")) {
            menuWebView.loadUrl(googleDocs + url);
            menuWebView.setWebViewClient(new MenuScreen());
        } else {
            loadingImage.setVisibility(View.GONE);
            menuImageView.setVisibility(View.VISIBLE);
            Glide.with(this).load(url).placeholder(R.mipmap.default_categroies).into(menuImageView);
//            menuWebView.getSettings().setUseWideViewPort(true);
//            menuWebView.getSettings().setLoadWithOverviewMode(true);
//            menuWebView.loadUrl(url);
        }
    }

    private class MenuScreen extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            loadingImage.setVisibility(View.VISIBLE);
            menuWebView.setVisibility(View.GONE);
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            loadingImage.setVisibility(View.GONE);
            menuWebView.setVisibility(View.VISIBLE);
            super.onPageFinished(view, url);
        }
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
