package dat255.refugeemap.model.db;

import java.util.List;

/**
 * An interface which provides access to all events stored in a database.
 * Events can be accessed from an {@link EventCollection}.
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
	 * Returns a collection of every {@link Event} with
	 * an ID number matching a number in {@code idArr}.
	 *
	 * Preconditions:
	 * - All arguments are non-null
	 */
	public EventCollection getEvents(List<Integer> idList);

	/**
	 * Returns a collection of references to all {@link Event} instances
	 * that satisfy {@code filter}, sorted according to {@code si}.
	 *
	 * Preconditions:
	 * - All arguments are non-null
	 */
	public EventCollection getEventsByFilter(Filter filter, Event.SortInfo si);

	// -----------------------------------
	// --- DEPRECATED, WILL BE REMOVED ---
	// -----------------------------------

	@Deprecated public EventCollection getAllEvents();
	@Deprecated public EventCollection getEventsByFilter(Filter filter);
}