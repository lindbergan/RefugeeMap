package dat255.refugeemap.model.db.impl;

import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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

	/* --------------------------- */
	/* ----- SORTING-RELATED ----- */
	/* --------------------------- */

	// For internal use only
	private static interface Sorter
	{ public void sort(List<Event> events, final Collator strClt); }

	private static final Map<SortCriteria, Sorter> sorters = new HashMap<>();

	static
	{
		sorters.put(SortCriteria.TitleAlphabetical, new Sorter() {
			@Override public void sort(List<Event> lst, final Collator strClt) {
				Collections.sort(lst, new Comparator<Event>() {
					@Override public int compare(Event e1, Event e2) {
						return strClt.compare(e2.getTitle(), e1.getTitle());
					}
				});
			}
		});

		sorters.put(SortCriteria.TitleAlphabeticalReverse, new Sorter() {
			@Override public void sort(List<Event> lst, final Collator strClt) {
				Collections.sort(lst, new Comparator<Event>() {
					@Override public int compare(Event e1, Event e2) {
						return -strClt.compare(e2.getTitle(), e1.getTitle());
					}
				});
			}
		});
	}

	@Override public void sort(SortCriteria criteria, Collator stringCollator)
	{ sorters.get(criteria).sort(events, stringCollator); }
}