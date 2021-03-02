/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions.scales;

import java.util.List;

import org.eclipse.ui.IWorkbenchWindow;

import com.plucknplay.csg.core.model.Categorizable;
import com.plucknplay.csg.core.model.IntervalContainer;
import com.plucknplay.csg.core.model.sets.ScaleList;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.IImageKeys;
import com.plucknplay.csg.ui.actions.ActionMessages;
import com.plucknplay.csg.ui.actions.general.AbstractCleanUpAction;

public class CleanUpScalesAction extends AbstractCleanUpAction {

	private static final String COMMAND_ID = "com.plucknplay.csg.ui.cleanUpScales"; //$NON-NLS-1$

	public CleanUpScalesAction(final IWorkbenchWindow window) {
		super(window);
		setId(COMMAND_ID);
		setActionDefinitionId(COMMAND_ID);
		setText(ActionMessages.CleanUpScalesAction_text);
		setToolTipText(ActionMessages.CleanUpScalesAction_tooltip);
		setImageDescriptor(Activator.getImageDescriptor(IImageKeys.CLEAN_UP_SCALES));
	}

	@Override
	protected List<Categorizable> getAllElements() {
		return ScaleList.getInstance().getRootCategory().getAllElements();
	}

	@Override
	protected Object getType() {
		return IntervalContainer.TYPE_SCALE;
	}
}
