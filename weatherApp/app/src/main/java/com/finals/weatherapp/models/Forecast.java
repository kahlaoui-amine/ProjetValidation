package com.finals.weatherapp.models;

public class Forecast {
    private String date, description, minMax, feelsLike, humidity, windSpeed, sunrise, sunset;
    private int icon;

    private boolean expanded;

    public Forecast(String date, String description, String minMax, String feelsLike, String humidity, String windSpeed, int icon, String sunrise, String sunset) {
        this.date = date;
        this.description = description;
        this.minMax = minMax;
        this.feelsLike = feelsLike;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
        this.icon = icon;
        this.sunrise = sunrise;
        this.sunset = sunset;
        this.expanded = false;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMinMax() {
        return minMax;
    }

    public void setMinMax(String minMax) {
        this.minMax = minMax;
    }

    public String getFeelsLike() {
        return feelsLike;
    }

    public void setFeelsLike(String feelsLike) {
        this.feelsLike = feelsLike;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(String windSpeed) {
        this.windSpeed = windSpeed;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public String getSunrise() {
        return sunrise;
    }

    public void setSunrise(String sunrise) {
        this.sunrise = sunrise;
    }

    public String getSunset() {
        return sunset;
    }

    public void setSunset(String sunset) {
        this.sunset = sunset;
    }

}
