/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions;

import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.IImageKeys;
import com.plucknplay.csg.ui.UIConstants;

public class OctaveBlockModeAction extends AbstractBlockModeAction {

	private static final String COMMAND_ID = "com.plucknplay.csg.ui.octaveBlockMode"; //$NON-NLS-1$

	public OctaveBlockModeAction() {
		super(COMMAND_ID, UIConstants.BLOCK_MODE_OCTAVE);
		setText(ActionMessages.BlockOctaveModeAction_text);
		setToolTipText(ActionMessages.BlockOctaveModeAction_text);
		setImageDescriptor(Activator.getImageDescriptor(IImageKeys.OCTAVE_BLOCK));
	}
}
