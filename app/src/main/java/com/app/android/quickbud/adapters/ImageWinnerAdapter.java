package com.app.android.quickbud.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.android.quickbud.R;
import com.app.android.quickbud.activities.ChatImagesActivity;
import com.app.android.quickbud.fragments.QCImagePreviewFragment;
import com.app.android.quickbud.modelClasses.ImageWinnerModel;
import com.app.android.quickbud.utils.Config;
import com.bumptech.glide.Glide;
//import com.facebook.drawee.backends.pipeline.Fresco;
//import com.facebook.drawee.interfaces.DraweeController;
//import com.facebook.drawee.view.SimpleDraweeView;
//import com.facebook.imagepipeline.request.ImageRequest;
//import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.util.ArrayList;

public class ImageWinnerAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<ImageWinnerModel> imageWinnerModels;

    public ImageWinnerAdapter(Context context, ArrayList<ImageWinnerModel> imageWinnerModels) {
        this.context = context;
        this.imageWinnerModels = imageWinnerModels;
    }

    @Override
    public int getCount() {
        return imageWinnerModels.size();
    }

    @Override
    public Object getItem(int i) {
        return imageWinnerModels.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.list_view_image_winner, viewGroup, false);
        }
        ImageView image = (ImageView) view.findViewById(R.id.chat_image);
//        SimpleDraweeView image = (SimpleDraweeView) view.findViewById(R.id.chat_image);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QCImagePreviewFragment qcImagePreviewFragment = new QCImagePreviewFragment();
                Bundle bundle = new Bundle();
                bundle.putString("PreviewImage", imageWinnerModels.get(i).getImagesUrl());
                qcImagePreviewFragment.setArguments(bundle);
                FragmentTransaction fragmentTransaction = ((ChatImagesActivity)context).getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.QCImagesFrameLayout, qcImagePreviewFragment).addToBackStack(null).commit();
            }
        });
        TextView userName = (TextView) view.findViewById(R.id.user_name);
        TextView numberOfLikes = (TextView) view.findViewById(R.id.likes);
        TextView location = (TextView) view.findViewById(R.id.location);

        LinearLayout winnerLayout = (LinearLayout) view.findViewById(R.id.layout_winner);
        winnerLayout.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
        TextView winnerText = (TextView) view.findViewById(R.id.text_winner);
        if (i == 0) {
            winnerText.setText("#1");
            winnerText.setVisibility(View.VISIBLE);
        } else if (i == 1) {
            winnerText.setText("#2");
            winnerText.setVisibility(View.VISIBLE);
        } else if (i == 2) {
            winnerText.setText("#3");
            winnerText.setVisibility(View.VISIBLE);
        } else if (i == 3) {
            winnerText.setText("#4");
            winnerText.setVisibility(View.VISIBLE);
        } else if (i == 4) {
            winnerText.setText("#5");
            winnerText.setVisibility(View.VISIBLE);
        } else {
            winnerLayout.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
            winnerText.setVisibility(View.GONE);
        }

        ImageWinnerModel winnerModel = imageWinnerModels.get(i);

//        Uri uri = Uri.parse("https://s3-us-west-2.amazonaws.com/stagingquicktable/chats/" + winnerModel.getImagesUrl());
//        Uri uri = Uri.parse(Config.QUICK_CHAT_IMAGES + winnerModel.getImagesUrl());
//        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
//                .setProgressiveRenderingEnabled(true)
//                .build();
//        DraweeController controller = Fresco.newDraweeControllerBuilder()
//                .setImageRequest(request)
//                .setOldController(image.getController())
//                .build();
//        image.setController(controller);

        Glide.with(context).load(Config.QUICK_CHAT_IMAGES + winnerModel.getImagesUrl())
                .into(image);
        userName.setText(winnerModel.getPatronName());
        if (winnerModel.getImageLikes() == 1){
            numberOfLikes.setText(winnerModel.getImageLikes() + " Like");
        }else {
            numberOfLikes.setText(winnerModel.getImageLikes() + " Likes");
        }

        location.setText(winnerModel.getRestaurantName() + "\n" + winnerModel.getRestaurantCity() + ", " + winnerModel.getRestaurantState());

        return view;
    }
}
