package dat255.refugeemap.model.db;

/**
 * An interface which provides access to all events stored in a database.
 * Events can be accessed from an {@link EventCollection}.
 * Using a {@code Filter}, a reduced collection can be accessed.
 * @author Shoulder
 */
public interface Database
{
	/**
	 * Returns the name of the category with the given ID.
	 *
	 * Preconditions:
	 * - {@code id} is a valid category ID
	 */
	public String getCategoryName(int id);

	/**
	 * Returns a collection of references to all {@link Event} instances
	 * that satisfy {@code filter}, sorted according to {@code si}.
	 *
	 * Preconditions:
	 * - All arguments are non-null
	 */
	public EventCollection getEventsByFilter(Filter filter, Event.SortInfo si);

	// -----------------------------------
	// --- DEPRECATED, WILL BE REMOVED ---
	// -----------------------------------

	@Deprecated public EventCollection getAllEvents();
	@Deprecated public EventCollection getEventsByFilter(Filter filter);
}