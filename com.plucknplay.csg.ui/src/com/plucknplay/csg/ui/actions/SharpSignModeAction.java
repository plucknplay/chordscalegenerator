/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions;

import com.plucknplay.csg.core.Constants;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.IImageKeys;

public class SharpSignModeAction extends AbstractSignModeAction {

	private static final String COMMAND_ID = "com.plucknplay.csg.ui.sharpSign"; //$NON-NLS-1$

	public SharpSignModeAction() {
		super(COMMAND_ID, Constants.NOTES_MODE_ONLY_CROSS);
		setText(ActionMessages.SharpSignAction_text);
		setToolTipText(ActionMessages.SharpSignAction_text);
		setImageDescriptor(Activator.getImageDescriptor(IImageKeys.SHARP_SIGN));
	}
}
