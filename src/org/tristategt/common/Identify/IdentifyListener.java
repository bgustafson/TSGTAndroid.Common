package org.tristategt.common.Identify;

import org.tristategt.common.R;

import android.app.Application;
import android.content.Context;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.esri.android.map.Callout;
import com.esri.android.map.Layer;
import com.esri.android.map.MapOnTouchListener;
import com.esri.android.map.MapView;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.Point;
import com.esri.core.tasks.ags.identify.IdentifyParameters;
import com.esri.core.tasks.ags.identify.IdentifyResult;

public class IdentifyListener extends MapOnTouchListener {

	Context context;
	MapView mMapView;
	Layer identifyLayer;
	IdentifyParameters params;
	IdentifyResult result;
	
	public IdentifyResult getResult() {
		return result;
	}
		
	public IdentifyListener(Context context, MapView view, Layer layer, Application app) {
		super(context, view);
		
		this.mMapView = view;
		this.context = context;
		this.identifyLayer = layer;
		
		params = new IdentifyParameters();
		params.setTolerance(20);
		params.setDPI(96);
		params.setLayerMode(IdentifyParameters.ALL_LAYERS);
	}

	public boolean onSingleTap(MotionEvent point) {
		if(!mMapView.isLoaded() || identifyLayer == null){
			return false;
		}
		
		Callout co = mMapView.getCallout();
		co.setStyle(R.xml.calloutstyle);
		co.setCoordinates(mMapView.toMapPoint(point.getX(), point.getY()));
		
		LinearLayout clayout = new LinearLayout(context);
		
		ProgressBar pBar = new ProgressBar(context);
		TextView tvCo = new TextView(context);
		tvCo.setPadding(2, 15, 0, 0);
		tvCo.setText("Searching...");
		
		clayout.addView(pBar);
		clayout.addView(tvCo);
		co.setContent(clayout);
		co.show();
		
		//establish the identify parameters	
		Point identifyPoint = mMapView.toMapPoint(point.getX(), point.getY());				
		params.setGeometry(identifyPoint);
		params.setSpatialReference(mMapView.getSpatialReference());									
		params.setMapHeight(mMapView.getHeight());
		params.setMapWidth(mMapView.getWidth());
		Envelope env = new Envelope();
		mMapView.getExtent().queryEnvelope(env);
		params.setMapExtent(env);
		
		TSGTIdentifyTask mTask = new TSGTIdentifyTask(identifyPoint, mMapView, identifyLayer);
		mTask.execute(params);		
		
		return true;
	}
	
	public boolean onDoubleTap(MotionEvent point) {
		mMapView.getCallout().hide();
		return true;
	}
	
}
