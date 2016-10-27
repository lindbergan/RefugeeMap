package dat255.refugeemap.app.base;

import android.accounts.NetworkErrorException;

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
 * A singleton class which loads and connects a single {@link Database}
 * instance to the rest of the application (the "GUI layer").
 * @author Axel
 */
public class AppDatabase
{
	public interface VisibleEventsListener
	{
		/** Called whenever {@link #updateVisibleEvents(List)} is called. */
		void onVisibleEventsChanged(List<Event> newEvents);
	}

	private static final String NULL_ERROR_MESSAGE =
		"'AppDatabase.init' must be called before 'getDatabaseInstance'";

	private static Database db = null;
	private static final List<VisibleEventsListener>
		listeners = new LinkedList<>();

	/**
	 * Initializes the {@link Database} instance. Must be called before
	 * `getDatabaseInstance` to prevent a {@link NullPointerException}.
	 *
	 * Precondition: All arguments are non-null.
	 */
	public static void init(File filesDirectory) throws IOException
	{
		if (db != null) return;

		File file = new File(filesDirectory, "events.json");

		Wrapper<byte[]> eventBytes = new Wrapper<>(null);

		try
		{
			DatabaseOnlineLoader.load(eventBytes);
			FileOutputStream os = new FileOutputStream(file);
			os.write(eventBytes.getValue());
			os.close();
		} catch(NetworkErrorException e)
		{
			if (!file.exists())
				System.exit(-1); // Internet is required on first start
		}

		db = new DatabaseImpl(new InputStreamReader(new FileInputStream(file),
			"UTF-8"), new JSONToolsImpl());
	}

	/**
	 * Returns a reference to the created {@link Database} instance.
	 *
	 * @throws NullPointerException if {@link #init(File)} has not been called.
	 */
	public static Database getDatabaseInstance() throws NullPointerException
	{
		if (db == null) throw new NullPointerException(NULL_ERROR_MESSAGE);
		return db;
	}

	/**
	 * Adds a listener to be notified whenever some other
	 * method calls {@link #updateVisibleEvents(List)}.
	 *
	 * Precondition: All arguments are non-null.
	 */
	public static void addVisibleEventsListener(VisibleEventsListener l)
	{ listeners.add(l); }

	/**
	 * Updates all listeners added using
	 * {@link #addVisibleEventsListener(VisibleEventsListener)}.
	 *
	 * Precondition: All arguments are non-null.
	 */
	public static void updateVisibleEvents(List<Event> newEvents)
	{
		for (VisibleEventsListener l : listeners)
			l.onVisibleEventsChanged(newEvents);
	}
}