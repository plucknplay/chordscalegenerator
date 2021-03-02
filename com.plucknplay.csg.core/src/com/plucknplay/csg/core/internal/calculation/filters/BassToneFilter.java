/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.core.internal.calculation.filters;

import com.plucknplay.csg.core.model.Griptable;
import com.plucknplay.csg.core.model.Note;

public class BassToneFilter implements IFilter {

	private final Note bassTone;

	/**
	 * The constructor.
	 * 
	 * @param bassTone
	 *            the bass tone, must not be null
	 */
	public BassToneFilter(final Note bassTone) {
		if (bassTone == null) {
			throw new IllegalArgumentException();
		}
		this.bassTone = bassTone;
	}

	@Override
	public boolean passFilter(final Griptable griptable) {
		final Note griptableBassTone = griptable.getBassTone();
		if (griptableBassTone == null) {
			return true;
		}
		return griptableBassTone.hasSameValue(bassTone);
	}
}
