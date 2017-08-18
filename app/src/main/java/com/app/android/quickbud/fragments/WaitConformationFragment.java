package com.app.android.quickbud.fragments;


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
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.app.android.quickbud.activities.ListOfRestaurantActivity;
import com.app.android.quickbud.activities.MenuActivity;
import com.app.android.quickbud.geofence.GeofenceStore;
import com.app.android.quickbud.network.VolleySingleton;
import com.app.android.quickbud.utils.Config;
import com.app.android.quickbud.utils.ConnectionDetector;
import com.app.android.quickbud.utils.Globals;
import com.app.android.quickbud.utils.SpecificMenuSingleton;
import com.app.android.quickbud.utils.TimerCounter;
import com.bumptech.glide.Glide;
import com.google.android.gms.location.Geofence;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 */
public class WaitConformationFragment extends Fragment {

    TextView userName, userLocation, textViewTime, position, partiesAheadText, firstPosition, restaurantName;
    Button seatedQuickChat;
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    ListView drawerList;
    ImageView menu, restaurantImage;
    RelativeLayout cartContent, loadingImageLayout, partiesSizeLayout, ewtLayout, seatedLayout;
    LinearLayout ewtScreenLayout;
    int v;
    CounterClass timer;
    Handler handler;
    Runnable runnable;
    private Geofence geofence;
    private static GeofenceStore geofenceStore;
    final SpecificMenuSingleton menuSingleton = SpecificMenuSingleton.getInstance();
    private static String currentTime;
    ConnectionDetector connectionDetector;

    public WaitConformationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_wait_conformation, container, false);
        Button quickChat = (Button) view.findViewById(R.id.quick_chat);
        Button placeYourOrder = (Button) view.findViewById(R.id.place_your_order);
        Button getOutOfLine = (Button) view.findViewById(R.id.get_out_of_line);
        seatedQuickChat = (Button) view.findViewById(R.id.seated_quick_chat);
        cartContent = (RelativeLayout) view.findViewById(R.id.cart_content);
        loadingImageLayout = (RelativeLayout) view.findViewById(R.id.loading_image_layout);
        partiesSizeLayout = (RelativeLayout) view.findViewById(R.id.parties_size_layout);
        ewtLayout = (RelativeLayout) view.findViewById(R.id.ewt_layout);
        toolbar = (Toolbar) view.findViewById(R.id.app_bar);
        partiesAheadText = (TextView) view.findViewById(R.id.parties_waiting_text);
        firstPosition = (TextView) view.findViewById(R.id.first_position);
        seatedLayout = (RelativeLayout) view.findViewById(R.id.seated_layout);
        ewtScreenLayout = (LinearLayout) view.findViewById(R.id.ewt_screen_layout);
        restaurantImage = (ImageView) view.findViewById(R.id.seated_rest_image);
        restaurantName = (TextView) view.findViewById(R.id.seated_restaurant_name);

//        userName= (TextView) view.findViewById(R.id.user_name);
//        userLocation= (TextView) view.findViewById(R.id.user_location);
//        drawerList = (ListView) view.findViewById(R.id.drawer_list);
//        drawerLayout = (DrawerLayout) view.findViewById(R.id.drawer);
        menu = (ImageView) view.findViewById(R.id.menu_button);
        textViewTime = (TextView) view.findViewById(R.id.textViewTime);
        position = (TextView) view.findViewById(R.id.position);


        connectionDetector = new ConnectionDetector(getActivity());

        toolbar.setTitle("Estimated Wait Time");
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

//        drawerList.setAdapter(new DrawerListAdapter(getActivity()));
//        menu.setVisibility(View.GONE);
//        cartContent.setVisibility(View.GONE);
        toolbar.setVisibility(View.GONE);

//        SharedPreferences preferences = getActivity().getSharedPreferences("ewt_info",Context.MODE_PRIVATE);
//        String str = preferences.getString("wait_info", null);
        String str = menuSingleton.getEwt();
        if (menuSingleton.getPosition() == 0) {
            partiesAheadText.setVisibility(View.GONE);
            position.setVisibility(View.GONE);
            firstPosition.setVisibility(View.VISIBLE);
            Globals.setCustomTypeface(getActivity());
            firstPosition.setTypeface(Globals.blackJack);
        } else {
            position.setText(String.valueOf(menuSingleton.getPosition()));
        }

        String[] array = str.split(":");

        v = (Integer.parseInt(array[0]) * 60 * 60) + (Integer.parseInt(array[1]) * 60) + (Integer.parseInt(array[2]));

//        if (timer == null){
        timer = new CounterClass(v * 1000, 100);
        timer.start();
//        }else {
//            textViewTime.setText(timer.toString());
//        }

        textViewTime.setText(TimerCounter.getTime());

        createGeoFence();

//        cartContent.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(getActivity(), OrderDetailsActivity.class));
//            }
//        });
//        quickChat.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(getActivity(), ChatActivity.class));
//            }
//        });
//
//        seatedQuickChat.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(getActivity(), ChatActivity.class));
//            }
//        });

        placeYourOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VolleySingleton.getInstance(getActivity()).postEventRequest("PreOrder", SpecificMenuSingleton.getInstance().getClickedRestaurant());
                if (SpecificMenuSingleton.getInstance().getClickedRestaurant().getRestaurantAccountName().equals("SILVER")){
                    showNotSupportedMessage();
                }else {
                    Config.viewMenu = false;
                    Config.bookingStatus = true;
                    startActivityForResult(new Intent(getActivity(), MenuActivity.class), 1);
                }
            }
        });

        getOutOfLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeGeoFence();
                Config.bookingStatus = false;
                showDialog();
            }
        });


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
        refreshData();

        return view;
    }


    public void refreshData() {
        handler.postDelayed(runnable, 60 * 1000);
    }

    @Override
    public void onResume() {
        super.onResume();

//        SharedPreferences sharedPreference = getActivity().getSharedPreferences("user_info", Context.MODE_PRIVATE);
//        String profileName = sharedPreference.getString("user_name", null);
//        String userAddress = sharedPreference.getString("user_address", null);
//        userName.setText(profileName);
//        userLocation.setText(userAddress);
//        userName.setTypeface(Globals.robotoBold);
//        userLocation.setTypeface(Globals.robotoRegular);
    }

    @Override
    public void onPause() {
        SharedPreferences preferences = getActivity().getSharedPreferences("ewt_info", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("wait_time",currentTime);
//        editor.putString("",);
        editor.commit();
        super.onPause();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == getActivity().RESULT_OK) {
                if (handler != null) {
                    handler.removeCallbacks(runnable);
//                    refreshDataRequest();
                    refreshData();
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        Glide.clear(restaurantImage);
        super.onDestroy();
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
//            if(handler != null){
//                handler.removeCallbacks(runnable);
//            }
        }

        @SuppressLint("NewApi")
        @TargetApi(Build.VERSION_CODES.GINGERBREAD)
        @Override
        public void onTick(long millisUntilFinished) {
            long millis = millisUntilFinished;
            String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                    TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                    TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
            currentTime = hms;
//            Log.i("Timer", hms);
//            Log.i("CurrentTime", currentTime);
            textViewTime.setText(hms);
        }
    }

    public static String getTime() {
        return currentTime;
    }

    public void getOutOfLine() {
        loadingImageLayout.setVisibility(View.VISIBLE);
        partiesSizeLayout.setVisibility(View.GONE);
        ewtLayout.setVisibility(View.GONE);
        SpecificMenuSingleton specificMenuSingleton = SpecificMenuSingleton.getInstance();
        String url = String.format
                ("http://159.203.88.161/qt/api/visit/get_out_of_line/?tenant_id=%d&visit_id=%d"
                        , specificMenuSingleton.getTenantId(), specificMenuSingleton.getVisitId());

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("GET_OUT_OF_LINE", response.toString());
                        if (timer != null) {
                            timer.cancel();
                        }
                        handler.removeCallbacks(runnable);
                        Intent intent = new Intent(getActivity(), ListOfRestaurantActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        getActivity().finish();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error != null){
                    if (error instanceof TimeoutError) {
                        Config.internetSlowError(getActivity());
                    } else if (error instanceof NoConnectionError) {
                        Config.internetSlowError(getActivity());
                    } else if (error instanceof ServerError) {
                        Config.internetSlowError(getActivity());
                    }
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", Config.SESSION_TOKEN_ID);
                return headers;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                45 * 1000,  // 45 sec - timeout
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        VolleySingleton.getInstance(getActivity()).addToRequestQueue(request);
    }

    public void refreshDataRequest() {
        final SpecificMenuSingleton menuSingleton = SpecificMenuSingleton.getInstance();
        String url = String.format("http://159.203.88.161/qt/api/visit/position/?tenant_id=%d&location_id=%d&visit_id=%d",
                menuSingleton.getTenantId(), menuSingleton.getLocationId(), menuSingleton.getVisitId());

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("RefreshData", response.toString());

                        String str = null;
                        try {
                            boolean inLine = response.getBoolean("seated");
                            boolean removed = response.getBoolean("cleared");
                            if (!inLine && !removed) {
                                if (response.getInt("position") == 0) {
                                    partiesAheadText.setVisibility(View.GONE);
                                    position.setVisibility(View.GONE);
                                    firstPosition.setVisibility(View.VISIBLE);
                                    Globals.setCustomTypeface(getActivity());
                                    firstPosition.setTypeface(Globals.blackJack);
                                } else {
                                    position.setText(String.valueOf(response.getInt("position")));
                                    menuSingleton.setPosition(response.getInt("position"));
                                }
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

                                refreshData();
                            } else if (inLine && !removed) {
//                                if (timer != null) {
//                                    timer.cancel();
//                                }
//                                handler.removeCallbacks(runnable);
                                ewtScreenLayout.setVisibility(View.GONE);
                                seatedLayout.setVisibility(View.VISIBLE);
                                Glide.with(getActivity()).load(Config.QT_IMAGE + menuSingleton.getImageUrl())
                                        .crossFade(1000).into(restaurantImage);
                                restaurantName.setText(menuSingleton.getRestaurantName());
                                refreshData();
                            } else if (removed) {
                                if (timer != null) {
                                    timer.cancel();
                                }
                                handler.removeCallbacks(runnable);
                                removeGeoFence();
                                Intent intent = new Intent(getActivity(), ListOfRestaurantActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                getActivity().finish();
                            }

                        } catch (JSONException e) {

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO
            }
        });

        VolleySingleton.getInstance(getActivity()).addToRequestQueue(request);
    }

    public void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Alert");
        builder.setMessage("Are you sure you want to get out of the line ?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
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

    private void showNotSupportedMessage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Alert");
        builder.setMessage("This restaurant doesn’t support that feature yet...would you like us to tell them to add this feature?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
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
        geofence = new Geofence.Builder()
                .setRequestId(menuSingleton.getRestaurantName())
                .setCircularRegion(menuSingleton.getLatitude(), menuSingleton.getLongitude(), 100)
//                .setCircularRegion(17.4147089, 78.430211, 500)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();

        Log.i("Geofence", "Geofence created");

        geofenceStore = new GeofenceStore(getActivity(), geofence);
    }

    public static void removeGeoFence() {
        geofenceStore.removeRequestPendingIntent();
        geofenceStore.removeGeofence();
    }


}
