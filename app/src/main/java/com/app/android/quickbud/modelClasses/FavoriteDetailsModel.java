package com.app.android.quickbud.modelClasses;

import java.util.ArrayList;

/**
 * Created by mobi11 on 16/11/15.
 */
public class FavoriteDetailsModel {

    private int orderId;
    private ArrayList<CartItemModel> cartItemModels;

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public ArrayList<CartItemModel> getCartItemModels() {
        return cartItemModels;
    }

    public void setCartItemModels(ArrayList<CartItemModel> cartItemModels) {
        this.cartItemModels = cartItemModels;
    }
}
