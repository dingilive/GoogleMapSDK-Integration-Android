package com.example.dingisample.google;

import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
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
import android.widget.Toast;

import com.example.dingisample.R;
import com.example.dingisample.model.SearchResult;
import com.example.dingisample.utils.Api;
import com.example.dingisample.utils.MyLocation;
import com.example.dingisample.utils.VolleyRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
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

public class GoogleMapNavigationDW extends FragmentActivity implements OnMapReadyCallback {


    JSONObject geometry;
    boolean isSrsSelected = false;
    AutoCompleteTextView edSrsSearch;
    HashMap<String, String> srsSearchResultsId = new HashMap<>();
    List<String> SrsnameList = new ArrayList<>();
    List<SearchResult> srsSearchResults;
    double slat = 0, slon = 0, dlat = 0, dlon = 0;
    boolean isdrsSelected = false;
    AutoCompleteTextView eddrsSearch;
    HashMap<String, String> drsSearchResultsId = new HashMap<>();
    List<String> drsnameList = new ArrayList<>();
    List<SearchResult> drsSearchResults;
    private SupportMapFragment mapFragment;
    private com.google.android.gms.maps.GoogleMap map;

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
        setContentView(R.layout.activity_google_map_dw);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Toolbar toolbar = findViewById(R.id.toolbar);

        toolbar.setTitle("Google Map Navigation");
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

    }

    private void callApiD(String q) {
        VolleyRequest vr = new VolleyRequest(GoogleMapNavigationDW.this);
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

                    ArrayAdapter<String> adapter =
                            new ArrayAdapter<String>(GoogleMapNavigationDW.this, android.R.layout.simple_list_item_1, drsnameList);
                    eddrsSearch.setAdapter(adapter);
                    eddrsSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                            String selection = (String) parent.getItemAtPosition(position);
                            String id = getKeysByValue(drsSearchResultsId, selection);
                            isdrsSelected = true;
                            drawMarkerD(id);

                        }
                    });

                    if (!isdrsSelected) {

                        eddrsSearch.showDropDown();
                    }
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
                if (slon < 1) {

                    Toast.makeText(GoogleMapNavigationDW.this, "Select your location !", Toast.LENGTH_SHORT).show();
                } else if (dlat < 1) {
                    Toast.makeText(GoogleMapNavigationDW.this, "Select your destination !", Toast.LENGTH_SHORT).show();
                } else {
                    getDriving();
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

        findViewById(R.id.driving_mode_train).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                ((CardView) findViewById(R.id.walkselected)).setCardBackgroundColor(getResources().getColor(R.color.colorAccent));
                ((CardView) findViewById(R.id.driveselected)).setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));

                if (slon < 1) {

                    Toast.makeText(GoogleMapNavigationDW.this, "Select your location !", Toast.LENGTH_SHORT).show();
                } else if (dlat < 1) {
                    Toast.makeText(GoogleMapNavigationDW.this, "Select your destination !", Toast.LENGTH_SHORT).show();
                } else {
                    VolleyRequest vr = new VolleyRequest(GoogleMapNavigationDW.this);

                    String q = slon + "a" + slat + "b" + dlon + "a" + dlat;
                    vr.VolleyGet(Api.navWalkingResult + "?steps=false&criteria=both&coordinates=" + q + "&language=en");
                    vr.setListener(new VolleyRequest.MyServerListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            map.clear();
                            drawAllMarkers();
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
                                        map.addPolyline(new PolylineOptions()
                                                .addAll(points)
                                                .color(Color.parseColor("#008577"))
                                                .width(10));
                                    } else {
                                        map.addPolyline(new PolylineOptions()
                                                .addAll(points)
                                                .color(Color.parseColor("#D81B60"))
                                                .width(10));
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
            }
        });


    }

    private void getDriving() {

        ((CardView) findViewById(R.id.walkselected)).setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));
        ((CardView) findViewById(R.id.driveselected)).setCardBackgroundColor(getResources().getColor(R.color.colorAccent));

        VolleyRequest vr = new VolleyRequest(GoogleMapNavigationDW.this);

        String q = slon + "a" + slat + "b" + dlon + "a" + dlat;
        vr.VolleyGet(Api.navDrivingResult + "?steps=false&criteria=both&coordinates=" + q + "&language=en");
        vr.setListener(new VolleyRequest.MyServerListener() {
            @Override
            public void onResponse(JSONObject response) {
                map.clear();
                drawAllMarkers();
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

                        if (i == result.length() - 1) {
                            map.addPolyline(new PolylineOptions()
                                    .addAll(points)
                                    .color(Color.parseColor("#008577"))
                                    .width(10));
                        } else {
                            map.addPolyline(new PolylineOptions()
                                    .addAll(points)
                                    .color(Color.parseColor("#9E9E9E"))
                                    .width(10));
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

    private void callApi(String q) {
        VolleyRequest vr = new VolleyRequest(GoogleMapNavigationDW.this);
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

                    ArrayAdapter<String> adapter =
                            new ArrayAdapter<String>(GoogleMapNavigationDW.this, android.R.layout.simple_list_item_1, SrsnameList);
                    edSrsSearch.setAdapter(adapter);
                    edSrsSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                            String selection = (String) parent.getItemAtPosition(position);
                            String id = getKeysByValue(srsSearchResultsId, selection);
                            isSrsSelected = true;
                            drawMarker(id);
                        }
                    });
                    if (!isSrsSelected) {

                        edSrsSearch.showDropDown();
                    }
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
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
                for (int i = 0; i < srsSearchResults.size(); i++) {
                    if (srsSearchResults.get(i).getId().equalsIgnoreCase(id)) {
                        LatLng sydney = new LatLng(srsSearchResults.get(i).getLat(), srsSearchResults.get(i).getLng());
                        slat = srsSearchResults.get(i).getLat();
                        slon = srsSearchResults.get(i).getLng();
                        map.addMarker(new MarkerOptions().position(sydney).title(srsSearchResults.get(i).getName()));
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 18));
                        hideKeyboard(GoogleMapNavigationDW.this);
                    }
                }
            }
        });


    }

    private void drawAllMarkers() {

        map.addMarker(new MarkerOptions().position(new LatLng(slat, slon)).title("").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
        map.addMarker(new MarkerOptions().position(new LatLng(dlat, dlon)).title("").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));


    }

    private void drawMarkerD(String id) {
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                for (int i = 0; i < drsSearchResults.size(); i++) {
                    if (drsSearchResults.get(i).getId().equalsIgnoreCase(id)) {
                        LatLng sydney = new LatLng(drsSearchResults.get(i).getLat(), drsSearchResults.get(i).getLng());
                        dlat = drsSearchResults.get(i).getLat();
                        dlon = drsSearchResults.get(i).getLng();
                        googleMap.addMarker(new MarkerOptions().position(sydney).title(drsSearchResults.get(i).getName()));
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 18));
                        getDriving();
                        hideKeyboard(GoogleMapNavigationDW.this);
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


    private void manageCamera() {


        try {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(new LatLng(slat, slon));
            builder.include(new LatLng(dlat, dlon));

            LatLngBounds bounds = builder.build();
            int padding = 200; // offset from edges of the map in pixels

            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            map.animateCamera(cu);
        } catch (Exception e) {

        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        MyLocation myLocation = new MyLocation(GoogleMapNavigationDW.this);
        myLocation.setListener(new MyLocation.MyLocationListener() {
            @Override
            public void onLocationFound(Location location) {
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 18);
                map.animateCamera(cameraUpdate);

                slat = location.getLatitude();
                slon = location.getLongitude();

            }

            @Override
            public void onFailed() {

            }
        });
    }


}
