package dat255.refugeemap.model.db.impl;

import java.util.LinkedList;

import dat255.refugeemap.model.db.Database;
import dat255.refugeemap.model.db.Event;
import dat255.refugeemap.model.db.EventCollection;
import dat255.refugeemap.model.db.Filter;

/**
 * @author Shoulder
 */
public class DatabaseImpl implements Database
{
  private final EventCollection events;
  private final String[] categoryNames, tagNames;

  // Temporary constructor: will be replaced with the JSON integration
  private DatabaseImpl(EventCollection events, String[] categoryNames,
    String[] tagNames)
  {
    this.events = events;
    this.categoryNames = categoryNames;
    this.tagNames = tagNames;
  }

  @Override public EventCollection getAllEvents()
  { return events; }

  @Override public EventCollection getEventsByFilter(Filter filter)
  {
    LinkedList<Event> list = new LinkedList<>();
    for (Event e : events)
      if (filter.doesEventFit(e))
        list.add(e);
    return new EventList(list);

  }

  @Override public String getCategoryName(int id)
  { return categoryNames[id]; }

  @Override public String getTagName(int id)
  { return tagNames[id]; }
}