package com.app.android.quickbud.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.app.android.quickbud.R;

/**
 * Created by mobi11 on 5/10/16.
 */
public class ChatRestaurantListAdapter extends BaseAdapter{

    private Context context;
    private String[] restaurantList;

    public ChatRestaurantListAdapter(Context context, String[] restaurantList) {
        this.context = context;
        this.restaurantList = restaurantList;
    }

    @Override
    public int getCount() {
        return restaurantList.length;
    }

    @Override
    public Object getItem(int position) {
        return restaurantList[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.chat_restaurant_list, parent, false);
        }

        TextView restName = (TextView) convertView.findViewById(R.id.chat_restaurant_name);
        restName.setText(restaurantList[position]);

        return convertView;
    }
}
