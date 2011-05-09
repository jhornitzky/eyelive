package au.net.ravex.ndroid;

import java.io.IOException;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.IBinder;
import android.util.Log;

public class Nexsight extends Service {
	private static final String TAG = "NEXSIGHT";
	private NexRecorder ar;
	private AudioManager am;
	private NotificationManager nm;
	private Notification notification;
	private boolean shouldRun = false;
	private static final int NM = 981;
	Context context;
	
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

		// Audio/video //FIXME
		boolean shouldRealTimeRecord = false;
		if (shouldRealTimeRecord) {
			try {
				ar.startMedia();
			} catch (IOException e) {
				Log.e(TAG, e.getMessage());
			}
		}

		//audio mgr
		am = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
		ar.am = am; // setAudio
		
		//start activity loop
		Log.i(TAG, "Start activity loop");
		shouldRun = true;
		loopTimer = new Timer();
		loopTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				Log.i(TAG, "In activity loop, should take pic");
				loopStep();
			}
		}, 0, 20*1000); //WAIT 10 seconds
		
		// notification manager to display started and messages
		Log.i(TAG, "Creating nmManager");
		nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		int icon = R.drawable.droid;
		CharSequence tickerText = "Nexsight update";
		long when = System.currentTimeMillis();
		notification = new Notification(icon, tickerText, when);
		context = getApplicationContext();
		CharSequence contentTitle = "Nexsight is running";
		CharSequence contentText = "Recording snapshots every 20 seconds";
		Intent notificationIntent = new Intent(this, NdroidMain.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		nm.notify(NM, notification);
	}
	
	public void loopStep() {
		if (shouldRun) { 
			try {
				//FIXME track movement, sound and more
				ar.takePicture();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			Log.i(TAG, "Not should run");
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
			if (loopTimer != null) 
				loopTimer.cancel();
			CharSequence contentTitle = "Nexsight is stopped";
			CharSequence contentText = "Press to setup recording";
			Intent notificationIntent = new Intent(this, NdroidMain.class);
			PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
			notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
			nm.notify(NM, notification);
			ar.cleanup();
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
	}
}
