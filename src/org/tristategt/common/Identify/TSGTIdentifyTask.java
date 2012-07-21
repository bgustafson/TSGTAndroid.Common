package org.tristategt.common.Identify;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.tristategt.common.R;

import com.esri.android.action.IdentifyResultSpinner;
import com.esri.android.map.Layer;
import com.esri.android.map.MapView;
import com.esri.core.geometry.Point;
import com.esri.core.tasks.ags.identify.IdentifyParameters;
import com.esri.core.tasks.ags.identify.IdentifyResult;
import com.esri.core.tasks.ags.identify.IdentifyTask;

import android.app.Dialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class TSGTIdentifyTask extends AsyncTask<IdentifyParameters, Void, IdentifyResult[]> {
	IdentifyTask mIdentifyTask;
	Layer identifyLayer;
	MapView mMapView;
	Point mAnchor;
	IdentifyResult result;
	
	TSGTIdentifyTask(Point anchorPoint, MapView mMapView, Layer identifyLayer) {
		this.mAnchor = anchorPoint;
		this.identifyLayer = identifyLayer;
		this.mMapView = mMapView;
	}
	
	@Override
	protected void onPreExecute() {
		mIdentifyTask = new IdentifyTask(identifyLayer.getUrl());
	}
	
		
	@Override
	protected IdentifyResult[] doInBackground(IdentifyParameters... params) {
		IdentifyResult[] mResult = null;
		if (params != null && params.length > 0) {
			IdentifyParameters mParams = params[0];
			try {
				mResult = mIdentifyTask.execute(mParams);
			} catch (Exception e) {
				e.printStackTrace();
			}
				
		}
		return mResult;
	}
		
	@Override
	protected void onPostExecute(IdentifyResult[] results) {
		
		if(results.length == 0){
			Toast toast = Toast.makeText(mMapView.getContext(), "No features found.", Toast.LENGTH_LONG);
			toast.setGravity(Gravity.BOTTOM, 0, 0);
			toast.show();
			mMapView.getCallout().hide();
			return;
		}
							
		ArrayList<IdentifyResult> resultList = new ArrayList<IdentifyResult>();
		for (int index=0; index < results.length; index++){
				
			if(results[index].getAttributes().get(results[index].getDisplayFieldName())!=null)
				resultList.add(results[index]);
		}
			
		mMapView.getCallout().show(mAnchor, createIdentifyContent(resultList));
	}
	
	private ViewGroup createIdentifyContent(final List<IdentifyResult> results){

        LinearLayout layout = new LinearLayout(mMapView.getContext());
        layout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        layout.setOrientation(LinearLayout.VERTICAL);
	        
        IdentifyResultSpinner spinner = new IdentifyResultSpinner(mMapView.getContext(), (List<IdentifyResult>) results);
        spinner.setClickable(true);  
        IdentifyAdapter adapter = new IdentifyAdapter(mMapView.getContext(), results);
        spinner.setAdapter(adapter);
        spinner.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));   
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View v, int arg2, long arg3) {
				//set the result
				result = results.get(arg2);
				Map<String, Object> atts = result.getAttributes();
				Set<Entry<String, Object>> set = atts.entrySet();
				
				LinearLayout layout = new LinearLayout(mMapView.getContext());
		        layout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		        layout.setOrientation(LinearLayout.VERTICAL);	
		        
		        LinearLayout innerLayout = new LinearLayout(mMapView.getContext());
		        innerLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		        innerLayout.setOrientation(LinearLayout.VERTICAL);
		        //innerLayout.setBackgroundResource(R.drawable.corkboard);
		        ScrollView sv = new ScrollView(mMapView.getContext());
				
				//fill in custom list view layout
				for(Entry<?, ?> e : set){
					String key = e.getKey().toString();
					String value = e.getValue().toString();
					value = key + ": " + value;
					ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
					HashMap<String, String> map = new HashMap<String, String>();
					map.put(key, value);
					mylist.add(map);
					
					SimpleAdapter data = new SimpleAdapter(mMapView.getContext(), mylist, R.layout.identifyrowlayout,
				            new String[] {key, value}, new int[] {R.id.identify_column, R.id.identify_value});
					
					ListView lv = new ListView(mMapView.getContext());
					lv.setAdapter(data);
					innerLayout.addView(lv);
				}
				
				sv.addView(innerLayout);
						        
		        layout.addView(sv);
		        
		        Dialog d = new Dialog(mMapView.getContext());
		        d.setTitle("Identify Result");
		        d.setContentView(layout);
		 
		        d.show();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				result = null;				
			}
        	
		});
               
        TextView tv = new TextView(mMapView.getContext());
        tv.setText("Identify Results");
        tv.setTextColor(Color.WHITE);
        tv.setLayoutParams(new ListView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        tv.setPadding(1, 0, 0, 3);
        layout.addView(tv);
        layout.addView(spinner);
	        
        return layout;
	}
}
