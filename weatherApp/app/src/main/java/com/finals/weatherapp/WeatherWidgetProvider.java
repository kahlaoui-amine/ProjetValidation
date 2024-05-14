package com.finals.weatherapp;


import static android.content.Context.MODE_PRIVATE;
import static android.provider.Settings.System.getString;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.finals.weatherapp.fragments.WeatherCurrent;

import org.apache.commons.text.WordUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Map;

public class WeatherWidgetProvider extends AppWidgetProvider {
    private Map<CharSequence, Integer> imageMap;

    private String location;


    SharedPreferences sp;

    // onAppWidgetOptionsChanged
    @Override

    public void onAppWidgetOptionsChanged(Context context,
                                          AppWidgetManager appWidgetManager,
                                          int appWidgetId,
                                          Bundle newOptions)
    {
        int appWidgetIds[] = {appWidgetId};
        onUpdate(context, appWidgetManager, appWidgetIds);
    }
    @Override
    @SuppressLint("InlinedApi")
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        getWeatherDetails(context);




        for (int appWidgetId : appWidgetIds){
            Intent intent = new Intent(context, Home.class);
            PendingIntent pendingIntent =
                    PendingIntent.getActivity(context, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT |
                                    PendingIntent.FLAG_IMMUTABLE);

            Intent updateIntent = new Intent(context, WeatherWidgetProvider.class);
            updateIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,
                    appWidgetIds);

            PendingIntent pendingUpdate =
                    PendingIntent.getBroadcast(context, 0, updateIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT |
                                    PendingIntent.FLAG_IMMUTABLE);
            // Get the layout for the widget and attach an on-click
            // listener to the view.
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.weather_widget);
            views.setOnClickPendingIntent(R.id.widgetId, pendingIntent);

           // views.setTextViewText(R.id.location,location);
            //views.setTextViewText(R.id.description, humidity);
            SharedPreferences sharedPreferences = context.getSharedPreferences("myrefs", MODE_PRIVATE);
            String temperature =sharedPreferences.getString("temp", "10");
            String location =sharedPreferences.getString("loc", "");
            String description =sharedPreferences.getString("desc", "");
            String icon =sharedPreferences.getString("ic", "");
            views.setTextViewText(R.id.TempW, temperature);
            views.setTextViewText(R.id.locationW, location);
            views.setTextViewText(R.id.descriptionW, description);
            views.setImageViewResource(R.id.WidgetWeatherIcon,getWeatherIcon(icon));

            appWidgetManager.updateAppWidget(appWidgetId, views);



        }

    }
    @Override
    public void onReceive(Context context, Intent intent) {
        // Called when the widget receives an update signal

        if (intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
            // Fetch weather data
            getWeatherDetails(context);
        }

        super.onReceive(context, intent);
    }
    public void getWeatherDetails(Context context) {
        String apiKey = "5237fae1840cf83fddda7202294f7989";
        // https://api.openweathermap.org/data/2.5/weather?lat={lat}&lon={lon}&appid={API key}

        SharedPreferences sharedPreferences = context.getSharedPreferences("longLat", Context.MODE_PRIVATE);
        String latitude = sharedPreferences.getString("latitude", "");
        String longitude = sharedPreferences.getString("longitude", "");

        String apiURL = "https://api.openweathermap.org/data/2.5/weather" + "?lat=" + latitude + "&lon=" + longitude + "&appid=" +  apiKey ;

        // Sends a request to the OpenWeather API to get the weather details
        StringRequest stringRequest = new StringRequest(Request.Method.POST, apiURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray arrayWeather = jsonResponse.getJSONArray("weather");
                    JSONObject object0 = arrayWeather.getJSONObject(0);
                    String description = object0.getString("description");
                    String icon = object0.getString("icon");
                    JSONObject objectMain = jsonResponse.getJSONObject("main");
                    String temperature = getTemperature(objectMain.getDouble("temp"));

                    String humidity = objectMain.getString("humidity") + "%";
                    JSONObject objectWind = jsonResponse.getJSONObject("wind");
                    String windSpeed = getWindSpeed(objectWind.getDouble("speed"));
                    JSONObject objectSys = jsonResponse.getJSONObject("sys");
                    String location = jsonResponse.getString("name") + ", "  + objectSys.getString("country");



                    // Displays the results
                    SharedPreferences sharedPreferences =context.getSharedPreferences("myrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor =sharedPreferences.edit();
                    editor.putString("temp", temperature);
                    editor.putString("loc", location);
                    editor.putString("desc", description);
                    editor.putString("ic", icon);
                    editor.apply();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            // Error handling for invalid location
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });// Add the request to the Volley request queue
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);

    }
    public String getTemperature(double temp) {
        final DecimalFormat df = new DecimalFormat("#.#");
        return df.format(temp - 273.15) + "°C";

    }

    // Convert m/s into km/h or mph
    public String getWindSpeed(double wind) {
        final DecimalFormat df = new DecimalFormat("#.#");

        return df.format((wind * 3.6)) + " km/h";

    }

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
