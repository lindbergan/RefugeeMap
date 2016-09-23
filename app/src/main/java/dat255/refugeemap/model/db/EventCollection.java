package dat255.refugeemap.model.db;

import java.util.Iterator;

/**
 * An interface for all types of {@link Event}
 * collections used by {@link Database}.
 * @author Shoulder
 */
public interface EventCollection extends Iterable<Event>
{
  /** Returns an iterator to the beginning of the collection. */
  public Iterator iterator();
}