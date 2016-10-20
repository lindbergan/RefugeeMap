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
import java.util.ArrayList;
import java.util.List;

import dat255.refugeemap.helpers.DirectionsHelper;
import dat255.refugeemap.helpers.GoogleAPIHelper;
import dat255.refugeemap.helpers.ViewHelper;
import dat255.refugeemap.model.db.Database;
import dat255.refugeemap.model.db.Event;
import dat255.refugeemap.model.db.impl.FilterImpl;
import dat255.refugeemap.model.db.sort.EventsSorter;

import static android.graphics.Bitmap.createBitmap;

/**
 * A {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GMapFragment.OnMapFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GMapFragment extends Fragment
        implements GoogleServicesAdapter, AppDatabase.VisibleEventsListener, GoogleAPIObserver {

    private static final String TAG = "GMapFragment";
		OnMapFragmentInteractionListener mCallback;
    Marker mCurrentMarker;
    private GoogleMap mGoogleMap;
    private List<Event> mEventsList;
		private List<Event>	currentShownEvents = new ArrayList<Event>();
    private Database mDatabase;
    private DirectionsHelper mDirectionHelper;
    private ViewHelper mViewHelper;
		private boolean isInfoWindowClosed;
		private List<Marker> mMarkerList = new ArrayList<Marker>();

    public GMapFragment() {
				GoogleAPIHelper googleAPIHelper = App.getGoogleApiHelper();
				googleAPIHelper.addApiListener(this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallback = (OnMapFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnMapFragmentInteractionListener");
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
				mViewHelper = new ViewHelper((MainActivity) getActivity());
		}

		public void getEvents() {
				mEventsList = mDatabase.getEventsByFilter(FilterImpl.EMPTY_FILTER,
				EventsSorter.NULL_SORTER);
				placeMarkers(currentShownEvents);
				currentShownEvents = mEventsList;
		}

		public void initiateListeners() {
				mGoogleMap.setOnInfoWindowClickListener(this);
				mGoogleMap.setOnMarkerClickListener(this);
				mGoogleMap.setInfoWindowAdapter(this);
				mGoogleMap.setOnMapClickListener(this);
		}

		public void configGoogleWidgets() {
				mGoogleMap.getUiSettings().setMapToolbarEnabled(false);
				mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
				mGoogleMap.setPadding(0, 135, 0, 135);
				//TODO: add scale to the map
		}

		/* a method that creates markers from events and places them on the map*/
		public void placeMarkers(List<Event> eventsList) {
			if (isAdded()) {
				if (eventsList != null) {
					for (Event e : eventsList) {
						newMarker(e);
					}
				}
			}
		}

    public void newMarker(Event event) {
        LatLng markerPosition = new LatLng(
            event.getLatitude(), event.getLongitude());
        MarkerOptions properties = new MarkerOptions();
		String title;
		if (App.getInstance().needTranslation(event)) {
			title = App.getInstance().translateEvent(event).get("title");
		}
		else {
			title = event.getTitle(App.getInstance().getLocale());
		}

        BitmapDescriptor marker = createMarker(event.getCategories());
				if (!currentShownEvents.contains(event)) {
						properties.position(markerPosition)
								.title(title)
								.icon(marker).alpha(0.35f);
				}
				else {
						properties.position(markerPosition)
								.title(title)
								.icon(marker);
				}
        // TODO: 2016-09-26 .getIcon needs to be implemented /Adrian

        Marker activeMarker = mGoogleMap.addMarker(properties);
        activeMarker.setTag(event);
				mMarkerList.add(activeMarker);
		}

		@Override
		public boolean onMarkerClick(Marker marker) {
				isInfoWindowClosed = false;
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

				removePreviousDirections();

				//check if user comes from favourites.
				// In that case - remove old infoWindow & display the correct one
				showCorrectInfoWindow(destination);

				mDirectionHelper.showDirection(origin, destination, transportation);
		}

		public void showCorrectInfoWindow(LatLng destination)
		{
			if (mCurrentMarker == null) return;
			if (mCurrentMarker.getPosition().equals(destination)) return;

			mCurrentMarker.hideInfoWindow();

			if (doesMarkerExist(destination))
				findMarkerByLatLng(destination).showInfoWindow();
		}

		public boolean doesMarkerExist(LatLng latLng){
				for (int i = 0; i < mMarkerList.size(); i++){
						if (mMarkerList.get(i).getPosition().equals(latLng)){
								return true;
						}
				}
				return false;
		}

		public boolean isMyLocationEnabled(){
				return mGoogleMap.isMyLocationEnabled();
		}

		private Marker findMarkerByLatLng(LatLng latLng) {
				for (int i = 0; i < mMarkerList.size(); i++) {
						if (mMarkerList.get(i).getPosition().equals(latLng)) {
								mCurrentMarker = mMarkerList.get(i);
						}
				}
				return mCurrentMarker;
		}

	private static final int[] MARKER_BITMAP_IDS = new int[]{
		R.drawable.marker0,
		R.drawable.marker1,
		R.drawable.marker2,
		R.drawable.marker3
	};

	// Precondition: `category` is one of {0, 1, 2, 3}
	public Bitmap createMarkerBitmap(int category) {
		Bitmap markerBitmap = BitmapFactory.decodeResource(getResources(),
			MARKER_BITMAP_IDS[category]);
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

		public void removePreviousDirections(){
				if (mDirectionHelper.isPreviousDirectionPresent()) {
						mDirectionHelper.removePreviousDirection();
				}
		}

    @Override
    public void onMapClick(LatLng latLng) {
				//check to see if any infoWindow is open to know if directions needs to be erased
				if (isInfoWindowClosed) {
						removePreviousDirections();
				}
				isInfoWindowClosed = true;
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
						AppDatabase.addVisibleEventsListener(this);
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
				currentShownEvents = newEvents;
				placeMarkers(mEventsList);
		}

		/**
	 	* This interface must be implemented by activities that contain this
	 	* fragment to allow an interaction in this fragment to be communicated
	 	* to the activity and potentially other fragments contained in that
	 	* activity.
	 	* <p/>
	 	* See the Android Training lesson <a href=
	 	* "http://developer.android.com/training/basics/fragments/communicating.html"
	 	* >Communicating with Other Fragments</a> for more information.
	 	*/
		public interface OnMapFragmentInteractionListener {
				void onInfoWindowClicked(Marker marker);
		}
}