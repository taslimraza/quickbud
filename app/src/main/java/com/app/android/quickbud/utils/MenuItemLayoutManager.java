package com.app.android.quickbud.utils;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;

/**
 * Created by mobi11 on 20/6/17.
 */

public class MenuItemLayoutManager extends LinearLayoutManager {

    private boolean isScrollEnabled = true;

    public MenuItemLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    @Override
    public boolean canScrollHorizontally() {
        return isScrollEnabled && super.canScrollHorizontally();
    }

    public void setScrolledEnabled(boolean flag){
        this.isScrollEnabled = flag;
    }
}
