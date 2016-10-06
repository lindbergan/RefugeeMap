package dat255.refugeemap;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ListFragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.Marker;

import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Locale;



import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import dat255.refugeemap.detailView.DetailFragment;
import dat255.refugeemap.model.db.Database;
import dat255.refugeemap.model.db.Event;
import dat255.refugeemap.model.db.EventCollection;
import dat255.refugeemap.model.db.impl.DatabaseImpl;
import dat255.refugeemap.model.db.impl.FilterImpl;
import dat255.refugeemap.model.db.impl.JSONToolsImpl;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;


public class MainActivity extends AppCompatActivity
		implements EventListFragment.OnListFragmentInteractionListener,
		DetailFragment.OnFragmentInteractionListener, GMapFragment.ReplaceWithDetailView,
		AppDatabase.Listener {

	private final int MAP_FRAGMENT = 0;
	private final int LIST_FRAGMENT = 1;
	private final int DETAIL_FRAGMENT = 2;
	FragmentManager fm = getFragmentManager();
	private Toolbar toolbar;
	private ImageButton mButton;
	private String ACTIVE_FRAGMENT;
	private Fragment[] currentFragments = new Fragment[3];

	private EditText searchEdit;
	private InputMethodManager inputManager;
	private ImageView logo;
	private ImageButton searchBtn;
	private long lastSearchClickTime = 0;
	private int clickThreshold = 500;
	public static String sDefSystemLanguage;
    private String[] drawerListItems;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerListView;

	private Database mDatabase;



	@Override protected void onCreate(Bundle savedInstanceState)
	{

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
		while (PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
			System.out.println("" + ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION));

		}

		toolbar = (Toolbar) findViewById(R.id.tool_bar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayShowTitleEnabled(false);

		this.searchEdit = (EditText) findViewById(R.id.et_search);
		this.inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		this.logo = (ImageView) findViewById(R.id.logo);
		this.searchBtn = (ImageButton) findViewById(R.id.action_search);
		try {
			this.mDatabase = new DatabaseImpl(new InputStreamReader(getResources().openRawResource(R.raw.ctgs)),
					new InputStreamReader(getResources().openRawResource(R.raw.db)),
					new JSONToolsImpl());
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		}

		stateSwitch("app_start");

		setUpNavigationDrawer();
	}


	public void setUpNavigationDrawer() {

        drawerListItems = getResources().getStringArray(R.array.drawer_list_items);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerListView = (ListView) findViewById(R.id.drawer_listView);

        //setting the custom content in the drawer
        mDrawerListView.setAdapter(new ArrayAdapter<String>(this,
            R.layout.drawer_list_item, R.id.drawer_list_item, drawerListItems));

        mDrawerListView.setOnItemClickListener(new DrawerItemClickListener());
    }


    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            if (drawerListItems[position].equals("Favourites")){

                Toast.makeText(getApplicationContext(), "you have clicked Favourites",
                    Toast.LENGTH_SHORT).show();
                EventCollection events = getSavedEvents();

            }

       }
    }

        /**
         * setLocaleToArabic is used for testing purposes. Changes reading from R -> L and changes all text to arabic
         */

	public void setLocaleToArabic() {
		Configuration newConfig = new Configuration();
		newConfig.setLocale(new Locale("ar"));
		getBaseContext().getResources().updateConfiguration(newConfig, getBaseContext().getResources().getDisplayMetrics());

	}

	@Override
	public void onInfoWindowClicked(Marker marker) {

		stateSwitch("marker_clicked");


	}



	@Override
	public void onListFragmentInteraction(Event item) {
		stateSwitch("list_item_clicked");
	}

	public void initializeViews(View view) {
		mButton = (ImageButton) view.findViewById(R.id.change_views_button);

		mButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				stateSwitch("map_list_toggle");
			}
		});
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
				if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
					toggleSearchFocus(v);
				}
			}
		}
		return super.dispatchTouchEvent(event);
	}

	public void toggleSearchFocus(View v) {
		long currentTime = SystemClock.elapsedRealtime();
		if (this.searchBtn.isEnabled() &&
						// Check if last click time was too recent in order to avoid accidental double-click
						currentTime - lastSearchClickTime > clickThreshold){
			AppDatabase.updateVisibleEvents(mDatabase.getAllEvents());
			this.searchEdit.setVisibility(VISIBLE);
			this.searchEdit.requestFocus();
			this.logo.setVisibility(GONE);
			this.inputManager.showSoftInput(searchEdit, InputMethodManager.SHOW_IMPLICIT);
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

	public void onMenuClick(View view) {
		Toast.makeText(this, "You clicked menu!", Toast.LENGTH_SHORT).show();
	}

	public void centerOnMap(View view) {
		stateSwitch("center_on_map");

	}

	@Override
	public void onVisibleEventsChanged(EventCollection newEvents) {

	}

	@Override
	public boolean onSaveEventButtonPressed(String id) {

		SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();

		//creates new TreeSet if no Set already linked to key
		Set<String> savedEvents = prefs.getStringSet(getString(R.string.saved_events_key), new TreeSet<String>());
		Set<String> updatedEventList = new TreeSet<>(savedEvents);

		if (!savedEvents.contains(id)) {
			updatedEventList.add(id);
			editor.putStringSet(getString(R.string.saved_events_key), updatedEventList);
		} else {
			updatedEventList.remove(id);
			editor.putStringSet(getString(R.string.saved_events_key), updatedEventList);

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
	public void onBackPressed(){
		stateSwitch("back_button_pressed");
	}

	private void stateSwitch(String args){
		FragmentManager fm = getFragmentManager();

		//Starting state
		if(args.equals("app_start")){
			Fragment mapFrag = new GMapFragment();
			Fragment listFrag = new EventListFragment();
			initializeViews(findViewById(R.id.main_layout));
			fm.beginTransaction().add(R.id.fragment_container, mapFrag)
				.add(R.id.fragment_container, listFrag).hide(listFrag).show(mapFrag).commit();
			currentFragments[MAP_FRAGMENT] = mapFrag;
			currentFragments[LIST_FRAGMENT] = listFrag;
			ACTIVE_FRAGMENT = GMapFragment.class.getSimpleName();
		}
		//end starting state

		//**** Map and list toggle button ****
		else if(args.equals("map_list_toggle")){
			if (ACTIVE_FRAGMENT.equals(GMapFragment.class.getSimpleName())) {
				Fragment frag = currentFragments[LIST_FRAGMENT];
				fm.beginTransaction().show(frag).hide(currentFragments[MAP_FRAGMENT]).commit();
				ACTIVE_FRAGMENT = EventListFragment.class.getSimpleName();
			} else if (ACTIVE_FRAGMENT.equals(EventListFragment.class.getSimpleName())) {
				Fragment frag = currentFragments[MAP_FRAGMENT];
				fm.beginTransaction().show(frag).hide(currentFragments[LIST_FRAGMENT]).commit();
				ACTIVE_FRAGMENT = GMapFragment.class.getSimpleName();
			}
			toggleImage();
		}
		//**** end map and list toggle button *****

		//For back button pressed
		else if(args.equals("back_button_pressed")){
			if(currentFragments[DETAIL_FRAGMENT] != null){
				fm.beginTransaction().remove(currentFragments[DETAIL_FRAGMENT])
					.hide(currentFragments[LIST_FRAGMENT])
					.show(currentFragments[MAP_FRAGMENT]).commit();
				ACTIVE_FRAGMENT = GMapFragment.class.getSimpleName();
				toggleImage();
				showHideToggleButton(true);
			}
			else if(ACTIVE_FRAGMENT.equals(EventListFragment.class.getSimpleName())) {
				Fragment frag = currentFragments[MAP_FRAGMENT];
				fm.beginTransaction().show(frag).hide(currentFragments[LIST_FRAGMENT]).commit();
				ACTIVE_FRAGMENT = GMapFragment.class.getSimpleName();
			}
		}
		//end back button

		//For: center on map button
		else if(args.equals("center_on_map")){
			fm.beginTransaction().remove(currentFragments[DETAIL_FRAGMENT]).
				show(currentFragments[MAP_FRAGMENT]).commit();
			currentFragments[DETAIL_FRAGMENT] = null;
			showHideToggleButton(true);
		}
		//end center on map button

		//For: click on list item
		else if(args.equals("list_item_clicked")){
			Fragment frag = DetailFragment.newInstance(new String[]{"title", "org", "description", "phone", "date", Integer.toString(3)});
			fm.beginTransaction().add(R.id.fragment_container, frag).hide(currentFragments[LIST_FRAGMENT]).commit();
			showHideToggleButton(false);
			currentFragments[DETAIL_FRAGMENT] = frag;
		}
		//end click on list item

		//For: click on marker
		else if(args.equals("")){
			String tempValues[] = {"title", "org", "description", "phone", "date", Integer.toString(2)};
			Fragment frag = DetailFragment.newInstance(tempValues);
			fm.beginTransaction().add(R.id.fragment_container, frag).hide(currentFragments[MAP_FRAGMENT]).commit();
			showHideToggleButton(false);
			currentFragments[DETAIL_FRAGMENT] = frag;
		}
		//end click on marker

	}


	private void showHideToggleButton(boolean showButton){
		if(showButton)
			mButton.setVisibility(VISIBLE);
		else
			mButton.setVisibility(INVISIBLE);
	}

    public EventCollection getSavedEvents(){

        Set<String> savedEventsStr = getPreferences(Context.MODE_PRIVATE).getStringSet(getString(R.string.saved_events_key), null);

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

	private void toggleImage(){
		Drawable map = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_map_black_48dp, null);
		Drawable list = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_list_black_48dp, null);
		if (mButton != null && map != null && list != null) {
			if (mButton.getDrawable().getConstantState().equals(map.getConstantState())) {
				mButton.setImageDrawable(list);
			} else {
				mButton.setImageDrawable(map);
			}
		}
	}

	@Override
	protected void onStart() {
		super.onStart();

		App.getGoogleApiHelper().connect();
	}
}





