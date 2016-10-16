package dat255.refugeemap.model.db;

import java.io.Reader;
import java.lang.reflect.Type;

/**
 * An interface for local loading and manipulation of JSON files.
 * @author Axel
 */
public interface JSONTools
{
	/**
	 * Deserializes the JSON code in the given reader according to
	 * the serialization settings for `objectType`.
	 * (The dynamic return type will match `objectType`.)
	 *
	 * Preconditions:
	 * - All arguments are non-null
	 * - `fileReader` 'contains' a valid JSON file
	 */
	public Object deserializeFile(Reader fileReader, Type objectType);

	/**
	 * Deserializes the given JSON string according to the
	 * serialization settings for `objectType`.
	 * (The dynamic return type will match `objectType`.)
	 *
	 * Preconditions:
	 * - All arguments are non-null
	 * - `json` contains valid JSON code
	 */
	public Object deserializeString(String json, Type objectType);
}