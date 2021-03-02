/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.core.model.sets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.plucknplay.csg.core.model.Categorizable;
import com.plucknplay.csg.core.model.listeners.IChangeListener;
import com.plucknplay.csg.core.model.listeners.IChangeListenerFilter;
import com.plucknplay.csg.core.model.workingCopies.WorkingCopy;
import com.plucknplay.csg.core.model.workingCopies.WorkingCopyManager;

public abstract class CategoryList {

	public static final Object PROP_ADDED = new Object();
	public static final Object PROP_REMOVED = new Object();
	public static final Object PROP_CHANGED_ELEMENT = new Object();
	public static final Object PROP_CHANGED_WHOLE_LIST = new Object();
	public static final Object PROP_MOVED = new Object();

	private final Category rootCategory;

	private List<IChangeListener> listeners;
	private List<IChangeListenerFilter> filters;

	protected CategoryList() {
		rootCategory = new Category();
	}

	/* --- listener handling --- */

	/**
	 * Adds a change listener to this list. Listeners will be notified if a new
	 * element is added to the list (PROP_CHORD_ADDED), an element is removed
	 * from the list (PROP_CHORD_REMOVED), or an existing element has changed
	 * (PROP_CHORD_CHANGED).
	 * 
	 * @param listener
	 *            the listener to add, must not be null
	 */
	public void addChangeListener(final IChangeListener listener) {
		if (listener == null) {
			throw new IllegalArgumentException();
		}
		if (listeners == null) {
			listeners = new ArrayList<IChangeListener>();
		}
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	/**
	 * Removes a change listener from this element list.
	 * 
	 * @param listener
	 *            the listener to remove
	 */
	public void removeChangeListener(final IChangeListener listener) {
		if (listener == null) {
			throw new IllegalArgumentException();
		}
		if (listeners != null) {
			listeners.remove(listener);
			if (listeners.isEmpty()) {
				listeners = null;
			}
		}
	}

	/**
	 * Adds a change listener filter to this list. Filters can
	 * 
	 * @param filter
	 *            the filter to add, must not be null
	 */
	public void addChangeListenerFilter(final IChangeListenerFilter filter) {
		if (filter == null) {
			throw new IllegalArgumentException();
		}
		if (filters == null) {
			filters = new ArrayList<IChangeListenerFilter>();
		}
		if (!filters.contains(filter)) {
			filters.add(filter);
		}
	}

	/**
	 * Removes a change listener filter from this element list.
	 * 
	 * @param filter
	 *            the filter to remove
	 */
	public void removeChangeListenerFilter(final IChangeListenerFilter filter) {
		filters.remove(filter);
		if (filters.isEmpty()) {
			filters = null;
		}
	}

	/**
	 * Notifies all the registered listeners that this list has changed.
	 * 
	 * @param obj
	 *            the element or category which was added, removed or changed
	 * @param category
	 *            the category which contains the changed element
	 * @param property
	 *            the property which identifies which kind of change has
	 *            happened
	 */
	protected void notifyListeners(final Object obj, final Category category, final Object property) {
		if (listeners == null) {
			return;
		}
		final List<IChangeListener> theListeners = new ArrayList<IChangeListener>(listeners);
		for (final IChangeListener listener : theListeners) {
			// check whether the current listener passes the registered filters
			boolean passFilters = true;
			if (filters != null) {
				for (final IChangeListenerFilter filter : filters) {
					if (!filter.passFilter(listener)) {
						passFilters = false;
						break;
					}
				}
			}

			// if yes: notify the current listener
			if (passFilters) {
				listener.notifyChange(obj, category, property);
			}
		}
	}

	/* --- list manipulation --- */

	/**
	 * Adds the given element to this list.
	 * 
	 * @param element
	 *            the element to be added, must not be null
	 * @param category
	 *            the category the element is added to, or null if none
	 */
	public void addElement(final Categorizable element, final Category category) {
		if (element == null) {
			throw new IllegalArgumentException();
		}

		checkCategorizableType(element);

		Category theCategory = category;
		if (category == null) {
			theCategory = rootCategory;
		}

		final String validatedName = theCategory.validateElementName(element.getName());
		element.setName(validatedName);
		theCategory.addElement(element);

		notifyListeners(element, theCategory, PROP_ADDED);
	}

	/**
	 * Removes the given element from this list.
	 * 
	 * @param element
	 *            the element to be removed, must not be null
	 */
	public void removeElement(final Categorizable element) {
		if (element == null) {
			throw new IllegalArgumentException();
		}

		checkCategorizableType(element);

		final Category category = rootCategory.getCategory(element);
		category.removeElement(element);

		notifyListeners(element, category, PROP_REMOVED);
	}

	/**
	 * Returns the element with the given name or null if no element with such a
	 * name exists.
	 * 
	 * @param name
	 *            the name, must not be null
	 * @param parentCategory
	 *            the category the element belongs to, must not be null
	 * @return the element with the given name or null
	 */
	public Categorizable getElement(final String name, final Category parentCategory) {
		if (name == null || parentCategory == null) {
			throw new IllegalArgumentException();
		}
		for (final Categorizable element : parentCategory.getElements()) {
			if (element.getName().equals(name)) {
				return element;
			}
		}
		return null;
	}

	/**
	 * Returns the (first) element with the given name or null if no element
	 * with such a name exists.
	 * 
	 * @param name
	 *            the name, must not be null
	 * @return the element with the given name or null
	 */
	public Categorizable getElement(final String name) {
		if (name == null) {
			throw new IllegalArgumentException();
		}
		for (final Categorizable element : rootCategory.getAllElements()) {
			if (element.getName().equals(name)) {
				return element;
			}
		}
		return null;
	}

	/**
	 * Notifies this list that the given element (element or category) has
	 * changed so that the list can forward this notification to all its
	 * listeners.
	 * 
	 * @param element
	 *            the element (element or category) which has changed
	 */
	public void changedElement(final Object element) {
		notifyListeners(element, null, PROP_CHANGED_ELEMENT);
	}

	/**
	 * Notifies this list that the whole list has changed so that the list can
	 * forward this notification to all its listeners.
	 */
	public void changedWholeList() {
		notifyListeners(null, null, PROP_CHANGED_WHOLE_LIST);
	}

	/**
	 * Duplicates the given element. The new element will be created inside the
	 * same category with an adjusted name.
	 * 
	 * @param element
	 *            the element to duplicate, must not be null
	 */
	public void duplicateElement(final Categorizable element) {
		if (element == null) {
			throw new IllegalArgumentException();
		}

		checkCategorizableType(element);

		// create new element with validated name
		final Category category = rootCategory.getCategory(element);
		final String validatedName = category.validateElementName(element.getName());
		final Categorizable newElement = createNewElement(element);
		newElement.setName(validatedName);

		addElement(newElement, category);
	}

	/**
	 * Adds a new category to this list.
	 * 
	 * Categories are useful to organize the list.
	 * 
	 * @param categoryToAdd
	 *            the category to be added, must not be null
	 * @param category
	 *            the category which shall contain the new category, or null if
	 *            none
	 */
	public void addCategory(final Category categoryToAdd, final Category category) {
		if (categoryToAdd == null) {
			throw new IllegalArgumentException();
		}

		Category theCategory = category;
		if (theCategory == null) {
			theCategory = rootCategory;
		}

		final String validatedName = theCategory.validateCategoryName(categoryToAdd.getName());
		categoryToAdd.setName(validatedName);
		theCategory.addCategory(categoryToAdd);

		notifyListeners(categoryToAdd, theCategory, PROP_ADDED);
	}

	/**
	 * Removes an existing category from this list.
	 * 
	 * @param category
	 *            the category to be removed, must not be null
	 */
	public void removeCategory(final Category category) {
		if (category == null) {
			throw new IllegalArgumentException();
		}

		// (1) delete all elements
		final Collection<Categorizable> elementsToDelete = category.getAllElements();
		for (final Categorizable element : elementsToDelete) {
			removeElement(element);
		}

		// (2) remove all (sub) categories
		final Category parentCategory = rootCategory.getCategory(category);
		removeSubCategoryRecursive(category, parentCategory);

		// (3) remove category
		parentCategory.removeCategory(category);
		notifyListeners(category, parentCategory, PROP_REMOVED);
	}

	/**
	 * Recursive helper method for the category removal.
	 */
	private void removeSubCategoryRecursive(final Category category, final Category parent) {
		final Collection<Category> categoriesToDelete = category.getCategories();
		for (final Category current : categoriesToDelete) {
			removeSubCategoryRecursive(current, category);
		}
		category.removeCategories(categoriesToDelete);
	}

	/**
	 * Returns the root category of this list.
	 * 
	 * @return the root category of this list
	 */
	public Category getRootCategory() {
		return rootCategory;
	}

	/**
	 * Clears the whole list.
	 */
	public void clear() {
		rootCategory.clear();
		changedWholeList();
	}

	/**
	 * Moves the given category to the given target category.
	 * 
	 * @param category
	 *            the category to be moved, must not be null
	 * @param targetCategory
	 *            the category where the category shall be moved in, must not be
	 *            null
	 */
	public void moveCategory(final Category category, final Category targetCategory) {
		if (category == null || targetCategory == null) {
			throw new IllegalArgumentException();
		}

		final Category originalParentCategory = getRootCategory().getCategory(category);

		// validate name
		final String validatedName = targetCategory.validateCategoryName(category.getName());
		if (!validatedName.equals(category.getName())) {
			category.setName(validatedName);
			notifyListeners(category, originalParentCategory, PROP_CHANGED_ELEMENT);
		}

		// move element
		originalParentCategory.removeCategory(category);
		targetCategory.addCategory(category);

		// notify listener
		notifyListeners(category, targetCategory, PROP_MOVED);
	}

	/**
	 * Moves the given element to the given target category.
	 * 
	 * @param element
	 *            the element to be moved, must not be null
	 * @param targetCategory
	 *            the category where the category shall be moved in, must not be
	 *            null
	 */
	public void moveElement(final Categorizable element, final Category targetCategory) {
		if (element == null || targetCategory == null) {
			throw new IllegalArgumentException();
		}

		checkCategorizableType(element);

		final Category originalCategory = getRootCategory().getCategory(element);

		// validate name
		final String validatedName = targetCategory.validateElementName(element.getName());
		if (!validatedName.equals(element.getName())) {
			final WorkingCopy workingCopy = WorkingCopyManager.getInstance().getWorkingCopy(element, originalCategory,
					false);
			workingCopy.setName(validatedName);
			workingCopy.saveName();
			WorkingCopyManager.getInstance().disposeWorkingCopy(element);
		}

		// move element
		originalCategory.removeElement(element);
		targetCategory.addElement(element);

		// notify listener
		notifyListeners(element, targetCategory, PROP_MOVED);
	}

	/**
	 * Checks whether this element is a valid element for this tzpe of list.
	 * 
	 * @param element
	 *            the element to check, must not be null
	 */
	protected abstract void checkCategorizableType(Categorizable element);

	/**
	 * Creates a new element using this element.
	 * 
	 * @param element
	 *            the element, must not be null
	 * @return a new cloned element, never null
	 */
	protected abstract Categorizable createNewElement(Categorizable element);
}
