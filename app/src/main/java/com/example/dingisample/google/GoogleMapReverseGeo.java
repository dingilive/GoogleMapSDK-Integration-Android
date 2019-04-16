package com.example.dingisample.google;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.Toolbar;
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

import org.json.JSONObject;

public class GoogleMapReverseGeo extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_map_reversegeo);
        Toolbar toolbar = findViewById(R.id.toolbar);

        toolbar.setTitle("Google Map Reverse Geocoding");
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
        MyLocation myLocation = new MyLocation(GoogleMapReverseGeo.this);
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
        VolleyRequest volleyRequest = new VolleyRequest(GoogleMapReverseGeo.this);
        volleyRequest.VolleyGet(Api.reverseGeo + "demo?lat=" + latLng.latitude + "&lng=" + latLng.longitude + "&address_level=UPTO_THANA");
        volleyRequest.setListener(new VolleyRequest.MyServerListener() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    ((EditText) findViewById(R.id.address)).setText(response.getString("addr_en"));

                } catch (Exception e) {

                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(GoogleMapReverseGeo.this, error, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void responseCode(int resposeCode) {

            }
        });
    }


}
