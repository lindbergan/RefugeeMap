package dat255.refugeemap;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import dat255.refugeemap.model.db.Database;
import dat255.refugeemap.model.db.Event;
import dat255.refugeemap.model.db.impl.FilterImpl;
import dat255.refugeemap.model.db.sort.EventsSorter;

/**
 *
 * @author Sebastian
 */
public class ListFilterButtonsFragment extends Fragment implements
	CategoryChangeListener {

	private Database mDatabase;
	private static List<ListFilterListener> listeners =
		new ArrayList<>();
	private int activeCategory = FilterImpl.NULL_CATEGORY;
	private Button distanceFilterButton;
	private Button timeFilterButton;
	private Button activeButton;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mDatabase = AppDatabase.getDatabaseInstance();
	}

	public void setButtonToActive(Button button) {
		button.setBackgroundColor(getResources().getColor(R.color
			.colorPrimary));
		button.setTextColor(getResources().getColor(R.color.colorAccent));
	}

	public void setButtonToInactive(Button button) {
		button.setBackgroundColor(getResources().getColor(R.color
			.TYPLITEVITTYP));
		button.setTextColor(getResources().getColor(R.color.common_google_signin_btn_text_light));
	}

	public void resetFilters() {
		FilterImpl filter = new FilterImpl(FilterImpl.NULL_CATEGORY, null,
			null, null);

		List<Event> newEvents = mDatabase.getEventsByFilter(filter,
			EventsSorter.NULL_SORTER);

		updateFilteredEvents(newEvents);
	}

	public void onDistanceButtonClick(View view) {

		LatLng userLocation = App.getGoogleApiHelper().getCurrentLocation();

		FilterImpl filter = new FilterImpl(FilterImpl.NULL_CATEGORY, null,
			new FilterImpl.DistanceCriteria(userLocation.longitude,
				userLocation.latitude, 100.0), null);

		List<Event> newEvents = mDatabase.getEventsByFilter(filter,
			EventsSorter.NULL_SORTER);

		updateFilteredEvents(newEvents);
	}

	public void onTimeButtonClick(View view) {
		FilterImpl filter = new FilterImpl(FilterImpl.NULL_CATEGORY, null,
			null, new FilterImpl.TimeCriteria());

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
				onDistanceButtonClick(v);
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
					onTimeButtonClick(v);
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

	public void onCategoryChange(int activeCategory) {
		this.activeCategory = activeCategory;
		disableButtons();
		this.activeButton = null;
	}

	public static interface ListFilterListener {
		public void onListFilterChanged(List<Event> newEvents);
	}

	public static void addFilteredEventsListener(ListFilterListener l)
	{ listeners.add(l); }

	public static void updateFilteredEvents(List<Event> newEvents)
	{
		for (ListFilterListener l : listeners)
			l.onListFilterChanged(newEvents);
	}
}
