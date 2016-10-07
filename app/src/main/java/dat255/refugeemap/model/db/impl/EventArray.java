package dat255.refugeemap.model.db.impl;

import java.util.Arrays;
import java.util.Comparator;

import dat255.refugeemap.model.ArrayUtils;
import dat255.refugeemap.model.db.Event;
import dat255.refugeemap.model.db.EventCollection;

/**
 * An implementation of {@link EventCollection} using arrays.
 * @author Shoulder
 */
public class EventArray implements EventCollection
{
	/** A custom (immutable) iterator for {@link Event} arrays. */
	public class Iterator implements java.util.Iterator<Event>
	{
		private int index = 0;

		public boolean hasNext()
		{ return (index < events.length); }

		public Event next()
		{ return events[index++]; }
	}

	private final Event[] events;

	public EventArray(Event[] events)
	{ this.events = events; }

	@Override public Iterator iterator()
	{ return new Iterator(); }

	@Override public Event get(int index)
	{ return events[index]; }

	@Override public int getSize()
	{ return events.length; }

	@Override public boolean contains(Event e)
	{ return ArrayUtils.contains(events, e); }

	public boolean equals(EventArray arr)
	{ return Arrays.equals(events, arr.events); }

	@Override public boolean equals(Object obj)
	{
		if (!(obj instanceof EventArray)) return false;
		return equals((EventArray) obj);
	}

	public void sort(Event.SortInfo sortInfo)
	{ EventSortingUtils.sortArray(events, sortInfo); }
}