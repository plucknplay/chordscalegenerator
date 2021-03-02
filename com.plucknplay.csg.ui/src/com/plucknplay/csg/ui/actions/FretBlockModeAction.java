/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions;

import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.IImageKeys;
import com.plucknplay.csg.ui.UIConstants;

public class FretBlockModeAction extends AbstractBlockModeAction {

	private static final String COMMAND_ID = "com.plucknplay.csg.ui.fretBlockMode"; //$NON-NLS-1$

	public FretBlockModeAction() {
		super(COMMAND_ID, UIConstants.BLOCK_MODE_FRETS);
		setText(ActionMessages.BlockFretModeAction_text);
		setToolTipText(ActionMessages.BlockFretModeAction_text);
		setImageDescriptor(Activator.getImageDescriptor(IImageKeys.FRET_BLOCK));
	}
}
