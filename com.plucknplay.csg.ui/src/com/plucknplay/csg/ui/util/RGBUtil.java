/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.util;

import org.eclipse.swt.graphics.RGB;

/**
 * This class provides some useful utility methods.
 */
public final class RGBUtil {

	private static final String SEPARATOR = ","; //$NON-NLS-1$

	private RGBUtil() {
	}

	public static String convertRGBToId(final RGB rgb) {
		return rgb.red + SEPARATOR + rgb.green + SEPARATOR + rgb.blue;
	}

	public static RGB convertStringToRGB(final String id) {
		final RGB result = new RGB(255, 0, 0);

		final int firstIndex = id.indexOf(SEPARATOR);
		final int lastIndex = id.lastIndexOf(SEPARATOR);

		try {
			result.red = Integer.valueOf(id.substring(0, firstIndex));
			result.green = Integer.valueOf(id.substring(firstIndex + 1, lastIndex));
			result.blue = Integer.valueOf(id.substring(lastIndex + 1));
		} catch (final NumberFormatException e) {
		}
		return result;
	}
}
