package dat255.refugeemap.model.db.sort;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import dat255.refugeemap.model.db.Event;

/**
 * An implementation of `EventsSorter` that sorts based on
 * the title of each event, alphabetically (from A to z).
 * @author Axel
 */
public class EventsSorterTitle implements EventsSorter
{
	private final Comparator strComp;

	Comparator<Event> eventComp = new Comparator<Event>() {
		@Override public int compare(Event e1, Event e2)
		{ return strComp.compare(e2.getTitle(), e1.getTitle()); }
	};

	public EventsSorterTitle(Comparator strComp)
	{ this.strComp = strComp; }

	@Override public void sort(List<Event> list)
	{ Collections.sort(list, eventComp); }
}