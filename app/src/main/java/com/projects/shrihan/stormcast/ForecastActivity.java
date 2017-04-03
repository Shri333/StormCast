package com.projects.shrihan.stormcast;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.net.MalformedURLException;
import java.net.URL;

public class ForecastActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Forecast> {

    private static final String TAG = "ForecastActivity";
    private static final int FORECAST_LOADER_ID = 2;

    private static final long ONE_HOUR = 60 * 60 * 1000;
    public static final String PREFS_NAME = "FORECAST_PREFS";
    public static final String PREFS_DATE_KEY = "date_prefs";
    public static final String PREFS_AREA_KEY = "area_prefs";
    public static final String PREFS_WEATHER_KEY = "weather_prefs";
    public static final String PREFS_TEMPERATURE_KEY = "temperature_prefs";
    public static final String PREFS_HUMIDITY_KEY = "humidity_prefs";
    public static final String PREFS_WINDSPEED_KEY = "windspeed_prefs";
    public static final String PREFS_DEWPOINT_KEY = "dewpoint_prefs";


    //private static String forecast_url = "http://api.wunderground.com/api/" + api_key + "/conditions/q/" + state_code + "/" + city + ".json";
    private String forecast_url = "http://api.wunderground.com/api/";

    private TextView mArea;
    private TextView mWeather;
    private TextView mTemperature;
    private TextView mHumidity;
    private TextView mWindSpeed;
    private TextView mDewPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);

        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                String key = bundle.getString("key");
                String city = bundle.getString("city");
                String state = bundle.getString("state");
                forecast_url = forecast_url +  key + "/conditions/q/" + state + "/" + city + ".json";
            }
        } else if (savedInstanceState != null) {
            String test = savedInstanceState.getString("city");
        }

        mArea = (TextView) findViewById(R.id.alert_area);
        mWeather = (TextView) findViewById(R.id.alert_weather);
        mTemperature = (TextView) findViewById(R.id.alert_temp);
        mHumidity = (TextView) findViewById(R.id.alert_humidity);
        mWindSpeed = (TextView) findViewById(R.id.alert_windspeed);
        mDewPoint = (TextView) findViewById(R.id.alert_dewpoint);

        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        boolean isTimeElapsed = checkTimestamp();
        if (isTimeElapsed) {
            getSupportLoaderManager().initLoader(FORECAST_LOADER_ID, null, this).forceLoad();
        } else {
            SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
            mArea.setText(sharedPref.getString(PREFS_AREA_KEY, ""));
            mWeather.setText(sharedPref.getString(PREFS_WEATHER_KEY, ""));
            mTemperature.setText(sharedPref.getString(PREFS_TEMPERATURE_KEY, ""));
            mHumidity.setText(sharedPref.getString(PREFS_HUMIDITY_KEY, ""));
            mWindSpeed.setText(sharedPref.getString(PREFS_WINDSPEED_KEY, ""));
            mDewPoint.setText(sharedPref.getString(PREFS_DEWPOINT_KEY, ""));
        }
    }

    private boolean checkTimestamp() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        long prevTime = -1;
        if(sharedPref != null)
            prevTime = sharedPref.getLong(PREFS_DATE_KEY, 0);

        long curTime = System.currentTimeMillis();
        boolean isElapsed = (curTime - prevTime) > ONE_HOUR;
        return isElapsed;
    }

//    private long getTimestamp() {
//        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
//        long time = -1;
//        if(sharedPref != null)
//            time = sharedPref.getLong(PREFS_DATE_KEY, -1);
//        return time;
//    }

    private void saveForecast(Forecast forecast) {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong(PREFS_DATE_KEY, System.currentTimeMillis());
        editor.putString(PREFS_AREA_KEY, forecast.getArea());
        editor.putString(PREFS_WEATHER_KEY, forecast.getWeather());
        editor.putString(PREFS_TEMPERATURE_KEY, forecast.getTemperature());
        editor.putString(PREFS_HUMIDITY_KEY, forecast.getHumidity());
        editor.putString(PREFS_WINDSPEED_KEY, forecast.getWindSpeed());
        editor.putString(PREFS_DEWPOINT_KEY, forecast.getDewPoint());
        editor.commit();
    }

    private void updateUi(Forecast forecast) {
        mArea.setText(forecast.getArea());
        mWeather.setText(forecast.getWeather());
        mTemperature.setText(forecast.getTemperature());
        mHumidity.setText(forecast.getHumidity());
        mWindSpeed.setText(forecast.getWindSpeed());
        mDewPoint.setText(forecast.getDewPoint());
    }

    @Override
    public Loader<Forecast> onCreateLoader(int id, Bundle args) {
        URL url = null;
        try {
            url = new URL(forecast_url);
        } catch (MalformedURLException e) {
            Log.e(TAG, e.getLocalizedMessage());
        }

        return new ForecastLoader(this, url);
    }

    @Override
    public void onLoadFinished(Loader<Forecast> loader, Forecast data) {
        saveForecast(data);
        updateUi(data);
    }

    @Override
    public void onLoaderReset(Loader<Forecast> loader) {

    }
}

//    private class GetForecastData extends AsyncTask<URL, Void, Forecast> {
//
//        @Override
//        protected Forecast doInBackground(URL... params) {
//            URL url = params[0];
//            try {
//                String jsonResponse = QueryUtils.makeHttpRequest(url);
//                return QueryUtils.extractJsonData_Forecast(jsonResponse);
//            } catch (IOException e) {
//                Log.e(TAG, "IOException");
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Forecast forecast) {
//            updateUi(forecast);
//        }
//    }
