/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.core.model;

import java.io.Serializable;

import com.plucknplay.csg.core.Constants;
import com.plucknplay.csg.core.util.NamesUtil;

/**
 * This class represents an interval. An interval is simply defined by its
 * halfsteps.
 */
public class Interval implements Serializable, Comparable<Interval> {

	private static final long serialVersionUID = -7752270606467319419L;

	private int halfsteps;

	// --- constructor --- //

	/**
	 * The constructor.
	 * 
	 * @param halfsteps
	 *            the halfsteps
	 */
	/* package */Interval(final int halfsteps) {
		this.halfsteps = halfsteps % Constants.INTERVALS_NUMBER;
		if (this.halfsteps < 0) {
			this.halfsteps = 0;
		}
	}

	// --- getter & setter --- //

	/**
	 * Returns the halfsteps of this interval.
	 * 
	 * @return the halfsteps of this interval
	 */
	public int getHalfsteps() {
		return halfsteps;
	}

	/**
	 * Returns the default name of this interval.
	 * 
	 * @return the default name of this interval
	 */
	public String getDefaultName() {
		return NamesUtil.getDefaultIntervalName(this);
	}

	// --- object methods --- //

	@Override
	public String toString() {
		return getDefaultName();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Interval)) {
			return false;
		}
		return halfsteps == ((Interval) obj).halfsteps;
	}

	@Override
	public int hashCode() {
		return Integer.valueOf(halfsteps).hashCode();
	}

	@Override
	public int compareTo(final Interval other) {
		if (other == null) {
			return 0;
		}
		return Integer.valueOf(halfsteps).compareTo(Integer.valueOf(other.halfsteps));
	}
}
