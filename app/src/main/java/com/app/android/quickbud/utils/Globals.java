package com.app.android.quickbud.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.app.android.quickbud.R;
import com.app.android.quickbud.interfaces.UserActionInterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by mobi11 on 18/9/15.
 */
public class Globals {

    public static boolean isTakeAway = false;

    public static Typeface robotoRegular, robotoBold, myraidProBold, myraidProRegular, myraidProSemiBold, blackJack,
                    quickChatMsg, quickChatUserName;

//    public static final String YELP_CONSUMER_KEY = "8lQczDuy3Pcop3TX628GPA";
//    public static final String YELP_CONSUMER_SECRET = "Ux47KL-chw5GYBpfsqtboZcHMMU";
//    public static final String YELP_TOKEN = "rHmqwDSUIGO8FVGKrLF-DrUWuEBksk9I";
//    public static final String YELP_TOKEN_SECRET = "LZP1ck7o0cO26BqK-M09Gn86Kog";

    private static AlertDialog alertDialog;

    public static void setCustomTypeface(Context context) {
        blackJack = Typeface.createFromAsset(context.getAssets(), "black_jack.ttf");
        quickChatMsg = Typeface.createFromAsset(context.getAssets(), "HelveticaNeue-Regular.ttf");
        quickChatUserName = Typeface.createFromAsset(context.getAssets(), "HelveticaNeueBold.ttf");
//        robotoRegular = Typeface.createFromAsset(context.getAssets(), "Roboto-Regular.ttf");
//        robotoBold = Typeface.createFromAsset(context.getAssets(), "Roboto-Bold.ttf");
//        myraidProRegular = Typeface.createFromAsset(context.getAssets(), "MYRIADPRO-REGULAR.OTF");
//        myraidProBold = Typeface.createFromAsset(context.getAssets(), "MYRIADPRO-BOLD.OTF");
//        myraidProSemiBold = Typeface.createFromAsset(context.getAssets(), "MYRIADPRO-SEMIBOLD.OTF");
    }

    public static Bitmap getBitmapFromURL(String src) {
        Bitmap bmImg;
        URL imageURL = null;

        try {
            imageURL = new URL(src);
            HttpURLConnection conn = (HttpURLConnection) imageURL
                    .openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();

            // BitmapFactory.Options options = new BitmapFactory.Options();
            // options.inSampleSize = 8;

            bmImg = BitmapFactory.decodeStream(is, null, null);
            return bmImg;
        } catch (Exception e) {
            Log.d("Error", e.toString());
            return null;
        }
    }

    public static String getNearByRestaurant(String url) {
        URL connectURL;
        BufferedReader bufferedReader;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            connectURL = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) connectURL.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            InputStream inputStream = connection.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String read = bufferedReader.readLine();

            while (read != null) {
                stringBuilder.append(read);
                Log.i("data", read);
                read = bufferedReader.readLine();
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    public static void showMissingPermissionDialog(final Context mContext,
                                                   final Fragment mFragment,
                                                   final String title,
                                                   final String message,
                                                   final Integer actionType) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
        dialogBuilder.setTitle(title);
        dialogBuilder.setMessage(message);
        dialogBuilder.setCancelable(false);
        dialogBuilder.setPositiveButton(mContext.getString(R.string.open_settings),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        alertDialog = null;
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + mContext.getPackageName()));
                        mContext.startActivity(intent);
                    }
                });
        dialogBuilder.setNegativeButton(mContext.getString(R.string.alert_cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        alertDialog = null;
                        UserActionInterface listener;
                        if (actionType != null) {
                            if (mFragment != null) {
                                listener = (UserActionInterface) mFragment;
                            } else {
                                listener = (UserActionInterface) mContext;
                            }
                            listener.onDialogConfirmed(actionType);
                        }
                    }
                });

        if (alertDialog == null) {
            alertDialog = dialogBuilder.create();
            alertDialog.show();
        }
    }
}
