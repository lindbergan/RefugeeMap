package dat255.refugeemap.model.db;

import java.io.FileNotFoundException;
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
   */
  public Object deserialize(String filePath, Type objectType)
    throws FileNotFoundException;
}