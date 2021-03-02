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
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveRegistry;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.UIConstants;
import com.plucknplay.csg.ui.util.DefaultCollectionContentProvider;

public class PerspectivesPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	public static final String ID = "com.plucknplay.csg.ui.perspectivesPreferences"; //$NON-NLS-1$
	public static final String HELP_ID = "perspectives_preference_page_context"; //$NON-NLS-1$

	private static final String NO_BINDING = PreferenceMessages.PerspectivesPreferencePage_no_perspective_binding;

	private IPreferenceStore prefs;
	private IPerspectiveRegistry registry;
	private List<Object> registeredPerspectives;

	private ComboViewer elementEditingCombo;
	private ComboViewer chordGenerationCombo;
	private ComboViewer findChordCombo;
	private ComboViewer findScaleCombo;

	private Button clearButton;

	@Override
	public void init(final IWorkbench workbench) {
		prefs = Activator.getDefault().getPreferenceStore();

		registry = workbench.getPerspectiveRegistry();
		registeredPerspectives = new ArrayList<Object>();
		final IPerspectiveDescriptor[] perspectives = registry.getPerspectives();
		for (final IPerspectiveDescriptor perspective : perspectives) {
			registeredPerspectives.add(perspective);
		}
		registeredPerspectives.add(NO_BINDING);
	}

	@Override
	protected Control createContents(final Composite parent) {

		// main composite
		final Composite mainComposite = new Composite(parent, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(1).equalWidth(false).applyTo(mainComposite);

		final Group bindingGroup = new Group(mainComposite, SWT.NONE);
		bindingGroup.setText(PreferenceMessages.PerspectivesPreferencePage_bind_actions_to_perspective);
		GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).margins(5, 5).applyTo(bindingGroup);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(bindingGroup);

		elementEditingCombo = addBindingRow(bindingGroup,
				PreferenceMessages.PerspectivesPreferencePage_element_editing,
				Preferences.PERSPECTIVES_BINDING_ELEMENT_EDITING);
		chordGenerationCombo = addBindingRow(bindingGroup,
				PreferenceMessages.PerspectivesPreferencePage_chord_generation,
				Preferences.PERSPECTIVES_BINDING_CHORD_GENERATION);
		findChordCombo = addBindingRow(bindingGroup, PreferenceMessages.PerspectivesPreferencePage_find_chord,
				Preferences.PERSPECTIVES_BINDING_FIND_CHORDS);
		findScaleCombo = addBindingRow(bindingGroup, PreferenceMessages.PerspectivesPreferencePage_find_scale,
				Preferences.PERSPECTIVES_BINDING_FIND_SCALES);

		clearButton = new Button(mainComposite, SWT.CHECK);
		clearButton.setText(PreferenceMessages.PerspectivesPreferencePage_clear_selection);
		clearButton.setSelection(prefs.getBoolean(Preferences.PERSPECTIVES_CLEAR_SELECTION));

		// set context-sensitive help
		Activator.getDefault().setHelp(getControl(), HELP_ID);

		return mainComposite;
	}

	private ComboViewer addBindingRow(final Composite parent, final String text, final String prefId) {

		// create label
		final Label label = new Label(parent, SWT.NONE);
		label.setText(text);

		// create combo viewer
		final ComboViewer combo = new ComboViewer(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
		combo.getCombo().setVisibleItemCount(7);
		combo.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(final Object element) {
				if (element instanceof IPerspectiveDescriptor) {
					return ((IPerspectiveDescriptor) element).getLabel();
				}
				return super.getText(element);
			}
		});
		combo.setContentProvider(new DefaultCollectionContentProvider());
		combo.setSorter(new ViewerSorter() {
			@Override
			public int compare(final Viewer viewer, final Object e1, final Object e2) {
				if (NO_BINDING.equals(e1)) {
					return -1;
				}
				if (NO_BINDING.equals(e2)) {
					return 1;
				}
				return super.compare(viewer, e1, e2);
			}
		});

		// set input and selection
		combo.setInput(registeredPerspectives);
		final IStructuredSelection sel = (IStructuredSelection) getSelection(prefs.getString(prefId));
		final IStructuredSelection defaultSel = (IStructuredSelection) getSelection(prefs.getDefaultString(prefId));
		combo.setSelection(sel == null ? defaultSel : sel);

		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(combo.getControl());

		return combo;
	}

	/* --- perform methods --- */

	@Override
	public boolean performOk() {
		prefs.setValue(Preferences.PERSPECTIVES_BINDING_ELEMENT_EDITING, getId(elementEditingCombo));
		prefs.setValue(Preferences.PERSPECTIVES_BINDING_CHORD_GENERATION, getId(chordGenerationCombo));
		prefs.setValue(Preferences.PERSPECTIVES_BINDING_FIND_CHORDS, getId(findChordCombo));
		prefs.setValue(Preferences.PERSPECTIVES_BINDING_FIND_SCALES, getId(findScaleCombo));
		prefs.setValue(Preferences.PERSPECTIVES_CLEAR_SELECTION, clearButton.getSelection());
		return super.performOk();
	}

	private String getId(final ComboViewer combo) {
		final ISelection selection = combo.getSelection();
		if (selection instanceof IStructuredSelection) {
			final Object first = ((IStructuredSelection) selection).getFirstElement();
			if (first == NO_BINDING) {
				return UIConstants.NO_PERSPECTIVES_BINDING;
			} else if (first instanceof IPerspectiveDescriptor) {
				return ((IPerspectiveDescriptor) first).getId();
			}
		}
		return UIConstants.NO_PERSPECTIVES_BINDING;
	}

	@Override
	protected void performDefaults() {
		elementEditingCombo.setSelection(getSelection(prefs
				.getDefaultString(Preferences.PERSPECTIVES_BINDING_ELEMENT_EDITING)));
		chordGenerationCombo.setSelection(getSelection(prefs
				.getDefaultString(Preferences.PERSPECTIVES_BINDING_CHORD_GENERATION)));
		findChordCombo.setSelection(getSelection(prefs.getDefaultString(Preferences.PERSPECTIVES_BINDING_FIND_CHORDS)));
		findScaleCombo.setSelection(getSelection(prefs.getDefaultString(Preferences.PERSPECTIVES_BINDING_FIND_SCALES)));
		clearButton.setSelection(prefs.getDefaultBoolean(Preferences.PERSPECTIVES_CLEAR_SELECTION));
	}

	private ISelection getSelection(final String id) {
		if (id.equals(UIConstants.NO_PERSPECTIVES_BINDING)) {
			return new StructuredSelection(NO_BINDING);
		} else {
			final IPerspectiveDescriptor perspective = registry.findPerspectiveWithId(id);
			if (perspective == null) {
				return null;
			}
			return new StructuredSelection(perspective);
		}
	}
}
