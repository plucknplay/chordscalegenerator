/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions.util;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import com.plucknplay.csg.ui.util.LoginUtil;

public class ShowActivationInfoAction extends Action implements IWorkbenchWindowActionDelegate {

	public static final String COMMAND_ID = "com.plucknplay.csg.util.showActivationInfo";

	private IWorkbenchWindow window;

	@Override
	public void init(final IWorkbenchWindow window) {
		this.window = window;
		setActionDefinitionId(COMMAND_ID);
	}

	@Override
	public void run(final IAction action) {

		final StringBuffer buf = new StringBuffer();

		buf.append("Hardware Adresses: ");
		buf.append(LoginUtil.getHardwareAdressesInfo());
		buf.append("\n");

		buf.append("Operating System: ");
		buf.append(LoginUtil.getOperatingSystemInfo());
		buf.append("\n");

		buf.append("Download Source: ");
		buf.append(LoginUtil.getDownloadSourceInfo());

		MessageDialog.openInformation(window.getShell(), "Show Activation Info", buf.toString());
	}

	@Override
	public void selectionChanged(final IAction action, final ISelection selection) {
		// do nothing
	}

	@Override
	public void dispose() {
		// do nothing
	}
}
