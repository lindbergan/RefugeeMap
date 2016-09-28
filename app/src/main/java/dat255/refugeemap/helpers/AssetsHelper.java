package dat255.refugeemap.helpers;

import android.app.Activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * refugee-map
 * Group: Cool Boyz 2001
 * Created on 2016-09-28.
 */

public class AssetsHelper {
	public static String getAssetFilePath(String fileName, Activity activity) {
		File f = new File(activity.getCacheDir() + "/" + fileName);

		if (!f.exists()) try {
			InputStream is = activity.getAssets().open(fileName);
			int size = is.available();
			byte[] buffer = new byte[size];
			is.read(buffer);
			is.close();


			FileOutputStream fos = new FileOutputStream(f);
			fos.write(buffer);
			fos.close();
		} catch (Exception e) { throw new RuntimeException(e); }

		return f.getPath();
	}

}
