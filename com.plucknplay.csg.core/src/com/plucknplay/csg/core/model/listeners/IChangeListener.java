/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.core.model.listeners;

/**
 * This interface has to be implemented by clients who want to listen to changes
 * which happened somewhere in the application.
 */
public interface IChangeListener {

	/**
	 * Notifies the listener when a change has happened.
	 * 
	 * @param source
	 *            the object the change is related to
	 * @param parentSource
	 *            the parent object of the source
	 * @param property
	 *            the property to specify which kind of change has happened
	 */
	void notifyChange(Object source, Object parentSource, Object property);
}
