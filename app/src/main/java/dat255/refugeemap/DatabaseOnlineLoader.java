package dat255.refugeemap;

import android.accounts.NetworkErrorException;
import android.os.AsyncTask;

import org.apache.commons.io.IOUtils;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

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

		private boolean didFinishWithoutErrors = false;
		private Wrapper<byte[]> bytes;

		public LoadEventTask(Wrapper<byte[]> bytes)
		{ this.bytes = bytes; }

		public boolean finishedWithoutErrors()
		{ return didFinishWithoutErrors; }

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
	};

	public static void load(Wrapper<byte[]> eventBytes)
		throws NetworkErrorException
	{
		LoadEventTask task = new LoadEventTask(eventBytes);
		try
		{
			Boolean didFinishWithoutErrors = task.execute().get();
			if (!didFinishWithoutErrors) // (May be unnecessary)
				throw new NetworkErrorException();
		} catch (ExecutionException | InterruptedException e) {
			throw new NetworkErrorException();
		}
	}
}