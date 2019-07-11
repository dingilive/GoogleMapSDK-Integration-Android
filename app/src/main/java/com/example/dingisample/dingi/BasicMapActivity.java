package com.example.dingisample.dingi;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.dingi.dingisdk.Dingi;
import com.dingi.dingisdk.constants.Style;
import com.dingi.dingisdk.maps.DingiMap;
import com.dingi.dingisdk.maps.MapView;
import com.example.dingisample.R;

public class BasicMapActivity extends AppCompatActivity {


    private static final String TAG = "BasicMapActivity";
    private MapView mapView;
    private DingiMap map;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Dingi.getInstance(this, "EjFUMTUMKFcnJ2VzRnL39Cd2ixtHScJ2p0C1vhP2");
        setContentView(R.layout.activity_basic_map);
        mapView = findViewById(R.id.dingi_map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(dingiMap -> dingiMap.setStyleUrl(Style.DINGI_ENGLISH));
    }

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
