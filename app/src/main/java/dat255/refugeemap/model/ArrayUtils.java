package dat255.refugeemap.model;

import java.util.Collection;

public class ArrayUtils
{
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
}