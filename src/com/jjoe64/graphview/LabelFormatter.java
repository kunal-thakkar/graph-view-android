package com.jjoe64.graphview;

import java.text.NumberFormat;

public class LabelFormatter implements CustomLabelFormatter{

	private int fractionDigits = 1;
	private final NumberFormat[] numberformatter;
	
	public LabelFormatter() {
		this(1);
	}

	/**
	 *  if (highestvalue - lowestvalue < 0.1) {
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
	 * @param fractionDigits
	 */
	public LabelFormatter(int fractionDigits) {
		this.fractionDigits = fractionDigits;
		this.numberformatter = new NumberFormat[2];
		
	}

	@Override
	public String formatLabel(double value, boolean isValueX) {
		int i = isValueX ? 1 : 0;
		if (numberformatter[i] == null) {
			numberformatter[i] = NumberFormat.getNumberInstance();
			numberformatter[i].setMaximumFractionDigits(fractionDigits);
		}
		return numberformatter[i].format(value);
	}

}
