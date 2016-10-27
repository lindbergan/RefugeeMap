package dat255.refugeemap.model;

/**
 * A purely static class with various array-related utility methods.
 * @author Axel
 */
public class ArrayUtils
{
	/**
	 * Returns {@code true} iff {@code arr} contains {@code elem}.
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
	 * Returns {@code true} iff {@code arr} contains {@code elem}. (All equality
	 * comparisons are performed using {@code ec} instead of {@code T.equals}.
	 *
	 * Precondition: All arguments are non-null.
	 */
	public static <T> boolean contains(T[] arr, T elem, EqualityChecker<T> ec)
	{
		for (T arrElem : arr)
			if (ec.areEqual(elem, arrElem))
				return true;
		return false;
	}
}