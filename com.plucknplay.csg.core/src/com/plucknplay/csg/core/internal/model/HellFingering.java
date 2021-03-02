/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.core.internal.model;

import java.util.ArrayList;
import java.util.List;

import com.plucknplay.csg.core.Constants;
import com.plucknplay.csg.core.model.FretboardPosition;
import com.plucknplay.csg.core.model.IFingering;

/**
 * This class represents the fingering for ungrippable griptables.
 */
public class HellFingering implements IFingering {

	private static final int MAX_DISTANCE = 1000;

	@Override
	public Byte getFingerNumber(final FretboardPosition fretboardPosition) {
		return null;
	}

	@Override
	public void setFingerNumber(final FretboardPosition fretboardPosition, final byte fingerNumber) {
		// do nothing
	}

	@Override
	public void setIsBarrePossible(final boolean isBarrePossible) {
		// do nothing
	}

	@Override
	public boolean isBarrePossible() {
		return false;
	}

	@Override
	public boolean hasBarre() {
		return false;
	}

	@Override
	public int getLevel() {
		return Constants.MAX_LEVEL;
	}

	@Override
	public List<FretboardPosition> getFretboardPositions(final byte fingerNumber) {
		return new ArrayList<FretboardPosition>();
	}

	@Override
	public int getMaxSingleDistance() {
		return MAX_DISTANCE;
	}

	@Override
	public int getTotalDistance() {
		return MAX_DISTANCE;
	}
}
