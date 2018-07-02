package com.thebaileybrew.nflnews;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Football>> {
    private static final String LOG_TAG = MainActivity.class.getName();

    private static final String REQUEST_URL_ONE =
            "https://content.guardianapis.com/search?from-date=2018-06-01&to-date=2018-07-01&q=NFL&api-key=9722bfef-08bf-4706-b3f0-a914a1dc5339";

    private FootballAdapter footballAdapter;
    private static final int FOOTBALL_LOADER_ID = 1;

    ListView footballListView;
    RelativeLayout noActiveNetwork;
    RelativeLayout loadingScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        noActiveNetwork = findViewById(R.id.no_network_layout);
        loadingScreen = findViewById(R.id.loading_layout);
        footballListView = findViewById(R.id.nfl_news_list);

        footballAdapter = new FootballAdapter(this,new ArrayList<Football>());
        footballListView.setAdapter(footballAdapter);

        footballListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Football currentFootballNews = footballAdapter.getItem(position);

                Uri footballUri = Uri.parse(currentFootballNews.getArticleUrl());
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, footballUri);
                startActivity(websiteIntent);
            }
        });

        if (isNetworkAvailable()) {
            LoaderManager loaderManager = getLoaderManager();

            loaderManager.initLoader(FOOTBALL_LOADER_ID, null, this);
        } else {
            loadingScreen.setVisibility(GONE);

            footballListView.setEmptyView(noActiveNetwork);
        }





    }

    @Override
    public Loader<List<Football>> onCreateLoader(int id, Bundle args) {
        return new FootballLoader(this, REQUEST_URL_ONE);
    }

    @Override
    public void onLoadFinished(Loader<List<Football>> loader, List<Football> FootballNews) {
        if (!isNetworkAvailable()) {
            footballListView.setEmptyView(noActiveNetwork);
            //set NO Network Display
        }
        footballAdapter.clear();

        //Check for valid dataset
        //add to adapter data set. This will trigger ListView to update
        if (isNetworkAvailable()) {
            loadingScreen.setVisibility(View.INVISIBLE);
            noActiveNetwork.setVisibility(View.INVISIBLE);
        }
        if (FootballNews != null && !FootballNews.isEmpty()) {
            loadingScreen.setVisibility(View.INVISIBLE);
            noActiveNetwork.setVisibility(View.INVISIBLE);
        }
        noActiveNetwork.setVisibility(View.INVISIBLE);
        footballAdapter.addAll(FootballNews);



    }

    @Override
    public void onLoaderReset(Loader<List<Football>> loader) {
        footballAdapter.clear();
    }

    public boolean isNetworkAvailable(){
        ConnectivityManager connectionCheck = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = connectionCheck.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnected();

        if (isConnected) {
            return true;
        } else {
            return false;
        }
    }
}

