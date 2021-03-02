/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions.general;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IViewPart;

import com.plucknplay.csg.core.model.Categorizable;
import com.plucknplay.csg.core.model.sets.Category;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.activation.NlsUtil;
import com.plucknplay.csg.ui.listeners.CategoryViewFilter;
import com.plucknplay.csg.ui.util.LoginUtil;

public abstract class AbstractDuplicateAction extends Action implements IViewSelectionAction, ICategoryAction {

	private static final String COMMAND_ID = "com.plucknplay.csg.ui.duplicate"; //$NON-NLS-1$

	private final IViewPart view;
	private IStructuredSelection selection;

	public AbstractDuplicateAction(final IViewPart view) {
		this.view = view;

		setId(getActionId());
		setActionDefinitionId(COMMAND_ID);
		setText(NlsUtil.getAction_duplicate());
		setToolTipText(NlsUtil.getAction_duplicate());
		setImageDescriptor(Activator.getImageDescriptor(NlsUtil.getAction_image_copy()));
	}

	protected abstract String getActionId();

	@Override
	public void run() {
		if (LoginUtil.isActivated()) {
			final CategoryViewFilter filter = new CategoryViewFilter();
			getCategoryList().addChangeListenerFilter(filter);

			for (final Object obj : selection.toArray()) {
				if (obj instanceof Categorizable) {
					getCategoryList().duplicateElement((Categorizable) obj);
				}
			}

			getCategoryList().removeChangeListenerFilter(filter);
			getCategoryList().changedWholeList();
		} else {
			LoginUtil.showUnsupportedFeatureInformation(view.getSite().getShell());
		}
	}

	@Override
	public void selectionChanged(final ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			this.selection = (IStructuredSelection) selection;
			boolean enable = !this.selection.isEmpty();
			if (enable) {
				for (final Object obj : this.selection.toArray()) {
					if (obj instanceof Category) {
						enable = false;
						break;
					}
				}
			}
			setEnabled(enable);
		} else {
			setEnabled(false);
		}
	}
}
