package com.app.android.quickbud.utils;

import android.app.Application;
import android.content.Context;

//import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * Created by mobi11 on 14/10/15.
 */
public class QTApplication extends Application {
    private static QTApplication mInstance;
    private static Context mAppContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
//        Fresco.initialize(this);

        this.setAppContext(getApplicationContext());
    }

    public static QTApplication getInstance(){
        return mInstance;
    }
    public static Context getAppContext() {
        return mAppContext;
    }
    public void setAppContext(Context mAppContext) {
        this.mAppContext = mAppContext;
    }
}
