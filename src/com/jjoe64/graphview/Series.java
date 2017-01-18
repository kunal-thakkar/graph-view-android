/**
 * This file is part of GraphView.
 *
 * GraphView is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GraphView is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GraphView.  If not, see <http://www.gnu.org/licenses/lgpl.html>.
 *
 * Copyright Jonas Gehring
 */

package com.jjoe64.graphview;

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * a graphview series.
 * holds the data, description and styles
 */
public abstract class Series <E extends DataPointInterface> {

	final String description;
	protected final SeriesStyle style;
	protected E[] values;
	protected final Paint paintBackground;
	protected final Paint paint;
	protected boolean drawBackground;
	protected boolean drawDataPoints;
	protected float dataPointsRadius = 10f;

	private Class<E> c;
	private final List<GraphView> graphViews = new ArrayList<GraphView>();
	protected NumberFormat numberFormat;

	abstract protected void drawSeries(Canvas canvas, float graphwidth, float graphheight, float border, double minX, double minY, double diffX, double diffY, float horstart, double viewportStart, double viewportSize);
	
    /**
     * create a series with predefined options
     * @param description the name of the series
     * @param style custom style. can be null for default styles
     * @param values the values must be in the correct order! x-value has to be ASC. First the lowest x value and at least the highest x value.
     */
	public Series(String description, SeriesStyle style, Class<E> c, E[] values) {
		super();
		this.description = description;
		if (style == null) {
			style = new SeriesStyle();
		}
		this.numberFormat = new DecimalFormat("#.00");
		this.numberFormat.setMaximumFractionDigits(2);
		this.c = c;
		this.style = style;
		this.values = values;
		this.paint = new Paint();
		this.paintBackground = new Paint();
		this.paintBackground.setColor(Color.rgb(20, 40, 60));
		this.paintBackground.setStrokeWidth(4);
		this.paintBackground.setAlpha(128);

        checkValueOrder();
    }

	/**
	 * this graphview will be redrawn if data changes
	 * @param graphView
	 */
	public void addGraphView(GraphView graphView) {
		this.graphViews.add(graphView);
	}

	/**
	 * add one data to current data
     * the values must be in the correct order! x-value has to be ASC. First the lowest x value and at least the highest x value.
     *
	 * @param value the new data to append. Important: the new value must be higher then the last value (x).
	 * @param scrollToEnd true => graphview will scroll to the end (maxX)
	 * @param maxDataCount if max data count is reached, the oldest data value will be lost
	 */
	@SuppressWarnings("unchecked")
	public void appendData(E value, boolean scrollToEnd, int maxDataCount) {
        if (values.length > 0 && value.getX() < values[values.length-1].getX()) {
            throw new IllegalArgumentException("new x-value must be greater then the last value. x-values has to be ordered in ASC.");
        }
		synchronized (values) {
			int curDataCount = values.length;
			E[] newValues;
			if (curDataCount < maxDataCount) {
				// enough space
				newValues = (E[]) Array.newInstance(c, curDataCount + 1);
				System.arraycopy(values, 0, newValues, 0, curDataCount);
				// append new data
				newValues[curDataCount] = value;
			} else {
				// we have to trim one data
				newValues = (E[]) Array.newInstance(c, maxDataCount);
				System.arraycopy(values, 1, newValues, 0, curDataCount-1);
				// append new data
				newValues[maxDataCount-1] = value;
			}
			values = newValues;
		}

		// update linked graph views
		for (GraphView g : graphViews) {
			if (scrollToEnd) {
				g.scrollToEnd();
			}
		}
	}

	/**
	 * @return series styles. never null
	 */
	public SeriesStyle getStyle() {
		return style;
	}

	/**
	 * you should use {@link GraphView#removeSeries(GraphViewSeries)}
	 * @param graphView
	 */
	public void removeGraphView(GraphView graphView) {
		graphViews.remove(graphView);
	}

	/**
	 * clears the current data and set the new.
     *
	 * redraws the graphview(s)
	 * @param values the values must be in the correct order! x-value has to be ASC. First the lowest x value and at least the highest x value.
	 */
	public void resetData(E[] values) {
		this.values = values;
        checkValueOrder();
        for (GraphView g : graphViews) {
			g.redrawAll();
		}
	}

    private void checkValueOrder() {
        if (values.length>0) {
            double lx = values[0].getX();
            for (int i=1;i<values.length;i++) {
                if (lx > values[i].getX()) {
                    throw new IllegalArgumentException("The order of the values is not correct. X-Values have to be ordered ASC. First the lowest x value and at least the highest x value.");
                }
                lx = values[i].getX();
            }
        }
    }
    
	public int getBackgroundColor() {
		return paintBackground.getColor();
	}

	public float getDataPointsRadius() {
		return dataPointsRadius;
	}

	public boolean getDrawBackground() {
		return drawBackground;
	}

	public boolean getDrawDataPoints() {
		return drawDataPoints;
	}

	/**
	 * sets the background color for the series.
	 * This is not the background color of the whole graph.
	 * @see #setDrawBackground(boolean)
	 */
	public void setBackgroundColor(int color) {
		paintBackground.setColor(color);
	}

	/**
	 * sets the radius of the circles at the data points.
	 * @see #setDrawDataPoints(boolean)
	 * @param dataPointsRadius
	 */
	public void setDataPointsRadius(float dataPointsRadius) {
		this.dataPointsRadius = dataPointsRadius;
	}

	/**
	 * @param drawBackground true for a light blue background under the graph line
	 * @see #setBackgroundColor(int)
	 */
	public void setDrawBackground(boolean drawBackground) {
		this.drawBackground = drawBackground;
	}

	/**
	 * You can set the flag to let the GraphView draw circles at the data points
	 * @see #setDataPointsRadius(float)
	 * @param drawDataPoints
	 */
	public void setDrawDataPoints(boolean drawDataPoints) {
		this.drawDataPoints = drawDataPoints;
	}

	@SuppressWarnings("unchecked")
	protected E[] _values(double viewPortStart, double viewPortSize) {
		synchronized (values) {
			if (viewPortStart == 0 && viewPortSize == 0) {
				// all data
				return values;
			} else {
				// viewport
				List<E> listData = new ArrayList<E>();
				for (int i=0; i<values.length; i++) {
					if (values[i].getX() >= viewPortStart) {
						if (values[i].getX() > viewPortStart+viewPortSize) {
							listData.add(values[i]); // one more for nice scrolling
							break;
						} else {
							listData.add(values[i]);
						}
					} else {
						if (listData.isEmpty()) {
							listData.add(values[i]);
						}
						listData.set(0, values[i]); // one before, for nice scrolling
					}
				}
				//return listData.toArray((E[])new Object[listData.size()]);
				return listData.toArray((E[]) Array.newInstance(c, listData.size()));
			}
		}
	}

	public double getMaxY(double viewPortStart, double viewPortSize){
		double largest = Integer.MIN_VALUE;
		E[] values = _values(viewPortStart, viewPortSize);
		for (int ii=0; ii<values.length; ii++)
			if (values[ii].getY() > largest)
				largest = values[ii].getY();
		return largest;
	}
	
	public double getMinY(double viewPortStart, double viewPortSize){
		double smallest = Integer.MAX_VALUE;
		E[] values = _values(viewPortStart, viewPortSize);
		for (int ii=0; ii<values.length; ii++)
			if (values[ii].getY() < smallest)
				smallest = values[ii].getY();
		return smallest;
	}

	public String getLabel(int xIndex){
		if(xIndex < 0 || values.length <= xIndex) return "";
		int index = xIndex - (int) values[0].getX();
		if(index < 0) return "";
		return " " + numberFormat.format(values[index].getY());
	}
}
