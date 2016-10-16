package dat255.refugeemap.model;

import lombok.Getter;
import lombok.Setter;

/**
 * A very simple wrapper class, used to bypass
 * some issues with Java's argument handling.
 * @author Axel
 */
public class Wrapper<T>
{
	@Getter @Setter private T value;

	public Wrapper(T value)
	{ this.value = value; }
}