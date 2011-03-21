package au.net.ravex.ndroid;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;
import android.view.Surface;

/**
 * Based on tutorial online
 *
 * @author James Hornitzky, <a href="http://www.benmccann.com">Ben McCann</a>
 */
public class NexRecorder {
	private Surface surface;

	private static final String TAG = NexRecorder.class.getName();
	final MediaRecorder recorder = new MediaRecorder();
	private String path;
	private Camera camera;

	/**
	 * Self explanatory
	 * @param path
	 * @return
	 */
	private String sanitizePath(String path) {
		if (!path.startsWith("/")) {
			path = "/" + path;
		}
		if (!path.contains(".")) {
			path += ".3gp";
		}
		return Environment.getExternalStorageDirectory().getAbsolutePath()
				+ path;
	}

	/**
	 * Starts a new recording.
	 */
	public void start() throws IOException {
		String state = android.os.Environment.getExternalStorageState();
		if (!state.equals(android.os.Environment.MEDIA_MOUNTED)) {
			throw new IOException("SD Card is not mounted.  It is " + state
					+ ".");
		}

		//Create a randomized filename based on milliseconds since epoch
		Log.d(TAG, "Creating file name");
		long mills = new Date().getTime();
		path = sanitizePath(String.valueOf(mills));

		// make sure the directory we plan to store the recording in exists
		File directory = new File(path).getParentFile();
		if (!directory.exists() && !directory.mkdirs()) {
			throw new IOException("Path to file could not be created.");
		}

		Log.d(TAG, "Starting recorder");

		/* FIXME cameras
 		Log.i(TAG, "Opening camera");
		camera = Camera.open();
		recorder.setCamera(camera);
		recorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
		recorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);
		*/

		//audio
		recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
		recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		recorder.setOutputFile(path);
		recorder.prepare();
		recorder.start();
	}

	/**
	 * Stops a recording that has been previously started.
	 */
	public void stop() throws IOException {
		try {
			recorder.stop();
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
		recorder.release();
	}
}
