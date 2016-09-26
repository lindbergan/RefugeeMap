package dat255.refugeemap.model.db.impl;

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

	public Iterator iterator()
	{ return events.iterator(); }
}