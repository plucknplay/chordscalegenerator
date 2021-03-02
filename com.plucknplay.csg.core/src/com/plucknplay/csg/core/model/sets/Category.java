/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.core.model.sets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.plucknplay.csg.core.model.Categorizable;
import com.plucknplay.csg.core.model.ModelObject;

/**
 * A category represents a simple container for model objects. Categories itself
 * can contain categories.
 */
public class Category extends ModelObject {

	private static final long serialVersionUID = 8361815330778407947L;

	private String name;
	private List<Categorizable> elements;
	private List<Category> categories;

	public Category() {
		this(""); //$NON-NLS-1$
	}

	public Category(final String name) {
		this.name = name;
	}

	/**
	 * Returns the name of this category.
	 * 
	 * @return the name of this category
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of this category.
	 * 
	 * @param name
	 *            the new name, must not be null
	 */
	public void setName(final String name) {
		if (name == null) {
			throw new IllegalArgumentException();
		}
		this.name = name;
	}

	/**
	 * Add the given element to this category.
	 * 
	 * @param element
	 *            the element to add, must not be null
	 */
	public void addElement(final Categorizable element) {
		if (element == null) {
			throw new IllegalArgumentException();
		}
		if (elements == null) {
			elements = new ArrayList<Categorizable>();
		}
		if (!elements.contains(element)) {
			elements.add(element);
		}
	}

	/**
	 * Adds the given elements to this category.
	 * 
	 * @param elements
	 *            the elements to add, must not be null
	 */
	public void addElements(final Collection<Categorizable> elements) {
		if (elements == null) {
			throw new IllegalArgumentException();
		}
		for (final Categorizable categorizable : elements) {
			addElement(categorizable);
		}
	}

	/**
	 * Removes the given element from this category.
	 * 
	 * @param element
	 *            the element to remove, must not be null
	 */
	public void removeElement(final Categorizable element) {
		if (element == null) {
			throw new IllegalArgumentException();
		}
		elements.remove(element);
		if (elements.isEmpty()) {
			elements = null;
		}
	}

	/**
	 * Removes the given elements from this category.
	 * 
	 * @param elements
	 *            the elements to remove, must not be null
	 */
	public void removeElements(final Collection<Categorizable> elements) {
		if (elements == null) {
			throw new IllegalArgumentException();
		}
		for (final Categorizable categorizable : elements) {
			removeElement(categorizable);
		}
	}

	/**
	 * Removes all elements and categories from this category.
	 */
	public void clear() {
		elements = null;
		categories = null;
	}

	/**
	 * Returns the elements of this category.
	 * 
	 * @return the elements of this category, never null
	 */
	public Collection<Categorizable> getElements() {
		if (elements == null) {
			return new ArrayList<Categorizable>();
		}
		return elements;
	}

	/**
	 * Add the given category to this category.
	 * 
	 * @param category
	 *            the category to add, must not be null
	 */
	public void addCategory(final Category category) {
		if (category == null) {
			throw new IllegalArgumentException();
		}
		if (categories == null) {
			categories = new ArrayList<Category>();
		}
		if (!categories.contains(category)) {
			categories.add(category);
		}
	}

	/**
	 * Adds the given categories to this category.
	 * 
	 * @param elements
	 *            the categories to add, must not be null
	 */
	public void addCategories(final Collection<Category> elements) {
		if (categories == null) {
			throw new IllegalArgumentException();
		}
		for (final Category category : categories) {
			addCategory(category);
		}
	}

	/**
	 * Removes the given category from this category.
	 * 
	 * @param category
	 *            the category to remove, must not be null
	 */
	public void removeCategory(final Category category) {
		if (category == null) {
			throw new IllegalArgumentException();
		}
		categories.remove(category);
		if (categories.isEmpty()) {
			categories = null;
		}
	}

	/**
	 * Removes the given categories from this category.
	 * 
	 * @param categoriesToRemove
	 *            the categories to remove, must not be null
	 */
	public void removeCategories(final Collection<Category> categoriesToRemove) {
		if (categoriesToRemove == null) {
			throw new IllegalArgumentException();
		}
		if (categories != null) {
			categories.removeAll(categoriesToRemove);
			if (categories.isEmpty()) {
				categories = null;
			}
		}
	}

	/**
	 * Returns the categories of this category.
	 * 
	 * @return the categories of this category, never null
	 */
	public Collection<Category> getCategories() {
		if (categories == null) {
			return new ArrayList<Category>();
		}
		return categories;
	}

	/**
	 * Returns the category which contains the given element or category.
	 * 
	 * @param element
	 *            the element or category, must not be null
	 * 
	 * @return the category which contains the given element
	 */
	public Category getCategory(final Object element) {
		if (element == null) {
			throw new IllegalArgumentException();
		}
		return findCategory(this, element);
	}

	/**
	 * Recursive helper method to find the category of a given Element or
	 * category (see getCategory(E element):Category).
	 * 
	 * @param category
	 *            the category to start with
	 * @param obj
	 *            the element or category
	 * 
	 * @return the category which contains the given element
	 */
	private Category findCategory(final Category category, final Object obj) {
		final List<Object> all = new ArrayList<Object>();
		all.addAll(category.getElements());
		all.addAll(category.getCategories());
		if (all.contains(obj)) {
			return category;
		}
		for (final Category current : category.getCategories()) {
			final Category found = findCategory(current, obj);
			if (found != null) {
				return found;
			}
		}
		return null;
	}

	/**
	 * Returns the category with the given name contained in this category, or
	 * null if no category exists.
	 * 
	 * @param name
	 *            the name
	 * 
	 * @return the category with the given name contained in this category, or
	 *         null
	 */
	public Category getCategory(final String name) {
		if (name == null) {
			return null;
		}
		for (final Category current : getCategories()) {
			if (current.getName().equals(name)) {
				return current;
			}
		}
		return null;
	}

	/**
	 * Returns all elements which are contained in this category and all sub
	 * categories.
	 * 
	 * @return all elements which are contained in this category and all sub
	 *         categories
	 */
	public List<Categorizable> getAllElements() {
		final List<Categorizable> result = new ArrayList<Categorizable>();
		findAllElements(this, result);
		return result;
	}

	/**
	 * Recursive helper method to find all elements of a given category
	 * 
	 * @param category
	 *            the category to start with
	 * @param result
	 *            the list to be filled
	 */
	private void findAllElements(final Category category, final Collection<Categorizable> result) {
		result.addAll(category.getElements());
		for (final Category current : category.getCategories()) {
			findAllElements(current, result);
		}
	}

	/**
	 * Returns all categories which are contained in this category and all sub
	 * categories.
	 * 
	 * @return all categories which are contained in this category and all sub
	 *         categories
	 */
	public Collection<Category> getAllCategories() {
		final List<Category> result = new ArrayList<Category>();
		findAllCategories(this, result);
		return result;
	}

	/**
	 * Recursive helper method to find all categories of a given category
	 * 
	 * @param category
	 *            the category to start with
	 * @param result
	 *            the list to be filled
	 */
	private void findAllCategories(final Category category, final Collection<Category> result) {
		result.addAll(category.getCategories());
		for (final Category current : category.getCategories()) {
			findAllCategories(current, result);
		}
	}

	/**
	 * Returns true if this category contains any element or category, or false
	 * otherwise.
	 * 
	 * @return true if this category contains any element or category, or false
	 *         otherwise
	 */
	public boolean isEmpty() {
		return (elements == null || elements.isEmpty()) && (categories == null || categories.isEmpty());
	}

	/**
	 * Returns the path from this category to the given element.
	 * 
	 * @return the path from this category to the given element
	 */
	public String getRelativePathName(final Object element) {
		String result = ""; //$NON-NLS-1$
		Category category = getCategory(element);
		while (category != this && category != null) {
			result = category.getName() + "/" + result; //$NON-NLS-1$
			category = getCategory(category);
		}
		return result;
	}

	/**
	 * Returns a list with category path of the given element. The category path
	 * contains this category as first element.
	 * 
	 * @param element
	 *            the element, must not be null
	 * @param includeElement
	 *            true if the given element shall be included into the category
	 *            path, or false otherwise
	 * 
	 * @return a list with category path of the given element, never null
	 */
	public List<?> getCategoryPath(final Object element, final boolean includeElement) {
		if (element == null) {
			throw new IllegalArgumentException();
		}

		final List<Object> result = new ArrayList<Object>();
		if (includeElement) {
			result.add(0, element);
		}
		Category category = getCategory(element);
		while (category != this && category != null) {
			result.add(0, category);
			category = getCategory(category);
		}
		return result;
	}

	/**
	 * Returns the first element of this category which can be found. The search
	 * will also effect sub categories if necessary.
	 * 
	 * @return the first element of this category, or null
	 */
	public Categorizable getFirstElement() {
		final List<Categorizable> allElements = getAllElements();
		if (allElements.isEmpty()) {
			return null;
		}
		return allElements.get(0);
	}

	/**
	 * Returns true if this category contains the given element or category
	 * somewhere in its hierarchy, or false otherwise.
	 * 
	 * @param element
	 *            the element or category, must not be null
	 * 
	 * @return true if this category contains the given element or category
	 *         somewhere in its hierarchy, or false otherwise
	 */
	public boolean contains(final Object element) {
		if (element == null) {
			throw new IllegalArgumentException();
		}
		if (element instanceof Category) {
			return getAllCategories().contains(element);
		}
		return getAllElements().contains(element);
	}

	/**
	 * This method checks whether the given category name already exists inside
	 * the selected category or not. If the name already exists a proper index
	 * number will be added to the name.
	 * 
	 * @param name
	 *            the name to check, must not be null
	 * 
	 * @return the validated name which can be used for the category creation
	 * 
	 *         TODO duplicated code for categories and instruments
	 */
	public String validateCategoryName(final String name) {
		if (name == null) {
			throw new IllegalArgumentException();
		}

		int index = 1;
		final int openBracketIndex = name.lastIndexOf("("); //$NON-NLS-1$
		final int closeBracketIndex = name.lastIndexOf(")"); //$NON-NLS-1$
		if (openBracketIndex > -1 && closeBracketIndex > -1 && closeBracketIndex == name.length() - 1
				&& openBracketIndex < closeBracketIndex) {
			final String potentialNumber = name.substring(openBracketIndex + 1, closeBracketIndex);
			try {
				index = new Integer(potentialNumber).intValue();
			} catch (final Exception e) {
			}
		}

		return validateCategoryName(name, index);
	}

	/**
	 * Helper method to check whether the given category name already exists
	 * inside the selected category or not. If the name already exists an index
	 * number will be added to the name and the new name is checked again. This
	 * will be repeated until a proper name was found.
	 * 
	 * @param nameToCheck
	 *            the name to check, must not be null
	 * @param index
	 *            the index
	 * 
	 * @return the validated name which can be used for the category creation
	 */
	private String validateCategoryName(final String nameToCheck, final int index) {

		int i = index;
		String theName = nameToCheck;

		boolean isOk = true;
		for (final Category current : getCategories()) {
			if (current.getName().equals(theName)) {
				isOk = false;
				break;
			}
		}
		if (!isOk) {
			if (i > 1) {
				theName = theName.substring(0, theName.lastIndexOf(" ")); //$NON-NLS-1$
			}
			i++;
			return validateCategoryName(theName + " (" + i + ")", i); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return theName;
	}

	/**
	 * This method checks whether the given element name already exists inside
	 * the selected category or not. If the name already exists a proper index
	 * number will be added to the name.
	 * 
	 * <p>
	 * Note the name of the element will be the one created using toString().
	 * </p>
	 * 
	 * @param name
	 *            the name to check, must not be null
	 * 
	 * @return the validated name which can be used for the category creation
	 */
	public String validateElementName(final String name) {
		if (name == null) {
			throw new IllegalArgumentException();
		}

		int index = 1;
		final int openBracketIndex = name.lastIndexOf("("); //$NON-NLS-1$
		final int closeBracketIndex = name.lastIndexOf(")"); //$NON-NLS-1$
		if (openBracketIndex > -1 && closeBracketIndex > -1 && closeBracketIndex == name.length() - 1
				&& openBracketIndex < closeBracketIndex) {
			final String potentialNumber = name.substring(openBracketIndex + 1, closeBracketIndex);
			try {
				index = new Integer(potentialNumber).intValue();
			} catch (final Exception e) {
			}
		}

		return validateElementName(name, index);
	}

	/**
	 * Helper method to check whether the given element name already exists
	 * inside the selected category or not. If the name already exists an index
	 * number will be added to the name and the new name is checked again. This
	 * will be repeated until a proper name was found.
	 * 
	 * @param nameToCheck
	 *            the name to check, must not be null
	 * @param index
	 *            the index
	 * 
	 * @return the validated name which can be used for the category creation
	 */
	private String validateElementName(final String nameToCheck, final int index) {

		int i = index;
		String theName = nameToCheck;

		boolean isOk = true;
		for (final Categorizable current : getElements()) {
			if (current.getName().equals(theName)) {
				isOk = false;
				break;
			}
		}
		if (!isOk) {
			if (i > 1) {
				theName = theName.substring(0, theName.lastIndexOf(" ")); //$NON-NLS-1$
			}
			i++;
			return validateElementName(theName + " (" + i + ")", i); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return theName;
	}

	/* --- Object methods --- */

	@Override
	public String toString() {
		return getName();
	}
}
