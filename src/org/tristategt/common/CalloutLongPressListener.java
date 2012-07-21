package org.tristategt.common;

import android.view.View;
import android.widget.TextView;

import com.esri.android.map.Callout;
import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapView;
import com.esri.android.map.event.OnLongPressListener;
import com.esri.core.geometry.Point;
import com.esri.core.map.Graphic;

public class CalloutLongPressListener implements OnLongPressListener {

	private static final long serialVersionUID = 1L;
	MapView mMapView;
	GraphicsLayer graphicsLayer;
	View content;
	Callout callout;
	
	public CalloutLongPressListener(View content, Callout callout, GraphicsLayer graphicsLayer, MapView mMapView){
		super();
		
		this.graphicsLayer = graphicsLayer;
		this.content = content;
		this.callout = callout;
		this.mMapView = mMapView;
		callout.setStyle(R.xml.calloutstyle);
	}

	@Override
	public void onLongPress(float x, float y) {				
		int[] graphicIDs = graphicsLayer.getGraphicIDs(x, y, 25); 
		if (graphicIDs != null && graphicIDs.length > 0) {
			Graphic gr = graphicsLayer.getGraphic(graphicIDs[0]);
			updateContent((String) gr.getAttributeValue("note"));			
			Point location = mMapView.toMapPoint(x, y);
			callout.setOffset(0, -15);
			callout.show(location, content);
		}	
	}
	
	public void updateContent(String note) {
		if (content == null)
			return;
		
		TextView txt = (TextView) content.findViewById(1);
		txt.setText(note);
	}
}
