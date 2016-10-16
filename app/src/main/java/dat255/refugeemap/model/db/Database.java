package dat255.refugeemap.model.db;

import java.util.List;

import dat255.refugeemap.model.db.sort.EventsSorter;

/**
 * An interface which provides access to all events stored in a database.
 * Events can be accessed from a `List<Event>`.
 * Using a `Filter`, a portion of the list can be accessed.
 * @author Axel
 */
public interface Database
{
	/**
	 * If it exists, the `Event` with the given ID number is returned.
	 * Otherwise, `null` is returned.
	 *
	 * Precondition: All arguments are non-null.
	 */
	public Event getEvent(Integer id);

	/**
	 * Returns a list of every `Event` with an
	 * ID number matching a number in `idArr`.
	 *
	 * Precondition: All arguments are non-null.
	 */
	public List<Event> getEvents(List<Integer> idList);

	/**
	 * Returns a collection of references to all `Event` instances
	 * that satisfy `filter`, sorted using `sorter`.
	 *
	 * Precondition: All arguments are non-null.
	 */
	public List<Event> getEventsByFilter(Filter filter, EventsSorter sorter);
}