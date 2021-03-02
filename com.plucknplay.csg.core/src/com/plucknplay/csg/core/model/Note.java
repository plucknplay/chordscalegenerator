/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.core.model;

import java.io.Serializable;

import com.plucknplay.csg.core.Constants;
import com.plucknplay.csg.core.util.NamesUtil;

/**
 * This class represents a note. A note is simply defined by its (note) value.
 */
public class Note implements Serializable, Comparable<Note> {

	private static final long serialVersionUID = 2352363228027670726L;

	private final int value;
	private final int level;

	/* --- constructor --- */

	/**
	 * The constructor.
	 * 
	 * @param newValue
	 *            the value of the note, must be between 0 and 11
	 * @param level
	 *            the level of the note, must be between 0 and 8
	 */
	/* package */Note(final int value, final int level) {
		if (value < 0 || value > 11 || level < 0 || level > 8 || level == 8 && value > 0) {
			throw new IllegalArgumentException();
		}

		this.level = level;
		this.value = value;
	}

	/* --- getter & setter --- */

	/**
	 * Returns the value of this note.
	 * 
	 * @return the value of this note
	 */
	public int getValue() {
		return value;
	}

	/**
	 * Returns the level of this note.
	 * 
	 * @return the level of this note
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * Returns the relative name of this note.
	 * 
	 * @return the relative name of this note
	 */
	public String getRelativeName() {
		return NamesUtil.getNoteName(value);
	}

	/**
	 * Returns the augmented relative name of this note.
	 * 
	 * @return the augmented relative name of this note
	 */
	public String getRelativeNameAug() {
		return NamesUtil.getFirstNotePart(NamesUtil.getNoteName(value));
	}

	/**
	 * Returns the diminished relative name of this note.
	 * 
	 * @return the diminished relative name of this note
	 */
	public String getRelativeNameDim() {
		return NamesUtil.getSecondNotePart(NamesUtil.getNoteName(value));
	}

	/**
	 * Returns the absolute name of this note.
	 * 
	 * @return the absolute name of this note
	 */
	public String getAbsoluteName() {
		return NamesUtil.getNoteName(value, level);
	}

	/**
	 * Returns the augmented absolute name of this note.
	 * 
	 * @return the augmented absolute name of this note
	 */
	public String getAbsoluteNameAug() {
		return NamesUtil.getFirstNotePart(NamesUtil.getNoteName(value, level));
	}

	/**
	 * Returns the diminished absolute name of this note.
	 * 
	 * @return the diminished absolute name of this note
	 */
	public String getAbsoluteNameDim() {
		return NamesUtil.getSecondNotePart(NamesUtil.getNoteName(value, level));
	}

	/**
	 * Returns true if the given note has the same value.
	 * 
	 * @param note
	 *            other note to compare with, must not be null
	 * 
	 * @return true if the given note has the same value
	 */
	public boolean hasSameValue(final Note note) {
		if (note == null) {
			throw new IllegalArgumentException();
		}

		return value == note.value;
	}

	/**
	 * Returns true if the given note has the same level.
	 * 
	 * @param note
	 *            other note to compare with, must not be null
	 * 
	 * @return true if the given note has the same level
	 */
	public boolean hasSameLevel(final Note note) {
		if (note == null) {
			throw new IllegalArgumentException();
		}

		return level == note.level;
	}

	/**
	 * Returns true if this note has an accidental, or false otherwise.
	 * 
	 * @return true if this note has an accidental, or false otherwise
	 */
	public boolean hasAccidental() {
		return value == 1 || value == 3 || value == 6 || value == 8 || value == 10;
	}

	/* --- calculations --- */

	/**
	 * Calculates the <code>Note</code> which distance to this note is the given
	 * interval.
	 * 
	 * @param interval
	 *            the interval, must not be null
	 * @return note which the give interval distance this note
	 */
	public Note calcNote(final Interval interval) {
		if (interval == null) {
			throw new IllegalArgumentException();
		}

		return Factory.getInstance().getNote(value + interval.getHalfsteps(), level);
	}

	/**
	 * Calculates the <code>Interval</code> between this note and the given one.
	 * 
	 * @param note
	 *            another note, must not be null
	 * @return interval between this note and the given one
	 */
	public Interval calcInterval(final Note note) {
		if (note == null) {
			throw new IllegalArgumentException();
		}
		int noteValue = note.value;
		if (noteValue < value) {
			noteValue += 12;
		}
		return Factory.getInstance().getInterval(noteValue - value);
	}

	/**
	 * Returns the next higher note of this note, or null if there is no higher
	 * note existing.
	 * 
	 * @return the next higher note of this note, or null if there is no higher
	 *         note existing
	 */
	public Note getNextHigherNote() {
		int nextValue = value + 1;
		int nextLevel = level;
		if (nextValue > Constants.MAX_NOTES_VALUE) {
			nextValue = 0;
			nextLevel++;
		}
		if (nextLevel == 8 && nextValue > 0) {
			return null;
		}
		return Factory.getInstance().getNote(nextValue, nextLevel);
	}

	/**
	 * Returns the next deeper note of this note, or null if there is no deeper
	 * note existing.
	 * 
	 * @return the next deeper note of this note, or null if there is no deeper
	 *         note existing
	 */
	public Note getNextDeeperNote() {
		int nextValue = value - 1;
		int nextLevel = level;
		if (nextValue < 0) {
			nextValue = Constants.MAX_NOTES_VALUE;
			nextLevel--;
		}
		if (nextLevel < 0) {
			return null;
		}
		return Factory.getInstance().getNote(nextValue, nextLevel);
	}

	/* --- object methods --- */

	@Override
	public String toString() {
		return getRelativeName();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Note)) {
			return false;
		}
		final Note other = (Note) obj;
		return value == other.value && level == other.level;
	}

	@Override
	public int hashCode() {
		return Integer.valueOf(value).hashCode();
	}

	@Override
	public int compareTo(final Note other) {
		if (other == null) {
			return 0;
		}
		int result = Integer.valueOf(level).compareTo(Integer.valueOf(other.level));
		if (result == 0) {
			result = Integer.valueOf(value).compareTo(Integer.valueOf(other.value));
		}
		return result;
	}
}
