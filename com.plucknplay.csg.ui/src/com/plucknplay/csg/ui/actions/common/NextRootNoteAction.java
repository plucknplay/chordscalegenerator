/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions.common;

import org.eclipse.jface.action.Action;

import com.plucknplay.csg.core.Constants;
import com.plucknplay.csg.core.model.Factory;
import com.plucknplay.csg.core.model.Note;
import com.plucknplay.csg.core.model.sets.ChordList;
import com.plucknplay.csg.core.model.sets.ScaleList;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.actions.ActionMessages;

public class NextRootNoteAction extends Action {

	private static final String COMMAND_ID = "com.plucknplay.csg.ui.nextRootNote"; //$NON-NLS-1$

	public NextRootNoteAction() {
		setId(COMMAND_ID);
		setActionDefinitionId(COMMAND_ID);
		setText(ActionMessages.NextRootNoteAction_text);
		setToolTipText(ActionMessages.NextRootNoteAction_text);
	}

	@Override
	public void run() {
		final Note currentRootNote = ScaleList.getInstance().getCurrentRootNote();
		int newRootNoteValue = currentRootNote.getValue() + 1;
		if (newRootNoteValue > Constants.MAX_NOTES_VALUE) {
			newRootNoteValue = 0;
		}
		final Note note = Factory.getInstance().getNote(newRootNoteValue);
		ScaleList.getInstance().setCurrentRootNote(note);
		ChordList.getInstance().setCurrentRootNote(note);
		Activator.getDefault().getPreferenceStore().setValue(Preferences.ROOT_NOTE, newRootNoteValue);
	}
}
