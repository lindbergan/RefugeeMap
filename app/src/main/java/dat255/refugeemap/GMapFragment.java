package dat255.refugeemap;


import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

import dat255.refugeemap.helpers.DirectionsHelper;
import dat255.refugeemap.helpers.GoogleAPIHelper;
import dat255.refugeemap.helpers.ViewHelper;
import dat255.refugeemap.model.db.Database;
import dat255.refugeemap.model.db.Event;

import static android.graphics.Bitmap.createBitmap;

public class GMapFragment extends Fragment
        implements GoogleServicesAdapter, AppDatabase.Listener, GoogleAPIObserver {

    private static final String TAG = "GMapFragment";
    ReplaceWithDetailView mCallback;
    Marker mCurrentMarker;
    private GoogleMap mGoogleMap;
    private List<Event> mEventsList;
    private Database mDatabase;
    private DirectionsHelper mDirectionHelper;
    private ViewHelper mViewHelper;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment.
     */
    public GMapFragment() {
        GoogleAPIHelper googleAPIHelper = App.getGoogleApiHelper();
        googleAPIHelper.addApiListener(this);
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
        setUpHelpers();
        getEvents();
        initiateListeners();
        if ((ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)) {
            mGoogleMap.setMyLocationEnabled(true);
            mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
        }

        configGoogleWidgets();
		placeMarkers(mEventsList);
		animateMapCamera(App.getGoogleApiHelper().getCurrentLocation());
	}

	@Override
	public void onApiConnected(GoogleAPIHelper googleAPIHelper) {
		animateMapCamera(googleAPIHelper.getCurrentLocation());
	}

	public void animateMapCamera(LatLng latLng) {
		mGoogleMap.moveCamera(
				CameraUpdateFactory.newLatLngZoom(latLng, 15));
	}

	public void setUpHelpers() {
		mDirectionHelper = new DirectionsHelper(mGoogleMap);
		mViewHelper = new ViewHelper(getActivity());
	}

	public void getEvents() {
		mEventsList = mDatabase.getAllEvents();
		placeMarkers(mEventsList);
	}

	public void initiateListeners() {
		mGoogleMap.setOnInfoWindowClickListener(this);
		mGoogleMap.setOnMarkerClickListener(this);
		mGoogleMap.setInfoWindowAdapter(this);
		mGoogleMap.setOnMapClickListener(this);
	}

	public void configGoogleWidgets() {
		mGoogleMap.getUiSettings().setMapToolbarEnabled(false);
        mGoogleMap.setPadding(0, 135, 0, 135);

		//TODO: add scale to the map
	}

	/* a method that creates markers from events and places them on the map*/
	public void placeMarkers(List<Event> eventsList) {
		if (eventsList != null) {
			for (Event e : eventsList) {
				newMarker(e);
			}
		}
	}

    public void newMarker(Event event) {
        LatLng markerPosition = new LatLng(
            event.getLatitude(), event.getLongitude());
        MarkerOptions properties = new MarkerOptions();

        BitmapDescriptor marker = createMarker(event.getCategories());

        properties.position(markerPosition)
            .title(event.getTitle())
            .icon(marker);
        // TODO: 2016-09-26 .getIcon needs to be implemented /Adrian

        Marker activeMarker = mGoogleMap.addMarker(properties);
        activeMarker.setTag(event);
    }

	@Override
	public boolean onMarkerClick(Marker marker) {

		//check to see if any previous direction already is displayed
		// - in that case, remove it
		if (mDirectionHelper.isPreviousDirectionPresent()) {
			mDirectionHelper.removePreviousDirection();
		}

		mCurrentMarker = marker;
		return false;
	}

    public BitmapDescriptor createMarker(Integer[] eventCategories){
        int category = eventCategories[0];
        Bitmap markerBitmap = createMarkerBitmap(category);

        if (eventCategories.length==2) {
            Bitmap markerBitmap2 = createMarkerBitmap(eventCategories[1]);
            markerBitmap = combineBitmaps(markerBitmap, markerBitmap2);
        }
        BitmapDescriptor marker = BitmapDescriptorFactory.fromBitmap(markerBitmap);
        return marker;
    }

    public void showDirections(LatLng origin, LatLng destination, String transportation) {
		mDirectionHelper.showDirection(
			origin, destination, transportation);
	}

    public Bitmap createMarkerBitmap(int category){
        //int id = getResources().getIdentifier("marker"+category, "drawable", this.getActivity().getPackageName());
        //Bitmap markerBitmap = BitmapFactory.decodeResource(getResources(), id);
        //return markerBitmap;
        Bitmap markerBitmap=null;
        switch(category){
            case 0:
                markerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.marker0);
                break;
            case 1:
                markerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.marker1);
                break;
            case 2:
                markerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.marker2);
                break;
            case 3:
                markerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.marker3);
                break;
        }
        return markerBitmap;
    }

    public Bitmap combineBitmaps(Bitmap b1, Bitmap b2){

        Bitmap bitmap = createBitmap(b1.getWidth(), b1.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas combo = new Canvas(bitmap);

        b1 = createBitmap(b1, 0, 0, b1.getWidth() / 2,b1.getHeight());
        b2 = createBitmap(b2, b2.getWidth()/2, 0, b2.getWidth()/2, b2.getHeight());

		combo.drawBitmap(b1, 0f, 0f, null);
		combo.drawBitmap(b2, b1.getWidth(), 0f, null);

		return bitmap;
	}

    @Override
    public void onMapClick(LatLng latLng) {

		//check if there is any directions present, in that case
		// - remove it
		if (mDirectionHelper.isPreviousDirectionPresent()) {
			mDirectionHelper.removePreviousDirection();
		}
	}

	/* When the infoWindow is clicked, we send a notification about which marker
	its about to the main activity. The main activity can then show the correct
	detailed view, with the values associated to the marker object.
	*/
	@Override
	public void onInfoWindowClick(Marker marker) {
		mCallback.onInfoWindowClicked(marker);
	}

	@Override
	public View getInfoWindow(Marker marker) {
		return null;
	}

	/* Our custom implementation of the infoWindow.*/
	@Override
	public View getInfoContents(Marker marker) {
		return mViewHelper.getCustomInfoView(marker);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try {
			AppDatabase.init(getActivity());
			mDatabase = AppDatabase.getDatabaseInstance();
			AppDatabase.addListener(this);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		MapFragment fragment = (MapFragment) getChildFragmentManager().
				findFragmentById(R.id.map);
		fragment.getMapAsync(this);
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Nullable
	@Override
	public View onCreateView(
			LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_gmap, container, false);
	}

	@Override
	public void onVisibleEventsChanged(List<Event> newEvents) {
		mGoogleMap.clear();
		mEventsList = newEvents;
		placeMarkers(mEventsList);
	}

	public interface ReplaceWithDetailView {
		void onInfoWindowClicked(Marker marker);
	}
}



