package com.app.android.quickbud.modelClasses;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by mobi11 on 8/2/16.
 */
public class FavoriteModel {

    private MenuItemModel menuItemModel;
    private ArrayList<MenuOptionModel> menuOptionModels;
    private ArrayList<MenuChoicesModel> menuChoicesModels;
    private LinkedHashMap<String, ArrayList<MenuChoicesModel>> menuChoicesHashMap = null;

    public MenuItemModel getMenuItemModel() {
        return menuItemModel;
    }

    public void setMenuItemModel(MenuItemModel menuItemModel) {
        this.menuItemModel = menuItemModel;
    }

    public ArrayList<MenuOptionModel> getMenuOptionModels() {
        return menuOptionModels;
    }

    public void setMenuOptionModels(ArrayList<MenuOptionModel> menuOptionModels) {
        this.menuOptionModels = menuOptionModels;
    }

    public ArrayList<MenuChoicesModel> getMenuChoicesModels() {
        return menuChoicesModels;
    }

    public void setMenuChoicesModels(ArrayList<MenuChoicesModel> menuChoicesModels) {
        this.menuChoicesModels = menuChoicesModels;
    }

    public LinkedHashMap<String, ArrayList<MenuChoicesModel>> getMenuChoicesHashMap() {
        return menuChoicesHashMap;
    }

    public void setMenuChoicesHashMap(LinkedHashMap<String, ArrayList<MenuChoicesModel>> menuChoicesHashMap) {
        this.menuChoicesHashMap = menuChoicesHashMap;
    }
}
