/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions.general;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IViewPart;

import com.plucknplay.csg.core.model.Categorizable;
import com.plucknplay.csg.core.model.sets.Category;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.activation.NlsUtil;
import com.plucknplay.csg.ui.listeners.CategoryViewFilter;
import com.plucknplay.csg.ui.model.sets.Clipboard;

public abstract class AbstractPasteAction extends Action implements ICategoryAction, IViewSelectionAction {

	private static final String COMMAND_ID = "org.eclipse.ui.edit.paste"; //$NON-NLS-1$

	private final IViewPart view;
	private IStructuredSelection selection;

	public AbstractPasteAction(final IViewPart view) {
		this.view = view;

		setId(getActionId());
		setActionDefinitionId(COMMAND_ID);
		setText(NlsUtil.getAction_paste());
		setToolTipText(NlsUtil.getAction_paste());
		setImageDescriptor(Activator.getImageDescriptor(NlsUtil.getAction_image_paste()));
	}

	protected abstract String getActionId();

	@Override
	public void run() {

		// (0) get input
		final List<?> input = Clipboard.getInstance().getInput();
		if (input == null) {
			return;
		}

		// (1) find category where to paste the input
		Category category = null;
		if (selection.isEmpty()) {
			category = getCategoryList().getRootCategory();
		} else if (selection.getFirstElement() instanceof Category) {
			category = (Category) selection.getFirstElement();
		} else if (selection.getFirstElement() instanceof Categorizable) {
			category = getCategoryList().getRootCategory().getCategory(selection.getFirstElement());
		}

		// (2) add inputs
		final CategoryViewFilter filter = new CategoryViewFilter();
		getCategoryList().addChangeListenerFilter(filter);

		for (final Object element : input) {
			if (element instanceof Category) {
				getCategoryList().addCategory((Category) element, category);
			} else if (element instanceof Categorizable) {
				getCategoryList().addElement((Categorizable) element, category);
			}
		}

		getCategoryList().removeChangeListenerFilter(filter);
		getCategoryList().changedWholeList();
	}

	@Override
	public void selectionChanged(final ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			this.selection = (IStructuredSelection) selection;
			setEnabled(!Clipboard.getInstance().isEmpty()
					&& Clipboard.getInstance().getInputType() == getClipboardType() && this.selection.size() <= 1);
		} else {
			setEnabled(false);
		}
	}

	protected IViewPart getViewPart() {
		return view;
	}

	protected abstract Object getClipboardType();
}
