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
	private final String[] categoryNames;
	private final EventCollection events;

	private DatabaseImpl(String[] categoryNames, EventCollection events)
	{
		this.categoryNames = categoryNames;
		this.events = events;
	}

	public DatabaseImpl(String ctgNamesFilePath, String tagNamesFilePath,
		String eventsFilePath, JSONTools json) throws FileNotFoundException
	{
		this(
			(String[])(json.deserializeFile(ctgNamesFilePath, String[].class)),
			new EventArray((EventImpl[])(
				json.deserializeFile(eventsFilePath, EventImpl[].class))
			)
		);
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

	// Only to be used for testing.
	public static DatabaseImpl create(String[] ctgNames, EventCollection events)
	{ return new DatabaseImpl(ctgNames, events); }
}