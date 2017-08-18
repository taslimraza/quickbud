package com.app.android.quickbud.modelClasses;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by mobi11 on 13/10/15.
 */
public class MenuDetailsModel {
    private static MenuDetailsModel menuModel = null;
    private ArrayList<OrderMenuModel> orderMenuModels;
    private ArrayList<MenuItemModel> menuItemModels;
    private ArrayList<MenuOptionModel> menuOptionModels;
    private ArrayList<MenuChoicesModel> menuChoicesModels;
    private MenuDetailsModel menuDetailsModel;
    private HashMap<String, ArrayList<MenuChoicesModel>> menuChoicesHashMap = null;
    private ArrayList<HashMap<String, ArrayList<MenuChoicesModel>>> menuChoicesArrayList = null;

    private MenuDetailsModel(){

    }

    public static MenuDetailsModel getInstance(){
        if(menuModel == null){
            menuModel = new MenuDetailsModel();
        }
        return menuModel;
    }

    public MenuDetailsModel getMenuDetailsModel() {
        return menuDetailsModel;
    }

    public void setMenuDetailsModel(MenuDetailsModel menuDetailsModel) {
        this.menuDetailsModel = menuDetailsModel;
    }

    public ArrayList<MenuChoicesModel> getMenuChoicesModels() {
        return menuChoicesModels;
    }

    public void setMenuChoicesModels(ArrayList<MenuChoicesModel> menuChoicesModels) {
        this.menuChoicesModels = menuChoicesModels;
    }

    public ArrayList<MenuOptionModel> getMenuOptionModels() {
        return menuOptionModels;
    }

    public void setMenuOptionModels(ArrayList<MenuOptionModel> menuOptionModels) {
        this.menuOptionModels = menuOptionModels;
    }

    public ArrayList<MenuItemModel> getMenuItemModels() {
        return menuItemModels;
    }

    public void setMenuItemModels(ArrayList<MenuItemModel> menuItemModels) {
        this.menuItemModels = menuItemModels;
    }

    public ArrayList<OrderMenuModel> getOrderMenuModels() {
        return orderMenuModels;
    }

    public void setOrderMenuModels(ArrayList<OrderMenuModel> orderMenuModels) {
        this.orderMenuModels = orderMenuModels;
    }

    public HashMap<String, ArrayList<MenuChoicesModel>> getMenuChoicesHashMap() {
        return menuChoicesHashMap;
    }

    public void setMenuChoicesHashMap(HashMap<String, ArrayList<MenuChoicesModel>> menuChoicesHashMap) {
        this.menuChoicesHashMap = menuChoicesHashMap;
    }

    public ArrayList<HashMap<String, ArrayList<MenuChoicesModel>>> getMenuChoicesArrayList() {
        return menuChoicesArrayList;
    }

    public void setMenuChoicesArrayList(ArrayList<HashMap<String, ArrayList<MenuChoicesModel>>> menuChoicesArrayList) {
        this.menuChoicesArrayList = menuChoicesArrayList;
    }

    public void clearMenuItem(){
        if (menuItemModels != null) {
            menuItemModels.clear();
        }
    }
}
