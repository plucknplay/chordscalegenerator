/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.editPartFactories;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

import com.plucknplay.csg.ui.editParts.TabDraftEditPart;
import com.plucknplay.csg.ui.model.TabDraft;

public class TabEditPartFactory implements EditPartFactory {

	@Override
	public EditPart createEditPart(final EditPart context, final Object model) {
		final EditPart part = model instanceof TabDraft ? new TabDraftEditPart() : null;
		if (part != null) {
			part.setModel(model);
		}
		return part;
	}
}
