package com.app.android.quickbud.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.android.quickbud.R;

/**
 * Created by mobi11 on 17/11/15.
 */
public class SortingSpinnerAdapter extends ArrayAdapter<String> {

    private String[] sortBy;
    private Context context;
    private int sortByClickPosition;

    public SortingSpinnerAdapter(Context context, int resource, String[] objects, int sortByClickPosition) {
        super(context, R.layout.restaurant_sorting_spinner, R.id.rest_spinner_text, objects);
        this.context = context;
        this.sortBy = objects;
        this.sortByClickPosition = sortByClickPosition;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view = getCustomView(position, convertView, parent);
        TextView spinnerText = (TextView) view.findViewById(R.id.rest_spinner_text);
        ImageView imageView = (ImageView) view.findViewById(R.id.rest_spinner_image);
        imageView.setImageResource(R.mipmap.tick_icon);
        if(position == sortByClickPosition){
            imageView.setVisibility(View.VISIBLE);
        }else{
            imageView.setVisibility(View.GONE);
        }

        if (position == 0){
            imageView.setVisibility(View.GONE);
        }
        spinnerText.setText(sortBy[position]);
        return view;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = getCustomView(position, convertView, parent);
        TextView spinnerText = (TextView) view.findViewById(R.id.rest_spinner_text);
        spinnerText.setText(sortBy[sortByClickPosition]);
        return view;
    }

    public View getCustomView(int position, View convertView, ViewGroup parent){

        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.restaurant_sorting_spinner,parent,false);
        }

        return convertView;
    }
}
