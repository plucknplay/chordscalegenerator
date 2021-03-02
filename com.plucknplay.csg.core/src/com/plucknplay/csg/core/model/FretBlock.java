/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.core.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FretBlock extends Block {

	public static final int MIN_FRET_RANGE = 2;
	public static final int DEFAULT_FRET_RANGE = 4;
	public static final int MAX_FRET_RANGE = 8;

	private List<FretboardPosition> fretboardPositions;

	private int minFret;
	private int fretRange;
	private boolean useEmptyStrings;
	private boolean hasEmptyStringNotes;

	private boolean changed;
	private Map<FretboardPosition, Byte> fingering;

	public FretBlock(final IntervalContainer intervalContainer) {
		this(intervalContainer, DEFAULT_FRET_RANGE, true);
	}

	public FretBlock(final IntervalContainer intervalContainer, final int fretRange, final boolean useEmptyStrings) {
		super(intervalContainer);
		minFret = getCurrentInstrument().getMinFret() + (useEmptyStrings ? 1 : 0);
		this.fretRange = fretRange;
		this.useEmptyStrings = useEmptyStrings;
		hasEmptyStringNotes = false;
		changed = true;
		fingering = null;
	}

	public FretBlock(final FretBlock other) {
		super(other.getIntervalContainer());
		minFret = other.minFret;
		fretRange = other.fretRange;
		useEmptyStrings = other.useEmptyStrings;
		hasEmptyStringNotes = other.hasEmptyStringNotes;
		changed = other.changed;
		fingering = null;
	}

	protected boolean isChanged() {
		return changed;
	}

	protected void setChanged(final boolean changed) {
		this.changed = changed;
	}

	/**
	 * Returns the fret range of this block.
	 * 
	 * @return the fret range of this block
	 */
	@Override
	public int getFretSpan() {
		return fretRange;
	}

	/**
	 * Sets the fret range of this block.
	 * 
	 * @param fretRange
	 *            the new fret range, must be a value between
	 *            {@value FretBlock#MIN_FRET_RANGE} and
	 *            {@value FretBlock#MAX_FRET_RANGE}
	 */
	public void setFretRange(final int fretRange) {
		if (fretRange < MIN_FRET_RANGE || fretRange > MAX_FRET_RANGE) {
			throw new IllegalArgumentException();
		}

		if (this.fretRange != fretRange) {
			this.fretRange = fretRange;
			if (minFret + fretRange - 1 > getCurrentInstrument().getFretCount()) {
				setMinFret(getCurrentInstrument().getFretCount() - fretRange + 1);
			}
			changed = true;
			determineMinMaxFrets();
		}
	}

	/**
	 * Sets the start fret of this block.
	 * 
	 * @param startFret
	 *            the new start fret, must be a value between the first valid
	 *            fret of the current instrument and the current instruments
	 *            fret count - fret range
	 */
	public void setMinFret(final int startFret) {
		if (startFret < getCurrentInstrument().getMinFret()
				|| startFret > getCurrentInstrument().getFretCount() - fretRange + 1) {
			throw new IllegalArgumentException();
		}

		if (minFret != startFret) {
			minFret = startFret;
			changed = true;
			determineMinMaxFrets();
		}
	}

	/**
	 * Returns true if empty string notes shall be taken into consideration to
	 * determine the fretboard positions, or false otherwise.
	 * 
	 * @return true if empty string notes shall be taken into consideration to
	 *         determine the fretboard positions, or false otherwise
	 */
	public boolean useEmptyStrings() {
		return useEmptyStrings;
	}

	/**
	 * Set true if empty string notes shall be taken into consideration to
	 * determine the fretboard positions, or false otherwise.
	 * 
	 * @param useEmptyStrings
	 *            the new empty string state
	 */
	public void setUseEmptyStrings(final boolean useEmptyStrings) {
		if (this.useEmptyStrings != useEmptyStrings) {
			this.useEmptyStrings = useEmptyStrings;
			changed = true;
		}
	}

	@Override
	public List<FretboardPosition> getFretboardPositions() {
		if (changed || fretboardPositions == null) {
			hasEmptyStringNotes = false;
			fretboardPositions = new ArrayList<FretboardPosition>();
			final Set<Integer> noteValues = getIntervalContainer().getNoteValues();
			for (int s = getMaxString() - 1; s >= getMinString() - 1; s--) {

				// empty strings
				if (useEmptyStrings) {
					final FretboardPosition currentFBP = new FretboardPosition(s, getCurrentInstrument().getCapoFret(
							s + 1));
					final Note currentNote = getCurrentInstrument().getNote(currentFBP);
					if (noteValues.contains(currentNote.getValue())) {
						fretboardPositions.add(currentFBP);
						hasEmptyStringNotes = true;
					}
				}

				// block frets
				for (int f = minFret; f < minFret + fretRange; f++) {
					final FretboardPosition currentFBP = new FretboardPosition(s, f);
					final Note currentNote = getCurrentInstrument().getNote(currentFBP);
					if (noteValues.contains(currentNote.getValue())) {
						fretboardPositions.add(currentFBP);
					}
				}
			}
			changed = false;
			fingering = null;
		}
		return fretboardPositions;
	}

	@Override
	public int getMinString() {
		return 1;
	}

	@Override
	public int getMaxString() {
		return getCurrentInstrument().getStringCount();
	}

	@Override
	public int getMaxFret() {
		return minFret + fretRange - 1;
	}

	@Override
	public int getMinFret(final boolean useCapoFret) {
		return minFret;
	}

	@Override
	public boolean hasEmptyStringNotes() {
		if (useEmptyStrings) {
			getFretboardPositions();
			return hasEmptyStringNotes;
		}
		return false;
	}

	@Override
	public boolean hasToneOnString(final int stringNumber) {
		return true;
	}

	@Override
	public Byte getFingerNumber(final FretboardPosition fretboardPosition) {
		if (fretboardPosition.getFret() == getCurrentInstrument().getMinFret()) {
			return 0;
		}
		if (fretRange <= 4) {
			final int fret = fretboardPosition.getFret();
			return fret == getCurrentInstrument().getMinFret() ? (byte) 0
					: (byte) (fret - minFret + (minFret == getCurrentInstrument().getMinFret() ? 0 : 1));
		}
		if (fingering == null) {
			determineFingering();
		}
		return fingering.get(fretboardPosition);
	}

	private void determineFingering() {
		fingering = new HashMap<FretboardPosition, Byte>();
		if (fretboardPositions == null) {
			return;
		}
		for (final FretboardPosition fbp : getFretboardPositions()) {
			final int f = fbp.getFret();
			final int maxFret = getMaxFret(fbp.getString() + 1);
			final int fingerNumber = 4 - (maxFret - f);
			fingering.put(fbp, (byte) (fingerNumber < 1 ? 1 : fingerNumber));
		}
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
		buf.append(")"); //$NON-NLS-1$

		return buf.toString();
	}
}
