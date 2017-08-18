package com.app.android.quickbud.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.app.android.quickbud.R;
import com.app.android.quickbud.modelClasses.LocationListModel;
import com.app.android.quickbud.modelClasses.RestaurantServicesModel;
import com.app.android.quickbud.utils.SpecificMenuSingleton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by mobi11 on 25/9/15.
 */
public class RestaurantServicesAdapter extends BaseAdapter {

    private Context mContext;
    private List<RestaurantServicesModel> restaurantServicesModels;
    private LocationListModel restDetails;
    private String currentTime = null, openTime = null, closeTime = null;

    public RestaurantServicesAdapter(Context context, List<RestaurantServicesModel> restaurantServicesModels) {
        this.mContext = context;
        this.restaurantServicesModels = restaurantServicesModels;
        restDetails = SpecificMenuSingleton.getInstance().getClickedRestaurant();
        setTimeZone();
    }


    @Override
    public int getCount() {
        if (restaurantServicesModels == null || restaurantServicesModels.size()>0){
            return restaurantServicesModels.size();
        }else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        return restaurantServicesModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.homescreen_single_listview, parent, false);
//        TextView title = (TextView) view.findViewById(R.id.single_title);
//        TextView subTitle = (TextView) view.findViewById(R.id.single_description);
        ImageView imageView= (ImageView) view.findViewById(R.id.single_image);

        try {

            RestaurantServicesModel model = restaurantServicesModels.get(position);
//        title.setText(titleList[position]);
//        subTitle.setText(subTitleList[position]);
            imageView.setImageResource(model.getImage());

            if (model.isSelected()){
                imageView.setBackgroundResource(R.drawable.highlight);
            }else {
                imageView.setBackgroundResource(0);
            }

        }catch (IndexOutOfBoundsException e){
            e.printStackTrace();
        }

//        if (position == 0) {
//            if (!restDetails.isQtSupported()) {
//                // Gray out
//                ColorMatrix matrix = new ColorMatrix();
//                matrix.setSaturation(0);  //0 means grayscale
//                ColorMatrixColorFilter cf = new ColorMatrixColorFilter(matrix);
//                imageView.setColorFilter(cf);
//                title.setTextColor(Color.GRAY);
//                subTitle.setTextSize(Color.GRAY);
//            }
//        }

//        if (position == 1) {
//            if (!(restDetails.isQtSupported() || restDetails.isDealActive())) {
//                // Gray out
//                ColorMatrix matrix = new ColorMatrix();
//                matrix.setSaturation(0);  //0 means grayscale
//                ColorMatrixColorFilter cf = new ColorMatrixColorFilter(matrix);
//                imageView.setColorFilter(cf);
//                title.setTextColor(Color.GRAY);
//                subTitle.setTextSize(Color.GRAY);
//            }
//        }

//        if (position == 0) {
//         if (!(restDetails.isQtSupported())){
////             view.setBackgroundColor(Color.parseColor("#c0c0c0"));
//             ColorMatrix matrix = new ColorMatrix();
//             matrix.setSaturation(0);  //0 means grayscale
//             ColorMatrixColorFilter cf = new ColorMatrixColorFilter(matrix);
//             imageView.setColorFilter(cf);
////             title.setTextColor(Color.GRAY);
////             subTitle.setTextSize(Color.GRAY);
//         }
//        }

//        if (position == 4) {
//            if (!(restDetails.isCarryOut() && SpecificMenuSingleton.getInstance().getClickedRestaurant().isHostessOnline() &&
//                    (currentTime.compareToIgnoreCase(openTime) >= 0 && currentTime.compareToIgnoreCase(closeTime) <= 0))){
////                view.setBackgroundColor(Color.parseColor("#c0c0c0"));
//                ColorMatrix matrix = new ColorMatrix();
//                matrix.setSaturation(0);  //0 means grayscale
//                ColorMatrixColorFilter cf = new ColorMatrixColorFilter(matrix);
//                imageView.setColorFilter(cf);
//                title.setTextColor(Color.GRAY);
//                subTitle.setTextSize(Color.GRAY);
//            }
//        }

        return view;
    }

    private void setTimeZone() {
        SpecificMenuSingleton menuSingleton = SpecificMenuSingleton.getInstance();
        SimpleDateFormat f = new SimpleDateFormat("HH:mm:ss");
        if (menuSingleton.getClickedRestaurant().isQtSupported()) {
            f.setTimeZone(TimeZone.getTimeZone(menuSingleton.getClickedRestaurant().getTimezone()));
            currentTime = f.format(new Date());
            openTime = menuSingleton.getClickedRestaurant().getRestaurantOpenTiming();
            closeTime = menuSingleton.getClickedRestaurant().getRestaurantCloseTiming();
        }
    }
}
