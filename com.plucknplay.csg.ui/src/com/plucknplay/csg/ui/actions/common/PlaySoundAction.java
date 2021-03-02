/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions.common;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;

import com.plucknplay.csg.core.model.Block;
import com.plucknplay.csg.core.model.Griptable;
import com.plucknplay.csg.core.model.Scale;
import com.plucknplay.csg.sound.SoundMachine;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.IImageKeys;
import com.plucknplay.csg.ui.actions.ActionMessages;
import com.plucknplay.csg.ui.model.BlockManager;

public class PlaySoundAction extends Action implements ISelectionListener {

	private static final String COMMAND_ID = "com.plucknplay.csg.ui.playSound"; //$NON-NLS-1$

	private final IWorkbenchWindow window;
	private Object selectedObject;

	public PlaySoundAction(final IWorkbenchWindow window) {
		setId(COMMAND_ID);
		setActionDefinitionId(COMMAND_ID);
		setText(ActionMessages.PlaySoundAction_text);
		setToolTipText(ActionMessages.PlaySoundAction_tooltip);
		setImageDescriptor(Activator.getImageDescriptor(IImageKeys.PLAY_SOUND));

		this.window = window;
		this.window.getSelectionService().addSelectionListener(this);
	}

	@Override
	public void run() {
		if (selectedObject == null) {
			return;
		}
		final SoundMachine soundMachine = Activator.getDefault().getSoundMachine();
		if (selectedObject instanceof Griptable) {
			soundMachine.play((Griptable) selectedObject);
		} else if (selectedObject instanceof Block) {
			soundMachine.play((Block) selectedObject);
		} else if (selectedObject instanceof Scale) {
			soundMachine.play(BlockManager.getInstance().getDefaultBlock((Scale) selectedObject));
		}
	}

	@Override
	public void selectionChanged(final IWorkbenchPart part, final ISelection selection) {
		if (selection != null && selection instanceof IStructuredSelection) {
			final IStructuredSelection structured = (IStructuredSelection) selection;
			if (!selection.isEmpty()) {
				final Object first = structured.getFirstElement();
				if (first instanceof Griptable || first instanceof Scale || first instanceof Block) {
					setEnabled(true);
					selectedObject = first;
					return;
				}
			}
		}
		setEnabled(false);
		selectedObject = null;
	}

	public void dispose() {
		window.getSelectionService().removeSelectionListener(this);
	}
}
