package dat255.refugeemap;

import android.accounts.NetworkErrorException;
import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import dat255.refugeemap.model.Wrapper;
import dat255.refugeemap.model.db.Database;
import dat255.refugeemap.model.db.Event;
import dat255.refugeemap.model.db.impl.DatabaseImpl;
import dat255.refugeemap.model.db.impl.JSONToolsImpl;

/**
 * A singleton class which loads and connects a single `Database`
 * instance to the rest of the application (the 'GUI layer').
 * @author Axel
 */
public class AppDatabase
{
	public static interface VisibleEventsListener
	{
		/** Called whenever `updateVisibleEvents` is called. */
		public void onVisibleEventsChanged(List<Event> newEvents);
	}

	private static final String NULL_ERROR_MESSAGE =
		"'AppDatabase.init' must be called before 'getDatabaseInstance'";

	private static Database db = null;
	private static List<VisibleEventsListener> listeners = new LinkedList<>();

	/**
	 * Initializes the `Database` instance. Must be called before
	 * `getDatabaseInstance` to prevent a `NullPointerException`.
	 *
	 * Precondition: All arguments are non-null.
	 */
	public static void init(Context context) throws IOException
	{
		if (db != null) return;

		Wrapper<byte[]> eventBytes = new Wrapper<>(null);
		try {
			DatabaseOnlineLoader.load(eventBytes);
		} catch(NetworkErrorException e) {
			System.exit(-1); // temp
		}

		File file = new File(context.getFilesDir(), "events.json");
		FileOutputStream os = new FileOutputStream(file);
		os.write(eventBytes.getValue());
		os.close();

		db = new DatabaseImpl(new InputStreamReader(new FileInputStream(file),
			"UTF-8"), new JSONToolsImpl());
	}

	/**
	 * Returns a reference to the created `Database` instance.
	 *
	 * @throws NullPointerException if `init` has not been called.
	 */
	public static Database getDatabaseInstance() throws NullPointerException
	{
		if (db == null) throw new NullPointerException(NULL_ERROR_MESSAGE);
		return db;
	}

	/**
	 * Adds a listener to be notified whenever `updateVisibleEvents` is called.
	 *
	 * Precondition: All arguments are non-null.
	 */
	public static void addVisibleEventsListener(VisibleEventsListener l)
	{ listeners.add(l); }

	/**
	 * Updates all listeners added using `addVisibleEventsListener`.
	 *
	 * Precondition: All arguments are non-null.
	 */
	public static void updateVisibleEvents(List<Event> newEvents)
	{
		for (VisibleEventsListener l : listeners)
			l.onVisibleEventsChanged(newEvents);
	}
}