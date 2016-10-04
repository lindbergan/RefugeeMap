package dat255.refugeemap;


import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.FileNotFoundException;
import java.io.InputStreamReader;

import dat255.refugeemap.model.db.Database;
import dat255.refugeemap.model.db.Event;
import dat255.refugeemap.model.db.EventCollection;

public class GMapFragment extends Fragment
implements GoogleServicesAdapter, AppDatabase.Listener{

    private GoogleMap mGoogleMap;
    ReplaceWithDetailView mCallback;
    Marker mCurrentMarker;
    private EventCollection mEventsList;
    private Database mDatabase;
    private DirectionsHelper mDirectionHelper;
    private PositionHelper mPositionHelper;

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
        setUpHelpers();
        getEvents();
        initiateListeners();
        configGoogleWidgets();
        setCurrentPosition();
        placeMarkers(mEventsList);
    }

    public void setUpHelpers() {
        mDirectionHelper = new DirectionsHelper(mGoogleMap);
        mPositionHelper = new PositionHelper(this,mGoogleMap);
        mPositionHelper.buildGoogleApiClient();
        mPositionHelper.getGoogleApiClient().connect();
    }

    public void getEvents() {
        mEventsList = mDatabase.getAllEvents();
        // TODO: 2016-09-26 Change to EventCollection /Adrian
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
        //TODO: add scale to the map
    }

    /* a method that places a marker at the position the user currently
    occupies*/

    public void setCurrentPosition() {
        //TODO: set this to "currentLocation" and zoom in on that one
        LatLng currentPosition = new LatLng(57.70887000, 11.97456000);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
            currentPosition, 15)); //zoom in on marker
    }

    /* a method that creates markers from events and places them on the map*/
    public void placeMarkers(EventCollection eventsList) {

        if(eventsList != null) {

            for (Event e : eventsList) {
                newMarker(e);
            }
        }

        //dummy Markers - Do not delete yet! need theese to confirm its working
        LatLng gbgMarker = new LatLng(57.70887000,11.97456000);//def of GBG
        mGoogleMap.addMarker(new MarkerOptions()
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

    public void newMarker(Event event) {
        LatLng markerPosition = new LatLng(
            event.getLatitude(), event.getLongitude());
        MarkerOptions properties = new MarkerOptions();

        properties.position(markerPosition)
            .title(event.getTitle())
            .icon(BitmapDescriptorFactory.defaultMarker());
        // TODO: 2016-09-26 .getIcon needs to be implemented /Adrian

        Marker activeMarker = mGoogleMap.addMarker(properties);
        activeMarker.setTag(event);
    }

    /* A method that shows the "directions" button as well as the custom
     infoWindow when user clicks on marker */
    @Override
    public boolean onMarkerClick(Marker marker) {
        Button directionButton = (Button) getActivity().
            findViewById(R.id.directions_button);
        directionButton.setVisibility(View.VISIBLE);

        directionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDirectionButtonClicked();
            }
        });

        mCurrentMarker = marker;
        return false;
    }

    public void onDirectionButtonClicked() {
        //NOTE: should be our current pos that is origin
        LatLng originLatLng = new LatLng(57.70887000,11.97456000);
        LatLng destinationLatLng = mCurrentMarker.getPosition();
        String transportation = "WillNotImplementRightNow";

        //check to see if any previous direction already is displayed
        // - in that case, remove it
        if(mDirectionHelper.isPreviousDirectionPresent()) {
            mDirectionHelper.removePreviousDirection();
        }

        //show the direction && set duration and distance text fields
        mDirectionHelper.showDirection(
            originLatLng, destinationLatLng, transportation);
        setDurationAndDistanceText();
    }

    public void setDurationAndDistanceText() {
        //TODO: fix synchronization issue (now main thread is quicker than Async task in backgroud - results in wrong values of Distans & Duration)
        TextView timeAndDistance = (TextView) getActivity().findViewById(
            R.id.info_time_and_distance);
        timeAndDistance.setText("Distance: " + mDirectionHelper.getDistance() +
            " Duration:" + mDirectionHelper.getDuration());
        timeAndDistance.setVisibility(View.VISIBLE);
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
        //Fetching the custom infoView
        View customView = getActivity().getLayoutInflater().inflate(
            R.layout.custom_info_window, null);

        if(marker.getTag() != null) {
            Event activeEvent = (Event) marker.getTag();

            //extracting the text fields
            TextView infoTitle = (TextView) customView.findViewById(
                R.id.info_title);
            TextView infoTime = (TextView) customView.findViewById(
                R.id.info_time);
            TextView infoCategory = (TextView) customView.findViewById(
                R.id.info_category);
            TextView infoContactInfo = (TextView) customView.findViewById(
                R.id.info_contactInformation);

            //TODO: get ALL the associated values from the event
            //setting the values corresponding to the event
            infoTitle.setText(activeEvent.getTitle());
            infoTime.setText("17.00-18-00");
            infoCategory.setText("Idrottsaktivitet");
            infoContactInfo.setText(activeEvent.getContactInformation());
        }
        //returning the custom_view with the correct values for the text fields
        return customView;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        //remove the "directions" button and corresponding textfield when
        // marker isn't in focus
        hideDirectionViews();

        //check if there is any previous directions present, in that case
        // - remove it
        if (mDirectionHelper.isPreviousDirectionPresent()) {
            mDirectionHelper.removePreviousDirection();
        }
    }

    public void hideDirectionViews() {
        Button directionButton = (Button) getActivity().findViewById(
            R.id.directions_button);
        directionButton.setVisibility(View.GONE);

        TextView timeAndDistance = (TextView) getActivity().findViewById(
            R.id.info_time_and_distance);
        timeAndDistance.setVisibility(View.GONE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

				try {
            AppDatabase.init(new InputStreamReader(getResources().
                openRawResource(R.raw.ctgs)), new InputStreamReader(
                getResources().openRawResource(R.raw.db)));
            mDatabase=AppDatabase.getDatabaseInstance();
            AppDatabase.addListener(this);
        }
        catch (FileNotFoundException ex) {
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
		mPositionHelper.getGoogleApiClient().disconnect();
	}

	@Nullable
	@Override
	public View onCreateView(
        LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
		return inflater.inflate(R.layout.fragment_gmap, container, false);
	}

  @Override
  public void onVisibleEventsChanged(EventCollection newEvents) {
    mGoogleMap.clear();
    mEventsList=newEvents;
    placeMarkers(mEventsList);
  }
}



