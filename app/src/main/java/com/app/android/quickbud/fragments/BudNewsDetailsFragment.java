package com.app.android.quickbud.fragments;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.app.android.quickbud.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class BudNewsDetailsFragment extends Fragment {


    public BudNewsDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bud_news_details, container, false);

        WebView budNewsWebView = (WebView) view.findViewById(R.id.bud_news_web_view);

        String url = getArguments().getString("url");
        Log.i("Bud news link - ", url);

        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "", "Loading", true);

        budNewsWebView.getSettings().setJavaScriptEnabled(true);
        budNewsWebView.getSettings().setLoadsImagesAutomatically(true);
        budNewsWebView.getSettings().setBuiltInZoomControls(true);
        budNewsWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        budNewsWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (progressDialog != null && progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
            }
        });
        budNewsWebView.loadUrl(url);

        return view;
    }

}
