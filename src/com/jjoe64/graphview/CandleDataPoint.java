package com.jjoe64.graphview;

import android.annotation.SuppressLint;

public class CandleDataPoint extends DataPoint {
	
	private double x, open, high, low, close;
	
	public CandleDataPoint(double x, double open, double high, double low, double close) {
		super(x, close);
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

	public double getOpen() {
		return open;
	}

	public double getHigh() {
		return high;
	}

	public double getLow() {
		return low;
	}

	public double getClose() {
		return close;
	}

	@SuppressLint("DefaultLocale")
	public String toString(){
		return String.format("Time %s Open %s High %s Low %s Close %s", String.valueOf(x), String.valueOf(open), String.valueOf(high), String.valueOf(low), String.valueOf(close));
	}
} 
