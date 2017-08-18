package com.app.android.quickbud.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.android.quickbud.R;
import com.app.android.quickbud.modelClasses.QTOffers;
import com.app.android.quickbud.utils.Config;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import java.util.ArrayList;

/**
 * Created by mobi11 on 31/3/16.
 */
public class OffersAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<QTOffers> offersArrayList;

    public OffersAdapter(Context context, ArrayList<QTOffers> offersArrayList) {
        this.context = context;
        this.offersArrayList = offersArrayList;
    }


    @Override
    public int getCount() {
        return offersArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return offersArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.qt_offer_row, parent, false);
        TextView restName = (TextView) view.findViewById(R.id.restaurant_name);
        TextView restAddress = (TextView) view.findViewById(R.id.restaurant_address);
        ImageView offerImage = (ImageView) view.findViewById(R.id.offer_image);
        TextView offerMessage = (TextView) view.findViewById(R.id.offer_message);
        TextView offerTime = (TextView) view.findViewById(R.id.time);
        ImageView yelpLogo = (ImageView) view.findViewById(R.id.yelp_logo);
        TextView yelpAvailOfferTxt = (TextView) view.findViewById(R.id.avail_offer_txt);

        restName.setText(offersArrayList.get(position).getRestaurantName());
        if (offersArrayList.get(position).getRestaurantAddress() != null && !offersArrayList.get(position).getRestaurantAddress().isEmpty()) {
            restAddress.setText(offersArrayList.get(position).getRestaurantAddress() + ", "
                    + offersArrayList.get(position).getRestaurantCity() + ", "
                    + offersArrayList.get(position).getRestaurantState());
        }else if (offersArrayList.get(position).getRestaurantCity() != null){
            restAddress.setText(offersArrayList.get(position).getRestaurantCity() + ", "
                    + offersArrayList.get(position).getRestaurantState());
        }else {
            restAddress.setText("");
        }
        offerMessage.setText(offersArrayList.get(position).getMsg());

        if (!offersArrayList.get(position).isYelp()){
            if (offersArrayList.get(position).getOfferImage() != null){
                offerImage.setVisibility(View.VISIBLE);
                Glide.with(context).load(Config.QUICK_CHAT_IMAGE + offersArrayList.get(position).getOfferImage())
                        .asBitmap()
                        .placeholder(android.R.drawable.progress_horizontal)
                        .into(new BitmapImageViewTarget(offerImage){
                            @Override
                            protected void setResource(Bitmap resource) {
                                super.setResource(resource);
                            }
                        });
            }else {
                offerImage.setVisibility(View.GONE);
            }
//            yelpLogo.setVisibility(View.GONE);
            yelpLogo.setImageResource(R.mipmap.ic_qb_small);
            yelpAvailOfferTxt.setVisibility(View.GONE);
        }else {
            Log.i("Offer Image", offersArrayList.get(position).getOfferImage());
            Glide.with(context).load(offersArrayList.get(position).getOfferImage())
                    .asBitmap()
                    .placeholder(android.R.drawable.progress_horizontal)
                    .into(new BitmapImageViewTarget(offerImage){
                        @Override
                        protected void setResource(Bitmap resource) {
                            super.setResource(resource);
                        }
                    });
//            yelpLogo.setVisibility(View.VISIBLE);
            yelpLogo.setImageResource(R.mipmap.ic_yelp_logo);
            yelpAvailOfferTxt.setVisibility(View.VISIBLE);
        }
//                    .into(new BitmapImageViewTarget(offerImage){
//                        @Override
//                        protected void setResource(Bitmap resource) {
//
//                            int width = resource.getWidth();
//                            int height = resource.getHeight();
//                            int ratio = height/width;
//                            Log.i("GetFinalImageSize", "height - " + height + "width - " + width);
//                            Bitmap bitmap = null;
//                            if (height > 2048 && width > 2048){
//                                bitmap = Bitmap.createScaledBitmap(resource, 2048, 2048, true);
//                            }else if (width > 2048){
//                                bitmap = Bitmap.createScaledBitmap(resource, 2048, height, true);
//                            }else if (height > 2048){
//                                bitmap = Bitmap.createScaledBitmap(resource, width/ratio, 2048, true);
//                            }else {
//                                bitmap = resource;
//                            }
//
//                            super.setResource(bitmap);
//                        }
//                    });
        offerTime.setText(offersArrayList.get(position).getTime());

        return view;
    }
}