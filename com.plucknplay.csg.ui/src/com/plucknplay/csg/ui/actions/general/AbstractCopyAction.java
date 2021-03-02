/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions.general;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;

import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.IImageKeys;
import com.plucknplay.csg.ui.actions.ActionMessages;
import com.plucknplay.csg.ui.model.sets.Clipboard;

public abstract class AbstractCopyAction extends Action implements IViewSelectionAction {

	private static final String COMMAND_ID = "org.eclipse.ui.edit.copy"; //$NON-NLS-1$

	private IStructuredSelection selection;

	public AbstractCopyAction() {
		setId(getActionId());
		setActionDefinitionId(COMMAND_ID);
		setText(ActionMessages.CopyAction_text);
		setToolTipText(ActionMessages.CopyAction_tooltip);
		setImageDescriptor(Activator.getImageDescriptor(IImageKeys.COPY));
	}

	@Override
	public void run() {
		Clipboard.getInstance().setInput(selection, getClipboardType());
	}

	@Override
	public void selectionChanged(final ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			this.selection = (IStructuredSelection) selection;
			setEnabled(!this.selection.isEmpty());
		}
	}

	protected abstract Object getClipboardType();

	protected abstract String getActionId();
}
