/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.plucknplay.csg.core.Constants;
import com.plucknplay.csg.core.model.Factory;
import com.plucknplay.csg.core.model.Note;

public final class WidgetFactory {

	private WidgetFactory() {
	}

	public static ComboViewer createNotesComboViewer(final Composite parent, final int style,
			final IBaseLabelProvider labelProvider, final List<Note> input) {
		final ComboViewer viewer = new ComboViewer(parent, style);
		viewer.setLabelProvider(labelProvider);
		viewer.setContentProvider(new DefaultCollectionContentProvider());
		viewer.setSorter(new NoteViewerSorter());
		viewer.setInput(input);
		viewer.getCombo().setVisibleItemCount(Math.max(Constants.MAX_NOTES_VALUE, Constants.MAX_NOTES_LEVEL) + 1);
		return viewer;
	}

	/**
	 * Returns a {@link ComboViewer} containing all RELATIVE note names. The
	 * note names depend on the generally set notes mode preference.
	 * 
	 * @param parent
	 *            the parent
	 * 
	 * @return a combo viewer containing all relative note names
	 */
	public static ComboViewer createRelativeNotesComboViewer(final Composite parent) {
		return createRelativeNotesComboViewer(parent, null);
	}

	/**
	 * Returns a {@link ComboViewer} containing all RELATIVE note names.
	 * 
	 * @param parent
	 *            the parent
	 * @param notesMode
	 *            the notes mode (Constants.NOTES_MODE_*) or <code>null</code>
	 *            if the general preferences shall be used
	 * 
	 * @return a combo viewer containing all relative note names
	 */
	public static ComboViewer createRelativeNotesComboViewer(final Composite parent, final String notesMode) {
		final List<Note> input = new ArrayList<Note>();
		for (int i = 0; i <= Constants.MAX_NOTES_VALUE; i++) {
			input.add(Factory.getInstance().getNote(i));
		}
		return createNotesComboViewer(parent, SWT.DROP_DOWN | SWT.READ_ONLY, new NotesLabelProvider(false, notesMode),
				input);
	}

	/**
	 * Returns a {@link ComboViewer} containing all ABSOLUTE note names. The
	 * note names depend on the generally set notes mode preference.
	 * 
	 * @param parent
	 *            the parent
	 * 
	 * @return a combo viewer containing all absolute note names
	 */
	public static ComboViewer createAbsoluteNotesComboViewer(final Composite parent) {
		return createAbsoluteNotesComboViewer(parent, null);
	}

	/**
	 * Returns a {@link ComboViewer} containing all ABSOLUTE note names.
	 * 
	 * @param parent
	 *            the parent
	 * @param notesMode
	 *            the notes mode (Constants.NOTES_MODE_*) or <code>null</code>
	 *            if the general preferences shall be used
	 * 
	 * @return a combo viewer containing all absolute note names
	 */
	public static ComboViewer createAbsoluteNotesComboViewer(final Composite parent, final String notesMode) {
		return createNotesComboViewer(parent, SWT.DROP_DOWN | SWT.READ_ONLY, new NotesLabelProvider(true, notesMode),
				new ArrayList<Note>());
	}
}
