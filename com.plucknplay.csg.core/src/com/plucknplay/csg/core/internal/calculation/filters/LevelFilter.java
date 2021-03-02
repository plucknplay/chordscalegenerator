/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.core.internal.calculation.filters;

import com.plucknplay.csg.core.model.Griptable;

public class LevelFilter implements IFilter {

	private final int minLevel;
	private final int maxLevel;
	private final boolean preferBarrees;

	public LevelFilter(final int minLevel, final int maxLevel, final boolean preferBarrees) {
		this.minLevel = minLevel;
		this.maxLevel = maxLevel;
		this.preferBarrees = preferBarrees;
	}

	@Override
	public boolean passFilter(final Griptable griptable) {
		final int level = griptable.getFingering(preferBarrees).getLevel();
		return level >= minLevel && level <= maxLevel;
	}
}
