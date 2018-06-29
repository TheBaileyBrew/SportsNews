package com.thebaileybrew.nflnews;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.List;

public class FootballLoader extends AsyncTaskLoader<List<Football>> {

    private static final String LOG_TAG = FootballLoader.class.getName();

    private String mUrl;



    public FootballLoader(Context context, String url) {
        super(context);
        mUrl = url;

    }

    @Override
    protected void onStartLoading() {
        Log.v("on start loader", "yes");
        forceLoad();
    }

    @Override
    public List<Football> loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        List<Football> GamesNews = QueryUtils.fetchNewsData(mUrl);

        Log.v("Loading on background", "yes");
        return GamesNews;
    }
}
