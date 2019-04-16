package com.example.dingisample.google;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dingisample.R;
import com.example.dingisample.adapter.SearchAddressAdapter;
import com.example.dingisample.model.SearchAddress;
import com.example.dingisample.utils.Api;
import com.example.dingisample.utils.VolleyRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class AddressSearchActivity extends AppCompatActivity {
    AutoCompleteTextView edRoad, edRegion;
    EditText edHouse;
    List<String> regionList;
    List<String> roadList;
    String way_id = "";
    boolean isRegSelected = false;
    boolean isRoadSelected = false;
    HashMap<String, String> roadListID = new HashMap<String, String>();
    HashMap<String, String> regionListID = new HashMap<String, String>();
    String SelectedRegion = "";
    List<SearchAddress> address = new ArrayList<>();
    String SelectedRoad = "";
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public static <T, E> String getKeysByValue(Map<T, E> map, E value) {

        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey().toString();
            }
        }
        return null;
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_search);
        Toolbar toolbar = findViewById(R.id.toolbar);

        toolbar.setTitle("Address Search");
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        regionList = new ArrayList<>();
        roadList = new ArrayList<>();
        edRegion = findViewById(R.id.region);
        edHouse = findViewById(R.id.house);
        edRoad = findViewById(R.id.road);
        mRecyclerView = findViewById(R.id.recycle);


        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(AddressSearchActivity.this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new SearchAddressAdapter(address, AddressSearchActivity.this);

        mRecyclerView.setHasFixedSize(true);

        // vertical RecyclerView
        // keep movie_list_row.xml width to `match_parent`
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());

        // horizontal RecyclerView
        // keep movie_list_row.xml width to `wrap_content`
        // RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);

        mRecyclerView.setLayoutManager(mLayoutManager);


        // adding custom divider line with padding 16dp
        // recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mRecyclerView.setAdapter(mAdapter);

        edRegion.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                getRegionSuggetions(edRegion.getText().toString());
                isRegSelected = false;
                isRoadSelected = false;

            }
        });
        findViewById(R.id.reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edRegion.setText("");
                edHouse.setText("");
                edRoad.setText("");

            }
        });
        edRoad.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {


                if (SelectedRegion.length() > 1) {

                    getRoads(edRoad.getText().toString());

                } else {
                    edRegion.setError("Select Region !");
                }

            }
        });

        findViewById(R.id.search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    isRegSelected = false;


                    if (SelectedRegion.length() < 1) {
                        edRegion.setError("Select Region !");

                    } else if (SelectedRoad.length() < 1) {
                        edRoad.setError("Select Road !");
                    } else {

                        isRoadSelected = false;
                        address.clear();

                        getSearchResult(edHouse.getText().toString());
                    }
                } catch (Exception e) {

                }


            }
        });


        edHouse.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    getSearchResult(edHouse.getText().toString());
                    hideKeyboard(AddressSearchActivity.this);
                    return true;
                }
                return false;
            }
        });

    }


    private void getRegionSuggetions(String token) {


        VolleyRequest vr = new VolleyRequest(AddressSearchActivity.this);
        vr.VolleyGet(Api.searchRegion + "?query_string=" + token);
        vr.setListener(new VolleyRequest.MyServerListener() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    regionList.clear();
                    regionListID.clear();
                    JSONArray jsonArray = new JSONArray(response.getString("search_result"));
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = new JSONObject(jsonArray.get(i).toString());
                        Log.e("jsonObject", jsonObject.getString("name"));
                        regionList.add(jsonObject.getString("name"));
                        regionListID.put(jsonObject.getString("id"), jsonObject.getString("name"));
                    }

                    ArrayAdapter<String> adapter =
                            new ArrayAdapter<String>(AddressSearchActivity.this, android.R.layout.simple_list_item_1, regionList);
                    edRegion.setAdapter(adapter);
                    edRegion.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                            String selection = (String) parent.getItemAtPosition(position);
                            SelectedRegion = getKeysByValue(regionListID, selection);
                            isRegSelected = true;
                            edRoad.setFocusable(true);
                            edRoad.setClickable(true);

                            Log.e("selection", getKeysByValue(regionListID, selection));
                        }
                    });

                    if (!isRegSelected) {

                        edRegion.showDropDown();
                    }


                } catch (JSONException e) {
                    Log.e("Error", e.toString());
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(AddressSearchActivity.this, error, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void responseCode(int resposeCode) {

            }
        });


    }

    private void getRoads(String token) {

        VolleyRequest vr = new VolleyRequest(AddressSearchActivity.this);
        vr.VolleyArraryGet(Api.searchWay + "?q=" + token + "&region_id=" + SelectedRegion);
        vr.setListenerarray(new VolleyRequest.MyServerListenerArray() {
            @Override
            public void onResponse(JSONArray response) {


                if (!AddressSearchActivity.this.isFinishing() || !AddressSearchActivity.this.isDestroyed()) {

                    Log.e("Response", "" + response.toString());
                    roadList.clear();
                    roadListID.clear();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.get(i).toString());
                            roadList.add(jsonObject.getString("name"));
                            roadListID.put(jsonObject.getString("id"), jsonObject.getString("name"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    ArrayAdapter<String> adapter =
                            new ArrayAdapter<String>(AddressSearchActivity.this, android.R.layout.simple_list_item_1, roadList);
                    edRoad.setAdapter(adapter);


                    edRoad.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                            String selection = (String) parent.getItemAtPosition(position);
                            SelectedRoad = selection;
                            way_id = getKeysByValue(roadListID, selection);

                            try {
                                SelectedRoad = URLEncoder.encode(SelectedRoad, "utf-8");
                                findViewById(R.id.search).setFocusable(true);
                                findViewById(R.id.search).setClickable(true);
                                isRoadSelected = true;
                            } catch (Exception e) {

                                e.printStackTrace();
                            }


                        }
                    });
                    if (!isRoadSelected) {
                        edRoad.showDropDown();
                    }


                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(AddressSearchActivity.this, error, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void responseCode(int resposeCode) {

            }
        });


    }

    private void getSearchResult(String token) {

        VolleyRequest vr = new VolleyRequest(AddressSearchActivity.this);
        vr.VolleyGet(Api.searchResult + "?house_number=" + token + "&street_name=" + SelectedRoad + "&way_id=" + way_id + "&region_id=" + SelectedRegion);
        vr.setListener(new VolleyRequest.MyServerListener() {
            @Override
            public void onResponse(JSONObject response) {
                if (!AddressSearchActivity.this.isFinishing() || !AddressSearchActivity.this.isDestroyed()) {

                    try {
                        JSONArray jsonArray = new JSONArray(response.getString("result"));
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = new JSONObject(jsonArray.get(i).toString());

                            JSONArray loc = new JSONArray(jsonObject.getString("location"));
                            Double lat = (Double) loc.get(0);
                            Double lon = (Double) loc.get(1);

                            address.add(new SearchAddress(jsonObject.getString("id"), jsonObject.getString("name"), jsonObject.getString("address"), "0", lat, lon));
                        }

                        mAdapter.notifyDataSetChanged();
                        hideKeyboard(AddressSearchActivity.this);
                    } catch (Exception e) {
                        Log.e("Error", e.toString());
                    }
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(AddressSearchActivity.this, error, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void responseCode(int resposeCode) {

            }
        });


    }


}