package com.app.android.quickbud.utils;

import com.app.android.quickbud.modelClasses.FavoriteMenuItem;
import com.app.android.quickbud.modelClasses.FavoriteMenuItemChoices;
import com.app.android.quickbud.modelClasses.LocationDistanceModel;
import com.app.android.quickbud.modelClasses.LocationListModel;
import com.app.android.quickbud.modelClasses.MenuChoicesModel;
import com.app.android.quickbud.modelClasses.MenuDetailsModel;
import com.app.android.quickbud.modelClasses.MenuItemModel;
import com.app.android.quickbud.modelClasses.MenuOptionModel;
import com.app.android.quickbud.modelClasses.OrderMenuModel;
import com.app.android.quickbud.modelClasses.TopBudsDispensaryModel;
import com.app.android.quickbud.modelClasses.TopBudsListModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by mobi11 on 2/10/15.
 */
public class JsonParser {

    String imageURL = Config.QUICK_CHAT_IMAGE;
//    String imageURL = "https://s3-us-west-2.amazonaws.com/stagingquicktable/";
    JSONArray restaurantList;
    JSONObject request;
    ArrayList<LocationListModel> locationListModels;
    ArrayList<LocationDistanceModel> locationDistanceModels;
    LocationListModel locationListModel;
    MenuDetailsModel menuDetailsModel;
    LocationDistanceModel locationDistanceModel;
    ArrayList<OrderMenuModel> orderMenuModels;
    ArrayList<MenuItemModel> menuItemModels;
    ArrayList<MenuOptionModel> menuOptionModels;
    ArrayList<MenuChoicesModel> menuChoicesModels;
    private String restName, restAddress, restImage, placeId, pageToken = null;
    private double latitude, longitude;

    public JsonParser() {

    }

    public String getPageToken(String data) {
        String token = null;
        try {
            request = new JSONObject(data);
            if (request.has("next_page_token")) {
                token = request.getString("next_page_token");
                return token;
            }
        } catch (JSONException e) {

        }
        return token;
    }

    public ArrayList<LocationListModel> getLocationListParsed(String data) {
        locationListModels = new ArrayList<>();
        try {
            request = new JSONObject(data);
            restaurantList = request.getJSONArray("results");

            for (int i = 0; i < restaurantList.length(); i++) {
                JSONObject singleRest = restaurantList.getJSONObject(i);
                restName = singleRest.getString("name");
//                restAddress = singleRest.getString("vicinity");
                restImage = singleRest.getString("icon");
                placeId = singleRest.getString("place_id");
                JSONObject locationGeocode = singleRest.getJSONObject("geometry");
                JSONObject location = locationGeocode.getJSONObject("location");
                latitude = location.getDouble("lat");
                longitude = location.getDouble("lng");

                locationListModel = new LocationListModel();
                locationListModel.setRestaurantImage(restImage);
                locationListModel.setRestaurantName(restName);
//                locationListModel.setRestaurantAddress(restAddress);
                locationListModel.setLatitude(latitude);
                locationListModel.setLongitude(longitude);
                locationListModel.setPlaceID(placeId);
                locationListModels.add(i, locationListModel);
            }


        } catch (JSONException e) {

        }
        return locationListModels;
    }

    public ArrayList<LocationListModel> getLocationDistanceParsed(String data, ArrayList<LocationListModel> model) {

        try {
            request = new JSONObject(data);
            restaurantList = request.getJSONArray("rows");
            for (int i = 0; i < restaurantList.length(); i++) {
                JSONObject singleRest = restaurantList.getJSONObject(i);

                JSONArray restDistance = singleRest.getJSONArray("elements");
                for (int j = 0; j < restDistance.length(); j++) {
                    JSONObject restaurantDistance = restDistance.getJSONObject(j);

                    JSONObject resDist = restaurantDistance.getJSONObject("distance");

                    String resDis = resDist.getString("text");
                    int resDisValue = resDist.getInt("value");
//                    model.get(j).setRestaurantDistance(resDis);
                    model.get(j).setDistanceValue(resDisValue);
                }
            }

        } catch (JSONException e) {

        }
        return model;
    }

    public MenuDetailsModel getMenuParsed(String data) {
        menuDetailsModel = MenuDetailsModel.getInstance();
        orderMenuModels = new ArrayList<>();
        menuItemModels = new ArrayList<>();
        menuOptionModels = new ArrayList<>();
        menuChoicesModels = new ArrayList<>();
        try {
            JSONObject menuDetails = new JSONObject(data);

            JSONObject menus = menuDetails.getJSONObject("data");
            JSONArray menuSections = menus.getJSONArray("sections");
            if (menuSections.length() > 0) {
                for (int i = 0; i < menuSections.length(); i++) {
                    OrderMenuModel orderMenuModel = new OrderMenuModel();
                    ArrayList<MenuItemModel> menuItemsModel = new ArrayList<>();
                    JSONObject menuTitle = menuSections.getJSONObject(i);
                    orderMenuModel.setMenuSectionId(menuTitle.getInt("id"));
                    orderMenuModel.setMenuSectionName(menuTitle.getString("name"));
                    orderMenuModel.setMenuItemCount(menuTitle.getJSONArray("items").length());
                    orderMenuModel.setMenuSectionCount("<b>" + menuTitle.getJSONArray("items").length() + "</b>" + " Items");
                    orderMenuModel.setMenuSectionImage(imageURL + menuTitle.getString("url"));

                    JSONArray menuItems = menuTitle.getJSONArray("items");
                    if (menuItems.length() > 0) {
                        for (int j = 0; j < menuItems.length(); j++) {
                            MenuItemModel menuItemModel = new MenuItemModel();
                            ArrayList<MenuOptionModel> menuOptionsModel = new ArrayList<>();
                            JSONObject itemDetails = menuItems.getJSONObject(j);
                            menuItemModel.setMenuItemId(itemDetails.getInt("id"));
                            menuItemModel.setMenuItemName(itemDetails.getString("name"));
                            menuItemModel.setMenuItemImage(imageURL + itemDetails.getString("url"));

                            if (!itemDetails.isNull("description")) {
                                menuItemModel.setMenuItemDescription(itemDetails.getString("description"));
                            }

                            menuItemModel.setMenuItemPrice(itemDetails.getString("price"));
                            menuItemModel.setMenuSectionId(itemDetails.getInt("menu_section"));
                            menuItemModel.setLikesCount(itemDetails.getInt("likes_count"));
                            menuItemModel.setItemQuantity(0);
                            if (!itemDetails.isNull("thc")){
                                menuItemModel.setThc(itemDetails.getString("thc"));
                            }else {
                                menuItemModel.setThc("0");
                            }

                            if (!itemDetails.isNull("cbd")){
                                menuItemModel.setCbd(itemDetails.getString("cbd"));
                            }else {
                                menuItemModel.setCbd("0");
                            }

                            JSONArray menuOptions = itemDetails.getJSONArray("options");
                            if (menuOptions.length() > 0) { // if menu options present then parse, else do nothing
                                for (int k = 0; k < menuOptions.length(); k++) {
                                    MenuOptionModel menuOptionModel = new MenuOptionModel();
                                    ArrayList<MenuChoicesModel> menuChoicesModel = new ArrayList<>();
                                    JSONObject menuOption = menuOptions.getJSONObject(k);
                                    menuOptionModel.setMenuOptionId(menuOption.getInt("id"));
                                    menuOptionModel.setMenuOptionName(menuOption.getString("name"));
                                    menuOptionModel.setMenuOptionPrice(menuOption.getString("price"));
                                    menuOptionModel.setMenuItemId(menuOption.getInt("menu_item"));
                                    menuOptionModel.setIsMultiSelect(menuOption.getInt("is_multiselect"));
//                                    menuOptionModel.setIsMultiQuantity(menuOption.getInt("is_multiquantity"));
                                    menuOptionModel.setIsMultiQuantity(0);
                                    if (menuOption.getInt("is_multiselect") == 1) {
                                        // checkbox visibility
                                        menuOptionModel.setCheckBoxVisibility(true);
                                        menuOptionModel.setRadioButtonVisibility(false);

                                    } else {
                                        // radiobutton
                                        menuOptionModel.setCheckBoxVisibility(false);
                                        menuOptionModel.setRadioButtonVisibility(true);
                                    }

                                    JSONArray menuChoices = menuOption.getJSONArray("choices");
                                    if (menuChoices.length() > 0) {  // if menu choices present then parse, else do nothing
                                        for (int l = 0; l < menuChoices.length(); l++) {
                                            MenuChoicesModel menuChoiceModel = new MenuChoicesModel();
                                            JSONObject menuChoice = menuChoices.getJSONObject(l);
                                            menuChoiceModel.setMenuChoiceId(menuChoice.getInt("id"));
                                            menuChoiceModel.setMenuChoiceName(menuChoice.getString("name"));
                                            menuChoiceModel.setMenuChoicePrice(menuChoice.getString("price"));
                                            menuChoiceModel.setMenuOptionId(menuChoice.getInt("menu_option"));
                                            if (!(menuChoice.isNull("is_default"))) {
                                                menuChoiceModel.setIsChoiceDefault(menuChoice.getInt("is_default"));
                                                menuChoiceModel.setIsDefault(menuChoice.getInt("is_default"));

                                                if (menuOption.getInt("is_multiselect") == 1) {
                                                    if (menuChoice.getInt("is_default") == 1) {
                                                        menuChoiceModel.setIsCheckBoxChecked(true);
                                                    }
                                                }else {
                                                    if (menuChoice.getInt("is_default") == 1) {
                                                        menuChoiceModel.setIsRadioButtonChecked(true);
                                                    }
                                                }

                                            } else {
                                                menuChoiceModel.setIsChoiceDefault(0);
                                                menuChoiceModel.setIsDefault(0);
                                            }
                                            menuChoicesModels.add(menuChoiceModel);
                                            menuChoicesModel.add(menuChoiceModel);
                                        }
                                    } else {

                                    }
                                    menuOptionModel.setMenuChoicesModels(menuChoicesModel);
                                    menuOptionModels.add(menuOptionModel);
                                    menuOptionsModel.add(menuOptionModel);
                                }
                            } else {

                            }
                            menuItemModel.setMenuOptionModels(menuOptionsModel);
                            menuItemModels.add(menuItemModel);
                            menuItemsModel.add(menuItemModel);
                        }
                    } else {

                    }
                    orderMenuModel.setMenuItemModels(menuItemsModel);
                    orderMenuModels.add(orderMenuModel);
                }
                menuDetailsModel.setMenuChoicesModels(menuChoicesModels);
                menuDetailsModel.setMenuOptionModels(menuOptionModels);
                menuDetailsModel.setMenuItemModels(menuItemModels);
                menuDetailsModel.setOrderMenuModels(orderMenuModels);
            } else {

            }
//            Integer numberOfItem = request.length();
//            orderMenuModel.setMenuSectionCount(numberOfItem.toString());
        } catch (JSONException e) {

        }
        return menuDetailsModel;
    }

    public ArrayList<FavoriteMenuItem> getFavoriteMenuParsed(String data) {

        ArrayList<FavoriteMenuItem> favoriteMenuItems = new ArrayList<>();
        ArrayList<FavoriteMenuItemChoices> menuItemChoices;
        try {
            JSONArray favorites = new JSONArray(data);

            for (int i = 0; i < favorites.length(); i++) {
                FavoriteMenuItem favoriteMenuItem = new FavoriteMenuItem();
                JSONObject favoriteItem = favorites.getJSONObject(i);
                favoriteMenuItem.setFavoriteId(favoriteItem.getInt("id"));
                favoriteMenuItem.setTenantId(favoriteItem.getInt("tenant_id"));
                favoriteMenuItem.setLocationId(favoriteItem.getInt("location_id"));
                favoriteMenuItem.setMenuItemId(favoriteItem.getInt("menu_item_id"));
                favoriteMenuItem.setPatronId(favoriteItem.getInt("patron_id"));
                favoriteMenuItem.setStatus(favoriteItem.getString("status"));

                JSONArray favoriteChoices = favoriteItem.getJSONArray("favoritechoice");
                menuItemChoices = new ArrayList<>();
                for (int j = 0; j < favoriteChoices.length(); j++) {
                    JSONObject menuChoice = favoriteChoices.getJSONObject(j);
                    FavoriteMenuItemChoices favoriteMenuItemChoices = new FavoriteMenuItemChoices();
                    favoriteMenuItemChoices.setStatus(menuChoice.getString("status"));
                    favoriteMenuItemChoices.setChoiceId(menuChoice.getInt("choice_id"));
                    menuItemChoices.add(favoriteMenuItemChoices);
                }
                favoriteMenuItem.setMenuItemChoices(menuItemChoices);
                favoriteMenuItems.add(favoriteMenuItem);
            }

        } catch (JSONException e) {

        }

        return favoriteMenuItems;
    }

    public ArrayList<LocationListModel> getFactualRestaurantList(String data) {
        locationListModels = new ArrayList<>();
        try {
            JSONArray requestArray = new JSONArray(data);
            JSONObject request = requestArray.getJSONObject(0);
            JSONObject object = request.getJSONObject("response");
            JSONArray restaurantList = object.getJSONArray("data");

            for (int i = 0; i < restaurantList.length(); i++) {
                JSONObject singleRest = restaurantList.getJSONObject(i);
                restName = singleRest.getString("name");
                if (singleRest.has("address")) {
                    restAddress = singleRest.getString("address");
                }
//                restImage = singleRest.getString("icon");
                placeId = singleRest.getString("factual_id");
                latitude = singleRest.getDouble("latitude");
                longitude = singleRest.getDouble("longitude");
                String restState = singleRest.getString("region");

                String restPhone = null;
                String restCity = null;
                if (singleRest.has("tel")){
                    restPhone = singleRest.getString("tel");
                }

                if (singleRest.has("locality")){
                    restCity = singleRest.getString("locality");
                }

                Double dist = singleRest.getDouble("$distance");

                locationListModel = new LocationListModel();
//                locationListModel.setRestaurantImage(restImage);
                locationListModel.setRestaurantName(restName);
                locationListModel.setRestaurantAddress(restAddress);
                locationListModel.setRestaurantDistance(dist);
                locationListModel.setLatitude(latitude);
                locationListModel.setLongitude(longitude);
                locationListModel.setPlaceID(placeId);
                locationListModel.setRestaurantCity(restCity);
                locationListModel.setRestaurantState(restState);
                locationListModel.setRestaurantPhone(restPhone);
                locationListModels.add(i, locationListModel);
            }


        } catch (JSONException e) {

        }
        return locationListModels;
    }

    public ArrayList<LocationListModel> getWhiteLabelledList(JSONObject request){

        ArrayList<LocationListModel> locationListModels = new ArrayList<>();

        try {
            JSONArray restaurantArrayList = request.getJSONArray("results");

            for (int i=0; i<restaurantArrayList.length(); i++){

                LocationListModel locationListModel = new LocationListModel();
                JSONObject object = restaurantArrayList.getJSONObject(i);

                locationListModel.setRestaurantName(object.getString("name"));
                locationListModel.setRestaurantImage(object.getString("url"));
                locationListModel.setRestaurantDistance(object.getDouble("distance"));
                locationListModel.setRestaurantPhone(object.getString("phone_number"));
                locationListModel.setRestaurantOpenTiming(object.getString("open_time"));
                locationListModel.setRestaurantCloseTiming(object.getString("close_time"));
                locationListModel.setRestaurantAddress(object.getString("address_line"));
                locationListModel.setRestaurantCity(object.getString("city"));
                locationListModel.setRestaurantState(object.getString("state"));
                locationListModel.setRestaurantMenuUrl(object.getString("menu_url"));
                locationListModel.setTenantID(object.getInt("tenant_id"));
                locationListModel.setLocationID(object.getInt("location_id"));
                locationListModel.setLatitude(object.getDouble("latitude"));
                locationListModel.setLongitude(object.getDouble("longitude"));
                JSONArray accountTypes = object.getJSONArray("account_type");
                JSONObject accountType = accountTypes.getJSONObject(0);
                locationListModel.setRestaurantAccountName(accountType.getString("account_name"));
                locationListModel.setCarryOut(accountType.getBoolean("carry_out"));
                locationListModel.setDisplayLogo(accountType.getBoolean("display_logo"));
                locationListModel.setGetInLine(accountType.getBoolean("get_in_line"));
                locationListModel.setPreOrder(accountType.getBoolean("pre_order"));
                locationListModel.setInteractiveMenu(accountType.getBoolean("interactive_menu"));
                locationListModel.setRestaurantChatAvailable(object.getString("chats"));
                locationListModel.setPlaceID(object.getString("placeId"));
                locationListModel.setRestaurantEWT(object.getString("ewt"));

                locationListModels.add(locationListModel);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return locationListModels;
    }

    public ArrayList<TopBudsListModel> topBudsListParser(JSONObject request){

        ArrayList<TopBudsListModel> topBudsListModels = new ArrayList<>();

        try {
            JSONArray responseArray = request.getJSONArray("result");

            for (int i = 0; i < responseArray.length(); i++) {
                JSONObject responseObj = responseArray.getJSONObject(i);
                TopBudsListModel topBudsListModel = new TopBudsListModel();
                topBudsListModel.setMenuItemName(responseObj.getString("menu_name"));
                topBudsListModel.setLikesNumber(responseObj.getInt("likes"));
                topBudsListModel.setMenuItemImage(responseObj.getString("menu_url"));

                ArrayList<TopBudsDispensaryModel> topBudsDispensaryModels= new ArrayList<>();
                JSONArray budsDispensaries = responseObj.getJSONArray("bud");
                for (int j=0; j<budsDispensaries.length(); j++){
                    TopBudsDispensaryModel topBudsDispensaryModel = new TopBudsDispensaryModel();
                    JSONObject budsDispensary = budsDispensaries.getJSONObject(j);
                    topBudsDispensaryModel.setDispensaryName(budsDispensary.getString("location_name"));
                    topBudsDispensaryModel.setPlaceId(budsDispensary.getString("placeId"));
                    topBudsDispensaryModel.setTenantId(budsDispensary.getInt("tenant_id"));
                    topBudsDispensaryModel.setLocationId(budsDispensary.getInt("location_id"));
                    topBudsDispensaryModel.setLikesCount(budsDispensary.getInt("likes_count"));
                    topBudsDispensaryModel.setMenuId(budsDispensary.getInt("menu_id"));
                    topBudsDispensaryModel.setLiked(budsDispensary.getBoolean("like"));
                    topBudsDispensaryModels.add(topBudsDispensaryModel);
                }

                topBudsListModel.setTopBudsDispensaryModels(topBudsDispensaryModels);
                if (topBudsDispensaryModels.size() == 1){
                    topBudsListModel.setLiked(topBudsDispensaryModels.get(0).isLiked());
                }

                topBudsListModels.add(topBudsListModel);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return topBudsListModels;
    }

}
