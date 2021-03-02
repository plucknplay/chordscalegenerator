/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions.common;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.internal.WorkbenchWindow;

import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.actions.ActionMessages;

@SuppressWarnings("restriction")
public class ToggleStatusBarAction extends Action {

	private static final String COMMAND_ID = "org.eclipse.ui.ToggleStatusbar"; //$NON-NLS-1$

	private final WorkbenchWindow window;

	public ToggleStatusBarAction(final IWorkbenchWindow window) {
		this.window = (WorkbenchWindow) window;
		setId(COMMAND_ID);
		setActionDefinitionId(COMMAND_ID);
		setText(ActionMessages.ToggleStatusBarAction_text);
		setToolTipText(ActionMessages.ToggleStatusBarAction_tooltip);
		update();
	}

	@Override
	public void run() {
		final boolean visible = window.getStatusLineVisible();
		Activator.getDefault().getPreferenceStore().setValue(Preferences.SHOW_STATUS_BAR, !visible);
		window.setStatusLineVisible(!visible);
		window.getShell().layout();
		update();
	}

	private void update() {
		setChecked(window.getStatusLineVisible());
	}
}
