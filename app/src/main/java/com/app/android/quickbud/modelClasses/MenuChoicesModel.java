package com.app.android.quickbud.modelClasses;

/**
 * Created by mobi11 on 16/10/15.
 */
public class MenuChoicesModel {
    private int menuChoiceId;
    private String menuChoiceName;
    private String menuChoicePrice;
    private int isDefault;
    private int isChoiceDefault;
    private int menuOptionId;
    private boolean isRadioButtonChecked;
    private boolean isCheckBoxChecked;
    private int menuChoiceQuantity;

    public int isChoiceDefault() {
        return isChoiceDefault;
    }

    public void setIsChoiceDefault(int isChoiceDefault) {
        this.isChoiceDefault = isChoiceDefault;
    }

    public boolean isRadioButtonChecked() {
        return isRadioButtonChecked;
    }

    public void setIsRadioButtonChecked(boolean isRadioButtonChecked) {
        this.isRadioButtonChecked = isRadioButtonChecked;
    }

    public boolean isCheckBoxChecked() {
        return isCheckBoxChecked;
    }

    public void setIsCheckBoxChecked(boolean isCheckBoxChecked) {
        this.isCheckBoxChecked = isCheckBoxChecked;
    }

    public int getMenuChoiceId() {
        return menuChoiceId;
    }

    public void setMenuChoiceId(int menuChoiceId) {
        this.menuChoiceId = menuChoiceId;
    }

    public String getMenuChoiceName() {
        return menuChoiceName;
    }

    public void setMenuChoiceName(String menuChoiceName) {
        this.menuChoiceName = menuChoiceName;
    }

    public String getMenuChoicePrice() {
        return menuChoicePrice;
    }

    public void setMenuChoicePrice(String menuChoicePrice) {
        this.menuChoicePrice = menuChoicePrice;
    }

    public int isDefault() {
        return isDefault;
    }

    public void setIsDefault(int isDefault) {
        this.isDefault = isDefault;
    }

    public int getMenuOptionId() {
        return menuOptionId;
    }

    public void setMenuOptionId(int menuOptionId) {
        this.menuOptionId = menuOptionId;
    }

    public int getMenuChoiceQuantity() {
        return menuChoiceQuantity;
    }

    public void setMenuChoiceQuantity(int menuChoiceQuantity) {
        this.menuChoiceQuantity = menuChoiceQuantity;
    }
}
