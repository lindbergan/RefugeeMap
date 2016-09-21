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
  @Getter private final int[] categories, tags;

  @Getter private final double latitudeInDegrees, longitudeInDegrees;

  @Getter private final String title, address, contactInformation, description;
}