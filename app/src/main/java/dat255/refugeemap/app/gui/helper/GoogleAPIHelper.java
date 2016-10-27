package dat255.refugeemap.app.gui.helper;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import dat255.refugeemap.app.base.GoogleAPIListener;
import lombok.val;

/**
 * Contains methods for communication with the Google
 * API through the {@link GoogleApiClient}
 */
public class GoogleAPIHelper implements GoogleApiClient.ConnectionCallbacks,
	GoogleApiClient.OnConnectionFailedListener, LocationListener
{
	private static final String TAG = "GoogleAPIHelper";
	private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 20;
	private GoogleApiClient mGoogleApiClient;
	private final Context mContext;
	private LatLng currentLocation = new LatLng(57.70, 11.97);
	private final List<GoogleAPIListener>
		mGoogleAPIListenerList = new ArrayList<>();

	/**
	 * Constructor.
	 * @param context the current app context
   */
	public GoogleAPIHelper(Context context) {
		this.mContext = context;
		buildGoogleApiClient(mContext);
		connect();
	}

	/**
	 * @return the current location of the user on the
	 * {@link com.google.android.gms.maps.GoogleMap}
   */
	public LatLng getCurrentLocation() {
		return currentLocation;
	}

	public GoogleApiClient getGoogleApiClient() {
		return this.mGoogleApiClient;
	}

	/**
	 * connects the app to the Google API through the {@link GoogleApiClient}
	 */
	public void connect() {
		Log.d(TAG, "connect: connecting!");
		if (mGoogleApiClient != null) {
			mGoogleApiClient.connect();
		}
	}

	public void disconnect() {
		if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
			mGoogleApiClient.disconnect();
		}
	}

	public boolean isConnected() {
		return mGoogleApiClient != null && mGoogleApiClient.isConnected();
	}

	private void buildGoogleApiClient(Context mContext) {
		mGoogleApiClient = new GoogleApiClient.Builder(mContext)
			.addConnectionCallbacks(this)
			.addOnConnectionFailedListener(this)
			.addApi(LocationServices.API).build();

	}

	@Override
	public void onConnected(Bundle bundle) {
		//You are connected do what ever you want
		//Like i get last known location
		if (ContextCompat.checkSelfPermission(mContext,
			Manifest.permission.ACCESS_FINE_LOCATION)
			!= PackageManager.PERMISSION_GRANTED) {

			ActivityCompat.requestPermissions((Activity) mContext,
				new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
				MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

			// MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION is an
			// app-defined int constant. The callback method gets the
			// result of the request.
		} else {
			Location location = LocationServices.FusedLocationApi
				.getLastLocation(mGoogleApiClient);

			this.requestOnLocationChange();

			if (location != null) {
				this.currentLocation = new LatLng(location.getLatitude(),
					location.getLongitude());
				this.notifyConnectionListeners();
			}
		}
	}

	private void requestOnLocationChange() {

		if (ContextCompat.checkSelfPermission(mContext,
			Manifest.permission.ACCESS_FINE_LOCATION)
			== PackageManager.PERMISSION_GRANTED) {
			val mLocationManager = (LocationManager)mContext.
				getSystemService(Context.LOCATION_SERVICE);

			mLocationManager.requestLocationUpdates(LocationManager.
				GPS_PROVIDER, 1000, 1,
				new android.location.LocationListener()
				{
					@Override public void onLocationChanged(Location location)
					{
						currentLocation = new LatLng(location.getLatitude(),
							location.getLongitude());
					}

					@Override public void onStatusChanged(String provider,
						int status, Bundle extras) {}
					@Override public void onProviderEnabled(String provider) {}
					@Override public void onProviderDisabled(String provider) {}
				}
			);
		}

	}

	/**
	 * Enables the usage of fine position, if the app has been granted such permissions.
	 */
	public void notifyPositionPermissions(){
		if(ContextCompat.checkSelfPermission(mContext,
			Manifest.permission.ACCESS_FINE_LOCATION)
			== PackageManager.PERMISSION_GRANTED){

			Location location = LocationServices.FusedLocationApi
				.getLastLocation(mGoogleApiClient);
			if (location != null) {
				this.currentLocation = new LatLng(location.getLatitude(),
					location.getLongitude());
				this.notifyConnectionListeners();
			}
		}
	}

	@Override
	public void onConnectionSuspended(int i) {
		Log.d(TAG, "onConnectionSuspended: googleApiClient.connect()");
		mGoogleApiClient.connect();
	}

	@Override
	public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
		Log.d(TAG, "onConnectionFailed: connectionResult.toString() = " +
			connectionResult.toString());
	}

	@Override
	public void onLocationChanged(Location location) {
		Log.d(TAG, "onLocationChanged: " + location.toString());
	}

	public void addApiListener(GoogleAPIListener googleAPIListener) {
		mGoogleAPIListenerList.add(googleAPIListener);
	}

	private void notifyConnectionListeners() {
		for (int i = 0; i < mGoogleAPIListenerList.size(); i++) {
			mGoogleAPIListenerList.get(i).onAPIConnected(this);
		}
	}
}