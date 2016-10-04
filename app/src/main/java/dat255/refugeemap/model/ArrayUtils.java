package dat255.refugeemap.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;

public class ArrayUtils
{
	public interface ArrayComparator<T>
	{
		/**
		 * Returns a integer that is negative, zero or positive if
		 * {@code arr[iOne]} is less than, equal to or greater
		 * than (respectively) {@code arr[iTwo]}.
		 */
		public int compare(T[] arr, int iOne, int iTwo);
	}

	public static <T> boolean contains(T[] arr, T elem)
	{
		for (T arrElem : arr)
			if (elem.equals(arrElem))
				return true;
		return false;
	}

	public static <T> boolean containsAll(T[] srcArr, Collection<T> tgtCol)
	{
		for (T elem : tgtCol)
			if (!contains(srcArr, elem))
				return false;
		return true;
	}

	public static <T> void swap(T[] arr, int iOne, int iTwo)
	{
		// because Java is awful (pass-by-value only)
		T temp = arr[iOne];
		arr[iOne] = arr[iTwo];
		arr[iTwo] = temp;
	}

	public static <T> void sort(T[] arr, Comparator<T> comp)
	{
		for (int i = 0; i < arr.length; i++)
			for (int j = i + 1; j < arr.length; j++)
				if (comp.compare(arr[i], arr[j]) > 0)
					swap(arr, i, j);
	}

	public static <T> void sort(T[] arr, ArrayComparator<T> comp)
	{
		for (int i = 0; i < arr.length; i++)
			for (int j = i + 1; j < arr.length; j++)
				if (comp.compare(arr, i, j) > 0)
					swap(arr, i, j);
	}
}