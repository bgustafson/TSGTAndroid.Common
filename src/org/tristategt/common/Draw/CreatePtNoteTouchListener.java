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
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapOnTouchListener;
import com.esri.android.map.MapView;
import com.esri.core.geometry.Point;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.PictureMarkerSymbol;

public class CreatePtNoteTouchListener extends MapOnTouchListener {

	GraphicsLayer graphicsLayer;
	MapView mMapView;
	Point pt;
	Drawable drawable;
	String note = "";
	PictureMarkerSymbol m;
	FeaturesDBAdapter dbAdapter;
	FragmentManager fm;
	
	public CreatePtNoteTouchListener(Context context, MapView view, GraphicsLayer gLayer, Drawable drawable, FeaturesDBAdapter dbAdapter, FragmentManager fm) {
		super(context, view);
		
		this.mMapView = view;
		this.graphicsLayer = gLayer;
		this.drawable = drawable;
		this.dbAdapter = dbAdapter;
		this.fm = fm;
	}
	
	@Override
	public boolean onSingleTap(MotionEvent e){
		m = new PictureMarkerSymbol(drawable);
		pt = new Point(e.getX(), e.getY());
		pt = mMapView.toMapPoint(pt);
		
		//need result from dialog to finish adding pt
		LayoutInflater factory = LayoutInflater.from(mMapView.getContext());
        final View textEntryView = factory.inflate(R.layout.save_dialog_layout, null);
        
        new AlertDialog.Builder(mMapView.getContext())
            .setTitle(R.string.titleSaveDialog)
            .setView(textEntryView)
            .setPositiveButton(R.string.titleSave, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {      		
                	EditText et = (EditText) textEntryView.findViewById(R.id.note_edit);
                	note = et.getText().toString();
                	            	
            		dbAdapter.open();
            		long rowid = dbAdapter.insertGraphic(pt.getX(), pt.getY(), note, "Point", "");
            		dbAdapter.close();
            		
            		HashMap<String, Object> attributeMap = new HashMap<String, Object>();
            		attributeMap.put("note", note);
            		attributeMap.put("rowid", rowid);
            		
            		Graphic graphic = new Graphic(pt, m, attributeMap, null);
            		graphicsLayer.addGraphic(graphic);
                }
                
            }).show();
						
		return true;
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
			myDialog.setDrawable(drawable);
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
