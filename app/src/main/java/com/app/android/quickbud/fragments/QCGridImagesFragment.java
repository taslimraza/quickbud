package com.app.android.quickbud.fragments;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import com.app.android.quickbud.activities.RegistrationActivity;
import com.app.android.quickbud.adapters.ChatImagesAdapter;
import com.app.android.quickbud.adapters.SortingSpinnerAdapter;
import com.app.android.quickbud.modelClasses.ImagesModel;
import com.app.android.quickbud.modelClasses.LocationListModel;
import com.app.android.quickbud.network.VolleySingleton;
import com.app.android.quickbud.utils.Config;
import com.app.android.quickbud.utils.GPSTracker;
import com.app.android.quickbud.utils.Globals;
import com.app.android.quickbud.utils.SpecificMenuSingleton;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.yelp.clientlib.connection.YelpAPIFactory;
import com.yelp.clientlib.entities.Business;
import com.yelp.clientlib.entities.SearchResponse;
import com.yelp.clientlib.entities.options.CoordinateOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;

import static android.app.Activity.RESULT_OK;

public class QCGridImagesFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {

    private final int SELECT_CAMERA = 1;
    private final int SELECT_GALLERY = 2;
    private int sortByClickPosition = 0;
    private String[] sortBy;
    private ArrayList<ImagesModel> imagesArrayList;
    public static ArrayList<ImagesModel> searchedImagesList;
    private static boolean isSearched = false;
    private static boolean isLikesSelected = false;
    private static boolean isDateSelected = false;
    private ChatImagesAdapter imagesAdapter;
    private GridView gridView;
    private ImageView progressBar;
    private Uri imageUri;
    private boolean isFromMainMenuScreen = false;
    private boolean shouldCheckForPermission = true;
    private double currentLatitude;
    private double currentLongitude;
    private GPSTracker gpsTracker;
    private boolean geofence = false;
    public ArrayList<LocationListModel> filteredLocationList;
    private int clickedRestaurantPosition = 0;
    private EditText imageSearch;
    private TextView noImagesText;
    private ImageView dummyImage;

    public QCGridImagesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_qcgrid_images, container, false);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            isFromMainMenuScreen = bundle.getBoolean("isFromMainMenu");
        }

        gridView = (GridView) view.findViewById(R.id.images);
        progressBar = (ImageView) view.findViewById(R.id.loading);
        final Spinner spinner = (Spinner) view.findViewById(R.id.chat_images_sorting_spinner);
        noImagesText = (TextView) view.findViewById(R.id.no_image_text);
        imageSearch = (EditText) view.findViewById(R.id.image_search);
        ImageView searchIcon = (ImageView) view.findViewById(R.id.search_image);
//        FloatingActionButton infoBtn = (FloatingActionButton) view.findViewById(R.id.info);
        ImageView homeBtn = (ImageView) getActivity().findViewById(R.id.menu_button);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        ImageView cameraBtn = (ImageView) view.findViewById(R.id.camera_btn);
        ImageView galleryBtn = (ImageView) view.findViewById(R.id.gallery_btn);
        dummyImage = (ImageView) view.findViewById(R.id.dummy_image);
//        infoBtn.setOnClickListener(this);

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RegistrationActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                getActivity().finish();

            }
        });

        if (isFromMainMenuScreen) {
            toolbar.setTitle("Main Bud Gallery");
            galleryBtn.setVisibility(View.VISIBLE);
        } else {
            toolbar.setTitle(SpecificMenuSingleton.getInstance().getClickedRestaurant().getRestaurantName() + " Bud Pics");
            galleryBtn.setVisibility(View.GONE);
        }

        cameraBtn.setOnClickListener(this);
        galleryBtn.setOnClickListener(this);
        gridView.setOnItemClickListener(this);

        imagesArrayList = new ArrayList<>();
        sortBy = getResources().getStringArray(R.array.chat_images_sorting);

        final SortingSpinnerAdapter adapter = new SortingSpinnerAdapter(getActivity(), R.layout.restaurant_sorting_spinner, sortBy, sortByClickPosition);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 1:
                        isLikesSelected = true;
                        isDateSelected = false;
                        sortByClickPosition = position;
                        final SortingSpinnerAdapter adapter = new SortingSpinnerAdapter(getActivity(), R.layout.restaurant_sorting_spinner, sortBy, sortByClickPosition);
                        spinner.setAdapter(adapter);
                        sortByLikes();
                        if (isSearched) {
                            imagesAdapter = new ChatImagesAdapter(getActivity(), searchedImagesList, gridView, progressBar);
                            gridView.setAdapter(imagesAdapter);
//                            gridView.setOnItemClickListener(QCGridImagesFragment.this);
                        } else {
                            imagesAdapter = new ChatImagesAdapter(getActivity(), imagesArrayList, gridView, progressBar);
                            gridView.setAdapter(imagesAdapter);
//                            gridView.setOnItemClickListener(QCGridImagesFragment.this);
                        }
                        break;

                    case 2:
                        isLikesSelected = false;
                        isDateSelected = true;
                        sortByClickPosition = position;
                        final SortingSpinnerAdapter adapter1 = new SortingSpinnerAdapter(getActivity(), R.layout.restaurant_sorting_spinner, sortBy, sortByClickPosition);
                        spinner.setAdapter(adapter1);
                        sortByDate();
                        if (isSearched) {
                            imagesAdapter = new ChatImagesAdapter(getActivity(), searchedImagesList, gridView, progressBar);
                            gridView.setAdapter(imagesAdapter);
//                            gridView.setOnItemClickListener(QCGridImagesFragment.this);
                        } else {
                            imagesAdapter = new ChatImagesAdapter(getActivity(), imagesArrayList, gridView, progressBar);
                            gridView.setAdapter(imagesAdapter);
//                            gridView.setOnItemClickListener(QCGridImagesFragment.this);
                        }
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                isLikesSelected = false;
                isDateSelected = false;
            }
        });


        getChatImages(gridView, progressBar, noImagesText);

//        switch (sortByClickPosition) {
//
//            case 0:
//                if (isSearched) {
//                    ChatImagesAdapter imagesAdapter = new ChatImagesAdapter(getActivity(), searchedImagesList);
//                    gridView.setAdapter(imagesAdapter);
//                    gridView.setOnItemClickListener(QCGridImagesFragment.this);
//                    progressBar.setVisibility(View.GONE);
//                } else {
//                    getChatImages(gridView, progressBar, noImagesText);
//                }
//                break;
//            case 1:
//                isLikesSelected = true;
//                isDateSelected = false;
//                getChatImages(gridView, progressBar, noImagesText);
//                sortByLikes();
//                if (isSearched) {
//                    imagesAdapter = new ChatImagesAdapter(getActivity(), searchedImagesList);
//                    gridView.setAdapter(imagesAdapter);
//                    gridView.setOnItemClickListener(QCGridImagesFragment.this);
//                } else {
//                    imagesAdapter = new ChatImagesAdapter(getActivity(), imagesArrayList);
//                    gridView.setAdapter(imagesAdapter);
//                    gridView.setOnItemClickListener(QCGridImagesFragment.this);
//                }
//                break;
//
//            case 2:
//                isLikesSelected = false;
//                isDateSelected = true;
//                getChatImages(gridView, progressBar, noImagesText);
//                sortByDate();
//                if (isSearched) {
//                    imagesAdapter = new ChatImagesAdapter(getActivity(), searchedImagesList);
//                    gridView.setAdapter(imagesAdapter);
//                    gridView.setOnItemClickListener(QCGridImagesFragment.this);
//                } else {
//                    imagesAdapter = new ChatImagesAdapter(getActivity(), imagesArrayList);
//                    gridView.setAdapter(imagesAdapter);
//                    gridView.setOnItemClickListener(QCGridImagesFragment.this);
//                }
//                break;
//        }

        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);

                if (imageSearch.getText().toString() != null && imageSearch.getText().toString().length() > 2) {
                    isSearched = true;
                    searchedImagesList = new ArrayList<>();
                    for (int i = 0; i < imagesArrayList.size(); i++) {
                        if (imagesArrayList.get(i).getRestaurantName().toLowerCase().contains(imageSearch.getText().toString().trim().toLowerCase()) ||
                                imagesArrayList.get(i).getPatronName().toLowerCase().contains(imageSearch.getText().toString().trim().toLowerCase())) {
                            searchedImagesList.add(imagesArrayList.get(i));
                        }
                    }

                    if (isLikesSelected) {
                        sortByLikes();
                    } else if (isDateSelected) {
                        sortByDate();
                    } else {
                        sortByLikes();
                    }

                    ChatImagesAdapter imagesAdapter = new ChatImagesAdapter(getActivity(), searchedImagesList, gridView, progressBar);
                    gridView.setAdapter(imagesAdapter);
//                    gridView.setOnItemClickListener(QCGridImagesFragment.this);

                } else if (imageSearch.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), "Empty Search Field!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Please enter atleast 3 characters!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        imageSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);

                    if (imageSearch.getText().toString() != null && imageSearch.getText().toString().length() > 2) {
                        isSearched = true;
                        searchedImagesList = new ArrayList<>();
                        for (int i = 0; i < imagesArrayList.size(); i++) {
                            if (imagesArrayList.get(i).getRestaurantName().toLowerCase().contains(imageSearch.getText().toString().trim().toLowerCase()) ||
                                    imagesArrayList.get(i).getPatronName().toLowerCase().contains(imageSearch.getText().toString().trim().toLowerCase())) {
                                searchedImagesList.add(imagesArrayList.get(i));
                            }
                        }

                        if (isLikesSelected) {
                            sortByLikes();
                        } else if (isDateSelected) {
                            sortByDate();
                        } else {
                            sortByLikes();
                        }

                        ChatImagesAdapter imagesAdapter = new ChatImagesAdapter(getActivity(), searchedImagesList, gridView, progressBar);
                        gridView.setAdapter(imagesAdapter);
//                        gridView.setOnItemClickListener(QCGridImagesFragment.this);

                    } else if (imageSearch.getText().toString().equals("")) {
                        Toast.makeText(getActivity(), "Empty Search Field!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "Please enter atleast 3 characters!", Toast.LENGTH_SHORT).show();
                    }

                    return true;
                }

                return false;
            }
        });

        imageSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("") && (start > 0 || before > 0)) {
                    isSearched = false;
                    if (imagesArrayList != null && imagesArrayList.size() > 0) {
                        if (isDateSelected) {
                            sortByDate();
                        } else {
                            sortByLikes();
                        }
                        ChatImagesAdapter imagesAdapter = new ChatImagesAdapter(getActivity(), imagesArrayList, gridView, progressBar);
                        gridView.setAdapter(imagesAdapter);
//                        gridView.setOnItemClickListener(QCGridImagesFragment.this);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && shouldCheckForPermission) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
                } else {
                    Globals.showMissingPermissionDialog(getActivity(), null, getString(R.string.missing_permission_title),
                            getString(R.string.location_missing_message), 1000);
                }
            }
        } else {
            shouldCheckForPermission = false;
            setUpLocationData();
        }
    }

    private void setUpLocationData() {
        gpsTracker = new GPSTracker(getActivity());
        gpsTracker.startLocation();
        if (gpsTracker.canGetLocation()) {
            currentLatitude = gpsTracker.getLatitude();
            currentLongitude = gpsTracker.getLongitude();
        } else {
            showGpsEnabledAlert();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        shouldCheckForPermission = true;
        if (gpsTracker != null) {
            gpsTracker.stopLocation();
        }
    }

    private void showGpsEnabledAlert() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Location Service Disabled");
        builder.setMessage("Please enable location services.");
        builder.setPositiveButton("Enable", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 100:
                shouldCheckForPermission = !(grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_DENIED);
                if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    getActivity().finish();
                }
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isSearched = false;
    }

    private void getChatImages(final GridView gridView, final ImageView progressBar, final TextView noImageText) {
        progressBar.setVisibility(View.VISIBLE);
        SharedPreferences sharedPreference = getActivity().getSharedPreferences("user_info", Context.MODE_PRIVATE);
        Config.SESSION_TOKEN_ID = sharedPreference.getString("session_token_id", null);
        String url;
        if (isFromMainMenuScreen) {
            url = Config.PROMOTIONAL_IMAGES_URL;
        } else {
            url = Config.PROMOTIONAL_IMAGES_URL + "?placeId=" + SpecificMenuSingleton.getInstance().getClickedRestaurant().getPlaceID();
        }
        Log.i("Images url - ", url);
//        String url = "http://159.203.88.161/qt/core/images";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.i("ChatImagesActivity", response.toString());
                        progressBar.setVisibility(View.GONE);

                        imagesArrayList = new ArrayList<>();

                        try {
                            JSONArray image = response.getJSONArray("data");

                            for (int i = 0; i < image.length(); i++) {
                                JSONObject object = image.getJSONObject(i);
//                                if(imagesArrayList.size() > 1){
//                                    int matchedIndex = 0;
//                                    for (matchedIndex=0; matchedIndex<imagesArrayList.size(); matchedIndex++){
//                                        if (imagesArrayList.get(matchedIndex).getImagesUrl().equals(object.getString("url"))){
//                                            break;
//                                        }
//                                    }
//                                    if (matchedIndex == imagesArrayList.size()) {
                                ImagesModel imagesModel = new ImagesModel();
                                imagesModel.setImagesUrl(object.getString("url"));
                                if (object.has("restaurant_name")) {
                                    imagesModel.setRestaurantName(object.getString("restaurant_name"));
                                }
                                if (object.has("city")) {
                                    imagesModel.setRestaurantCity(object.getString("city"));
                                }
                                if (object.has("state")) {
                                    imagesModel.setRestaurantState(object.getString("state"));
                                }
                                if (object.has("phone_number")) {
                                    imagesModel.setUserPhoneNumber(object.getString("phone_number"));
                                }
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
                                    imagesModel.setPatronImage(" ");
                                }
                                imagesArrayList.add(imagesModel);
//                                    }
//                                }else {
//                                    ImagesModel imagesModel = new ImagesModel();
//                                    imagesModel.setImagesUrl(object.getString("url"));
//                                    imagesModel.setRestaurantName(object.getString("restaurant_name"));
//                                    imagesModel.setRestaurantCity(object.getString("city"));
//                                    imagesModel.setRestaurantState(object.getString("state"));
//                                    imagesModel.setUserPhoneNumber(object.getString("phone_number"));
//                                    imagesModel.setImageLikes(object.getInt("likes_count"));
//                                    imagesModel.setLiked(object.getBoolean("like"));
//                                    imagesModel.setImageDate(object.getString("date"));
//                                    if (!object.isNull("patron_name")) {
//                                        imagesModel.setPatronName(object.getString("patron_name"));
//                                    } else {
//                                        imagesModel.setPatronName(" ");
//                                    }
//                                    if (!object.isNull("patron_url")) {
//                                        imagesModel.setPatronImage(object.getString("patron_url"));
//                                    } else {
//                                        imagesModel.setPatronImage(" ");
//                                    }
//                                    imagesArrayList.add(imagesModel);
//                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (imagesArrayList.size() > 0) {
                            noImageText.setVisibility(View.GONE);
                            gridView.setVisibility(View.VISIBLE);

                            switch (sortByClickPosition) {
                                case 0:
//                                    sortByLikes();
                                    sortByDate();
                                    if (isSearched) {
                                        imagesAdapter = new ChatImagesAdapter(getActivity(), searchedImagesList, gridView, progressBar);
                                        gridView.setAdapter(imagesAdapter);
//                                        gridView.setOnItemClickListener(QCGridImagesFragment.this);
                                        progressBar.setVisibility(View.GONE);
                                    } else {
                                        imagesAdapter = new ChatImagesAdapter(getActivity(), imagesArrayList, gridView, progressBar);
                                        gridView.setAdapter(imagesAdapter);
//                                        gridView.setOnItemClickListener(QCGridImagesFragment.this);
                                    }
                                    break;

                                case 1:
                                    isLikesSelected = true;
                                    isDateSelected = false;
                                    sortByLikes();
                                    if (isSearched) {
                                        imagesAdapter = new ChatImagesAdapter(getActivity(), searchedImagesList, gridView, progressBar);
                                        gridView.setAdapter(imagesAdapter);
//                                        gridView.setOnItemClickListener(QCGridImagesFragment.this);
                                    } else {
                                        imagesAdapter = new ChatImagesAdapter(getActivity(), imagesArrayList, gridView, progressBar);
                                        gridView.setAdapter(imagesAdapter);
//                                        gridView.setOnItemClickListener(QCGridImagesFragment.this);
                                    }
                                    break;

                                case 2:
                                    isLikesSelected = false;
                                    isDateSelected = true;
                                    sortByDate();
                                    if (isSearched) {
                                        imagesAdapter = new ChatImagesAdapter(getActivity(), searchedImagesList, gridView, progressBar);
                                        gridView.setAdapter(imagesAdapter);
//                                        gridView.setOnItemClickListener(QCGridImagesFragment.this);
                                    } else {
                                        imagesAdapter = new ChatImagesAdapter(getActivity(), imagesArrayList, gridView, progressBar);
                                        gridView.setAdapter(imagesAdapter);
//                                        gridView.setOnItemClickListener(QCGridImagesFragment.this);
                                    }
                                    break;
                            }

//                            sortByLikes();
//                            ChatImagesAdapter imagesAdapter = new ChatImagesAdapter(getActivity(), imagesArrayList);
//                            gridView.setAdapter(imagesAdapter);
//                            gridView.setOnItemClickListener(QCGridImagesFragment.this);
                        } else {
                            noImageText.setVisibility(View.VISIBLE);
                            gridView.setVisibility(View.GONE);
                        }

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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

//        ImageView likeImage = (ImageView) view.findViewById(R.id.like_image);
//        ImageView image = (ImageView) view.findViewById(R.id.image);

//        likeImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (isSearched) {
//                    postLike(searchedImagesList.get(position).isLiked(), searchedImagesList.get(position).getImagesUrl(), position);
//                }else {
//                    postLike(imagesArrayList.get(position).isLiked(), imagesArrayList.get(position).getImagesUrl(), position);
//                }
//            }
//        });

//        image.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getActivity(), "Clicked", Toast.LENGTH_SHORT).show();
//            }
//        });

////        view.findViewById(R.id.like_image).setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                postLike(imagesArrayList.get(position).isLiked(), imagesArrayList.get(position).getImagesUrl(), position);
////            }
////        });
//
//
//        image.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {

        QCImageDetailsFragment qcImageDetailsFragment;
        if (isSearched) {
            qcImageDetailsFragment = new QCImageDetailsFragment(searchedImagesList, position);
        } else {
            qcImageDetailsFragment = new QCImageDetailsFragment(imagesArrayList, position);
        }
//                Bundle bundle = new Bundle();

//                if (isSearched) {
//                    bundle.putString("Selected Image", searchedImagesList.get(position).getImagesUrl());
//                    bundle.putString("restaurantName", searchedImagesList.get(position).getRestaurantName());
//                    bundle.putString("restaurantCity", searchedImagesList.get(position).getRestaurantCity());
//                    bundle.putString("restaurantState", searchedImagesList.get(position).getRestaurantState());
//                    bundle.putString("phoneNumber", searchedImagesList.get(position).getUserPhoneNumber());
//                    bundle.putString("patronName", searchedImagesList.get(position).getPatronName());
//                    bundle.putString("patronImage", searchedImagesList.get(position).getPatronImage());
//                    bundle.putInt("likes", searchedImagesList.get(position).getImageLikes());
//                    bundle.putBoolean("isLiked", searchedImagesList.get(position).isLiked());
//                } else {
//                    bundle.putString("Selected Image", imagesArrayList.get(position).getImagesUrl());
//                    bundle.putString("restaurantName", imagesArrayList.get(position).getRestaurantName());
//                    bundle.putString("restaurantCity", imagesArrayList.get(position).getRestaurantCity());
//                    bundle.putString("restaurantState", imagesArrayList.get(position).getRestaurantState());
//                    bundle.putString("phoneNumber", imagesArrayList.get(position).getUserPhoneNumber());
//                    bundle.putString("patronName", imagesArrayList.get(position).getPatronName());
//                    bundle.putString("patronImage", imagesArrayList.get(position).getPatronImage());
//                    bundle.putInt("likes", imagesArrayList.get(position).getImageLikes());
//                    bundle.putBoolean("isLiked", imagesArrayList.get(position).isLiked());
//                }
//
//                qcImageDetailsFragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.QCImagesFrameLayout, qcImageDetailsFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

//            }
//        });

    }

    private void sortByLikes() {

        if (isSearched) {
            for (int i = 0; i < searchedImagesList.size() - 1; i++) {
                for (int j = 0; j < searchedImagesList.size() - 1 - i; j++) {
                    if (searchedImagesList.get(j).getImageLikes() < searchedImagesList.get(j + 1).getImageLikes()) {
                        ImagesModel imagesModel = searchedImagesList.get(j);
                        searchedImagesList.set(j, searchedImagesList.get(j + 1));
                        searchedImagesList.set(j + 1, imagesModel);
                    }
                }
            }
        } else {
            if (imagesArrayList != null && imagesArrayList.size() > 0) {
                for (int i = 0; i < imagesArrayList.size() - 1; i++) {
                    for (int j = 0; j < imagesArrayList.size() - 1 - i; j++) {
                        if (imagesArrayList.get(j).getImageLikes() < imagesArrayList.get(j + 1).getImageLikes()) {
                            ImagesModel imagesModel = imagesArrayList.get(j);
                            imagesArrayList.set(j, imagesArrayList.get(j + 1));
                            imagesArrayList.set(j + 1, imagesModel);
                        }
                    }
                }
            }
        }

    }

    private void sortByDate() {

        if (isSearched) {
            for (int i = 0; i < searchedImagesList.size() - 1; i++) {
                for (int j = 0; j < searchedImagesList.size() - 1 - i; j++) {
                    if (searchedImagesList.get(j).getImageDate().compareToIgnoreCase(searchedImagesList.get(j + 1).getImageDate()) < 0) {
                        ImagesModel imagesModel = searchedImagesList.get(j);
                        searchedImagesList.set(j, searchedImagesList.get(j + 1));
                        searchedImagesList.set(j + 1, imagesModel);
                    }
                }
            }
        } else {
            if (imagesArrayList != null && imagesArrayList.size() > 0) {
                for (int i = 0; i < imagesArrayList.size() - 1; i++) {
                    for (int j = 0; j < imagesArrayList.size() - 1 - i; j++) {
                        if (imagesArrayList.get(j).getImageDate().compareToIgnoreCase(imagesArrayList.get(j + 1).getImageDate()) < 0) {
                            ImagesModel imagesModel = imagesArrayList.get(j);
                            imagesArrayList.set(j, imagesArrayList.get(j + 1));
                            imagesArrayList.set(j + 1, imagesModel);
                        }
                    }
                }
            }
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

//            case R.id.info:
//
//                QuickPicsInfoFragment fragment = new QuickPicsInfoFragment();
//                getActivity().getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.QCImagesFrameLayout, fragment)
//                        .addToBackStack(null).commit();
//
//                break;

            case R.id.camera_btn:
                if (isFromMainMenuScreen) {
                    geofence = false;
                    openCamera();
                } else {
                    if (checkGeoFence()) {
                        geofence = true;
                        startCameraIntent();
//                        openCamera();
//                        new YelpRestaurantList().execute();
                    } else {
                        geofence = false;
                        openCamera();
                    }
                }

                break;

            case R.id.gallery_btn:

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, SELECT_GALLERY);

                break;
        }
    }

    private void startCameraIntent() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera");
        imageUri = getActivity().getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, SELECT_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

            case SELECT_CAMERA:
                if (resultCode == RESULT_OK) {
                    imageSearch.setText("");
                    Glide.with(this).load(imageUri).asBitmap().listener(new RequestListener<Uri, Bitmap>() {
                        @Override
                        public boolean onException(Exception e, Uri model, Target<Bitmap> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap bitmap, Uri model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            sendPicture(bitmap, false);
                            return false;
                        }
                    }).override(1280, 960).into(dummyImage);
//                    if (imageUri == null) {
//                        return;
//                    }
//                    getActivity().getContentResolver().notifyChange(imageUri, null);
//                    ContentResolver cr = getActivity().getContentResolver();
//                    Bitmap bitmap = null;
//                    try {
//                        bitmap = MediaStore.Images.Media.getBitmap(cr, imageUri);
//                    } catch (IOException e) {
//                    }
//
//                    ExifInterface exifInterface = null;
//                    try {
//                        exifInterface = new ExifInterface(imageUri.getPath());
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//
//                    int rotation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
//
//                    System.out.println("rotation = " + rotation);
//
//                    SharedPreferences preferences = getActivity().getSharedPreferences("user_info", getActivity().MODE_PRIVATE);
//                    String deviceName = preferences.getString("device_name", null);
//
//                    if (deviceName.equals("samsung") || deviceName.equals("Sony") || deviceName.equalsIgnoreCase("LGE")) {
//
//                        if (Build.VERSION.SDK_INT > 17) {
//                            if (rotation == 6) {
//                                // portrait == 6
//                                Matrix matrix = new Matrix();
//                                matrix.postRotate(90);
//                                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
//                            }
//
//                            if (rotation == 8) {
//                                // portrait == 8 front camera
//                                Matrix matrix = new Matrix();
//                                matrix.postRotate(270);
//                                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
//                            }
//                        }
//                    }
//
////                    imageUri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, null, null));
//
//                    int actualWidth = bitmap.getWidth();
//                    int actualHeight = bitmap.getHeight();
//                    int maxHeight = 1280;//1136.0;
//                    int maxWidth = 960;//640.0;
//                    float imgRatio = (float) actualWidth / actualHeight;
//                    float maxRatio = (float) maxWidth / maxHeight;
//                    if (actualHeight > maxHeight || actualWidth > maxWidth) {
//                        if (imgRatio < maxRatio) {
//                            //adjust width according to maxHeight
//                            imgRatio = (float) maxHeight / actualHeight;
//                            actualWidth = (int) (imgRatio * actualWidth);
//                            actualHeight = maxHeight;
//                        } else if (imgRatio > maxRatio) {
//                            //adjust height according to maxWidth
//                            imgRatio = (float) maxWidth / actualWidth;
//                            actualHeight = (int) (imgRatio * actualHeight);
//                            actualWidth = maxWidth;
//                        } else {
//                            actualHeight = maxHeight;
//                            actualWidth = maxWidth;
//                        }
//                    }
//                    bitmap = Bitmap.createScaledBitmap(bitmap, actualWidth, actualHeight, true);
//
//                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
//                    byte[] bytes = baos.toByteArray();
//
//                    String encoded = Base64.encodeToString(bytes, Base64.DEFAULT);
//
//                    postImage(encoded, imageUri.getPath(), null, null);

                }

                break;

            case SELECT_GALLERY:
                if (resultCode == RESULT_OK) {
                    imageSearch.setText("");
                    if (data != null) {

                        try {
                            imageUri = data.getData();
                            Glide.with(this).load(imageUri).asBitmap().listener(new RequestListener<Uri, Bitmap>() {
                                @Override
                                public boolean onException(Exception e, Uri model, Target<Bitmap> target, boolean isFirstResource) {
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Bitmap bitmap, Uri model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                    sendPicture(bitmap, true);
                                    return false;
                                }
                            }).override(1280, 960).into(dummyImage);
//                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
//
//                            ExifInterface exifInterface = null;
//                            try {
//                                exifInterface = new ExifInterface(imageUri.getPath());
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//
//                            int rotation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
//
//                            System.out.println("rotation = " + rotation);
//
//                            SharedPreferences preferences = getActivity().getSharedPreferences("user_info", getActivity().MODE_PRIVATE);
//                            String deviceName = preferences.getString("device_name", null);
//
//                            if (deviceName.equals("samsung") || deviceName.equals("Sony") || deviceName.equalsIgnoreCase("LGE")) {
//
//                                if (Build.VERSION.SDK_INT > 17) {
//                                    if (rotation == 6) {
//                                        // portrait == 6
//                                        Matrix matrix = new Matrix();
//                                        matrix.postRotate(90);
//                                        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
//                                    }
//
//                                    if (rotation == 8) {
//                                        // portrait == 8 front camera
//                                        Matrix matrix = new Matrix();
//                                        matrix.postRotate(270);
//                                        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
//                                    }
//                                }
//                            }

//                            int actualWidth = bitmap.getWidth();
//                            int actualHeight = bitmap.getHeight();
//                            int maxHeight = 1280;//1136.0;
//                            int maxWidth = 960;//640.0;
//                            float imgRatio = (float) actualWidth / actualHeight;
//                            float maxRatio = (float) maxWidth / maxHeight;
//                            if (actualHeight > maxHeight || actualWidth > maxWidth) {
//                                if (imgRatio < maxRatio) {
//                                    //adjust width according to maxHeight
//                                    imgRatio = (float) maxHeight / actualHeight;
//                                    actualWidth = (int) (imgRatio * actualWidth);
//                                    actualHeight = maxHeight;
//                                } else if (imgRatio > maxRatio) {
//                                    //adjust height according to maxWidth
//                                    imgRatio = (float) maxWidth / actualWidth;
//                                    actualHeight = (int) (imgRatio * actualHeight);
//                                    actualWidth = maxWidth;
//                                } else {
//                                    actualHeight = maxHeight;
//                                    actualWidth = maxWidth;
//                                }
//                            }
//                            bitmap = Bitmap.createScaledBitmap(bitmap, actualWidth, actualHeight, true);
//
//                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
//                            byte[] bytes = baos.toByteArray();
//
//                            String encoded = Base64.encodeToString(bytes, Base64.DEFAULT);

//                            postImage(encoded, null, null, null);


                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                }

                break;
        }
    }

    private void sendPicture(Bitmap bitmap, boolean isGalleryUpload){
        int actualWidth = bitmap.getWidth();
        int actualHeight = bitmap.getHeight();
        int maxHeight = 1280;//1136.0;
        int maxWidth = 960;//640.0;
        float imgRatio = (float) actualWidth / actualHeight;
        float maxRatio = (float) maxWidth / maxHeight;
        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                //adjust width according to maxHeight
                imgRatio = (float) maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = maxHeight;
            } else if (imgRatio > maxRatio) {
                //adjust height according to maxWidth
                imgRatio = (float) maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = maxWidth;
            } else {
                actualHeight = maxHeight;
                actualWidth = maxWidth;
            }
        }
        bitmap = Bitmap.createScaledBitmap(bitmap, actualWidth, actualHeight, true);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        byte[] bytes = baos.toByteArray();

        String encoded = Base64.encodeToString(bytes, Base64.DEFAULT);

        if (isGalleryUpload){
            postImage(encoded, null, null, null);
        }else {
            postImage(encoded, imageUri.getPath(), null, null);
        }
    }

    private void openCamera() {

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photo = null;
        try {
            File directory = new File(Environment.getExternalStorageDirectory() + File.separator + "QuickBud");
            boolean success = true;
            if (!directory.exists()) {
                success = directory.mkdir();
            }
            Log.i("QuickChat ", "File inserting!" + success);
            if (success) {
                photo = new File(directory.toString() + File.separator + System.currentTimeMillis() + ".jpg");
                Log.i("QuickChat ", "File inserted! " + photo.toString());
            } else {
                photo = new File(directory.toString() + File.separator + System.currentTimeMillis() + ".jpg");
                Log.i("QuickChat ", "File inserted!");
            }

        } catch (Exception e) {

        }
        imageUri = Uri.fromFile(photo);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(cameraIntent, SELECT_CAMERA);

    }

    class YelpRestaurantList extends AsyncTask<Void, String, Void> {
        String[] restaurantNamesList;
        ProgressDialog dialog = new ProgressDialog(getActivity());

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Fetching dispensaries data...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... param) {
            filteredLocationList = new ArrayList<>();
            YelpAPIFactory apiFactory = new YelpAPIFactory(Config.YELP_CONSUMER_KEY, Config.YELP_CONSUMER_SECRET,
                    Config.YELP_TOKEN, Config.YELP_TOKEN_SECRET);
            com.yelp.clientlib.connection.YelpAPI yelpAPI = apiFactory.createAPI();
            Map<String, String> params = new HashMap<>();
            // general params
            params.put("term", "Dispensaries");
            params.put("radius_filter", "320");
            CoordinateOptions coordinate = CoordinateOptions.builder()
                    .latitude(currentLatitude)
                    .longitude(currentLongitude).build();

            Call<SearchResponse> call = yelpAPI.search(coordinate, params);
            retrofit2.Response<SearchResponse> response = null;
            try {
                response = call.execute();
                System.out.println("LOG: Dispensaries List - " + response.body());
                ArrayList<Business> businesses = response.body().businesses();
                for (int i = 0; i < businesses.size(); i++) {
                    LocationListModel locationListModel = new LocationListModel();
                    locationListModel.setRestaurantName(businesses.get(i).name());
                    if (businesses.get(i).location().address().size() > 0) {
                        locationListModel.setRestaurantAddress(businesses.get(i).location().address().get(0));
                    }
                    locationListModel.setRestaurantCity(businesses.get(i).location().city());
                    locationListModel.setRestaurantState(businesses.get(i).location().stateCode());
                    locationListModel.setRestaurantDistance(businesses.get(i).distance());
                    locationListModel.setRestaurantPhone(businesses.get(i).displayPhone());
                    locationListModel.setRatingImage(businesses.get(i).ratingImgUrl());
                    locationListModel.setPlaceID(businesses.get(i).id());
                    locationListModel.setLatitude(businesses.get(i).location().coordinate().latitude());
                    locationListModel.setLongitude(businesses.get(i).location().coordinate().longitude());
                    filteredLocationList.add(locationListModel);
                }
            } catch (IOException e) {

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            if (filteredLocationList.size() > 0) {
                if (filteredLocationList.size() == 1) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            if (SpecificMenuSingleton.getInstance().getClickedRestaurant().getPlaceID().
//                                    equalsIgnoreCase(filteredLocationList.get(0).getPlaceID())) {
                            openCamera();
//                            }
//                            openChatScreen(filteredLocationList.get(0));
//                            Toast.makeText(MainMenuActivity.this, "Fetching restaurant data!", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            chatMsgTxt.setText(getResources().getText(R.string.in_geofence));
//                            chatMsgLayout.setVisibility(View.VISIBLE);
//                            mainMenuList.setVisibility(View.GONE);
//                            restaurantListView.setVisibility(View.VISIBLE);
                            restaurantNamesList = new String[filteredLocationList.size()];
//                            restaurantIds = new String[filteredLocationList.size()];
                            for (int i = 0; i < filteredLocationList.size(); i++) {
                                restaurantNamesList[i] = filteredLocationList.get(i).getRestaurantName();
//                                restaurantIds[i] = filteredLocationList.get(i).getPlaceID();
                            }
                            showChatRestaurantList(restaurantNamesList, filteredLocationList);
//                            Toast.makeText(MainMenuActivity.this, "Fetching multiple restaurant data!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }
    }

    private void showChatRestaurantList(String[] restaurantNames, final ArrayList<LocationListModel> locationList) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Multiple location found, select anyone:-");

        builder.setItems(restaurantNames, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                clickedRestaurantPosition = which;
                openCamera();
//                getActivity().finish();
//                startActivity(new Intent(getActivity(), ChatImagesActivity.class));
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

//        ChatRestaurantListAdapter adapter = new ChatRestaurantListAdapter(this, restaurantNamesList);
//        restaurantListView.setAdapter(adapter);
//        restaurantListView.setOnItemClickListener(this);

    }

    private boolean checkGeoFence() {

        Location currentLocation = new Location("current location");
        currentLocation.setLatitude(currentLatitude);
        currentLocation.setLongitude(currentLongitude);

        Location restaurantLocation = new Location("restaurant location");
        restaurantLocation.setLatitude(SpecificMenuSingleton.getInstance().getClickedRestaurant().getLatitude());
        restaurantLocation.setLongitude(SpecificMenuSingleton.getInstance().getClickedRestaurant().getLongitude());

        float distance = currentLocation.distanceTo(restaurantLocation);

        if (distance < 320) {
            return true;
        } else {
            return false;
        }
    }

    private void postImage(final String image, final String imageUri, final String date, String msgId) {

        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Uploading image...");
        dialog.show();
        SharedPreferences sharedPreference = getActivity().getSharedPreferences("user_info", getActivity().MODE_PRIVATE);
        Config.SESSION_TOKEN_ID = sharedPreference.getString("session_token_id", null);
        String userPhone = sharedPreference.getString("user_phone", null);

        final String url = Config.QT_IMAGE_UPLOAD;

        JSONObject object = new JSONObject();
        try {
            object.put("image", image);
            object.put("gallery", true);
            object.put("geofence", geofence);
//            if (geofence) {
//                object.put("placeId", filteredLocationList.get(clickedRestaurantPosition).getPlaceID());
//                object.put("city", filteredLocationList.get(clickedRestaurantPosition).getRestaurantCity());
//                object.put("restaurant_name", filteredLocationList.get(clickedRestaurantPosition).getRestaurantName());
//                object.put("address_line", filteredLocationList.get(clickedRestaurantPosition).getRestaurantAddress());
//                object.put("state", filteredLocationList.get(clickedRestaurantPosition).getRestaurantState());
//            } else {
            if (SpecificMenuSingleton.getInstance().getClickedRestaurant() != null) {
                object.put("placeId", SpecificMenuSingleton.getInstance().getClickedRestaurant().getPlaceID());
                object.put("city", SpecificMenuSingleton.getInstance().getClickedRestaurant().getRestaurantCity());
                object.put("restaurant_name", SpecificMenuSingleton.getInstance().getClickedRestaurant().getRestaurantName());
                object.put("address_line", SpecificMenuSingleton.getInstance().getClickedRestaurant().getRestaurantAddress());
                object.put("state", SpecificMenuSingleton.getInstance().getClickedRestaurant().getRestaurantState());
            }
//            }
            object.put("phone_number", userPhone);
        } catch (JSONException e) {

        }

//        byte[] imageBytes = Base64.decode(image, Base64.DEFAULT);
//        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
//        int imageHeight = bitmap.getHeight();
//        int imageWidth = bitmap.getWidth();
//        Log.i("ImageSize", "height - " + imageHeight + "width - " + imageWidth);

//        Log.i("imageEncode", object.toString());

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("ImageUpload", response.toString());
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        if (getActivity() != null) {

                            try {
                                JSONArray data = response.getJSONArray("result");

                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject dataObj = data.getJSONObject(i);
                                    ImagesModel imagesModel = new ImagesModel();
                                    if (isFromMainMenuScreen) {
                                        imagesModel.setImagesUrl(dataObj.getString("url"));
                                        imagesModel.setRestaurantName(dataObj.getString("restaurant_name"));
                                        imagesModel.setRestaurantCity(dataObj.getString("city"));
                                        imagesModel.setRestaurantState(dataObj.getString("state"));
                                        imagesModel.setUserPhoneNumber(dataObj.getString("phone_number"));
                                        imagesModel.setImageLikes(dataObj.getInt("likes_count"));
                                        imagesModel.setLiked(false);
                                        imagesModel.setImageDate(dataObj.getString("date"));
                                        if (!dataObj.isNull("patron_name")) {
                                            imagesModel.setPatronName(dataObj.getString("patron_name"));
                                        } else {
                                            imagesModel.setPatronName(" ");
                                        }
                                        if (!dataObj.isNull("patron_url")) {
                                            imagesModel.setPatronImage(dataObj.getString("patron_url"));
                                        } else {
                                            imagesModel.setPatronImage(" ");
                                        }
                                        imagesArrayList.add(imagesModel);
                                    } else {
                                        if (SpecificMenuSingleton.getInstance().getClickedRestaurant().getPlaceID().
                                                equalsIgnoreCase(dataObj.getString("placeId"))) {
                                            imagesModel.setImagesUrl(dataObj.getString("url"));
                                            imagesModel.setRestaurantName(dataObj.getString("restaurant_name"));
                                            imagesModel.setRestaurantCity(dataObj.getString("city"));
                                            imagesModel.setRestaurantState(dataObj.getString("state"));
                                            imagesModel.setUserPhoneNumber(dataObj.getString("phone_number"));
                                            imagesModel.setImageLikes(dataObj.getInt("likes_count"));
                                            imagesModel.setLiked(false);
                                            imagesModel.setImageDate(dataObj.getString("date"));
                                            if (!dataObj.isNull("patron_name")) {
                                                imagesModel.setPatronName(dataObj.getString("patron_name"));
                                            } else {
                                                imagesModel.setPatronName(" ");
                                            }
                                            if (!dataObj.isNull("patron_url")) {
                                                imagesModel.setPatronImage(dataObj.getString("patron_url"));
                                            } else {
                                                imagesModel.setPatronImage(" ");
                                            }
                                            imagesArrayList.add(imagesModel);
                                        }
                                    }
                                }

                                if (imagesArrayList != null && imagesArrayList.size() > 0) {
                                    noImagesText.setVisibility(View.GONE);
                                    gridView.setVisibility(View.VISIBLE);
                                }

                                imagesAdapter = new ChatImagesAdapter(getActivity(), imagesArrayList, gridView, progressBar);
                                gridView.setAdapter(imagesAdapter);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

//                        Toast.makeText(ChatActivity.this, "Image uploaded", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
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
                Config.TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        VolleySingleton.getInstance(getActivity()).addToRequestQueue(request);
    }

    private void postLike(boolean isLiked, String imageUrl, final int position) {

        progressBar.setVisibility(View.VISIBLE);
        gridView.setVisibility(View.GONE);

        isLiked = !isLiked;
        String url = Config.IMAGE_LIKE_URL;

        JSONObject object = new JSONObject();
        try {
            object.put("url", imageUrl);
            object.put("like", isLiked);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("LikePost", object.toString());

        final boolean finalIsLiked = isLiked;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("LikedResponse", response.toString());

                        progressBar.setVisibility(View.GONE);
                        gridView.setVisibility(View.VISIBLE);

                        int imageLikes = imagesArrayList.get(position).getImageLikes();

                        if (finalIsLiked) {
                            imagesArrayList.get(position).setLiked(true);
                            imageLikes++;
                            imagesArrayList.get(position).setImageLikes(imageLikes);

                        } else {
                            imagesArrayList.get(position).setLiked(false);
                            imageLikes--;
                            imagesArrayList.get(position).setImageLikes(imageLikes);
                        }
                        imagesAdapter.notifyDataSetChanged();

//                        if (QCGridImagesFragment.searchedImagesList != null && QCGridImagesFragment.searchedImagesList.size() > 0){
//                            for (int i = 0; i < QCGridImagesFragment.searchedImagesList.size(); i++) {
//                                if (QCGridImagesFragment.searchedImagesList.get(i).getImagesUrl().equalsIgnoreCase(imageUrl)) {
//                                    QCGridImagesFragment.searchedImagesList.get(i).setImageLikes(imageLikes);
//                                    QCGridImagesFragment.searchedImagesList.get(i).setLiked(finalIsLiked);
//                                }
//                            }
//                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
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
                Config.TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        VolleySingleton.getInstance(getActivity()).addToRequestQueue(request);
    }
}
