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

	public GoogleAPIHelper getGoogleApiHelperInstance() {
		return this.mGoogleAPIHelper;
	}
	public static GoogleAPIHelper getGoogleApiHelper() {
		return getInstance().getGoogleApiHelperInstance();
	}

	public boolean needTranslation(Event e) {
		return !e.getAvailableLanguages().contains(App.getInstance().getLocale());
	}

	public HashMap<String, String> translateEvent(Event e) {
		try {
			return UrlConnectionHelper.translateEvent(e);
		} catch (ExecutionException | InterruptedException e1) {
			e1.printStackTrace();
		}
		return null;
	}
}