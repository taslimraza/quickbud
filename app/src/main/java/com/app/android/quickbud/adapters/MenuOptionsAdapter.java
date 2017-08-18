package com.app.android.quickbud.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.app.android.quickbud.R;
import com.app.android.quickbud.modelClasses.MenuChoicesModel;
import com.app.android.quickbud.modelClasses.MenuOptionModel;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by mobi11 on 17/10/15.
 */
public class MenuOptionsAdapter extends BaseAdapter {

    private Context mContext;
    ArrayList<MenuOptionModel> menuOptionModels;
    HashMap<String,ArrayList<MenuChoicesModel>> menuChoices;
    MenuChoicesAdapter menuChoicesAdapter;

    public MenuOptionsAdapter(Context mContext, ArrayList<MenuOptionModel> menuOptionModels,
                              HashMap<String,ArrayList<MenuChoicesModel>> menuChoices) {
        this.mContext = mContext;
        this.menuOptionModels = menuOptionModels;
        this.menuChoices = menuChoices;
    }

    @Override
    public int getCount() {
        return menuOptionModels.size();
    }

    @Override
    public Object getItem(int position) {
        return menuOptionModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final OrderViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new OrderViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.menu_option_single, null, false);
            viewHolder.orderItemName = (TextView) convertView.findViewById(R.id.option_name);
//            viewHolder.orderItemName = (TextView) convertView.findViewById(R.id.extra_order_title);
//            viewHolder.orderRadioButton = (RadioButton) convertView.findViewById(R.id.extra_order_radioButton);
//            viewHolder.orderCheckBox = (CheckBox) convertView.findViewById(R.id.extra_order_checkBox);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (OrderViewHolder) convertView.getTag();
        }
        viewHolder.orderItemName.setText(menuOptionModels.get(position).getMenuOptionName());

        viewHolder.orderItemName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuChoicesAdapter = new MenuChoicesAdapter(mContext, menuChoices.get(position));
                viewHolder.menuChoicesList.setAdapter(menuChoicesAdapter);
            }
        });

//        if (menuOptionModels.get(position).isRadioButtonVisibility()) {
//            viewHolder.orderRadioButton.setVisibility(View.VISIBLE);
//        } else {
//            viewHolder.orderRadioButton.setVisibility(View.GONE);
//        }
//
//        if (menuOptionModels.get(position).isCheckBoxChecked()) {
//            viewHolder.orderCheckBox.setVisibility(View.VISIBLE);
//        } else {
//            viewHolder.orderCheckBox.setVisibility(View.GONE);
//        }
//
//        if (menuOptionModels.get(position).isRadioButtonChecked()) {
//            viewHolder.orderRadioButton.setChecked(true);
//        } else {
//            viewHolder.orderRadioButton.setChecked(false);
//        }
//
//        if (menuOptionModels.get(position).isCheckBoxChecked()) {
//            viewHolder.orderCheckBox.setChecked(true);
//        } else {
//            viewHolder.orderCheckBox.setChecked(false);
//        }
        return convertView;
    }

    public class OrderViewHolder {
        private TextView orderItemName;
        private ListView menuChoicesList;
//        private RadioButton orderRadioButton;
//        private CheckBox orderCheckBox;

    }
}
