package com.app.android.quickbud.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.app.android.quickbud.R;
import com.app.android.quickbud.activities.MenuActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class TableBookingConfirmationFragment extends Fragment {


    public TableBookingConfirmationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_table_booking_confirmation, container, false);
        Button openPreOrderMenu = (Button) view.findViewById(R.id.open_preOrder_menu);
        openPreOrderMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MenuActivity.class));
            }
        });
        return view;
    }


}
