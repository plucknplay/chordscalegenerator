/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.core.calculation;

import java.util.ArrayList;
import java.util.List;

import com.plucknplay.csg.core.Constants;
import com.plucknplay.csg.core.model.Chord;
import com.plucknplay.csg.core.model.Factory;
import com.plucknplay.csg.core.model.Instrument;
import com.plucknplay.csg.core.model.Interval;
import com.plucknplay.csg.core.model.Note;
import com.plucknplay.csg.core.model.Unit;
import com.plucknplay.csg.core.model.sets.InstrumentList;

public class CalculationDescriptor {

	private final Instrument currentInstrument;

	private Chord chord;
	private Note bassTone;
	private Note leadTone;
	private int minLevel;
	private int maxLevel;
	private int minString;
	private int maxString;
	private int minFret;
	private int maxFret;
	private Integer fretGripRange;
	private Double gripRange;
	private Unit gripRangeUnit;
	private List<Integer> scanStartFrets;
	private Integer toneNumber;
	private boolean emptyStrings;
	private boolean mutedStrings;
	private boolean onlyPacked;
	private boolean onlySingleMutedStrings;
	private boolean doubledTonesAllowed;
	private boolean onlyAscendingDescendingToneSequence;
	private boolean without1st;
	private boolean without3rd;
	private boolean without5th;

	private List<Interval> excludedIntervals;
	private boolean intervalsChanged;

	public CalculationDescriptor() {
		intervalsChanged = true;
		currentInstrument = InstrumentList.getInstance().getCurrentInstrument();
		gripRangeUnit = currentInstrument.getScaleLengthUnit();
	}

	public Chord getChord() {
		return chord;
	}

	public void setChord(final Chord chord) {
		if (chord == null) {
			throw new IllegalArgumentException();
		}
		this.chord = chord;
	}

	public Note getBassTone() {
		return bassTone;
	}

	public void setBassTone(final Note bassTone) {
		this.bassTone = bassTone;
	}

	public Note getLeadTone() {
		return leadTone;
	}

	public void setLeadTone(final Note leadTone) {
		this.leadTone = leadTone;
	}

	public int getMinLevel() {
		return minLevel;
	}

	public void setMinLevel(final int minLevel) {
		this.minLevel = minLevel;
	}

	public int getMaxLevel() {
		return maxLevel;
	}

	public void setMaxLevel(final int maxLevel) {
		this.maxLevel = maxLevel;
	}

	public int getMinString() {
		return minString;
	}

	public void setMinString(final int minString) {
		this.minString = minString;
	}

	public int getMaxString() {
		return maxString;
	}

	public void setMaxString(final int maxString) {
		this.maxString = maxString;
	}

	public int getMinFret() {
		return minFret;
	}

	public void setMinFret(final int minFret) {
		this.minFret = minFret;
	}

	public int getMaxFret() {
		return maxFret;
	}

	public void setMaxFret(final int maxFret) {
		this.maxFret = maxFret;
	}

	public void setFretGripRange(final Integer fretGripRange) {
		this.fretGripRange = fretGripRange;
	}

	public void setGripRange(final Double gripRange) {
		this.gripRange = gripRange;
		updateGripRange();
	}

	public void setGripRangeUnit(final Unit gripRangeUnit) {
		this.gripRangeUnit = gripRangeUnit;
		updateGripRange();
	}

	private void updateGripRange() {
		final Unit instrumentUnit = currentInstrument.getScaleLengthUnit();
		if (instrumentUnit != gripRangeUnit) {
			gripRange = Unit.convert(gripRangeUnit, gripRange, instrumentUnit);
			gripRangeUnit = instrumentUnit;
		}
	}

	public int getFretGripRange(final int startFret) {
		int fretRange = fretGripRange != null ? fretGripRange : currentInstrument.getFretInDistance(startFret,
				gripRange) - startFret + 1;
		if (fretRange > Constants.MAX_FRET_GRIP_RANGE) {
			fretRange = Constants.MAX_FRET_GRIP_RANGE;
		}
		return fretRange;
	}

	public int getNumberOfCalculationLoops() {
		if (scanStartFrets == null) {
			determineScanStartFrets();
		}
		return scanStartFrets.size();
	}

	public int getMaxStartFret() {
		if (scanStartFrets == null) {
			determineScanStartFrets();
		}
		return scanStartFrets.get(scanStartFrets.size() - 1);
	}

	private void determineScanStartFrets() {
		scanStartFrets = new ArrayList<Integer>();
		for (int startFret = minFret; startFret <= maxFret; startFret++) {
			final int endFret = startFret + getFretGripRange(startFret) - 1;
			if (endFret <= maxFret) {
				scanStartFrets.add(startFret);
			} else {
				return;
			}
		}
	}

	public Integer getToneNumber() {
		return toneNumber;
	}

	public void setToneNumber(final Integer toneNumber) {
		this.toneNumber = toneNumber;
	}

	public boolean allowEmptyStrings() {
		return emptyStrings;
	}

	public void setEmptyStrings(final boolean emptyStrings) {
		this.emptyStrings = emptyStrings;
	}

	public boolean allowMutedStrings() {
		return mutedStrings;
	}

	public void setMutedStrings(final boolean mutedStrings) {
		this.mutedStrings = mutedStrings;
	}

	public void setOnlyPacked(final boolean onlyPacked) {
		this.onlyPacked = onlyPacked;
	}

	public boolean isOnlyPacked() {
		return onlyPacked;
	}

	public void setOnlySingleMutedStrings(final boolean onlySingleMutedStrings) {
		this.onlySingleMutedStrings = onlySingleMutedStrings;
	}

	public boolean isOnlySingleMutedStrings() {
		return onlySingleMutedStrings;
	}

	public boolean areDoubledTonesAllowed() {
		return doubledTonesAllowed;
	}

	public void setDoubledTones(final boolean doubledTonesAllowed) {
		this.doubledTonesAllowed = doubledTonesAllowed;
	}

	public boolean isOnlyAscendingDescendingToneSequenceAllowed() {
		return onlyAscendingDescendingToneSequence;
	}

	public void setAscendingDescending(final boolean onlyAscendingDescendingToneSequence) {
		this.onlyAscendingDescendingToneSequence = onlyAscendingDescendingToneSequence;
	}

	public boolean isWithout1st() {
		return without1st;
	}

	public void setWithout1st(final boolean without1st) {
		this.without1st = without1st;
		intervalsChanged = true;
	}

	public boolean isWithout3rd() {
		return without3rd;
	}

	public void setWithout3rd(final boolean without3rd) {
		this.without3rd = without3rd;
		intervalsChanged = true;
	}

	public boolean isWithout5th() {
		return without5th;
	}

	public void setWithout5th(final boolean without5th) {
		this.without5th = without5th;
		intervalsChanged = true;
	}

	public List<Interval> getExcludedIntervals() {
		if (excludedIntervals == null || intervalsChanged) {
			excludedIntervals = new ArrayList<Interval>();
			if (isWithout1st()) {
				excludedIntervals.add(Factory.getInstance().getInterval(0));
			}
			if (isWithout3rd()) {
				excludedIntervals.add(Factory.getInstance().getInterval(3));
				excludedIntervals.add(Factory.getInstance().getInterval(4));
			}
			if (isWithout5th()) {
				excludedIntervals.add(Factory.getInstance().getInterval(7));
			}
			intervalsChanged = false;
		}
		return excludedIntervals;
	}
}
