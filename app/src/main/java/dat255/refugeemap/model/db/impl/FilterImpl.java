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

	public static class TimeCriteria
	{
		private final int iWeekDay;

		// Precondition: `weekDayIndex` is in {0, 1, ..., 7}
		public TimeCriteria(int weekDayIndex)
		{ iWeekDay = weekDayIndex; }

		public boolean doesEventFit(Event e)
		{
			final int[][] timeData = e.getTimeData();
			for (int[] aTimeData : timeData)
				if (iWeekDay == aTimeData[0])
					return true;
			return false;
		}
	}

	public static final Filter EMPTY_FILTER =
		new FilterImpl(null, null, null, null);

	private final Integer category;
	private final Collection<String> searchTerms;
	private final DistanceCriteria distanceCriteria;
	private final TimeCriteria timeCriteria;

	/**
	 * Creates a {@link Filter} instance with the given criteria.
	 * To ignore any criteria, set it to {@code null}.
	 */
	public FilterImpl(Integer category, Collection<String> searchTerms,
		DistanceCriteria distCriteria, TimeCriteria timeCriteria)
	{
		this.category = category;
		this.searchTerms = searchTerms;
		this.distanceCriteria = distCriteria;
		this.timeCriteria = timeCriteria;
	}

	private boolean isTermInAnyTitle(Event e, String termLowerCase)
	{
		for (String lang : e.getAvailableLanguages())
			if (e.getTitle(lang).toLowerCase().contains(termLowerCase))
				return true;
		return false;
	}

	@Override public boolean doesEventFit(Event e)
	{
		if (category != null)
			if (!ArrayUtils.contains(e.getCategories(), category))
				return false;

		if (searchTerms != null)
		{
			val ec = new EqualityChecker<String>() {
				@Override public boolean areEqual(String one, String two)
				{ return one.toLowerCase().equals(two.toLowerCase()); }
			};

			for (String term : searchTerms)
				if (!ArrayUtils.contains(e.getTags(), term.toLowerCase(), ec) &&
					!isTermInAnyTitle(e, term.toLowerCase()))
						return false;
		}

		if (distanceCriteria != null && distanceCriteria.doesEventFit(e))
			return false;

		if (timeCriteria != null && !timeCriteria.doesEventFit(e))
			return false;

		return true;
	}

	@Override public boolean isEmpty()
	{
		return ((category == null) &&
			(searchTerms == null || searchTerms.size() == 0) &&
			(distanceCriteria == null) &&
			(timeCriteria == null));
	}
}