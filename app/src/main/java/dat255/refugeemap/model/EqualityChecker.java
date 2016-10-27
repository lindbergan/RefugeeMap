package dat255.refugeemap.model;

/**
 * A simple interface used for equality comparison.
 * @author Axel
 */
public interface EqualityChecker<T>
{
	/* Returns {@code true} iff {@code one} and {@code two} are equal. */
	boolean areEqual(T one, T two);
}