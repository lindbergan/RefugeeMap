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
	@Getter private final Collection<String> searchTerms;

	public FilterImpl(Collection<Integer> ctgs, Collection<String> searchTerms)
	{
		this.categories = ctgs;
		this.searchTerms = searchTerms;
	}

	public boolean doesEventFit(Event e)
	{
		for (int c : categories)
			for (int ec : e.getCategories())
				if (c == ec)
					return true;

		for (String term : searchTerms)
		{
			for (String tag : e.getTags())
				if (term.equals(tag))
					return true;
			if (e.getTitle().contains(term)) return true;
		}

		return false;
	}
}