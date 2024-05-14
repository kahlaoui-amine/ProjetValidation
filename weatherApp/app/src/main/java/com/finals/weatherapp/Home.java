package com.finals.weatherapp;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Home extends AppCompatActivity {
    EditText etCityInput;
    TextView tvAppName;
    Button btnContinue, btnGetLocation;
    ImageView ivSettings;
    Intent intent;
    FusedLocationProviderClient fusedLocationProviderClient;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        etCityInput = findViewById(R.id.etCityInput);
        tvAppName = findViewById(R.id.tvAppName);
        btnContinue = findViewById(R.id.btnContinue);
        btnGetLocation = findViewById(R.id.btnGetLocation);
        ivSettings = findViewById(R.id.ivSettings);
        intent = new Intent(Home.this, WeatherTab.class);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // btnContinue onClickListener
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Input validation for empty input
                if (etCityInput.length() == 0) {
                    Toast.makeText(Home.this, "City cannot be empty!", Toast.LENGTH_SHORT).show();
                } else {
                    // Gets the input, place it inside the SharedPreferences and proceeds on the next activity.
                    String city = etCityInput.getText().toString().trim();
                    getCoordinates(city);
                }
            }
        });

        // btnGetLocation onClickListener
        btnGetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Ask the user for GPS permission
                // If user grants location access, proceed to fetch the location and move on to the next activity
                if (ActivityCompat.checkSelfPermission(Home.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                    // Else, ask the user for permission again
                } else {
                    ActivityCompat.requestPermissions(Home.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
                }
            }
        });
        ivSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Home.this, Settings.class);
                startActivity(i);
            }
        });

    }

    // Gets the location using GPS then places it on the SharedPreferences
    private void getLocation() {
        // Checks if the user granted the permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Home.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
            return;
        }

        // Gets the user's longitude and latitude
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if (location != null) {
                    // Place their longitude and latitude inside the SharedPreferences
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("latitude", String.valueOf(location.getLatitude()));
                    editor.putString("longitude", String.valueOf(location.getLongitude()));
                    editor.apply();

                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Invalid location, please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Gets the coordinates of the inputted city using OpenWeather API
    private void getCoordinates(String city) {
        String apiKey = "5237fae1840cf83fddda7202294f7989";
        String apiURL = "https://api.openweathermap.org/geo/1.0/direct?q=" + city + "&limit=1&appid=" + apiKey ;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, apiURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    if (response.equals("[]")) {
                        Toast.makeText(getApplicationContext(), "Invalid location, please try again.", Toast.LENGTH_SHORT).show();
                    } else {
                        /*  [] JSON Response
                            └── {} 0
                               ├── lon
                               └── lat
                         */

                        JSONArray jsonResponse = new JSONArray(response);
                        JSONObject object0 = jsonResponse.getJSONObject(0);

                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("longitude", String.valueOf(object0.getDouble("lon")));
                        editor.putString("latitude", String.valueOf(object0.getDouble("lat")));
                        editor.apply();

                        startActivity(intent);
                        etCityInput.getText().clear();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            // Error handling for invalid location
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof NetworkError || error instanceof AuthFailureError || error instanceof TimeoutError) {
                    Toast.makeText(getApplicationContext(), "Cannot connect to the internet, please check your connection.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Invalid location, please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Executes the API request
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }
}