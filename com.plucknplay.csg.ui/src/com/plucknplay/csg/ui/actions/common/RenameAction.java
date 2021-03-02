/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions.common;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IViewPart;

import com.plucknplay.csg.ui.actions.general.IViewSelectionAction;
import com.plucknplay.csg.ui.activation.NlsUtil;
import com.plucknplay.csg.ui.util.LoginUtil;
import com.plucknplay.csg.ui.views.AbstractIntervalContainerView;
import com.plucknplay.csg.ui.views.IRenameableView;

/**
 * This actions starts the renaming of a view.
 */
public class RenameAction extends Action implements IViewSelectionAction {

	private static final String COMMAND_ID = "org.eclipse.ui.edit.rename"; //$NON-NLS-1$
	private static final String ACTION_ID = "com.plucknplay.csg.ui.actions.renameAction"; //$NON-NLS-1$

	private final IViewPart view;

	public RenameAction(final IViewPart view) {
		this.view = view;

		setId(ACTION_ID);
		setActionDefinitionId(COMMAND_ID);
		setText(NlsUtil.getAction_rename());
		setToolTipText(NlsUtil.getAction_rename());
	}

	@Override
	public void run() {
		if (view instanceof IRenameableView) {
			if (view instanceof AbstractIntervalContainerView && !LoginUtil.isActivated()) {
				LoginUtil.showUnsupportedFeatureInformation(view.getSite().getShell());
			} else {
				((IRenameableView) view).performRenaming();
			}
		}
	}

	@Override
	public void selectionChanged(final ISelection selection) {
		if (!selection.isEmpty() && selection instanceof IStructuredSelection) {
			setEnabled(((IStructuredSelection) selection).size() == 1);
		} else {
			setEnabled(false);
		}
	}
}
