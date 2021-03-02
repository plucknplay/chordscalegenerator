/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.advisors;

import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.activation.ActivateAction;
import com.plucknplay.csg.ui.activation.ActivationDialog;
import com.plucknplay.csg.ui.model.BlockManager;
import com.plucknplay.csg.ui.model.sets.ListManager;
import com.plucknplay.csg.ui.perspectives.ChordsPerspective;
import com.plucknplay.csg.ui.util.LoginUtil;

/**
 * This workbench advisor creates the window advisor, and specifies the
 * perspective id for the initial window.
 */
public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {

	@Override
	public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(final IWorkbenchWindowConfigurer configurer) {
		return new ApplicationWorkbenchWindowAdvisor(configurer);
	}

	@Override
	public String getInitialWindowPerspectiveId() {
		return ChordsPerspective.ID;
	}

	/* --- loading the lists --- */

	@Override
	public void initialize(final IWorkbenchConfigurer configurer) {
		configurer.setSaveAndRestore(true);
		TrayDialog.setDialogHelpAvailable(true);
		ListManager.loadLists();
	}

	@Override
	public void postStartup() {
		if (Activator.getDefault().getPreferenceStore().getBoolean(Preferences.SHOW_LOGIN_PROMPT)
				&& !LoginUtil.isActivated()) {
			ActivateAction.perform(ActivationDialog.MODE_START_UP);
		}
	}

	/* --- storing the lists --- */

	@Override
	public void postShutdown() {
		BlockManager.getInstance().dispose();
		ListManager.storeLists();
	}
}
