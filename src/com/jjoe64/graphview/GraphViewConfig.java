package com.jjoe64.graphview;

import android.content.Context;

public class GraphViewConfig {

	static final float BORDER = 10;
	static final double ratio = 0.8;

	private boolean staticVerticalLabels;
    private boolean showHorizontalLabels = true;
    private boolean showVerticalLabels = true;
	private boolean showTitle = true;
	private boolean scalable;
	private boolean scrollable;
	private boolean disableTouch;
	private boolean showLegend = false;
	private LegendAlign legendAlign = LegendAlign.MIDDLE;
	private boolean manualYAxis;
	private boolean manualMaxY;
    private boolean manualMinY;
	private double manualMaxYValue;
	private double manualMinYValue;
	private GraphViewStyle graphViewStyle;
	private boolean staticHorizontalLabels;
	public GraphViewConfig(Context context) {
		this.graphViewStyle = new GraphViewStyle();
		this.graphViewStyle.useTextColorFromTheme(context);
	}
	public boolean isScalable() {
		return scalable;
	}
	public void setScalable(boolean scalable) {
		this.scalable = scalable;
		if(scalable) setScrollable(true);
	}
	public boolean isScrollable() {
		return scrollable;
	}
	public void setScrollable(boolean scrollable) {
		this.scrollable = scrollable;
	}
	public boolean isDisableTouch() {
		return disableTouch;
	}
	public void setDisableTouch(boolean disableTouch) {
		this.disableTouch = disableTouch;
	}
	public boolean isShowLegend() {
		return showLegend;
	}
	public void setShowLegend(boolean showLegend) {
		this.showLegend = showLegend;
	}
	public LegendAlign getLegendAlign() {
		return legendAlign;
	}
	public void setLegendAlign(LegendAlign legendAlign) {
		this.legendAlign = legendAlign;
	}
	public boolean isManualYAxis() {
		return manualYAxis;
	}
	public void setManualYAxis(boolean manualYAxis) {
		this.manualYAxis = manualYAxis;
	}
	public boolean isManualMaxY() {
		return manualMaxY;
	}
	public void setManualMaxY(boolean manualMaxY) {
		this.manualMaxY = manualMaxY;
	}
	public boolean isManualMinY() {
		return manualMinY;
	}
	public void setManualMinY(boolean manualMinY) {
		this.manualMinY = manualMinY;
	}
	public double getManualMaxYValue() {
		return manualMaxYValue;
	}
	public void setManualMaxYValue(double manualMaxYValue) {
		this.manualMaxYValue = manualMaxYValue;
	}
	public double getManualMinYValue() {
		return manualMinYValue;
	}
	public void setManualMinYValue(double manualMinYValue) {
		this.manualMinYValue = manualMinYValue;
	}
	public GraphViewStyle getGraphViewStyle() {
		return graphViewStyle;
	}
	public void setGraphViewStyle(GraphViewStyle graphViewStyle) {
		this.graphViewStyle = graphViewStyle;
	}
	public boolean isStaticHorizontalLabels() {
		return staticHorizontalLabels;
	}
	public void setStaticHorizontalLabels(boolean staticHorizontalLabels) {
		this.staticHorizontalLabels = staticHorizontalLabels;
	}
	public boolean isStaticVerticalLabels() {
		return staticVerticalLabels;
	}
	public void setStaticVerticalLabels(boolean staticVerticalLabels) {
		this.staticVerticalLabels = staticVerticalLabels;
	}
	public boolean isShowHorizontalLabels() {
		return showHorizontalLabels;
	}
	public void setShowHorizontalLabels(boolean showHorizontalLabels) {
		this.showHorizontalLabels = showHorizontalLabels;
	}
	public boolean isShowVerticalLabels() {
		return showVerticalLabels;
	}
	public void setShowVerticalLabels(boolean showVerticalLabels) {
		this.showVerticalLabels = showVerticalLabels;
	}
	public boolean isShowTitle() {
		return showTitle;
	}
	public void setShowTitle(boolean showTitle) {
		this.showTitle = showTitle;
	}
	public static float getBorder() {
		return BORDER;
	}
	public static double getRatio() {
		return ratio;
	}
	public void setManualYMaxBound(double max) {
    	manualMaxYValue = max;
    	manualMaxY = true;
	}
	public void setManualYMinBound(double min) {
    	manualMinYValue = min;
    	manualMinY = true;
	}
	public void setManualYAxisBounds(double max, double min) {
		manualMaxYValue = max;
		manualMinYValue = min;
		manualYAxis = true;
	}
}
