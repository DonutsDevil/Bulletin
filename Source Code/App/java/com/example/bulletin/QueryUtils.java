package com.example.bulletin;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;


/*
* this class performs
* Connection to the API,
* Fetching JSON RESPONSE from the API,
* Extracting the JSON RESPONSE and adding it in the Array.
*/
public class QueryUtils {

    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    // we don't want to make instance of this class.
    private QueryUtils(){}


    public static ArrayList<News> fetchDataFromUrl(String URL_REQUEST){
        // create a url from the given String
        URL url = createURL(URL_REQUEST);
        String JSONResponse = "";
        try{
            // we will get a JSON response from the URL and if any error while parsing it will be catch'ed
            JSONResponse = MakeHTTPRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG,"Error closing Input Stream",e);
            e.printStackTrace();
        }
        // news Array List is filled from the JSONResponse and returned to fill the List in the ListView of the MainActivity
        ArrayList<News> news = extractNews(JSONResponse);
        return news;
    }

    private static URL createURL(String requestedUrl){
        URL url = null;
        try{
            url = new URL(requestedUrl);
        } catch (MalformedURLException e) {
            Log.i(LOG_TAG,"Problem in creating the URL: "+e);
            e.printStackTrace();
        }
        return url;
    }

    private static String MakeHTTPRequest(URL url) throws IOException {
        String JSONResponse ="";
        if (url == null){
            return JSONResponse;
        }
        HttpURLConnection URLConnection = null;
        InputStream inputStream = null;
        try{
            URLConnection = (HttpURLConnection) url.openConnection();
            URLConnection.setReadTimeout(10000/*millisecond*/);
            URLConnection.setConnectTimeout(15000/*millisecond*/);
            URLConnection.connect();

            // we only parse fill the JSONResponse if the Status we get is  OKAY i.e 200
            if(URLConnection.getResponseCode() == 200){
                inputStream = URLConnection.getInputStream();
                JSONResponse = readFromStream(inputStream);
            }

        } catch (IOException e) {
            Log.e(LOG_TAG,"Error while parsing  Input Stream from url",e);
            e.printStackTrace();
        }finally {
            if(URLConnection != null){
                URLConnection.disconnect();
            }

            if(inputStream!=null){
                inputStream.close();
            }
        }
        return  JSONResponse;
    }

    // Here the InputStream from the URL is Parsed And Converted into String.
    private static String readFromStream(InputStream urlJsonResponse) throws IOException {
        StringBuilder JSONResponse = new StringBuilder();
        if (urlJsonResponse != null){
            InputStreamReader isr = new InputStreamReader(urlJsonResponse, StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(isr);
            String line = br.readLine();
            while(line != null){
                JSONResponse.append(line);
                line = br.readLine();
            }
        }
        return JSONResponse.toString();
    }

    /**
     * Return a list of {@link News} objects that has been built up from
     * parsing a JSON response.
     */
    private static ArrayList<News> extractNews(String JSONResponse){
        // If the JSON string is empty or null, then return early.
        if (JSONResponse.isEmpty()){
            return null;
        }
        // Create an empty ArrayList that we can start adding earthquakes to
        ArrayList <News> news = new ArrayList<>();

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try{
            JSONObject root = new JSONObject(JSONResponse);
            JSONObject response = root.getJSONObject("response");
            JSONArray results = response.getJSONArray("results");
            for(int resultOBJ = 0 ; resultOBJ < results.length(); resultOBJ++){
                JSONObject resultObject = results.getJSONObject(resultOBJ);
                String newsType = resultObject.getString("sectionName");
                String newsDate = resultObject.getString("webPublicationDate");
                String newsTitle = resultObject.getString("webTitle");
                String newsUrl = resultObject.getString("webUrl");
                news.add(new News(newsType,newsTitle,newsDate,newsUrl));
            }
        } catch (JSONException e) {
            // any exception is caught here when parsing the Json Response.
            Log.i(LOG_TAG,"Problem parsing the earthquake JSON results: "+e);
            e.printStackTrace();
        }

        Log.i(LOG_TAG,"NEWS ARRAY LIST SIZE: "+news.size());
        return news;
    }
}
