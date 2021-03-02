/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.core.calculation;

import org.eclipse.osgi.util.NLS;

public final class CalculationMessages extends NLS {

	public static String StandardCalculator_found_chords;

	public static String StandardCalculator_scanning_frets;

	static {
		// initialize resource bundle
		NLS.initializeMessages("com.plucknplay.csg.core.calculation.CalculationMessages", CalculationMessages.class);
	}

	private CalculationMessages() {
	}
}
