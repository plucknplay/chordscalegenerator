/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.plucknplay.csg.core.model.sets.Category;
import com.plucknplay.csg.core.model.sets.CategoryList;

public class CategoryTreeContentProvider implements ITreeContentProvider {

	private final CategoryList categoryList;

	public CategoryTreeContentProvider(final CategoryList categoryList) {
		if (categoryList == null) {
			throw new IllegalArgumentException();
		}
		this.categoryList = categoryList;
	}

	@Override
	public Object[] getChildren(final Object parentElement) {
		if (parentElement instanceof Category) {
			final Category category = (Category) parentElement;
			final List<Object> all = new ArrayList<Object>();
			all.addAll(category.getCategories());
			all.addAll(category.getElements());
			return all.toArray();
		}
		return new Object[0];
	}

	@Override
	public Object getParent(final Object element) {
		return categoryList.getRootCategory().getCategory(element);
	}

	@Override
	public boolean hasChildren(final Object element) {
		if (element instanceof Category) {
			return !((Category) element).isEmpty();
		}
		return false;
	}

	@Override
	public Object[] getElements(final Object inputElement) {
		return getChildren(inputElement);
	}

	@Override
	public void dispose() {
		// do nothing
	}

	@Override
	public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
		// do nothing
	}
}
