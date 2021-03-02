/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.views.dnd;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;

import com.plucknplay.csg.core.model.Categorizable;
import com.plucknplay.csg.core.model.sets.Category;
import com.plucknplay.csg.ui.util.Util;

public class ModelObjectDragListener implements DragSourceListener {

	private final TreeViewer viewer;
	private final Object type;

	public ModelObjectDragListener(final TreeViewer viewer, final Object type) {
		this.viewer = viewer;
		this.type = type;
	}

	@Override
	public void dragStart(final DragSourceEvent event) {
		viewer.cancelEditing();
		event.doit = !viewer.getSelection().isEmpty();
	}

	@Override
	public void dragSetData(final DragSourceEvent event) {
		final IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
		final List<?> validatedSelection = Util.validateSelection(selection);
		addPathsToSelections(validatedSelection);
		if (ModelObjectTransfer.getInstance(type).isSupportedType(event.dataType)) {
			event.data = validatedSelection;
		}
	}

	private void addPathsToSelections(final List<?> validatedSelection) {
		final Category rootCategory = ModelObjectTransfer.getRootCategory(type);
		if (rootCategory == null) {
			return;
		}
		for (final Object element : validatedSelection) {
			if (element instanceof Categorizable) {
				final Categorizable theElement = (Categorizable) element;
				final String fqName = rootCategory.getRelativePathName(theElement) + "/" + theElement.getName(); //$NON-NLS-1$
				theElement.setData(ModelObjectTransfer.DATA_KEY_FQ_NAME, fqName);
			} else if (element instanceof Category) {
				final Category category = (Category) element;
				final String fqName = rootCategory.getRelativePathName(category) + "/" + category.getName(); //$NON-NLS-1$
				category.setData(ModelObjectTransfer.DATA_KEY_FQ_NAME, fqName);
			}
		}
	}

	@Override
	public void dragFinished(final DragSourceEvent event) {
		// do nothing
	}
}
