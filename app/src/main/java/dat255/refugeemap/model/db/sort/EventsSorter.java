package dat255.refugeemap.model.db.sort;

import java.util.List;

import dat255.refugeemap.model.db.Event;

/**
 * @author Shoulder
 */
public interface EventsSorter
{
	public void sort(List<Event> list);
}