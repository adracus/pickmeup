package com.pickmeupscotty.android.maps;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by axel on 07/03/15.
 */
public class GooglePlaces {
    private static final String LOG_TAG = GooglePlaces.class.getName();
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String TYPE_TEXTSEARCH = "/textsearch";
    private static final String TYPE_DISTANCEMATRIX = "/distancematrix";
    private static final String OUT_JSON = "/json";
    private static final String browserKey = "AIzaSyDQC2EsFulKdRM01JsdD5o_oOo5xrm4BBQ";

    public static ArrayList<String> autocompleteResults(String input) {
        ArrayList<String> resultList = null;

        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?key=" + browserKey);
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));
            URL url = new URL(sb.toString());

            JSONObject jsonObj = doJsonRequest(url);
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            resultList = new ArrayList<String>(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
            }
        } catch (IOException e) {
            return resultList;
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Cannot process JSON results", e);
        }

        return resultList;
    }


    public static ArrayList<Place> placesFor(String query) {
        ArrayList<Place> result = null;

        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_TEXTSEARCH + OUT_JSON);
            sb.append("?key=" + browserKey);
            sb.append("&query=" + URLEncoder.encode(query, "utf8"));
            URL url = new URL(sb.toString());

            JSONObject jsonObj = doJsonRequest(url);
            JSONArray placeResults = jsonObj.getJSONArray("results");

            // Extract the Place descriptions from the results
            result = new ArrayList<Place>(placeResults.length());
            for (int i = 0; i < placeResults.length(); i++) {
                result.add(new Place(placeResults.getJSONObject(i)));
            }
        } catch (IOException e) {
            return result;
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Cannot process JSON results", e);
        }

        return result;
    }

    public static Distance durationBetween(double fromLatitude, double fromLongitude, double toLatitude, double toLongitude) {
        Distance result = null;

        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_DISTANCEMATRIX + OUT_JSON);
            sb.append("?key=" + browserKey);
            sb.append("&origins=" + fromLatitude + "," + fromLongitude);
            sb.append("&destinations=" + toLatitude + "," + toLongitude);
            URL url = new URL(sb.toString());

            JSONObject jsonObj = doJsonRequest(url);
            JSONObject elem = jsonObj.getJSONArray("rows").getJSONObject(0);
            result = new Distance(elem);


        } catch (IOException e) {
            return result;
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Cannot process JSON results", e);
        }

        return result;
    }

    public static AsyncTask<String, Void, List<Place>> placesForAsync(String query) {
        return new AsyncPlacesRequest().execute(query);
    }


    public static AsyncTask<Double, Void, Distance> durationBetweenAsync(double fromLatitude, double fromLongitude, double toLatitude, double toLongitude) {
        Double[] params = new Double[4];
        params[0] = fromLatitude;
        params[1] = fromLongitude;
        params[2] = toLatitude;
        params[3] = toLongitude;

        return new AsyncDurationRequest().execute(params);
    }



    private static JSONObject doJsonRequest(URL url) {
        ArrayList<String> resultList = null;
        JSONObject jsonObj = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error connecting to Places API", e);
            return jsonObj;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            jsonObj = new JSONObject(jsonResults.toString());

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Cannot process JSON results", e);
        }

        return jsonObj;
    }

    public static class Place {
        private String name;
        private double latitude;
        private double longitude;

        public Place(String name, double latitude, double longitude) {
            this.name = name;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public Place(JSONObject json) throws JSONException {
            this.name = json.getString("name");
            JSONObject geometry = json.getJSONObject("geometry");
            JSONObject location = geometry.getJSONObject("location");
            this.latitude = location.getDouble("lat");
            this.longitude = location.getDouble("lng");
        }

        public String getName() {
            return name;
        }

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }
    }

    public static class Distance {
        private int seconds;
        private String durationText;
        private int meters;
        private String distanceText;

        public Distance(int seconds, String durationText, int meters, String distanceText) {
            this.seconds = seconds;
            this.durationText = durationText;
            this.meters = meters;
            this.distanceText = distanceText;
        }

        public Distance(JSONObject json) throws JSONException {
            JSONObject duration = json.getJSONObject("duration");
            this.seconds = duration.getInt("value");
            this.durationText = duration.getString("text");
            JSONObject distance = json.getJSONObject("distance");
            this.meters = distance.getInt("value");
            this.distanceText = distance.getString("text");
        }

        public int getSeconds() {
            return seconds;
        }

        public void setSeconds(int seconds) {
            this.seconds = seconds;
        }

        public String getDurationText() {
            return durationText;
        }

        public void setDurationText(String durationText) {
            this.durationText = durationText;
        }

        public int getMeters() {
            return meters;
        }

        public void setMeters(int meters) {
            this.meters = meters;
        }

        public String getDistanceText() {
            return distanceText;
        }

        public void setDistanceText(String distanceText) {
            this.distanceText = distanceText;
        }
    }
}
