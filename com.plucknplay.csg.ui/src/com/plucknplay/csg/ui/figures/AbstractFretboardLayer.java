/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.figures;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;

import com.plucknplay.csg.core.model.Instrument;
import com.plucknplay.csg.core.model.sets.InstrumentList;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.model.FretboardDraft;
import com.plucknplay.csg.ui.util.enums.Position;

public class AbstractFretboardLayer extends AntiAliasedLayer {

	static {
		inlayFrets = Arrays.asList(new Integer[] { 3, 5, 7, 9, 12, 15, 17, 19, 21, 24 });
	}

	private static List<Integer> inlayFrets;

	private final FretboardDraft fretboardDraft;

	public AbstractFretboardLayer(final FretboardDraft fretboardDraft) {
		this.fretboardDraft = fretboardDraft;
	}

	public FretboardDraft getFretboardDraft() {
		return fretboardDraft;
	}

	protected List<Integer> getInlayFrets() {
		return inlayFrets;
	}

	/**
	 * Convenience method to retrieve the current instrument.
	 * 
	 * @return the current instrument
	 */
	protected Instrument getCurrentInstrument() {
		return InstrumentList.getInstance().getCurrentInstrument();
	}

	/**
	 * Returns the x start position of the given fret.
	 * 
	 * Note that this method takes the hand state into consideration.
	 * 
	 * @param fretNumber
	 *            the number of the fret, must be a positive value and smaller
	 *            than the fret count of the current instrument + 1
	 * 
	 * @return the x position of the given fret
	 */
	protected int getXFretPosition(final int fretNumber) {
		if (fretNumber > getCurrentInstrument().getFretCount() + 1) {
			throw new IllegalArgumentException();
		}

		final boolean isLeftHander = Activator.getDefault().isLeftHander();
		int factor = isLeftHander ? getCurrentInstrument().getFretCount() - fretNumber + 1 : fretNumber;
		if (!Activator.getDefault().getPreferenceStore()
				.getBoolean(Preferences.FRETBOARD_VIEW_SHOW_EMPTY_STRINGS_TWICE)
				&& isLeftHander) {
			factor -= 1;
		}
		return IFigureConstants.FRETBOARD_OFFSET_X + factor * IFigureConstants.FRET_WIDTH;
	}

	protected int getOffsetY() {
		final IPreferenceStore prefs = Activator.getDefault().getPreferenceStore();
		int offset = IFigureConstants.FRETBOARD_OFFSET_Y;
		final boolean showFretNumbersTop = prefs.getBoolean(Preferences.FRETBOARD_VIEW_SHOW_FRET_NUMBERS)
				&& Position.TOP == Position.valueOf(prefs.getString(Preferences.FRETBOARD_VIEW_FRET_NUMBERS_POSITION));
		final boolean showInlaysTop = prefs.getBoolean(Preferences.FRETBOARD_VIEW_SHOW_INLAYS)
				&& Position.TOP == Position.valueOf(prefs.getString(Preferences.FRETBOARD_VIEW_INLAYS_POSITION));
		if (showFretNumbersTop || showInlaysTop) {
			offset += IFigureConstants.FRETBOARD_OFFSET_Y2;
		}
		return offset;
	}
}
