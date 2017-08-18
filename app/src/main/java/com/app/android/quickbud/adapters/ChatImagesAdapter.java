package com.app.android.quickbud.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
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
import com.app.android.quickbud.modelClasses.ImagesModel;
import com.app.android.quickbud.network.VolleySingleton;
import com.app.android.quickbud.utils.Config;
import com.bumptech.glide.Glide;
//import com.facebook.drawee.backends.pipeline.Fresco;
//import com.facebook.drawee.interfaces.DraweeController;
//import com.facebook.drawee.view.SimpleDraweeView;
//import com.facebook.imagepipeline.request.ImageRequest;
//import com.facebook.imagepipeline.request.ImageRequestBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mobi11 on 5/7/16.
 */
public class ChatImagesAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<ImagesModel> imagesLink;
    private ImageViewHolder imageViewHolder = null;
    private ImageView progressBar;
    private GridView gridView;

    public ChatImagesAdapter(Context context, ArrayList<ImagesModel> imagesLink, GridView  gridView, ImageView progressBar) {
        this.context = context;
        this.imagesLink = imagesLink;
        this.gridView = gridView;
        this.progressBar = progressBar;
    }

    @Override
    public int getCount() {
        return imagesLink.size();
    }

    @Override
    public Object getItem(int position) {
        return imagesLink.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.single_chat_image, parent, false);
            imageViewHolder = new ImageViewHolder(convertView);
            convertView.setTag(imageViewHolder);
        } else {
            imageViewHolder = (ImageViewHolder) convertView.getTag();
        }

        ImagesModel imagesModel = imagesLink.get(position);

//            Uri uri = Uri.parse("https://s3-us-west-2.amazonaws.com/stagingquicktable/chats/thumbnails/" + imagesModel.getImagesUrl());
//        Uri uri = Uri.parse(Config.QUICK_CHAT_THUMBNAILS_IMAGES + imagesModel.getImagesUrl());
//        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
//                .setProgressiveRenderingEnabled(true)
//                .build();
//        DraweeController controller = Fresco.newDraweeControllerBuilder()
//                .setImageRequest(request)
//                .setOldController(imageViewHolder.image.getController())
//                .build();
////            GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(context.getResources());
////            GenericDraweeHierarchy hierarchy = builder.setProgressBarImage(new ProgressBarDrawable()).build();
//        imageViewHolder.image.setController(controller);
//            draweeView.setHierarchy(hierarchy);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.image);
            Glide.with(context).load(Config.QUICK_CHAT_THUMBNAILS_IMAGES + imagesModel.getImagesUrl()).into(imageView);

        if (imagesModel.isLiked()) {
            imageViewHolder.likeImage.setImageResource(R.mipmap.ic_like);
        } else {
            imageViewHolder.likeImage.setImageResource(R.mipmap.ic_unlike);
        }

        imageViewHolder.numberOfLikes.setText("" + imagesModel.getImageLikes());

        if (imagesModel.getImageLikes() == 1) {
            imageViewHolder.tvLikes.setText(" Like");
        } else {
            imageViewHolder.tvLikes.setText(" Likes");
        }


        imageViewHolder.likeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postLike(imagesLink.get(position).isLiked(), imagesLink.get(position).getImagesUrl(), position);
            }
        });
//        ImageView imageView = (ImageView) convertView.findViewById(R.id.image);
//        Glide.with(context).load(imagesLink[position]).into(imageView);

        return convertView;

    }

    class ImageViewHolder {
        ImageView image;
        ImageView likeImage;
        TextView numberOfLikes, tvLikes;

        public ImageViewHolder(View view) {
            image = (ImageView) view.findViewById(R.id.image);
            likeImage = (ImageView) view.findViewById(R.id.like_image);
            numberOfLikes = (TextView) view.findViewById(R.id.like_text);
            tvLikes = (TextView) view.findViewById(R.id.tv_like);
        }
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

                        int imageLikes = imagesLink.get(position).getImageLikes();

                        if (finalIsLiked) {
                            imagesLink.get(position).setLiked(true);
                            imageLikes++;
                            imagesLink.get(position).setImageLikes(imageLikes);

                        } else {
                            imagesLink.get(position).setLiked(false);
                            imageLikes--;
                            imagesLink.get(position).setImageLikes(imageLikes);
                        }
                        notifyDataSetChanged();

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
}
