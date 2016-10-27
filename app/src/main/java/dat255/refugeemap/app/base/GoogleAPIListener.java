package dat255.refugeemap.app.base;

import dat255.refugeemap.app.gui.helper.GoogleAPIHelper;

/**
 * Observer interface used for getting updates from the
 * Google API through a {@link GoogleAPIHelper}.
 */
public interface GoogleAPIListener
{
	void onAPIConnected(GoogleAPIHelper googleAPIHelper);
}