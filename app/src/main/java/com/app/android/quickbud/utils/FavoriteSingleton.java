package com.app.android.quickbud.utils;

import com.app.android.quickbud.modelClasses.FavoriteModel;
import com.app.android.quickbud.modelClasses.MenuChoicesModel;
import com.app.android.quickbud.modelClasses.MenuItemModel;
import com.app.android.quickbud.modelClasses.MenuOptionModel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by mobi11 on 8/2/16.
 */
public class FavoriteSingleton {

    ArrayList<FavoriteModel> favoriteModels = new ArrayList<>();

    private static FavoriteSingleton favoriteSingleton = null;

    private FavoriteSingleton(){

    }

    public static FavoriteSingleton getInstance(){
        if (favoriteSingleton == null){
            favoriteSingleton = new FavoriteSingleton();
        }
        return favoriteSingleton;
    }

    public void addToFavorite(MenuItemModel menuItemModel){
        FavoriteModel favoriteModel = new FavoriteModel();
        favoriteModel.setMenuItemModel(menuItemModel);
        favoriteModels.add(favoriteModel);
    }

    public void addToFavoriteWithOptions(MenuItemModel menuItemModel, ArrayList<MenuOptionModel> menuOptionModel,
                              LinkedHashMap<String, ArrayList<MenuChoicesModel>> menuChoicesHashMap){
        FavoriteModel favoriteModel = new FavoriteModel();
        favoriteModel.setMenuItemModel(menuItemModel);

        LinkedHashMap<String, ArrayList<MenuChoicesModel>> choices = new LinkedHashMap<>();

        if (menuChoicesHashMap != null) {
            ArrayList<String> optionsName = initializeData(menuChoicesHashMap);

            for (int i = 0; i < optionsName.size(); i++) {
                ArrayList<MenuChoicesModel> menuChoicesModels = new ArrayList<>();
                for (int j = 0; j < menuChoicesHashMap.get(optionsName.get(i)).size(); j++) {
                    MenuChoicesModel menuChoicesModel = new MenuChoicesModel();

                    menuChoicesModel = menuChoicesHashMap.get(optionsName.get(i)).get(j);

                    menuChoicesModels.add(menuChoicesModel);
                }
                choices.put(optionsName.get(i), menuChoicesModels);
            }
        }
        favoriteModel.setMenuChoicesHashMap(choices);
        favoriteModel.setMenuOptionModels(menuOptionModel);
        favoriteModels.add(favoriteModel);
    }

    public ArrayList<FavoriteModel> getFavoriteItems(){
        return favoriteModels;
    }

    public void deleteFavoriteItem(int pos){
        favoriteModels.remove(pos);
    }

    public void deleteFavorite(int favoriteId){

        for (int i=0; i<favoriteModels.size(); i++){
            if (favoriteModels.get(i).getMenuItemModel().getFavoriteId() == favoriteId){
                favoriteModels.remove(i);
                break;
            }
        }

    }

    private ArrayList<String> initializeData(Map<String, ArrayList<MenuChoicesModel>> menuChoices) {
        ArrayList<String> optionsName = new ArrayList<>();
        Set<String> menuOptions = menuChoices.keySet();
        for (String name : menuOptions) {
            optionsName.add(name);
        }
        return optionsName;
    }

    public void clearFavoriteItems(){
        favoriteModels.clear();
    }


}
