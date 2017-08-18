package com.app.android.quickbud.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.app.android.quickbud.adapters.CountryCodeAdapter;
import com.app.android.quickbud.modelClasses.CountryCodeModel;
import com.app.android.quickbud.network.VolleySingleton;
import com.app.android.quickbud.utils.Config;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by mobi11 on 14/9/15.
 */
public class RegistrationFragment extends Fragment implements View.OnClickListener {

    Button registerBtn;
    EditText userName,userPhone;
    String status,requestId;
    private ProgressDialog progressDialog;
    ImageView countryFlagImage;
    TextView countryCodeTxt;
    private String[] countryNames = {"US", "Canada", "UK", "France", "Netherlands", "Germany"};
    private int[] countryFlag = {R.mipmap.ic_flag_us, R.mipmap.ic_flag_canada, R.mipmap.ic_flag_uk,
            R.mipmap.ic_flag_france, R.mipmap.ic_flag_netherlands, R.mipmap.ic_flag_germany};
    private String[] countryCode = {"1", "1", "44", "33", "31", "49"};
    private ArrayList<CountryCodeModel> countryCodeModels;
    private String selectedCountryCode = "1";

    public static RegistrationFragment newInstance() {
        return new RegistrationFragment();
    }

    public RegistrationFragment() {
        //default Contructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.registration_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        registerBtn = (Button) getActivity().findViewById(R.id.register);
        userName = (EditText) getActivity().findViewById(R.id.user_name);
        userPhone = (EditText) getActivity().findViewById(R.id.user_phone);
        countryFlagImage = (ImageView) getActivity().findViewById(R.id.flag_image);
        countryCodeTxt = (TextView) getActivity().findViewById(R.id.country_code);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Checking your credentials, Please Wait!");
        progressDialog.setCancelable(false);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                if (userName.getText().toString().equalsIgnoreCase("")){
                    Toast.makeText(getActivity(), "Please enter your name!", Toast.LENGTH_SHORT).show();
                }else {
                    saveUserData(userName.getText().toString(), userPhone.getText().toString());
                    sendMessage();
                }
//                saveUserData(userName.getText().toString(), userPhone.getText().toString());
//                RegistrationValidateFragment registrationValidateFragment = new RegistrationValidateFragment();
//                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_activity, registrationValidateFragment)
//                        .addToBackStack(RegistrationValidateFragment.class.getName()).commit();

            }
        });

        countryFlagImage.setOnClickListener(this);
        countryCodeTxt.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.flag_image:
            case R.id.country_code:
                showCountrySelection();
                break;
        }

    }

    public void sendMessage(){
        final String url = "https://api.nexmo.com/verify/json?";
        progressDialog.show();
        HashMap<String, String> params = new HashMap<String, String>();
//        params.put("api_key", "306bbed9");
//        params.put("api_secret", "76ea5f99");
        params.put("api_key", "53ef6fd6");
        params.put("api_secret", "9f522ef1");
        params.put("number", selectedCountryCode + userPhone.getText().toString());
        params.put("brand",getResources().getString(R.string.app_name));

        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("Verification",response.toString());

                        try {
                            status = response.getString("status");
                            if(status.equals("0")) {
                                progressDialog.dismiss();
                                requestId = response.getString("request_id");
                                RegistrationValidateFragment registrationValidateFragment = new RegistrationValidateFragment();
                                Bundle bundle = new Bundle();
                                bundle.putString("request_id",requestId);
                                bundle.putString("phone_number",selectedCountryCode + userPhone.getText().toString());
                                registrationValidateFragment.setArguments(bundle);
                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_activity, registrationValidateFragment)
                                        .addToBackStack(RegistrationValidateFragment.class.getName()).commit();
                            }else if(status.equals("10")) {
                                progressDialog.dismiss();
                                Toast.makeText(getActivity(),"Same number concurrently not allowed!",Toast.LENGTH_SHORT).show();
                            }else {
                                progressDialog.dismiss();
                                Toast.makeText(getActivity(),"Please enter valid phone number!",Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {

                        }
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
                        Config.internetSlowError(getActivity());
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

    private void saveUserData(String userName, String userPhone){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_info", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user_name",userName);
        editor.putString("user_phone",selectedCountryCode + userPhone);
        editor.commit();
    }

    private void setUpData(){
        countryCodeModels = new ArrayList<>();

        for (int i=0; i<countryNames.length; i++) {
            CountryCodeModel model = new CountryCodeModel();
            model.setCountryName(countryNames[i]);
            model.setCountryFlag(countryFlag[i]);
            model.setCountryCode(countryCode[i]);
            countryCodeModels.add(model);
        }
    }

    private void showCountrySelection() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        setUpData();
        CountryCodeAdapter adapter = new CountryCodeAdapter(getActivity(), countryCodeModels);

        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                countryFlagImage.setImageResource(countryCodeModels.get(which).getCountryFlag());
                countryCodeTxt.setText("+" + countryCodeModels.get(which).getCountryCode());
                selectedCountryCode = countryCodeModels.get(which).getCountryCode();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setCancelable(false);

        builder.show();
    }
}
