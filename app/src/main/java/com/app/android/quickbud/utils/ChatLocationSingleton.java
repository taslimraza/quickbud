package com.app.android.quickbud.utils;

/**
 * Created by mobi11 on 2/2/16.
 */
public class ChatLocationSingleton {

    public static ChatLocationSingleton chatLocationSingleton = null;
    double currentLatitude, currentLongitude, restLatitude, restLongitude;

    public ChatLocationSingleton() {
    }

    public static ChatLocationSingleton getInstance(){
        if(chatLocationSingleton == null){
            chatLocationSingleton = new ChatLocationSingleton();
        }
        return chatLocationSingleton;
    }

    public double getCurrentLatitude() {
        return currentLatitude;
    }

    public void setCurrentLatitude(double currentLatitude) {
        this.currentLatitude = currentLatitude;
    }

    public double getCurrentLongitude() {
        return currentLongitude;
    }

    public void setCurrentLongitude(double currentLongitude) {
        this.currentLongitude = currentLongitude;
    }

    public double getRestLatitude() {
        return restLatitude;
    }

    public void setRestLatitude(double restLatitude) {
        this.restLatitude = restLatitude;
    }

    public double getRestLongitude() {
        return restLongitude;
    }

    public void setRestLongitude(double restLongitude) {
        this.restLongitude = restLongitude;
    }
}
