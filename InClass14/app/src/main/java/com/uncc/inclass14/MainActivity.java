
package com.uncc.inclass14;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements HomePlaceAdapter.InteractWithHomePlaceActivity {

    private static final int REQ_CODE = 9000;
    ImageView addTripImageView;
    private FirebaseFirestore db;
    ArrayList<Trip> tripArrayList;
    RecyclerView tripRecycleView;
    private MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = FirebaseFirestore.getInstance();
        tripRecycleView = findViewById(R.id.tripRecycleView);
        addTripImageView = findViewById(R.id.addTripImageView);

        tripArrayList = new ArrayList<Trip>();

        setTitle("Trips");

        myAdapter = new MyAdapter(tripArrayList, MainActivity.this);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        tripRecycleView.setLayoutManager(layoutManager);
        tripRecycleView.setItemAnimator(new DefaultItemAnimator());
        tripRecycleView.addItemDecoration(new DividerItemDecoration(MainActivity.this, DividerItemDecoration.HORIZONTAL));

        tripRecycleView.setAdapter(myAdapter);
        tripRecycleView.setVisibility(View.VISIBLE);

        addTripImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toMainActivity = new Intent(MainActivity.this, AddTrip.class);
                startActivityForResult(toMainActivity, REQ_CODE);
            }
        });

        db.collection("trips")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Log.d("demo", "onSuccess: " + documentSnapshot.getId());

                            String tripName = (String) documentSnapshot.get("title");
                            String city = (String) documentSnapshot.get("city");
                            String placeId = (String) documentSnapshot.get("placeId");
                            String lat = (String) documentSnapshot.get("lat");
                            String lng = (String) documentSnapshot.get("lng");
                            List<Place> placesArrayList = new ArrayList<>();
                            List<Map> placesList =  (List) documentSnapshot.get("places");

                            for (Map<String, String> placeMap : placesList) {
                                Place placeObj = new Place(placeMap.get("name"), placeMap.get("lat"), placeMap.get("lng"), placeMap.get("imageUrl"), placeMap.get("id"), placeMap.get("tripID"));
                                placesArrayList.add(placeObj);
                            }

                            Trip tripObj = new Trip(tripName, city, placeId, lat, lng, placesArrayList);

                            tripArrayList.add(tripObj);
                        }
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                myAdapter.notifyDataSetChanged();

                            }
                        });
                    }

                });
    }

    @Override
    public void deleteItem(int position, Place selectedPlace) {
        Map<String, Object> placeMap = new HashMap<>();

        placeMap.put("name", selectedPlace.name);
        placeMap.put("lat", selectedPlace.lat);
        placeMap.put("lng",selectedPlace.lng);
        placeMap.put("id", selectedPlace.id);
        placeMap.put("imageUrl", selectedPlace.imageUrl);
        placeMap.put("tripID", selectedPlace.tripID);

        db.collection("trips").document(selectedPlace.tripID)
                .update("places", FieldValue.arrayRemove(placeMap)).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(MainActivity.this, "Deleted place successfully", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("Demo", "Error adding document", e);
                Toast.makeText(MainActivity.this, "Failed to add place", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
