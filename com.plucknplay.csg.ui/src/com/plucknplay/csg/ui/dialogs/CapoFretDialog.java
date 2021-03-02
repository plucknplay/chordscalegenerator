/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;

import com.plucknplay.csg.core.Constants;
import com.plucknplay.csg.core.model.Instrument;
import com.plucknplay.csg.ui.util.OnlyNumbersKeyListener;

public class CapoFretDialog extends Dialog {

	private Spinner spinner;
	private int fret;

	/**
	 * The constructor.
	 * 
	 * @param shell
	 *            the shell
	 */
	public CapoFretDialog(final Shell shell) {
		super(shell);
	}

	@Override
	protected Control createDialogArea(final Composite parent) {

		// composite
		final Composite composite = (Composite) super.createDialogArea(parent);
		GridLayoutFactory.fillDefaults().numColumns(3).equalWidth(false).extendedMargins(10, 15, 10, 10).spacing(5, 5)
				.applyTo(composite);

		// Label
		new Label(composite, SWT.LEFT).setText(DialogMessages.CapoFretDialog_fret + ":"); //$NON-NLS-1$

		// Spinner
		final int maxValue = Instrument.getFretNumber() - Constants.MIN_ACTIVE_FRET_NUMBER;
		spinner = new Spinner(composite, SWT.BORDER);
		spinner.setValues(Instrument.getCapoFret(), 0, maxValue, 0, 1, 5);
		spinner.setTextLimit(maxValue > 9 ? 2 : 1);
		spinner.addKeyListener(new OnlyNumbersKeyListener());
		GridDataFactory.fillDefaults().grab(true, false).applyTo(spinner);

		// Range-Label
		new Label(composite, SWT.LEFT).setText("(0.." + maxValue + ")"); //$NON-NLS-1$ //$NON-NLS-2$

		return composite;
	}

	@Override
	protected void okPressed() {
		fret = spinner.getSelection();
		super.okPressed();
	}

	public int getSelectedFret() {
		return fret;
	}

	@Override
	protected void configureShell(final Shell shell) {
		super.configureShell(shell);
		shell.setText(DialogMessages.CapoFretDialog_change_capo_fret);
	}
}
