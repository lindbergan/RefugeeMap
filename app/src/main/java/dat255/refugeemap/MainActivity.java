package dat255.refugeemap;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;


import android.graphics.Rect;

import android.content.SharedPreferences;


import android.content.SharedPreferences;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;

import android.util.ArraySet;




import android.util.ArraySet;

import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.Marker;

import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;


import java.util.Iterator;
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


public class MainActivity extends AppCompatActivity
	implements EventListFragment.OnListFragmentInteractionListener,
				DetailFragment.OnFragmentInteractionListener, GMapFragment.ReplaceWithDetailView,
	AppDatabase.Listener{

	FragmentManager fm = getFragmentManager();
	private Toolbar toolbar;
	private ImageButton mButton;
	private String ACTIVE_FRAGMENT;
	private final int MAP_FRAGMENT = 0;
	private final int LIST_FRAGMENT = 1;
	private final int DETAIL_FRAGMENT = 2;
	private Fragment[] currentFragments = new Fragment[3];

	private EditText searchEdit;
	private InputMethodManager inputManager;
	private ImageView logo;
	private ImageButton searchBtn;
	private long lastSearchClickTime=0;
	private int clickThreshold=500;

	private Database mDatabase;

	@Override protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
    ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
    while (PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)){
      System.out.println(""+ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION));
    }
    toolbar = (Toolbar) findViewById(R.id.tool_bar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayShowTitleEnabled(false);

		this.searchEdit = (EditText)findViewById(R.id.et_search);
		this.inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		this.logo = (ImageView)findViewById(R.id.logo);
		this.searchBtn = (ImageButton) findViewById(R.id.action_search);
		try {
			this.mDatabase = new DatabaseImpl(new InputStreamReader(getResources().openRawResource(R.raw.ctgs)),
							new InputStreamReader(getResources().openRawResource(R.raw.db)),
							new JSONToolsImpl());
		}
		catch (FileNotFoundException ex) {
			ex.printStackTrace();
		}

		firstStart();
	}

	@Override
	public void onInfoWindowClicked(Marker marker) {

    String tempValues[] = {"title", "org", "description", "phone", "date", Integer.toString(2)};
    Fragment frag = DetailFragment.newInstance(tempValues);
    fm.beginTransaction().add(R.id.fragment_container, frag).hide(currentFragments[MAP_FRAGMENT]).commit();
    currentFragments[DETAIL_FRAGMENT] = frag;

	}

	public void showEventList(){
		if (ACTIVE_FRAGMENT.equals(GMapFragment.class.getSimpleName())) {
			FragmentManager fm = getFragmentManager();
			Fragment frag = currentFragments[LIST_FRAGMENT];
			fm.beginTransaction().show(frag).hide(currentFragments[MAP_FRAGMENT]).commit();
			ACTIVE_FRAGMENT = EventListFragment.class.getSimpleName();
		}

		else if (ACTIVE_FRAGMENT.equals(EventListFragment.class.getSimpleName())) {
			FragmentManager fm = getFragmentManager();
			Fragment frag = currentFragments[MAP_FRAGMENT];
			fm.beginTransaction().show(frag).hide(currentFragments[LIST_FRAGMENT]).commit();
			ACTIVE_FRAGMENT = GMapFragment.class.getSimpleName();
		}
	}

	public void firstStart() {
		FragmentManager fm = getFragmentManager();
		Fragment mapFrag = new GMapFragment();
    Fragment listFrag = new EventListFragment();
		initializeViews(findViewById(R.id.main_layout));
		fm.beginTransaction().add(R.id.fragment_container, mapFrag)
      .add(R.id.fragment_container, listFrag).hide(listFrag).show(mapFrag).commit();
		currentFragments[MAP_FRAGMENT] = mapFrag;
    currentFragments[LIST_FRAGMENT] = listFrag;
		ACTIVE_FRAGMENT = GMapFragment.class.getSimpleName();
	}

	@Override
	public void onListFragmentInteraction(Event item){


		Fragment frag = DetailFragment.newInstance(new String[]{"title", "org", "description", "phone", "date", Integer.toString(3)});
		fm.beginTransaction().add(R.id.fragment_container, frag).hide(currentFragments[LIST_FRAGMENT]).commit();
		currentFragments[DETAIL_FRAGMENT] = frag;

	}

	public void initializeViews(View view) {
		mButton = (ImageButton) view.findViewById(R.id.change_views_button);

		mButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				showEventList();
				Drawable map = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_map_black_48dp, null);
				Drawable list = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_list_black_48dp, null);
				if (mButton != null && map != null && list != null) {
					if (mButton.getDrawable().getConstantState().equals(map.getConstantState())) {
						mButton.setImageDrawable(list);
					}
					else {
						mButton.setImageDrawable(map);
					}
				}
			}
		});
	}


	/*
	Activates and focuses search EditText
	 */
	public void onSearchClick(View view){
		toggleSearchFocus(view);
		searchEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override //Sends search query on ENTER
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_NULL
								&& event.getAction() == KeyEvent.ACTION_DOWN) {
						String input = searchEdit.getText().toString();
						Collection<String> searchTerms = Arrays.asList(input.split(" "));
						FilterImpl filter = new FilterImpl(new ArrayList<Integer>(), searchTerms);
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
			if ( v instanceof EditText) {
				Rect outRect = new Rect();
				v.getGlobalVisibleRect(outRect);
				if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
					toggleSearchFocus(v);
				}
			}
		}
		return super.dispatchTouchEvent( event );
	}

	public void toggleSearchFocus(View v){
		long currentTime = SystemClock.elapsedRealtime();
		if (this.searchBtn.isEnabled() &&
						// Check if last click time was too recent in order to avoid accidental double-click
						currentTime - lastSearchClickTime > clickThreshold){
			this.searchEdit.setVisibility(View.VISIBLE);
			this.searchEdit.requestFocus();
			this.logo.setVisibility(View.GONE);
			this.inputManager.showSoftInput(searchEdit, InputMethodManager.SHOW_IMPLICIT);
			this.searchBtn.setEnabled(false);
		}
		else {
			v.clearFocus();
			this.inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
			this.searchEdit.setVisibility(View.GONE);
			this.logo.setVisibility(View.VISIBLE);
			this.searchBtn.setEnabled(true);
		}
		lastSearchClickTime = currentTime;
	}

	public void onMenuClick(View view){
		Toast.makeText(this, "You clicked menu!", Toast.LENGTH_SHORT).show();
	}

  public void centerOnMap(View view){
      fm.beginTransaction().remove(currentFragments[DETAIL_FRAGMENT]).
        show(currentFragments[MAP_FRAGMENT]).commit();
      ((GMapFragment)currentFragments[MAP_FRAGMENT]).setCurrentPosition();
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

        if(!savedEvents.contains(id)){
            updatedEventList.add(id);
            editor.putStringSet(getString(R.string.saved_events_key), updatedEventList);
        }else{
            updatedEventList.remove(id);
			editor.putStringSet(getString(R.string.saved_events_key), updatedEventList);

        }

        return editor.commit(); //returns true if values was successfully updated
	}




	@Override
	public boolean isEventSaved(String id){

		try {
			SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
			return prefs.getStringSet(getString(R.string.saved_events_key), null).contains(id);
		}catch(NullPointerException e){
			return false;
		}

	}



}





