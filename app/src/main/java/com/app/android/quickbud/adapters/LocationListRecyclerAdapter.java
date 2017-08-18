package com.app.android.quickbud.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.android.quickbud.utils.Config;
import com.app.android.quickbud.utils.Globals;
import com.bumptech.glide.Glide;
import com.app.android.quickbud.R;
import com.app.android.quickbud.modelClasses.LocationListModel;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import java.util.ArrayList;

/**
 * Created by mobi11 on 1/10/15.
 */
public class LocationListRecyclerAdapter extends
        RecyclerView.Adapter<LocationListRecyclerAdapter.RecyclerViewHolder> {

    Context context;
    ArrayList<LocationListModel> locationListModels;

    public LocationListRecyclerAdapter(Context context, ArrayList<LocationListModel> locationListModels) {
        this.context = context;
        this.locationListModels = locationListModels;
    }

    @Override
    public LocationListRecyclerAdapter.RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.location_singlerow,parent,false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder locationListViewHolder, int position) {
        Glide.with(context).load(Config.QUICK_CHAT_IMAGE + locationListModels.get(position).getRestaurantImage())
                .asBitmap()
                .placeholder(R.mipmap.restaurant_defaulticon)
                .into(new BitmapImageViewTarget(locationListViewHolder.restImage) {
                    @Override
                    protected void setResource(Bitmap resource) {
//                            System.out.println("resource = " + resource.getHeight() + "\t" + resource.getWidth());
                        super.setResource(resource);
                    }
                });
//        }else {
//            Glide.with(context).load(locationListModels.get(position).getRestaurantImage())
//                    .placeholder(R.mipmap.restaurant_defaulticon)
//                    .into(locationListViewHolder.restImage);
//        }

        if (locationListModels.get(position).getRestRating() == 5.0){
            locationListViewHolder.ratingImage.setImageResource(R.mipmap.rating_5_star);
        }else {
            locationListViewHolder.ratingImage.setImageResource(R.mipmap.rating_4_star);
        }
//        Glide.with(context).load(locationListModels.get(position).getRatingImage())
//                .placeholder(R.mipmap.rating_bg)
//                .into(locationListViewHolder.ratingImage);



        locationListViewHolder.restName.setText(locationListModels.get(position).getRestaurantName());
        locationListViewHolder.restAddress.setText(locationListModels.get(position).getRestaurantAddress() + ", " + locationListModels.get(position).getRestaurantCity());
//        locationListViewHolder.restPhone.setText(locationListModels.get(position).getRestaurantPhone());
        String distance;
        if (!Config.APP_NAME.equalsIgnoreCase("QuickTable")){
            distance = String.format("%.2fmi", locationListModels.get(position).getRestaurantDistance());
        }else {
            distance = String.format("%.2fmi", locationListModels.get(position).getRestaurantDistance()/*/1609.344*/);
        }
        locationListViewHolder.restDistance.setText("Dist-"+ distance);
        String openTiming = locationListModels.get(position).getRestaurantOpenTiming();
        String closeTiming = locationListModels.get(position).getRestaurantCloseTiming();

//        if (locationListModels.get(position).getRestaurantChatAvailable() != null){
//            if (locationListModels.get(position).getRestaurantChatAvailable().equals("0")){
//                locationListViewHolder.chatPersons.setVisibility(View.GONE);
////            locationListViewHolder.chatPersons.setBackgroundResource(R.mipmap.quick_chat_logo);
//            }else {
//                locationListViewHolder.chatPersons.setVisibility(View.VISIBLE);
////            locationListViewHolder.chatPersons.setText(locationListModels.get(position).getRestaurantChatAvailable());
////            locationListViewHolder.chatPersons.setBackgroundResource(R.mipmap.quick_chat_green_logo);
////            locationListViewHolder.chatPersons.setTextColor(Color.parseColor("#ffffff"));
//            }
//        }

        StringBuilder stringBuilder = new StringBuilder();
        if(openTiming != null){
            String[] openTime = openTiming.split(":");
            String[] closeTime = closeTiming.split(":");
            if(openTiming.compareTo("12:00:00")<0){
                stringBuilder.append(openTime[0] + ":" + openTime[1] + "am");
            }else if(openTiming.compareTo("12:00:00") == 0){
                stringBuilder.append(openTime[0] + ":" + openTime[1] + "am");
            }else {
                stringBuilder.append(String.valueOf(24 - Integer.parseInt(openTime[0])) + ":" + openTime[1] + "pm");
            }
            if(closeTiming.compareTo("12:00:00")<0){
                stringBuilder.append(" to " + closeTime[0] + ":" + closeTime[1] + "am");
            }else if(closeTiming.compareTo("12:00:00") == 0){
                stringBuilder.append(" to " + closeTime[0] + ":" + closeTime[1] + "pm");
            }else {
                stringBuilder.append(" to " + String.valueOf(Integer.parseInt(closeTime[0]) - 12) + ":" + closeTime[1] + "pm");
            }
        }
        locationListViewHolder.restTiming.setText("Hours:" + stringBuilder.toString());

//        if(locationListModels.get(position).getRestaurantEWT() != null && locationListModels.get(position).getRestaurantEWT().length() > 0){
//            String ewt = locationListModels.get(position).getRestaurantEWT();
//            String[] timeArray = ewt.split(":");
//
//            if(timeArray[0].equals("00") && timeArray[1].equals("00")){
//                locationListViewHolder.restEWT.setText("Wait Time-"+"0"+" mins");
//            }else if(!timeArray[1].equals("00")){
//                locationListViewHolder.restEWT.setText("Wait Time-"+timeArray[1]+" mins");
//            }else {
//                locationListViewHolder.restEWT.setText("Wait Time-"+timeArray[0]+"hr "+timeArray[1]+"mins");
//            }
//        }else {
//            locationListViewHolder.restEWT.setText("Wait Time-N/A");
//        }

        locationListViewHolder.restName.setTypeface(Globals.myraidProBold);
        locationListViewHolder.restAddress.setTypeface(Globals.myraidProRegular);
        locationListViewHolder.restDistance.setTypeface(Globals.myraidProRegular);
//        locationListViewHolder.restPhone.setTypeface(Globals.myraidProRegular);
        locationListViewHolder.restDistance.setTypeface(Globals.myraidProRegular);
        locationListViewHolder.restEWT.setTypeface(Globals.myraidProRegular);
        locationListViewHolder.restTiming.setTypeface(Globals.myraidProRegular);
//        if (position <= 1) {
//            locationListViewHolder.linkedRest.setVisibility(View.VISIBLE);
//            locationListViewHolder.restEWT.setVisibility(View.VISIBLE);
//        }

//        locationListViewHolder.restAddress.setVisibility(View.GONE);
//        locationListViewHolder.restEWT.setVisibility(View.VISIBLE);
        if(locationListModels.get(position).getRestaurantDistance() != null){
            locationListViewHolder.restDistance.setVisibility(View.VISIBLE);
        }else {
            locationListViewHolder.restDistance.setVisibility(View.GONE);
        }
        if(locationListModels.get(position).getRestaurantEWT() != null){
//            locationListViewHolder.restPhone.setVisibility(View.VISIBLE);
            locationListViewHolder.linkedRest.setVisibility(View.GONE);
            locationListViewHolder.restTiming.setVisibility(View.GONE);
        }else {
//            locationListViewHolder.restPhone.setVisibility(View.GONE);
            locationListViewHolder.linkedRest.setVisibility(View.GONE);
            locationListViewHolder.restTiming.setVisibility(View.GONE);
        }

        if (Config.APP_NAME.equalsIgnoreCase("QuickTable")){

        }else {
            locationListViewHolder.linkedRest.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return locationListModels.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView restName,restAddress,restPhone,restDistance,restEWT,restTiming;
        ImageView restImage,linkedRest, ratingImage;

        public RecyclerViewHolder(View view) {
            super(view);
            restName = (TextView) view.findViewById(R.id.location_name);
            restAddress = (TextView) view.findViewById(R.id.location_address);
//            restPhone = (TextView) view.findViewById(R.id.location_phone);
            restDistance = (TextView) view.findViewById(R.id.location_distance);
            restEWT = (TextView) view.findViewById(R.id.location_ewt);
            restImage = (ImageView) view.findViewById(R.id.location_image);
            linkedRest = (ImageView) view.findViewById(R.id.linked_rest);
//            chatPersons = (ImageView) view.findViewById(R.id.chat_number);
            restTiming = (TextView) view.findViewById(R.id.rest_timing);
            ratingImage = (ImageView) view.findViewById(R.id.location_rating);
        }
    }
}
