package org.tristategt.common.Draw;

import org.tristategt.common.GenericMapTouchListener;

import android.content.Context;
import android.graphics.Color;
import android.view.MotionEvent;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapOnTouchListener;
import com.esri.android.map.MapView;
import com.esri.core.geometry.MultiPath;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.geometry.Polyline;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol;

public class DrawTouchListener extends MapOnTouchListener {
	
	String type = "";
	Point startPoint = null;
	MultiPath multiPath;
	int polyUID = 0;
	GraphicsLayer graphicsLayer;
	MapView mMapView;
	
	public DrawTouchListener(Context context, MapView view, GraphicsLayer gLayer) {
		super(context, view);
		
		this.mMapView = view;
		this.graphicsLayer = gLayer;
	}

	public void setType(String geometryType) {
		this.type = geometryType;
	}

	public String getType() {
		return this.type;
	}
			
	public boolean onSingleTap(MotionEvent e) {
		if (type.length() > 1 && type.equalsIgnoreCase("POINT")) {
			SimpleMarkerSymbol m = new SimpleMarkerSymbol(Color.BLUE, 15, SimpleMarkerSymbol.STYLE.CIRCLE);
			Graphic graphic = new Graphic(mMapView.toMapPoint(new Point(e.getX(), e.getY())), m);
			graphicsLayer.addGraphic(graphic);
			
			return true;
		}else if (type.length() > 1 && (type.equalsIgnoreCase("POLYLINE") || type.equalsIgnoreCase("POLYGON"))) {
			
			Point mapPt = mMapView.toMapPoint(e.getX(), e.getY());
			if (startPoint == null) {
				multiPath = type.equalsIgnoreCase("POLYLINE") ? new Polyline() : new Polygon();
				startPoint = mMapView.toMapPoint(e.getX(), e.getY());
				multiPath.startPath((float) startPoint.getX(), (float) startPoint.getY());

				Graphic graphic = new Graphic(startPoint, new SimpleMarkerSymbol(Color.BLUE, 10, SimpleMarkerSymbol.STYLE.CIRCLE));

				graphicsLayer.addGraphic(graphic);
			}

			multiPath.lineTo((float) mapPt.getX(), (float) mapPt.getY());
						
			if(multiPath.getType().name().equalsIgnoreCase("POLYLINE")){
				graphicsLayer.addGraphic(new Graphic((Polyline)multiPath, new SimpleLineSymbol(Color.BLACK, 3)));	
			}else if(multiPath.getType().name().equalsIgnoreCase("POLYGON")){
				SimpleFillSymbol sfs = new SimpleFillSymbol(Color.BLUE);
				sfs.setAlpha(70);
				sfs.setOutline(new SimpleLineSymbol(Color.BLACK, 1));
				
				if(polyUID == 0){
					Graphic g = new Graphic((Polygon)multiPath, sfs);
					polyUID = graphicsLayer.addGraphic(g);		
				}else{
					graphicsLayer.removeGraphic(polyUID);
					Graphic g = new Graphic((Polygon)multiPath, sfs);
					polyUID = graphicsLayer.addGraphic(g);	
				}
			}
				
			graphicsLayer.addGraphic(new Graphic(new Point((float) mapPt.getX(), (float) mapPt.getY()), new SimpleMarkerSymbol(Color.BLUE, 10, SimpleMarkerSymbol.STYLE.CIRCLE)));
	
			return true;
		}
		return false;
	}
	
	public boolean onDoubleTap(MotionEvent point) {
		startPoint = null;
		polyUID = 0;
		mMapView.setOnTouchListener(new GenericMapTouchListener(mMapView.getContext(), mMapView));
		return true;
	}

	public boolean onDragPointerMove(MotionEvent from, MotionEvent to) {
		if (type.length() > 1 && (type.equalsIgnoreCase("Freehand Line") || type.equalsIgnoreCase("Freehand Polygon"))) {

			Point mapPt = mMapView.toMapPoint(to.getX(), to.getY());

			if (startPoint == null) {
				multiPath = type.equalsIgnoreCase("Freehand Line") ? new Polyline() : new Polygon();
				startPoint = mMapView.toMapPoint(from.getX(), from.getY());
				multiPath.startPath((float) startPoint.getX(), (float) startPoint.getY());

				Graphic graphic = new Graphic(startPoint,new SimpleLineSymbol(Color.BLACK, 3));
				graphicsLayer.addGraphic(graphic);
			}

			multiPath.lineTo((float) mapPt.getX(), (float) mapPt.getY());
			
			return true;
		}
		return super.onDragPointerMove(from, to);
	}
	
	@Override
	public boolean onDragPointerUp(MotionEvent from, MotionEvent to) {
		if (type.length() > 1 && (type.equalsIgnoreCase("Freehand Line") || type.equalsIgnoreCase("Freehand Polygon"))) {


			if (type.equalsIgnoreCase("Freehand Polygon")) {
				multiPath.lineTo((float) startPoint.getX(), (float) startPoint.getY());
				
				SimpleFillSymbol sfs = new SimpleFillSymbol(Color.BLUE);
				sfs.setAlpha(70);
				sfs.setOutline(new SimpleLineSymbol(Color.BLACK, 1));

				graphicsLayer.addGraphic(new Graphic(multiPath, sfs));
			}
			graphicsLayer.addGraphic(new Graphic(multiPath,new SimpleLineSymbol(Color.BLACK, 3)));
			startPoint = null;
			mMapView.setOnTouchListener(new GenericMapTouchListener(mMapView.getContext(), mMapView));
			return true;
		}
		return super.onDragPointerUp(from, to);
	}
}
