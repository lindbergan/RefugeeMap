package dat255.refugeemap.model.db.impl;

import java.text.Collator;
import java.util.Iterator;
import java.util.List;

import dat255.refugeemap.model.db.Event;
import dat255.refugeemap.model.db.EventCollection;

/**
 * An implementation of {@link EventCollection} using lists.
 * @author Shoulder
 */
public class EventList implements EventCollection
{
	private List<Event> events;

	public EventList(List<Event> events)
	{ this.events = events; }

	@Override public Iterator iterator()
	{ return events.iterator(); }

	@Override public Event get(int index)
	{ return events.get(index); }

	@Override public int getSize()
	{ return events.size(); }

	@Override public boolean contains(Event e)
	{
		for (Event event : events)
			if (event == e) // yes, this is meant to compare references
				return true;
		return false;
	}

	public boolean equals(EventList lst)
	{ return events.equals(lst.events); }

	@Override public boolean equals(Object obj)
	{
		if (!(obj instanceof EventList)) return false;
		return equals((EventList)obj);
	}

	@Override public void sort(SortCriteria sc, Collator strCol)
	{ EventSortingUtils.sortList(events, sc, strCol); }
}