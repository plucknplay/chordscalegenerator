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
import com.plucknplay.csg.core.model.listeners.IChangeListener;
import com.plucknplay.csg.core.model.sets.Category;
import com.plucknplay.csg.core.model.sets.CategoryList;

public abstract class AbstractAddAction extends Action implements ICategoryAction, IChangeListener,
		IViewSelectionAction {

	private Category selectedCategory;
	private final IViewPart view;

	public AbstractAddAction(final IViewPart view) {
		this.view = view;
		getCategoryList().addChangeListener(this);
		selectedCategory = getCategoryList().getRootCategory();
	}

	@Override
	public void selectionChanged(final ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			final IStructuredSelection structured = (IStructuredSelection) selection;
			if (!structured.isEmpty()) {
				final Object first = structured.getFirstElement();
				if (first instanceof Category) {
					selectedCategory = (Category) first;
				} else if (first instanceof Categorizable) {
					selectedCategory = getCategoryList().getRootCategory().getCategory(first);
				}
			} else {
				selectedCategory = getCategoryList().getRootCategory();
			}
		}
	}

	@Override
	public void notifyChange(final Object source, final Object parentSource, final Object property) {
		// necessary since it can happen that an old selection remains where
		// elements can't be added to
		if (property == CategoryList.PROP_REMOVED && source == selectedCategory) {
			selectedCategory = null;
		}
	}

	protected Category getSelectedCategory() {
		if (selectedCategory == null) {
			return getCategoryList().getRootCategory();
		}
		return selectedCategory;
	}

	protected IViewPart getViewPart() {
		return view;
	}
}
