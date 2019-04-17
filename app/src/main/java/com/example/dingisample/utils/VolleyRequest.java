package com.example.dingisample.utils;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class VolleyRequest {


    public Context mContext;
    RequestQueue queue;
    private MyServerListener listener;
    private MyServerListenerArray listenerarray;

    public VolleyRequest(Context context) {
        mContext = context;
        queue = VolleyController.getInstance(mContext).getRequestQueue();


        this.listener = null;
        this.listenerarray = null;


    }

    public void setListenerarray(MyServerListenerArray listener) {
        this.listenerarray = listener;
    }

    public void setListener(MyServerListener listener) {
        this.listener = listener;
    }


    public void VolleyGet(String url) {
        queue.cancelAll(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                return true;
            }
        });


        JsonObjectRequest JOPR = new JsonObjectRequest(Request.Method.GET,
                url, null, response -> {

            Log.e("OO9", "" + response.toString());
            if (listener != null) {
                Log.e("Asd", response.toString());
                listener.onResponse(response);
            }

        }, volleyError -> {


            sendError(volleyError);


        }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put("x-api-key", "2ad24ZZoOm5GQ2tPtm3y09lDqTDUg8E76IBB8grO");

                return headers;
            }


            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                int mStatusCode = response.statusCode;
                Log.e("mStatusCode", "" + mStatusCode);
                if (listener != null) {
                    listener.responseCode(mStatusCode);
                }
                return super.parseNetworkResponse(response);
            }


        };
        JOPR.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Log.e("urrrl", url);
        queue.add(JOPR);

    }


    public void VolleyArraryGet(String url) {


        JsonArrayRequest JOPR = new JsonArrayRequest(Request.Method.GET,
                url, null, response -> {

            Log.e("OO9", "" + response.toString());
            if (listenerarray != null) {
                listenerarray.onResponse(response);
            }

        }, volleyError -> {

            sendError(volleyError);
        }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put("x-api-key", "2ad24ZZoOm5GQ2tPtm3y09lDqTDUg8E76IBB8grO");

                return headers;
            }


            @Override
            protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
                int mStatusCode = response.statusCode;
                Log.e("mStatusCode", "" + mStatusCode);
                if (listenerarray != null) {
                    listenerarray.responseCode(mStatusCode);
                }
                return super.parseNetworkResponse(response);
            }


        };
        JOPR.setRetryPolicy(new DefaultRetryPolicy(
                2000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(JOPR);

    }

    private void sendError(VolleyError volleyError) {
        String message = null;
        if (volleyError instanceof NetworkError) {
            message = "Cannot connect to Internet...Please check your connection!";
        } else if (volleyError instanceof ServerError) {
            message = "The server could not be found. Please try again after some time!!";
        } else if (volleyError instanceof AuthFailureError) {
            message = "Cannot connect to Internet...Please check your connection!";
        } else if (volleyError instanceof ParseError) {
            message = "Parsing error! Please try again after some time!!";
        } else if (volleyError instanceof NoConnectionError) {
            message = "Cannot connect to Internet...Please check your connection!";
        } else if (volleyError instanceof TimeoutError) {
            message = "Connection TimeOut! Please check your internet connection.";
        } else if (volleyError.networkResponse != null && volleyError.networkResponse.data != null) {
            VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
            volleyError = error;
            try {
                String[] animals = volleyError.toString().split("\"");

                listener.onError(animals[3]);
            } catch (Exception e) {
                Log.e("eeeeee", e.toString());
                message = "Something Went Wrong !";
            }


        } else {
            message = volleyError.toString();
        }
        String[] animals = volleyError.toString().split("\"");
        Log.e("Error", "   " + volleyError.toString());

        if (listener != null) {
            listener.onError(message);
        }

    }

    public interface MyServerListener {
        void onResponse(JSONObject response);

        void onError(String error);

        void responseCode(int resposeCode);
    }


    public interface MyServerListenerArray {
        void onResponse(JSONArray response);

        void onError(String error);

        void responseCode(int resposeCode);
    }

}
