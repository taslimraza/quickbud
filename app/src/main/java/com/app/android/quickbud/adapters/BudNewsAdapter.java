package com.app.android.quickbud.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.android.quickbud.R;
import com.app.android.quickbud.modelClasses.BudNewsModel;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by mobi11 on 28/3/17.
 */
public class BudNewsAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<BudNewsModel> budNewsModels;

    public BudNewsAdapter(Context context, ArrayList<BudNewsModel> budNewsModels) {
        this.context = context;
        this.budNewsModels = budNewsModels;
    }

    @Override
    public int getCount() {
        return budNewsModels.size();
    }

    @Override
    public Object getItem(int position) {
        return budNewsModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.bud_news_list_single, parent, false);

        ImageView articleImage = (ImageView) view.findViewById(R.id.article_image);
        TextView articleDate = (TextView) view.findViewById(R.id.article_date);
        TextView articleHeadline = (TextView) view.findViewById(R.id.article_headline);
        TextView articleSource = (TextView) view.findViewById(R.id.article_source);
//        TextView articleLink = (TextView) view.findViewById(R.id.article_link);

        BudNewsModel budNewsModel = budNewsModels.get(position);

        Glide.with(context).load(budNewsModel.getArticleImage())
                .placeholder(R.mipmap.restaurant_defaulticon)
                .into(articleImage);

        articleDate.setText(budNewsModel.getArticleDate());
        articleHeadline.setText(budNewsModel.getArticleHeadline());
        if (budNewsModel.getArticleSource() != null && !budNewsModel.getArticleSource().isEmpty())
              articleSource.setText("-" + budNewsModel.getArticleSource());
//        articleLink.setText(budNewsModel.getArticleLink());

        return view;
    }
}
