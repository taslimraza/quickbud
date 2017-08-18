package com.app.android.quickbud.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.app.android.quickbud.R;
import com.app.android.quickbud.fragments.BudNewsListFragment;

public class BudNewsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bud_news);

        RelativeLayout cartContent = (RelativeLayout) findViewById(R.id.cart_content);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ImageView homeBtn = (ImageView) findViewById(R.id.menu_button);
        toolbar.setTitle("Bud News");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        cartContent.setVisibility(View.GONE);

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BudNewsActivity.this, MainMenuActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();

            }
        });

        getSupportFragmentManager().beginTransaction().add(R.id.bud_news_container, new BudNewsListFragment()).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }
}
