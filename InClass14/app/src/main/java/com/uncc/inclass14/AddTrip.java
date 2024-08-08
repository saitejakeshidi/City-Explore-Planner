
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AddTrip extends AppCompatActivity implements CityAdapter.InteractWithAddTripActivity {

    public EditText tripDesTextView, cityTextView;
    Button searchCityButton, addtripButton;
    List<City> cityArrayList;
    String selectedCityPlaceId, selectedCityName, tripDescription;
    RecyclerView cityRecycleView;
    private TextView emptyView;
    private FirebaseFirestore db;
    private static final int REQ_CODE = 9000;
    private CityAdapter cityAdapter;
    SharedPreferences.Editor editor;
    SharedPreferences pref;


    final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip);
        tripDesTextView = findViewById(R.id.tripTxtView);
        cityTextView = findViewById(R.id.cityTextView);
        searchCityButton = findViewById(R.id.searchCityBtn);
        addtripButton = findViewById(R.id.addtripButton);
        cityArrayList = new ArrayList<>();
        emptyView = findViewById(R.id.empty_view);
        cityRecycleView = findViewById(R.id.cityRecyclerView);

        db = FirebaseFirestore.getInstance();

        setTitle("Add Trip");

        cityAdapter = new CityAdapter(cityArrayList, AddTrip.this);

        cityRecycleView.setAdapter(cityAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        cityRecycleView.setLayoutManager(layoutManager);
        cityRecycleView.setItemAnimator(new DefaultItemAnimator());
        cityRecycleView.addItemDecoration(new DividerItemDecoration(AddTrip.this, DividerItemDecoration.VERTICAL));

        pref = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE); // 0 - for private mode
        editor = pref.edit();

        if (cityArrayList.isEmpty()) {
            cityRecycleView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }
        else {
            cityRecycleView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }

        if (getIntent() != null && getIntent().getExtras() != null) {
            String savedtripName = pref.getString("tripDescription", null);
            tripDesTextView.setText(savedtripName);
            selectedCityName = getIntent().getStringExtra(CityAdapter.ADD_TRIP_CITY);
            cityTextView.setText(selectedCityName);
            selectedCityPlaceId = getIntent().getStringExtra(CityAdapter.ADD_TRIP_PLACEID);
            emptyView.setVisibility(View.GONE);
        }

        searchCityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cityArrayList.clear();
                editor.putString("tripDescription",  tripDesTextView.getText().toString());
                editor.commit();
                getCities();
            }
        });

        addtripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String tripName = tripDesTextView.getText().toString();
                String cityName = cityTextView.getText().toString();
                if(tripName.equals("") || tripName == null) {
                    Toast.makeText(AddTrip.this, "Trip name can't be empty", Toast.LENGTH_SHORT).show();
                } else if (cityName.equals("")|| cityName == null ) {
                    Toast.makeText(AddTrip.this, "City name can't be empty", Toast.LENGTH_SHORT).show();
                } else {
                    getCityGeoCordinates();
                }
            }
        });
    }


    public void getCities() {

        final OkHttpClient client = new OkHttpClient();

        String city = cityTextView.getText().toString();

        HttpUrl url = HttpUrl.parse("https://maps.googleapis.com/maps/api/place/autocomplete/json").newBuilder()
                .addQueryParameter("key", getResources().getString(R.string.api_key))
                .addQueryParameter("types", "(cities)")
                .addQueryParameter("input",  city)
                .build();


        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.d(TAG, "onFailure: "+e.getMessage());

            }

            @Override
            public void onResponse(Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) {

                        System.out.println("Failure");

                    } else {

                        JSONObject jsonObject = new JSONObject(responseBody.string());
                        JSONArray predictions = jsonObject.getJSONArray("predictions");

                        for (int i = 0; i < predictions.length(); i++) {
                            JSONObject c = predictions.getJSONObject(i);
                            String cityName = c.getString("description");
                            String placeId = c.getString("place_id");

                            City cityObj = new City();
                            cityObj.description = cityName;
                            cityObj.place_id = placeId;

                            cityArrayList.add(cityObj);
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                cityRecycleView.setVisibility(View.VISIBLE);
                                emptyView.setVisibility(View.GONE);
                                cityAdapter.notifyDataSetChanged();
                            }
                        });

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });
    }

    public void getCityGeoCordinates() {

        final OkHttpClient client = new OkHttpClient();

        HttpUrl url = HttpUrl.parse("https://maps.googleapis.com/maps/api/place/details/json").newBuilder()
                .addQueryParameter("key", getResources().getString(R.string.api_key))
                .addQueryParameter("placeid", selectedCityPlaceId)
                .build();


        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.d(TAG, "onFailure: "+e.getMessage());

            }

            @Override
            public void onResponse(Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) {

                        System.out.println("Failure");

                    } else {

                        JSONObject jsonObject = new JSONObject(responseBody.string());
                        JSONObject resultObject = jsonObject.getJSONObject("result");
                        JSONObject geometryObject = resultObject.getJSONObject("geometry");
                        JSONObject locationObject = geometryObject.getJSONObject("location");
                        String lat = locationObject.getString("lat");
                        String lng = locationObject.getString("lng");

                        addTripDetailsToFirebase(lat, lng);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });
    }

    public void addTripDetailsToFirebase(String lat, String lng) {

        Map<String, Object> trip = new HashMap<>();

        trip.put("title", tripDesTextView.getText().toString());
        trip.put("city",selectedCityName);
        trip.put("placeId",selectedCityPlaceId);
        trip.put("lat", lat);
        trip.put("lng", lng);
        trip.put("places", Arrays.asList());

        db.collection("trips").document(selectedCityPlaceId)
                .set(trip).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(AddTrip.this, "Added trip successfully", Toast.LENGTH_SHORT).show();
                Intent toMainActivity = new Intent(AddTrip.this, MainActivity.class);
                startActivityForResult(toMainActivity, REQ_CODE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("Demo", "Error adding document", e);
                Toast.makeText(AddTrip.this, "Failed to add trip", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
