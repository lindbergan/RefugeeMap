package dat255.refugeemap.app.gui;

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
import dat255.refugeemap.app.gui.helper.SavedEventsHelper;
import dat255.refugeemap.model.db.Event;

/**
 * Fragment for displaying {@link Event}s marked as favourites
 */
public class FavoritesListFragment extends EventListFragment
	implements SavedEventsHelper.SavedEventListener {
	private EventRecyclerViewAdapter mEventRecyclerViewAdapter;
	private static final String TAG = "FavoritesListFragment";

	@Override public View onCreateView(LayoutInflater inflater,
		ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);

		View view = inflater.inflate(R.layout.fragment_favorites_list,
			container, false);

		// Set the adapter
		if (view instanceof RecyclerView) {
			Context context = view.getContext();
			RecyclerView recyclerView = (RecyclerView) view;
			recyclerView.setLayoutManager(new LinearLayoutManager(context));

			// Create new database instance and fetch categories and events
			try {
				AppDatabase.init(getActivity().getFilesDir());
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