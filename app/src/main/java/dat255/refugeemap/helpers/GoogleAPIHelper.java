package dat255.refugeemap.helpers;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
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

import dat255.refugeemap.GoogleAPIObserver;

public class GoogleAPIHelper implements GoogleApiClient.ConnectionCallbacks,
	GoogleApiClient.OnConnectionFailedListener, LocationListener {
	private static final String TAG = "GoogleAPIHelper";
	private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 20;
	private GoogleApiClient mGoogleApiClient;
	private Context mContext;
	private LatLng currentLocation;
	private List<GoogleAPIObserver> mGoogleAPIObserverList = new ArrayList<>();

	public GoogleAPIHelper(Context context) {
		this.mContext = context;
		buildGoogleApiClient(mContext);
		connect();
	}

	public LatLng getCurrentLocation() {
		return currentLocation;
	}

	public GoogleApiClient getGoogleApiClient() {
		return this.mGoogleApiClient;
	}

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

	public void addApiListener(GoogleAPIObserver googleAPIObserver) {
		mGoogleAPIObserverList.add(googleAPIObserver);
	}

	private void notifyConnectionListeners() {
		for (int i = 0; i < mGoogleAPIObserverList.size(); i++) {
			mGoogleAPIObserverList.get(i).onApiConnected(this);
		}
	}

}
