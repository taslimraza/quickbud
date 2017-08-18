package com.app.android.quickbud.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.app.android.quickbud.R;
import com.app.android.quickbud.utils.Config;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

public class QCImagePreviewFragment extends Fragment {

    private ProgressBar progressBar;
    private ImageView previewImage;

    public QCImagePreviewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layoutView = inflater.inflate(R.layout.fragment_qcimage_preview, container, false);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Image Preview");
        progressBar = (ProgressBar) layoutView.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        previewImage = (ImageView) layoutView.findViewById(R.id.image_preview);
        ImageView homeBtn = (ImageView) getActivity().findViewById(R.id.menu_button);
        homeBtn.setVisibility(View.GONE);
        Glide.with(getActivity())
//                .load("https://s3-us-west-2.amazonaws.com/stagingquicktable/chats/" + getArguments().getString("PreviewImage"))
                .load(Config.QUICK_CHAT_IMAGES + getArguments().getString("PreviewImage"))
                .asBitmap()
                .listener(new RequestListener<String, Bitmap>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        progressBar.setVisibility(View.INVISIBLE);
                        return false;
                    }
                })
                .into(previewImage);
//        byte[] bytes = getArguments().getByteArray("PreviewImage");
//        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//        previewImage.setImageBitmap(bitmap);
        return layoutView;
    }
}
