package com.projects.shrihan.stormcast;

/**
 * Created by shrihan on 3/11/17.
 */

class Forecast {

    private String mArea;
    private String mWeather;
    private String mTemperature;
    private String mHumidity;
    private String mWindSpeed;
    private String mDewPoint;

    Forecast(String area, String weather, String temperature, String humidity, String windSpeed, String dewPoint) {
        mArea = area;
        mWeather = weather;
        mTemperature = temperature;
        mHumidity = humidity;
        mWindSpeed = windSpeed;
        mDewPoint = dewPoint;
    }

    public String getArea() {
        return mArea;
    }

    public String getWeather() {
        return mWeather;
    }

    public String getTemperature() {
        return mTemperature;
    }

    public String getHumidity() {
        return mHumidity;
    }

    public String getWindSpeed() {
        return mWindSpeed;
    }

    public String getDewPoint() {
        return mDewPoint;
    }
}
