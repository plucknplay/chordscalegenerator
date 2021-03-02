/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.views;

import org.eclipse.jface.preference.IPreferenceStore;

import com.plucknplay.csg.core.model.ScaleResult;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.Preferences;

/**
 * This class is responsible for sorting the entries in the scale results view.
 */
public class ScaleResultsViewSorter extends AbstractTableViewerSorter {

	private static final long serialVersionUID = 217967216361599377L;

	@Override
	protected Integer[] getDefaultColumnSorting() {
		return getIntegerArray(Activator.getDefault().getPreferenceStore()
				.getString(Preferences.SCALE_RESULTS_VIEW_COLUMN_SORTING_ORDER));
	}

	@Override
	protected int[] getDefaultDirections() {
		return getIntArray(Activator.getDefault().getPreferenceStore()
				.getString(Preferences.SCALE_RESULTS_VIEW_COLUMN_SORTING_DIRECTIONS));
	}

	@Override
	public int compare(final int column, final Object e1, final Object e2) {

		final ScaleResult g1 = (ScaleResult) e1;
		final ScaleResult g2 = (ScaleResult) e2;

		final IPreferenceStore prefs = Activator.getDefault().getPreferenceStore();
		final String notesMode = prefs.getString(Preferences.NOTES_MODE);

		int result = 0;

		switch (column) {
		case 0: // scale
			final int ftCompare = g1.getRootNote().compareTo(g2.getRootNote());
			result = ftCompare != 0 ? ftCompare : g1.getName().compareTo(g2.getName());
			break;
		case 1: // intervals
			result = g1.getIntervalString().compareTo(g2.getIntervalString());
			break;
		case 2: // notes
			result = g1.getNoteString(notesMode).compareTo(g2.getNoteString(notesMode));
			break;
		case 3: // root note
			result = Boolean.valueOf(g1.isRootNoteContained()).compareTo(Boolean.valueOf(g2.isRootNoteContained()));
			break;
		case 4: // coverage
			result = Integer.valueOf(g1.getCoverage()).compareTo(Integer.valueOf(g2.getCoverage()));
			break;
		default:
			result = 0;
			break;
		}

		return result * getDirection(column);
	}

	@Override
	protected void storeSortingOrderString(final String prefValue) {
		Activator.getDefault().getPreferenceStore()
				.setValue(Preferences.SCALE_RESULTS_VIEW_COLUMN_SORTING_ORDER, prefValue);
	}

	@Override
	protected void storeDirectionsString(final String prefValue) {
		Activator.getDefault().getPreferenceStore()
				.setValue(Preferences.SCALE_RESULTS_VIEW_COLUMN_SORTING_DIRECTIONS, prefValue);
	}
}
