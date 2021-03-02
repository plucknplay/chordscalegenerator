/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.preferencePages;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;

import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.util.MyBooleanFieldEditor;
import com.plucknplay.csg.ui.util.RGBUtil;

public class TabViewPreferencePage extends AbstractPreferencePage {

	public static final String ID = "com.plucknplay.csg.ui.views.tabViewPreferences"; //$NON-NLS-1$
	public static final String HELP_ID = "tab_view_preference_page_context"; //$NON-NLS-1$

	private IPreferenceStore prefs;

	private Button highlightRootNoteButton;
	private Button boldRootNoteButton;
	private Button framedRootNoteButton;
	private Button colorRootNoteButton;
	private ColorSelector colorSelector;

	@Override
	public void init(final IWorkbench workbench) {
		prefs = Activator.getDefault().getPreferenceStore();
	}

	@Override
	protected void initialize() {
		super.initialize();
		updateButtonEnablement();
	}

	@Override
	protected void createFieldEditors() {

		// preference page links
		final Composite linkComposite = PreferenceLinkUtil.createMainLinkComposite(getFieldEditorParent());
		PreferenceLinkUtil.createViewsLink(linkComposite, (IWorkbenchPreferenceContainer) getContainer(), false);
		PreferenceLinkUtil.createChordAndScaleNamesLink(linkComposite, (IWorkbenchPreferenceContainer) getContainer());

		// show muted strings
		final BooleanFieldEditor showMutedStringsEditor = new BooleanFieldEditor(
				Preferences.TAB_VIEW_SHOW_MUTED_STRINGS, PreferenceMessages.TabViewPreferencePage_show_muted_strings,
				getFieldEditorParent());
		addField(showMutedStringsEditor);
		GridDataFactory.fillDefaults().applyTo(showMutedStringsEditor.getDescriptionControl(getFieldEditorParent()));

		// draw doubled strings
		final BooleanFieldEditor drawDoubledStringsEditor = new BooleanFieldEditor(
				Preferences.TAB_VIEW_DRAW_DOUBLED_STRINGS,
				PreferenceMessages.TabViewPreferencePage_draw_doubled_strings, getFieldEditorParent());
		addField(drawDoubledStringsEditor);

		// highlight root note
		final MyBooleanFieldEditor highlightRootNoteEditor = new MyBooleanFieldEditor(
				Preferences.TAB_VIEW_HIGHLIGHT_ROOT_NOTE, PreferenceMessages.TabViewPreferencePage_highlight_root_note,
				getFieldEditorParent());
		highlightRootNoteButton = highlightRootNoteEditor.getButton(getFieldEditorParent());
		addField(highlightRootNoteEditor);

		// highlight root note composite
		final Composite highlightRootNoteComposite = new Composite(getFieldEditorParent(), SWT.NONE);
		GridDataFactory.fillDefaults().indent(50, -5).applyTo(highlightRootNoteComposite);
		GridLayoutFactory.fillDefaults().numColumns(4).applyTo(highlightRootNoteComposite);

		// highlight with bold font weight
		boldRootNoteButton = new Button(highlightRootNoteComposite, SWT.CHECK);
		boldRootNoteButton.setText(PreferenceMessages.TabViewPreferencePage_highlight_root_note_with_bold_font);
		boldRootNoteButton.setSelection(prefs.getBoolean(Preferences.TAB_VIEW_HIGHLIGHT_ROOT_NOTE_WITH_BOLD_FONT));

		// highlight with frame
		framedRootNoteButton = new Button(highlightRootNoteComposite, SWT.CHECK);
		framedRootNoteButton.setText(PreferenceMessages.TabViewPreferencePage_highlight_root_note_with_frame);
		framedRootNoteButton.setSelection(prefs.getBoolean(Preferences.TAB_VIEW_HIGHLIGHT_ROOT_NOTE_WITH_FRAME));

		// highlight with color
		colorRootNoteButton = new Button(highlightRootNoteComposite, SWT.CHECK);
		colorRootNoteButton.setText(PreferenceMessages.ViewPreferencePage_highlight_root_note_with_foreground_color);
		colorRootNoteButton.setSelection(prefs.getBoolean(Preferences.TAB_VIEW_HIGHLIGHT_ROOT_NOTE_WITH_COLOR));

		// color editor
		colorSelector = new ColorSelector(highlightRootNoteComposite);
		colorSelector.setColorValue(RGBUtil.convertStringToRGB(prefs
				.getString(Preferences.TAB_VIEW_HIGHLIGHT_ROOT_NOTE_COLOR_ID)));

		// add listener
		final SelectionAdapter listener = new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {

				// ensure that at least one button is selected
				final boolean selection = boldRootNoteButton.getSelection() || framedRootNoteButton.getSelection()
						|| colorRootNoteButton.getSelection();
				if (!selection && highlightRootNoteButton.getSelection()) {
					if (e.getSource() == boldRootNoteButton) {
						framedRootNoteButton.setSelection(true);
					} else if (e.getSource() == framedRootNoteButton || e.getSource() == colorRootNoteButton) {
						boldRootNoteButton.setSelection(true);
					}
				}

				// update enablement
				if (e.getSource() == highlightRootNoteButton || e.getSource() == colorRootNoteButton) {
					updateButtonEnablement();
				}
			}
		};
		highlightRootNoteButton.addSelectionListener(listener);
		boldRootNoteButton.addSelectionListener(listener);
		framedRootNoteButton.addSelectionListener(listener);
		colorRootNoteButton.addSelectionListener(listener);

		// set context-sensitive help
		Activator.getDefault().setHelp(getControl(), HELP_ID);
	}

	private void updateButtonEnablement() {
		final boolean enabled = highlightRootNoteButton.getSelection();
		boldRootNoteButton.setEnabled(enabled);
		framedRootNoteButton.setEnabled(enabled);
		colorRootNoteButton.setEnabled(enabled);
		colorSelector.setEnabled(enabled && colorRootNoteButton.getSelection());
	}

	/* --- perform buttons handling --- */

	@Override
	public boolean performOk() {

		prefs.setValue(Preferences.TAB_VIEW_HIGHLIGHT_ROOT_NOTE_WITH_BOLD_FONT, boldRootNoteButton.getSelection());
		prefs.setValue(Preferences.TAB_VIEW_HIGHLIGHT_ROOT_NOTE_WITH_FRAME, framedRootNoteButton.getSelection());
		prefs.setValue(Preferences.TAB_VIEW_HIGHLIGHT_ROOT_NOTE_WITH_COLOR, colorRootNoteButton.getSelection());
		prefs.setValue(Preferences.TAB_VIEW_HIGHLIGHT_ROOT_NOTE_COLOR_ID,
				RGBUtil.convertRGBToId(colorSelector.getColorValue()));

		return super.performOk();
	}

	@Override
	protected void performDefaults() {
		super.performDefaults();

		boldRootNoteButton.setSelection(prefs
				.getDefaultBoolean(Preferences.TAB_VIEW_HIGHLIGHT_ROOT_NOTE_WITH_BOLD_FONT));
		framedRootNoteButton.setSelection(prefs.getDefaultBoolean(Preferences.TAB_VIEW_HIGHLIGHT_ROOT_NOTE_WITH_FRAME));
		colorRootNoteButton.setSelection(prefs.getDefaultBoolean(Preferences.TAB_VIEW_HIGHLIGHT_ROOT_NOTE_WITH_COLOR));
		colorSelector.setColorValue(RGBUtil.convertStringToRGB(prefs
				.getDefaultString(Preferences.TAB_VIEW_HIGHLIGHT_ROOT_NOTE_COLOR_ID)));

		updateButtonEnablement();
	}
}
