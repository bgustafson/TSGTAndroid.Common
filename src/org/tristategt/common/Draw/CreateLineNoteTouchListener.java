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
import com.esri.core.geometry.MultiPath;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polyline;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol;

public class CreateLineNoteTouchListener extends MapOnTouchListener {
	
	GraphicsLayer graphicsLayer, scratch;
	MapView mMapView;
	String note = "";
	FeaturesDBAdapter dbAdapter;
	FragmentManager fm;
	
	Point startPoint = null;
	MultiPath poly;
	
	public CreateLineNoteTouchListener(Context context, MapView view, GraphicsLayer gLayer, GraphicsLayer scratch, FeaturesDBAdapter dbAdapter, FragmentManager fm) {
		super(context, view);
		
		this.mMapView = view;
		this.graphicsLayer = gLayer;
		this.scratch = scratch;
		this.dbAdapter = dbAdapter;
		this.fm = fm;
	}
	
	@Override
	public boolean onSingleTap(MotionEvent e){
		Point mapPt = mMapView.toMapPoint(e.getX(), e.getY());
		if (startPoint == null) {
			poly = new Polyline();
			startPoint = mMapView.toMapPoint(e.getX(), e.getY());
			poly.startPath((float) startPoint.getX(), (float) startPoint.getY());
			
			Graphic graphic = new Graphic(startPoint, new SimpleMarkerSymbol(Color.RED, 10, SimpleMarkerSymbol.STYLE.CIRCLE));
			scratch.addGraphic(graphic);
		}
		
		Graphic graphic = new Graphic(mMapView.toMapPoint(e.getX(), e.getY()), new SimpleMarkerSymbol(Color.RED, 10, SimpleMarkerSymbol.STYLE.CIRCLE));
		scratch.addGraphic(graphic);

		poly.lineTo((float) mapPt.getX(), (float) mapPt.getY());				
		scratch.addGraphic(new Graphic(poly, new SimpleLineSymbol(Color.BLUE, 3)));	
				
		return true;
	}
	
	@Override
	public void onLongPress(MotionEvent point) {
		startPoint = null;
		
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
                	
                	//create string of values
                	String values = "";
                	Polyline line = (Polyline)poly;
                	int iCount = line.getPointCount();
                	int i = 0;
                	
                	while(i < iCount){
                		Point p = line.getPoint(i);
                		values = values + p.getX() + ":" + p.getY() + ",";
                		i++;
                	}
                	                	            	
            		dbAdapter.open();
            		long rowid = dbAdapter.insertGraphic(0.0, 0.0, note, "Line", values);
            		dbAdapter.close();
            		
            		HashMap<String, Object> attributeMap = new HashMap<String, Object>();
            		attributeMap.put("note", note);
            		attributeMap.put("rowid", rowid);
            		
            		scratch.removeAll();	
            		Graphic graphic = new Graphic(line, new SimpleLineSymbol(Color.BLUE, 3), attributeMap, null);
            		graphicsLayer.addGraphic(graphic);
                }
                
            }).show();
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
