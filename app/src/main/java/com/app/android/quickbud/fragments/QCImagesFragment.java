package com.app.android.quickbud.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.android.quickbud.R;
import com.app.android.quickbud.adapters.ChatImagesPagerAdapter;

public class QCImagesFragment extends Fragment implements ViewPager.OnPageChangeListener {

    private ViewPager chatImagesPager;
    private TabLayout chatImagesTab;
    private ChatImagesPagerAdapter chatImagesPagerAdapter;
    private TextView imageTabText, winnerTabText;
    ImageView imageTabImage, winnerTabImage;

    public QCImagesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_qcimages, container, false);

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Review All QuickPics");

        chatImagesTab = (TabLayout) view.findViewById(R.id.chat_images_tab);
        chatImagesPager = (ViewPager) view.findViewById(R.id.chat_images_pager);
        chatImagesPager.addOnPageChangeListener(this);
        setUpViewPager();
        chatImagesTab.setupWithViewPager(chatImagesPager);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            chatImagesTab.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.primary_color));
        }

        setUpTabLayout();
        return view;
    }

    private void setUpViewPager() {
        chatImagesPagerAdapter = new ChatImagesPagerAdapter(getChildFragmentManager());
        chatImagesPagerAdapter.addFragments(new QCGridImagesFragment(), "Quick Pics");
        chatImagesPagerAdapter.addFragments(new ImageWinnerFragment(), "Past Winners");
        chatImagesPager.setAdapter(chatImagesPagerAdapter);
    }

    private void setUpTabLayout() {
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View imageTab = layoutInflater.inflate(R.layout.tab_layout, null);
        imageTabText = (TextView) imageTab.findViewById(R.id.text_tab);
        imageTabText.setText("Quick Pics");
        imageTabText.setTextColor(Color.parseColor("#FFFFFF"));
        imageTabImage = (ImageView) imageTab.findViewById(R.id.image_tab);
        imageTabImage.setImageResource(R.mipmap.ic_picture_white);
        chatImagesTab.getTabAt(0).setCustomView(imageTab);
        View winnerTab = layoutInflater.inflate(R.layout.tab_layout, null);
        winnerTabText = (TextView) winnerTab.findViewById(R.id.text_tab);
        winnerTabText.setText("Past Winners");
        winnerTabText.setTextColor(Color.parseColor("#000000"));
        winnerTabImage = (ImageView) winnerTab.findViewById(R.id.image_tab);
        winnerTabImage.setImageResource(R.mipmap.ic_winner_black);
        chatImagesTab.getTabAt(1).setCustomView(winnerTab);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        if (position == 0) {
            imageTabText.setTextColor(Color.parseColor("#FFFFFF"));
            winnerTabText.setTextColor(Color.parseColor("#000000"));
            imageTabImage.setImageResource(R.mipmap.ic_picture_white);
            winnerTabImage.setImageResource(R.mipmap.ic_winner_black);
        } else {
            imageTabText.setTextColor(Color.parseColor("#000000"));
            winnerTabText.setTextColor(Color.parseColor("#FFFFFF"));
            imageTabImage.setImageResource(R.mipmap.ic_picture_black);
            winnerTabImage.setImageResource(R.mipmap.ic_winner_white);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
