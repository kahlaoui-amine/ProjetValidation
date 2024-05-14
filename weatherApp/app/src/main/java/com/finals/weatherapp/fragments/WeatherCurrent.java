package com.finals.weatherapp.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.finals.weatherapp.R;

import org.apache.commons.text.WordUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class WeatherCurrent extends Fragment {
    View view;
    TextView tvCity, tvTemperature, tvUnit, tvDescription, tvMinMax, tvFeelsLike, tvHumidity, tvWindSpeed, tvSunrise, tvSunset;
    ImageView ivIcon, ivBack;
    LinearLayout llTemperature;

    GridLayout glWeatherDetails;
    SharedPreferences sp;

    public static String PREF_DESC;
    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_weather_current, container, false);
        tvCity = view.findViewById(R.id.tvCity);
        tvTemperature = view.findViewById(R.id.tvTemperature);
        tvUnit = view.findViewById(R.id.tvUnit);
        tvDescription = view.findViewById(R.id.tvDescription);
        tvMinMax = view.findViewById(R.id.tvMinMax);
        tvFeelsLike = view.findViewById(R.id.tvFeelsLike);
        tvHumidity = view.findViewById(R.id.tvHumidity);
        tvWindSpeed = view.findViewById(R.id.tvWindSpeed);
        tvSunrise = view.findViewById(R.id.tvSunrise);
        tvSunset = view.findViewById(R.id.tvSunset);
        ivIcon = view.findViewById(R.id.ivIcon);
        ivBack = view.findViewById(R.id.ivBack);
        llTemperature = view.findViewById(R.id.llTemperature);

        glWeatherDetails = view.findViewById(R.id.glWeatherDetails);
        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());

        getWeatherDetails(getContext());

        return view;

    }

    // Get current weather details using OpenWeather API
    public void getWeatherDetails(Context context) {
        String apiKey = "5237fae1840cf83fddda7202294f7989";
        // https://api.openweathermap.org/data/2.5/weather?lat={lat}&lon={lon}&appid={API key}
        String latitude = sp.getString("latitude", "");
        String longitude = sp.getString("longitude", "");
        SharedPreferences sharedPreferences =context.getSharedPreferences("longLat", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("latitude", latitude);
        editor.putString("longitude", longitude);
        editor.apply();

        String apiURL = "https://api.openweathermap.org/data/2.5/weather" + "?lat=" + latitude + "&lon=" + longitude + "&appid=" +  apiKey ;

        // Sends a request to the OpenWeather API to get the weather details
        StringRequest stringRequest = new StringRequest(Request.Method.POST, apiURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    /*  {} JSON Response
                        ├── [] weather
                        │   ├── {} 0
                        │   │   ├── description
                        │   │   └── icon
                        ├── {} main
                        │   ├── temp
                        │   ├── feels_like
                        │   ├── temp_min
                        │   ├── temp_max
                        │   └── humidity
                        ├── {} wind
                        │   └─ speed
                        ├── {} sys
                        │   ├── country
                        │   ├── sunrise
                        │   └── sunset
                        ├── timezone
                        └── name
                     */

                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray arrayWeather = jsonResponse.getJSONArray("weather");
                    JSONObject object0 = arrayWeather.getJSONObject(0);
                    String description = object0.getString("description");
                    String icon = object0.getString("icon");
                    JSONObject objectMain = jsonResponse.getJSONObject("main");
                    String temperature = getTemperature(objectMain.getDouble("temp"));
                    String feelsLike = getTemperature(objectMain.getDouble("feels_like"));
                    String minTemperature = getTemperature(objectMain.getDouble("temp_min"));
                    String maxTemperature = getTemperature(objectMain.getDouble("temp_max"));
                    String minMax = minTemperature + " | " + maxTemperature;
                    String humidity = objectMain.getString("humidity") + "%";
                    JSONObject objectWind = jsonResponse.getJSONObject("wind");
                    String windSpeed = getWindSpeed(objectWind.getDouble("speed"));
                    JSONObject objectSys = jsonResponse.getJSONObject("sys");
                    String location = jsonResponse.getString("name") + ", "  + objectSys.getString("country");
                    long sunrise = objectSys.getLong("sunrise");
                    long sunset = objectSys.getLong("sunset");
                    int timezone = jsonResponse.getInt("timezone");
                    PREF_DESC= description;
                    // Displays the results
                    ivIcon.setImageResource(getWeatherIcon(icon));
                    tvDescription.setText(WordUtils.capitalize(description));
                    tvTemperature.setText(temperature.substring(0, temperature.length() - 2));
                    tvFeelsLike.append(feelsLike);
                    tvMinMax.append(minMax);
                    tvHumidity.append(humidity);
                    tvWindSpeed.append(windSpeed);
                    tvSunrise.append(getTime(sunrise, timezone));
                    tvSunset.append(getTime(sunset, timezone));
                    tvCity.setText(location);

                    tvDescription.setVisibility(View.VISIBLE);
                    llTemperature.setVisibility(View.VISIBLE);
                    glWeatherDetails.setVisibility(View.VISIBLE);
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

    // Convert kelvin into celsius or fahrenheit
    public String getTemperature(double temp) {
        final DecimalFormat df = new DecimalFormat("#.#");
        sp.getString("temperature", "").equals("celsius");
        tvUnit.setText(R.string.unit_c);
        return df.format(temp - 273.15) + "°C";

    }

    // Convert m/s into km/h or mph
    public String getWindSpeed(double wind) {
        final DecimalFormat df = new DecimalFormat("#.#");
        sp.getString("wind", "").equals("km/h");
            return df.format((wind * 3.6)) + " km/h";

    }

    // Convert time into 12H/24H using the location's timezone
    public String getTime(long time, int timezone) {
        LocalDateTime dateTime = LocalDateTime.ofEpochSecond(time, 0, ZoneOffset.ofTotalSeconds(timezone));
        return dateTime.format(DateTimeFormatter.ofPattern("HH:mm"));
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