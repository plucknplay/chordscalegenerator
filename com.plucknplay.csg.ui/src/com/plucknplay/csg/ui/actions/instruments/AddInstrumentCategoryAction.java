/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions.instruments;

import org.eclipse.ui.IViewPart;

import com.plucknplay.csg.core.model.sets.CategoryList;
import com.plucknplay.csg.core.model.sets.InstrumentList;
import com.plucknplay.csg.ui.actions.general.AbstractAddCategoryAction;

public class AddInstrumentCategoryAction extends AbstractAddCategoryAction {

	private static final String ACTION_ID = "com.plucknplay.csg.ui.actions.addInstrumentCategoryAction"; //$NON-NLS-1$

	public AddInstrumentCategoryAction(final IViewPart view, final boolean popup) {
		super(view, popup);
		setId(ACTION_ID);
	}

	@Override
	public CategoryList getCategoryList() {
		return InstrumentList.getInstance();
	}
}
