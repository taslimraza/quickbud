package com.app.android.quickbud.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.android.quickbud.R;
import com.app.android.quickbud.adapters.DrawerListAdapter;
import com.app.android.quickbud.adapters.OrderViewPagerAdapter;
import com.app.android.quickbud.fragments.OrderFavoriteFragment;
import com.app.android.quickbud.fragments.OrderMenuFragment;
import com.app.android.quickbud.utils.Globals;

public class MenuSectionActivity extends AppCompatActivity {

    TextView userName,userLocation;
    DrawerLayout drawerLayout;
    ListView drawerList;
    ImageView menu;
    private ViewPager preOrderViewPager;
    private TabLayout preOrderTabLayout;
    private OrderViewPagerAdapter pagerAdapter;
    RelativeLayout cartContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pre_order_activity_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Rocknes Menu");
        }

        drawerList = (ListView) findViewById(R.id.drawer_list);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        userName= (TextView) findViewById(R.id.user_name);
        userLocation= (TextView) findViewById(R.id.user_location);
        cartContent= (RelativeLayout) findViewById(R.id.cart_content);
        cartContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuSectionActivity.this, OrderDetailsActivity.class));
            }
        });
        userName.setTypeface(Globals.robotoBold);
        userLocation.setTypeface(Globals.robotoRegular);
        drawerList.setAdapter(new DrawerListAdapter(this));

        menu = (ImageView) findViewById(R.id.menu_button);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        preOrderViewPager = (ViewPager) findViewById(R.id.preOrder_viewPager);
        setupUpViewPager();
        preOrderTabLayout = (TabLayout) findViewById(R.id.preOrder_tabLayout);
        preOrderTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        preOrderTabLayout.setupWithViewPager(preOrderViewPager);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
        }
        return true;
    }

    private void setupUpViewPager() {
        pagerAdapter = new OrderViewPagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragments(new OrderMenuFragment(), getString(R.string.menu));
        pagerAdapter.addFragments(new OrderFavoriteFragment(), getString(R.string.favorites));
        preOrderViewPager.setAdapter(pagerAdapter);
    }
}
