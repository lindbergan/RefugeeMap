package dat255.refugeemap.app.base;

import android.os.AsyncTask;
import android.util.Base64;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import dat255.refugeemap.app.gui.App;
import dat255.refugeemap.model.db.Event;
import lombok.val;

/**
 * Class for translating an {@link Event}'s title and description.
 */
public class EventOnlineTranslator
{
	/**
	 * Returns a translation of the given event's title and description to
	 * the language set by the current {@link Locale}. If the translation
	 * fails, the title and description are returned in English.
	 */
	public static Event.Translation translateEvent(Event event)
	{
		try
		{
			return new RetrieveFeedTask().execute(event).get();
		} catch (InterruptedException | ExecutionException e)
		{
			return new Event.Translation(event.getTitle("en"),
				event.getDescription("en"));
		}
	}

	/**
	 * Class for retrieving a translation from an URL.
	 */
	private static class RetrieveFeedTask extends
		AsyncTask<Event, Boolean, Event.Translation>
	{
		public List<String> translate(String text) throws IOException
		{
			URL textAsURL = new URL(makeURL(text));
			val connection = (HttpURLConnection)textAsURL.openConnection();
			connection.setRequestProperty("Authorization",
				"Basic " + encodeAuthKey());

			val reader = new BufferedReader(new InputStreamReader(connection.
				getInputStream(), "UTF-8"));

			List<String> list = new LinkedList<>();
			for (String inputLine = reader.readLine(); inputLine != null;)
				list.add(inputLine);

			reader.close();
			connection.disconnect();

			return list;
		}

		@Override
		protected Event.Translation doInBackground(Event... events)
		{
			Event event = events[0];
			String title = event.getTitle("en"),
				desc = event.getDescription("en");

			try
			{
				StringBuilder builderTitle = new StringBuilder("");
				for (String str : translate(title)) builderTitle.append(str);

				StringBuilder builderDesc = new StringBuilder("");
				for (String str : translate(desc)) builderDesc.append(str);

				return new Event.Translation(extractFromXML(builderTitle.
					toString()), extractFromXML(builderDesc.toString()));
			} catch (IOException e)
			{
				// Error, return in English
				return new Event.Translation(title, desc);
			}
		}

		private static final String CLIENT_ID = "h2359918@mvrht.com";
		private static final String CLIENT_SECRET =
			"+itinbYoNupzDZf/1F44HKt9h+hwNA4Ph36LBA7iZR0";

		private String encodeAuthKey() throws UnsupportedEncodingException
		{
			return Base64.encodeToString((CLIENT_ID + ":" + CLIENT_SECRET).
				getBytes("UTF-8"), Base64.NO_WRAP);
		}

		private String extractFromXML(String inputLine)
		{
			for (String str : inputLine.split("<"))
				if (str.contains("d:Text"))
					return str.split(">")[1];
			return "Translation failed";
		}

		private String makeURL(String text)
		{
			return "https://api.datamarket.azure.com" +
				"/Bing/MicrosoftTranslator/v1/Translate?Text=%27" +
				text.replace(" ", "%20") + " %27&To=%27" +
				App.getInstance().getLocaleCode() + "%27&From=%27en%27";
		}
	}
}