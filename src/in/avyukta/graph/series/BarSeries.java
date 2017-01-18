package in.avyukta.graph.series;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint.Align;

import com.jjoe64.graphview.DataPoint;
import com.jjoe64.graphview.DataPointInterface;
import com.jjoe64.graphview.SeriesStyle;
import com.jjoe64.graphview.Series;

public class BarSeries extends Series<DataPoint> {

	private boolean drawValuesOnTop;
	private int valuesOnTopColor = Color.WHITE;

	public BarSeries(String description, SeriesStyle style, DataPoint[] values) {
		super(description, style, DataPoint.class, values);
	}

	@Override
	protected void drawSeries(Canvas canvas, float graphwidth, float graphheight, float border, double minX,
			double minY, double diffX, double diffY, float horstart, double viewPortStart, double viewPortSize) {
		DataPointInterface[] values = _values(viewPortStart, viewPortSize);
		float colwidth = graphwidth / (values.length);

		paint.setStrokeWidth(style.thickness);

		float offset = 0;

		// draw data
		for (int i = 0; i < values.length; i++) {
			float valY = (float) (values[i].getY() - minY);
			float ratY = (float) (valY / diffY);
			float y = graphheight * ratY;

			// hook for value dependent color
			if (style.getValueDependentColor() != null) {
				paint.setColor(style.getValueDependentColor().get(values[i]));
			} else {
				paint.setColor(style.color);
			}

			float left = (i * colwidth) + horstart -offset;
			float top = (border - y) + graphheight;
			float right = ((i * colwidth) + horstart) + (colwidth - 1) -offset;
			canvas.drawRect(left, top, right, graphheight + border - 1, paint);

			// -----Set values on top of graph---------
			if (drawValuesOnTop) {
				top -= 4;
				if (top<=border) top+=border+4;
				paint.setTextAlign(Align.CENTER);
				paint.setColor(valuesOnTopColor );
				//canvas.drawText(formatLabel(values[i].getY(), false), (left+right)/2, top, paint);
			}
		}
	}

	public boolean getDrawValuesOnTop() {
		return drawValuesOnTop;
	}

	public int getValuesOnTopColor() {
		return valuesOnTopColor;
	}

	/**
	 * You can set the flag to let the GraphView draw the values on top of the bars
	 * @param drawValuesOnTop
	 */
	public void setDrawValuesOnTop(boolean drawValuesOnTop) {
		this.drawValuesOnTop = drawValuesOnTop;
	}

	public void setValuesOnTopColor(int valuesOnTopColor) {
		this.valuesOnTopColor = valuesOnTopColor;
	}
}
