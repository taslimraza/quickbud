package com.app.android.quickbud.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.app.android.quickbud.R;
import com.app.android.quickbud.activities.AboutUsActivity;
import com.app.android.quickbud.activities.MenuActivity;
import com.app.android.quickbud.activities.OrderConfirmationActivity;
import com.app.android.quickbud.activities.OrderHistoryActivity;
import com.app.android.quickbud.activities.UserProfileActivity;
import com.app.android.quickbud.adapters.DrawerListAdapter;
import com.app.android.quickbud.network.VolleySingleton;
import com.app.android.quickbud.utils.Config;
import com.app.android.quickbud.utils.Globals;
import com.app.android.quickbud.utils.SpecificMenuSingleton;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class WaitTimeInfoFragment extends Fragment {

    TextView userName,userLocation;
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    ListView drawerList;
    ImageView menu, restImage;
    RelativeLayout cartContent;

    public WaitTimeInfoFragment() {
        getRestaurantLogo();
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_wait_time_info, container, false);
        toolbar= (Toolbar) view.findViewById(R.id.app_bar);
        Button finish= (Button) view.findViewById(R.id.book_table_finish);
        Button placeYourOrder= (Button) view.findViewById(R.id.place_your_order);

        userName= (TextView) view.findViewById(R.id.user_name);
        userLocation= (TextView) view.findViewById(R.id.user_location);
        drawerList = (ListView) view.findViewById(R.id.drawer_list);
        drawerLayout = (DrawerLayout) view.findViewById(R.id.drawer);
        cartContent= (RelativeLayout) view.findViewById(R.id.cart_content);
        menu = (ImageView) view.findViewById(R.id.menu_button);
        restImage = (ImageView) view.findViewById(R.id.rest_image);
        TextView restAddress = (TextView) view.findViewById(R.id.rest_address);
        TextView restName = (TextView) view.findViewById(R.id.rest_name);
        TextView patronName = (TextView) view.findViewById(R.id.customer_name);

        toolbar.setTitle("Pre-order");
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        drawerList.setAdapter(new DrawerListAdapter(getActivity()));

        SpecificMenuSingleton singleton = SpecificMenuSingleton.getInstance();
        restAddress.setText(singleton.getRestaurantAddress());
        restName.setText(singleton.getRestaurantName());

        SharedPreferences sharedPreference = getActivity().getSharedPreferences("user_info", Context.MODE_PRIVATE);
        String profileName = sharedPreference.getString("user_name", null);
        patronName.setText(profileName);

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        cartContent.setVisibility(View.GONE);
//        cartContent.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(getActivity(), OrderDetailsActivity.class));
//            }
//        });

        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Config.bookingStatus = true;
                WaitConformationFragment waitConformationFragment = new WaitConformationFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.pre_order, waitConformationFragment)
                        .addToBackStack(null).commit();
            }
        });

        placeYourOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Config.viewMenu = false;
                Config.bookingStatus = true;
                startActivity(new Intent(getActivity(), MenuActivity.class));
            }
        });

//         FOR USER PROFILE OPTIONS
        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 1:
                        // User Profile
                        startActivity(new Intent(getActivity(), UserProfileActivity.class));
                        drawerLayout.closeDrawers();
                        break;
                    case 2:
                        // User Favorites Orders List
                        startActivity(new Intent(getActivity(), OrderHistoryActivity.class));
                        drawerLayout.closeDrawers();
                        break;
                    case 3:
                        // Order Booking Status - Show user estimated wait time
                        Config.bookingStatus = false;
                        startActivity(new Intent(getActivity(), OrderConfirmationActivity.class));
                        drawerLayout.closeDrawers();
                        break;
                    case 4:
                        startActivity(new Intent(getActivity(), AboutUsActivity.class));
                        drawerLayout.closeDrawers();
                        break;
                }
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sharedPreference = getActivity().getSharedPreferences("user_info", Context.MODE_PRIVATE);
        String profileName = sharedPreference.getString("user_name", null);
        String userAddress = sharedPreference.getString("user_address", null);
        userName.setText(profileName);
        userLocation.setText(userAddress);
        userName.setTypeface(Globals.robotoBold);
        userLocation.setTypeface(Globals.robotoRegular);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                SeatSelectionFragment fragment=new SeatSelectionFragment();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.pre_order, fragment)
                        .commit();
        }
        return true;
    }

    private void getRestaurantLogo(){
        final SpecificMenuSingleton menuSingleton = SpecificMenuSingleton.getInstance();
        final String url = "http://159.203.88.161/qt/core/tenant/"+menuSingleton.getClickedRestaurant().getTenantID();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.i("RestaurantLogo", response.toString());
                            String imageUrl = response.getString("url");
                            menuSingleton.setImageUrl(imageUrl);
                            SpecificMenuSingleton menuSingleton = SpecificMenuSingleton.getInstance();
                            Glide.with(getActivity()).load(Config.QT_IMAGE + menuSingleton.getImageUrl())
                                    .crossFade(1000).into(restImage);

                        } catch (JSONException e) {

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        VolleySingleton.getInstance(getActivity()).addToRequestQueue(request);
    }

}
