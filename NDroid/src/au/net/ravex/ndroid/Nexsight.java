package au.net.ravex.ndroid;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class Nexsight extends Service {
	private static final String TAG = "NEXSIGHT";
	private NexRecorder ar;
	private boolean shouldRun = false;
	
	private Timer loopTimer;

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onStart(Intent intent, int startid) {
		// FIXME read config and decide what to do

		// Should start new thread and start recording
		Log.i(TAG, "Creating nexRecorder");
		ar = new NexRecorder();

		// notification manager to dispaly started and messages
		Log.i(TAG, "Creating nmManager");
		NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		Notification note = new Notification(R.drawable.icon, "Notify",
				System.currentTimeMillis());

		// Audio/video //FIXME
		boolean shouldRealTimeRecord = false;
		if (shouldRealTimeRecord) {
			try {
				ar.startMedia();
			} catch (IOException e) {
				Log.e(TAG, e.getMessage());
			}
		}
		
		//start activity loop
		Log.i(TAG, "Start activity loop");
		
		shouldRun = true;
		/*
		try {
			ar.startTakingPictures();
		} catch (Exception e) {
			e.printStackTrace();
		}
		*/
		
		loopTimer = new Timer();
		loopTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				Log.i(TAG, "In activity loop, should take pic");
				loopStep();
			}
		}, 0, 10000); //WAIT 10 seconds
	}
	
	public void loopStep() {
		try {
			ar.takePicture();
			//FIXME track movement, sound and more
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onDestroy() {
		try {
			shouldRun = false;
			ar.cleanup();
			if (loopTimer != null) 
				loopTimer.cancel();
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
	}
}
