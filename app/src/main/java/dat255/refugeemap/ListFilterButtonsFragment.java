package dat255.refugeemap;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import dat255.refugeemap.App;
import dat255.refugeemap.AppDatabase;
import dat255.refugeemap.CategoryChangeListener;
import dat255.refugeemap.R;
import dat255.refugeemap.model.db.Database;
import dat255.refugeemap.model.db.Event;
import dat255.refugeemap.model.db.impl.FilterImpl;
import dat255.refugeemap.model.db.sort.EventsSorter;
import dat255.refugeemap.model.db.sort.EventsSorterDistance;
import dat255.refugeemap.model.db.sort.EventsSorterTitle;

/**
 *
 * @author Sebastian
 */
public class ListFilterButtonsFragment extends Fragment implements
	CategoryChangeListener {

	private Database mDatabase;
	private int activeCategory = FilterImpl.NULL_CATEGORY;

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

	public void onDistanceButtonClick(View view) {

		LatLng userLocation = App.getGoogleApiHelper().getCurrentLocation();

		FilterImpl filter = new FilterImpl(activeCategory, null,
			new FilterImpl.DistanceCriteria(userLocation.longitude,
				userLocation.latitude, 100.0), null);

		List<Event> newEvents = mDatabase.getEventsByFilter(filter,
			EventsSorter.NULL_SORTER);

		AppDatabase.updateVisibleEvents(newEvents);
	}

	public void onTimeButtonClick(View view) {
		FilterImpl filter = new FilterImpl(activeCategory, null,
			null, new FilterImpl.TimeCriteria());

		List<Event> newEvents = mDatabase.getEventsByFilter(filter,
			EventsSorter.NULL_SORTER);

		AppDatabase.updateVisibleEvents(newEvents);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		View fragmentView
			= inflater.inflate(R.layout.fragment_list_filter_buttons,
			container, false);

		final Button distanceFilterButton = (Button)fragmentView.findViewById(R.id
			.distance_button);
		setButtonToInactive(distanceFilterButton);

		final Button timeFilterButton = (Button)fragmentView.findViewById(R.id
			.time_button);

		distanceFilterButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setButtonToActive(distanceFilterButton);
				setButtonToInactive(timeFilterButton);
				onDistanceButtonClick(v);
			}
		});

		timeFilterButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setButtonToActive(timeFilterButton);
				setButtonToInactive(distanceFilterButton);
				onTimeButtonClick(v);
			}
		});

		return fragmentView;
	}

	public void onCategoryChange(int activeCategory) {
		this.activeCategory = activeCategory;
	}
}
