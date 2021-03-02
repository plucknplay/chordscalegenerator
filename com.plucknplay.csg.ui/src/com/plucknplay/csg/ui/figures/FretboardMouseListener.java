/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.figures;

import java.util.Collections;

import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.jface.preference.IPreferenceStore;

import com.plucknplay.csg.core.model.FretboardPosition;
import com.plucknplay.csg.core.model.Instrument;
import com.plucknplay.csg.core.model.Note;
import com.plucknplay.csg.core.model.sets.InstrumentList;
import com.plucknplay.csg.sound.SoundConstants;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.Preferences;

public class FretboardMouseListener implements MouseListener {

	private Note note;

	private final IPreferenceStore prefs;
	private final Instrument currentInstrument;
	private FretboardPosition fbp;

	public FretboardMouseListener(final Note note, final FretboardPosition fbp) {
		this.note = note;
		this.fbp = fbp;

		// determine preferences
		prefs = Activator.getDefault().getPreferenceStore();
		currentInstrument = InstrumentList.getInstance().getCurrentInstrument();
	}

	@Override
	public void mouseDoubleClicked(final MouseEvent me) {
	}

	@Override
	public void mousePressed(final MouseEvent me) {
		if (me.button != 1) {
			return;
		}

		final int toneLength = prefs.getInt(Preferences.SOUND_FRETBOARD_VIEW_TONE_LENGTH);

		if (note != null) {
			Activator
					.getDefault()
					.getSoundMachine()
					.play(Collections.singletonList(note), Collections.singletonList(fbp), currentInstrument,
							currentInstrument.getMidiInstrumentNumber(), toneLength, 0,
							SoundConstants.ASCENDING_PATTERN, false);
		}
	}

	@Override
	public void mouseReleased(final MouseEvent me) {
	}

	public void setNote(final Note note) {
		this.note = note;
	}

	public void setFretboardPosition(final FretboardPosition fbp) {
		this.fbp = fbp;
	}
}
