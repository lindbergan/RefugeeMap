package dat255.refugeemap.model.db.sort;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import dat255.refugeemap.model.DistanceCalculator;
import dat255.refugeemap.model.db.Event;

/**
 * @author Shoulder
 */
public class EventsSorterDistance implements EventsSorter
{
	private double userLat, userLon;

	public EventsSorterDistance(double userLatitude, double userLongitude)
	{
		userLat = userLatitude;
		userLon = userLongitude;
	}

	@Override public void sort(List<Event> list)
	{
		final HashMap<Integer, Double> distances = new HashMap<>();

		for (Event e : list)
		{
			Double distance = DistanceCalculator.getGreatCircleDistance(userLat,
				userLon, e.getLatitude(), e.getLongitude());
			distances.put(e.getID(), distance);
		}

		Collections.sort(list, new Comparator<Event>() {
			@Override public int compare(Event e1, Event e2)
			{ return (int)Math.round(distances.get(e1) - distances.get(e2)); }
		});
	}
}