package dat255.refugeemap.helpers;

import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dat255.refugeemap.App;
import dat255.refugeemap.R;
import dat255.refugeemap.helpers.DataParser;

public class DirectionsHelper {

    private static final String API_DEST = "https://maps.googleapis.com/maps/api/directions/json?";
    private static final String API_KEY = "AIzaSyDUBRjMFZm7l7cvzLE1mTup3QI1qh4IoxM";
    private GoogleMap mGoogleMap;
    private String duration;
    private String distance;
    private Polyline mCurrentDirection;

    public DirectionsHelper(GoogleMap googleMap) {
        mGoogleMap = googleMap;
    }

    /*Note: Both walking and bicycling directions may sometimes not include
    clear pedestrian or bicycling paths, so these directions will return
    warnings in the returned result which you must display to the user.
    For further documentation on the matter, visit:
    https://developers.google.com/maps/documentation/directions/intro#TravelModes*/
    public void showDirection(
        LatLng origin, LatLng destination, String transportationMode) {

        // Getting URL to the Google Directions API

        String validTransportMode;

        if(isTransportationModeValid(transportationMode)){
            validTransportMode = transportationMode;
        }
        else{
            validTransportMode = "driving"; //default
        }

        String originAsString = latLngToString(origin);
        String destinationAsString = latLngToString(destination);

        String url = getUrl(originAsString, destinationAsString, validTransportMode);
        Log.d("onMapClick", url);
        FetchUrl FetchUrl = new FetchUrl();

        // Start downloading json data from Google Directions API
        FetchUrl.execute(url);
        //move map camera
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(origin));
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(13));

    }
    //NOTE: valid transportation modes are "driving", "walking", "bicycling" or "transit".
    public boolean isTransportationModeValid(String transportationMode){

        return (transportationMode.equals("driving") ||
            transportationMode.equals("walking") ||
            transportationMode.equals("transit") ||
            transportationMode.equals("bicycling"));
    }

    public String latLngToString(LatLng latLng) {
        Double latitude = latLng.latitude;
        Double longitude = latLng.longitude;
        return latitude.toString() + ',' + longitude.toString();
    }


    public String getUrl(String origin, String destination, String transportationMode) {
        return API_DEST + "origin=" + origin + "&" + "destination="
            + destination + "&" + "mode=" + transportationMode + "&" + "key=" + API_KEY;
    }

    // Fetches data from url passed
    /* Courtesy: http://www.androidtutorialpoint.com/intermediate/google-maps-draw-path-two-points-using-google-directions-google-map-android-api-v2/ */

    private class FetchUrl extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("Background Task data", data.toString());
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /* Courtesy: http://www.androidtutorialpoint.com/intermediate/google-maps-draw-path-two-points-using-google-directions-google-map-android-api-v2/ */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(
                new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            Log.d("downloadUrl", data.toString());
            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            if (iStream != null) iStream.close();
            if (urlConnection != null) urlConnection.disconnect();
        }
        return data;
    }

    /* Courtesy: http://www.androidtutorialpoint.com/intermediate/google-maps-draw-path-two-points-using-google-directions-google-map-android-api-v2/ */
    private class ParserTask extends AsyncTask<String, Integer,
        List<List<HashMap<String, String>>>> {
        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(
            String... jsonData) {
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask", jsonData[0].toString());
                DataParser parser = new DataParser();
                Log.d("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
                Log.d("ParserTask", "Executing routes");
                Log.d("ParserTask", routes.toString());
                
            } catch (Exception e) {
                Log.d("ParserTask", e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        /* Courtesy: http://www.androidtutorialpoint.com/intermediate/google-maps-draw-path-two-points-using-google-directions-google-map-android-api-v2/
         * && http://androidmapv2.blogspot.se/2013/11/driving-distance-and-travel-time.html  */
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result)
        {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;

            if (result != null) {

                // Traversing through all the routes
                for (int i = 0; i < result.size(); i++) {
                    points = new ArrayList<>();
                    lineOptions = new PolylineOptions();

                    // Fetching i-th route
                    List<HashMap<String, String>> path = result.get(i);

                    // Fetching all the points in i-th route
                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);

                        if (j == 0) {    // Get distance from the list
                            distance = point.get("distance");
                            continue;
                        } else if (j == 1) { // Get duration from the list
                            duration = point.get("duration");
                            continue;
                        }

                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);

                        points.add(position);
                    }

                    // Adding all the points in the route to LineOptions
                    lineOptions.addAll(points);
                    lineOptions.width(10);
                    lineOptions.color(Color.RED);

                    Log.d("onPostExecute", "onPostExecute lineoptions decoded");
                }

                // Drawing polyline in the Google Map for the i-th route
                if (lineOptions != null) {
                    mCurrentDirection = mGoogleMap.addPolyline(lineOptions);
                } else {
                    Log.d("onPostExecute", "without Polylines drawn");
                }

            } else {
                // No results from google directions api
                CharSequence text = App.getInstance().getResources().getText
                    (R.string.directions_error);
                int duration = Toast.LENGTH_LONG;

                Toast toast = Toast.makeText(App.getInstance().getApplicationContext(), text,
                    duration);
                toast.show();

            }
        }
    }

    public void removePreviousDirection() {
        mCurrentDirection.remove();
    }

    public boolean isPreviousDirectionPresent() {
        if (mCurrentDirection != null) {
            return true;
        } else {
            return false;
        }
    }

    public String getDuration() {
        return duration;
    }

    public String getDistance() {
        return distance;
    }
}
