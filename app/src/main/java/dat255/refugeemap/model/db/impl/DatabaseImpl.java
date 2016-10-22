package dat255.refugeemap.model.db.impl;

import java.io.FileNotFoundException;
import java.io.Reader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import dat255.refugeemap.model.db.Database;
import dat255.refugeemap.model.db.Event;
import dat255.refugeemap.model.db.Filter;
import dat255.refugeemap.model.db.JSONTools;
import dat255.refugeemap.model.db.sort.EventsSorter;

/**
 * @author Axel
 */
public class DatabaseImpl implements Database
{
	private final HashMap<Locale, String[]> categoryNames = new HashMap<>();

	// TODO Maybe change to a HashMap<Integer, Event> for quick access by ID
	private final List<Event> events;

	private EventsSorter prevSorter = null;

	public DatabaseImpl(Reader eventsReader, JSONTools json)
		throws FileNotFoundException
	{
		events = Arrays.asList((Event[])json.deserializeReader(eventsReader,
			EventImpl[].class));
	}

	@Override public Event getEvent(Integer id)
	{
		for (Event e : events)
			if (e.getID().equals(id))
				return e;
		return null;
	}

	@Override public List<Event> getEvents(List<Integer> idList)
	{
		LinkedList<Event> eventList = new LinkedList<>();
		for (Event e : events)
			for (Iterator<Integer> it = idList.iterator(); it.hasNext();)
				if (e.getID().equals(it.next()))
				{
					it.remove();
					eventList.add(e);
				}
		return eventList;
	}

	@Override
	public List<Event> getEventsByFilter(Filter filter, EventsSorter sorter)
	{
		if (filter.isEmpty())
		{
			if (prevSorter != sorter)
			{
				sorter.sort(events);
				prevSorter = sorter;
			}

			return events;
		}

		LinkedList<Event> list = new LinkedList<>();
		for (Event e : events)
			if (filter.doesEventFit(e))
				list.add(e);
		sorter.sort(list);
		return list;
	}

	// Only exists for testing purposes ('create')
	private DatabaseImpl(List<Event> events) { this.events = events; }

	// Only to be used for testing.
	public static DatabaseImpl create(List<Event> events)
	{ return new DatabaseImpl(events); }
}