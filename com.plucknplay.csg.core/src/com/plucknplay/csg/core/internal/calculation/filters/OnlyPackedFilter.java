/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.core.internal.calculation.filters;

import com.plucknplay.csg.core.model.FretboardPosition;
import com.plucknplay.csg.core.model.Griptable;

public class OnlyPackedFilter implements IFilter {

	@Override
	public boolean passFilter(final Griptable griptable) {

		boolean foundFirstBlockBeginnning = false;
		boolean foundFirstBlockEnding = false;

		for (final FretboardPosition fbp : griptable.getFretboardPositions(true)) {
			final int currentAssignment = fbp.getFret();
			if (!foundFirstBlockBeginnning && currentAssignment != -1) {
				foundFirstBlockBeginnning = true;
			} else if (foundFirstBlockBeginnning && !foundFirstBlockEnding && currentAssignment == -1) {
				foundFirstBlockEnding = true;
			} else if (foundFirstBlockEnding && currentAssignment != -1) {
				return false;
			}
		}
		return true;
	}
}
