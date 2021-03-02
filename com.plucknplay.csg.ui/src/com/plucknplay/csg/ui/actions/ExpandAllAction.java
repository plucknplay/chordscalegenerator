/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions;

import org.eclipse.jface.action.Action;

import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.IImageKeys;
import com.plucknplay.csg.ui.views.IExpandableView;

public class ExpandAllAction extends Action {

	private static final String COMMAND_ID = "org.eclipse.ui.navigate.expandAll"; //$NON-NLS-1$

	private final IExpandableView view;

	/**
	 * The constuctor.
	 * 
	 * @param view
	 *            the collapsable view, must not be null
	 */
	public ExpandAllAction(final IExpandableView view) {
		if (view == null) {
			throw new IllegalArgumentException();
		}
		this.view = view;

		setId(IActionIds.ACT_COLLAPSE_ALL);
		setActionDefinitionId(COMMAND_ID);
		setText(ActionMessages.ExpandAllAction_text);
		setToolTipText(ActionMessages.ExpandAllAction_text);
		setImageDescriptor(Activator.getImageDescriptor(IImageKeys.EXPAND_ALL));
	}

	@Override
	public void run() {
		view.expandAll();
	}
}
