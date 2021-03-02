/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.core.internal.calculation.filters;

import java.util.Collections;
import java.util.List;

import com.plucknplay.csg.core.model.Griptable;
import com.plucknplay.csg.core.model.Interval;

public class ToneNumberFilter implements IFilter {

	private final int maxToneNumber;

	/**
	 * The constructor.
	 * 
	 * @param maxToneNumber
	 *            the tone number which is not be allowed to be exceeded
	 */
	public ToneNumberFilter(final int maxToneNumber) {
		this.maxToneNumber = maxToneNumber;
	}

	@Override
	public boolean passFilter(final Griptable griptable) {
		final List<Interval> intervals = griptable.getIntervals(false);
		for (final Interval interval : intervals) {
			if (Collections.frequency(intervals, interval) > maxToneNumber) {
				return false;
			}
		}
		return true;
	}
}
