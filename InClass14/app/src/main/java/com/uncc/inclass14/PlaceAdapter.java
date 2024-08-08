package com.uncc.inclass14;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder> {


    public static PlaceAdapter.InteractWithAddPlaceActivity interact;

    static String ADD_PLACE = "addPlace";
    static String PLACE_ADAPTER = "placeAdapter";


    private List<Place> placeArrayList;

    static String TAG = "demo";

    private Context context;

    public PlaceAdapter(List<Place> placeArrayList, Context context) {
        this.placeArrayList = placeArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.placerowlayout, parent, false);
        return new PlaceViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceAdapter.PlaceViewHolder holder, final int position) {

        final Place placeObj = placeArrayList.get(position);
        holder.placeTextView.setText(placeObj.name);
         new DownloadImageTask((ImageView) holder.placeIconImageView)
                .execute(placeObj.imageUrl);
        holder.addPlaceImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Place placeObj = placeArrayList.get(position);
                Intent toAddPlace = new Intent(v.getContext(), AddPlace.class);
                toAddPlace.putExtra(PLACE_ADAPTER, "PlaceAdapter");
                toAddPlace.putExtra(ADD_PLACE, (Serializable) placeObj);
                context.startActivity(toAddPlace);

            }
        });
    }

    @Override
    public int getItemCount() {
        return placeArrayList.size();
    }

    public class PlaceViewHolder extends RecyclerView.ViewHolder {

        public TextView placeTextView;
        public ImageView placeIconImageView, addPlaceImageView;
        ConstraintLayout placeConstraintLayout;

        public PlaceViewHolder(@NonNull View itemView) {
            super(itemView);
            placeTextView = itemView.findViewById(R.id.placeTextView);
            placeIconImageView = itemView.findViewById(R.id.placeIconImageView);
            addPlaceImageView = itemView.findViewById(R.id.addPlaceImageView);
            placeConstraintLayout = itemView.findViewById(R.id.placeConstraintLayout);
        }
    }

    public interface InteractWithAddPlaceActivity {
    }

    public static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
