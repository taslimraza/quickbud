package com.app.android.quickbud.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.app.android.quickbud.R;
import com.app.android.quickbud.modelClasses.SearchLocationModel;

import java.util.ArrayList;

/**
 * Created by mg-110 on 26/10/15.
 */
public class SavedSearchesAdapter extends BaseAdapter {


    ArrayList<SearchLocationModel> myList = new ArrayList<SearchLocationModel>();
    LayoutInflater inflater;
    Context context;


    public SavedSearchesAdapter(Context context, ArrayList<SearchLocationModel> myList) {
        this.myList = myList;
        this.context = context;
        inflater = LayoutInflater.from(this.context);
    }

    @Override
    public int getCount() {
        return myList.size();
    }

    @Override
    public SearchLocationModel getItem(int position) {
        return myList.get(position);
    }

    @Override
    public long getItemId(int position1) {
        return 0;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyViewHolder mViewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.single_search_location_row, parent, false);
            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }

        SearchLocationModel currentListData = myList.get(position);

        mViewHolder.tvTitle.setText(currentListData.getAddress1());
        mViewHolder.tvDesc.setText(currentListData.getAddress2());

        return convertView;
    }

    private class MyViewHolder {
        TextView tvTitle, tvDesc;


        public MyViewHolder(View item) {
            tvTitle = (TextView) item.findViewById(R.id.address1);
            tvDesc = (TextView) item.findViewById(R.id.place1);

        }
    }
}



