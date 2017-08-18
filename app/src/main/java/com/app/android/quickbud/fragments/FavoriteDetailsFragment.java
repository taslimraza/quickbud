package com.app.android.quickbud.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.app.android.quickbud.R;
import com.app.android.quickbud.activities.OrderConfirmationActivity;
import com.app.android.quickbud.adapters.FavoriteItemDetailsAdapter;
import com.app.android.quickbud.modelClasses.CartItemModel;
import com.app.android.quickbud.modelClasses.FavoriteDetailsModel;
import com.app.android.quickbud.modelClasses.FavoriteListModel;
import com.app.android.quickbud.modelClasses.MenuChoicesModel;
import com.app.android.quickbud.network.VolleySingleton;
import com.app.android.quickbud.utils.Config;
import com.app.android.quickbud.utils.ExpandableListView;
import com.app.android.quickbud.utils.SpecificMenuSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavoriteDetailsFragment extends Fragment {

    FavoriteListModel orderHistoryListModel;
    FavoriteDetailsModel favoriteDetailsModel;
    ArrayList<CartItemModel> cartItemModels;
    private LinkedHashMap<String,ArrayList<MenuChoicesModel>> menuChoicesHashMap, menuChoices;
    ArrayList<MenuChoicesModel> menuChoicesModels;
    ArrayList<String> menuOptionsName;

    TextView restName, restAddress, orderNumber, totalItemQuantity, totalItemCost, billTotalCost, grandTotalCost;
    ExpandableListView itemListView;
    ImageView loadingImage;
    RelativeLayout favoriteDetailsLayout;
    Button reOrder, editOrder;

    public FavoriteDetailsFragment(){

    }

    public FavoriteDetailsFragment(FavoriteListModel orderHistoryListModel) {
        this.orderHistoryListModel = orderHistoryListModel;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorite_details, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        loadingImage = (ImageView) getActivity().findViewById(R.id.loading_image);
        restName = (TextView) getActivity().findViewById(R.id.restaurant_name);
        restAddress = (TextView) getActivity().findViewById(R.id.restaurant_address);
        orderNumber = (TextView) getActivity().findViewById(R.id.order_number);
        itemListView = (ExpandableListView) getActivity().findViewById(R.id.cart_list);
        totalItemQuantity = (TextView) getActivity().findViewById(R.id.item_total_quantity);
        totalItemCost = (TextView) getActivity().findViewById(R.id.item_total_price);
        billTotalCost = (TextView) getActivity().findViewById(R.id.bill_total_cost);
        grandTotalCost = (TextView) getActivity().findViewById(R.id.grand_total_cost);
        favoriteDetailsLayout = (RelativeLayout) getActivity().findViewById(R.id.favorite_details_layout);
        reOrder = (Button) getActivity().findViewById(R.id.reorder);
//        editOrder = (Button) getActivity().findViewById(R.id.edit_order);

        getOrderDetails();
    }

    private void getOrderDetails() {
        final SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_info", Context.MODE_PRIVATE);
        String patronId = sharedPreferences.getString("patron_id", null);
        loadingImage.setVisibility(View.VISIBLE);
        favoriteDetailsLayout.setVisibility(View.GONE);
        cartItemModels = new ArrayList<>();
        final String orderDetailsURL = "http://159.203.88.161/qt/api/order/order_detail/";

        JSONArray params = new JSONArray();
        JSONObject param = new JSONObject();
        try {
            param.put("patron_id",patronId);
            param.put("tenant_id",orderHistoryListModel.getTenantId());
            param.put("order_id",orderHistoryListModel.getOrderId());
            params.put(param);
        } catch (JSONException e) {

        }

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.POST, orderDetailsURL, params,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        // getting data from server and setting it in model class
                        for(int i=0; i<response.length(); i++){
                            try {
                                favoriteDetailsModel = new FavoriteDetailsModel();
                                JSONObject orderList = response.getJSONObject(i);
                                favoriteDetailsModel.setOrderId(orderList.getInt("order_id"));
                                JSONArray menuItems = orderList.getJSONArray("cart_items");
                                for(int j=0; j<menuItems.length(); j++){
                                    CartItemModel cartItemModel = new CartItemModel();
                                    JSONObject singleItem = menuItems.getJSONObject(j);
                                    cartItemModel.setItemName(singleItem.getString("menu_item_name"));
                                    cartItemModel.setItemId(singleItem.getInt("menu_item_id"));
                                    cartItemModel.setItemPrice(singleItem.getString("menu_item_price"));
                                    cartItemModel.setItemQuantity(singleItem.getString("quantity"));

                                    JSONArray menuOptions = singleItem.getJSONArray("menu_options");
                                    menuChoicesHashMap = new LinkedHashMap<>();
                                    for(int k=0; k<menuOptions.length(); k++){
                                        JSONObject menuOption = menuOptions.getJSONObject(k);
                                        String optionName = menuOption.getString("option_name");

                                        JSONArray menuChoices = menuOption.getJSONArray("menu_choices");
                                        menuChoicesModels = new ArrayList<>();
                                        for(int l=0; l<menuChoices.length(); l++){
                                            JSONObject menuChoice = menuChoices.getJSONObject(l);
                                            MenuChoicesModel menuChoicesModel = new MenuChoicesModel();
                                            menuChoicesModel.setMenuChoiceName(menuChoice.getString("choice_name"));
                                            menuChoicesModel.setMenuChoicePrice(menuChoice.getString("choice_price"));
                                            menuChoicesModel.setMenuChoiceId(menuChoice.getInt("choice_id"));
                                            menuChoicesModel.setMenuOptionId(menuOption.getInt("option_id"));
                                            menuChoicesModels.add(menuChoicesModel);
                                        }
                                        menuChoicesHashMap.put(optionName, menuChoicesModels);
                                    }
                                    cartItemModel.setMenuChoicesHashMap(menuChoicesHashMap);
                                    cartItemModels.add(cartItemModel);
                                }
                                favoriteDetailsModel.setCartItemModels(cartItemModels);

                                setViews();

                            } catch (JSONException e) {
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(request);
    }

    private void setViews(){
        float totalAmount = 0;
        loadingImage.setVisibility(View.GONE);
        favoriteDetailsLayout.setVisibility(View.VISIBLE);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(orderHistoryListModel.getLocationAddress());
        stringBuilder.append(", " + orderHistoryListModel.getLocationCity());
        stringBuilder.append(", " + orderHistoryListModel.getLocationState());
        stringBuilder.append(", " + orderHistoryListModel.getLocationZip());

        restName.setText(orderHistoryListModel.getLocationName());
        restAddress.setText(stringBuilder.toString());
        orderNumber.setText("Order Number: " + favoriteDetailsModel.getOrderId());

        FavoriteItemDetailsAdapter favoriteItemDetailsAdapter = new
                FavoriteItemDetailsAdapter(getActivity(), favoriteDetailsModel.getCartItemModels(), favoriteDetailsModel);
        itemListView.setAdapter(favoriteItemDetailsAdapter);

        for(int i = 0; i<favoriteDetailsModel.getCartItemModels().size(); i++){
            Double totalItemPrice = Double.parseDouble(cartItemModels.get(i).getItemPrice());
            initializeData(i);
            for(int j=0; j<menuOptionsName.size(); j++){
                for(int k=0; k<menuChoices.get(menuOptionsName.get(j)).size(); k++){
                    totalItemPrice += Double.parseDouble(menuChoices.get(menuOptionsName.get(j)).get(k).getMenuChoicePrice());
                }
            }
            totalAmount += totalItemPrice * Double.parseDouble(cartItemModels.get(i).getItemQuantity());
        }

        String cost = String.format("%.2f", totalAmount);
        totalItemCost.setText("$ " + cost);
        billTotalCost.setText("$ " + cost);
        grandTotalCost.setText("$ " + cost);

        reOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Config.bookingStatus = true;
                SpecificMenuSingleton specificMenuSingleton = SpecificMenuSingleton.getInstance();

                // checking whether user selected dine-in/take away or not
                // if user selected any of these, then only re-order

                if(specificMenuSingleton.getVisitId() != 0){
                    reOrderRequest(1);
                }else {
                    Snackbar.make(getActivity().findViewById(android.R.id.content),
                            "First select dine-in or take away!", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initializeData(int i) {
        menuOptionsName = new ArrayList<>();
        menuChoices = cartItemModels.get(i).getMenuChoicesHashMap();
        Set<String> menuOptions = menuChoices.keySet();
        for (String name : menuOptions) {
            menuOptionsName.add(name);
        }
    }

    private JSONObject reOrderPostData(int isFavorite) {
        final SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_info", Context.MODE_PRIVATE);
        String patronId = sharedPreferences.getString("patron_id", null);
        SpecificMenuSingleton menuSingleton = SpecificMenuSingleton.getInstance();
        cartItemModels = favoriteDetailsModel.getCartItemModels();
        JSONObject cartItems = new JSONObject();
        try {
            JSONArray itemsDetailsArray = new JSONArray();
            for (int i = 0; i < cartItemModels.size(); i++) {
                JSONObject itemDetail = new JSONObject();
                itemDetail.put("menu_item_id", cartItemModels.get(i).getItemId());
                itemDetail.put("quantity", cartItemModels.get(i).getItemQuantity());

                if (cartItemModels.get(i).getMenuChoicesHashMap() != null) {  // if menu options are there
                    initializeData(i);
                    JSONArray itemOptions = new JSONArray();
                    for (int j = 0; j < cartItemModels.get(i).getMenuChoicesHashMap().size(); j++) {
                        JSONObject itemOption = new JSONObject();

                        JSONArray itemChoices = new JSONArray();
                        for (int k = 0; k < cartItemModels.get(i).getMenuChoicesHashMap().get(menuOptionsName.get(j)).size(); k++) {
                                JSONObject itemChoice = new JSONObject();
                                itemChoice.put("menu_choice_id", cartItemModels.get(i).getMenuChoicesHashMap().get(menuOptionsName.get(j)).get(k).getMenuChoiceId());
                                itemChoice.put("quantity",1);
                                itemChoice.put("action","A");
                                itemOption.put("menu_option_id", cartItemModels.get(i).getMenuChoicesHashMap().get(menuOptionsName.get(j)).get(k).getMenuOptionId());
                                itemChoices.put(itemChoice);
                        }
                        itemOption.put("menu_choice", itemChoices);
                        itemOptions.put(itemOption);
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

    private void reOrderRequest(final int isFavorite){
        favoriteDetailsLayout.setVisibility(View.GONE);
        loadingImage.setVisibility(View.VISIBLE);

        final String url = "http://159.203.88.161/qt/api/cart/";

        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, reOrderPostData(isFavorite),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("Verification", response.toString());
                        try {
                            // Getting Order Id and setting it in singleton object
                            int orderId = response.getInt("order_id");
                            if(orderId != 0){
                                if(isFavorite == 1){
                                    favoriteDetailsLayout.setVisibility(View.VISIBLE);
                                    loadingImage.setVisibility(View.GONE);
                                }
                                startActivity(new Intent(getActivity(), OrderConfirmationActivity.class));
                                getActivity().finish();
                            }
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
