package com.app.android.quickbud.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.app.android.quickbud.R;
import com.app.android.quickbud.modelClasses.FavoriteRestListModel;

import java.util.ArrayList;

/**
 * Created by mobi11 on 24/10/16.
 */
public class FavoriteRestListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<FavoriteRestListModel> favoriteRestListModels;

    public FavoriteRestListAdapter(Context context, ArrayList<FavoriteRestListModel> favoriteRestListModels) {
        this.context = context;
        this.favoriteRestListModels = favoriteRestListModels;
    }

    @Override
    public int getCount() {
        return favoriteRestListModels.size();
    }

    @Override
    public Object getItem(int position) {
        return favoriteRestListModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.favorite_rest_list_single, parent, false);
        }

        TextView restaurantName = (TextView) convertView.findViewById(R.id.favorite_rest_name);
        TextView restaurantAddress = (TextView) convertView.findViewById(R.id.favorite_rest_address);

        FavoriteRestListModel favoriteRestListModel = favoriteRestListModels.get(position);

        restaurantName.setText(favoriteRestListModel.getRestName());
        restaurantAddress.setText(favoriteRestListModel.getRestAddressLine() + ", " + favoriteRestListModel.getRestCity()
                                                        + ", " + favoriteRestListModel.getRestState());

        return convertView;
    }
}
