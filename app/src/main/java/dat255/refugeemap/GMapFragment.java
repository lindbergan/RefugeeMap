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
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class GMapFragment extends Fragment implements OnMapReadyCallback,
  GoogleApiClient.ConnectionCallbacks,
  GoogleApiClient.OnConnectionFailedListener, LocationListener {

  private static final String TAG = "GMapFragment";
  Location mLastLocation;
  private GoogleApiClient mGoogleApiClient;
  private LocationRequest mLocationRequest;
  private GoogleMap mGoogleMap;
  String lat, lon;
  private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 20;

  @Override
  public void onMapReady(GoogleMap googleMap) {

    Log.v(TAG, "mapready");

    this.mGoogleMap = googleMap;

    // Default map location is gothenburg
    LatLng gbgMarker = new LatLng(57.70887000, 11.97456000);//def of GBG

    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(gbgMarker, 15)); //zoom in on marker

    googleMap.addMarker(new MarkerOptions() //place the marker
      .position(gbgMarker)
      .title("Hello world"));


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
  public void onConnectionSuspended(int i) {
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    MapFragment fragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.map);
    fragment.getMapAsync(this);
  }


  @Override
  public void onLocationChanged(Location location) {
    lat = String.valueOf(location.getLatitude());
    lon = String.valueOf(location.getLongitude());
    updateUI();


  }


  public void updateUI() {
    Log.v(TAG, "Lat: " + lat + " Long: " + lon);

    LatLng userLocation = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));//def of GBG

    this.mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15)); //zoom in on marker
  }

  @Override
  public void onConnectionFailed(ConnectionResult connectionResult) {
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

}
