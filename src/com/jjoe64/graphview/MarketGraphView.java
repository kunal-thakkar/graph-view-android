package com.jjoe64.graphview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.graphics.RectF;
import android.util.AttributeSet;

public class MarketGraphView extends GraphView {

	private boolean showHairline = true;

	public MarketGraphView(Context context, String title){
		super(context, title);
	}
	
	public MarketGraphView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	protected void drawLegend(Canvas canvas, float height, float width) {
		float textSize = paint.getTextSize();
		int spacing = config.getGraphViewStyle().getLegendSpacing();
		int border = config.getGraphViewStyle().getLegendBorder();
		int legendWidth = (int) (textSize*title.length());
		int shapeSize = (int) (textSize*0.8d);
		float legendHeight = (shapeSize+spacing)*(1+graphSeries.size()+graphSeries2.size()) +2*border -spacing;
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
		
		int xValue = (int) (viewportStart + (xLocation/getWidth()) * viewportSize); //the x-Value of the graph where you touched
		int xIndex = Math.min(xValue, graphSeries.get(0).values.length);
		int legendCounter = -1;
		Series<?> series;

		paint.setColor(Color.WHITE);
		paint.setTextAlign(Align.LEFT);
		if(graphSeries.get(0).values.length > 0 && graphSeries.get(0).description != null){
			legendCounter++;

			if(showHairline){
				float newXLocation = (float)((xValue - viewportStart)/(viewportSize==0?graphSeries.get(0).values.length:viewportSize)) * getWidth();
				System.out.println("xVal "+xValue + " xInd " +xIndex + " oldLoc " + xLocation + " newX "+newXLocation);
				paint.setStrokeWidth(2);
				paint.setColor(config.getGraphViewStyle().getGridColor());
				canvas.drawLine(newXLocation, 0, newXLocation, getHeight(), paint);
			}

			paint.setColor(Color.WHITE);
			canvas.drawText(
				title + " " + formatLabel(graphSeries, xIndex, true),
				lLeft+border+spacing, 
				lTop+border+shapeSize+(legendCounter*(shapeSize+spacing)), 
				paint
			);
		}

		for (int i=0; i<graphSeries.size(); i++) {
			series = graphSeries.get(i);
			if (series.description != null) {
				legendCounter++;
				paint.setColor(series.style.color);
				if(!series.description.equals(""))canvas.drawRect(
					new RectF(
						lLeft+border, 
						lTop+border+(legendCounter*(shapeSize+spacing)), 
						lLeft+border+shapeSize, 
						lTop+border+(legendCounter*(shapeSize+spacing))+shapeSize
					), paint
				);
				paint.setColor(Color.WHITE);
				paint.setTextAlign(Align.LEFT);
				canvas.drawText(
					series.description + series.getLabel(xIndex),
					lLeft+border+shapeSize, 
					lTop+border+shapeSize+(legendCounter*(shapeSize+spacing)), 
					paint
				);
			}
		}
		for (int i=0; i<graphSeries2.size(); i++) {
			series = graphSeries2.get(i);
			legendCounter++;
			paint.setColor(series.style.color);
			canvas.drawRect(
				new RectF(
					lLeft+border, 
					lTop+border+(legendCounter*(shapeSize+spacing)), 
					lLeft+border+shapeSize, 
					lTop+border+(legendCounter*(shapeSize+spacing))+shapeSize
				), paint
			);
			paint.setColor(Color.WHITE);
			paint.setTextAlign(Align.LEFT);
			canvas.drawText(
				series.description + series.getLabel(xIndex),
				lLeft+border+spacing+shapeSize, 
				lTop+border+shapeSize+(legendCounter*(shapeSize+spacing)), 
				paint
			);
		}
	}

	public void setShowHairline(boolean showHairline){
		this.showHairline  = showHairline;
	}
	
}
