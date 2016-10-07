package dat255.refugeemap.model.db;

import java.util.Collection;

/**
 * An interface which contains a selection of categories and
 * tags, used to filter events from a {@link Database}.
 * @author Shoulder
 */
public interface Filter
{
	/**
	 * Returns true iff the given {@link Event}
	 * fulfills the filter's requirements.
	 *
	 * Preconditions:
	 * - {@code e} is non-null
	 */
	public boolean doesEventFit(Event e);

	/** Returns {@code true} iff no search criteria have been specified. */
	public boolean isEmpty();

	// May be removed
	public Collection<Integer> getCategories();

	// May be removed
	public Collection<String> getSearchTerms();
}