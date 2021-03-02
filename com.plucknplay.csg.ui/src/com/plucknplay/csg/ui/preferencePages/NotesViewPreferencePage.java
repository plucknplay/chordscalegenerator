/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.preferencePages;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;

import com.plucknplay.csg.core.model.enums.Clef;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.util.MyBooleanFieldEditor;

public class NotesViewPreferencePage extends AbstractPreferencePage {

	public static final String ID = "com.plucknplay.csg.ui.views.notesViewPreferences"; //$NON-NLS-1$
	public static final String HELP_ID = "notes_view_preference_page_context"; //$NON-NLS-1$

	private IPreferenceStore prefs;

	private Button openNoteButton;
	private Button closedNoteButton;
	private Button useMaxWidthButton;
	private Button flexibleNotesSpacingButton;
	private Button noNoteOnStaffButton;
	private Button notWholeStaffUsedButton;
	private ComboViewer fallbackClefComboViewer;

	@Override
	public void init(final IWorkbench workbench) {
		prefs = Activator.getDefault().getPreferenceStore();
	}

	@Override
	public void createControl(final Composite parent) {
		super.createControl(parent);
		updateNotesSpacingEnablement();
		updateFilterEnablement();
	}

	@Override
	protected void createFieldEditors() {

		// preference page links
		final Composite linkComposite = PreferenceLinkUtil.createMainLinkComposite(getFieldEditorParent());
		PreferenceLinkUtil.createViewsLink(linkComposite, (IWorkbenchPreferenceContainer) getContainer(), false);
		PreferenceLinkUtil.createChordAndScaleNamesLink(linkComposite, (IWorkbenchPreferenceContainer) getContainer());
		PreferenceLinkUtil.createNoteNamesLink(linkComposite, (IWorkbenchPreferenceContainer) getContainer());
		PreferenceLinkUtil.createLink(linkComposite, (IWorkbenchPreferenceContainer) getContainer(),
				PreferenceMessages.ViewPreferencePage_see_settings_for_blocks, BlockPreferencePage.ID);

		// note representation
		final boolean openNotes = prefs.getBoolean(Preferences.NOTES_VIEW_OPEN_NOTE_REPRESENTATION);
		final Group noteRepresentationGroup = new Group(getFieldEditorParent(), SWT.NONE);
		noteRepresentationGroup.setText(PreferenceMessages.NotesViewPreferencePage_note_representation);
		GridLayoutFactory.fillDefaults().numColumns(2).margins(5, 5).applyTo(noteRepresentationGroup);
		GridDataFactory.fillDefaults().applyTo(noteRepresentationGroup);
		openNoteButton = new Button(noteRepresentationGroup, SWT.RADIO);
		openNoteButton.setText(PreferenceMessages.NotesViewPreferencePage_open_note_head);
		openNoteButton.setSelection(openNotes);
		closedNoteButton = new Button(noteRepresentationGroup, SWT.RADIO);
		closedNoteButton.setText(PreferenceMessages.NotesViewPreferencePage_closed_note_head);
		closedNoteButton.setSelection(!openNotes);

		// highlight root notes
		final Composite highlightRootNoteComposite = new Composite(noteRepresentationGroup, SWT.NONE);
		GridDataFactory.fillDefaults().span(2, 1).applyTo(highlightRootNoteComposite);
		final BooleanFieldEditor highlightRootNoteEditor = new BooleanFieldEditor(
				Preferences.NOTES_VIEW_HIGHLIGHT_ROOT_NOTE,
				PreferenceMessages.NotesViewPreferencePage_highlight_root_note, highlightRootNoteComposite);
		addField(highlightRootNoteEditor);

		// use max width
		final Composite useMaxWidthComposite = new Composite(noteRepresentationGroup, SWT.NONE);
		GridDataFactory.fillDefaults().span(2, 1).applyTo(useMaxWidthComposite);
		final MyBooleanFieldEditor useMaxWidthEditor = new MyBooleanFieldEditor(Preferences.NOTES_VIEW_USE_MAX_WIDTH,
				PreferenceMessages.NotesViewPreferencePage_use_max_width, useMaxWidthComposite);
		useMaxWidthButton = useMaxWidthEditor.getButton(useMaxWidthComposite);
		addField(useMaxWidthEditor);

		// flexible notes spacing
		final Composite flexibleNotesSpacingComposite = new Composite(noteRepresentationGroup, SWT.NONE);
		GridDataFactory.fillDefaults().span(2, 1).applyTo(flexibleNotesSpacingComposite);
		final MyBooleanFieldEditor flexibleNotesSpacingEditor = new MyBooleanFieldEditor(
				Preferences.NOTES_VIEW_FLEXIBLE_SPACING,
				PreferenceMessages.NotesViewPreferencePage_flexible_notes_spacing, flexibleNotesSpacingComposite);
		flexibleNotesSpacingButton = flexibleNotesSpacingEditor.getButton(flexibleNotesSpacingComposite);
		addField(flexibleNotesSpacingEditor);

		useMaxWidthButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				updateNotesSpacingEnablement();
			}
		});

		// clefs group
		final Group clefGroup = new Group(getFieldEditorParent(), SWT.NONE);
		clefGroup.setText(PreferenceMessages.NotesViewPreferencePage_clefs);
		GridLayoutFactory.fillDefaults().margins(5, 5).applyTo(clefGroup);
		GridDataFactory.fillDefaults().applyTo(clefGroup);

		// show clef annotation
		final Composite clefAnnotationComposite = new Composite(clefGroup, SWT.NONE);
		GridDataFactory.fillDefaults().applyTo(clefAnnotationComposite);
		final BooleanFieldEditor showClefAnnotationEditor = new BooleanFieldEditor(
				Preferences.NOTES_VIEW_SHOW_CLEF_ANNOTATION,
				PreferenceMessages.NotesViewPreferencePage_show_clef_annotation, clefAnnotationComposite);
		addField(showClefAnnotationEditor);

		final Label separator = new Label(clefGroup, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(separator);

		// filter
		final Label filterLabel = new Label(clefGroup, SWT.NONE);
		filterLabel.setText(PreferenceMessages.NotesViewPreferencePage_filter_clefs + ":"); //$NON-NLS-1$
		GridDataFactory.fillDefaults().applyTo(filterLabel);

		final Composite chiavetteComposite = new Composite(clefGroup, SWT.NONE);
		GridDataFactory.fillDefaults().applyTo(chiavetteComposite);
		final BooleanFieldEditor chiavetteEditor = new BooleanFieldEditor(Preferences.NOTES_VIEW_FILTER_CLEF_CHIAVETTE,
				PreferenceMessages.NotesViewPreferencePage_filter_chiavette, chiavetteComposite);
		addField(chiavetteEditor);

		final Composite notWholeStaffUsedComposite = new Composite(clefGroup, SWT.NONE);
		GridDataFactory.fillDefaults().applyTo(notWholeStaffUsedComposite);
		final MyBooleanFieldEditor notWholeStaffUsedEditor = new MyBooleanFieldEditor(
				Preferences.NOTES_VIEW_FILTER_CLEF_NOT_WHOLE_STAFF_USED,
				PreferenceMessages.NotesViewPreferencePage_filter_not_whole_staff_used, notWholeStaffUsedComposite);
		notWholeStaffUsedButton = notWholeStaffUsedEditor.getButton(notWholeStaffUsedComposite);
		notWholeStaffUsedButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				updateFilterEnablement();
			}
		});
		addField(notWholeStaffUsedEditor);
		updateNotesSpacingEnablement();

		final Composite noNoteOnStaffComposite = new Composite(clefGroup, SWT.NONE);
		GridDataFactory.fillDefaults().applyTo(noNoteOnStaffComposite);
		final MyBooleanFieldEditor noNoteOnStaffEditor = new MyBooleanFieldEditor(
				Preferences.NOTES_VIEW_FILTER_CLEF_NO_NOTE_ON_STAFF,
				PreferenceMessages.NotesViewPreferencePage_filter_no_note_on_staff, noNoteOnStaffComposite);
		noNoteOnStaffButton = noNoteOnStaffEditor.getButton(noNoteOnStaffComposite);
		addField(noNoteOnStaffEditor);
		updateFilterEnablement();

		// fallback clef
		final Composite fallbackClefComposite = new Composite(clefGroup, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(2).applyTo(fallbackClefComposite);

		final Label fallbackClefLabel = new Label(fallbackClefComposite, SWT.NONE);
		fallbackClefLabel.setText(PreferenceMessages.NotesViewPreferencePage_fallback_clef + ":"); //$NON-NLS-1$

		final Clef clef = Clef.valueOf(prefs.getString(Preferences.NOTES_VIEW_FALLBACK_CLEF));
		fallbackClefComboViewer = new ComboViewer(fallbackClefComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
		fallbackClefComboViewer.setContentProvider(new ArrayContentProvider());
		fallbackClefComboViewer.setInput(Clef.values());
		fallbackClefComboViewer.setSelection(new StructuredSelection(clef));
		fallbackClefComboViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(final Object element) {
				return element instanceof Clef ? ((Clef) element).getName() : super.getText(element);
			}
		});
		fallbackClefComboViewer.addFilter(new ViewerFilter() {
			@Override
			public boolean select(final Viewer viewer, final Object parentElement, final Object element) {
				return element != Clef.NONE;
			}
		});
		GridDataFactory.fillDefaults().grab(true, false).applyTo(fallbackClefComboViewer.getControl());

		// show only chord blocks
		final BooleanFieldEditor showOnlyChordBlocksEditor = new BooleanFieldEditor(
				Preferences.NOTES_VIEW_SHOW_ONLY_CHORD_BLOCKS,
				PreferenceMessages.NotesViewPreferencePage_show_only_chord_blocks, getFieldEditorParent());
		addField(showOnlyChordBlocksEditor);

		// show only chord blocks
		final BooleanFieldEditor showOnlyScaleBlocksEditor = new BooleanFieldEditor(
				Preferences.NOTES_VIEW_SHOW_ONLY_SCALE_BLOCKS,
				PreferenceMessages.NotesViewPreferencePage_show_only_scale_blocks, getFieldEditorParent());
		addField(showOnlyScaleBlocksEditor);

		// set context-sensitive help
		Activator.getDefault().setHelp(getControl(), HELP_ID);
	}

	private void updateNotesSpacingEnablement() {
		flexibleNotesSpacingButton.setEnabled(useMaxWidthButton.getSelection());
	}

	private void updateFilterEnablement() {
		noNoteOnStaffButton.setEnabled(!notWholeStaffUsedButton.getSelection());
		if (notWholeStaffUsedButton.getSelection()) {
			noNoteOnStaffButton.setSelection(true);
		}
	}

	@Override
	public boolean performOk() {

		prefs.setValue(Preferences.NOTES_VIEW_OPEN_NOTE_REPRESENTATION, openNoteButton.getSelection());

		final ISelection selection = fallbackClefComboViewer.getSelection();
		if (selection instanceof IStructuredSelection && !selection.isEmpty()) {
			final Object first = ((IStructuredSelection) selection).getFirstElement();
			if (first instanceof Clef) {
				prefs.setValue(Preferences.NOTES_VIEW_FALLBACK_CLEF, ((Clef) first).toString());
			}
		}

		return super.performOk();
	}

	@Override
	protected void performDefaults() {
		super.performDefaults();

		final boolean openNotes = prefs.getDefaultBoolean(Preferences.NOTES_VIEW_OPEN_NOTE_REPRESENTATION);
		openNoteButton.setSelection(openNotes);
		closedNoteButton.setSelection(!openNotes);

		fallbackClefComboViewer.setSelection(new StructuredSelection(Clef.valueOf(prefs
				.getDefaultString(Preferences.NOTES_VIEW_FALLBACK_CLEF))));

		updateNotesSpacingEnablement();
		updateFilterEnablement();
	}
}
