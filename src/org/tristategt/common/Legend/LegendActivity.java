package org.tristategt.common.Legend;

import java.util.List;

import org.tristategt.common.R;

import com.esri.android.map.ags.ArcGISDynamicMapServiceLayer;
import com.esri.android.map.ags.ArcGISLayerInfo;
import com.esri.core.map.Legend;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class LegendActivity extends Activity {
	ListView listView;
	String uri;
	/*
	 Intent intent = new Intent(getBaseContext(), LegendActivity.class);
	 intent.putExtra("uri", webUri);
	 startActivity(intent)
	 */
	
	//Set the source to the image from the rest end point
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.legend);
		savedInstanceState.getString("uri");
		listView = (ListView)findViewById(R.id.legendItems);
		LayoutParams params = new LayoutParams(
	            LayoutParams.MATCH_PARENT,
	            LayoutParams.WRAP_CONTENT);
		
		ArcGISLayerInfo[] layerInfos = new ArcGISDynamicMapServiceLayer(savedInstanceState.getString("uri")).getLayers();
		
		for(ArcGISLayerInfo layerInfo : layerInfos){
			List<Legend> legends = layerInfo.getLegend();
			
			for(Legend legend : legends){
				String label = legend.getLabel();
				Bitmap bitmap = legend.getImage();
				
				TextView textView = new TextView(this);
				SpannableString content = new SpannableString(label);
			    content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
				textView.setText(content);
				textView.setTextSize(10);
				textView.setTextColor(Color.WHITE);
				textView.setLayoutParams(params);
				listView.addView(textView);
				
				ImageView imageView = new ImageView(this);
				imageView.setImageBitmap(bitmap);
				imageView.setLayoutParams(params);
				listView.addView(imageView);
			}		
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
	
}
