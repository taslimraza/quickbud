package com.app.android.quickbud.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.app.android.quickbud.R;
import com.app.android.quickbud.modelClasses.MenuChoicesModel;
import com.app.android.quickbud.modelClasses.MenuOptionModel;
import com.app.android.quickbud.utils.CartSingleton;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/**
 * Created by mobi11 on 9/11/15.
 */
public class CartMenuOptionsAdapter extends BaseAdapter {
    Context context;
    Map<String, ArrayList<MenuChoicesModel>> menuChoices;
    ArrayList<String> optionsName;
    private CartSingleton cartSingleton;
    private int itemPosition;
    ArrayList<MenuOptionModel> menuOptionModels;

    public CartMenuOptionsAdapter(Context context, Map<String, ArrayList<MenuChoicesModel>> menuChoices, int itemPosition,
                                  ArrayList<MenuOptionModel> menuOptionModels) {
        this.context = context;
        this.menuChoices = menuChoices;
        this.itemPosition = itemPosition;
        this.menuOptionModels = menuOptionModels;
        initializeData();
    }

    @Override
    public int getCount() {
        return menuChoices.size();
    }

    @Override
    public Object getItem(int position) {
        return menuChoices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.cart_menu_option_single,parent,false);
        }
        TextView optionName = (TextView) view.findViewById(R.id.options_name);
        TextView choiceName = (TextView) view.findViewById(R.id.options_choice_name);

        StringBuilder stringBuilder = new StringBuilder();
        for(int i=0; i<menuChoices.get(optionsName.get(position)).size(); i++){

            if (menuOptionModels.get(position).getIsMultiQuantity() == 1) {
                // if multi quantity enabled
                if(menuChoices.get(optionsName.get(position)).get(i).getMenuChoiceQuantity() > 0){
                    if(stringBuilder.toString().equals("")){
                        String name = menuChoices.get(optionsName.get(position)).get(i).getMenuChoiceName();
                        int quantity = menuChoices.get(optionsName.get(position)).get(i).getMenuChoiceQuantity();
                        stringBuilder.append(name + " - " + quantity);
                    }else {
                        String name = menuChoices.get(optionsName.get(position)).get(i).getMenuChoiceName();
                        int quantity = menuChoices.get(optionsName.get(position)).get(i).getMenuChoiceQuantity();
                        stringBuilder.append("\n"+name + " - " + quantity);
                    }
                }
            }else {
                // if multi quantity disabled
                if(menuChoices.get(optionsName.get(position)).get(i).isCheckBoxChecked() ||
                        menuChoices.get(optionsName.get(position)).get(i).isRadioButtonChecked() ||
                        menuChoices.get(optionsName.get(position)).get(i).isDefault() == 1){
                    if(stringBuilder.toString().equals("")){
                        String name = menuChoices.get(optionsName.get(position)).get(i).getMenuChoiceName();
                        stringBuilder.append(name);
                    }else {
                        String name = menuChoices.get(optionsName.get(position)).get(i).getMenuChoiceName();
                        stringBuilder.append("\n"+name);
                    }
                }
            }
        }
        if(!(stringBuilder.toString().equals(""))){ //if choices selected than only show options and choice
            optionName.setText(optionsName.get(position));
            choiceName.setText(stringBuilder.toString());
        }else {  // else don't show
            optionName.setVisibility(View.GONE);
            choiceName.setVisibility(View.GONE);
        }
//        String name = menuChoices.get(optionsName.get(position)).get(position).getMenuChoiceName();
        return view;
    }

    private void initializeData() {
        optionsName = new ArrayList<>();
        Set<String> menuOptions = menuChoices.keySet();
        for(String name:menuOptions){
            optionsName.add(name);
        }
    }
}
