package dat255.refugeemap.model.db.impl;

import dat255.refugeemap.model.db.Event;
import dat255.refugeemap.model.db.EventCollection;

/**
 * An implementation of {@link EventCollection} using arrays.
 * @author Shoulder
 */
public class EventArray implements EventCollection
{
	/** A custom iterator for {@link Event} arrays. */
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

	public Iterator iterator()
	{ return new Iterator(); }
}