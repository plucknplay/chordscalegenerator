/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import com.plucknplay.csg.core.model.sets.Category;

public class AbstractExportCategoryAction extends AbstractExportAction implements IObjectActionDelegate {

	private IWorkbenchPart targetPart;

	private List<Category> selectedCategories = new ArrayList<Category>();

	@Override
	public void setActivePart(final IAction action, final IWorkbenchPart targetPart) {
		this.targetPart = targetPart;
	}

	@Override
	public void selectionChanged(final IAction action, final ISelection selection) {
		selectedCategories = new ArrayList<Category>();
		if (selection != null && selection instanceof IStructuredSelection) {
			final IStructuredSelection structered = (IStructuredSelection) selection;
			for (final Object selectedElement : structered.toArray()) {
				if (selectedElement instanceof Category) {
					selectedCategories.add((Category) selectedElement);
				}
			}
		}
	}

	@Override
	protected List<Category> getCategories() {
		return selectedCategories;
	}

	@Override
	protected Shell getShell() {
		return targetPart.getSite().getShell();
	}

	@Override
	public void run(final IAction action) {
		run();
	}
}
