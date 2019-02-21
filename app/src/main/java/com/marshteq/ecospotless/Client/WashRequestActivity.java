package com.marshteq.ecospotless.Client;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.iamhabib.easy_preference.EasyPreference;
import com.marshteq.ecospotless.Helpers.Credentials;
import com.marshteq.ecospotless.Helpers.VolleyMultipartRequest;
import com.marshteq.ecospotless.Helpers.VolleySingleton;
import com.marshteq.ecospotless.Models.Price;
import com.marshteq.ecospotless.Models.UserPref;
import com.marshteq.ecospotless.Models.WashRequest;
import com.marshteq.ecospotless.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import com.google.android.libraries.places.api.Places;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WashRequestActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener  {

    TextView wash_date,wash_time;
    Spinner price_spinner;
    int mHour,mMinute;
    String wash_time_string,wash_date_string;
    Boolean wash_clicked;
    String API_KEY = "AIzaSyDQ4GStHwABeWW1Z3tW-aGnyiUtAesqgb4";
    ArrayList<String> service_names;
    ArrayList<Price> services;
    ProgressBar progressBar;
    CoordinatorLayout wash_requests_layout;
    String price_id;
    String wash_location;
    EditText extra_notes;
    Button submit_request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wash_request);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        wash_date = (TextView) findViewById(R.id.wash_date);
        wash_time = (TextView) findViewById(R.id.wash_time);
        price_spinner = (Spinner) findViewById(R.id.price_spinner);
        extra_notes = (EditText) findViewById(R.id.extra_notes);
        wash_requests_layout = (CoordinatorLayout) findViewById(R.id.wash_requests_layout);
        submit_request = (Button) findViewById(R.id.submit_request_btn);
        final Calendar c = Calendar.getInstance();

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        wash_date_string = null;
        wash_time_string = null;
        service_names = new ArrayList<String>();
        services = new ArrayList<Price>();

        progressBar = new ProgressBar(WashRequestActivity.this,null,android.R.attr.progressBarStyle);
        progressBar.setIndeterminate(true);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                550, // Width in pixels
                LinearLayout.LayoutParams.WRAP_CONTENT // Height of progress bar
        );
        progressBar.setLayoutParams(lp);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) progressBar.getLayoutParams();
        params.gravity = Gravity.CENTER_VERTICAL;
        progressBar.setLayoutParams(params);
        wash_requests_layout.addView(progressBar);

        wash_date.setText(year+"-"+(month+1)+"-"+day);
        wash_date_string = year+"-"+(month+1)+"-"+day;
        if(mMinute<10)
         wash_time.setText(mHour+":0"+mMinute);
        else
            wash_time.setText(mHour+":"+mMinute);
        wash_time_string = mHour +":"+mMinute;

        final DatePickerDialog datePickerDialog = new DatePickerDialog(
                WashRequestActivity.this, this, year, month, day);

        wash_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wash_clicked = true;
                datePickerDialog.show();
            }
        });

        wash_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(WashRequestActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        if(i1<10){
                            wash_time.setText(i +":0"+i1);
                            wash_time_string = i +":0"+i1;
                        }
                        else{
                            wash_time.setText(i+":"+i1);
                            wash_time_string = i +":"+i1;
                        }
                    }
                }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });
        wash_location = "79 Parliament Street, Central, Port Elizabeth";
        price_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Price price = services.get(position);
                price_id = price.id;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        submit_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitRequest();
            }
        });

        Places.initialize(getApplicationContext(), API_KEY);
        PlacesClient placesClient = Places.createClient(this);
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i("Check Place", "Place: " + place.getName() + ", " + place.getId());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("Check Place", "An error occurred: " + status);
            }
        });
        getServices();

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        if(wash_clicked){
            wash_date.setText(year+"-"+(month+1)+"-"+dayOfMonth);
            wash_date_string = year+"-"+(month+1)+"-"+dayOfMonth;
        }
    }

    public void submitRequest() {
        Credentials credentials = EasyPreference.with(WashRequestActivity.this).getObject("server_details", Credentials.class);
        UserPref pref = EasyPreference.with(WashRequestActivity.this).getObject("user_pref", UserPref.class);
        final String url = credentials.server_url;
        String URL = url+"api/submit-wash-request";
        Log.d("Check request time",wash_date_string);
        Log.d("Check request times",wash_time_string);

        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, URL, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);
                Log.d("Check Response",resultResponse);
                try {
                    JSONObject result = new JSONObject(resultResponse);
                    Log.d("fun",result.toString());
                    String message = result.getString("message");

                    Toast.makeText(WashRequestActivity.this, message, Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(WashRequestActivity.this, ClientMainActivity.class);
                    startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage = "Unknown error";
                if (networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        errorMessage = "Request timeout, Please try again later";
                    } else if (error.getClass().equals(NoConnectionError.class)) {
                        errorMessage = "Failed to connect server";
                    }
                } else {
                    String result = new String(networkResponse.data);
                    try {
                        Log.d("Error Result", result);
                        JSONObject response = new JSONObject(result);
                        String status = response.getString("status");
                        String message = response.getString("message");
                        errorMessage = message;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.i("Error", errorMessage);
                error.printStackTrace();
                Toast.makeText(WashRequestActivity.this, errorMessage, Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                UserPref pref = EasyPreference.with(WashRequestActivity.this).getObject("user_pref", UserPref.class);
                Map<String, String> params = new HashMap<>();
                params.put("wash_date", String.valueOf(wash_date_string));
                params.put("wash_time", String.valueOf(wash_time_string));
                params.put("price_id", String.valueOf(price_id));
                params.put("client_id", pref.id);
                params.put("wash_location", wash_location);
                params.put("extra_notes", String.valueOf(extra_notes.getText()));
                params.put("status","Pending");
                return params;
            }

            @Override
            protected Map<String, VolleyMultipartRequest.DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                // file name could found file base or direct access from real path
                // for now just get bitmap data from ImageView

                return params;
            }
        };
        VolleySingleton.getInstance(WashRequestActivity.this).addToRequestQueue(multipartRequest);
    }

    public void getServices(){
        progressBar.setVisibility(View.VISIBLE);
        RequestQueue requestQueue = Volley.newRequestQueue(WashRequestActivity.this);
        Credentials credentials = EasyPreference.with(WashRequestActivity.this).getObject("server_details", Credentials.class);
        final String url = credentials.server_url;
        String URL = url+"api/get_pricing";

        JsonObjectRequest provinceRequest = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray response_obj = response.getJSONArray("prices");
                    Log.d("Response2",response_obj.toString());
                    if (response_obj.length() > 0) {
                        for (int i = 0; i < response_obj.length(); i++) {
                            JSONObject obj = response_obj.getJSONObject(i);
                            JsonParser parser = new JsonParser();
                            JsonElement element = parser.parse(obj.toString());
                            Gson gson = new Gson();
                            Price price = gson.fromJson(element, Price.class);
                            services.add(price);
                            service_names.add(price.service + " - R" + price.price +" "+ price.vehicle.name);
                        }
                        price_spinner.setAdapter(new ArrayAdapter<String>(WashRequestActivity.this,android.R.layout.simple_spinner_dropdown_item,service_names));
//                        send_shift_offer.setEnabled(true);
                    }
                    progressBar.setVisibility(View.INVISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("Error2",e.getMessage());
                    progressBar.setVisibility(View.INVISIBLE);
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.d("error", error.toString());

            }
        });
        requestQueue.add(provinceRequest);
    }
}
