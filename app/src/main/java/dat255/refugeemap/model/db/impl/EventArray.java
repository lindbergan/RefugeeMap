package dat255.refugeemap.model.db.impl;

import java.text.Collator;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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

	@Override public Iterator iterator()
	{ return new Iterator(); }

	@Override public Event get(int index)
	{ return events[index]; }

	@Override public int getSize()
	{ return events.length; }

	@Override public boolean contains(Event e)
	{
		for (int i = 0; i < events.length; i++)
			if (events[i] == e) // yes, this is meant to compare references
				return true;
		return false;
	}

	public boolean equals(EventArray arr)
	{ return Arrays.equals(events, arr.events); }

	@Override public boolean equals(Object obj)
	{
		if (!(obj instanceof EventArray)) return false;
		return equals((EventArray) obj);
	}

	/* --------------------------- */
	/* ----- SORTING-RELATED ----- */
	/* --------------------------- */

	// For internal use only
	private static interface Sorter
	{ public void sort(Event[] events, final Collator strClt); }

	private static final Map<SortCriteria, Sorter> sorters = new HashMap<>();

	static
	{
		sorters.put(SortCriteria.TitleAlphabetical, new Sorter() {
			@Override public void sort(Event[] arr, final Collator strClt) {
				for (int i = 0; i < arr.length; i++)
					for (int j = i + 1; j < arr.length; j++)
					{
						boolean shouldSwap = (strClt.compare(arr[i].getTitle(),
							arr[j].getTitle()) < 0);
						if (shouldSwap)
						{
							// because Java is awful (pass-by-value only)
							Event tempEvent = arr[i];
							arr[i] = arr[j];
							arr[j] = tempEvent;
						}
					}
			}
		});

		sorters.put(SortCriteria.TitleAlphabeticalReverse, new Sorter() {
			@Override public void sort(Event[] arr, final Collator strClt) {
				for (int i = 0; i < arr.length; i++)
					for (int j = i + 1; j < arr.length; j++)
					{
						boolean shouldSwap = (strClt.compare(arr[i].getTitle(),
							arr[j].getTitle()) > 0);
						if (shouldSwap)
						{
							// because Java is awful (pass-by-value only)
							Event tempEvent = arr[i];
							arr[i] = arr[j];
							arr[j] = tempEvent;
						}
					}
			}
		});
	}

	@Override public void sort(SortCriteria criteria, Collator stringCollator)
	{ sorters.get(criteria).sort(events, stringCollator); }
}