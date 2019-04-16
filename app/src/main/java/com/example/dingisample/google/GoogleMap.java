package com.example.dingisample.google;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.example.dingisample.R;
import com.example.dingisample.utils.MyLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

public class GoogleMap extends AppCompatActivity
        implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        ResultCallback<Status> {

    private MyLocation myLocation;
    private MapFragment mapFragment;
    private com.google.android.gms.maps.GoogleMap map;
    private Location lastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_map);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        myLocation = new MyLocation(GoogleMap.this);
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(com.google.android.gms.maps.GoogleMap googleMap) {
                Log.e("MapReady", "Yes");
                googleMap.getUiSettings().setMapToolbarEnabled(false);
                map = googleMap;
                myLocation.setListener(new MyLocation.MyLocationListener() {
                    @Override
                    public void onLocationFound(Location location) {
                        markerLocation(location);


                    }

                    @Override
                    public void onFailed() {

                    }
                });
            }
        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onResult(@NonNull Status status) {

    }

    @Override
    public void onLocationChanged(Location location) {
        lastLocation = location;
        markerLocation(location);

    }


    private void markerLocation(Location latLng) {
        if (map != null) {
            CameraPosition position = new CameraPosition.Builder()
                    .target(new LatLng(latLng.getLatitude(), latLng.getLongitude())) // Sets the new camera position
                    .zoom(12)  // Rotate the camera
                    .tilt(30) // Set the camera tilt
                    .build(); // Creates a CameraPosition from the builder
            map.animateCamera(CameraUpdateFactory
                    .newCameraPosition(position));
        }
    }

}
