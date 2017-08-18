package com.app.android.quickbud.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.app.android.quickbud.R;

public class GcmMessageActivity extends AppCompatActivity {

    Toolbar toolbar;
    ImageView menu;
    RelativeLayout cartContent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        Button quickChat = (Button) findViewById(R.id.quick_chat);
        toolbar= (Toolbar) findViewById(R.id.app_bar);
        menu= (ImageView) findViewById(R.id.menu_button);
        cartContent= (RelativeLayout) findViewById(R.id.cart_content);
        menu.setVisibility(View.GONE);
        cartContent.setVisibility(View.GONE);
        toolbar.setTitle("GCM Notification");
        setSupportActionBar(toolbar);
    }
}
