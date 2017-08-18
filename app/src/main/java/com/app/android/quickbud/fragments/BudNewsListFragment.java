package com.app.android.quickbud.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

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
import com.app.android.quickbud.adapters.BudNewsAdapter;
import com.app.android.quickbud.modelClasses.BudNewsModel;
import com.app.android.quickbud.network.VolleySingleton;
import com.app.android.quickbud.utils.Config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class BudNewsListFragment extends Fragment implements AdapterView.OnItemClickListener {

    private ImageView loadingImage;
    private ListView budNewsList;
    private ArrayList<BudNewsModel> budNewsModels;

    public BudNewsListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bud_news_list, container, false);

        loadingImage = (ImageView) view.findViewById(R.id.loading_image);
        budNewsList = (ListView) view.findViewById(R.id.bud_news_list);

        getBudNewsList();

        return view;
    }

    private void getBudNewsList() {

        loadingImage.setVisibility(View.VISIBLE);
        budNewsList.setVisibility(View.GONE);

        String url = Config.BUD_NEWS_URL;

        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        System.out.println("Bud News - " + response);

                        loadingImage.setVisibility(View.GONE);
                        budNewsList.setVisibility(View.VISIBLE);

                        budNewsModels = new ArrayList<>();

                        try {
                            JSONArray responseJSONArray = response.getJSONArray("data");

                            for (int i = 0; i < responseJSONArray.length(); i++) {
                                JSONObject object = responseJSONArray.getJSONObject(i);
                                BudNewsModel budNewsModel = new BudNewsModel();

//                                String articleDate = null;
//                                try {
//                                    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
//                                    Date date = sdf1.parse(object.getString("date"));
//                                    SimpleDateFormat sdf2 = new SimpleDateFormat("MMMM dd,yyyy");
//                                    articleDate = sdf2.format(date);
//                                } catch (ParseException e) {
//                                    e.printStackTrace();
//                                }


                                budNewsModel.setArticleDate(object.getString("date"));
                                budNewsModel.setArticleHeadline(object.getString("headline"));
                                budNewsModel.setArticleSource(object.getString("source"));
                                budNewsModel.setArticleLink(object.getString("link"));
                                if (object.has("url")) {
                                    budNewsModel.setArticleImage(Config.QUICK_CHAT_IMAGE + object.getString("url"));
                                }

                                budNewsModels.add(budNewsModel);
                            }

                            sortByDate();

                            BudNewsAdapter adapter = new BudNewsAdapter(getActivity(), budNewsModels);
                            budNewsList.setAdapter(adapter);
                            budNewsList.setOnItemClickListener(BudNewsListFragment.this);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingImage.setVisibility(View.GONE);
                budNewsList.setVisibility(View.VISIBLE);
                if (error instanceof TimeoutError) {
                    Config.internetSlowError(getActivity());
                } else if (error instanceof NoConnectionError) {
                    Config.internetSlowError(getActivity());
                } else if (error instanceof ServerError) {
                    Config.serverError(getActivity());
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Bundle bundle = new Bundle();
        bundle.putString("url", budNewsModels.get(position).getArticleLink());

        Fragment fragment = new BudNewsDetailsFragment();
        fragment.setArguments(bundle);

        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.bud_news_container, fragment)
                .addToBackStack(null).commit();

    }

    private void sortByDate() {

        if (budNewsModels != null && budNewsModels.size() > 0) {
            for (int i = 0; i < budNewsModels.size() - 1; i++) {
                for (int j = 0; j < budNewsModels.size() - 1 - i; j++) {
                    if (budNewsModels.get(j).getArticleDate().compareToIgnoreCase(budNewsModels.get(j + 1).getArticleDate()) < 0) {
                        BudNewsModel budNewsModel = budNewsModels.get(j);
                        budNewsModels.set(j, budNewsModels.get(j + 1));
                        budNewsModels.set(j + 1, budNewsModel);
                    }
                }
            }

            for (int k=0; k<budNewsModels.size(); k++){
                try {
                    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
                    Date date = sdf1.parse(budNewsModels.get(k).getArticleDate());
                    SimpleDateFormat sdf2 = new SimpleDateFormat("MMMM dd,yyyy");
                    budNewsModels.get(k).setArticleDate(sdf2.format(date));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
