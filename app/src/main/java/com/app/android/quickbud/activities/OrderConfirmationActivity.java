package com.app.android.quickbud.activities;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.multidex.MultiDex;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.app.android.quickbud.geofence.GeofenceStore;
import com.app.android.quickbud.modelClasses.LocationListModel;
import com.app.android.quickbud.network.VolleySingleton;
import com.app.android.quickbud.utils.Config;
import com.app.android.quickbud.utils.ConnectionDetector;
import com.app.android.quickbud.utils.Globals;
import com.app.android.quickbud.utils.SpecificMenuSingleton;
import com.bumptech.glide.Glide;
import com.google.android.gms.location.Geofence;
import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class OrderConfirmationActivity extends AppCompatActivity {
    Button quickChat, preOrder, getOutOfLine;
    TextView userName, userLocation, position, ewtText, partiesAheadText, firstPosition,
            restaurantName, orderInTxt, restName, restAddress;
    DrawerLayout drawerLayout;
    ListView drawerList;
    ImageView menu, callUs, restaurantImage;
    CounterClass timer;
    RelativeLayout cartContent, bookingStatus1, bookingStatus, waitingInfoLayout, loadingImageLayout, seatedLayout;
    LinearLayout ewtScreenLayout;
    TextView textViewTime, noStatus;
    int v;
    Handler handler;
    Runnable runnable;
    ConnectionDetector connectionDetector;
    public static String currentTime;
    private Geofence geofence;
    private static GeofenceStore geofenceStore;
    private boolean isGetOutOfLine = false;
    private boolean isOutOfLine = false;
    private boolean isTakeAway = false;
    private int patronPosition;
    private String waitTime;
    private boolean isDineInOnly;
    private boolean isPatronSeated = false;
    private static boolean isAppKilled = false;
    private String systemTime = null, openTime = null, closeTime = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MultiDex.install(this);
        setContentView(R.layout.activity_order_confirmation);
        refreshData();

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        drawerList = (ListView) findViewById(R.id.drawer_list);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        userName = (TextView) findViewById(R.id.user_name);
        userLocation = (TextView) findViewById(R.id.user_location);
        cartContent = (RelativeLayout) findViewById(R.id.cart_content);
        textViewTime = (TextView) findViewById(R.id.textViewTime);
        bookingStatus = (RelativeLayout) findViewById(R.id.bookingStatus_layout);
        bookingStatus1 = (RelativeLayout) findViewById(R.id.bookingStatus_layout1);
        waitingInfoLayout = (RelativeLayout) findViewById(R.id.waiting_layout);
        loadingImageLayout = (RelativeLayout) findViewById(R.id.loading_image_layout);
        noStatus = (TextView) findViewById(R.id.noStatus);
        position = (TextView) findViewById(R.id.position);
        quickChat = (Button) findViewById(R.id.quick_chat);
        preOrder = (Button) findViewById(R.id.edit_order);
        getOutOfLine = (Button) findViewById(R.id.get_out_of_line);
        ewtText = (TextView) findViewById(R.id.estimated_wait_time_text);
        partiesAheadText = (TextView) findViewById(R.id.parties_waiting_text);
        firstPosition = (TextView) findViewById(R.id.first_position);
        seatedLayout = (RelativeLayout) findViewById(R.id.seated_layout);
        ewtScreenLayout = (LinearLayout) findViewById(R.id.ewt_screen_layout);
        restaurantImage = (ImageView) findViewById(R.id.seated_rest_image);
        restaurantName = (TextView) findViewById(R.id.seated_restaurant_name);
        Button seatedQuickChat = (Button) findViewById(R.id.seated_quick_chat);
        orderInTxt = (TextView) findViewById(R.id.order_in);
        restName = (TextView) findViewById(R.id.rest_name);
        restAddress = (TextView) findViewById(R.id.rest_address);

        toolbar.setTitle("Booking Status");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        connectionDetector = new ConnectionDetector(this);
        Globals.setCustomTypeface(this);

//        SharedPreferences preferences = getSharedPreferences("Dine_In", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = preferences.edit();
//        editor.putBoolean("isDineInOnly", false);
//        editor.commit();

        menu = (ImageView) findViewById(R.id.menu_button);
        menu.setVisibility(View.GONE);

        // for android v6.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            bookingStatus.setBackgroundColor(ContextCompat.getColor(this, R.color.primary_color));
//            toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.primary_color));
            quickChat.setBackgroundColor(ContextCompat.getColor(this, R.color.primary_color));
            preOrder.setBackgroundColor(ContextCompat.getColor(this, R.color.primary_color));
            getOutOfLine.setBackgroundColor(ContextCompat.getColor(this, R.color.primary_color));
            seatedQuickChat.setBackgroundColor(ContextCompat.getColor(this, R.color.primary_color));
        }

        SpecificMenuSingleton menuSingleton = SpecificMenuSingleton.getInstance();
        SharedPreferences sharedPreferences = getSharedPreferences("ewt_info", Context.MODE_PRIVATE);
        isDineInOnly = sharedPreferences.getBoolean("isDineInOnly", false);
        isTakeAway = sharedPreferences.getBoolean("isTakeAway", false);

        if (!Config.bookingStatus) {
            bookingStatus.setVisibility(View.GONE);
            waitingInfoLayout.setVisibility(View.GONE);
            bookingStatus1.setVisibility(View.VISIBLE);
        } else {
            // if user booked a table/did carryout
            toolbar.setVisibility(View.GONE);
            bookingStatus.setVisibility(View.VISIBLE);
            waitingInfoLayout.setVisibility(View.VISIBLE);
            bookingStatus1.setVisibility(View.GONE);

//            SharedPreferences preferences = getSharedPreferences("ewt_info", MODE_PRIVATE);
//            SharedPreferences.Editor editor = preferences.edit();
//            editor.putBoolean("is_patron_seated", false);
//            editor.commit();
            isPatronSeated = sharedPreferences.getBoolean("is_patron_seated", false);

            if (isPatronSeated) {
                // show thank you screen
//                new XMPPAsyncTask().execute();
                String restDetails = sharedPreferences.getString("rest_details", null);
                int visitId = sharedPreferences.getInt("visit_id", 0);
                Gson gson = new Gson();
                LocationListModel restModel = gson.fromJson(restDetails, LocationListModel.class);
                menuSingleton.setClickedRestaurant(restModel);
                menuSingleton.setVisitId(visitId);
                ewtScreenLayout.setVisibility(View.GONE);
                seatedLayout.setVisibility(View.VISIBLE);
//                isAppKilled = true;
                Glide.with(OrderConfirmationActivity.this).load(Config.QUICK_CHAT_IMAGE + menuSingleton.getClickedRestaurant().getRestaurantImage())
                        .crossFade(1000).into(restaurantImage);
                restaurantName.setText(menuSingleton.getClickedRestaurant().getRestaurantName());
                currentTime = "00:00:00";
            } else {
                // show ewt screen
                ewtScreenLayout.setVisibility(View.VISIBLE);
                seatedLayout.setVisibility(View.GONE);

                if (menuSingleton.getEwt() != null) {
//                    isAppKilled = false;
                    waitTime = menuSingleton.getEwt();
                    patronPosition = menuSingleton.getPosition();
                    if (currentTime != null) {
                        if (!isTakeAway) {
                            waitTime = currentTime;
                        }
                    }
                } else {
//                    new XMPPAsyncTask().execute();
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                    Date date = Calendar.getInstance().getTime();
                    String time = sdf.format(date);
                    String systemTime = sharedPreferences.getString("system_time", null);
                    waitTime = sharedPreferences.getString("wait_time", null);
//                    isAppKilled = true;
                    waitTime = getExactWaitTime(time, systemTime, waitTime);
                    if (waitTime.contains("-")) {
                        waitTime = "00:00:00";
                    } else {
                        int hr = Integer.parseInt(waitTime) / 3600;
                        int rem = Integer.parseInt(waitTime) % 3600;
                        int min = rem / 60;
                        int sec = rem % 60;
                        String hrStr = (hr < 10 ? "0" : "") + hr;
                        String mnStr = (min < 10 ? "0" : "") + min;
                        String secStr = (sec < 10 ? "0" : "") + sec;

                        waitTime = hrStr + ":" + mnStr + ":" + secStr;
                    }
                    String restDetails = sharedPreferences.getString("rest_details", null);
                    int visitId = sharedPreferences.getInt("visit_id", 0);
                    patronPosition = sharedPreferences.getInt("patron_position", 0);
                    Gson gson = new Gson();
                    LocationListModel restModel = gson.fromJson(restDetails, LocationListModel.class);
                    menuSingleton.setClickedRestaurant(restModel);
                    menuSingleton.setVisitId(visitId);
                    menuSingleton.setEwt(waitTime);
                    menuSingleton.setPosition(patronPosition);
                }
                if (isDineInOnly) {
                    // if user select only getInLine
                    if (!RegistrationActivity.isAppKilled) {
                        createGeoFence();
                    }
                    isOutOfLine = true;
                    if (patronPosition == 0) {
                        partiesAheadText.setVisibility(View.GONE);
                        position.setVisibility(View.GONE);
                        firstPosition.setVisibility(View.VISIBLE);
                        orderInTxt.setVisibility(View.GONE);
                        firstPosition.setTypeface(Globals.blackJack);
                        orderInTxt.setTypeface(Globals.blackJack);
                    } else {
                        partiesAheadText.setVisibility(View.VISIBLE);
                        position.setVisibility(View.VISIBLE);
                        firstPosition.setVisibility(View.GONE);
                        orderInTxt.setVisibility(View.GONE);
                        position.setText(String.valueOf(patronPosition));
                    }

                    if (waitTime != null) {
                        if (!waitTime.equals("00:00:00")) {
                            String[] array1 = waitTime.split(":");
                            v = (Integer.parseInt(array1[0]) * 60 * 60) + (Integer.parseInt(array1[1]) * 60) + (Integer.parseInt(array1[2]));
                            timer = new CounterClass(v * 1000, 100);
                            timer.start();
                            textViewTime.setText(timer.toString());
                        } else {
                            currentTime = "00:00:00";
                            textViewTime.setText("00:00:00");
                        }
                    }/* else {
                        String[] array = waitTime.split(":");
                        v = (Integer.parseInt(array[0]) * 60 * 60) + (Integer.parseInt(array[1]) * 60) + (Integer.parseInt(array[2]));
                        timer = new CounterClass(v * 1000, 100);
                        timer.start();
                        textViewTime.setText(timer.toString());
                    }*/

                } else {
                    // user select for pre-order/carryout

                    if (isTakeAway) {
                        // if carryout
                        isOutOfLine = false;
                        if (!RegistrationActivity.isAppKilled) {
                            createGeoFence();
                        }
                        ewtText.setText("Your Order Ready In:");
                        partiesAheadText.setVisibility(View.GONE);
                        position.setVisibility(View.GONE);
                        quickChat.setVisibility(View.VISIBLE);
                        firstPosition.setVisibility(View.GONE);
                        orderInTxt.setVisibility(View.GONE);
                        getOutOfLine.setText("I Picked Up My Order");
                        preOrder.setVisibility(View.GONE);

                        String time = waitTime;
                        if (!time.equals("00:00:00")) {
                            String[] array1 = time.split(":");
                            v = (Integer.parseInt(array1[0]) * 60 * 60) + (Integer.parseInt(array1[1]) * 60) + (Integer.parseInt(array1[2]));
                            timer = new CounterClass(v * 1000, 100);
                            timer.start();
                            textViewTime.setText(timer.toString());
                        } else {
                            currentTime = "00:00:00";
                            textViewTime.setText("00:00:00");
                        }

//                        getOutOfLine.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                startActivity(new Intent(OrderConfirmationActivity.this, ChatActivity.class));
//                            }
//                        });
                    } else {
                        // if pre-oreder
                        isOutOfLine = true;
                        preOrder.setVisibility(View.GONE);
                        if (menuSingleton.getPosition() == 0) {
                            partiesAheadText.setVisibility(View.GONE);
                            position.setVisibility(View.GONE);
                            firstPosition.setVisibility(View.VISIBLE);
                            orderInTxt.setVisibility(View.VISIBLE);
                            firstPosition.setTypeface(Globals.blackJack);
                            orderInTxt.setTypeface(Globals.blackJack);
                        } else {
                            partiesAheadText.setVisibility(View.VISIBLE);
                            position.setVisibility(View.VISIBLE);
                            firstPosition.setVisibility(View.GONE);
                            orderInTxt.setVisibility(View.VISIBLE);
                            orderInTxt.setTypeface(Globals.blackJack);
                            position.setText(String.valueOf(patronPosition));
                        }

                        if (waitTime != null) {
                            String time = waitTime;
                            if (!time.equals("00:00:00")) {
                                String[] array1 = time.split(":");
                                v = (Integer.parseInt(array1[0]) * 60 * 60) + (Integer.parseInt(array1[1]) * 60) + (Integer.parseInt(array1[2]));
                                timer = new CounterClass(v * 1000, 100);
                                timer.start();
                                textViewTime.setText(timer.toString());
                            } else {
                                currentTime = "00:00:00";
                                textViewTime.setText("00:00:00");
                            }
                        }/* else {
                            String[] array = waitTime.split(":");
                            v = (Integer.parseInt(array[0]) * 60 * 60) + (Integer.parseInt(array[1]) * 60) + (Integer.parseInt(array[2]));
                            timer = new CounterClass(v * 1000, 100);
                            timer.start();
                            textViewTime.setText(timer.toString());
                        }*/
                    }

                }
            }
            restName.setText(SpecificMenuSingleton.getInstance().getClickedRestaurant().getRestaurantName());
            restAddress.setText(SpecificMenuSingleton.getInstance().getClickedRestaurant().getRestaurantAddress() + ", " +
                    SpecificMenuSingleton.getInstance().getClickedRestaurant().getRestaurantCity() + ", " +
                    SpecificMenuSingleton.getInstance().getClickedRestaurant().getRestaurantState());
        }

        cartContent.setVisibility(View.GONE);
        quickChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OrderConfirmationActivity.this, ChatImagesActivity.class));
            }
        });

        seatedQuickChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OrderConfirmationActivity.this, ChatImagesActivity.class));
            }
        });

        preOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTimeZone();
                VolleySingleton.getInstance(OrderConfirmationActivity.this).postEventRequest("PreOrder", SpecificMenuSingleton.getInstance().getClickedRestaurant());
                if (SpecificMenuSingleton.getInstance().getClickedRestaurant().getRestaurantAccountName().equals("SILVER")) {
                    showNotSupportedMessage();
                } else {
                    if (SpecificMenuSingleton.getInstance().getClickedRestaurant().isPreOrder()) {
                        if (systemTime.compareToIgnoreCase(openTime) >= 0 && systemTime.compareToIgnoreCase(closeTime) <= 0) {
                            if (SpecificMenuSingleton.getInstance().getClickedRestaurant().isHostessOnline()) {
                                Config.viewMenu = false;
                                Config.bookingStatus = true;
                                startActivityForResult(new Intent(OrderConfirmationActivity.this, MenuActivity.class), 1);
                            }else {
                                Log.i("Hostess Offline", "true");
                                Toast.makeText(OrderConfirmationActivity.this, R.string.hostess_offline_msg, Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            Log.i("Business time", "true");
                            Toast.makeText(OrderConfirmationActivity.this, R.string.hostess_offline_msg, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        showNotSupportedMessage();
                    }
                }
            }
        });

//        if (!isTakeAway) {
        getOutOfLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                    removeGeoFence();
                showDialog(isOutOfLine);
            }
        });
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreference = getSharedPreferences("user_info", MODE_PRIVATE);
        Config.SESSION_TOKEN_ID = sharedPreference.getString("session_token_id", null);
//        if(!Config.takeAway){
//        refreshDataRequest();
//        }
    }

    @Override
    protected void onPause() {
        if (isGetOutOfLine) {
            clearData();
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            Date date = Calendar.getInstance().getTime();
            String systemTime = sdf.format(date);
            SharedPreferences preferences = getSharedPreferences("ewt_info", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("wait_time", currentTime);
            editor.putString("system_time", systemTime);
            Gson gson = new Gson();
            String json = gson.toJson(SpecificMenuSingleton.getInstance().getClickedRestaurant());
            editor.putString("rest_details", json);
            editor.putInt("visit_id", SpecificMenuSingleton.getInstance().getVisitId());
            editor.putInt("patron_position", patronPosition);
            editor.putBoolean("is_patron_seated", isPatronSeated);
//        editor.putString("",);
            editor.commit();
            SpecificMenuSingleton.getInstance().setPosition(patronPosition);
        }
        super.onPause();
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @SuppressLint("NewApi")
    public class CounterClass extends CountDownTimer {
        public CounterClass(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            textViewTime.setText("00:00:00");
            currentTime = "00:00:00";
//            if(handler != null) {
//                handler.removeCallbacks(runnable);
//            }
        }

        @SuppressLint("NewApi")
        @TargetApi(Build.VERSION_CODES.GINGERBREAD)
        @Override
        public void onTick(long millisUntilFinished) {
            long millis = millisUntilFinished;
            String hms = String.format(Locale.getDefault(), "%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                    TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                    TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
            currentTime = hms;
            textViewTime.setText(hms);
        }

    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        startActivity(new Intent(this,MenuActivity.class));
//        finish();
//    }

    public void getOutOfLine() {
        loadingImageLayout.setVisibility(View.VISIBLE);
        bookingStatus.setVisibility(View.GONE);
        waitingInfoLayout.setVisibility(View.GONE);
        final SpecificMenuSingleton specificMenuSingleton = SpecificMenuSingleton.getInstance();
        String url = String.format
                (Config.QT_GET_OUT_OF_LINE_URL + "?tenant_id=%d&visit_id=%d"
                        , specificMenuSingleton.getClickedRestaurant().getTenantID(), specificMenuSingleton.getVisitId());
        Log.i("PICK_UP_URL", url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("GET_OUT_OF_LINE", response.toString());
                        if (timer != null) {
                            timer.cancel();
                        }
                        isGetOutOfLine = true;
                        handler.removeCallbacks(runnable);
                        specificMenuSingleton.setEwt(null);
                        if (!RegistrationActivity.isAppKilled) {
                            removeGeoFence();
                        }
                        Config.bookingStatus = false;
                        clearData();
                        Intent intent = new Intent(OrderConfirmationActivity.this, RegistrationActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                loadingImageLayout.setVisibility(View.GONE);
                bookingStatus.setVisibility(View.VISIBLE);
                waitingInfoLayout.setVisibility(View.VISIBLE);

                if (error != null) {
                    if (error instanceof TimeoutError) {
                        Config.internetSlowError(OrderConfirmationActivity.this);
                    } else if (error instanceof NoConnectionError) {
                        Config.internetSlowError(OrderConfirmationActivity.this);
                    } else if (error instanceof ServerError) {
                        Config.serverError(OrderConfirmationActivity.this);
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
                Config.TIMEOUT,  // 45 sec - timeout
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        VolleySingleton.getInstance(OrderConfirmationActivity.this).addToRequestQueue(request);
    }

    @Override
    protected void onDestroy() {
        Glide.clear(restaurantImage);
        if (timer != null) {
            timer.cancel();
        }
        handler.removeCallbacks(runnable);
        SpecificMenuSingleton.getInstance().setEwt(null);
        super.onDestroy();
    }

    public void refreshData() {
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                if (connectionDetector.isConnectedToInternet()) {
                    refreshDataRequest();
                } else {
                    connectionDetector.clearDialog();
                    connectionDetector.internetError();
                    refreshData();
                }
            }
        };
        handler.postDelayed(runnable, 60 * 1000);
    }

    public void refreshDataRequest() {
        final SpecificMenuSingleton menuSingleton = SpecificMenuSingleton.getInstance();
        String url = String.format(Config.QT_EWT_REFRESH_URL + "?tenant_id=%d&location_id=%d&visit_id=%d",
                menuSingleton.getClickedRestaurant().getTenantID(), menuSingleton.getClickedRestaurant().getLocationID(), menuSingleton.getVisitId());
        Log.i("refresh_data", url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("RefreshData", response.toString());

                        String str = null;
                        try {
                            boolean inLine = response.getBoolean("seated");
                            boolean removed = response.getBoolean("cleared");
                            if (!inLine) {

                                if (isTakeAway) {
                                    str = menuSingleton.getEwt();
                                    firstPosition.setText("");
                                    orderInTxt.setText("");
                                    partiesAheadText.setVisibility(View.GONE);
                                    position.setVisibility(View.GONE);
                                    quickChat.setVisibility(View.VISIBLE);
                                    getOutOfLine.setText("I Picked Up My Order");
//                                    getOutOfLine.setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View v) {
//                                            startActivity(new Intent(OrderConfirmationActivity.this, ChatActivity.class));
//                                        }
//                                    });

                                } else {
                                    Integer ewt = response.getInt("ewt");
                                    StringBuilder stringBuilder = new StringBuilder();
                                    stringBuilder.append("00:");
                                    if (ewt < 10) {
                                        stringBuilder.append("0" + ewt + ":");
                                    } else {
                                        stringBuilder.append(ewt + ":");
                                    }
                                    stringBuilder.append("00");
                                    str = stringBuilder.toString();

                                    patronPosition = response.getInt("position");

                                    if (isDineInOnly) {

                                        if (patronPosition == 0) {
                                            partiesAheadText.setVisibility(View.GONE);
                                            position.setVisibility(View.GONE);
                                            firstPosition.setVisibility(View.VISIBLE);
                                            orderInTxt.setVisibility(View.GONE);
                                            firstPosition.setTypeface(Globals.blackJack);
                                            orderInTxt.setTypeface(Globals.blackJack);
                                        } else {
                                            partiesAheadText.setVisibility(View.VISIBLE);
                                            position.setVisibility(View.VISIBLE);
                                            firstPosition.setVisibility(View.GONE);
                                            orderInTxt.setVisibility(View.GONE);
                                            position.setText(String.valueOf(patronPosition));
                                        }

                                    } else {

                                        if (patronPosition == 0) {
                                            partiesAheadText.setVisibility(View.GONE);
                                            position.setVisibility(View.GONE);
                                            firstPosition.setVisibility(View.VISIBLE);
                                            orderInTxt.setVisibility(View.VISIBLE);
                                            firstPosition.setTypeface(Globals.blackJack);
                                            orderInTxt.setTypeface(Globals.blackJack);
                                        } else {
                                            partiesAheadText.setVisibility(View.VISIBLE);
                                            position.setVisibility(View.VISIBLE);
                                            firstPosition.setVisibility(View.GONE);
                                            orderInTxt.setVisibility(View.VISIBLE);
                                            orderInTxt.setTypeface(Globals.blackJack);
                                            position.setText(String.valueOf(patronPosition));
                                        }
                                        preOrder.setVisibility(View.GONE);
                                    }
                                }

                                if (menuSingleton.getEwt() != null) {

                                    if (menuSingleton.getEwt().equals(str) || (currentTime.compareTo(str) < 0)) {

                                    } else {
//                                    timer.cancel();
                                        menuSingleton.setEwt(str);

                                        String[] array = str.split(":");
                                        v = (Integer.parseInt(array[0]) * 60 * 60) + (Integer.parseInt(array[1]) * 60) + (Integer.parseInt(array[2]));
                                        if (timer != null) {
                                            timer.cancel();
                                        }
                                        timer = new CounterClass(v * 1000, 100);
                                        timer.start();
                                        textViewTime.setText(timer.toString());
                                    }
                                }

                                isPatronSeated = false;

                                refreshData();
                            } else if (inLine && !removed) {
//                                if (timer != null) {
//                                    timer.cancel();
//                                }
//                                handler.removeCallbacks(runnable);
                                if (!isTakeAway) {
                                    ewtScreenLayout.setVisibility(View.GONE);
                                    seatedLayout.setVisibility(View.VISIBLE);
                                    Glide.with(OrderConfirmationActivity.this).load(Config.QUICK_CHAT_IMAGE + menuSingleton.getClickedRestaurant().getRestaurantImage())
                                            .crossFade(1000).into(restaurantImage);
                                    restaurantName.setText(menuSingleton.getClickedRestaurant().getRestaurantName());
                                    isPatronSeated = true;
                                }
                                refreshData();
                            } else if (removed) {
                                if (timer != null) {
                                    timer.cancel();
                                }
                                handler.removeCallbacks(runnable);
                                isTakeAway = false;
                                menuSingleton.setEwt(null);
//                                removeGeoFence();
                                isGetOutOfLine = true;
                                isPatronSeated = false;
                                clearData();
                                if (!RegistrationActivity.isAppKilled) {
                                    removeGeoFence();
                                }
                                Config.bookingStatus = false;
                                Intent intent = new Intent(OrderConfirmationActivity.this, RegistrationActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            }

                        } catch (JSONException e) {

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                refreshData();
                if (error != null) {
                    if (error instanceof TimeoutError) {
                        Config.internetSlowError(OrderConfirmationActivity.this);
                    } else if (error instanceof NoConnectionError) {
                        Config.internetSlowError(OrderConfirmationActivity.this);
                    } else if (error instanceof ServerError) {
                        Config.serverError(OrderConfirmationActivity.this);
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

    public void showDialog(boolean isGetOutOfLine) {
        AlertDialog.Builder builder = new AlertDialog.Builder(OrderConfirmationActivity.this);
        builder.setTitle("Alert");
        if (isGetOutOfLine) {
            builder.setMessage("Are you sure you want to get out of the line ?");
        } else {
            builder.setMessage("Are you sure you picked up your order ?");
        }
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Config.bookingStatus = false;
                getOutOfLine();
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

    @Override
    public void onBackPressed() {
        if (!Config.bookingStatus) {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    private void showNotSupportedMessage() {
        String name = SpecificMenuSingleton.getInstance().getClickedRestaurant().getRestaurantName();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alert");
        builder.setMessage(name + " doesn’t support that feature yet...would you like us to tell them to add this feature?");
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

    public void createGeoFence() {
        // adding geofence
        SpecificMenuSingleton menuSingleton = SpecificMenuSingleton.getInstance();
        geofence = new Geofence.Builder()
                .setRequestId(menuSingleton.getClickedRestaurant().getRestaurantName())
                .setCircularRegion(menuSingleton.getLatitude(), menuSingleton.getLongitude(), 100)
//                .setCircularRegion(17.4147089, 78.430211, 500)
                .setExpirationDuration(2 * 60 * 60 * 1000)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();

        Log.i("Geofence", "Geofence created");

        geofenceStore = new GeofenceStore(this, geofence);
    }

    public static void removeGeoFence() {
        geofenceStore.removeRequestPendingIntent();
        geofenceStore.removeGeofence();
    }

    private void clearData() {
        SharedPreferences preferences = getSharedPreferences("ewt_info", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("wait_time", null);
        editor.putString("rest_details", null);
        editor.putInt("visit_id", 0);
        editor.putInt("patron_position", 0);
        editor.putBoolean("isDineInOnly", false);
        editor.putBoolean("isTakeAway", false);
        editor.putBoolean("is_patron_seated", false);
//        editor.putString("",);
        editor.commit();
    }

    private String getExactWaitTime(String time, String systemTime, String waitTime) {

        System.out.println("LOG: time = " + time);
        System.out.println("LOG: systemTime = " + systemTime);
        System.out.println("LOG: waitTime = " + waitTime);

        Integer newTime = Integer.parseInt(timeToSecond(waitTime)) - (Integer.parseInt(timeToSecond(time)) - Integer.parseInt(timeToSecond(systemTime)));

        return newTime.toString();
    }

    private String timeToSecond(String time) {
        String t[] = time.split(":");
        String sec = t[2];
        String min = t[1];
        String hr = t[0];
        Integer newTime = (Integer.parseInt(hr) * 60 * 60) + (Integer.parseInt(min) * 60) + (Integer.parseInt(sec));
        return newTime.toString();
    }

//    private XMPPTCPConnection connection;
//    private MultiUserChatManager manager;

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
//                connection.setPacketReplyTimeout(30000);
//                connection.connect();
//
//                Log.i("Login", "Try to login as " + userPhone);
////                Log.i("factualId", "FactualId " + SpecificMenuSingleton.getInstance().getClickedRestaurant().getPlaceID());
//                connection.login(userPhone, userPhone);
////                connection.login("917569550492", "917569550492");
//                Log.i("XMPPClient", "Logged in as " + connection.getUser());
//                Presence presence = new Presence(Presence.Type.available);
//                connection.sendPacket(presence);
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

    private void setTimeZone() {
        SpecificMenuSingleton menuSingleton = SpecificMenuSingleton.getInstance();
        SimpleDateFormat f = new SimpleDateFormat("HH:mm:ss");
        f.setTimeZone(TimeZone.getTimeZone(menuSingleton.getClickedRestaurant().getTimezone()));
        systemTime = f.format(new Date());
        openTime = menuSingleton.getClickedRestaurant().getRestaurantOpenTiming();
        closeTime = menuSingleton.getClickedRestaurant().getRestaurantCloseTiming();
        int time1 = systemTime.compareToIgnoreCase(openTime);
        int time2 = systemTime.compareToIgnoreCase(closeTime);
        Log.i("Timings", "CurrentTime= " + systemTime + " OpenTime= " + openTime + " CloseTime= " + closeTime + " timestatus= " + time1 + " timestatus= " + time2);
    }

}
