package com.app.android.quickbud.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;

import com.app.android.quickbud.R;
import com.app.android.quickbud.modelClasses.PlaceOrderModel;

import java.util.ArrayList;

/**
 * Created by vinay on 18/9/15.
 */
public class PlaceOrderAdapter extends BaseAdapter {

    private Context mContext;
    ArrayList<PlaceOrderModel> orderModelList;

    public PlaceOrderAdapter(Context mContext, ArrayList<PlaceOrderModel> orderModel) {
        this.mContext = mContext;
        this.orderModelList = orderModel;
    }

    @Override
    public int getCount() {
        return orderModelList.size();
    }

    @Override
    public Object getItem(int position) {
        return orderModelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final OrderViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new OrderViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.order_extra_row, null, false);
            viewHolder.orderItemName = (TextView) convertView.findViewById(R.id.extra_order_title);
            viewHolder.orderRadioButton = (RadioButton) convertView.findViewById(R.id.extra_order_radioButton);
            viewHolder.orderCheckBox = (CheckBox) convertView.findViewById(R.id.extra_order_checkBox);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (OrderViewHolder) convertView.getTag();
        }
        viewHolder.orderItemName.setText(orderModelList.get(position).getOrderExtraName());
        if (orderModelList.get(position).getRadioButtonVisibility()) {
            viewHolder.orderRadioButton.setVisibility(View.VISIBLE);
        } else {
            viewHolder.orderRadioButton.setVisibility(View.GONE);
        }

        if (orderModelList.get(position).getCheckBoxVisibility()) {
            viewHolder.orderCheckBox.setVisibility(View.VISIBLE);
        } else {
            viewHolder.orderCheckBox.setVisibility(View.GONE);
        }

        if (orderModelList.get(position).isRadioButtonChecked()) {
            viewHolder.orderRadioButton.setChecked(true);
        } else {
            viewHolder.orderRadioButton.setChecked(false);
        }

        if (orderModelList.get(position).isCheckBoxChecked()) {
            viewHolder.orderCheckBox.setChecked(true);
        } else {
            viewHolder.orderCheckBox.setChecked(false);
        }
        return convertView;
    }

    public class OrderViewHolder {
        private TextView orderItemName;
        private RadioButton orderRadioButton;
        private CheckBox orderCheckBox;

    }
}
