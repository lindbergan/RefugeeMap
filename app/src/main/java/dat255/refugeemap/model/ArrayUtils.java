package dat255.refugeemap.model;

import java.util.Collection;

/**
 * @author Shoulder
 */
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

	public interface EqualityChecker<T>
	{
		/* Returns {@code true} iff {@code one} and {@code two} are equal. */
		public boolean areEqual(T one, T two);
	}

	public static <T> boolean contains(T[] arr, T elem)
	{
		for (T arrElem : arr)
			if (elem.equals(arrElem))
				return true;
		return false;
	}

	public static <T> boolean contains(T[] arr, T elem, EqualityChecker ec)
	{
		for (T arrElem : arr)
			if (ec.areEqual(elem, arrElem))
				return true;
		return false;
	}

	public static <T> boolean containsAny(T[] srcArr, Collection<T> tgtCol)
	{
		for (T elem : tgtCol)
			if (contains(srcArr, elem))
				return true;
		return false;
	}
}