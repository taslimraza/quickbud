package com.app.android.quickbud.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.android.quickbud.R;
import com.app.android.quickbud.utils.Globals;

/**
 * Created by mobi11 on 22/9/15.
 */
public class DrawerListAdapter extends BaseAdapter {
    Context context;
    String[] titles = {"Quick links","Profile", "My Favorites","Share with Friends", "Information"};
    int[] images = {R.mipmap.userprofile_icon,
            R.mipmap.ic_my_favorite_flyout,
            R.mipmap.ic_people_share,
            R.mipmap.ic_app_info};

    public DrawerListAdapter(Context context){
        this.context=context;
    }
    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.navigation_singlerow, parent, false);
        ImageView menuItem = (ImageView) view.findViewById(R.id.menu_image);
        TextView menuTitle = (TextView) view.findViewById(R.id.menu_title);
        if (position == 0) {
            menuItem.setVisibility(View.GONE);
            menuTitle.setTextSize(15);
            menuTitle.setText(titles[position]);
            menuTitle.setTypeface(Globals.robotoBold);
            return view;
        }
        menuItem.setImageResource(images[position - 1]);
        menuTitle.setText(titles[position]);
        menuTitle.setTypeface(Globals.robotoBold);

        return view;
    }
}
