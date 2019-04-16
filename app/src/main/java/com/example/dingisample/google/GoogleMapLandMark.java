package com.example.dingisample.google;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dingisample.R;
import com.example.dingisample.utils.Api;
import com.example.dingisample.utils.MyLocation;
import com.example.dingisample.utils.VolleyRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class GoogleMapLandMark extends FragmentActivity implements OnMapReadyCallback {

    ArrayList<LatLng> points = new ArrayList<>();
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_map_landmark);
        Toolbar toolbar = findViewById(R.id.toolbar);

        toolbar.setTitle("Dingi Map Reverse Geocoding Landmark");
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        MyLocation myLocation = new MyLocation(GoogleMapLandMark.this);
        myLocation.setListener(new MyLocation.MyLocationListener() {
            @Override
            public void onLocationFound(Location location) {

                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 18);
                mMap.animateCamera(cameraUpdate);


                googleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
                    @Override
                    public void onCameraMove() {
                        LatLng center = mMap.getCameraPosition().target;
                        callAPI(center);
                    }
                });
            }

            @Override
            public void onFailed() {

            }
        });


    }

    private void callAPI(LatLng latLng) {
        VolleyRequest volleyRequest = new VolleyRequest(GoogleMapLandMark.this);
        volleyRequest.VolleyGet(Api.reverseLandmark + "?lat=" + latLng.latitude + "&lng=" + latLng.longitude);
        volleyRequest.setListener(new VolleyRequest.MyServerListener() {
            @Override
            public void onResponse(JSONObject response) {
                points.clear();
                mMap.clear();
                try {

                    JSONObject way = response.getJSONObject("way");
                    JSONObject location = way.getJSONObject("location");
                    JSONArray coordinates = location.getJSONArray("coordinates");
                    for (int i = 0; i < coordinates.length(); i++) {

                        JSONArray coo = coordinates.getJSONArray(i);
                        points.add(new LatLng(coo.getDouble(1), coo.getDouble(0)));
                        Log.e("options", coo.getDouble(0) + "");
                    }

                    JSONObject poi = response.getJSONObject("poi");
                    JSONObject locationPoi = poi.getJSONObject("location");
                    drawMarker(locationPoi.getDouble("lat"), locationPoi.getDouble("lon"), poi.getString("name"), poi.getString("distance"));
                    mMap.addPolyline(new PolylineOptions()
                            .addAll(points)
                            .color(Color.parseColor("#9E9E9E"))
                            .width(6));
                } catch (Exception e) {
                    Log.e("LandMark", e.toString());
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(GoogleMapLandMark.this, error, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void responseCode(int resposeCode) {

            }
        });
    }

    private void drawMarker(double lat, double lon, String name, String distance) {

        LatLng sydney = new LatLng(lat, lon);
        ((EditText) findViewById(R.id.address)).setText(name + " ( " + distance + "m ) ");
        mMap.addMarker(new MarkerOptions().position(sydney).title(name + " ( " + distance + " )"));

    }


    // Draw polyline on map
    public void drawPolyLineOnMap(PolylineOptions polylineOptions) {


        Polyline polyline1 = mMap.addPolyline(polylineOptions);


    }

}
