package dat255.refugeemap.model.db;

/**
 * An interface with accessor methods for all information tied to an event.
 * This includes the latitude and longitude, for use when displaying on the
 * map, as well as title, address, contact information and description, for
 * use in the detail view and the 'popup' view (which contains a few details).
 * @author Shoulder
 */
public interface Event
{
	public Integer getID();
	public Integer getOwnerID();

	public Integer[] getCategories();
	public Integer[] getTags();

	public Double getLatitude();
	public Double getLongitude();

	public String getTitle();
	public String getAddress();
	public String getContactInformation();
	public String getDescription();
}