/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions;

import org.eclipse.jface.action.Action;

import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.IImageKeys;
import com.plucknplay.csg.ui.listeners.ISimpleChangeListener;

public class LeftRightHanderAction extends Action implements ISimpleChangeListener {

	private static final String COMMAND_ID = "com.plucknplay.csg.ui.toggleHand"; //$NON-NLS-1$

	public LeftRightHanderAction() {
		setId(COMMAND_ID);
		setActionDefinitionId(COMMAND_ID);
		setText(ActionMessages.LeftRightHanderAction_text);
		setToolTipText(ActionMessages.LeftRightHanderAction_tooltip);
		updateImage();
		Activator.getDefault().addSimpleChangeListener(this);
	}

	@Override
	public void run() {
		perform();
	}

	static void perform() {
		final Activator activator = Activator.getDefault();
		activator.setLeftHander(!activator.isLeftHander());
	}

	private void updateImage() {
		setImageDescriptor(Activator.getDefault().isLeftHander() ? Activator.getImageDescriptor(IImageKeys.RIGHT_HAND)
				: Activator.getImageDescriptor(IImageKeys.LEFT_HAND));
	}

	@Override
	public void notifyChange(final Object property, final Object value) {
		if (property == Activator.PROP_HAND_CHANGED) {
			updateImage();
		}
	}
}
