/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.editPartFactories;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

import com.plucknplay.csg.ui.editParts.NotesDraftEditPart;
import com.plucknplay.csg.ui.model.NotesDraft;
import com.plucknplay.csg.ui.model.NotesDraftUtil;
import com.plucknplay.csg.ui.views.NotesView;

public class NotesEditPartFactory implements EditPartFactory {

	private final NotesView notesView;

	public NotesEditPartFactory(final NotesView notesView) {
		this.notesView = notesView;
	}

	@Override
	public EditPart createEditPart(final EditPart context, final Object model) {
		if (model == null || !(model instanceof NotesDraft)) {
			return null;
		}

		final EditPart part = new NotesDraftEditPart(notesView, NotesDraftUtil.getDisplayMode((NotesDraft) model));
		if (part != null) {
			part.setModel(model);
		}
		return part;
	}
}
