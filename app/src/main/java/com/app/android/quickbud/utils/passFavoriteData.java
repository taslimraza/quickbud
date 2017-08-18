package com.app.android.quickbud.utils;

import com.app.android.quickbud.modelClasses.FavoriteMenuItem;
import com.app.android.quickbud.modelClasses.MenuItemModel;

import java.util.ArrayList;

/**
 * Created by mobi11 on 20/4/16.
 */
public interface passFavoriteData {
    void passData(ArrayList<MenuItemModel> menuItemModels, ArrayList<FavoriteMenuItem> favoriteMenuItems);
}
