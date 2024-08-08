
package com.uncc.inclass14;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.util.List;

public class HomePlaceAdapter extends  RecyclerView.Adapter<HomePlaceAdapter.HomePlaceViewHolder>{


    public static HomePlaceAdapter.InteractWithHomePlaceActivity interact;

    static String ADD_PLACE = "addPlace";

    private List<Place> placeArrayList;

    static String TAG = "demo";

    private Context context;

    public HomePlaceAdapter(List<Place> placeArrayList, Context context) {
        this.placeArrayList = placeArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public HomePlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.homeplacerow, parent, false);
        return new HomePlaceViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull HomePlaceViewHolder holder, final int position) {

        interact = (InteractWithHomePlaceActivity) context;

        final Place placeObj = placeArrayList.get(position);
        holder.placeTitle.setText(placeObj.name);
        new PlaceAdapter.DownloadImageTask((ImageView) holder.placeIconImageView)
                .execute(placeObj.imageUrl);
        holder.placeDeleteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Place placeObj = placeArrayList.get(position);
                interact.deleteItem(position, placeObj);
                Intent toMainActivity = new Intent(v.getContext(), MainActivity.class);
                toMainActivity.putExtra(ADD_PLACE, (Serializable) placeObj);
                context.startActivity(toMainActivity);


            }
        });
    }

    @Override
    public int getItemCount() {
        return placeArrayList.size();
    }


    public class HomePlaceViewHolder extends RecyclerView.ViewHolder {
        public TextView placeTitle;
        public ImageView placeIconImageView, placeDeleteImageView;
        public HomePlaceViewHolder(@NonNull View itemView) {
            super(itemView);
            placeTitle = itemView.findViewById(R.id.homePlaceTitle);
            placeIconImageView = itemView.findViewById(R.id.homePlaceIconImageView);
            placeDeleteImageView = itemView.findViewById(R.id.placeDeleteImageView);
        }
    }

    public interface InteractWithHomePlaceActivity {
        void deleteItem(int position, Place selectedPlace);

    }
}
