/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.core.internal.calculation.filters;

import com.plucknplay.csg.core.model.Griptable;

public class OnlySingleMutedStringsFilter implements IFilter {

	@Override
	public boolean passFilter(final Griptable griptable) {
		return griptable.getMutedStringsCount() == 1;
	}
}
