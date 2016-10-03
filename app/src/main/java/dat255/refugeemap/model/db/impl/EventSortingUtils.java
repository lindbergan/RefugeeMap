package dat255.refugeemap.model.db.impl;

import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import dat255.refugeemap.model.db.Event;
import dat255.refugeemap.model.db.EventCollection.SortCriteria;

/**
 * A utility class containing implementations of various sorting algorithms
 * for arrays and lists of {@link Event} references as well as static
 * methods for accessing them through {@link SortCriteria} arguments.
 * @author Shoulder
 */
public class EventSortingUtils
{
	/**
	 * Sorts {@code arr} according to {@code sc}.
	 * {@code col} is used if the sorting requires string comparison.
	 *
	 * Preconditions:
	 * - All arguments are non-null
	 */
	public static void sortArray(Event[] arr, SortCriteria sc, Collator col)
	{ arraySorters.get(sc).sort(arr, col); }

	/**
	 * Sorts {@code list} according to {@code sc}.
	 * {@code col} is used if the sorting requires string comparison.
	 *
	 * Preconditions:
	 * - All arguments are non-null
	 */
	public static void sortList(List<Event> list, SortCriteria sc, Collator col)
	{ listSorters.get(sc).sort(list, col); }

	// -----------------------------------------------------
	// --- PRIVATE CONSTANTS, METHODS AND INITIALIZATION ---
	// -----------------------------------------------------

	interface ArraySorter
	{ public void sort(Event[] arr, final Collator strClt); }

	interface ListSorter
	{ public void sort(List<Event> list, final Collator strClt); }

	private static final HashMap<SortCriteria, ArraySorter>
		arraySorters = new HashMap<>();
	private static final HashMap<SortCriteria, ListSorter>
		listSorters = new HashMap<>();

	private static ArraySorter asTitle = new ArraySorter() {
		@Override public void sort(Event[] arr, Collator sc)
		{
			for (int i = 0; i < arr.length; i++)
				for (int j = i + 1; j < arr.length; j++)
				{
					boolean shouldSwap = (sc.compare(arr[i].getTitle(),
						arr[j].getTitle()) < 0);
					if (shouldSwap)
					{
						// because Java is awful (pass-by-value only)
						Event tempEvent = arr[i];
						arr[i] = arr[j];
						arr[j] = tempEvent;
					}
				}
		}
	};

	private static ArraySorter asTitleReverse = new ArraySorter() {
		@Override public void sort(Event[] arr, Collator sc)
		{
			for (int i = 0; i < arr.length; i++)
				for (int j = i + 1; j < arr.length; j++)
				{
					boolean shouldSwap = (sc.compare(arr[i].getTitle(),
						arr[j].getTitle()) > 0);
					if (shouldSwap)
					{
						// because Java is awful (pass-by-value only)
						Event tempEvent = arr[i];
						arr[i] = arr[j];
						arr[j] = tempEvent;
					}
				}
		}
	};

	private static ListSorter lsTitle = new ListSorter() {
		@Override public void sort(List<Event> list, final Collator sc)
		{
			Collections.sort(list, new Comparator<Event>() {
				@Override public int compare(Event e1, Event e2)
				{ return sc.compare(e2.getTitle(), e1.getTitle()); }
			});
		}
	};

	private static ListSorter lsTitleReverse = new ListSorter() {
		@Override public void sort(List<Event> list, final Collator sc)
		{
			Collections.sort(list, new Comparator<Event>() {
				@Override public int compare(Event e1, Event e2)
				{ return -sc.compare(e2.getTitle(), e1.getTitle()); }
			});
		}
	};

	static
	{
		arraySorters.put(SortCriteria.TitleAlphabetical, asTitle);
		arraySorters.put(SortCriteria.TitleAlphabeticalReverse, asTitleReverse);

		listSorters.put(SortCriteria.TitleAlphabetical, lsTitle);
		listSorters.put(SortCriteria.TitleAlphabeticalReverse, lsTitleReverse);
	}
}