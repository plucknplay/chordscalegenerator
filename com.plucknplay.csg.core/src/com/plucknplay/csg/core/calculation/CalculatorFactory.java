/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.core.calculation;

import com.plucknplay.csg.core.internal.calculation.StandardCalculator;

public final class CalculatorFactory {

	private CalculatorFactory() {
	}

	public static ICalculator getCalculator(final boolean findChordsWithout1st, final boolean findChordsWithout3rd,
			final boolean findChordsWithout5th, final boolean preferBarres, final int maxResultsNumber,
			final int findChordsRestriction) {
		return new StandardCalculator(findChordsWithout1st, findChordsWithout3rd, findChordsWithout5th, preferBarres,
				maxResultsNumber, findChordsRestriction);
	}
}
