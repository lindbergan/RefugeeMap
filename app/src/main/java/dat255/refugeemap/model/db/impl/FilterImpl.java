package dat255.refugeemap.model.db.impl;

import java.util.Collection;

import dat255.refugeemap.model.ArrayUtils;
import dat255.refugeemap.model.db.Event;
import dat255.refugeemap.model.db.Filter;
import lombok.Getter;

/**
 * @author Shoulder
 */
public class FilterImpl implements Filter
{
	@Getter private final Collection<Integer> categories;
	@Getter private final Collection<String> searchTerms;

	public FilterImpl(Collection<Integer> ctgs, Collection<String> searchTerms)
	{
		this.categories = ctgs;
		this.searchTerms = searchTerms;
	}

	public boolean doesEventFit(Event e)
	{
		if (!ArrayUtils.containsAll(e.getCategories(), categories))
			return false;

		for (String term : searchTerms)
			if (!ArrayUtils.contains(e.getTags(), term) &&
				!e.getTitle().contains(term))
					return false;

		return true;
	}
}