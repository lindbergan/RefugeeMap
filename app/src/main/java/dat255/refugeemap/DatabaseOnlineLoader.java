package dat255.refugeemap;

import android.os.AsyncTask;

import org.apache.commons.io.IOUtils;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.URL;

import dat255.refugeemap.model.Wrapper;

/**
 * @author Shoulder
 */
public class DatabaseOnlineLoader
{
	private static String categoryNamesDropboxURL =
		"https://dl.dropboxusercontent.com/s/rqop57wt052u532/ctgs.json?dl=0";
	private static String eventsDropboxURL =
		"https://dl.dropboxusercontent.com/s/ho8cmx5g8gwbuka/db.json?dl=0";

	private static class LoadAndSaveTask extends AsyncTask<String, Void, Boolean>
	{
		private Wrapper<byte[]> bytes;

		public LoadAndSaveTask(Wrapper<byte[]> bytes)
		{ this.bytes = bytes; }

		protected Boolean doInBackground(String... urlStrings)
		{
			try {
				DataInputStream is = new DataInputStream(new URL(urlStrings[0]).openStream());
				bytes.setValue(IOUtils.toByteArray(is));
				return true;
			} catch (IOException e) {
				return false;
			}
		}

		protected void onPostExecute(Boolean didLoadingSucceed)
		{
			if (!didLoadingSucceed || bytes.getValue() == null)
				System.exit(-1); // TEMP
		}
	};

	public static void load(Wrapper<byte[]> ctgBytes, Wrapper<byte[]> eventBytes) throws IOException
	{
		LoadAndSaveTask taskA = new LoadAndSaveTask(ctgBytes),
			taskB = new LoadAndSaveTask(eventBytes);
		taskA.execute(categoryNamesDropboxURL);
		taskB.execute(eventsDropboxURL);

		while (taskA.bytes.getValue() == null || taskB.bytes.getValue() == null) {}
	}
}