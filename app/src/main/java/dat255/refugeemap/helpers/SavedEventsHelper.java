package dat255.refugeemap.helpers;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import dat255.refugeemap.AppDatabase;
import dat255.refugeemap.EventListFragment;
import dat255.refugeemap.ListFilterButtonsFragment;
import dat255.refugeemap.R;
import dat255.refugeemap.model.db.Event;

/**
 * Helper class to handle the local storage of {@link Event} saved by the user.
 * Also initiates graphic updates of relevant {@link Fragment} when saving/removing an {@link Event}.
 */

public class SavedEventsHelper {

    private Activity mActivity;
    private List<SavedEventListener> savedEventListeners = new ArrayList<>();
    private List<Event> savedEvents = new ArrayList<>();

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

        //returns true if save/remove was successful
        boolean commited = editor.commit();
        if (commited) {
            updateSavedEventListeners();
        }
        return commited;
        // successful
    }

  /**
   * @return The {@link Event} objects stored in the database
   */
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

  /**
   * Checks if the specified {@link Event} is saved
   * @param id the identifier for the {@link Event}
   * @return true, if the {@link Event} was found. False otherwise.
   */
    public boolean isEventSaved(String id) {
        try {

            SharedPreferences prefs = mActivity.getPreferences(Context.MODE_PRIVATE);
            return prefs.getStringSet(mActivity.getString(R.string.saved_events_key), null).contains(id);

        } catch (NullPointerException e) {
            return false;
        }
    }

  /**
   * Updates the {@link Fragment} containing the saved {@link Event}s.
   */
  public void updateSavedEventsFrag(){

        Fragment frag = mActivity.getFragmentManager().findFragmentByTag("saved_events_list_frag");

        if(frag instanceof EventListFragment){
            ((EventListFragment) frag).onVisibleEventsChanged(getSavedEvents());
        }

        updateSavedEventListeners();
    }

    public static interface SavedEventListener {
        public void onSavedEvent(List<Event> savedEvents);
    }

  /**
   * Add listener to list of {@link SavedEventListener}s
   * @param listener the listener to be added
   */
  public void addSavedEventListener(SavedEventListener listener) {
        savedEventListeners.add(listener);
    }

  /**
   * Causes all the saved {@link SavedEventListener}s to update themselves
   */
  public void updateSavedEventListeners() {
        for (SavedEventListener l : savedEventListeners)
            l.onSavedEvent(getSavedEvents());
    }

    public void updateSavedEventListeners(List<Event> savedEvents) {
        for (SavedEventListener l : savedEventListeners)
            l.onSavedEvent(savedEvents);
    }

}


