package org.tristategt.common.Dialogs;

import java.util.HashMap;

import org.tristategt.common.R;
import org.tristategt.common.DBAction.FeaturesDBAdapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapView;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.geometry.Polyline;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.symbol.SimpleLineSymbol;

public class EditNoteDialog extends DialogFragment {
	
	FeaturesDBAdapter dbAdapter;
	public FeaturesDBAdapter getDbAdapter() {
		return dbAdapter;
	}

	public void setDbAdapter(FeaturesDBAdapter dbAdapter) {
		this.dbAdapter = dbAdapter;
	}
	
	GraphicsLayer graphicsLayer;
	public GraphicsLayer getGraphicsLayer() {
		return graphicsLayer;
	}

	public void setGraphicsLayer(GraphicsLayer graphicsLayer) {
		this.graphicsLayer = graphicsLayer;
	}
		
	Drawable drawable;
	public Drawable getDrawable() {
		return drawable;
	}

	public void setDrawable(Drawable drawable) {
		this.drawable = drawable;
	}
	
	MapView mMapView;
	public MapView getmMapView() {
		return mMapView;
	}

	public void setmMapView(MapView mMapView) {
		this.mMapView = mMapView;
	}
	
	float X;
	public float getX() {
		return X;
	}

	public void setX(float x) {
		X = x;
	}
	
	float Y;
	public float getY() {
		return Y;
	}

	public void setY(float y) {
		Y = y;
	}

	Integer gid;
	public Integer getGid() {
		return gid;
	}

	public void setGid(Integer gid) {
		this.gid = gid;
	}
	
	long rowid;
	public long getRowid() {
		return rowid;
	}

	public void setRowid(long rowid) {
		this.rowid = rowid;
	}

	final String[] actionTypes = new String[] { "Edit Note", "Delete Feature"};

	public static EditNoteDialog newInstance(String message){		
		EditNoteDialog myDialog = new EditNoteDialog();
		Bundle bundle = new Bundle();
		bundle.putString("alert-message", message);
		myDialog.setArguments(bundle);
		
		return myDialog;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setCancelable(true);
		int style = DialogFragment.STYLE_NORMAL, theme = 0;
		setStyle(style, theme);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		return new AlertDialog.Builder(getActivity())
		.setTitle("Select Action")
		.setItems(actionTypes, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					
					//need graphic id as a property so it can be deleted or updated
					
					Activity a = getActivity();
					Context c = a.getApplicationContext();

					Toast toast = Toast.makeText(c, "", Toast.LENGTH_LONG); 
					toast.setGravity(Gravity.BOTTOM, 0, 0);

					// process user selection
					if (id == 0) {
						try {							
							//need result from dialog to finish adding pt
							LayoutInflater factory = LayoutInflater.from(mMapView.getContext());
					        final View textEntryView = factory.inflate(R.layout.save_dialog_layout, null);
					        
					        new AlertDialog.Builder(mMapView.getContext())
					            .setIconAttribute(android.R.attr.alertDialogIcon)
					            .setTitle(R.string.titleSaveDialog)
					            .setView(textEntryView)
					            .setPositiveButton(R.string.titleSave, new DialogInterface.OnClickListener() {
					                public void onClick(DialogInterface dialog, int whichButton) {

					                	EditText et = (EditText) textEntryView.findViewById(R.id.note_edit);
					                	String note = et.getText().toString();
					                	
					                	//figure out what type of geometry is the sender and then edit that type
					                	
					                	Geometry g = graphicsLayer.getGraphic(gid).getGeometry();
					                	dbAdapter.open();

					               		if(g.getType().equals(Geometry.Type.POINT)){	
					               			Point pt = (Point)g;
						            		long rowid = dbAdapter.insertGraphic(pt.getX(), pt.getY(), note, "Point", "");
						            		
						            		HashMap<String, Object> attributeMap = new HashMap<String, Object>();
						            		attributeMap.put("note", note);
						            		attributeMap.put("rowid", rowid);
						            		
						            		Graphic graphic = new Graphic(g, new PictureMarkerSymbol(drawable), attributeMap, null);
						            		graphicsLayer.addGraphic(graphic);
					               			 
					               		}else if(g.getType().equals(Geometry.Type.POLYLINE)){
					               			Polyline line = (Polyline)g;
					               			String values = "";
					                    	int iCount = line.getPointCount();
					                    	int i = 0;
					                    	
					                    	while(i < iCount){
					                    		Point p = line.getPoint(i);
					                    		values = values + p.getX() + ":" + p.getY() + ",";
					                    		i++;
					                    	}
					               			
					               			long rowid = dbAdapter.insertGraphic(0.0, 0.0, note, "Line", values);
						            		
						            		HashMap<String, Object> attributeMap = new HashMap<String, Object>();
						            		attributeMap.put("note", note);
						            		attributeMap.put("rowid", rowid);
						            		
						            		Graphic graphic = new Graphic(line, new SimpleLineSymbol(Color.BLUE, 3), attributeMap, null);
						            		graphicsLayer.addGraphic(graphic);
					               		}else if(g.getType().equals(Geometry.Type.POLYGON)){
					               			Polygon polygon = (Polygon)g;
					               			String values = "";
					                    	int iCount = polygon.getPointCount();
					                    	int i = 0;
					                    	
					                    	while(i < iCount){
					                    		Point p = polygon.getPoint(i);
					                    		values  = values + p.getX() + ":" + p.getY() + ",";
					                    		i++;
					                    	}
					                    	
					                    	long rowid = dbAdapter.insertGraphic(0.0, 0.0, note, "Line", values);
						            		
						            		HashMap<String, Object> attributeMap = new HashMap<String, Object>();
						            		attributeMap.put("note", note);
						            		attributeMap.put("rowid", rowid);
						            		
						            		SimpleFillSymbol sfs = new SimpleFillSymbol(Color.BLUE);
						    				sfs.setAlpha(70);
						    				sfs.setOutline(new SimpleLineSymbol(Color.BLACK, 1));
						            		
						            		Graphic graphic = new Graphic(polygon, sfs, attributeMap, null);
						            		graphicsLayer.addGraphic(graphic);
					               		}

					               		//delete the old one from the graphics layer and the DB
					            		dbAdapter.deleteGraphic(rowid);
								        graphicsLayer.removeGraphic(gid);
					            		dbAdapter.close();
					                }
					                
					            }).show();
					        					        							
	                        toast.setText("Note Saved");
		                    
		                } catch (Exception e) {
		               
		                	toast.setText("Edit Failed.");
		                }
					} else if (id == 1) {
						//delete the graphic and refresh the graphics layer
						dbAdapter.open();
				        dbAdapter.deleteGraphic(rowid);
				        graphicsLayer.removeGraphic(gid);
				        dbAdapter.close();				
						
						toast.setText("Feature Deleted");					  
					} 

					toast.show();
				}
			}).create();
	}
	
	@Override
	public void onDismiss(DialogInterface dialog) {
		super.onDismiss(dialog);
	}
}
