package com.jjoe64.graphview;


public class DataPoint implements DataPointInterface {

	private double x, y;
	
	public DataPoint(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	@Override
	public double getX() {
		return x;
	}

	@Override
	public double getY() {
		return y;
	}

}
