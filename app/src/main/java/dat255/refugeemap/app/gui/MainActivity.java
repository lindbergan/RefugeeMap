package dat255.refugeemap.app.gui;

import android.Manifest;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import dat255.refugeemap.R;
import dat255.refugeemap.app.base.AppDatabase;
import dat255.refugeemap.app.base.CategoryChangeListener;
import dat255.refugeemap.app.gui.helper.GoogleAPIHelper;
import dat255.refugeemap.app.gui.helper.SavedEventsHelper;
import dat255.refugeemap.app.gui.helper.ViewHelper;
import dat255.refugeemap.model.db.Database;
import dat255.refugeemap.model.db.Event;
import dat255.refugeemap.model.db.Filter;
import dat255.refugeemap.model.db.impl.FilterImpl;
import dat255.refugeemap.model.db.sort.EventsSorter;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Main {@link AppCompatActivity} for this application
 */
public class MainActivity extends AppCompatActivity
	implements EventListFragment.OnListFragmentInteractionListener,
	DetailsFragment.OnFragmentInteractionListener,
	MapFragment.OnMapFragmentInteractionListener,AppDatabase
		.VisibleEventsListener, ListFilterButtonsFragment.ListFilterListener {

	private ViewHelper mViewHelper;
	private SavedEventsHelper mSavedEventsHelper;
	private long lastSearchClickTime = 0;
	private InputMethodManager inputManager;
	private ImageView logo;
	private EditText searchEdit;
	private ImageButton searchBtn;
	private Database mDatabase;
	private final List<CategoryChangeListener>
		mActiveCategoryChangeListeners = new ArrayList<>();
	private Integer activeCategory = null;
	private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 20;
	private Collection<String> activeSearchTerms = null;
	private GoogleAPIHelper mGoogleAPIHelper;

	@Override protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		mViewHelper = new ViewHelper(this);
		if (ContextCompat.checkSelfPermission(this,
			Manifest.permission.ACCESS_FINE_LOCATION)
			!= PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this,
				new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
				MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

			// MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION is an
			// app-defined int constant. The callback method gets the
			// result of the request.
		}
		mGoogleAPIHelper = new GoogleAPIHelper(getApplicationContext());
		mSavedEventsHelper = new SavedEventsHelper(this);
		setContentView(R.layout.activity_main);
		setUpViews();
		setUpToolbar();
		ListFilterButtonsFragment.addFilteredEventsListener(this);
		this.inputManager = (InputMethodManager) getSystemService(
			Context.INPUT_METHOD_SERVICE);

		try {
			AppDatabase.init(getFilesDir());
			this.mDatabase = AppDatabase.getDatabaseInstance();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		mViewHelper.stateSwitch("app_start");
		mViewHelper.setUpNavigationDrawer();


		// The {@link ListFilterButtonsFragment} now
		// listens to when categories change
		Fragment[] currentFragments = mViewHelper.getCurrentFragments();
		CategoryChangeListener listFilterButtons = (CategoryChangeListener)
			currentFragments[ViewHelper.LIST_FILTER_BUTTONS];
		mActiveCategoryChangeListeners.add(listFilterButtons);
		App.getInstance().setLocaleCode(getBaseContext().getResources().
			getConfiguration().locale.getLanguage());
	}

	private void setUpViews()
	{
		this.logo = (ImageView)findViewById(R.id.logo);
		this.searchEdit = (EditText)findViewById(R.id.et_search);
		this.searchBtn = (ImageButton)findViewById(R.id.action_search);
	}

	private void setUpToolbar(){
		Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
		setSupportActionBar(toolbar);
		if (getSupportActionBar() != null) {
			getSupportActionBar().setDisplayShowTitleEnabled(false);
		}
	}

	//Override?
	public void onInfoWindowClicked(Marker marker) {

		if(marker.getTag() instanceof Event) {
			Event currentEvent = (Event) marker.getTag();
			mViewHelper.setCurrentEvent(currentEvent);
			mViewHelper.stateSwitch("marker_clicked");
		}
	}

	@Override
	public void onListFragmentInteraction(Event item) {
		mViewHelper.setCurrentEvent(item);
		mViewHelper.stateSwitch("list_item_clicked");
	}

	//private?
	private void toggleSearchFocus(View v) {
		long currentTime = SystemClock.elapsedRealtime();
		int clickThreshold = 500;
		if (this.searchBtn.isEnabled() &&
			// Check if last click time was too recent in order to avoid
			// accidental double-click
			currentTime - lastSearchClickTime > clickThreshold) {
			Filter f = new FilterImpl(activeCategory,
				activeSearchTerms, null, null);
			AppDatabase.updateVisibleEvents(mDatabase.getEventsByFilter(f,
				EventsSorter.NULL_SORTER));
			this.searchEdit.setVisibility(VISIBLE);
			this.searchEdit.requestFocus();
			this.logo.setVisibility(GONE);
			this.inputManager.showSoftInput(searchEdit,
				InputMethodManager.SHOW_IMPLICIT);
			this.searchBtn.setEnabled(false);
		} else {
			v.clearFocus();
			this.inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
			this.searchEdit.setVisibility(GONE);
			this.logo.setVisibility(VISIBLE);
			this.searchBtn.setEnabled(true);
		}
		lastSearchClickTime = currentTime;
	}

	/**
	 * Used by xml-files for the category toolbar
	 * @param view The {@link View} of the clicked button
	 */
	public void onCategoryClick(View view) {
		Integer ctgPressed = Integer.parseInt(view.getTag().toString());

		if (ctgPressed.equals(activeCategory)) {
			activeCategory = null;
			toggleCategoryButton(ctgPressed, true);
		} else {
			if (activeCategory != null)
				toggleCategoryButton(activeCategory, true);
			toggleCategoryButton(ctgPressed, false);
			activeCategory = ctgPressed;
		}

		FilterImpl filter = new FilterImpl(activeCategory,
			activeSearchTerms, null, null);
		List<Event> newEvents = mDatabase.getEventsByFilter(filter,
			EventsSorter.NULL_SORTER);

		this.broadcastActiveCategory();

		AppDatabase.updateVisibleEvents(newEvents);
	}

	//private?
	private void toggleCategoryButton(int category, boolean activated){
		int id = getResources().getIdentifier("category"+category,
			"id", getPackageName());
		ImageButton button = (ImageButton)findViewById(id);
		if (activated)
			button.animate().translationY(0);
		else
			button.animate().translationY(-50);
	}


	/*
	Activates and focuses search EditText
	 */

	/**
	 * Used by xml-files for the toolbar
	 * @param view the {@link View} of the clicked button
	 */
	public void onSearchClick(View view) {
		toggleSearchFocus(view);
		searchEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override //Sends search query on ENTER
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_NULL
					&& event.getAction() == KeyEvent.ACTION_DOWN) {
					String input = searchEdit.getText().toString();
					activeSearchTerms = Arrays.asList(input.split(" "));
					FilterImpl filter = new FilterImpl(activeCategory,
						activeSearchTerms, null, null);
					List<Event> newEvents = mDatabase.getEventsByFilter(filter,
						EventsSorter.NULL_SORTER);
					AppDatabase.updateVisibleEvents(newEvents);
					toggleSearchFocus(v);
				}
				return true;
			}
		});
	}

	/*
	* Evaluates whether the user has clicked outside the search EditText
	 */
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			View v = getCurrentFocus();
			if (v instanceof EditText) {
				Rect outRect = new Rect();
				v.getGlobalVisibleRect(outRect);
				if (!outRect.contains((int) event.getRawX(),
					(int) event.getRawY())) {
					toggleSearchFocus(v);
				}
			}
		}
		return super.dispatchTouchEvent(event);
	}

	/**
	 * Used by xml-files for the toolbar
	 */
	public void onMenuClick(View view) {
		mViewHelper.openDrawer();
	}

	//unused?
	public void centerOnMap() {
		mViewHelper.stateSwitch("center_on_map");
	}

	@Override
	public void onVisibleEventsChanged(List<Event> newEvents) {
	}

	@Override
	public boolean onSaveEventButtonPressed(String id) {
		return mSavedEventsHelper.onSaveEventButtonPressed(id);
	}

	@Override
	public boolean isEventSaved(String id) {
		return mSavedEventsHelper.isEventSaved(id);
	}

	@Override
	public void onBackPressed() {
		mViewHelper.stateSwitch("back_button_pressed");
	}

	public List<Event> getSavedEvents(){
		return mSavedEventsHelper.getSavedEvents();
	}

	@Override
	public void updateSavedEventsFrag(){
		mSavedEventsHelper.updateSavedEventsFrag();
	}

	protected void onStart() {
		super.onStart();
		updateSavedEventsFrag();
		App.getGoogleApiHelper().connect();
	}

	@Override public void onRequestPermissionsResult(int requestCode,
		@NonNull String permissions[], @NonNull int[] grantResults)
	{
		if (requestCode != MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION ||
			grantResults.length == 0 ||
			grantResults[0] != PackageManager.PERMISSION_GRANTED) return;
		mGoogleAPIHelper.notifyPositionPermissions();
	}

	@Override public void onDirectionButtonPressed(LatLng destination,
		String transportationMode)
	{
		//Change to MapView
		mViewHelper.stateSwitch("center_on_map");


		//Call the corresponding method in GMap
		Fragment f = getFragmentManager().findFragmentByTag("map");
		if (f instanceof MapFragment && ((MapFragment)f).isMyLocationEnabled())
		{
			((MapFragment)f).showDirections(mGoogleAPIHelper.
				getCurrentLocation(),destination,transportationMode);
		}

		else Toast.makeText(this, R.string.toast_directions,
			Toast.LENGTH_LONG).show();
	}

	// Preconditions: {@code langCode} is either "en", "sv" or "ar"
	public void setLocale(String langCode)
	{
		Configuration newConfig = new Configuration();
		newConfig.setLocale(Locale.forLanguageTag(langCode));

		Resources res = getBaseContext().getResources();
		res.updateConfiguration(newConfig, res.getDisplayMetrics());
		refresh();
	}

	private void broadcastActiveCategory() {
		if (activeCategory == null) return;
		for (int i = 0; i < mActiveCategoryChangeListeners.size(); i++) {
			mActiveCategoryChangeListeners.get(i).onCategoryChange();
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		App.getInstance().setLocaleCode(newConfig.locale.getLanguage());

	}

	@Override
	public void onListFilterChanged(List<Event> newEvents) {
		toggleCategoryButton(0, true);
		toggleCategoryButton(1, true);
		toggleCategoryButton(2, true);
		toggleCategoryButton(3, true);
		activeCategory = null;

		List<Event> events = mDatabase.getEventsByFilter(FilterImpl.
			EMPTY_FILTER, EventsSorter.NULL_SORTER);
		AppDatabase.updateVisibleEvents(events);
	}

	/**
	 * Accesses the {@link SavedEventsHelper} stored in this class
	 * @return The stored {@link SavedEventsHelper}
	 */
	public SavedEventsHelper getSavedEventsHelper() {
		return mSavedEventsHelper;
	}

	//private?
	private void refresh() {
		Intent intent = getIntent();
		overridePendingTransition(0, 0);
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		finish();
		overridePendingTransition(0, 0);
		startActivity(intent);
	}
}