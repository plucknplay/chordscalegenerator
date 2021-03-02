/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.editors.input;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import com.plucknplay.csg.core.model.Categorizable;
import com.plucknplay.csg.core.model.sets.Category;
import com.plucknplay.csg.core.model.sets.CategoryList;

public abstract class CategorizableEditorInput implements IEditorInput {

	private final Categorizable element;
	private final Category category;
	private boolean isNewElement;

	public CategorizableEditorInput(final Categorizable element, final Category category, final boolean isNewElement) {
		if (element == null || category == null) {
			throw new IllegalArgumentException();
		}
		this.element = element;
		this.category = category;
		this.isNewElement = isNewElement;
	}

	@Override
	public boolean exists() {
		return false;
	}

	@Override
	public String getName() {
		return element.getName();
	}

	@Override
	public IPersistableElement getPersistable() {
		return null;
	}

	@Override
	public Object getAdapter(final Class adapter) {
		return null;
	}

	@Override
	public String getToolTipText() {
		if (isNewElement()) {
			final String path = getCategoryList().getRootCategory().getRelativePathName(category) + category.getName();
			return "".equals(path) ? getNewElementName() : path + "/" + getNewElementName(); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return getCategoryList().getRootCategory().getRelativePathName(element) + element.getName();
	}

	/**
	 * Returns the categorizable element which shall be shown inside the
	 * corresponding editor.
	 * 
	 * @return the categorizable element which shall be shown inside the
	 *         corresponding editor
	 */
	public Categorizable getElement() {
		return element;
	}

	/**
	 * Returns the category of the element which shall be shown inside the
	 * corresponding editor.
	 * 
	 * @return the category of the element which shall be shown inside the
	 *         corresponding editor
	 */
	public Category getCategory() {
		return category;
	}

	/**
	 * Returns true if the element is a new one, false otherwise.
	 * 
	 * @return true if the element is a new one, false otherwise
	 */
	public boolean isNewElement() {
		return isNewElement;
	}

	/**
	 * Sets the new element state.
	 * 
	 * @param isNewElement
	 *            true if the element is a new one, false otherwise
	 */
	public void setNewElement(final boolean isNewElement) {
		this.isNewElement = isNewElement;
	}

	/**
	 * Returns the name for the new element which shall be shown inside the
	 * tooltip.
	 * 
	 * @return the name for the new element which shall be shown inside the
	 *         tooltip
	 */
	protected abstract String getNewElementName();

	/**
	 * Returns the category list this editor input is related with.
	 * 
	 * @return the category list this editor input is related with
	 */
	public abstract CategoryList getCategoryList();

	/* --- object methods --- */

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (element == null ? 0 : element.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final CategorizableEditorInput other = (CategorizableEditorInput) obj;
		if (other.element == null) {
			return false;
		}
		return other.element.equals(element);
	}
}
