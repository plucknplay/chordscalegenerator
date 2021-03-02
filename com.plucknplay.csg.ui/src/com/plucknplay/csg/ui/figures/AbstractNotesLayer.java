/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.figures;

import com.plucknplay.csg.core.model.Note;
import com.plucknplay.csg.core.model.NotePosition;
import com.plucknplay.csg.core.model.enums.Accidental;
import com.plucknplay.csg.ui.model.NotesDraft;

public abstract class AbstractNotesLayer extends AntiAliasedLayer {

	private final NotesDraft notesDraft;
	private String displayMode;

	public AbstractNotesLayer(final NotesDraft notesDraft, final String displayMode) {
		this.notesDraft = notesDraft;
		this.displayMode = displayMode;
	}

	public NotesDraft getNotesDraft() {
		return notesDraft;
	}

	public void setDisplayMode(final String displayMode) {
		this.displayMode = displayMode;
	}

	public String getDisplayMode() {
		return displayMode;
	}

	protected int getRelativePosition(final Note note) {
		return getRelativePosition(new NotePosition(note, getNotesDraft().isSharpSignOn()));
	}

	protected int getRelativePosition(final NotePosition notePosition) {
		final NotePosition endNotePosition = notesDraft.getEndNotePosition();
		return Math.abs(endNotePosition.getPosition() - notePosition.getPosition());
	}

	protected int getYOffset() {
		int result = IFigureConstants.NOTES_OFFSET_Y;
		if (notesDraft.getNumberOfLedgerLinesAbove() < 3) {
			result += (new NotePosition(notesDraft.getLastLineNotePosition().getPosition()
					+ IFigureConstants.MIN_NOTE_POSITION_OFFSET, Accidental.NONE).getPosition() - notesDraft
					.getEndNotePosition().getPosition()) * IFigureConstants.NOTE_LINE_DISTANCE / 2;
		} else if (notesDraft.isOnTopOfLine(notesDraft.getToneRange().getEndTone())) {
			result += IFigureConstants.NOTE_LINE_DISTANCE / 2;
		}
		return result;
	}
}
