package com.example.dingisample.dingi;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.dingi.dingisdk.Dingi;
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

import org.json.JSONObject;

public class DingiReverseGeo extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "BasicMapActivity";
    private MapView mMap;
    private DingiMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Dingi.getInstance(this, getString(R.string.dingi_api));

        setContentView(R.layout.activity_dingi_reversegeo);
        Toolbar toolbar = findViewById(R.id.toolbar);

        toolbar.setTitle("Dingi Map Reverse Geocoding");
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
    }


    @Override
    public void onMapReady(@NonNull DingiMap dingiMap) {
        dingiMap.setStyleUrl(Style.DINGI_ENGLISH);
        map = dingiMap;
        MyLocation myLocation = new MyLocation(DingiReverseGeo.this);
        myLocation.setListener(new MyLocation.MyLocationListener() {
            @Override
            public void onLocationFound(Location location) {

                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 18);
                map.animateCamera(cameraUpdate);
                map.addOnCameraMoveListener(new DingiMap.OnCameraMoveListener() {
                    @Override
                    public void onCameraMove() {
                        LatLng center = map.getCameraPosition().target;
                        callAPI(center);
                    }
                });


            }

            @Override
            public void onFailed() {

            }
        });
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

    private void callAPI(LatLng latLng) {
        VolleyRequest volleyRequest = new VolleyRequest(DingiReverseGeo.this);
        volleyRequest.VolleyGet(Api.reverseGeo + "demo?lat=" + latLng.getLatitude() + "&lng=" + latLng.getLongitude() + "&address_level=UPTO_THANA");
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
                Toast.makeText(DingiReverseGeo.this, error, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void responseCode(int resposeCode) {

            }
        });
    }


}
