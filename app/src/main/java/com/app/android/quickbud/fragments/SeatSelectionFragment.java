package com.app.android.quickbud.fragments;


import android.app.Dialog;
import android.app.Instrumentation;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
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
import com.app.android.quickbud.activities.OrderConfirmationActivity;
import com.app.android.quickbud.network.VolleySingleton;
import com.app.android.quickbud.utils.CartSingleton;
import com.app.android.quickbud.utils.Config;
import com.app.android.quickbud.utils.ConnectionDetector;
import com.app.android.quickbud.utils.ExpandableHeightGridView;
import com.app.android.quickbud.utils.SpecificMenuSingleton;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class SeatSelectionFragment extends Fragment implements NumberPicker.OnValueChangeListener {
    TextView userName, userLocation, restaurantName, restaurantAddress, tableCount;
    View rootView;
    Toolbar toolbar;
    ExpandableHeightGridView gridView;
    Button bookTable;
    DrawerLayout drawerLayout;
    ListView drawerList;
    ImageView homeBtn, loadingImage, restaurantImage, userImage;
    EditText extraNeed;
    RelativeLayout cartContent;
    LinearLayout seatSelectionLayout;
    boolean isHighChair = false, isBoosterSeat = false, isWheelChair = false, isOthers = false;
    private String seatSelection;
    private Integer memberNumber = 0;
    private ConnectionDetector connectionDetector;
    private Instrumentation instrumentation;
    int softkeyboard_height = 0;
    boolean calculated_keyboard_height;
    private ScrollView scrollView;

    public static SeatSelectionFragment newInstance() {
        SeatSelectionFragment fragment = new SeatSelectionFragment();
        return fragment;
    }

    public SeatSelectionFragment() {
//        getRestaurantLogo();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_seat_selection, container, false);
        rootView = view;
        instrumentation = new Instrumentation();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        final Spinner spinner = (Spinner) getActivity().findViewById(R.id.seat_spinner);
        gridView = (ExpandableHeightGridView) getActivity().findViewById(R.id.special_seat_gridview);
        bookTable = (Button) getActivity().findViewById(R.id.book_table);
        toolbar = (Toolbar) getActivity().findViewById(R.id.app_bar);
//        drawerList = (ListView) getActivity().findViewById(R.id.drawer_list);
//        drawerLayout = (DrawerLayout) getActivity().findViewById(R.id.drawer);
//        userName = (TextView) getActivity().findViewById(R.id.user_name);
        userLocation = (TextView) getActivity().findViewById(R.id.user_location);
        cartContent = (RelativeLayout) getActivity().findViewById(R.id.cart_content);
        extraNeed = (EditText) getActivity().findViewById(R.id.extra_need);
        seatSelectionLayout = (LinearLayout) getActivity().findViewById(R.id.seat_selection_layout);
        loadingImage = (ImageView) getActivity().findViewById(R.id.loading_image);
        restaurantImage = (ImageView) getActivity().findViewById(R.id.rest_image);
        restaurantName = (TextView) getActivity().findViewById(R.id.rest_name);
        restaurantAddress = (TextView) getActivity().findViewById(R.id.rest_address);
        tableCount = (TextView) getActivity().findViewById(R.id.table_count_number);
        LinearLayout pickerSelectionLayout = (LinearLayout) getActivity().findViewById(R.id.seat_spinner_layout);
//        userImage = (ImageView) getActivity().findViewById(R.id.user_image);
        rootView = getActivity().findViewById(R.id.root_layout);
        scrollView = (ScrollView) getActivity().findViewById(R.id.scroll_view);

        toolbar.setTitle("Book Your Table");
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // for android v6.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            bookTable.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.primary_color));
            toolbar.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.primary_color));
        }

        cartContent.setVisibility(View.GONE);
        connectionDetector = new ConnectionDetector(getActivity());
        SpecificMenuSingleton menuSingleton = SpecificMenuSingleton.getInstance();
        Glide.with(getActivity()).load(Config.QUICK_CHAT_IMAGE + menuSingleton.getImageUrl())
                .crossFade(1000).into(restaurantImage);

//        cartContent.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(getActivity(), OrderDetailsActivity.class));
//            }
//        });
        restaurantName.setText(menuSingleton.getRestaurantName());

        ContextCompat.getColor(getActivity(), R.color.primary_color);

        StringBuilder builder = new StringBuilder();
        builder.append(menuSingleton.getClickedRestaurant().getRestaurantAddress());
        builder.append(", " + menuSingleton.getClickedRestaurant().getRestaurantCity());
        builder.append(", " + menuSingleton.getClickedRestaurant().getRestaurantState());
        restaurantAddress.setText(builder);

//        drawerList.setAdapter(new DrawerListAdapter(getActivity()));
        homeBtn = (ImageView) getActivity().findViewById(R.id.menu_button);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
//                drawerLayout.openDrawer(Gravity.LEFT);

                Intent intent = new Intent(getActivity(), MainMenuActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                getActivity().finish();

            }
        });
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.seat_selection, android.R.layout.simple_spinner_item);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner.setAdapter(adapter);
        gridView.setExpanded(true);
        gridView.setAdapter(new CustomAdapter());
        bookTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                if (memberNumber == 0) {
                    showDialog();
                } else {
                    if (connectionDetector.isConnectedToInternet()) {
                        if (CartSingleton.getInstance().getCartItem() != null && CartSingleton.getInstance().getCartItem().size() > 0) {
                            if (CartSingleton.getInstance().getCartItem().get(0).getTenantId() == SpecificMenuSingleton.getInstance().getClickedRestaurant().getTenantID()
                                    && CartSingleton.getInstance().getCartItem().get(0).getLocationId() == SpecificMenuSingleton.getInstance().getClickedRestaurant().getLocationID()) {
                                VolleySingleton.getInstance(getActivity()).postEventRequest("GetInLine", SpecificMenuSingleton.getInstance().getClickedRestaurant());
                                postTableRequest();
                            }else {
                                showRestaurantVaryError();
                            }
                        }else {
                            VolleySingleton.getInstance(getActivity()).postEventRequest("GetInLine", SpecificMenuSingleton.getInstance().getClickedRestaurant());
                            postTableRequest();
                        }
                    } else {
                        connectionDetector.internetError();
                    }
                }
            }
        });

        // ------------selecting special need for dine in-----------------------------
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ImageView image = (ImageView) view.findViewById(R.id.special_need_image);
                switch (position) {
                    case 0:
                        isHighChair = !isHighChair;
                        if (isHighChair) {
                            image.setImageResource(R.mipmap.icon_selected);
                        } else {
                            image.setImageResource(R.mipmap.icon_hightable);
                        }
                        break;
                    case 1:
                        isBoosterSeat = !isBoosterSeat;
                        if (isBoosterSeat) {
                            image.setImageResource(R.mipmap.icon_selected);
                        } else {
                            image.setImageResource(R.mipmap.icon_booster_seat);
                        }
                        break;
                    case 2:
                        isWheelChair = !isWheelChair;
                        if (isWheelChair) {
                            image.setImageResource(R.mipmap.icon_selected);
                        } else {
                            image.setImageResource(R.mipmap.icon_wheelchair);
                        }
                        break;
                    case 3:
                        isOthers = !isOthers;
                        if (isOthers) {
                            image.setImageResource(R.mipmap.icon_selected);
                        } else {
                            image.setImageResource(R.mipmap.icon_others);
                        }
                        break;
                }
            }
        });

//        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                spinner.setPrompt("1");
//                seatSelection = (String) spinner.getItemAtPosition(position);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });

//         FOR USER PROFILE OPTIONS
//        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                switch (position) {
//                    case 1:
//                        // User Profile
//                        startActivity(new Intent(getActivity(), UserProfileActivity.class));
//                        drawerLayout.closeDrawers();
//                        break;
//                    case 2:
//                        // User Favorites Orders List
//                        startActivity(new Intent(getActivity(), OrderHistoryActivity.class));
//                        drawerLayout.closeDrawers();
//                        break;
//                    case 3:
//                        // Order Booking Status - Show user estimated wait time
//                        Config.bookingStatus = false;
//                        startActivity(new Intent(getActivity(), OrderConfirmationActivity.class));
//                        drawerLayout.closeDrawers();
//                        break;
//
//                    case 4:
//                        shareApp();
//                        break;
//
//                    case 5:
//                        startActivity(new Intent(getActivity(), ChatImages.class));
//                        drawerLayout.closeDrawers();
//                        break;
//
//                    case 6:
//                        startActivity(new Intent(getActivity(), AboutUsActivity.class));
//                        drawerLayout.closeDrawers();
//                        break;
//                }
//            }
//        });

        pickerSelectionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPicker();
            }
        });

        extraNeed.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.scrollTo(0, bookTable.getBottom());
                    }
                }, 500);
                return false;
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
//        SharedPreferences sharedPreference = getActivity().getSharedPreferences("user_info", Context.MODE_PRIVATE);
//        String profileName = sharedPreference.getString("user_name", null);
//        String userAddress = sharedPreference.getString("user_address", null);
//        String userImageUrl = sharedPreference.getString("user_image", null);
//        userName.setText(profileName);
////        userLocation.setText(userAddress);
//        userName.setTypeface(Globals.robotoBold);
////        userLocation.setTypeface(Globals.robotoRegular);
//        Glide.with(this).load(Config.QUICK_CHAT_IMAGE + userImageUrl)
//                .asBitmap().placeholder(R.mipmap.default_profile_pic)
//                .into(userImage);
////        Glide.with(this).load("https://s3-us-west-2.amazonaws.com/stagingquicktable/profile/" + userImageUrl)
////                .asBitmap().placeholder(R.mipmap.default_profile_pic)
////                .into(userImage);
//
//        userImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
    }

    public void postTableRequest() {
        loadingImage.setVisibility(View.VISIBLE);
        seatSelectionLayout.setVisibility(View.GONE);
        final SpecificMenuSingleton menuSingleton = SpecificMenuSingleton.getInstance();
        final SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_info", Context.MODE_PRIVATE);
        String patronId = sharedPreferences.getString("patron_id", null);

        final String url = Config.QT_VISIT_URL;
//        final String url = "http://192.168.1.21:13000/qt/api/visit/";

        JSONObject params = new JSONObject();
        try {
            params.put("party_size", memberNumber.toString());
            if (isHighChair) {
                params.put("high_chairs", "1");
            } else {
                params.put("high_chairs", "0");
            }
            if (isBoosterSeat) {
                params.put("booster_seats", "1");
            } else {
                params.put("booster_seats", "0");
            }
            if (isWheelChair) {
                params.put("is_wheel_chair_required", "1");
            } else {
                params.put("is_wheel_chair_required", "0");
            }

            params.put("special_request", extraNeed.getText().toString());
            params.put("tenant_id", menuSingleton.getTenantId());
            params.put("location_id", menuSingleton.getLocationId());
            params.put("visit_type", "D");
            params.put("patron_id", patronId);

            if (Config.APP_NAME.equalsIgnoreCase("QuickTable")) {
                params.put("from_qt", true);
            } else {
                params.put("from_qt", false);
            }

        } catch (JSONException e) {

        }


        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("GET_IN_LINE", response.toString());
                        loadingImage.setVisibility(View.GONE);
                        seatSelectionLayout.setVisibility(View.VISIBLE);
                        try {
                            if (response.has("error_msg")){
                                Toast.makeText(getActivity(), R.string.hostess_offline_msg, Toast.LENGTH_SHORT).show();
                                return;
                            }else {
                                menuSingleton.setVisitId(response.getInt("id"));
                                int ewt = response.getInt("ewt");
                                menuSingleton.setPosition(response.getInt("position"));
                                int visitId = response.getInt("id");
                                StringBuilder stringBuilder = new StringBuilder();
                                stringBuilder.append("00:");
                                if (ewt < 10) {
                                    stringBuilder.append("0" + ewt + ":");
                                } else {
                                    stringBuilder.append(ewt + ":");
                                }
                                stringBuilder.append("00");
                                menuSingleton.setEwt(stringBuilder.toString());
                                if (visitId != 0) {
                                    SharedPreferences preferences = getActivity().getSharedPreferences("ewt_info", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putBoolean("isDineInOnly", true);
                                    editor.putBoolean("isTakeAway", false);
                                    editor.putBoolean("is_patron_seated", false);
                                    editor.commit();
                                    loadingImage.setVisibility(View.GONE);
//                                seatSelectionLayout.setVisibility(View.VISIBLE);
                                    Config.bookingStatus = true;
                                    startActivity(new Intent(getActivity(), OrderConfirmationActivity.class));
                                }
                            }
                        } catch (JSONException e) {
                        }
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingImage.setVisibility(View.GONE);
                if (error != null) {
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
                Config.TIMEOUT,  // 25 sec - timeout
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        VolleySingleton.getInstance(getActivity()).addToRequestQueue(request);
    }

    private void internetSlowError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Your internet seems to be slow...");
        builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (connectionDetector.isConnectedToInternet()) {
                    postTableRequest();
                } else {
                    connectionDetector.internetError();
                }
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

    }

    class CustomAdapter extends BaseAdapter {

        String[] options = {"High Chair", "Booster Seat", "Wheel Chair", "Others"};
        int[] specialNeedImages = {R.mipmap.icon_hightable,
                R.mipmap.icon_booster_seat,
                R.mipmap.icon_wheelchair,
                R.mipmap.icon_others};

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.seat_single_gridview, parent, false);
            TextView options_text = (TextView) view.findViewById(R.id.special_need_options);
            ImageView specialNeedImage = (ImageView) view.findViewById(R.id.special_need_image);
            specialNeedImage.setImageResource(specialNeedImages[position]);
            options_text.setText(options[position]);

//            if (position == 3) {
//                options_text.setText(options[position]);
//                options_text.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
//            }
            return view;
        }
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Alert");
        builder.setMessage("Please select A Table for ?");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showPicker() {
        final Dialog numberPickerDialog = new Dialog(getActivity());
        numberPickerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        numberPickerDialog.setContentView(R.layout.number_picker_dialog);
        Button set = (Button) numberPickerDialog.findViewById(R.id.set_button);
        Button cancel = (Button) numberPickerDialog.findViewById(R.id.cancel_button);
        final NumberPicker numberPicker = (NumberPicker) numberPickerDialog.findViewById(R.id.number_picker);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(30);
        if (memberNumber != 0) {
            numberPicker.setValue(memberNumber);
        }
        numberPicker.setWrapSelectorWheel(false);
        numberPicker.setOnValueChangedListener(this);
        numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                memberNumber = numberPicker.getValue();
                tableCount.setText(memberNumber.toString());
                numberPickerDialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numberPickerDialog.dismiss();
            }
        });
        numberPickerDialog.show();
    }

    private void showRestaurantVaryError() {
        SharedPreferences preferences = getActivity().getSharedPreferences("ewt_info", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = preferences.edit();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Items already in cart!");
        builder.setMessage("Your cart contains items from other restaurant. Would you like to reset your cart before browsing this restaurant?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                CartSingleton.getInstance().clearCart();
                MenuItemFragment.updateCartCount();
                VolleySingleton.getInstance(getActivity()).postEventRequest("GetInLine", SpecificMenuSingleton.getInstance().getClickedRestaurant());
                postTableRequest();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void shareApp() {
        final SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_info", Context.MODE_PRIVATE);
        String userName = sharedPreferences.getString("user_name", null);
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

        // Add data to the intent, the receiving app will decide
        // what to do with it.
//        share.putExtra(Intent.EXTRA_SHORTCUT_ICON, "");
//        share.putExtra(Intent.EXTRA_TITLE, "QuickTable");
        share.putExtra(Intent.EXTRA_TEXT, "Hey ! " + userName.toUpperCase() + " has personally invited you to download the new QuickTable app…it's like all your favorite restaurant apps rolled into one, and you will love the QuickChat feature!  Just click the link below or go to the app store and search for QuickTable (all one word).\n" +
                "\n" +
                "Have a great day!\n\n" + "https://play.google.com/store/apps/details?id=com.app.mobi.quicktabledemo&hl=en");

        startActivity(Intent.createChooser(share, "Share link!"));
    }
}
