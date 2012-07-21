package org.tristategt.common.Measure;

import java.text.DecimalFormat;
import java.util.List;

import org.tristategt.common.GenericMapTouchListener;

import android.content.Context;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapOnTouchListener;
import com.esri.android.map.MapView;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.MultiPath;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.geometry.Polyline;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol;

public class MeasureTouchListener extends MapOnTouchListener {
	String type = "";
	Point startPoint = null;
	MultiPath poly;
	int polyUID = 0;
	Context context;
	GraphicsLayer graphicsLayer;
	MapView mMapView;
	TextView resultText;
	LinearLayout layout;
	double result = 0;
	List<Integer> graphicIDs;
	double area;
	double length;
	DecimalFormat df;
	
	public double getArea() {
		return area;
	}

	public double getLength() {
		return length;
	}

	public void setType(String measurementType) {
		this.type = measurementType;
	}

	public String getType() {
		return this.type;
	}
	
	public MeasureTouchListener(Context context, MapView view, TextView tv, LinearLayout layout, GraphicsLayer gLayer) {
		super(context, view);
		
		this.context = context;
		this.graphicsLayer = gLayer;
		this.mMapView = view;
		this.resultText = tv;
		this.layout = layout;
		df = new DecimalFormat("0.0000");
	}
	
	public boolean onSingleTap(MotionEvent e) {
				
		if (type.length() > 1 && (type.equalsIgnoreCase("Length"))) {
			Point mapPt = mMapView.toMapPoint(e.getX(), e.getY());
			if (startPoint == null) {
				if(graphicsLayer.getNumberOfGraphics() > 0)
					graphicsLayer.removeAll();
				
				poly = new Polyline();
				startPoint = mMapView.toMapPoint(e.getX(), e.getY());
				poly.startPath((float) startPoint.getX(), (float) startPoint.getY());

				Graphic graphic = new Graphic(startPoint, new SimpleMarkerSymbol(Color.RED, 10, SimpleMarkerSymbol.STYLE.CIRCLE));
				graphicsLayer.addGraphic(graphic);
			}

			poly.lineTo((float) mapPt.getX(), (float) mapPt.getY());
			graphicsLayer.addGraphic(new Graphic((Polyline)poly, new SimpleLineSymbol(Color.BLACK, 2)));	
			graphicsLayer.addGraphic(new Graphic(mapPt, new SimpleMarkerSymbol(Color.RED, 10, SimpleMarkerSymbol.STYLE.CIRCLE)));
			
			double d = GeometryEngine.geodesicLength(poly, mMapView.getSpatialReference(), null);
			if(d == 0){
				
			}
			else{
				length = d * 0.00062137119;
			}
		}
		else if(type.length() > 1 && (type.equalsIgnoreCase("Area"))){
			Point mapPt = mMapView.toMapPoint(e.getX(), e.getY());
			if (startPoint == null) {
				if(graphicsLayer.getNumberOfGraphics() > 0)
					graphicsLayer.removeAll();
				
				poly = new Polygon();
				startPoint = mMapView.toMapPoint(e.getX(), e.getY());
				poly.startPath((float) startPoint.getX(), (float) startPoint.getY());

				Graphic graphic = new Graphic(startPoint, new SimpleMarkerSymbol(Color.RED, 10, SimpleMarkerSymbol.STYLE.CIRCLE));
				graphicsLayer.addGraphic(graphic);
			}

			poly.lineTo((float) mapPt.getX(), (float) mapPt.getY());
			
			SimpleFillSymbol sfs = new SimpleFillSymbol(Color.RED);
			sfs.setAlpha(70);
				
			if(polyUID == 0){
				Graphic g = new Graphic((Polygon)poly, sfs);
				polyUID = graphicsLayer.addGraphic(g);		
			}else{
				graphicsLayer.removeGraphic(polyUID);
				Graphic g = new Graphic((Polygon)poly, sfs);
				polyUID = graphicsLayer.addGraphic(g);	
					
				//calculating area
				Polygon p = (Polygon)poly;
				
				p = (Polygon) GeometryEngine.simplify(p, graphicsLayer.getSpatialReference());
				area = p.calculateArea2D();
				area = area/1000000;
				area = area * 0.38610215854;	
			}
			graphicsLayer.addGraphic(new Graphic(new Point((float) mapPt.getX(), (float) mapPt.getY()), new SimpleMarkerSymbol(Color.RED, 10, SimpleMarkerSymbol.STYLE.CIRCLE)));
		}
					
		return true;
	}
	
	public boolean onDoubleTap(MotionEvent point) {
		mMapView.setOnTouchListener(new GenericMapTouchListener(mMapView.getContext(), mMapView));
		
		startPoint = null;
		polyUID = 0;
		
		if (type.equalsIgnoreCase("Length")){
			resultText.setText("Miles: " + df.format(length));
			layout.setVisibility(View.VISIBLE);
		}else if(type.equalsIgnoreCase("Area")){
			resultText.setText("Sq Miles: " + df.format(area));
			layout.setVisibility(View.VISIBLE);
		}		
		
		return true;
	}
}
