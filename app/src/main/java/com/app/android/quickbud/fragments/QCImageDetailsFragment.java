package com.app.android.quickbud.fragments;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.app.android.quickbud.activities.MainMenuActivity;
import com.app.android.quickbud.adapters.QCImageDetailsAdapter;
import com.app.android.quickbud.modelClasses.ImagesModel;
import com.app.android.quickbud.network.VolleySingleton;
import com.app.android.quickbud.utils.Config;
import com.app.android.quickbud.utils.CustomRecyclerView;
import com.app.android.quickbud.utils.ProfileRoundView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class QCImageDetailsFragment extends Fragment implements View.OnClickListener {

    private String imageUrl = null;
    private boolean isLiked = false;
    private ImageView likeButton, shareButton, imageView;
    private TextView numberOfLikes;
    private Bitmap imageBitmap, shareImageBitmap;
    private int imageLikes;
    private ProfileRoundView userProfileImage;
    private ProgressBar loading;
    private LinearLayout imageDetailsLayout;
    private Uri tempImageUri;
    private ArrayList<ImagesModel> imagesModels;
    private int position;

    public QCImageDetailsFragment() {
        // Required empty public constructor

    }

    public QCImageDetailsFragment(ArrayList<ImagesModel> imagesModels, int position) {
        // Required empty public constructor
        this.imagesModels = imagesModels;
        this.position = position;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layoutView = inflater.inflate(R.layout.fragment_qcimage_details, container, false);

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Gallery");

        ImageView homeBtn = (ImageView) getActivity().findViewById(R.id.menu_button);
        homeBtn.setVisibility(View.VISIBLE);

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MainMenuActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                getActivity().finish();

            }
        });

        CustomRecyclerView imageDetailsView = (CustomRecyclerView) layoutView.findViewById(R.id.imagesRecyclerView);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        imageDetailsView.setLayoutManager(llm);
        Log.i("position", "" + position);
//        imageDetailsView.findViewHolderForAdapterPosition(position);
        imageDetailsView.scrollToPosition(position);
        QCImageDetailsAdapter adapter = new QCImageDetailsAdapter(getActivity(), imagesModels);
        imageDetailsView.setAdapter(adapter);

//        imageView = (ImageView) layoutView.findViewById(R.id.image_selected);
//        likeButton = (ImageView) layoutView.findViewById(R.id.like_button);
//        shareButton = (ImageView) layoutView.findViewById(R.id.share_button);
//        numberOfLikes = (TextView) layoutView.findViewById(R.id.likes_number);
//        TextView locationDetails = (TextView) layoutView.findViewById(R.id.restaurant_details);
//        userProfileImage = (ProfileRoundView) layoutView.findViewById(R.id.user_profile);
//        TextView userName = (TextView) layoutView.findViewById(R.id.user_name);
//        loading = (ProgressBar) layoutView.findViewById(R.id.loading);
//        imageDetailsLayout = (LinearLayout) layoutView.findViewById(R.id.image_details_layout);
//        imageView.setEnabled(false);
//        Bundle bundle = this.getArguments();
//        imageUrl = bundle.getString("Selected Image");
//        loading.setVisibility(View.VISIBLE);
//        Glide.with(getActivity())/*.load("https://s3-us-west-2.amazonaws.com/stagingquicktable/chats/" + imageUrl)*/
//                .load(Config.QUICK_CHAT_IMAGES + imageUrl)
//                .asBitmap()
//                .listener(new RequestListener<String, Bitmap>() {
//                    @Override
//                    public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
//                        return false;
//                    }
//
//                    @Override
//                    public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
//                        loading.setVisibility(View.GONE);
//                        imageView.setEnabled(true);
//                        imageBitmap = resource;
//                        Bitmap.Config config = imageBitmap.getConfig();
//                        int height = imageBitmap.getHeight();
//                        int width = imageBitmap.getWidth();
//
//                        shareImageBitmap = Bitmap.createBitmap(width, height, config);
//
//                        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_qt_small);
//                        Canvas canvas = new Canvas(shareImageBitmap);
//                        canvas.drawBitmap(imageBitmap, 0, 0, null);
//
//                        Paint paint = new Paint();
//                        paint.setColor(getResources().getColor(R.color.primary_color));
//                        paint.setTextSize(40);
//
////                        canvas.drawBitmap(bitmap, width-310, height-60, null);
////                        canvas.drawText("QuickTable", width - 250, height - 10, paint);
//                        canvas.drawBitmap(bitmap, width-320, height-70, null);
//                        canvas.drawText("QuickTable", width - 250, height - 20, paint);
//
//                        return false;
//                    }
//                })
//                .into(new BitmapImageViewTarget(imageView) {
//                    @Override
//                    protected void setResource(Bitmap resource) {
//                        int width = resource.getWidth();
//                        int height = resource.getHeight();
//                        Bitmap bitmap = null;
//                        if (width > height) {
//                            bitmap = Bitmap.createBitmap(resource, 100, 0, height, height);
//                        } else if (width < height) {
//                            bitmap = Bitmap.createBitmap(resource, 0, 100, width, width);
//                        } else {
//                            bitmap = Bitmap.createBitmap(resource, 0, 0, width, height);
//                        }
//                        super.setResource(bitmap);
//                    }
//                });
//        imageLikes = bundle.getInt("likes");
//        if (imageLikes == 1){
//            numberOfLikes.setText(imageLikes + " Like");
//        }else {
//            numberOfLikes.setText(imageLikes + " Likes");
//        }
//        locationDetails.setText(bundle.getString("restaurantName") + "\n" + bundle.getString("restaurantCity") + ", " + bundle.getString("restaurantState"));
//        isLiked = bundle.getBoolean("isLiked");
//        userName.setText(bundle.getString("patronName"));
//
//        if (isLiked) {
//            likeButton.setImageResource(R.mipmap.ic_favorite);
//        } else {
//            likeButton.setImageResource(R.mipmap.ic_dislike);
//        }
//
//        if (bundle.getString("patronImage") != null) {
//            Glide.with(getActivity())/*.load("https://s3-us-west-2.amazonaws.com/stagingquicktable/profile/" + bundle.getString("patronImage"))*/
//                    .load(Config.QUICK_CHAT_IMAGE + bundle.getString("patronImage"))
//                    .asBitmap()
//                    .placeholder(R.mipmap.default_profile_pic)
//                    .into(userProfileImage);
//        }
//
//        likeButton.setOnClickListener(this);
//        shareButton.setOnClickListener(this);
//        imageView.setOnClickListener(this);
//        imageView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                saveImage();
//                return true;
//            }
//        });

        return layoutView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.like_button:
                likeButton.setOnClickListener(null);
                postLike();
                break;

            case R.id.share_button:
                shareApp();
                break;

            case R.id.image_selected:
                showFullImage();
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        Glide.clear(userProfileImage);
//        Glide.clear(imageView);
        if (tempImageUri != null){
            getActivity().getContentResolver().delete(tempImageUri, null, null);
        }
    }

    private void postLike() {
        imageDetailsLayout.setVisibility(View.VISIBLE);
        loading.setVisibility(View.VISIBLE);
        isLiked = !isLiked;
        String url = Config.ROOT_URL + "/qt/api/like/";

        JSONObject object = new JSONObject();
        try {
            object.put("url", imageUrl);
            object.put("like", isLiked);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("LikePost", object.toString());

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("LikedResponse", response.toString());

                        imageDetailsLayout.setVisibility(View.VISIBLE);
                        loading.setVisibility(View.GONE);

                        if (isLiked) {
                            likeButton.setImageResource(R.mipmap.ic_favorite);
                            imageLikes++;
                            if (imageLikes == 1){
                                numberOfLikes.setText(imageLikes + " Like");
                            }else {
                                numberOfLikes.setText(imageLikes + " Likes");
                            }

                        } else {
                            likeButton.setImageResource(R.mipmap.ic_dislike);
                            imageLikes--;
                            if (imageLikes == 1){
                                numberOfLikes.setText(imageLikes + " Like");
                            }else {
                                numberOfLikes.setText(imageLikes + " Likes");
                            }
                        }

                        if (QCGridImagesFragment.searchedImagesList != null && QCGridImagesFragment.searchedImagesList.size() > 0){
                            for (int i = 0; i < QCGridImagesFragment.searchedImagesList.size(); i++) {
                                if (QCGridImagesFragment.searchedImagesList.get(i).getImagesUrl().equalsIgnoreCase(imageUrl)) {
                                    QCGridImagesFragment.searchedImagesList.get(i).setImageLikes(imageLikes);
                                    QCGridImagesFragment.searchedImagesList.get(i).setLiked(isLiked);
                                }
                            }
                        }
                        likeButton.setOnClickListener(QCImageDetailsFragment.this);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error != null) {
                    imageDetailsLayout.setVisibility(View.VISIBLE);
                    loading.setVisibility(View.GONE);
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

    private void shareApp() {
        if (imageBitmap != null) {
            String path = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), shareImageBitmap, "image.jpeg", null);
            tempImageUri = Uri.parse(path);
            Intent share = new Intent(android.content.Intent.ACTION_SEND);
            share.setType("image/*");
            share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            share.putExtra(Intent.EXTRA_STREAM, tempImageUri);
            share.putExtra(Intent.EXTRA_TEXT, "INSTALL NOW - " + "https://play.google.com/store/apps/details?id=com.app.mobi.quicktabledemo&hl=en");

            startActivity(Intent.createChooser(share, "Share Image!"));
        }
    }

//    private void showFullImage(){
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        String path = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), imageBitmap, null, null);
//        tempImageUri = Uri.parse(path);
//        intent.setDataAndType(tempImageUri, "image/*");
//        startActivity(intent);
//    }

    private void showFullImage(){
        QCImagePreviewFragment qcImagePreviewFragment = new QCImagePreviewFragment();
//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
//        byte[] bytes = stream.toByteArray();
//        Bundle bundle = new Bundle();
//        bundle.putByteArray("PreviewImage", bytes);
        Bundle bundle = new Bundle();
        bundle.putString("PreviewImage", imageUrl);
        qcImagePreviewFragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.QCImagesFrameLayout, qcImagePreviewFragment).addToBackStack(null).commit();
    }

    private void saveImage(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Save your image");
        builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), imageBitmap, imageUrl, null);
                Toast.makeText(getActivity(), "Image saved successfully", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("no", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
