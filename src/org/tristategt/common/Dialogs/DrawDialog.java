package org.tristategt.common.Dialogs;

import org.tristategt.common.GenericMapTouchListener;
import org.tristategt.common.Draw.DrawTouchListener;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Toast;

public class DrawDialog extends DialogFragment {
	
	final String[] geometryTypes = new String[] { "Point", "Polyline", "Polygon", "Clear" };
	int selectedGeometryIndex = -1;
	
	DrawTouchListener myListener;
	public DrawTouchListener getMyListener() {
		return myListener;
	}

	public void setMyListener(DrawTouchListener Listener) {
		this.myListener = Listener;
	}
	
	MapView mMapView;
		
	public MapView getMapView() {
		return mMapView;
	}

	public void setMapView(MapView mMapView) {
		this.mMapView = mMapView;
	}

	public static DrawDialog newInstance(String message){		
		DrawDialog myDrawDialog = new DrawDialog();
		Bundle bundle = new Bundle();
		bundle.putString("alert-message", message);
		myDrawDialog.setArguments(bundle);
		
		return myDrawDialog;
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
		.setTitle("Select Geometry")
		.setItems(geometryTypes, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					
					Activity a = getActivity();
					GraphicsLayer graphicsLayer = (GraphicsLayer) mMapView.getLayer(3);
					Context c = a.getApplicationContext();

					Toast toast = Toast.makeText(c, "", Toast.LENGTH_LONG); 
					toast.setGravity(Gravity.BOTTOM, 0, 0);

					// Get item selected by user.
					String geomType = geometryTypes[id];
					selectedGeometryIndex = id;

					// process user selection
					if (geomType.equalsIgnoreCase("Polygon")) {
						mMapView.setOnTouchListener(myListener);
						myListener.setType("POLYGON");
						toast.setText("Tap screen to draw a polygon. \nDouble tap to stop drawing.");
					} else if (geomType.equalsIgnoreCase("Polyline")) {
						mMapView.setOnTouchListener(myListener);
						myListener.setType("POLYLINE");
						toast.setText("Tap screen to draw a line. \nDouble tap to stop drawing.");
					} else if (geomType.equalsIgnoreCase("Point")) {
						mMapView.setOnTouchListener(myListener);
						myListener.setType("POINT");
						toast.setText("Tap on screen to draw a point.");
					} else if (geomType.equalsIgnoreCase("Clear")) {
						graphicsLayer.removeAll();
						mMapView.setOnTouchListener(new GenericMapTouchListener(mMapView.getContext(), mMapView));
						toast.setText("Graphics layer cleared.");
					}

					toast.show();
				}
			}).create();
	}
}
