package dat255.refugeemap;

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
import java.util.List;
import java.util.Locale;

import dat255.refugeemap.EventListFragment.OnListFragmentInteractionListener;
import dat255.refugeemap.helpers.GoogleAPIHelper;
import dat255.refugeemap.model.DistanceCalculator;
import dat255.refugeemap.model.db.Event;

/**
 * {@link RecyclerView.Adapter} that can display a {@link dat255.refugeemap.model.db.Event} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class EventRecyclerViewAdapter
	extends RecyclerView.Adapter<EventRecyclerViewAdapter.ViewHolder>
	implements GoogleAPIObserver {

	private final static String TAG = "EventRecyclerViewAdapt";

	private List<Event> mEvents;
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

	/**
	 * Constructor.
	 *
	 * @param events List of {@link Event} objects to be used in list
	 * @param listener {@link OnListFragmentInteractionListener} listening to an {@link EventListFragment}
   */
	public EventRecyclerViewAdapter(List<Event> events,
		OnListFragmentInteractionListener listener)
	{
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
		HashMap<String, String> result;
		if (App.getInstance().needTranslation(event)) {
			result = App.getInstance().translateEvent(event);
			holder.mIdView.setText(result.get("title"));
		}
		else {
			holder.mIdView.setText(event.getTitle(App.getInstance().getLocale()));
		}

		holder.mItem = event;
		if (currentLocation != null) {
			double distanceToEvent = DistanceCalculator.getGreatCircleDistance(
				currentLocation.latitude, currentLocation.longitude,
				event.getLatitude(), event.getLongitude());
			holder.mDistanceView
				.setText(String.format(new Locale(App.getInstance().getLocale()), "%.2f km", distanceToEvent));
		}

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
		return mEvents.size();
	}

	public void setEvents(List<Event> newEvents){
		mEvents=newEvents;
	}

	/**
	 * Class for holding on to a {@link View}
	 */
	public class ViewHolder extends RecyclerView.ViewHolder {
		public final View mView;
		public final LinearLayout mLayout;
		public final TextView mIdView;
		//public final TextView mContentView;
		public final TextView mDistanceView;
		public Event mItem;


		/**
		 * Constructor
		 * @param view {@link View} to be held
     */
		public ViewHolder(View view) {
			super(view);
			mView = view;
			mLayout = (LinearLayout) view.findViewById(R.id.list_item_layout);
			mIdView = (TextView) view.findViewById(R.id.id);
			//mContentView = (TextView) view.findViewById(R.id.content);
			mDistanceView = (TextView) view.findViewById(R.id.distance);
		}

		//@Override
		//public String toString() {
		//	return super.toString() + " '" + mContentView.getText() + "'";
		//}
	}
}
