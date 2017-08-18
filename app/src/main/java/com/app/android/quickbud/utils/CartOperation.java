package com.app.android.quickbud.utils;

/**
 * Created by mobi11 on 19/10/15.
 */
public class CartOperation {
    private Integer itemCount = 0;

    public void addToCart(int count){
        itemCount = count;
        itemCount++;
    }

    public Integer getCartCount(){
        return itemCount;
    }
}
