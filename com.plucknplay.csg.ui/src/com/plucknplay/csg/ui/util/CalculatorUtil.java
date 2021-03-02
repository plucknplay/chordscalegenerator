/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.util;

import org.eclipse.jface.preference.IPreferenceStore;

import com.plucknplay.csg.core.calculation.CalculatorFactory;
import com.plucknplay.csg.core.calculation.ICalculator;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.Preferences;

public final class CalculatorUtil {

	private CalculatorUtil() {
	}

	public static ICalculator getCalculator() {

		final IPreferenceStore prefs = Activator.getDefault().getPreferenceStore();
		final boolean findChordsWithout1st = prefs.getBoolean(Preferences.CALCULATOR_FIND_CHORDS_WITHOUT_1ST);
		final boolean findChordsWithout3rd = prefs.getBoolean(Preferences.CALCULATOR_FIND_CHORDS_WITHOUT_3RD);
		final boolean findChordsWithout5th = prefs.getBoolean(Preferences.CALCULATOR_FIND_CHORDS_WITHOUT_5TH);
		final int maxResultsNumber = prefs.getInt(Preferences.CALCULATOR_MAX_RESULTS_NUMBER);
		final boolean preferBarres = prefs.getBoolean(Preferences.CALCULATOR_BARRES_PREFERRED);
		final int findChordsRestriction = prefs.getInt(Preferences.CALCULATOR_FIND_CHORDS_RESTRICTIONS);

		return CalculatorFactory.getCalculator(findChordsWithout1st, findChordsWithout3rd, findChordsWithout5th,
				preferBarres, maxResultsNumber, findChordsRestriction);
	}
}
