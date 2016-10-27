package dat255.refugeemap.app.gui.helper;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dat255.refugeemap.R;
import dat255.refugeemap.app.gui.DetailsFragment;
import dat255.refugeemap.app.gui.DrawerListAdapter;
import dat255.refugeemap.app.gui.EventListFragment;
import dat255.refugeemap.app.gui.FavoritesListFragment;
import dat255.refugeemap.app.gui.ListFilterButtonsFragment;
import dat255.refugeemap.app.gui.MainActivity;
import dat255.refugeemap.app.gui.MapFragment;
import dat255.refugeemap.model.db.Event;

/* A helper class that takes care of the different view changes
and custom view content*/

/**
 * Helper class for managing {@link View}s
 */
public class ViewHelper {

	private static final int MAP_FRAGMENT = 0;
	private static final int LIST_FRAGMENT = 1;
	private static final int DETAIL_FRAGMENT = 2;
	private static final int SAVED_LIST_FRAGMENT = 3;
	public static final int LIST_FILTER_BUTTONS = 4;
	private final FragmentManager fm;
	private boolean drawerOpen = false;
	private final Class[] mFragmentHistory = new Class[2];
	private final Fragment[] currentFragments = new Fragment[5];
	private final MainActivity mActivity;
	private ImageButton mToggleImageButton;
	private DrawerLayout mDrawer;
	private ListView mDrawerListView;
	private Event mCurrentEvent;
	private Drawable mMapIcon;
	private Drawable mListIcon;
	private List<String> mNavItems = new ArrayList<>();

	/**
	 * Constructor.
	 * @param activity the {@link MainActivity} to be used with this helper
	 */
	public ViewHelper(MainActivity activity) {
		mActivity = activity;
		fm = mActivity.getFragmentManager();
	}

	/**
	 * Method which is used to switch between different view modes in the app
	 * @param args a {@link String} object with an appropriate command
	 */
	public void stateSwitch(String args) {


		if (args.equals("app_start")) {
			Fragment mapFrag = new MapFragment();
			Fragment listFrag = new EventListFragment();
			FavoritesListFragment savedListFrag = new FavoritesListFragment();
			mActivity.getSavedEventsHelper().
				addSavedEventListener(savedListFrag);
			Fragment listFilterBtnsFrag = new ListFilterButtonsFragment();
			initializeViews(mActivity.findViewById(R.id.main_layout));

			fm.beginTransaction()
				.add(R.id.fragment_container, mapFrag, "map")
				.add(R.id.fragment_container, listFrag, "list")
				.add(R.id.fragment_container, listFilterBtnsFrag,
					"list_filter_buttons")
				.add(R.id.fragment_container, savedListFrag,
					"saved_events_list_frag")
				.hide(listFrag)
				.hide(savedListFrag)
				.hide(listFilterBtnsFrag)
				.show(mapFrag)
				.commit();
			currentFragments[MAP_FRAGMENT] = mapFrag;
			currentFragments[LIST_FRAGMENT] = listFrag;
			currentFragments[SAVED_LIST_FRAGMENT] = savedListFrag;
			currentFragments[LIST_FILTER_BUTTONS] = listFilterBtnsFrag;
			mFragmentHistory[0] = MapFragment.class;
			mFragmentHistory[1] = null;
		}
		//end starting state

		//**** Map and mListIcon toggle button ****
		else if (args.equals("map_list_toggle")) {
			if (mFragmentHistory[0] == (MapFragment.class)) {
				fm.beginTransaction()
					.show(currentFragments[LIST_FRAGMENT])
					.show(currentFragments[LIST_FILTER_BUTTONS])
					.hide(currentFragments[MAP_FRAGMENT])
					.commit();
				mFragmentHistory[1] = mFragmentHistory[0];
				mFragmentHistory[0] = EventListFragment.class;
				toggleImage(mMapIcon);
			} else if (mFragmentHistory[0] == EventListFragment.class) {
				fm.beginTransaction()
					.show(currentFragments[MAP_FRAGMENT])
					.hide(currentFragments[LIST_FRAGMENT])
					.hide(currentFragments[LIST_FILTER_BUTTONS])
					.commit();
				mFragmentHistory[0] = MapFragment.class;
				mFragmentHistory[1] = EventListFragment.class;
				toggleImage(mListIcon);
			}
		}
		//**** end mMapIcon and mListIcon toggle button *****

		//For "Favourites" button pressed
		/** @author Jonathan S */
		else if (args.equals("favourites_button_pressed")) {
			if (currentFragments[DETAIL_FRAGMENT] != null) {
				fm.beginTransaction()
					.remove(currentFragments[DETAIL_FRAGMENT])
					.commit();
			}
			fm.beginTransaction()
				.hide(currentFragments[LIST_FILTER_BUTTONS])
				.hide(currentFragments[LIST_FRAGMENT])
				.hide(currentFragments[MAP_FRAGMENT])
				.show(currentFragments[SAVED_LIST_FRAGMENT])
				.commit();
			mFragmentHistory[1] = mFragmentHistory[0];
			mFragmentHistory[0] = FavoritesListFragment.class;
			setToggleButtonVisible(false);
			closeDrawer();
		}


		//For back button pressed
		else if (args.equals("back_button_pressed")) {

			if (this.drawerOpen) {
				this.closeDrawer();
				return;
			}

			if (mFragmentHistory[0] == MapFragment.class) {
				if (mFragmentHistory[1] == null) {
					Intent intent = new Intent(Intent.ACTION_MAIN);
					intent.addCategory(Intent.CATEGORY_HOME);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					mActivity.startActivity(intent);
				}
			}

			else if (mFragmentHistory[0] == EventListFragment.class) {
				if (mFragmentHistory[1] == MapFragment.class) {
					fm.beginTransaction()
						.hide(currentFragments[LIST_FRAGMENT])
						.hide(currentFragments[LIST_FILTER_BUTTONS])
						.show(currentFragments[MAP_FRAGMENT])
						.commit();
					mFragmentHistory[0] = mFragmentHistory[1];
					mFragmentHistory[1] = null;
					toggleImage(mListIcon);
					setToggleButtonVisible(true);
				}
			}

			else if (mFragmentHistory[0] == FavoritesListFragment.class) {
				if (mFragmentHistory[1] == MapFragment.class) {
					fm.beginTransaction()
						.hide(currentFragments[SAVED_LIST_FRAGMENT])
						.show(currentFragments[MAP_FRAGMENT])
						.commit();
					mFragmentHistory[0] = mFragmentHistory[1];
					mFragmentHistory[1] = null;
					toggleImage(mListIcon);
					setToggleButtonVisible(true);
				}
				else if (mFragmentHistory[1] == EventListFragment.class) {
					fm.beginTransaction()
						.hide(currentFragments[SAVED_LIST_FRAGMENT])
						.show(currentFragments[LIST_FILTER_BUTTONS])
						.show(currentFragments[LIST_FRAGMENT])
						.commit();
					mFragmentHistory[0] = mFragmentHistory[1];
					mFragmentHistory[1] = MapFragment.class;
					toggleImage(mMapIcon);
					setToggleButtonVisible(true);
				}
				else {
					fm.beginTransaction()
						.hide(currentFragments[SAVED_LIST_FRAGMENT])
						.show(currentFragments[MAP_FRAGMENT])
						.commit();
					mFragmentHistory[0] = MapFragment.class;
					mFragmentHistory[1] = null;
					toggleImage(mListIcon);
					setToggleButtonVisible(true);
				}
			}

			else if (mFragmentHistory[0] == DetailsFragment.class) {
				if (mFragmentHistory[1] == MapFragment.class) {
					fm.beginTransaction()
						.remove(currentFragments[DETAIL_FRAGMENT])
						.show(currentFragments[MAP_FRAGMENT])
						.commit();
					mFragmentHistory[0] = MapFragment.class;
					mFragmentHistory[1] = null;
					toggleImage(mListIcon);
					setToggleButtonVisible(true);
				}
				else if (mFragmentHistory[1] == EventListFragment.class) {
					fm.beginTransaction()
						.remove(currentFragments[DETAIL_FRAGMENT])
						.show(currentFragments[LIST_FILTER_BUTTONS])
						.show(currentFragments[LIST_FRAGMENT])
						.commit();
					mFragmentHistory[0] = mFragmentHistory[1];
					mFragmentHistory[1] = MapFragment.class;
					toggleImage(mMapIcon);
					setToggleButtonVisible(true);
				}
				else if (mFragmentHistory[1] == FavoritesListFragment.class) {
					fm.beginTransaction()
						.remove(currentFragments[DETAIL_FRAGMENT])
						.show(currentFragments[SAVED_LIST_FRAGMENT])
						.commit();
					mFragmentHistory[0] = mFragmentHistory[1];
					mFragmentHistory[1] = MapFragment.class;
					setToggleButtonVisible(false);
				}
			}
		}

		//For: center on mMapIcon button
		else if (args.equals("center_on_map")) {
			fm.beginTransaction().remove(currentFragments[DETAIL_FRAGMENT]).
				show(currentFragments[MAP_FRAGMENT]).commit();
			currentFragments[DETAIL_FRAGMENT] = null;

			/** @author: Sebastian **/
			mFragmentHistory[0] = MapFragment.class;
			mFragmentHistory[1] = null;
			/** end edit by Sebastian **/
			toggleImage(mListIcon);
			setToggleButtonVisible(true);
		}
		//end center on mMapIcon button

		//For: click on mListIcon item
		else if (args.equals("list_item_clicked")) {
			Fragment frag = DetailsFragment.newInstance(mCurrentEvent);
			fm.beginTransaction()
				.add(R.id.fragment_container, frag)
				.hide(currentFragments[LIST_FRAGMENT])
				.hide(currentFragments[LIST_FILTER_BUTTONS])
				.hide(currentFragments[SAVED_LIST_FRAGMENT])
				.commit();
			setToggleButtonVisible(false);
			/** @author: Sebastian **/
			/** end edit by Sebastian **/
			currentFragments[DETAIL_FRAGMENT] = frag;
			mFragmentHistory[0] = DetailsFragment.class;
			mFragmentHistory[1] = EventListFragment.class;
		}
		//end click on mListIcon item

		//For: click on marker
		else if (args.equals("marker_clicked")) {
			Fragment frag = DetailsFragment.newInstance(mCurrentEvent);
			fm.beginTransaction()
				.add(R.id.fragment_container, frag)
				.hide(currentFragments[MAP_FRAGMENT])
				.commit();
			setToggleButtonVisible(false);
			/** @author: Sebastian **/
			/** end edit by Sebastian **/
			currentFragments[DETAIL_FRAGMENT] = frag;
			mFragmentHistory[0] = DetailsFragment.class;
			mFragmentHistory[1] = MapFragment.class;
		}
		//end click on marker
	}

	private void toggleImage(Drawable icon) {
		mToggleImageButton.setImageDrawable(icon);
	}

	private void initializeViews(View view) {

		mMapIcon = ResourcesCompat.getDrawable(mActivity.getResources(),
			R.drawable.ic_map_black_48dp, null);

		mListIcon = ResourcesCompat.getDrawable(mActivity.getResources(),
			R.drawable.ic_list_black_48dp, null);

		mToggleImageButton = (ImageButton)view.findViewById(R.
			id.change_views_button);

		mToggleImageButton.setOnClickListener(new View.OnClickListener()
			{
				@Override public void onClick(View view)
				{ stateSwitch("map_list_toggle"); }
			}
		);
	}

	private void setToggleButtonVisible(boolean showButton) {
		if (showButton)
			mToggleImageButton.setVisibility(View.VISIBLE);
		else
			mToggleImageButton.setVisibility(View.INVISIBLE);
	}

	/**
	 * Sets up the {@link DrawerLayout} for use
	 */
	public void setUpNavigationDrawer() {
		mDrawer = (DrawerLayout) mActivity.findViewById(R.id.drawer_layout);
		mDrawerListView = (ListView)mActivity.findViewById(R.
			id.drawer_listView);
		setNavigationListItems();
	}

	//private?
	private void setNavigationListItems() {
		List<String> temp = new ArrayList<>();
		String[] navOptions = mActivity.getBaseContext().getResources().
			getStringArray(R.array.drawer_list_items);
		Collections.addAll(temp, navOptions);
		mNavItems = temp;
		mDrawerListView.setAdapter(new DrawerListAdapter(mActivity.
			getBaseContext(), mNavItems));
		mDrawerListView.setOnItemClickListener(new DrawerItemClickListener());
	}

	//private?
	private void setLanguageListItems() {
		List<String> temp = new ArrayList<>();
		String[] languages = mActivity.getBaseContext().getResources().
			getStringArray(R.array.language_options);
		Collections.addAll(temp, languages);
		mNavItems = temp;
		mDrawerListView.setAdapter(new DrawerListAdapter(mActivity.
			getBaseContext(), mNavItems));
		mDrawerListView.setOnItemClickListener(new DrawerItemClickListener());
	}

	/**
	 * Opens the {@link DrawerLayout} set up in setUpNavigationDrawer
	 */
	public void openDrawer() {
		mDrawer.openDrawer(mDrawerListView);
		drawerOpen = true;
	}

	/**
	 * Closes the {@link DrawerLayout} set up in setUpNavigationDrawer
	 */
	private void closeDrawer() {
		mDrawer.closeDrawer(mDrawerListView);
		drawerOpen = false;
	}

	/** @author Jonathan S */
	private class DrawerItemClickListener implements
		ListView.OnItemClickListener
	{

		@Override public void onItemClick(AdapterView<?> parent, View view,
			int position, long id)
		{
			String navItem = mNavItems.get(position);
			if (navItem == null) return;

			if (navItem.equals(mActivity.getString(R.string.navitem_favorites)))
			{
				List<Event> savedEvents = mActivity.getSavedEvents();

				if (savedEvents != null && savedEvents.size() > 0)
					stateSwitch("favourites_button_pressed");
				else Toast.makeText(mActivity.getApplicationContext(),
					R.string.toast_viewhelper_no_events_saved,
					Toast.LENGTH_SHORT).show();
				return;
			}

			if (navItem.equals(mActivity.getString(R.string.navitem_language)))
			{
				setLanguageListItems();
				return;
			}

			if (navItem.equals(mActivity.getString(R.string.language_english)))
				mActivity.setLocale("en");
			if (navItem.equals(mActivity.getString(R.string.language_swedish)))
				mActivity.setLocale("sv");
			if (navItem.equals(mActivity.getString(R.string.language_arabic)))
				mActivity.setLocale("ar");
			setNavigationListItems();
			closeDrawer();
		}
	}

	public void setCurrentEvent(Event event) {
		mCurrentEvent = event;
	}

	public Fragment[] getCurrentFragments() {
		return currentFragments.clone();
	}
}