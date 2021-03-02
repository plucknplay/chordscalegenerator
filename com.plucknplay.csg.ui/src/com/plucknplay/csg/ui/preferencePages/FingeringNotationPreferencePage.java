/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.preferencePages;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.activation.NlsUtil;
import com.plucknplay.csg.ui.util.enums.FingeringMode;

public class FingeringNotationPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	public static final String ID = "com.plucknplay.csg.ui.fingeringNotationPreferences"; //$NON-NLS-1$
	public static final String HELP_ID = "fingering_notation_preference_page_context"; //$NON-NLS-1$

	private IPreferenceStore prefs;

	private List<Button> buttons;
	private List<Text> texts;
	private Button customButton;

	@Override
	public void init(final IWorkbench workbench) {
		prefs = Activator.getDefault().getPreferenceStore();
	}

	@Override
	protected Control createContents(final Composite parent) {

		// (1) main composite
		final Composite mainComposite = new Composite(parent, SWT.NONE);
		GridLayoutFactory.fillDefaults().equalWidth(false).numColumns(5).spacing(15, 5).applyTo(mainComposite);

		// (2) notation buttons
		final FingeringMode theMode = getFingeringMode(prefs.getString(Preferences.GENERAL_FINGERING_MODE));
		buttons = new ArrayList<Button>();
		for (final FingeringMode mode : FingeringMode.values()) {
			if (mode.isCommon() || NlsUtil.isGerman()) {
				final Button button = new Button(mainComposite, SWT.RADIO);
				button.setText(mode.getBeautifiedName(false));
				button.setSelection(mode == theMode);
				button.setData(mode);
				buttons.add(button);
				if (mode == FingeringMode.CUSTOM) {
					GridDataFactory.fillDefaults().span(2, 1).applyTo(button);
					customButton = button;
				}
			}
		}

		// (3) finger text widgets
		final Group notationGroup = new Group(mainComposite, SWT.NONE);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).span(5, 1).grab(true, false).applyTo(notationGroup);
		GridLayoutFactory.fillDefaults().equalWidth(false).numColumns(2).margins(15, 10).spacing(5, 5)
				.applyTo(notationGroup);

		texts = new ArrayList<Text>();
		createFingerWidgets(notationGroup, PreferenceMessages.FingeringNotationPreferencePage_thumb);
		createFingerWidgets(notationGroup, PreferenceMessages.FingeringNotationPreferencePage_index_finger);
		createFingerWidgets(notationGroup, PreferenceMessages.FingeringNotationPreferencePage_middle_finger);
		createFingerWidgets(notationGroup, PreferenceMessages.FingeringNotationPreferencePage_ring_finger);
		createFingerWidgets(notationGroup, PreferenceMessages.FingeringNotationPreferencePage_little_finger);

		// (4) define listener
		for (final Button button : buttons) {
			button.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(final SelectionEvent e) {
					updateEnablement();
					updateTextWidgets((FingeringMode) button.getData());
				}
			});
		}

		for (final Text text : texts) {
			text.addFocusListener(new FocusListener() {

				private String memo;

				@Override
				public void focusLost(final FocusEvent e) {
					if ("".equals(text.getText())) {
						text.setText(memo);
					}
				}

				@Override
				public void focusGained(final FocusEvent e) {
					text.selectAll();
					memo = text.getText();
				}
			});
		}

		// (5) last initializations
		updateEnablement();
		updateTextWidgets(theMode);

		// (6) set context-sensitive help
		Activator.getDefault().setHelp(getControl(), HELP_ID);

		return mainComposite;
	}

	private void createFingerWidgets(final Composite parent, final String labelText) {

		// (1) create label
		final Label label = new Label(parent, SWT.NONE);
		label.setText(labelText + ":"); //$NON-NLS-1$
		label.setFont(buttons.get(0).getFont());
		GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.TOP).applyTo(label);

		// (2) create text widget
		final Text text = new Text(parent, SWT.BORDER);
		text.setTextLimit(1);
		texts.add(text);
		GridDataFactory.fillDefaults().hint(50, SWT.DEFAULT).applyTo(text);

	}

	private void updateEnablement() {
		final boolean enabled = customButton.getSelection();
		for (final Text text : texts) {
			text.setEnabled(enabled);
		}
	}

	private void updateTextWidgets(final FingeringMode mode) {
		for (int i = 0; i < 5; i++) {
			texts.get(i).setText("" + mode.getValue(i));
		}
	}

	@Override
	public boolean performOk() {
		for (final Button button : buttons) {
			if (button.getSelection()) {
				prefs.setValue(Preferences.GENERAL_FINGERING_MODE, button.getData().toString());
				if (FingeringMode.CUSTOM == button.getData()) {
					final StringBuffer buf = new StringBuffer();
					for (final Text text : texts) {
						buf.append(text.getText());
					}
					prefs.setValue(Preferences.GENERAL_FINGERING_MODE_CUSTOM_NOTATION, buf.toString());
				}
			}
		}
		return super.performOk();
	}

	@Override
	protected void performDefaults() {
		final FingeringMode mode = getFingeringMode(prefs.getDefaultString(Preferences.GENERAL_FINGERING_MODE));
		prefs.setValue(Preferences.GENERAL_FINGERING_MODE_CUSTOM_NOTATION, prefs.getDefaultString(Preferences.GENERAL_FINGERING_MODE_CUSTOM_NOTATION));
		for (final Button button : buttons) {
			button.setSelection(button.getData() == mode);
		}
		updateEnablement();
		updateTextWidgets(mode);
		super.performDefaults();
	}

	private FingeringMode getFingeringMode(final String fingeringNotation) {
		FingeringMode mode = FingeringMode.NUMBERS_T;
		if (fingeringNotation != null) {
			mode = FingeringMode.valueOf(fingeringNotation);
		}
		return mode;
	}
}
