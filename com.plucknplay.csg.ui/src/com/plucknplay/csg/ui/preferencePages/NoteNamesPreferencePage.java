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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.plucknplay.csg.core.Constants;
import com.plucknplay.csg.core.model.enums.NoteNamesMode;
import com.plucknplay.csg.core.util.NamesUtil;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.Preferences;

public class NoteNamesPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	public static final String ID = "com.plucknplay.csg.ui.noteNamesPreferences"; //$NON-NLS-1$
	public static final String HELP_ID = "note_names_preference_page_context"; //$NON-NLS-1$

	private IPreferenceStore prefs;

	private Button hHButton;
	private Button hBButton;
	private Button bHbButton;
	private Button bBButton;
	private Button bBbButton;

	private String hName;
	private String bName;

	private List<Button> noteNameButtons;

	@Override
	public void init(final IWorkbench workbench) {
		prefs = Activator.getDefault().getPreferenceStore();
	}

	@Override
	protected Control createContents(final Composite parent) {

		// main composite
		final Composite mainComposite = new Composite(parent, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(1).equalWidth(true).applyTo(mainComposite);

		// h naming group
		final Group hGroup = new Group(mainComposite, SWT.NONE);
		hGroup.setText(PreferenceMessages.NoteNamesPreferencePage_naming_problems_title);
		GridLayoutFactory.fillDefaults().numColumns(4).margins(5, 5).applyTo(hGroup);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(hGroup);

		final Label hLabel = new Label(hGroup, SWT.NONE);
		hLabel.setText(PreferenceMessages.NoteNamesPreferencePage_name_of_h_or_b);

		hHButton = new Button(hGroup, SWT.RADIO);
		hHButton.setText("H"); //$NON-NLS-1$
		hHButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				final String oldName = hName;
				hName = Constants.H_NOTE_NAME_H;
				if (oldName.equals(hName)) {
					return;
				}
				bName = Constants.B_NOTE_NAME_B;
				updateSelection();
				updateEnablement();
			}
		});

		hBButton = new Button(hGroup, SWT.RADIO);
		hBButton.setText("B"); //$NON-NLS-1$
		hBButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				final String oldName = hName;
				hName = Constants.H_NOTE_NAME_B;
				if (oldName.equals(hName)) {
					return;
				}
				bName = Constants.B_NOTE_NAME_BB;
				updateSelection();
				updateEnablement();
			}
		});
		GridDataFactory.fillDefaults().grab(true, false).span(2, 1).applyTo(hBButton);

		final Label bLabel = new Label(hGroup, SWT.NONE);
		bLabel.setText(PreferenceMessages.NoteNamesPreferencePage_name_of_b_or_bb);

		bHbButton = new Button(hGroup, SWT.RADIO);
		bHbButton.setText("Hb"); //$NON-NLS-1$
		bHbButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				bName = Constants.B_NOTE_NAME_HB;
				updateSelection();
			}
		});

		bBButton = new Button(hGroup, SWT.RADIO);
		bBButton.setText("B"); //$NON-NLS-1$
		bBButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				bName = Constants.B_NOTE_NAME_B;
				updateSelection();
			}
		});

		bBbButton = new Button(hGroup, SWT.RADIO);
		bBbButton.setText("Bb"); //$NON-NLS-1$
		bBbButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				bName = Constants.B_NOTE_NAME_BB;
				updateSelection();
			}
		});

		initButtons();

		// absolute note names group
		final Group absoluteNoteNamesGroup = new Group(mainComposite, SWT.NONE);
		absoluteNoteNamesGroup.setText(PreferenceMessages.NoteNamesPreferencePage_absolute_note_names);
		GridLayoutFactory.fillDefaults().numColumns(17).spacing(2, 5).margins(5, 5).applyTo(absoluteNoteNamesGroup);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(absoluteNoteNamesGroup);

		noteNameButtons = new ArrayList<Button>();
		for (final NoteNamesMode mode : NoteNamesMode.values()) {
			noteNameButtons.add(createNoteNamesButton(absoluteNoteNamesGroup, mode));
		}

		// set context-sensitive help
		Activator.getDefault().setHelp(getControl(), HELP_ID);

		return mainComposite;
	}

	private Button createNoteNamesButton(final Composite parent, final NoteNamesMode mode) {
		final Button button = new Button(parent, SWT.RADIO);
		button.setText(mode.getName(0, 0));
		button.setSelection(mode == NoteNamesMode.valueOf(prefs.getString(Preferences.ABSOLUTE_NOTE_NAMES_MODE)));
		button.setData(mode);
		for (int level = 1; level <= Constants.MAX_NOTES_LEVEL; level++) {
			new Label(parent, SWT.NONE).setText("-");
			new Label(parent, SWT.NONE).setText(mode.getName(0, level));
		}
		return button;
	}

	private void initButtons() {
		hName = prefs.getString(Preferences.GENERAL_H_NOTE_NAME);
		bName = prefs.getString(Preferences.GENERAL_B_NOTE_NAME);
		updateSelection();
		updateEnablement();
	}

	private void updateSelection() {
		hHButton.setSelection(hName.equals(Constants.H_NOTE_NAME_H));
		hBButton.setSelection(hName.equals(Constants.H_NOTE_NAME_B));
		bHbButton.setSelection(bName.equals(Constants.B_NOTE_NAME_HB));
		bBButton.setSelection(bName.equals(Constants.B_NOTE_NAME_B));
		bBbButton.setSelection(bName.equals(Constants.B_NOTE_NAME_BB));
	}

	private void updateEnablement() {
		bHbButton.setEnabled(hName.equals(Constants.H_NOTE_NAME_H));
		bBButton.setEnabled(hName.equals(Constants.H_NOTE_NAME_H));
	}

	@Override
	public boolean performOk() {
		NoteNamesMode.setHName(hName);
		NoteNamesMode.setBName(bName);
		prefs.setValue(Preferences.GENERAL_H_NOTE_NAME, hName);
		prefs.setValue(Preferences.GENERAL_B_NOTE_NAME, bName);
		for (final Button button : noteNameButtons) {
			if (button.getSelection()) {
				final NoteNamesMode mode = (NoteNamesMode) button.getData();
				NamesUtil.setNoteNamesMode(mode);
				prefs.setValue(Preferences.ABSOLUTE_NOTE_NAMES_MODE, mode.toString());
			}
		}
		return super.performOk();
	}

	@Override
	protected void performDefaults() {
		hName = prefs.getDefaultString(Preferences.GENERAL_H_NOTE_NAME);
		bName = prefs.getDefaultString(Preferences.GENERAL_B_NOTE_NAME);
		updateSelection();
		updateEnablement();
		for (final Button button : noteNameButtons) {
			button.setSelection(button.getData() == NoteNamesMode.valueOf(prefs
					.getDefaultString(Preferences.ABSOLUTE_NOTE_NAMES_MODE)));
		}
		super.performDefaults();
	}
}
