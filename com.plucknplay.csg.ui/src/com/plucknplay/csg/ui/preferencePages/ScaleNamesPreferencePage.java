/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.preferencePages;

import java.util.List;

import com.plucknplay.csg.core.model.sets.ScaleList;
import com.plucknplay.csg.ui.Preferences;

public class ScaleNamesPreferencePage extends AbstractNamesPreferencePage {

	public static final String ID = "com.plucknplay.csg.ui.scaleNamesPreferences"; //$NON-NLS-1$
	public static final String HELP_ID = "scale_names_preference_page_context"; //$NON-NLS-1$

	@Override
	protected String getUseSeparatorString() {
		return PreferenceMessages.ScaleNamePreferencePage_use_separator;
	}

	@Override
	protected String getUseSeparatorPreferenceId() {
		return Preferences.SCALE_NAMES_USE_SEPARATOR;
	}

	@Override
	protected String getSeparatorPreferenceId() {
		return Preferences.SCALE_NAMES_SEPARATOR;
	}

	@Override
	protected String getHelpContext() {
		return HELP_ID;
	}

	@Override
	protected void setUseSeparator(final boolean useSeparator) {
		getNameProvider().setUseScaleNameSeparator(useSeparator);
	}

	@Override
	protected void setSeparator(final String separator) {
		getNameProvider().setScaleNameSeparatorMode(separator);
	}

	@Override
	protected void addExamples(final List<Object> examples) {
		final boolean scalesExist = !ScaleList.getInstance().getRootCategory().getAllElements().isEmpty();
		if (scalesExist) {
			if (scalesExist) {
				examples.add(ExampleCreator.createRandomScale());
				examples.add(ExampleCreator.createRandomScale());
				examples.add(ExampleCreator.createRandomScale());
				examples.add(ExampleCreator.createRandomScale());
				examples.add(ExampleCreator.createRandomScale());
			}
			getRefreshExamplesButton().setEnabled(true);
		} else {
			showExampleErrorMessage(PreferenceMessages.ScaleNamesPreferencePage_no_scales_exist);
		}
	}

	@Override
	protected void performDefaults() {
		super.performDefaults();
		updateExamples();
	}
}
