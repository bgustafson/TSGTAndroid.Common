package org.tristategt.common.Identify;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.esri.android.action.IdentifyResultSpinnerAdapter;
import com.esri.core.tasks.ags.identify.IdentifyResult;

public class IdentifyAdapter extends IdentifyResultSpinnerAdapter {
	String m_show = null;	
	List<IdentifyResult> resultList;
	int currentDataViewed = -1;
	Context m_context;


	public IdentifyAdapter(Context context, List<IdentifyResult> results) {
		super(context,results);
		this.resultList = results;
		this.m_context = context;
			
	}

	//This is the view that will get added to the callout
	//Create a text view and assign the text that should be visible in the callout		
	public View getView(int position, View convertView, ViewGroup parent) {
		String outputVal = null;
		TextView txtView;
		IdentifyResult curResult = this.resultList.get(position);
			
		outputVal = String.format("%s (%s)", curResult.getValue().toString(), curResult.getLayerName());
							
		txtView = new TextView(this.m_context);
		txtView.setText(outputVal);
		txtView.setTextColor(Color.WHITE);
		txtView.setLayoutParams(new ListView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		txtView.setGravity(Gravity.CENTER_VERTICAL);
		
		return txtView;
	}
}
