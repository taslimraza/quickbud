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
import com.app.android.quickbud.modelClasses.OrderMenuModel;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by vinay on 15/9/15.
 */
public class OrderMenuRecyclerAdapter extends
        RecyclerView.Adapter<OrderMenuRecyclerAdapter.RecyclerViewHolder> {

    private Context mContext;
    private ArrayList<OrderMenuModel> menuModel;

    public OrderMenuRecyclerAdapter(Context mContext, ArrayList<OrderMenuModel> menuModel) {
        this.mContext = mContext;
        this.menuModel = menuModel;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).
                inflate(R.layout.order_menu_row, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        holder.categoryImage.setImageResource(R.mipmap.menu_placeholder);
        holder.categoryTitle.setText(Html.fromHtml(menuModel.get(position).getMenuSectionName()));
        holder.categoryCount.setText(Html.fromHtml(menuModel.get(position).getMenuSectionCount()));
        Glide.with(mContext).load(menuModel.get(position).getMenuSectionImage()).placeholder(R.mipmap.menu_placeholder)
                .fitCenter().crossFade(1000)
                .into(holder.categoryImage);
    }

    @Override
    public int getItemCount() {
        return menuModel.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {

        ImageView categoryImage;
        TextView categoryTitle;
        TextView categoryCount;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            categoryImage = (ImageView) itemView.findViewById(R.id.category_image);
            categoryTitle = (TextView) itemView.findViewById(R.id.category_title);
            categoryCount = (TextView) itemView.findViewById(R.id.category_count);
        }
    }
}
