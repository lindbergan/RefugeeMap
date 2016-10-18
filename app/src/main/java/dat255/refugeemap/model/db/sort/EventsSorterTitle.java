package dat255.refugeemap.model.db.sort;

import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import dat255.refugeemap.model.db.Event;

/**
 * An implementation of `EventsSorter` that sorts based on
 * the title of each event, alphabetically (from A to z).
 * @author Axel
 */
public class EventsSorterTitle implements EventsSorter
{
	private final String lang;
	private final Comparator strComp;

	Comparator<Event> eventComp = new Comparator<Event>() {
		@Override public int compare(Event e1, Event e2)
		{ return strComp.compare(e2.getTitle(lang), e1.getTitle(lang)); }
	};

	public EventsSorterTitle(Locale locale)
	{
		lang = locale.getLanguage();
		strComp = Collator.getInstance(locale);
	}

	@Override public void sort(List<Event> list)
	{ Collections.sort(list, eventComp); }
}