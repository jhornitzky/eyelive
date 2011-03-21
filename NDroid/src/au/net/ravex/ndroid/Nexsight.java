package au.net.ravex.ndroid;

import java.io.IOException;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class Nexsight extends Service {
	private static final String TAG = "NEXSIGHT";
	private NexRecorder ar;

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onStart(Intent intent, int startid) {
		ar = new NexRecorder();
		try {
			ar.start();
			NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			Notification note = new Notification(R.drawable.icon,
					"Notify", System.currentTimeMillis());
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onDestroy() {
		try {
			ar.stop();
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}
	}
}
