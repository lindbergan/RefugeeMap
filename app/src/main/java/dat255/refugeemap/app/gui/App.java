package dat255.refugeemap.app.gui;

import android.app.Application;

import dat255.refugeemap.app.base.EventOnlineTranslator;
import dat255.refugeemap.app.gui.helper.GoogleAPIHelper;
import dat255.refugeemap.model.db.Event;
import lombok.Getter;
import lombok.Setter;

public class App extends Application
{
	private GoogleAPIHelper mGoogleAPIHelper;
	private static App mInstance;

	@Override
	public void onCreate()
	{
		super.onCreate();

		// Since `onCreate` will always be called once, having
		// `mInstance` be non-final is not a problem
		mInstance = this;

		mGoogleAPIHelper = new GoogleAPIHelper(getApplicationContext());
	}

	@Getter @Setter private String localeCode = "en";

	public static synchronized App getInstance()
	{ return mInstance; }

	/**
	 * Returns the {@link GoogleAPIHelper} for the
	 * current instance of this {@link App}.
	 */
	public static GoogleAPIHelper getGoogleApiHelper()
	{ return mInstance.mGoogleAPIHelper; }

	/**
	 * Returns {@code true} if the given {@link Event} does not
	 * have a manual translation for the current localeCode.
	 */
	public boolean needTranslation(Event e)
	{
		return !e.getAvailableLanguages().contains(App.
			getInstance().getLocaleCode());
	}

	/**
	 * Returns a {@link dat255.refugeemap.model.db.Event.Translation}
	 * for the given {@link Event} for the language
	 * set by the current {@link java.util.Locale}.
	 * (If the translation fails, English is used instead.)
	 */
	public Event.Translation translateEvent(Event e)
	{ return EventOnlineTranslator.translateEvent(e); }
}