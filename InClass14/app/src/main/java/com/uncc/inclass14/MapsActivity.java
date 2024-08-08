

package com.uncc.inclass14;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private List<Place> placeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        placeList = new ArrayList<Place>();

        if (getIntent() != null && getIntent().getSerializableExtra(MyAdapter.MAP_PLACE) != null) {
            placeList = (List<Place>) getIntent().getSerializableExtra(MyAdapter.MAP_PLACE);
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        if (placeList.size() > 0) {

            for (Place place : placeList) {
                LatLng latLngObj = new LatLng(Double.parseDouble(place.lat), Double.parseDouble(place.lng));
                Marker marker = mMap.addMarker(new MarkerOptions().position(latLngObj).title(place.name));


                builder.include(latLngObj);
            }

            final LatLngBounds latLngBounds = builder.build();


            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    Toast.makeText(MapsActivity.this, "Infowindow clicked", Toast.LENGTH_SHORT).show();
                }
            });

            mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 100));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 100));

                    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {
                            String title = (String) (marker.getTitle());
                            marker.showInfoWindow();
                            marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                            return true;
                        }
                    });

                }
            });

        }
    }

}
