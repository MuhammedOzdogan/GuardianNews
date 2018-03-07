package com.example.muhammed.guardiannews;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.List;

/**
 * Created by Muhammed on 2/21/2018.
 */

public class NewsLoader extends AsyncTaskLoader<List<News>>{

    private static String LOG_TAG = NewsLoader.class.getSimpleName();

    private String url;

    public NewsLoader(Context context, String url) {
        super(context);
        this.url = url;
    }

    @Override
    public List<News> loadInBackground() {
        Log.v(LOG_TAG, "loadInBackground is now called.");
        return QueryUtils.fetchNews(url);
    }

    @Override
    protected void onStartLoading() {
        Log.v(LOG_TAG, "onStartLoading is now called");
        forceLoad();
    }
}
