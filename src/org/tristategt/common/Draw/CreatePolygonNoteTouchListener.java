package org.tristategt.common.Draw;

import java.util.HashMap;

import org.tristategt.common.R;
import org.tristategt.common.DBAction.FeaturesDBAdapter;
import org.tristategt.common.Dialogs.EditNoteDialog;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapOnTouchListener;
import com.esri.android.map.MapView;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.symbol.SimpleLineSymbol;


public class CreatePolygonNoteTouchListener extends MapOnTouchListener {

	
	GraphicsLayer graphicsLayer, scratch;
	MapView mMapView;
	String note = "";
	FeaturesDBAdapter dbAdapter;
	FragmentManager fm;
	
	Point startPoint = null;
	Polygon poly;
	int polyUID = 0;
	
	public CreatePolygonNoteTouchListener(Context context, MapView view, GraphicsLayer gLayer, GraphicsLayer gScratchLayer, FeaturesDBAdapter dbAdapter, FragmentManager fm) {
		super(context, view);
		
		this.mMapView = view;
		this.graphicsLayer = gLayer;
		this.scratch = gScratchLayer;
		this.dbAdapter = dbAdapter;
		this.fm = fm;
	}
	
	@Override
	public boolean onSingleTap(MotionEvent e){
		Point mapPt = mMapView.toMapPoint(e.getX(), e.getY());
		if (startPoint == null) {
			poly = new Polygon();
			startPoint = mMapView.toMapPoint(e.getX(), e.getY());
			poly.startPath((float) startPoint.getX(), (float) startPoint.getY());
		}

		poly.lineTo((float) mapPt.getX(), (float) mapPt.getY());
					
		SimpleFillSymbol sfs = new SimpleFillSymbol(Color.BLUE);
		sfs.setAlpha(70);
		sfs.setOutline(new SimpleLineSymbol(Color.BLACK, 1));
			
		if(polyUID == 0){
			Graphic g = new Graphic(poly, sfs);
			polyUID = scratch.addGraphic(g);		
		}else{
			scratch.removeGraphic(polyUID);
			Graphic g = new Graphic(poly, sfs);
			polyUID = scratch.addGraphic(g);	
		}

		return true;
	}
	
	@Override
	public void onLongPress(MotionEvent point) {
				
		//insert into DB
		LayoutInflater factory = LayoutInflater.from(mMapView.getContext());
        final View textEntryView = factory.inflate(R.layout.save_dialog_layout, null);
        
        new AlertDialog.Builder(mMapView.getContext())
            .setTitle(R.string.titleSaveDialog)
            .setView(textEntryView)
            .setOnCancelListener(new OnCancelListener() {			
				public void onCancel(DialogInterface dialog) {
					scratch.removeAll();
				}
			})
            .setPositiveButton(R.string.titleSave, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {      		
                	EditText et = (EditText) textEntryView.findViewById(R.id.note_edit);
                	note = et.getText().toString();
                	
                	//create string of value
                	String values = "";
                	Polygon polygon = poly;
                	int iCount = polygon.getPointCount();
                	int i = 0;
                	
                	while(i < iCount){
                		Point p = polygon.getPoint(i);
                		values  = values + p.getX() + ":" + p.getY() + ",";
                		i++;
                	}
                	                	            	
            		dbAdapter.open();
            		long rowid = dbAdapter.insertGraphic(0.0, 0.0, note, "Polygon", values);
            		dbAdapter.close();
            		
            		HashMap<String, Object> attributeMap = new HashMap<String, Object>();
            		attributeMap.put("note", note);
            		attributeMap.put("rowid", rowid);
            		
            		SimpleFillSymbol sfs = new SimpleFillSymbol(Color.BLUE);
    				sfs.setAlpha(70);
    				sfs.setOutline(new SimpleLineSymbol(Color.BLACK, 1));
            		
            		Graphic graphic = new Graphic(polygon, sfs, attributeMap, null);
            		graphicsLayer.addGraphic(graphic);
            		scratch.removeAll();
                }
                
            }).show();
        	
        
        startPoint = null;
		polyUID = 0;
	}
	
	@Override
	public boolean onDoubleTap(MotionEvent e) {
		
		//get points under the click 
		int[] graphicIDs = graphicsLayer.getGraphicIDs(e.getX(), e.getY(), 25); 
		if (graphicIDs != null && graphicIDs.length > 0) {
			//show a dialog to either edit note or delete point			
			FragmentTransaction ft = fm.beginTransaction(); 
			EditNoteDialog myDialog = EditNoteDialog.newInstance("Message");
			myDialog.setDbAdapter(dbAdapter);
			myDialog.setGraphicsLayer(graphicsLayer);
			myDialog.setmMapView(mMapView);
			myDialog.setX(e.getX());
			myDialog.setY(e.getY());
			myDialog.setRowid(Long.valueOf(graphicsLayer.getGraphic(graphicIDs[0]).getAttributeValue("rowid").toString()));
			myDialog.setGid(graphicsLayer.getGraphic(graphicIDs[0]).getUid());
			myDialog.show(ft, "");
		}	
						
		return true;
	}
}
