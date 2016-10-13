package dat255.refugeemap;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;
import java.util.List;

import dat255.refugeemap.model.db.Database;
import dat255.refugeemap.model.db.Event;
import dat255.refugeemap.model.db.Filter;
import dat255.refugeemap.model.db.impl.FilterImpl;
import dat255.refugeemap.model.db.sort.EventsSorter;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class EventListFragment extends Fragment implements AppDatabase.VisibleEventsListener {
	private int mColumnCount = 1;
	private static final String TAG = "EventListFragment";

	private OnListFragmentInteractionListener mListener;

	private Database mDatabase;

	private EventRecyclerViewAdapter eventRecycler;

	private RecyclerView recyclerView;
	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment.
	 */
	public EventListFragment() {
	}

	public static EventListFragment newInstance(){
		return new EventListFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		//Log.v(TAG, "Lat: " + latLng.latitude + " Lng: " + latLng.longitude);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_event_list, container, false);

		// Set the adapter
		if (view instanceof RecyclerView) {
			Context context = view.getContext();
			recyclerView = (RecyclerView) view;
			if (mColumnCount <= 1) {
				recyclerView.setLayoutManager(new LinearLayoutManager(context));
			} else {
				recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
			}

			// Create new database instance and fetch categories and events
			try {
				AppDatabase.init(getActivity());
				mDatabase=AppDatabase.getDatabaseInstance();
			} catch (IOException e) {
				Log.v(TAG, "Database file not found: " + e.getMessage());
			}
			eventRecycler = fillListFragment();
			recyclerView.setAdapter(eventRecycler);
			AppDatabase.addVisibleEventsListener(this);
		}
		return view;
	}

	public EventRecyclerViewAdapter fillListFragment() {
		return new EventRecyclerViewAdapter(mDatabase.
			getEventsByFilter(FilterImpl.EMPTY_FILTER,
			EventsSorter.NULL_SORTER), mListener);
	}

	/**
	 * Will fill the listview with events matching a color (category)
	 */

	public EventRecyclerViewAdapter fillListFragment(Filter filter) {
		return new EventRecyclerViewAdapter(mDatabase.getEventsByFilter(filter,
			EventsSorter.NULL_SORTER), mListener);
	}

	public EventRecyclerViewAdapter fillListFragment(List<Event> events){
		return new EventRecyclerViewAdapter(events, mListener);
	}


	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if (context instanceof OnListFragmentInteractionListener) {
			mListener = (OnListFragmentInteractionListener) context;
		} else {
			throw new RuntimeException(context.toString()
				+ " must implement OnListFragmentInteractionListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated
	 * to the activity and potentially other fragments contained in that
	 * activity.
	 */
	public interface OnListFragmentInteractionListener {
		void onListFragmentInteraction(Event item);
	}

	@Override
	public void onVisibleEventsChanged(List<Event> newEvents){
		eventRecycler.setEvents(newEvents);
		eventRecycler.notifyDataSetChanged();
	}
}
