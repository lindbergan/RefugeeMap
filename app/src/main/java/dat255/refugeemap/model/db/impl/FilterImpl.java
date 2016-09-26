package dat255.refugeemap.model.db.impl;

import java.util.Collection;

import dat255.refugeemap.model.db.Event;
import dat255.refugeemap.model.db.Filter;
import lombok.Getter;

/**
 * @author Shoulder
 */
public class FilterImpl implements Filter
{
	@Getter private final Collection<Integer> categories;
	@Getter private final Collection<String> tags;

	public FilterImpl(Collection<Integer> categories, Collection<String> tags)
	{
		this.categories = categories;
		this.tags = tags;
	}

	public boolean doesEventFit(Event e)
	{
		for (int c : categories)
			for (int ec : e.getCategories())
				if (c == ec)
					return true;

		for (String t : tags)
			for (String et : e.getTags())
				if (t.equals(et))
					return true;

		return false;
	}
}