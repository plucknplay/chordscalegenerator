/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.preferencePages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;

import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.UIConstants;
import com.plucknplay.csg.ui.util.DefaultCollectionContentProvider;
import com.plucknplay.csg.ui.util.OnlyNumbersKeyListener;

public class ExportPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	public static final String ID = "com.plucknplay.csg.ui.exportPreferences"; //$NON-NLS-1$
	public static final String HELP_ID = "export_preference_page_context"; //$NON-NLS-1$

	private static final String NONE_COMBO_ENTRY = PreferenceMessages.ExportPreferencePage_none;
	private static final String BOX_COMBO_ENTRY = PreferenceMessages.ExportPreferencePage_box;
	private static final String TAB_COMBO_ENTRY = PreferenceMessages.ExportPreferencePage_tab;
	private static final String NOTES_COMBO_ENTRY = PreferenceMessages.ExportPreferencePage_notes;

	private IPreferenceStore prefs;

	private List<Button> liveSizeButtons;
	private List<Button> fixedHeigthButtons;
	private List<Text> heightTextFields;
	private boolean[] enablements;

	private Combo typeCombo;
	private ComboViewer firstCombo;
	private ComboViewer secondCombo;
	private ComboViewer thirdCombo;
	private Label thirdLabel;
	private Button reverseOrderButton;

	private final ISelectionChangedListener selectionListener = new ISelectionChangedListener() {
		@Override
		public void selectionChanged(final SelectionChangedEvent event) {
			updateCombos();
		}
	};

	@Override
	public void init(final IWorkbench workbench) {
		prefs = Activator.getDefault().getPreferenceStore();
		liveSizeButtons = new ArrayList<Button>();
		fixedHeigthButtons = new ArrayList<Button>();
		heightTextFields = new ArrayList<Text>();
		enablements = new boolean[5];
	}

	@Override
	protected Control createContents(final Composite parent) {

		// main composite
		final Composite mainComposite = new Composite(parent, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(1).equalWidth(true).applyTo(mainComposite);

		// export filename preference page link
		PreferenceLinkUtil.createLink(PreferenceLinkUtil.createMainLinkComposite(mainComposite),
				(IWorkbenchPreferenceContainer) getContainer(),
				PreferenceMessages.ExportPreferencePage_see_settings_for_suggested_filename,
				ExportFilenamePreferencePage.ID);

		// default file extension
		final Composite fileExtensionComposite = new Composite(mainComposite, SWT.NONE);
		GridLayoutFactory.fillDefaults().equalWidth(false).numColumns(2).extendedMargins(5, 0, 0, 5)
				.applyTo(fileExtensionComposite);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(fileExtensionComposite);

		final Label typeLabel = new Label(fileExtensionComposite, SWT.NONE);
		typeLabel.setText(PreferenceMessages.ExportPreferencePage_file_type);

		typeCombo = new Combo(fileExtensionComposite, SWT.READ_ONLY | SWT.DROP_DOWN);
		typeCombo.setItems(new String[] { UIConstants.FILE_EXTENSION_BMP, UIConstants.FILE_EXTENSION_JPG,
				UIConstants.FILE_EXTENSION_PNG });
		typeCombo.setText(prefs.getString(Preferences.CLIPBOARD_EXPORT_FILE_EXTENSION));
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(typeCombo);

		// export size group
		final Group sizeGroup = new Group(mainComposite, SWT.NONE);
		sizeGroup.setText(PreferenceMessages.ExportPreferencePage_export_size);
		GridLayoutFactory.fillDefaults().equalWidth(false).numColumns(5).margins(5, 5).applyTo(sizeGroup);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(sizeGroup);

		// create group entries
		createExportSizeEntry(sizeGroup, 0, PreferenceMessages.ExportPreferencePage_fretboard_view,
				Preferences.FRETBOARD_VIEW_EXPORT_LIVE_SIZE, Preferences.FRETBOARD_VIEW_EXPORT_HEIGHT, 100, 1000);
		createExportSizeEntry(sizeGroup, 1, PreferenceMessages.ExportPreferencePage_keyboard_view,
				Preferences.KEYBOARD_VIEW_EXPORT_LIVE_SIZE, Preferences.KEYBOARD_VIEW_EXPORT_HEIGHT, 100, 1000);
		createExportSizeEntry(sizeGroup, 2, PreferenceMessages.ExportPreferencePage_box_view,
				Preferences.BOX_VIEW_EXPORT_LIVE_SIZE, Preferences.BOX_VIEW_EXPORT_HEIGHT, 100, 1000);
		createExportSizeEntry(sizeGroup, 3, PreferenceMessages.ExportPreferencePage_tab_view,
				Preferences.TAB_VIEW_EXPORT_LIVE_SIZE, Preferences.TAB_VIEW_EXPORT_HEIGHT, 100, 1000);
		createExportSizeEntry(sizeGroup, 4, PreferenceMessages.ExportPreferencePage_notes_view,
				Preferences.NOTES_VIEW_EXPORT_LIVE_SIZE, Preferences.NOTES_VIEW_EXPORT_HEIGHT, 100, 1000);

		// chord results clipboard export group
		final Group chordResultsExportGroup = new Group(mainComposite, SWT.NONE);
		chordResultsExportGroup.setText(PreferenceMessages.ExportPreferencePage_chord_results_clipboard_export);
		GridLayoutFactory.fillDefaults().equalWidth(false).numColumns(2).margins(5, 5).applyTo(chordResultsExportGroup);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(chordResultsExportGroup);

		final Label firstLabel = new Label(chordResultsExportGroup, SWT.NONE);
		firstLabel.setText(PreferenceMessages.ExportPreferencePage_first_image);
		firstCombo = createCombo(chordResultsExportGroup, 1);

		final Label secondLabel = new Label(chordResultsExportGroup, SWT.NONE);
		secondLabel.setText(PreferenceMessages.ExportPreferencePage_second_image);
		secondCombo = createCombo(chordResultsExportGroup, 2);

		thirdLabel = new Label(chordResultsExportGroup, SWT.NONE);
		thirdLabel.setText(PreferenceMessages.ExportPreferencePage_third_image);
		thirdCombo = createCombo(chordResultsExportGroup, 3);

		firstCombo.getCombo().setText(getComboText(prefs.getString(Preferences.CLIPBOARD_EXPORT_FIRST_VIEW)));
		secondCombo.getCombo().setText(getComboText(prefs.getString(Preferences.CLIPBOARD_EXPORT_SECOND_VIEW)));
		thirdCombo.getCombo().setText(getComboText(prefs.getString(Preferences.CLIPBOARD_EXPORT_THIRD_VIEW)));

		updateCombos();

		// copy to clipboard in reverse order
		reverseOrderButton = new Button(mainComposite, SWT.CHECK);
		reverseOrderButton.setText(PreferenceMessages.ExportPreferencePage_reverse_order);
		reverseOrderButton.setSelection(prefs.getBoolean(Preferences.CLIPBOARD_EXPORT_REVERSE_ORDER));
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(reverseOrderButton);

		// set context-sensitive help
		Activator.getDefault().setHelp(getControl(), HELP_ID);

		return mainComposite;
	}

	private ComboViewer createCombo(final Group chordResultsExportGroup, final int index) {
		final ComboViewer combo = new ComboViewer(chordResultsExportGroup, SWT.READ_ONLY | SWT.DROP_DOWN);
		combo.setContentProvider(new DefaultCollectionContentProvider());
		combo.setSorter(new ViewerSorter() {
			@Override
			public int compare(final Viewer viewer, final Object e1, final Object e2) {
				if (e1 instanceof String && e1.equals(NONE_COMBO_ENTRY)) {
					return -1;
				}
				if (e2 instanceof String && e2.equals(NONE_COMBO_ENTRY)) {
					return +1;
				}
				return super.compare(viewer, e1, e2);
			}
		});
		combo.addFilter(new ViewerFilter() {
			@Override
			public boolean select(final Viewer viewer, final Object parentElement, final Object element) {
				if (element == null || !(element instanceof String)) {
					return false;
				}
				final String text = (String) element;
				if (index == 1 && text.equals(NONE_COMBO_ENTRY)) {
					return false;
				}
				if (index == 2 && text.equals(firstCombo.getCombo().getText())) {
					return false;
				}
				if (index == 3 && text.equals(NONE_COMBO_ENTRY)) {
					return true;
				}
				if (index == 3
						&& (text.equals(firstCombo.getCombo().getText()) || text.equals(secondCombo.getCombo()
								.getText()))) {
					return false;
				}
				return true;
			}
		});
		combo.addSelectionChangedListener(selectionListener);

		final String[] items = { NONE_COMBO_ENTRY, BOX_COMBO_ENTRY, TAB_COMBO_ENTRY, NOTES_COMBO_ENTRY };
		combo.setInput(Arrays.asList(items));
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(combo.getCombo());
		return combo;
	}

	private void updateCombos() {
		if (firstCombo == null || secondCombo == null || thirdCombo == null || firstCombo.getCombo().isDisposed()
				|| secondCombo.getCombo().isDisposed() || thirdCombo.getCombo().isDisposed()) {
			return;
		}

		// (1) remove all selection change listeners to prevent deadlocks
		firstCombo.removeSelectionChangedListener(selectionListener);
		secondCombo.removeSelectionChangedListener(selectionListener);
		thirdCombo.removeSelectionChangedListener(selectionListener);

		// (2) refresh all combos
		firstCombo.refresh();
		secondCombo.refresh();
		thirdCombo.refresh();

		// (3) update combos depending on the currently set text
		if ("".equals(secondCombo.getCombo().getText())) { //$NON-NLS-1$
			secondCombo.getCombo().setText(NONE_COMBO_ENTRY);
		}
		if ("".equals(thirdCombo.getCombo().getText()) || secondCombo.getCombo().getText().equals(NONE_COMBO_ENTRY)) {
			thirdCombo.getCombo().setText(NONE_COMBO_ENTRY);
		}

		thirdCombo.getCombo().setEnabled(!secondCombo.getCombo().getText().equals(NONE_COMBO_ENTRY));
		thirdLabel.setEnabled(!secondCombo.getCombo().getText().equals(NONE_COMBO_ENTRY));

		// (4) add selection change listeners again
		firstCombo.addSelectionChangedListener(selectionListener);
		secondCombo.addSelectionChangedListener(selectionListener);
		thirdCombo.addSelectionChangedListener(selectionListener);
	}

	private String getPreferenceValue(final ComboViewer combo) {
		final String text = combo.getCombo().getText();
		String result = text.equals(BOX_COMBO_ENTRY) ? UIConstants.EXPORT_BOX
				: text.equals(TAB_COMBO_ENTRY) ? UIConstants.EXPORT_TAB
						: text.equals(NOTES_COMBO_ENTRY) ? UIConstants.EXPORT_NOTES : UIConstants.EXPORT_NONE;

		if (combo == thirdCombo && getPreferenceValue(secondCombo).equals(UIConstants.EXPORT_NONE)) {
			result = UIConstants.EXPORT_NONE;
		}

		return result;
	}

	private String getComboText(final String prefValue) {
		return prefValue.equals(UIConstants.EXPORT_BOX) ? BOX_COMBO_ENTRY
				: prefValue.equals(UIConstants.EXPORT_TAB) ? TAB_COMBO_ENTRY : prefValue
						.equals(UIConstants.EXPORT_NOTES) ? NOTES_COMBO_ENTRY : NONE_COMBO_ENTRY;
	}

	private void createExportSizeEntry(final Composite parent, final int index, final String text,
			final String prefLiveSize, final String prefHeight, final int minHeight, final int maxHeight) {

		final boolean liveSize = prefs.getBoolean(prefLiveSize);
		enablements[index] = liveSize;

		// label
		final Label label = new Label(parent, SWT.NONE);
		label.setText(text);

		// live size button
		final Button liveSizeButton = new Button(parent, SWT.RADIO);
		liveSizeButton.setText(PreferenceMessages.ExportPreferencePage_live_size);
		liveSizeButton.setSelection(liveSize);
		liveSizeButtons.add(liveSizeButton);

		// fixed height button
		final Button fixedHeightButton = new Button(parent, SWT.RADIO);
		fixedHeightButton.setText(PreferenceMessages.ExportPreferencePage_fixed_height);
		fixedHeightButton.setSelection(!liveSize);
		fixedHeigthButtons.add(fixedHeightButton);

		// height text field
		final Text heightText = new Text(parent, SWT.SINGLE | SWT.BORDER);
		heightText.setText("" + prefs.getInt(prefHeight)); //$NON-NLS-1$
		heightText.addKeyListener(new OnlyNumbersKeyListener());
		heightText.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(final FocusEvent e) {
				if ("".equals(heightText.getText())) {
					heightText.setText("0"); //$NON-NLS-1$
				}
				int currentValue = Integer.parseInt(heightText.getText());
				if (currentValue < minHeight) {
					currentValue = minHeight;
				}
				if (currentValue > maxHeight) {
					currentValue = maxHeight;
				}
				heightText.setText("" + currentValue); //$NON-NLS-1$
			}
		});
		heightText.setTextLimit(4);
		heightTextFields.add(heightText);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(heightText);

		// comment label
		final Label commentLabel = new Label(parent, SWT.NONE);
		commentLabel.setText("(" + minHeight + ".." + maxHeight + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		// handle enablement
		liveSizeButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				enablements[index] = true;
				updateEnablements();
			}
		});
		fixedHeightButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				enablements[index] = false;
				updateEnablements();
			}
		});
	}

	private void updateEnablements() {
		for (int i = 0; i < enablements.length; i++) {
			final boolean liveSize = enablements[i];
			liveSizeButtons.get(i).setSelection(liveSize);
			fixedHeigthButtons.get(i).setSelection(!liveSize);
		}
	}

	/* --- perform methods --- */

	@Override
	public boolean performOk() {

		prefs.setValue(Preferences.FRETBOARD_VIEW_EXPORT_LIVE_SIZE, liveSizeButtons.get(0).getSelection());
		prefs.setValue(Preferences.KEYBOARD_VIEW_EXPORT_LIVE_SIZE, liveSizeButtons.get(1).getSelection());
		prefs.setValue(Preferences.BOX_VIEW_EXPORT_LIVE_SIZE, liveSizeButtons.get(2).getSelection());
		prefs.setValue(Preferences.TAB_VIEW_EXPORT_LIVE_SIZE, liveSizeButtons.get(3).getSelection());
		prefs.setValue(Preferences.NOTES_VIEW_EXPORT_LIVE_SIZE, liveSizeButtons.get(4).getSelection());

		prefs.setValue(Preferences.FRETBOARD_VIEW_EXPORT_HEIGHT, Integer.parseInt(heightTextFields.get(0).getText()));
		prefs.setValue(Preferences.KEYBOARD_VIEW_EXPORT_HEIGHT, Integer.parseInt(heightTextFields.get(1).getText()));
		prefs.setValue(Preferences.BOX_VIEW_EXPORT_HEIGHT, Integer.parseInt(heightTextFields.get(2).getText()));
		prefs.setValue(Preferences.TAB_VIEW_EXPORT_HEIGHT, Integer.parseInt(heightTextFields.get(3).getText()));
		prefs.setValue(Preferences.NOTES_VIEW_EXPORT_HEIGHT, Integer.parseInt(heightTextFields.get(4).getText()));

		prefs.setValue(Preferences.CLIPBOARD_EXPORT_FILE_EXTENSION, typeCombo.getText().trim());

		prefs.setValue(Preferences.CLIPBOARD_EXPORT_FIRST_VIEW, getPreferenceValue(firstCombo));
		prefs.setValue(Preferences.CLIPBOARD_EXPORT_SECOND_VIEW, getPreferenceValue(secondCombo));
		prefs.setValue(Preferences.CLIPBOARD_EXPORT_THIRD_VIEW, getPreferenceValue(thirdCombo));

		prefs.setValue(Preferences.CLIPBOARD_EXPORT_REVERSE_ORDER, reverseOrderButton.getSelection());

		return super.performOk();
	}

	@Override
	protected void performDefaults() {

		enablements[0] = prefs.getDefaultBoolean(Preferences.FRETBOARD_VIEW_EXPORT_LIVE_SIZE);
		enablements[1] = prefs.getDefaultBoolean(Preferences.KEYBOARD_VIEW_EXPORT_LIVE_SIZE);
		enablements[2] = prefs.getDefaultBoolean(Preferences.BOX_VIEW_EXPORT_LIVE_SIZE);
		enablements[3] = prefs.getDefaultBoolean(Preferences.TAB_VIEW_EXPORT_LIVE_SIZE);
		enablements[4] = prefs.getDefaultBoolean(Preferences.NOTES_VIEW_EXPORT_LIVE_SIZE);
		updateEnablements();

		heightTextFields.get(0).setText("" + prefs.getDefaultInt(Preferences.FRETBOARD_VIEW_EXPORT_HEIGHT)); //$NON-NLS-1$
		heightTextFields.get(1).setText("" + prefs.getDefaultInt(Preferences.KEYBOARD_VIEW_EXPORT_HEIGHT)); //$NON-NLS-1$
		heightTextFields.get(2).setText("" + prefs.getDefaultInt(Preferences.BOX_VIEW_EXPORT_HEIGHT)); //$NON-NLS-1$
		heightTextFields.get(3).setText("" + prefs.getDefaultInt(Preferences.TAB_VIEW_EXPORT_HEIGHT)); //$NON-NLS-1$
		heightTextFields.get(4).setText("" + prefs.getDefaultInt(Preferences.NOTES_VIEW_EXPORT_HEIGHT)); //$NON-NLS-1$

		typeCombo.setText(" " + prefs.getDefaultString(Preferences.CLIPBOARD_EXPORT_FILE_EXTENSION)); //$NON-NLS-1$

		firstCombo.getCombo().setText(getComboText(prefs.getDefaultString(Preferences.CLIPBOARD_EXPORT_FIRST_VIEW)));
		secondCombo.getCombo().setText(getComboText(prefs.getDefaultString(Preferences.CLIPBOARD_EXPORT_SECOND_VIEW)));
		thirdCombo.getCombo().setText(getComboText(prefs.getDefaultString(Preferences.CLIPBOARD_EXPORT_THIRD_VIEW)));
		updateCombos();

		reverseOrderButton.setSelection(prefs.getDefaultBoolean(Preferences.CLIPBOARD_EXPORT_REVERSE_ORDER));

		super.performDefaults();
	}
}
