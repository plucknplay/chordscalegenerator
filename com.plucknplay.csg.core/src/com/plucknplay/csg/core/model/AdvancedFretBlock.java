/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.core.model;

public class AdvancedFretBlock extends FretBlock {

	public static final int MIN_STRING_RANGE_DECREASE = 1;
	public static final int DEFAULT_STRING_RANGE_DECREASE = 1;

	private int stringRangeDecrease;
	private int maxString;

	public AdvancedFretBlock(final IntervalContainer intervalContainer) {
		this(intervalContainer, DEFAULT_FRET_RANGE, DEFAULT_STRING_RANGE_DECREASE, false);
	}

	public AdvancedFretBlock(final IntervalContainer intervalContainer, final int fretRange,
			final int stringRangeDecrease, final boolean useEmptyStrings) {
		super(intervalContainer, fretRange, useEmptyStrings);
		this.stringRangeDecrease = stringRangeDecrease;
		maxString = getCurrentInstrument().getStringCount();
	}

	public AdvancedFretBlock(final AdvancedFretBlock other) {
		super(other);
		maxString = other.maxString;
		stringRangeDecrease = other.stringRangeDecrease;
	}

	public void setStartString(final int startString) {
		if (startString - (getCurrentInstrument().getStringCount() - stringRangeDecrease) < 0
				|| startString > getCurrentInstrument().getStringCount()) {
			throw new IllegalArgumentException();
		}
		if (maxString != startString) {
			maxString = startString;
			setChanged(true);
			determineMinMaxFrets();
		}
	}

	public void setStringRangeDecrease(final int stringRangeDecrease) {
		if (stringRangeDecrease < MIN_FRET_RANGE || stringRangeDecrease > getCurrentInstrument().getStringCount() - 1) {
			throw new IllegalArgumentException();
		}
		if (this.stringRangeDecrease != stringRangeDecrease) {
			this.stringRangeDecrease = stringRangeDecrease;
			if (maxString - (getCurrentInstrument().getStringCount() - stringRangeDecrease) + 1 <= 0) {
				setStartString(getCurrentInstrument().getStringCount() - stringRangeDecrease);
			}
			setChanged(true);
			determineMinMaxFrets();
		}
	}

	public int getStringRangeDecrease() {
		return stringRangeDecrease;
	}

	@Override
	public int getMaxString() {
		return maxString;
	}

	@Override
	public int getMinString() {
		return maxString - (getCurrentInstrument().getStringCount() - stringRangeDecrease) + 1;
	}

	@Override
	public String getBeautifiedName(final String notesMode) {
		final StringBuffer buf = new StringBuffer(getIntervalContainer().getBeautifiedName(notesMode));

		buf.append(" ("); //$NON-NLS-1$
		buf.append(ModelMessages.FretBlock_frets);
		buf.append(": "); //$NON-NLS-1$
		buf.append(getMinFret());
		buf.append("-"); //$NON-NLS-1$
		buf.append(getMaxFret());
		if (getMinString() > 1 || getMaxString() < getCurrentInstrument().getStringCount()) {
			buf.append(", "); //$NON-NLS-1$
			buf.append(ModelMessages.AdvancedFretBlock_strings);
			buf.append(": "); //$NON-NLS-1$
			buf.append(getMinString());
			buf.append("-"); //$NON-NLS-1$
			buf.append(getMaxString());
		}
		buf.append(")"); //$NON-NLS-1$

		return buf.toString();
	}
}
