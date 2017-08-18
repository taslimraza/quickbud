package com.app.android.quickbud.activities;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.android.quickbud.R;
import com.app.android.quickbud.utils.Config;
import com.bumptech.glide.Glide;

public class PageScreenActivity extends AppCompatActivity {

    private Vibrator vibrator;
    private MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_screen);

        ImageView restaurantImage = (ImageView) findViewById(R.id.rest_image);
        TextView restaurantName = (TextView) findViewById(R.id.rest_name);
        TextView restaurantAddress = (TextView) findViewById(R.id.rest_address);
        TextView restaurantPhone = (TextView) findViewById(R.id.rest_phone);
        TextView message1 = (TextView) findViewById(R.id.paging_message1);
        TextView message2 = (TextView) findViewById(R.id.paging_message2);
        TextView thanksTxt = (TextView) findViewById(R.id.paging_thanks);
        Button stopPage = (Button) findViewById(R.id.stop_paging);

        Intent intent = getIntent();
        String message = intent.getStringExtra("message");
        String restName = intent.getStringExtra("rest_name");
        String restAddress = intent.getStringExtra("rest_address");
        String restPhone = intent.getStringExtra("rest_phone");
        final String restCity = intent.getStringExtra("rest_city");
        final String restState = intent.getStringExtra("rest_state");
        final String restZip = intent.getStringExtra("rest_zip");
        String restImage = intent.getStringExtra("rest_image");

        restaurantName.setText(restName);
        restaurantAddress.setText(restAddress + "\n" + restCity + "," + restState + " " + restZip);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            stopPage.setBackgroundColor(ContextCompat.getColor(this, R.color.primary_color));
        }
        if (restPhone != null) {
            String phoneStart = restPhone.substring(0, 3);
            String phoneMiddle = restPhone.substring(3, 6);
            String phoneEnd = restPhone.substring(6);
            restaurantPhone.setText(phoneStart + "-" + phoneMiddle + "-" + phoneEnd);
        }

        message1.setText(message);
        Glide.with(this).load(Config.QUICK_CHAT_IMAGE + restImage)
                .crossFade(1000)
                .into(restaurantImage);
        message2.setVisibility(View.GONE);

        long[] vibrationPattern = new long[]{0, 100, 200, 500, 700, 1000};
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(vibrationPattern, 1);

        mp = MediaPlayer.create(getBaseContext(), R.raw.iphone_ringtone);
        mp.start();
        mp.setLooping(true);

        stopVibration();

        stopPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (vibrator.hasVibrator()) {
                    vibrator.cancel();
                }
                mp.stop();
                showEWTScreen();
//                finish();
            }
        });
    }

    private void showEWTScreen() {
        Intent intent = null;
//        SharedPreferences preferences = getSharedPreferences("ewt_info", Context.MODE_PRIVATE);
//        boolean dineIn = preferences.getBoolean("isDineInOnly", false);
//        if (dineIn) {
//            intent = new Intent(this, BookYourTableActivity.class);
//            intent.putExtra("isDineIn", true);
//            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        } else {
        Config.bookingStatus = true;
        RegistrationActivity.isAppKilled = true;
        intent = new Intent(this, RegistrationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        }
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        System.out.println("Page screen destroyed!");
        if (vibrator.hasVibrator()) {
            vibrator.cancel();
        }
        mp.stop();
    }

    private void stopVibration() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (vibrator.hasVibrator()) {
                    vibrator.cancel();
                }
                mp.stop();

            }
        }, 60 * 1000);
    }
}
