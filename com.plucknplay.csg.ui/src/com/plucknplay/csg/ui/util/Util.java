/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;

import com.plucknplay.csg.core.model.sets.Category;

/**
 * This class provides some useful utility methods.
 */
public final class Util {

	private Util() {
	}

	/**
	 * Converts a collection into a beautified string. The elements of the
	 * collection will be separated with ", ".
	 * 
	 * @param collection
	 *            the collection, must not be null
	 * 
	 * @return a string where each collection element is separated by a comma
	 */
	public static String convertCollectionToString(final Collection<?> collection) {
		return convertCollectionToString(collection, ", "); //$NON-NLS-1$
	}

	/**
	 * Converts a collection into a beautified string. The elements of the
	 * collection will be separated with the given separator.
	 * 
	 * @param collection
	 *            the collection, must not be null
	 * @param separator
	 *            the string for the separation of the elements
	 * 
	 * @return a string where each collection element is separated by the given
	 *         separator
	 */
	public static String convertCollectionToString(final Collection<?> collection, final String separator) {
		if (collection == null) {
			throw new IllegalArgumentException();
		}

		final StringBuffer result = new StringBuffer();
		for (final Object name : collection) {
			if (result.length() > 0) {
				result.append(separator);
			}
			result.append(name);
		}
		return result.toString();
	}

	/**
	 * Converts an array into a beautified string. The elements of the array
	 * will be separated with ", ".
	 * 
	 * @param array
	 *            the array, must not be null
	 * 
	 * @return a string where each array element is separated by a comma
	 */
	public static String convertArrayToString(final Object[] array) {
		return convertArrayToString(array, ", "); //$NON-NLS-1$
	}

	/**
	 * Converts an array into a beautified string. The elements of the array
	 * will be separated with the given separator.
	 * 
	 * @param array
	 *            the array, must not be null
	 * @param separator
	 *            the string for the separation of the elements
	 * 
	 * @return a string where each array element is separated by the given
	 *         separator
	 */
	public static String convertArrayToString(final Object[] array, final String separator) {
		if (array == null) {
			throw new IllegalArgumentException();
		}

		final StringBuffer result = new StringBuffer();
		for (int i = 0; i < array.length; i++) {
			if (i > 0) {
				result.append(separator);
			}
			result.append(array[i].toString());
		}
		return result.toString();
	}

	/**
	 * Validates the given selection. Selections which are implicitely covered
	 * by other selections will be removed.
	 * 
	 * @param selection
	 *            the selection
	 * 
	 * @return the validated selection as a list
	 */
	@SuppressWarnings("unchecked")
	public static List<?> validateSelection(final IStructuredSelection selection) {
		final List<?> result = new ArrayList<Object>(selection.toList());
		final List<?> theList = new ArrayList<Object>(selection.toList());
		final List<?> theList2 = new ArrayList<Object>(selection.toList());

		// (1) find all categories
		for (final Object element : theList) {
			if (element instanceof Category) {
				final Category category = (Category) element;
				for (final Object current : theList2) {
					if (category.contains(current)) {
						result.remove(current);
					}
				}
			}
		}

		return result;
	}

	/**
	 * Calculates a readable string for the given time in milliseconds.
	 * 
	 * @param timeInMilliseconds
	 *            a time in milliseconds
	 * 
	 * @return a readable string for the given time in milliseconds
	 */
	public static String calculateTimeInformation(final long timeInMilliseconds) {
		final int milliSeconds = (int) (timeInMilliseconds % 1000);
		final int seconds = (int) (timeInMilliseconds / 1000);

		final String result = seconds > 0 ? seconds + "." + milliSeconds + "s" : //$NON-NLS-1$ //$NON-NLS-2$
				milliSeconds + "ms"; //$NON-NLS-1$

		return result;
	}

	/**
	 * Determines the number of selected elements.
	 * 
	 * @param selectedElements
	 *            the selected elements, must not be null
	 * 
	 * @return the number of selected elements
	 */
	public static int determineNumberOfSelections(final List<?> selectedElements) {
		if (selectedElements == null) {
			throw new IllegalArgumentException();
		}

		int counter = 0;
		for (final Object next : selectedElements) {
			if (next instanceof Category) {
				counter += ((Category) next).getAllElements().size();
			} else {
				counter++;
			}
		}
		return counter;
	}
}
