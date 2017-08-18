package com.app.android.quickbud.modelClasses;

import java.util.ArrayList;

/**
 * Created by vinay on 15/9/15.
 */
public class OrderMenuModel {

    private  int menuSectionId;
    private String menuSectionImage;
    private String menuSectionName;
    private String menuSectionCount;
    private int menuItemCount;
    private ArrayList<MenuItemModel> menuItemModels;

    public int getMenuItemCount() {
        return menuItemCount;
    }

    public void setMenuItemCount(int menuItemCount) {
        this.menuItemCount = menuItemCount;
    }

    public int getMenuSectionId() {
        return menuSectionId;
    }

    public void setMenuSectionId(int menuSectionId) {
        this.menuSectionId = menuSectionId;
    }

    public String getMenuSectionImage() {
        return menuSectionImage;
    }

    public void setMenuSectionImage(String menuSectionImage) {
        this.menuSectionImage = menuSectionImage;
    }

    public String getMenuSectionName() {
        return menuSectionName;
    }

    public void setMenuSectionName(String menuSectionName) {
        this.menuSectionName = menuSectionName;
    }

    public String getMenuSectionCount() {
        return menuSectionCount;
    }

    public void setMenuSectionCount(String menuSectionCount) {
        this.menuSectionCount = menuSectionCount;
    }

    public ArrayList<MenuItemModel> getMenuItemModels() {
        return menuItemModels;
    }

    public void setMenuItemModels(ArrayList<MenuItemModel> menuItemModels) {
        this.menuItemModels = menuItemModels;
    }
}
