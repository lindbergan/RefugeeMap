package dat255.refugeemap.model.db.impl;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;

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

	private final Integer id;
	@Getter private final Integer ownerID;
	@Getter private final Integer[] categories;
	@Getter private final String[] tags;
	@Getter private final Double latitude, longitude;
	@Getter private final String dateInformation, title,
		address, contactInformation;
	private final HashMap<String, String> descriptions;

	public Integer getID()
	{ return id; }

	@Override public Collection<String> getAvailableDescriptionLanguages()
	{ return descriptions.keySet(); }

	@Override public String getDescription(String lang)
	{ return descriptions.get(lang); }

	@Override public boolean equals(Object o)
	{ return (this == o); }

	@Override public boolean equals(Event e)
	{ return (this == e); }
}