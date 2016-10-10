package dat255.refugeemap;
import android.app.Application;

import dat255.refugeemap.helpers.GoogleAPIHelper;

public class App extends Application  {
	private GoogleAPIHelper mGoogleAPIHelper;
	private static App mInstance;
	private static final String TAG = "App";

	@Override
	public void onCreate() {
		super.onCreate();

		mInstance = this;
		mGoogleAPIHelper = new GoogleAPIHelper(getApplicationContext());
	}

	public static synchronized App getInstance() {
		return mInstance;
	}

	public GoogleAPIHelper getGoogleApiHelperInstance() {
		return this.mGoogleAPIHelper;
	}
	public static GoogleAPIHelper getGoogleApiHelper() {
		return getInstance().getGoogleApiHelperInstance();
	}
}