package com.app.android.quickbud.activities;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.app.android.quickbud.R;
import com.app.android.quickbud.adapters.SavedSearchesAdapter;
import com.app.android.quickbud.modelClasses.SearchLocationModel;
import com.app.android.quickbud.network.VolleySingleton;
import com.app.android.quickbud.utils.Config;
import com.app.android.quickbud.utils.ConnectionDetector;
import com.app.android.quickbud.utils.GPSTracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchLocationActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    //    https://maps.googleapis.com/maps/api/place/autocomplete/json?input=pizza&location=17.4147205,78.4301799&radius=5000&key=AIzaSyDXZZEpRuN3AOrFF9lOYq4jTbCQGV6WFOo
    Toolbar toolbar;
    ListView locationSearchedList, savedSearchedList;
    EditText searchRest;
    private GooglePlacesAutocompleteAdapter googlePlacesAutocompleteAdapter;
    private ArrayList<SearchLocationModel> resultList = new ArrayList<>();
    private ArrayList<SearchLocationModel> outputList = new ArrayList<>();
    private ConnectionDetector connectionDetector;
    private boolean isInternetPresent;
    String location;
    private static double latitude;
    private static double longitude;
    private SavedSearchesAdapter savedSearchesAdapter;
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private static final String API_KEY = "AIzaSyBbU-VhGVEZQ0D8k7TIMyVSPoJq-oFWODY";
    private static final String DEFAULT = "N/A";
    private GPSTracker gpsTracker;
    private boolean isSearchByName = false;
    private ArrayList<SearchLocationModel> storedArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_location);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        ImageView userProfile = (ImageView) findViewById(R.id.menu_button);
        searchRest = (EditText) findViewById(R.id.search_view);
        RelativeLayout cartContent = (RelativeLayout) findViewById(R.id.cart_content);
        LinearLayout searchContent = (LinearLayout) findViewById(R.id.search_container);
        locationSearchedList = (ListView) findViewById(R.id.searched_listview);
        savedSearchedList = (ListView) findViewById(R.id.savedSearchListView);
        TextView currentLocation = (TextView) findViewById(R.id.current_location);
        LinearLayout currentLocationLayout = (LinearLayout) findViewById(R.id.current_location_layout);
        userProfile.setVisibility(View.GONE);
        cartContent.setVisibility(View.GONE);
        searchContent.setVisibility(View.VISIBLE);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.primary_color));
//        }

        Intent intent = getIntent();
        location = intent.getStringExtra("location");
        isSearchByName = intent.getBooleanExtra("isSearchByName", false);

        currentLocation.setText("Current Location");
        currentLocationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("search_location", location);
                intent.putExtra("current_location", true);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        searchRest.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ConnectionDetector detector = new ConnectionDetector(SearchLocationActivity.this);
                if (!detector.isConnectedToInternet()) {
                    detector.internetError();
                } else {
                    if (s.length() > 2) {
                        googlePlacesAutocompleteAdapter.getFilter().filter(s.toString());
                    } else if (s.length() <= 2) {
                        resultList.clear();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        resultList = new ArrayList<>();
        googlePlacesAutocompleteAdapter = new GooglePlacesAutocompleteAdapter(SearchLocationActivity.this, R.layout.single_search_location_item);
        locationSearchedList.setAdapter(googlePlacesAutocompleteAdapter);
        locationSearchedList.setOnItemClickListener(this);


        savedSearchesAdapter = new SavedSearchesAdapter(this, outputList);
        savedSearchedList.setAdapter(savedSearchesAdapter);
        savedSearchedList.setOnItemClickListener(new AdapterView.OnItemClickListener() { //saved location Click
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SearchLocationModel searchLocationModel = (SearchLocationModel) parent.getItemAtPosition(position);
                Intent intent = new Intent();
                intent.putExtra("search_location", searchLocationModel.getAddress1() + ", " + searchLocationModel.getAddress2());
                intent.putExtra("latitude", searchLocationModel.getLatitude());
                intent.putExtra("longitude", searchLocationModel.getLongitude());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        load();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    public void load() {
        SharedPreferences sharedPreferences = getSharedPreferences("RecentSearchesModel", Context.MODE_PRIVATE);
        String searchObjectString = sharedPreferences.getString("recentSearchListObject", DEFAULT);
        try {

            JSONObject searchInsertObject = new JSONObject(searchObjectString);
            JSONArray jsonArray = searchInsertObject.getJSONArray("searchListObject");
            storedArrayList = new ArrayList<SearchLocationModel>();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                SearchLocationModel searchesModel = new SearchLocationModel();
                searchesModel.setAddress1(jsonObject.getString("place"));
                searchesModel.setAddress2(jsonObject.getString("place1"));
                if (jsonObject.has("placeId")){
                    searchesModel.setPlaceId(jsonObject.getString("placeId"));
                }
                if (jsonObject.has("latitude")){
                    searchesModel.setLatitude(jsonObject.getDouble("latitude"));
                    searchesModel.setLongitude(jsonObject.getDouble("longitude"));
                }
                storedArrayList.add(searchesModel);
            }
//            storedArrayList = removeDuplicate(storedArrayList);
            savedSearchesAdapter = new SavedSearchesAdapter(this, storedArrayList);
            savedSearchedList.setAdapter(savedSearchesAdapter);
        } catch (JSONException e) {
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void save(SearchLocationModel model) {
        boolean isPresent = false;
        outputList.add(model);

        SharedPreferences sharedPreferences = getSharedPreferences("RecentSearchesModel", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        SearchLocationModel model1 = model;

        if (storedArrayList != null){
            for (int i=0; i<storedArrayList.size(); i++){
                if (storedArrayList.get(i).getPlaceId() != null){
                    if (model1.getPlaceId().equalsIgnoreCase(storedArrayList.get(i).getPlaceId())){
                        isPresent = true;
                        break;
                    }
                }
            }
        }

        if (!(sharedPreferences.contains("recentSearchListObject"))) {
            JSONArray jsonArray = new JSONArray();
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("placeId", model1.getPlaceId());
                jsonObject.put("place", model1.getAddress1());
                jsonObject.put("place1", model1.getAddress2());
                jsonObject.put("latitude", model1.getLatitude());
                jsonObject.put("longitude", model1.getLongitude());
                jsonArray.put(jsonObject);

                JSONObject searchObject = new JSONObject();
                searchObject.put("searchListObject", jsonArray);
                editor.putString("recentSearchListObject", searchObject.toString());
                editor.commit();
            } catch (JSONException e) {

            }
        } else if (sharedPreferences.contains("recentSearchListObject")) {

            if (!isPresent){
                String searchObjectString = sharedPreferences.getString("recentSearchListObject", DEFAULT);

                try {
                    JSONObject searchInsertObject = new JSONObject(searchObjectString);
                    JSONArray jsonArray = searchInsertObject.getJSONArray("searchListObject");
                    JSONArray searchedArray = new JSONArray();

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("placeId", model1.getPlaceId());
                    jsonObject.put("place", model1.getAddress1());
                    jsonObject.put("place1", model1.getAddress2());
                    jsonObject.put("latitude", model1.getLatitude());
                    jsonObject.put("longitude", model1.getLongitude());

                    searchedArray.put(0, jsonObject);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        searchedArray.put(i + 1, object);
                        if (i >= 4) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                searchedArray.remove(i);
                            }
                        }
                    }

                    searchInsertObject.put("searchListObject", searchedArray);
                    editor.putString("recentSearchListObject", searchInsertObject.toString());
                    editor.commit();

                } catch (Exception e) {

                }
            }

        }


    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        save(resultList.get(position));
        SearchLocationModel searchLocationModel = (SearchLocationModel) parent.getItemAtPosition(position);
        Intent intent = new Intent();
        intent.putExtra("search_location", searchLocationModel.getAddress1() + " " + searchLocationModel.getAddress2());
        intent.putExtra("latitude", resultList.get(position).getLatitude());
        intent.putExtra("longitude", resultList.get(position).getLongitude());
        setResult(RESULT_OK, intent);
        finish();
//        Toast.makeText(this,str,Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        connectionDetector = new ConnectionDetector(this);
        isInternetPresent = connectionDetector.isConnectedToInternet();
        if (!isInternetPresent) {
            connectionDetector.internetError();
            return;
        }
    }

    @Override
    protected void onPause() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchRest.getWindowToken(), 0);
        super.onPause();
        if (gpsTracker != null) {
            gpsTracker.stopLocation();
        }
    }

    private void searchDispensaryLocation(String input){

        String url = Config.QB_SEARCH_URL + "?searchkey=";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.i("Search Response", response.toString());

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("QTAuthorization", Config.SESSION_TOKEN_ID);
                return header;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                Config.TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        VolleySingleton.getInstance(this).addToRequestQueue(request);

    }

    public ArrayList autocomplete(String input) {
        ArrayList<SearchLocationModel> resultList = new ArrayList<>();

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(Config.QB_SEARCH_URL);
//            sb.append("?key=" + API_KEY);
//            sb.append("?input=" + URLEncoder.encode(input, "utf8"));
            if (isSearchByName){
                sb.append("?searchname=" + URLEncoder.encode(input, "utf8"));
            }else {
                sb.append("?searchcity=" + URLEncoder.encode(input, "utf8"));
            }

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            Log.i("url", url.toString());
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            return resultList;
        } catch (IOException e) {
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            Log.i("Search result", jsonResults.toString());
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("result");
//            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            resultList = new ArrayList<>();
            for (int i = 0; i < predsJsonArray.length(); i++) {
                String placeId = predsJsonArray.getJSONObject(i).getString("placeId");
                String name = predsJsonArray.getJSONObject(i).getString("name");
                String addressLine = predsJsonArray.getJSONObject(i).getString("address_line");
                String city = predsJsonArray.getJSONObject(i).getString("city");
                String state = predsJsonArray.getJSONObject(i).getString("state");
//                String[] parts = description.split(", ");
//                StringBuilder stringBuilder = new StringBuilder();
                SearchLocationModel searchesModel = new SearchLocationModel();
//                for (int j = 0; j < parts.length; j++) {
//                    if (j == 2) {
//                        stringBuilder.append(parts[j]);
//                    }
//                    if (j > 2) {
//                        stringBuilder.append(", " + parts[j]);
//                    }
//                }

//                if (isSearchByName){
                    searchesModel.setAddress1(name + ", " + addressLine);
//                }else {
//                    searchesModel.setAddress1(addressLine);
//                }
                searchesModel.setAddress2(city + ", " + state);
                searchesModel.setLatitude(predsJsonArray.getJSONObject(i).getDouble("latitude"));
                searchesModel.setLongitude(predsJsonArray.getJSONObject(i).getDouble("longitude"));
                searchesModel.setPlaceId(placeId);
                resultList.add(searchesModel);
            }
        } catch (JSONException e) {

        }

        return resultList;
    }


    class GooglePlacesAutocompleteAdapter extends ArrayAdapter implements Filterable {

        public GooglePlacesAutocompleteAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        @Override
        public int getCount() {
            return resultList.size();
        }

        @Override
        public Object getItem(int index) {
            return resultList.get(index);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            MyViewHolder mViewHolder;

            if (convertView == null) {
                LayoutInflater inflater = getLayoutInflater();
                convertView = inflater.inflate(R.layout.single_search_location_row, parent, false);
                mViewHolder = new MyViewHolder(convertView);
                convertView.setTag(mViewHolder);
            } else {
                mViewHolder = (MyViewHolder) convertView.getTag();
            }

            if (resultList != null && resultList.size() > 0) {
                SearchLocationModel model = resultList.get(position);
                mViewHolder.tvTitle.setText(model.getAddress1());
                mViewHolder.tvDesc.setText(model.getAddress2());
            }

            return convertView;
        }

        private class MyViewHolder {
            TextView tvTitle, tvDesc;


            public MyViewHolder(View item) {

                tvTitle = (TextView) item.findViewById(R.id.address1);
                tvDesc = (TextView) item.findViewById(R.id.place1);

            }
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        resultList = autocomplete(constraint.toString());
                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null /*&& results.count > 0*/) {
                        notifyDataSetChanged();
//                    } else {
//                        notifyDataSetInvalidated();
                    }
                }
            };
            return filter;
        }
    }

    private ArrayList<SearchLocationModel> removeDuplicate(ArrayList<SearchLocationModel> searchLocationModels){
        List<SearchLocationModel> uniqueList = new ArrayList<>();
        for(int i=0; i<searchLocationModels.size()-1; i++){
            if (!searchLocationModels.get(i).getPlaceId().equalsIgnoreCase(searchLocationModels.get(i+1).getPlaceId())){
                uniqueList.add(searchLocationModels.get(i));
            }
        }
        return (ArrayList<SearchLocationModel>) uniqueList;
    }

    public void delayTime() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                GPSTracker gpsTracker = new GPSTracker(SearchLocationActivity.this);
                if (gpsTracker.canGetLocation()) {
                    finish();
                }
            }
        }, 3000);
    }

}
