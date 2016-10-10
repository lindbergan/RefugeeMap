package dat255.refugeemap;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.Locale;

import dat255.refugeemap.EventListFragment.OnListFragmentInteractionListener;
import dat255.refugeemap.helpers.GoogleAPIHelper;
import dat255.refugeemap.model.DistanceCalculator;
import dat255.refugeemap.model.db.Database;
import dat255.refugeemap.model.db.Event;
import dat255.refugeemap.model.db.EventCollection;

/**
 * {@link RecyclerView.Adapter} that can display a {@link dat255.refugeemap.model.db.Event} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class EventRecyclerViewAdapter
	extends RecyclerView.Adapter<EventRecyclerViewAdapter.ViewHolder>
	implements GoogleAPIObserver {

	private final static String TAG = "EventRecyclerViewAdapt";

	private EventCollection mEvents;
	private final OnListFragmentInteractionListener mListener;
	private HashMap<Integer, Integer> listItemColor = new HashMap<>();
	private LatLng currentLocation;

	/**
	 * Set the colors or drawables that we want to use here
	 */

	private void initListItemColors(ViewHolder holder) {
		listItemColor.put(0, ContextCompat.getColor(holder.mView.getContext(), R.color.colorCategory1));
		listItemColor.put(1, ContextCompat.getColor(holder.mView.getContext(), R.color.colorCategory2));
		listItemColor.put(2, ContextCompat.getColor(holder.mView.getContext(), R.color.colorCategory3));
		listItemColor.put(3, ContextCompat.getColor(holder.mView.getContext(), R.color.colorCategory4));
	}

	public EventRecyclerViewAdapter(EventCollection events,
									OnListFragmentInteractionListener listener) {
		mEvents = events;
		mListener = listener;

		GoogleAPIHelper googleAPIHelper = App.getGoogleApiHelper();
		googleAPIHelper.addApiListener(this);
	}

	@Override
	public void onApiConnected(GoogleAPIHelper googleAPIHelper) {
		Log.d(TAG, "onApiConnected: " + googleAPIHelper.getCurrentLocation());
		currentLocation = googleAPIHelper.getCurrentLocation();
	}

	/**
	 * If mItem has > 1 categories the layout gets a gradient with corresponding colors
	 */

	private void setListItemColor(final ViewHolder holder) {
		initListItemColors(holder);
		for (int i  = 0; i < holder.mItem.getCategories().length; i++) {
			holder.mLayout.setBackgroundColor(listItemColor.get(holder.mItem.getCategories()[0]));
		}
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext())
			.inflate(R.layout.fragment_event_item, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(final ViewHolder holder, int position) {

		Event event = mEvents.get(position);

		holder.mItem = event;
		holder.mIdView.setText(event.getTitle());
		if (currentLocation != null) {
			double distanceToEvent = DistanceCalculator.getGreatCircleDistance(
				currentLocation.latitude, currentLocation.longitude,
				event.getLatitude(), event.getLongitude());

			Locale currentLocale = App.getInstance().getApplicationContext()
				.getResources().getConfiguration().locale;

			holder.mDistanceView
				.setText(String.format(currentLocale, "%.2f km", distanceToEvent));
		}

		//holder.mContentView.setText(mEvents.get(position).getDescription());
		holder.mView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (null != mListener) {
					// Notify the active callbacks interface (the activity, if the
					// fragment is attached to one) that an item has been selected.
					mListener.onListFragmentInteraction(holder.mItem);
				}
			}
		});
		setListItemColor(holder);
	}

	@Override
	public int getItemCount() {
		return mEvents.getSize();
	}

	public void setEvents(EventCollection newEvents){
		mEvents=newEvents;
	}

	public class ViewHolder extends RecyclerView.ViewHolder {
		public final View mView;
		public final LinearLayout mLayout;
		public final TextView mIdView;
		public final TextView mContentView;
		public final TextView mDistanceView;
		public Event mItem;


		public ViewHolder(View view) {
			super(view);
			mView = view;
			mLayout = (LinearLayout) view.findViewById(R.id.list_item_layout);
			mIdView = (TextView) view.findViewById(R.id.id);
			mContentView = (TextView) view.findViewById(R.id.content);
			mDistanceView = (TextView) view.findViewById(R.id.distance);
		}
		@Override
		public String toString() {
			return super.toString() + " '" + mContentView.getText() + "'";
		}
	}
}
