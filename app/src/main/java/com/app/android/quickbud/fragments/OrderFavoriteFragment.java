package com.app.android.quickbud.fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.app.android.quickbud.adapters.FavoriteMenuRecyclerAdapter;
import com.app.android.quickbud.modelClasses.FavoriteMenuItem;
import com.app.android.quickbud.modelClasses.FavoriteModel;
import com.app.android.quickbud.modelClasses.MenuChoicesModel;
import com.app.android.quickbud.modelClasses.MenuDetailsModel;
import com.app.android.quickbud.modelClasses.MenuItemModel;
import com.app.android.quickbud.modelClasses.MenuOptionModel;
import com.app.android.quickbud.modelClasses.OrderMenuModel;
import com.app.android.quickbud.network.VolleySingleton;
import com.app.android.quickbud.utils.CartSingleton;
import com.app.android.quickbud.utils.Config;
import com.app.android.quickbud.utils.FavoriteSingleton;
import com.app.android.quickbud.utils.RecyclerViewItemClickListener;
import com.app.android.quickbud.utils.SpecificMenuSingleton;
import com.app.android.quickbud.utils.ViewPagerInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrderFavoriteFragment extends Fragment implements ViewPagerInterface {

    private static RecyclerView favoriteRecyclerView;
    private static TextView favoriteEmptyTxt;
    private static ImageView loadingImage;
    private ArrayList<OrderMenuModel> menuModel;
    public static ArrayList<FavoriteMenuItem> favoriteMenuItems;
    private ArrayList<FavoriteModel> favoriteModels;
    private Context context;
    private ArrayList<String> optionsName;
    private HashMap<String, ArrayList<MenuChoicesModel>> favoritesChoices;
    private int choiceCount;
    private ArrayList<MenuOptionModel> menuOptionModels;
    private ArrayList<MenuChoicesModel> menuChoices;
    private static FavoriteMenuRecyclerAdapter adapter;
    private int clickedPosition;
    private ArrayList<MenuItemModel> menuItemModels;
    private static TextView cartItem;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_order_favorite, container, false);
        favoriteRecyclerView = (RecyclerView) view.findViewById(R.id.favorite_recyclerView);
        favoriteEmptyTxt = (TextView) view.findViewById(R.id.no_favorite_item);
        loadingImage = (ImageView) view.findViewById(R.id.loading_image);

        favoriteRecyclerView.addOnItemTouchListener(
                new RecyclerViewItemClickListener(getActivity(),
                        new RecyclerViewItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                showDialog(position, favoriteMenuItems);
                            }
                        }));

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        cartItem = (TextView) getActivity().findViewById(R.id.cart_items);
        if (MenuItemFragment.getCountItem() > 0) {
            cartItem.setVisibility(View.VISIBLE);
            cartItem.setText(MenuItemFragment.getCountItem().toString());
        } else {
            cartItem.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        update();
    }

    private void setupRecyclerViewAdapter(final Context context, RecyclerView favoriteRecyclerView) {
//        menuModel = new ArrayList<>();
        if (favoriteModels.size() > 0) {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 2);
            if (favoriteRecyclerView != null) {
                favoriteRecyclerView.setLayoutManager(gridLayoutManager);
                adapter = new FavoriteMenuRecyclerAdapter(context, favoriteModels);
                favoriteRecyclerView.setAdapter(adapter);
            }
            favoriteEmptyTxt.setVisibility(View.GONE);
            favoriteRecyclerView.setVisibility(View.VISIBLE);
        } else {
            favoriteEmptyTxt.setVisibility(View.VISIBLE);
            favoriteRecyclerView.setVisibility(View.GONE);
        }

    }

    private void getFavoriteMenuItems(Context context) {

        menuItemModels = new ArrayList<>();
        loadingImage.setVisibility(View.VISIBLE);
        favoriteRecyclerView.setVisibility(View.GONE);

        favoriteModels = FavoriteSingleton.getInstance().getFavoriteItems();
        favoriteMenuItems = OrderMenuFragment.favoriteMenuItems;
        menuItemModels = MenuDetailsModel.getInstance().getMenuItemModels();

        loadingImage.setVisibility(View.GONE);
        favoriteRecyclerView.setVisibility(View.VISIBLE);
        setupRecyclerViewAdapter(context, favoriteRecyclerView);

//        SpecificMenuSingleton specificMenuSingleton = SpecificMenuSingleton.getInstance();
//        int locationId = specificMenuSingleton.getClickedRestaurant().getLocationID();
//        int tenantId = specificMenuSingleton.getClickedRestaurant().getTenantID();
//        SharedPreferences sharedPreferences = context.getSharedPreferences("user_info", Context.MODE_PRIVATE);
//        String patronId = sharedPreferences.getString("patron_id", null);
//
//        String url = "http://159.203.88.161/qt/api/favorite/?patron_id=" + patronId + "&tenant_id=" +
//                tenantId + "&location_id=" + locationId;
//
//        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url,
//                new Response.Listener<JSONArray>() {
//                    @Override
//                    public void onResponse(JSONArray response) {
//                        Log.i("Favorite_item", response.toString());
//                        favoriteMenuItems = new ArrayList<>();
//                        FavoriteSingleton.getInstance().clearFavoriteItems();
//                        JsonParser jsonParser = new JsonParser();
//                        favoriteMenuItems = jsonParser.getFavoriteMenuParsed(response.toString());
//
//                        favoriteModels = FavoriteSingleton.getInstance().getFavoriteItems();
//
//                        menuItemModels = MenuDetailsModel.getInstance().getMenuItemModels();
//                        for (int i = 0; i < favoriteMenuItems.size(); i++) {
//                            for (int j = 0; j < menuItemModels.size(); j++) {
//                                if (favoriteMenuItems.get(i).getMenuItemId() == menuItemModels.get(j).getMenuItemId()) {
//
//                                    menuItemModels.get(j).setFavorite(true);
//                                    FavoriteSingleton.getInstance().addToFavorite(menuItemModels.get(j));
//
//                                    favoritesChoices = new LinkedHashMap<>();
////                                    initializeData();
//                                    if (favoriteMenuItems.get(i).getMenuItemChoices().size() > 0) {
//                                        // if menu item choices are present
//
//                                        menuOptionModels = menuItemModels.get(j).getMenuOptionModels();
//                                        for (int l = 0; l < menuOptionModels.size(); l++) {
//                                            menuChoices = menuOptionModels.get(l).getMenuChoicesModels();
//                                            ArrayList<MenuChoicesModel> choicesModels = new ArrayList<>();
//                                            for (int m = 0; m < menuChoices.size(); m++) {
//                                                // loop till each options choices
//                                                for (int k = 0; k < favoriteMenuItems.get(i).getMenuItemChoices().size(); k++) {
//                                                    if (favoriteMenuItems.get(i).getMenuItemChoices().get(k).getChoiceId() ==
//                                                            menuChoices.get(m).getMenuChoiceId()) {
//                                                        menuChoices.get(m).setIsDefault(1);
//                                                        choicesModels.add(menuChoices.get(m));
//                                                    }
//                                                }
//                                            }
//                                            favoritesChoices.put(menuOptionModels.get(l).getMenuOptionName(), choicesModels);
//                                        }
//                                    }
//                                }
//                            }
//                            favoriteModels.get(i).setMenuChoicesHashMap(favoritesChoices);
//                        }
//
////                        favoriteModels = OrderMenuFragment.favoriteModels;
//
//                        loadingImage.setVisibility(View.GONE);
//                        favoriteRecyclerView.setVisibility(View.VISIBLE);
//                        setupRecyclerViewAdapter(context, favoriteRecyclerView);
//
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//            }
//        });
//
//        request.setRetryPolicy(new DefaultRetryPolicy(
//                30 * 1000,  // 30 sec - timeout
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                2  // 30+30*2 = 90sec - total timeout
//        ));
//
//        VolleySingleton.getInstance(getActivity()).addToRequestQueue(request);

    }

    @Override
    public void onPauseFragment(Context context) {
    }

    @Override
    public void onResumeFragment(Context context) {
        getFavoriteMenuItems(context);
//        ConnectionDetector detector = new ConnectionDetector(context);
//
////        if (favoriteModels == null) {
//            if (detector.isConnectedToInternet()) {
//                getFavoriteMenuItems();
//            } else {
//                detector.internetError();
//            }

//        } else {
//            setupRecyclerViewAdapter(context, favoriteRecyclerView);
//        }
    }

    public static void update() {
        CartSingleton cartSingleton = CartSingleton.getInstance();
        Integer itemCount = cartSingleton.getItemCount();
        MenuItemFragment.updateCartCount();
        if (itemCount == 0) {
            cartItem.setVisibility(View.GONE);
        } else {
            cartItem.setVisibility(View.VISIBLE);
            cartItem.setText(itemCount.toString());
        }
    }

    public void showDialog(final int position, final ArrayList<FavoriteMenuItem> favoriteMenuItems) {

        final MenuItemModel menuItemModel = FavoriteSingleton.getInstance().getFavoriteItems().get(position).getMenuItemModel();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(menuItemModel.getMenuItemName());
        builder.setPositiveButton("Add to cart", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                favoriteModels = FavoriteSingleton.getInstance().getFavoriteItems();
                CartSingleton cartSingleton = CartSingleton.getInstance();
                SpecificMenuSingleton menuSingleton = SpecificMenuSingleton.getInstance();
                if (cartSingleton.getItemCount() > 0) {
                    if (cartSingleton.getCartItem().get(0).getTenantId() == menuSingleton.getClickedRestaurant().getTenantID()
                            && cartSingleton.getCartItem().get(0).getLocationId() == menuSingleton.getClickedRestaurant().getLocationID()) {

                        if ((favoriteModels.get(position).getMenuChoicesHashMap() == null) || (favoriteModels.get(position).getMenuChoicesHashMap().size() == 0)) {
                            CartSingleton.getInstance().addToCart(menuItemModel.getMenuItemName(), menuItemModel.getMenuItemPrice(),
                                    1, menuItemModel.getMenuItemId(), getActivity());
                        } else {
                            CartSingleton.getInstance().addToCartWithOptions(null, menuItemModel.getMenuItemName(),
                                    menuItemModel.getMenuItemPrice(), menuItemModel.getMenuItemId(), favoriteModels.get(position).getMenuChoicesHashMap(), favoriteModels.get(position).getMenuOptionModels());
                        }

                    }else {
                        showRestaurantVaryError(position, menuItemModel);
                    }
                }else {
                    if ((favoriteModels.get(position).getMenuChoicesHashMap() == null) || (favoriteModels.get(position).getMenuChoicesHashMap().size() == 0)) {
                        CartSingleton.getInstance().addToCart(menuItemModel.getMenuItemName(), menuItemModel.getMenuItemPrice(),
                                1, menuItemModel.getMenuItemId(), getActivity());
                    } else {
                        CartSingleton.getInstance().addToCartWithOptions(null, menuItemModel.getMenuItemName(),
                                menuItemModel.getMenuItemPrice(), menuItemModel.getMenuItemId(), favoriteModels.get(position).getMenuChoicesHashMap(), favoriteModels.get(position).getMenuOptionModels());
                    }
                }
                update();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Remove", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                favoriteMenuItems.remove(clickedPosition);
//                FavoriteSingleton.getInstance().deleteFavoriteItem(clickedPosition);
//                setupRecyclerViewAdapter(context, favoriteRecyclerView);
                removeFavoriteItem(menuItemModel.getMenuItemName(), menuItemModel.getFavoriteId(), menuItemModel.getMenuItemId());
//                if (favoriteMenuItems.size() > position){
//                    favoriteMenuItems.remove(position);
//                }
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

//    private void initializeData() {
//        optionsName = new ArrayList<>();
//        Set<String> menuOptions = menuChoices.keySet();
//        for (String name : menuOptions) {
//            optionsName.add(name);
//        }
//    }

    private void removeFavoriteItem(final String itemName, int favoriteId, final int menuItemId) {

        loadingImage.setVisibility(View.VISIBLE);
        favoriteRecyclerView.setVisibility(View.GONE);

        String url = Config.QT_FAVORITE_URL + favoriteId + "/delete";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("Favorite_delete", response.toString());

                        int favoriteId = 0;

                        try {
                            favoriteId = response.getInt("id");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        loadingImage.setVisibility(View.GONE);
                        favoriteRecyclerView.setVisibility(View.VISIBLE);

                        FavoriteSingleton.getInstance().deleteFavorite(favoriteId);
                        adapter.notifyDataSetChanged();
//                        setupRecyclerViewAdapter(context, favoriteRecyclerView);

                        menuItemModels = new ArrayList<>();
                        menuItemModels = MenuDetailsModel.getInstance().getMenuItemModels();
                        for (int i = 0; i < menuItemModels.size(); i++) {
                            if (menuItemModels.get(i).getMenuItemId() == menuItemId){

                                menuItemModels.get(i).setFavorite(false);
//                                menuItemModels.get(i).setLikesCount(menuItemModels.get(i).getLikesCount() - 1);
                            }
                        }
                        update();

                        Toast.makeText(getActivity(), itemName + " removed from favorites!", Toast.LENGTH_SHORT).show();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingImage.setVisibility(View.GONE);
                favoriteRecyclerView.setVisibility(View.VISIBLE);
                if(error instanceof TimeoutError){
                    Config.internetSlowError(getActivity());
                }else if (error instanceof NoConnectionError){
                    Config.internetSlowError(getActivity());
                }else if (error instanceof ServerError){
                    Config.serverError(getActivity());
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

    private void showRestaurantVaryError(final int position, final MenuItemModel menuItemModel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Items already in cart!");
        builder.setMessage("Your cart contains items from other restaurant. Would you like to reset your cart before browsing this restaurant?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                CartSingleton.getInstance().clearCart();

                if ((favoriteModels.get(position).getMenuChoicesHashMap() == null) || (favoriteModels.get(position).getMenuChoicesHashMap().size() == 0)) {
                    CartSingleton.getInstance().addToCart(menuItemModel.getMenuItemName(), menuItemModel.getMenuItemPrice(),
                            1, menuItemModel.getMenuItemId(), getActivity());
                } else {
                    CartSingleton.getInstance().addToCartWithOptions(null, menuItemModel.getMenuItemName(),
                            menuItemModel.getMenuItemPrice(), menuItemModel.getMenuItemId(), favoriteModels.get(position).getMenuChoicesHashMap(), favoriteModels.get(position).getMenuOptionModels());
                }

                MenuItemFragment.updateCartCount();
                update();
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
}