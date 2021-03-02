/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.views.dnd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TransferData;

import com.plucknplay.csg.core.model.Categorizable;
import com.plucknplay.csg.core.model.sets.Category;
import com.plucknplay.csg.core.model.sets.CategoryList;
import com.plucknplay.csg.ui.listeners.CategoryViewFilter;

public abstract class ModelObjectDropAdapter extends ViewerDropAdapter {

	private final Object type;

	public ModelObjectDropAdapter(final TreeViewer viewer, final Object type) {
		super(viewer);
		this.type = type;
		setFeedbackEnabled(false);
	}

	protected List<?> getOriginalList(final List<?> toDrop) {
		final List<Object> originals = new ArrayList<Object>();
		final Category rootCategory = ModelObjectTransfer.getRootCategory(type);
		originals.add(rootCategory);
		originals.addAll(rootCategory.getAllCategories());
		originals.addAll(rootCategory.getAllElements());

		final Map<String, Object> elementFQNameMap = new HashMap<String, Object>();
		for (final Object element : originals) {
			if (element instanceof Categorizable) {
				final Categorizable theElement = (Categorizable) element;
				final String fqName = rootCategory.getRelativePathName(theElement) + "/" + theElement.getName(); //$NON-NLS-1$
				elementFQNameMap.put(fqName, element);
			} else if (element instanceof Category) {
				final Category category = (Category) element;
				final String fqName = rootCategory.getRelativePathName(category) + "/" + category.getName(); //$NON-NLS-1$
				elementFQNameMap.put(fqName, element);
			}
		}

		final List<Object> result = new ArrayList<Object>();
		for (final Object element : toDrop) {
			if (element instanceof Category) {
				final Category category = (Category) element;
				final String copyFQName = (String) category.getData(ModelObjectTransfer.DATA_KEY_FQ_NAME);
				final Category originalCategory = (Category) elementFQNameMap.get(copyFQName);
				if (originalCategory != null) {
					result.add(originalCategory);
				}
			} else if (element instanceof Categorizable) {
				final Categorizable theElement = (Categorizable) element;
				final String copyFQName = (String) theElement.getData(ModelObjectTransfer.DATA_KEY_FQ_NAME);
				final Categorizable originalElement = (Categorizable) elementFQNameMap.get(copyFQName);
				if (originalElement != null) {
					result.add(originalElement);
				}
			}
		}
		return result;
	}

	@Override
	public boolean performDrop(final Object data) {

		// (1) get necessary information
		final List<?> toDrop = (List<?>) data;
		if (toDrop == null || toDrop.isEmpty()) {
			return false;
		}
		final int operation = getCurrentOperation();

		// (1) determine the target category
		final Object target = getCurrentTarget();
		Category targetCategory = null;
		if (target == null) {
			targetCategory = getCategoryList().getRootCategory();
		} else if (target instanceof Category) {
			targetCategory = (Category) target;
		} else if (target instanceof Categorizable) {
			targetCategory = getCategoryList().getRootCategory().getCategory(target);
		}
		if (targetCategory == null) {
			return false;
		}

		// (2) perform copy
		if (operation == DND.DROP_COPY) {
			for (final Object element : toDrop) {
				if (element instanceof Category) {
					getCategoryList().addCategory((Category) element, targetCategory);
				} else if (element instanceof Categorizable) {
					getCategoryList().addElement((Categorizable) element, targetCategory);
				}
			}

			// (3) perform move
		} else if (operation == DND.DROP_MOVE) {
			// (3.1) map to original list of elements
			final List<?> originals = getOriginalList(toDrop);

			// (3.2) check target category - cannot drop a category onto itself
			// or a child
			for (final Object element : originals) {
				final Category originalCategory = getCategoryList().getRootCategory().getCategory(element);
				if (originalCategory.equals(targetCategory)) {
					return false;
				}
				if (element instanceof Category) {
					final Category category = (Category) element;
					if (category.equals(targetCategory) || category.getAllCategories().contains(targetCategory)) {
						return false;
					}
				}
			}

			// (3.3) perform move
			final CategoryViewFilter filter = new CategoryViewFilter();
			getCategoryList().addChangeListenerFilter(filter);

			for (final Object element : originals) {
				if (element instanceof Category) {
					getCategoryList().moveCategory((Category) element, targetCategory);
				} else if (element instanceof Categorizable) {
					getCategoryList().moveElement((Categorizable) element, targetCategory);
				}
			}

			getCategoryList().removeChangeListenerFilter(filter);
			getCategoryList().changedWholeList();
		}
		return true;
	}

	@Override
	public boolean validateDrop(final Object target, final int operation, final TransferData transferType) {
		return ModelObjectTransfer.getInstance(type).isSupportedType(transferType);
	}

	protected abstract CategoryList getCategoryList();
}
