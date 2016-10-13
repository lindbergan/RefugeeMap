package dat255.refugeemap.model.db;

import java.util.List;

import dat255.refugeemap.model.db.sort.EventsSorter;

/**
 * An interface which provides access to all events stored in a database.
 * Events can be accessed from a {@code List<Event>}.
 * Using a {@code Filter}, a reduced collection can be accessed.
 * @author Shoulder
 */
public interface Database
{
	/**
	 * If it exists, the {@link Event} with the given ID number is returned.
	 * Otherwise, {@code null} is returned.
	 *
	 * Preconditions:
	 * - All arguments are non-null
	 */
	public Event getEvent(Integer id);

	/**
	 * Returns a list of every {@link Event} with
	 * an ID number matching a number in {@code idArr}.
	 *
	 * Preconditions:
	 * - All arguments are non-null
	 */
	public List<Event> getEvents(List<Integer> idList);

	/**
	 * Returns a collection of references to all {@link Event} instances
	 * that satisfy {@code filter}, sorted using {@code sorter}.
	 *
	 * Preconditions:
	 * - All arguments are non-null
	 */
	public List<Event> getEventsByFilter(Filter filter, EventsSorter sorter);

	// -----------------------------------
	// --- DEPRECATED, WILL BE REMOVED ---
	// -----------------------------------

	@Deprecated public List<Event> getAllEvents();
	@Deprecated public List<Event> getEventsByFilter(Filter filter);
}