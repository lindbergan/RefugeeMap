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

	private String toLocale = App
			.getInstance()
			.getBaseContext()
			.getResources()
			.getConfiguration()
			.locale
			.toString();
	private String fromLocale = "sv";

	String getToLocale() {
		return toLocale;
	}

	String getFromLocale() {
		return fromLocale;
	}

	public void setToLocale(String toLocale) {
		this.toLocale = toLocale;
	}

	public void setFromLocale(String fromLocale) {
		this.fromLocale = fromLocale;
	}

	public HashMap<String, String> getTranslatedEventDescription(HashMap<String, String> args) throws ExecutionException, InterruptedException {
		AsyncTask<HashMap<String, String>, Boolean, HashMap<String, String>> execute = new RetrieveFeedTask().execute(args);
		return execute.get();
	}

}

class RetrieveFeedTask extends AsyncTask<HashMap<String, String>, Boolean, HashMap<String, String>> {

	UrlConnectionHelper url = new UrlConnectionHelper();

	@Override
	protected HashMap<String, String> doInBackground(HashMap<String, String>... args) {
		try {

			String CLIENT_ID = "h2359918@mvrht.com";
			String CLIENT_SECRET = "+itinbYoNupzDZf/1F44HKt9h+hwNA4Ph36LBA7iZR0";
			String description = args[0].get("description");
			String urlString =
					"https://api.datamarket.azure.com/Bing/MicrosoftTranslator/v1/Translate?Text=%27"
							+ description.replace("", "%20") + " %27&To=%27" + url.getToLocale()
							+ "%27&From=%27" + url.getFromLocale() + "%27";

			URL url = new URL(urlString);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			byte[] b = Base64.encode((CLIENT_ID+":"+CLIENT_SECRET).getBytes("UTF-8"), 2);
			String encoded = new String(b);
			connection.setRequestProperty("Authorization", "Basic " + encoded);

			String inputLine;
			List<String> list = new LinkedList<>();

			while ((inputLine = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine()) != null) {
				list.add(inputLine);
			}

			for (String str : list) {
				inputLine = str + " ";
			}

			for (String str : inputLine.split("<")) {
				if (str.contains("d:Text")) {
					args[0].remove("description");
					args[0].put("description", str.split(">")[1]);
					break;
				}
			}

			connection.disconnect();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return args[0];
	}
}
