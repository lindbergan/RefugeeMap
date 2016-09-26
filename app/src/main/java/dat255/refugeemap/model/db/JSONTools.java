package dat255.refugeemap.model.db;

import java.io.FileNotFoundException;
import java.io.Reader;
import java.lang.reflect.Type;

/**
 * An interface for local loading and manipulation of JSON files.
 * @author Shoulder
 */
public interface JSONTools
{
	/**
	 * Deserializes the file at the given path according to
	 * the serialization settings for {@code objectType}.
	 * (The dynamic return type will match {@code objectType}.)
	 *
	 * Preconditions:
	 * - All arguments are non-null
	 * - {@code filePath} is a path to a valid JSON file
	 */
	public Object deserializeFile(String filePath, Type objectType)
		throws FileNotFoundException;

	/**
	 * Deserializes the JSON code in the given reader according to
	 * the serialization settings for {@code objectType}.
	 * (The dynamic return type will match {@code objectType}.)
	 *
	 * Preconditions:
	 * - All arguments are non-null
	 * - {@code fileReader} is a reader containing a valid JSON file
	 */
	public Object deserializeFile(Reader fileReader, Type objectType);

	/**
	 * Deserializes the given JSON string according to the
	 * serialization settings for {@code objectType}.
	 * (The dynamic return type will match {@code objectType}.)
	 *
	 * Preconditions:
	 * - All arguments are non-null
	 * - {@code json} contains valid JSON code
	 */
	public Object deserializeString(String json, Type objectType);
}