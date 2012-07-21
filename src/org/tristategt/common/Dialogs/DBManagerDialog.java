package org.tristategt.common.Dialogs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.nio.channels.FileChannel;
import java.util.HashMap;

import org.tristategt.common.DBAction.FeaturesDBAdapter;

import com.esri.android.map.GraphicsLayer;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.geometry.Polyline;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.symbol.SimpleLineSymbol;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.widget.Toast;

public class DBManagerDialog extends DialogFragment {
	
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

	final String[] actionTypes = new String[] { "Export Database", "Load Features"};//, "Delete Database" };

	public static DBManagerDialog newInstance(String message){		
		DBManagerDialog myDialog = new DBManagerDialog();
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
					
					Activity a = getActivity();
					Context c = a.getApplicationContext();

					Toast toast = Toast.makeText(c, "", Toast.LENGTH_LONG); 
					toast.setGravity(Gravity.BOTTOM, 0, 0);

					// process user selection
					if (id == 0) {
						try {
		                    File sd = Environment.getExternalStorageDirectory();
		                    File data = Environment.getDataDirectory();

		                    if (sd.canWrite()) {
		                    	dbAdapter.open();
								Cursor cursor = dbAdapter.getAllGraphics();			
								FileWriter fw = new FileWriter(new File(sd, "Feature_Graphics.csv"));
								
								if(cursor.moveToFirst()){
									do{
										String type = cursor.getString(cursor.getColumnIndex("geometry"));
										//write appropriate row to csv
							        	if(type.equalsIgnoreCase("point")){
							        		Point pt = new Point(cursor.getDouble(cursor.getColumnIndex("lat")), cursor.getDouble(cursor.getColumnIndex("long")));
							        		String note = cursor.getString(cursor.getColumnIndex("note"));
							        		fw.append("Point, " + pt.getX() + ", " + pt.getY() + ", 15, #FF92D050, Square, " + note + "\n");
							        	}else if(type.equalsIgnoreCase("line")){
							        		String note = cursor.getString(cursor.getColumnIndex("note"));
							        		String values = cursor.getString(cursor.getColumnIndex("_values"));
							        		values = values.replace(":", ", ");
							        		
							        		fw.append("Polyline,4,Solid,#FF92D050," + note + "," + values + ",\n");
							        	}else if(type.equalsIgnoreCase("polygon")){
							        		String note = cursor.getString(cursor.getColumnIndex("note"));
							        		String values = cursor.getString(cursor.getColumnIndex("_values"));
							        		values = values.replace(":", ", ");
							        		
							        		String[] latlongs = values.split(",");
							        		
							        		fw.append("Polygon,2,#FF92D050," + note + "," + values + latlongs[0] + "," + latlongs[1] + ",\n");
							        	}										
									}while(cursor.moveToNext());
								}
					
		                    	fw.flush();
		                    	fw.close();			
								dbAdapter.close();
		                    	
		                        String currentDBPath = "/data/org.tristategt.project/databases/GIS_Features";
		                        String backupDBPath = "GIS_Features";
		                        File currentDB = new File(data, currentDBPath);
		                        File backupDB = new File(sd, backupDBPath);

		                        FileChannel src = new FileInputStream(currentDB).getChannel();
		                        FileChannel dst = new FileOutputStream(backupDB).getChannel();
		                        dst.transferFrom(src, 0, src.size());
		                        src.close();
		                        dst.close();
		                        toast.setText("Exported: " + backupDB.getPath());
		                    }
		                } catch (Exception e) {
		                	toast.setText("Export Failed.");
		                }
					} else if (id == 1) {
						dbAdapter.open();
						Cursor cursor = dbAdapter.getAllGraphics();
						
						if (cursor != null) {
						    if (cursor.moveToFirst()) {
						        do {
						        	//get the geometry type and load the graphics
						        	String type = cursor.getString(cursor.getColumnIndex("geometry"));
						        	
						        	if(type.equalsIgnoreCase("point")){
						        		Point pt = new Point(cursor.getDouble(cursor.getColumnIndex("lat")), cursor.getDouble(cursor.getColumnIndex("long")));
							        	
							        	HashMap<String, Object> attributeMap = new HashMap<String, Object>();
					            		attributeMap.put("note", cursor.getString(cursor.getColumnIndex("note")));
					            		attributeMap.put("rowid", cursor.getString(cursor.getColumnIndex("_id")));
					            	
					            		graphicsLayer.addGraphic(new Graphic(pt, new PictureMarkerSymbol(drawable), attributeMap, null)); 
					            		
						        	}else if(type.equalsIgnoreCase("line")){
						        		String value = cursor.getString(cursor.getColumnIndex("_values"));
						        		String[] values = value.split(",");
						        		
						        		Polyline poly = new Polyline();
						        		Point pt;
						        		int i = 0;
						        		
						        		for(String s : values){
						        			String[] LatLong = s.split(":");
						        			
						        			if(i == 0){
						        				pt = new Point(Double.valueOf(LatLong[0]), Double.valueOf(LatLong[1]));				        										            		

						        				poly.startPath((float) pt.getX(), (float) pt.getY());
						        				i = 1;
						        			}
						        			else{
						        				pt = new Point(Double.valueOf(LatLong[0]), Double.valueOf(LatLong[1]));
						        				
						        				HashMap<String, Object> attributeMap = new HashMap<String, Object>();
						        				attributeMap.put("note", cursor.getString(cursor.getColumnIndex("note")));
						        				attributeMap.put("rowid", cursor.getString(cursor.getColumnIndex("_id")));
						        				
						        				poly.lineTo((float) pt.getX(), (float) pt.getY());	
							        			graphicsLayer.addGraphic(new Graphic(poly, new SimpleLineSymbol(Color.BLUE, 3), attributeMap, null));	
						        			}
						        		}
						         		
						        	}else if(type.equalsIgnoreCase("polygon")){
						        		String value = cursor.getString(cursor.getColumnIndex("_values"));
						        		String[] values = value.split(",");
						        		
						        		Polygon poly = new Polygon();
						        		Point pt;
						        		int i = 0;
						        		int polyUID = 0;
						        		
						        		for(String s : values){
						        			String[] LatLong = s.split(":");
						        			
						        			if(i == 0){
						        				pt = new Point(Double.valueOf(LatLong[0]), Double.valueOf(LatLong[1]));		        										            		
						        				
						        				poly.startPath((float) pt.getX(), (float) pt.getY());
						        				i = 1;
						        			}
						        			else{
						        				pt = new Point(Double.valueOf(LatLong[0]), Double.valueOf(LatLong[1]));
						        				
						        				SimpleFillSymbol sfs = new SimpleFillSymbol(Color.BLUE);
						        				sfs.setAlpha(70);
						        				sfs.setOutline(new SimpleLineSymbol(Color.BLACK, 1));
						        				
						        				HashMap<String, Object> attributeMap = new HashMap<String, Object>();
						        				attributeMap.put("note", cursor.getString(cursor.getColumnIndex("note")));
						        				attributeMap.put("rowid", cursor.getString(cursor.getColumnIndex("_id")));
						        				
						        				poly.lineTo((float) pt.getX(), (float) pt.getY());
						        				
						        				if(polyUID == 0){
						        					polyUID = graphicsLayer.addGraphic(new Graphic(poly, sfs, attributeMap, null));		
						        				}else{
						        					graphicsLayer.removeGraphic(polyUID);
						        					polyUID = graphicsLayer.addGraphic(new Graphic(poly, sfs, attributeMap, null));
						        				}				        				        			
						        			}
						        		}
						        	}				            						            
						        } while (cursor.moveToNext());
						    }
						}
						dbAdapter.close();
						
						toast.setText("Features Loaded");					  
					} else if (id == 2) {
					    dbAdapter.open();
					    dbAdapter.dropDB();
					    dbAdapter.close();

					    graphicsLayer.removeAll();
					    
						toast.setText("Database Deleted.");
					}

					toast.show();
				}
			}).create();
	}
}
