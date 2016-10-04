package dat255.refugeemap.model.db.impl;

import java.io.FileNotFoundException;
import java.io.Reader;
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

	private Event.SortInfo prevSortInfo = new Event.SortInfo() {
		public int getInternalID() { return -1; }
	};

	public DatabaseImpl(String ctgNamesFilePath, String eventsFilePath,
		JSONTools json) throws FileNotFoundException
	{
		categoryNames = (String[])(json.
			deserializeFile(ctgNamesFilePath, String[].class));
		events = new EventArray((EventImpl[])(json.
			deserializeFile(eventsFilePath, EventImpl[].class)));
	}

	// may be removed
	public DatabaseImpl(Reader ctgNamesReader, Reader eventsReader,
		JSONTools json) throws FileNotFoundException
	{
		categoryNames = (String[])(json.
			deserializeFile(ctgNamesReader, String[].class));
		events = new EventArray((EventImpl[])(json.
			deserializeFile(eventsReader, EventImpl[].class)));
	}

	@Override public String getCategoryName(int id)
	{ return categoryNames[id]; }

	@Override
	public EventCollection getEventsByFilter(Filter filter, Event.SortInfo info)
	{
		if (filter.isEmpty())
		{
			if (prevSortInfo.getInternalID() != info.getInternalID())
			{
				events.sort(info);
				prevSortInfo = info;
			}

			return events;
		}

		LinkedList<Event> list = new LinkedList<>();
		for (Event e : events)
			if (filter.doesEventFit(e))
				list.add(e);
		EventCollection ec = new EventList(list);
		ec.sort(info);
		return ec;
	}

	// Only exists for testing purposes ('create')
	private DatabaseImpl(String[] categoryNames, EventCollection events)
	{
		this.categoryNames = categoryNames;
		this.events = events;
	}

	// Only to be used for testing.
	public static DatabaseImpl create(String[] ctgNames, EventCollection events)
	{ return new DatabaseImpl(ctgNames, events); }

	// Will be removed
	@Deprecated@Override public EventCollection getAllEvents()
	{ return events; }

	// Will be removed
	@Deprecated@Override public EventCollection getEventsByFilter(Filter filter)
	{
		LinkedList<Event> list = new LinkedList<>();
		for (Event e : events)
			if (filter.doesEventFit(e))
				list.add(e);
		return new EventList(list);
	}
}