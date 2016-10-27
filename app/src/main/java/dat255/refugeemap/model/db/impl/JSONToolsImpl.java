package dat255.refugeemap.model.db.impl;

import com.google.gson.Gson;

import java.io.Reader;
import java.lang.reflect.Type;

import dat255.refugeemap.model.db.JSONTools;

/**
 * An implementation of {@link JSONTools} using the <b>Gson</b> library.
 * @author Axel
 */
public class JSONToolsImpl implements JSONTools
{
	@Override public Object deserializeReader(Reader reader, Type objectType)
	{ return new Gson().fromJson(reader, objectType); }

	@Override public Object deserializeString(String json, Type objectType)
	{ return new Gson().fromJson(json, objectType); }
}