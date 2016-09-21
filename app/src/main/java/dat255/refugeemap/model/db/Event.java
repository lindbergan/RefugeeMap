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
  public int[] getCategories();
  public int[] getTags();

  public double getLatitudeInDegrees();
  public double getLongitudeInDegrees();

  public String getTitle();
  public String getAddress();
  public String getContactInformation();
  public String getDescription();
}