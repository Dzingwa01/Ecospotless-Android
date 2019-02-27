package com.marshteq.ecospotless.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.iamhabib.easy_preference.EasyPreference;
import com.marshteq.ecospotless.CarValet.CarValetMainActivity;
import com.marshteq.ecospotless.Helpers.Credentials;
import com.marshteq.ecospotless.Models.UserPref;
import com.marshteq.ecospotless.Models.WashRequest;
import com.marshteq.ecospotless.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ValetWashRequestAdapter extends RecyclerView.Adapter<ValetWashRequestAdapter.ViewHolder> {
    private final List<WashRequest> washRequests;
    Context context;


    public ValetWashRequestAdapter(List<WashRequest> washRequests, Context context){
        this.washRequests = washRequests;
        this.context = context;
    }

    @Override
    public ValetWashRequestAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.valet_wash_request_row_layout,parent,false);
            return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ValetWashRequestAdapter.ViewHolder holder, int position) {
       WashRequest request = washRequests.get(position);
       if(request.id!="empty"){
           holder.service.setText("Requested Service: "+request.price.service);
           holder.vehicle.setText("Service Description: "+request.price.description);
           holder.wash_date.setText("Wash Date: "+request.wash_date.split(" ")[0] + " at " +request.wash_time);
           holder.status.setText("Status: "+request.status);
           holder.cost.setText("Cost: R"+request.price.price);

           holder.wash_location.setText("Location: "+request.wash_location);
       }else{
           holder.service.setText(request.price.service);
//           holder.vehicle.setText(request.price.vehicle.name);
//           holder.wash_time.setText(request.wash_date);
       }

    }


    @Override
    public int getItemCount() {
        return washRequests.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView service, vehicle, wash_date, wash_location,status,cost;
        public ViewHolder(View itemView) {
            super(itemView);
            wash_date = itemView.findViewById(R.id.wash_date_label);
            wash_location = itemView.findViewById(R.id.wash_location);
            service = itemView.findViewById(R.id.service_label);
            vehicle = itemView.findViewById(R.id.vehicle_label);
            status = itemView.findViewById(R.id.request_status);
            cost = itemView.findViewById(R.id.request_cost);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            final WashRequest order = washRequests.get(getLayoutPosition());
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            alertDialogBuilder.setMessage("Do You Want Accept or Decline Wash Request");
            alertDialogBuilder.setPositiveButton("Accept",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
//                                    Toast.makeText(context,"You clicked accept button",Toast.LENGTH_LONG).show();
                            AcceptWashRequest(order);
                        }
                    });

            alertDialogBuilder.setNegativeButton("Decline",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
//                    declineOrder(order);
//                    Toast.makeText(context,"You clicked decline button",Toast.LENGTH_LONG).show();
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }

    public void AcceptWashRequest(final WashRequest order){

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        Credentials credentials = EasyPreference.with(context).getObject("server_details", Credentials.class);
        UserPref pref = EasyPreference.with(context).getObject("user_pref", UserPref.class);
        final String url = credentials.server_url;
        String URL = url+"api/accept-request/"+pref.id+"/"+order.id;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String response_obj = null;
                try {
                    response_obj = response.getString("message");
                    Toast.makeText(context,response_obj, Toast.LENGTH_LONG).show();
                    washRequests.remove(order);
                    notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("error", error.toString());
            }
        });
        requestQueue.add(request);

    }
}
