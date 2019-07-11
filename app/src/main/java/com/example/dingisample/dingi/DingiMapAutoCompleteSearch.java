package com.example.dingisample.dingi;

import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;
import androidx.appcompat.widget.Toolbar;
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

import com.dingi.dingisdk.Dingi;
import com.dingi.dingisdk.annotations.MarkerOptions;
import com.dingi.dingisdk.camera.CameraUpdate;
import com.dingi.dingisdk.camera.CameraUpdateFactory;
import com.dingi.dingisdk.constants.Style;
import com.dingi.dingisdk.geometry.LatLng;
import com.dingi.dingisdk.maps.DingiMap;
import com.dingi.dingisdk.maps.MapView;
import com.dingi.dingisdk.maps.OnMapReadyCallback;
import com.example.dingisample.R;
import com.example.dingisample.model.SearchResult;
import com.example.dingisample.utils.Api;
import com.example.dingisample.utils.MyLocation;
import com.example.dingisample.utils.VolleyRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.example.dingisample.google.AddressSearchActivity.hideKeyboard;

public class DingiMapAutoCompleteSearch extends FragmentActivity implements OnMapReadyCallback {

    boolean isSelected = false;
    List<SearchResult> searchResults;
    AutoCompleteTextView edSearch;
    HashMap<String, String> searchResultsId = new HashMap<>();
    List<String> nameList = new ArrayList<>();
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

        setContentView(R.layout.activity_dingi_map_auto_com);

        Toolbar toolbar = findViewById(R.id.toolbar);

        toolbar.setTitle("Dingi Map Auto Complete Search");
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        mapView = findViewById(R.id.dingi_map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(dingiMap -> dingiMap.setStyleUrl(Style.DINGI_ENGLISH));
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


    private void callApi(String q) {
        VolleyRequest vr = new VolleyRequest(DingiMapAutoCompleteSearch.this);
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
                            new ArrayAdapter<String>(DingiMapAutoCompleteSearch.this, android.R.layout.simple_list_item_1, nameList);
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
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull DingiMap dingiMap) {
                for (int i = 0; i < searchResults.size(); i++) {
                    if (searchResults.get(i).getId().equalsIgnoreCase(id)) {
                        LatLng sydney = new LatLng(searchResults.get(i).getLat(), searchResults.get(i).getLng());
                        ((TextView) findViewById(R.id.text)).setText(searchResults.get(i).getAddress());
                        hideKeyboard(DingiMapAutoCompleteSearch.this);
                        dingiMap.addMarker(new MarkerOptions().position(sydney).title(searchResults.get(i).getName()));
                        dingiMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 18));


                    }
                }
            }
        });


    }

    @Override
    public void onMapReady(@NonNull DingiMap dingiMap) {
        mMap = dingiMap;

        MyLocation myLocation = new MyLocation(DingiMapAutoCompleteSearch.this);
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
