/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import com.plucknplay.csg.core.model.sets.Category;
import com.plucknplay.csg.core.model.sets.CategoryList;

public abstract class AbstractExportWorkbenchAction extends AbstractExportAction implements
		IWorkbenchWindowActionDelegate {

	private List<Category> categories;

	private IWorkbenchWindow window;

	@Override
	public void init(final IWorkbenchWindow window) {
		this.window = window;
	}

	@Override
	public void selectionChanged(final IAction action, final ISelection selection) {
		// do nothing
	}

	@Override
	public void dispose() {
		// do nothing
	}

	@Override
	protected List<Category> getCategories() {
		if (categories == null) {
			categories = new ArrayList<Category>();
			for (final Category category : getCategoryList().getRootCategory().getCategories()) {
				findCategories(category);
			}
		}
		return categories;
	}

	private void findCategories(final Category category) {
		categories.add(category);
		for (final Category subCategory : category.getCategories()) {
			findCategories(subCategory);
		}
	}

	protected abstract CategoryList getCategoryList();

	@Override
	protected Shell getShell() {
		return window.getShell();
	}
}
