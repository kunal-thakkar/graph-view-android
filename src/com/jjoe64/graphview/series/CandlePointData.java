package com.jjoe64.graphview.series;

public class CandlePointData implements CandlePointInterface{

	double x, open, high, low, close;
	
	public CandlePointData(double x, double open, double high, double low, double close) {
		this.x = x;
		this.open = open;
		this.high = high;
		this.low = low;
		this.close = close;
	}
	
	@Override
	public double getX() {
		return x;
	}

	@Override
	public double getY() {
		return close;
	}

	@Override
	public double getOpen() {
		return open;
	}

	@Override
	public double getHigh() {
		return high;
	}

	@Override
	public double getLow() {
		return low;
	}

	@Override
	public double getClose() {
		return close;
	}

}
