/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.util;

import org.eclipse.swt.widgets.Text;

/**
 * Utility class which offers some useful static method for ui concerns.
 */
public final class ViewUtil {

	private ViewUtil() {
	}

	/**
	 * Returns the integer value of a given text widget. If there is no valid
	 * int value 0 will be returned.
	 * 
	 * @param text
	 *            the text widget, must not be null
	 * 
	 * @return the integer value of a given text widget, or 0 if there are any
	 *         problems
	 */
	public static int getIntValue(final Text text) {
		if (text == null) {
			throw new IllegalArgumentException();
		}
		try {
			return Integer.parseInt(text.getText());
		} catch (final NumberFormatException e) {
			return 0;
		}
	}

	/**
	 * Returns the double value of a given text widget. If there is no valid
	 * double value 0.0 will be returned.
	 * 
	 * @param text
	 *            the text widget, must not be null
	 * 
	 * @return the integer value of a given text widget, or 0 if there are any
	 *         problems
	 */
	public static double getDoubleValue(final Text text) {
		if (text == null) {
			throw new IllegalArgumentException();
		}
		try {
			return Double.parseDouble(text.getText());
		} catch (final NumberFormatException e) {
			return 0.0f;
		}
	}
}
