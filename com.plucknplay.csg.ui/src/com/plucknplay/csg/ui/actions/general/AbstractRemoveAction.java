/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions.general;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IViewPart;

import com.plucknplay.csg.core.model.Categorizable;
import com.plucknplay.csg.core.model.sets.Category;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.actions.ActionMessages;
import com.plucknplay.csg.ui.activation.NlsUtil;
import com.plucknplay.csg.ui.listeners.CategoryViewFilter;
import com.plucknplay.csg.ui.util.Util;

public abstract class AbstractRemoveAction extends Action implements ICategoryAction, IViewSelectionAction {

	private static final String COMMAND_ID = "org.eclipse.ui.edit.delete"; //$NON-NLS-1$

	private final IViewPart view;
	private IStructuredSelection selection;

	public AbstractRemoveAction(final IViewPart view) {
		this.view = view;

		setId(getActionId());
		setActionDefinitionId(COMMAND_ID);
		setText(NlsUtil.getAction_remove());
		setToolTipText(NlsUtil.getAction_remove());
		setImageDescriptor(Activator.getImageDescriptor(NlsUtil.getAction_image_remove()));
	}

	protected abstract String getActionId();

	@Override
	public void run() {
		if (isDeletionConfirmed(selection)) {

			// validate selection
			final List<?> selectedElements = Util.validateSelection(selection);
			final CategoryViewFilter filter = new CategoryViewFilter();
			getCategoryList().addChangeListenerFilter(filter);

			// remove selected elements
			for (final Object next : selectedElements) {
				if (next instanceof Categorizable) {
					getCategoryList().removeElement((Categorizable) next);
				} else if (next instanceof Category) {
					getCategoryList().removeCategory((Category) next);
				}
			}

			getCategoryList().removeChangeListenerFilter(filter);
			getCategoryList().changedWholeList();
		}
	}

	protected boolean isDeletionConfirmed(final IStructuredSelection selection) {
		return MessageDialog.openConfirm(view.getSite().getShell(),
				ActionMessages.AbstractRemoveAction_confirm_dialog_title,
				ActionMessages.AbstractRemoveAction_confirm_dialog_msg);
	}

	@Override
	public void selectionChanged(final ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			this.selection = (IStructuredSelection) selection;
			setEnabled(!this.selection.isEmpty());
		} else {
			setEnabled(false);
		}
	}

	protected IViewPart getViewPart() {
		return view;
	}
}
