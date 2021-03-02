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

public class IncreaseCapoFretAction extends Action implements IPropertyChangeListener {

	private static final String COMMAND_ID = "com.plucknplay.csg.ui.increaseCapoFret"; //$NON-NLS-1$

	public IncreaseCapoFretAction() {
		setId(COMMAND_ID);
		setActionDefinitionId(COMMAND_ID);
		setText(ActionMessages.IncreaseCapoFretAction_text);
		setToolTipText(ActionMessages.IncreaseCapoFretAction_text);
		Activator.getDefault().getPreferenceStore().addPropertyChangeListener(this);
		setEnablement();
	}

	@Override
	public void run() {
		final int currentCapoFret = Instrument.getCapoFret();
		if (currentCapoFret + Constants.MIN_ACTIVE_FRET_NUMBER < Instrument.getFretNumber()) {
			final int newCapoFret = currentCapoFret + 1;
			Instrument.setCapoFret(newCapoFret);
			Activator.getDefault().getPreferenceStore().setValue(Preferences.CAPO_FRET, newCapoFret);
		}
	}

	@Override
	public void propertyChange(final PropertyChangeEvent event) {
		if (Preferences.FRET_NUMBER.equals(event.getProperty()) || Preferences.CAPO_FRET.equals(event.getProperty())) {
			setEnablement();
		}
	}

	private void setEnablement() {
		setEnabled(Instrument.getCapoFret() + Constants.MIN_ACTIVE_FRET_NUMBER < Instrument.getFretNumber());
	}
}
