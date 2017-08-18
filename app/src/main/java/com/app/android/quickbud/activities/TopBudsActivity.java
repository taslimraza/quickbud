package com.app.android.quickbud.activities;

import android.content.Intent;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.app.android.quickbud.R;
import com.app.android.quickbud.fragments.TopBudsListFragment;

public class TopBudsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_buds);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ImageView homeBtn = (ImageView) findViewById(R.id.menu_button);
        ImageView cartButton = (ImageView) findViewById(R.id.cart_button);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            toolbar.setBackgroundResource(R.mipmap.toolbar_image);
        }

        toolbar.setTitle("Top Buds");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        cartButton.setVisibility(View.GONE);

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TopBudsActivity.this, MainMenuActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();

            }
        });

        Fragment fragment = new TopBudsListFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("isFromMainMenu", getIntent().getBooleanExtra("isFromMainMenu", false));
        bundle.putDouble("currentLatitude", getIntent().getDoubleExtra("currentLatitude", 0.0));
        bundle.putDouble("currentLongitude", getIntent().getDoubleExtra("currentLongitude", 0.0));
        fragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction().add(R.id.top_buds_container, fragment).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
        }
        return true;
    }
}
