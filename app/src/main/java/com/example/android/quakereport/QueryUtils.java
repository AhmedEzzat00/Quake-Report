
package com.example.android.quakereport;

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
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public final class QueryUtils {


    private static final String LOG_TAG = QueryUtils.class.getName();

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    public static List<Earthquake> fetchEarthquakeData(String requestURL) {
        URL url = createURL(requestURL);

        String jsonResponse = "";
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        return extractEarthquakesFromJSON(jsonResponse);
    }

    private static URL createURL(String stringURL) {
        URL url = null;
        try {
            url = new URL(stringURL);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error in building the URL ", e);
        }
        return url;
    }


    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        //Validating the URL
        if (url == null) return jsonResponse;

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setReadTimeout(15000);
            urlConnection.setRequestMethod("GET");//GET not Get watch out ;)
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromInputStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (ProtocolException e) {
            Log.e(LOG_TAG, "Error in Connection ", e);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error Retrieving the JSON Results ", e);
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
            if (inputStream != null)
                inputStream.close();
        }
        return jsonResponse;
    }

    private static String readFromInputStream(InputStream inputStream) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                stringBuilder.append(line);
                line = reader.readLine();
            }
        }
        return stringBuilder.toString();
    }

    /**
     * Return a list of {@link Earthquake} objects that has been built up from
     * parsing a JSON response.
     */
    private static List<Earthquake> extractEarthquakesFromJSON(String earthquakeJSON) {

        // if the JSON Response is empty or null
        if (TextUtils.isEmpty(earthquakeJSON)) return null;

        List<Earthquake> earthquakes = new ArrayList<>();
        try {
            JSONObject root = new JSONObject(earthquakeJSON);
            JSONArray features = root.optJSONArray("features");
            for (int i = 0; i < features.length(); i++) {

                //iterate throw each earthquake
                JSONObject currentEarthquake = features.optJSONObject(i);
                JSONObject properties = currentEarthquake.getJSONObject("properties");

                //extract the data
                double magnitude = properties.optDouble("mag");
                String location = properties.optString("place");
                long date = properties.optLong("time");
                String url = properties.optString("url");

                earthquakes.add(new Earthquake(magnitude, location, date, url));
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the earthquake JSON results", e);
        }

        // Return the list of earthquakes
        return earthquakes;
    }
}