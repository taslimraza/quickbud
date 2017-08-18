package com.app.android.quickbud.modelClasses;

import java.util.ArrayList;

/**
 * Created by mobi11 on 11/4/17.
 */
public class TopBudsListModel {

    private String restaurantName;
    private String menuName;
    private String menuItemName;
    private String menuItemImage;
    private int likesNumber;
    private boolean isLiked;
    private ArrayList<TopBudsDispensaryModel> topBudsDispensaryModels;

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public String getMenuItemName() {
        return menuItemName;
    }

    public void setMenuItemName(String menuItemName) {
        this.menuItemName = menuItemName;
    }

    public String getMenuItemImage() {
        return menuItemImage;
    }

    public void setMenuItemImage(String menuItemImage) {
        this.menuItemImage = menuItemImage;
    }

    public int getLikesNumber() {
        return likesNumber;
    }

    public void setLikesNumber(int likesNumber) {
        this.likesNumber = likesNumber;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public ArrayList<TopBudsDispensaryModel> getTopBudsDispensaryModels() {
        return topBudsDispensaryModels;
    }

    public void setTopBudsDispensaryModels(ArrayList<TopBudsDispensaryModel> topBudsDispensaryModels) {
        this.topBudsDispensaryModels = topBudsDispensaryModels;
    }
}
