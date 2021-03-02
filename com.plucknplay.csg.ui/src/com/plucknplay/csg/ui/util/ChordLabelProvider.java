/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.util;

import org.eclipse.jface.viewers.LabelProvider;

import com.plucknplay.csg.core.Constants;
import com.plucknplay.csg.core.model.Chord;

public class ChordLabelProvider extends LabelProvider {

	private final boolean showRootNote;
	private final NotesLabelProvider notesLabelProvider;

	public ChordLabelProvider() {
		this(false);
	}

	public ChordLabelProvider(final boolean showRootNote) {
		this.showRootNote = showRootNote;
		notesLabelProvider = new NotesLabelProvider(false);
	}

	@Override
	public String getText(final Object element) {
		if (element instanceof Chord) {
			final Chord chord = (Chord) element;
			final StringBuffer buf = new StringBuffer();
			if (showRootNote && chord.getRootNote() != null) {
				buf.append(notesLabelProvider.getText(chord.getRootNote()));
				buf.append(" "); //$NON-NLS-1$
			}
			buf.append(chord.getName().equals(Constants.BLANK_CHORD_NAME) ? "" : chord.getName()); //$NON-NLS-1$
			return buf.toString();
		}
		throw new IllegalArgumentException();
	}
}
