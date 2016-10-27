package dat255.refugeemap;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import dat255.refugeemap.model.db.impl.JSONToolsImpl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/** @author Axel */
public class TestJSONToolsImpl
{
	private static class DummyClass
	{
		private Integer i;
		private Double d;
		private String s;

		public DummyClass() {}
	}

	private static final String JSON =
		"{\"i\":42,\"d\":7.1024,\"s\":\"Hello!\"}";

	@Test public void testDeserializeString()
	{
		DummyClass dummy = (DummyClass)(new JSONToolsImpl().
			deserializeString(JSON, DummyClass.class));

		performAssertions(dummy);
	}

	@Test public void testDeserializeReader()
	{
		byte[] bytes = JSON.getBytes(StandardCharsets.UTF_8);

		try
		{
			Reader reader = new InputStreamReader(new
				ByteArrayInputStream(bytes), "UTF-8");

			DummyClass dummy = (DummyClass)(new JSONToolsImpl().
				deserializeReader(reader, DummyClass.class));

			performAssertions(dummy);
		} catch (UnsupportedEncodingException e) { fail(); }
	}

	private void performAssertions(DummyClass dummy)
	{
		assertEquals(dummy.i, Integer.valueOf(42));
		assertEquals(dummy.d, Double.valueOf(7.1024));
		assertEquals(dummy.s, "Hello!");
	}
}