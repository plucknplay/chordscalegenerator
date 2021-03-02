/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions.chords;

import org.eclipse.ui.IViewPart;

import com.plucknplay.csg.core.model.sets.CategoryList;
import com.plucknplay.csg.core.model.sets.ChordList;
import com.plucknplay.csg.ui.actions.general.AbstractAddCategoryAction;
import com.plucknplay.csg.ui.util.LoginUtil;

public class AddChordCategoryAction extends AbstractAddCategoryAction {

	private static final String ACTION_ID = "com.plucknplay.csg.ui.actions.addChordCategoryAction"; //$NON-NLS-1$

	public AddChordCategoryAction(final IViewPart view, final boolean popup) {
		super(view, popup);
		setId(ACTION_ID);
	}

	@Override
	public CategoryList getCategoryList() {
		return ChordList.getInstance();
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
