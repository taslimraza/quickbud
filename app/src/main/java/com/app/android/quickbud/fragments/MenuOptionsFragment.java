package com.app.android.quickbud.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.app.android.quickbud.adapters.OptionsExpandableAdapter;
import com.app.android.quickbud.interfaces.ItemChoiceQuantityUpdate;
import com.app.android.quickbud.modelClasses.FavoriteMenuItem;
import com.app.android.quickbud.modelClasses.FavoriteModel;
import com.app.android.quickbud.modelClasses.MenuChoicesModel;
import com.app.android.quickbud.modelClasses.MenuItemModel;
import com.app.android.quickbud.modelClasses.MenuOptionModel;
import com.app.android.quickbud.network.VolleySingleton;
import com.app.android.quickbud.utils.CartSingleton;
import com.app.android.quickbud.utils.Config;
import com.app.android.quickbud.utils.FavoriteSingleton;
import com.app.android.quickbud.utils.SpecificMenuSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 */
public class MenuOptionsFragment extends Fragment implements ItemChoiceQuantityUpdate {
    ArrayList<MenuOptionModel> menuOptionModels;
    private ExpandableListView optionsListView;
    private Map<String, ArrayList<MenuChoicesModel>> menuChoicesHashMap, menuChoices;
    private Button addToCart;
    private String orderName, orderPrice;
    private int singleOptionPosition = 0;
    private ArrayList<MenuChoicesModel> menuChoicesArrayList, choicesArrayList;
    private MenuItemModel menuItemModel;
    private ArrayList<String> menuOptionsName;
    private TextView favoriteText, likesCount;
    private EditText specialRequest;
    private ImageView favoriteIcon;
    private ProgressBar loadingImage;
    private RelativeLayout menuOptionLayout;
    private boolean isFavorite = false;
    private FavoriteMenuItem favoriteMenuItem;
    private int favoriteId = 0;
    private OptionsExpandableAdapter menuOptionAdapter;


    public MenuOptionsFragment() {

    }

    public MenuOptionsFragment(MenuItemModel menuItemModel,
                               ArrayList<MenuOptionModel> menuOptionModels,
                               Map<String, ArrayList<MenuChoicesModel>> menuChoicesHashMap) {
        this.menuItemModel = menuItemModel;
        this.menuOptionModels = menuOptionModels;
        this.menuChoicesHashMap = menuChoicesHashMap;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_menu_options, container, false);
        favoriteIcon = (ImageView) view.findViewById(R.id.favorite_icon);
        favoriteText = (TextView) view.findViewById(R.id.favorite_text);
//        likesCount = (TextView) view.findViewById(R.id.likes_count);
        specialRequest = (EditText) view.findViewById(R.id.special_request);
        loadingImage = (ProgressBar) view.findViewById(R.id.loading_image);
        menuOptionLayout = (RelativeLayout) view.findViewById(R.id.menu_option_layout);

        Config.isMenuOptionClicked = false;

        isFavorite = compareFavorite();
        if (isFavorite) {
            favoriteIcon.setImageResource(R.mipmap.ic_favorite_selected);
        } else {
            favoriteIcon.setImageResource(R.mipmap.ic_favorite_unselected);
        }

        favoriteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                isFavorite = menuItemModel.isFavorite();
                if (!isFavorite) {
                    isFavorite = true;
                    postFavoriteItem();
//                    favoriteIcon.setImageResource(R.mipmap.favorite_green_image);
//                    favoriteText.setText("Added to favorites");
                } else {
//                    Toast.makeText(getActivity(), "Already added in favorites!", Toast.LENGTH_SHORT).show();
//                    if (menuItemModel.getFavoriteId() != 0) {
                    isFavorite = false;
                    removeFavoriteItem();
//                    }
//                    favoriteIcon.setImageResource(R.mipmap.favorite_green_icon);
//                    favoriteText.setText("Add to favorites");
                }
            }
        });

        favoriteText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                isFavorite = menuItemModel.isFavorite();
                if (!isFavorite) {
                    isFavorite = true;
                    postFavoriteItem();
//                    favoriteIcon.setImageResource(R.mipmap.favorite_green_image);
//                    favoriteText.setText("Added to favorites");
                } else {
//                    Toast.makeText(getActivity(), "Already added in favorites!", Toast.LENGTH_SHORT).show();
//                    if (menuItemModel.getFavoriteId() != 0) {
                    isFavorite = false;
                    removeFavoriteItem();
//                    }
//                    favoriteIcon.setImageResource(R.mipmap.favorite_green_icon);
//                    favoriteText.setText("Add to favorites");
                }
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        optionsListView = (ExpandableListView) getActivity().findViewById(R.id.options_list);
        addToCart = (Button) getActivity().findViewById(R.id.add_to_cart);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.app_bar);
//        ImageView homeBtn = (ImageView) getActivity().findViewById(R.id.menu_button);
//
//        homeBtn.setVisibility(View.INVISIBLE);

        // for android v6.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            addToCart.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.primary_color));
        }

        /*final LinkedHashMap<String, ArrayList<MenuChoicesModel>> */
        menuChoices = new LinkedHashMap<>();

        for (int i = 0; i < menuOptionModels.size(); i++) {
            ArrayList<MenuChoicesModel> menuChoicesArrayList = menuChoicesHashMap.get(menuOptionModels.get(i).getMenuOptionName());

            if (menuOptionModels.get(i).getIsMultiQuantity() == 1) {
                for (int j = 0; j < menuChoicesArrayList.size(); j++) {
                    final MenuChoicesModel menuChoicesModel = menuChoicesArrayList.get(j);
                    menuChoicesArrayList.get(j).setMenuChoiceQuantity(menuChoicesModel.isDefault());
                }
            } else {
                for (int j = 0; j < menuChoicesArrayList.size(); j++) {
                    final MenuChoicesModel menuChoicesModel = menuChoicesArrayList.get(j);
                    if (menuChoicesModel.isChoiceDefault() == 1) {
                        menuChoicesArrayList.get(j).setIsDefault(1);
                        if (menuOptionModels.get(i).isMultiSelect() == 1) {
                            menuChoicesArrayList.get(j).setIsCheckBoxChecked(true);
                        } else {
                            menuChoicesArrayList.get(j).setIsRadioButtonChecked(true);
                        }
                    } else if (menuChoicesModel.isChoiceDefault() == 0) {
                        menuChoicesArrayList.get(j).setIsDefault(0);
                        if (menuOptionModels.get(i).isMultiSelect() == 1) {
                            menuChoicesArrayList.get(j).setIsCheckBoxChecked(false);
                        } else {
                            menuChoicesArrayList.get(j).setIsRadioButtonChecked(false);
                        }
                    }
                }
            }
            menuChoices.put(menuOptionModels.get(i).getMenuOptionName(), menuChoicesArrayList);
        }

//        if (isFavorite) {
//
//            favoriteIcon.setImageResource(R.mipmap.favorite_green_image);
////            favoriteText.setText("Added to favorites");
//
//        } else {
//
//            favoriteIcon.setImageResource(R.mipmap.favorite_green_icon);
////            favoriteText.setText("Add to favorites");
//
//        }

//        likesCount.setText(String.valueOf(menuItemModel.getLikesCount()));

        menuOptionAdapter = new OptionsExpandableAdapter(getActivity(), menuOptionModels, menuChoices, this);
        optionsListView.setAdapter(menuOptionAdapter);
        for (int i = menuOptionModels.size() - 1; i >= 0; i--) {
            optionsListView.expandGroup(i, true);
        }

        toolbar.setTitle("Menu-item-options");

        initializeData();

        optionsListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                if (menuOptionModels.get(groupPosition).getIsMultiQuantity() == 1) {
                    return true;
                }

//                Button incrementQuantityBtn = (Button) v.findViewById(R.id.up_button);
//                incrementQuantityBtn.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Toast.makeText(getActivity(), "Button clicked", Toast.LENGTH_SHORT).show();
//                    }
//                });

                Config.isMenuOptionClicked = true;
                ArrayList<MenuChoicesModel> menuChoicesArrayList = menuChoices.get(menuOptionModels.get(groupPosition).getMenuOptionName());
                final MenuOptionModel menuOptionModel = menuOptionModels.get(groupPosition);
                final MenuChoicesModel menuChoicesModel = menuChoicesArrayList.get(childPosition);
                if (menuOptionModel.isMultiSelect() != 0) {
                    // if multi select option == checkbox
                    if (menuChoicesModel.isCheckBoxChecked()) {
                        menuChoicesArrayList.get(childPosition).setIsCheckBoxChecked(false);
                        menuChoicesArrayList.get(childPosition).setIsDefault(0);
                    } else {
                        menuChoicesArrayList.get(childPosition).setIsCheckBoxChecked(true);
                        menuChoicesArrayList.get(childPosition).setIsDefault(1);
                    }
//                    menuChoices.put(menuOptionModel.getMenuOptionName(), menuChoicesArrayList);
                    menuOptionAdapter.notifyDataSetChanged();

                } else {
                    // if single select option == radio button
                    if (menuChoicesModel.isDefault() == 0) {
                        for (MenuChoicesModel model : menuChoicesArrayList) {
                            if (model.isDefault() == 1) {
                                singleOptionPosition = menuChoicesArrayList.indexOf(model);
                                break;
                            }
                        }
                        if (childPosition == 0 && singleOptionPosition == 0) {
                            menuChoicesArrayList.get(childPosition).setIsRadioButtonChecked(true);
                            menuChoicesArrayList.get(childPosition).setIsDefault(1);
                        } else {
                            menuChoicesArrayList.get(childPosition).setIsRadioButtonChecked(true);
                            menuChoicesArrayList.get(singleOptionPosition).setIsRadioButtonChecked(false);
                            menuChoicesArrayList.get(childPosition).setIsDefault(1);
                            menuChoicesArrayList.get(singleOptionPosition).setIsDefault(0);
                        }
                    }
//                    menuChoices.put(menuOptionModel.getMenuOptionName(), menuChoicesArrayList);
                    menuOptionAdapter.notifyDataSetChanged();
                }

                return true;
            }
        });

        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuItemModel.setSpecialRequest(specialRequest.getText().toString());

                LinkedHashMap<String, ArrayList<MenuChoicesModel>> menuChoice = new LinkedHashMap<>();

                for (int i = 0; i < menuOptionModels.size(); i++) {
                    ArrayList<MenuChoicesModel> menuChoicesArrayList = new ArrayList<MenuChoicesModel>();
                    menuChoicesArrayList = menuChoices.get(menuOptionModels.get(i).getMenuOptionName());

                    for (int j = 0; j < menuChoicesArrayList.size(); j++) {
                        final MenuChoicesModel menuChoicesModel = menuChoicesArrayList.get(j);

                        if (menuOptionModels.get(i).isMultiSelect() == 1) {
                            if (menuChoicesModel.isCheckBoxChecked()) {
                                menuChoicesModel.setIsCheckBoxChecked(true);
                            } else {
                                menuChoicesModel.setIsCheckBoxChecked(false);
                            }
                        } else {
                            if (menuChoicesModel.isRadioButtonChecked()) {
                                menuChoicesModel.setIsRadioButtonChecked(true);
                            } else {
                                menuChoicesModel.setIsRadioButtonChecked(false);
                            }
                        }
                        menuChoicesModel.setMenuChoiceQuantity(menuChoicesModel.getMenuChoiceQuantity());
                    }
                    menuChoice.put(menuOptionModels.get(i).getMenuOptionName(), menuChoicesArrayList);
                }

                MenuItemModel itemModel = new MenuItemModel();
                itemModel.setMenuItemId(menuItemModel.getMenuItemId());
                itemModel.setMenuItemName(menuItemModel.getMenuItemName());
                itemModel.setMenuItemPrice(menuItemModel.getMenuItemPrice());
                itemModel.setSpecialRequest(menuItemModel.getSpecialRequest());

                CartSingleton cartSingleton = CartSingleton.getInstance();

                cartSingleton.addToCartWithOptions(itemModel.getSpecialRequest(), itemModel.getMenuItemName(),
                        itemModel.getMenuItemPrice(), itemModel.getMenuItemId(),
                        menuChoice, menuOptionModels);
                MenuItemFragment.updateCartCount();
                getActivity().getSupportFragmentManager().popBackStack();
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
    }

    private void initializeData() {
        menuOptionsName = new ArrayList<>();
        Set<String> menuOptions = menuChoicesHashMap.keySet();
        for (String name : menuOptions) {
            menuOptionsName.add(name);
        }
    }

    private void postFavoriteItem() {

        loadingImage.setVisibility(View.VISIBLE);
//        menuOptionLayout.setVisibility(View.GONE);

        String url = Config.QT_FAVORITE_URL;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, postFavoriteItemData(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("Favorite_item_response", response.toString());

                        favoriteMenuItem = new FavoriteMenuItem();
//                        int favoriteId = 0;
                        try {
                            favoriteId = response.getInt("favorite_id");
                            favoriteMenuItem.setFavoriteId(favoriteId);
                        } catch (JSONException e) {

                        }
                        loadingImage.setVisibility(View.GONE);
//                        menuOptionLayout.setVisibility(View.VISIBLE);
                        favoriteIcon.setImageResource(R.mipmap.ic_favorite_selected);
//                        favoriteText.setText("Added to favorites");
//                        menuItemModel.setLikesCount(menuItemModel.getLikesCount() + 1);
//                        likesCount.setText(String.valueOf(menuItemModel.getLikesCount()));

                        MenuItemModel itemModel = new MenuItemModel();
                        itemModel.setItemQuantity(menuItemModel.getItemQuantity());
                        itemModel.setMenuItemPrice(menuItemModel.getMenuItemPrice());
                        itemModel.setMenuItemDescription(menuItemModel.getMenuItemDescription());
                        itemModel.setMenuItemId(menuItemModel.getMenuItemId());
                        itemModel.setMenuItemImage(menuItemModel.getMenuItemImage());
                        itemModel.setMenuItemName(menuItemModel.getMenuItemName());

//                        Toast.makeText(getActivity(), itemModel.getMenuItemName() + " added to favorites!", Toast.LENGTH_SHORT).show();

                        itemModel.setFavorite(true);
                        itemModel.setFavoriteId(favoriteId);

                        LinkedHashMap<String, ArrayList<MenuChoicesModel>> favoriteChoices = new LinkedHashMap<>();

                        for (int i = 0; i < menuOptionsName.size(); i++) {

                            ArrayList<MenuChoicesModel> menuChoicesArrayList = menuChoicesHashMap.get(menuOptionsName.get(i));
                            final MenuOptionModel menuOptionModel = menuOptionModels.get(i);
                            ArrayList<MenuChoicesModel> menuChoicesModels = new ArrayList<>();

                            for (int j = 0; j < menuChoicesArrayList.size(); j++) {
                                if (menuChoicesArrayList.get(j).isCheckBoxChecked() || menuChoicesArrayList.get(j).isRadioButtonChecked()) {

                                    MenuChoicesModel menuChoicesModel = new MenuChoicesModel();
                                    menuChoicesModel = menuChoicesArrayList.get(j);
                                    if (menuOptionModels.get(i).isMultiSelect() == 1) {
                                        menuChoicesModel.setIsCheckBoxChecked(true);
                                    } else {
                                        menuChoicesModel.setIsRadioButtonChecked(true);
                                    }
                                    menuChoicesModels.add(menuChoicesModel);
                                }
                            }
                            favoriteChoices.put(menuOptionModel.getMenuOptionName(), menuChoicesModels);
                        }

                        FavoriteSingleton.getInstance().addToFavoriteWithOptions(itemModel, menuOptionModels, favoriteChoices);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingImage.setVisibility(View.GONE);
//                menuOptionLayout.setVisibility(View.VISIBLE);
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
            favoriteObject.put("tenant_id", menuSingleton.getClickedRestaurant().getTenantID());
            favoriteObject.put("location_id", menuSingleton.getClickedRestaurant().getLocationID());
            favoriteObject.put("patron_id", patronId);
            favoriteObject.put("menu_id", menuItemModel.getMenuItemId());

            JSONArray choicesArray = new JSONArray();
            initializeData();
            for (int j = 0; j < menuChoicesHashMap.size(); j++) {
                ArrayList<MenuChoicesModel> menuChoicesArrayList = menuChoicesHashMap.get(menuOptionsName.get(j));
                for (int i = 0; i < menuChoicesArrayList.size(); i++) {
                    JSONObject choiceObject = new JSONObject();
                    if (menuChoicesArrayList.get(i).isChoiceDefault() == 1 && menuChoicesArrayList.get(i).isDefault() == 1) {
                        choiceObject.put("choice_id", menuChoicesArrayList.get(i).getMenuChoiceId());
                        choiceObject.put("choice_name", menuChoicesArrayList.get(i).getMenuChoiceName());
                        choicesArray.put(choiceObject);
                    } else if (menuChoicesArrayList.get(i).isChoiceDefault() == 0 && menuChoicesArrayList.get(i).isDefault() == 1) {
                        choiceObject.put("choice_id", menuChoicesArrayList.get(i).getMenuChoiceId());
                        choiceObject.put("choice_name", menuChoicesArrayList.get(i).getMenuChoiceName());
                        choicesArray.put(choiceObject);
                    }
                }
            }
            favoriteObject.put("choices", choicesArray);
            favoriteArray.put(favoriteObject);

            favoritesObject.put("favorite_items", favoriteArray);
            favoritesObject.put("like", true);

        } catch (JSONException e) {

        }

        return favoritesObject;
    }

    private void removeFavoriteItem() {

        loadingImage.setVisibility(View.VISIBLE);
//        menuOptionLayout.setVisibility(View.GONE);

        String url = Config.QT_FAVORITE_URL + favoriteId + "/delete";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("Favorite_delete", response.toString());

                        loadingImage.setVisibility(View.GONE);
//                        menuOptionLayout.setVisibility(View.VISIBLE);
                        favoriteIcon.setImageResource(R.mipmap.ic_favorite_unselected);
//                        favoriteText.setText("Add to favorites");
//                        menuItemModel.setLikesCount(menuItemModel.getLikesCount() - 1);
//                        likesCount.setText(String.valueOf(menuItemModel.getLikesCount()));

                        menuItemModel.setFavorite(false);
                        FavoriteSingleton.getInstance().deleteFavorite(favoriteId);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingImage.setVisibility(View.GONE);
//                menuOptionLayout.setVisibility(View.VISIBLE);
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

    private void checkFavorite() {

        ArrayList<FavoriteModel> favoriteModels = FavoriteSingleton.getInstance().getFavoriteItems();

        for (int i = 0; i < favoriteModels.size(); i++) {

            if (favoriteModels.get(i).getMenuItemModel().getMenuItemId() == menuItemModel.getMenuItemId()) {


            }
        }
    }

    private boolean compareFavorite() {

        ArrayList<FavoriteModel> favoriteModels = FavoriteSingleton.getInstance().getFavoriteItems();
        int found = 0;
        int checked = 0;
        boolean isFavorite = false;

        for (int i = 0; i < favoriteModels.size(); i++) {

            if (favoriteModels.get(i).getMenuItemModel().getMenuItemId() == menuItemModel.getMenuItemId()) {
                found = 0;
                checked = 0;
                Map<String, ArrayList<MenuChoicesModel>> favoriteChoiceModel = favoriteModels.get(i).getMenuChoicesHashMap();

                ArrayList<String> optionsName = getOptionName(favoriteChoiceModel);

                initializeData();

                if (optionsName != null) {
                    for (int k = 0; k < menuChoicesHashMap.size(); k++) {

                        ArrayList<MenuChoicesModel> menuChoicesModels = menuChoicesHashMap.get(optionsName.get(k));

                        for (int l = 0; l < menuChoicesModels.size(); l++) {

                            if (menuChoicesModels.get(l).isCheckBoxChecked() || menuChoicesModels.get(l).isRadioButtonChecked()) {


                                for (int j = 0; j < favoriteChoiceModel.size(); j++) {

                                    ArrayList<MenuChoicesModel> choicesModels = favoriteChoiceModel.get(optionsName.get(j));

                                    for (int m = 0; m < choicesModels.size(); m++) {

                                        if (choicesModels.get(m).getMenuChoiceId() == menuChoicesModels.get(l).getMenuChoiceId()) {
                                            if ((menuChoicesModels.get(l).isCheckBoxChecked() && (menuChoicesModels.get(l).isDefault() == 1)) || (menuChoicesModels.get(l).isRadioButtonChecked() && (menuChoicesModels.get(l).isDefault() == 1))) {
                                                found++;
                                                break;
                                            }
                                        }
                                    }
                                }
                                checked++;
                            }
                        }
                    }

                    if (checked > 0) {
                        if (found == getFavoriteChoicesSize(favoriteChoiceModel) && found == checked) {
                            isFavorite = true;
                            favoriteId = favoriteModels.get(i).getMenuItemModel().getFavoriteId();
                            return isFavorite;
                        }
                    } else {
                        isFavorite = true;
                        favoriteId = favoriteModels.get(i).getMenuItemModel().getFavoriteId();
                    }
                } else {
                    isFavorite = true;
                    favoriteId = favoriteModels.get(i).getMenuItemModel().getFavoriteId();
                }
            }
        }

        return isFavorite;
    }

    private int getFavoriteChoicesSize(Map<String, ArrayList<MenuChoicesModel>> favoriteChoiceModel) {

        int totalSize = 0;
        ArrayList<String> optionsName = getOptionName(favoriteChoiceModel);

        for (int i = 0; i < favoriteChoiceModel.size(); i++) {
            totalSize += favoriteChoiceModel.get(optionsName.get(i)).size();
        }

        return totalSize;
    }

    private ArrayList<String> getOptionName(Map<String, ArrayList<MenuChoicesModel>> favoriteChoiceModel) {

        ArrayList<String> favoriteOptionName = new ArrayList<>();
        if (favoriteChoiceModel != null && favoriteChoiceModel.size() > 0) {
            Set<String> menuOptions = favoriteChoiceModel.keySet();
            for (String name : menuOptions) {
                favoriteOptionName.add(name);
            }
            return favoriteOptionName;
        } else {
            return null;
        }
    }

    @Override
    public void updateMenu(int quantity, int groupPosition, int childPosition) {
        menuChoices.get(menuOptionModels.get(groupPosition).getMenuOptionName()).get(childPosition).setMenuChoiceQuantity(quantity);
        if (menuOptionModels.get(groupPosition).isMultiSelect() == 0) {
            for (int i = 0; i < menuChoices.get(menuOptionModels.get(groupPosition).getMenuOptionName()).size(); i++) {
                if (i == childPosition) {
                    menuChoices.get(menuOptionModels.get(groupPosition).getMenuOptionName()).get(i).setMenuChoiceQuantity(quantity);
                } else {
                    menuChoices.get(menuOptionModels.get(groupPosition).getMenuOptionName()).get(i).setMenuChoiceQuantity(0);
                }
            }
            menuOptionAdapter.notifyDataSetChanged();
        }
    }
}
