
package com.uncc.inclass14;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddPlace extends AppCompatActivity implements PlaceAdapter.InteractWithAddPlaceActivity {

    private RecyclerView placeRecyclerView;

    private static final int REQ_CODE = 9000;
    private FirebaseFirestore db;
    ArrayList<Place> placeArrayList;
    private PlaceAdapter placeAdapter;
    String placeId, lat, lng;
    Place selectedPlaceObj;
    SharedPreferences.Editor editor;
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_place);
        placeRecyclerView = findViewById(R.id.placeRecyclerView);

        placeArrayList = new ArrayList<Place>();
        placeAdapter = new PlaceAdapter(placeArrayList, AddPlace.this);
        db = FirebaseFirestore.getInstance();

        setTitle("Add Places");
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        placeRecyclerView.setLayoutManager(layoutManager);
        placeRecyclerView.setItemAnimator(new DefaultItemAnimator());
        placeRecyclerView.addItemDecoration(new DividerItemDecoration(AddPlace.this, DividerItemDecoration.HORIZONTAL));

        placeRecyclerView.setAdapter(placeAdapter);
        placeRecyclerView.setVisibility(View.VISIBLE);

        pref = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE); // 0 - for private mode
        editor = pref.edit();

        if (getIntent() != null && getIntent().getExtras() != null) {
            String isFromPlaceAdapter = getIntent().getStringExtra(PlaceAdapter.PLACE_ADAPTER);
            if(isFromPlaceAdapter != null ) {
                selectedPlaceObj = (Place) getIntent().getSerializableExtra(PlaceAdapter.ADD_PLACE);
                addSelectedPlaceToFirebase();
            } else {
                placeId = getIntent().getStringExtra(MyAdapter.ADD_PLACE_PLACEID);
                lat = getIntent().getStringExtra(MyAdapter.ADD_PLACE_LAT);
                lng = getIntent().getStringExtra(MyAdapter.ADD_PLACE_LNG);
                editor.putString("placeID",  placeId);
                editor.commit();
                getPlaces();
            }
        }

    }

    public void getPlaces() {

        final OkHttpClient client = new OkHttpClient();

        HttpUrl url = HttpUrl.parse("https://maps.googleapis.com/maps/api/place/nearbysearch/json").newBuilder()
                .addQueryParameter("key", getResources().getString(R.string.api_key))
                .addQueryParameter("location", lat + "," + lng)
                .addQueryParameter("radius", "1000")
                .build();


        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.d("Demo", "onFailure: "+e.getMessage());

            }

            @Override
            public void onResponse(Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) {

                        System.out.println("Failure");

                    } else {

                        JSONObject jsonObject = new JSONObject(responseBody.string());
                        JSONArray resultsArray = jsonObject.getJSONArray("results");
                        for (int i = 0; i < resultsArray.length(); i++) {
                            JSONObject c = resultsArray.getJSONObject(i);
                            String name = c.getString("name");
                            String indPlaceID = c.getString("id");
                            String icon = c.getString("icon");

                            JSONObject geometryObject = c.getJSONObject("geometry");

                            JSONObject locationObject = geometryObject.getJSONObject("location");
                            String lat = locationObject.getString("lat");
                            String lng = locationObject.getString("lng");

                            Place placeObj = new Place(name, lat, lng, icon, indPlaceID, placeId);

                            placeArrayList.add(placeObj);
                        }
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                placeAdapter.notifyDataSetChanged();

                            }
                        });

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });
    }

    public void addSelectedPlaceToFirebase() {
        Map<String, Object> placeMap = new HashMap<>();
        placeId = pref.getString("placeID", null);

        placeMap.put("name", selectedPlaceObj.name);
        placeMap.put("lat", selectedPlaceObj.lat);
        placeMap.put("lng",selectedPlaceObj.lng);
        placeMap.put("id", selectedPlaceObj.id);
        placeMap.put("imageUrl", selectedPlaceObj.imageUrl);
        placeMap.put("tripID", placeId);

        db.collection("trips").document(placeId)
                .update("places", FieldValue.arrayUnion(placeMap)).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(AddPlace.this, "Added place successfully", Toast.LENGTH_SHORT).show();
                Intent toMainActivity = new Intent(AddPlace.this, MainActivity.class);
                startActivityForResult(toMainActivity, REQ_CODE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("Demo", "Error adding document", e);
                Toast.makeText(AddPlace.this, "Failed to add place", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
