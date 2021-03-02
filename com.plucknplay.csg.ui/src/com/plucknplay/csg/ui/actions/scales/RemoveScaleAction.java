/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions.scales;

import org.eclipse.ui.IViewPart;

import com.plucknplay.csg.core.model.sets.CategoryList;
import com.plucknplay.csg.core.model.sets.ScaleList;
import com.plucknplay.csg.ui.actions.general.AbstractRemoveAction;

public class RemoveScaleAction extends AbstractRemoveAction {

	private static final String ACTION_ID = "com.plucknplay.csg.ui.actions.removeScaleAction"; //$NON-NLS-1$

	public RemoveScaleAction(final IViewPart view) {
		super(view);
	}

	@Override
	public CategoryList getCategoryList() {
		return ScaleList.getInstance();
	}

	@Override
	protected String getActionId() {
		return ACTION_ID;
	}
}
