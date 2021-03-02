/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.listeners;

/**
 * This interface has to be implemented by clients who want to listen to simple
 * changes of this plugin.
 */
public interface ISimpleChangeListener {

	/**
	 * Notifies the listeners.
	 * 
	 * @param property
	 *            the property to specify which kind of change has happened
	 * @param value
	 *            the new value if there is one
	 */
	void notifyChange(Object property, Object value);
}
