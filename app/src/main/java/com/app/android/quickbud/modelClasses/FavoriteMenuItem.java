package com.app.android.quickbud.modelClasses;

import java.util.ArrayList;

/**
 * Created by mobi11 on 8/2/16.
 */
public class FavoriteMenuItem {

    private int favoriteId = 0;
    private int tenantId;
    private int locationId;
    private int menuItemId;
    private int patronId;
    private String status;
    private ArrayList<FavoriteMenuItemChoices> menuItemChoices = null;

    public int getFavoriteId() {
        return favoriteId;
    }

    public void setFavoriteId(int favoriteId) {
        this.favoriteId = favoriteId;
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

    public int getMenuItemId() {
        return menuItemId;
    }

    public void setMenuItemId(int menuItemId) {
        this.menuItemId = menuItemId;
    }

    public int getPatronId() {
        return patronId;
    }

    public void setPatronId(int patronId) {
        this.patronId = patronId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<FavoriteMenuItemChoices> getMenuItemChoices() {
        return menuItemChoices;
    }

    public void setMenuItemChoices(ArrayList<FavoriteMenuItemChoices> menuItemChoices) {
        this.menuItemChoices = menuItemChoices;
    }
}
