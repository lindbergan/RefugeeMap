package dat255.refugeemap;

import org.junit.Test;

import java.text.Collator;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import dat255.refugeemap.model.db.Event;
import dat255.refugeemap.model.db.impl.EventImpl;
import dat255.refugeemap.model.db.sort.EventsSorterDistance;
import dat255.refugeemap.model.db.sort.EventsSorterTitle;

import static org.junit.Assert.assertEquals;

/** @author Axel */
public class TestEventsSorterDistance
{
	private Event createEvent(Integer id, Double lat, Double lon)
	{
		return EventImpl.create(id, null, null, null, lat, lon,
			null, null, null, null, null, null);
	}

	@Test
	public void testSort()
	{
		Event
			e1 = createEvent(0, 57.708870, 11.974561),
			e2 = createEvent(1, 57.708870, 11.974562),
			e3 = createEvent(2, 57.708870, 11.974563),
			e4 = createEvent(3, 57.708870, 11.974564),
			e5 = createEvent(4, 57.708853, 11.974483),
			e6 = createEvent(5, 57.708847, 11.974422),
			e7 = createEvent(6, 57.708966, 11.974996),
			e8 = createEvent(7, 57.708607, 11.974985);

		List<Event> list = Arrays.asList(e2, e1, e4, e3, e5, e8, e7, e6);
		List<Event> sortedList = Arrays.asList(e1, e2, e3, e4, e5, e6, e7, e8);
		new EventsSorterDistance(57.708870, 11.974560).sort(list);
		assertEquals(list, sortedList);
	}
}