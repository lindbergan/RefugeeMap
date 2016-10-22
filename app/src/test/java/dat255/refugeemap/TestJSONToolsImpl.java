package dat255.refugeemap;

import org.apache.commons.io.Charsets;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import dat255.refugeemap.model.db.impl.JSONToolsImpl;

import static org.junit.Assert.assertEquals;

/** @author Axel */
public class TestJSONToolsImpl
{
	private static class DummyClass
	{
		Integer i;
		Double d;
		String s;
	}

	private final String JSON = "{\"i\":42,\"d\":7.1024,\"s\":\"Hello!\"}";

	@Test public void testDeserializeString()
	{
		DummyClass dummy = (DummyClass)(new JSONToolsImpl().
			deserializeString(JSON, DummyClass.class));

		performAssertions(dummy);
	}

	@Test public void testDeserializeReader()
	{
		Reader reader = new InputStreamReader(new ByteArrayInputStream(JSON.
			getBytes(Charsets.UTF_8)));

		DummyClass dummy = (DummyClass)(new JSONToolsImpl().
			deserializeReader(reader, DummyClass.class));

		performAssertions(dummy);
	}

	private void performAssertions(DummyClass dummy)
	{
		assertEquals(dummy.i, new Integer(42));
		assertEquals(dummy.d, new Double(7.1024));
		assertEquals(dummy.s, "Hello!");
	}
}