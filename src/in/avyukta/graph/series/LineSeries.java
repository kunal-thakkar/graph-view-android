package in.avyukta.graph.series;

import android.graphics.Canvas;
import android.graphics.Path;

import com.jjoe64.graphview.DataPoint;
import com.jjoe64.graphview.DataPointInterface;
import com.jjoe64.graphview.SeriesStyle;
import com.jjoe64.graphview.Series;

public class LineSeries extends Series<DataPoint> {

	public LineSeries(String description, SeriesStyle style, DataPoint[] values) {
		super(description, style, DataPoint.class, values);
	}

	@Override
	protected void drawSeries(Canvas canvas, float graphwidth, float graphheight, float border, double minX,
			double minY, double diffX, double diffY, float horstart, double viewPortStart, double viewPortSize) {
		DataPointInterface[] values = _values(viewPortStart, viewPortSize);
		// draw background
		double lastEndY = 0;
		double lastEndX = 0;

		// draw data
		paint.setStrokeWidth(style.thickness);
		paint.setColor(style.color);


		Path bgPath = null;
		if (drawBackground) {
			bgPath = new Path();
		}

		lastEndY = 0;
		lastEndX = 0;
		float firstX = 0;
		for (int i = 0; i < values.length; i++) {
			double valY = values[i].getY() - minY;
			double ratY = valY / diffY;
			double y = graphheight * ratY;

			double valX = values[i].getX() - minX;
			double ratX = valX / diffX;
			double x = graphwidth * ratX;

			if (i > 0) {
				float startX = (float) lastEndX + (horstart + 1);
				float startY = (float) (border - lastEndY) + graphheight;
				float endX = (float) x + (horstart + 1);
				float endY = (float) (border - y) + graphheight;

				// draw data point
				if (drawDataPoints) {
					//fix: last value was not drawn. Draw here now the end values
					canvas.drawCircle(endX, endY, dataPointsRadius, paint);
				}

				canvas.drawLine(startX, startY, endX, endY, paint);
				if (bgPath != null) {
					if (i==1) {
						firstX = startX;
						bgPath.moveTo(startX, startY);
					}
					bgPath.lineTo(endX, endY);
				}
			} else if (drawDataPoints) {
				//fix: last value not drawn as datapoint. Draw first point here, and then on every step the end values (above)
				float first_X = (float) x + (horstart + 1);
				float first_Y = (float) (border - y) + graphheight;
				canvas.drawCircle(first_X, first_Y, dataPointsRadius, paint);
			}
			lastEndY = y;
			lastEndX = x;
		}

		if (bgPath != null) {
			// end / close path
			bgPath.lineTo((float) lastEndX, graphheight + border);
			bgPath.lineTo(firstX, graphheight + border);
			bgPath.close();
			canvas.drawPath(bgPath, paintBackground);
		}
	}

}
