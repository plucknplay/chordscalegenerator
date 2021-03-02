/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.core.internal.calculation.filters;

import com.plucknplay.csg.core.model.Griptable;

public interface IFilter {

	/**
	 * Returns true if the given griptable passes this filter, false otherwise.
	 * 
	 * @param griptable
	 *            the griptable, must not be null
	 * 
	 * @return true if the given griptable passes this filter, false otherwise
	 */
	boolean passFilter(Griptable griptable);
}
