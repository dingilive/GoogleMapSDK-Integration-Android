package com.example.dingisample.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import static android.content.Context.LOCATION_SERVICE;

public class MyLocation {

    Locations location;
    private FusedLocationProviderClient mFusedLocationClient;
    private MyLocationListener mlistener;
    private LocationManager locationManager;

    public MyLocation(Context context) {
        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        this.mlistener = null;
        location = new Locations(context);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        } else {

            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {

                        @Override
                        public void onSuccess(Location x) {

                            if (x != null) {
                                if (mlistener != null) {
                                    mlistener.onLocationFound(x);
                                } else {
                                    Log.e("Location", "Success");
                                }

                            } else {
                                if (location.getLatitude() != 0) {
                                    Location l = new Location("");
                                    l.setLatitude(location.getLatitude());
                                    l.setLongitude(location.getLongitude());
                                    if (mlistener != null) {
                                        mlistener.onLocationFound(l);
                                    }
                                } else {
                                    location = new Locations(context);
                                    Toast.makeText(context, "Location not found !", Toast.LENGTH_SHORT).show();
                                }


                            }
                        }
                    });

            mFusedLocationClient.getLastLocation().addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("location", "123123123");
                    if (location.getLatitude() != 0) {
                        Location l = new Location("");
                        l.setLatitude(location.getLatitude());
                        l.setLongitude(location.getLongitude());
                        if (mlistener != null) {
                            mlistener.onLocationFound(l);
                        }
                    } else {
                        location = new Locations(context);
                        mlistener.onFailed();
                    }


                }
            });

        }
    }

    public void setListener(MyLocationListener listener) {
        this.mlistener = listener;
    }

    public FusedLocationProviderClient getFused() {
        return mFusedLocationClient;
    }

    public interface MyLocationListener {
        void onLocationFound(Location location);

        void onFailed();

    }


}
