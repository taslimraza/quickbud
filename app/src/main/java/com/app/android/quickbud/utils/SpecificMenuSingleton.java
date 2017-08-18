package com.app.android.quickbud.utils;

import com.app.android.quickbud.modelClasses.LocationListModel;
import com.app.android.quickbud.modelClasses.MenuChoicesModel;
import com.app.android.quickbud.modelClasses.RestaurantTimingsModel;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by mobi11 on 3/11/15.
 */
public class SpecificMenuSingleton {
    private static SpecificMenuSingleton specificMenuSingleton = null;
    int locationId,tenantId,orderId,visitId = 0, position, restPosition;
    double latitude, longitude, lastLatitude, lastLongitude;
    String restaurantAddress, restaurantName, imageUrl, ewt = null, placeId, chatPlaceID = null;
    ArrayList<LocationListModel> restaurantList = null, totalRestaurantList = null;
    ArrayList<RestaurantTimingsModel> restaurantTimingsModels;
    String lastAddress = null;
    private boolean isQtSupported;
    Map<String, ArrayList<MenuChoicesModel>> menuChoicesHashMap = null;
    private LocationListModel clickedRestaurant = null;
//    private MultiUserChatManager manager;
//    private XMPPTCPConnection connection = null;


    private SpecificMenuSingleton(){
    }

    public static SpecificMenuSingleton getInstance(){
        if(specificMenuSingleton == null){
            specificMenuSingleton = new SpecificMenuSingleton();
        }
        return specificMenuSingleton;
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

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getEwt() {
        return ewt;
    }

    public void setEwt(String ewt) {
        this.ewt = ewt;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getVisitId() {
        return visitId;
    }

    public void setVisitId(int visitId) {
        this.visitId = visitId;
    }

    public void setLocationId(int id){
        locationId = id;
    }

    public int getLocationId(){
        return locationId;
    }

    public void setTenantId(int id){
        tenantId = id;
    }

    public int getTenantId(){
        return tenantId;
    }

    public ArrayList<LocationListModel> getRestaurantList() {
        return restaurantList;
    }

    public void setRestaurantList(ArrayList<LocationListModel> restaurantList) {
        this.restaurantList = restaurantList;
    }

    public String getLastAddress() {
        return lastAddress;
    }

    public void setLastAddress(String lastAddress) {
        this.lastAddress = lastAddress;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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

    public double getLastLatitude() {
        return lastLatitude;
    }

    public void setLastLatitude(double lastLatitude) {
        this.lastLatitude = lastLatitude;
    }

    public double getLastLongitude() {
        return lastLongitude;
    }

    public void setLastLongitude(double lastLongitude) {
        this.lastLongitude = lastLongitude;
    }

    public Map<String, ArrayList<MenuChoicesModel>> getMenuChoicesHashMap() {
        return menuChoicesHashMap;
    }

    public void setMenuChoicesHashMap(Map<String, ArrayList<MenuChoicesModel>> menuChoicesHashMap) {
        this.menuChoicesHashMap = menuChoicesHashMap;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getChatPlaceID() {
        return chatPlaceID;
    }

    public void setChatPlaceID(String chatPlaceID) {
        this.chatPlaceID = chatPlaceID;
    }

    public boolean isQtSupported() {
        return isQtSupported;
    }

    public void setQtSupported(boolean qtSupported) {
        isQtSupported = qtSupported;
    }

    public int getRestPosition() {
        return restPosition;
    }

    public void setRestPosition(int restPosition) {
        this.restPosition = restPosition;
    }

    public ArrayList<LocationListModel> getTotalRestaurantList() {
        return totalRestaurantList;
    }

    public void setTotalRestaurantList(ArrayList<LocationListModel> totalRestaurantList) {
        this.totalRestaurantList = totalRestaurantList;
    }

    public LocationListModel getClickedRestaurant() {
        return clickedRestaurant;
    }

    public void setClickedRestaurant(LocationListModel clickedRestaurant) {
        this.clickedRestaurant = clickedRestaurant;
    }

//    public MultiUserChatManager getManager() {
//        return manager;
//    }
//
//    public void setManager(MultiUserChatManager manager) {
//        this.manager = manager;
//    }
//
//    public XMPPTCPConnection getConnection() {
//        return connection;
//    }
//
//    public void setConnection(XMPPTCPConnection connection) {
//        this.connection = connection;
//    }

    public ArrayList<RestaurantTimingsModel> getRestaurantTimingsModels() {
        return restaurantTimingsModels;
    }

    public void setRestaurantTimingsModels(ArrayList<RestaurantTimingsModel> restaurantTimingsModels) {
        this.restaurantTimingsModels = restaurantTimingsModels;
    }
}
