package com.app.android.quickbud.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.android.quickbud.R;
import com.app.android.quickbud.modelClasses.TopBudsDispensaryModel;

import java.util.ArrayList;

/**
 * Created by mobi11 on 19/4/17.
 */
public class TopBudDispensariesListAdapter extends BaseAdapter{

    private Context context;
    private ArrayList<TopBudsDispensaryModel> topBudsDispensaryModels;

    public TopBudDispensariesListAdapter(Context context, ArrayList<TopBudsDispensaryModel> topBudsDispensaryModels) {
        this.context = context;
        this.topBudsDispensaryModels = topBudsDispensaryModels;
    }

    @Override
    public int getCount() {
        return topBudsDispensaryModels.size();
    }

    @Override
    public Object getItem(int position) {
        return topBudsDispensaryModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.top_buds_list_single, parent, false);
        }

        TextView dispensaryName = (TextView) convertView.findViewById(R.id.menu_item_name);
        TextView dispensaryAddress = (TextView) convertView.findViewById(R.id.menu_name);
        TextView likesCount = (TextView) convertView.findViewById(R.id.likes_number);
        ImageView dispensaryImage = (ImageView) convertView.findViewById(R.id.menu_item_image);
        ImageView isLiked = (ImageView) convertView.findViewById(R.id.is_liked);

        TopBudsDispensaryModel topBudsDispensaryModel = topBudsDispensaryModels.get(position);

        dispensaryName.setText(topBudsDispensaryModel.getDispensaryName());
        likesCount.setText("" + topBudsDispensaryModel.getLikesCount());

        if (topBudsDispensaryModel.isLiked()){
            isLiked.setImageResource(R.mipmap.favorite_green_image);
        }else {
            isLiked.setImageResource(R.mipmap.favorite_green_icon);
        }

        return convertView;
    }
}
