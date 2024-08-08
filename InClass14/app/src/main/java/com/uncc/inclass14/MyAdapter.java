package com.uncc.inclass14;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    public static final int REQ_CODE = 9000;

    private List<Trip> tripArrayList;

    static String TAG = "demo";

    private Context context;
    public HomePlaceAdapter homePlaceAdapter;


    static String ADD_PLACE_PLACEID = "addPlacelaceId";
    static String ADD_PLACE_LAT = "addPlaceLat";
    static String ADD_PLACE_LNG = "addPlaceLng";
    static String MAP_PLACE = "mapPlace";


    public MyAdapter(List<Trip> tripArrayList, Context context) {
        this.tripArrayList = tripArrayList;
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.triprowlayout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
//        interact = (InteractWithMainActivity) context;

        final Trip tripObj = tripArrayList.get(position);
        holder.tripDesTextView.setText(tripObj.tripName);
        holder.cityTextView.setText(tripObj.cityName);
        homePlaceAdapter = new HomePlaceAdapter(tripObj.placeList, context);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context.getApplicationContext());
        holder.homePlaceRecycleView.setLayoutManager(layoutManager);
        holder.homePlaceRecycleView.setItemAnimator(new DefaultItemAnimator());
        holder.homePlaceRecycleView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL));

        holder.homePlaceRecycleView.setAdapter(homePlaceAdapter);
        holder.homePlaceRecycleView.setVisibility(View.VISIBLE);


        holder.addPlaceImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Trip tripObj = tripArrayList.get(position);
                Intent toAddPlace = new Intent(v.getContext(), AddPlace.class);
                toAddPlace.putExtra(ADD_PLACE_PLACEID, tripObj.getPlaceId());
                toAddPlace.putExtra(ADD_PLACE_LAT, tripObj.getLat());
                toAddPlace.putExtra(ADD_PLACE_LNG, tripObj.getLng());

                context.startActivity(toAddPlace);

            }
        });

        holder.locationImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Trip tripObj = tripArrayList.get(position);
                Intent toMapActivity = new Intent(v.getContext(), MapsActivity.class);
                toMapActivity.putExtra(MAP_PLACE, (Serializable) tripObj.placeList);
                context.startActivity(toMapActivity);
            }
        });

    }

    @Override
    public int getItemCount() {
        return tripArrayList.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tripDesTextView, cityTextView;
        public ImageView locationImageView, addPlaceImageView;
        public RecyclerView homePlaceRecycleView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tripDesTextView = itemView.findViewById(R.id.tripNameTxtView);
            cityTextView = itemView.findViewById(R.id.cityTextView);
            locationImageView = itemView.findViewById(R.id.locationImageView);
            addPlaceImageView = itemView.findViewById(R.id.placeImageView);
            homePlaceRecycleView = itemView.findViewById(R.id.homePlaceRecycleView);
        }
    }



}
