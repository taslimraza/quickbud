package com.app.android.quickbud.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.app.android.quickbud.R;
import com.app.android.quickbud.modelClasses.CartItemModel;
import com.app.android.quickbud.modelClasses.FavoriteDetailsModel;
import com.app.android.quickbud.modelClasses.MenuChoicesModel;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/**
 * Created by mobi11 on 21/11/15.
 */
public class FavoriteItemDetailsAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<CartItemModel> cartItemModels;
    private ArrayList<String> optionsName;
    Map<String, ArrayList<MenuChoicesModel>> menuChoices;
    private Double totalItemPrice;
    private FavoriteDetailsModel favoriteDetailsModel;

    public FavoriteItemDetailsAdapter(Context context, ArrayList<CartItemModel> cartItemModels, FavoriteDetailsModel favoriteDetailsModel) {
        this.context = context;
        this.cartItemModels = cartItemModels;
        this.favoriteDetailsModel = favoriteDetailsModel;
    }


    @Override
    public int getCount() {
        return cartItemModels.size();
    }

    @Override
    public Object getItem(int position) {
        return cartItemModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = LayoutInflater.from(context).inflate(R.layout.favorite_item_details_single,parent,false);
        TextView itemQuantity = (TextView) view.findViewById(R.id.item_quantity);
        TextView itemPrice = (TextView) view.findViewById(R.id.item_price);
        TextView itemName = (TextView) view.findViewById(R.id.item_name);
        ListView optionsListView = (ListView) view.findViewById(R.id.options_list);

        if (cartItemModels.get(position).getMenuChoicesHashMap() != null && cartItemModels.get(position).getMenuChoicesHashMap().size() > 0) {
            FavoriteMenuOptionsAdapter cartMenuOptionsAdapter = new FavoriteMenuOptionsAdapter(context, cartItemModels.get(position).getMenuChoicesHashMap(), position);
            optionsListView.setAdapter(cartMenuOptionsAdapter);
            setListViewHeight(optionsListView);
        }else {
            clearListViewHeight(optionsListView);
        }

        if (cartItemModels.get(position).getMenuChoicesHashMap() != null && cartItemModels.get(position).getMenuChoicesHashMap().size() > 0) {
            menuChoices = cartItemModels.get(position).getMenuChoicesHashMap();
            initializeData();
            totalItemPrice = Double.parseDouble(cartItemModels.get(position).getItemPrice());
            for(int j=0; j<optionsName.size(); j++){
                for(int i=0; i<menuChoices.get(optionsName.get(j)).size(); i++){
                    totalItemPrice += Double.parseDouble(menuChoices.get(optionsName.get(j)).get(i).getMenuChoicePrice());
                }
            }
            Double quantity = Double.parseDouble(cartItemModels.get(position).getItemQuantity());
            Double totalCost = totalItemPrice * quantity;
            String cost = String.format("%.2f",totalCost);
            itemPrice.setText("$ " + cost);
        }else {
            Float price = Float.parseFloat(cartItemModels.get(position).getItemPrice());
            Float quantity = Float.parseFloat(cartItemModels.get(position).getItemQuantity());
            Float totalCost = price * quantity;
            String cost = String.format("%.2f",totalCost);
            itemPrice.setText("$ " + cost);
        }
        itemName.setText(cartItemModels.get(position).getItemName());
        itemQuantity.setText(cartItemModels.get(position).getItemQuantity() + "x");
        return view;
    }

    public void setListViewHeight(ListView optionList) {
        ListAdapter listAdapter = optionList.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, optionList);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = optionList.getLayoutParams();
        params.height = totalHeight + (optionList.getDividerHeight() * (listAdapter.getCount() - 1));
        optionList.setLayoutParams(params);
        optionList.requestLayout();
    }

    public void clearListViewHeight(ListView optionList){
        ViewGroup.LayoutParams params = optionList.getLayoutParams();
        params.height = 0;
        optionList.setLayoutParams(params);
        optionList.requestLayout();
    }

    private void initializeData() {
        optionsName = new ArrayList<>();
        Set<String> menuOptions = menuChoices.keySet();
        for(String name:menuOptions){
            optionsName.add(name);
        }
    }
}
