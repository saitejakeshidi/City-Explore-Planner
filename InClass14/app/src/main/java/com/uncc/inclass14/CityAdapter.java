

package com.uncc.inclass14;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.CityViewHolder> {

    public static CityAdapter.InteractWithAddTripActivity interact;

    static String ADD_TRIP_PLACEID = "addTripPlaceId";
    static String ADD_TRIP_CITY = "addTripCity";


    private List<City> cityArrayList;

    static String TAG = "demo";

    private Context context;

    public CityAdapter(List<City> cityArrayList, Context context) {
        this.cityArrayList = cityArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public CityAdapter.CityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cityrowlayout, parent, false);
        return new CityViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CityAdapter.CityViewHolder holder, final int position) {
        interact = (InteractWithAddTripActivity) context;

        final City cityObj = cityArrayList.get(position);
        holder.cityRowTextView.setText(cityObj.description);

        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                City cityObj = cityArrayList.get(position);
                Intent toAddTrip = new Intent(v.getContext(), AddTrip.class);
                toAddTrip.putExtra(ADD_TRIP_PLACEID, cityObj.getPlace_id());
                toAddTrip.putExtra(ADD_TRIP_CITY, cityObj.getDescription());

                context.startActivity(toAddTrip);

            }
        });
    }

    @Override
    public int getItemCount() {
        return cityArrayList.size();
    }

    public class CityViewHolder extends RecyclerView.ViewHolder {
        public TextView cityRowTextView;
        public ConstraintLayout constraintLayout;


        public CityViewHolder(@NonNull View itemView) {
            super(itemView);
            cityRowTextView = itemView.findViewById(R.id.cityRowTextView);
            constraintLayout = itemView.findViewById(R.id.constraintLayout);
        }
    }

    public interface InteractWithAddTripActivity {

    }
}
