package com.app.android.quickbud.modelClasses;

/**
 * Created by mobi11 on 3/10/16.
 */
public class CategoryModel {

    String categoryName;
    boolean isSelected;

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
