package com.app.android.quickbud.modelClasses;

/**
 * Created by mobi11 on 19/4/17.
 */
public class TopBudsDispensaryModel {

    private String dispensaryName;
    private String placeId;
    private int tenantId;
    private int locationId;
    private int likesCount;
    private int menuId;
    private int patronId;
    private boolean isLiked;
    private String dispensaryAddress;
    private String dispensaryCity;
    private String dispensaryState;

    public String getDispensaryName() {
        return dispensaryName;
    }

    public void setDispensaryName(String dispensaryName) {
        this.dispensaryName = dispensaryName;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
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

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public int getMenuId() {
        return menuId;
    }

    public void setMenuId(int menuId) {
        this.menuId = menuId;
    }

    public int getPatronId() {
        return patronId;
    }

    public void setPatronId(int patronId) {
        this.patronId = patronId;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public String getDispensaryAddress() {
        return dispensaryAddress;
    }

    public void setDispensaryAddress(String dispensaryAddress) {
        this.dispensaryAddress = dispensaryAddress;
    }

    public String getDispensaryCity() {
        return dispensaryCity;
    }

    public void setDispensaryCity(String dispensaryCity) {
        this.dispensaryCity = dispensaryCity;
    }

    public String getDispensaryState() {
        return dispensaryState;
    }

    public void setDispensaryState(String dispensaryState) {
        this.dispensaryState = dispensaryState;
    }
}
