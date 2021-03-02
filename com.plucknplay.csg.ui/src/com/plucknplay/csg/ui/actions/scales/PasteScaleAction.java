/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions.scales;

import org.eclipse.ui.IViewPart;

import com.plucknplay.csg.core.model.sets.CategoryList;
import com.plucknplay.csg.core.model.sets.ScaleList;
import com.plucknplay.csg.ui.actions.general.AbstractPasteAction;
import com.plucknplay.csg.ui.model.sets.Clipboard;
import com.plucknplay.csg.ui.util.LoginUtil;

public class PasteScaleAction extends AbstractPasteAction {

	private static final String ACTION_ID = "com.plucknplay.csg.ui.actions.pasteScaleAction"; //$NON-NLS-1$

	public PasteScaleAction(final IViewPart view) {
		super(view);
	}

	@Override
	protected Object getClipboardType() {
		return Clipboard.TYPE_SCALE;
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

	@Override
	protected String getActionId() {
		return ACTION_ID;
	}
}
