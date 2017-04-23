package com.projects.shrihan.stormcast;

import android.content.Context;
import android.util.Log;
import android.support.v4.content.AsyncTaskLoader;

import java.io.IOException;
import java.net.URL;

/**
 * Created by shrihan on 3/30/2017.
 */

// loader for alerts
public class AlertDataLoader extends AsyncTaskLoader<Alert> {
    private URL mUrl;
    private static final String TAG = "AlertDataLoader";

    public AlertDataLoader(Context context, URL url) {
        super(context);
        mUrl = url;
     }

    public Alert loadInBackground() {
        if (mUrl == null)
            return null;

        try {
            String jsonResponse = QueryUtils.makeHttpRequest(mUrl);
            return QueryUtils.extractJsonData_Alert(jsonResponse);
        } catch (IOException e) {
            Log.e(TAG, e.getLocalizedMessage());
            return null;
        }
    }
}
