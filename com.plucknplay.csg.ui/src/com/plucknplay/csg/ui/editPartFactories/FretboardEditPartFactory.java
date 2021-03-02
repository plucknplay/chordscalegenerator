/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.editPartFactories;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.editParts.FretboardDraftEditPart;
import com.plucknplay.csg.ui.model.FretboardDraft;

public class FretboardEditPartFactory implements EditPartFactory {

	@Override
	public EditPart createEditPart(final EditPart context, final Object model) {
		final String mode = Activator.getDefault().getPreferenceStore().getString(Preferences.FRETBOARD_VIEW_MODE);
		final EditPart part = model instanceof FretboardDraft ? new FretboardDraftEditPart(mode) : null;
		if (part != null) {
			part.setModel(model);
		}
		return part;
	}
}
