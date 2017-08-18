package com.app.android.quickbud.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.app.android.quickbud.network.VolleySingleton;
import com.app.android.quickbud.utils.Config;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UserProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    RelativeLayout cartContent;
    ImageView homeBtn;
    RoundedImageView ivProfile;
    Context context = this;
    ImageView btnUpload;
    Button savebutton, cancelBtn;
    EditText user_name, user_email, userAddressLine, userAddressCity, userAddressState, userAddressZip;
    TextView user_phone;
    CameraHandler cameraHandler;
    ImageView loadingImage;
    ScrollView rootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        cartContent = (RelativeLayout) findViewById(R.id.cart_content);
        homeBtn = (ImageView) findViewById(R.id.menu_button);
        loadingImage = (ImageView) findViewById(R.id.loading_screen);
        rootLayout = (ScrollView) findViewById(R.id.root_layout);

        toolbar.setTitle("Profile");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        findViews();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.primary_color));
            savebutton.setBackgroundColor(ContextCompat.getColor(this, R.color.primary_color));
            cancelBtn.setBackgroundColor(ContextCompat.getColor(this, R.color.primary_color));
        }

        cartContent.setVisibility(View.GONE);

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserProfileActivity.this, MainMenuActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();

            }
        });

        ivProfile = (RoundedImageView) findViewById(R.id.iv_upload);

        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.default_profile_pic);
        ivProfile.setImageBitmap(icon);

        cameraHandler = new CameraHandler(context);
        cameraHandler.setIvPicture(ivProfile);

        if (cameraHandler.getImage() != null) {
            Bitmap cameraImage = cameraHandler.getImage();
        }

        SharedPreferences sharedPreferences = getSharedPreferences("user_info", MODE_PRIVATE);
        if (sharedPreferences != null) {
            user_name.setText(sharedPreferences.getString("user_name", null));
            user_email.setText(sharedPreferences.getString("user_email", null));
            user_phone.setText(sharedPreferences.getString("user_phone", null));
            userAddressLine.setText(sharedPreferences.getString("user_address_line", null));
            userAddressCity.setText(sharedPreferences.getString("user_address_city", null));
            userAddressState.setText(sharedPreferences.getString("user_address_state", null));
            userAddressZip.setText(sharedPreferences.getString("user_address_zip", null));

            String imageUrl = sharedPreferences.getString("user_image", null);

//            if (imageUrl != null){
//                Glide.with(this).load(Config.QUICK_CHAT_IMAGE + imageUrl)
//                        .asBitmap().fitCenter()
//                        .placeholder(R.mipmap.default_profile_pic)
//                        .into(ivProfile);
//
////                Glide.with(this).load("https://s3-us-west-2.amazonaws.com/stagingquicktable/profile/" + imageUrl)
////                        .asBitmap().fitCenter()
////                        .placeholder(R.mipmap.default_profile_pic)
////                        .into(ivProfile);
//            }
        }

        getUserProfile();
    }


    private void findViews() {
        ivProfile = (RoundedImageView) findViewById(R.id.iv_upload);
        btnUpload = (ImageView) findViewById(R.id.btn_upload_image);
        savebutton = (Button) findViewById(R.id.save_user_profile);
        cancelBtn = (Button) findViewById(R.id.cancel_user_profile);
        user_name = (EditText) findViewById(R.id.user_name);
        user_email = (EditText) findViewById(R.id.user_email);
        user_phone = (TextView) findViewById(R.id.user_phone);
        userAddressLine = (EditText) findViewById(R.id.address_line);
        userAddressCity = (EditText) findViewById(R.id.address_city);
        userAddressState = (EditText) findViewById(R.id.address_state);
        userAddressZip = (EditText) findViewById(R.id.address_zip);
        savebutton.setOnClickListener(this);
        btnUpload.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_upload_image) {
            cameraHandler.showView();
        } else if (id == R.id.save_user_profile) {
            if (!user_email.getText().toString().equals("")) {
                if (user_name.getText().toString().isEmpty()) {
                    Toast.makeText(this, "Please enter your name!", Toast.LENGTH_SHORT).show();
                } else if (userAddressLine.getText().toString().isEmpty() || userAddressCity.getText().toString().isEmpty()
                        || userAddressState.getText().toString().isEmpty() || userAddressZip.getText().toString().isEmpty()) {
                    Toast.makeText(this, "Please enter your full address!", Toast.LENGTH_SHORT).show();
                } else {
                    if (validateEmail(user_email.getText().toString())) {
                        SharedPreferences sharedPreferences = getSharedPreferences("user_info", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("user_name", user_name.getText().toString());
                        editor.putString("user_email", user_email.getText().toString());
                        editor.putString("user_phone", user_phone.getText().toString());
                        editor.putString("user_address_line", userAddressLine.getText().toString());
                        editor.putString("user_address_city", userAddressCity.getText().toString());
                        editor.putString("user_address_state", userAddressState.getText().toString());
                        editor.putString("user_address_zip", userAddressZip.getText().toString());
                        editor.commit();
                        sendUserProfile(view);
                    } else {
                        Toast.makeText(this, "Please enter a valid email Id!", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                if (user_name.getText().toString().isEmpty()) {
                    Toast.makeText(this, "Please enter your name!", Toast.LENGTH_SHORT).show();
                } else if (userAddressLine.getText().toString().isEmpty() || userAddressCity.getText().toString().isEmpty()
                        || userAddressState.getText().toString().isEmpty() || userAddressZip.getText().toString().isEmpty()) {
                    Toast.makeText(this, "Please enter your full address!", Toast.LENGTH_SHORT).show();
                } else {
                    SharedPreferences sharedPreferences = getSharedPreferences("user_info", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("user_name", user_name.getText().toString());
                    editor.putString("user_email", user_email.getText().toString());
                    editor.putString("user_phone", user_phone.getText().toString());
                    editor.putString("user_address_line", userAddressLine.getText().toString());
                    editor.putString("user_address_city", userAddressCity.getText().toString());
                    editor.putString("user_address_state", userAddressState.getText().toString());
                    editor.putString("user_address_zip", userAddressZip.getText().toString());
                    editor.commit();
                    sendUserProfile(view);
                }
            }
        } else if (id == R.id.cancel_user_profile) {
            showAlert(view);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        cameraHandler.onResult(requestCode, resultCode, data);
        Log.v("", "code = > " + requestCode);
//        Bitmap cameraImage = cameraHandler.getImage();

        Uri imageUri = cameraHandler.getImageUri();

        Glide.with(this).load(imageUri).asBitmap().listener(new RequestListener<Uri, Bitmap>() {
            @Override
            public boolean onException(Exception e, Uri model, Target<Bitmap> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Bitmap bitmap, Uri model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] bytes = baos.toByteArray();
                String encoded = Base64.encodeToString(bytes, Base64.DEFAULT);
                postImage(encoded);
                return false;
            }
        }).into(ivProfile);

//        if (imageUri != null) {
//
//            ExifInterface exifInterface = null;
//            try {
//                exifInterface = new ExifInterface(imageUri.getPath());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            int rotation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
//
//            System.out.println("rotation = " + rotation);
//
////         int screenOrientation = getResources().getConfiguration().orientation;
//
//            SharedPreferences preferences = getSharedPreferences("user_info", MODE_PRIVATE);
//            String deviceName = preferences.getString("device_name", null);
//
////        Camera.CameraInfo info = new Camera.CameraInfo();
//
//            if (deviceName.equals("samsung") || deviceName.equals("Sony") || deviceName.equalsIgnoreCase("LGE")) {
//
//                if (Build.VERSION.SDK_INT > 17) {
//                    if (rotation == 6) {
//                        // portrait == 6
//                        Matrix matrix = new Matrix();
//                        matrix.postRotate(90);
//                        cameraImage = Bitmap.createBitmap(cameraImage, 0, 0, cameraImage.getWidth(), cameraImage.getHeight(), matrix, true);
//                    }
//
//                    if (rotation == 8) {
//                        // portrait == 8 front camera
//                        Matrix matrix = new Matrix();
//                        matrix.postRotate(270);
//                        cameraImage = Bitmap.createBitmap(cameraImage, 0, 0, cameraImage.getWidth(), cameraImage.getHeight(), matrix, true);
//                    }
//                }
//            }
//
//            if (cameraImage != null) {
//
//                Glide.with(this).load(imageUri)
//                        .asBitmap()
//                        .into(ivProfile);
//
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                cameraImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//                byte[] bytes = baos.toByteArray();
//
//                String encoded = Base64.encodeToString(bytes, Base64.DEFAULT);
//                postImage(encoded);
//
////            ivProfile.setImageBitmap(cameraImage);
//            }

//        try {
//            String string = URLEncoder.encode(bytes,"UTF-8");
//            Log.i("utf-8",string);
//        } catch (UnsupportedEncodingException e) {
//
//        }
//        try {
//            String str = new String (baos.toByteArray(), "UTF-8");
//
//        } catch (UnsupportedEncodingException e) {
//
//        }

//        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    public void postImage(String image) {

        String url = Config.QT_IMAGE_UPLOAD;
//        String url = Config.QUICK_CHAT_IMAGE;
//        String url = "http://104.131.142.154/act/api/upload";

        JSONObject object = new JSONObject();
        try {
//            object.put("type","image");
            object.put("image", image);
        } catch (JSONException e) {

        }

        Log.i("imageEncode", object.toString());

        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("ImageUpload", response.toString());
                        String imageUrl = null;
                        try {
                            imageUrl = response.getString("url");
                        } catch (JSONException e) {

                        }

                        SharedPreferences sharedPreferences = getSharedPreferences("user_info", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("user_image", imageUrl);
                        editor.commit();

//                        Toast.makeText(UserProfileActivity.this, "Image uploaded", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error != null) {
                    if (error instanceof TimeoutError) {
                        Config.internetSlowError(UserProfileActivity.this);
                    } else if (error instanceof NoConnectionError) {
                        Config.internetSlowError(UserProfileActivity.this);
                    } else if (error instanceof ServerError) {
                        Config.serverError(UserProfileActivity.this);
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
                Config.TIMEOUT,  // 45 sec - timeout
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        VolleySingleton.getInstance(this).addToRequestQueue(request);

    }

    private void sendUserProfile(final View view) {

        loadingImage.setVisibility(View.VISIBLE);
        rootLayout.setVisibility(View.GONE);

        SharedPreferences sharedPreferences = getSharedPreferences("user_info", MODE_PRIVATE);
        String patronId = sharedPreferences.getString("patron_id", null);
        String imageUrl = sharedPreferences.getString("user_image", null);
        String url = Config.QT_PATRON_UPDATE + patronId + "/modify/";
        String userName = sharedPreferences.getString("user_name", null);
        String userPhone = sharedPreferences.getString("user_phone", null);

        JSONObject object = new JSONObject();
        try {
            object.put("name", userName);
//            object.put("phone_number", userPhone);
            object.put("url", imageUrl);
            object.put("address_line", userAddressLine.getText().toString());
            object.put("city", userAddressCity.getText().toString());
            object.put("state", userAddressState.getText().toString());
            object.put("zip", userAddressZip.getText().toString());
        } catch (JSONException e) {

        }

//        System.out.println("UserProfileUrl = " + url);
//        System.out.println("UserProfileObject = " + object.toString());

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("UserProfile", response.toString());
                        loadingImage.setVisibility(View.GONE);
                        rootLayout.setVisibility(View.VISIBLE);
                        Toast.makeText(UserProfileActivity.this, "User profile saved successfully!", Toast.LENGTH_SHORT).show();
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        finish();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingImage.setVisibility(View.GONE);
                rootLayout.setVisibility(View.VISIBLE);
                if (error != null) {
                    if (error instanceof TimeoutError) {
                        Config.internetSlowError(UserProfileActivity.this);
                    } else if (error instanceof NoConnectionError) {
                        Config.internetSlowError(UserProfileActivity.this);
                    } else if (error instanceof ServerError) {
                        Config.serverError(UserProfileActivity.this);
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

        VolleySingleton.getInstance(this).addToRequestQueue(request);

    }

    private void getUserProfile() {

        loadingImage.setVisibility(View.VISIBLE);
        rootLayout.setVisibility(View.GONE);

        SharedPreferences sharedPreferences = getSharedPreferences("user_info", MODE_PRIVATE);
        String patronId = sharedPreferences.getString("patron_id", null);

        String url = Config.QT_PATRON_UPDATE + patronId;


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("UserProfile", response.toString());
                        loadingImage.setVisibility(View.GONE);
                        rootLayout.setVisibility(View.VISIBLE);

                        try {
                            user_name.setText(response.getString("name"));
//                            if (response.has(""))
//                            user_email.setText(sharedPreferences.getString("user_email", null));
                            user_phone.setText(response.getString("phone_number"));

                            JSONArray addressArray = response.getJSONArray("address");
                            if (addressArray != null && addressArray.length() > 0) {
                                JSONObject addressObj = addressArray.getJSONObject(0);
                                userAddressLine.setText(addressObj.getString("address_line"));
                                userAddressCity.setText(addressObj.getString("city"));
                                userAddressState.setText(addressObj.getString("state"));
                                userAddressZip.setText(addressObj.getString("zip"));
                            }

                            String imageUrl = response.getString("url");

                            if (imageUrl != null) {
                                Glide.with(UserProfileActivity.this).load(Config.QUICK_CHAT_IMAGE + imageUrl)
                                        .asBitmap().fitCenter()
                                        .placeholder(R.mipmap.default_profile_pic)
                                        .into(ivProfile);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                loadingImage.setVisibility(View.GONE);
//                rootLayout.setVisibility(View.VISIBLE);
                if (error != null) {
                    if (error instanceof TimeoutError) {
                        Config.internetSlowError(UserProfileActivity.this);
                    } else if (error instanceof NoConnectionError) {
                        Config.internetSlowError(UserProfileActivity.this);
                    } else if (error instanceof ServerError) {
                        Config.serverError(UserProfileActivity.this);
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

        VolleySingleton.getInstance(this).addToRequestQueue(request);

    }

    private void showAlert(final View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alert");
        builder.setMessage("Do you really want to discard your changes ?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                finish();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private boolean validateEmail(String email) {

        if (email.contains("@") && email.contains(".")) {
            return true;
        }
        return false;
    }
}




