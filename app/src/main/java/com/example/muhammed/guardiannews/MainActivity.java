package com.example.muhammed.guardiannews;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
implements LoaderManager.LoaderCallbacks<List<News>>{

    private static final String LOG_TAG = MainActivity.class.getName();

    private static final String REST_API_HOST = "http://content.guardianapis.com/search";

    private static final String API_KEY = "3440f659-1cd5-45e1-a1bb-7dd1d5f29d30";

    private ListView mNewsListVeiw;

    private TextView mEmptyStateTextView;

    private ProgressBar mSpinner;

    private NewsAdapter mNewsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEmptyStateTextView = findViewById(R.id.empty_state_text_view);
        mSpinner = findViewById(R.id.loading_spinner);

        mNewsListVeiw = this.findViewById(R.id.news_list_view);
        mNewsAdapter = new NewsAdapter(this,new ArrayList<News>());
        mNewsListVeiw.setAdapter(mNewsAdapter);
        mNewsListVeiw.setEmptyView(mEmptyStateTextView);

        ConnectivityManager cm = (ConnectivityManager)
                this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (isConnected) {
            Log.v(LOG_TAG, "initLoader is now calling.");
            getLoaderManager().initLoader(0, null, this);
        } else {
            mEmptyStateTextView.setText(R.string.no_internet);
            mSpinner.setVisibility(View.GONE);
        }

    }

    @Override
    public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {
        Log.v(LOG_TAG, "onCreateLoader is starting.");
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String itemCount = sharedPrefs.getString(
                getString(R.string.max_result_key),
                getString(R.string.max_result_default));

        Uri baseUri = Uri.parse(REST_API_HOST);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("api-key",API_KEY);
        uriBuilder.appendQueryParameter("format","json");
        uriBuilder.appendQueryParameter("page-size",itemCount);

        return new NewsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> data) {
        Log.v(LOG_TAG, "onLoadFinished is starting.");
        //A result reached.
        mSpinner.setVisibility(View.GONE);
        //If no item it will appear.
        mEmptyStateTextView.setText("No news found on this topic.");
        //Clear old data.
        mNewsAdapter.clear();

        if(data != null) {
            mNewsAdapter.addAll(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        Log.v(LOG_TAG, "onLoaderReset is starting.");
        mNewsAdapter.clear();
    }

    //Adding settins to appbar.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.settings_section) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
