package com.app.android.quickbud.modelClasses;

import java.util.ArrayList;

/**
 * Created by mobi11 on 27/9/15.
 */
public class MenuItemModel {

    private Integer menuItemId;
    private String menuItemImage;
    private String menuItemName;
    private String menuItemDescription = null;
    private String menuItemPrice;
    private Integer menuSectionId;
    private Integer itemQuantity;
    private String specialRequest;
    private boolean isFavorite = false;
    private int favoriteId = 0;
    private String thc;
    private String cbd;
    private int likesCount;
    private ArrayList<MenuOptionModel> menuOptionModels;
    private boolean isTopBudLiked;

    public String getSpecialRequest() {
        return specialRequest;
    }

    public void setSpecialRequest(String specialRequest) {
        this.specialRequest = specialRequest;
    }

    public Integer getMenuItemId() {
        return menuItemId;
    }

    public void setMenuItemId(Integer menuItemId) {
        this.menuItemId = menuItemId;
    }

    public Integer getMenuSectionId() {
        return menuSectionId;
    }

    public void setMenuSectionId(Integer menuSectionId) {
        this.menuSectionId = menuSectionId;
    }

    public String getMenuItemImage() {
        return menuItemImage;
    }

    public void setMenuItemImage(String menuItemImage) {
        this.menuItemImage = menuItemImage;
    }

    public String getMenuItemName() {
        return menuItemName;
    }

    public void setMenuItemName(String menuItemName) {
        this.menuItemName = menuItemName;
    }

    public String getMenuItemPrice() {
        return menuItemPrice;
    }

    public void setMenuItemPrice(String menuItemPrice) {
        this.menuItemPrice = menuItemPrice;
    }

    public String getMenuItemDescription() {
        return menuItemDescription;
    }

    public void setMenuItemDescription(String menuItemDescription) {
        this.menuItemDescription = menuItemDescription;
    }

    public Integer getItemQuantity() {
        return itemQuantity;
    }

    public void setItemQuantity(Integer itemQuantity) {
        this.itemQuantity = itemQuantity;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public ArrayList<MenuOptionModel> getMenuOptionModels() {
        return menuOptionModels;
    }

    public void setMenuOptionModels(ArrayList<MenuOptionModel> menuOptionModels) {
        this.menuOptionModels = menuOptionModels;
    }

    public int getFavoriteId() {
        return favoriteId;
    }

    public void setFavoriteId(int favoriteId) {
        this.favoriteId = favoriteId;
    }

    public String getThc() {
        return thc;
    }

    public void setThc(String thc) {
        this.thc = thc;
    }

    public String getCbd() {
        return cbd;
    }

    public void setCbd(String cbd) {
        this.cbd = cbd;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public boolean isTopBudLiked() {
        return isTopBudLiked;
    }

    public void setTopBudLiked(boolean topBudLiked) {
        isTopBudLiked = topBudLiked;
    }
}
