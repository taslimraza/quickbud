package com.app.android.quickbud.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.android.quickbud.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class EmptyMenuFragment extends Fragment {


    public EmptyMenuFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_no_menu_item, container, false);
    }


}
