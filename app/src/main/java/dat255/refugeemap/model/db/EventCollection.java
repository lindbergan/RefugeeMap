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

	/**
	 * Returns true if the given {@link Event} exists in the collection.
	 *
	 * Preconditions:
	 * - {@code e} is non-null
	 */
	public boolean contains(Event e);
}