package dat255.refugeemap.model.db.sort;

import java.util.List;

import dat255.refugeemap.model.db.Event;

/**
 * An interface containing a method with sorts a `List<Event>`.
 * @author Axel
 */
public interface EventsSorter
{
	/**
	 * Sorts the given list.
	 *
	 * Precondition: All arguments are non-null.
	 */
	public void sort(List<Event> list);
}