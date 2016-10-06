package dat255.refugeemap.model.db.impl;

import java.util.Collection;

import dat255.refugeemap.model.ArrayUtils;
import dat255.refugeemap.model.DistanceCalculator;
import dat255.refugeemap.model.db.Event;
import dat255.refugeemap.model.db.Filter;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Shoulder
 */
public class FilterImpl implements Filter
{
	/**
	 * A class which describes the user's maximum distance to any given event.
	 * @author Shoulder
	 */
	@AllArgsConstructor(access = AccessLevel.PUBLIC)
	public static class DistanceCriteria
	{
		private final Double lonUser, latUser, maxGreatCircleDistance;

		public boolean doesEventFit(Event e)
		{
			return (DistanceCalculator.getGreatCircleDistance(latUser, lonUser,
				e.getLatitude(), e.getLongitude()) > maxGreatCircleDistance);
		}
	}

	// Getters might be removed
	@Getter private final Collection<Integer> categories;
	@Getter private final Collection<String> searchTerms;
	private final DistanceCriteria distanceCriteria;

	// If an argument is {@code null}, it will be counted as not being set
	public FilterImpl(Collection<Integer> ctgs, Collection<String> searchTerms,
		DistanceCriteria distanceCriteria)
	{
		this.categories = ctgs;
		this.searchTerms = searchTerms;
		this.distanceCriteria = distanceCriteria;
	}

	@Override public boolean doesEventFit(Event e)
	{
		if (categories.size()!=0) {
			for (int cat : categories)
				if (ArrayUtils.contains(e.getCategories(), cat))
					return true;
			return false;
		}

		if (searchTerms != null)
			for (String term : searchTerms)
				if (!ArrayUtils.contains(e.getTags(), term) &&
					!e.getTitle().contains(term))
						return false;

		if (distanceCriteria != null && distanceCriteria.doesEventFit(e))
			return false;

		return true;
	}

	@Override public boolean isEmpty()
	{ return (categories.size() == 0 && searchTerms.size() == 0); }
}