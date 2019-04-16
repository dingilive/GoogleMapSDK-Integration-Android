package com.example.dingisample.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dingisample.R;
import com.example.dingisample.dingi.DingiMapAddressSearchViewer;
import com.example.dingisample.google.GoogleMapAddressSearchViewer;
import com.example.dingisample.model.SearchAddress;
import com.example.dingisample.utils.PreferenceSaver;

import java.util.List;


public class SearchAddressAdapter extends RecyclerView.Adapter<SearchAddressAdapter.ViewHolder> {

    private List<SearchAddress> addressList;
    private Context context;

    public SearchAddressAdapter(List<SearchAddress> addressList, Context context) {
        this.addressList = addressList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_address_search, parent, false);

        return new ViewHolder(itemView);
    }

    // Replace the addresss of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {


        SearchAddress Review = addressList.get(position);
        holder.roadName.setText(Review.getRoadName());
        holder.address.setText(Review.getAddress());
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferenceSaver ps = new PreferenceSaver(context);
                if (ps.isDingi()) {
                    Intent i = new Intent(context, DingiMapAddressSearchViewer.class);
                    i.putExtra("addressLat", Review.getLat() + "");
                    i.putExtra("addressLng", Review.getLon() + "");
                    i.putExtra("addressName", Review.getRoadName() + "," + Review.getAddress() + "");
                    context.startActivity(i);
                } else {
                    Intent i = new Intent(context, GoogleMapAddressSearchViewer.class);
                    i.putExtra("addressLat", Review.getLat() + "");
                    i.putExtra("addressLng", Review.getLon() + "");
                    i.putExtra("addressName", Review.getRoadName() + "," + Review.getAddress() + "");
                    context.startActivity(i);
                }


            }
        });

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return addressList.size();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case

        private TextView roadName, address;
        private CardView card;

        public ViewHolder(View view) {
            super(view);
            card = view.findViewById(R.id.card);
            roadName = view.findViewById(R.id.name);
            address = view.findViewById(R.id.address);
        }
    }
}
