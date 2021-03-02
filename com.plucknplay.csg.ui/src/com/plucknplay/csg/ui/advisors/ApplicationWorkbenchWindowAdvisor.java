/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.advisors;

import java.awt.Dimension;
import java.awt.Toolkit;

import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.internal.IPreferenceConstants;
import org.eclipse.ui.internal.WorkbenchWindow;
import org.eclipse.ui.util.APIPrefUtil;

import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.actions.LeftRightHanderContributionItem;
import com.plucknplay.csg.ui.actions.instruments.FretNumberContributionItem;
import com.plucknplay.csg.ui.actions.instruments.OpenCurrentInstrumentAction;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

	private static final int MIN_WIDTH = 640;
	private static final int MIN_HEIGHT = 480;
	private static final int MAX_WIDTH = 1400;
	private static final int MAX_HEIGHT = 1050;

	public ApplicationWorkbenchWindowAdvisor(final IWorkbenchWindowConfigurer configurer) {
		super(configurer);
	}

	@Override
	public ActionBarAdvisor createActionBarAdvisor(final IActionBarConfigurer configurer) {
		return new ApplicationActionBarAdvisor(configurer);
	}

	@Override
	@SuppressWarnings("restriction")
	public void preWindowOpen() {

		final IWorkbenchWindowConfigurer configurer = getWindowConfigurer();

		final IPreferenceStore prefs = APIPrefUtil.getInternalPreferenceStore();

		configurer.setInitialSize(new Point(1000, 750));
		configurer.setShowCoolBar(true);
		configurer.setShowStatusLine(true);
		configurer.setShowPerspectiveBar(prefs.getBoolean(IPreferenceConstants.PERSPECTIVEBAR_VISIBLE));
		configurer.setShowFastViewBars(prefs.getBoolean(IPreferenceConstants.FASTVIEWBAR_VISIBLE));
		configurer.setShowProgressIndicator(false);

		((WorkbenchWindow) configurer.getWindow()).setStatusLineVisible(Activator.getDefault().getPreferenceStore()
				.getBoolean(Preferences.SHOW_STATUS_BAR));

		final IStatusLineManager mgr = configurer.getActionBarConfigurer().getStatusLineManager();
		mgr.appendToGroup(StatusLineManager.BEGIN_GROUP, new LeftRightHanderContributionItem());
		mgr.appendToGroup(StatusLineManager.MIDDLE_GROUP, new OpenCurrentInstrumentAction(configurer.getWindow()));
		mgr.appendToGroup(StatusLineManager.END_GROUP, new FretNumberContributionItem());
	}

	@Override
	@SuppressWarnings("restriction")
	public void postWindowOpen() {

		// JarSignUtil.validateJar();

		((WorkbenchWindow) getWindowConfigurer().getWindow()).getCoolBarManager2().setLockLayout(
				Activator.getDefault().getPreferenceStore().getBoolean(Preferences.LOCK_TOOLBAR));
	}

	@Override
	public void postWindowCreate() {
		final IPreferenceStore prefs = Activator.getDefault().getPreferenceStore();
		if (prefs.getBoolean(Preferences.FIRST_START)) {

			// only perfom this method while first start up
			prefs.setValue(Preferences.FIRST_START, false);

			// determine screen dimension
			final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			final int screenWidth = (int) screenSize.getWidth();
			final int screenHeight = (int) screenSize.getHeight();

			// set initial window size
			final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			shell.setMinimumSize(new Point(MIN_WIDTH, MIN_HEIGHT));
			if (screenWidth > MAX_WIDTH || screenHeight > MAX_HEIGHT) {
				final int width = Math.min(screenWidth, MAX_WIDTH);
				final int height = Math.min(screenHeight, MAX_HEIGHT);
				shell.setBounds((screenWidth - width) / 2, (screenHeight - height) / 2, width, height);
			} else {
				shell.setMaximized(true);
			}
		}
	}

	@Override
	public boolean preWindowShellClose() {
		if (getWindowConfigurer().getWorkbenchConfigurer().getWorkbench().getWorkbenchWindowCount() > 1) {
			return true;
		}

		final IPreferenceStore prefs = Activator.getDefault().getPreferenceStore();
		final boolean promptOnExit = prefs.getBoolean(Preferences.CONFIRM_EXIT);

		if (promptOnExit) {

			final String title = AdvisorMessages.ApplicationWorkbenchWindowAdvisorexit_prompt_title;
			final String message = AdvisorMessages.ApplicationWorkbenchWindowAdvisorexit_prompt_message;
			final String prompt = AdvisorMessages.ApplicationWorkbenchWindowAdvisorexit_prompt_question;

			final MessageDialogWithToggle dialog = MessageDialogWithToggle.openOkCancelConfirm(getWindowConfigurer()
					.getWindow().getShell(), title, message, prompt, false, null, null);

			if (dialog.getReturnCode() != IDialogConstants.OK_ID) {
				return false;
			}
			if (dialog.getToggleState()) {
				prefs.setValue(Preferences.CONFIRM_EXIT, false);
			}
		}

		return true;
	}
}
