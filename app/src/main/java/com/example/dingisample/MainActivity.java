package com.example.dingisample;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;

import com.example.dingisample.dingi.DingiLandMark;
import com.example.dingisample.dingi.DingiMapAutoCompleteSearch;
import com.example.dingisample.dingi.DingiMapNavigationDW;
import com.example.dingisample.dingi.DingiReverseGeo;
import com.example.dingisample.google.AddressSearchActivity;
import com.example.dingisample.google.GoogleMapAutoCompleteSearch;
import com.example.dingisample.google.GoogleMapLandMark;
import com.example.dingisample.google.GoogleMapNavigationDW;
import com.example.dingisample.google.GoogleMapReverseGeo;
import com.example.dingisample.utils.PreferenceSaver;

public class MainActivity extends AppCompatActivity {
    PreferenceSaver ps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ps = new PreferenceSaver(MainActivity.this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        askPermission();
        setColor();

        findViewById(R.id.reverse).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ps.isDingi()) {
                    startActivity(new Intent(MainActivity.this, DingiReverseGeo.class));
                } else {
                    startActivity(new Intent(MainActivity.this, GoogleMapReverseGeo.class));
                }
            }
        });


        findViewById(R.id.landmark).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ps.isDingi()) {
                    startActivity(new Intent(MainActivity.this, DingiLandMark.class));
                } else {
                    startActivity(new Intent(MainActivity.this, GoogleMapLandMark.class));
                }


            }
        });
        findViewById(R.id.autocomplete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ps.isDingi()) {
                    startActivity(new Intent(MainActivity.this, DingiMapAutoCompleteSearch.class));
                } else {
                    startActivity(new Intent(MainActivity.this, GoogleMapAutoCompleteSearch.class));
                }
            }
        });

        findViewById(R.id.addresssearch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AddressSearchActivity.class));
            }
        });
        findViewById(R.id.dw).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ps.isDingi()) {
                    startActivity(new Intent(MainActivity.this, DingiMapNavigationDW.class));
                } else {
                    startActivity(new Intent(MainActivity.this, GoogleMapNavigationDW.class));
                }
            }
        });

        findViewById(R.id.dingi).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ps.setDingi(true);
                setColor();
            }
        });
        findViewById(R.id.google).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ps.setDingi(false);
                setColor();
            }
        });

    }

    private void setColor() {
        if (ps.isDingi()) {
            findViewById(R.id.isdingi).setVisibility(View.VISIBLE);
            findViewById(R.id.isgoogle).setVisibility(View.GONE);
        } else {
            findViewById(R.id.isdingi).setVisibility(View.GONE);
            findViewById(R.id.isgoogle).setVisibility(View.VISIBLE);
        }
    }

    private void askPermission() {
        ActivityCompat.requestPermissions(
                MainActivity.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                11
        );
    }

}
