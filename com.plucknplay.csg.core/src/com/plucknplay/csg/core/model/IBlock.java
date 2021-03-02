/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.core.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.plucknplay.csg.core.model.sets.InstrumentList;

public abstract class IBlock {

	private IntervalContainer intervalContainer;
	private final Instrument currentInstrument;

	public IBlock(final IntervalContainer intervalContainer) {
		if (intervalContainer == null) {
			throw new IllegalArgumentException();
		}
		this.intervalContainer = intervalContainer;
		currentInstrument = InstrumentList.getInstance().getCurrentInstrument();
	}

	public IBlock(final IBlock block) {
		if (block == null) {
			throw new IllegalArgumentException();
		}
		intervalContainer = block.intervalContainer;
		currentInstrument = block.currentInstrument;
	}

	/**
	 * Returns the corresponding interval container of this block.
	 * 
	 * @return the corresponding interval container of this block
	 */
	public IntervalContainer getIntervalContainer() {
		return intervalContainer;
	}

	protected void setIntervalContainer(final IntervalContainer intervalContainer) {
		this.intervalContainer = intervalContainer;
	}

	public Instrument getCurrentInstrument() {
		return currentInstrument;
	}

	/**
	 * Returns the set of notes of this block.
	 * 
	 * @param compareOnlyNoteValue
	 *            true if only the note value shall be taken into consideration,
	 *            false if also the level has importance
	 * 
	 * @return the set of notes of this block, never <code>null</code>
	 */
	public Set<Note> getNotes(final boolean compareOnlyNoteValue) {
		final Set<Note> result = new HashSet<Note>();
		for (final FretboardPosition fbp : getFretboardPositions()) {
			final Note currentNote = getCurrentInstrument().getNote(fbp);
			result.add(compareOnlyNoteValue ? Factory.getInstance().getNote(currentNote.getValue()) : currentNote);

			// add one octave higher note if necessary
			if (getCurrentInstrument().isDoubledStringWithOctaveJump(fbp.getString() + 1) && !compareOnlyNoteValue) {
				result.add(Factory.getInstance().getNote(currentNote.getValue(), currentNote.getLevel() + 1));
			}
		}
		return result;
	}

	/**
	 * Returns all fretboard positions of this block.
	 * 
	 * @return all fretboard positions of this block
	 */
	public abstract List<FretboardPosition> getFretboardPositions();

	/**
	 * Returns the minimal string number of this block.
	 * 
	 * <p>
	 * Note that the minimal value for a string number is 1.
	 * </p>
	 * 
	 * @return the minimal string number of this block
	 */
	public abstract int getMinString();

	/**
	 * Returns the maximum string number of this block.
	 * 
	 * <p>
	 * Note that the minimal value for a string number is 1.
	 * </p>
	 * 
	 * @return the maximum string number of this block
	 */
	public abstract int getMaxString();

	/**
	 * Returns the fret span of this block.
	 * 
	 * @return the fret span of this block
	 */
	public abstract int getFretSpan();

	/**
	 * Returns the minimal fret of this block.
	 * 
	 * <p>
	 * Note: This method takes the possibly defined capotasto fret(s) into
	 * consideration.
	 * </p>
	 * 
	 * @see #getMinFret(boolean)
	 * 
	 * @return the minimal fret of this block
	 */
	public int getMinFret() {
		return getMinFret(true);
	}

	/**
	 * Returns the minimal fret of this block.
	 * 
	 * @param useCapoFret
	 *            <code>true</code> if the possibly defined capotasto fret(s)
	 *            shall be taken into consideration, <code>false</code>
	 *            otherwise
	 * 
	 * @see #getMinFret()
	 * 
	 * @return the minimal fret of this block
	 */
	public abstract int getMinFret(boolean useCapoFret);

	/**
	 * Returns the maximal fret of this block.
	 * 
	 * @return the maximal fret of this block
	 */
	public abstract int getMaxFret();

	public abstract boolean hasEmptyStringNotes();

	/**
	 * Returns the beautified name of this block.
	 * 
	 * @param notesMode
	 *            the notes mode that shall be used to render this name, must be
	 *            of Constants.NOTES_MODE_*
	 * 
	 * @return the beautified name of this block
	 */
	public abstract String getBeautifiedName(String notesMode);
}
