/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.quakereport;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;

import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class EarthquakeActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<Earthquake>> {

    public static final String LOG_TAG = EarthquakeActivity.class.getName();
    private static final int EARTHQUAKE_LOADER_UNIQUE_ID = 1;
    /** TextView that is displayed when the list is empty */
    private TextView emptyStateTextView;


    //This query will provide you with the top 10 most recent
    //earthquakes in the world with at least a magnitude of 6.
    private static final String USGS_REQUEST_URL =
            "https://earthquake.usgs.gov/fdsnws/event/1/query";
    private EarthquakeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);

        ListView earthquakeListView = findViewById(R.id.list);
        emptyStateTextView =  findViewById(R.id.empty_list_text);
        earthquakeListView.setEmptyView(emptyStateTextView);

        // Create a new adapter that takes an empty list of earthquakes as input
        adapter = new EarthquakeAdapter(this, new ArrayList<Earthquake>());
        earthquakeListView.setAdapter(adapter);

        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Earthquake earthquake = adapter.getItem(position);
                Uri earthquakeSiteURI = Uri.parse(earthquake.getUrlString());
                Intent webIntent = new Intent(Intent.ACTION_VIEW, earthquakeSiteURI);
                startActivity(webIntent);
            }
        });

       //Checking connectivity
        ConnectivityManager cm =(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (isConnected){//if connected initialize the loader
            LoaderManager loaderManager=getLoaderManager();
            loaderManager.initLoader(EARTHQUAKE_LOADER_UNIQUE_ID,null,this);
        }
        else {
            //if there is no internet connection hide the progress bar and prompt the user
            ProgressBar progressBar = findViewById(R.id.progress_circular);
            progressBar.setVisibility(View.GONE);
            emptyStateTextView.setText(R.string.no_internet_connection);
        }
    }

    @Override
    public Loader<List<Earthquake>> onCreateLoader(int i, Bundle bundle) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String minMagnitude = sharedPrefs.getString(
                getString(R.string.settings_min_magnitude_key),
                getString(R.string.settings_min_magnitude_default));

        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default)
        );

        Uri baseUri = Uri.parse(USGS_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("format", "geojson");
        uriBuilder.appendQueryParameter("limit", "25");
        uriBuilder.appendQueryParameter("minmag", minMagnitude);
        uriBuilder.appendQueryParameter("orderby", orderBy);

        return new EarthquakeAsyncLoader(this, uriBuilder.toString());
    }


    @Override
    public void onLoadFinished(Loader<List<Earthquake>> loader, List<Earthquake> earthquakes) {
        ConnectivityManager cm =(ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo == null ) {
            emptyStateTextView.setText(R.string.no_internet_connection);
        } else if (networkInfo!=null && networkInfo.isConnected()){
            emptyStateTextView.setText(R.string.no_earthquakes);
        }
        ProgressBar progressBar = findViewById(R.id.progress_circular);
        progressBar.setVisibility(View.GONE);
        // Clear the adapter of previous earthquake data
        adapter.clear();
        // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (earthquakes != null && !earthquakes.isEmpty()) {
            adapter.addAll(earthquakes);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Earthquake>> loader) {
        adapter.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private static class EarthquakeAsyncLoader extends AsyncTaskLoader< List<Earthquake>> {

        private String url;
        public EarthquakeAsyncLoader(Context context,String url) {
            super(context);
            this.url=url;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Override
        public List<Earthquake> loadInBackground() {
            //Validating URL
            if ( url == null)
                return null;

            return QueryUtils.fetchEarthquakeData(url);
        }
    }
}
