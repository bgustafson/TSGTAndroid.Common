package org.tristategt.common.Dialogs;

import org.tristategt.common.GenericMapTouchListener;
import org.tristategt.common.R;
import org.tristategt.common.Measure.MeasureTouchListener;

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
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MeasureDialog extends DialogFragment {
	
	final String[] measurementTypes = new String[] { "Length", "Area", "Clear" };
	int selectedMeasureIndex = -1;
	
	MeasureTouchListener myListener;
	public MeasureTouchListener getMyListener() {
		return myListener;
	}

	public void setMyListener(MeasureTouchListener Listener) {
		this.myListener = Listener;
	}
	
	MapView mMapView;
	public MapView getMapView() {
		return mMapView;
	}

	public void setMapView(MapView mMapView) {
		this.mMapView = mMapView;
	}

	public static MeasureDialog newInstance(String message){		
		MeasureDialog myMeasureDialog = new MeasureDialog();
		Bundle bundle = new Bundle();
		bundle.putString("alert-message", message);
		myMeasureDialog.setArguments(bundle);
		
		return myMeasureDialog;
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
		.setTitle("Select Type")
		.setItems(measurementTypes, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					
					Activity a = getActivity();
					LinearLayout layout = (LinearLayout) a.findViewById(R.id.measurelayout);
					GraphicsLayer graphicsLayer = (GraphicsLayer) mMapView.getLayer(4);
					Context c = a.getApplicationContext();

					Toast toast = Toast.makeText(c, "", Toast.LENGTH_LONG); 
					toast.setGravity(Gravity.BOTTOM, 0, 0);

					// Get item selected by user.
					String measurmentType = measurementTypes[id];
					selectedMeasureIndex = id;

					// process user selection
					if (measurmentType.equalsIgnoreCase("Length")) {
						mMapView.setOnTouchListener(myListener);
						myListener.setType("Length");
						toast.setText("Tap screen to draw a line. \nDouble tap to stop drawing.");
					} else if (measurmentType.equalsIgnoreCase("Area")) {
						mMapView.setOnTouchListener(myListener);
						myListener.setType("Area");
						toast.setText("Tap screen to draw a polygon. \nDouble tap to stop drawing.");
					} else if (measurmentType.equalsIgnoreCase("Clear")) {
						graphicsLayer.removeAll();
						mMapView.setOnTouchListener(new GenericMapTouchListener(mMapView.getContext(), mMapView));
						layout.setVisibility(View.INVISIBLE);
						toast.setText("Measure layer cleared.");
					}

					toast.show();
				}
			}).create();
	}
}
