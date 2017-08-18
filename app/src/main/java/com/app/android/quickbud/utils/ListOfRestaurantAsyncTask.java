package com.app.android.quickbud.utils;

import android.os.AsyncTask;

/**
 * Created by mobi11 on 6/10/15.
 */
public class ListOfRestaurantAsyncTask extends AsyncTask<String, Void, String> {
    String url=null;
    int requestNo;

    public ListOfRestaurantAsyncTask(String url,int requestNo) {
        this.url=url;
        this.requestNo=requestNo;
    }

    @Override
    protected String doInBackground(String... params) {
        return null;
    }
}
