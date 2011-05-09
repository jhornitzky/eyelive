package au.net.ravex.ndroid;

import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class NdroidImages extends Activity implements OnClickListener {
	Button buttonMain;
	
	
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.images);
	    
	    buttonMain = (Button) findViewById(R.id.buttonMain);
		buttonMain.setOnClickListener(this);
	    
	    GridView gridview = (GridView) findViewById(R.id.gridview);
	    gridview.setAdapter(new ImageAdapter(this, NdroidHelper.grabFiles()));
	}
	
	public class ImageAdapter extends BaseAdapter {
		int mGalleryItemBackground;
		private Context mContext;
		private List files;
		private String currDate = null;
		int decrement = 0;

		public ImageAdapter(Context c, List files) {
			mContext = c;
			this.files = files;
		}

		public void setList(List files) {
			this.files = files;
			decrement = 0;
			currDate = null;
		}

		private static final int LIMIT = 3000;
		
		public int getCount() {
			if(files.size() > LIMIT)
				return LIMIT;
			else 
				return files.size();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			String fname = files.get(position + decrement).toString();
			View v;
			LayoutInflater li = getLayoutInflater();
			/*
			if (position == 0 || !NdroidHelper.isSameDay(NdroidHelper.extractDateFromFName(currDate), NdroidHelper.extractDateFromFName(fname))) {
				currDate = fname;
				v = li.inflate(R.layout.titlesq, null);
				TextView tv = (TextView)v.findViewById(R.id.title_text);
				tv.setText(NdroidHelper.getDateFromName(fname));
				//decrement--;
			} else {
				Bitmap bmap = NdroidHelper.obtainPreviewMap(fname);
				v = li.inflate(R.layout.icon, null);
				//TextView tv = (TextView)v.findViewById(R.id.icon_text);
				//tv.setText(NdroidHelper.getDateFromName(fname));
				ImageView iv = (ImageView)v.findViewById(R.id.icon_image);
				iv.setImageBitmap(bmap);
			}
			*/
			Bitmap bmap = NdroidHelper.obtainPreviewMap(fname);
			v = li.inflate(R.layout.icon, null);
			TextView tv = (TextView) v.findViewById(R.id.icon_text);
			if (position == 0 || !NdroidHelper.isSameDay(NdroidHelper.extractDateFromFName(currDate), NdroidHelper.extractDateFromFName(fname))) {
				currDate = fname;
				tv.setText(NdroidHelper.getDateFromName(fname));
			} else {
				tv.setText("");
			}
			ImageView iv = (ImageView)v.findViewById(R.id.icon_image);
			iv.setImageBitmap(bmap);
	        
	        return v;
		}
	}
	
	 
	/**
	 * on click handler for button
	 */
	@Override
	public void onClick(View src) {
		switch (src.getId()) {
		case R.id.buttonMain:
			Intent myIntent2 = new Intent(getApplicationContext(), NdroidMain.class);
            startActivityForResult(myIntent2, 0);
			break;
		}
	}
}
