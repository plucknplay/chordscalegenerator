/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.views;

import java.util.List;

import com.plucknplay.csg.core.model.FretboardPosition;
import com.plucknplay.csg.core.model.Griptable;
import com.plucknplay.csg.core.model.Instrument;
import com.plucknplay.csg.core.model.Interval;
import com.plucknplay.csg.core.model.sets.InstrumentList;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.Preferences;

/**
 * This class is responsible for sorting the entries in the chord results view.
 */
public class ChordResultsViewSorter extends AbstractTableViewerSorter {

	private static final long serialVersionUID = 5157249384424427765L;

	@Override
	protected Integer[] getDefaultColumnSorting() {
		return getIntegerArray(Activator.getDefault().getPreferenceStore()
				.getString(Preferences.CHORD_RESULTS_VIEW_COLUMN_SORTING_ORDER));
	}

	@Override
	protected int[] getDefaultDirections() {
		return getIntArray(Activator.getDefault().getPreferenceStore()
				.getString(Preferences.CHORD_RESULTS_VIEW_COLUMN_SORTING_DIRECTIONS));
	}

	@Override
	public int compare(final int column, final Object e1, final Object e2) {

		final boolean preferBarrees = Activator.getDefault().getPreferenceStore()
				.getBoolean(Preferences.CALCULATOR_BARRES_PREFERRED);

		final Griptable g1 = (Griptable) e1;
		final Griptable g2 = (Griptable) e2;

		int result = 0;

		switch (column) {
		case 0: // selection
			result = -1 * Boolean.valueOf(g1.isSelected()).compareTo(Boolean.valueOf(g2.isSelected()));
			break;
		case 1: // chord
			result = g1.getChord().toString().compareTo(g2.getChord().toString());
			break;
		case 2: // level
			result = Integer.valueOf(g1.getFingering(preferBarrees).getLevel()).compareTo(
					g2.getFingering(preferBarrees).getLevel());
			break;
		case 3: // bass
			result = g1.getBassTone().compareTo(g2.getBassTone());
			break;
		case 4: // lead
			result = g1.getLeadTone().compareTo(g2.getLeadTone());
			break;
		case 5: // notes
			result = compareIntervals(g1, g2);
			break;
		case 6: // intervals
			result = compareIntervals(g1, g2);
			break;
		case 7: // frets
			result = compareFrets(g1, g2);
			break;
		case 8: // min string
			result = Integer.valueOf(g1.getMinString()).compareTo(Integer.valueOf(g2.getMinString()));
			break;
		case 9: // max string
			result = Integer.valueOf(g1.getMaxString()).compareTo(Integer.valueOf(g2.getMaxString()));
			break;
		case 10: // string span
			result = Integer.valueOf(g1.getStringSpan()).compareTo(Integer.valueOf(g2.getStringSpan()));
			break;
		case 11: // min fret
			result = Integer.valueOf(g1.getMinFret()).compareTo(Integer.valueOf(g2.getMinFret()));
			break;
		case 12: // max fret
			result = Integer.valueOf(g1.getMaxFret()).compareTo(Integer.valueOf(g2.getMaxFret()));
			break;
		case 13: // fret span
			result = Integer.valueOf(g1.getFretSpan()).compareTo(Integer.valueOf(g2.getFretSpan()));
			break;
		case 14: // grip distance
			result = Double.valueOf(g1.getGripDistance()).compareTo(Double.valueOf(g2.getGripDistance()));
			break;
		case 15: // number of empty strings
			result = Integer.valueOf(g1.getEmptyStringsCount()).compareTo(Integer.valueOf(g2.getEmptyStringsCount()));
			break;
		case 16: // number of muted strings
			result = Integer.valueOf(g1.getMutedStringsCount()).compareTo(Integer.valueOf(g2.getMutedStringsCount()));
			break;
		case 17: // doubled tones
			result = Boolean.valueOf(g1.hasDoubledTones()).compareTo(Boolean.valueOf(g2.hasDoubledTones()));
			break;
		default:
			result = 0;
			break;
		}

		return result * getDirection(column);
	}

	private int compareIntervals(final Griptable g1, final Griptable g2) {

		final Instrument instrument = InstrumentList.getInstance().getCurrentInstrument();
		final List<FretboardPosition> fbps1 = g1.getFretboardPositions(true);
		final List<FretboardPosition> fbps2 = g2.getFretboardPositions(true);

		for (int i = fbps1.size() - 1; i >= 0; i--) {

			final FretboardPosition fbp1 = fbps1.get(i);
			final FretboardPosition fbp2 = fbps2.get(i);

			final int fret1 = fbp1.getFret();
			final int fret2 = fbp2.getFret();

			if (fret1 == -1 && fret2 > -1) {
				return ASCENDING;
			}
			if (fret2 == -1 && fret1 > -1) {
				return DESCENDING;
			}
			if (fret1 == -1 && fret2 == -1) {
				continue;
			}

			final Interval i1 = g1.getChord().getRootNote().calcInterval(instrument.getNote(fbp1));
			final Interval i2 = g2.getChord().getRootNote().calcInterval(instrument.getNote(fbp2));

			final int result = i1.compareTo(i2);
			if (result != 0) {
				return result;
			}
		}
		return 0;
	}

	private int compareFrets(final Griptable g1, final Griptable g2) {

		final List<FretboardPosition> fbps1 = g1.getFretboardPositions(true);
		final List<FretboardPosition> fbps2 = g2.getFretboardPositions(true);

		for (int i = fbps1.size() - 1; i >= 0; i--) {
			final int fret1 = fbps1.get(i).getFret();
			final int fret2 = fbps2.get(i).getFret();

			if (fret1 < fret2) {
				return fret1 < 0 ? ASCENDING : DESCENDING;
			}
			if (fret1 > fret2) {
				return fret2 < 0 ? DESCENDING : ASCENDING;
			}
		}
		return 0;
	}

	@Override
	protected void storeSortingOrderString(final String prefValue) {
		Activator.getDefault().getPreferenceStore()
				.setValue(Preferences.CHORD_RESULTS_VIEW_COLUMN_SORTING_ORDER, prefValue);
	}

	@Override
	protected void storeDirectionsString(final String prefValue) {
		Activator.getDefault().getPreferenceStore()
				.setValue(Preferences.CHORD_RESULTS_VIEW_COLUMN_SORTING_DIRECTIONS, prefValue);
	}
}
