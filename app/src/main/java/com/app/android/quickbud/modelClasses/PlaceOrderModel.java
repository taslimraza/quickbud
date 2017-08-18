package com.app.android.quickbud.modelClasses;

/**
 * Created by vinay on 18/9/15.
 */
public class PlaceOrderModel {

    private String orderExtraName;
    private boolean checkBoxVisibility;
    private boolean radioButtonVisibility;
    private boolean isRadioButtonChecked;
    private boolean isCheckBoxChecked;

    public String getOrderExtraName() {
        return orderExtraName;
    }

    public void setOrderExtraName(String orderExtraName) {
        this.orderExtraName = orderExtraName;
    }

    public boolean getCheckBoxVisibility() {
        return checkBoxVisibility;
    }

    public void setCheckBoxVisibility(boolean checkBoxVisibility) {
        this.checkBoxVisibility = checkBoxVisibility;
    }

    public boolean getRadioButtonVisibility() {
        return radioButtonVisibility;
    }

    public void setRadioButtonVisibility(boolean radioButtonVisibility) {
        this.radioButtonVisibility = radioButtonVisibility;
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
}
