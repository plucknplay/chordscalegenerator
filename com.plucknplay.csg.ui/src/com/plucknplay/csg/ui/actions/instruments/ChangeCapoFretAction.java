/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions.instruments;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.ui.IWorkbenchWindow;

import com.plucknplay.csg.core.model.Instrument;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.actions.ActionMessages;
import com.plucknplay.csg.ui.dialogs.CapoFretDialog;

public class ChangeCapoFretAction extends Action {

	private static final String COMMAND_ID = "com.plucknplay.csg.ui.changeCapoFret"; //$NON-NLS-1$

	private final IWorkbenchWindow window;

	public ChangeCapoFretAction(final IWorkbenchWindow window) {
		this.window = window;
		setId(COMMAND_ID);
		setActionDefinitionId(COMMAND_ID);
		setText(ActionMessages.ChangeCapoFretAction_text);
		setToolTipText(ActionMessages.ChangeCapoFretAction_text);
	}

	@Override
	public void run() {
		final CapoFretDialog dialog = new CapoFretDialog(window.getShell());
		if (dialog.open() == Dialog.OK) {
			final int newCapoFret = dialog.getSelectedFret();
			if (newCapoFret != Instrument.getCapoFret()) {
				Instrument.setCapoFret(newCapoFret);
				Activator.getDefault().getPreferenceStore().setValue(Preferences.CAPO_FRET, newCapoFret);
			}
		}
	}
}
