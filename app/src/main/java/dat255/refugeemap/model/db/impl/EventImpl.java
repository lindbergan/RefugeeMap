package dat255.refugeemap.model.db.impl;

import java.util.Collection;
import java.util.HashMap;

import dat255.refugeemap.model.db.Event;
import lombok.Getter;

/**
 * @author Axel
 */
public class EventImpl implements Event
{
	private Integer id;
	@Getter private Integer ownerID;
	private Integer[] categories;
	private String[] tags;
	@Getter private Double latitude, longitude;
	@Getter private String dateInformation, address, contactInformation;
	private HashMap<String, String> titles, descriptions;
	private int[][] timeData;

	// Gson requires zero-argument constructor
	private EventImpl() {}

	public Integer getID()
	{ return id; }

	public Integer[] getCategories()
	{ return categories.clone(); }

	public String[] getTags()
	{ return tags.clone(); }

	@Override public String getTitle(String lang)
	{ return titles.get(lang); }

	@Override public String getDescription(String lang)
	{ return descriptions.get(lang); }

	@Override public Collection<String> getAvailableLanguages()
	{ return descriptions.keySet(); } // same as titles.keySet()

	public int[][] getTimeData()
	{ return timeData.clone(); }

	@Override public boolean equals(Object o)
	{ return (this == o); }

	@Override public boolean equals(Event e)
	{ return (this == e); }

	public static EventImpl create(Integer id, Integer ownerID,
		Integer[] categories, String[] tags, Double latitude,
		Double longitude, String dateInformation, String address,
		String contactInformation, HashMap<String, String> descriptions,
		HashMap<String, String> titles, int[][] timeData)
	{
		EventImpl e = new EventImpl();
		e.id = id;
		e.ownerID = ownerID;
		e.categories = categories;
		e.tags = tags;
		e.latitude = latitude;
		e.longitude = longitude;
		e.dateInformation = dateInformation;
		e.titles = titles;
		e.address = address;
		e.contactInformation = contactInformation;
		e.descriptions = descriptions;
		e.timeData = timeData;
		return e;
	}
}