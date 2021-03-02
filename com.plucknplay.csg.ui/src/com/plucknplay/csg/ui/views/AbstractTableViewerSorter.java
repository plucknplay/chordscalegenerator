/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.views;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public abstract class AbstractTableViewerSorter implements Comparator<Object>, Serializable {

	protected static final int ASCENDING = 1;
	protected static final int DESCENDING = -1;

	private static final long serialVersionUID = 8521430382490158240L;

	private List<Integer> columnsList;

	private int[] directions;

	/**
	 * The default constructor.
	 */
	public AbstractTableViewerSorter() {
		resetState();
	}

	/**
	 * Resets the sorter to the default sorting state.
	 */
	public void resetState() {
		directions = new int[getDefaultDirections().length];
		System.arraycopy(getDefaultDirections(), 0, directions, 0, directions.length);
		columnsList = new ArrayList<Integer>(Arrays.asList(getDefaultColumnSorting()));

		storeSortingOrder();
		storeDirections();
	}

	/**
	 * Sets the column which should be sorted. If it is the same column as
	 * before, the direction will be reversed.
	 * 
	 * @param column
	 *            the column
	 */
	public void setColumn(final int column) {
		if (columnsList.get(0) != column) {
			columnsList.remove(Integer.valueOf(column));
			columnsList.add(0, column);
			storeSortingOrder();
			setDirection(column, ASCENDING);
		} else {
			reverseDirection(column);
		}
	}

	/**
	 * Reverses the direction of the sorting.
	 * 
	 * @param column
	 *            the column to be sorted
	 * @param ascend
	 *            one of the constants ASCENDING or DESCENDING
	 */
	private void setDirection(final int column, final int ascend) {
		directions[column] = ascend;
		storeDirections();
	}

	/**
	 * Returns the direction of the given column.
	 * 
	 * @param column
	 *            the column number
	 * @return 1 if ascending, -1 if descending
	 */
	public int getDirection(final int column) {
		return directions[column];
	}

	/**
	 * Returns the current column.
	 * 
	 * @return the current column
	 */
	public int getColumn() {
		return columnsList.get(0);
	}

	/**
	 * Reverses the sorting direction of the column specified.
	 * 
	 * @param column
	 *            the column whose sorting direction should be reversed.
	 */
	private void reverseDirection(final int column) {
		directions[column] *= -1;
		storeDirections();
	}

	/**
	 * Compares two objects from the table.
	 */
	@Override
	public int compare(final Object e1, final Object e2) {
		for (final Integer column : columnsList) {
			final int result = compare(column, e1, e2);
			if (result != 0) {
				return result;
			}
		}
		return 0;
	}

	/**
	 * Returns the default column sorting of this sorter.
	 * 
	 * @return the default column sorting of this sorter
	 */
	protected abstract Integer[] getDefaultColumnSorting();

	/**
	 * Returns the default column directions of this sorter.
	 * 
	 * @return the default column directions of this sorter
	 */
	protected abstract int[] getDefaultDirections();

	/**
	 * Compares two objects from the table.
	 * 
	 * @param column
	 *            the column
	 * @param e1
	 *            the first object to compare
	 * @param e2
	 *            the second object to compare
	 * 
	 * @return 0 for e1 = e2, negative value for e1 < e2, positive value for e1
	 *         > e2
	 */
	protected abstract int compare(int column, Object e1, Object e2);

	/**
	 * Stores the current sorting order to the corresponding preference.
	 */
	private void storeSortingOrder() {
		final String prefValue = getIntegerString(columnsList);
		storeSortingOrderString(prefValue);
	}

	/**
	 * Stores the given value for the sorting order to the appropriate
	 * preference.
	 * 
	 * @param prefValue
	 *            the preference value to store
	 */
	protected abstract void storeSortingOrderString(String prefValue);

	/**
	 * Stores the current column directions to the corresponding preference.
	 */
	private void storeDirections() {
		final String prefValue = getIntegerString(directions);
		storeDirectionsString(prefValue);
	}

	/**
	 * Stores the given value for the column directions to the appropriate
	 * preference.
	 * 
	 * @param prefValue
	 *            the preference value to store
	 */
	protected abstract void storeDirectionsString(String prefValue);

	/* --- helper methods --- */

	/**
	 * Returns an int array for the given string.
	 * 
	 * @param string
	 *            the string to parse
	 * 
	 * @return an int array for the given string
	 */
	protected int[] getIntArray(final String string) {
		final List<Integer> resultList = getIntegerList(string);
		final int[] result = new int[resultList.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = resultList.get(i).intValue();
		}
		return result;
	}

	/**
	 * Returns an Integer array for the given string.
	 * 
	 * @param string
	 *            the string to parse
	 * 
	 * @return an Integer array for the given string
	 */
	protected Integer[] getIntegerArray(final String string) {
		final List<Integer> resultList = getIntegerList(string);
		final Integer[] result = new Integer[resultList.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = resultList.get(i);
		}
		return result;
	}

	private List<Integer> getIntegerList(final String string) {
		final String theString = string.trim();
		final List<Integer> result = new ArrayList<Integer>();

		boolean finished = false;
		int startIndex = 0;
		while (!finished) {
			final int endIndex = theString.indexOf(", ", startIndex); //$NON-NLS-1$
			final String currentInteger = endIndex > 0 ? theString.substring(startIndex, endIndex) : theString
					.substring(startIndex);
			result.add(Integer.parseInt(currentInteger));
			startIndex = endIndex + 2;
			finished = startIndex > theString.length() || startIndex == 1;
		}
		return result;
	}

	/**
	 * Returns a string for the given integer list. The elements of the list
	 * will be separated by ", ".
	 * 
	 * @param integers
	 *            the integers list
	 * 
	 * @return a string for the given integer list
	 */
	protected String getIntegerString(final List<Integer> integers) {
		final StringBuffer buf = new StringBuffer();
		for (final Iterator<Integer> iter = integers.iterator(); iter.hasNext();) {
			final Integer currentInt = iter.next();
			buf.append(currentInt.intValue());
			if (iter.hasNext()) {
				buf.append(", "); //$NON-NLS-1$
			}
		}
		return buf.toString();
	}

	/**
	 * Returns a string for the given int array. The elements of the list will
	 * be separated by ", ".
	 * 
	 * @param integers
	 *            the int array
	 * 
	 * @return a string for the given int array
	 */
	protected String getIntegerString(final int[] integers) {
		final StringBuffer buf = new StringBuffer();
		for (int i = 0; i < integers.length; i++) {
			buf.append(integers[i]);
			if (i < integers.length - 1) {
				buf.append(", "); //$NON-NLS-1$
			}
		}
		return buf.toString();
	}
}
