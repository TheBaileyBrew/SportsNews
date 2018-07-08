package com.thebaileybrew.sportsnews;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Sport>> {
    private static final String LOG_TAG = MainActivity.class.getName();

    // Swipe Refresh Layout
    SwipeRefreshLayout mySwipeRefreshLayout;
    SharedPreferences sharedPrefs;
    // URL for request API data
    private static final String requestUrl = "https://content.guardianapis.com/search";
    // Constant value for Loader reference
    private static final int FOOTBALL_LOADER_ID = 1;

    private SportsAdapter sportsAdapter;
    RecyclerView footballListView;
    RelativeLayout emptyLayout;
    RelativeLayout noActiveNetwork;
    RelativeLayout noNewsAvailable;
    RelativeLayout loadingScreen;
    LinearLayout filtersDisplay;
    ImageView noNewsDrawable;
    ImageView noNetworkDrawable;
    ImageView emptyDrawable;
    TextView filteredStartDate;
    TextView filteredEndDate;
    TextView filteredOrderBy;
    Boolean correctDateFormat;
    String startDate;
    String endDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        noNewsDrawable = findViewById(R.id.no_news_drawable);
        noNetworkDrawable = findViewById(R.id.no_network_drawable);
        emptyDrawable = findViewById(R.id.empty_drawable);
        mySwipeRefreshLayout = findViewById(R.id.swipe_refresh);
        filtersDisplay = findViewById(R.id.filters_display);
        filteredStartDate = findViewById(R.id.main_filter_start_date);
        filteredEndDate = findViewById(R.id.main_filter_end_date);
        filteredOrderBy = findViewById(R.id.main_filter_sort_by);
        mySwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(LOG_TAG, "onRefresh: ");
                getLoaderManager().destroyLoader(FOOTBALL_LOADER_ID);
                checkForNetwork();
            }
        });
        noActiveNetwork = findViewById(R.id.no_network_layout);
        noNewsAvailable = findViewById(R.id.no_news_layout);
        loadingScreen = findViewById(R.id.loading_layout);
        footballListView = findViewById(R.id.nfl_news_list);
        emptyLayout = findViewById(R.id.empty_layout);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        sportsAdapter = new SportsAdapter(this,new ArrayList<Sport>());
        footballListView.setLayoutManager(layoutManager);
        footballListView.setAdapter(sportsAdapter);


        checkForNetwork();
    }

    //Updates the display if there is a network available
    private void checkForNetwork() {
        if (isNetworkAvailable()) {
            noActiveNetwork.setVisibility(View.INVISIBLE);
            footballListView.setVisibility(VISIBLE);
            getDataRefresh();
        } else {
            getLoaderManager().destroyLoader(FOOTBALL_LOADER_ID);
            loadingScreen.setVisibility(GONE);
            noActiveNetwork.setVisibility(View.VISIBLE);
            noNewsAvailable.setVisibility(View.INVISIBLE);
            footballListView.setVisibility(View.INVISIBLE);
            mySwipeRefreshLayout.setRefreshing(false);
        }
    }
    //Creates the Loader
    private void getDataRefresh() {
        getLoaderManager().destroyLoader(FOOTBALL_LOADER_ID);
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(FOOTBALL_LOADER_ID, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.settings:
                Intent settingsIntent = new Intent(this, FootballSettings.class);
                startActivity(settingsIntent);
                return true;
            case R.id.refresh:
                mySwipeRefreshLayout.setRefreshing(true);
                checkForNetwork();
                getDataRefresh();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public Loader<List<Sport>> onCreateLoader(int id, Bundle args) {
        String pageSize = "20";
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        // Retrieve string values from SharedPreferences. Second parameter is the default value.
        startDate = sharedPrefs.getString(
                getString(R.string.settings_start_date_key),
                getString(R.string.settings_start_date_default));
        checkForDateFormatting(startDate);
        if(!correctDateFormat) {
            startDate = getString(R.string.settings_start_date_default);
        }
        endDate = sharedPrefs.getString(
                getString(R.string.settings_end_date_key),
                getString(R.string.settings_end_date_default));
        checkForDateFormatting(endDate);
        if(!correctDateFormat) {
            endDate = getString(R.string.settings_end_date_default);
        }
        String category = sharedPrefs.getString(getString(R.string.settings_category_key),
                getString(R.string.settings_category_default));
        assignFilterValues(startDate, endDate, category);

        Uri baseUri = Uri.parse(requestUrl);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("from-date", startDate);
        uriBuilder.appendQueryParameter("to-date", endDate);
        uriBuilder.appendQueryParameter("api-key", "9722bfef-08bf-4706-b3f0-a914a1dc5339");
        uriBuilder.appendQueryParameter("q", category);
        uriBuilder.appendQueryParameter("show-tags", "contributor");
        uriBuilder.appendQueryParameter("page-size", "20");

        return new SportLoader(this, uriBuilder.toString());
    }

    private void assignFilterValues(String startDate, String endDate, String category) {
        if (startDate.equals(getString(R.string.startDefault))
                && endDate.equals(getString(R.string.endDefault))
                && category.equals(getString(R.string.categoryDefault))) {
            filtersDisplay.setVisibility(GONE);
        } else {
            filtersDisplay.setVisibility(VISIBLE);
            filteredStartDate.setText(startDate);
            filteredEndDate.setText(endDate);
            filteredOrderBy.setText(category);
        }
        assignDrawableResource(category);
    }

    private void assignDrawableResource(String category) {
        int intRes = 0;
        switch(category) {
            case "NFL":
                intRes = R.drawable.nfl_logo;
                break;
            case "Football":
                intRes = R.drawable.fifa_logo;
                break;
            case "Baseball":
                intRes = R.drawable.mlb_logo;
                break;
            case "Hockey":
                intRes = R.drawable.nhl_logo;
                break;
            case "Basketball":
                intRes = R.drawable.nba_logo;
                break;
        }
        noNewsDrawable.setImageResource(intRes);
        noNetworkDrawable.setImageResource(intRes);
        emptyDrawable.setImageResource(intRes);
    }

    //Checks the date value in EditText - If date
    private void checkForDateFormatting(String submitDate) {
        String datePattern = "\\d{4}-\\d{2}-\\d{2}";
        if(submitDate.equals(" ")) {
            //Toast.makeText(this, "Please enter a valid date range YYYY-MM-DD", Toast.LENGTH_SHORT).show();
            correctDateFormat = false;
        }
        if(!submitDate.matches(datePattern) && !submitDate.equals(" ")) {
            //Toast.makeText(this, "Verify Date Formatting YYYY-MM-DD", Toast.LENGTH_SHORT).show();
            correctDateFormat = false;
        } else {
            //Toast.makeText(this, "Date of " + submitDate + " is valid", Toast.LENGTH_SHORT).show();
            correctDateFormat = true;
        }
    }

    @Override
    public void onLoadFinished(Loader<List<Sport>> loader, List<Sport> sportNews) {
        sportsAdapter = new SportsAdapter(this, sportNews);
        footballListView.setAdapter(sportsAdapter);
        if (!isNetworkAvailable()) {
            footballListView.setVisibility(View.INVISIBLE);
            //set NO Network Display
        }

        //Check for valid dataset
        //add to adapter data set. This will trigger ListView to update
        //Rechecks for Network availability
        if (isNetworkAvailable()) {
            loadingScreen.setVisibility(View.INVISIBLE);
            noActiveNetwork.setVisibility(View.INVISIBLE);
            noNewsAvailable.setVisibility(View.INVISIBLE);
            if (sportNews == null) {
                loadingScreen.setVisibility(View.INVISIBLE);
                noActiveNetwork.setVisibility(View.INVISIBLE);
                noNewsAvailable.setVisibility(VISIBLE);
            } else {
                noActiveNetwork.setVisibility(View.INVISIBLE);
                noNewsAvailable.setVisibility(View.INVISIBLE);
            }
            //Checks to verify that ArrayList has data
            if (sportNews != null && !sportNews.isEmpty()) {
                loadingScreen.setVisibility(View.INVISIBLE);
                noActiveNetwork.setVisibility(View.INVISIBLE);
                noNewsAvailable.setVisibility(View.INVISIBLE);
                mySwipeRefreshLayout.setRefreshing(false);
            }
        } else if (!isNetworkAvailable()) {
            loadingScreen.setVisibility(View.INVISIBLE);
            noActiveNetwork.setVisibility(VISIBLE);
            noNewsAvailable.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Sport>> loader) {

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

