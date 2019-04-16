package com.example.dingisample.dingi;

import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import com.dingi.dingisdk.Dingi;
import com.dingi.dingisdk.annotations.Marker;
import com.dingi.dingisdk.annotations.MarkerOptions;
import com.dingi.dingisdk.annotations.PolylineOptions;
import com.dingi.dingisdk.camera.CameraUpdate;
import com.dingi.dingisdk.camera.CameraUpdateFactory;
import com.dingi.dingisdk.constants.Style;
import com.dingi.dingisdk.geometry.LatLng;
import com.dingi.dingisdk.geometry.LatLngBounds;
import com.dingi.dingisdk.maps.DingiMap;
import com.dingi.dingisdk.maps.MapView;
import com.dingi.dingisdk.maps.OnMapReadyCallback;
import com.example.dingisample.R;
import com.example.dingisample.model.SearchResult;
import com.example.dingisample.utils.Api;
import com.example.dingisample.utils.MyLocation;
import com.example.dingisample.utils.VolleyRequest;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.utils.PolylineUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.example.dingisample.google.AddressSearchActivity.hideKeyboard;

public class DingiMapNavigationDW extends FragmentActivity implements OnMapReadyCallback {


    JSONObject geometry;
    boolean isSrsSelected = false;
    AutoCompleteTextView edSrsSearch;
    HashMap<String, String> srsSearchResultsId = new HashMap<>();
    List<String> SrsnameList = new ArrayList<>();
    List<SearchResult> srsSearchResults;
    double slat, slon, dlat, dlon;
    boolean isdrsSelected = false;
    AutoCompleteTextView eddrsSearch;
    HashMap<String, String> drsSearchResultsId = new HashMap<>();
    List<String> drsnameList = new ArrayList<>();
    List<SearchResult> drsSearchResults;
    private MapView mapView;
    private DingiMap mMap;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static <T, E> String getKeysByValue(Map<T, E> map, E value) {

        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey().toString();
            }
        }
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Dingi.getInstance(this, "EjFUMTUMKFcnJ2VzRnL39Cd2ixtHScJ2p0C1vhP2");
        setContentView(R.layout.dingi_map_dw);
        mapView = findViewById(R.id.dingi_map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(dingiMap -> dingiMap.setStyleUrl(Style.DINGI_ENGLISH));


        Toolbar toolbar = findViewById(R.id.toolbar);

        toolbar.setTitle("Dingi Map Navigation");
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        srsSearchResults = new ArrayList<>();
        edSrsSearch = ((AutoCompleteTextView) findViewById(R.id.map_dir_src));
        edSrsSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (edSrsSearch.getText().toString().length() > 2) {
                    callApi(edSrsSearch.getText().toString() + "");
                    isSrsSelected = false;
                }

            }
        });


        drsSearchResults = new ArrayList<>();
        eddrsSearch = ((AutoCompleteTextView) findViewById(R.id.map_dir_dest));
        eddrsSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (eddrsSearch.getText().toString().length() > 2) {
                    callApiD(eddrsSearch.getText().toString() + "");
                    isdrsSelected = false;
                }

            }
        });


        ((AutoCompleteTextView) findViewById(R.id.map_dir_dest)).setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    eddrsSearch.showDropDown();
                    return true;
                }
                return false;
            }
        });

        ((AutoCompleteTextView) findViewById(R.id.map_dir_src)).setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    eddrsSearch.showDropDown();
                    return true;
                }
                return false;
            }
        });
    }

    private void callApiD(String q) {
        VolleyRequest vr = new VolleyRequest(DingiMapNavigationDW.this);
        vr.VolleyGet(Api.autoCompleteSearch + "?q=" + q + "/");
        vr.setListener(new VolleyRequest.MyServerListener() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    drsnameList.clear();
                    drsSearchResults.clear();
                    drsSearchResultsId.clear();

                    JSONArray result = response.getJSONArray("result");
                    for (int i = 0; i < result.length(); i++) {
                        JSONObject obj = result.getJSONObject(i);
                        drsSearchResults.add(new SearchResult(obj.getString("name"), obj.getString("id"), obj.getString("type"), obj.getString("address"), (obj.getJSONArray("location")).getDouble(0), (obj.getJSONArray("location")).getDouble(1)));
                        drsSearchResultsId.put(obj.getString("id"), obj.getString("name"));
                        drsnameList.add(obj.getString("name"));

                    }
                    if (!isdrsSelected) {

                        eddrsSearch.showDropDown();
                    }
                    ArrayAdapter<String> adapter =
                            new ArrayAdapter<String>(DingiMapNavigationDW.this, android.R.layout.simple_list_item_1, drsnameList);
                    eddrsSearch.setAdapter(adapter);
                    eddrsSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                            String selection = (String) parent.getItemAtPosition(position);
                            String id = getKeysByValue(drsSearchResultsId, selection);
                            isdrsSelected = true;
                            drawMarkerD(id);
                        }
                    });
                } catch (
                        Exception w) {

                }
            }

            @Override
            public void onError(String error) {

            }

            @Override
            public void responseCode(int resposeCode) {

            }
        });


        findViewById(R.id.driving_mode_intercity_bus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDriving();
            }
        });


        findViewById(R.id.driving_mode_train).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VolleyRequest vr = new VolleyRequest(DingiMapNavigationDW.this);

                String q = slon + "a" + slat + "b" + dlon + "a" + dlat;
                vr.VolleyGet(Api.navWalkingResult + "?steps=false&criteria=both&coordinates=" + q + "&language=en");
                vr.setListener(new VolleyRequest.MyServerListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONArray result = response.getJSONArray("routes");
                            for (int i = 0; i < result.length(); i++) {
                                JSONObject routeObj = result.getJSONObject(i);
                                String geometryString = routeObj.getString("geometry");
                                List<Point> pointList = PolylineUtils.decode(geometryString, 6);
                                ArrayList<LatLng> points = new ArrayList<>();
                                for (int ss = 0; ss < pointList.size(); ss++) {
                                    points.add(new LatLng(pointList.get(ss).latitude(), pointList.get(ss).longitude()));
                                }

                                if (i == 0) {
                                    mMap.addPolyline(new PolylineOptions()
                                            .addAll(points)
                                            .color(Color.parseColor("#008577"))
                                            .width(6));
                                } else {
                                    mMap.addPolyline(new PolylineOptions()
                                            .addAll(points)
                                            .color(Color.parseColor("#D81B60"))
                                            .width(6));
                                }
                                manageCamera();
                            }

                        } catch (Exception e) {
                            Log.e("EE", e.toString());
                        }
                    }

                    @Override
                    public void onError(String error) {

                    }

                    @Override
                    public void responseCode(int resposeCode) {

                    }
                });
            }
        });

        ((AutoCompleteTextView) findViewById(R.id.map_dir_dest)).setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    eddrsSearch.showDropDown();
                    return true;
                }
                return false;
            }
        });

        ((AutoCompleteTextView) findViewById(R.id.map_dir_src)).setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    eddrsSearch.showDropDown();
                    return true;
                }
                return false;
            }
        });
    }

    private void getDriving() {
        VolleyRequest vr = new VolleyRequest(DingiMapNavigationDW.this);

        String q = slon + "a" + slat + "b" + dlon + "a" + dlat;
        vr.VolleyGet(Api.navDrivingResult + "?steps=false&criteria=both&coordinates=" + q + "&language=en");
        vr.setListener(new VolleyRequest.MyServerListener() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONArray result = response.getJSONArray("routes");
                    for (int i = 0; i < result.length(); i++) {
                        JSONObject routeObj = result.getJSONObject(i);
                        String geometryString = routeObj.getString("geometry");
                        List<Point> pointList = PolylineUtils.decode(geometryString, 6);
                        ArrayList<LatLng> points = new ArrayList<>();
                        for (int ss = 0; ss < pointList.size(); ss++) {
                            points.add(new LatLng(pointList.get(ss).latitude(), pointList.get(ss).longitude()));
                        }

                        if (i == 0) {
                            mMap.addPolyline(new PolylineOptions()
                                    .addAll(points)
                                    .color(Color.parseColor("#008577"))
                                    .width(6));
                        } else {
                            mMap.addPolyline(new PolylineOptions()
                                    .addAll(points)
                                    .color(Color.parseColor("#9E9E9E"))
                                    .width(6));
                        }
                    }
                    manageCamera();

                } catch (Exception e) {
                    Log.e("EE", e.toString());
                }
            }

            @Override
            public void onError(String error) {

            }

            @Override
            public void responseCode(int resposeCode) {

            }
        });
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

    private void callApi(String q) {
        VolleyRequest vr = new VolleyRequest(DingiMapNavigationDW.this);
        vr.VolleyGet(Api.autoCompleteSearch + "?q=" + q + "/");
        vr.setListener(new VolleyRequest.MyServerListener() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    SrsnameList.clear();
                    srsSearchResults.clear();
                    srsSearchResultsId.clear();

                    JSONArray result = response.getJSONArray("result");
                    for (int i = 0; i < result.length(); i++) {
                        JSONObject obj = result.getJSONObject(i);
                        srsSearchResults.add(new SearchResult(obj.getString("name"), obj.getString("id"), obj.getString("type"), obj.getString("address"), (obj.getJSONArray("location")).getDouble(0), (obj.getJSONArray("location")).getDouble(1)));
                        srsSearchResultsId.put(obj.getString("id"), obj.getString("name"));
                        SrsnameList.add(obj.getString("name"));

                    }
                    if (!isSrsSelected) {

                        edSrsSearch.showDropDown();
                    }
                    ArrayAdapter<String> adapter =
                            new ArrayAdapter<String>(DingiMapNavigationDW.this, android.R.layout.simple_list_item_1, SrsnameList);
                    edSrsSearch.setAdapter(adapter);
                    edSrsSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                            String selection = (String) parent.getItemAtPosition(position);
                            String id = getKeysByValue(srsSearchResultsId, selection);
                            isSrsSelected = true;
                            drawMarker(id);
                        }
                    });
                } catch (
                        Exception w) {

                }
            }

            @Override
            public void onError(String error) {

            }

            @Override
            public void responseCode(int resposeCode) {

            }
        });

    }

    private void drawMarker(String id) {
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull DingiMap dingiMap) {
                mMap = dingiMap;
                for (int i = 0; i < srsSearchResults.size(); i++) {
                    if (srsSearchResults.get(i).getId().equalsIgnoreCase(id)) {
                        LatLng sydney = new LatLng(srsSearchResults.get(i).getLat(), srsSearchResults.get(i).getLng());
                        slat = srsSearchResults.get(i).getLat();
                        slon = srsSearchResults.get(i).getLng();
                        mMap.addMarker(new MarkerOptions().position(sydney).title(srsSearchResults.get(i).getName()));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 18));
                        hideKeyboard(DingiMapNavigationDW.this);
                    }
                }
            }
        });


    }

    private void drawMarkerD(String id) {
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull DingiMap dingiMap) {
                mMap = dingiMap;
                for (int i = 0; i < drsSearchResults.size(); i++) {
                    if (drsSearchResults.get(i).getId().equalsIgnoreCase(id)) {
                        LatLng sydney = new LatLng(drsSearchResults.get(i).getLat(), drsSearchResults.get(i).getLng());
                        dlat = drsSearchResults.get(i).getLat();
                        dlon = drsSearchResults.get(i).getLng();
                        mMap.addMarker(new MarkerOptions().position(sydney).title(drsSearchResults.get(i).getName()));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 18));
                        hideKeyboard(DingiMapNavigationDW.this);

                        getDriving();
                    }
                }
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
    public void onMapReady(@NonNull DingiMap dingiMap) {
        mMap = dingiMap;
        MyLocation myLocation = new MyLocation(DingiMapNavigationDW.this);
        myLocation.setListener(new MyLocation.MyLocationListener() {
            @Override
            public void onLocationFound(Location location) {

                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 18);
                mMap.animateCamera(cameraUpdate);

                slon = location.getLongitude();
                slat = location.getLatitude();
            }

            @Override
            public void onFailed() {

            }
        });
    }

    private void manageCamera() {
        try {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            List<com.dingi.dingisdk.annotations.Marker> markers = mMap.getMarkers();
            for (Marker marker : markers) {
                builder.include(marker.getPosition());
            }
            LatLngBounds bounds = builder.build();
            int padding = 200; // offset from edges of the map in pixels

            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            mMap.animateCamera(cu);
        } catch (Exception e) {

        }
    }
}
