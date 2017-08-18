package com.app.android.quickbud.network;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by mobi11 on 26/1/16.
 */
public class ChatService extends Service {

    private static final String DOMAIN = "159.203.88.161";
    private static final String USERNAME = "917569550492";
    private static final String PASSWORD = "917569550492";
    public static ConnectivityManager connectivityManager;
//    public static ChatXMPP chatXMPP;
    public static boolean serverChatCreated = false;
    String text = "";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new LocalBinder<ChatService>(this);
    }

//    public Chat chat;

    @Override
    public void onCreate() {
        super.onCreate();
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        chatXMPP = ChatXMPP.getInstance(ChatService.this, DOMAIN, USERNAME, PASSWORD);
//        chatXMPP.connect("onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_NOT_STICKY;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        chatXMPP.connection.disconnect();
    }

    public static boolean isNetworkConnected() {
        return connectivityManager.getActiveNetworkInfo() != null;
    }
}
