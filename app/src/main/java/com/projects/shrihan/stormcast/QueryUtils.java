package com.projects.shrihan.stormcast;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * Created by shrihan on 3/4/17.
 */

public final class QueryUtils {

    private static final String TAG = "QueryUtils";
    private static final int HTTP_RESPONSE_OK = 200;
    private static final int READ_TIMEOUT = 10000;
    private static final int CONNECTION_TIMEOUT = 10000;

    private static final String SAMPLE_ALERT = "{\n" +
            "  \"response\": {\n" +
            "    \"version\": \"0.1\",\n" +
            "    \"termsofService\": \"http:\\/\\/www.wunderground.com\\/weather\\/api\\/d\\/terms.html\",\n" +
            "    \"features\": {\n" +
            "      \"alerts\": 1\n" +
            "    }\n" +
            "  },\n" +
            "  \"alerts\": [\n" +
            "    {\n" +
            "      \"type\": \"HEA\",\n" +
            "      \"description\": \"Heat Advisory\",\n" +
            "      \"date\": \"11:14 am CDT on July 3, 2012\",\n" +
            "      \"date_epoch\": \"1341332040\",\n" +
            "      \"expires\": \"7:00 AM CDT on July 07, 2012\",\n" +
            "      \"expires_epoch\": \"1341662400\",\n" +
            "      \"message\": \"\\n...Heat advisory remains in effect until 7 am CDT Saturday...\\n\\n* temperature...heat indices of 100 to 105 are expected each \\n afternoon...as Max temperatures climb into the mid to upper \\n 90s...combined with dewpoints in the mid 60s to around 70. \\n Heat indices will remain in the 75 to 80 degree range at \\n night. \\n\\n* Impacts...the hot and humid weather will lead to an increased \\n risk of heat related stress and illnesses. \\n\\nPrecautionary\\/preparedness actions...\\n\\nA heat advisory means that a period of hot temperatures is\\nexpected. The combination of hot temperatures and high humidity\\nwill combine to create a situation in which heat illnesses are\\npossible. Drink plenty of fluids...stay in an air-conditioned\\nroom...stay out of the sun...and check up on relatives...pets...\\nneighbors...and livestock.\\n\\nTake extra precautions if you work or spend time outside. Know\\nthe signs and symptoms of heat exhaustion and heat stroke. Anyone\\novercome by heat should be moved to a cool and shaded location.\\nHeat stroke is an emergency...call 9 1 1.\\n\\n\\n\\nMjb\\n\\n\\n\",\n" +
            "      \"phenomena\": \"HT\",\n" +
            "      \"significance\": \"Y\",\n" +
            "      \"ZONES\": [\n" +
            "        {\n" +
            "          \"state\": \"UT\",\n" +
            "          \"ZONE\": \"001\"\n" +
            "        }\n" +
            "      ],\n" +
            "      \"StormBased\": {\n" +
            "        \"vertices\": [\n" +
            "          {\n" +
            "            \"lat\": \"38.87\",\n" +
            "            \"lon\": \"-87.13\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"lat\": \"38.89\",\n" +
            "            \"lon\": \"-87.13\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"lat\": \"38.91\",\n" +
            "            \"lon\": \"-87.11\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"lat\": \"38.98\",\n" +
            "            \"lon\": \"-86.93\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"lat\": \"38.87\",\n" +
            "            \"lon\": \"-86.69\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"lat\": \"38.75\",\n" +
            "            \"lon\": \"-86.3\"\n" +
            "          },\n" +
            "          {\n" +
            "            \"lat\": \"38.84\",\n" +
            "            \"lon\": \"-87.16\"\n" +
            "          }\n" +
            "        ],\n" +
            "        \"Vertex_count\": 7,\n" +
            "        \"stormInfo\": {\n" +
            "          \"time_epoch\": 1363464360,\n" +
            "          \"Motion_deg\": 243,\n" +
            "          \"Motion_spd\": 18,\n" +
            "          \"position_lat\": 38.9,\n" +
            "          \"position_lon\": -86.96\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  ]\n" +
            "}\n";

    private static final String SAMPLE_FORECAST = "{\n" +
            "  \"response\": {\n" +
            "  \"version\": \"0.1\",\n" +
            "  \"termsofService\": \"http://www.wunderground.com/weather/api/d/terms.html\",\n" +
            "  \"features\": {\n" +
            "  \"conditions\": 1\n" +
            "  }\n" +
            "  },\n" +
            "  \"current_observation\": {\n" +
            "  \"image\": {\n" +
            "  \"url\": \"http://icons-ak.wxug.com/graphics/wu2/logo_130x80.png\",\n" +
            "  \"title\": \"mWeather Underground\",\n" +
            "  \"link\": \"http://www.wunderground.com\"\n" +
            "  },\n" +
            "  \"display_location\": {\n" +
            "  \"full\": \"San Francisco, CA\",\n" +
            "  \"city\": \"San Francisco\",\n" +
            "  \"state\": \"CA\",\n" +
            "  \"state_name\": \"California\",\n" +
            "  \"country\": \"US\",\n" +
            "  \"country_iso3166\": \"US\",\n" +
            "  \"zip\": \"94101\",\n" +
            "  \"latitude\": \"37.77500916\",\n" +
            "  \"longitude\": \"-122.41825867\",\n" +
            "  \"elevation\": \"47.00000000\"\n" +
            "  },\n" +
            "  \"observation_location\": {\n" +
            "  \"full\": \"SOMA - Near Van Ness, San Francisco, California\",\n" +
            "  \"city\": \"SOMA - Near Van Ness, San Francisco\",\n" +
            "  \"state\": \"California\",\n" +
            "  \"country\": \"US\",\n" +
            "  \"country_iso3166\": \"US\",\n" +
            "  \"latitude\": \"37.773285\",\n" +
            "  \"longitude\": \"-122.417725\",\n" +
            "  \"elevation\": \"49 ft\"\n" +
            "  },\n" +
            "  \"estimated\": {},\n" +
            "  \"station_id\": \"KCASANFR58\",\n" +
            "  \"observation_time\": \"Last Updated on June 27, 5:27 PM PDT\",\n" +
            "  \"observation_time_rfc822\": \"Wed, 27 Jun 2012 17:27:13 -0700\",\n" +
            "  \"observation_epoch\": \"1340843233\",\n" +
            "  \"local_time_rfc822\": \"Wed, 27 Jun 2012 17:27:14 -0700\",\n" +
            "  \"local_epoch\": \"1340843234\",\n" +
            "  \"local_tz_short\": \"PDT\",\n" +
            "  \"local_tz_long\": \"America/Los_Angeles\",\n" +
            "  \"local_tz_offset\": \"-0700\",\n" +
            "  \"weather\": \"Partly Cloudy\",\n" +
            "  \"temperature_string\": \"66.3 F (19.1 C)\",\n" +
            "  \"temp_f\": 66.3,\n" +
            "  \"temp_c\": 19.1,\n" +
            "  \"relative_humidity\": \"65%\",\n" +
            "  \"wind_string\": \"From the NNW at 22.0 MPH Gusting to 28.0 MPH\",\n" +
            "  \"wind_dir\": \"NNW\",\n" +
            "  \"wind_degrees\": 346,\n" +
            "  \"wind_mph\": 22.0,\n" +
            "  \"wind_gust_mph\": \"28.0\",\n" +
            "  \"wind_kph\": 35.4,\n" +
            "  \"wind_gust_kph\": \"45.1\",\n" +
            "  \"pressure_mb\": \"1013\",\n" +
            "  \"pressure_in\": \"29.93\",\n" +
            "  \"pressure_trend\": \"+\",\n" +
            "  \"dewpoint_string\": \"54 F (12 C)\",\n" +
            "  \"dewpoint_f\": 54,\n" +
            "  \"dewpoint_c\": 12,\n" +
            "  \"heat_index_string\": \"NA\",\n" +
            "  \"heat_index_f\": \"NA\",\n" +
            "  \"heat_index_c\": \"NA\",\n" +
            "  \"windchill_string\": \"NA\",\n" +
            "  \"windchill_f\": \"NA\",\n" +
            "  \"windchill_c\": \"NA\",\n" +
            "  \"feelslike_string\": \"66.3 F (19.1 C)\",\n" +
            "  \"feelslike_f\": \"66.3\",\n" +
            "  \"feelslike_c\": \"19.1\",\n" +
            "  \"visibility_mi\": \"10.0\",\n" +
            "  \"visibility_km\": \"16.1\",\n" +
            "  \"solarradiation\": \"\",\n" +
            "  \"UV\": \"5\",\n" +
            "  \"precip_1hr_string\": \"0.00 in ( 0 mm)\",\n" +
            "  \"precip_1hr_in\": \"0.00\",\n" +
            "  \"precip_1hr_metric\": \" 0\",\n" +
            "  \"precip_today_string\": \"0.00 in (0 mm)\",\n" +
            "  \"precip_today_in\": \"0.00\",\n" +
            "  \"precip_today_metric\": \"0\",\n" +
            "  \"icon\": \"partlycloudy\",\n" +
            "  \"icon_url\": \"http://icons-ak.wxug.com/i/c/k/partlycloudy.gif\",\n" +
            "  \"forecast_url\": \"http://www.wunderground.com/US/CA/San_Francisco.html\",\n" +
            "  \"history_url\": \"http://www.wunderground.com/history/airport/KCASANFR58/2012/6/27/DailyHistory.html\",\n" +
            "  \"ob_url\": \"http://www.wunderground.com/cgi-bin/findweather/getForecast?query=37.773285,-122.417725\"\n" +
            "  }\n" +
            "}";

    static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        if (url == null)
            return null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(READ_TIMEOUT);
            urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
            urlConnection.connect();
            if (urlConnection.getResponseCode() == HTTP_RESPONSE_OK) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(TAG, "Input Stream Error");
            }
        } catch (IOException e) {
            // TODO: Handle the exception
            Log.e(TAG, e.getLocalizedMessage());
            e.printStackTrace();
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // function must handle java.io.IOException here
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    static Alert extractJsonData_Alert(String jsonResponse) {
        try {
            //jsonResponse = SAMPLE_ALERT;
            JSONObject rootObject = new JSONObject(jsonResponse);
            JSONArray alertsArray = rootObject.optJSONArray("alerts");
            try {
                JSONObject alertsObject = alertsArray.getJSONObject(0);//.optJSONObject(0);
                String type = alertsObject.optString("type");
                String description = alertsObject.optString("description");
                String date = alertsObject.optString("date");
                String expires = alertsObject.optString("expires");
                String message = alertsObject.optString("message");
                return new Alert(type, description, date, expires, message);
            } catch (Exception e) {
                return new Alert("", "No alerts", "", "", "");
            }
        } catch (JSONException e) {
            Log.e(TAG, e.getLocalizedMessage());
            e.printStackTrace();
            return null;
        }
    }

    static Forecast extractJsonData_Forecast(String jsonResponse) {
        try {
            //jsonResponse = SAMPLE_FORECAST;
            JSONObject rootObject = new JSONObject(jsonResponse);
            JSONObject current_observationObject = rootObject.optJSONObject("current_observation");
            JSONObject display_location = current_observationObject.optJSONObject("display_location");
            String area = display_location.optString("city") + ", " + display_location.optString("state");
            String weather = current_observationObject.optString("weather");
            String temperature = current_observationObject.optString("temperature_string");
            String relative_humidity = current_observationObject.optString("relative_humidity");
            String wind = current_observationObject.optString("wind_mph");
            if (!TextUtils.isEmpty(wind)) {
                wind += "mph";
            }
            String dewpoint = current_observationObject.optString("dewpoint_string");
            return new Forecast(area, weather, temperature, "Humidity: " + relative_humidity, "Wind Speed: " + wind,
                    "Dew point: " + dewpoint);
        } catch (JSONException e) {
            Log.e(TAG, "JsonException");
        }
        return null;
    }

    static NetworkInfo getNetworkInfo(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo;
    }
}
