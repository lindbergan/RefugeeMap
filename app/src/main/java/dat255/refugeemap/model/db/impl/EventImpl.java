package dat255.refugeemap.model.db.impl;

import java.util.Comparator;

import dat255.refugeemap.model.db.Event;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Shoulder
 */
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class EventImpl implements Event
{
	@AllArgsConstructor
	public static class TitleSortInfo implements SortInfo
	{
		public final Comparator stringComparator;
		public int getInternalID() { return 0; }
	}

	@AllArgsConstructor
	public static class DistanceSortInfo implements SortInfo
	{
		public final double userLat, userLon, maxGreatCircleDistance;
		public int getInternalID() { return 1; }
	}

	@Getter private final Integer ID;
	@Getter private final Integer ownerID;
	@Getter private final Integer[] categories;
	@Getter private final String[] tags;
	@Getter private final Double latitude, longitude;
	@Getter private final String title, address,
		contactInformation, description;

	@Override public boolean equals(Object o)
	{ return (this == o); }

	@Override public boolean equals(Event e)
	{ return (this == e); }
}