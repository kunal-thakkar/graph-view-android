package com.jjoe64.graphview;

import java.util.Random;

import android.graphics.Color;

/**
 * graph series style: color and thickness
 */
public class SeriesStyle {
	public static Random random = new Random();
	public int color = 0xff0077cc;
	public int thickness = 3;
	private ValueDependentColor valueDependentColor;

	public SeriesStyle() {
		this(Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256)), 2);
	}

	public SeriesStyle(int color, int thickness) {
		this.color = color;
		this.thickness = thickness;
	}
	
	public ValueDependentColor getValueDependentColor() {
		return valueDependentColor;
	}
	
	/**
	 * the color depends on the value of the data.
	 * only possible in BarGraphView
	 * @param valueDependentColor
	 */
	public void setValueDependentColor(ValueDependentColor valueDependentColor) {
		this.valueDependentColor = valueDependentColor;
	}
}
