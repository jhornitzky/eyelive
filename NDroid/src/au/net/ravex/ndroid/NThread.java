package au.net.ravex.ndroid;

import java.io.IOException;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class NThread extends Thread {
	private NexRecorder ar;
	private static final int SLEEP_TIME = 60 * 1000; //act every minute or so
	private static final String TAG = "NThread";
	public Handler mHandler;

	public NThread(NexRecorder ar) {
		this.ar = ar;
	}

	// Main loop of the Nexsight service, mainly for bg snapshoting and location
	// tracking
	public void run() {
		Looper.prepare();

        mHandler = new Handler() {
            public void handleMessage(Message msg) {
        		Log.i(TAG, "Run Nthread");
        		try {
        			ar.takePicture();
        			sleep(SLEEP_TIME);
        		} catch (IOException e) {
        			Log.e(TAG, e.getMessage());
        		} catch (InterruptedException e) {
        			Log.e(TAG, e.getMessage());
        		} catch (Exception e) {
        			Log.e(TAG, e.getMessage());
        		}
            }
        };

        Looper.loop();
	}
}
