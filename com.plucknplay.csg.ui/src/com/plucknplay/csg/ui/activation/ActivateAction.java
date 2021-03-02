/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.activation;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.IImageKeys;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.util.LoginUtil;

public class ActivateAction extends Action {

	private static final String ID = "com.plucknplay.csg.ui.actions.activate"; //$NON-NLS-1$

	public ActivateAction() {
		setText(NlsUtil.getActivateAction_activate_full_version());
		setId(ID);
		setImageDescriptor(Activator.getImageDescriptor(IImageKeys.ACTIVATE));
	}

	@Override
	public void run() {
		perform(ActivationDialog.MODE_NORMAL);
	}

	public static void perform(final Object dialogMode) {
		final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();

		if (dialogMode == ActivationDialog.MODE_START_UP
				&& !Activator.getDefault().getPreferenceStore().getBoolean(Preferences.SHOW_LOGIN_PROMPT)) {
			return;
		}

		final ActivationDialog dialog = new ActivationDialog(shell, dialogMode);
		if (dialog.open() == Dialog.OK) {

			final int result = dialog.getResult();

			if (result == 0) {
				LoginUtil.activate();
			} else if (result == 1) {
				new ExpiredKeyDialog(shell, dialog.getKey()).open();
			}
		}
	}
}
