package dat255.refugeemap.model.db;

import java.util.Iterator;

/**
 * An interface for all types of {@link Event}
 * collections used by {@link Database}.
 * @author Shoulder
 */
public interface EventCollection extends Iterable<Event>
{
	/** Returns an iterator to the beginning of the collection. */
	public Iterator iterator();

	/**
	 * Returns the {@link Event} at the given index.
	 * (Note that the complexity may vary greatly with implementations.)
	 *
	 * Preconditions:
	 * - {@code index} is within the bounds of the collection.
	 */
	public Event get(int index);

	/** Returns the amount of elements in the collection. */
	public int getSize();

	/**
	 * Returns true if the given {@link Event} exists in the collection.
	 *
	 * Preconditions:
	 * - All arguments are non-null
	 */
	public boolean contains(Event e);

	/**
	 * Sorts the collection according to {@code sortInfo}.
	 *
	 * Preconditions:
	 * - All arguments are non-null
	 */
	public void sort(Event.SortInfo sortInfo);
}