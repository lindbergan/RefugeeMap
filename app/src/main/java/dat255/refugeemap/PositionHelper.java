package dat255.refugeemap;

import android.Manifest;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

public class PositionHelper implements GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener, LocationListener {

    Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    String lat, lon;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 20;
    private GoogleMap mGoogleMap;
    private Fragment mGMapFragment;
    private static final String TAG = "GMapFragment";

    public PositionHelper(Fragment frag, GoogleMap googleMap) {
        mGMapFragment = frag;
        mGoogleMap = googleMap;
        Log.v(TAG, "mapready");
    }

    synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(
            mGMapFragment.getActivity()).addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this).addApi(LocationServices.API)
            .build();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.v(TAG, "connected");

        int permissionCheck = ContextCompat.checkSelfPermission(
            mGMapFragment.getActivity(),
            Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheck != -1) {
            mGoogleMap.setMyLocationEnabled(true);
        }

        if (ContextCompat.checkSelfPermission(mGMapFragment.getActivity(),
            Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mGMapFragment.getActivity(),
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
            mLocationRequest.setPriority(
                LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(100); // Update location every second

            LocationServices.FusedLocationApi.requestLocationUpdates(
                this.mGoogleApiClient, mLocationRequest, this);

            mLastLocation = LocationServices.FusedLocationApi.
                getLastLocation(mGoogleApiClient);

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

    //@Override
    public void onRequestPermissionsResult(
        int requestCode, String permissions[], int[] grantResults) {
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

        LatLng userLocation = new LatLng(
            Double.parseDouble(lat), Double.parseDouble(lon));

        this.mGoogleMap.moveCamera(
            CameraUpdateFactory.newLatLngZoom(userLocation, 15));
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    public GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }
}
