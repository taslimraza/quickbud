package com.app.android.quickbud.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.app.android.quickbud.R;
import com.app.android.quickbud.activities.OrderDetailsActivity;
import com.app.android.quickbud.fragments.MenuItemFragment;
import com.app.android.quickbud.modelClasses.CartItemModel;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by mobi24 on 23/9/15.
 */
public class ItemListAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<CartItemModel> cartItemModels;
    private String itemCost;
    private String price;
    private TextView itemPrice;
    private Double calculatePrice;
    private String calculatedPrice;
    private int optionCount = 0;
    private Set<String> menuOptions;
    private RelativeLayout orderDetailsLayout;
    private RelativeLayout emptyCartLayout;

    public ItemListAdapter(Context context, ArrayList<CartItemModel> cartItemModels,
                           RelativeLayout orderDetailsLayout, RelativeLayout emptyCartLayout) {
        mContext = context;
        this.cartItemModels = cartItemModels;
        this.orderDetailsLayout = orderDetailsLayout;
        this.emptyCartLayout = emptyCartLayout;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        optionCount = 0;
        View view = convertView;
        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.single_item_list, parent, false);
        }
        Spinner spinner = (Spinner) view.findViewById(R.id.orderSpinner);
        TextView items = (TextView) view.findViewById(R.id.orderItem);
        itemPrice = (TextView) view.findViewById(R.id.orderPrice);
        ListView optionsList = (ListView) view.findViewById(R.id.options_list);

        String itemCount = cartItemModels.get(position).getItemQuantity();
        spinner.setSelection(Integer.parseInt(itemCount) - 1);
        items.setText(cartItemModels.get(position).getItemName());
        if (Integer.parseInt(itemCount) == 1) {
            cartItemModels.get(position).setCalculatedItemPrice(cartItemModels.get(position).getItemPrice());
        } else if (Integer.parseInt(itemCount) > 1) {
            cartItemModels.get(position).setCalculatedItemPrice(cartItemModels.get(position).getCalculatedItemPrice());
        }
        itemPrice.setText(cartItemModels.get(position).getCalculatedItemPrice());

        if (cartItemModels.get(position).getMenuChoicesHashMap() != null) {
            CartMenuOptionsAdapter cartMenuOptionsAdapter = new CartMenuOptionsAdapter(mContext, cartItemModels.get(position).getMenuChoicesHashMap(), position, cartItemModels.get(position).getMenuOptionModels());
            optionsList.setAdapter(cartMenuOptionsAdapter);
            setListViewHeight(optionsList);
        }else {
            clearListViewHeight(optionsList);
        }
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
//                if (pos == 0) {
//                    if (cartItemModels.size() == 1) {
//                     // if there is only 1 item and it's removed then set empty cart layout
//                        orderDetailsLayout.setVisibility(View.GONE);
//                        emptyCartLayout.setVisibility(View.VISIBLE);
//                    }
//                    CartSingleton cartSingleton = CartSingleton.getInstance();
//                    cartSingleton.removeFromCart(position);
//                    notifyDataSetChanged();
//                    OrderDetailsActivity.updateData();
//                    MenuItemFragment.updateCartCount();
//                } else if (pos == 1) {
//                    price = cartItemModels.get(position).getItemPrice();
//                    DecimalFormat df = new DecimalFormat(".##");
//                    calculatePrice = Double.parseDouble(price) * pos;
//                    calculatedPrice = String.format("%.2f", calculatePrice);
//                    cartItemModels.get(position).setCalculatedItemPrice(calculatedPrice);
//                    cartItemModels.get(position).setItemQuantity("" + pos);
//                    notifyDataSetChanged();
//                    OrderDetailsActivity.updateData();
//                    MenuItemFragment.updateCartCount();
//                } else {
                    price = cartItemModels.get(position).getItemPrice();
                    calculatePrice = Double.parseDouble(price) * (pos + 1);
                    calculatedPrice = String.format("%.2f", calculatePrice);
                    cartItemModels.get(position).setCalculatedItemPrice(calculatedPrice);
                    cartItemModels.get(position).setItemQuantity("" + (pos + 1));
                    notifyDataSetChanged();
                    OrderDetailsActivity.updateData();
                    MenuItemFragment.updateCartCount();
//                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        optionsList.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
//                Toast.makeText(mContext, "Delete item!", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

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
}
