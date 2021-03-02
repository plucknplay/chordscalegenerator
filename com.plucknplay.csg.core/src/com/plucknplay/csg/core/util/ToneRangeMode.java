/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.core.util;

import com.plucknplay.csg.core.Constants;
import com.plucknplay.csg.core.model.Factory;
import com.plucknplay.csg.core.model.Instrument;
import com.plucknplay.csg.core.model.Note;
import com.plucknplay.csg.core.model.sets.InstrumentList;

public enum ToneRangeMode {

	RELATIVE_NOTES_MODE(24, 35, true), ACTIVE_INSTRUMENT(24, 71, false), ACTIVE_INSTRUMENT_WHITE_KEYS(24, 71, false), ACTIVE_INSTRUMENT_FULL_OCTAVES(
			24, 71, false), KEYBOARD_49(24, 72, false), KEYBOARD_61(24, 84, false), KEYBOARD_76(9, 84, false), PIANO_88(
			9, 96, false), IMPERIAL_GRAND_97(0, 96, false), USER_DEFINED(24, 71, false);

	private final int startNoteIndex;
	private final int endNoteIndex;
	private final boolean showOnlyRelativeNotes;

	private ToneRangeMode(final int startNoteIndex, final int endNoteIndex, final boolean showOnlyRelativeNotes) {
		this.startNoteIndex = startNoteIndex;
		this.endNoteIndex = endNoteIndex;
		this.showOnlyRelativeNotes = showOnlyRelativeNotes;
	}

	public ToneRange getToneRange() {
		Note startTone = Factory.getInstance().getNoteByIndex(startNoteIndex);
		Note endTone = Factory.getInstance().getNoteByIndex(endNoteIndex);
		final Instrument activeInstrument = InstrumentList.getInstance().getCurrentInstrument();
		if (activeInstrument != null
				&& (this == ACTIVE_INSTRUMENT || this == ACTIVE_INSTRUMENT_WHITE_KEYS || this == ACTIVE_INSTRUMENT_FULL_OCTAVES)) {
			startTone = activeInstrument.getDeepestNote();
			endTone = activeInstrument.getHighestNote();
			if (this == ACTIVE_INSTRUMENT_WHITE_KEYS) {
				if (startTone.hasAccidental()) {
					startTone = Factory.getInstance().getNote(startTone.getValue() - 1, startTone.getLevel());
				}
				if (endTone.hasAccidental()) {
					endTone = Factory.getInstance().getNote(endTone.getValue() + 1, endTone.getLevel());
				}
			} else if (this == ACTIVE_INSTRUMENT_FULL_OCTAVES) {
				if (startTone.getValue() != 0) {
					startTone = Factory.getInstance().getNote(0, startTone.getLevel());
				}
				if (endTone.getValue() < Constants.MAX_NOTES_VALUE) {
					endTone = Factory.getInstance().getNote(Constants.MAX_NOTES_VALUE, endTone.getLevel());
				}
			}
		}
		return new ToneRange(startTone, endTone);
	}

	public boolean showOnlyRelativeNotes() {
		return showOnlyRelativeNotes;
	}
}
