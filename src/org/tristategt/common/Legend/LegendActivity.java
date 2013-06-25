package org.tristategt.common.Legend;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.tristategt.common.R;

import com.esri.core.map.Legend;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class LegendActivity extends Activity {
	LinearLayout legendItems;
	LinearLayout linearLayout;
	LayoutParams params;
	String uri;

	//Set the source to the image from the rest end point
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.legend);
		legendItems = (LinearLayout)findViewById(R.id.legendItems);
		params = new LayoutParams(
	            LayoutParams.MATCH_PARENT,
	            LayoutParams.WRAP_CONTENT);
		
		Bundle extras = getIntent().getExtras();
		@SuppressWarnings("unchecked")
		HashMap<String, HashMap<String, String>> legends = (HashMap<String, HashMap<String, String>>) extras.getSerializable("legends");	
							 			
		new BuildLegendsTask().execute(legends);
	}
	
	private void addLayouts(List<View> views){
		for(View child: views){
			legendItems.addView(child);
		}
	}
	
	@Override 
	protected void onDestroy() { 
		super.onDestroy();
	}
	
	@Override
	protected void onPause() {
		super.onPause();		
	}
	
	@Override 	
	protected void onResume() {
		super.onResume();
	}
	
		 
	public class BuildLegendsTask extends AsyncTask<HashMap<String, HashMap<String, String>>, Void, List<View>> {
		
		List<View> myList;
		
		@Override
		protected List<View> doInBackground(HashMap<String, HashMap<String, String>> ... legends) {			
					
			myList = new ArrayList<View>();
			
			for(Map.Entry<String, HashMap<String, String>> legend : legends[0].entrySet()){
				String layerTitle = legend.getKey();
				HashMap<String, String> labels_images_map = legend.getValue();
					
				linearLayout = new LinearLayout(getApplicationContext());
				linearLayout.setOrientation(LinearLayout.VERTICAL);			
					
				TextView textView = new TextView(getApplicationContext());
				textView.setLayoutParams(params);
				SpannableString content = new SpannableString(layerTitle);
				content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
				textView.setText(content);
				textView.setTextSize(19);
				textView.setTypeface(null, Typeface.BOLD);
				textView.setTextColor(Color.WHITE);			
				linearLayout.addView(textView);
					
				for(Map.Entry<String, String> symbol : labels_images_map.entrySet()){	
					String label = symbol.getKey();
					String imageUrl = "http://geowebp.tristategt.org/ArcGIS/rest/services/Android/MapServer/" + symbol.getValue();
											
					LinearLayout itemLayout = new LinearLayout(getApplicationContext());
					itemLayout.setOrientation(LinearLayout.HORIZONTAL);
					itemLayout.setGravity(Gravity.CENTER);
					
					BitmapFactory.Options bmOptions;
					bmOptions = new BitmapFactory.Options();
					bmOptions.inSampleSize = 1;
					Bitmap bm = LoadImage(imageUrl, bmOptions);
										
					ImageView imageView = new ImageView(getApplicationContext());
					imageView.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.LEFT));
					
					Legend l = new Legend(bm, label);
					imageView.setImageBitmap(Bitmap.createScaledBitmap(l.getImage(), 100, 100, false));

					TextView tv_label = new TextView(getApplicationContext());
					tv_label.setLayoutParams(params);
					tv_label.setTextSize(14);
					tv_label.setTextColor(Color.WHITE);
					tv_label.setTypeface(null, Typeface.ITALIC);
					tv_label.setGravity(Gravity.CENTER_VERTICAL);
					tv_label.setText(l.getLabel());
					
					itemLayout.addView(imageView);
					itemLayout.addView(tv_label);
					
					linearLayout.addView(itemLayout);
				}	
				
				myList.add(linearLayout);
			}
				
			return myList;
		}
		
		@Override
	    protected void onPostExecute(List<View> legendToBuild) {    			
			addLayouts(legendToBuild);
	    }
			
		private Bitmap LoadImage(String URL, BitmapFactory.Options options){       
			Bitmap bitmap = null;
			InputStream in = null;       
			    try {
			         in = OpenHttpConnection(URL);
			         bitmap = BitmapFactory.decodeStream(in, null, options);
			         in.close();
			     } catch (IOException e1) {
			     }
			     return bitmap;               
		}
			 
	    private InputStream OpenHttpConnection(String strURL) throws IOException{
	    	InputStream inputStream = null;
			URL url = new URL(strURL);
			URLConnection conn = url.openConnection();

			try{
				HttpURLConnection httpConn = (HttpURLConnection)conn;
				httpConn.setRequestMethod("GET");
				httpConn.connect();

				if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				   inputStream = httpConn.getInputStream();
				}
			}catch (Exception ex){
				Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
			}
			
			return inputStream;
		}
	}
}
