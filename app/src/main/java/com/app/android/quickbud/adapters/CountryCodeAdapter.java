package com.app.android.quickbud.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.android.quickbud.R;
import com.app.android.quickbud.modelClasses.CountryCodeModel;

import java.util.ArrayList;

/**
 * Created by mobi11 on 5/11/16.
 */
public class CountryCodeAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<CountryCodeModel> countryCodeModels;

    public CountryCodeAdapter(Context context, ArrayList<CountryCodeModel> countryCodeModels) {
        this.context = context;
        this.countryCodeModels = countryCodeModels;
    }

    @Override
    public int getCount() {
        return countryCodeModels.size();
    }

    @Override
    public Object getItem(int position) {
        return countryCodeModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.country_code_single, parent, false);
        }

        ImageView countryFlag = (ImageView) convertView.findViewById(R.id.country_flag);
        TextView countryName = (TextView) convertView.findViewById(R.id.country_name);

        CountryCodeModel model = countryCodeModels.get(position);

        countryFlag.setImageResource(model.getCountryFlag());
        countryName.setText(model.getCountryName());

        return convertView;
    }
}
