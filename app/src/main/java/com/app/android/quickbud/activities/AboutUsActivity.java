package com.app.android.quickbud.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.android.quickbud.R;

public class AboutUsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    RelativeLayout cartContent;
    ImageView homeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        cartContent = (RelativeLayout) findViewById(R.id.cart_content);
        homeBtn = (ImageView) findViewById(R.id.menu_button);
        TextView versionNumber = (TextView) findViewById(R.id.version_number);

        toolbar.setTitle("About");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
//            toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.primary_color));
//        }

        cartContent.setVisibility(View.GONE);

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AboutUsActivity.this, MainMenuActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();

            }
        });

        try {
            versionNumber.setText("v" + getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            onBackPressed();
        }
        return true;
    }
}
