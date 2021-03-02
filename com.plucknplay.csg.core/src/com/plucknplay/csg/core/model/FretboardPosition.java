/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.core.model;

import com.plucknplay.csg.core.Constants;

/**
 * This class represents a fretboard position. A fretboard position is simply
 * defined by its string and fret.
 */
public class FretboardPosition implements Comparable<FretboardPosition> {

	private static final int MIN_FRET = 0;
	private static final int MAX_FRET = Constants.MAX_FRET_NUMBER + 1;

	private int string;
	private int fret;

	// --- constructors --- //

	/**
	 * The constructor. The string will be set to zero and the fret to the
	 * miminal possible value.
	 */
	public FretboardPosition() {
		string = 0;
		fret = MIN_FRET;
	}

	/**
	 * The constructor.
	 * 
	 * @param string
	 *            the string
	 * @param fret
	 *            the fret
	 */
	public FretboardPosition(final int string, final int fret) {
		setString(string);
		setFret(fret);
	}

	/**
	 * The constructor.
	 * 
	 * @param fPos
	 *            another fretboard position
	 */
	public FretboardPosition(final FretboardPosition fPos) {
		string = fPos.string;
		fret = fPos.fret;
	}

	// --- getter & setter --- //

	/**
	 * Returns the string.
	 * 
	 * @return the string
	 */
	public int getString() {
		return string;
	}

	/**
	 * Sets the string.
	 * 
	 * @param newString
	 *            the new string
	 */
	public void setString(final int newString) {
		string = newString % Constants.MAX_STRING_SIZE;
		if (string < 0) {
			string = 0;
		}
	}

	/**
	 * Returns the fret.
	 * 
	 * @return the fret
	 */
	public int getFret() {
		return fret;
	}

	/**
	 * Sets the fret.
	 * 
	 * @param newFret
	 *            the new fret
	 */
	public void setFret(final int newFret) {
		if (fret < -1 || fret > MAX_FRET) {
			throw new IllegalArgumentException();
		}
		fret = newFret;
	}

	// --- object methods --- //

	@Override
	public String toString() {
		return "string: " + (string + 1) + " | fret: " + fret; //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof FretboardPosition)) {
			return false;
		}
		final FretboardPosition fPos = (FretboardPosition) obj;
		return string == fPos.string && fret == fPos.fret;
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public int compareTo(final FretboardPosition other) {
		if (other == null) {
			return 0;
		}
		int result = Integer.valueOf(string).compareTo(Integer.valueOf(other.string));
		if (result == 0) {
			result = Integer.valueOf(fret).compareTo(Integer.valueOf(other.fret));
		}
		return result;
	}
}
