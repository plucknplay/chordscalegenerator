/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions;

import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.IImageKeys;
import com.plucknplay.csg.ui.UIConstants;

public class AdvancedFretBlockModeAction extends AbstractBlockModeAction {

	private static final String COMMAND_ID = "com.plucknplay.csg.ui.advancedFretBlockMode"; //$NON-NLS-1$

	public AdvancedFretBlockModeAction() {
		super(COMMAND_ID, UIConstants.BLOCK_MODE_FRETS_ADVANCED);
		setText(ActionMessages.BlockAdvancedFretModeAction_text);
		setToolTipText(ActionMessages.BlockAdvancedFretModeAction_text);
		setImageDescriptor(Activator.getImageDescriptor(IImageKeys.ADVANCED_FRET_BLOCK));
	}
}
