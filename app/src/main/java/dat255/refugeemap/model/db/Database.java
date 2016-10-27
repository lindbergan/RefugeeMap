package dat255.refugeemap.model.db;

import java.util.List;

import dat255.refugeemap.model.db.sort.EventsSorter;

/**
 * An interface which provides access to all events stored in a database.
 * Events can be accessed from a {@link List} of {@link Event}s.
 * Using a {@link Filter}, a portion of the list can be accessed.
 * @author Axel
 */
public interface Database
{
	/**
	 * If it exists, the {@link Event} with the given ID number is returned.
	 * Otherwise, {@code null} is returned.
	 *
	 * Precondition: All arguments are non-null.
	 */
	Event getEvent(Integer id);

	/**
	 * Returns a list of every {@link Event} with an ID
	 * number matching a number in {@code idList}.
	 *
	 * Precondition: All arguments are non-null.
	 */
	List<Event> getEvents(List<Integer> idList);

	/**
	 * Returns a collection of references to all {@link Event} instances
	 * that satisfy {@code filter}, sorted using {@code sorter}.
	 *
	 * Precondition: All arguments are non-null.
	 */
	List<Event> getEventsByFilter(Filter filter, EventsSorter sorter);
}