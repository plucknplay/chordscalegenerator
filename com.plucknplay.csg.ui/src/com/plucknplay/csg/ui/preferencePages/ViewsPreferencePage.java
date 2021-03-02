/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.preferencePages;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.PreferenceLinkArea;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;

import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.util.MyBooleanFieldEditor;

public class ViewsPreferencePage extends AbstractPreferencePage {

	public static final String ID = "com.plucknplay.csg.ui.viewsPreferences"; //$NON-NLS-1$
	public static final String HELP_ID = "views_preference_page_context"; //$NON-NLS-1$

	private IPreferenceStore prefs;

	private Button enableFastEditingButton;
	private Button highToDeepButton;
	private Button deepToHighButton;
	private Button usePointsModeButton;
	private Button useAlwaysPointsModeButton;
	private Button usePointsModeWhenEmptyInputButton;
	private Button ctrlCmdKeyButton;
	private Button altKeyButton;

	@Override
	public void init(final IWorkbench workbench) {
		super.init(workbench);
		prefs = Activator.getDefault().getPreferenceStore();
	}

	@Override
	protected void initialize() {
		super.initialize();
		updateFastEditingButtonEnablement();
		updatePointsModeButtonEnablement();
	}

	@Override
	protected void createFieldEditors() {

		// info group
		final Group infoGroup = new Group(getFieldEditorParent(), SWT.NONE);
		infoGroup.setText(PreferenceMessages.ViewsPreferencePage_info_group);
		GridLayoutFactory.fillDefaults().applyTo(infoGroup);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(infoGroup);

		// show info - input
		final BooleanFieldEditor showInputInfoEditor = new BooleanFieldEditor(Preferences.VIEWS_SHOW_INFO_INPUT,
				PreferenceMessages.ViewsPreferencePage_show_info_input, infoGroup);
		addField(showInputInfoEditor);
		GridDataFactory.fillDefaults().indent(5, 5).applyTo(showInputInfoEditor.getDescriptionControl(infoGroup));

		// show info - search mode
		final BooleanFieldEditor showSearchModeInfoEditor = new BooleanFieldEditor(
				Preferences.VIEWS_SHOW_INFO_SEARCH_MODE, PreferenceMessages.ViewsPreferencePage_show_info_search_mode,
				infoGroup);
		addField(showSearchModeInfoEditor);
		GridDataFactory.fillDefaults().indent(5, 0).applyTo(showSearchModeInfoEditor.getDescriptionControl(infoGroup));

		// fretboard view preference page link
		final PreferenceLinkArea link = PreferenceLinkUtil.createLink(
				PreferenceLinkUtil.createMainLinkComposite(infoGroup, 1, 5),
				(IWorkbenchPreferenceContainer) getContainer(),
				PreferenceMessages.ViewsPreferencePage_see_info_settings_for_fretboard_view,
				FretboardViewPreferencePage.ID);
		link.getControl().setFont(showSearchModeInfoEditor.getDescriptionControl(infoGroup).getFont());
		GridDataFactory.fillDefaults().grab(true, false).applyTo(link.getControl());

		// search mode group
		final Group searchModeGroup = new Group(getFieldEditorParent(), SWT.NONE);
		searchModeGroup.setText(PreferenceMessages.ViewsPreferencePage_search_mode_group);
		GridLayoutFactory.fillDefaults().applyTo(searchModeGroup);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(searchModeGroup);

		// enable double click
		final BooleanFieldEditor enableDoubleClickEditor = new BooleanFieldEditor(
				Preferences.VIEWS_SEARCH_MODE_ENABLE_DOUBLE_CLICK,
				PreferenceMessages.ViewsPreferencePage_search_mode_enable_double_click, searchModeGroup);
		addField(enableDoubleClickEditor);
		GridDataFactory.fillDefaults().indent(5, 5)
				.applyTo(enableDoubleClickEditor.getDescriptionControl(searchModeGroup));

		// enable [Esc] key binding
		final BooleanFieldEditor enableEscKeyEditor = new BooleanFieldEditor(
				Preferences.VIEWS_SEARCH_MODE_ENABLE_ESC_KEY,
				PreferenceMessages.ViewsPreferencePage_search_mode_enable_esc_key, searchModeGroup);
		addField(enableEscKeyEditor);
		GridDataFactory.fillDefaults().indent(5, 0).applyTo(enableEscKeyEditor.getDescriptionControl(searchModeGroup));

		// enable fast editing
		final MyBooleanFieldEditor enableFastEditingEditor = new MyBooleanFieldEditor(
				Preferences.VIEWS_SEARCH_MODE_ENABLE_FAST_EDITING,
				PreferenceMessages.ViewsPreferencePage_search_mode_enable_fast_editing, searchModeGroup);
		enableFastEditingButton = enableFastEditingEditor.getButton(searchModeGroup);
		addField(enableFastEditingEditor);
		GridDataFactory.fillDefaults().indent(5, 0)
				.applyTo(enableFastEditingEditor.getDescriptionControl(searchModeGroup));

		// fast editing order
		final boolean deepToHigh = prefs.getBoolean(Preferences.VIEWS_SEARCH_MODE_FAST_EDITING_DEEP_TO_HIGH);
		final Composite fastEditingOrderComposite = new Composite(searchModeGroup, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(3).equalWidth(false).applyTo(fastEditingOrderComposite);
		GridDataFactory.fillDefaults().indent(50, 0).applyTo(fastEditingOrderComposite);
		final Label fastEditingOrderLabel = new Label(fastEditingOrderComposite, SWT.NONE);
		fastEditingOrderLabel.setText(PreferenceMessages.ViewsPreferencePage_search_mode_fast_editing_order + ":"); //$NON-NLS-1$
		fastEditingOrderLabel.setFont(enableFastEditingButton.getFont());
		highToDeepButton = new Button(fastEditingOrderComposite, SWT.RADIO);
		highToDeepButton.setText(PreferenceMessages.ViewsPreferencePage_high_to_deep);
		highToDeepButton.setSelection(!deepToHigh);
		highToDeepButton.setFont(enableFastEditingButton.getFont());
		deepToHighButton = new Button(fastEditingOrderComposite, SWT.RADIO);
		deepToHighButton.setText(PreferenceMessages.ViewsPreferencePage_deep_to_high);
		deepToHighButton.setSelection(deepToHigh);
		deepToHighButton.setFont(enableFastEditingButton.getFont());

		// add listener
		enableFastEditingButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				updateFastEditingButtonEnablement();
			}
		});

		// use points mode
		final MyBooleanFieldEditor usePointsModeEditor = new MyBooleanFieldEditor(
				Preferences.VIEWS_SEARCH_MODE_USE_POINTS_MODE,
				PreferenceMessages.ViewsPreferencePage_search_mode_use_points_mode, searchModeGroup);
		usePointsModeButton = usePointsModeEditor.getButton(searchModeGroup);
		addField(usePointsModeEditor);
		GridDataFactory.fillDefaults().indent(5, 0).applyTo(usePointsModeEditor.getDescriptionControl(searchModeGroup));

		// use points mode options
		final boolean useAlwaysPointsMode = prefs.getBoolean(Preferences.VIEWS_SEARCH_MODE_USE_ALWAYS_POINTS_MODE);
		final Composite usePointsModeOptionsComposite = new Composite(searchModeGroup, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).applyTo(usePointsModeOptionsComposite);
		GridDataFactory.fillDefaults().indent(50, 0).applyTo(usePointsModeOptionsComposite);
		useAlwaysPointsModeButton = new Button(usePointsModeOptionsComposite, SWT.RADIO);
		useAlwaysPointsModeButton.setText(PreferenceMessages.ViewsPreferencePage_search_mode_use_always_points_mode);
		useAlwaysPointsModeButton.setSelection(useAlwaysPointsMode);
		useAlwaysPointsModeButton.setFont(usePointsModeButton.getFont());
		usePointsModeWhenEmptyInputButton = new Button(usePointsModeOptionsComposite, SWT.RADIO);
		usePointsModeWhenEmptyInputButton
				.setText(PreferenceMessages.ViewsPreferencePage_search_mode_use_points_mode_when_empty_input);
		usePointsModeWhenEmptyInputButton.setSelection(!useAlwaysPointsMode);
		usePointsModeWhenEmptyInputButton.setFont(usePointsModeButton.getFont());

		// add listener
		usePointsModeButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				updatePointsModeButtonEnablement();
			}
		});

		// relative notes mode keys
		final Label relativeNotesModeKeyLabel = new Label(searchModeGroup, SWT.NONE);
		relativeNotesModeKeyLabel.setText(PreferenceMessages.ViewPreferencePage_relative_notes_mode_key + ":"); //$NON-NLS-1$
		relativeNotesModeKeyLabel.setFont(usePointsModeButton.getFont());
		GridDataFactory.fillDefaults().indent(5, 0).applyTo(relativeNotesModeKeyLabel);

		final boolean relativeNotesModeKeyAlt = prefs
				.getBoolean(Preferences.VIEWS_SEARCH_MODE_RELATIVE_NOTES_MODE_KEY_ALT);
		final Composite relativeNotesModeComposite = new Composite(searchModeGroup, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).applyTo(relativeNotesModeComposite);
		GridDataFactory.fillDefaults().indent(50, 0).applyTo(relativeNotesModeComposite);

		ctrlCmdKeyButton = new Button(relativeNotesModeComposite, SWT.RADIO);
		ctrlCmdKeyButton
				.setText(Platform.OS_MACOSX.equals(Platform.getOS()) ? PreferenceMessages.ViewsPreferencePage_search_mode_command_key
						: PreferenceMessages.ViewsPreferencePage_search_mode_ctrl_key);
		ctrlCmdKeyButton.setSelection(!relativeNotesModeKeyAlt);
		ctrlCmdKeyButton.setFont(usePointsModeButton.getFont());

		altKeyButton = new Button(relativeNotesModeComposite, SWT.RADIO);
		altKeyButton.setText(PreferenceMessages.ViewsPreferencePage_search_mode_alt_key);
		altKeyButton.setSelection(relativeNotesModeKeyAlt);
		altKeyButton.setFont(usePointsModeButton.getFont());

		// set context-sensitive help
		Activator.getDefault().setHelp(getControl(), HELP_ID);
	}

	private void updateFastEditingButtonEnablement() {
		final boolean enabled = enableFastEditingButton.getSelection();
		deepToHighButton.setEnabled(enabled);
		highToDeepButton.setEnabled(enabled);
	}

	private void updatePointsModeButtonEnablement() {
		final boolean enabled = usePointsModeButton.getSelection();
		useAlwaysPointsModeButton.setEnabled(enabled);
		usePointsModeWhenEmptyInputButton.setEnabled(enabled);
	}

	@Override
	public boolean performOk() {
		prefs.setValue(Preferences.VIEWS_SEARCH_MODE_FAST_EDITING_DEEP_TO_HIGH, deepToHighButton.getSelection());
		prefs.setValue(Preferences.VIEWS_SEARCH_MODE_USE_ALWAYS_POINTS_MODE, useAlwaysPointsModeButton.getSelection());
		prefs.setValue(Preferences.VIEWS_SEARCH_MODE_RELATIVE_NOTES_MODE_KEY_ALT, altKeyButton.getSelection());
		return super.performOk();
	}

	@Override
	protected void performDefaults() {
		super.performDefaults();

		final boolean deepToHighDefault = prefs
				.getDefaultBoolean(Preferences.VIEWS_SEARCH_MODE_FAST_EDITING_DEEP_TO_HIGH);
		deepToHighButton.setSelection(deepToHighDefault);
		highToDeepButton.setSelection(!deepToHighDefault);
		updateFastEditingButtonEnablement();

		final boolean useFingeringAlways = prefs
				.getDefaultBoolean(Preferences.VIEWS_SEARCH_MODE_USE_ALWAYS_POINTS_MODE);
		useAlwaysPointsModeButton.setSelection(useFingeringAlways);
		usePointsModeWhenEmptyInputButton.setSelection(!useFingeringAlways);
		updatePointsModeButtonEnablement();

		final boolean relativeNotesModeKeyAlt = prefs
				.getDefaultBoolean(Preferences.VIEWS_SEARCH_MODE_RELATIVE_NOTES_MODE_KEY_ALT);
		ctrlCmdKeyButton.setSelection(!relativeNotesModeKeyAlt);
		altKeyButton.setSelection(relativeNotesModeKeyAlt);
	}
}
