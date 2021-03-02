/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.core.model.sets;

import com.plucknplay.csg.core.model.Categorizable;
import com.plucknplay.csg.core.model.Scale;

/**
 * This container class stores all known scales for the application.
 * 
 * <p>
 * Note this class is a singleton and can't be initiate using a constructor.
 * Invoke getInstance() to retrieve the shared instance of this class.
 * </p>
 */
public final class ScaleList extends IntervalContainerList {

	private static ScaleList instance;

	/**
	 * The private default constructor.
	 */
	private ScaleList() {
		super();
	}

	/**
	 * Returns the singleton instance of the scale list.
	 * 
	 * @return the singleton instance of the scale list, never null
	 */
	public static ScaleList getInstance() {
		if (instance == null) {
			instance = new ScaleList();
		}
		return instance;
	}

	@Override
	protected void checkCategorizableType(final Categorizable element) {
		if (!(element instanceof Scale)) {
			throw new IllegalArgumentException();
		}
	}

	@Override
	protected Categorizable createNewElement(final Categorizable element) {
		if (!(element instanceof Scale)) {
			throw new IllegalArgumentException();
		}
		return new Scale((Scale) element);
	}
}
