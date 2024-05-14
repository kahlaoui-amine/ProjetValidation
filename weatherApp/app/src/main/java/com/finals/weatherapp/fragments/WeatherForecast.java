package com.finals.weatherapp.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.finals.weatherapp.BuildConfig;
import com.finals.weatherapp.R;
import com.finals.weatherapp.adapters.RecyclerViewAdapter;
import com.finals.weatherapp.models.Forecast;

import org.apache.commons.text.WordUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WeatherForecast extends Fragment {
    View view;
    LinearLayout llFetchingData;
    RecyclerView recyclerView;
    SharedPreferences sp;
    List<Forecast> forecastList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_weather_forecast, container, false);
        llFetchingData = view.findViewById(R.id.llFetchingData);
        recyclerView = view.findViewById(R.id.recyclerView);
        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());

        getForecastDetails();
        return view;
    }

    private void getForecastDetails() {
        String apiKey = "5237fae1840cf83fddda7202294f7989";

        forecastList = new ArrayList<>();
        final DecimalFormat df = new DecimalFormat("#.#");
        final DateFormat dtf = new SimpleDateFormat("EEE, MMMM dd, yyyy", Locale.ENGLISH);

        // https://api.openweathermap.org/data/2.5/onecall?lat={lat}&lon={lon}&exclude={part}&appid={API key}
        String latitude = sp.getString("latitude", "");
        String longitude = sp.getString("longitude", "");

        String apiURL = "https://api.openweathermap.org/data/2.5/forecast/daily?lat="+latitude+"&lon="+ longitude +"&cnt={cnt}&appid="+ apiKey;

        // Sends a request to the OpenWeather API to get the weather details
        StringRequest stringRequest = new StringRequest(Request.Method.POST, apiURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    /*  {} JSON Response
                        ├── [] daily
                        │   └── {} 0 - 7
                        │       ├── dt
                        │       ├── {} temp
                        │       │   ├── min
                        │       │   └── max
                        │       ├── {} feels_like
                        │       │   └── day
                        │       ├── humidity
                        │       ├── wind_speed
                        │       └── [] weather
                        │           └── {} 0
                        │               ├── description
                        │               └── icon
                        └── timezone
                     */

                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray arrayDaily = jsonResponse.getJSONArray("daily");
                    int timezone = jsonResponse.getInt("timezone_offset");
                    for (int i = 0; i <= 7; i++) {
                        JSONObject objectDays = arrayDaily.getJSONObject(i);
                        Date date = new Date(objectDays.getLong("dt") * 1000L);
                        JSONObject objectTemp = objectDays.getJSONObject("temp");
                        String minTemperature = getTemperature(objectTemp.getDouble("min"));
                        String maxTemperature = getTemperature(objectTemp.getDouble("max"));
                        String minMax = minTemperature + " | " + maxTemperature;
                        JSONObject objectFeelsLike = objectDays.getJSONObject("feels_like");
                        String feelsLike = getTemperature(objectFeelsLike.getDouble("day"));
                        String humidity = objectDays.getString("humidity") + "%";
                        String windSpeed = getWindSpeed(objectDays.getDouble("wind_speed"));
                        JSONArray arrayWeather = objectDays.getJSONArray("weather");
                        JSONObject object0 = arrayWeather.getJSONObject(0);
                        String description = WordUtils.capitalize(object0.getString("description"));
                        int icon = getWeatherIcon(object0.getString("icon"));
                        String sunrise =  getTime(objectDays.getLong("sunrise"), timezone);
                        String sunset = getTime(objectDays.getLong("sunset"), timezone);

                        forecastList.add(new Forecast(dtf.format(date), description, minMax, feelsLike, humidity, windSpeed, icon, sunrise, sunset));
                    }
                    setRecyclerView();

                    // Set visibility
                    recyclerView.setVisibility(View.VISIBLE);
                    llFetchingData.setVisibility(View.GONE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            // Error handling for invalid location
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

        // Executes the API request
        RequestQueue requestQueue = Volley.newRequestQueue(requireActivity().getApplicationContext());
        requestQueue.add(stringRequest);
    }

    // Inserts the data stored on the forecastList inside the RecyclerView
    private void setRecyclerView() {
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(forecastList);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setHasFixedSize(true);
    }

    // Convert kelvin to celsius/fahrenheit
    public String getTemperature(double temp) {
        final DecimalFormat df = new DecimalFormat("#.#");
        if (sp.getString("temperature", "").equals("celsius")) {
            return df.format(temp - 273.15) + "°C";
        } else if (sp.getString("temperature", "").equals("fahrenheit")){
            return df.format((temp - 273.15) * 9/5 +32) + "°F";
        } else {
            return df.format(temp) + "K";
        }
    }

    // Convert m/s into km/h or mph
    public String getWindSpeed(double wind) {
        final DecimalFormat df = new DecimalFormat("#.#");
        if (sp.getString("wind", "").equals("m/s")) {
            return wind + " m/s";
        } else if (sp.getString("wind", "").equals("km/h")){
            return df.format((wind * 3.6)) + " km/h";
        } else {
            return df.format((wind * 2.237)) + " mph";
        }
    }

    // Convert time into 12H/24H using the location's timezone
    public String getTime(long time, int timezone) {
        LocalDateTime dateTime = LocalDateTime.ofEpochSecond(time, 0, ZoneOffset.ofTotalSeconds(timezone));
        String settings = sp.getString("time", "");

        if (settings.equals("12-hour")) {
            return dateTime.format(DateTimeFormatter.ofPattern("hh:mm a"));
        } else {
            return dateTime.format(DateTimeFormatter.ofPattern("HH:mm"));
        }
    }

    // Convert id from API response to its icon drawable id
    public int getWeatherIcon(String icon) {
        switch (icon) {
            case "01d":
                return R.drawable.ic_01d;
            case "01n":
                return R.drawable.ic_01n;
            case "02d":
                return R.drawable.ic_02d;
            case "02n":
                return R.drawable.ic_02n;
            case "03d":
                return R.drawable.ic_03d;
            case "03n":
                return R.drawable.ic_03n;
            case "04d":
                return R.drawable.ic_04d;
            case "04n":
                return R.drawable.ic_04n;
            case "09d":
                return R.drawable.ic_09d;
            case "09n":
                return R.drawable.ic_09n;
            case "10d":
                return R.drawable.ic_10d;
            case "10n":
                return R.drawable.ic_10n;
            case "11d":
                return R.drawable.ic_11d;
            case "11n":
                return R.drawable.ic_11n;
            case "13d":
                return R.drawable.ic_13d;
            case "13n":
                return R.drawable.ic_13n;
            case "50d":
                return R.drawable.ic_50d;
            case "50n":
                return R.drawable.ic_50n;
            default:
                return R.drawable.ic_unknown;
        }
    }
}