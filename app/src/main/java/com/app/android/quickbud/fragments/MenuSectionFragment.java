package com.app.android.quickbud.fragments;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.android.quickbud.R;
import com.app.android.quickbud.adapters.OrderViewPagerAdapter;
import com.app.android.quickbud.utils.ViewPagerInterface;

/**
 * A simple {@link Fragment} subclass.
 */
public class MenuSectionFragment extends Fragment {

    private ViewPager preOrderViewPager;
    private TabLayout preOrderTabLayout;
    private OrderViewPagerAdapter pagerAdapter;

    public MenuSectionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_menu_section, container, false);
        preOrderTabLayout= (TabLayout) view.findViewById(R.id.preOrder_tabLayout);
        preOrderViewPager= (ViewPager) view.findViewById(R.id.preOrder_viewPager);
        setUpViewPager();
        preOrderTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        preOrderTabLayout.setupWithViewPager(preOrderViewPager);
        return view;
    }

    private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {

        int currentPosition = 0;

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

            ViewPagerInterface fragmentToHide = (ViewPagerInterface) pagerAdapter.getItem(currentPosition);
            fragmentToHide.onPauseFragment(getActivity());

            ViewPagerInterface fragmentToShow = (ViewPagerInterface) pagerAdapter.getItem(position);
            fragmentToShow.onResumeFragment(getActivity());

            currentPosition = position;
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private void setUpViewPager() {
        pagerAdapter = new OrderViewPagerAdapter(getChildFragmentManager());
        pagerAdapter.addFragments(new OrderMenuFragment(), getString(R.string.menu));
        pagerAdapter.addFragments(new OrderFavoriteFragment(), getString(R.string.favorites));
        preOrderViewPager.setAdapter(pagerAdapter);
        preOrderViewPager.addOnPageChangeListener(pageChangeListener);
    }
}
