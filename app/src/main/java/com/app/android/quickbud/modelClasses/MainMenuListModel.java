package com.app.android.quickbud.modelClasses;

/**
 * Created by mobi11 on 26/9/16.
 */
public class MainMenuListModel {
    private int mainMenuImage;
    private String mainMenuTitle;
    private String mainMenuDescription;
    private boolean isSelected;

    public int getMainMenuImage() {
        return mainMenuImage;
    }

    public void setMainMenuImage(int mainMenuImage) {
        this.mainMenuImage = mainMenuImage;
    }

    public String getMainMenuTitle() {
        return mainMenuTitle;
    }

    public void setMainMenuTitle(String mainMenuTitle) {
        this.mainMenuTitle = mainMenuTitle;
    }

    public String getMainMenuDescription() {
        return mainMenuDescription;
    }

    public void setMainMenuDescription(String mainMenuDescription) {
        this.mainMenuDescription = mainMenuDescription;
    }
    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
