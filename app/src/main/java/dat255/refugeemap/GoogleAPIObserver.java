package dat255.refugeemap;

import dat255.refugeemap.helpers.GoogleAPIHelper;

/**
 * refugee-map
 * Group 22
 * Created on 2016-10-05.
 * Observer Interface used for getting updates from the Google API through a {@link GoogleAPIHelper}
 */

public interface GoogleAPIObserver {
	void onApiConnected(GoogleAPIHelper googleAPIHelper);
}
