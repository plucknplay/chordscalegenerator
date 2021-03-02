/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions;

import org.eclipse.jface.action.Action;

import com.plucknplay.csg.ui.UIConstants;
import com.plucknplay.csg.ui.views.IModeView;

public class AbstractShowAction extends Action {

	private final IModeView view;
	private final String relatedMode;

	public AbstractShowAction(final IModeView view, final String relatedMode) {
		if (view == null || relatedMode == null) {
			throw new IllegalArgumentException();
		}

		this.view = view;
		this.relatedMode = relatedMode;
	}

	@Override
	public int getStyle() {
		return AS_CHECK_BOX;
	}

	@Override
	public void run() {
		view.setMode(isChecked() ? relatedMode : UIConstants.MODE_POINTS);
	}
}
