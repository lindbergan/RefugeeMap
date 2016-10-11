package dat255.refugeemap.helpers;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
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
import android.widget.Toast;

import com.google.android.gms.maps.model.Marker;

import java.util.Locale;

import dat255.refugeemap.EventListFragment;
import dat255.refugeemap.GMapFragment;
import dat255.refugeemap.MainActivity;
import dat255.refugeemap.R;
import dat255.refugeemap.DetailFragment;
import dat255.refugeemap.model.db.Event;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

/* A helper class that takes care of most of the different view changes
and custom view content*/

public class ViewHelper {

    private final int MAP_FRAGMENT = 0;
    private final int LIST_FRAGMENT = 1;
    private final int DETAIL_FRAGMENT = 2;
    private final int SAVED_LIST_FRAGMENT = 3;
    private String ACTIVE_FRAGMENT;
    private boolean drawerOpen = false;
    private Fragment[] currentFragments = new Fragment[4];
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
        if(args.equals("app_start")){
            Fragment mapFrag = new GMapFragment();
            Fragment listFrag = new EventListFragment();
            Fragment savedListFrag = new EventListFragment();
            initializeViews(mActivity.findViewById(R.id.main_layout));
            fm.beginTransaction().add(R.id.fragment_container, mapFrag)
                    .add(R.id.fragment_container, listFrag).add(R.id.fragment_container, savedListFrag,
                    "saved_events_list_frag").hide(listFrag).hide(savedListFrag).show(mapFrag)
                    .commit();
            currentFragments[MAP_FRAGMENT] = mapFrag;
            currentFragments[LIST_FRAGMENT] = listFrag;
            currentFragments[SAVED_LIST_FRAGMENT] = savedListFrag;
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

        //For "Favourites" button pressed

        else if(args.equals("favourites_button_pressed")){
            if(currentFragments[DETAIL_FRAGMENT] != null){
                fm.beginTransaction().remove(currentFragments[DETAIL_FRAGMENT]).commit();
            }
            if(ACTIVE_FRAGMENT.equals(EventListFragment.class.getSimpleName())){
                fm.beginTransaction().hide(currentFragments[LIST_FRAGMENT]).commit();
            }
            if(ACTIVE_FRAGMENT.equals(GMapFragment.class.getSimpleName())){
                fm.beginTransaction().hide(currentFragments[MAP_FRAGMENT]).commit();
            }
            fm.beginTransaction().show(currentFragments[SAVED_LIST_FRAGMENT]).commit();

            //Note that this line of code does not specify which of the two EventListFragments
            //is the active one. Should be fixed in future release.
            ACTIVE_FRAGMENT = EventListFragment.class.getSimpleName();
            showHideToggleButton(false);
        }
        //end "favourites" button pressed


        //For back button pressed
        else if(args.equals("back_button_pressed")) {
            fm.beginTransaction().hide(currentFragments[SAVED_LIST_FRAGMENT]).commit();

            /** @author: Sebastian
             * if we're on the map view, minimize the app (default back btn
             behaviour)
             **/
            if (ACTIVE_FRAGMENT.equals(GMapFragment.class.getSimpleName())) {

                // if the drawer is open, back button minimizes it first
                if (this.drawerOpen) {
                    this.closeDrawer();
                    return;
                }

                // minimize
                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                this.mActivity.startActivity(startMain);
                return;
            }
            /** end edit by Sebastian **/

            if(currentFragments[DETAIL_FRAGMENT] != null) {
                fm.beginTransaction().remove(currentFragments[DETAIL_FRAGMENT])
                    .hide(currentFragments[LIST_FRAGMENT])
                    .show(currentFragments[MAP_FRAGMENT]).commit();
                ACTIVE_FRAGMENT = GMapFragment.class.getSimpleName();
                toggleImage();
                showHideToggleButton(true);
            }

            if(ACTIVE_FRAGMENT.equals(
                EventListFragment.class.getSimpleName())) {
                Fragment frag = currentFragments[MAP_FRAGMENT];
                fm.beginTransaction().show(frag).hide(
                    currentFragments[LIST_FRAGMENT])
                        .hide(currentFragments[SAVED_LIST_FRAGMENT]).commit();
                ACTIVE_FRAGMENT = GMapFragment.class.getSimpleName();
                /** @author: Sebastian **/
                toggleImage();
                /** end edit by Sebastian **/
            }


        }
        //end back button

        //For: center on map button
        else if(args.equals("center_on_map")) {
            fm.beginTransaction().remove(currentFragments[DETAIL_FRAGMENT]).
                show(currentFragments[MAP_FRAGMENT]).commit();
            currentFragments[DETAIL_FRAGMENT] = null;

            /** @author: Sebastian **/
            ACTIVE_FRAGMENT = GMapFragment.class.getSimpleName();
            toggleImage();
            /** end edit by Sebastian **/

            showHideToggleButton(true);
        }
        //end center on map button

        //For: click on list item
        else if(args.equals("list_item_clicked")) {
            Fragment frag = DetailFragment.newInstance(new String[]{
                "title", "org", "description", "phone", "date", 
                Integer.toString(3)});
            fm.beginTransaction().add(R.id.fragment_container, frag)
                .hide(currentFragments[LIST_FRAGMENT]).
                    hide(currentFragments[SAVED_LIST_FRAGMENT]).commit();
            showHideToggleButton(false);
            /** @author: Sebastian **/
            ACTIVE_FRAGMENT = DetailFragment.class.getSimpleName();
            /** end edit by Sebastian **/
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
            /** @author: Sebastian **/
            ACTIVE_FRAGMENT = DetailFragment.class.getSimpleName();
            /** end edit by Sebastian **/
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
            if (mDrawerListItems[position].equals("Favourites")){
                if(mActivity instanceof MainActivity){
                if(((MainActivity)mActivity).getSavedEvents() != null
                        && ((MainActivity)mActivity).getSavedEvents().getSize() > 0) {

                    Toast.makeText(mActivity.getApplicationContext(), "you have clicked Favourites",
                            Toast.LENGTH_SHORT).show();
                    //EventCollection events = getSavedEvents();
                    ((MainActivity)mActivity).updateSavedEventsFrag();
                    stateSwitch("favourites_button_pressed");
                }
                }else{
                    Toast.makeText(mActivity.getApplicationContext(), "No saved events", Toast.LENGTH_SHORT).
                            show();
                }
            }
        }
    }

    public void openDrawer(){
        mDrawer.openDrawer(mDrawerListView);
        drawerOpen = true;
    }

    public void closeDrawer() {
        mDrawer.closeDrawer(mDrawerListView);
        drawerOpen = false;
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
            TextView infoContactInfo = (TextView) customView.findViewById(
                R.id.info_contactInformation);

            infoTitle.setText(activeEvent.getTitle());
            infoTime.setText(activeEvent.getDateInformation());
            infoContactInfo.setText(activeEvent.getContactInformation());
        }
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
