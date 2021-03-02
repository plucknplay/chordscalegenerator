/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions.chords;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;

import com.plucknplay.csg.core.model.Griptable;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.IImageKeys;
import com.plucknplay.csg.ui.actions.ActionMessages;
import com.plucknplay.csg.ui.actions.general.IViewSelectionAction;

public class PlayChordResultAction extends Action implements IViewSelectionAction {

	private static final String COMMAND_ID = "com.plucknplay.csg.ui.playSound"; //$NON-NLS-1$

	private Griptable selectedGriptable;

	public PlayChordResultAction() {
		setActionDefinitionId(COMMAND_ID);
		setText(ActionMessages.PlayChordAction_text);
		setToolTipText(ActionMessages.PlayChordAction_text);
		setImageDescriptor(Activator.getImageDescriptor(IImageKeys.PLAY_SOUND));
		setEnabled(false);
	}

	@Override
	public void run() {
		if (selectedGriptable != null) {
			Activator.getDefault().getSoundMachine().play(selectedGriptable);
		}
	}

	@Override
	public void selectionChanged(final ISelection selection) {
		boolean enabled = false;
		if (selection != null && selection instanceof IStructuredSelection) {
			final IStructuredSelection structered = (IStructuredSelection) selection;
			if (structered.size() == 1 && structered.getFirstElement() instanceof Griptable) {
				selectedGriptable = (Griptable) structered.getFirstElement();
				enabled = true;
			}
		}
		setEnabled(enabled);
	}
}
