package dat255.refugeemap.app.gui.helper;

import android.graphics.Color;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dat255.refugeemap.R;
import dat255.refugeemap.app.gui.App;
import lombok.val;

/**
 * Contains methods used for finding the shortest route
 * between the current position and an event.
 */
public class DirectionsHelper {

	private static final String API_DEST = "https://maps.googleapis.com/maps/api/directions/json?";
	private static final String API_KEY = "AIzaSyDUBRjMFZm7l7cvzLE1mTup3QI1qh4IoxM";
	private final GoogleMap mGoogleMap;
	private Polyline mCurrentDirection;

	/**
	 * Constructor.
	 * @param gm The {@link GoogleMap} used in the current running instance
	 */
	public DirectionsHelper(GoogleMap gm) {
		mGoogleMap = gm;
	}

    /*Note: Both walking and bicycling directions may sometimes not include
    clear pedestrian or bicycling paths, so these directions will return
    warnings in the returned result which you must display to the user.
    For further documentation on the matter, visit:
    https://developers.google.com/maps/documentation/directions/intro#TravelModes*/

	/**
	 * Main method used for finding and drawing the shortest route between
	 * two points on the map.
	 * @param origin The {@link LatLng} describing the point of origin
	 * @param destination The {@link LatLng} describing the destination
	 * @param transportationMode method of transportation. If invalid, defaults to driving
	 */
	public void showDirection(LatLng origin, LatLng destination,
		String transportationMode)
	{
		// Getting URL to the Google Directions API

		if (!isTransportationModeValid(transportationMode))
			transportationMode = "driving";

		String url = getURL(latLngToString(origin), latLngToString(destination),
			transportationMode);

		new FetchURLTask().execute(url);

		mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(origin));
		mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(13));
	}

	private boolean isTransportationModeValid(String mode)
	{
		return (mode.equals("driving") || mode.equals("walking") ||
			mode.equals("transit") || mode.equals("bicycling"));
	}

	private String latLngToString(LatLng latLng)
	{
		Double latitude = latLng.latitude;
		Double longitude = latLng.longitude;
		return latitude.toString() + ',' + longitude.toString();
	}


	private String getURL(String origin, String destination,
		String transportationMode)
	{
		return (API_DEST + "origin=" + origin + "&destination=" + destination +
			"&mode=" + transportationMode + "&key=" + API_KEY);
	}

	// Taken from:
	// http://www.androidtutorialpoint.com/intermediate/google-maps-draw-path-two-points-using-google-directions-google-map-android-api-v2/
	private class FetchURLTask extends AsyncTask<String, Void, String>
	{
		@Override protected String doInBackground(String... url)
		{
			// For storing data from web service
			String data = "";

			try
			{
				// Fetching the data from web service
				data = downloadUrl(url[0]);
			} catch (Exception e) {}
			return data;
		}

		@Override
		protected void onPostExecute(String result)
		{
			super.onPostExecute(result);

			ParserTask parserTask = new ParserTask();

			// Invokes the thread for parsing the JSON data
			parserTask.execute(result);
		}
	}

	// Taken from:
	// http://www.androidtutorialpoint.com/intermediate/google-maps-draw-path-two-points-using-google-directions-google-map-android-api-v2/
	private String downloadUrl(String strUrl)
	{
		String data = "";
		HttpURLConnection urlConnection = null;

		try
		{
			URL url = new URL(strUrl);

			// Creating an http connection to communicate with url
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.connect();

			val br = new BufferedReader(new InputStreamReader(urlConnection.
				getInputStream(),  "UTF-8"));

			StringBuilder sb = new StringBuilder();

			String line = "";
			while ((line = br.readLine()) != null) sb.append(line);

			data = sb.toString();
			br.close();
		} catch (IOException e) {}

		if (urlConnection != null) urlConnection.disconnect();

		return data;
	}

	// Taken from:
	// http://www.androidtutorialpoint.com/intermediate/google-maps-draw-path-two-points-using-google-directions-google-map-android-api-v2/
	private class ParserTask extends AsyncTask<String, Integer,
		List<List<HashMap<String, String>>>>
	{
		// Parsing the data in non-UI thread
		@Override protected List<List<HashMap<String, String>>>
			doInBackground(String... jsonData)
		{
			JSONObject jObject;
			List<List<HashMap<String, String>>> routes = null;

			try
			{
				jObject = new JSONObject(jsonData[0]);

				// Starts parsing data
				routes = parse(jObject);

			} catch (Exception e) { e.printStackTrace(); }

			return routes;
		}

		// Executes in UI thread after the parsing process
        // Taken from:
		// http://www.androidtutorialpoint.com/intermediate/google-maps-draw-path-two-points-using-google-directions-google-map-android-api-v2/
		// http://androidmapv2.blogspot.se/2013/11/driving-distance-and-travel-time.html
		@Override
		protected void onPostExecute(List<List<HashMap<String, String>>> result)
		{
			ArrayList<LatLng> points;
			PolylineOptions lineOptions = null;

			if (result != null)
			{
				// Traversing through all the routes
				for (int i = 0; i < result.size(); i++)
				{
					points = new ArrayList<>();
					lineOptions = new PolylineOptions();

					// Fetching i-th route
					List<HashMap<String, String>> path = result.get(i);

					// Fetching all the points in i-th route
					for (int j = 0; j < path.size(); j++) {
						HashMap<String, String> point = path.get(j);

						double lat = Double.parseDouble(point.get("lat"));
						double lng = Double.parseDouble(point.get("lng"));
						LatLng position = new LatLng(lat, lng);

						points.add(position);
					}

					// Adding all the points in the route to LineOptions
					lineOptions.addAll(points);
					lineOptions.width(10);
					lineOptions.color(Color.RED);
				}

				// Drawing polyline in the Google Map for the i-th route
				if (lineOptions != null)
					mCurrentDirection = mGoogleMap.addPolyline(lineOptions);

			}

			else
			{
				// No results from google directions api
				CharSequence text = App.getInstance().getResources().
					getText(R.string.toast_directions_error);

				Toast.makeText(App.getInstance().getApplicationContext(), text,
					Toast.LENGTH_LONG).show();
			}
		}
	}

	/**
	 * Removes the current {@link Polyline} direction
	 * so that another may be chosen
	 */
	public void removePreviousDirection()
	{ mCurrentDirection.remove(); }

	/**
	 * Returns true iff there is a {@link Polyline} direction in use.
	 */
	public boolean isPreviousDirectionPresent()
	{ return mCurrentDirection != null; }

	/**
	 * Receives a {@link JSONObject} and returns a list
	 * of lists containing latitude and longitude.
	 *
	 * Taken from:
	 * http://www.androidtutorialpoint.com/intermediate/google-maps-draw-path-two-points-using-google-directions-google-map-android-api-v2/
	 * http://androidmapv2.blogspot.se/2013/11/driving-distance-and-travel-time.html
	 */
	private List<List<HashMap<String,String>>> parse(JSONObject jObject)
	{
		List<List<HashMap<String, String>>> routes = new ArrayList<>();

		try
		{
			JSONArray jRoutes = jObject.getJSONArray("routes");

			/** Traversing all routes */
			for (int i = 0; i < jRoutes.length(); i++)
			{
				JSONArray jLegs = ((JSONObject)jRoutes.get(i)).getJSONArray("legs");
				List path = new ArrayList<>();

				/** Traversing all legs */
				for (int j = 0; j < jLegs.length(); j++)
				{
					JSONArray jSteps = ((JSONObject)jLegs.get(j)).
						getJSONArray("steps");

					/** Getting distance from the json data */
					JSONObject jDistance = ((JSONObject)jLegs.get(j)).
						getJSONObject("distance");
					HashMap<String, String> hmDistance = new HashMap<>();
					hmDistance.put("distance", jDistance.getString("text"));

					/** Getting duration from the json data */
					JSONObject jDuration = ((JSONObject)jLegs.get(j)).
						getJSONObject("duration");
					HashMap<String, String> hmDuration = new HashMap<>();
					hmDuration.put("duration", jDuration.getString("text"));

					/** Adding distance object to the path */
					path.add(hmDistance);

					/** Adding duration object to the path */
					path.add(hmDuration);

					/** Traversing all steps */
					for (int k=0;k<jSteps.length();k++){
						String polyline = "";
						polyline = (String)((JSONObject)((JSONObject)jSteps.
							get(k)).get("polyline")).get("points");
						List<LatLng> list = decodePoly(polyline);

						/** Traversing all points */
						for (int l = 0; l < list.size(); l++)
						{
							HashMap<String, String> hm = new HashMap<>();
							hm.put("lat", Double.toString((list.get(l)).
								latitude));
							hm.put("lng", Double.toString((list.get(l)).
								longitude));
							path.add(hm);
						}
					}

					routes.add(path);
				}
			}
		}
		catch (JSONException e) { e.printStackTrace(); }
		catch (Exception e) {}

		return routes;
	}

	/**
	 * Method to decode polyline points.
	 * Taken from:
	 * http://jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
	 */
	private List<LatLng> decodePoly(String encoded)
	{
		List<LatLng> poly = new ArrayList<>();
		int index = 0, len = encoded.length();
		int lat = 0, lng = 0;

		while (index < len) {
			int b, shift = 0, result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lat += dlat;

			shift = 0;
			result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lng += dlng;

			LatLng p = new LatLng((((double) lat / 1E5)),
				(((double) lng / 1E5)));
			poly.add(p);
		}

		return poly;
	}
}