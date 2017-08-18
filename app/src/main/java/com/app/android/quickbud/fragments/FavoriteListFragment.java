package com.app.android.quickbud.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.app.android.quickbud.R;
import com.app.android.quickbud.adapters.OrderHistoryAdapter;
import com.app.android.quickbud.modelClasses.FavoriteListModel;
import com.app.android.quickbud.network.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavoriteListFragment extends Fragment {

    private RecyclerView orderHistoryList;
    private OrderHistoryAdapter orderHistoryAdapter;
    private ArrayList<FavoriteListModel> orderHistoryModels;
    private ImageView loadingImage;
    private RelativeLayout emptyFavoriteLayout;

    public FavoriteListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorite_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        orderHistoryList = (RecyclerView) getActivity().findViewById(R.id.order_history_list);
        loadingImage = (ImageView) getActivity().findViewById(R.id.loading_image);
        emptyFavoriteLayout = (RelativeLayout) getActivity().findViewById(R.id.favorite_empty_layout);

        getOrderList();
    }

    private void getOrderList() {
        loadingImage.setVisibility(View.VISIBLE);
        final SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_info", Context.MODE_PRIVATE);
        String patronId = sharedPreferences.getString("patron_id", null);
        orderHistoryModels = new ArrayList<>();
        final String orderListURL = "http://159.203.88.161/qt/api/order/favorite/?patron_id="+patronId;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, orderListURL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.i("FavoriteList",response.toString());

                        for (int i = 0; i < response.length(); i++) {
                            try {
                                loadingImage.setVisibility(View.GONE);
                                JSONObject favoriteList = response.getJSONObject(i);

                                JSONArray orderList = favoriteList.getJSONArray("orders");
                                for (int j=0; j<orderList.length(); j++){
                                    JSONObject favoriteOrder = orderList.getJSONObject(j);
                                    FavoriteListModel orderHistoryModel = new FavoriteListModel();
                                    orderHistoryModel.setPatronId(favoriteOrder.getInt("patron_id"));
                                    orderHistoryModel.setTenantId(favoriteOrder.getInt("tenant_id"));
                                    orderHistoryModel.setVisitId(favoriteOrder.getInt("visit_id"));
                                    orderHistoryModel.setOrderId(favoriteOrder.getInt("order_id"));
                                    orderHistoryModel.setTenantName(favoriteOrder.getString("tenant_name"));
                                    orderHistoryModel.setLocationName(favoriteOrder.getString("location_name"));
                                    orderHistoryModel.setLocationId(favoriteOrder.getInt("location_id"));
                                    orderHistoryModel.setLocationAddress(favoriteOrder.getString("address_line"));
                                    orderHistoryModel.setLocationCity(favoriteOrder.getString("location_city"));
                                    orderHistoryModel.setLocationState(favoriteOrder.getString("location_state"));
                                    orderHistoryModel.setLocationZip(favoriteOrder.getString("zip"));
                                    orderHistoryModels.add(orderHistoryModel);
                                }

                                if(orderHistoryModels.size() > 0){

                                }
                                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                                orderHistoryList.setLayoutManager(layoutManager);
                                orderHistoryAdapter = new OrderHistoryAdapter(getActivity(), orderHistoryModels, loadingImage, orderHistoryList);
                                orderHistoryList.setAdapter(orderHistoryAdapter);

                            } catch (JSONException e) {
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingImage.setVisibility(View.GONE);

            }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(5000,
                3, 1f));

        VolleySingleton.getInstance(getActivity()).addToRequestQueue(request);
    }
}
