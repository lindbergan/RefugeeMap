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

import java.io.FileNotFoundException;
import java.io.InputStreamReader;

import dat255.refugeemap.model.db.Database;
import dat255.refugeemap.model.db.Event;
import dat255.refugeemap.model.db.EventCollection;
import dat255.refugeemap.model.db.Filter;
import dat255.refugeemap.model.db.impl.FilterImpl;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class EventListFragment extends Fragment implements AppDatabase.Listener{
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
				AppDatabase.init(new InputStreamReader(getResources().openRawResource(R.raw.ctgs)),
								new InputStreamReader(getResources().openRawResource(R.raw.db)));
				mDatabase=AppDatabase.getDatabaseInstance();
			} catch (FileNotFoundException e) {
				Log.v(TAG, "Database file not found: " + e.getMessage());
			}
			eventRecycler = fillListFragment();
			recyclerView.setAdapter(eventRecycler);
			AppDatabase.addListener(this);
		}
		return view;
	}

	public EventRecyclerViewAdapter fillListFragment() {
		return new EventRecyclerViewAdapter(mDatabase.getAllEvents(), mListener);
	}

	/**
	 * Will fill the listview with events matching a color (category)
	 */

	public EventRecyclerViewAdapter fillListFragment(Filter filter) {
		return new EventRecyclerViewAdapter(mDatabase.getEventsByFilter(filter), mListener);
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
	public void onVisibleEventsChanged(EventCollection newEvents){
		eventRecycler.setEvents(newEvents);
		eventRecycler.notifyDataSetChanged();
	}
}
