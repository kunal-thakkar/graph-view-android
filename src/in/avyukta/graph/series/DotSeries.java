package in.avyukta.graph.series;

import android.graphics.Canvas;

import com.jjoe64.graphview.DataPoint;
import com.jjoe64.graphview.DataPointInterface;
import com.jjoe64.graphview.SeriesStyle;
import com.jjoe64.graphview.Series;

public class DotSeries extends Series<DataPoint>{

	public DotSeries(String description, SeriesStyle style, DataPoint[] values) {
		super(description, style, DataPoint.class, values);
	}

	@Override
	protected void drawSeries(Canvas canvas, float graphwidth, float graphheight, float border, double minX,
			double minY, double diffX, double diffY, float horstart, double viewPortStart, double viewPortSize) {

		// draw background
		// draw data
		paint.setStrokeWidth(style.thickness);
		paint.setColor(style.color);
		DataPointInterface[] values = _values(viewPortStart, viewPortSize);
		for (int i = 0; i < values.length; i++) {
			double y = graphheight * ((values[i].getY() - minY) / diffY);
			double x = graphwidth * ((values[i].getX() - minX) / diffX);
			float endX = (float) (x + (horstart + 1));
			float endY = (float) (border - y) + graphheight;
			canvas.drawCircle(endX, endY, dataPointsRadius, paint);
		}
	}

}
