package dat255.refugeemap.model.db.impl;

import java.util.Collection;

import dat255.refugeemap.model.ArrayUtils;
import dat255.refugeemap.model.DistanceCalculator;
import dat255.refugeemap.model.EqualityChecker;
import dat255.refugeemap.model.db.Event;
import dat255.refugeemap.model.db.Filter;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.val;

/**
 * @author Axel
 */
public class FilterImpl implements Filter
{
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

	public static final Filter EMPTY_FILTER = new FilterImpl(null, null, null);

	private final Collection<Integer> categories;
	private final Collection<String> searchTerms;
	private final DistanceCriteria distanceCriteria;

	/**
	 * Creates a `Filter` instance with the given criteria. If any
	 * argument is `null`, it will be ignored in `doesEventFit`.
	 */
	public FilterImpl(Collection<Integer> ctgs, Collection<String> searchTerms,
		DistanceCriteria distanceCriteria)
	{
		this.categories = ctgs;
		this.searchTerms = searchTerms;
		this.distanceCriteria = distanceCriteria;
	}

	@Override public boolean doesEventFit(Event e)
	{
		if (categories != null && categories.size() != 0)
			if (!ArrayUtils.containsAny(e.getCategories(), categories))
				return false;

		if (searchTerms != null)
		{
			val equalityChecker = new EqualityChecker<String>() {
				@Override public boolean areEqual(String one, String two)
				{ return one.toLowerCase().equals(two.toLowerCase()); }
			};

			for (String term : searchTerms)
				if (!ArrayUtils.contains(e.getTags(), term, equalityChecker) &&
					!e.getTitle().toLowerCase().contains(term.toLowerCase()))
						return false;
		}

		if (distanceCriteria != null && distanceCriteria.doesEventFit(e))
			return false;

		return true;
	}

	@Override public boolean isEmpty()
	{
		return (
			(categories == null || categories.size() == 0) &&
			(searchTerms == null || searchTerms.size() == 0) &&
			(distanceCriteria == null)
		);
	}
}