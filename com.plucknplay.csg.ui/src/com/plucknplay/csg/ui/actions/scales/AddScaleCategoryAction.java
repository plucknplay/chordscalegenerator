/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions.scales;

import org.eclipse.ui.IViewPart;

import com.plucknplay.csg.core.model.sets.CategoryList;
import com.plucknplay.csg.core.model.sets.ScaleList;
import com.plucknplay.csg.ui.actions.general.AbstractAddCategoryAction;
import com.plucknplay.csg.ui.util.LoginUtil;

public class AddScaleCategoryAction extends AbstractAddCategoryAction {

	private static final String ACTION_ID = "com.plucknplay.csg.ui.actions.addScaleAction"; //$NON-NLS-1$

	public AddScaleCategoryAction(final IViewPart view, final boolean popup) {
		super(view, popup);
		setId(ACTION_ID);
	}

	@Override
	public CategoryList getCategoryList() {
		return ScaleList.getInstance();
	}

	@Override
	public void run() {
		if (LoginUtil.isActivated()) {
			super.run();
		} else {
			LoginUtil.showUnsupportedFeatureInformation(getViewPart().getSite().getShell());
		}
	}
}
