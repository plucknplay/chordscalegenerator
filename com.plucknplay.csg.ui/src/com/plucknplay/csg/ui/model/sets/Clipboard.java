/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.model.sets;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;

import com.plucknplay.csg.core.model.Categorizable;
import com.plucknplay.csg.core.model.Chord;
import com.plucknplay.csg.core.model.Instrument;
import com.plucknplay.csg.core.model.Scale;
import com.plucknplay.csg.core.model.sets.Category;
import com.plucknplay.csg.ui.util.Util;

public final class Clipboard {

	public static final Object TYPE_INSTRUMENT = new Object();
	public static final Object TYPE_CHORD = new Object();
	public static final Object TYPE_SCALE = new Object();

	private static Clipboard instance;

	private List<?> input;
	private Object type;
	private Instrument currentInstrument;

	/**
	 * Private default constructor.
	 */
	private Clipboard() {
	}

	/**
	 * Returns the singleton instance of the clipboard.
	 * 
	 * @return the singleton instance of the clipboard
	 */
	public static Clipboard getInstance() {
		if (instance == null) {
			instance = new Clipboard();
		}
		return instance;
	}

	/**
	 * Sets the input for the clipboard. This is the selection which shall be
	 * stored. The selection may contain categories and elements.
	 * 
	 * @param selection
	 *            the input
	 * @param type
	 *            the type of selection, use TYPE_INSTRUMENT, TYPE_CHORD or
	 *            TYPE_SCALE
	 */
	public void setInput(final IStructuredSelection selection, final Object type) {
		if (selection == null || selection.isEmpty()) {
			return;
		}
		this.type = type;
		if (type != TYPE_INSTRUMENT) {
			currentInstrument = null;
		}

		input = new ArrayList<Object>();

		// (1) validate selection
		input = Util.validateSelection(selection);

		// (2) clone input so that a cut could be performed afterwards (without
		// removing stored elements)
		cloneInput();
	}

	private void cloneInput() {
		final List<Object> clonedInput = new ArrayList<Object>();
		for (final Object element : input) {
			if (element instanceof Category) {
				addCategory(clonedInput, (Category) element, null);
			} else if (element instanceof Categorizable) {
				addElement(clonedInput, (Categorizable) element, null);
			}
		}
		input = new ArrayList<Object>(clonedInput);
	}

	private void addCategory(final List<Object> clonedInput, final Category categoryToAdd, final Category category) {
		final Category newCategory = new Category(categoryToAdd.getName());
		if (category == null) {
			clonedInput.add(newCategory);
		} else {
			category.addCategory(newCategory);
		}
		for (final Categorizable categorizable : categoryToAdd.getElements()) {
			addElement(clonedInput, categorizable, newCategory);
		}
		for (final Category category2 : categoryToAdd.getCategories()) {
			addCategory(clonedInput, category2, newCategory);
		}
	}

	private void addElement(final List<Object> clonedInput, final Categorizable element, final Category category) {
		final Categorizable theElement = createNewElement(element);

		// update the current instrument if necessary
		if (currentInstrument != null && currentInstrument == element) {
			currentInstrument = (Instrument) theElement;
		}

		if (category == null) {
			clonedInput.add(theElement);
		} else {
			category.addElement(theElement);
		}
	}

	private Categorizable createNewElement(final Categorizable element) {
		if (element instanceof Instrument) {
			return new Instrument((Instrument) element);
		}
		if (element instanceof Chord) {
			return new Chord((Chord) element);
		}
		if (element instanceof Scale) {
			return new Scale((Scale) element);
		}
		throw new IllegalArgumentException();
	}

	/**
	 * Returns the filtered given input, or null if there is no input set.
	 * 
	 * @return the filtered given input, or null if there is no input set
	 */
	public List<?> getInput() {
		cloneInput();
		return input;
	}

	/**
	 * Clears the clipboard.
	 */
	public void clear() {
		input = null;
	}

	/**
	 * Returns true if the input is empty, false otherwise.
	 * 
	 * @return true if the input is empty, false otherwise
	 */
	public boolean isEmpty() {
		return input == null || input.isEmpty();
	}

	/**
	 * Returns the type of the input which is currently inside the clipboard.
	 * 
	 * @return the type of the input which is currently inside the clipboard
	 */
	public Object getInputType() {
		return type;
	}

	/**
	 * Saves the current instrument so that in the case that it will be deleted
	 * with the cut operation it could be set with the paste again.
	 * 
	 * <p>
	 * Note if you want to discard the stored current instrument pass null.
	 * </p>
	 * 
	 * @param currentInstrument
	 *            the current instrument, or null if you want to discard the
	 *            stored current instrument
	 */
	public void saveCurrentInstrument(final Instrument currentInstrument) {
		this.currentInstrument = currentInstrument;
	}

	/**
	 * Returns the stored current instrument, or null if there is none.
	 * 
	 * @return the stored current instrument, or null
	 */
	public Instrument getStoredCurrentInstrument() {
		return currentInstrument;
	}
}
