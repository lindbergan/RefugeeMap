package dat255.refugeemap;


import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import dat255.refugeemap.model.db.Database;
import dat255.refugeemap.model.db.Event;
import dat255.refugeemap.model.db.impl.DatabaseImpl;
import dat255.refugeemap.model.db.impl.EventArray;
import dat255.refugeemap.model.db.impl.JSONToolsImpl;


public class GMapFragment extends Fragment
	implements GoogleApiClient.ConnectionCallbacks,
	GoogleApiClient.OnConnectionFailedListener, LocationListener,
	GoogleMap.OnInfoWindowClickListener, OnMapReadyCallback,
	GoogleMap.InfoWindowAdapter {

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
	private EventArray mEventsList;
	private Database mDatabase;

	public interface ReplaceWithDetailView{
		void onInfoWindowClicked(Marker marker);
	}

	public GMapFragment(){

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

	@Override
	public void onMapReady(GoogleMap googleMap){
		mGoogleMap = googleMap;
		mGoogleMap.setOnInfoWindowClickListener(this);
		mGoogleMap.setInfoWindowAdapter(this);
		setCurrentPosition(mGoogleMap);
		getEvents();
	}

	public void placeMarkers(EventArray eventsList){
		for (Event e : eventsList) {
			newMarker(e);
		}
	}

	public void newMarker(Event event) {
		LatLng markerPosition = new LatLng(event.getLatitude(), event.getLongitude());
		MarkerOptions properties = new MarkerOptions();

		properties.position(markerPosition)
				  .title(event.getTitle())
				  .icon(BitmapDescriptorFactory.defaultMarker());
		// TODO: 2016-09-26 .getIcon needs to be implemented /Adrian

		mGoogleMap.addMarker(properties);
	}


	public void setCurrentPosition(GoogleMap googleMap){
		//TODO: set this to "currentLocation" and zoom in on that one

		LatLng gbgMarker = new LatLng(57.70887000, 11.97456000);//def of GBG
		googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(gbgMarker,15)); //zoom in on marker
	}

  /* When the infoWindow is clicked, we send a notification about which marker
  its about to the main activity. The main activity can then show the correct
  view, with the values associated to the marker object.
   */

	@Override
	public void onInfoWindowClick(Marker marker) {
		mCallback.onInfoWindowClicked(marker);
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

	@Override
	public View getInfoContents(Marker marker) {
		return null;
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
	}

	@Override
	public void onConnectionSuspended(int i) {
	}
}



