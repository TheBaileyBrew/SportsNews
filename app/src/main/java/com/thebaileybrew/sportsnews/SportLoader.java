package com.thebaileybrew.sportsnews;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.List;

public class SportLoader extends AsyncTaskLoader<List<Sport>> {

    private static final String LOG_TAG = SportLoader.class.getName();

    private String mUrl;



    public SportLoader(Context context, String url) {
        super(context);
        mUrl = url;

    }

    @Override
    protected void onStartLoading() {
        Log.v("on start loader", "yes");
        forceLoad();
    }

    @Override
    public List<Sport> loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        List<Sport> SportsNews = QueryUtils.fetchNewsData(mUrl);

        Log.v("Loading on background", "yes");
        return SportsNews;
    }
}
