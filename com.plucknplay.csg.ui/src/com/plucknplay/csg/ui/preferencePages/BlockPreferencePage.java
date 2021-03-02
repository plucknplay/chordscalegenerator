/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.preferencePages;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.plucknplay.csg.core.model.AdvancedFretBlock;
import com.plucknplay.csg.core.model.FretBlock;
import com.plucknplay.csg.core.model.Instrument;
import com.plucknplay.csg.core.model.sets.InstrumentList;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.model.BlockManager;
import com.plucknplay.csg.ui.util.OnlyNumbersKeyListener;

public class BlockPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	public static final String ID = "com.plucknplay.csg.ui.views.fretboardView.blockPreferences"; //$NON-NLS-1$
	public static final String HELP_ID = "blocks_preference_page_context"; //$NON-NLS-1$

	private IPreferenceStore prefs;
	private Instrument currentInstrument;

	private Text fretRangeText;
	private Button useEmptyStringNotesButton;
	private Button onlyRootNotesButton;
	private Text advancedFretRangeText;
	private ComboViewer advancedStringRangeComboViewer;
	private Button advancedUseEmptyStringNotesButton;

	@Override
	public void init(final IWorkbench workbench) {
		prefs = Activator.getDefault().getPreferenceStore();
		currentInstrument = InstrumentList.getInstance().getCurrentInstrument();
	}

	@Override
	protected Control createContents(final Composite parent) {

		// main composite
		final Composite mainComposite = new Composite(parent, SWT.NONE);
		GridLayoutFactory.fillDefaults().applyTo(mainComposite);

		// fret block group
		final Group fretBlocksGroup = createFretBlockGroup(mainComposite,
				PreferenceMessages.BlockPreferencePage_fret_blocks_settings);
		fretRangeText = createFretRangeText(fretBlocksGroup, Preferences.FRET_BLOCK_RANGE);
		useEmptyStringNotesButton = createUseEmptyStringNotesButton(fretBlocksGroup,
				Preferences.FRET_BLOCK_USE_EMPTY_STRINGS);

		// advanced fret block group
		final Group advancedFretBlocksGroup = createFretBlockGroup(mainComposite,
				PreferenceMessages.BlockPreferencePage_advanced_fret_blocks_settings);
		advancedFretRangeText = createFretRangeText(advancedFretBlocksGroup, Preferences.ADVANCED_FRET_BLOCK_RANGE);
		advancedStringRangeComboViewer = createStringRangeComboViewer(advancedFretBlocksGroup);
		advancedUseEmptyStringNotesButton = createUseEmptyStringNotesButton(advancedFretBlocksGroup,
				Preferences.ADVANCED_FRET_BLOCK_USE_EMPTY_STRINGS);
		setStringRange(prefs.getInt(Preferences.ADVANCED_FRET_BLOCK_STRING_RANGE_DECREASE));

		// octave block settings
		final Group octaveBlocksGroup = new Group(mainComposite, SWT.NONE);
		octaveBlocksGroup.setText(PreferenceMessages.BlockPreferencePage_octave_blocks_settings);
		GridLayoutFactory.fillDefaults().margins(5, 5).applyTo(octaveBlocksGroup);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(octaveBlocksGroup);

		onlyRootNotesButton = new Button(octaveBlocksGroup, SWT.CHECK);
		onlyRootNotesButton.setText(PreferenceMessages.BlockPreferencePage_jump_only_to_next_root_note);
		onlyRootNotesButton.setSelection(prefs.getBoolean(Preferences.OCTAVE_BLOCK_ONLY_ROOT_NOTES));

		// set context-sensitive help
		Activator.getDefault().setHelp(getControl(), HELP_ID);

		return mainComposite;
	}

	private Group createFretBlockGroup(final Composite parent, final String text) {
		final Group group = new Group(parent, SWT.NONE);
		group.setText(text);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(group);
		GridLayoutFactory.fillDefaults().numColumns(3).equalWidth(false).margins(5, 5).applyTo(group);
		return group;
	}

	private Text createFretRangeText(final Composite parent, final String prefsKey) {

		// label
		final Label advancedFretRangeLabel = new Label(parent, SWT.LEFT);
		advancedFretRangeLabel.setText(PreferenceMessages.BlockPreferencePage_fret_range);

		// text widget
		final Text text = new Text(parent, SWT.BORDER | SWT.SINGLE);
		text.setText("" + prefs.getInt(prefsKey)); //$NON-NLS-1$
		text.addKeyListener(new OnlyNumbersKeyListener());
		text.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(final FocusEvent e) {
				updateFretRangeText();
			}
		});
		text.setTextLimit(1);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(text);

		// comment
		final Label advancedFretRangeCommentLabel = new Label(parent, SWT.LEFT);
		advancedFretRangeCommentLabel.setText("(" + FretBlock.MIN_FRET_RANGE + ".." + FretBlock.MAX_FRET_RANGE + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		return text;
	}

	private ComboViewer createStringRangeComboViewer(final Composite parent) {

		// label
		final Label advancedStringRangeLabel = new Label(parent, SWT.LEFT);
		advancedStringRangeLabel.setText(PreferenceMessages.BlockPreferencePage_string_range);

		// combo viewer
		final ComboViewer comboViewer = new ComboViewer(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
		comboViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(final Object element) {
				if (element instanceof Integer) {
					final int value = (Integer) element;
					final int result = currentInstrument.getStringCount() - value;
					return NLS.bind(PreferenceMessages.BlockPreferencePage_string_range_decrease, value, result);
				}
				return super.getText(element);
			}
		});
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(comboViewer.getControl());

		// set input
		if (currentInstrument == null) {
			comboViewer.add(AdvancedFretBlock.DEFAULT_STRING_RANGE_DECREASE);
		} else {
			for (int i = AdvancedFretBlock.MIN_STRING_RANGE_DECREASE; i < currentInstrument.getStringCount(); i++) {
				comboViewer.add(i);
			}
		}
		return comboViewer;
	}

	private Button createUseEmptyStringNotesButton(final Composite parent, final String prefsKey) {
		final Button button = new Button(parent, SWT.CHECK);
		button.setText(PreferenceMessages.BlockPreferencePage_include_empty_string_notes);
		button.setSelection(prefs.getBoolean(prefsKey));
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).span(3, 1).grab(true, false).applyTo(button);
		return button;
	}

	protected void updateFretRangeText() {
		if ("".equals(fretRangeText.getText())) { //$NON-NLS-1$
			fretRangeText.setText("" + FretBlock.MIN_FRET_RANGE); //$NON-NLS-1$
		}
		int currentValue = Integer.parseInt(fretRangeText.getText());
		if (currentValue < FretBlock.MIN_FRET_RANGE) {
			currentValue = FretBlock.MIN_FRET_RANGE;
		} else if (currentValue > FretBlock.MAX_FRET_RANGE) {
			currentValue = FretBlock.MAX_FRET_RANGE;
		}
		fretRangeText.setText("" + currentValue); //$NON-NLS-1$
	}

	private void setStringRange(final int value) {
		int theValue = value;
		if (theValue < AdvancedFretBlock.MIN_STRING_RANGE_DECREASE) {
			theValue = AdvancedFretBlock.MIN_STRING_RANGE_DECREASE;
		} else if (currentInstrument == null) {
			theValue = AdvancedFretBlock.DEFAULT_STRING_RANGE_DECREASE;
		} else if (theValue > currentInstrument.getStringCount() - 1) {
			theValue = currentInstrument.getStringCount() - 1;
		}
		advancedStringRangeComboViewer.setSelection(new StructuredSelection(Integer.valueOf(theValue)));
	}

	@Override
	public boolean performOk() {
		BlockManager.getInstance().reset();
		updateFretRangeText();
		prefs.setValue(Preferences.FRET_BLOCK_RANGE, Integer.parseInt(fretRangeText.getText()));
		prefs.setValue(Preferences.FRET_BLOCK_USE_EMPTY_STRINGS, useEmptyStringNotesButton.getSelection());
		prefs.setValue(Preferences.ADVANCED_FRET_BLOCK_RANGE, Integer.parseInt(advancedFretRangeText.getText()));
		prefs.setValue(Preferences.ADVANCED_FRET_BLOCK_STRING_RANGE_DECREASE,
				(Integer) ((IStructuredSelection) advancedStringRangeComboViewer.getSelection()).getFirstElement());
		prefs.setValue(Preferences.ADVANCED_FRET_BLOCK_USE_EMPTY_STRINGS,
				advancedUseEmptyStringNotesButton.getSelection());
		prefs.setValue(Preferences.OCTAVE_BLOCK_ONLY_ROOT_NOTES, onlyRootNotesButton.getSelection());
		return super.performOk();
	}

	@Override
	protected void performDefaults() {
		fretRangeText.setText("" + prefs.getDefaultInt(Preferences.FRET_BLOCK_RANGE)); //$NON-NLS-1$
		useEmptyStringNotesButton.setSelection(prefs.getDefaultBoolean(Preferences.FRET_BLOCK_USE_EMPTY_STRINGS));
		advancedFretRangeText.setText("" + prefs.getDefaultInt(Preferences.ADVANCED_FRET_BLOCK_RANGE)); //$NON-NLS-1$
		setStringRange(prefs.getDefaultInt(Preferences.ADVANCED_FRET_BLOCK_STRING_RANGE_DECREASE));
		advancedUseEmptyStringNotesButton.setSelection(prefs
				.getDefaultBoolean(Preferences.ADVANCED_FRET_BLOCK_USE_EMPTY_STRINGS));
		onlyRootNotesButton.setSelection(prefs
				.getDefaultBoolean(Preferences.OCTAVE_BLOCK_ONLY_ROOT_NOTES));
		super.performDefaults();
	}
}
