package org.tristategt.common.Measure;

import java.text.DecimalFormat;

public class MeasureCalcConverter {

	DecimalFormat df;
	
	public MeasureCalcConverter(){
		df = new DecimalFormat("0.0000");
	}
		
	public String calcSqMiles(double area){
		double value = new Double(area);
		return df.format(value);
	}
	
	public String calcSqFeet(double area){
		double value = new Double(area) * 27878400;
		return df.format(value);
	}
	
	public String calcSqKiloMeters(double area){
		double value = new Double(area) * 2.58998811;
		return df.format(value);
	}

	public String calcSqMeters(double area){
		double value = new Double(area) * 2589988.11;
		return df.format(value);
	}
	
	public String calcMiles(double length){
		double value = new Double(length);
		return df.format(value);
	}
		
	public String calcFeet(double length){
		double value = new Double(length) * 5280;
		return df.format(value);
	}
	
	public String calcKiloMeters(double length){
		double value = new Double(length) * 1.609344;
		return df.format(value);
	}

	public String calcMeters(double length){
		double value = new Double(length) * 1609.344;
		return df.format(value);
	}
}
