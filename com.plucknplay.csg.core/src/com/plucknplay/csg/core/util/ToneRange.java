/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.core.util;

import java.util.Iterator;

import com.plucknplay.csg.core.model.Note;

public class ToneRange {

	private final Note startTone;
	private final Note endTone;

	public ToneRange(final Note startTone, final Note endTone) {
		this.startTone = startTone;
		this.endTone = endTone;
	}

	public ToneRange(final ToneRange other) {
		startTone = other.startTone;
		endTone = other.endTone;
	}

	public Note getStartTone() {
		return startTone;
	}

	public Note getEndTone() {
		return endTone;
	}

	public boolean isInside(final Note note) {
		return note != null && note.compareTo(startTone) >= 0 && note.compareTo(endTone) <= 0;
	}

	public Iterator<Note> iterator() {
		return new NotesIterator(startTone, endTone);
	}

	@Override
	public String toString() {
		return "(" + startTone.getAbsoluteName() + ", " + endTone.getAbsoluteName() + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
}
