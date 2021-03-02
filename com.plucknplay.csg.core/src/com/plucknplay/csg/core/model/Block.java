/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.core.model;

import java.util.HashMap;
import java.util.Map;

public abstract class Block extends IBlock implements IFingerNumberProvider {

	private Map<Integer, Integer> minFretMap;
	private Map<Integer, Integer> maxFretMap;

	public Block(final IntervalContainer intervalContainer) {
		super(intervalContainer);

		minFretMap = null;
		maxFretMap = null;
	}

	/**
	 * Returns true if this block contains empty string notes, or false
	 * otherwise.
	 * 
	 * @return true if this block contains empty string notes, or false
	 *         otherwise
	 */
	public abstract boolean hasEmptyStringNotes();

	/**
	 * Returns true if this block has a tone on the given string, or false
	 * otherwise.
	 * 
	 * @param stringNumber
	 *            the string number, must be a value >= 1
	 * 
	 * @return true if this block has a tone on the given string, or false
	 *         otherwise
	 */
	public abstract boolean hasToneOnString(int stringNumber);

	/**
	 * Returns the beautified name of this block.
	 * 
	 * @param notesMode
	 *            the notes mode that shall be used to render this name, must be
	 *            of Constants.NOTES_MODE_*
	 * 
	 * @return the beautified name of this block
	 */
	@Override
	public abstract String getBeautifiedName(String notesMode);

	protected void determineMinMaxFrets() {
		minFretMap = new HashMap<Integer, Integer>();
		maxFretMap = new HashMap<Integer, Integer>();

		for (int s = 1; s <= getCurrentInstrument().getStringCount() + 1; s++) {
			boolean found = false;
			int theMaxFret = -1;
			int theMinFret = getCurrentInstrument().getFretCount();
			for (final FretboardPosition fbp : getFretboardPositions()) {
				if (fbp.getString() + 1 != s) {
					continue;
				}
				if (fbp.getFret() > getCurrentInstrument().getMinFret()) {
					if (fbp.getFret() > theMaxFret) {
						theMaxFret = fbp.getFret();
						found = true;
					}
					if (fbp.getFret() < theMinFret) {
						theMinFret = fbp.getFret();
						found = true;
					}
				}
			}
			minFretMap.put(s, found ? theMinFret : -1);
			maxFretMap.put(s, found ? theMaxFret : -1);
		}
	}

	/**
	 * Returns the minimal fret on the given string which is gripped on this
	 * block.
	 * 
	 * Note: returns -1 if there is no tone on the given string.
	 * 
	 * @param stringNumber
	 *            the string number, must be a value >= 1
	 * 
	 * @return the minimal fret on the given string which is gripped on this
	 *         block
	 */
	public int getMinFret(final int stringNumber) {
		if (getFretboardPositions() == null) {
			return 0;
		}
		if (minFretMap == null) {
			determineMinMaxFrets();
		}
		final Integer fret = minFretMap.get(stringNumber);
		return fret != null ? fret.intValue() : -1;
	}

	/**
	 * Returns the maximum fret on the given string which is gripped on this
	 * block.
	 * 
	 * Note: returns -1 if there is no tone on the given string.
	 * 
	 * @param stringNumber
	 *            the string number, must be a value >= 1
	 * 
	 * @return the maximum fret on the given string which is gripped on this
	 *         block
	 */
	public int getMaxFret(final int stringNumber) {
		if (getFretboardPositions() == null) {
			return 0;
		}
		if (maxFretMap == null) {
			determineMinMaxFrets();
		}
		final Integer fret = maxFretMap.get(stringNumber);
		return fret != null ? fret.intValue() : -1;
	}
}
