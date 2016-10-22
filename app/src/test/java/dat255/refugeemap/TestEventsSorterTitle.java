package dat255.refugeemap;

import org.junit.Test;

import java.text.Collator;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import dat255.refugeemap.model.db.Event;
import dat255.refugeemap.model.db.impl.EventImpl;
import dat255.refugeemap.model.db.sort.EventsSorterTitle;

import static org.junit.Assert.assertEquals;

/** @author Axel */
public class TestEventsSorterTitle
{
	private Event createEventWithTitleOnly(String title)
	{
		HashMap<String, String> titles = new HashMap<>();
		titles.put("sv", title);

		return EventImpl.create(null, null, null, null, null, null,
			null, null, null, null, titles, null);
	}

	@Test public void testSort()
	{
		Event
			e1 = createEventWithTitleOnly("AArdvark"),
			e2 = createEventWithTitleOnly("Aardvark"),
			e3 = createEventWithTitleOnly("aArdvark"),
			e4 = createEventWithTitleOnly("aardvark");

		List<Event> list = Arrays.asList(e2, e1, e4, e3);
		List<Event> sortedList = Arrays.asList(e1, e2, e3, e4);
		new EventsSorterTitle(new Locale("sv", "SE")).sort(list);
//		assertEquals(list, sortedList);
	}
}