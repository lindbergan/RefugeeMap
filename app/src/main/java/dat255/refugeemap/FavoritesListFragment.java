package dat255.refugeemap;

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

import dat255.refugeemap.helpers.SavedEventsHelper;
import dat255.refugeemap.model.db.Database;
import dat255.refugeemap.model.db.Event;
import dat255.refugeemap.model.db.impl.FilterImpl;
import dat255.refugeemap.model.db.sort.EventsSorter;

/**
 * Fragment for displaying {@link Event}s marked as favourites
 */
public class FavoritesListFragment extends EventListFragment
	implements SavedEventsHelper.SavedEventListener {
	private int mColumnCount = 1;
	private Database mDatabase;
	private EventRecyclerViewAdapter mEventRecyclerViewAdapter;
	private SavedEventsHelper mSavedEventsHelper;
	private static final String TAG = "FavoritesListFragment";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mSavedEventsHelper = new SavedEventsHelper(this.getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		View view = inflater.inflate(R.layout.fragment_favorites_list, container, false);

		// Set the adapter
		if (view instanceof RecyclerView) {
			Context context = view.getContext();
			RecyclerView recyclerView = (RecyclerView) view;
			if (mColumnCount <= 1) {
				recyclerView.setLayoutManager(new LinearLayoutManager(context));
			} else {
				recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
			}

			// Create new database instance and fetch categories and events
			try {
				AppDatabase.init(getActivity());
			} catch (IOException e) {
				Log.v(TAG, "Database file not found: " + e.getMessage());
			}
			mEventRecyclerViewAdapter = fillListFragment();
			recyclerView.setAdapter(mEventRecyclerViewAdapter);
			AppDatabase.addVisibleEventsListener(this);
		}
		return view;
	}

	@Override
	public EventRecyclerViewAdapter fillListFragment(List<Event> savedEvents) {
		return new EventRecyclerViewAdapter(savedEvents, mListener);
	}

	@Override
	public void onSavedEvent(List<Event> savedEvents) {
		mEventRecyclerViewAdapter.setEvents(savedEvents);
		mEventRecyclerViewAdapter.notifyDataSetChanged();
	}
}
