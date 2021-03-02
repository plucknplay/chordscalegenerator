/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions;

import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.IImageKeys;
import com.plucknplay.csg.ui.views.IModeView;

public class ShowNotesAction extends AbstractShowAction {

	private static final String COMMAND_ID = "com.plucknplay.csg.ui.showNotes"; //$NON-NLS-1$

	public ShowNotesAction(final IModeView view, final String relatedMode) {
		super(view, relatedMode);

		setActionDefinitionId(COMMAND_ID);
		setText(ActionMessages.ShowNotesAction_text);
		setToolTipText(ActionMessages.ShowNotesAction_text);
		setImageDescriptor(Activator.getImageDescriptor(IImageKeys.SHOW_NOTES));
	}
}
