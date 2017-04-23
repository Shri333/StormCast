package com.projects.shrihan.stormcast;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import java.io.IOException;
import java.net.URL;

/**
 * Created by shrihan on 3/30/2017.
 */

// loader for forecast
public class ForecastLoader extends AsyncTaskLoader<Forecast> {
    private URL mUrl;
    private static final String TAG = "ForecastLoader";

    public ForecastLoader(Context context, URL url) {
        super(context);
        mUrl = url;
    }

    @Override
    public Forecast loadInBackground() {
        if (mUrl == null)
            return null;

        try {
            String jsonResponse = QueryUtils.makeHttpRequest(mUrl);
            return QueryUtils.extractJsonData_Forecast(jsonResponse);
        } catch (IOException e) {
            Log.e(TAG, "IOException");
            return null;
        }
    }
}
