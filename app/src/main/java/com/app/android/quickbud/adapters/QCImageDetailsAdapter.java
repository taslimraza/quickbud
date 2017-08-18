package com.app.android.quickbud.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
import com.app.android.quickbud.activities.ChatImagesActivity;
import com.app.android.quickbud.fragments.QCImagePreviewFragment;
import com.app.android.quickbud.modelClasses.ImagesModel;
import com.app.android.quickbud.network.VolleySingleton;
import com.app.android.quickbud.utils.Config;
import com.app.android.quickbud.utils.ProfileRoundView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mobi11 on 5/11/16.
 */
public class QCImageDetailsAdapter extends RecyclerView.Adapter<QCImageDetailsAdapter.RecyclerViewHolder> {

    private Context context;
    private ArrayList<ImagesModel> imagesArrayList;
    private int imageLikes = 0;
    private Bitmap imageBitmap, shareImageBitmap;
    private Uri tempImageUri;
    private int itemPosition;
    private RecyclerView.ViewHolder viewHolder;

    public QCImageDetailsAdapter(Context context, ArrayList<ImagesModel> imagesArrayList) {
        this.context = context;
        this.imagesArrayList = imagesArrayList;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.qc_imgae_details_single, parent, false);

        WindowManager windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        int width = windowManager.getDefaultDisplay().getWidth();
        int height = windowManager.getDefaultDisplay().getHeight();
        view.setLayoutParams(new RecyclerView.LayoutParams(width, RecyclerView.LayoutParams.MATCH_PARENT));

//        view.setMinimumWidth(parent.getMeasuredWidth());
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, int position) {

        itemPosition = position;
        viewHolder = holder;

        final ImagesModel qcImage = imagesArrayList.get(position);

        holder.imageView.setEnabled(false);
//        Bundle bundle = this.getArguments();
//        imageUrl = bundle.getString("Selected Image");
        holder.loading.setVisibility(View.VISIBLE);
        Glide.with(context)/*.load("https://s3-us-west-2.amazonaws.com/stagingquicktable/chats/" + imageUrl)*/
                .load(Config.QUICK_CHAT_IMAGES + qcImage.getImagesUrl())
                .asBitmap()
                .listener(new RequestListener<String, Bitmap>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        holder.loading.setVisibility(View.GONE);
                        holder.imageView.setEnabled(true);
                        imageBitmap = resource;
                        Bitmap.Config config = imageBitmap.getConfig();
                        int height = imageBitmap.getHeight();
                        int width = imageBitmap.getWidth();

                        shareImageBitmap = Bitmap.createBitmap(width, height, config);

                        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.logo_splash);
                        Canvas canvas = new Canvas(shareImageBitmap);
                        canvas.drawBitmap(imageBitmap, 0, 0, null);

                        Paint paint = new Paint();
                        paint.setColor(context.getResources().getColor(R.color.primary_color));
                        paint.setTextSize(40);

//                        canvas.drawBitmap(bitmap, width-310, height-60, null);
//                        canvas.drawText("QuickTable", width - 250, height - 10, paint);
                        canvas.drawBitmap(bitmap, width - 320, height-70, null);
                        canvas.drawText("QuickBud", width - 250, height - 20, paint);

                        return false;
                    }
                })
                .into(new BitmapImageViewTarget(holder.imageView) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        int width = resource.getWidth();
                        int height = resource.getHeight();
                        Bitmap bitmap = null;
                        if (width > height) {
                            if (width - height > 100){
                                bitmap = Bitmap.createBitmap(resource, 100, 0, height, height);
                            }else {
                                bitmap = Bitmap.createBitmap(resource, width - height, 0, height, height);
                            }
                        } else if (width < height) {
                            if((height - width) > 100){
                                bitmap = Bitmap.createBitmap(resource, 0, 100, width, width);
                            }else {
                                bitmap = Bitmap.createBitmap(resource, 0, height-width, width, width);
                            }
                        } else {
                            bitmap = Bitmap.createBitmap(resource, 0, 0, width, height);
                        }
                        super.setResource(bitmap);
                    }
                });
        imageLikes = qcImage.getImageLikes();
        if (imageLikes == 1){
            holder.numberOfLikes.setText(imageLikes + " Like");
        }else {
            holder.numberOfLikes.setText(imageLikes + " Likes");
        }
        if (qcImage.getRestaurantCity() != null && !qcImage.getRestaurantCity().isEmpty()){
            holder.locationDetails.setText("");
//            holder.locationDetails.setText(qcImage.getRestaurantName() + "\n" + qcImage.getRestaurantCity() + ", " + qcImage.getRestaurantState());
            holder.restaurantName.setText(qcImage.getRestaurantName());
            holder.restaurantAddress.setText(qcImage.getRestaurantCity() + ", " + qcImage.getRestaurantState());
        }else {
            holder.locationDetails.setText("");
            holder.restaurantName.setText("");
            holder.restaurantAddress.setText("");
        }
        boolean isLiked = qcImage.isLiked();
        holder.userName.setText(qcImage.getPatronName());

        if (isLiked) {
            holder.likeButton.setImageResource(R.mipmap.ic_favorite);
        } else {
            holder.likeButton.setImageResource(R.mipmap.ic_dislike);
        }

        if (imagesArrayList.get(position).getPatronImage() != null) {
            Glide.with(context)/*.load("https://s3-us-west-2.amazonaws.com/stagingquicktable/profile/" + bundle.getString("patronImage"))*/
                    .load(Config.QUICK_CHAT_IMAGE + qcImage.getPatronImage())
                    .asBitmap()
                    .placeholder(R.mipmap.default_profile_pic)
                    .into(holder.userProfileImage);
        }


        holder.imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                saveImage(qcImage.getImagesUrl());
                return true;
            }
        });

        holder.likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.likeButton.setEnabled(false);
                postLike(holder.imageDetailsLayout, holder.loading, holder.likeButton, holder.numberOfLikes, qcImage);
            }
        });

    }

    @Override
    public int getItemCount() {
        return imagesArrayList.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView likeButton, shareButton, imageView;
        private TextView numberOfLikes, locationDetails, userName, imageTime, restaurantName, restaurantAddress;
        private ProfileRoundView userProfileImage;
        private ProgressBar loading;
        private LinearLayout imageDetailsLayout;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.image_selected);
            likeButton = (ImageView) itemView.findViewById(R.id.like_button);
            shareButton = (ImageView) itemView.findViewById(R.id.share_button);
            numberOfLikes = (TextView) itemView.findViewById(R.id.likes_number);
            locationDetails = (TextView) itemView.findViewById(R.id.restaurant_details);
            userProfileImage = (ProfileRoundView) itemView.findViewById(R.id.user_profile);
            userName = (TextView) itemView.findViewById(R.id.user_name);
            loading = (ProgressBar) itemView.findViewById(R.id.loading);
            imageDetailsLayout = (LinearLayout) itemView.findViewById(R.id.image_details_layout);
            imageTime = (TextView) itemView.findViewById(R.id.image_time);
            restaurantName = (TextView) itemView.findViewById(R.id.restaurant_name);
            restaurantAddress = (TextView) itemView.findViewById(R.id.restaurant_address);

            shareButton.setOnClickListener(this);
            imageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                case R.id.share_button:
                    shareApp();
                    break;

                case R.id.image_selected:
                    showFullImage(imagesArrayList.get(itemPosition).getImagesUrl());
                    break;
            }
        }
    }

    private void postLike(final LinearLayout imageDetailsLayout, final ProgressBar loading,
            final ImageView likeButton,final TextView numberOfLikes, final ImagesModel imagesModel) {
        imageDetailsLayout.setVisibility(View.VISIBLE);
        loading.setVisibility(View.VISIBLE);
        boolean isLiked = imagesModel.isLiked();
        isLiked = !isLiked;
        String url = Config.IMAGE_LIKE_URL;

        JSONObject object = new JSONObject();
        try {
            object.put("url", imagesModel.getImagesUrl());
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

                        imageDetailsLayout.setVisibility(View.VISIBLE);
                        loading.setVisibility(View.GONE);

                        int imageLikes = imagesModel.getImageLikes();

                        if (finalIsLiked) {
                            imagesModel.setLiked(true);
                            imageLikes++;
                            imagesModel.setImageLikes(imageLikes);

                        } else {
                            imagesModel.setLiked(false);
                            imageLikes--;
                            imagesModel.setImageLikes(imageLikes);
                        }

//                        if (QCGridImagesFragment.searchedImagesList != null && QCGridImagesFragment.searchedImagesList.size() > 0){
//                            for (int i = 0; i < QCGridImagesFragment.searchedImagesList.size(); i++) {
//                                if (QCGridImagesFragment.searchedImagesList.get(i).getImagesUrl().equalsIgnoreCase(imagesModel.getImagesUrl())) {
//                                    QCGridImagesFragment.searchedImagesList.get(i).setImageLikes(imageLikes);
//                                    QCGridImagesFragment.searchedImagesList.get(i).setLiked(finalIsLiked);
//                                }
//                            }
//                        }
                        notifyDataSetChanged();
                        likeButton.setEnabled(true);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error != null) {
                    imageDetailsLayout.setVisibility(View.VISIBLE);
                    loading.setVisibility(View.GONE);
                    if (error instanceof TimeoutError) {
                        Config.internetSlowError(context);
                    } else if (error instanceof NoConnectionError) {
                        Config.internetSlowError(context);
                    } else if (error instanceof ServerError) {
                        Config.serverError(context);
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

        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    private void shareApp() {
        if (imageBitmap != null) {
            String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), shareImageBitmap, "image.jpeg", null);
            tempImageUri = Uri.parse(path);
            Intent share = new Intent(android.content.Intent.ACTION_SEND);
            share.setType("image/*");
            share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            share.putExtra(Intent.EXTRA_STREAM, tempImageUri);
//            share.putExtra(Intent.EXTRA_TEXT, "INSTALL NOW - " + "https://play.google.com/store/apps/details?id=com.app.mobi.quicktabledemo&hl=en");

            context.startActivity(Intent.createChooser(share, "Share Image!"));
        }
    }

//    private void showFullImage(){
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        String path = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), imageBitmap, null, null);
//        tempImageUri = Uri.parse(path);
//        intent.setDataAndType(tempImageUri, "image/*");
//        startActivity(intent);
//    }

    private void showFullImage(String imageUrl){
        QCImagePreviewFragment qcImagePreviewFragment = new QCImagePreviewFragment();
        Bundle bundle = new Bundle();
        bundle.putString("PreviewImage", imageUrl);
        qcImagePreviewFragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = ((ChatImagesActivity)context).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.QCImagesFrameLayout, qcImagePreviewFragment).addToBackStack(null).commit();
    }

    private void saveImage(final String imageUrl){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Save your image");
        builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                MediaStore.Images.Media.insertImage(context.getContentResolver(), imageBitmap, imageUrl, null);
                Toast.makeText(context, "Image saved successfully", Toast.LENGTH_SHORT).show();
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
