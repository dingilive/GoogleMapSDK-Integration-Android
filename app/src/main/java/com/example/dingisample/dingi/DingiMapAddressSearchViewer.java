package com.example.dingisample.dingi;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.dingi.dingisdk.Dingi;
import com.dingi.dingisdk.annotations.MarkerOptions;
import com.dingi.dingisdk.camera.CameraUpdateFactory;
import com.dingi.dingisdk.constants.Style;
import com.dingi.dingisdk.geometry.LatLng;
import com.dingi.dingisdk.maps.DingiMap;
import com.dingi.dingisdk.maps.MapView;
import com.dingi.dingisdk.maps.OnMapReadyCallback;
import com.example.dingisample.R;

public class DingiMapAddressSearchViewer extends FragmentActivity {

    private MapView mapView;
    private DingiMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Dingi.getInstance(this, "EjFUMTUMKFcnJ2VzRnL39Cd2ixtHScJ2p0C1vhP2");
        setContentView(R.layout.activity_dingi_map_adress_viewer);
        mapView = findViewById(R.id.dingi_map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull DingiMap dingiMap) {
                dingiMap.setStyleUrl(Style.DINGI_ENGLISH);
                map = dingiMap;
            }
        });
        Toolbar toolbar = findViewById(R.id.toolbar);

        toolbar.setTitle("Dingi Map Address Search");
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Intent i = getIntent();

        Double lat = Double.parseDouble(i.getStringExtra("addressLat"));
        Double lng = Double.parseDouble(i.getStringExtra("addressLng"));
        String Address = i.getStringExtra("addressName");

        showMarker(lat, lng, Address);
    }

    private void showMarker(Double lat, Double lng, String address) {
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull DingiMap dingiMap) {
                LatLng sydney = new LatLng(lat, lng);
                dingiMap.addMarker(new MarkerOptions().position(sydney).title(""));
                dingiMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 18));
                ((TextView) findViewById(R.id.text)).setText(address);
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
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

}
