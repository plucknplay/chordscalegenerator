/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.editPartFactories;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.jface.preference.IPreferenceStore;

import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.editParts.BoxDraftEditPart;
import com.plucknplay.csg.ui.model.BoxDraft;

public class BoxEditPartFactory implements EditPartFactory {

	@Override
	public EditPart createEditPart(final EditPart context, final Object model) {

		final IPreferenceStore prefs = Activator.getDefault().getPreferenceStore();
		final boolean showFingering = prefs.getBoolean(Preferences.BOX_VIEW_SHOW_FINGERING);
		final boolean showFingeringOutside = prefs.getBoolean(Preferences.BOX_VIEW_SHOW_FINGERING_OUTSIDE_BOX);
		final boolean showNotes = prefs.getBoolean(Preferences.BOX_VIEW_SHOW_NOTES);
		final boolean showNotesOutside = prefs.getBoolean(Preferences.BOX_VIEW_SHOW_NOTES_OUTSIDE_BOX);
		final boolean showIntervals = prefs.getBoolean(Preferences.BOX_VIEW_SHOW_INTERVALS);
		final boolean showIntervalsOutside = prefs.getBoolean(Preferences.BOX_VIEW_SHOW_INTERVALS_OUTSIDE_BOX);

		final EditPart part = model instanceof BoxDraft ? new BoxDraftEditPart(showFingering, showFingeringOutside,
				showNotes, showNotesOutside, showIntervals, showIntervalsOutside) : null;
		if (part != null) {
			part.setModel(model);
		}
		return part;
	}
}
