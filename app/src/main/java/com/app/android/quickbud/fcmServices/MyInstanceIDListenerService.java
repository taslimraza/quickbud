package com.app.android.quickbud.fcmServices;

import android.content.Intent;

import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by mobigesture-hp on 28/7/15.
 */
public class MyInstanceIDListenerService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        Intent intent=new Intent(this,RegistrationIntentService.class);
        startService(intent);
    }
}
