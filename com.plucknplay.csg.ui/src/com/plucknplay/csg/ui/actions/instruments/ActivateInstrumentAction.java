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
import com.plucknplay.csg.ui.IImageKeys;
import com.plucknplay.csg.ui.actions.ActionMessages;
import com.plucknplay.csg.ui.dialogs.InstrumentSelectionDialog;

public class ActivateInstrumentAction extends Action {

	private static final String COMMAND_ID = "com.plucknplay.csg.ui.activateInstrument"; //$NON-NLS-1$

	private final IWorkbenchWindow window;

	public ActivateInstrumentAction(final IWorkbenchWindow window) {
		this.window = window;
		setId(COMMAND_ID);
		setActionDefinitionId(COMMAND_ID);
		setText(ActionMessages.ActivateInstrumentAction_text);
		setToolTipText(ActionMessages.ActivateInstrumentdAction_tooltip);
		setImageDescriptor(Activator.getImageDescriptor(IImageKeys.CURRENT_INSTRUMENT));
	}

	@Override
	public void run() {
		final InstrumentSelectionDialog dialog = new InstrumentSelectionDialog(window.getShell(), true);

		if (dialog.open() == Dialog.OK) {
			final Instrument instrument = dialog.getSelectedInstrument();
			SetCurrentInstrumentAction.setCurrentInstrument(instrument, window);
		}
	}
}
