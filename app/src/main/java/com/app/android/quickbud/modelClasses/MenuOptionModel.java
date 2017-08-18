package com.app.android.quickbud.modelClasses;

import java.util.ArrayList;

/**
 * Created by mobi11 on 13/10/15.
 */
public class MenuOptionModel {

    private int menuOptionId;
    private String menuOptionName;
    private String menuOptionPrice;
    private int menuItemId;
    private int isMultiSelect;
    private int isMultiQuantity;
    private boolean checkBoxVisibility;
    private boolean radioButtonVisibility;
    private String specialRequest;
    private ArrayList<MenuChoicesModel> menuChoicesModels;

    public boolean isRadioButtonVisibility() {
        return radioButtonVisibility;
    }

    public void setRadioButtonVisibility(boolean radioButtonVisibility) {
        this.radioButtonVisibility = radioButtonVisibility;
    }

    public boolean isCheckBoxVisibility() {
        return checkBoxVisibility;
    }

    public void setCheckBoxVisibility(boolean checkBoxVisibility) {
        this.checkBoxVisibility = checkBoxVisibility;
    }

    public int getMenuItemId() {
        return menuItemId;
    }

    public void setMenuItemId(int menuItemId) {
        this.menuItemId = menuItemId;
    }

    public String getMenuOptionPrice() {
        return menuOptionPrice;
    }

    public void setMenuOptionPrice(String menuOptionPrice) {
        this.menuOptionPrice = menuOptionPrice;
    }

    public String getMenuOptionName() {
        return menuOptionName;
    }

    public void setMenuOptionName(String menuOptionName) {
        this.menuOptionName = menuOptionName;
    }

    public int getMenuOptionId() {
        return menuOptionId;
    }

    public void setMenuOptionId(int menuOptionId) {
        this.menuOptionId = menuOptionId;
    }

    public int isMultiSelect() {
        return isMultiSelect;
    }

    public void setIsMultiSelect(int isMultiSelect) {
        this.isMultiSelect = isMultiSelect;
    }

    public String getSpecialRequest() {
        return specialRequest;
    }

    public void setSpecialRequest(String specialRequest) {
        this.specialRequest = specialRequest;
    }

    public ArrayList<MenuChoicesModel> getMenuChoicesModels() {
        return menuChoicesModels;
    }

    public void setMenuChoicesModels(ArrayList<MenuChoicesModel> menuChoicesModels) {
        this.menuChoicesModels = menuChoicesModels;
    }

    public int getIsMultiQuantity() {
        return isMultiQuantity;
    }

    public void setIsMultiQuantity(int isMultiQuantity) {
        this.isMultiQuantity = isMultiQuantity;
    }
}
