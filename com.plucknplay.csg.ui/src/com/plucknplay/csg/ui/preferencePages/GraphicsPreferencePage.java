/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.preferencePages;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Group;

import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.Preferences;

public class GraphicsPreferencePage extends AbstractPreferencePage {

	public static final String ID = "com.plucknplay.csg.ui.graphicsPreferences"; //$NON-NLS-1$
	public static final String HELP_ID = "graphics_preference_page_context"; //$NON-NLS-1$

	@Override
	protected void createFieldEditors() {

		// create anti-aliasing group
		final Group group = new Group(getFieldEditorParent(), SWT.NONE);
		group.setText(PreferenceMessages.GraphicsPreferencePage_anti_aliasing);
		GridDataFactory.fillDefaults().applyTo(group);

		// create field editors
		final RadioGroupFieldEditor textAntialiasingEditor = new RadioGroupFieldEditor(
				Preferences.GRAPHICS_TEXT_ANTI_ALIASING_MODE, PreferenceMessages.GraphicsPreferencePage_text_elements,
				3, new String[][] { { PreferenceMessages.GraphicsPreferencePage_default, SWT.DEFAULT + "" }, //$NON-NLS-1$
						{ PreferenceMessages.GraphicsPreferencePage_off, SWT.OFF + "" }, //$NON-NLS-1$
						{ PreferenceMessages.GraphicsPreferencePage_on, SWT.ON + "" } }, group, false); //$NON-NLS-1$
		GridDataFactory.fillDefaults().indent(5, 5).applyTo(textAntialiasingEditor.getLabelControl(group));
		GridDataFactory.fillDefaults().indent(5, 0).applyTo(textAntialiasingEditor.getRadioBoxControl(group));
		addField(textAntialiasingEditor);

		final RadioGroupFieldEditor antialiasingEditor = new RadioGroupFieldEditor(
				Preferences.GRAPHICS_ANTI_ALIASING_MODE, PreferenceMessages.GraphicsPreferencePage_non_text_elements,
				3, new String[][] { { PreferenceMessages.GraphicsPreferencePage_default, SWT.DEFAULT + "" }, //$NON-NLS-1$
						{ PreferenceMessages.GraphicsPreferencePage_off, SWT.OFF + "" }, //$NON-NLS-1$
						{ PreferenceMessages.GraphicsPreferencePage_on, SWT.ON + "" } }, group, false); //$NON-NLS-1$
		GridDataFactory.fillDefaults().indent(5, 10).applyTo(antialiasingEditor.getLabelControl(group));
		GridDataFactory.fillDefaults().indent(5, 0).applyTo(antialiasingEditor.getRadioBoxControl(group));
		addField(antialiasingEditor);

		final RadioGroupFieldEditor interpolationEditor = new RadioGroupFieldEditor(
				Preferences.GRAPHICS_INTERPOLATION_MODE, PreferenceMessages.GraphicsPreferencePage_interpolation, 4,
				new String[][] { { PreferenceMessages.GraphicsPreferencePage_default, SWT.DEFAULT + "" }, //$NON-NLS-1$
						{ PreferenceMessages.GraphicsPreferencePage_off, SWT.OFF + "" }, //$NON-NLS-1$
						{ PreferenceMessages.GraphicsPreferencePage_low, SWT.LOW + "" }, //$NON-NLS-1$
						{ PreferenceMessages.GraphicsPreferencePage_high, SWT.HIGH + "" } }, //$NON-NLS-1$
				getFieldEditorParent(), true);
		addField(interpolationEditor);

		// finally fix layout problems (bug #88)
		interpolationEditor.getRadioBoxControl(getFieldEditorParent()).setFont(group.getFont());
		// textAntialiasingEditor.getRadioBoxControl(group).setFont(group.getFont());
		// antialiasingEditor.getRadioBoxControl(group).setFont(group.getFont());

		// set context-sensitive help
		Activator.getDefault().setHelp(getControl(), HELP_ID);
	}
}
