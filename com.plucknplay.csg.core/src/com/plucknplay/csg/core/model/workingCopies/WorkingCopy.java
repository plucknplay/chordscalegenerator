/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.core.model.workingCopies;

import java.util.ArrayList;
import java.util.List;

import com.plucknplay.csg.core.model.Categorizable;

public abstract class WorkingCopy {

	public static final Object PROP_NAME_CHANGED = new Object();
	public static final Object PROP_COMMENT_CHANGED = new Object();
	public static final Object PROP_DIRTY_STATE_CHANGED = new Object();

	private List<IWorkingCopyChangeListener> listeners;
	private boolean isDirty;

	/**
	 * The constructor.
	 */
	public WorkingCopy() {
		isDirty = false;
	}

	/* --- getter --- */

	/**
	 * Creates the corresponding working copy of this element.
	 * 
	 * @return the corresponding working copy of this element
	 */
	protected abstract Categorizable getWorkingCopy();

	/**
	 * Returns the current name of this working copy.
	 * 
	 * @return the current name of this working copy
	 */
	public String getName() {
		return getWorkingCopy().getName();
	}

	/**
	 * Returns the current comment of this working copy.
	 * 
	 * @return the current comment of this working copy
	 */
	public String getComment() {
		return getWorkingCopy().getComment();
	}

	/* --- setter --- */

	/**
	 * Sets the current name of this working copy.
	 * 
	 * @param name
	 *            the new current name
	 */
	public void setName(final String name) {
		getWorkingCopy().setName(name);
		notifyListeners(name, PROP_NAME_CHANGED);
	}

	/**
	 * Sets the current comment of this working copy.
	 * 
	 * @param comment
	 *            the new current comment
	 */
	public void setComment(final String comment) {
		getWorkingCopy().setComment(comment);
		notifyListeners(comment, PROP_COMMENT_CHANGED);
	}

	/* --- save --- */

	/**
	 * Saves only the name of this working copy.
	 * 
	 * @return true if the save was successful, false otherwise
	 */
	public abstract boolean saveName();

	/**
	 * Saves this working copy.
	 * 
	 * @return true if the save was successful, false otherwise
	 */
	public abstract boolean save();

	/* --- listener --- */

	/**
	 * Adds a working copy change listener to this working copy.
	 * 
	 * @param listener
	 *            the listener to add, must not be null
	 */
	public void addListener(final IWorkingCopyChangeListener listener) {
		if (listeners == null) {
			listeners = new ArrayList<IWorkingCopyChangeListener>();
		}
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	/**
	 * Removes a working copy change listener from this working copy.
	 * 
	 * @param listener
	 *            the listener to remove
	 */
	public void removeListener(final IWorkingCopyChangeListener listener) {
		if (listeners == null) {
			return;
		}
		listeners.remove(listener);
		if (listeners.isEmpty()) {
			listeners = null;
		}
	}

	/**
	 * Notifies all listeners.
	 * 
	 * @param value
	 *            the value which has changed
	 * @param property
	 *            the property (see PROP_*) which has changed
	 */
	protected void notifyListeners(final Object value, final Object property) {
		if (listeners == null) {
			return;
		}
		for (final IWorkingCopyChangeListener listener : listeners) {
			listener.notifyChange(value, property);
		}
		// replace with equal check
		if (property != PROP_DIRTY_STATE_CHANGED) {
			checkDirty();
		}
	}

	/* --- dirty handling --- */

	/**
	 * Sets the dirty state of this working copy and notifies all listeneres.
	 * 
	 * @param dirty
	 *            the new dirty state
	 */
	protected void setDirty(final boolean dirty) {
		if (dirty != isDirty) {
			isDirty = dirty;
			notifyListeners(Boolean.valueOf(isDirty), PROP_DIRTY_STATE_CHANGED);
		}
	}

	/**
	 * Returns true if this working copy is dirty, false otherwise.
	 * 
	 * @return true if this working copy is dirty, false otherwise
	 */
	public boolean isDirty() {
		return isDirty;
	}

	protected abstract void checkDirty();

	/**
	 * Returns true if the name of this working copy is valid, or false
	 * otherwise. The name is valid if the name is not empty.
	 * 
	 * @return true if the name of this working copy is valid, or false
	 *         otherwise
	 */
	public boolean isValidName() {
		return !"".equals(getWorkingCopy().getName().trim()); //$NON-NLS-1$
	}
}
