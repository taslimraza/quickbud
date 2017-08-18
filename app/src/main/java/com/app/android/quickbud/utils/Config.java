package com.app.android.quickbud.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.app.android.quickbud.interfaces.DialogActionInterface;

/**
 * Created by mobi11 on 26/11/15.
 */

public class Config {

    public static String VERSION_NUMBER = "1.5";
    public static String APP_NAME = "QuickTable";

    public static String APP_PACKAGE = "com.app.mobi.quicktabledemo";

    public static int TIMEOUT = 10 * 1000;

    public static int APP_TENANT_ID = 1;

    public static boolean viewMenu = false;

    public static boolean bookingStatus = false;

    public static boolean takeAway = false;

    public static boolean isMenuOptionClicked = false;

    public static String YELP_CONSUMER_KEY = "8lQczDuy3Pcop3TX628GPA";
    public static String YELP_CONSUMER_SECRET = "Ux47KL-chw5GYBpfsqtboZcHMMU";
    public static String YELP_TOKEN = "uzpSDk-22QDoqX-HrjwFDqYRnGwy0K20";
    public static String YELP_TOKEN_SECRET = "QkSStN053RWJiPfzbztNzPG0beg";

//    public static String QUICK_CHAT_URL = "159.203.88.161";

    //for production
//    public static String QUICK_CHAT_URL = "ec2-52-39-155-202.us-west-2.compute.amazonaws.com";
//    public static String QUICK_CHAT_IP_URL = "52.39.155.202";

    //for testing
    public static String QUICK_CHAT_IP_URL = "52.41.8.42";
    public static String QUICK_CHAT_URL = "ec2-52-41-8-42.us-west-2.compute.amazonaws.com";

    //for production
//    public static String QUICK_CHAT_IMAGE = "https://s3-us-west-2.amazonaws.com/quicktableimages/";

    //for testing
    public static String QUICK_CHAT_IMAGE = "https://s3-us-west-2.amazonaws.com/quickbudimages/";

    //for production
//    public static String QUICK_CHAT_IMAGES = "https://s3-us-west-2.amazonaws.com/quickchatimages/";

    //for testing
    public static String QUICK_CHAT_IMAGES = "https://s3-us-west-2.amazonaws.com/budgallery/";

    //for production
//    public static String QUICK_CHAT_THUMBNAILS_IMAGES = "https://s3-us-west-2.amazonaws.com/quickchatimages/thumbnails/";

    //for testing
    public static String QUICK_CHAT_THUMBNAILS_IMAGES = "https://s3-us-west-2.amazonaws.com/budgallery/thumbnails/";


//    public static String QUICK_CHAT_URL = "ec2-52-91-21-6.compute-1.amazonaws.com";

//    public static String ROOT_URL = "http://159.203.88.161";

    //for testing
    public static String ROOT_URL = "http://52.25.3.27";

    //for production - old server
//    public static String ROOT_URL = "http://qtloadbalancer-1162110760.us-west-2.elb.amazonaws.com";

    //for production
//    public static String ROOT_URL = "http://qtloadbalancer-v-1-1-56157812.us-west-2.elb.amazonaws.com";

    public static String CORE_URL = ROOT_URL + "/qb/core/";

    public static String QB_REST_LIST = CORE_URL + "certloc/compact/";

    public static String QT_SUPPORTED_URL = CORE_URL + "certloc/compareplaceId/";

    public static String QT_TENANT_URL = CORE_URL + "certloc/compact/";

    public static String QT_WHITE_LABEL_URL = CORE_URL + "certloc/whiteLabel/";

    public static String QT_REGISTRATION_URL = ROOT_URL + "/qb/api/patron/";

    public static String QT_VISIT_URL = ROOT_URL + "/qb/api/visit/";

    public static String QT_GET_OUT_OF_LINE_URL = ROOT_URL + "/qb/api/visit/get_out_of_line/";

    public static String QT_EWT_REFRESH_URL = ROOT_URL + "/qb/api/visit/position/";

    public static String QT_MENU_URL = ROOT_URL + "/qb/api/menu/tenant_loc/";

    public static String QT_FAVORITE_URL = ROOT_URL + "/qb/api/favorite/";

    public static String QT_POST_CART_URL = ROOT_URL + "/qb/api/cart/";

    public static String QT_OFFERS_URL = ROOT_URL + "/qb/api/pushsent/getPushSent/";

    public static String QT_IMAGE = ROOT_URL + "/qb/api/images/";

    public static String QT_PATRON_UPDATE = ROOT_URL + "/qb/api/patron/";

    public static String QT_IMAGE_UPLOAD = ROOT_URL + "/qb/api/uploadimage";

    public static String QT_CREATE_CHAT_ROOM = ROOT_URL + "/qb/api/createChatRoom/";

    public static String QT_REMOVE_CHAT_ROOM = ROOT_URL + "/qb/api/patron/removeUser/";

    public static String QT_PATRON_ARRIVED = ROOT_URL + "/qb/api/visit/enroute/";

    public static String QT_EVENT_URL = ROOT_URL + "/qb/api/event/";

    public static String FAVORITE_REST_LIST = ROOT_URL + "/qb/api/favloc/";

    public static String QT_MENU = ROOT_URL + "/qb/api/menus/";

    public static String YELP_KEYS_URL = CORE_URL + "yelpkeys";

    public static String APP_VERSION_URL = Config.ROOT_URL + "/qb/core/version/";

    public static String PROMOTIONAL_IMAGES_URL = Config.ROOT_URL + "/qb/api/images/";

    public static String IMAGE_LIKE_URL = Config.ROOT_URL + "/qb/api/like/";

    public static String BUD_NEWS_URL = ROOT_URL + "/qb/api/budnews";

    public static String QB_ADD_TOP_BUDS = ROOT_URL + "/qb/api/topbud/";

    public static String QB_TOP_BUDS_LIST = ROOT_URL + "/qb/api/topbud/";

    public static String QB_TOP_BUD_DETAILS = ROOT_URL + "/qb/api/menuitem/";

    public static String QB_TOP_BUD_DELETE = ROOT_URL + "/qb/api/topbud/delete/";

    public static String QB_SEARCH_URL = CORE_URL + "search/";

    public static String QB_LOCATION_SEND_URL = ROOT_URL + "/qb/api/track/";

    public static String SESSION_TOKEN_ID = "";

    public static void internetSlowError(final Context context) {
        if (context != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Warning");
            builder.setMessage("Your internet seems to be slow, Please try again later");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
//                    ((AppCompatActivity)context).finish();
                }
            });
            builder.setCancelable(false);
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

    public static void serverError(final Context context) {
        if (context != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Alert");
            builder.setMessage("Unable to process your request, Please try again later.");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    ((AppCompatActivity) context).finish();
                }
            });
            builder.setCancelable(false);
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

    public static void internetError(final Context context, final DialogActionInterface listener) {
        if (context != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Warning");
            builder.setMessage("Your internet seems to be slow, Please try again later.");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    Log.d("Instance", "\t" + listener.getClass().getSimpleName());
                    listener.onDialogConfirmed();
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }


}
