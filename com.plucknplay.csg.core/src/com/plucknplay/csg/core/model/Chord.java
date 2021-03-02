/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.core.model;

import com.plucknplay.csg.core.Constants;
import com.plucknplay.csg.core.model.sets.ChordList;

/**
 * This class represents a chord. A chord is defined by its name, a root note
 * and a set of intervals.
 * 
 * <p>
 * Names are f.e.: 'min', 'maj', 'sus4', ...
 * </p>
 */
public class Chord extends IntervalContainer {

	private static final long serialVersionUID = 7599661696493258893L;

	/* --- constructors --- */

	/**
	 * The default constructor.
	 */
	public Chord() {
		super(""); //$NON-NLS-1$
	}

	/**
	 * The constructor. The root note will be set to 'C'.
	 * 
	 * @param name
	 *            the name of the chord, must not be null
	 */
	public Chord(final String name) {
		super(name, null);
	}

	/**
	 * The constructor.
	 * 
	 * @param name
	 *            name of the chord, must not be null
	 * @param rootNote
	 *            the root note of the chord, must not be null
	 */
	public Chord(final String name, final Note rootNote) {
		super(name, rootNote);

		if (rootNote == null) {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * The constructor.
	 * 
	 * @param chord
	 *            another Chord, must not be null
	 */
	public Chord(final Chord chord) {
		super(chord);
	}

	@Override
	public Object getType() {
		return TYPE_CHORD;
	}

	@Override
	public Note getRootNote() {
		return rootNote != null ? rootNote : ChordList.getInstance().getCurrentRootNote();
	}

	/* --- object methods --- */

	@Override
	public String toString() {
		if (Constants.BLANK_CHORD_NAME.equals(getName())) {
			return getRootNote().toString();
		}
		return getRootNote().toString() + getName();
	}
}
