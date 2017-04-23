package com.projects.shrihan.stormcast;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Alert> {

    private static final String TAG = "MainActivity";
    private static final String api_key = "42b5749a5bd9ee14";
    private static final int TAG_CODE_PERMISSION_LOCATION = 6;
    private static final int ALERT_LOADER_ID = 1;
    private static final String ALERT_SHARED_PREFS = "ALERT_PREFS";
    private static final long TEN_MINUTES = 10 * 60 * 1000;
    public static final String PREFS_TIME_KEY = "time_prefs";
    public static final String PREFS_DESCRIPTION_KEY = "description_prefs";
    public static final String PREFS_DATE_KEY = "date_prefs";
    public static final String PREFS_EXPIRY_KEY = "expiry_prefs";
    public static final String PREFS_TYPE_KEY = "type_prefs";
    public static final String PREFS_MESSAGEE_KEY = "messagee_prefs";

    private String state = null;
    private String city = null;
    private String state_code = null;

    //    private static String alert_url = "http://api.wunderground.com/api/" + api_key + "/alerts/q/" + state + "/" + city + ".json";
    private static String alert_url = "http://api.wunderground.com/api/" + api_key + "/alerts/q/";

    private TextView mDescriptionTextView = null;
    private TextView mDateTextView = null;
    private TextView mExpiresTextView = null;
    private ImageView mImageView = null;
    private TextView mEmptyView = null;
    private View mLoadingIndicator = null;
    private View mAlertLayoutView = null;

    private Alert mAlert;

    private static HashMap<String, String> states = new HashMap<String, String>();

    static {
        states.put("Alabama", "AL");
        states.put("Arizona", "AZ");
        states.put("Arkansas", "AR");
        states.put("British Columbia", "BC");
        states.put("California", "CA");
        states.put("Colorado", "CO");
        states.put("Connecticut", "CT");
        states.put("Delaware", "DE");
        states.put("District Of Columbia", "DC");
        states.put("Florida", "FL");
        states.put("Georgia", "GA");
        states.put("Hawaii", "HI");
        states.put("Idaho", "ID");
        states.put("Illinois", "IL");
        states.put("Indiana", "IN");
        states.put("Iowa", "IA");
        states.put("Kansas", "KS");
        states.put("Kentucky", "KY");
        states.put("Louisiana", "LA");
        states.put("Maine", "ME");
        states.put("Maryland", "MD");
        states.put("Massachusetts", "MA");
        states.put("Michigan", "MI");
        states.put("Minnesota", "MN");
        states.put("Mississippi", "MS");
        states.put("Missouri", "MO");
        states.put("Montana", "MT");
        states.put("Nebraska", "NE");
        states.put("Nevada", "NV");
        states.put("New Hampshire", "NH");
        states.put("New Jersey", "NJ");
        states.put("New Mexico", "NM");
        states.put("New York", "NY");
        states.put("North Carolina", "NC");
        states.put("North Dakota", "ND");
        states.put("Ohio", "OH");
        states.put("Oklahoma", "OK");
        states.put("Oregon", "OR");
        states.put("Pennsylvania", "PA");
        states.put("Rhode Island", "RI");
        states.put("South Carolina", "SC");
        states.put("South Dakota", "SD");
        states.put("Tennessee", "TN");
        states.put("Texas", "TX");
        states.put("Utah", "UT");
        states.put("Vermont", "VT");
        states.put("Virginia", "VA");
        states.put("Washington", "WA");
        states.put("West Virginia", "WV");
        states.put("Wisconsin", "WI");
        states.put("Wyoming", "WY");
    }

    private LocationManager mLocationManager;
    private LocationListener mLocationListener = new WeatherLocationListener();

    private class WeatherLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location loc) {
    /*----------to get City-Name from coordinates ------------- */
            Geocoder gcd = new Geocoder(MainActivity.this, Locale.getDefault());
            List<Address> addresses;
            try {
                addresses = gcd.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
                if (addresses != null && addresses.size() > 0) {
                    city = addresses.get(0).getLocality();
                    state = addresses.get(0).getAdminArea();
                    state_code = states.get(state);
                    //alert_url = alert_url + state_code + "/" + city + ".json";

                    getSupportLoaderManager().initLoader(ALERT_LOADER_ID, null, MainActivity.this).forceLoad();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
            mLoadingIndicator.setVisibility(View.GONE);
            mAlertLayoutView.setVisibility(View.GONE);
            mEmptyView.setText("Please enable location to download weather alerts");
            mEmptyView.setVisibility(View.VISIBLE);
            return;
        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onStatusChanged(String provider,
                                    int status, Bundle extras) {
            // TODO Auto-generated method stub
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDescriptionTextView = (TextView) findViewById(R.id.alert_desc);
        mDateTextView = (TextView) findViewById(R.id.alert_date);
        mExpiresTextView = (TextView) findViewById(R.id.alert_expires);
        mImageView = (ImageView) findViewById(R.id.imageView);
        mEmptyView = (TextView) findViewById(R.id.empty_view);
        mLoadingIndicator = findViewById(R.id.loading_indicator);
        mAlertLayoutView = findViewById(R.id.weather_id);

        boolean isTimeElapsed = checkTimestamp();
        if (!isTimeElapsed) {
            SharedPreferences sharedPref = getSharedPreferences(ALERT_SHARED_PREFS, Context.MODE_PRIVATE);
            Alert tempAlert = new Alert(sharedPref.getString(PREFS_TYPE_KEY, ""),
                    sharedPref.getString(PREFS_DESCRIPTION_KEY, "No alerts"),
                    sharedPref.getString(PREFS_DATE_KEY, ""),
                    sharedPref.getString(PREFS_EXPIRY_KEY, ""),
                    sharedPref.getString(PREFS_MESSAGEE_KEY, ""));
            updateUi(tempAlert);
            return;
        }

        NetworkInfo ni = QueryUtils.getNetworkInfo(this);
        if (ni == null || !ni.isConnected() ||
                (ni.getType() != ConnectivityManager.TYPE_WIFI && ni.getType() != ConnectivityManager.TYPE_MOBILE)) {
            mLoadingIndicator.setVisibility(View.GONE);
            mAlertLayoutView.setVisibility(View.GONE);

            mEmptyView.setText("No internet connection to download weather alerts. Please check the connection.");
            mEmptyView.setVisibility(View.VISIBLE);
            return;
        }
        mEmptyView.setVisibility(View.GONE);
        int permission1 = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        int permission2 = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION);
        if (permission1 != PackageManager.PERMISSION_GRANTED && permission2 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    TAG_CODE_PERMISSION_LOCATION);
        } else {
            getWeatherData();
        }

        mLoadingIndicator.setVisibility(View.VISIBLE);
        mAlertLayoutView.setVisibility(View.GONE);
    }

    private boolean checkTimestamp() {
        SharedPreferences sharedPref = getSharedPreferences(ALERT_SHARED_PREFS, Context.MODE_PRIVATE);
        long prevTime = -1;
        if(sharedPref != null)
            prevTime = sharedPref.getLong(PREFS_TIME_KEY, 0);

        long curTime = System.currentTimeMillis();
        boolean isElapsed = (curTime - prevTime) > TEN_MINUTES;
        return isElapsed;
    }

    // button for detailactivity
    public void onViewAlert(View view) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("alert", mAlert);
        startActivity(intent);
    }

    // button for forecastactivity
    public void onCheckForecast(View view) {
        Intent intent = new Intent(this, ForecastActivity.class);
        intent.putExtra("city", city);
        intent.putExtra("state", state_code);
        intent.putExtra("key", api_key);
        startActivity(intent);
    }

    // updateUi for loader
    private void updateUi(Alert alert) {
        mAlert = alert;
        mLoadingIndicator.setVisibility(View.GONE);
        mAlertLayoutView.setVisibility(View.VISIBLE);

        boolean isNoAlert = TextUtils.isEmpty(alert.getDate()) && TextUtils.isEmpty(alert.getExpires());

        mDescriptionTextView.setText(alert.getDescription());
        if (isNoAlert) {
            mDateTextView.setVisibility(View.GONE);
            mExpiresTextView.setVisibility(View.GONE);
        } else {
            mDateTextView.setVisibility(View.VISIBLE);
            mDateTextView.setText(alert.getDate());

            mExpiresTextView.setVisibility(View.VISIBLE);
            mExpiresTextView.setText(alert.getExpires());
        }
        String type = alert.getType();
        if (type == null) {
            mImageView.setVisibility(View.GONE);
            return;
        }
        switch (type.toUpperCase()) {
            case "FIR":
                mImageView.setImageResource(R.drawable.ic_fir);
                break;
            case "FLO":
                mImageView.setImageResource(R.drawable.ic_flo);
                break;
            case "FOG":
                mImageView.setImageResource(R.drawable.ic_fog);
                break;
            case "HEA":
                mImageView.setImageResource(R.drawable.ic_hea);
                break;
            case "HWW":
                mImageView.setImageResource(R.drawable.ic_hww);
                break;
            case "REC":
                mImageView.setImageResource(R.drawable.ic_rec);
                break;
            case "SEW":
                mImageView.setImageResource(R.drawable.ic_sew);
                break;
            case "SPE":
                mImageView.setImageResource(R.drawable.ic_spe);
                break;
            case "SVR":
                mImageView.setImageResource(R.drawable.ic_svr);
                break;
            case "TOR":
                mImageView.setImageResource(R.drawable.ic_tor);
                break;
            case "TOW":
                mImageView.setImageResource(R.drawable.ic_tow);
                break;
            case "VOL":
                mImageView.setImageResource(R.drawable.ic_vol);
                break;
            case "WAT":
                mImageView.setImageResource(R.drawable.ic_wat);
                break;
            case "WIN":
                mImageView.setImageResource(R.drawable.ic_win);
                break;
            case "WND":
                mImageView.setImageResource(R.drawable.ic_wnd);
                break;
            case "WRN":
                mImageView.setImageResource(R.drawable.ic_wrn);
                break;
            case "":
                mImageView.setVisibility(View.GONE);
                break;
        }
        Button moreBtn = (Button) findViewById(R.id.view_alert);
        if (isNoAlert) {
            moreBtn.setVisibility(View.GONE);
        } else {
            moreBtn.setVisibility(View.VISIBLE);
        }
    }

    private void getWeatherData() {
        try {
            mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 50, 10, mLocationListener);
            //mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        } catch (SecurityException e) {
            Log.e(TAG, e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    @Override
    public Loader<Alert> onCreateLoader(int id, Bundle args) {
        URL url = null;
        try {
            String tempUrl = alert_url + state_code + "/" + city + ".json";
            url = new URL(tempUrl);
        } catch (MalformedURLException e) {
            Log.e(TAG, e.getLocalizedMessage());
            e.printStackTrace();
        }
        return new AlertDataLoader(this, url);
    }

    @Override
    public void onLoadFinished(Loader<Alert> loader, Alert data) {
        saveAlert(data);
        updateUi(data);
    }

    @Override
    public void onLoaderReset(Loader<Alert> loader) {
    }

    private void saveAlert(Alert alert) {
        SharedPreferences sharedPref = getSharedPreferences(ALERT_SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong(PREFS_TIME_KEY, System.currentTimeMillis());
        editor.putString(PREFS_DESCRIPTION_KEY, alert.getDescription());
        editor.putString(PREFS_TYPE_KEY, alert.getType());
        editor.putString(PREFS_MESSAGEE_KEY, alert.getMessage());
        editor.putString(PREFS_DATE_KEY, alert.getDate());
        editor.putString(PREFS_EXPIRY_KEY, alert.getExpires());
        editor.commit();
    }
}

//    private class GetAlertData extends AsyncTask<URL, Void, Alert> {
//
//        @Override
//        protected Alert doInBackground(URL... params) {
//            URL url = params[0];
//            try {
//                String jsonResponse = QueryUtils.makeHttpRequest(url);
//                return QueryUtils.extractJsonData_Alert(jsonResponse);
//            } catch (IOException e) {
//                Log.e(TAG, "IOException");
//                return null;
//            }
//        }
//
//        @Override
//        protected void onPostExecute(Alert alert) {
//            updateUi(alert);
//        }
//    }

