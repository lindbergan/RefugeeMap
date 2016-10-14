package dat255.refugeemap.helpers;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import dat255.refugeemap.AppDatabase;
import dat255.refugeemap.EventListFragment;
import dat255.refugeemap.R;
import dat255.refugeemap.model.db.Event;

/**
 * Helper class to handle the local storage of Events saved by the user.
 * Also initiates graphic updates of relevant Fragments when saving/removing an Event.
 */

public class SavedEventsHelper {

    private Activity mActivity;

    public SavedEventsHelper(Activity activity){
        mActivity = activity;
    }

    public boolean onSaveEventButtonPressed(String id) {
        SharedPreferences prefs = mActivity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        //creates new TreeSet if no Set already linked to key
        Set<String> savedEvents = prefs.getStringSet(mActivity
                .getString(R.string.saved_events_key), new TreeSet<String>());
        Set<String> updatedEventList = new TreeSet<>(savedEvents);

        if (!savedEvents.contains(id)) {

            updatedEventList.add(id);
            editor.putStringSet(mActivity.getString(R.string.saved_events_key), updatedEventList);

        }else{

            updatedEventList.remove(id);
            editor.putStringSet(mActivity.getString(R.string.saved_events_key), updatedEventList);

        }
        return editor.commit(); //returns true if save/remove was successful
    }


    public List<Event> getSavedEvents() {
        Set<String> savedEventsStr = mActivity.getPreferences(Context.MODE_PRIVATE)
                .getStringSet(mActivity.getString(R.string.saved_events_key), null);

        if(savedEventsStr != null){

            List<Integer> savedEventsIds = new LinkedList<>();

            for(String s : savedEventsStr){
                savedEventsIds.add(Integer.valueOf(s));
            }
            return AppDatabase.getDatabaseInstance().getEvents(savedEventsIds);
        }else{
            return null;
        }
    }


    public boolean isEventSaved(String id) {
        try {

            SharedPreferences prefs = mActivity.getPreferences(Context.MODE_PRIVATE);
            return prefs.getStringSet(mActivity.getString(R.string.saved_events_key), null).contains(id);

        } catch (NullPointerException e) {
            return false;
        }
    }

    public void updateSavedEventsFrag(){

        Fragment frag = mActivity.getFragmentManager().findFragmentByTag("saved_events_list_frag");

        if(frag instanceof EventListFragment){
            ((EventListFragment) frag).onVisibleEventsChanged(getSavedEvents());
        }

    }
}


