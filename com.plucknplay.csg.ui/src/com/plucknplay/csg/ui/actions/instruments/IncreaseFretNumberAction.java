/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions.instruments;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

import com.plucknplay.csg.core.Constants;
import com.plucknplay.csg.core.model.Instrument;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.actions.ActionMessages;

public class IncreaseFretNumberAction extends Action implements IPropertyChangeListener {

	private static final String COMMAND_ID = "com.plucknplay.csg.ui.increaseFretNumber"; //$NON-NLS-1$

	public IncreaseFretNumberAction() {
		setId(COMMAND_ID);
		setActionDefinitionId(COMMAND_ID);
		setText(ActionMessages.IncreaseFretNumberAction_text);
		setToolTipText(ActionMessages.IncreaseFretNumberAction_text);
		Activator.getDefault().getPreferenceStore().addPropertyChangeListener(this);
		setEnablement();
	}

	@Override
	public void run() {
		final int currentFretNumber = Instrument.getFretNumber();
		if (currentFretNumber < Constants.MAX_FRET_NUMBER) {
			final int newFretNumber = currentFretNumber + 1;
			Instrument.setFretNumber(newFretNumber);
			Activator.getDefault().getPreferenceStore().setValue(Preferences.FRET_NUMBER, newFretNumber);
		}
	}

	@Override
	public void propertyChange(final PropertyChangeEvent event) {
		if (Preferences.FRET_NUMBER.equals(event.getProperty()) && event.getNewValue() instanceof Integer) {
			setEnablement();
		}
	}

	private void setEnablement() {
		setEnabled(Instrument.getFretNumber() != Constants.MAX_FRET_NUMBER);
	}
}
