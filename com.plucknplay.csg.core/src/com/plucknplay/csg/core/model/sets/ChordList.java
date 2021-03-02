/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.core.model.sets;

import com.plucknplay.csg.core.model.Categorizable;
import com.plucknplay.csg.core.model.Chord;

/**
 * This container class stores all known chords of the application.
 * 
 * <p>
 * Note this class is a singleton and can't be initiate using a constructor.
 * Invoke getInstance() to retrieve the shared instance of this class.
 * </p>
 */
public final class ChordList extends IntervalContainerList {

	private static ChordList instance;

	/**
	 * The private default constructor.
	 */
	private ChordList() {
		super();
	}

	/**
	 * Returns the singleton instance of the chord list.
	 * 
	 * @return the singleton instance of the chord list, never null
	 */
	public static ChordList getInstance() {
		if (instance == null) {
			instance = new ChordList();
		}
		return instance;
	}

	@Override
	protected void checkCategorizableType(final Categorizable element) {
		if (!(element instanceof Chord)) {
			throw new IllegalArgumentException();
		}
	}

	@Override
	protected Categorizable createNewElement(final Categorizable element) {
		if (!(element instanceof Chord)) {
			throw new IllegalArgumentException();
		}
		return new Chord((Chord) element);
	}
}
