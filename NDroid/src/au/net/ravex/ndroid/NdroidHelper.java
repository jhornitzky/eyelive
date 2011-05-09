package au.net.ravex.ndroid;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Environment;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.Log;

public class NdroidHelper {
	private static final String TAG = "NdroidHelper";

	public static File[] flipArray(File[] b) {
		for (int left = 0, right = b.length - 1; left < right; left++, right--) {
			// exchange the first and last
			File temp = b[left];
			b[left] = b[right];
			b[right] = temp;
		}
		return b;
	}

	public static List grabFiles() {
		List<String> tFileList = new ArrayList<String>();
		String p = Environment.getExternalStorageDirectory().getAbsolutePath()
				+ "/nexsight/";
		File f = new File(p);

		Log.d(TAG, "Read from " + p);
		File[] files = f.listFiles();
		files = flipArray(files);
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				File file = files[i];
				tFileList.add(file.getPath());
			}
		} else {
			Log.w(TAG, "nothing found in " + p);
		}

		return tFileList;
	}

	public static Bitmap obtainPreviewMap(String path, int sample) {
		Options opts = new Options();
		opts.inSampleSize = sample;
		return BitmapFactory.decodeFile(path, opts);
	}

	public static final int DEF_MAP_SAMPLE = 16;

	public static Bitmap obtainPreviewMap(String path) {
		return obtainPreviewMap(path, DEF_MAP_SAMPLE);
	}
	
	public static String extractDateFromFName(String fName) {
		if (fName == null)
			return fName;
		
		// get basename
		String name = new File(fName).getName();
		// cut extension
		return removeExtension(name);
	}

	public static String getDateFromName(String fName) {
		if (fName == null)
			return fName;
		String name = extractDateFromFName(fName);

		// output as time or date
		CharSequence df;
		long millis = Long.parseLong(name);
		if (DateUtils.isToday(millis)) {
			df = "'today' hh:mmaa";
		} else {
			df = "dd MMM hh:mmaa";
		}
		return (String) DateFormat.format(df, millis);
	}

	public static String removeExtension(String s) {
		String separator = System.getProperty("file.separator");
		String filename;

		// Remove the path up to the filename.
		int lastSeparatorIndex = s.lastIndexOf(separator);
		if (lastSeparatorIndex == -1) {
			filename = s;
		} else {
			filename = s.substring(lastSeparatorIndex + 1);
		}

		// Remove the extension.
		int extensionIndex = filename.lastIndexOf(".");
		if (extensionIndex == -1)
			return filename;

		return filename.substring(0, extensionIndex);
	}
	
	public static boolean isSameDay(String s1, String s2) {
		if (s1 == null || s2 == null) 
			return false;
		Calendar c1 = Calendar.getInstance();
		c1.setTime(new Date(Long.parseLong(s1)));
		Calendar c2 = Calendar.getInstance();
		c2.setTime(new Date(Long.parseLong(s2)));
		return isSameDay(c1, c2);
	}

	public static boolean isSameDay(Calendar cal1, Calendar cal2) {
		if (cal1 == null || cal2 == null) {
			throw new IllegalArgumentException("The date must not be null");
		}
		return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA)
				&& cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1
				.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
	}
	
	public static boolean isSameDayHour(Calendar cal1, Calendar cal2) {
		return (isSameDay(cal1, cal2) && (cal1.get(Calendar.HOUR_OF_DAY) == cal2.get(Calendar.HOUR_OF_DAY)));
	}
}