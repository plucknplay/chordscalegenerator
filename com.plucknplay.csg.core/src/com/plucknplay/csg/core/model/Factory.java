/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.core.model;

import java.util.HashMap;
import java.util.Map;

import com.plucknplay.csg.core.Constants;
import com.plucknplay.csg.core.util.NamesUtil;

public final class Factory {

	private static Factory instance;

	private static Map<Integer, Interval> intervalsMap;
	private static Map<String, Note> notesMap;

	/**
	 * The private default constructor.
	 */
	private Factory() {
	}

	/**
	 * Returns the singleton instance of this factory.
	 * 
	 * @return the singleton instance of this factory
	 */
	public static Factory getInstance() {
		if (instance == null) {
			instance = new Factory();
			intervalsMap = new HashMap<Integer, Interval>();
			notesMap = new HashMap<String, Note>();
		}
		return instance;
	}

	/**
	 * Returns the interval with the given halfsteps.
	 * 
	 * @param halfsteps
	 *            the halfsteps
	 * 
	 * @return the interval with the given halfsteps
	 */
	public Interval getInterval(final int halfsteps) {
		Interval interval = intervalsMap.get(halfsteps);
		if (interval == null) {
			interval = new Interval(halfsteps);
			intervalsMap.put(halfsteps, interval);
		}
		return interval;
	}

	/**
	 * Returns the note with the given value.
	 * 
	 * @param value
	 *            the value
	 * 
	 * @return the note with the given value
	 */
	public Note getNote(final int value) {
		return getNote(value, Constants.DEFAULT_NOTE_LEVEL);
	}

	/**
	 * Returns the note with the given value and level.
	 * 
	 * @param value
	 *            the value
	 * @param level
	 *            the level, must be between 0 and 8
	 * 
	 * @return the note with the given value and level
	 */
	public Note getNote(final int value, final int level) {
		final int theValue = value % Constants.INTERVALS_NUMBER;
		final int theLevel = level + value / Constants.INTERVALS_NUMBER;

		final String key = theValue + "," + theLevel; //$NON-NLS-1$
		Note note = notesMap.get(key);
		if (note == null) {
			note = new Note(theValue, theLevel);
			notesMap.put(key, note);
		}
		return note;
	}

	/**
	 * Returns the note with the given name.
	 * 
	 * @param name
	 *            the name, must not be null
	 * 
	 * @return the note with the given name
	 */
	public Note getNote(final String name) {
		return getNote(name, Constants.DEFAULT_NOTE_LEVEL);
	}

	/**
	 * Returns the note with the given name and level.
	 * 
	 * @param name
	 *            the name, must not be null
	 * @param level
	 *            the level, must be between 0 and 8
	 * 
	 * @return the note with the given name and level
	 */
	public Note getNote(final String name, final int level) {
		if (name == null) {
			throw new IllegalArgumentException();
		}

		return getNote(NamesUtil.getNoteValue(name), level);
	}

	/**
	 * Returns the note with the given index.
	 * 
	 * @param index
	 *            the index, must be between 0 and 96
	 * 
	 * @return the note with the given index
	 */
	public Note getNoteByIndex(final int index) {
		if (index < 0 || index > Constants.MAX_NOTES_LEVEL * 12) {
			throw new IllegalArgumentException();
		}

		return getNote(index % 12, index / 12);
	}

	/**
	 * Returns true if the note with the given value and level is a valid one,
	 * false otherwise.
	 * 
	 * @param value
	 *            the value
	 * @param level
	 *            the level
	 * 
	 * @return true if the note with the given value and level is a valid one,
	 *         false otherwise
	 */
	public boolean isValidNote(final int value, final int level) {
		return value >= 0 && value <= 11 && level >= 0 && level <= 7 || level == 8 && value == 0;
	}
}
