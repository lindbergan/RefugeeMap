package dat255.refugeemap;

import java.io.FileNotFoundException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import dat255.refugeemap.model.db.Database;
import dat255.refugeemap.model.db.EventCollection;
import dat255.refugeemap.model.db.impl.DatabaseImpl;
import dat255.refugeemap.model.db.impl.JSONToolsImpl;

/**
 * A singleton class which connects a single {@link Database}
 * instance to the rest of the application.
 * @author Shoulder
 */
public class AppDatabase
{
	private static final String NULL_ERROR_MESSAGE =
		"'AppDatabase.init' must be called before 'getDatabaseInstance'";

	private static Database db = null;
	private static List<Database.Listener> listeners = new ArrayList<>();

	/**
	 * Must be called before {@code getDatabaseInstance}
	 * to prevent a {@link NullPointerException}.
	 * @throws FileNotFoundException if either of the given paths are invalid.
	 *
	 * Preconditions:
	 * - All arguments are non-null.
	 * - Both arguments are paths to valid JSON database files.
	 */
	public static void init(String ctgNamesFilePath, String eventsFilePath)
		throws FileNotFoundException
	{
		if (db != null) return;
		db = new DatabaseImpl(ctgNamesFilePath, eventsFilePath,
			new JSONToolsImpl());
	}

	/**
	 * Must be called before {@code getDatabaseInstance}
	 * to prevent a {@link NullPointerException}.
	 * @throws FileNotFoundException if either of the given paths are invalid.
	 *
	 * Preconditions:
	 * - All arguments are non-null.
	 * - Both arguments contain valid JSON database code.
	 */
	public static void init(Reader ctgNamesReader, Reader eventsReader)
		throws FileNotFoundException
	{
		if (db != null) return;
		db = new DatabaseImpl(ctgNamesReader, eventsReader,
			new JSONToolsImpl());
	}

	/**
	 * Returns a reference to the created {@link Database} instance.
	 *
	 * @throws NullPointerException if {@code init} has not been called.
	 *
	 * Preconditions:
	 * - Either version of {@code init} has been called.
	 */
	public static Database getDatabaseInstance() throws NullPointerException
	{
		if (db == null) throw new NullPointerException(NULL_ERROR_MESSAGE);
		return db;
	}

	public static void addListener(Database.Listener l)
	{ listeners.add(l); }

	public static void updateVisibleEvents(EventCollection newEvents)
	{
		for (Database.Listener l : listeners)
			l.onVisibleEventsChanged(newEvents);
	}
}