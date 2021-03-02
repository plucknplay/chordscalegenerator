/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions;

import org.eclipse.jface.action.Action;

import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.IImageKeys;
import com.plucknplay.csg.ui.Preferences;

public class ShowBlocksAction extends Action {

	private static final String COMMAND_ID = "com.plucknplay.csg.ui.showBlocks"; //$NON-NLS-1$

	public ShowBlocksAction() {
		setId(COMMAND_ID);
		setActionDefinitionId(COMMAND_ID);
		setText(ActionMessages.ShowBlocksAction_text);
		setToolTipText(ActionMessages.ShowBlocksAction_text);
		setImageDescriptor(Activator.getImageDescriptor(IImageKeys.SHOW_BLOCKS));
		setChecked(Activator.getDefault().getPreferenceStore().getBoolean(Preferences.SHOW_BLOCKS));
	}

	@Override
	public int getStyle() {
		return AS_CHECK_BOX;
	}

	@Override
	public void run() {
		Activator.getDefault().getPreferenceStore().setValue(Preferences.SHOW_BLOCKS, isChecked());
	}
}
