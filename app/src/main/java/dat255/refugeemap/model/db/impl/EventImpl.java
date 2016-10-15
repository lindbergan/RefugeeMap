package dat255.refugeemap.model.db.impl;

import java.util.Collection;
import java.util.HashMap;

import dat255.refugeemap.model.db.Event;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Axel
 */
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class EventImpl implements Event
{
	private final Integer id;
	@Getter private final Integer ownerID;
	@Getter private final Integer[] categories;
	@Getter private final String[] tags;
	@Getter private final Double latitude, longitude;
	@Getter private final String dateInformation, title,
		address, contactInformation;
	private final HashMap<String, String> descriptions;
	@Getter private final int[][] timeData;

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