package com.app.android.quickbud.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.android.quickbud.R;
import com.app.android.quickbud.modelClasses.FavoriteModel;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by mobi11 on 8/2/16.
 */
public class FavoriteMenuRecyclerAdapter extends RecyclerView.Adapter<FavoriteMenuRecyclerAdapter.RecyclerViewHolder>{

    private Context mContext;
    private ArrayList<FavoriteModel> favoriteModels;

    public FavoriteMenuRecyclerAdapter(Context mContext, ArrayList<FavoriteModel> favoriteModels) {
        this.mContext = mContext;
        this.favoriteModels = favoriteModels;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(mContext).
//                inflate(R.layout.order_menu_row, parent, false);
        View view = LayoutInflater.from(mContext).
                inflate(R.layout.favorite_menu_row, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        holder.categoryImage.setImageResource(R.mipmap.menu_placeholder);
        holder.categoryTitle.setText(Html.fromHtml(favoriteModels.get(position).getMenuItemModel().getMenuItemName()));
//        holder.categoryCount.setText(Html.fromHtml(favoriteModels.get(position).getMenuItemModel().getMenuItemPrice()));

        Glide.with(mContext).load(favoriteModels.get(position).getMenuItemModel().getMenuItemImage()).placeholder(R.mipmap.menu_placeholder)
                .fitCenter().crossFade(1000)
                .into(holder.categoryImage);
    }

    @Override
    public int getItemCount() {
        return favoriteModels.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {

        ImageView categoryImage;
        TextView categoryTitle;
//        TextView categoryCount;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            categoryImage = (ImageView) itemView.findViewById(R.id.category_image);
            categoryTitle = (TextView) itemView.findViewById(R.id.category_title);
//            categoryCount = (TextView) itemView.findViewById(R.id.category_count);
        }
    }
}
