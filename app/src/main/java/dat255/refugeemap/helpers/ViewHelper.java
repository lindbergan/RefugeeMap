package dat255.refugeemap.helpers;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.model.Marker;

import java.util.Locale;

import dat255.refugeemap.EventListFragment;
import dat255.refugeemap.GMapFragment;
import dat255.refugeemap.MainActivity;
import dat255.refugeemap.R;
import dat255.refugeemap.detailView.DetailFragment;
import dat255.refugeemap.model.db.Event;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

/* A helper class that takes care of most of the different view changes
and custom view content*/

public class ViewHelper {

    private final int MAP_FRAGMENT = 0;
    private final int LIST_FRAGMENT = 1;
    private final int DETAIL_FRAGMENT = 2;
    private String ACTIVE_FRAGMENT;
    private Fragment[] currentFragments = new Fragment[3];
    FragmentManager fm;
    private Activity mActivity;
    private ImageButton mButton;
    private DrawerLayout mDrawer;
    private ListView mDrawerListView;
    private String[] mDrawerListItems;

    public ViewHelper(Activity activity) {
        mActivity = activity;
        fm = mActivity.getFragmentManager();
    }

    public void stateSwitch(String args) {
        //Starting state
        if(args.equals("app_start")) {
            Fragment mapFrag = new GMapFragment();
            Fragment listFrag = new EventListFragment();
            initializeViews(mActivity.findViewById(R.id.main_layout));
            fm.beginTransaction().add(R.id.fragment_container, mapFrag)
                .add(R.id.fragment_container, listFrag).hide(listFrag)
                .show(mapFrag).commit();
            currentFragments[MAP_FRAGMENT] = mapFrag;
            currentFragments[LIST_FRAGMENT] = listFrag;
            ACTIVE_FRAGMENT = GMapFragment.class.getSimpleName();
        }
        //end starting state

        //**** Map and list toggle button ****
        else if(args.equals("map_list_toggle")) {
            if (ACTIVE_FRAGMENT.equals(GMapFragment.class.getSimpleName())) {
                Fragment frag = currentFragments[LIST_FRAGMENT];
                fm.beginTransaction().show(frag).hide(
                    currentFragments[MAP_FRAGMENT]).commit();
                ACTIVE_FRAGMENT = EventListFragment.class.getSimpleName();
            } else if (ACTIVE_FRAGMENT.equals(
                EventListFragment.class.getSimpleName())) {
                Fragment frag = currentFragments[MAP_FRAGMENT];
                fm.beginTransaction().show(frag).hide(
                    currentFragments[LIST_FRAGMENT]).commit();
                ACTIVE_FRAGMENT = GMapFragment.class.getSimpleName();
            }
            toggleImage();
        }
        //**** end map and list toggle button *****

        //For back button pressed
        else if(args.equals("back_button_pressed")) {
            if(currentFragments[DETAIL_FRAGMENT] != null) {
                fm.beginTransaction().remove(currentFragments[DETAIL_FRAGMENT])
                    .hide(currentFragments[LIST_FRAGMENT])
                    .show(currentFragments[MAP_FRAGMENT]).commit();
                ACTIVE_FRAGMENT = GMapFragment.class.getSimpleName();
                toggleImage();
                showHideToggleButton(true);
            }
            else if(ACTIVE_FRAGMENT.equals(
                EventListFragment.class.getSimpleName())) {
                Fragment frag = currentFragments[MAP_FRAGMENT];
                fm.beginTransaction().show(frag).hide(
                    currentFragments[LIST_FRAGMENT]).commit();
                ACTIVE_FRAGMENT = GMapFragment.class.getSimpleName();
            }
        }
        //end back button

        //For: center on map button
        else if(args.equals("center_on_map")) {
            fm.beginTransaction().remove(currentFragments[DETAIL_FRAGMENT]).
                show(currentFragments[MAP_FRAGMENT]).commit();
            currentFragments[DETAIL_FRAGMENT] = null;
            showHideToggleButton(true);
        }
        //end center on map button

        //For: click on list item
        else if(args.equals("list_item_clicked")) {
            Fragment frag = DetailFragment.newInstance(new String[]{
                "title", "org", "description", "phone", "date", 
                Integer.toString(3)});
            fm.beginTransaction().add(R.id.fragment_container, frag)
                .hide(currentFragments[LIST_FRAGMENT]).commit();
            showHideToggleButton(false);
            currentFragments[DETAIL_FRAGMENT] = frag;
        }
        //end click on list item

        //For: click on marker
        else if(args.equals("marker_clicked")) {
            String tempValues[] = {
                "title", "org", "description", "phone", "date",
                Integer.toString(2)};
            Fragment frag = DetailFragment.newInstance(tempValues);
            fm.beginTransaction().add(R.id.fragment_container, frag)
                .hide(currentFragments[MAP_FRAGMENT]).commit();
            showHideToggleButton(false);
            currentFragments[DETAIL_FRAGMENT] = frag;
        }
        //end click on marker
    }

    private void toggleImage() {
        Drawable map = ResourcesCompat.getDrawable(mActivity.getResources(),
            R.drawable.ic_map_black_48dp, null);
        Drawable list = ResourcesCompat.getDrawable(mActivity.getResources(),
            R.drawable.ic_list_black_48dp, null);
        if (mButton != null && map != null && list != null) {
            if (mButton.getDrawable().getConstantState()
                .equals(map.getConstantState())) {
                mButton.setImageDrawable(list);
            } else {
                mButton.setImageDrawable(map);
            }
        }
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

    private void showHideToggleButton(boolean showButton) {
        if(showButton)
            mButton.setVisibility(VISIBLE);
        else
            mButton.setVisibility(INVISIBLE);
    }

    /**
     * setLocaleToArabic is used for testing purposes.
     * Changes reading from R -> L and changes all text to arabic
     */
    public void setLocaleToArabic() {
        Configuration newConfig = new Configuration();
        newConfig.setLocale(new Locale("ar"));
        mActivity.getBaseContext().getResources().updateConfiguration(newConfig,
            mActivity.getBaseContext().getResources().getDisplayMetrics());
    }

    public void setUpNavigationDrawer(String[] drawerListItems) {
        mDrawerListItems = drawerListItems;
        mDrawer = (DrawerLayout) mActivity.findViewById(R.id.drawer_layout);
        mDrawerListView = (ListView) mActivity.findViewById(
            R.id.drawer_listView);

        //setting the custom content in the drawer
        mDrawerListView.setAdapter(new ArrayAdapter<String>(mActivity,
            R.layout.drawer_list_item, R.id.drawer_list_item,
            mDrawerListItems));

        //hook up the listener
        mDrawerListView.setOnItemClickListener(new DrawerItemClickListener());
    }

    private class DrawerItemClickListener
        implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            if (mDrawerListItems[position].equals("Favourites")) {
                //EventCollection events = mActivity.getSavedEvents();
                if(mActivity instanceof MainActivity) {
                    ((MainActivity) mActivity).getSavedEvents();
                }
            }
        }
    }

    public void openDrawer(){
        mDrawer.openDrawer(mDrawerListView);
    }

    public void hideDirectionViews() {
        Button directionButton = (Button) mActivity.findViewById(
            R.id.directions_button);
        directionButton.setVisibility(View.GONE);

        TextView timeAndDistance = (TextView) mActivity.findViewById(
            R.id.info_time_and_distance);
        timeAndDistance.setVisibility(View.GONE);
    }

    public View getCustomInfoView(Marker marker){

        //Fetching the custom infoView
        View customView = mActivity.getLayoutInflater().inflate(
            R.layout.custom_info_window, null);

        if(marker.getTag() != null) {
            Event activeEvent = (Event) marker.getTag();

            //extracting the text fields
            TextView infoTitle = (TextView) customView.findViewById(
                R.id.info_title);
            TextView infoTime = (TextView) customView.findViewById(
                R.id.info_time);
            TextView infoCategory = (TextView) customView.findViewById(
                R.id.info_category);
            TextView infoContactInfo = (TextView) customView.findViewById(
                R.id.info_contactInformation);

            //TODO: get ALL the associated values from the event
            //setting the values corresponding to the event
            infoTitle.setText(activeEvent.getTitle());
            infoTime.setText("17.00-18-00");
            infoCategory.setText("Idrottsaktivitet");
            infoContactInfo.setText(activeEvent.getContactInformation());
        }
        //returning the custom_view with the correct values for the text fields
        return customView;
    }

    public void setDurationAndDistanceText(String duration, String distance) {
        //TODO: fix synchronization issue (now main thread is quicker than Async task in backgroud - results in wrong values of Distans & Duration)
        TextView timeAndDistance = (TextView) mActivity.findViewById(
            R.id.info_time_and_distance);
        timeAndDistance.setText("Duration: " + duration + " Distance:" +
            distance);
        timeAndDistance.setVisibility(View.VISIBLE);
    }
}
