package org.tristategt.common;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;

import com.esri.android.map.MapOnTouchListener;
import com.esri.android.map.MapView;

public class GenericMapTouchListener extends MapOnTouchListener {
	
	MapView mMapView;

	public GenericMapTouchListener(Context context, MapView view) {
		super(context, view);
		this.mMapView = view;
	}

	@Override
	public boolean onDoubleTap(MotionEvent point) {
				
		if(!mMapView.getCallout().isShowing())
			return super.onDoubleTap(point);
		else{
			mMapView.getCallout().hide();
			return true;
		}
	}

	@Override
	public boolean onDragPointerMove(MotionEvent from, MotionEvent to) {
		return super.onDragPointerMove(from, to);
	}

	@Override
	public boolean onDragPointerUp(MotionEvent from, MotionEvent to) {
		return super.onDragPointerUp(from, to);
	}

	@Override
	public void onLongPress(MotionEvent point) {
		super.onLongPress(point);
	}

	@Override
	public void onMultiPointersSingleTap(MotionEvent event) {
		super.onMultiPointersSingleTap(event);
	}

	@Override
	public boolean onPinchPointersDown(MotionEvent event) {
		return super.onPinchPointersDown(event);
	}

	@Override
	public boolean onPinchPointersMove(MotionEvent event) {
		return super.onPinchPointersMove(event);
	}

	@Override
	public boolean onPinchPointersUp(MotionEvent event) {
		return super.onPinchPointersUp(event);
	}

	@Override
	public boolean onSingleTap(MotionEvent point) {
		return super.onSingleTap(point);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return super.onTouch(v, event);
	}

}
