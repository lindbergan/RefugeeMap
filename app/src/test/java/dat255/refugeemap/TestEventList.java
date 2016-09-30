package dat255.refugeemap;

import org.junit.Test;

import java.text.Collator;
import java.util.Arrays;
import java.util.Locale;

import dat255.refugeemap.model.db.Event;
import dat255.refugeemap.model.db.EventCollection;
import dat255.refugeemap.model.db.impl.EventImpl;
import dat255.refugeemap.model.db.impl.EventList;

import static org.junit.Assert.assertEquals;

/**
 * @author Shoulder
 */
public class TestEventList
{
	private EventImpl createEventWithTitleOnly(String title)
	{
		return new EventImpl(0, 0, null, null, 0.0, 0.0,
			title, null, null, null);
	}

	@Test public void testSortByTitle()
	{
		Event
			e1 = createEventWithTitleOnly("AArdvark"),
			e2 = createEventWithTitleOnly("Aardvark"),
			e3 = createEventWithTitleOnly("aArdvark"),
			e4 = createEventWithTitleOnly("aardvark");

		EventList list = new EventList(Arrays.asList(e2, e1, e4, e3));
		EventList sortedList = new EventList(Arrays.asList(e1, e2, e3, e4));
		list.sort(EventCollection.SortCriteria.TitleAlphabetical,
			Collator.getInstance(Locale.US));
		assertEquals(list, sortedList);

		list = new EventList(Arrays.asList(e2, e1, e4, e3));
		sortedList = new EventList(Arrays.asList(e4, e3, e2, e1));
		list.sort(EventCollection.SortCriteria.TitleAlphabeticalReverse,
			Collator.getInstance(Locale.US));
		assertEquals(list, sortedList);
	}
}