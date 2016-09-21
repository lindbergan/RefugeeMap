package dat255.refugeemap.model.db;

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

  /** Returns an array of all categories in the filter's requirements. */
  public int[] getCategories();

  /** Returns an array of all tags in the filter's requirements. */
  public int[] getTags();
}