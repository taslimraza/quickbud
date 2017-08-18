package com.app.android.quickbud.fragments;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.app.android.quickbud.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class QuickPicsInfoFragment extends Fragment {


    public QuickPicsInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_quick_pics_info, container, false);
        WebView infoWebView = (WebView) view.findViewById(R.id.info_webview);

        infoWebView.loadUrl("file:///android_asset/quicktable.html");
//        infoWebView.setWebViewClient(new ChatScreen());
//        WebSettings settings = infoWebView.getSettings();
//        settings.setJavaScriptEnabled(true);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Rules");
    }

    private class ChatScreen extends WebViewClient {
        ProgressDialog progressDialog;

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onLoadResource(WebView view, String url) {
//            loadingImage.setVisibility(View.VISIBLE);
            if (progressDialog == null) {
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Loading...");
                progressDialog.show();
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
//            loadingImage.setVisibility(View.GONE);
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }

}
