package dat255.refugeemap;
import android.app.Application;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import dat255.refugeemap.helpers.GoogleAPIHelper;
import dat255.refugeemap.helpers.SavedEventsHelper;
import dat255.refugeemap.helpers.UrlConnectionHelper;
import dat255.refugeemap.model.db.Event;
import lombok.Getter;
import lombok.Setter;

public class App extends Application {
	private GoogleAPIHelper mGoogleAPIHelper;
	private static App mInstance;
	private static SavedEventsHelper savedEventsHelper;
	private static final String TAG = "App";

	@Override
	public void onCreate() {
		super.onCreate();

		// Since `onCreate` will always be called once, having
		// `mInstance` be non-final is not a problem
		mInstance = this;

		mGoogleAPIHelper = new GoogleAPIHelper(getApplicationContext());
	}

	@Getter
	@Setter
	private String locale = "en";


	public static synchronized App getInstance() {
		return mInstance;
	}

	/**
	 * @return The {@link GoogleAPIHelper} for this {@link App}
   */
	public GoogleAPIHelper getGoogleApiHelperInstance() {
		return this.mGoogleAPIHelper;
	}

	/**
	 * @return The {@link GoogleAPIHelper} for the currently running instance of this {@link App}
	 */
	public static GoogleAPIHelper getGoogleApiHelper() {
		return getInstance().getGoogleApiHelperInstance();
	}

	/**
	 * Returns {@code true} if the given {@link Event} does not have a manual
	 * translation for the current locale
	 * @param e The Event in need of translation
	 * @return true, if the Event does not have a manual translation
   */
	public boolean needTranslation(Event e) {
		return !e.getAvailableLanguages().contains(App.getInstance().getLocale());
	}

	/**
	 * Returns a HashMap (containing the keys "title" and "description")
	 * with translations for the given {@link Event}'s title and description
	 * @param e the Event in need of translation
	 * @return a HashMap with the translation
   */
	public HashMap<String, String> translateEvent(Event e) {
		try {
			return UrlConnectionHelper.translateEvent(e);
		} catch (ExecutionException | InterruptedException e1) {
			e1.printStackTrace();
		}
		return null;
	}
}