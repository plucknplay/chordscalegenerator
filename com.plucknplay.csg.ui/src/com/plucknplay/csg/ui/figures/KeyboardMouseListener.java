/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.figures;

import java.util.Collections;

import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.jface.preference.IPreferenceStore;

import com.plucknplay.csg.core.model.Note;
import com.plucknplay.csg.sound.SoundConstants;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.Preferences;

public class KeyboardMouseListener implements MouseListener {

	private Note note;

	private final IPreferenceStore prefs;

	public KeyboardMouseListener(final Note note) {
		this.note = note;

		// determine preferences
		prefs = Activator.getDefault().getPreferenceStore();
	}

	@Override
	public void mouseDoubleClicked(final MouseEvent me) {
	}

	@Override
	public void mousePressed(final MouseEvent me) {
		if (me.button != 1) {
			return;
		}

		final int toneLength = prefs.getInt(Preferences.SOUND_KEYBOARD_VIEW_TONE_LENGTH);
		final int midiKeyboardInstrumentNumber = prefs.getInt(Preferences.SOUND_KEYBOARD_INSTRUMENT);

		if (note != null) {
			Activator
					.getDefault()
					.getSoundMachine()
					.play(Collections.singletonList(note), null, null, midiKeyboardInstrumentNumber, toneLength, 0,
							SoundConstants.ASCENDING_PATTERN, false);
		}
	}

	@Override
	public void mouseReleased(final MouseEvent me) {
	}

	public void setNote(final Note note) {
		this.note = note;
	}
}
