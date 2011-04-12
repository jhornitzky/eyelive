package au.net.ravex.ndroid;

import android.app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class NdroidMain extends Activity implements OnClickListener {
	private static final String TAG = "Ndroid";
	Button buttonStart, buttonStop;
	boolean startedService = false;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		Log.d(TAG, "Created view... binding buttons");

		buttonStart = (Button) findViewById(R.id.buttonStart);
		buttonStop = (Button) findViewById(R.id.buttonStop);

		buttonStart.setOnClickListener(this);
		buttonStop.setOnClickListener(this);
	}

	/**
	 * on click handler for button
	 */
	@Override
	public void onClick(View src) {
		switch (src.getId()) {
		case R.id.buttonStart:
			Log.d(TAG, "starting nexsight service"); 
			if (!startedService) 
				startService(new Intent(this, Nexsight.class));
			startedService = true;
			break;
		case R.id.buttonStop:
			Log.d(TAG, "stopping nexsight service");
			if (startedService) 
				stopService(new Intent(this, Nexsight.class));
			startedService = false;
			break;
		}
	}
}