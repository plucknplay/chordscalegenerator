/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.core.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.plucknplay.csg.core.model.Note;

public class NotesIterator implements Iterator<Note> {

	private final Note startNote;
	private final Note endNote;
	private Note currentNote;

	/**
	 * The constructor.
	 * 
	 * @param startNote
	 *            the start note, must not be null
	 * @param endNote
	 *            the end note, must not be null
	 */
	public NotesIterator(final Note startNote, final Note endNote) {
		if (startNote == null || endNote == null) {
			throw new IllegalArgumentException();
		}

		this.startNote = startNote;
		this.endNote = endNote;
		currentNote = null;
	}

	@Override
	public boolean hasNext() {
		if (currentNote == null) {
			return true;
		}
		if (startNote.equals(endNote)) {
			return false;
		}
		if (isAscending()) {
			return currentNote.compareTo(endNote) < 0;
		}
		return currentNote.compareTo(endNote) > 0;
	}

	@Override
	public Note next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}
		currentNote = currentNote == null ? startNote : isAscending() ? currentNote.getNextHigherNote() : currentNote
				.getNextDeeperNote();
		return currentNote;
	}

	private boolean isAscending() {
		return startNote.compareTo(endNote) < 0;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
