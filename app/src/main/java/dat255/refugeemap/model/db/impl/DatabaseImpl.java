package dat255.refugeemap.model.db.impl;

import java.io.FileNotFoundException;
import java.util.LinkedList;

import dat255.refugeemap.model.db.Database;
import dat255.refugeemap.model.db.Event;
import dat255.refugeemap.model.db.EventCollection;
import dat255.refugeemap.model.db.Filter;
import dat255.refugeemap.model.db.JSONTools;

/**
 * @author Shoulder
 */
public class DatabaseImpl implements Database
{
	private final EventCollection events;
	private final String[] categoryNames, tagNames;

	public DatabaseImpl(String categoryNamesFilePath, String tagNamesFilePath,
						String eventsFilePath, JSONTools json) throws FileNotFoundException
	{
		this.categoryNames = (String[])(json.
			deserialize(categoryNamesFilePath, String[].class));
		this.tagNames = (String[])(json.
			deserialize(tagNamesFilePath, String[].class));
		this.events = new EventArray((EventImpl[])(json.
			deserialize(eventsFilePath, EventImpl[].class)));
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