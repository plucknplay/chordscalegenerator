/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IPageListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;

public class SavePerspectiveAction extends Action {

	private static final String ID = "com.plucknplay.csg.ui.actions.save.perspective"; //$NON-NLS-1$
	private static final String COMMAND_ID = "com.plucknplay.csg.ui.savePerspective"; //$NON-NLS-1$

	private final IWorkbenchWindow window;

	public SavePerspectiveAction(final IWorkbenchWindow window) {
		this.window = window;
		setId(ID);
		setActionDefinitionId(COMMAND_ID);
		setText(ActionMessages.SavePerspectiveAction_text);

		// add page listener to manage the enablement
		window.addPageListener(new IPageListener() {
			@Override
			public void pageActivated(final IWorkbenchPage page) {
				updateEnablement();
			}

			@Override
			public void pageClosed(final IWorkbenchPage page) {
				updateEnablement();
			}

			@Override
			public void pageOpened(final IWorkbenchPage page) {
			}
		});
	}

	private void updateEnablement() {
		setEnabled(window.getActivePage() != null);
	}

	@Override
	public void run() {
		window.getActivePage().savePerspective();
	}
}
