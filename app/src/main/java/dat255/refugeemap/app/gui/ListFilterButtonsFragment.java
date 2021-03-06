package dat255.refugeemap.app.gui;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import dat255.refugeemap.R;
import dat255.refugeemap.app.base.AppDatabase;
import dat255.refugeemap.app.base.CategoryChangeListener;
import dat255.refugeemap.model.db.Database;
import dat255.refugeemap.model.db.Event;
import dat255.refugeemap.model.db.impl.FilterImpl;
import dat255.refugeemap.model.db.sort.EventsSorter;
import lombok.val;

/**
 * Fragment used for the filtering of {@link Event}s
 * @author Sebastian
 */
public class ListFilterButtonsFragment extends Fragment implements
	CategoryChangeListener {

	private Database mDatabase;
	private static final List<ListFilterListener> listeners =
		new ArrayList<>();
	private Button distanceFilterButton;
	private Button timeFilterButton;
	private Button activeButton;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mDatabase = AppDatabase.getDatabaseInstance();
	}

	private void setButtonToActive(Button button) {
		button.setBackgroundColor(getResources().getColor(R.color
			.colorPrimary));
		button.setTextColor(getResources().getColor(R.color.colorAccent));
	}

	private void setButtonToInactive(Button button) {
		button.setBackgroundColor(getResources().
			getColor(R.color.TYPLITEVITTYP));
		button.setTextColor(getResources().getColor(R.color.
			common_google_signin_btn_text_light));
	}

	private void resetFilters() {
		FilterImpl filter = new FilterImpl(null, null, null, null);

		List<Event> newEvents = mDatabase.getEventsByFilter(filter,
			EventsSorter.NULL_SORTER);

		updateFilteredEvents(newEvents);
	}

	private void onDistanceButtonClick() {

		LatLng userLocation = App.getGoogleApiHelper().getCurrentLocation();

		FilterImpl filter = new FilterImpl(null, null,
			new FilterImpl.DistanceCriteria(userLocation.longitude,
				userLocation.latitude, 100.0), null);

		List<Event> newEvents = mDatabase.getEventsByFilter(filter,
			EventsSorter.NULL_SORTER);

		updateFilteredEvents(newEvents);
	}

	private void onTimeButtonClick() {
		val timeCriteria = new FilterImpl.TimeCriteria(Calendar.
			getInstance().get(Calendar.DAY_OF_WEEK));
		FilterImpl filter = new FilterImpl(null, null, null, timeCriteria);

		List<Event> newEvents = mDatabase.getEventsByFilter(filter,
			EventsSorter.NULL_SORTER);

		updateFilteredEvents(newEvents);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		View fragmentView
			= inflater.inflate(R.layout.fragment_list_filter_buttons,
			container, false);

		distanceFilterButton = (Button)fragmentView.findViewById(R.id
			.distance_button);
		setButtonToInactive(distanceFilterButton);

		timeFilterButton = (Button)fragmentView.findViewById(R.id
			.time_button);

		distanceFilterButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (activeButton != null && distanceFilterButton.equals
					(activeButton)) {
					setButtonToInactive(distanceFilterButton);
					resetFilters();
					activeButton = null;
				} else {
					setButtonToActive(distanceFilterButton);
					setButtonToInactive(timeFilterButton);
					onDistanceButtonClick();
					activeButton = distanceFilterButton;
				}
			}
		});

		timeFilterButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (activeButton != null && activeButton.equals
					(timeFilterButton)) {
					setButtonToInactive(timeFilterButton);
					resetFilters();
					activeButton = null;
				} else {
					setButtonToActive(timeFilterButton);
					setButtonToInactive(distanceFilterButton);
					onTimeButtonClick();
					activeButton = timeFilterButton;
				}
			}
		});

		return fragmentView;
	}

	private void disableButtons() {
		setButtonToInactive(distanceFilterButton);
		setButtonToInactive(timeFilterButton);
	}

	@Override
	public void onCategoryChange() {
		disableButtons();
		this.activeButton = null;
	}

	/**
	 * Interface for listening to changes in the filter
	 */
	public interface ListFilterListener {
		/**
		 * Called when the currently filtered categories change
		 * @param newEvents a {@link List} of new {@link Event} objects
		 */
		void onListFilterChanged(List<Event> newEvents);
	}

	/**
	 * Adds a {@link ListFilterListener} to this fragment
	 * @param l The {@link ListFilterListener} to be added
	 */
	public static void addFilteredEventsListener(ListFilterListener l)
	{ listeners.add(l); }

	//private?
	private static void updateFilteredEvents(List<Event> newEvents)
	{
		for (ListFilterListener l : listeners)
			l.onListFilterChanged(newEvents);
	}
}
