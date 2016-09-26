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

	/** Returns a collection of all categories in the filter's requirements. */
	public Collection<Integer> getCategories();

	/**
	 * Returns a collection of all search terms (title
	 * words and tags) in the filter's requirements.
	 */
	public Collection<String> getSearchTerms();
}