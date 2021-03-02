/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions.common;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;

import com.plucknplay.csg.ui.actions.ActionMessages;

public class EnableEditorAreaAction extends Action {

	private static final String COMMAND_ID = "com.plucknplay.csg.ui.showEditorArea"; //$NON-NLS-1$

	private final IWorkbenchWindow window;

	private final IPerspectiveListener perspectiveListener = new IPerspectiveListener() {
		@Override
		public void perspectiveActivated(final IWorkbenchPage page, final IPerspectiveDescriptor perspective) {
			updateActionEnablement();
		}

		@Override
		public void perspectiveChanged(final IWorkbenchPage page, final IPerspectiveDescriptor perspective,
				final String changeId) {
			updateActionEnablement();
		}
	};

	public EnableEditorAreaAction(final IWorkbenchWindow window) {
		setId(COMMAND_ID);
		setActionDefinitionId(COMMAND_ID);
		setText(ActionMessages.EnableEditorAreaAction_text);
		setToolTipText(ActionMessages.EnableEditorAreaAction_tooltip);

		this.window = window;
		this.window.addPerspectiveListener(perspectiveListener);
	}

	@Override
	public void run() {
		window.getActivePage().setEditorAreaVisible(!window.getActivePage().isEditorAreaVisible());
		updateActionEnablement();
	}

	private void updateActionEnablement() {
		if (window == null || window.getActivePage() == null) {
			return;
		}
		setChecked(window.getActivePage().isEditorAreaVisible());
		setEnabled(!isChecked() || window.getActivePage().getEditorReferences().length == 0);
	}

	public void dispose() {
		window.removePerspectiveListener(perspectiveListener);
	}
}
