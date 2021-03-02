/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.core.internal.calculation.filters;

import com.plucknplay.csg.core.model.Griptable;
import com.plucknplay.csg.core.model.Note;

public class LeadToneFilter implements IFilter {

	private final Note leadTone;

	/**
	 * The constructor.
	 * 
	 * @param leadTone
	 *            the bass tone, must not be null
	 */
	public LeadToneFilter(final Note leadTone) {
		if (leadTone == null) {
			throw new IllegalArgumentException();
		}
		this.leadTone = leadTone;
	}

	@Override
	public boolean passFilter(final Griptable griptable) {
		final Note griptableLeadTone = griptable.getLeadTone();
		if (griptableLeadTone == null) {
			return true;
		}
		return griptableLeadTone.hasSameValue(leadTone);
	}
}
