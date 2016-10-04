package dat255.refugeemap;

import org.junit.Test;

import java.text.Collator;
import java.util.Locale;

import dat255.refugeemap.model.db.Event;
import dat255.refugeemap.model.db.EventCollection;
import dat255.refugeemap.model.db.impl.EventArray;
import dat255.refugeemap.model.db.impl.EventImpl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Shoulder
 */
public class TestEventArray
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

		EventArray arr = new EventArray(new Event[]{e2, e1, e4, e3});
		EventArray sortedArr = new EventArray(new Event[]{e1, e2, e3, e4});
		arr.sort(new EventImpl.TitleSortInfo(Collator.getInstance(Locale.US)));
		assertEquals(arr, sortedArr);
	}
}