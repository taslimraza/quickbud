package com.app.android.quickbud.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.android.quickbud.R;
import com.app.android.quickbud.modelClasses.TopBudsListModel;
import com.app.android.quickbud.utils.Config;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by mobi11 on 11/4/17.
 */
public class TopBudsListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<TopBudsListModel> topBudsListModels;

    public TopBudsListAdapter(Context context, ArrayList<TopBudsListModel> topBudsListModels) {
        this.context = context;
        this.topBudsListModels = topBudsListModels;
    }

    @Override
    public int getCount() {
        return topBudsListModels.size();
    }

    @Override
    public Object getItem(int position) {
        return topBudsListModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.top_buds_list_single, parent, false);
        }

        TopBudsListModel topBudsListModel = topBudsListModels.get(position);

        TextView menuItemName = (TextView) convertView.findViewById(R.id.menu_item_name);
        TextView menuName = (TextView) convertView.findViewById(R.id.menu_name);
        TextView likesCount = (TextView) convertView.findViewById(R.id.likes_number);
        ImageView menuImage = (ImageView) convertView.findViewById(R.id.menu_item_image);
        ImageView isLiked = (ImageView) convertView.findViewById(R.id.is_liked);

        menuItemName.setText(topBudsListModel.getMenuItemName());
        menuName.setText(topBudsListModel.getMenuName());
        likesCount.setText(""+topBudsListModel.getLikesNumber());
        Glide.with(context).load(Config.QUICK_CHAT_IMAGE + topBudsListModel.getMenuItemImage())
                .placeholder(R.mipmap.restaurant_defaulticon)
                .into(menuImage);

        if (topBudsListModel.isLiked()){
            isLiked.setImageResource(R.mipmap.favorite_green_image);
        }else {
            isLiked.setImageResource(R.mipmap.favorite_green_icon);
        }

//        if (topBudsListModel.getTopBudsDispensaryModels() != null && topBudsListModel.getTopBudsDispensaryModels().size() > 1) {
//            isLiked.setVisibility(View.GONE);
//            likesCount.setVisibility(View.GONE);
//        }else {
//            isLiked.setVisibility(View.VISIBLE);
//            likesCount.setVisibility(View.VISIBLE);
//        }

        return convertView;
    }
}
