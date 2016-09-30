package dat255.refugeemap.model.db;

/**
 * An interface which provides access to all events stored in a database.
 * Events can be accessed from an {@link EventCollection}.
 * Using a {@code Filter}, a reduced collection can be accessed.
 * @author Shoulder
 */
public interface Database
{
	public static interface Listener
	{
		/**
		 * Called when the collection of 'visible events' changes
		 * (i.e. when either {@code Event.getAllEvents} or
		 * {@code Event.getEventsByFilter} is called).
		 */
		public void onVisibleEventsChanged(EventCollection newEvents);
	}

	/**
	 * Returns a collection of references to all the
	 * {@link Event} instances in the database.
	 */
	public EventCollection getAllEvents();

	/**
	 * Returns a collection of references to all the {@link Event}
	 * instances that satisfy the given filter in the database.
	 *
	 * Preconditions:
	 * - {@code filter} is non-null
	 */
	public EventCollection getEventsByFilter(Filter filter);

	/**
	 * Returns the name of the category with the given ID.
	 *
	 * Preconditions:
	 * - {@code id} is a valid category ID
	 */
	public String getCategoryName(int id);
}