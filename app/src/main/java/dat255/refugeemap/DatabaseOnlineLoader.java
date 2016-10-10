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
	private static class LoadEventTask extends AsyncTask<Void, Void, Boolean>
	{
		private static final String eventsDropboxURL = "https://dl.dropbox" +
			"usercontent.com/s/q9vii9pslxg91sn/events.json?dl=0";

		private Wrapper<byte[]> bytes;

		public LoadEventTask(Wrapper<byte[]> bytes)
		{ this.bytes = bytes; }

		protected Boolean doInBackground(Void... nothing)
		{
			try
			{
				DataInputStream is = new DataInputStream(new
					URL(eventsDropboxURL).openStream());
				bytes.setValue(IOUtils.toByteArray(is));
				return true;
			} catch (IOException e) { return false; }
		}

		protected void onPostExecute(Boolean didLoadingSucceed)
		{
			if (!didLoadingSucceed || bytes.getValue() == null)
				System.exit(-1); // TEMP
		}
	};

	public static void load(Wrapper<byte[]> eventBytes) throws IOException
	{
		LoadEventTask task = new LoadEventTask(eventBytes);
		task.execute();
		while (task.bytes.getValue() == null) {}
	}
}