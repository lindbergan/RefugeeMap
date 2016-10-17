package dat255.refugeemap.model.db.sort;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import dat255.refugeemap.model.DistanceCalculator;
import dat255.refugeemap.model.db.Event;

/**
 * An implementation of `EventsSorter` that sorts based on
 * distance to the user's point, from least to greatest.
 * @author Axel
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
		final HashMap<Integer, Double> distMap = new HashMap<>();

		for (Event e : list)
		{
			Double dist = DistanceCalculator.getGreatCircleDistance(userLat,
				userLon, e.getLatitude(), e.getLongitude());
			distMap.put(e.getID(), dist);
		}

		Collections.sort(list, new Comparator<Event>() {
			@Override public int compare(Event e1, Event e2)
			{
				double dist = distMap.get(e1.getID()) - distMap.get(e2.getID());
				return (dist == 0.f ? 0 : (dist < 0 ? -1 : 1));
			}
		});
	}
}