package dat255.refugeemap.model.db.impl;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import dat255.refugeemap.model.ArrayUtils;
import dat255.refugeemap.model.DistanceCalculator;
import dat255.refugeemap.model.db.Event;
import dat255.refugeemap.model.db.Event.SortInfo;
import lombok.val;

/**
 * A utility class containing implementations of various sorting algorithms
 * for arrays and lists of {@link Event} references as well as static
 * methods for accessing them through {@link SortInfo} arguments.
 * @author Shoulder
 */
public class EventSortingUtils
{
	/**
	 * Sorts {@code arr} according to {@code info}.
	 *
	 * Preconditions:
	 * - {@code info} is a valid implementation of
	 *   {@link SortInfo} (i.e. from {@link EventImpl}).
	 * - All arguments are non-null
	 */
	public static void sortArray(Event[] arr, SortInfo info)
	{ arraySorters[info.getInternalID()].sort(arr, info); }

	/**
	 * Sorts {@code list} according to {@code info}.
	 *
	 * Preconditions:
	 * - {@code info} is a valid implementation of
	 *   {@link SortInfo} (i.e. from {@link EventImpl}).
	 * - All arguments are non-null
	 */
	public static void sortList(List<Event> list, SortInfo info)
	{ listSorters[info.getInternalID()].sort(list, info); }

	interface ArraySorter
	{ public void sort(Event[] arr, SortInfo info); }

	interface ListSorter
	{ public void sort(List<Event> list, SortInfo info); }

	// ---------------------------------------------------
	// --- SORT BY TITLE (ALPHABETICALLY, FROM A TO Z) ---
	// ---------------------------------------------------

	private static ArraySorter asTitle = new ArraySorter() {
		@Override public void sort(Event[] arr, SortInfo info)
		{
			final Comparator strComp =
				((EventImpl.TitleSortInfo)info).stringComparator;

			ArrayUtils.sort(arr, new Comparator<Event>() {
				@Override public int compare(Event e1, Event e2)
				{ return strComp.compare(e2.getTitle(), e1.getTitle()); }
			});
		}
	};

	private static ListSorter lsTitle = new ListSorter() {
		@Override public void sort(List<Event> list, SortInfo info)
		{
			final Comparator strComp =
				((EventImpl.TitleSortInfo)info).stringComparator;

			Collections.sort(list, new Comparator<Event>() {
				@Override public int compare(Event e1, Event e2)
				{ return strComp.compare(e2.getTitle(), e1.getTitle()); }
			});
		}
	};

	// ------------------------------------------------------
	// --- SORT BY DISTANCE FROM USER (LEAST TO GREATEST) ---
	// ------------------------------------------------------

	private static ArraySorter asDistance = new ArraySorter() {
		@Override public void sort(Event[] arr, SortInfo info)
		{
			val dInfo = (EventImpl.DistanceSortInfo)info;

			final double[] distances = new double[arr.length];
			for (int i = 0; i < arr.length; i++)
			{
				distances[i] = DistanceCalculator.getGreatCircleDistance(
					dInfo.userLat, dInfo.userLon,
					arr[i].getLatitude(), arr[i].getLongitude()
				);
			}

			ArrayUtils.sort(arr, new ArrayUtils.ArrayComparator<Event>() {
				@Override public int compare(Event[] arr, int iOne, int iTwo)
				{ return (int)(Math.round(distances[iOne] - distances[iTwo])); }
			});
		}
	};

	private static ListSorter lsDistance = new ListSorter() {
		@Override public void sort(List<Event> list, SortInfo info)
		{
			final val dInfo = (EventImpl.DistanceSortInfo)info;

			Collections.sort(list, new Comparator<Event>() {
				@Override public int compare(Event e1, Event e2)
				{
					double d1 = DistanceCalculator.getGreatCircleDistance(
						dInfo.userLat, dInfo.userLon,
						e1.getLatitude(), e1.getLongitude()
					);

					double d2 = DistanceCalculator.getGreatCircleDistance(
						dInfo.userLat, dInfo.userLon,
						e2.getLatitude(), e2.getLongitude()
					);

					return (int)(Math.round((d1 - d2)));
				}
			});
		}
	};

	private static final ArraySorter[] arraySorters = new ArraySorter[] {
		asTitle, asDistance
	};

	private static final ListSorter[] listSorters = new ListSorter[] {
		lsTitle, lsDistance
	};
}