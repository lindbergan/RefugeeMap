package dat255.refugeemap.model.db;

/**
 * An interface which contains a selection of categories
 * and tags, used to filter events from a `Database`.
 * @author Axel
 */
public interface Filter
{
	/**
	 * Returns true iff the given `Event` fulfills the filter's requirements.
	 *
	 * Precondition: All arguments are non-null.
	 */
	public boolean doesEventFit(Event e);

	/** Returns `true` iff no search criteria have been specified. */
	public boolean isEmpty();
}