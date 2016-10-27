package dat255.refugeemap.app.gui;


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
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import dat255.refugeemap.R;
import dat255.refugeemap.app.base.AppDatabase;
import dat255.refugeemap.app.base.GoogleAPIListener;
import dat255.refugeemap.app.gui.helper.DirectionsHelper;
import dat255.refugeemap.app.gui.helper.GoogleAPIHelper;
import dat255.refugeemap.model.db.Database;
import dat255.refugeemap.model.db.Event;
import dat255.refugeemap.model.db.impl.FilterImpl;
import dat255.refugeemap.model.db.sort.EventsSorter;
import lombok.val;

import static android.graphics.Bitmap.createBitmap;

/**
 * A {@link Fragment} subclass for holding a {@link GoogleMap}.
 * Activities that contain this fragment must implement the
 * {@link MapFragment.OnMapFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment implements
	GoogleMap.OnInfoWindowClickListener, GoogleMap.InfoWindowAdapter,
	GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener,
	OnMapReadyCallback, AppDatabase.VisibleEventsListener, GoogleAPIListener
{
	private static final String TAG = "MapFragment";
	private OnMapFragmentInteractionListener mCallback;
	private Marker mCurrentMarker;
	private GoogleMap mGoogleMap;
	private List<Event> mEventsList;
	private List<Event>	currentShownEvents = new ArrayList<>();
	private Database mDatabase;
	private DirectionsHelper mDirectionHelper;
	private boolean isInfoWindowClosed;
	private final List<Marker> mMarkerList = new ArrayList<>();

	public MapFragment() {
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
		mDirectionHelper = new DirectionsHelper(mGoogleMap);
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
	public void onAPIConnected(GoogleAPIHelper googleAPIHelper) {
		animateMapCamera(googleAPIHelper.getCurrentLocation());
	}

	private void animateMapCamera(LatLng latLng) {
		mGoogleMap.moveCamera(
			CameraUpdateFactory.newLatLngZoom(latLng, 15));
	}

	private void getEvents() {
		mEventsList = mDatabase.getEventsByFilter(FilterImpl.EMPTY_FILTER,
			EventsSorter.NULL_SORTER);
		placeMarkers(currentShownEvents);
		currentShownEvents = mEventsList;
	}

	private void initiateListeners() {
		mGoogleMap.setOnInfoWindowClickListener(this);
		mGoogleMap.setOnMarkerClickListener(this);
		mGoogleMap.setInfoWindowAdapter(this);
		mGoogleMap.setOnMapClickListener(this);
	}

	private void configGoogleWidgets() {
		mGoogleMap.getUiSettings().setMapToolbarEnabled(false);
		mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
		mGoogleMap.setPadding(0, 135, 0, 135);
		//TODO: add scale to the map
	}

	/* a method that creates markers from events and places them on the map*/
	private void placeMarkers(List<Event> eventsList) {
		if (isAdded()) {
			if (eventsList != null) {
				for (Event e : eventsList) {
					newMarker(e);
				}
			}
		}
	}
	//private?
	private void newMarker(Event event) {
		LatLng markerPosition = new LatLng(
			event.getLatitude(), event.getLongitude());
		MarkerOptions properties = new MarkerOptions();
		String title;
		if (App.getInstance().needTranslation(event)) {
			title = App.getInstance().translateEvent(event).getTitle();
		}
		else {
			title = event.getTitle(App.getInstance().getLocaleCode());
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

	/**
	 * Creates a marker using an array of categories represented as integers
	 * @param eventCategories the desired categories
	 * @return A {@link BitmapDescriptor} representing a marker
	 */
	private BitmapDescriptor createMarker(Integer[] eventCategories){
		int category = eventCategories[0];
		Bitmap markerBitmap = createMarkerBitmap(category);

		if (eventCategories.length==2) {
			Bitmap markerBitmap2 = createMarkerBitmap(eventCategories[1]);
			markerBitmap = combineBitmaps(markerBitmap, markerBitmap2);
		}
		return BitmapDescriptorFactory.fromBitmap(markerBitmap);
	}

	/**
	 * Displays the directions between two {@link LatLng} points on the map
	 * @param origin The {@link LatLng} describing the point of origin
	 * @param destination The {@link LatLng} describing the destination
	 * @param transportation The string describing the mode of transportation
	 */
	public void showDirections(LatLng origin, LatLng destination,
		String transportation)
	{
		removePreviousDirections();

		//check if user comes from favourites.
		// In that case - remove old infoWindow & display the correct one
		showCorrectInfoWindow(destination);

		mDirectionHelper.showDirection(origin, destination, transportation);
	}

	private void showCorrectInfoWindow(LatLng destination)
	{
		if (mCurrentMarker != null) mCurrentMarker.hideInfoWindow();
		if (!doesMarkerExist(destination)) return;
		updateMarker(destination);
		mCurrentMarker.showInfoWindow();
	}

	private boolean doesMarkerExist(LatLng latLng)
	{
		for (int i = 0; i < mMarkerList.size(); i++)
			if (mMarkerList.get(i).getPosition().equals(latLng))
				return true;
		return false;
	}

	/**
	 * Returns true iff the {@link GoogleMap} has enabled location tracking.
	 */
	public boolean isMyLocationEnabled(){
		return mGoogleMap.isMyLocationEnabled();
	}

	private void updateMarker(LatLng latLng) {
		for (int i = 0; i < mMarkerList.size(); i++) {
			if (mMarkerList.get(i).getPosition().equals(latLng)) {
				mCurrentMarker = mMarkerList.get(i);
			}
		}
	}

	private static final int[] MARKER_BITMAP_IDS = new int[]{
		R.drawable.marker0,
		R.drawable.marker1,
		R.drawable.marker2,
		R.drawable.marker3
	};

	// Precondition: `category` is one of {0, 1, 2, 3}
	private Bitmap createMarkerBitmap(int category) {
		return BitmapFactory.decodeResource(getResources(),
			MARKER_BITMAP_IDS[category]);
	}

	private Bitmap combineBitmaps(Bitmap b1, Bitmap b2){

		Bitmap bitmap = createBitmap(b1.getWidth(), b1.getHeight(),
			Bitmap.Config.ARGB_8888);
		Canvas combo = new Canvas(bitmap);

		b1 = createBitmap(b1, 0, 0, b1.getWidth() / 2,b1.getHeight());
		b2 = createBitmap(b2, b2.getWidth()/2, 0,
			b2.getWidth()/2, b2.getHeight());

		combo.drawBitmap(b1, 0f, 0f, null);
		combo.drawBitmap(b2, b1.getWidth(), 0f, null);

		return bitmap;
	}

	private void removePreviousDirections(){
		if (mDirectionHelper.isPreviousDirectionPresent()) {
			mDirectionHelper.removePreviousDirection();
		}
	}

	@Override
	public void onMapClick(LatLng latLng) {
		//check to see if any infoWindow is open to
		// know if directions needs to be erased
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

	/**
	 * Used for getting the custom info {@link View}
	 * using a tagged {@link Marker}
	 */
	@Override
	public View getInfoContents(Marker marker) {
		//Fetching the custom infoView
		View customView = getActivity().getLayoutInflater().inflate(
			R.layout.custom_info_window, null);

		if (marker.getTag() != null) {
			Event activeEvent = (Event) marker.getTag();

			TextView infoTitle = (TextView) customView.findViewById(
				R.id.info_title);
			TextView infoTime = (TextView) customView.findViewById(
				R.id.info_time);
			TextView infoContactInfo = (TextView) customView.findViewById(
				R.id.info_contactInformation);

			App app = App.getInstance();
			if (app.needTranslation(activeEvent))
				infoTitle.setText(app.translateEvent(activeEvent).getTitle());
			else infoTitle.setText(activeEvent.getTitle(app.getLocaleCode()));

			infoTime.setText(activeEvent.getDateInformation());
			infoContactInfo.setText(activeEvent.getContactInformation());
		}
		return customView;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try {
			AppDatabase.init(getActivity().getFilesDir());
			mDatabase = AppDatabase.getDatabaseInstance();
			AppDatabase.addVisibleEventsListener(this);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		val fragment = (com.google.android.gms.maps.MapFragment)
			getChildFragmentManager().findFragmentById(R.id.map);
		fragment.getMapAsync(this);
	}

	@Nullable @Override public View onCreateView(LayoutInflater inflater,
		ViewGroup container, Bundle savedInstanceState)
	{
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
	 * See the Android training lesson "Communicating
	 * with Other Fragments" for more information.
	 */
	public interface OnMapFragmentInteractionListener {
		void onInfoWindowClicked(Marker marker);
	}
}