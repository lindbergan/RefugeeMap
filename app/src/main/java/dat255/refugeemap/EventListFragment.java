package dat255.refugeemap;

import android.app.Fragment;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import dat255.refugeemap.StaticContent.StaticItem;
import dat255.refugeemap.helpers.AssetsHelper;
import dat255.refugeemap.model.db.Database;
import dat255.refugeemap.model.db.Event;
import dat255.refugeemap.model.db.EventCollection;
import dat255.refugeemap.model.db.JSONTools;
import dat255.refugeemap.model.db.impl.DatabaseImpl;
import dat255.refugeemap.model.db.impl.EventArray;
import dat255.refugeemap.model.db.impl.EventList;
import dat255.refugeemap.model.db.impl.JSONToolsImpl;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class EventListFragment extends Fragment {
	private int mColumnCount = 1;
	private static final String TAG = "EventListFragment";

	private OnListFragmentInteractionListener mListener;

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
			RecyclerView recyclerView = (RecyclerView) view;
			if (mColumnCount <= 1) {
				recyclerView.setLayoutManager(new LinearLayoutManager(context));
			} else {
				recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
			}

			// Get path to local json databases
			String categoriesJsonFilePath = AssetsHelper.getAssetFilePath("categories.json", this.getActivity());
			String eventsJsonFilePath = AssetsHelper.getAssetFilePath("events.json", this.getActivity());
			JSONTools jsonTools = new JSONToolsImpl();

			List<Event> events = new ArrayList<>();
			EventCollection eventCollection = new EventList(events);

			// Create new database instance and fetch categories and events
			try {
				Database db = new DatabaseImpl(categoriesJsonFilePath, eventsJsonFilePath, jsonTools);
				eventCollection = db.getAllEvents();
			} catch (FileNotFoundException e) {
				Log.v(TAG, "Database file not found: " + e.getMessage());
			}

			recyclerView.setAdapter(new EventRecyclerViewAdapter(eventCollection, mListener));
		}
		return view;
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
}
