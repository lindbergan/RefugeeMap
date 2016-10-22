package dat255.refugeemap.model;

import java.util.Collection;

/**
 * A purely static class with various array-related utility methods.
 * @author Axel
 */
public class ArrayUtils
{
	/**
	 * Returns `true` iff `arr` contains `elem`.
	 *
	 * Precondition: All arguments are non-null.
	 */
	public static <T> boolean contains(T[] arr, T elem)
	{
		for (T arrElem : arr)
			if (elem.equals(arrElem))
				return true;
		return false;
	}

	/**
	 * Returns `true` iff `arr` contains `elem`.
	 * (Equality comparisons are performed using `ec` instead of `T.equals`).
	 *
	 * Precondition: All arguments are non-null.
	 */
	public static <T> boolean contains(T[] arr, T elem, EqualityChecker ec)
	{
		for (T arrElem : arr)
			if (ec.areEqual(elem, arrElem))
				return true;
		return false;
	}
}