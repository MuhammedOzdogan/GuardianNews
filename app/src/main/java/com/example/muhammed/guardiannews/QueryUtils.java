package com.example.muhammed.guardiannews;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Muhammed on 2/21/2018.
 */

public class QueryUtils {

    /**
     * Tag for the log message
     */
    private static final String LOG_TAG = QueryUtils.class.getName();

    /**
     * NO one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directyly from the class name {@link QueryUtils} (and an object instance of QueryUtils is not
     * needed).
     */
    private QueryUtils(){

    }

    public static String makeHttpsRequest(URL url) throws IOException {
       //Data comes form of InputStream
        InputStream inputStream = null;

        //Connection object.
        HttpURLConnection httpURLConnection = null;

        //Text data readed from inputstream
        String result = "";
        try {
            httpURLConnection  =
                    (HttpURLConnection) url.openConnection();
            //Timeout for reading InputStream arbitrarily set to 3000ms.
            httpURLConnection.setReadTimeout(3000);
            //Timeout for httpsURLConnnection.connect() arbitrarily set to 3000ms.
            httpURLConnection.setConnectTimeout(3000);
            //For this use case, set HTTP method to GET.
            httpURLConnection.setRequestMethod("GET");
            //Already tru by default but setting just in case
            //needs to be true since this request is carrying an input(response) body.
            httpURLConnection.setDoInput(true);
            //Opens communications link (network traffic occurs here).
            httpURLConnection.connect();

            int responseCode = httpURLConnection.getResponseCode();

            if(responseCode != HttpsURLConnection.HTTP_OK) {
                throw new IOException("HTTP error code : " + responseCode);
            }

            //Retrive the response body as InputStream.
            inputStream = httpURLConnection.getInputStream();

            if (inputStream != null) {
                //Converts Stream to String.
                result = readFromInputStream(inputStream);
            }

        } finally {
            if(inputStream != null) {
                inputStream.close();
            }
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
        return result;
    }

    /**
     * @param urlString url address which connected in text form.
     * @return an URL object which contain connection address.
     */
    public static URL createURL(String urlString) {
        if(TextUtils.isEmpty(urlString)) {
            return null;
        }
        URL url;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG,  "Error with creating URL, url is : " + urlString, e);
            url = null;
        }
        return url;
    }

    public static String readFromInputStream(InputStream inputStream) throws IOException {

        StringBuilder result = new StringBuilder();

        if(inputStream == null) {
            return result.toString();
        }

        //Read InputStream usint the UTF-8 charset.
        InputStreamReader inputStreamReader =
                new InputStreamReader(inputStream, "UTF-8");

        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        String line = bufferedReader.readLine();

        while (line != null) {
            result.append(line);
            line = bufferedReader.readLine();
        }

        return result.toString();
    }

    public static List<News> fetchNews(String requestURL) {

        Log.v(LOG_TAG, "fetchNews is now calling.");

        URL url = createURL(requestURL);

        String response = null;

        try {
            response = makeHttpsRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error when connectioning to server.");
        }

        //Extract relevant fields from the JSON response and create an {@link News} list
        return  extractNews(response);

    }

    public static List<News> extractNews(String responseString) {

        if(TextUtils.isEmpty(responseString)) {
            return null;
        }

        List<News> newsList = new ArrayList<>();

        JSONObject root = null;
        JSONObject response = null;
        JSONArray results = null;

        String id = null;
        String type = null;
        String sectionId = null;
        String sectionName = null;
        Date webPublicationDate = null;
        String webTitle = null;
        String webUrl = null;
        String apiUrl = null;
        Boolean isHosted = null;
        String pillarId = null;
        String pillarName = null;

        try {
             root = new JSONObject(responseString);
        } catch (JSONException e) {
           Log.e(LOG_TAG, "Error when parising root JSON objecect ");
        }

        try {
             response = root.getJSONObject("response");
        } catch (JSONException e) {
           Log.e(LOG_TAG, "Error when parising root JSON objecect ");
        }

          try {
             results = response.getJSONArray("results");
        } catch (JSONException e) {
           Log.e(LOG_TAG, "Error when parising root JSON objecect ");
        }

        //Start extracting news data from results array.
        int length = results.length();

        for(int i = 0; i < length; i++) {
            try {
                JSONObject object = results.getJSONObject(i);
                id = extractStringFromJson(object,"id");
                type = extractStringFromJson(object,"type");
                sectionId = extractStringFromJson(object,"sectionId");
                sectionName = extractStringFromJson(object,"sectionName");
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

                webPublicationDate = formatter.parse(extractStringFromJson(object,"webPublicationDate").replaceAll("Z$", "+0000"));

                webTitle = extractStringFromJson(object,"webTitle");
                webUrl = extractStringFromJson(object,"webUrl");
                apiUrl = extractStringFromJson(object,"apiUrl");
                isHosted = Boolean.parseBoolean(extractStringFromJson(object,"isHosted"));
                pillarId = extractStringFromJson(object,"pillarId");
                pillarName = extractStringFromJson(object,"pillarName");

                newsList.add( new News(id, type, sectionId, sectionName,
                        webPublicationDate.getTime(), webTitle, webUrl, apiUrl, isHosted,
                        pillarId, pillarName));
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Error when parsing object in Json results.",e);
            } catch (ParseException e) {
                Log.e(LOG_TAG, "Error when parsing simple date object.",e);
            }
        }

        return newsList;

    }

    private static String extractStringFromJson(JSONObject jsonObject, String field) {
        String result;
        try {
            result = jsonObject.getString(field);
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error when parising" + field + "field.");
            result = null;
        }
        return result;
    }

}
