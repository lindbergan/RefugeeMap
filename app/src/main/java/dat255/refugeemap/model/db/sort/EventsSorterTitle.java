package dat255.refugeemap.model.db.sort;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import dat255.refugeemap.model.db.Event;

/**
 * @author Shoulder
 */
public class EventsSorterTitle implements EventsSorter
{
	private final Comparator strComp;

	Comparator<Event> eventComp = new Comparator<Event>() {
		@Override public int compare(Event e1, Event e2)
		{ return strComp.compare(e1, e2); }
	};

	public EventsSorterTitle(Comparator strComp)
	{ this.strComp = strComp; }

	@Override public void sort(List<Event> list)
	{ Collections.sort(list, eventComp); }
}