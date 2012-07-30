package org.tristategt.common.Legend;

import java.util.ArrayList;
import java.util.List;

import org.tristategt.common.R;

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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class LegendActivity extends Activity {
	ListView listView;
	String uri;

	//Set the source to the image from the rest end point
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.legend);
		listView = (ListView)findViewById(R.id.legendItems);
		LayoutParams params = new LayoutParams(
	            LayoutParams.MATCH_PARENT,
	            LayoutParams.WRAP_CONTENT);
		
		Bundle extras = getIntent().getExtras();
		ArrayList<ArcGISLayerInfo> layerInfos = (ArrayList<ArcGISLayerInfo>) extras.getSerializable("uri");	

		for(ArcGISLayerInfo layerInfo : layerInfos){
			List<Legend> legends = layerInfo.getLegend();
			
			for(Legend legend : legends){
				String label = legend.getLabel();
				Bitmap bitmap = legend.getImage();
				
				LinearLayout linearLayout = new LinearLayout(this);
				
				TextView textView = new TextView(this);
				textView.setLayoutParams(params);
				SpannableString content = new SpannableString(label);
			    content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
				textView.setText(content);
				textView.setTextSize(10);
				textView.setTextColor(Color.WHITE);			
				linearLayout.addView(textView);
				
				ImageView imageView = new ImageView(this);
				imageView.setLayoutParams(params);
				imageView.setImageBitmap(bitmap);
				linearLayout.addView(imageView);
				
				listView.addView(linearLayout);
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
