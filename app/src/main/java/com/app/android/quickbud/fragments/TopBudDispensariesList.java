package com.app.android.quickbud.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.app.android.quickbud.R;
import com.app.android.quickbud.adapters.TopBudDispensariesListAdapter;
import com.app.android.quickbud.modelClasses.TopBudsDispensaryModel;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class TopBudDispensariesList extends Fragment implements AdapterView.OnItemClickListener {

    ArrayList<TopBudsDispensaryModel> topBudsDispensaryModels;

    public TopBudDispensariesList() {
        // Required empty public constructor
    }

    public TopBudDispensariesList(ArrayList<TopBudsDispensaryModel> topBudsDispensaryModels) {
        this.topBudsDispensaryModels = topBudsDispensaryModels;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_top_bud_dispensaries_list, container, false);
        ListView topBudDispensariesList = (ListView) view.findViewById(R.id.top_buds_list);

        TopBudDispensariesListAdapter adapter = new TopBudDispensariesListAdapter(getActivity(), topBudsDispensaryModels);
        topBudDispensariesList.setAdapter(adapter);
        topBudDispensariesList.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Fragment fragment = new TopBudDetailsFragment(topBudsDispensaryModels.get(position));
        Bundle bundle = getArguments();
        fragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.top_buds_container, fragment)
                .addToBackStack(null).commit();
    }
}
