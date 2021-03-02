/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions.instruments;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;

import com.plucknplay.csg.core.Constants;
import com.plucknplay.csg.core.model.Instrument;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.actions.ActionMessages;

public class FretNumberContributionItem extends ContributionItem implements IPropertyChangeListener {

	private Spinner fretNumberSpinner;

	public FretNumberContributionItem() {
		Activator.getDefault().getPreferenceStore().addPropertyChangeListener(this);
	}

	@Override
	public void fill(final Composite parent) {

		final Composite composite = new Composite(parent, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(2).extendedMargins(20, 10, 0, 0).applyTo(composite);

		new Label(composite, SWT.LEFT).setText(ActionMessages.FretNumberContributionItem_fret_number + ":"); //$NON-NLS-1$
		fretNumberSpinner = new Spinner(composite, SWT.BORDER | SWT.READ_ONLY);
		fretNumberSpinner.setValues(Instrument.getFretNumber(), Constants.MIN_FRET_NUMBER, Constants.MAX_FRET_NUMBER,
				0, 1, 5);
		fretNumberSpinner.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(final ModifyEvent e) {
				final int newFretNumber = fretNumberSpinner.getSelection();
				Instrument.setFretNumber(newFretNumber);
				Activator.getDefault().getPreferenceStore().setValue(Preferences.FRET_NUMBER, newFretNumber);
				final int currentCapoFret = Instrument.getCapoFret();
				if (currentCapoFret + Constants.MIN_ACTIVE_FRET_NUMBER > newFretNumber) {
					final int newCapoFret = newFretNumber - Constants.MIN_ACTIVE_FRET_NUMBER;
					Instrument.setCapoFret(newCapoFret);
					Activator.getDefault().getPreferenceStore().setValue(Preferences.CAPO_FRET, newCapoFret);
				}
			}
		});
	}

	@Override
	public void propertyChange(final PropertyChangeEvent event) {
		if (Preferences.FRET_NUMBER.equals(event.getProperty())) {
			fretNumberSpinner.setSelection(Instrument.getFretNumber());
		}
	}
}
