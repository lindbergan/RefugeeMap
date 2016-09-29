package dat255.refugeemap;


import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import dat255.refugeemap.model.db.Database;
import dat255.refugeemap.model.db.Event;
import dat255.refugeemap.model.db.impl.DatabaseImpl;
import dat255.refugeemap.model.db.impl.EventArray;
import dat255.refugeemap.model.db.impl.JSONToolsImpl;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GMapFragment extends Fragment
    implements GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener, LocationListener,
    GoogleMap.OnInfoWindowClickListener, OnMapReadyCallback,
    GoogleMap.InfoWindowAdapter, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener {

        // TODO: 2016-09-26 Build adapter that only inherits
        // methods that we really need instead of the of every method in these interfaces


    private GoogleMap mGoogleMap;
    ReplaceWithDetailView mCallback;
    private static final String TAG = "GMapFragment";
    Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    String lat, lon;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 20;
    private final String API_DEST = "https://maps.googleapis.com/maps/api/directions/json?";
    private final String API_KEY = "AIzaSyDUBRjMFZm7l7cvzLE1mTup3QI1qh4IoxM";
    Polyline mCurrentDirection;
    Marker mCurrentMarker;
    private EventArray mEventsList;
    private Database mDatabase;
    private String duration;
    private String distance;


    public interface ReplaceWithDetailView {
        void onInfoWindowClicked(Marker marker);
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment.
     */
    public GMapFragment() {
    }

    public static GMapFragment newInstance() {
        return new GMapFragment();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallback = (ReplaceWithDetailView) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                + " must implement ReplaceWithDetailView");
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mGoogleMap = googleMap;
        getEvents();
        initiateListeners();
        Log.v(TAG, "mapready");
        disableGoogleToolbar();
        setCurrentPosition();
        enableFindCurrentPosition();
        placeMarkers(mEventsList);
    }

    public void initiateListeners(){
        mGoogleMap.setOnInfoWindowClickListener(this);
        mGoogleMap.setOnMarkerClickListener(this);
        mGoogleMap.setInfoWindowAdapter(this);
        mGoogleMap.setOnMapClickListener(this);
    }

    public void disableGoogleToolbar(){
        mGoogleMap.getUiSettings().setMapToolbarEnabled(false);
    }

    /* a method that creates markers for the desired events. Should take
    events[] as argument*/
    public void placeMarkers(EventArray eventsList) {

        if(eventsList != null) {

            for (Event e : eventsList) {
                newMarker(e);
            }
        }

        //dummy Markers - Do not delete yet! need theese to confirm its working
        LatLng gbgMarker = new LatLng(57.70887000,11.97456000);//def of GBG
        mGoogleMap.addMarker(new MarkerOptions() //place the marker
            .position(gbgMarker)
            .title("Location1"));

        LatLng gbgMarker2 = new LatLng(57.908871000,12.000);
        mGoogleMap.addMarker(new MarkerOptions()
            .position(gbgMarker2).title("Location2")
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory
                .HUE_GREEN)));

        LatLng gbgMarker3 = new LatLng(57.908871000,11.90456000);
        mGoogleMap.addMarker(new MarkerOptions()
            .position(gbgMarker3).title("Location3")
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory
                .HUE_AZURE)));
    }

    /* A method that lets the user find his/her position by pressing the
    correspondig button in th UI */
    public void enableFindCurrentPosition(){

        //TODO: This should be our present location (has to do with our geolocation definition i guess):
        mGoogleMap.setMyLocationEnabled(true);

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
    }

    /* a method that places a marker at the position the user currently occupies*/
    public void setCurrentPosition() {
        //TODO: set this to "currentLocation" and zoom in on that one
        LatLng gbgMarker = new LatLng(57.70887000, 11.97456000);//def of GBG
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(gbgMarker, 15)); //zoom in on marker
    }

    @Override
    public void onMapClick(LatLng latLng) {

        //remove the "directions" button and corresponding textfield & the previous direction when user clicks on map
        hideDirectionViews();

        if (mCurrentDirection != null){
            removePreviousDirection();
        }
    }

    public void hideDirectionViews(){
        Button directionButton = (Button) getActivity().findViewById(R.id.directions_button);
        directionButton.setVisibility(View.GONE);

        TextView timeAndDistance = (TextView) getActivity().findViewById(R.id.info_time_and_distance);
        timeAndDistance.setVisibility(View.GONE);
    }

    /* A method that shows the "directions" button as well as the custom infoWindow when user clicks on marker */
    @Override
    public boolean onMarkerClick(Marker marker) {

        //TODO: show previously hidden "directions" button. That button - when clicked - should do the methods that follow:

        Button directionButton = (Button) getActivity().findViewById(R.id.directions_button);
        directionButton.setVisibility(View.VISIBLE);

        directionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDirectionButtonClicked();
            }
        });

        //check to see if any previous direction already is active - in that case, remove it
            if(mCurrentDirection != null) {
                removePreviousDirection();
            }

        mCurrentMarker = marker;
        return false;
    }

    public void onDirectionButtonClicked(){

        //NOTE: should be our current pos that is origin
        LatLng originLatLng = new LatLng(57.70887000,11.97456000);
        LatLng destinationLatLng = mCurrentMarker.getPosition();
        String transportation = "WillNotImplementRightNow";

        showDestination(originLatLng, destinationLatLng, transportation);
    }

    /* When the infoWindow is clicked, we send a notification about which marker
    its about to the main activity. The main activity can then show the correct
    detailed view, with the values associated to the marker object.
    */
	@Override
	public void onInfoWindowClick(Marker marker) {
            mCallback.onInfoWindowClicked(marker);
    }

    public void removePreviousDirection(){
        mCurrentDirection.remove();
    }

    public void showDestination(LatLng origin, LatLng destination, String transportation){

        //TODO: determine what mode of transportation to correctly estimate the time it takes & perhaps which waypoints to avoid
        // Getting URL to the Google Directions API

        String originAsString = latLngToString(origin);
        String destinationAsString = latLngToString(destination);

        String url = getUrl(originAsString, destinationAsString);
        Log.d("onMapClick", url);
        FetchUrl FetchUrl = new FetchUrl();

        // Start downloading json data from Google Directions API
        FetchUrl.execute(url);
        //move map camera
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(origin));
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(11));

    }

    public String latLngToString(LatLng latLng){
        Double latitude = latLng.latitude;
        Double longitude = latLng.longitude;
        return latitude.toString() +',' + longitude.toString();
    }


    public String getUrl(String origin, String destination){
        return API_DEST + "origin=" + origin + "&" + "destination=" + destination + "&" +"key=" + API_KEY;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            mDatabase = new DatabaseImpl(new InputStreamReader(getResources().openRawResource(R.raw.ctgs)),
                new InputStreamReader(getResources().openRawResource(R.raw.db)),
                new JSONToolsImpl());
        }
        catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    public void newMarker(Event event) {
        LatLng markerPosition = new LatLng(event.getLatitude(), event.getLongitude());
        MarkerOptions properties = new MarkerOptions();

        properties.position(markerPosition)
            .title(event.getTitle())
            .icon(BitmapDescriptorFactory.defaultMarker());
        // TODO: 2016-09-26 .getIcon needs to be implemented /Adrian

        Marker activeMarker = mGoogleMap.addMarker(properties);
        activeMarker.setTag(event);
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

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

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
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    /* Courtesy: http://www.androidtutorialpoint.com/intermediate/google-maps-draw-path-two-points-using-google-directions-google-map-android-api-v2/ */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask",jsonData[0].toString());
                DataParser parser = new DataParser();
                Log.d("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
                Log.d("ParserTask","Executing routes");
                Log.d("ParserTask",routes.toString());

            } catch (Exception e) {
                Log.d("ParserTask",e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        /* Courtesy: http://www.androidtutorialpoint.com/intermediate/google-maps-draw-path-two-points-using-google-directions-google-map-android-api-v2/
         * && http://androidmapv2.blogspot.se/2013/11/driving-distance-and-travel-time.html  */
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {

            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    if(j==0){    // Get distance from the list
                        distance = (String)point.get("distance");
                        continue;
                    }else if(j==1){ // Get duration from the list
                        duration = (String)point.get("duration");
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

                Log.d("onPostExecute","onPostExecute lineoptions decoded");
            }

            // Drawing polyline in the Google Map for the i-th route
            if(lineOptions != null) {

                mCurrentDirection = mGoogleMap.addPolyline(lineOptions);
                setDurationAndDistanceText();
            }
            else {
                Log.d("onPostExecute","without Polylines drawn");
            }
        }
    }

    public void setDurationAndDistanceText(){
        TextView timeAndDistance = (TextView) getActivity().findViewById(R.id.info_time_and_distance);
        timeAndDistance.setText("Distance: " + distance + " Duration:" + duration);
        timeAndDistance.setVisibility(View.VISIBLE);
    }


	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		MapFragment fragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.map);
		fragment.getMapAsync(this);
	}

	synchronized void buildGoogleApiClient() {
		mGoogleApiClient = new GoogleApiClient.Builder(this.getActivity())
			.addConnectionCallbacks(this)
			.addOnConnectionFailedListener(this)
			.addApi(LocationServices.API)
			.build();

	}

	@Override
	public void onConnected(@Nullable Bundle bundle) {
		Log.v(TAG, "connected");

		int permissionCheck = ContextCompat.checkSelfPermission(this.getActivity(),
			Manifest.permission.ACCESS_FINE_LOCATION);

		if (permissionCheck != -1) {
			this.mGoogleMap.setMyLocationEnabled(true);
		}

		if (ContextCompat.checkSelfPermission(this.getActivity(),
			Manifest.permission.ACCESS_FINE_LOCATION)
			!= PackageManager.PERMISSION_GRANTED) {

			ActivityCompat.requestPermissions(this.getActivity(),
				new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
				MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

			// MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION is an
			// app-defined int constant. The callback method gets the
			// result of the request.
		} else {
			getCurrentLocation();
		}

	}

	public void getCurrentLocation() {
		Log.v(TAG, "Getting Current Location");
		try {
			mLocationRequest = LocationRequest.create();
			mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
			mLocationRequest.setInterval(100); // Update location every second

			LocationServices.FusedLocationApi.requestLocationUpdates(this.mGoogleApiClient, mLocationRequest, this);

			mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
				mGoogleApiClient);

			Log.v(TAG, "Last Latitude: " + mLastLocation);

			if (mLastLocation != null) {
				lat = String.valueOf(mLastLocation.getLatitude());
				lon = String.valueOf(mLastLocation.getLongitude());
				updateUI();
			}

		} catch (SecurityException e) {
			Log.e(TAG, e.getMessage());
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
		Log.v(TAG, "Request Code: " + requestCode);
		switch (requestCode) {
			case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
				// If request is cancelled, the result arrays are empty.
				if (grantResults.length > 0
					&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {

					getCurrentLocation();

				} else {
					// TODO: Felmeddelande
				}
				return;
			}
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		lat = String.valueOf(location.getLatitude());
		lon = String.valueOf(location.getLongitude());
		//updateUI();
	}

	public void updateUI() {
		Log.v(TAG, "Lat: " + lat + " Long: " + lon);

		LatLng userLocation = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));//def of GBG

		this.mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15)); //zoom in on marker

	}


	@Override
	public void onStart() {
		super.onStart();
		buildGoogleApiClient();
		mGoogleApiClient.connect();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mGoogleApiClient.disconnect();
	}
	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_gmap, container, false);
	}

	public void getEvents() {
		mEventsList = (EventArray) mDatabase.getAllEvents();
		// TODO: 2016-09-26 Change to EventCollection /Adrian

		placeMarkers(mEventsList);
	}

	@Override
	public View getInfoWindow(Marker marker) {
		return null;
	}

  /* Our custom implementation of the infoWindow. should take Event as an argument and not marker*/
	@Override
	public View getInfoContents(Marker marker) {

        //TODO: get the associated event attached to the marker (this is done by using setTag() and getTag() on marker)

        //Fetching the custom infoView
        View customView = getActivity().getLayoutInflater().inflate(R.layout.custom_info_window, null);

        if(marker.getTag() != null) {
            Event activeEvent = (Event) marker.getTag();

            //extracting the text fields
            TextView infoTitle = (TextView) customView.findViewById(R.id.info_title);
            TextView infoTime = (TextView) customView.findViewById(R.id.info_time);
            TextView infoCategory = (TextView) customView.findViewById(R.id.info_category);
            TextView infoContactInfo = (TextView) customView.findViewById(R.id.info_contactInformation);

            //setting the values (should be obtained from the activeEvent we have extracted from marker)
            infoTitle.setText(activeEvent.getTitle());
            infoTime.setText("17.00-18-00");
            infoCategory.setText("Idrottsaktivitet");
            infoContactInfo.setText(activeEvent.getContactInformation());
        }

            //returning the custom_view with the correct values for the text fields
            return customView;

	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
	}

	@Override
	public void onConnectionSuspended(int i) {
	}
}



