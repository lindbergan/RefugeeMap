package dat255.refugeemap.helpers;

import android.os.AsyncTask;
import android.util.Base64;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import dat255.refugeemap.App;
import dat255.refugeemap.model.db.Event;

public class UrlConnectionHelper {

	public static HashMap<String, String> translateEvent(Event event) throws ExecutionException, InterruptedException {
		Event[] array = new Event[1];
		array[0] = event;
		return new RetrieveFeedTask().execute(array).get();
	}

}

class RetrieveFeedTask extends AsyncTask<Event, Boolean, HashMap<String, String>> {

	public List<String> translate(String text) throws IOException {
		String urlAsString = getUrl(text);
		URL url = new URL(urlAsString);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestProperty("Authorization", "Basic " + encodeAuthorizationKey());

		String inputLine;
		List<String> list = new LinkedList<>();

		while ((inputLine = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine()) != null) {
			list.add(inputLine);
		}
		connection.disconnect();
		return list;
	}

	@Override
	protected HashMap<String, String> doInBackground(Event... events) {
		HashMap<String, String> temp = new HashMap<>();
		StringBuilder builder = new StringBuilder("");
		Event event = events[0];
		try {
			for (String str : translate(event.getDescription("en"))) builder.append(str);
			temp.put("description", extractFromXml(builder.toString()));

			for (String str : translate(event.getTitle("en"))) builder.append(str);
			temp.put("title", extractFromXml(builder.toString()));
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return temp;
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

	private String getUrl(String text) {
		String fromLocale = "en";

		String urlString = "https://api.datamarket.azure.com"
						+ "/Bing/MicrosoftTranslator/v1/Translate?Text=%27"
						+ text.replace(" ", "%20")
						+ " %27&To=%27"
						+ App.getInstance().getLocale()
						+ "%27&From=%27"
						+ fromLocale
						+ "%27";
		return urlString;
	}
}

