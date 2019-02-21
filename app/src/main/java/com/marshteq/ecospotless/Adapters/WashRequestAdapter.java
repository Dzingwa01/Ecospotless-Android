package com.marshteq.ecospotless.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.marshteq.ecospotless.Models.WashRequest;
import com.marshteq.ecospotless.R;

import java.util.List;

public class WashRequestAdapter extends RecyclerView.Adapter<WashRequestAdapter.ViewHolder> {
    private final List<WashRequest> washRequests;
    Context context;


    public WashRequestAdapter(List<WashRequest> washRequests,Context context){
        this.washRequests = washRequests;
        this.context = context;
    }

    @Override
    public WashRequestAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.wash_request_row_layout,parent,false);
            return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(WashRequestAdapter.ViewHolder holder, int position) {
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

        }
    }
}
