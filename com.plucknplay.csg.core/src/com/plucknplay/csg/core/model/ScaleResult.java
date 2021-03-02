/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.core.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.plucknplay.csg.core.Constants;

public class ScaleResult extends Scale {

	private static final long serialVersionUID = -1277131907082836803L;

	private final List<Note> referenceNotes = new ArrayList<Note>();
	private List<Interval> referenceIntervals = new ArrayList<Interval>();

	private boolean recalculate;

	/**
	 * The constructor.
	 * 
	 * @param scale
	 *            the scale this scale result represents, must not be null
	 * @param rootNote
	 *            the root note, must not be null
	 */
	public ScaleResult(final Scale scale, final Note rootNote) {
		super(scale);
		setRootNote(rootNote);
		recalculate = true;
	}

	/* --- reference notes --- */

	/**
	 * Adds a reference note to the list of reference notes of this scale
	 * result.
	 * 
	 * @param note
	 *            a reference note, must not be null
	 */
	public void addReferenceNote(final Note note) {
		if (note == null) {
			throw new IllegalArgumentException();
		}

		final Note theNote = Factory.getInstance().getNote(note.getValue());
		if (!referenceNotes.contains(theNote)) {
			referenceNotes.add(theNote);
			recalculate = true;
		}
	}

	/**
	 * Adds a list of notes to the list of references notes of this scale
	 * result.
	 * 
	 * @param notes
	 *            a list of reference notes, must not be null
	 */
	public void addReferenceNotes(final Collection<Note> notes) {
		if (notes == null) {
			throw new IllegalArgumentException();
		}

		for (final Note note : notes) {
			addReferenceNote(note);
		}
	}

	/**
	 * Removes a note from the list of reference notes of this scale result.
	 * 
	 * @param note
	 *            a reference note, must not be null
	 */
	public void removeReferenceNote(final Note note) {
		if (note == null) {
			throw new IllegalArgumentException();
		}

		final Note theNote = Factory.getInstance().getNote(note.getValue());
		if (referenceNotes.contains(theNote)) {
			referenceNotes.remove(theNote);
			recalculate = true;
		}
	}

	/**
	 * Removes a list of notes from the list of reference notes of this scale
	 * result.
	 * 
	 * @param notes
	 *            a list of reference notes, must not be null
	 */
	public void removeReferenceNotes(final Collection<Note> notes) {
		if (notes == null) {
			throw new IllegalArgumentException();
		}

		for (final Note note : notes) {
			removeReferenceNote(note);
		}
	}

	@Override
	public void updateIntervalNames() {
		super.updateIntervalNames();
		referenceIntervals = null;
	}

	/**
	 * Returns a sorted list of the reference notes of this scale result.
	 * 
	 * @return a sorted list of the reference notes of this scale result
	 */
	public List<Note> getReferenceNotes() {
		if (recalculate) {
			Collections.sort(referenceNotes);
		}
		return referenceNotes;
	}

	/**
	 * Returns a sorted list of the reference intervals of this scale result.
	 * 
	 * @return a sorted list of the reference intervals of this scale result
	 */
	public List<Interval> getReferenceIntervals() {
		if (referenceIntervals == null || recalculate) {
			referenceIntervals = new ArrayList<Interval>();
			for (final Note note : getReferenceNotes()) {
				final Interval interval = getRootNote().calcInterval(note);
				if (!referenceIntervals.contains(interval)) {
					referenceIntervals.add(interval);
				}
			}
			Collections.sort(referenceIntervals);
		}
		return referenceIntervals;
	}

	/**
	 * Returns a printable string of all intervals of this scale result.
	 * 
	 * @return a printable string of all intervals of this griptable
	 */
	public String getIntervalString() {
		return getIntervalString(getSortedIntervals());
	}

	/**
	 * Returns the sorted list of intervals of this scale result.
	 * 
	 * @return the sorted list of intervals of this scale result
	 */
	public List<Interval> getSortedIntervals() {
		final List<Interval> intervals = new ArrayList<Interval>(getIntervals());
		Collections.sort(intervals);
		return intervals;
	}

	/**
	 * Returns a printable string of all notes of this scale result.
	 * 
	 * @param notesMode
	 *            the mode each note shall be represented, must not be null,
	 *            valid values are '# and b', 'only b' and 'only #'
	 * 
	 * @return a printable string of all notes of this scale result
	 */
	public String getNoteString(final String notesMode) {
		return getNoteString(getSortedNotes(), notesMode);
	}

	/**
	 * Returns the sorted list of notes of this scale result.
	 * 
	 * @return the sorted list of notes of this scale result
	 */
	public List<Note> getSortedNotes() {
		final List<Note> notes = new ArrayList<Note>(getNotes());
		Collections.sort(notes);
		return notes;
	}

	/**
	 * Returns a printable string for the given intervals list.
	 * 
	 * @param intervals
	 *            the intervals, must not be null
	 * 
	 * @return a printable string for the given intervals list
	 */
	private String getIntervalString(final List<Interval> intervals) {
		if (intervals == null) {
			throw new IllegalArgumentException();
		}

		final StringBuffer buf = new StringBuffer();
		for (final Iterator<Interval> iter = intervals.iterator(); iter.hasNext();) {
			final Interval interval = iter.next();
			if (interval == null) {
				continue;
			}
			buf.append(getIntervalName(interval));
			if (iter.hasNext()) {
				buf.append("-"); //$NON-NLS-1$
			}
		}
		return buf.toString();
	}

	/**
	 * Returns a printable string for the given notes list.
	 * 
	 * @param notes
	 *            the notes, must not be null
	 * @param notesMode
	 *            the mode each note shall be represented, must not be null,
	 *            valid values are '# and b', 'only b' and 'only #'
	 * 
	 * @return a printable string for the given notes list
	 */
	private String getNoteString(final List<Note> notes, final String notesMode) {
		if (notesMode == null || !notesMode.equals(Constants.NOTES_MODE_CROSS_AND_B)
				&& !notesMode.equals(Constants.NOTES_MODE_ONLY_CROSS) && !notesMode.equals(Constants.NOTES_MODE_ONLY_B)) {
			throw new IllegalArgumentException();
		}

		final StringBuffer buf = new StringBuffer();
		for (final Iterator<Note> iter = notes.iterator(); iter.hasNext();) {
			final Note note = iter.next();
			buf.append(notesMode.equals(Constants.NOTES_MODE_ONLY_CROSS) ? note.getRelativeNameAug() : notesMode
					.equals(Constants.NOTES_MODE_ONLY_B) ? note.getRelativeNameDim() : note.getRelativeName());
			if (iter.hasNext()) {
				buf.append("-"); //$NON-NLS-1$
			}
		}
		return buf.toString();
	}

	/**
	 * Returns true if the root note of the underlying scale is contained in the
	 * reference notes list, or false otherwise.
	 * 
	 * @return true if the root note of the underlying scale is contained in the
	 *         reference notes list, or false otherwise
	 */
	public boolean isRootNoteContained() {
		return referenceNotes.contains(getRootNote());
	}

	/**
	 * Returns true if this scale result is a valid one, or false otherwise.
	 * 
	 * The reference notes of a valid scale result must be subset of the
	 * underlying scales notes (or equal to the scales notes).
	 * 
	 * @return true if this scale result is a valid one, or false otherwise
	 */
	public boolean isValid() {
		// one has to ensure that in both notes collections are only relative
		// notes
		final Set<Integer> scaleNoteValues = getNoteValues();
		final Set<Note> scaleNotes = new HashSet<Note>();
		for (final Integer integer : scaleNoteValues) {
			scaleNotes.add(Factory.getInstance().getNote(integer));
		}

		return referenceNotes.size() <= scaleNotes.size() && scaleNotes.containsAll(referenceNotes);
	}

	/**
	 * Returns the coverage in percent of a valid scale result.
	 * 
	 * @return the coverage in percent of a valid scale result
	 */
	public int getCoverage() {
		if (!isValid()) {
			return 0;
		}

		final Set<Note> scaleNotes = getNotes();
		final int result = 100 * referenceNotes.size() / scaleNotes.size();
		return result;
	}
}
