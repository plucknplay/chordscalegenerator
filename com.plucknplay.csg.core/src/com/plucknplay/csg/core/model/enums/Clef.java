/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.core.model.enums;

import com.plucknplay.csg.core.model.Factory;
import com.plucknplay.csg.core.model.ModelMessages;
import com.plucknplay.csg.core.model.Note;
import com.plucknplay.csg.core.model.NotePosition;

public enum Clef {

	NONE(ModelMessages.Clef_none, Factory.getInstance().getNote(0, 0)), G_TWO_OCTAVES_HIGHER(
			ModelMessages.Clef_g_two_octaves_higher, Factory.getInstance().getNote(4, 6)), G_ONE_OCTAVE_HIGHER(
			ModelMessages.Clef_g_one_octave_higher, Factory.getInstance().getNote(4, 5)), G_CHIAVETTE(
			ModelMessages.Clef_g_chiavette, Factory.getInstance().getNote(7, 4)), G_STANDARD(
			ModelMessages.Clef_g_standard, Factory.getInstance().getNote(4, 4)), G_ONE_OCTAVE_DEEPER(
			ModelMessages.Clef_g_one_octave_deeper, Factory.getInstance().getNote(4, 3)), C_SOPRANO(
			ModelMessages.Clef_c_sopran, Factory.getInstance().getNote(0, 4)), C_MEZZO_SOPRANO(
			ModelMessages.Clef_c_mezzosopran, Factory.getInstance().getNote(9, 3)), C_ALTO(ModelMessages.Clef_c_alt,
			Factory.getInstance().getNote(5, 3)), C_TENOR(ModelMessages.Clef_c_tenor, Factory.getInstance().getNote(2,
			3)), C_BARITON(ModelMessages.Clef_c_bariton, Factory.getInstance().getNote(11, 2)), F_CHIAVETTE(
			ModelMessages.Clef_f_chiavette, Factory.getInstance().getNote(11, 2)), F_STANDARD(
			ModelMessages.Clef_f_standard, Factory.getInstance().getNote(7, 2)), F_ONE_OCTAVE_DEEPER(
			ModelMessages.Clef_f_one_octave_deeper, Factory.getInstance().getNote(7, 1));

	private static final int FIRST_LAST_LINE_DIFFERENTIAL = 8;

	private String name;
	private Note firstLineNote;

	private Clef(final String name, final Note firstLineNote) {
		this.name = name;
		this.firstLineNote = firstLineNote;
	}

	public String getName() {
		return name;
	}

	public Note getFirstLineNote() {
		return firstLineNote;
	}

	public Note getLastLineNote() {
		return new NotePosition(new NotePosition(firstLineNote, true).getPosition() + FIRST_LAST_LINE_DIFFERENTIAL,
				Accidental.NONE).getNote();
	}
}
