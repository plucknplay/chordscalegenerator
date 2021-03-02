/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.views.dnd;

import org.eclipse.jface.viewers.TreeViewer;

import com.plucknplay.csg.core.model.sets.CategoryList;
import com.plucknplay.csg.core.model.sets.InstrumentList;

public class InstrumentDropAdapter extends ModelObjectDropAdapter {

	public InstrumentDropAdapter(final TreeViewer viewer) {
		super(viewer, ModelObjectTransfer.TYPE_INSTRUMENT);
	}

	@Override
	protected CategoryList getCategoryList() {
		return InstrumentList.getInstance();
	}
}
