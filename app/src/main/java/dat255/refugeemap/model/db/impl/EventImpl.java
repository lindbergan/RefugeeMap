package dat255.refugeemap.model.db.impl;

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
	private final Integer id;
	@Getter private final Integer ownerID;
	@Getter private final Integer[] categories;
	@Getter private final String[] tags;
	@Getter private final Double latitude, longitude;
	@Getter private final String title, address,
		contactInformation, description;

	@Override public Integer getID()
	{ return id; }
}