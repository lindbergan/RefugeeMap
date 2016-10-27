package dat255.refugeemap.model.db.sort;

import java.util.List;

import dat255.refugeemap.model.db.Event;

/**
 * An interface containing a method which
 * sorts a {@link List} of {@link Event}s.
 * @author Axel
 */
public interface EventsSorter
{
	/**
	 * Sorts the given list.
	 *
	 * Precondition: All arguments are non-null.
	 */
	void sort(List<Event> list);

	EventsSorter NULL_SORTER = new EventsSorter() {
		@Override public void sort(List<Event> list) {}
	};
}