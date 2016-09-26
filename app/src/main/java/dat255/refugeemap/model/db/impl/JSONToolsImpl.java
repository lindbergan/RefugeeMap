package dat255.refugeemap.model.db.impl;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;

import dat255.refugeemap.model.db.JSONTools;

/**
 * An implementation of {@link JSONTools} using the <b>Gson</b> library.
 * @author Shoulder
 */
public class JSONToolsImpl implements JSONTools
{
	@Override public Object deserializeFile(String filePath, Type objectType)
		throws FileNotFoundException
	{
		JsonReader reader = new JsonReader(new FileReader(filePath));
		return new Gson().fromJson(reader, objectType);
	}

	@Override public Object deserializeString(String json, Type objectType)
	{ return new Gson().fromJson(json, objectType); }
}