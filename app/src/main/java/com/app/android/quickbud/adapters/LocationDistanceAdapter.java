package com.app.android.quickbud.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.app.android.quickbud.R;
import com.app.android.quickbud.modelClasses.LocationDistanceModel;
import com.app.android.quickbud.utils.Globals;

import java.util.ArrayList;

/**
 * Created by mobi11 on 9/10/15.
 */
public class LocationDistanceAdapter extends BaseAdapter {
    Context context;
    ArrayList<LocationDistanceModel> locationDistanceModels;

    public LocationDistanceAdapter(Context context,ArrayList<LocationDistanceModel> locationDistanceModels){
        this.context=context;
        this.locationDistanceModels=locationDistanceModels;
    }
    @Override
    public int getCount() {
        return locationDistanceModels.size();
    }

    @Override
    public Object getItem(int position) {
        return locationDistanceModels.get(position).toString();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.location_singlerow, parent, false);

        TextView restDistance= (TextView) view.findViewById(R.id.location_distance);

        restDistance.setText(locationDistanceModels.get(position).getDistance());

        restDistance.setTypeface(Globals.myraidProRegular);
        return view;
    }
}
