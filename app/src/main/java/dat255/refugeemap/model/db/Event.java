package dat255.refugeemap.model.db;

import java.util.Collection;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * An interface with accessor methods for all information tied to an event.
 * @author Axel
 */
public interface Event
{
	@AllArgsConstructor(access = AccessLevel.PUBLIC)
	public class Translation
	{
		@Getter private final String title, description;
	}

	Integer getID();
	Integer getOwnerID();

	Integer[] getCategories();
	String[] getTags();

	Double getLatitude();
	Double getLongitude();

	String getDateInformation();
	String getAddress();
	String getContactInformation();

	int[][] getTimeData();

	/**
	 * Returns the event's title in the given language.
	 *
	 * Preconditions:
	 * - All arguments are non-null
	 * - {@code lang} is in {@link #getAvailableLanguages()}
	 */
	String getTitle(String lang);

	/**
	 * Returns the event's description in the given language.
	 *
	 * Preconditions:
	 * - All arguments are non-null
	 * - {@code lang} is in {@link #getAvailableLanguages()}
	 */
	String getDescription(String lang);

	Collection<String> getAvailableLanguages();

	@Override boolean equals(Object o);
	boolean equals(Event e);
}