package com.app.android.quickbud.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.app.android.quickbud.R;
import com.app.android.quickbud.modelClasses.CategoryModel;

import java.util.ArrayList;

/**
 * Created by mobi11 on 3/10/16.
 */
public class CategoryAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<CategoryModel> categoryModels;

    public CategoryAdapter(Context context, ArrayList<CategoryModel> categoryModels) {
        this.context = context;
        this.categoryModels = categoryModels;
    }

    @Override
    public int getCount() {
        return categoryModels.size();
    }

    @Override
    public Object getItem(int position) {
        return categoryModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.category_single, parent, false);
        }

        TextView categoryNameTxt = (TextView) convertView.findViewById(R.id.category_name);

        CategoryModel categoryModel = categoryModels.get(position);

        categoryNameTxt.setText(categoryModel.getCategoryName());

        if (categoryModel.isSelected()) {
            categoryNameTxt.setTypeface(null, Typeface.BOLD);
            categoryNameTxt.setBackgroundResource(R.drawable.category_bg);
        }else {
            categoryNameTxt.setTypeface(null, Typeface.NORMAL);
            categoryNameTxt.setBackgroundResource(R.drawable.category_unselected_bg);
        }

        return convertView;
    }
}
