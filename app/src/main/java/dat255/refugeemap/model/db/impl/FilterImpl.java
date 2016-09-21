package dat255.refugeemap.model.db.impl;

import java.util.List;

import dat255.refugeemap.model.db.Event;
import dat255.refugeemap.model.db.Filter;
import lombok.Getter;

/**
 * @author Shoulder
 */
public class FilterImpl implements Filter
{
  @Getter private final List<Integer> categories, tags;

  public FilterImpl(List<Integer> categories, List<Integer> tags)
  {
    this.categories = categories;
    this.tags = tags;
  }

  public boolean doesEventFit(Event e)
  {
    for (int c : categories)
      for (int ec : e.getCategories())
        if (c == ec)
          return true;

    for (int t : tags)
      for (int et : e.getTags())
        if (t == et)
          return true;

    return false;
  }
}