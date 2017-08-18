package com.app.android.quickbud.utils;

import android.content.Context;
import android.widget.Toast;

import com.app.android.quickbud.modelClasses.CartItemModel;
import com.app.android.quickbud.modelClasses.MenuChoicesModel;
import com.app.android.quickbud.modelClasses.MenuOptionModel;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by mobi11 on 24/10/15.
 */
public class CartSingleton {
    private static CartSingleton cartSingleton = null;
    private ArrayList<CartItemModel> cartItemModels = new ArrayList<>();
    private Integer itemTotalCount = 0;
    private SpecificMenuSingleton menuSingleton;

    private CartSingleton() {
    }

    public static CartSingleton getInstance() {
        if (cartSingleton == null) {
            cartSingleton = new CartSingleton();
        }
        return cartSingleton;
    }

    public void addToCart(String itemName, String itemPrice, Integer itemQuantity, int itemId, Context context) {
        CartItemModel cartItemModel = new CartItemModel();
        menuSingleton = SpecificMenuSingleton.getInstance();
        for (int i = 0; i < cartItemModels.size(); i++) {
            // if item already present in cart then increment the item quantity
            if (cartItemModels.get(i).getItemId() == itemId) {
                if (Integer.parseInt(cartItemModels.get(i).getItemQuantity())<99){
                    if (itemQuantity == 1){
                        // if item added from favorites
                        itemTotalCount += itemQuantity + Integer.parseInt(cartItemModels.get(i).getItemQuantity());
                        cartItemModels.get(i).setItemQuantity(String.valueOf(itemQuantity + Integer.parseInt(cartItemModels.get(i).getItemQuantity())));
                    }else {
                        itemTotalCount += itemQuantity - Integer.parseInt(cartItemModels.get(i).getItemQuantity());
                        cartItemModels.get(i).setItemQuantity(itemQuantity.toString());
                    }
                }else {
                    Toast.makeText(context, "Maximum quantity for a menu item is reached!", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
        itemTotalCount += itemQuantity;
        cartItemModel.setItemName(itemName);
        cartItemModel.setItemPrice(itemPrice);
        cartItemModel.setItemId(itemId);
        cartItemModel.setItemQuantity(itemQuantity.toString());
        cartItemModel.setCalculatedItemPrice(itemPrice);
        cartItemModel.setTenantId(menuSingleton.getClickedRestaurant().getTenantID());
        cartItemModel.setLocationId(menuSingleton.getClickedRestaurant().getLocationID());
        cartItemModels.add(cartItemModel);
    }

    public String getIndividualItemQuantity(int position) {
        if (cartItemModels.get(position) != null) {
            if (cartItemModels.get(position).getItemQuantity() != null) {
                return cartItemModels.get(position).getItemQuantity();
            }
        }
//        for(int i=0; i< cartItemModels.size(); i++){
//            if(cartItemModels.get(i).getItemQuantity() != null){
//                return cartItemModels.get(position).getItemQuantity();
//            }
//        }
        return null;
    }

    public void removeFromCart(String itemName, String itemPrice, Integer itemQuantity) {
        for (int i = 0; i < cartItemModels.size(); i++) {
            if (cartItemModels.get(i).getItemName().equals(itemName)) {
                if (Integer.parseInt(cartItemModels.get(i).getItemQuantity()) > 1) {
                    itemTotalCount += itemQuantity - Integer.parseInt(cartItemModels.get(i).getItemQuantity());
                    cartItemModels.get(i).setItemQuantity(itemQuantity.toString());
                } else {
                    itemTotalCount += itemQuantity - Integer.parseInt(cartItemModels.get(i).getItemQuantity());
//                    cartItemModels.get(i).setItemQuantity(itemQuantity.toString());
                    cartItemModels.remove(i);
                }
                return;
            }
        }
    }

    public void addToCartWithOptions(String specialRequest, String itemName, String itemPrice, Integer menuItemId,
                                     Map<String, ArrayList<MenuChoicesModel>> menuChoicesHashMap,
                                     ArrayList<MenuOptionModel> optionModels) {
        itemTotalCount += 1;
        CartItemModel cartItemModel = new CartItemModel();
        cartItemModel.setItemName(itemName);
        cartItemModel.setItemQuantity("1");
        cartItemModel.setItemId(menuItemId);
        cartItemModel.setItemPrice(itemPrice);
        cartItemModel.setSpecialRequest(specialRequest);
        cartItemModel.setMenuOptionModels(optionModels);
        cartItemModel.setTenantId(SpecificMenuSingleton.getInstance().getClickedRestaurant().getTenantID());
        cartItemModel.setLocationId(SpecificMenuSingleton.getInstance().getClickedRestaurant().getLocationID());
        DecimalFormat df = new DecimalFormat(".##");
        LinkedHashMap<String, ArrayList<MenuChoicesModel>> choices = new LinkedHashMap<>();
//        choices.putAll(menuChoicesHashMap);
//        menuChoices = menuChoicesHashMap;
        if (menuChoicesHashMap != null) {
            ArrayList<String> optionsName = initializeData(menuChoicesHashMap);
            for (int i = 0; i < optionsName.size(); i++) {
                for (int j = 0; j < menuChoicesHashMap.get(optionsName.get(i)).size(); j++) {
                    // loop till size of choices

                    if (optionModels.get(i).getIsMultiQuantity() == 1) {

                        String choicePrice = menuChoicesHashMap.get(optionsName.get(i)).get(j).getMenuChoicePrice();
                        int quantity = menuChoicesHashMap.get(optionsName.get(i)).get(j).getMenuChoiceQuantity();
                        String itemCost = cartItemModel.getItemPrice();
                        Double calculatedPrice = (Double.parseDouble(choicePrice) * quantity) + Double.parseDouble(itemCost);
                        Double calculatePrice = Double.valueOf(df.format(calculatedPrice));
//                        cartItemModel.setItemPrice(calculatePrice.toString());
                        cartItemModel.setItemPrice(String.format("%.2f", calculatePrice));

                    } else {

                        if (menuChoicesHashMap.get(optionsName.get(i)).get(j).isCheckBoxChecked() ||
                                menuChoicesHashMap.get(optionsName.get(i)).get(j).isDefault() == 1) {
                            String choicePrice = menuChoicesHashMap.get(optionsName.get(i)).get(j).getMenuChoicePrice();
                            String itemCost = cartItemModel.getItemPrice();
                            Double calculatedPrice = Double.parseDouble(choicePrice) + Double.parseDouble(itemCost);
                            Double calculatePrice = Double.valueOf(df.format(calculatedPrice));
                            cartItemModel.setItemPrice(calculatePrice.toString());
                        } else if (menuChoicesHashMap.get(optionsName.get(i)).get(j).isRadioButtonChecked() ||
                                menuChoicesHashMap.get(optionsName.get(i)).get(j).isDefault() == 1) {
                            if (menuChoicesHashMap.get(optionsName.get(i)).get(j).isChoiceDefault() == 1) {
                                // if radio button and default set, don't add the price
                                String itemCost = cartItemModel.getItemPrice();
                                cartItemModel.setItemPrice(itemCost);
                            } else {
                                String choicePrice = menuChoicesHashMap.get(optionsName.get(i)).get(j).getMenuChoicePrice();
                                String itemCost = cartItemModel.getItemPrice();
                                Double calculatedPrice = Double.parseDouble(choicePrice) + Double.parseDouble(itemCost);
//                            Double calculatePrice = Double.valueOf(df.format(calculatedPrice));
                                String cost = String.format("%.2f", calculatedPrice);
                                cartItemModel.setItemPrice(cost);
                            }
                        }

                    }
                }
            }

            for (int i = 0; i < optionsName.size(); i++) {
                ArrayList<MenuChoicesModel> menuChoicesModels = new ArrayList<>();
                for (int j = 0; j < menuChoicesHashMap.get(optionsName.get(i)).size(); j++) {
                    MenuChoicesModel menuChoicesModel = new MenuChoicesModel();
                    menuChoicesModel.setMenuChoiceName(menuChoicesHashMap.get(optionsName.get(i)).get(j).getMenuChoiceName());
                    menuChoicesModel.setMenuChoiceId(menuChoicesHashMap.get(optionsName.get(i)).get(j).getMenuChoiceId());
                    menuChoicesModel.setMenuOptionId(menuChoicesHashMap.get(optionsName.get(i)).get(j).getMenuOptionId());
                    // loop till size of choices
                    if (optionModels.get(i).isMultiSelect() == 1){
                        if (menuChoicesHashMap.get(optionsName.get(i)).get(j).isCheckBoxChecked()) {
                            menuChoicesModel.setIsCheckBoxChecked(true);
                            menuChoicesModel.setIsDefault(1);

                        } else {
                            menuChoicesModel.setIsCheckBoxChecked(false);
                            menuChoicesModel.setIsDefault(0);
                        }
                    }else {
                        if (menuChoicesHashMap.get(optionsName.get(i)).get(j).isRadioButtonChecked()) {
                            menuChoicesModel.setIsRadioButtonChecked(true);
                            menuChoicesModel.setIsDefault(1);

                        } else {
                            menuChoicesModel.setIsRadioButtonChecked(false);
                            menuChoicesModel.setIsDefault(0);
                        }
                    }
                    menuChoicesModel.setMenuChoiceQuantity(menuChoicesHashMap.get(optionsName.get(i)).get(j).getMenuChoiceQuantity());
                    menuChoicesModel.setIsChoiceDefault(menuChoicesHashMap.get(optionsName.get(i)).get(j).isChoiceDefault());
                    menuChoicesModels.add(menuChoicesModel);
                }
                choices.put(optionsName.get(i), menuChoicesModels);
            }
        }
        cartItemModel.setMenuChoicesHashMap(choices);
        cartItemModels.add(cartItemModel);
    }

    public void removeFromCart(int position) {
        itemTotalCount -= 1;
        cartItemModels.remove(position);
    }

    public ArrayList<CartItemModel> getCartItem() {
        return cartItemModels;
    }

    public Integer getTotalItemQuantity() {
        return itemTotalCount;
    }

    public Integer getItemCount() {
        Integer totalCount = 0;
        for (int i = 0; i < cartItemModels.size(); i++) {
            totalCount += Integer.parseInt(cartItemModels.get(i).getItemQuantity());
        }
        return totalCount;
    }

    public double getItemTotalCost() {
        double totalCost = 0;
        for (int i = 0; i < cartItemModels.size(); i++) {
            totalCost += Double.parseDouble(cartItemModels.get(i).getItemPrice());
        }
        return totalCost;
    }

    public double getCalculatedTotalCost() {
        double totalCost = 0;
        for (int i = 0; i < cartItemModels.size(); i++) {
            totalCost += Double.parseDouble(cartItemModels.get(i).getCalculatedItemPrice());
        }
        return totalCost;
    }

    private ArrayList<String> initializeData(Map<String, ArrayList<MenuChoicesModel>> menuChoices) {
        ArrayList<String> optionsName = new ArrayList<>();
        Set<String> menuOptions = menuChoices.keySet();
        for (String name : menuOptions) {
            optionsName.add(name);
        }
        return optionsName;
    }

    public void clearCart() {
        cartItemModels.clear();
        itemTotalCount = 0;
    }

    public ArrayList<CartItemModel> getCartItemModels() {
        return cartItemModels;
    }

    public void setCartItemModels(ArrayList<CartItemModel> cartItemModels) {
        this.cartItemModels = cartItemModels;
    }
}
