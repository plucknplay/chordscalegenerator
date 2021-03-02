/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.core.model;

import com.plucknplay.csg.core.model.sets.ScaleList;

/**
 * This class represents a scale. A scale is defined by its name, a root note
 * and a set of intervals.
 * 
 * <p>
 * Names are f.e.: 'ionian', 'dorian', ...
 * </p>
 */
public class Scale extends IntervalContainer {

	private static final long serialVersionUID = -720955458430313788L;

	/* --- constructors --- */

	/**
	 * The default constructor.
	 */
	public Scale() {
		super(""); //$NON-NLS-1$
	}

	/**
	 * The constructor. The root note will be null. Thus the general root note
	 * of the scales view will be used as root note.
	 * 
	 * @param name
	 *            the name of the scale, must not be null
	 */
	public Scale(final String name) {
		super(name, null);
	}

	/**
	 * The constructor.
	 * 
	 * @param name
	 *            name of the scale, must not be null
	 * @param rootNote
	 *            the root note of the scale, must not be null
	 */
	public Scale(final String name, final Note rootNote) {
		super(name, rootNote);

		if (rootNote == null) {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * The constructor.
	 * 
	 * @param scale
	 *            another scale, must not be null
	 */
	public Scale(final Scale scale) {
		super(scale);
	}

	@Override
	public Object getType() {
		return TYPE_SCALE;
	}

	@Override
	public Note getRootNote() {
		return rootNote != null ? rootNote : ScaleList.getInstance().getCurrentRootNote();
	}

	/* --- object methods --- */

	@Override
	public String toString() {
		String result = getRootNote().toString();
		if (!"".equals(getName())) {
			result += " - " + getName(); //$NON-NLS-1$
		}
		return result;
	}
}
