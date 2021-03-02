/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.core.model.sets;

import com.plucknplay.csg.core.model.Categorizable;
import com.plucknplay.csg.core.model.Factory;
import com.plucknplay.csg.core.model.IntervalContainer;
import com.plucknplay.csg.core.model.Note;

/**
 * Abstact super class of the {@link ScaleList} and {@link ChordList} classes
 * that handles the root note setting.
 */
public abstract class IntervalContainerList extends CategoryList {

	public static final Object PROP_UPDATED_NAMES = new Object();

	private Note currentRootNote;

	protected IntervalContainerList() {
		super();
		currentRootNote = Factory.getInstance().getNote(0);
	}

	/**
	 * Sets the current root note.
	 * 
	 * <p>
	 * Note that only the relative note value is of interest.
	 * </p>
	 * 
	 * @param newRootNote
	 *            the new root note, must not be null
	 */
	public void setCurrentRootNote(final Note newRootNote) {
		currentRootNote = newRootNote;
	}

	/**
	 * Returns the currently set root note.
	 * 
	 * @return the currently set root note, never null
	 */
	public Note getCurrentRootNote() {
		return currentRootNote;
	}

	public void updateIntervalNames() {
		for (final Categorizable ic : getRootCategory().getAllElements()) {
			if (ic instanceof IntervalContainer) {
				((IntervalContainer) ic).updateIntervalNames();
			}
		}
		notifyListeners(null, null, PROP_UPDATED_NAMES);
	}
}
