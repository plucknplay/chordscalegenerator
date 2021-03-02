/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions.common;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.internal.WorkbenchWindow;
import org.eclipse.ui.internal.util.PrefUtil;

import com.plucknplay.csg.ui.actions.ActionMessages;

@SuppressWarnings("restriction")
public class ToggleToolbarAction extends Action {

	private static final String COMMAND_ID = "org.eclipse.ui.ToggleToolbar"; //$NON-NLS-1$

	private final WorkbenchWindow window;

	public ToggleToolbarAction(final IWorkbenchWindow window) {
		this.window = (WorkbenchWindow) window;
		setId(COMMAND_ID);
		setActionDefinitionId(COMMAND_ID);
		setText(ActionMessages.ToggleToolbarAction_text);
		setToolTipText(ActionMessages.ToggleToolbarAction_tooltip);
		update();
	}

	@Override
	public void run() {
		final IPreferenceStore prefs = PrefUtil.getAPIPreferenceStore();
		if (window.getCoolBarVisible()) {
			prefs.setValue(IWorkbenchPreferenceConstants.SHOW_TEXT_ON_PERSPECTIVE_BAR, false);
			prefs.setValue(IWorkbenchPreferenceConstants.DOCK_PERSPECTIVE_BAR, IWorkbenchPreferenceConstants.LEFT);
		} else {
			prefs.setValue(IWorkbenchPreferenceConstants.SHOW_TEXT_ON_PERSPECTIVE_BAR, true);
			prefs.setValue(IWorkbenchPreferenceConstants.DOCK_PERSPECTIVE_BAR, IWorkbenchPreferenceConstants.TOP_RIGHT);
		}
		window.toggleToolbarVisibility();
		update();
	}

	private void update() {
		setChecked(window.getCoolBarVisible());
	}
}
