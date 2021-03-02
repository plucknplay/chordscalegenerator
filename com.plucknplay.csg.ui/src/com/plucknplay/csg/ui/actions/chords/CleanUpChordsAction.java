/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions.chords;

import java.util.List;

import org.eclipse.ui.IWorkbenchWindow;

import com.plucknplay.csg.core.model.Categorizable;
import com.plucknplay.csg.core.model.IntervalContainer;
import com.plucknplay.csg.core.model.sets.ChordList;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.IImageKeys;
import com.plucknplay.csg.ui.actions.ActionMessages;
import com.plucknplay.csg.ui.actions.general.AbstractCleanUpAction;

public class CleanUpChordsAction extends AbstractCleanUpAction {

	private static final String COMMAND_ID = "com.plucknplay.csg.ui.cleanUpChords"; //$NON-NLS-1$

	public CleanUpChordsAction(final IWorkbenchWindow window) {
		super(window);
		setId(COMMAND_ID);
		setActionDefinitionId(COMMAND_ID);
		setText(ActionMessages.CleanUpChordsAction_text);
		setToolTipText(ActionMessages.CleanUpChordsAction_tooltip);
		setImageDescriptor(Activator.getImageDescriptor(IImageKeys.CLEAN_UP_CHORDS));
	}

	@Override
	protected List<Categorizable> getAllElements() {
		return ChordList.getInstance().getRootCategory().getAllElements();
	}

	@Override
	protected Object getType() {
		return IntervalContainer.TYPE_CHORD;
	}
}
