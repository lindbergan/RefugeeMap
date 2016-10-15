package dat255.refugeemap.helpers;

import android.os.AsyncTask;
import android.util.Base64;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import dat255.refugeemap.App;
import dat255.refugeemap.model.db.Event;

public class UrlConnectionHelper {

	public HashMap<String, String> translateEvent(Event event) throws ExecutionException, InterruptedException {
		Event[] array = new Event[1];
		array[0] = event;
		AsyncTask<Event, Boolean, HashMap<String, String>> execute = new RetrieveFeedTask().execute(array);
		return execute.get();
	}

}

class RetrieveFeedTask extends AsyncTask<Event, Boolean, HashMap<String, String>> {

	@Override
	protected HashMap<String, String> doInBackground(Event... events) {

		HashMap<String, String> result = new HashMap<>();
		try {

			String urlAsString = getUrl(events[0].getDescription("sv"));
			URL url = new URL(urlAsString);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestProperty("Authorization", "Basic " + encodeAuthorizationKey());

			String inputLine;
			List<String> list = new LinkedList<>();

			while ((inputLine = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine()) != null) {
				list.add(inputLine);
			}
			connection.disconnect();

			StringBuilder builder = new StringBuilder("");
			for (String str : list) builder.append(str);
			result.put("description", extractFromXml(builder.toString()));

		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	private String encodeAuthorizationKey() throws UnsupportedEncodingException {
		String CLIENT_ID = "h2359918@mvrht.com";
		String CLIENT_SECRET = "+itinbYoNupzDZf/1F44HKt9h+hwNA4Ph36LBA7iZR0";
		byte[] b = Base64.encode((CLIENT_ID+":"+CLIENT_SECRET).getBytes("UTF-8"), 2);
		String encoded = new String(b);
		return encoded;
	}

	private String extractFromXml(String inputLine) {
		for (String str : inputLine.split("<")) {
			if (str.contains("d:Text")) {
				return str.split(">")[1];
			}
		}
		return "Translation failed";
	}

	private String getUrl(String description) {
		String fromLocale = "sv";
		String toLocale = App
				.getInstance()
				.getBaseContext()
				.getResources()
				.getConfiguration()
				.locale
				.toString();

		String urlString = "https://api.datamarket.azure.com"
						+ "/Bing/MicrosoftTranslator/v1/Translate?Text=%27"
						+ description.replace(" ", "%20")
						+ " %27&To=%27"
						+ toLocale
						+ "%27&From=%27"
						+ fromLocale
						+ "%27";
		return urlString;
	}
}
