/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions;

import com.plucknplay.csg.core.Constants;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.IImageKeys;

public class SharpAndFlatSignModeAction extends AbstractSignModeAction {

	private static final String COMMAND_ID = "com.plucknplay.csg.ui.sharpAndFlatSign"; //$NON-NLS-1$

	public SharpAndFlatSignModeAction() {
		super(COMMAND_ID, Constants.NOTES_MODE_CROSS_AND_B);
		setText(ActionMessages.SharpAndFlatSignAction_text);
		setToolTipText(ActionMessages.SharpAndFlatSignAction_text);
		setImageDescriptor(Activator.getImageDescriptor(IImageKeys.SHARP_AND_FLAT_SIGN));
	}
}
