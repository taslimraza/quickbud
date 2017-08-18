package com.app.android.quickbud.modelClasses;

/**
 * Created by mobi11 on 4/7/16.
 */
public class ImagesModel {

    String ImagesUrl = null;
    String restaurantName = null;
    String restaurantCity = null;
    String restaurantState = null;
    int imageLikes;
    String userPhoneNumber;
    boolean isLiked;
    String patronName;
    String patronImage;
    String imageDate;

    public String getImagesUrl() {
        return ImagesUrl;
    }

    public void setImagesUrl(String imagesUrl) {
        ImagesUrl = imagesUrl;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getRestaurantCity() {
        return restaurantCity;
    }

    public void setRestaurantCity(String restaurantCity) {
        this.restaurantCity = restaurantCity;
    }

    public String getRestaurantState() {
        return restaurantState;
    }

    public void setRestaurantState(String restaurantState) {
        this.restaurantState = restaurantState;
    }

    public int getImageLikes() {
        return imageLikes;
    }

    public void setImageLikes(int imageLikes) {
        this.imageLikes = imageLikes;
    }

    public String getUserPhoneNumber() {
        return userPhoneNumber;
    }

    public void setUserPhoneNumber(String userPhoneNumber) {
        this.userPhoneNumber = userPhoneNumber;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public String getPatronName() {
        return patronName;
    }

    public void setPatronName(String patronName) {
        this.patronName = patronName;
    }

    public String getPatronImage() {
        return patronImage;
    }

    public void setPatronImage(String patronImage) {
        this.patronImage = patronImage;
    }

    public String getImageDate() {
        return imageDate;
    }

    public void setImageDate(String imageDate) {
        this.imageDate = imageDate;
    }
}
