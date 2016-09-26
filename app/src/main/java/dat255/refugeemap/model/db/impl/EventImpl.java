package dat255.refugeemap.model.db.impl;

import dat255.refugeemap.model.db.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Shoulder
 */
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
// Temporary constructor: will be replaced with the JSON integration
public class EventImpl implements Event
{
	private final Integer id;
	@Getter private final Integer ownerID;
	@Getter private final Integer[] categories, tags;
	@Getter private final Double latitude, longitude;
	@Getter private final String title, address, contactInformation, description;

	@Override public Integer getID()
	{ return id; }
}