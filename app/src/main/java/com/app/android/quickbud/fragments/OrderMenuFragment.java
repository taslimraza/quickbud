package com.app.android.quickbud.fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.app.android.quickbud.R;
import com.app.android.quickbud.adapters.OrderMenuRecyclerAdapter;
import com.app.android.quickbud.modelClasses.FavoriteMenuItem;
import com.app.android.quickbud.modelClasses.FavoriteModel;
import com.app.android.quickbud.modelClasses.MenuChoicesModel;
import com.app.android.quickbud.modelClasses.MenuDetailsModel;
import com.app.android.quickbud.modelClasses.MenuItemModel;
import com.app.android.quickbud.modelClasses.MenuOptionModel;
import com.app.android.quickbud.modelClasses.OrderMenuModel;
import com.app.android.quickbud.modelClasses.TopBudsListModel;
import com.app.android.quickbud.network.VolleySingleton;
import com.app.android.quickbud.utils.Config;
import com.app.android.quickbud.utils.ConnectionDetector;
import com.app.android.quickbud.utils.FavoriteSingleton;
import com.app.android.quickbud.utils.JsonParser;
import com.app.android.quickbud.utils.RecyclerViewItemClickListener;
import com.app.android.quickbud.utils.SpecificMenuSingleton;
import com.app.android.quickbud.utils.ViewPagerInterface;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrderMenuFragment extends Fragment implements ViewPagerInterface {

    private RecyclerView menuRecyclerView;
    private ImageView loadingImage;
    private TextView loadingText;
    private MenuDetailsModel menuModel;
    private MenuDetailsModel menuDetailsModel;
    private Toolbar toolbar;
    public static ArrayList<FavoriteMenuItem> favoriteMenuItems;
    private ArrayList<TopBudsListModel> topBudsListModels;
    public ArrayList<FavoriteModel> favoriteModels;
    private ArrayList<MenuOptionModel> menuOptionModels;
    private ArrayList<MenuChoicesModel> menuChoices;
    private ArrayList<MenuItemModel> menuItemModels;
    private LinkedHashMap<String, ArrayList<MenuChoicesModel>> favoritesChoices;
    private final String menuSectionURL = Config.QT_MENU_URL;
    private Context context;
    //{
    //    "location_id":1,
    //    "tenant_id":1
    //}
    ArrayList<OrderMenuModel> menuModels;
    String[] categoryTitles;
    String[] categoryCount;
    JsonParser parser = new JsonParser();
    SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_order_menu, container, false);
        menuRecyclerView = (RecyclerView) view.findViewById(R.id.menu_recyclerView);
        loadingImage = (ImageView) view.findViewById(R.id.loading_image);
        loadingText = (TextView) view.findViewById(R.id.loading_text);
        loadingImage.setVisibility(View.VISIBLE);
        loadingText.setVisibility(View.VISIBLE);
        menuRecyclerView.setVisibility(View.GONE);

        sharedPreferences = getActivity().getSharedPreferences("user_info", Context.MODE_PRIVATE);

        menuRecyclerView.addOnItemTouchListener(
                new RecyclerViewItemClickListener(getActivity(),
                        new RecyclerViewItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
//                                Log.d("Position", "" + position);
//                                Log.d("Item Name", "" + menuModel.getOrderMenuModels().get(position).getMenuSectionName());
                                if (menuModel.getOrderMenuModels().get(position).getMenuItemCount() > 0) {
                                    MenuItemFragment menuItemFragment = new MenuItemFragment(menuModel);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("itemName", menuModel.getOrderMenuModels().get(position).getMenuSectionName());
//                                bundle.putString("itemName",menuModels.get(position).getMenuSectionName());
                                    bundle.putInt("itemPosition", position);
                                    menuItemFragment.setArguments(bundle);
                                    getActivity().getSupportFragmentManager().beginTransaction()
                                            .replace(R.id.menu_container, menuItemFragment)
                                            .addToBackStack(null).commit();
                                } else {
                                    Toast.makeText(getActivity(), "No menu items!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }));
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        toolbar = (Toolbar) getActivity().findViewById(R.id.app_bar);
        toolbar.setTitle("Menu");

        TextView cartItem = (TextView) getActivity().findViewById(R.id.cart_items);
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
        if (menuModel == null) {
            ConnectionDetector detector = new ConnectionDetector(getActivity());
            if (detector.isConnectedToInternet()) {
                getMenuDetails();
            } else {
                detector.internetError();
            }
            if (FavoriteSingleton.getInstance() != null) {
                FavoriteSingleton.getInstance().clearFavoriteItems();
            }
        } else {
            menuDetailsModel = MenuDetailsModel.getInstance();
            menuDetailsModel = menuModel.getMenuDetailsModel();
            setMenuDetails();
        }
    }

    private void setMenuDetails() {
        loadingImage.setVisibility(View.GONE);
        loadingText.setVisibility(View.GONE);
        menuRecyclerView.setVisibility(View.VISIBLE);
        if (menuDetailsModel.getOrderMenuModels() != null) {
            ArrayList<OrderMenuModel> orderMenuModels = menuDetailsModel.getOrderMenuModels();
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
            menuRecyclerView.setLayoutManager(gridLayoutManager);
            OrderMenuRecyclerAdapter adapter = new OrderMenuRecyclerAdapter(
                    getActivity(), orderMenuModels);
            menuRecyclerView.setAdapter(adapter);
        } else {
            Toast.makeText(getActivity(), "Menu Coming Soon", Toast.LENGTH_SHORT).show();
            getActivity().finish();
//            EmptyMenuFragment noMenuItemFragment = new EmptyMenuFragment();
//            getActivity().getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.menu_container, noMenuItemFragment)
//                    .commit();
        }
    }

    private void getMenuDetails() {
        SpecificMenuSingleton specificMenuSingleton = SpecificMenuSingleton.getInstance();
        int locationId = specificMenuSingleton.getClickedRestaurant().getLocationID();
        int tenantId = specificMenuSingleton.getClickedRestaurant().getTenantID();
        HashMap<String, Integer> params = new HashMap<>();
        params.put("location_id", locationId);
        params.put("tenant_id", tenantId);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, menuSectionURL, new JSONObject(params), new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("MenuList", response.toString());
                        loadingImage.setVisibility(View.GONE);
                        loadingText.setVisibility(View.GONE);
                        menuRecyclerView.setVisibility(View.VISIBLE);

                        if (response.has("reason")){
                            Toast.makeText(getActivity(), "Menu Coming Soon", Toast.LENGTH_SHORT).show();
                            getActivity().finish();
//                            EmptyMenuFragment noMenuItemFragment = new EmptyMenuFragment();
//                            getActivity().getSupportFragmentManager().beginTransaction()
//                                    .replace(R.id.menu_container, noMenuItemFragment)
//                                    .commit();
                        }else {
                            menuModel = parser.getMenuParsed(response.toString());
                            menuModel.setMenuDetailsModel(menuModel);

                            if (menuModel.getOrderMenuModels() != null && menuModel.getOrderMenuModels().size()>0) {
                                ArrayList<OrderMenuModel> orderMenuModels = menuModel.getOrderMenuModels();
                                GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
                                menuRecyclerView.setLayoutManager(gridLayoutManager);
                                OrderMenuRecyclerAdapter adapter = new OrderMenuRecyclerAdapter(
                                        getActivity(), orderMenuModels);
                                menuRecyclerView.setAdapter(adapter);
                                getFavoriteMenuItem();
                            } else {
                                Toast.makeText(getActivity(), "Menu Coming Soon", Toast.LENGTH_SHORT).show();
                                getActivity().finish();
//                                EmptyMenuFragment noMenuItemFragment = new EmptyMenuFragment();
//                                getActivity().getSupportFragmentManager().beginTransaction()
//                                        .replace(R.id.menu_container, noMenuItemFragment)
//                                        .commit();
                            }
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        loadingImage.setVisibility(View.GONE);
                        loadingText.setVisibility(View.GONE);
                        menuRecyclerView.setVisibility(View.VISIBLE);

                        if (error != null) {
                            if (error instanceof TimeoutError) {
                                internetSlowError();
//                            Toast.makeText(getActivity(), "Time Out...", Toast.LENGTH_SHORT).show();
                            } else if (error instanceof NoConnectionError) {
                                Config.internetSlowError(getActivity());
//                                Toast.makeText(getActivity(), "No Connection!", Toast.LENGTH_SHORT).show();
                            } else if (error instanceof ServerError) {
                                Config.serverError(getActivity());
//                                Toast.makeText(getActivity(), "Server Error!", Toast.LENGTH_SHORT).show();
                            }
                        }
//                        internetSlowError();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("QTAuthorization", Config.SESSION_TOKEN_ID);
                return headers;
            }
        };

        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                Config.TIMEOUT,  // 25 sec - timeout
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(jsObjRequest);
    }

    private void internetSlowError() {
        if (getActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Your internet seems to be slow...");
            builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    getMenuDetails();
                    dialog.dismiss();
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

    @Override
    public void onPauseFragment(Context context) {
        this.context = context;
    }

    @Override
    public void onResumeFragment(Context context) {
        this.context = context;
    }

    private void getFavoriteMenuItem() {

        final SpecificMenuSingleton specificMenuSingleton = SpecificMenuSingleton.getInstance();
        int locationId = specificMenuSingleton.getClickedRestaurant().getLocationID();
        int tenantId = specificMenuSingleton.getClickedRestaurant().getTenantID();

        String patronId = sharedPreferences.getString("patron_id", null);

        String url = Config.QT_FAVORITE_URL + "?patron_id=" + patronId + "&tenant_id=" +
                tenantId + "&location_id=" + locationId;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.i("Favorite_item", response.toString());
                        favoriteMenuItems = new ArrayList<>();
                        FavoriteSingleton.getInstance().clearFavoriteItems();
                        JsonParser jsonParser = new JsonParser();
                        favoriteMenuItems = jsonParser.getFavoriteMenuParsed(response.toString());

                        menuItemModels = MenuDetailsModel.getInstance().getMenuItemModels();
                        for (int i = 0; i < favoriteMenuItems.size(); i++) {
                            for (int j = 0; j < menuItemModels.size(); j++) {
                                if (favoriteMenuItems.get(i).getMenuItemId() == menuItemModels.get(j).getMenuItemId()) {

                                    menuItemModels.get(j).setFavorite(true);
                                    menuItemModels.get(j).setFavoriteId(favoriteMenuItems.get(i).getFavoriteId());

                                    favoritesChoices = new LinkedHashMap<>();
//                                    initializeData();
                                    if (favoriteMenuItems.get(i).getMenuItemChoices().size() > 0) {
                                        // if menu item choices are present

                                        menuOptionModels = menuItemModels.get(j).getMenuOptionModels();
                                        for (int l = 0; l < menuOptionModels.size(); l++) {
                                            menuChoices = menuOptionModels.get(l).getMenuChoicesModels();
                                            ArrayList<MenuChoicesModel> choicesModels = new ArrayList<>();
                                            for (int m = 0; m < menuChoices.size(); m++) {
                                                // loop till each options choices
                                                for (int k = 0; k < favoriteMenuItems.get(i).getMenuItemChoices().size(); k++) {
                                                    if (favoriteMenuItems.get(i).getMenuItemChoices().get(k).getChoiceId() ==
                                                            menuChoices.get(m).getMenuChoiceId()) {
                                                        MenuChoicesModel menuChoicesModel = new MenuChoicesModel();
                                                        menuChoicesModel = menuChoices.get(m);
                                                        if (menuOptionModels.get(l).isMultiSelect() == 1) {
                                                            menuChoicesModel.setIsCheckBoxChecked(true);
                                                        } else {
                                                            menuChoicesModel.setIsRadioButtonChecked(true);
                                                        }
                                                        choicesModels.add(menuChoicesModel);
                                                    }
                                                }
                                            }
                                            favoritesChoices.put(menuOptionModels.get(l).getMenuOptionName(), choicesModels);
                                        }
                                        FavoriteSingleton.getInstance().addToFavoriteWithOptions(menuItemModels.get(j), menuOptionModels, favoritesChoices);
                                    } else {

                                        FavoriteSingleton.getInstance().addToFavorite(menuItemModels.get(j));

                                    }
                                }
                            }
//                            favoriteModels = FavoriteSingleton.getInstance().getFavoriteItems();
//                            favoriteModels.get(i).setMenuChoicesHashMap(favoritesChoices);
                        }

//                        loadingImage.setVisibility(View.GONE);
//                        favoriteRecyclerView.setVisibility(View.VISIBLE);
//                        setupRecyclerViewAdapter(context, favoriteRecyclerView);

                        getTopBudsList();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error != null) {
                    if (error instanceof TimeoutError) {
                        internetSlowError();
                    } else if (error instanceof NoConnectionError) {
                        Config.internetSlowError(getActivity());
                    } else if (error instanceof ServerError) {
                        Config.serverError(getActivity());
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

        VolleySingleton.getInstance(getActivity()).addToRequestQueue(request);

    }

    private void getTopBudsList() {

        final SpecificMenuSingleton specificMenuSingleton = SpecificMenuSingleton.getInstance();
        int locationId = specificMenuSingleton.getClickedRestaurant().getLocationID();
        int tenantId = specificMenuSingleton.getClickedRestaurant().getTenantID();

        String patronId = sharedPreferences.getString("patron_id", null);

        String url = Config.QB_TOP_BUDS_LIST + "?patron_id=" + patronId;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("Top Buds List", response.toString());
                        topBudsListModels = new ArrayList<>();
                        JsonParser jsonParser = new JsonParser();
                        topBudsListModels = jsonParser.topBudsListParser(response);

                        menuItemModels = MenuDetailsModel.getInstance().getMenuItemModels();
                        for (int i = 0; i < topBudsListModels.size(); i++) {
                            for (int k=0; k<topBudsListModels.get(i).getTopBudsDispensaryModels().size(); k++){
                                for (int j = 0; j < menuItemModels.size(); j++) {
                                    if (topBudsListModels.get(i).getTopBudsDispensaryModels().get(k).getMenuId() == menuItemModels.get(j).getMenuItemId()) {
                                        if (topBudsListModels.get(i).getTopBudsDispensaryModels().get(k).getTenantId() ==
                                                SpecificMenuSingleton.getInstance().getClickedRestaurant().getTenantID()){
                                            if (topBudsListModels.get(i).getTopBudsDispensaryModels().get(k).isLiked()) {
                                                menuItemModels.get(j).setTopBudLiked(true);
                                            }
                                        }
//                                        menuItemModels.get(j).setFavoriteId(favoriteMenuItems.get(i).getFavoriteId());

//                                        favoritesChoices = new LinkedHashMap<>();
//                                    initializeData();
//                                        if (favoriteMenuItems.get(i).getMenuItemChoices().size() > 0) {
//                                            // if menu item choices are present
//
//                                            menuOptionModels = menuItemModels.get(j).getMenuOptionModels();
//                                            for (int l = 0; l < menuOptionModels.size(); l++) {
//                                                menuChoices = menuOptionModels.get(l).getMenuChoicesModels();
//                                                ArrayList<MenuChoicesModel> choicesModels = new ArrayList<>();
//                                                for (int m = 0; m < menuChoices.size(); m++) {
//                                                    // loop till each options choices
//                                                    for (int k = 0; k < favoriteMenuItems.get(i).getMenuItemChoices().size(); k++) {
//                                                        if (favoriteMenuItems.get(i).getMenuItemChoices().get(k).getChoiceId() ==
//                                                                menuChoices.get(m).getMenuChoiceId()) {
//                                                            MenuChoicesModel menuChoicesModel = new MenuChoicesModel();
//                                                            menuChoicesModel = menuChoices.get(m);
//                                                            if (menuOptionModels.get(l).isMultiSelect() == 1) {
//                                                                menuChoicesModel.setIsCheckBoxChecked(true);
//                                                            } else {
//                                                                menuChoicesModel.setIsRadioButtonChecked(true);
//                                                            }
//                                                            choicesModels.add(menuChoicesModel);
//                                                        }
//                                                    }
//                                                }
//                                                favoritesChoices.put(menuOptionModels.get(l).getMenuOptionName(), choicesModels);
//                                            }
//                                            FavoriteSingleton.getInstance().addToFavoriteWithOptions(menuItemModels.get(j), menuOptionModels, favoritesChoices);
//                                        } else {
//
//                                            FavoriteSingleton.getInstance().addToFavorite(menuItemModels.get(j));
//
//                                        }
                                    }
                                }
                            }
//                            favoriteModels = FavoriteSingleton.getInstance().getFavoriteItems();
//                            favoriteModels.get(i).setMenuChoicesHashMap(favoritesChoices);
                        }

//                        loadingImage.setVisibility(View.GONE);
//                        favoriteRecyclerView.setVisibility(View.VISIBLE);
//                        setupRecyclerViewAdapter(context, favoriteRecyclerView);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error != null) {
                    if (error instanceof TimeoutError) {
                        internetSlowError();
                    } else if (error instanceof NoConnectionError) {
                        Config.internetSlowError(getActivity());
                    } else if (error instanceof ServerError) {
                        Config.serverError(getActivity());
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

        VolleySingleton.getInstance(getActivity()).addToRequestQueue(request);

    }
}