package com.app.android.quickbud.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.app.android.quickbud.fragments.MenuItemFragment;
import com.app.android.quickbud.fragments.MenuOptionsFragment;
import com.app.android.quickbud.interfaces.DialogActionInterface;
import com.app.android.quickbud.modelClasses.CartItemModel;
import com.app.android.quickbud.modelClasses.FavoriteMenuItem;
import com.app.android.quickbud.modelClasses.LocationListModel;
import com.app.android.quickbud.modelClasses.MenuChoicesModel;
import com.app.android.quickbud.modelClasses.MenuDetailsModel;
import com.app.android.quickbud.modelClasses.MenuItemModel;
import com.app.android.quickbud.modelClasses.MenuOptionModel;
import com.app.android.quickbud.network.VolleySingleton;
import com.app.android.quickbud.utils.CartSingleton;
import com.app.android.quickbud.utils.Config;
import com.app.android.quickbud.utils.FavoriteSingleton;
import com.app.android.quickbud.utils.MenuItemLayoutManager;
import com.app.android.quickbud.utils.SpecificMenuSingleton;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;


public class MenuItemRecyclerAdapter extends
        RecyclerView.Adapter<MenuItemRecyclerAdapter.RecyclerViewHolder>
        implements DialogActionInterface {

    private Context mContext;
    private ArrayList<MenuItemModel> menuModel;
    private Integer itemCount;
    private int orderSizePosition;
    private ArrayList<MenuOptionModel> menuOptionModels, optionModels;
    private ArrayList<MenuChoicesModel> menuChoicesModels;
    private Map<String, ArrayList<MenuChoicesModel>> menuChoicesHashMap, menuChoices;
    private MenuDetailsModel menuDetailsModel;
    private CartSingleton cartSingleton;
    private CartItemModel cartItemModel;
    private ArrayList<CartItemModel> cartItemModels = new ArrayList<>();
    MenuItemFragment menuItemFragment;
    private int itemPosition;
    private int optionCount = 0;
    private int choiceCount = 0;
    private int count = 0;
    private ArrayList<Map<String, ArrayList<MenuChoicesModel>>> menuChoicesArrayList, menuChoicesList;
    private ArrayList<String> menuOptionsName;
    private SpecificMenuSingleton menuSingleton;
    private boolean isMenuOption = false, isFavorite = false, isLiked = false;
    private FavoriteMenuItem favoriteMenuItem;
    private MenuItemLayoutManager linearLayoutManager;


    public MenuItemRecyclerAdapter(Context mContext, ArrayList<MenuItemModel> menuItemModels,
                                   MenuDetailsModel menuDetailsModel, int itemPosition,
                                   ArrayList<Map<String, ArrayList<MenuChoicesModel>>> menuChoicesArrayList,
                                   ArrayList<MenuOptionModel> menuOptionModels, MenuItemLayoutManager linearLayoutManager) {
        this.mContext = mContext;
        this.menuModel = menuItemModels;
        this.menuDetailsModel = menuDetailsModel;
        this.itemPosition = itemPosition;
        this.menuChoicesList = menuChoicesArrayList;
        this.menuOptionModels = menuOptionModels;
        this.linearLayoutManager = linearLayoutManager;
        menuSingleton = SpecificMenuSingleton.getInstance();
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        setupData();
        View view = LayoutInflater.from(mContext).inflate(R.layout.menu_item_row, parent, false);
        cartSingleton = CartSingleton.getInstance();
        view.setMinimumWidth(parent.getMeasuredWidth());

        menuChoicesArrayList = new ArrayList<>();
        menuChoicesArrayList.addAll(menuChoicesList);

        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {

        holder.categoryTitle.setText(Html.fromHtml(menuModel.get(position).getMenuItemName()));

        if (menuModel.get(position).getMenuItemDescription() != null) {
            holder.categoryDescription.setText(menuModel.get(position).getMenuItemDescription());
        }
        Glide.with(mContext).load(menuModel.get(position).getMenuItemImage()).placeholder(R.mipmap.menu_placeholder)
                .fitCenter().crossFade(1000).into(holder.categoryImage);

        for (int i = 0; i < cartSingleton.getCartItem().size(); i++) {
            if (cartSingleton.getCartItem().get(i).getItemId() == menuModel.get(position).getMenuItemId()) {
//                String num = cartSingleton.getCartItem().get(i).getItemQuantity();
                menuModel.get(position).setItemQuantity(Integer.parseInt(cartSingleton.getCartItem().get(i).getItemQuantity()));
            }
        }

        holder.itemQuantity.setText(menuModel.get(position).getItemQuantity().toString());

        if (!menuModel.get(position).getMenuItemPrice().equals("0.00")) {
            holder.categoryCount.setText("$ " + Html.fromHtml(menuModel.get(position).getMenuItemPrice()));
        } else {
            holder.categoryCount.setText("");
        }

        initializeData(position);
        menuChoicesHashMap = new LinkedHashMap<>();
        menuChoicesHashMap.putAll(menuChoicesArrayList.get(position));

        isFavorite = menuModel.get(position).isFavorite();

        if (menuModel.get(position).isFavorite()) {
            holder.favoriteIcon.setImageResource(R.mipmap.ic_favorite_selected);
//            holder.favoriteText.setText("Added to favorites");
        } else {
            holder.favoriteIcon.setImageResource(R.mipmap.ic_favorite_unselected);
//            holder.favoriteText.setText("Add to favorites");
        }

//        setUpData();
    }

    @Override
    public void onViewAttachedToWindow(RecyclerViewHolder holder) {
        itemPosition = holder.getLayoutPosition();
        initializeData(itemPosition);
        count = 0;
        optionModels = new ArrayList<>();
        for (int i = 0; i < menuOptionModels.size(); i++) {
            if (count < menuOptionsName.size()) {
                if (menuOptionModels.get(i).getMenuOptionName().equals(menuOptionsName.get(count))) {
                    optionModels.add(menuOptionModels.get(i));
                    count++;
                }
            }
        }

        if (menuModel.get(holder.getAdapterPosition()).isTopBudLiked()){
            isLiked = true;
            holder.likeIcon.setImageResource(R.mipmap.favorite_green_image);
        }else {
            isLiked = false;
            holder.likeIcon.setImageResource(R.mipmap.favorite_green_icon);
        }
        holder.likesCount.setText(String.valueOf(menuModel.get(holder.getAdapterPosition()).getLikesCount()));

        holder.thcData.setText("THC - " + menuModel.get(holder.getAdapterPosition()).getThc() + "%");
        holder.cbdData.setText("CBD - " + menuModel.get(holder.getAdapterPosition()).getCbd() + "%");

        System.out.println("LOG: option model - " + optionModels.size());

        if (Config.viewMenu) {
            holder.menuItemOptions.setVisibility(View.GONE);
            holder.addToCartLayout.setVisibility(View.GONE);
            holder.favoriteIcon.setVisibility(View.GONE);
            holder.favoriteText.setVisibility(View.GONE);
            holder.favoriteText.setVisibility(View.VISIBLE);
            holder.favoriteIcon.setVisibility(View.VISIBLE);
            holder.likesCount.setVisibility(View.VISIBLE);
//            holder.likesCount.setText(String.valueOf(menuModel.get(holder.getAdapterPosition()).getLikesCount()));
//
//            if (menuModel.get(itemPosition).isFavorite()) {
//                holder.favoriteIcon.setImageResource(R.mipmap.favorite_green_image);
//                holder.favoriteText.setText("Added to favorites");
//            } else {
//                holder.favoriteIcon.setImageResource(R.mipmap.favorite_green_icon);
//                holder.favoriteText.setText("Add to favorites");
//            }

        } else {
            if (optionModels.size() > 0) {
                holder.menuItemOptions.setVisibility(View.VISIBLE);
                holder.addToCartLayout.setVisibility(View.GONE);
                holder.favoriteIcon.setVisibility(View.GONE);
                holder.favoriteText.setVisibility(View.GONE);
            } else {
                holder.addToCartLayout.setVisibility(View.VISIBLE);
                holder.menuItemOptions.setVisibility(View.GONE);
                holder.favoriteText.setVisibility(View.VISIBLE);
                holder.favoriteIcon.setVisibility(View.VISIBLE);

//                if (menuModel.get(itemPosition).isFavorite()) {
//                    holder.favoriteIcon.setImageResource(R.mipmap.ic_favorite_selected);
////                    holder.favoriteText.setText("Added to favorites");
//                } else {
//                    holder.favoriteIcon.setImageResource(R.mipmap.ic_favorite_unselected);
////                    holder.favoriteText.setText("Add to favorites");
//                }
            }
        }
        super.onViewAttachedToWindow(holder);
    }

    @Override
    public int getItemCount() {
        return menuModel.size();
    }

    @Override
    public void onDialogConfirmed() {
        Log.d("onDialogDismissed", "OK");

    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private ImageView categoryImage;
        private ImageView favoriteIcon, likeIcon;
        private TextView categoryTitle;
        private TextView categoryDescription;
        private TextView categoryCount;
        private TextView itemQuantity;
        private TextView favoriteText, thcData, cbdData, likesCount, likeTxt;
        private Button addToCart, removeFromCart, menuItemOptions;
        private LinearLayout addToCartLayout, menuItemRootLayout;
        private RelativeLayout menuItemLayout;
        private ProgressBar loading;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            categoryImage = (ImageView) itemView.findViewById(R.id.category_image);
            categoryTitle = (TextView) itemView.findViewById(R.id.category_title);
            categoryDescription = (TextView) itemView.findViewById(R.id.category_description);
            categoryCount = (TextView) itemView.findViewById(R.id.category_count);
            addToCart = (Button) itemView.findViewById(R.id.up_buttton);
            removeFromCart = (Button) itemView.findViewById(R.id.down_button);
            itemQuantity = (TextView) itemView.findViewById(R.id.item_quantity);
            menuItemOptions = (Button) itemView.findViewById(R.id.option_button);
            addToCartLayout = (LinearLayout) itemView.findViewById(R.id.add_to_cart_layout);
            favoriteIcon = (ImageView) itemView.findViewById(R.id.favorite_icon);
            favoriteText = (TextView) itemView.findViewById(R.id.favorite_text);
            loading = (ProgressBar) itemView.findViewById(R.id.loading);
            menuItemLayout = (RelativeLayout) itemView.findViewById(R.id.row_layout);
            menuItemRootLayout = (LinearLayout) itemView.findViewById(R.id.menu_item_root_layout);
            thcData = (TextView) itemView.findViewById(R.id.thc_data);
            cbdData = (TextView) itemView.findViewById(R.id.cbd_data);
            likesCount = (TextView) itemView.findViewById(R.id.likes_count);
            likeTxt = (TextView) itemView.findViewById(R.id.like_text);
            likeIcon = (ImageView) itemView.findViewById(R.id.like_icon);
            addToCart.setOnClickListener(this);
            removeFromCart.setOnClickListener(this);
            menuItemOptions.setOnClickListener(this);
            favoriteIcon.setOnClickListener(this);
            favoriteText.setOnClickListener(this);
            likeIcon.setOnClickListener(this);
            likeTxt.setOnClickListener(this);
            likesCount.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int id = v.getId();
            menuItemFragment = new MenuItemFragment();
            switch (id) {
                case R.id.up_buttton:
                    itemCount = menuModel.get(getLayoutPosition()).getItemQuantity();
                    if (itemCount < 100) { // u can add till 99 items
                        if (cartSingleton.getItemCount() > 0) {
                            if (cartSingleton.getCartItem().get(0).getTenantId() == menuSingleton.getClickedRestaurant().getTenantID()
                                    && cartSingleton.getCartItem().get(0).getLocationId() == menuSingleton.getClickedRestaurant().getLocationID()) {
                                itemCount++;
                                menuModel.get(getLayoutPosition()).setItemQuantity(itemCount);
                                notifyItemChanged(getLayoutPosition());
                                cartSingleton.addToCart(menuModel.get(getLayoutPosition()).getMenuItemName(),
                                        menuModel.get(getLayoutPosition()).getMenuItemPrice(),
                                        menuModel.get(getLayoutPosition()).getItemQuantity(),
                                        menuModel.get(getLayoutPosition()).getMenuItemId(), mContext);
                                MenuItemFragment.updateCartCount();
                            } else {
                                showRestaurantVaryError();
                            }
                        } else {
                            itemCount++;
                            menuModel.get(getLayoutPosition()).setItemQuantity(itemCount);
                            notifyItemChanged(getLayoutPosition());
                            cartSingleton.addToCart(menuModel.get(getLayoutPosition()).getMenuItemName(),
                                    menuModel.get(getLayoutPosition()).getMenuItemPrice(),
                                    menuModel.get(getLayoutPosition()).getItemQuantity(),
                                    menuModel.get(getLayoutPosition()).getMenuItemId(), mContext);
                            MenuItemFragment.updateCartCount();
                        }
                    }
                    break;
                case R.id.down_button:
                    itemCount = menuModel.get(getLayoutPosition()).getItemQuantity();
                    if (itemCount > 0) {
                        itemCount--;
                        menuModel.get(getLayoutPosition()).setItemQuantity(itemCount);
                        notifyItemChanged(getLayoutPosition());
                        cartSingleton.removeFromCart(menuModel.get(getLayoutPosition()).getMenuItemName(),
                                menuModel.get(getLayoutPosition()).getMenuItemPrice(),
                                menuModel.get(getLayoutPosition()).getItemQuantity());
                        MenuItemFragment.updateCartCount();
                    }
                    break;
                case R.id.option_button:
                    orderSizePosition = -1;
                    isMenuOption = true;
                    if (cartSingleton.getItemCount() > 0) {
                        if (cartSingleton.getCartItem().get(0).getTenantId() == menuSingleton.getClickedRestaurant().getTenantID()
                                && cartSingleton.getCartItem().get(0).getLocationId() == menuSingleton.getClickedRestaurant().getLocationID()) {

                            getMenuOptions(menuModel.get(getAdapterPosition()));

                        } else {
                            showRestaurantVaryError();
                        }
                    } else {
                        getMenuOptions(menuModel.get(getAdapterPosition()));
                    }
//                    handleOrderDialog(menuModel.get(getLayoutPosition()).getMenuItemName(), menuModel.get(getLayoutPosition()).getMenuItemPrice(), menuOptionModels);
                    break;

                case R.id.favorite_icon:
                case R.id.favorite_text:
                    isFavorite = menuModel.get(getLayoutPosition()).isFavorite();
                    if (!isFavorite) {
                        postFavoriteItem(favoriteIcon, favoriteText, loading, menuItemLayout, menuItemRootLayout, likesCount);
//                        favoriteIcon.setImageResource(R.mipmap.favorite_green_image);
//                        favoriteText.setText("Added to favorites");
                    } else {
                        if (menuModel.get(getLayoutPosition()).getFavoriteId() != 0) {
                            removeFavoriteItem(menuModel.get(getLayoutPosition()).getFavoriteId(), favoriteIcon, favoriteText,
                                    loading, menuItemLayout, menuItemRootLayout , likesCount);
                        }
//                        favoriteIcon.setImageResource(R.mipmap.favorite_green_icon);
//                        favoriteText.setText("Add to favorites");
                    }
                    break;

                case R.id.like_text:
                case R.id.like_icon:
                case R.id.likes_count:
                    if (isLiked){
                        removeTopBud(likeIcon, likeTxt, likesCount, loading, menuItemLayout, menuItemRootLayout, likesCount);
                    }else {
                        postTopBud(likeIcon, likeTxt, likesCount, loading, menuItemLayout, menuItemRootLayout, likesCount);
                    }
                    break;
            }
        }
    }

    private void setUpData() {
        ArrayList<MenuChoicesModel> choicesModels = new ArrayList<>();
        for (int i = 0; i < menuOptionModels.size(); i++) {
            menuOptionModels.get(i).setMenuOptionName(menuOptionModels.get(i).getMenuOptionName());
        }
    }

    public void getMenuOptions(MenuItemModel menuItemModel) {
//        CartItemModel cartItemModel = new CartItemModel();
//        cartItemModel.setItemName(menuItemModel.getMenuItemName());
//        cartItemModel.setItemPrice(menuItemModel.getMenuItemPrice());
//        cartItemModel.setItemId(menuItemModel.getMenuItemId());

        LinkedHashMap<String, ArrayList<MenuChoicesModel>> menuChoices = new LinkedHashMap<>();
        menuChoices.putAll(menuChoicesArrayList.get(itemPosition));

//        LinkedHashMap<String, ArrayList<MenuChoicesModel>> menuChoice = new LinkedHashMap<>();
//        setUpData();
//        for (int i = 0; i < menuOptionModels.size(); i++) {
//            ArrayList<MenuChoicesModel> menuChoicesArrayList = new ArrayList<MenuChoicesModel>();
//            menuChoicesArrayList = menuChoices.get(menuOptionModels.get(i).getMenuOptionName());
//
//            for (int j = 0; j < menuChoicesArrayList.size(); j++) {
//                final MenuChoicesModel menuChoicesModel = menuChoicesArrayList.get(j);
//
//                if (menuOptionModels.get(i).isMultiSelect() == 1){
//                    if (menuChoicesModel.isCheckBoxChecked()){
//                        menuChoicesModel.setIsCheckBoxChecked(true);
//                    }else {
//                        menuChoicesModel.setIsCheckBoxChecked(false);
//                    }
//                }else {
//                    if (menuChoicesModel.isRadioButtonChecked()){
//                        menuChoicesModel.setIsRadioButtonChecked(true);
//                    }else {
//                        menuChoicesModel.setIsRadioButtonChecked(false);
//                    }
//                }
//            }
//            menuChoice.put(menuOptionModels.get(i).getMenuOptionName(), menuChoicesArrayList);
//        }

        MenuOptionsFragment menuOptionsFragment = new MenuOptionsFragment(menuItemModel, optionModels, menuChoices);
//        menuChoice.clear();
        ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().
                replace(R.id.menu_container, menuOptionsFragment).
                addToBackStack(null).commit();
    }

    private void initializeData(int i) {
        menuOptionsName = new ArrayList<>();
        Set<String> menuOptions = menuChoicesArrayList.get(i).keySet();
        for (String name : menuOptions) {
            menuOptionsName.add(name);
        }
    }

    private void initializeData() {
        menuOptionsName = new ArrayList<>();
        Set<String> menuOptions = menuChoicesHashMap.keySet();
        for (String name : menuOptions) {
            menuOptionsName.add(name);
        }
    }

    private void showRestaurantVaryError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Items already in cart!");
        builder.setMessage("Your cart contains items from other restaurant. Would you like to reset your cart before browsing this restaurant?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cartSingleton.clearCart();
                MenuItemFragment.updateCartCount();
                if (!isMenuOption) {
                    itemCount++;
                    menuModel.get(itemPosition).setItemQuantity(itemCount);
                    notifyItemChanged(itemPosition);
                    cartSingleton.addToCart(menuModel.get(itemPosition).getMenuItemName(),
                            menuModel.get(itemPosition).getMenuItemPrice(),
                            menuModel.get(itemPosition).getItemQuantity(),
                            menuModel.get(itemPosition).getMenuItemId(), mContext);
                    MenuItemFragment.updateCartCount();
                } else {
                    getMenuOptions(menuModel.get(itemPosition));
                }

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

    private void postFavoriteItem(final ImageView image, final TextView text, final ProgressBar loadingImage,
                                  final RelativeLayout menuItemLayout, final LinearLayout menuItemRootLayout, final TextView likeCount) {

        linearLayoutManager.setScrolledEnabled(false);
        text.setEnabled(false);
        image.setEnabled(false);
        loadingImage.setVisibility(View.VISIBLE);

        String url = Config.QT_FAVORITE_URL;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, postFavoriteItemData(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("Favorite_item_response", response.toString());

                        int favoriteId = 0;
                        favoriteMenuItem = new FavoriteMenuItem();
                        try {
                            favoriteId = response.getInt("favorite_id");
                            favoriteMenuItem.setFavoriteId(favoriteId);
                        } catch (JSONException e) {

                        }

                        loadingImage.setVisibility(View.GONE);

                        image.setImageResource(R.mipmap.ic_favorite_selected);
                        image.setEnabled(true);
                        text.setEnabled(true);
                        linearLayoutManager.setScrolledEnabled(true);
//                        text.setText("Added to favorites");
//                        menuModel.get(itemPosition).setLikesCount(menuModel.get(itemPosition).getLikesCount() + 1);
//                        likeCount.setText(String.valueOf(menuModel.get(itemPosition).getLikesCount()));
                        menuModel.get(itemPosition).setFavorite(true);
                        menuModel.get(itemPosition).setFavoriteId(favoriteId);

                        FavoriteSingleton.getInstance().addToFavorite(menuModel.get(itemPosition));

//                        postTopBud(image,text, loadingImage,menuItemLayout, menuItemRootLayout, likeCount);


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingImage.setVisibility(View.GONE);
//                menuItemLayout.setVisibility(View.VISIBLE);
//                menuItemRootLayout.setBackgroundColor(Color.parseColor("#000000"));
                if (error != null) {
                    if (error instanceof TimeoutError) {
                        Config.internetSlowError(mContext);
                    } else if (error instanceof NoConnectionError) {
                        Config.internetSlowError(mContext);
                    } else if (error instanceof ServerError) {
                        Config.serverError(mContext);
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

        VolleySingleton.getInstance(mContext).addToRequestQueue(request);
    }

    private void postTopBud(final ImageView image, final TextView likeTxt, final TextView likesCount, final ProgressBar loadingImage,
                                  final RelativeLayout menuItemLayout, final LinearLayout menuItemRootLayout, final TextView likeCount) {

        linearLayoutManager.setScrolledEnabled(false);
        likeTxt.setEnabled(false);
        likesCount.setEnabled(false);
        image.setEnabled(false);
//        menuItemRootLayout.setBackgroundColor(Color.parseColor("#ffffff"));
        loadingImage.setVisibility(View.VISIBLE);
//        menuItemLayout.setVisibility(View.GONE);

        String url = Config.QB_ADD_TOP_BUDS;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, postFavoriteItemData(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("Top Bud", response.toString());
                        isLiked = true;

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
//                        menuItemLayout.setVisibility(View.VISIBLE);
//                        menuItemRootLayout.setBackgroundColor(Color.parseColor("#000000"));
//
                        image.setImageResource(R.mipmap.favorite_green_image);
                        image.setEnabled(true);
                        likeTxt.setEnabled(true);
                        likesCount.setEnabled(true);
                        linearLayoutManager.setScrolledEnabled(true);
//                        text.setText("Added to favorites");
                        menuModel.get(itemPosition).setLikesCount(menuModel.get(itemPosition).getLikesCount() + 1);
                        likeCount.setText(String.valueOf(menuModel.get(itemPosition).getLikesCount()));
                        menuModel.get(itemPosition).setTopBudLiked(true);
//                        menuModel.get(itemPosition).setFavoriteId(favoriteId);
//
//                        FavoriteSingleton.getInstance().addToFavorite(menuModel.get(itemPosition));


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingImage.setVisibility(View.GONE);
//                menuItemLayout.setVisibility(View.VISIBLE);
//                menuItemRootLayout.setBackgroundColor(Color.parseColor("#000000"));
                if (error != null) {
                    if (error instanceof TimeoutError) {
                        Config.internetSlowError(mContext);
                    } else if (error instanceof NoConnectionError) {
                        Config.internetSlowError(mContext);
                    } else if (error instanceof ServerError) {
                        Config.serverError(mContext);
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

        VolleySingleton.getInstance(mContext).addToRequestQueue(request);
    }

    private void removeTopBud(final ImageView image, final TextView likeTxt, final TextView likesCount, final ProgressBar loadingImage,
                                    final RelativeLayout menuItemLayout, final LinearLayout menuItemRootLayout, final TextView likeCount) {

        linearLayoutManager.setScrolledEnabled(false);
        likeTxt.setEnabled(false);
        likesCount.setEnabled(false);
        image.setEnabled(false);
//        menuItemRootLayout.setBackgroundColor(Color.parseColor("#ffffff"));
        loadingImage.setVisibility(View.VISIBLE);
//        menuItemLayout.setVisibility(View.GONE);

        SharedPreferences sharedPreferences = mContext.getSharedPreferences("user_info", Context.MODE_PRIVATE);
        String patronId = sharedPreferences.getString("patron_id", null);

        LocationListModel dispensaryDetails = SpecificMenuSingleton.getInstance().getClickedRestaurant();

        String url = Config.QB_TOP_BUD_DELETE + "?tenant_id=" + dispensaryDetails.getTenantID()
                + "&patron_id=" + patronId + "&location_id=" + dispensaryDetails.getLocationID() + "&menu_item_id=" + menuModel.get(itemPosition).getMenuItemId();

        Log.i("top_bud_url", url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("Top bud delete", response.toString());

                        isLiked = false;

                        loadingImage.setVisibility(View.GONE);
//                        menuItemLayout.setVisibility(View.VISIBLE);
//                        menuItemRootLayout.setBackgroundColor(Color.parseColor("#000000"));

                        image.setImageResource(R.mipmap.favorite_green_icon);
                        image.setEnabled(true);
                        likeTxt.setEnabled(true);
                        likesCount.setEnabled(true);
                        linearLayoutManager.setScrolledEnabled(true);
//                        text.setText("Add to favorites");
                        menuModel.get(itemPosition).setLikesCount(menuModel.get(itemPosition).getLikesCount() - 1);
                        likeCount.setText(String.valueOf(menuModel.get(itemPosition).getLikesCount()));

                        menuModel.get(itemPosition).setTopBudLiked(false);
//
//                        FavoriteSingleton.getInstance().deleteFavorite(favoriteId);


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error != null) {
                    loadingImage.setVisibility(View.GONE);
//                    menuItemLayout.setVisibility(View.VISIBLE);
//                    menuItemRootLayout.setBackgroundColor(Color.parseColor("#000000"));
                    if (error instanceof TimeoutError) {
                        Config.internetSlowError(mContext);
                    } else if (error instanceof NoConnectionError) {
                        Config.internetSlowError(mContext);
                    } else if (error instanceof ServerError) {
                        Config.serverError(mContext);
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

        VolleySingleton.getInstance(mContext).addToRequestQueue(request);

    }

    private JSONObject postFavoriteItemData() {

        SpecificMenuSingleton menuSingleton = SpecificMenuSingleton.getInstance();
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("user_info", Context.MODE_PRIVATE);
        String patronId = sharedPreferences.getString("patron_id", null);

        JSONObject favoritesObject = new JSONObject();

        JSONArray favoriteArray = new JSONArray();

        JSONObject favoriteObject = new JSONObject();

        try {
            favoriteObject.put("tenant_id", menuSingleton.getClickedRestaurant().getTenantID());
            favoriteObject.put("location_id", menuSingleton.getClickedRestaurant().getLocationID());
            favoriteObject.put("patron_id", patronId);
            favoriteObject.put("menu_id", menuModel.get(itemPosition).getMenuItemId());

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

    private void removeFavoriteItem(int favoriteId, final ImageView image, final TextView text, final ProgressBar loadingImage,
                                    final RelativeLayout menuItemLayout, final LinearLayout menuItemRootLayout, final TextView likeCount) {

        linearLayoutManager.setScrolledEnabled(false);
        text.setEnabled(false);
        image.setEnabled(false);
//        menuItemRootLayout.setBackgroundColor(Color.parseColor("#ffffff"));
        loadingImage.setVisibility(View.VISIBLE);
//        menuItemLayout.setVisibility(View.GONE);

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
//                        menuItemLayout.setVisibility(View.VISIBLE);
//                        menuItemRootLayout.setBackgroundColor(Color.parseColor("#000000"));

                        image.setImageResource(R.mipmap.ic_favorite_unselected);
                        image.setEnabled(true);
                        text.setEnabled(true);
                        linearLayoutManager.setScrolledEnabled(true);
//                        text.setText("Add to favorites");
//                        menuModel.get(itemPosition).setLikesCount(menuModel.get(itemPosition).getLikesCount() - 1);
//                        likeCount.setText(String.valueOf(menuModel.get(itemPosition).getLikesCount()));

                        menuModel.get(itemPosition).setFavorite(false);

                        FavoriteSingleton.getInstance().deleteFavorite(favoriteId);


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error != null) {
                    loadingImage.setVisibility(View.GONE);
//                    menuItemLayout.setVisibility(View.VISIBLE);
//                    menuItemRootLayout.setBackgroundColor(Color.parseColor("#000000"));
                    if (error instanceof TimeoutError) {
                        Config.internetSlowError(mContext);
                    } else if (error instanceof NoConnectionError) {
                        Config.internetSlowError(mContext);
                    } else if (error instanceof ServerError) {
                        Config.serverError(mContext);
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

        VolleySingleton.getInstance(mContext).addToRequestQueue(request);

    }
}
