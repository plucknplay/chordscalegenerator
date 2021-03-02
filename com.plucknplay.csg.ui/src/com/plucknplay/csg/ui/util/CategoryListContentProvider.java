/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.util;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.plucknplay.csg.core.model.sets.ChordList;
import com.plucknplay.csg.core.model.sets.InstrumentList;
import com.plucknplay.csg.core.model.sets.ScaleList;

public class CategoryListContentProvider implements IStructuredContentProvider {

	@Override
	public Object[] getElements(final Object inputElement) {
		if (inputElement == ChordList.getInstance()) {
			return ChordList.getInstance().getRootCategory().getAllElements().toArray();
		} else if (inputElement == ScaleList.getInstance()) {
			return ScaleList.getInstance().getRootCategory().getAllElements().toArray();
		} else if (inputElement == InstrumentList.getInstance()) {
			return InstrumentList.getInstance().getRootCategory().getAllElements().toArray();
		}
		return new Object[0];
	}

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
	}
}
