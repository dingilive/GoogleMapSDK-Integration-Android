package com.example.dingisample.dingi;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.dingi.dingisdk.Dingi;
import com.dingi.dingisdk.annotations.MarkerOptions;
import com.dingi.dingisdk.annotations.PolylineOptions;
import com.dingi.dingisdk.camera.CameraUpdate;
import com.dingi.dingisdk.camera.CameraUpdateFactory;
import com.dingi.dingisdk.constants.Style;
import com.dingi.dingisdk.geometry.LatLng;
import com.dingi.dingisdk.maps.DingiMap;
import com.dingi.dingisdk.maps.MapView;
import com.dingi.dingisdk.maps.OnMapReadyCallback;
import com.example.dingisample.R;
import com.example.dingisample.utils.Api;
import com.example.dingisample.utils.MyLocation;
import com.example.dingisample.utils.VolleyRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class DingiLandMark extends FragmentActivity implements OnMapReadyCallback {
    ArrayList<LatLng> points;
    private MapView mMap;
    private DingiMap dingiMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Dingi.getInstance(this, "EjFUMTUMKFcnJ2VzRnL39Cd2ixtHScJ2p0C1vhP2");
        setContentView(R.layout.activity_dingi_landmark);
        Toolbar toolbar = findViewById(R.id.toolbar);

        toolbar.setTitle("Dingi Map Reverse Geocoding Landmark");
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mMap = findViewById(R.id.dingi_map);
        mMap.onCreate(savedInstanceState);
        mMap.getMapAsync(this);
        points = new ArrayList<>();

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

    private void callAPI(LatLng latLng) {
        VolleyRequest volleyRequest = new VolleyRequest(DingiLandMark.this);
        volleyRequest.VolleyGet(Api.reverseLandmark + "?lat=" + latLng.getLatitude() + "&lng=" + latLng.getLongitude() + "&language=en");
        volleyRequest.setListener(new VolleyRequest.MyServerListener() {
            @Override
            public void onResponse(JSONObject response) {
                points.clear();
                dingiMap.clear();
                try {

                    try {
                        JSONObject way = response.getJSONObject("way");
                        JSONObject location = way.getJSONObject("location");
                        JSONArray coordinates = location.getJSONArray("coordinates");
                        for (int i = 0; i < coordinates.length(); i++) {

                            JSONArray coo = coordinates.getJSONArray(i);
                            points.add(new LatLng(coo.getDouble(1), coo.getDouble(0)));
                            Log.e("options", coo.getDouble(0) + "");
                        }
                    } catch (Exception e) {
                        Log.e("eee", e.toString());
                    }

                    dingiMap.addPolyline(new PolylineOptions()
                            .addAll(points)
                            .color(Color.parseColor("#9E9E9E"))
                            .width(6));
                    try {
                        JSONObject poi = response.getJSONObject("poi");
                        JSONObject locationPoi = poi.getJSONObject("location");
                        drawMarker(locationPoi.getDouble("lat"), locationPoi.getDouble("lon"), poi.getString("name"), poi.getString("distance"));

                    } catch (Exception e) {
                        Log.e("eee", e.toString());
                    }
                } catch (Exception e) {
                    Log.e("LandMark", e.toString());
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(DingiLandMark.this, error, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void responseCode(int resposeCode) {

            }
        });
    }

    private void drawMarker(double lat, double lon, String name, String distance) {

        LatLng sydney = new LatLng(lat, lon);
        ((EditText) findViewById(R.id.address)).setText(name + " ( " + distance + "m ) ");
        dingiMap.addMarker(new MarkerOptions().position(sydney).title(name + " ( " + distance + " )"));

    }


    @Override
    public void onMapReady(@NonNull DingiMap dingiMa) {
        dingiMa.setStyleUrl(Style.DINGI_ENGLISH);
        dingiMap = dingiMa;
        MyLocation myLocation = new MyLocation(DingiLandMark.this);
        myLocation.setListener(new MyLocation.MyLocationListener() {
            @Override
            public void onLocationFound(Location location) {

                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 18);
                dingiMa.animateCamera(cameraUpdate);
                dingiMap.addOnCameraMoveListener(new DingiMap.OnCameraMoveListener() {
                    @Override
                    public void onCameraMove() {
                        LatLng center = dingiMa.getCameraPosition().target;
                        callAPI(center);
                    }
                });


            }

            @Override
            public void onFailed() {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mMap.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMap.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMap.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMap.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMap.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMap.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMap.onSaveInstanceState(outState);
    }
}
