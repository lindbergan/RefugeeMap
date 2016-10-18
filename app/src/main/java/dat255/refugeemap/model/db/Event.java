package dat255.refugeemap.model.db;

import java.util.Collection;

/**
 * An interface with accessor methods for all information tied to an event.
 * This includes the latitude and longitude, for use when displaying on the
 * map, as well as title, address, contact information and description, for
 * use in the detail view and the 'popup' view (which contains a few details).
 * @author Axel
 */
public interface Event
{
	public Integer getID();
	public Integer getOwnerID();

	public Integer[] getCategories();
	public String[] getTags();

	public Double getLatitude();
	public Double getLongitude();

	public String getDateInformation();
	public String getAddress();
	public String getContactInformation();

	public int[][] getTimeData();

	/**
	 * Returns the event's title in the given language.
	 *
	 * Preconditions:
	 * - All arguments are non-null
	 * - `lang` is in `getAvailableLanguages`
	 */
	public String getTitle(String lang);

	/**
	 * Returns the event's description in the given language.
	 *
	 * Preconditions:
	 * - All arguments are non-null
	 * - `lang` is in `getAvailableLanguages`
	 */
	public String getDescription(String lang);

	public Collection<String> getAvailableLanguages();

	@Override public boolean equals(Object o);
	public boolean equals(Event e);
}