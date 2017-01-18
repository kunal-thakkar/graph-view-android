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

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.jjoe64.graphview.compatible.ScaleGestureDetector;

/**
 * GraphView is a Android View for creating zoomable and scrollable graphs.
 * This is the abstract base class for all graphs. Extend this class and implement {@link #drawSeries(android.graphics.Canvas, DataPointInterface[], float, float, float, double, double, double, double, float, com.jjoe64.graphview.Series.GraphViewSeriesStyle)} to display a custom graph.
 * Use {@link com.jjoe64.graphview.LineGraphView} for creating a line chart.
 *
 * @author jjoe64 - jonas gehring - http://www.jjoe64.com
 *
 * Copyright (C) 2011 Jonas Gehring
 * Licensed under the GNU Lesser General Public License (LGPL)
 * http://www.gnu.org/licenses/lgpl.html
 */
public class GraphView extends LinearLayout {

	private class GraphViewContentView extends View {
		private float lastTouchEventX;
		private float graphwidth;
		private boolean scrollingStarted;

		/**
		 * @param context
		 */
		public GraphViewContentView(Context context) {
			super(context);
			setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		}

		/**
		 * @param canvas
		 */
		@Override
		protected void onDraw(Canvas canvas) {
			paint.setAntiAlias(true);
			paint.setStrokeWidth(0);

			float border = GraphViewConfig.BORDER;
			float horstart = 0;
			float height = getHeight();
			float graphheight = height - (2 * border);
			float width = getWidth() - 1;
			float div = (float) (graphSeries2.size() > 0?GraphViewConfig.ratio:1);

			 // measure bottom text
			if (labelTextHeight == null || horLabelTextWidth == null) {
				paint.setTextSize(config.getGraphViewStyle().getTextSize());
				double testX = ((getMaxX(graphSeries, true)-getMinX(graphSeries, true))*0.783)+getMinX(graphSeries, true);
				String testLabel = formatLabel(graphSeries, testX, true);
				paint.getTextBounds(testLabel, 0, testLabel.length(), textBounds);
                int lines = 1;
                for (byte c : testLabel.getBytes()) {
                    if (c == '\n') lines++;
                }
                labelTextHeight = textBounds.height()*lines;
				horLabelTextWidth = textBounds.width();
			}
            border += labelTextHeight;

			graphwidth = width;

			if (horlabels == null) {
				horlabels = generateHorlabels(graphwidth);
            } else if (config.getGraphViewStyle().getNumHorizontalLabels() > 0) {
                Log.w("GraphView", "when you use static labels (via setHorizontalLabels) the labels will just be shown exactly in that way, that you have set it. setNumHorizontalLabels does not have any effect.");
			}
			if (verlabels == null) {
				verlabels = generateVerlabels(graphSeries, graphheight * div);
            } else if (config.getGraphViewStyle().getNumVerticalLabels() > 0) {
                Log.w("GraphView", "when you use static labels (via setVerticalLabels) the labels will just be shown exactly in that way, that you have set it. setNumVerticalLabels does not have any effect.");
			}
			if (verlabels2 == null) {
				verlabels2 = generateVerlabels(graphSeries2, graphheight * (1-div));
            }
			drawhorizontalLines(canvas, graphheight*div, border, horstart, width, verlabels);
			drawhorizontalLines(canvas, graphheight*(1-div), (graphheight * div) + border, horstart, width, verlabels2);

			paint.setColor(config.getGraphViewStyle().getHorizontalLabelsColor());
			drawHorizontalLabels(canvas, border, horstart, height, horlabels, graphwidth);

			if(config.isShowTitle()){
	            paint.setTextAlign(Align.CENTER);
				canvas.drawText(title, (graphwidth / 2) + horstart, border - 4, paint);
			}
			
			if(graphSeries.size()>0) drawSeries(canvas, horstart, border, graphheight*div, div, graphSeries);
			if(graphSeries2.size()>0) drawSeries(canvas, horstart, (graphheight * div) + border, graphheight*(1-div), div, graphSeries2);
			if(config.isShowLegend()) drawLegend(canvas, height, width);
		}
		
		private void drawSeries(Canvas canvas, float horstart, float startY, float graphHeight, float div, List<Series<?>> graphSeries){
			double maxY = getMaxY(graphSeries);
			double minY = getMinY(graphSeries);
			if (maxY == minY) { // if minY/maxY is the same, fake it so that we can render a line
				if(maxY == 0) { // if both are zero, change the values to prevent division by zero
					maxY = 1.0d;
					minY = 0.0d;
				} else {
					maxY = maxY*1.05d;
					minY = minY*0.95d;
				}
			}
			double diffY = maxY - minY;

			double maxX = getMaxX(graphSeries, false);
			double minX = getMinX(graphSeries, false);
			double diffX = maxX - minX;
			paint.setColor(Color.BLACK);
			canvas.drawLine(0, startY, getWidth(), startY, paint);
			paint.setStrokeCap(Paint.Cap.ROUND);
			for (int i=0; i<graphSeries.size(); i++){
				graphSeries.get(i).drawSeries(canvas, graphwidth, graphHeight, startY, minX, minY, diffX, diffY, horstart, viewportStart, viewportSize);
			}
		}

		private void drawhorizontalLines(Canvas canvas, float graphheight, float border, float horstart, float width, String[] verlabels){
			// horizontal lines
			if (config.getGraphViewStyle().getGridStyle().drawHorizontal()) {
				paint.setTextAlign(Align.LEFT);
				int vers = verlabels.length - 1;
				for (int i = 0; i < verlabels.length; i++) {
					paint.setColor(config.getGraphViewStyle().getGridColor());
					float y = ((graphheight / vers) * i) + border;
					canvas.drawLine(horstart, y, width, y, paint);
				}
			}
		}

		private void onMoveGesture(float f) {
			// view port update
			if (viewportSize != 0) {
				viewportStart -= f*viewportSize/graphwidth;

				// minimal and maximal view limit
				double minX = getMinX(graphSeries, true);
				double maxX = getMaxX(graphSeries, true);
				if (viewportStart < minX) {
					viewportStart = minX;
				} else if (viewportStart+viewportSize > maxX) {
					viewportStart = maxX - viewportSize;
				}

				// labels have to be regenerated
				if (!config.isStaticHorizontalLabels()) horlabels = null;
				if (!config.isStaticVerticalLabels()) verlabels = null;
				viewVerLabels.invalidate();
			}
			invalidate();
		}

		public boolean performClick(){
			return super.performClick();
		}
		
		/**
		 * @param event
		 */
		@Override
		public boolean onTouchEvent(MotionEvent event) {
			performClick();
			xLocation = event.getX(event.getActionIndex());
			if (!config.isScrollable() || config.isDisableTouch()) {
				invalidate();
				return super.onTouchEvent(event);
			}
			
			boolean handled = false;
			// first scale
			if (config.isScalable() && scaleDetector != null) {
				System.out.println("Scaling");
				scaleDetector.onTouchEvent(event);
				handled = scaleDetector.isInProgress();
			}
			if (!handled) {
				System.out.println("Scroll");
				// if not scaled, scroll
				if ((event.getAction() & MotionEvent.ACTION_DOWN) == MotionEvent.ACTION_DOWN &&
						(event.getAction() & MotionEvent.ACTION_MOVE) == 0) {
					scrollingStarted = true;
					handled = true;
				}
				if ((event.getAction() & MotionEvent.ACTION_UP) == MotionEvent.ACTION_UP) {
					scrollingStarted = false;
					lastTouchEventX = 0;
					handled = true;
				}
				if ((event.getAction() & MotionEvent.ACTION_MOVE) == MotionEvent.ACTION_MOVE) {
					if (scrollingStarted) {
						if (lastTouchEventX != 0) {
							onMoveGesture(event.getX() - lastTouchEventX);
						}
						lastTouchEventX = event.getX();
						handled = true;
					}
				}
				if (handled) invalidate();
			} else {
				// currently scaling
				scrollingStarted = false;
				lastTouchEventX = 0;
			}
			return handled;
		}

	}

	private class VerLabelsView extends View {
		private LayoutParams layoutParams;
		/**
		 * @param context
		 */
		public VerLabelsView(Context context) {
			super(context);
			layoutParams = new LayoutParams(
					config.getGraphViewStyle().getVerticalLabelsWidth()==0?100:config.getGraphViewStyle().getVerticalLabelsWidth(), 
					LayoutParams.MATCH_PARENT);
			setLayoutParams(layoutParams);
		}

		/**
		 * @param canvas
		 */
		@Override
		protected void onDraw(Canvas canvas) {
			// normal
			paint.setStrokeWidth(0);

			 // measure bottom text
			if (labelTextHeight == null || verLabelTextWidth == null) {
				paint.setTextSize(config.getGraphViewStyle().getTextSize());
				double testY = ((getMaxY(graphSeries)-getMinY(graphSeries))*0.783)+getMinY(graphSeries);
				String testLabel = formatLabel(graphSeries, testY, false);
				paint.getTextBounds(testLabel, 0, testLabel.length(), textBounds);
				labelTextHeight = (textBounds.height());
				verLabelTextWidth = (textBounds.width());
			}
			if (config.getGraphViewStyle().getVerticalLabelsWidth()==0 && getLayoutParams().width != verLabelTextWidth+GraphViewConfig.BORDER) {
				layoutParams.width = (int) (verLabelTextWidth + GraphViewConfig.BORDER);
			} else if (config.getGraphViewStyle().getVerticalLabelsWidth()!=0 && config.getGraphViewStyle().getVerticalLabelsWidth() != getLayoutParams().width) {
				layoutParams.width = config.getGraphViewStyle().getVerticalLabelsWidth();
			}
			setLayoutParams(layoutParams);

			float border = GraphViewConfig.BORDER;
			border += labelTextHeight;
			float height = getHeight();
			float graphheight = height - (2 * border);

			if (verlabels == null) {
				verlabels = generateVerlabels(graphSeries, graphheight);
			} else if (config.getGraphViewStyle().getNumVerticalLabels() > 0) {
                Log.w("GraphView", "when you use static labels (via setVerticalLabels) the labels will just be shown exactly in that way, that you have set it. setNumVerticalLabels does not have any effect.");
            }

			// vertical labels
			paint.setTextAlign(config.getGraphViewStyle().getVerticalLabelsAlign());
			int labelsWidth = getWidth();
			int labelsOffset = 0;
			if (config.getGraphViewStyle().getVerticalLabelsAlign() == Align.RIGHT) {
				labelsOffset = labelsWidth;
			} else if (config.getGraphViewStyle().getVerticalLabelsAlign() == Align.CENTER) {
				labelsOffset = labelsWidth / 2;
			}
			int vers = verlabels.length - 1;
			for (int i = 0; i < verlabels.length; i++) {
				float y = ((graphheight / vers) * i) + border;
				paint.setColor(config.getGraphViewStyle().getVerticalLabelsColor());

                String[] lines = verlabels[i].split("\n");
                for (int li=0; li<lines.length; li++) {
                    // for the last line y = height
                    float y2 = y - (lines.length-li-1)*config.getGraphViewStyle().getTextSize()*1.1f;
                    canvas.drawText(lines[li], labelsOffset, y2, paint);
                }
            }

			// reset
			paint.setTextAlign(Align.LEFT);
		}
	}

	protected final GraphViewConfig config;
	protected final Paint paint;
	private String[] horlabels;
	private String[] verlabels;
	private String[] verlabels2;
	protected String title;
	protected double viewportStart;
	protected double viewportSize;
	private final View viewVerLabels;
	private ScaleGestureDetector scaleDetector = new ScaleGestureDetector(getContext(), new ScaleGestureDetector.SimpleOnScaleGestureListener() {
		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			double center = viewportStart + viewportSize / 2;
			viewportSize /= detector.getScaleFactor();
			viewportStart = center - viewportSize / 2;

			// viewportStart must not be < minX
			double minX = getMinX(graphSeries, true);
			if (viewportStart < minX) {
				viewportStart = minX;
			}

			// viewportStart + viewportSize must not be > maxX
			double maxX = getMaxX(graphSeries, true);
			if (viewportSize == 0) {
				viewportSize = maxX;
			}
			double overlap = viewportStart + viewportSize - maxX;
			if (overlap > 0) {
				// scroll left
				if (viewportStart-overlap > minX) {
					viewportStart -= overlap;
				} else {
					// maximal scale
					viewportStart = minX;
					viewportSize = maxX - viewportStart;
				}
			}
			redrawAll();
			return true;
		}
	});
	protected final List<Series<?>> graphSeries;
	protected final List<Series<?>> graphSeries2;
	protected float xLocation;
	private final GraphViewContentView graphViewContentView;
	private final NumberFormat[] numberformatter = new NumberFormat[2];
	private CustomLabelFormatter labelFormatter;
	private Integer labelTextHeight;
	private Integer horLabelTextWidth;
	private Integer verLabelTextWidth;
	private final Rect textBounds = new Rect();

	public GraphView(Context context, AttributeSet attrs) {
		this(context, attrs.getAttributeValue(null, "title"));

		int width = attrs.getAttributeIntValue("android", "layout_width", LayoutParams.MATCH_PARENT);
		int height = attrs.getAttributeIntValue("android", "layout_height", LayoutParams.MATCH_PARENT);
		setLayoutParams(new LayoutParams(width, height));
	}

	/**
	 * @param context
	 * @param title [optional]
	 */
	public GraphView(Context context, String title) {
		super(context);
		setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		this.labelFormatter = new LabelFormatter();
		if (title == null)
			this.title = "";
		else
			this.title = title;

		this.config = new GraphViewConfig(context);
		this.paint = new Paint();
		this.graphSeries = new ArrayList<Series<?>>();
		this.graphSeries2 = new ArrayList<Series<?>>();
		
		viewVerLabels = new VerLabelsView(context);
		addView(viewVerLabels);
		graphViewContentView = new GraphViewContentView(context);
		addView(graphViewContentView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1));
	}

	/**
	 * add a series of data to the graph
	 * @param series
	 */
	public void addSeries(Series<?> series) {
		series.addGraphView(this);
		graphSeries.add(series);
		redrawAll();
	}

	/**
	 * add a second series of data to the graph
	 * @param series
	 */
	public void addSeries2(Series<?> series, boolean redraw) {
		if(redraw){
			for (Series<?> s : graphSeries2) {
				s.removeGraphView(this);
			}
			while (!graphSeries2.isEmpty()) {
				graphSeries2.remove(0);
			}
		}
		series.addGraphView(this);
		graphSeries2.add(series);
		redrawAll();
	}

	protected void drawHorizontalLabels(Canvas canvas, float border, float horstart, float height, String[] horlabels, float graphwidth) {
		// horizontal labels + lines
		int hors = horlabels.length - 1;
		for (int i = 0; i < horlabels.length; i++) {
			paint.setColor(config.getGraphViewStyle().getGridColor());
			float x = ((graphwidth / hors) * i) + horstart;
			if(config.getGraphViewStyle().getGridStyle().drawVertical()) { // vertical lines
				canvas.drawLine(x, height - border, x, border, paint);
			}
            if(config.isShowHorizontalLabels()) {
                paint.setTextAlign(Align.CENTER);
                if (i==horlabels.length-1)
                    paint.setTextAlign(Align.RIGHT);
                if (i==0)
                    paint.setTextAlign(Align.LEFT);
                paint.setColor(config.getGraphViewStyle().getHorizontalLabelsColor());
                String[] lines = horlabels[i].split("\n");
                for (int li=0; li<lines.length; li++) {
                    // for the last line y = height
                    float y = (height-4) - (lines.length-li-1)*config.getGraphViewStyle().getTextSize()*1.1f;
                    canvas.drawText(lines[li], x, y, paint);
                }
            }
		}
	}

	protected void drawLegend(Canvas canvas, float height, float width) {
		float textSize = paint.getTextSize();
		int spacing = config.getGraphViewStyle().getLegendSpacing();
		int border = config.getGraphViewStyle().getLegendBorder();
		int legendWidth = config.getGraphViewStyle().getLegendWidth();
		int shapeSize = (int) (textSize*0.8d);
		float legendHeight = (shapeSize+spacing)*graphSeries.size() +2*border -spacing;
		float lLeft = width-legendWidth - border*2;
		float lTop;
		switch (config.getLegendAlign()) {
		case TOP_LEFT:
			lTop = 0;
			lLeft = 0;
			break;
		case TOP:
			lTop = 0;
			break;
		case MIDDLE:
			lTop = height/2 - legendHeight/2;
			break;
		default:
			lTop = height - GraphViewConfig.BORDER - legendHeight - config.getGraphViewStyle().getLegendMarginBottom();
		}
		float lRight = lLeft+legendWidth;
		float lBottom = lTop+legendHeight;

		paint.setARGB(180, 100, 100, 100);
		canvas.drawRoundRect(new RectF(lLeft, lTop, lRight, lBottom), 8, 8, paint);

		for (int i=0; i<graphSeries.size(); i++) {
			paint.setColor(graphSeries.get(i).style.color);
			canvas.drawRect(new RectF(lLeft+border, lTop+border+(i*(shapeSize+spacing)), lLeft+border+shapeSize, lTop+border+(i*(shapeSize+spacing))+shapeSize), paint);
			if (graphSeries.get(i).description != null) {
				paint.setColor(Color.WHITE);
				paint.setTextAlign(Align.LEFT);
				canvas.drawText(graphSeries.get(i).description, lLeft+border+shapeSize+spacing, lTop+border+shapeSize+(i*(shapeSize+spacing)), paint);
			}
		}
	}

	private String[] generateHorlabels(float graphwidth) {
		int numLabels = config.getGraphViewStyle().getNumHorizontalLabels()-1;
		if (numLabels < 0) { // automatic
			if (graphwidth <= 0) graphwidth = 1f;
			numLabels = (int) (graphwidth/(horLabelTextWidth*2));
		}

		String[] labels = new String[numLabels+1];
		double min = getMinX(graphSeries, false);
		double max = getMaxX(graphSeries, false);
		for (int i=0; i<=numLabels; i++) {
			labels[i] = labelFormatter.formatLabel(min + ((max-min)*i/numLabels), true);
		}
		return labels;
	}

	synchronized private String[] generateVerlabels(List<Series<?>> graphSeries, float graphheight) {
		int numLabels = config.getGraphViewStyle().getNumVerticalLabels()-1;
		if (numLabels < 0) { // automatic
			if (graphheight <= 0) graphheight = 1f;
			numLabels = (int) (graphheight/(labelTextHeight*3));
			if (numLabels == 0) {
				Log.w("GraphView", "Height of Graph is smaller than the label text height, so no vertical labels were shown!");
			}
		}
		String[] labels = new String[numLabels+1];
		double min = getMinY(graphSeries);
		double max = getMaxY(graphSeries);
		if (max == min) {
			// if min/max is the same, fake it so that we can render a line
			if(max == 0) {
				// if both are zero, change the values to prevent division by zero
				max = 1.0d;
				min = 0.0d;
			} else {
				max = max*1.05d;
				min = min*0.95d;
			}
		}

		for (int i=0; i<=numLabels; i++) {
			labels[numLabels-i] = formatLabel(graphSeries, min + ((max-min)*i/numLabels), false);
		}
		return labels;
	}

	/**
	 * formats the label
	 * use #setCustomLabelFormatter or static labels if you want custom labels
	 *
	 * @param value x and y values
	 * @param isValueX if false, value y wants to be formatted
	 * @return value to display
	 */
	protected String formatLabel(List<Series<?>>graphSeries, double value, boolean isValueX) {
		if (labelFormatter != null) {
			String label = labelFormatter.formatLabel(value, isValueX);
			if (label != null) {
				return label;
			}
		}
		int i = isValueX ? 1 : 0;
		if (numberformatter[i] == null) {
			numberformatter[i] = NumberFormat.getNumberInstance();
			double highestvalue = isValueX ? getMaxX(graphSeries, false) : getMaxY(graphSeries);
			double lowestvalue = isValueX ? getMinX(graphSeries, false) : getMinY(graphSeries);
			if (highestvalue - lowestvalue < 0.1) {
				numberformatter[i].setMaximumFractionDigits(6);
			} else if (highestvalue - lowestvalue < 1) {
				numberformatter[i].setMaximumFractionDigits(4);
			} else if (highestvalue - lowestvalue < 20) {
				numberformatter[i].setMaximumFractionDigits(3);
			} else if (highestvalue - lowestvalue < 100) {
				numberformatter[i].setMaximumFractionDigits(1);
			} else {
				numberformatter[i].setMaximumFractionDigits(0);
			}
		}
		return numberformatter[i].format(value);
	}

	/**
	 * @return the custom label formatter, if there is one. otherwise null
	 */
	public CustomLabelFormatter getCustomLabelFormatter() {
		return labelFormatter;
	}

	/**
	 * returns the maximal X value of the current viewport (if viewport is set)
	 * otherwise maximal X value of all data.
	 * @param graphSeries 
	 * @param ignoreViewport
	 *
	 * warning: only override this, if you really know want you're doing!
	 */
	private double getMaxX(List<Series<?>> graphSeries, boolean ignoreViewport) {
		// if viewport is set, use this
		if (!ignoreViewport && viewportSize != 0) {
			return viewportStart+viewportSize;
		} else {
			// otherwise use the max x value
			// values must be sorted by x, so the last value has the largest X value
			double highest = 0;
			if (graphSeries.size() > 0) {
				DataPointInterface[] values = graphSeries.get(0).values;
				if (values.length == 0) {
					highest = 0;
				} else {
					highest = values[values.length-1].getX();
				}
				for (int i=1; i<graphSeries.size(); i++) {
					values = graphSeries.get(i).values;
					if (values.length > 0) {
						highest = Math.max(highest, values[values.length-1].getX());
					}
				}
			}
			return highest;
		}
	}

	/**
	 * returns the maximal Y value of all data.
	 *
	 * warning: only override this, if you really know want you're doing!
	 * @param graphSeries3 
	 */
	private double getMaxY(List<Series<?>> graphSeries3) {
		double largest;
		if (config.isManualYAxis() || config.isManualMaxY()) {
			largest = config.getManualMaxYValue();
		} else {
			largest = Integer.MIN_VALUE;
			for (int i=0; i<graphSeries3.size(); i++) {
				double maxY = graphSeries3.get(i).getMaxY(viewportStart, viewportSize);
				if(maxY > largest) largest = maxY;
			}
		}
		return largest;
	}

	/**
	 * returns the minimal X value of the current viewport (if viewport is set)
	 * otherwise minimal X value of all data.
	 * @param graphSeries 
	 * @param ignoreViewport
	 *
	 * warning: only override this, if you really know want you're doing!
	 */
	private double getMinX(List<Series<?>> graphSeries, boolean ignoreViewport) {
		// if viewport is set, use this
		if (!ignoreViewport && viewportSize != 0) {
			return viewportStart;
		} else {
			// otherwise use the min x value
			// values must be sorted by x, so the first value has the smallest X value
			double lowest = 0;
			if (graphSeries.size() > 0) {
				DataPointInterface[] values = graphSeries.get(0).values;
				if (values.length == 0) {
					lowest = 0;
				} else {
					lowest = values[0].getX();
				}
				for (int i=1; i<graphSeries.size(); i++) {
					values = graphSeries.get(i).values;
					if (values.length > 0) {
						lowest = Math.min(lowest, values[0].getX());
					}
				}
			}
			return lowest;
		}
	}

	/**
	 * returns the minimal Y value of all data.
	 *
	 * warning: only override this, if you really know want you're doing!
	 * @param graphSeries 
	 */
	private double getMinY(List<Series<?>> graphSeries) {
		double smallest;
		if (config.isManualYAxis() || config.isManualMinY()) {
			smallest = config.getManualMinYValue();
		} else {
			smallest = Integer.MAX_VALUE;
			for (int i=0; i<graphSeries.size(); i++) {
				double minY = graphSeries.get(i).getMinY(viewportStart, viewportSize);
				if(minY < smallest) smallest = minY;
			}
		}
		return smallest;
	}
	
	/**
	 * returns the size of the Viewport
	 * 
	 */
	public double getViewportSize(){
		return viewportSize;
	}

	/**
	 * forces graphview to invalide all views and caches.
	 * Normally there is no need to call this manually.
	 */
	public void redrawAll() {
		if (!config.isStaticVerticalLabels()) verlabels = null;
		if (!config.isStaticHorizontalLabels()) horlabels = null;
		labelTextHeight = null;
		horLabelTextWidth = null;
		verLabelTextWidth = null;

		invalidate();
		viewVerLabels.invalidate();
		graphViewContentView.invalidate();
	}

	/**
	 * removes all series
	 */
	public void removeAllSeries() {
		for (Series<?> s : graphSeries) {
			s.removeGraphView(this);
		}
		while (!graphSeries.isEmpty()) {
			graphSeries.remove(0);
		}
		redrawAll();
	}

	/**
	 * removes a series
	 * @param series series to remove
	 */
	public void removeSeries(Series<?> series) {
		series.removeGraphView(this);
		graphSeries.remove(series);
		redrawAll();
	}

	/**
	 * removes series
	 * @param index
	 */
	public void removeSeries(int index) {
		if (index < 0 || index >= graphSeries.size()) {
			throw new IndexOutOfBoundsException("No series at index " + index);
		}

		removeSeries(graphSeries.get(index));
	}

	/**
	 * scrolls to the last x-value
	 * @throws IllegalStateException if scrollable == false
	 */
	public void scrollToEnd() {
		if (!config.isScrollable()) throw new IllegalStateException("This GraphView is not scrollable.");
		double max = getMaxX(graphSeries, true);
		viewportStart = max-viewportSize;

		// don't clear labels width/height cache
		// so that the display is not flickering
		if (!config.isStaticVerticalLabels()) verlabels = null;
		if (!config.isStaticHorizontalLabels()) horlabels = null;

		invalidate();
		viewVerLabels.invalidate();
		graphViewContentView.invalidate();
	}

	/**
	 * set a custom label formatter
	 * @param customLabelFormatter
	 */
	public void setCustomLabelFormatter(CustomLabelFormatter customLabelFormatter) {
		this.labelFormatter = customLabelFormatter;
	}

	/**
	 * set custom graphview style
	 * @param style
	 */
	public void setGraphViewStyle(GraphViewStyle style) {
		config.setGraphViewStyle(style);
		labelTextHeight = null;
	}

	/**
	 * set's static horizontal labels (from left to right)
	 * @param horlabels if null, labels were generated automatically
	 */
	public void setHorizontalLabels(String[] horlabels) {
		config.setStaticHorizontalLabels(horlabels != null);
		this.horlabels = horlabels;
	}

	/**
	 * sets the title of graphview
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * set's static vertical labels (from top to bottom)
	 * @param verlabels if null, labels were generated automatically
	 */
	public void setVerticalLabels(String[] verlabels) {
		config.setStaticVerticalLabels(verlabels != null);
		this.verlabels = verlabels;
	}

	/**
	 * set's the viewport for the graph.
	 * @see #setManualYAxisBounds(double, double) to limit the y-viewport
	 * @param start x-value
	 * @param size
	 */
	public void setViewPort(double start, double size) {
		if (size<0) {
			throw new IllegalArgumentException("Viewport size must be greater than 0!");
		}
		viewportStart = start;
		viewportSize = size;
	}

    /**
     * Sets whether horizontal labels are drawn or not.
     *
     * @param showHorizontalLabels
     */
    public void setShowHorizontalLabels(boolean showHorizontalLabels) {
        config.setShowHorizontalLabels(showHorizontalLabels);
        redrawAll();
    }

    /**
     * Sets whether vertical labels are drawn or not.
     *
     * @param showVerticalLabels
     */
    public void setShowVerticalLabels(boolean showVerticalLabels) {
        config.setShowVerticalLabels(showVerticalLabels);
        if(showVerticalLabels) {
            addView(viewVerLabels, 0);
        } else {
            removeView(viewVerLabels);
        }
    }

    public double[] getViewPort() {
    	return new double[]{viewportStart, viewportSize};
	}

	
    public GraphViewConfig getConfig() {
		return config;
	}
}
