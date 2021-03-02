/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.util;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.LabelProvider;

import com.plucknplay.csg.core.Constants;
import com.plucknplay.csg.core.model.Note;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.Preferences;

public class NotesLabelProvider extends LabelProvider {

	private final IPreferenceStore prefs;

	private final boolean showAbsolute;
	private final String notesMode;

	public NotesLabelProvider(final boolean showAbsolute) {
		this(showAbsolute, null);
	}

	public NotesLabelProvider(final boolean showAbsolute, final String notesMode) {
		prefs = Activator.getDefault().getPreferenceStore();
		this.showAbsolute = showAbsolute;
		this.notesMode = notesMode;
	}

	@Override
	public String getText(final Object element) {
		final String currentNotesMode = prefs.getString(Preferences.NOTES_MODE);

		if (element instanceof Note) {
			final Note note = (Note) element;
			if (notesMode == null && currentNotesMode.equals(Constants.NOTES_MODE_CROSS_AND_B) || notesMode != null
					&& notesMode.equals(Constants.NOTES_MODE_CROSS_AND_B)) {
				return showAbsolute ? note.getAbsoluteName() : note.getRelativeName();
			}
			if (notesMode == null && currentNotesMode.equals(Constants.NOTES_MODE_ONLY_B) || notesMode != null
					&& notesMode.equals(Constants.NOTES_MODE_ONLY_B)) {
				return showAbsolute ? note.getAbsoluteNameDim() : note.getRelativeNameDim();
			}
			if (notesMode == null && currentNotesMode.equals(Constants.NOTES_MODE_ONLY_CROSS) || notesMode != null
					&& notesMode.equals(Constants.NOTES_MODE_ONLY_CROSS)) {
				return showAbsolute ? note.getAbsoluteNameAug() : note.getRelativeNameAug();
			}
		}
		return super.getText(element);
	}
}
