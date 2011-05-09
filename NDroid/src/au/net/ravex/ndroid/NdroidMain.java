package au.net.ravex.ndroid;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import org.json.JSONException;

import android.app.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class NdroidMain extends Activity implements OnClickListener,
		OnInitListener {
	private static final String TAG = "Ndroid";
	boolean startedService = false;
	private Timer loopTimer;
	private ImageAdapter iAdapt;
	private Editor edit;

	Button buttonStart, buttonStop, buttonTalk, buttonGo, buttonGrid;
	TextView texty;
	Gallery g;
	ProgressBar pg;

	private static final String KEY_STATE = "NS_STARTED";
	private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;
	private static final int TTS_REQUEST_CODE = 1235;
	private static final String HELLO = "Online";
	private TextToSpeech tts;
	private static SharedPreferences prefs;

	/** called after init **/
	@Override
	public void onInit(int status) {
		//tts.setLanguage(Locale.US);
		//tts.speak(HELLO, TextToSpeech.QUEUE_FLUSH, null);
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.main);

		Log.d(TAG, "Created view... binding buttons");

		buttonStart = (Button) findViewById(R.id.buttonStart);
		buttonStop = (Button) findViewById(R.id.buttonStop);
		//buttonTalk = (Button) findViewById(R.id.buttonTalk);
		buttonGo = (Button) findViewById(R.id.buttonGo);
		buttonGrid = (Button) findViewById(R.id.buttonGrid);
		texty = (TextView) findViewById(R.id.textStat);
		pg = (ProgressBar) findViewById(R.id.pg);

		buttonStart.setOnClickListener(this);
		buttonStop.setOnClickListener(this);
		buttonGo.setOnClickListener(this);
		buttonGrid.setOnClickListener(this);

		// Setup tts
		Intent checkIntent = new Intent();
		checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
		startActivityForResult(checkIntent, TTS_REQUEST_CODE);

		// get prefs and setup
		prefs = getPreferences(MODE_WORLD_WRITEABLE);
		updateStatus();

		// gallery
		g = (Gallery) findViewById(R.id.galleryView);
		iAdapt = new ImageAdapter(this, NdroidHelper.grabFiles());
		g.setAdapter(iAdapt);

		// Check to see if a recognition activity is present
		/*
		PackageManager pm = getPackageManager();
		List activities = pm.queryIntentActivities(new Intent(
				RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
		if (activities.size() != 0) {
			buttonTalk.setOnClickListener(this);
			Log.d("Recognised speech engine", "");
		} else {
			buttonTalk.setEnabled(false);
			buttonTalk.setText("Recognizer not present");
		}
		*/

		// refresh imgs periodically ///FIXME actually refresh vview every now and then
		loopTimer = new Timer();
		loopTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				Log.i(TAG, "Refreshing view");
				step();
			}
		}, 0, 30*1000);
		
	}

	public void step() {
		iAdapt.setList(NdroidHelper.grabFiles());
		//g.refreshDrawableState();
		//g.forceLayout();
		g.postInvalidate();
	}

	public class ImageAdapter extends BaseAdapter {
		int mGalleryItemBackground;
		private Context mContext;
		private List files;

		public ImageAdapter(Context c, List files) {
			mContext = c;
			this.files = files;
		}

		public void setList(List files) {
			this.files = files;
		}

		public int getCount() {
			return files.size();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			Bitmap bmap = NdroidHelper.obtainPreviewMap(files.get(position).toString(), 4);
			try {
				ImageView i = new ImageView(mContext);
				i.setImageBitmap(bmap);
				i.setScaleType(ImageView.ScaleType.FIT_XY);
				return i;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
			
		}
	}
	
	private void updateStatus() {
		if (prefs.getBoolean(KEY_STATE, false)) {
			texty.setText("recording to /sdcard/nexsight");
			pg.setVisibility(View.VISIBLE);
			texty.setTextColor(Color.WHITE);
		} else {
			texty.setText("inactive");
			pg.setVisibility(View.INVISIBLE);
			texty.setTextColor(Color.GRAY);
		}
	}

	/**
	 * on click handler for button
	 */
	@Override
	public void onClick(View src) {
		switch (src.getId()) {
		case R.id.buttonStart:
			Log.d(TAG, "starting nexsight service");
			edit = prefs.edit();
			edit.putBoolean(KEY_STATE, true);
			edit.commit();
			updateStatus();
			startService(new Intent(this, Nexsight.class));
			break;
		case R.id.buttonStop:
			Log.d(TAG, "stopping nexsight service");
			edit = prefs.edit();
			edit.putBoolean(KEY_STATE, false);
			edit.commit();
			updateStatus();
			stopService(new Intent(this, Nexsight.class));
			break;
		//case R.id.buttonTalk:
			//startVoiceRecognitionActivity();
			//break;
		case R.id.buttonGo:
			Intent myIntent = new Intent(Intent.ACTION_VIEW,
					Uri.parse("http://nexos.ravex.net.au"));
			startActivity(myIntent);
			break;
		case R.id.buttonGrid:
			Intent myIntent2 = new Intent(getApplicationContext(), NdroidImages.class);
            startActivityForResult(myIntent2, 0);
			break;
		}
	}

	/**
	 * Fire an intent to start the speech recognition activity.
	 */
	private void startVoiceRecognitionActivity() {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Talk to XNET");
		startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
	}

	/**
	 * Handle the results from the recognition activity.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == VOICE_RECOGNITION_REQUEST_CODE
				&& resultCode == RESULT_OK) {
			try {
				String toTalk = XDroid.getXnetResponse(data);
				Log.i(TAG, "going to speak: " + toTalk);
				tts.speak(toTalk, TextToSpeech.QUEUE_FLUSH, null);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (requestCode == TTS_REQUEST_CODE) {
			if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
				// success, create the TTS instance
				tts = new TextToSpeech(this, this);
			} else {
				// missing data, install it
				Intent installIntent = new Intent();
				installIntent
						.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
				startActivity(installIntent);
			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onDestroy() {
		//tts.shutdown();
		loopTimer.cancel();
		super.onDestroy();
	}
}