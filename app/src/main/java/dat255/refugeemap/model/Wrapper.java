package dat255.refugeemap.model;

import lombok.Getter;
import lombok.Setter;

public class Wrapper<T>
{
	@Getter @Setter private T value;

	public Wrapper(T value)
	{ this.value = value; }
}