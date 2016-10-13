package dat255.refugeemap;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;

import dat255.refugeemap.model.Wrapper;
import dat255.refugeemap.model.db.Database;
import dat255.refugeemap.model.db.Event;
import dat255.refugeemap.model.db.impl.DatabaseImpl;
import dat255.refugeemap.model.db.impl.JSONToolsImpl;

/**
 * A singleton class which connects a single {@link Database}
 * instance to the rest of the application.
 * @author Shoulder
 */
public class AppDatabase
{
	public static interface Listener
	{
		/** Called whenever {@code updateVisibleEvents} is called. */
		public void onVisibleEventsChanged(List<Event> newEvents);
	}

	private static final String NULL_ERROR_MESSAGE =
		"'AppDatabase.init' must be called before 'getDatabaseInstance'";

	private static Database db = null;
	private static List<Listener> listeners = new LinkedList<>();

	public static void init(Context context) throws IOException
	{
		if (db != null) return;

		Wrapper<byte[]> eventBytes = new Wrapper<>(null);
		DatabaseOnlineLoader.load(eventBytes);

		File file = new File(context.getFilesDir(), "db.json");
		FileOutputStream os = new FileOutputStream(file);
		os.write(eventBytes.getValue());

		db = new DatabaseImpl(
			new InputStreamReader(new FileInputStream(file)),
			new JSONToolsImpl()
		);
	}

	/**
	 * Returns a reference to the created {@link Database} instance.
	 *
	 * @throws NullPointerException if {@code init} has not been called.
	 */
	public static Database getDatabaseInstance() throws NullPointerException
	{
		if (db == null) throw new NullPointerException(NULL_ERROR_MESSAGE);
		return db;
	}

	public static void addListener(Listener l)
	{ listeners.add(l); }

	public static void updateVisibleEvents(List<Event> newEvents)
	{
		for (Listener l : listeners)
			l.onVisibleEventsChanged(newEvents);
	}
}