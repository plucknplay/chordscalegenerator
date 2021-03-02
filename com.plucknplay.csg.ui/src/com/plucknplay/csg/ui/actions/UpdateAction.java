/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.update.ui.UpdateJob;
import org.eclipse.update.ui.UpdateManagerUI;

public class UpdateAction extends Action {

	private static final String ID = "com.plucknplay.csg.ui.actions.global.check.for.updates"; //$NON-NLS-1$

	private final IWorkbenchWindow window;

	public UpdateAction(final IWorkbenchWindow window) {
		this.window = window;
		setText(ActionMessages.UpdateAction_check_for_updates);
		setId(ID);
	}

	@Override
	public void run() {
		BusyIndicator.showWhile(window.getShell().getDisplay(), new Runnable() {
			@Override
			public void run() {
				final UpdateJob job = new UpdateJob(ActionMessages.UpdateAction_search_for_updates, false, false);
				UpdateManagerUI.openInstaller(window.getShell(), job);
			}
		});
	}
}
