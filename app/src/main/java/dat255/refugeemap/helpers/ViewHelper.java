package dat255.refugeemap.helpers;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;

import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.List;

import dat255.refugeemap.DetailFragment;
import dat255.refugeemap.EventListFragment;
import dat255.refugeemap.FavoritesListFragment;
import dat255.refugeemap.GMapFragment;
import dat255.refugeemap.MainActivity;
import dat255.refugeemap.R;
import dat255.refugeemap.ListFilterButtonsFragment;
import dat255.refugeemap.model.db.Event;

/* A helper class that takes care of the different view changes
and custom view content*/

public class ViewHelper {

	public static final int MAP_FRAGMENT = 0;
	public static final int LIST_FRAGMENT = 1;
	public static final int DETAIL_FRAGMENT = 2;
	public static final int SAVED_LIST_FRAGMENT = 3;
	public static final int LIST_FILTER_BUTTONS = 4;
	FragmentManager fm;
	private boolean drawerOpen = false;
	private Class[] mFragmentHistory = new Class[2];
	private Fragment[] currentFragments = new Fragment[5];
	private MainActivity mActivity;
	private ImageButton mToggleImageButton;
	private DrawerLayout mDrawer;
	private ListView mDrawerListView;
	private String[] mDrawerListItems;
	private Event mCurrentEvent;


	private Drawable mMapIcon;
	private Drawable mListIcon;

	public ViewHelper(MainActivity activity) {
		mActivity = activity;
		activity.getSavedEventsHelper();
		fm = mActivity.getFragmentManager();
	}

	public void stateSwitch(String args) {


		if (args.equals("app_start")) {
			Fragment mapFrag = new GMapFragment();
			Fragment listFrag = new EventListFragment();
			FavoritesListFragment savedListFrag = new FavoritesListFragment();
			mActivity.getSavedEventsHelper().addSavedEventListener(savedListFrag);
			Fragment listFilterBtnsFrag = new ListFilterButtonsFragment();
			initializeViews(mActivity.findViewById(R.id.main_layout));
			fm.beginTransaction()
					.add(R.id.fragment_container, mapFrag, "map")
					.add(R.id.fragment_container, listFrag, "list")
					.add(R.id.fragment_container, listFilterBtnsFrag,
						"list_filter_buttons")
					.add(R.id.fragment_container, savedListFrag, "saved_events_list_frag")
					.hide(listFrag)
					.hide(savedListFrag)
					.hide(listFilterBtnsFrag)
					.show(mapFrag)
					.commit();
			currentFragments[MAP_FRAGMENT] = mapFrag;
			currentFragments[LIST_FRAGMENT] = listFrag;
			currentFragments[SAVED_LIST_FRAGMENT] = savedListFrag;
			currentFragments[LIST_FILTER_BUTTONS] = listFilterBtnsFrag;
			mFragmentHistory[0] = GMapFragment.class;
			mFragmentHistory[1] = null;
		}
		//end starting state

		//**** Map and mListIcon toggle button ****
		else if (args.equals("map_list_toggle")) {
			if (mFragmentHistory[0] == (GMapFragment.class)) {
				Fragment frag = currentFragments[LIST_FRAGMENT];
				Fragment listFilterButtonsFrag =
					currentFragments[LIST_FILTER_BUTTONS];
				fm.beginTransaction()
						.show(frag)
						.show(listFilterButtonsFrag)
						.hide(currentFragments[MAP_FRAGMENT])
						.commit();
				mFragmentHistory[1] = mFragmentHistory[0];
				mFragmentHistory[0] = EventListFragment.class;
				toggleImage(mMapIcon);
			} else if (mFragmentHistory[0] == EventListFragment.class) {
				Fragment frag = currentFragments[MAP_FRAGMENT];
				fm.beginTransaction()
						.show(frag)
						.hide(currentFragments[LIST_FRAGMENT])
						.hide(currentFragments[LIST_FILTER_BUTTONS])
						.commit();
				mFragmentHistory[1] = mFragmentHistory[0];
				mFragmentHistory[0] = GMapFragment.class;
				toggleImage(mListIcon);
			}
		}
		//**** end mMapIcon and mListIcon toggle button *****

		//For "Favourites" button pressed
        /** @author Jonathan S, Adrian */
		else if (args.equals("favourites_button_pressed")) {
			if (currentFragments[DETAIL_FRAGMENT] != null) {
				fm.beginTransaction()
						.remove(currentFragments[DETAIL_FRAGMENT])
						.commit();
			}
			if (mFragmentHistory[0] == EventListFragment.class) {
				fm.beginTransaction()
						.hide(currentFragments[LIST_FRAGMENT])
						.hide(currentFragments[LIST_FILTER_BUTTONS])
						.commit();
			}
			if (mFragmentHistory[0] == GMapFragment.class) {
				fm.beginTransaction()
						.hide(currentFragments[MAP_FRAGMENT])
						.commit();
			}
			fm.beginTransaction()
					.hide(currentFragments[LIST_FILTER_BUTTONS])
					.show(currentFragments[SAVED_LIST_FRAGMENT])
					.show(currentFragments[LIST_FILTER_BUTTONS])
					.commit();
			closeDrawer();
			mFragmentHistory[1] = mFragmentHistory[0];
			mFragmentHistory[0] = EventListFragment.class;
			setToggleButtonVisible(false);
		}


		//For back button pressed
		else if (args.equals("back_button_pressed")) {

			/** @author: Sebastian
			 * if we're on the mMapIcon view, minimize the app (default back btn
			behaviour)
			 **/

			if (this.drawerOpen) {
				this.closeDrawer();
			}
			else {
				if (mFragmentHistory[1] == null) {

					Intent intent = new Intent(Intent.ACTION_MAIN);
					intent.addCategory(Intent.CATEGORY_HOME);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					mActivity.startActivity(intent);

				}

				else{
					fm.beginTransaction()
							.hide(currentFragments[LIST_FILTER_BUTTONS])
							.hide(currentFragments[SAVED_LIST_FRAGMENT])
							.commit();

					if (currentFragments[DETAIL_FRAGMENT] != null) {

						if (mFragmentHistory[1] == EventListFragment.class) {
							fm.beginTransaction()
									.remove(currentFragments[DETAIL_FRAGMENT])
									.show(currentFragments[LIST_FRAGMENT])
									.show(currentFragments[LIST_FILTER_BUTTONS])
									.hide(currentFragments[MAP_FRAGMENT])
									.commit();

							mFragmentHistory[1] = mFragmentHistory[0];
							mFragmentHistory[0] = EventListFragment.class;
							setToggleButtonVisible(true);
						}
						else if (mFragmentHistory[0] == EventListFragment.class && mFragmentHistory[1] == DetailFragment.class) {
							fm.beginTransaction()
									.remove(currentFragments[DETAIL_FRAGMENT])
									.hide(currentFragments[LIST_FRAGMENT])
									.hide(currentFragments[LIST_FILTER_BUTTONS])
									.show(currentFragments[MAP_FRAGMENT])
									.commit();
							mFragmentHistory[1] = null;
							mFragmentHistory[0] = GMapFragment.class;
							toggleImage(mListIcon);
							setToggleButtonVisible(true);
						}
						else {
							fm.beginTransaction()
									.remove(currentFragments[DETAIL_FRAGMENT])
									.hide(currentFragments[LIST_FRAGMENT])
									.hide(currentFragments[LIST_FILTER_BUTTONS])
									.show(currentFragments[MAP_FRAGMENT])
									.commit();
							mFragmentHistory[1] = null;
							mFragmentHistory[0] = GMapFragment.class;
							toggleImage(mListIcon);
							setToggleButtonVisible(true);
						}
					}
					else if (mFragmentHistory[0] == EventListFragment.class) {
						Fragment frag = currentFragments[MAP_FRAGMENT];
						fm.beginTransaction()
								.show(frag)
								.hide(currentFragments[LIST_FRAGMENT])
								.hide(currentFragments[LIST_FILTER_BUTTONS])
								.hide(currentFragments[SAVED_LIST_FRAGMENT])
								.commit();
						mFragmentHistory[1] = null;
						mFragmentHistory[0] = GMapFragment.class;
						toggleImage(mListIcon);
						setToggleButtonVisible(true);
					}

				}
			}
		}

		//For: center on mMapIcon button
		else if (args.equals("center_on_map")) {
			fm.beginTransaction().remove(currentFragments[DETAIL_FRAGMENT]).
					show(currentFragments[MAP_FRAGMENT]).commit();
			currentFragments[DETAIL_FRAGMENT] = null;

			/** @author: Sebastian **/
			mFragmentHistory[0] = GMapFragment.class;
			mFragmentHistory[1] = null;
			/** end edit by Sebastian **/
			toggleImage(mListIcon);
			setToggleButtonVisible(true);
		}
		//end center on mMapIcon button

		//For: click on mListIcon item
		else if (args.equals("list_item_clicked")) {
			Fragment frag = DetailFragment.newInstance(mCurrentEvent);
			fm.beginTransaction().add(R.id.fragment_container, frag)
					.hide(currentFragments[LIST_FRAGMENT])
					.hide(currentFragments[LIST_FILTER_BUTTONS])
					.hide(currentFragments[SAVED_LIST_FRAGMENT]).commit();
			setToggleButtonVisible(false);
			/** @author: Sebastian **/
			/** end edit by Sebastian **/
			currentFragments[DETAIL_FRAGMENT] = frag;
			mFragmentHistory[0] = DetailFragment.class;
			mFragmentHistory[1] = EventListFragment.class;
		}
		//end click on mListIcon item

		//For: click on marker
		else if (args.equals("marker_clicked")) {
			Fragment frag = DetailFragment.newInstance(mCurrentEvent);
			fm.beginTransaction().add(R.id.fragment_container, frag)
					.hide(currentFragments[MAP_FRAGMENT]).commit();
			setToggleButtonVisible(false);
			/** @author: Sebastian **/
			/** end edit by Sebastian **/
			currentFragments[DETAIL_FRAGMENT] = frag;
			mFragmentHistory[0] = DetailFragment.class;
			mFragmentHistory[1] = GMapFragment.class;
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

		mToggleImageButton = (ImageButton) view.findViewById(R.id.change_views_button);

		mToggleImageButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				stateSwitch("map_list_toggle");
			}
		});
	}

	private void setToggleButtonVisible(boolean showButton) {
		if (showButton)
			mToggleImageButton.setVisibility(View.VISIBLE);
		else
			mToggleImageButton.setVisibility(View.INVISIBLE);
	}


	public void setUpNavigationDrawer(Resources resources) {
		mDrawerListItems = resources.getStringArray(R.array.drawer_list_items);
		mDrawer = (DrawerLayout) mActivity.findViewById(R.id.drawer_layout);
		mDrawerListView = (ListView) mActivity.findViewById(
				R.id.drawer_listView);

		List<String> navItems = new ArrayList<>();
		for(int i = 0; i < mDrawerListItems.length; i++)
			navItems.add(mDrawerListItems[i]);

        mDrawerListView.setAdapter(new DrawerListAdapter(mActivity.getApplicationContext(),
                navItems));

		mDrawerListView.setOnItemClickListener(new DrawerItemClickListener());
	}

	public void openDrawer() {
		mDrawer.openDrawer(mDrawerListView);
		drawerOpen = true;
	}

	private void closeDrawer() {
		mDrawer.closeDrawer(mDrawerListView);
		drawerOpen = false;
	}

	public View getCustomInfoView(Marker marker) {

		//Fetching the custom infoView
		View customView = mActivity.getLayoutInflater().inflate(
				R.layout.custom_info_window, null);

		if (marker.getTag() != null) {
			Event activeEvent = (Event) marker.getTag();

			TextView infoTitle = (TextView) customView.findViewById(
					R.id.info_title);
			TextView infoTime = (TextView) customView.findViewById(
					R.id.info_time);
			TextView infoContactInfo = (TextView) customView.findViewById(
					R.id.info_contactInformation);

			infoTitle.setText(activeEvent.getTitle());
			infoTime.setText(activeEvent.getDateInformation());
			infoContactInfo.setText(activeEvent.getContactInformation());
		}
		return customView;
	}


    /** @author Jonathan S */
	private class DrawerItemClickListener
			implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
								long id) {

            if (mActivity instanceof MainActivity) {

                if (mDrawerListItems[position]
                        .equals((mActivity.getString(R.string.nav_men_favorites)))) {

                    if (((MainActivity) mActivity).getSavedEvents() != null
                        && ((MainActivity) mActivity).getSavedEvents().size() > 0) {

                        stateSwitch("favourites_button_pressed");
                } else {
                        Toast.makeText(mActivity.getApplicationContext(), "No saved events",
                            Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
	}

	public void setCurrentEvent(Event event) {
		mCurrentEvent = event;
	}

	public Fragment[] getCurrentFragments() {
		return currentFragments.clone();
	}
}