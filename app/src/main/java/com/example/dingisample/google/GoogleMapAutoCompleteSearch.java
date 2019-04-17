package com.example.dingisample.google;

import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.example.dingisample.google.AddressSearchActivity.hideKeyboard;

public class GoogleMapAutoCompleteSearch extends FragmentActivity implements OnMapReadyCallback {

    boolean isSelected = false;
    List<SearchResult> searchResults;
    AutoCompleteTextView edSearch;
    HashMap<String, String> searchResultsId = new HashMap<>();
    List<String> nameList = new ArrayList<>();
    private GoogleMap mMap;

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
        setContentView(R.layout.activity_google_map_auto_com);

        Toolbar toolbar = findViewById(R.id.toolbar);

        toolbar.setTitle("Google Map Auto Complete Search");
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
        searchResults = new ArrayList<>();
        edSearch = ((AutoCompleteTextView) findViewById(R.id.address));
        edSearch.addTextChangedListener(new TextWatcher() {

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
                if (edSearch.getText().toString().length() > 2) {
                    callApi(edSearch.getText().toString() + "");
                    isSelected = false;
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
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        MyLocation myLocation = new MyLocation(GoogleMapAutoCompleteSearch.this);
        myLocation.setListener(new MyLocation.MyLocationListener() {
            @Override
            public void onLocationFound(Location location) {

                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 18);
                mMap.animateCamera(cameraUpdate);


            }

            @Override
            public void onFailed() {

            }
        });


    }

    private void callApi(String q) {
        VolleyRequest vr = new VolleyRequest(GoogleMapAutoCompleteSearch.this);
        vr.VolleyGet(Api.autoCompleteSearch + "?q=" + q + "/");
        vr.setListener(new VolleyRequest.MyServerListener() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    nameList.clear();
                    searchResults.clear();
                    searchResultsId.clear();

                    JSONArray result = response.getJSONArray("result");
                    for (int i = 0; i < result.length(); i++) {
                        JSONObject obj = result.getJSONObject(i);
                        searchResults.add(new SearchResult(obj.getString("name"), obj.getString("id"), obj.getString("type"), obj.getString("address"), (obj.getJSONArray("location")).getDouble(0), (obj.getJSONArray("location")).getDouble(1)));
                        searchResultsId.put(obj.getString("id"), obj.getString("name"));
                        nameList.add(obj.getString("name"));

                    }

                    ArrayAdapter<String> adapter =
                            new ArrayAdapter<String>(GoogleMapAutoCompleteSearch.this, android.R.layout.simple_list_item_1, nameList);
                    edSearch.setAdapter(adapter);
                    edSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                            String selection = (String) parent.getItemAtPosition(position);
                            String id = getKeysByValue(searchResultsId, selection);
                            isSelected = true;
                            drawMarker(id);
                        }
                    });

                    if (!isSelected) {

                        edSearch.showDropDown();
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


        edSearch.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    edSearch.showDropDown();
                    return true;
                }
                return false;
            }
        });

    }

    private void drawMarker(String id) {

        for (int i = 0; i < searchResults.size(); i++) {
            if (searchResults.get(i).getId().equalsIgnoreCase(id)) {
                LatLng sydney = new LatLng(searchResults.get(i).getLat(), searchResults.get(i).getLng());
                mMap.addMarker(new MarkerOptions().position(sydney).title(searchResults.get(i).getName()));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 18));
                ((TextView) findViewById(R.id.text)).setText(searchResults.get(i).getAddress());
                hideKeyboard(GoogleMapAutoCompleteSearch.this);
            }
        }


    }

}
