package com.app.android.quickbud.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.app.android.quickbud.activities.RestaurantServices;
import com.app.android.quickbud.modelClasses.LocationListModel;
import com.app.android.quickbud.modelClasses.RestaurantTimingsModel;
import com.app.android.quickbud.modelClasses.TopBudsDispensaryModel;
import com.app.android.quickbud.network.VolleySingleton;
import com.app.android.quickbud.utils.Config;
import com.app.android.quickbud.utils.SpecificMenuSingleton;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class TopBudDetailsFragment extends Fragment implements View.OnClickListener {

    private ImageView loadingImage, menuItemImage, likeIcon, rightArrow;
    private TextView menuName, menuItemName, menuDescription, thc, cbd, likesCount, likeTxt;
    private RelativeLayout itemDetailsLayout;
    private TopBudsDispensaryModel topBudsDispensaryModel;
    private Toolbar toolbar;
    private double currentLatitude, currentLongitude;

    public TopBudDetailsFragment() {
        // Required empty public constructor
    }

    public TopBudDetailsFragment(TopBudsDispensaryModel topBudsDispensaryModel) {
        this.topBudsDispensaryModel = topBudsDispensaryModel;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_top_bud_details, container, false);
        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);

        menuName = (TextView) view.findViewById(R.id.menu_name);
        menuItemImage = (ImageView) view.findViewById(R.id.category_image);
        menuItemName = (TextView) view.findViewById(R.id.category_title);
        menuDescription = (TextView) view.findViewById(R.id.category_description);
        loadingImage = (ImageView) view.findViewById(R.id.loading_image);
        itemDetailsLayout = (RelativeLayout) view.findViewById(R.id.row_layout);
        thc = (TextView) view.findViewById(R.id.thc_fav_data);
        cbd = (TextView) view.findViewById(R.id.cbd_fav_data);
        likesCount = (TextView) view.findViewById(R.id.likes_count);
        likeIcon = (ImageView) view.findViewById(R.id.like_icon);
        likeTxt = (TextView) view.findViewById(R.id.like_text);
        rightArrow = (ImageView) view.findViewById(R.id.right_arrow);

        likeIcon.setOnClickListener(this);
        likeTxt.setOnClickListener(this);

        toolbar.setTitle(topBudsDispensaryModel.getDispensaryName());

        menuName.setText(topBudsDispensaryModel.getDispensaryName());

        Bundle bundle = getArguments();
        currentLatitude = bundle.getDouble("currentLatitude");
        currentLongitude = bundle.getDouble("currentLongitude");
        boolean isFromMainMenu = bundle.getBoolean("isFromMainMenu");

        if (isFromMainMenu){
            menuName.setOnClickListener(this);
            menuName.setTextColor(Color.parseColor("#0000ff"));
            rightArrow.setVisibility(View.VISIBLE);
        }else {
            rightArrow.setVisibility(View.GONE);
        }

        getBudDetails();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void getBudDetails() {

        loadingImage.setVisibility(View.VISIBLE);
        itemDetailsLayout.setVisibility(View.GONE);

        String url = Config.QB_TOP_BUD_DETAILS + topBudsDispensaryModel.getMenuId() +"/?tenant_id=" + topBudsDispensaryModel.getTenantId();
        Log.i("Bud details url", url);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.i("Bud details", response.toString());
                        loadingImage.setVisibility(View.GONE);
                        itemDetailsLayout.setVisibility(View.VISIBLE);

                        try {
                            JSONObject responseObj = response.getJSONObject(0);

                            menuItemName.setText(responseObj.getString("name"));
                            Glide.with(getActivity()).load(Config.QUICK_CHAT_IMAGE + responseObj.getString("url"))
                                    .placeholder(R.mipmap.menu_placeholder)
                                    .into(menuItemImage);
                            thc.setText("THC - " + responseObj.getString("thc") + "%");
                            cbd.setText("CBD - " + responseObj.getString("cbd") + "%");
                            likesCount.setText(""+responseObj.getInt("likes_count"));

                            if (!responseObj.isNull("description")){
                                menuDescription.setText(responseObj.getString("description"));
                            }

                            if (topBudsDispensaryModel.isLiked()){
                                likeIcon.setImageResource(R.mipmap.favorite_green_image);
//                                favoriteTxt.setText("Added to favorites");
                            }else {
                                likeIcon.setImageResource(R.mipmap.favorite_green_icon);
//                                favoriteTxt.setText("Add to favorites");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingImage.setVisibility(View.GONE);
                itemDetailsLayout.setVisibility(View.VISIBLE);
                if (error != null) {
                    if (error instanceof TimeoutError) {
                        Config.internetSlowError(getActivity());
                    } else if (error instanceof NoConnectionError) {
                        Config.internetSlowError(getActivity());
                    } else if (error instanceof ServerError) {
                        Config.serverError(getActivity());
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

        VolleySingleton.getInstance(getActivity()).addToRequestQueue(request);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.like_icon:
            case R.id.like_text:
                if (topBudsDispensaryModel.isLiked()){
                    removeFavoriteItem();
                }else {
                    postTopBud();
                }
                break;

            case R.id.menu_name:
//                getActivity().getSupportFragmentManager().popBackStackImmediate();
                LocationListModel locationListModel = new LocationListModel();
                locationListModel.setPlaceID(topBudsDispensaryModel.getPlaceId());
                locationListModel.setRestaurantName(topBudsDispensaryModel.getDispensaryName());
                locationListModel.setRestaurantAddress(topBudsDispensaryModel.getDispensaryAddress());
                locationListModel.setRestaurantCity(topBudsDispensaryModel.getDispensaryCity());
                locationListModel.setRestaurantState(topBudsDispensaryModel.getDispensaryState());
                getQTSupportedRestaurants(locationListModel);
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        toolbar.setTitle("Top Buds");
    }

    private void postTopBud() {

//        itemDetailsLayout.setBackgroundColor(Color.parseColor("#ffffff"));
        loadingImage.setVisibility(View.VISIBLE);
        itemDetailsLayout.setVisibility(View.GONE);

        String url = Config.QB_ADD_TOP_BUDS;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, postFavoriteItemData(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("Top Bud", response.toString());

//                        int favoriteId = 0;
//                        favoriteMenuItem = new FavoriteMenuItem();
//                        try {
//                            favoriteId = response.getInt("favorite_id");
//                            favoriteMenuItem.setFavoriteId(favoriteId);
//                        } catch (JSONException e) {
//
//                        }
//
                        loadingImage.setVisibility(View.GONE);
                        itemDetailsLayout.setVisibility(View.VISIBLE);
//                        itemDetailsLayout.setBackgroundColor(Color.parseColor("#000000"));
//
                        likeIcon.setImageResource(R.mipmap.favorite_green_image);
//                        favoriteTxt.setText("Added to favorites");
                        topBudsDispensaryModel.setLikesCount(topBudsDispensaryModel.getLikesCount() + 1);
                        likesCount.setText(String.valueOf(topBudsDispensaryModel.getLikesCount()));
                        topBudsDispensaryModel.setLiked(true);
//                        menuModel.get(itemPosition).setFavorite(true);
//                        menuModel.get(itemPosition).setFavoriteId(favoriteId);
//
//                        FavoriteSingleton.getInstance().addToFavorite(menuModel.get(itemPosition));


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                loadingImage.setVisibility(View.GONE);
//                menuItemLayout.setVisibility(View.VISIBLE);
//                menuItemRootLayout.setBackgroundColor(Color.parseColor("#000000"));
                if (error != null) {
                    if (error instanceof TimeoutError) {
                        Config.internetSlowError(getActivity());
                    } else if (error instanceof NoConnectionError) {
                        Config.internetSlowError(getActivity());
                    } else if (error instanceof ServerError) {
                        Config.serverError(getActivity());
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

        VolleySingleton.getInstance(getActivity()).addToRequestQueue(request);
    }
    
    private JSONObject postFavoriteItemData() {

        SpecificMenuSingleton menuSingleton = SpecificMenuSingleton.getInstance();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_info", Context.MODE_PRIVATE);
        String patronId = sharedPreferences.getString("patron_id", null);

        JSONObject favoritesObject = new JSONObject();

        JSONArray favoriteArray = new JSONArray();

        JSONObject favoriteObject = new JSONObject();

        try {
            favoriteObject.put("tenant_id", topBudsDispensaryModel.getTenantId());
            favoriteObject.put("location_id", topBudsDispensaryModel.getLocationId());
            favoriteObject.put("patron_id", patronId);
            favoriteObject.put("menu_id", topBudsDispensaryModel.getMenuId());

            JSONArray choicesArray = new JSONArray();
//            initializeData();
//            for (int j = 0; j < menuChoicesHashMap.size(); j++){
//                menuChoicesModels = menuChoicesHashMap.get(menuOptionsName.get(j));
//                for(int i=0; i<menuChoicesArrayList.size(); i++){
//                    JSONObject choiceObject = new JSONObject();
//                    if(menuChoicesModels.get(i).isChoiceDefault() == 1 && menuChoicesModels.get(i).isDefault() == 1){
//                        choiceObject.put("choice_id",menuChoicesModels.get(i).getMenuChoiceId());
//                        choiceObject.put("choice_name", menuChoicesModels.get(i).getMenuChoiceName());
//                        choicesArray.put(choiceObject);
//                    } else if (menuChoicesModels.get(i).isChoiceDefault() == 0 && menuChoicesModels.get(i).isDefault() == 1){
//                        choiceObject.put("choice_id",menuChoicesModels.get(i).getMenuChoiceId());
//                        choiceObject.put("choice_name", menuChoicesModels.get(i).getMenuChoiceName());
//                        choicesArray.put(choiceObject);
//                    }
//                }
//            }
            favoriteObject.put("choices", choicesArray);
            favoriteArray.put(favoriteObject);

            favoritesObject.put("favorite_items", favoriteArray);
            favoritesObject.put("like", true);

        } catch (JSONException e) {

        }

        return favoritesObject;
    }

    private void removeFavoriteItem() {

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_info", Context.MODE_PRIVATE);
        String patronId = sharedPreferences.getString("patron_id", null);

//        itemDetailsLayout.setBackgroundColor(Color.parseColor("#ffffff"));
        loadingImage.setVisibility(View.VISIBLE);
        itemDetailsLayout.setVisibility(View.GONE);

        String url = Config.QB_TOP_BUD_DELETE + "?tenant_id=" + topBudsDispensaryModel.getTenantId()
                + "&patron_id=" + patronId + "&location_id=" + topBudsDispensaryModel.getLocationId() + "&menu_item_id=" + topBudsDispensaryModel.getMenuId();

        Log.i("Remove Top buds url", url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("Favorite_delete", response.toString());

                        loadingImage.setVisibility(View.GONE);
                        itemDetailsLayout.setVisibility(View.VISIBLE);
//                        itemDetailsLayout.setBackgroundColor(Color.parseColor("#000000"));

                        likeIcon.setImageResource(R.mipmap.favorite_green_icon);
//                        favoriteTxt.setText("Add to favorites");
                        topBudsDispensaryModel.setLikesCount(topBudsDispensaryModel.getLikesCount() - 1);
                        likesCount.setText(String.valueOf(topBudsDispensaryModel.getLikesCount()));
                        topBudsDispensaryModel.setLiked(false);
//
//                        menuModel.get(itemPosition).setFavorite(false);
//
//                        FavoriteSingleton.getInstance().deleteFavorite(favoriteId);


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error != null) {
                    loadingImage.setVisibility(View.GONE);
                    itemDetailsLayout.setVisibility(View.VISIBLE);
                    itemDetailsLayout.setBackgroundColor(Color.parseColor("#000000"));
                    if (error instanceof TimeoutError) {
                        Config.internetSlowError(getActivity());
                    } else if (error instanceof NoConnectionError) {
                        Config.internetSlowError(getActivity());
                    } else if (error instanceof ServerError) {
                        Config.serverError(getActivity());
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

        VolleySingleton.getInstance(getActivity()).addToRequestQueue(request);

    }

    private void getQTSupportedRestaurants(final LocationListModel listModels) {
        final ProgressDialog dialog = new ProgressDialog(getActivity());
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

                        Log.i("QTRestaurantList", response.toString());

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

                            Intent dispensaryPageIntent = new Intent(getActivity(), RestaurantServices.class);
                            dispensaryPageIntent.putExtra("RestName", locationListModel.getRestaurantName());
                            dispensaryPageIntent.putExtra("RestAddress", locationListModel.getRestaurantAddress());
                            dispensaryPageIntent.putExtra("RestDistance", locationListModel.getRestaurantDistance());
                            dispensaryPageIntent.putExtra("RestChat", locationListModel.getRestaurantChatAvailable());
                            dispensaryPageIntent.putExtra("RestEWT", locationListModel.getRestaurantEWT());
                            dispensaryPageIntent.putExtra("RestPhone", locationListModel.getRestaurantPhone());
                            dispensaryPageIntent.putExtra("LocationId", locationListModel.getLocationID());
                            dispensaryPageIntent.putExtra("TenantId", locationListModel.getTenantID());
                            dispensaryPageIntent.putExtra("RestaurantLatitude", locationListModel.getLatitude());
                            dispensaryPageIntent.putExtra("RestaurantLongitude", locationListModel.getLongitude());
                            dispensaryPageIntent.putExtra("RestaurantImage", locationListModel.getRestaurantImage());
                            dispensaryPageIntent.putExtra("RestaurantMenuUrl", locationListModel.getRestaurantMenuUrl());
                            SpecificMenuSingleton.getInstance().setQtSupported(locationListModel.isQtSupported());
                            dispensaryPageIntent.putExtra("CurrentLatitude", currentLatitude);
                            dispensaryPageIntent.putExtra("CurrentLongitude", currentLongitude);
                            startActivity(dispensaryPageIntent);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error != null) {
                    if (error instanceof TimeoutError) {
                        Config.internetSlowError(getActivity());
                    } else if (error instanceof NoConnectionError) {
                        Config.internetSlowError(getActivity());
                    } else if (error instanceof ServerError) {
                        Config.serverError(getActivity());
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

        VolleySingleton.getInstance(getActivity()).addToRequestQueue(request);

    }
}
