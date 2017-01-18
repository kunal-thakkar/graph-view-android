package in.avyukta.graph.series;

import android.graphics.Canvas;
import android.graphics.Color;

import com.jjoe64.graphview.CandleDataPoint;
import com.jjoe64.graphview.SeriesStyle;
import com.jjoe64.graphview.Series;

public class CandleSeries extends Series<CandleDataPoint> {

	public CandleSeries(String description, SeriesStyle style, CandleDataPoint[] values) {
		super(description, style, CandleDataPoint.class, values);
	}

	@Override
	protected void drawSeries(Canvas canvas, float graphwidth, float graphheight, float border, double minX,
			double minY, double diffX, double diffY, float horstart, double viewPortStart, double viewPortSize) {
		CandleDataPoint[] values = _values(viewPortStart, viewPortSize);
		float candleBoxwidth = Math.min(8, graphwidth / (values.length));
		float candleLinewidth = candleBoxwidth/4;
		CandleDataPoint candleData;
		for (int i = 0; i < values.length; i++) {
			candleData = values[i];
			float open = (float) (graphheight * ((candleData.getOpen() - minY) / diffY));
			float high = (float) (graphheight * ((candleData.getHigh() - minY) / diffY));
			float low = (float) (graphheight * ((candleData.getLow() - minY) / diffY));
			float close = (float) (graphheight * ((candleData.getClose() - minY) / diffY));
			float x = (float) (graphwidth * ((values[i].getX() - minX) / diffX));

			float endX = (float) (x + (horstart + 1));
			float endOpen = (float) ((border - open) + graphheight);
			float endHigh = (float) ((border - high) + graphheight);
			float endLow = (float) ((border - low) + graphheight);
			float endClose = (float) ((border - close) + graphheight);
			
			paint.setColor(endOpen <= endClose?Color.RED:Color.GREEN);

			paint.setStrokeWidth(candleLinewidth);
			canvas.drawLine(endX, endHigh, endX, endLow, paint);

			paint.setStrokeWidth(candleBoxwidth);
			canvas.drawLine(endX, endOpen, endX, endClose, paint);
		}
		paint.setColor(style.color);
		paint.setStrokeWidth(style.thickness);
	}

	public double getMaxY(double viewPortStart, double viewPortSize){
		double largest = Integer.MIN_VALUE;
		CandleDataPoint[] values = _values(viewPortStart, viewPortSize);
		for (int ii=0; ii<values.length; ii++)
			if (values[ii].getY() > largest)
				largest = values[ii].getHigh();
		return largest;
	}
	
	public double getMinY(double viewPortStart, double viewPortSize){
		double smallest = Integer.MAX_VALUE;
		CandleDataPoint[] values = _values(viewPortStart, viewPortSize);
		for (int ii=0; ii<values.length; ii++)
			if (values[ii].getY() < smallest)
				smallest = values[ii].getLow();
		return smallest;
	}
	
	public String getLabel(int xIndex){
		if(xIndex < 0 || values.length <= xIndex) return "";
		int index = xIndex - (int) values[0].getX();
		if(index < 0) return "";
		CandleDataPoint data = values[index];
		return String.format(" Open %s High %s Low %s Close %s",
			numberFormat.format(data.getOpen()),
			numberFormat.format(data.getHigh()),
			numberFormat.format(data.getLow()),
			numberFormat.format(data.getClose())
		);
	}

}
