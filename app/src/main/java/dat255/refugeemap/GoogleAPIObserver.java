package dat255.refugeemap;

import dat255.refugeemap.helpers.GoogleAPIHelper;

/**
 * refugee-map
 * Group 22
 * Created on 2016-10-05.
 */

public interface GoogleAPIObserver {
	void onApiConnected(GoogleAPIHelper googleAPIHelper);
}
