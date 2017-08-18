package com.app.android.quickbud.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import com.app.android.quickbud.network.VolleySingleton;
import com.app.android.quickbud.utils.Config;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegistrationValidateFragment extends Fragment {

    ProgressDialog dialog;
    String requestId, phoneNumber, newRequestId;
    EditText verificationCode;
    TextView resendText;
    boolean isResendCode = false;
    Button resend;
    private Handler sendCodeHandler;
    private Runnable runnable;


    public RegistrationValidateFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_registration_validate, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Button verify = (Button) getActivity().findViewById(R.id.verify);
        resend = (Button) getActivity().findViewById(R.id.resend_verfication);
        verificationCode = (EditText) getActivity().findViewById(R.id.verification_code);
        resendText = (TextView) getActivity().findViewById(R.id.resend_text);
        resend.setEnabled(false);
        resend.setTextColor(0xffD3D3D3);
        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isResendCode = true;
                Toast toast = Toast.makeText(getActivity(), "you will receive an sms shortly, Please wait!", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.TOP, Gravity.CENTER_HORIZONTAL, 30);
                toast.show();
                resendVerificationCode();
//                startActivity(new Intent(getActivity(), ListOfRestaurantActivity.class));
//                getActivity().finish();
            }
        });
        Toast toast = Toast.makeText(getActivity(), "You will receive an sms shortly, Please wait!", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP, Gravity.CENTER_HORIZONTAL, 30);
        toast.show();
        requestId = getArguments().getString("request_id");
        phoneNumber = getArguments().getString("phone_number");

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateUser();
//                startActivity(new Intent(getActivity(), ListOfRestaurantActivity.class));
//                getActivity().finish();
            }
        });

        activateResendButton();
    }


    private void validateUser() {
        final String checkVerificationURL = "https://api.nexmo.com/verify/check/json?";
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Verification in progress, Please wait!");
        progressDialog.setCancelable(false);
        progressDialog.show();
        HashMap<String, String> params = new HashMap<>();
        params.put("api_key", "53ef6fd6");
        params.put("api_secret", "9f522ef1");
        if (isResendCode) {
            params.put("request_id", newRequestId);
        } else {
            params.put("request_id", requestId);
        }
        params.put("code", verificationCode.getText().toString());

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, checkVerificationURL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.i("Validating User", response.toString());
                        try {
                            String status = response.getString("status");

                            if (status.equals("0")) { //verification code matches then,
                                QTRegistration(progressDialog);
                                SharedPreferences isRegistered = getActivity().getSharedPreferences("Registration", 0);
                                SharedPreferences.Editor editor = isRegistered.edit();
                                editor.putBoolean("isFirstTime", true);
                                editor.commit();
                            } else {  //verification code mis-matched then,
                                if (progressDialog.isShowing()) {
                                    progressDialog.dismiss();
                                }
                                Toast.makeText(getActivity(), "Incorrect code entered!", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error != null){
                    Log.i("Validate user error: ", error.toString());
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    if (error instanceof TimeoutError) {
                        Config.internetSlowError(getActivity());
                    } else if (error instanceof NoConnectionError) {
                        Config.internetSlowError(getActivity());
                    } else if (error instanceof ServerError) {
                        Config.serverError(getActivity());
                    }
                }
            }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(
                Config.TIMEOUT,  // 25 sec - timeout
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        VolleySingleton.getInstance(getActivity()).addToRequestQueue(request);
    }

    private void resendVerificationCode() {
        final String resendCodeURL = "https://api.nexmo.com/verify/control/json?";

        HashMap<String, String> params = new HashMap<>();
        params.put("api_key", "53ef6fd6");
        params.put("api_secret", "9f522ef1");
        params.put("request_id", requestId);
        params.put("cmd", "cancel");

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, resendCodeURL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        sendMessage();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error != null){
                    if (error instanceof TimeoutError) {
                        Config.internetSlowError(getActivity());
                    } else if (error instanceof NoConnectionError) {
                        Config.internetSlowError(getActivity());
                    } else if (error instanceof ServerError) {
                        Config.serverError(getActivity());
                    }
                }
            }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(
                Config.TIMEOUT,  // 25 sec - timeout
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        VolleySingleton.getInstance(getActivity()).addToRequestQueue(request);
    }

    private void QTRegistration(final ProgressDialog progressDialog) {
        final String QTRegistrationURL = Config.QT_REGISTRATION_URL;

        final SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_info", Context.MODE_PRIVATE);
        String userPhone = sharedPreferences.getString("user_phone", null);
        String userName = sharedPreferences.getString("user_name", null);
        String tokenId = sharedPreferences.getString("TokenId", "No Token Found!");
        Log.i("FCM Token Id", tokenId);
//        userPhone = "1" + userPhone;

        JSONObject params = new JSONObject();
        try {
            params.put("name", userName);
            params.put("phone_number", userPhone);
            params.put("registration_id", tokenId);
            params.put("app_id", Config.APP_PACKAGE);
            if (!Config.APP_NAME.equalsIgnoreCase("QuickTable")) {
                params.put("tenant_id", Config.APP_TENANT_ID);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, QTRegistrationURL, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("QTRegistration", response.toString());

                        if (progressDialog.isShowing()){
                            progressDialog.dismiss();
                        }
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        try {

//                            ((RegistrationActivity)getActivity()).backgroundTask();
                            editor.putString("patron_id", response.getString("id"));
                            editor.putString("session_token_id", response.getString("token"));
//                            editor.putBoolean("set_location_timer", true);
                            editor.apply();
                            Config.SESSION_TOKEN_ID = response.getString("token");
//                            startActivity(new Intent(getActivity(), ListOfRestaurantActivity.class));
                            startActivity(new Intent(getActivity(), MainMenuActivity.class));
                            getActivity().finish();

                        } catch (JSONException e) {

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (progressDialog.isShowing()){
                    progressDialog.dismiss();
                }

                SharedPreferences isRegistered = getActivity().getSharedPreferences("Registration", 0);
                SharedPreferences.Editor editor = isRegistered.edit();
                editor.putBoolean("isFirstTime", false);
                editor.commit();

                Log.i("QTReg user error: ", error.toString());

                if (error != null){
                    if (error instanceof TimeoutError) {
                        Config.internetSlowError(getActivity());
                    } else if (error instanceof NoConnectionError) {
                        Config.internetSlowError(getActivity());
                    } else if (error instanceof ServerError) {
                        Config.serverError(getActivity());
                    }
                }
            }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(
                Config.TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        VolleySingleton.getInstance(getActivity()).addToRequestQueue(request);
    }

    public void sendMessage() {
        final String url = "https://api.nexmo.com/verify/json?";
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Verification in progress, Please wait!");
        progressDialog.setCancelable(false);
        progressDialog.show();
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("api_key", "53ef6fd6");
        params.put("api_secret", "9f522ef1");
        params.put("number", phoneNumber);
        params.put("brand", "QuickTable");

        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("Verification", response.toString());

                        try {
                            String status = response.getString("status");
                            progressDialog.dismiss();
                            if (status.equals("0")) {
                                progressDialog.dismiss();
                                newRequestId = response.getString("request_id");
                            }
                        } catch (JSONException e) {

                        }
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error != null){
                    if (progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                    if (error instanceof TimeoutError) {
                        Config.internetSlowError(getActivity());
                    } else if (error instanceof NoConnectionError) {
                        Config.internetSlowError(getActivity());
                    } else if (error instanceof ServerError) {
                        Config.serverError(getActivity());
                    }
                }
            }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(
                Config.TIMEOUT,  // 25 sec - timeout
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        VolleySingleton.getInstance(getActivity()).addToRequestQueue(request);
    }

    private void showVerificationMessage() {
        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Verification in progress, Please wait!");
        progressDialog.show();
    }

    private void activateResendButton() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                Log.d("Button","Resend Button Activated");
                resend.setEnabled(true);
                resend.setTextColor(0xffFFFFFF);

            }
        }, 30 * 1000);
    }
}
