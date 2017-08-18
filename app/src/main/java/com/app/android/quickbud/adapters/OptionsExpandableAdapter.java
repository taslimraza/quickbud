package com.app.android.quickbud.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.app.android.quickbud.R;
import com.app.android.quickbud.interfaces.ItemChoiceQuantityUpdate;
import com.app.android.quickbud.modelClasses.MenuChoicesModel;
import com.app.android.quickbud.modelClasses.MenuOptionModel;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/**
 * Created by mobi11 on 6/11/15.
 */
public class OptionsExpandableAdapter extends BaseExpandableListAdapter {
    private Context mContext;
    private ArrayList<MenuOptionModel> menuOptionModels;
    private Map<String, ArrayList<MenuChoicesModel>> menuChoicesHashMap;
    private ArrayList<String> choicesList = new ArrayList<>();
    private ArrayList<String> optionsList = new ArrayList<>();
    private boolean isChecked = false;
    private ArrayList<MenuChoicesModel> menuChoicesArrayList;
    private ArrayList<String> menuOptionName;
    private Integer itemQuantityCount = 0;
    private ItemChoiceQuantityUpdate itemChoiceQuantityUpdate;
    private Fragment fragment;

    public OptionsExpandableAdapter(Context mContext, ArrayList<MenuOptionModel> menuOptionModels,
                                    Map<String, ArrayList<MenuChoicesModel>> menuChoicesHashMap, Fragment fragment) {
        this.mContext = mContext;
        this.menuOptionModels = menuOptionModels;
        this.menuChoicesHashMap = menuChoicesHashMap;
        this.fragment = fragment;
        initializeData();
    }

    @Override
    public int getGroupCount() {
        return menuChoicesHashMap.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return menuChoicesHashMap.get(menuOptionName.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return menuChoicesHashMap.get(menuOptionName.get(groupPosition));
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return menuChoicesHashMap.get(menuOptionName.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).
                    inflate(R.layout.menu_option_single, parent, false);
        }
        TextView optionName = (TextView) convertView.findViewById(R.id.option_name);
        ImageView dropDownImage = (ImageView) convertView.findViewById(R.id.dropdown_button);

        optionName.setText(menuOptionModels.get(groupPosition).getMenuOptionName());

        if (isExpanded) {
            dropDownImage.setImageResource(R.mipmap.arrow_up_icon);
        } else {
            dropDownImage.setImageResource(R.mipmap.dropdown_icon);
        }
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, final boolean isLastChild, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.order_extra_row, parent, false);
        TextView choiceName = (TextView) view.findViewById(R.id.extra_order_title);
        CheckBox choiceCheckBox = (CheckBox) view.findViewById(R.id.extra_order_checkBox);
        RadioButton choiceRadioButton = (RadioButton) view.findViewById(R.id.extra_order_radioButton);
        final LinearLayout itemQuantityLayout = (LinearLayout) view.findViewById(R.id.add_to_cart_layout);
        Button incrementQuantityBtn = (Button) view.findViewById(R.id.up_button);
        Button decrementQuantityBtn = (Button) view.findViewById(R.id.down_button);
        final TextView itemQuantityTxt = (TextView) view.findViewById(R.id.item_quantity);

        itemChoiceQuantityUpdate = (ItemChoiceQuantityUpdate) fragment;

        menuChoicesArrayList = menuChoicesHashMap.get(menuOptionName.get(groupPosition));

        final MenuOptionModel menuOptionModel = menuOptionModels.get(groupPosition);
        final MenuChoicesModel menuChoicesModel = menuChoicesArrayList.get(childPosition);

        incrementQuantityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                itemQuantityCount = Integer.parseInt(itemQuantityTxt.getText().toString());
                itemQuantityCount++;
                itemQuantityTxt.setText(itemQuantityCount.toString());
                itemChoiceQuantityUpdate.updateMenu(itemQuantityCount, groupPosition, childPosition);
            }
        });

        decrementQuantityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemQuantityCount = Integer.parseInt(itemQuantityTxt.getText().toString());
                if (itemQuantityCount > 0){
                    itemQuantityCount--;
                    itemQuantityTxt.setText(itemQuantityCount.toString());
                    itemChoiceQuantityUpdate.updateMenu(itemQuantityCount, groupPosition, childPosition);
                }
            }
        });

        itemQuantityTxt.setText(""+menuChoicesModel.getMenuChoiceQuantity());

        if (menuOptionModel.getIsMultiQuantity() == 1) {

            choiceCheckBox.setVisibility(View.GONE);
            choiceRadioButton.setVisibility(View.GONE);
            itemQuantityLayout.setVisibility(View.VISIBLE);

        }else {

            itemQuantityLayout.setVisibility(View.GONE);
            if (menuOptionModel.isMultiSelect() != 0) {
                // check box
                if (menuOptionModel.isCheckBoxVisibility()) {
                    choiceCheckBox.setVisibility(View.VISIBLE);
                } else {
                    choiceCheckBox.setVisibility(View.GONE);
                }

                if (menuChoicesModel.isDefault() == 1) {
                    menuChoicesModel.setIsCheckBoxChecked(true);
                    choiceCheckBox.setChecked(true);
                } else {
                    menuChoicesModel.setIsCheckBoxChecked(false);
                    choiceCheckBox.setChecked(false);
                }
            } else {
                // radio button
                if (menuOptionModel.isRadioButtonVisibility()) {
                    choiceRadioButton.setVisibility(View.VISIBLE);
                } else {
                    choiceRadioButton.setVisibility(View.GONE);
                }

                if (menuChoicesModel.isDefault() == 1) {
                    menuChoicesModel.setIsRadioButtonChecked(true);
                    choiceRadioButton.setChecked(true);
                } else {
                    menuChoicesModel.setIsRadioButtonChecked(false);
                    choiceRadioButton.setChecked(false);
                }

            }
        }

        if (menuChoicesModel.getMenuChoicePrice() != null && !menuChoicesModel.getMenuChoicePrice().equals("0.00")) {
            choiceName.setText(menuChoicesModel.getMenuChoiceName() + " - $" + menuChoicesModel.getMenuChoicePrice());
        } else {
            choiceName.setText(menuChoicesModel.getMenuChoiceName());
        }

        return view;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private void initializeData() {
        menuOptionName = new ArrayList<>();
        Set<String> menuOptions = menuChoicesHashMap.keySet();
        for (String name : menuOptions) {
            menuOptionName.add(name);
        }
    }
}
