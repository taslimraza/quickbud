package com.app.android.quickbud.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.app.android.quickbud.fragments.FavoriteDetailsFragment;
import com.app.android.quickbud.modelClasses.FavoriteListModel;
import com.app.android.quickbud.network.VolleySingleton;
import com.app.android.quickbud.utils.Config;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mobi11 on 16/11/15.
 */
public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.RecyclerViewHolder>{

    private Context context;
    private ArrayList<FavoriteListModel> orderHistoryModels;
    private ImageView loadingImage;
    private RecyclerView orderHistoryList;

    public OrderHistoryAdapter(Context context, ArrayList<FavoriteListModel> orderHistoryModels,
                               ImageView loadingImage, RecyclerView orderHistoryList) {
        this.context = context;
        this.orderHistoryModels = orderHistoryModels;
        this.loadingImage = loadingImage;
        this.orderHistoryList = orderHistoryList;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.order_history_single,parent,false);

        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        holder.restaurantName.setText(orderHistoryModels.get(position).getLocationName());
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(orderHistoryModels.get(position).getLocationAddress());
        stringBuilder.append(", " + orderHistoryModels.get(position).getLocationCity());
        stringBuilder.append(", " + orderHistoryModels.get(position).getLocationState());
        stringBuilder.append(", " + orderHistoryModels.get(position).getLocationZip());
        holder.restaurantAddress.setText(stringBuilder.toString());
    }

    @Override
    public int getItemCount() {
        return orderHistoryModels.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView restaurantName;
        private TextView restaurantAddress;
        private TextView viewDetails;
        private ImageView favoriteIcon;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            restaurantName = (TextView) itemView.findViewById(R.id.restaurant_name);
            restaurantAddress = (TextView) itemView.findViewById(R.id.restaurant_address);
            viewDetails = (TextView) itemView.findViewById(R.id.order_id);
            favoriteIcon = (ImageView) itemView.findViewById(R.id.favorite_icon);
            viewDetails.setOnClickListener(this);
            favoriteIcon.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id){
                case R.id.order_id:
                    FavoriteDetailsFragment favoriteDetailsFragment = new FavoriteDetailsFragment(orderHistoryModels.get(getLayoutPosition()));
                    ((FragmentActivity) context).getSupportFragmentManager().beginTransaction()
                            .replace(R.id.favorite_content,favoriteDetailsFragment)
                            .addToBackStack(null).commit();
                    break;
                case R.id.favorite_icon:
                    showMessage(orderHistoryModels.get(getLayoutPosition()).getOrderId(), getLayoutPosition(), orderHistoryModels.get(getLayoutPosition()).getTenantId());
                    break;
            }
        }
    }

    private void showMessage(final Integer orderId, final int itemCount, final Integer tenantId){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Are you sure to remove it as your favorite?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteFavoriteOrder(orderId, itemCount, tenantId);
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

    private void deleteFavoriteOrder(Integer orderId, final int itemCount, Integer tenantId){
        final SharedPreferences sharedPreferences = context.getSharedPreferences("user_info", Context.MODE_PRIVATE);
        String patronId = sharedPreferences.getString("patron_id", null);
        orderHistoryList.setVisibility(View.GONE);
        loadingImage.setVisibility(View.VISIBLE);
        String deleteFavoriteURL = "http://159.203.88.161/qt/api/order/deletefavorite/?patron_id="+patronId+"&order_id=" + orderId +
                                        "&tenant_id="+tenantId;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, deleteFavoriteURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        orderHistoryList.setVisibility(View.VISIBLE);
                        loadingImage.setVisibility(View.GONE);
                        orderHistoryModels.remove(itemCount);
                        notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error != null){
                    if (error instanceof TimeoutError) {
                        Config.internetSlowError(context);
                    } else if (error instanceof NoConnectionError) {
                        Config.internetSlowError(context);
                    } else if (error instanceof ServerError) {
                        Config.serverError(context);
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

        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }
}
