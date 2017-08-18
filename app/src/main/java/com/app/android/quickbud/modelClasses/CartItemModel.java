package com.app.android.quickbud.modelClasses;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by mobi11 on 19/10/15.
 */
public class CartItemModel {
    private String itemName;
    private int itemId;
    private String itemPrice;
    private String calculatedItemPrice;
    private String itemQuantity;
    private String specialRequest;
    private int tenantId = 0;
    private int locationId = 0;
    private ArrayList<MenuOptionModel> menuOptionModels;
    private LinkedHashMap<String,ArrayList<MenuChoicesModel>> menuChoicesHashMap;

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(String itemPrice) {
        this.itemPrice = itemPrice;
    }

    public String getItemQuantity() {
        return itemQuantity;
    }

    public void setItemQuantity(String itemQuantity) {
        this.itemQuantity = itemQuantity;
    }

    public LinkedHashMap<String, ArrayList<MenuChoicesModel>> getMenuChoicesHashMap() {
        return menuChoicesHashMap;
    }

    public void setMenuChoicesHashMap(LinkedHashMap<String, ArrayList<MenuChoicesModel>> menuChoicesHashMap) {
        this.menuChoicesHashMap = menuChoicesHashMap;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getCalculatedItemPrice() {
        return calculatedItemPrice;
    }

    public void setCalculatedItemPrice(String calculatedItemPrice) {
        this.calculatedItemPrice = calculatedItemPrice;
    }

    public ArrayList<MenuOptionModel> getMenuOptionModels() {
        return menuOptionModels;
    }

    public void setMenuOptionModels(ArrayList<MenuOptionModel> menuOptionModels) {
        this.menuOptionModels = menuOptionModels;
    }

    public int getTenantId() {
        return tenantId;
    }

    public void setTenantId(int tenantId) {
        this.tenantId = tenantId;
    }

    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public String getSpecialRequest() {
        return specialRequest;
    }

    public void setSpecialRequest(String specialRequest) {
        this.specialRequest = specialRequest;
    }
}
