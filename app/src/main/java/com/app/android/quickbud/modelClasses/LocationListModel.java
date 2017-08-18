package com.app.android.quickbud.modelClasses;

import java.util.ArrayList;

/**
 * Created by mobi11 on 1/10/15.
 */
public class LocationListModel {

    private String restaurantImage;
    private String restaurantName = null;
    private String restaurantAddress;
    private String restaurantCity;
    private String restaurantState;
    private String restaurantPhone;
    private Double restaurantDistance;
    private int distanceValue;
    private String restaurantEWT = null;
    private String restaurantChatAvailable = null;
    private String placeID = null;
    private int locationID;
    private int tenantID = 0;
    private String pageToken = null;
    private String restaurantOpenTiming;
    private String restaurantCloseTiming;
    private boolean qtSupported;
    private String restaurantType;
    private ArrayList<LocationListModel> restaurantListModels;
    private String restaurantAccountName;
    private String restaurantMenuUrl;
    private boolean isHostessOnline = false;
    private boolean isCarryOut = false;
    private boolean isDisplayLogo = false;
    private boolean isGetInLine = false;
    private boolean isPreOrder = false;
    private boolean isInteractiveMenu = false;
    private String timezone = null;
    private String ratingImage;
    private double restRating;
    private boolean isFavoriteRestaurant;
    private boolean isDealActive = false;
    private ArrayList<RestaurantTimingsModel> restaurantTimings;
    private boolean isAtm = false;
    private boolean isMedical = false;
    private boolean isRecreational = false;
    private boolean isPickUpAvailable;
    private boolean isDeliveryAvailable;
    private boolean isMenuAvailable;

    public String getRestaurantCloseTiming() {
        return restaurantCloseTiming;
    }

    public void setRestaurantCloseTiming(String restaurantCloseTiming) {
        this.restaurantCloseTiming = restaurantCloseTiming;
    }

    public String getRestaurantOpenTiming() {
        return restaurantOpenTiming;
    }

    public void setRestaurantOpenTiming(String restaurantOpenTiming) {
        this.restaurantOpenTiming = restaurantOpenTiming;
    }

    public String getPageToken() {
        return pageToken;
    }

    public void setPageToken(String pageToken) {
        this.pageToken = pageToken;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    private double latitude;
    private double longitude;


    public String getRestaurantImage() {
        return restaurantImage;
    }

    public void setRestaurantImage(String restaurantImage) {
        this.restaurantImage = restaurantImage;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getRestaurantAddress() {
        return restaurantAddress;
    }

    public void setRestaurantAddress(String restaurantAddress) {
        this.restaurantAddress = restaurantAddress;
    }

    public String getRestaurantPhone() {
        return restaurantPhone;
    }

    public void setRestaurantPhone(String restaurantPhone) {
        this.restaurantPhone = restaurantPhone;
    }

    public Double getRestaurantDistance() {
        return restaurantDistance;
    }

    public void setRestaurantDistance(Double restaurantDistance) {
        this.restaurantDistance = restaurantDistance;
    }

    public String getRestaurantEWT() {
        return restaurantEWT;
    }

    public void setRestaurantEWT(String restaurantEWT) {
        this.restaurantEWT = restaurantEWT;
    }

    public String getRestaurantChatAvailable() {
        return restaurantChatAvailable;
    }

    public void setRestaurantChatAvailable(String restaurantChatAvailable) {
        this.restaurantChatAvailable = restaurantChatAvailable;
    }

    public String getPlaceID() {
        return placeID;
    }

    public void setPlaceID(String placeID) {
        this.placeID = placeID;
    }

    public int getLocationID() {
        return locationID;
    }

    public void setLocationID(int locationID) {
        this.locationID = locationID;
    }

    public int getTenantID() {
        return tenantID;
    }

    public void setTenantID(int tenantID) {
        this.tenantID = tenantID;
    }

    public int getDistanceValue() {
        return distanceValue;
    }

    public void setDistanceValue(int distanceValue) {
        this.distanceValue = distanceValue;
    }

    public ArrayList<LocationListModel> getRestaurantListModels() {
        return restaurantListModels;
    }

    public void setRestaurantListModels(ArrayList<LocationListModel> restaurantListModels) {
        this.restaurantListModels = restaurantListModels;
    }

    public boolean isQtSupported() {
        return qtSupported;
    }

    public void setQtSupported(boolean qtSupported) {
        this.qtSupported = qtSupported;
    }

    public String getRestaurantType() {
        return restaurantType;
    }

    public void setRestaurantType(String restaurantType) {
        this.restaurantType = restaurantType;
    }

    public String getRestaurantState() {
        return restaurantState;
    }

    public void setRestaurantState(String restaurantState) {
        this.restaurantState = restaurantState;
    }

    public String getRestaurantCity() {
        return restaurantCity;
    }

    public void setRestaurantCity(String restaurantCity) {
        this.restaurantCity = restaurantCity;
    }

    public String getRestaurantMenuUrl() {
        return restaurantMenuUrl;
    }

    public void setRestaurantMenuUrl(String restaurantMenuUrl) {
        this.restaurantMenuUrl = restaurantMenuUrl;
    }

    public boolean isHostessOnline() {
        return isHostessOnline;
    }

    public void setHostessOnline(boolean hostessOnline) {
        isHostessOnline = hostessOnline;
    }

    public String getRestaurantAccountName() {
        return restaurantAccountName;
    }

    public void setRestaurantAccountName(String restaurantAccountName) {
        this.restaurantAccountName = restaurantAccountName;
    }

    public boolean isCarryOut() {
        return isCarryOut;
    }

    public void setCarryOut(boolean carryOut) {
        isCarryOut = carryOut;
    }

    public boolean isDisplayLogo() {
        return isDisplayLogo;
    }

    public void setDisplayLogo(boolean displayLogo) {
        isDisplayLogo = displayLogo;
    }

    public boolean isGetInLine() {
        return isGetInLine;
    }

    public void setGetInLine(boolean getInLine) {
        isGetInLine = getInLine;
    }

    public boolean isPreOrder() {
        return isPreOrder;
    }

    public void setPreOrder(boolean preOrder) {
        isPreOrder = preOrder;
    }

    public boolean isInteractiveMenu() {
        return isInteractiveMenu;
    }

    public void setInteractiveMenu(boolean interactiveMenu) {
        isInteractiveMenu = interactiveMenu;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getRatingImage() {
        return ratingImage;
    }

    public void setRatingImage(String ratingImage) {
        this.ratingImage = ratingImage;
    }

    public double getRestRating() {
        return restRating;
    }

    public void setRestRating(double restRating) {
        this.restRating = restRating;
    }

    public boolean isFavoriteRestaurant() {
        return isFavoriteRestaurant;
    }

    public void setFavoriteRestaurant(boolean favoriteRestaurant) {
        isFavoriteRestaurant = favoriteRestaurant;
    }

    public boolean isDealActive() {
        return isDealActive;
    }

    public void setDealActive(boolean dealActive) {
        isDealActive = dealActive;
    }

    public ArrayList<RestaurantTimingsModel> getRestaurantTimings() {
        return restaurantTimings;
    }

    public void setRestaurantTimings(ArrayList<RestaurantTimingsModel> restaurantTimings) {
        this.restaurantTimings = restaurantTimings;
    }

    public boolean isAtm() {
        return isAtm;
    }

    public void setAtm(boolean atm) {
        isAtm = atm;
    }

    public boolean isMedical() {
        return isMedical;
    }

    public void setMedical(boolean medical) {
        isMedical = medical;
    }

    public boolean isRecreational() {
        return isRecreational;
    }

    public void setRecreational(boolean recreational) {
        isRecreational = recreational;
    }

    public boolean isPickUpAvailable() {
        return isPickUpAvailable;
    }

    public void setPickUpAvailable(boolean pickUpAvailable) {
        isPickUpAvailable = pickUpAvailable;
    }

    public boolean isDeliveryAvailable() {
        return isDeliveryAvailable;
    }

    public void setDeliveryAvailable(boolean deliveryAvailable) {
        isDeliveryAvailable = deliveryAvailable;
    }

    public boolean isMenuAvailable() {
        return isMenuAvailable;
    }

    public void setMenuAvailable(boolean menuAvailable) {
        isMenuAvailable = menuAvailable;
    }
}
