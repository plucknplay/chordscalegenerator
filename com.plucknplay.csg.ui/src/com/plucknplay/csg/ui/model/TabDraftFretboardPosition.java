/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.model;

import com.plucknplay.csg.core.model.FretBlock;
import com.plucknplay.csg.core.model.FretboardPosition;

/**
 * Simple wrapper class for fretboard positions which get extended with a column
 * information.
 */
public class TabDraftFretboardPosition {

	private final int column;
	private final FretboardPosition fbp;

	/**
	 * The constructor.
	 * 
	 * @param parent
	 *            the parent tab draft which contains this tab draft fretboard
	 *            position, must not be null
	 * @param column
	 *            the column, must be a value between 1 and
	 *            {@value FretBlock#MAX_FRET_RANGE}
	 * @param fbp
	 *            the fretboard position, must not be null
	 */
	public TabDraftFretboardPosition(final TabDraft parent, final int column, final FretboardPosition fbp) {
		if (parent == null || column < 1 || column > FretBlock.MAX_FRET_RANGE + 1 || fbp == null) {
			throw new IllegalArgumentException();
		}

		this.column = column;
		this.fbp = fbp;
	}

	/**
	 * Returns the column of this tab draft fretboard position.
	 * 
	 * @return the column of this tab draft fretboard position
	 */
	public int getColumn() {
		return column;
	}

	/**
	 * Returns the fretboard postion of this tab draft fretboard position.
	 * 
	 * @return the fretboard postion of this tab draft fretboard position, never
	 *         null
	 */
	public FretboardPosition getFretboardPosition() {
		return fbp;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + column;
		result = prime * result + fbp.getString();
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		final TabDraftFretboardPosition other = (TabDraftFretboardPosition) obj;
		if (column != other.column) {
			return false;
		}
		if (fbp == null) {
			if (other.fbp != null) {
				return false;
			}
		} else if (!fbp.equals(other.fbp)) {
			return false;
		}
		return true;
	}
}
