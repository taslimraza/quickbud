package com.app.android.quickbud.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.app.android.quickbud.R;
import com.app.android.quickbud.adapters.TopBudsListAdapter;
import com.app.android.quickbud.modelClasses.TopBudsDispensaryModel;
import com.app.android.quickbud.modelClasses.TopBudsListModel;
import com.app.android.quickbud.network.VolleySingleton;
import com.app.android.quickbud.utils.Config;
import com.app.android.quickbud.utils.SpecificMenuSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class TopBudsListFragment extends Fragment implements AdapterView.OnItemClickListener {

    String[] menuItemName = {"Blue Haze", "Sour Diesel", "Cannon Ball Run", "Stash"};
    String[] menuName = {"Sativa", "Hybrid", "Indica", "Sativa"};
    private ListView topBudsList;
    private ImageView loadingImage;
    private TextView noBudTxt;
    private boolean isFromMainMenu = false;

    ArrayList<TopBudsListModel> topBudsListModels;

    public TopBudsListFragment() {
        // Required empty public constructor
//        setUpData();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_top_buds_list, container, false);

        topBudsList = (ListView) view.findViewById(R.id.top_buds_list);
        loadingImage = (ImageView) view.findViewById(R.id.loading);
        noBudTxt = (TextView) view.findViewById(R.id.no_bud_txt);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);

        Bundle bundle = getArguments();
        if (bundle != null){
            isFromMainMenu = bundle.getBoolean("isFromMainMenu");
        }

        if (isFromMainMenu){
            getTopBudsList();
        }else {
            view.findViewById(R.id.national_top_bud_txt).setVisibility(View.GONE);
            view.findViewById(R.id.national_top_bud_sub_txt).setVisibility(View.GONE);
            getDispensaryTopBudsList();
            toolbar.setTitle(SpecificMenuSingleton.getInstance().getClickedRestaurant().getRestaurantName());
        }

//        TopBudsListAdapter adapter = new TopBudsListAdapter(getActivity(), topBudsListModels);
//        topBudsList.setAdapter(adapter);

        return view;
    }

    private void setUpData() {

        topBudsListModels = new ArrayList<>();

        for (int i = 0; i < menuItemName.length; i++) {
            TopBudsListModel topBudsListModel = new TopBudsListModel();
            topBudsListModel.setMenuItemName(menuItemName[i]);
            topBudsListModel.setMenuName(menuName[i]);
            topBudsListModels.add(topBudsListModel);
        }

    }

    private void getTopBudsList() {

        loadingImage.setVisibility(View.VISIBLE);
        topBudsList.setVisibility(View.GONE);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_info", getActivity().MODE_PRIVATE);
        final String patronId = sharedPreferences.getString("patron_id", null);

        String url = Config.QB_TOP_BUDS_LIST + "?patron_id=" + patronId;


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("Top Buds List", response.toString());
                        loadingImage.setVisibility(View.GONE);
                        topBudsList.setVisibility(View.VISIBLE);

                        topBudsListModels = new ArrayList<>();

                        try {
                            JSONArray responseArray = response.getJSONArray("result");

                            for (int i = 0; i < responseArray.length(); i++) {
                                JSONObject responseObj = responseArray.getJSONObject(i);
                                TopBudsListModel topBudsListModel = new TopBudsListModel();
                                topBudsListModel.setMenuItemName(responseObj.getString("menu_name"));
                                topBudsListModel.setLikesNumber(responseObj.getInt("likes"));
                                topBudsListModel.setMenuItemImage(responseObj.getString("menu_url"));

                                ArrayList<TopBudsDispensaryModel> topBudsDispensaryModels= new ArrayList<>();
                                JSONArray budsDispensaries = responseObj.getJSONArray("bud");
                                for (int j=0; j<budsDispensaries.length(); j++){
                                    TopBudsDispensaryModel topBudsDispensaryModel = new TopBudsDispensaryModel();
                                    JSONObject budsDispensary = budsDispensaries.getJSONObject(j);
                                    topBudsDispensaryModel.setDispensaryName(budsDispensary.getString("location_name"));
                                    topBudsDispensaryModel.setPlaceId(budsDispensary.getString("placeId"));
                                    topBudsDispensaryModel.setTenantId(budsDispensary.getInt("tenant_id"));
                                    topBudsDispensaryModel.setLocationId(budsDispensary.getInt("location_id"));
                                    topBudsDispensaryModel.setLikesCount(budsDispensary.getInt("likes_count"));
                                    topBudsDispensaryModel.setMenuId(budsDispensary.getInt("menu_id"));
                                    topBudsDispensaryModel.setLiked(budsDispensary.getBoolean("like"));
//                                    String patronIdStr = budsDispensary.getString("patron_id");
//                                    patronIdStr = patronIdStr.substring(1, patronIdStr.length()-1);
//                                    String[] patronIdsStr = patronIdStr.split(",");
////                                    topBudsDispensaryModel.setPatronId(budsDispensary.getInt("patron_id"));
//                                    for (int k=0; k<patronIdsStr.length; k++){
//                                        topBudsDispensaryModel.setPatronId(budsDispensary.getInt("patron_id"));
//                                        if (Integer.parseInt(patronId)== Integer.parseInt(patronIdsStr[k])){
//                                            topBudsDispensaryModel.setLiked(true);
//                                        }else {
//                                            topBudsDispensaryModel.setLiked(false);
//                                        }
//                                    }
                                    topBudsDispensaryModels.add(topBudsDispensaryModel);
                                }

                                topBudsListModel.setTopBudsDispensaryModels(topBudsDispensaryModels);
                                if (topBudsDispensaryModels.size() == 1){
                                    topBudsListModel.setLiked(topBudsDispensaryModels.get(0).isLiked());
                                }

                                topBudsListModels.add(topBudsListModel);
                            }

                            sortByLikes();

                            if (topBudsListModels.size() == 0){
                                noBudTxt.setVisibility(View.VISIBLE);
                            }else {
                                noBudTxt.setVisibility(View.GONE);
                                TopBudsListAdapter adapter = new TopBudsListAdapter(getActivity(), topBudsListModels);
                                topBudsList.setAdapter(adapter);
                                topBudsList.setOnItemClickListener(TopBudsListFragment.this);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingImage.setVisibility(View.GONE);
                topBudsList.setVisibility(View.VISIBLE);
                if (error != null) {
                    if (error instanceof TimeoutError) {
                        Config.internetSlowError(getActivity());
                    } else if (error instanceof NoConnectionError) {
                        Config.internetSlowError(getActivity());
                    } else if (error instanceof ServerError) {
                        Config.serverError(getActivity());
                    }
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("QTAuthorization", Config.SESSION_TOKEN_ID);
                return headers;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                Config.TIMEOUT,  // 25 sec - timeout
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        VolleySingleton.getInstance(getActivity()).addToRequestQueue(request);

    }

    private void getDispensaryTopBudsList() {

        loadingImage.setVisibility(View.VISIBLE);
        topBudsList.setVisibility(View.GONE);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_info", getActivity().MODE_PRIVATE);
        final String patronId = sharedPreferences.getString("patron_id", null);

        String url = Config.QB_TOP_BUDS_LIST + "?patron_id=" + patronId + "&tenant_id="
                + SpecificMenuSingleton.getInstance().getClickedRestaurant().getTenantID() + "&location_id="
                + SpecificMenuSingleton.getInstance().getClickedRestaurant().getLocationID();


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("Top bud list", response.toString());
                        loadingImage.setVisibility(View.GONE);
                        topBudsList.setVisibility(View.VISIBLE);

                        topBudsListModels = new ArrayList<>();

                        try {
                            JSONArray responseArray = response.getJSONArray("result");

                            for (int i = 0; i < responseArray.length(); i++) {
                                JSONObject responseObj = responseArray.getJSONObject(i);
                                TopBudsListModel topBudsListModel = new TopBudsListModel();
                                topBudsListModel.setMenuItemName(responseObj.getString("menu_name"));
                                topBudsListModel.setLikesNumber(responseObj.getInt("likes"));
                                topBudsListModel.setMenuItemImage(responseObj.getString("menu_url"));

                                ArrayList<TopBudsDispensaryModel> topBudsDispensaryModels= new ArrayList<>();
                                JSONArray budsDispensaries = responseObj.getJSONArray("bud");
                                for (int j=0; j<budsDispensaries.length(); j++){
                                    TopBudsDispensaryModel topBudsDispensaryModel = new TopBudsDispensaryModel();
                                    JSONObject budsDispensary = budsDispensaries.getJSONObject(j);
                                    topBudsDispensaryModel.setDispensaryName(budsDispensary.getString("location_name"));
                                    topBudsDispensaryModel.setPlaceId(budsDispensary.getString("placeId"));
                                    topBudsDispensaryModel.setTenantId(budsDispensary.getInt("tenant_id"));
                                    topBudsDispensaryModel.setLocationId(budsDispensary.getInt("location_id"));
                                    topBudsDispensaryModel.setLikesCount(budsDispensary.getInt("likes_count"));
                                    topBudsDispensaryModel.setMenuId(budsDispensary.getInt("menu_id"));
                                    topBudsDispensaryModel.setLiked(budsDispensary.getBoolean("like"));
                                    topBudsDispensaryModel.setDispensaryAddress(budsDispensary.getString("location_address"));
                                    topBudsDispensaryModel.setDispensaryCity(budsDispensary.getString("location_city"));
                                    topBudsDispensaryModel.setDispensaryState(budsDispensary.getString("location_state"));
//                                    topBudsDispensaryModel.setPatronId(budsDispensary.getInt("patron_id"));
//                                    if (Integer.parseInt(patronId)== budsDispensary.getInt("patron_id")){
//                                        topBudsDispensaryModel.setLiked(true);
//                                    }else {
//                                        topBudsDispensaryModel.setLiked(false);
//                                    }
                                    topBudsDispensaryModels.add(topBudsDispensaryModel);
                                }

                                topBudsListModel.setTopBudsDispensaryModels(topBudsDispensaryModels);
                                if (topBudsDispensaryModels.size() == 1){
                                    topBudsListModel.setLiked(topBudsDispensaryModels.get(0).isLiked());
//                                    if (topBudsDispensaryModels.get(0).getPatronId() == Integer.parseInt(patronId)){
//                                        topBudsListModel.setLiked(true);
//                                    }else {
//                                        topBudsListModel.setLiked(false);
//                                    }
                                }

                                topBudsListModels.add(topBudsListModel);
                            }

                            sortByLikes();

                            if (topBudsListModels.size() == 0){
                                noBudTxt.setVisibility(View.VISIBLE);
                            }else {
                                noBudTxt.setVisibility(View.GONE);
                                TopBudsListAdapter adapter = new TopBudsListAdapter(getActivity(), topBudsListModels);
                                topBudsList.setAdapter(adapter);
                                topBudsList.setOnItemClickListener(TopBudsListFragment.this);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingImage.setVisibility(View.GONE);
                topBudsList.setVisibility(View.VISIBLE);
                if (error != null) {
                    if (error instanceof TimeoutError) {
                        Config.internetSlowError(getActivity());
                    } else if (error instanceof NoConnectionError) {
                        Config.internetSlowError(getActivity());
                    } else if (error instanceof ServerError) {
                        Config.serverError(getActivity());
                    }
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("QTAuthorization", Config.SESSION_TOKEN_ID);
                return headers;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                Config.TIMEOUT,  // 25 sec - timeout
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        VolleySingleton.getInstance(getActivity()).addToRequestQueue(request);

    }

    private void sortByLikes(){
        TopBudsListModel topBudsListModel = new TopBudsListModel();

        for (int i=0; i<topBudsListModels.size(); i++) {
            for (int j=0; j< topBudsListModels.size() - i - 1; j++){
                if (topBudsListModels.get(j).getLikesNumber() < topBudsListModels.get(j+1).getLikesNumber()){
                    topBudsListModel = topBudsListModels.get(j);
                    topBudsListModels.set(j, topBudsListModels.get(j+1));
                    topBudsListModels.set(j+1, topBudsListModel);
                }
            }
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TopBudsListModel topBudsListModel = topBudsListModels.get(position);
        ArrayList<TopBudsDispensaryModel> topBudsDispensaryModels = topBudsListModel.getTopBudsDispensaryModels();

        if (topBudsDispensaryModels != null && topBudsDispensaryModels.size() > 0){
            if (topBudsDispensaryModels.size() > 1){
                Fragment fragment = new TopBudDispensariesList(topBudsDispensaryModels);
                Bundle bundle = getArguments();
                fragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.top_buds_container, fragment)
                        .addToBackStack(null).commit();
            }else {
                Fragment fragment = new TopBudDetailsFragment(topBudsDispensaryModels.get(0));
                Bundle bundle = getArguments();
                fragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.top_buds_container, fragment)
                        .addToBackStack(null).commit();
            }
        }
    }
}
