package com.example.dingisample;

import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;

import com.dingi.dingisdk.Dingi;
import com.dingi.dingisdk.camera.CameraUpdate;
import com.dingi.dingisdk.camera.CameraUpdateFactory;
import com.dingi.dingisdk.constants.Style;
import com.dingi.dingisdk.geometry.LatLng;
import com.dingi.dingisdk.maps.DingiMap;
import com.dingi.dingisdk.maps.MapFragment;
import com.dingi.dingisdk.maps.MapView;
import com.dingi.dingisdk.maps.OnMapReadyCallback;
import com.example.dingisample.utils.Api;
import com.example.dingisample.utils.MyLocation;
import com.example.dingisample.utils.VolleyRequest;

import org.json.JSONObject;

public class Listeners extends FragmentActivity implements DingiMap.OnFlingListener,DingiMap.OnMapLongClickListener, DingiMap.OnMapClickListener,DingiMap.OnCompassAnimationListener,DingiMap.OnCameraMoveCanceledListener ,OnMapReadyCallback, MapFragment.OnMapViewReadyCallback {

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
    }


    @Override
    public void onMapReady(@NonNull DingiMap dingiMap) {
        dingiMap.setStyleUrl(Style.DINGI_ENGLISH);
        map = dingiMap;
        MyLocation myLocation = new MyLocation(Listeners.this);
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
        VolleyRequest volleyRequest = new VolleyRequest(Listeners.this);
        volleyRequest.VolleyGet(Api.reverseGeo + "demo?lat=" + latLng.getLatitude() + "&lng=" + latLng.getLongitude() + "&address_level=UPTO_THANA");
        volleyRequest.setListener(new VolleyRequest.MyServerListener() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    ((EditText) findViewById(R.id.address)).setText(response.getJSONObject("result").getString("address"));

                } catch (Exception e) {

                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(Listeners.this, error, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void responseCode(int resposeCode) {

            }
        });
    }


    @Override
    public void onCameraMoveCanceled() {

    }

    @Override
    public void onCompassAnimation() {

    }

    @Override
    public void onCompassAnimationFinished() {

    }

    @Override
    public boolean onMapClick(@NonNull LatLng latLng) {
        return false;
    }

    @Override
    public void onFling() {

    }

    @Override
    public boolean onMapLongClick(@NonNull LatLng latLng) {
        return false;
    }

    @Override
    public void onMapViewReady(MapView mapView) {

    }
}
