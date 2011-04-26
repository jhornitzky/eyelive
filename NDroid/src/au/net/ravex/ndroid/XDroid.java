package au.net.ravex.ndroid;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.content.Intent;
import android.speech.RecognizerIntent;
import android.util.Log;

public class XDroid {
	private static final String XNET_URL = "http://nexos.ravex.net.au/xi/handler.php";
	private static final String CALLER = "xdroid";
	private static final String BOT_NAME = "TestBot"; //FIXME xnet
	
	public static String getXnetResponse(Intent data) {
		// Fill the list view with the strings the recognizer thought it
		// could have heard
		ArrayList<String> matches = data
				.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

		Log.d("MATCHED WORD", matches.get(0));
		String toSend = matches.get(0).replace(" ", "+"); //Need to strip illegal chars
		toSend = toSend.replace("#", ""); // cleanse
		
		// Go and talk to XNET now
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpPost httpost = new HttpPost(XNET_URL);
		
		// Add your data    
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();    
		nameValuePairs.add(new BasicNameValuePair("action", "talk")); 
		nameValuePairs.add(new BasicNameValuePair("botname", BOT_NAME));      
		nameValuePairs.add(new BasicNameValuePair("input", toSend));      
		nameValuePairs.add(new BasicNameValuePair("caller", CALLER)); 
		
		try {
			httpost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			Log.d("HTTP POST", httpost.getURI().toString());
			
			try {
				HttpResponse response = httpclient.execute(httpost);
				Log.d("HTTP POST RESP",response.toString());
				Log.d("HTTP POST STATUS",String.valueOf(response.getStatusLine().getStatusCode()));
				Log.d("HTTP POST REASON",response.getStatusLine().getReasonPhrase());
				
				HttpEntity entity = response.getEntity();
				String toTalk = convertStreamToString(entity.getContent());
				ArrayList<String> array = new ArrayList<String>();
				array.add(toTalk);
				Log.d("XNET RESPONSE",toTalk);
				entity.consumeContent();
				return toTalk;
			} catch (ClientProtocolException e) {
				Log.e("CPE", e.getMessage());
			} catch (IOException e) {
				Log.e("IOE", e.getMessage());
			}
		} catch (UnsupportedEncodingException e1) {
			Log.e("UEE", e1.getMessage());
		}    
		return null;
	}
	
	public static String convertStreamToString(InputStream is) throws IOException {
		/*
		 * To convert the InputStream to String we use the
		 * BufferedReader.readLine() method. We iterate until the BufferedReader
		 * return null which means there's no more data to read. Each line will
		 * appended to a StringBuilder and returned as String.
		 */
		if (is != null) {
			StringBuilder sb = new StringBuilder();
			String line;

			try {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is, "UTF-8"));
				while ((line = reader.readLine()) != null) {
					sb.append(line).append("\n");
				}
			} finally {
				is.close();
			}
			return sb.toString();
		} else {
			return "";
		}
	}
}
