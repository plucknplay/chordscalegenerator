/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.core.model;

public enum Unit {

	mm, inch;

	private static final double INCH_TO_MM = 25.400000000000309880000000003781d;
	private static final double MM_TO_INCH = 0.039370078740157d;

	/**
	 * Returns an array of Stings which contains the name of all elements of
	 * this enumeration.
	 * 
	 * @return an array of Stings which contains the name of all elements of
	 *         this enumeration
	 */
	public static String[] printableValues() {
		final String[] result = new String[values().length];
		for (int i = 0; i < values().length; i++) {
			result[i] = values()[i].toString();
		}
		return result;
	}

	/**
	 * Converts a given value of a unit into the corresponding value of another
	 * unit.
	 * 
	 * @param sourceUnit
	 *            the unit of the source value, must not be null
	 * @param sourceValue
	 *            the value to convert
	 * @param destinationUnit
	 *            the unit of the destination value, must not be null
	 * 
	 * @return the converted value
	 */
	public static double convert(final Unit sourceUnit, final double sourceValue, final Unit destinationUnit) {
		if (sourceUnit == null || destinationUnit == null) {
			throw new IllegalArgumentException();
		}

		if (sourceUnit == destinationUnit) {
			return sourceValue;
		}

		if (sourceUnit == mm && destinationUnit == inch) {
			return sourceValue * MM_TO_INCH;
		} else if (sourceUnit == inch && destinationUnit == mm) {
			return sourceValue * INCH_TO_MM;
		}
		return 0.0f;
	}
}
