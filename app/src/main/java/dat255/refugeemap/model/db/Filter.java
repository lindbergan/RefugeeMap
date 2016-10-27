package dat255.refugeemap.model.db;

/**
 * An interface which contains a selection of categories and
 * tags, used to filter events from a {@link Database}.
 * @author Axel
 */
public interface Filter
{
	/**
	 * Returns {@code true} iff the given {@link Event}
	 * fulfills the filter's requirements.
	 *
	 * Precondition: All arguments are non-null.
	 */
	boolean doesEventFit(Event e);

	/** Returns {@code true} iff no search criteria have been specified. */
	boolean isEmpty();
}