/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.core.model.listeners;

public interface IChangeListenerFilter {

	/**
	 * Returns true if the given listener passes this filter, or false
	 * otherwise.
	 * 
	 * @param listener
	 *            the listener, must not be null
	 * 
	 * @return true if the given listener passes this filter, or false otherwise
	 */
	boolean passFilter(IChangeListener listener);
}
