package com.app.android.quickbud.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

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
import com.app.android.quickbud.adapters.ImageWinnerAdapter;
import com.app.android.quickbud.modelClasses.ImageWinnerModel;
import com.app.android.quickbud.network.VolleySingleton;
import com.app.android.quickbud.utils.Config;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class ImageWinnerFragment extends Fragment {

    private ArrayList<ImageWinnerModel> imagesArrayList;

    public ImageWinnerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layoutView = inflater.inflate(R.layout.fragment_image_winner, container, false);
        ListView listView = (ListView) layoutView.findViewById(R.id.list_details);
        ProgressBar progressBar = (ProgressBar) layoutView.findViewById(R.id.loading);
        getWinnerList(listView, progressBar);
        return layoutView;
    }

    private void getWinnerList(final ListView listView, final ProgressBar progressBar) {

        listView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        String url = Config.ROOT_URL + "/qt/api/winners/";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("Winner Response", response.toString());
                        progressBar.setVisibility(View.GONE);
                        listView.setVisibility(View.VISIBLE);

                        imagesArrayList = new ArrayList<>();

                        try {
                            JSONArray image = response.getJSONArray("data");

                            for (int i = 0; i < image.length(); i++) {
                                JSONObject object = image.getJSONObject(i);
                                ImageWinnerModel imagesModel = new ImageWinnerModel();
                                imagesModel.setImagesUrl(object.getString("url"));
                                imagesModel.setRestaurantName(object.getString("restaurant_name"));
                                imagesModel.setRestaurantCity(object.getString("city"));
                                imagesModel.setRestaurantState(object.getString("state"));
                                imagesModel.setUserPhoneNumber(object.getString("phone_number"));
                                imagesModel.setImageLikes(object.getInt("likes_count"));
                                imagesModel.setLiked(object.getBoolean("like"));
                                imagesModel.setImageDate(object.getString("date"));
                                if (!object.isNull("patron_name")) {
                                    imagesModel.setPatronName(object.getString("patron_name"));
                                } else {
                                    imagesModel.setPatronName(" ");
                                }
                                if (!object.isNull("patron_url")) {
                                    imagesModel.setPatronImage(object.getString("patron_url"));
                                } else {
                                    imagesModel.setPatronImage(null);
                                }
                                imagesArrayList.add(imagesModel);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        sortByLikes();
                        ImageWinnerAdapter ImageWinnerAdapter = new ImageWinnerAdapter(getActivity(), imagesArrayList);
                        listView.setAdapter(ImageWinnerAdapter);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error != null) {
                    progressBar.setVisibility(View.GONE);
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
                Config.TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        VolleySingleton.getInstance(getActivity()).addToRequestQueue(request);

    }

    private void sortByLikes() {

        for (int i = 0; i < imagesArrayList.size() - 1; i++) {
            for (int j = 0; j < imagesArrayList.size() - 1 - i; j++) {
                if (imagesArrayList.get(j).getImageLikes() < imagesArrayList.get(j + 1).getImageLikes()) {
                    ImageWinnerModel imagesModel = imagesArrayList.get(j);
                    imagesArrayList.set(j, imagesArrayList.get(j + 1));
                    imagesArrayList.set(j + 1, imagesModel);
                }
            }
        }

    }

}
