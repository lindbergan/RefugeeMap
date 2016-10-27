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
	 * the serialization settings for {@code objectType}.
	 * (The dynamic return type will match {@code objectType}.)
	 *
	 * Preconditions:
	 * - All arguments are non-null
	 * - {@code reader} links to a stream of valid JSON code
	 */
	Object deserializeReader(Reader reader, Type objectType);

	/**
	 * Deserializes the given JSON string according to the
	 * serialization settings for {@code objectType}.
	 * (The dynamic return type will match {@code objectType}.)
	 *
	 * Preconditions:
	 * - All arguments are non-null
	 * - {@code json} contains valid JSON code
	 */
	Object deserializeString(String json, Type objectType);
}