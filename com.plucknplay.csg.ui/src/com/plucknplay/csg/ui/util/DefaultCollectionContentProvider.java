/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.util;

import java.util.Collection;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class DefaultCollectionContentProvider implements IStructuredContentProvider {

	@Override
	public Object[] getElements(final Object inputElement) {
		if (inputElement instanceof Collection) {
			return ((Collection<?>) inputElement).toArray();
		}
		return new Object[0];
	}

	@Override
	public void dispose() {
		// do nothing
	}

	@Override
	public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
		// do nothing
	}
}
