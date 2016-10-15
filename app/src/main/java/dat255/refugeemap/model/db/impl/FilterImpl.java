package dat255.refugeemap.model.db.impl;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

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

	/** @author Sebastian */
	@AllArgsConstructor(access = AccessLevel.PUBLIC)
	public static class TimeCriteria
	{
		public boolean doesEventFit(Event e)
		{
			int[][] timeData = e.getTimeData();

			Calendar c = Calendar.getInstance();
			c.setTime(new Date());

			// Monday to Sunday represented by the indices 0 to 6
			int currentDayOfWeek = c.get(Calendar.DAY_OF_WEEK) - 2;
			int currentHourOfDay =  c.get(Calendar.HOUR_OF_DAY);

			for(int i = 0; i < timeData.length; i++)
			{
				int[] day = timeData[i];

				int weekDay = day[0];
				int startHours = day[1];
				int endHours = day[3];
				if (currentDayOfWeek == weekDay
					&& currentHourOfDay > startHours
					&& currentHourOfDay < endHours)
						return true;
			}

			return false;
		}
	}

	public static final int NULL_CATEGORY = -1;

	public static final Filter EMPTY_FILTER =
		new FilterImpl(NULL_CATEGORY, null, null, null);

	private final int category;
	private final Collection<String> searchTerms;
	private final DistanceCriteria distanceCriteria;
	private final TimeCriteria timeCriteria;

	/**
	 * Creates a `Filter` instance with the given criteria.
	 * To ignore `category`, set it to `EMPTY_FILTER`.
	 * To ignore any other criteria, set it to `null`.
	 */
	public FilterImpl(int category, Collection<String> searchTerms,
		DistanceCriteria distCriteria, TimeCriteria timeCriteria)
	{
		this.category = category;
		this.searchTerms = searchTerms;
		this.distanceCriteria = distCriteria;
		this.timeCriteria = timeCriteria;
	}

	@Override public boolean doesEventFit(Event e)
	{
		if (category != NULL_CATEGORY)
			if (!ArrayUtils.contains(e.getCategories(), category))
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

		if (timeCriteria != null && !timeCriteria.doesEventFit(e))
			return false;

		return true;
	}

	@Override public boolean isEmpty()
	{
		return ((category == NULL_CATEGORY) &&
			(searchTerms == null || searchTerms.size() == 0) &&
			(distanceCriteria == null) &&
			(timeCriteria == null));
	}
}