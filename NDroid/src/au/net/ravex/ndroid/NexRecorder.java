package au.net.ravex.ndroid;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceView;
import android.widget.ImageView;

/**
 * Based on tutorial online
 *
 * @author James Hornitzky based on <a href="http://www.benmccann.com">Ben McCann</a>
 */
public class NexRecorder {
	private Surface surface; //dummy service for picture taking
	private static final String TAG = NexRecorder.class.getName(); //for logging
	final MediaRecorder recorder = new MediaRecorder(); //technically dont need this
	private String path; //path to output file
	private Camera camera;
	private boolean shouldRun = false;

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
	 * make sure the SDCard is mounted before starting
	 * @throws IOException
	 */
	private void checkForSDCard() throws IOException {
		String state = android.os.Environment.getExternalStorageState();
		if (!state.equals(android.os.Environment.MEDIA_MOUNTED)) {
			throw new IOException("SD Card is not mounted.  It is " + state
					+ ".");
		}
	}
	
	/**
	 * Start the media recorder (audio/video)
	 * @throws IOException
	 */
	public void startMedia() throws IOException {
		checkForSDCard();
		
		Log.d(TAG, "Starting recorder");
		
		//Create a randomized filename based on milliseconds since epoch
		Log.d(TAG, "Creating file name");
		long mills = new Date().getTime();
		path = sanitizePath(String.valueOf(mills));

		// make sure the directory we plan to store the recording in exists
		File directory = new File(path).getParentFile();
		if (!directory.exists() && !directory.mkdirs()) {
			throw new IOException("Path to file could not be created.");
		}
		
		//Now start background recording
		/* 
		//FIXME camera recording
		 
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
	public void stopMedia() throws IOException {
		try {
			recorder.stop();
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
		recorder.release();
	}
	
	
	public void startTakingPictures() throws IOException {
		takePicture();
	}
	
	public void takePicture() throws IOException {
		Log.i(TAG, "Entered take picture");
		checkForSDCard();
		
		Log.i(TAG, "LookAtCamera");
		//camera = Camera.open();
		if (camera == null) {
			Log.i(TAG, "Opening camera");
			camera = Camera.open();
		}
		
		//Create a dummy view and context
		Log.i(TAG, "createSurface");
		SurfaceView view = new SurfaceView(new DummyContext());
		Log.i(TAG, "goingToSetPreviewDisplay");
		camera.setPreviewDisplay(view.getHolder());
		Log.i(TAG, "goingToStartPreview");
		camera.startPreview();
		
		//wait
		Log.i(TAG, "Wait for cam");
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			Log.e(TAG, "Woken violently");
			e.printStackTrace();
		}
		
		//take pic
		Log.i(TAG, "Take pic");
		camera.takePicture(shutterCallback, rawCallback, jpegCallback); //try take a pciture by iteself
	}
	
	// Called when shutter is opened
	ShutterCallback shutterCallback = new ShutterCallback() { // <6>
		public void onShutter() {
			Log.d(TAG, "onShutter'd");
		}		
	};

	// Handles data for raw picture
	PictureCallback rawCallback = new PictureCallback() { // <7>
		public void onPictureTaken(byte[] data, Camera camera) {
			Log.d(TAG, "onPictureTaken - raw");
			
		}
	};
	
	// Handles data for jpeg picture
	PictureCallback jpegCallback = new PictureCallback() { // <8>
		public void onPictureTaken(byte[] data, Camera camera) {
			Log.i(TAG, "onPictureTakenStart");
			FileOutputStream outStream = null;
			try {
				// Write to SD Card
				outStream = new FileOutputStream(String.format(
						"/sdcard/%d.jpg", System.currentTimeMillis())); // <9>
				outStream.write(data);
				outStream.close();
				Log.d(TAG, "onPictureTaken - wrote bytes: " + data.length);
			} catch (FileNotFoundException e) { // <10>
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				camera.stopPreview();
				//camera.release();
			}
			Log.d(TAG, "onPictureTakenFinish - jpeg written");
			
			/*
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} 
			*/
			
			//Then repeat the picture process
			try {
				takePicture();
			} catch (IOException e) {
				e.printStackTrace();
			} 
		}
	};
	
	public void cleanup() {
		if (camera != null) {
			camera.release();
		}
	}
}