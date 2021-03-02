/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions;

import org.eclipse.jface.action.Action;

import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.IImageKeys;
import com.plucknplay.csg.ui.views.ICollapsableView;

public class CollapseAllAction extends Action {

	private static final String COMMAND_ID = "org.eclipse.ui.navigate.collapseAll"; //$NON-NLS-1$

	private final ICollapsableView view;

	/**
	 * The constuctor.
	 * 
	 * @param view
	 *            the collapsable view, must not be null
	 */
	public CollapseAllAction(final ICollapsableView view) {
		if (view == null) {
			throw new IllegalArgumentException();
		}
		this.view = view;

		setId(IActionIds.ACT_COLLAPSE_ALL);
		setActionDefinitionId(COMMAND_ID);
		setText(ActionMessages.CollapseAllAction_text);
		setToolTipText(ActionMessages.CollapseAllAction_text);
		setImageDescriptor(Activator.getImageDescriptor(IImageKeys.COLLAPSE_ALL));
	}

	@Override
	public void run() {
		view.collapseAll();
	}
}
