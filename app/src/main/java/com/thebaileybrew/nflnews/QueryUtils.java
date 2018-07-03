package com.thebaileybrew.nflnews;

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
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class QueryUtils {
    //Tag for LOG message
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    private QueryUtils() { }
    //Query the Guardian dataset and return the list of (@link Football) objects
    public static List<Football> fetchNewsData(String requestedUrl) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
        //Create the URL object
        URL newsUrl = createUrl(requestedUrl);
        //Perform the HTTP Request
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(newsUrl);
        } catch (IOException ioe) {
            Log.e(LOG_TAG, "fetchNewsData: Problem making HTTP Request.", ioe);
        }
        //Extract the relevant fiels from the JSON response and create a list of Football news
        List<Football> FootballNews = extractNewsFromJson(jsonResponse);
        Log.v("Fetch Method", "Yes");
        return FootballNews;
    }
    //Returns a hew URL object from the given String URL
    private static URL createUrl(String requestedUrl) {
        URL url = null;
        try {
            url = new URL(requestedUrl);
        } catch (MalformedURLException mue) {
            Log.e(LOG_TAG, "createUrl: Problem building the URL", mue);
        }
        return url;
    }
    //Make an HTTP Request and return a string as response
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        //Checks the URL for null value
        if(url == null) {
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        //Try to create a url connection
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(12000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            //If request was successful (resp code 200) then read the input stream and parse the response.
            if(urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "makeHttpRequest: Error Code: "+ urlConnection.getResponseCode());
            }
        } catch (IOException ioe) {
            Log.e(LOG_TAG, "makeHttpRequest: Could not retrieve JSON results", ioe);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }
    //Convert the InputStream data into a String which contains the entire JSON response from API
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
    //Return the List<> extracted from the JSON data
    private static List<Football> extractNewsFromJson(String newsJSON) {
        String title;
        String date;
        String time;
        String source;
        String url;
        String author;
        //Checks the JSON string for empty/null -- returns early if null
        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }
        List<Football> FootballNews = new ArrayList<>();
        try {
            JSONObject baseJsonResponse = new JSONObject(newsJSON);
            Log.v("Json Array", "NewsArray");
            JSONObject baseJsonResponseResult = baseJsonResponse.getJSONObject("response");
            JSONArray currentNewsArray = baseJsonResponseResult.getJSONArray("results");
            // For each news in the newsArray, create an {@link News} object
            for (int i = 0; i < currentNewsArray.length(); i++) {
                JSONObject currentNews = currentNewsArray.getJSONObject(i);
                // Extract the value for the key called "webTitle" -- Article Title
                title = currentNews.getString("webTitle");
                Log.v("title", title);
                // Extract the value for the key called "pillarName" -- Article Category / Drawable
                source = currentNews.getString("pillarName");
                Log.v("category", source);
                // Extract the value for the key called "webUrl" -- Article URL Address for onClick
                url = currentNews.getString("webUrl");
                Log.v("url", url);
                // Extract the value for the key called "webPublicationDate" -- Article Date/Time
                String rawdate = currentNews.getString("webPublicationDate");
                Log.v("rawdate", rawdate);
                // Parse the Date/Time into separate text fields.
                String [] dateAndTime;
                dateAndTime = rawdate.split("T");
                date = dateAndTime[0];
                String [] rawTime = dateAndTime[1].split("Z");
                time = rawTime[0];
                Log.v("date", date);
                Log.v("time", time);
                //Extract the authors name
                JSONArray authorArray = currentNews.getJSONArray("tags");
                JSONObject currentAuthor = authorArray.getJSONObject(0);
                String authorName = currentAuthor.getString("webTitle");
                //Concatenation of author name and type of author (pulled from JSON)
                StringBuilder authorBuilder = new StringBuilder();
                authorBuilder.append(authorName);
                //Check for 2nd author
                if (authorArray.length() > 1 ){
                    JSONObject secondaryAuthor = authorArray.getJSONObject(1);
                    String secondAuthor = secondaryAuthor.getString("webTitle");
                    authorBuilder.append(" & ");
                    authorBuilder.append(secondAuthor);
                }
                author = authorBuilder.toString();
                //Add each record to the ArrayList
                FootballNews.add(new Football(title,date,time,url,source, author));
            }
        } catch (JSONException je) {
            Log.e(LOG_TAG, "Problem extracting JSON results ", je);
        }
        return FootballNews;
    }
}
