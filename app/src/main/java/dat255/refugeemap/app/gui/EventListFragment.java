package dat255.refugeemap.app.gui;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;
import java.util.List;

import dat255.refugeemap.R;
import dat255.refugeemap.app.base.AppDatabase;
import dat255.refugeemap.model.db.Database;
import dat255.refugeemap.model.db.Event;
import dat255.refugeemap.model.db.impl.FilterImpl;
import dat255.refugeemap.model.db.sort.EventsSorter;

/**
 * A fragment representing a list of {@link Event} items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class EventListFragment extends Fragment implements
	AppDatabase.VisibleEventsListener,
	ListFilterButtonsFragment.ListFilterListener
{
	private static final String TAG = "EventListFragment";

	OnListFragmentInteractionListener mListener;

	private Database mDatabase;

	private EventRecyclerViewAdapter eventRecycler;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment.
	 */
	public EventListFragment() {}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ListFilterButtonsFragment.addFilteredEventsListener(this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_event_list,
			container, false);

		if (getActivity() instanceof OnListFragmentInteractionListener)
			mListener = (OnListFragmentInteractionListener) getActivity();

		else throw new RuntimeException(getActivity().toString() +
			" must implement OnListFragmentInteractionListener");

		// Set the adapter
		if (view instanceof RecyclerView) {
			Context context = view.getContext();
			RecyclerView recyclerView = (RecyclerView) view;
			recyclerView.setLayoutManager(new LinearLayoutManager(context));

			// Create new database instance and fetch categories and events
			try {
				AppDatabase.init(getActivity().getFilesDir());
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

	protected EventRecyclerViewAdapter fillListFragment() {
		return new EventRecyclerViewAdapter(mDatabase.
			getEventsByFilter(FilterImpl.EMPTY_FILTER,
				EventsSorter.NULL_SORTER), mListener);
	}

	public EventRecyclerViewAdapter fillListFragment(List<Event> events){
		return new EventRecyclerViewAdapter(events, mListener);
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

	@Override
	public void onListFilterChanged(List<Event> newEvents) {
		eventRecycler.setEvents(newEvents);
		eventRecycler.notifyDataSetChanged();
	}
}