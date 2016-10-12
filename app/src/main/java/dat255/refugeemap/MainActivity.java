package dat255.refugeemap;

import android.Manifest;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
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

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import dat255.refugeemap.helpers.ViewHelper;
import dat255.refugeemap.model.db.Database;
import dat255.refugeemap.model.db.Event;
import dat255.refugeemap.model.db.EventCollection;
import dat255.refugeemap.model.db.impl.FilterImpl;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class MainActivity extends AppCompatActivity
		implements EventListFragment.OnListFragmentInteractionListener,
		DetailFragment.OnFragmentInteractionListener,
        GMapFragment.ReplaceWithDetailView, AppDatabase.Listener {

    private String[] mDrawerListItems;
    private ViewHelper mViewHelper;
    private long lastSearchClickTime = 0;
    private int clickThreshold = 500;
    private InputMethodManager inputManager;
    private ImageView logo;
    private EditText searchEdit;
    private ImageButton searchBtn;
    private Database mDatabase;
	private Toolbar toolbar;
	private ArrayList<Integer> activeCategories = new ArrayList<>();
	private String currentLocale;

	@Override protected void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        mViewHelper = new ViewHelper(this);
		setContentView(R.layout.activity_main);
		ActivityCompat.requestPermissions(this, new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION}, 1);

		while (PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION)) {
			System.out.println("" + ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION));
		}

        setUpViews();
        setUpToolbar();
        this.inputManager = (InputMethodManager) getSystemService(
            Context.INPUT_METHOD_SERVICE);

        try {
        	AppDatabase.init(this);
			this.mDatabase = AppDatabase.getDatabaseInstance();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		// setLocaleToArabic();
        mViewHelper.stateSwitch("app_start");
        mDrawerListItems = getResources().getStringArray(R.array.drawer_list_items);
		mViewHelper.setUpNavigationDrawer(mDrawerListItems);
	}

    public void setUpViews(){
        this.logo = (ImageView) findViewById(R.id.logo);
        this.searchEdit = (EditText) findViewById(R.id.et_search);
        this.searchBtn = (ImageButton) findViewById(R.id.action_search);
    }

    public void setUpToolbar(){
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
		setSupportActionBar(toolbar);
		if (getSupportActionBar() != null) {
			getSupportActionBar().setDisplayShowTitleEnabled(false);
		}
    }

    @Override
	public void onInfoWindowClicked(Marker marker) {

		if(marker.getTag() instanceof Event) {
			Event currentEvent = (Event) marker.getTag();
			mViewHelper.setEventInformation(extractDetailedInformation(
				currentEvent));
		}

        mViewHelper.stateSwitch("marker_clicked");
	}

	public String[] extractDetailedInformation(Event event){

		//TODO:Need to have "time" variable as well
		return new String[]{event.getID().toString(), event.getTitle(),
			event.getAddress(), event.getContactInformation(),
			event.getDescription("sv"), //OBS!!
			event.getLongitude().toString(), event.getLatitude().toString()};
	}

	@Override
	public void onListFragmentInteraction(Event item) {

		mViewHelper.setEventInformation(extractDetailedInformation(item));
        mViewHelper.stateSwitch("list_item_clicked");
	}
  
    public void toggleSearchFocus(View v) {
        long currentTime = SystemClock.elapsedRealtime();
        if (this.searchBtn.isEnabled() &&
            // Check if last click time was too recent in order to avoid
            // accidental double-click
            currentTime - lastSearchClickTime > clickThreshold) {
            AppDatabase.updateVisibleEvents(mDatabase.getAllEvents());
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

	public void onCategoryClick(View view){
		int cat = Integer.parseInt(view.getTag().toString());
		boolean buttonActivated=false;
		if (activeCategories.contains(cat)){
			int index = activeCategories.indexOf(cat);
			activeCategories.remove(index);
			buttonActivated=true;
		}
		else {
			activeCategories.add(cat);
		}
		toggleCategoryButton(cat, buttonActivated);
		FilterImpl filter = new FilterImpl(activeCategories, null, null);
		EventCollection newEvents = mDatabase.getEventsByFilter(filter);

		AppDatabase.updateVisibleEvents(newEvents);
	}

	public void toggleCategoryButton(int category, boolean activated){
		int id = getResources().getIdentifier("category"+category, "id", getPackageName());
		ImageButton button = (ImageButton)findViewById(id);
		if (activated)
			button.animate().translationY(0);
		else
			button.animate().translationY(-50);
	}


	/*
	Activates and focuses search EditText
	 */
	public void onSearchClick(View view) {
		toggleSearchFocus(view);
		searchEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override //Sends search query on ENTER
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_NULL
						&& event.getAction() == KeyEvent.ACTION_DOWN) {
					String input = searchEdit.getText().toString();
					Collection<String> searchTerms = Arrays.asList(input.split(" "));
					FilterImpl filter = new FilterImpl(null, searchTerms, null);
					EventCollection newEvents = mDatabase.getEventsByFilter(filter);
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

	public void onMenuClick(View view) {
        mViewHelper.openDrawer();
	}

    public void centerOnMap(View view) {
        mViewHelper.stateSwitch("center_on_map");
    }

	@Override
	public void onVisibleEventsChanged(EventCollection newEvents) {
	}

	@Override
	public boolean onSaveEventButtonPressed(String id) {
		SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();

		//creates new TreeSet if no Set already linked to key
		Set<String> savedEvents = prefs.getStringSet(getString(
            R.string.saved_events_key),
            new TreeSet<String>());
		Set<String> updatedEventList = new TreeSet<>(savedEvents);

		if (!savedEvents.contains(id)) {

			updatedEventList.add(id);
			editor.putStringSet(getString(R.string.saved_events_key),
                updatedEventList);
		} else {

			updatedEventList.remove(id);
			editor.putStringSet(getString(R.string.saved_events_key),
                updatedEventList);
		}

		return editor.commit(); //returns true if save/remove was successful
	}

	@Override
	public boolean isEventSaved(String id) {
		try {
			SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
			return prefs.getStringSet(getString(R.string.saved_events_key), null).contains(id);
		} catch (NullPointerException e) {
			return false;
		}
	}

	@Override
	public void onBackPressed() {
        mViewHelper.stateSwitch("back_button_pressed");

	}

    public EventCollection getSavedEvents(){

        Set<String> savedEventsStr = getPreferences(Context.MODE_PRIVATE)
            .getStringSet(getString(R.string.saved_events_key), null);

        if(savedEventsStr != null){
            List<Integer> savedEventsIds = new LinkedList<>();

            for(String s : savedEventsStr){
                savedEventsIds.add(Integer.valueOf(s));
            }
            return mDatabase.getEvents(savedEventsIds);
        }else{
            return null;
        }
    }

	@Override

	public void updateSavedEventsFrag(){

		android.app.Fragment frag = getFragmentManager().findFragmentByTag("saved_events_list_frag");
			if(frag instanceof EventListFragment){
				//((EventListFragment) frag).fillListFragment(getSavedEvents());
				((EventListFragment) frag).onVisibleEventsChanged(getSavedEvents());
			}

		}

	protected void onStart() {
		super.onStart();

		App.getGoogleApiHelper().connect();
	}

	@Override
	public void DirectionButtonPressed(LatLng origin, LatLng destination, String transportationMode){
		//Change to MapView
		mViewHelper.stateSwitch("center_on_map");

        //Call the corresponding method in GMap
        Fragment f = getFragmentManager().findFragmentByTag("map");
        if(f instanceof GMapFragment){
            ((GMapFragment) f).showDirections(origin,destination,transportationMode);
        }
	}
	/**
	 * setLocaleToArabic is used for testing purposes.
	 * Changes reading from R -> L and changes all text to arabic
	 */
	public void setLocaleToArabic() {
		Configuration newConfig = new Configuration();
		newConfig.setLocale(new Locale("ar"));
		getBaseContext().getResources().updateConfiguration(newConfig,
				getBaseContext().getResources().getDisplayMetrics());
		currentLocale = getString(R.string.arabic_locale_id);
	}

	public void setLocaleToSwedish() {
		Configuration newConfig = new Configuration();
		newConfig.setLocale(new Locale("sv"));
		getBaseContext().getResources().updateConfiguration(newConfig,
				getBaseContext().getResources().getDisplayMetrics());
        currentLocale = getString(R.string.swedish_locale_id);
	}

    public String getCurrentLocale() {
        return currentLocale;
    }
}





