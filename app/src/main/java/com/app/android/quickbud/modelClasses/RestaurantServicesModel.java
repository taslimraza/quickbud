package com.app.android.quickbud.modelClasses;

/**
 * Created by mobi11 on 7/6/17.
 */
public class RestaurantServicesModel {

    private int image;
    private String titleList;
    private String subTitleList;
    private boolean isSelected;

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getTitleList() {
        return titleList;
    }

    public void setTitleList(String titleList) {
        this.titleList = titleList;
    }

    public String getSubTitleList() {
        return subTitleList;
    }

    public void setSubTitleList(String subTitleList) {
        this.subTitleList = subTitleList;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
