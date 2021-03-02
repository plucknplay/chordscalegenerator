/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.preferencePages;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.dialogs.PreferenceLinkArea;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;

public final class PreferenceLinkUtil {

	private PreferenceLinkUtil() {
	}

	/* --- common link --- */

	static PreferenceLinkArea createLink(final Composite parent, final IWorkbenchPreferenceContainer container,
			final String message, final String id) {
		final PreferenceLinkArea linkArea = new PreferenceLinkArea(parent, SWT.NONE, id, message, container, null);
		linkArea.getControl().setBackground(getBackgroundColor());
		return linkArea;
	}

	static PreferenceLinkArea createViewsLink(final Composite parent, final IWorkbenchPreferenceContainer container,
			final boolean onlyInfo) {
		return createLink(parent, container,
				onlyInfo ? PreferenceMessages.ViewPreferencePage_see_views_preference_page_info
						: PreferenceMessages.ViewPreferencePage_see_views_preference_page_info_search_mode,
				ViewsPreferencePage.ID);
	}

	/* --- fingering notation link --- */

	static PreferenceLinkArea createFingeringNotationLink(final Composite parent,
			final IWorkbenchPreferenceContainer container) {
		return createLink(parent, container,
				PreferenceMessages.ViewPreferencePage_see_fingering_notation_preference_page,
				FingeringNotationPreferencePage.ID);
	}

	/* --- note names link --- */

	static PreferenceLinkArea createNoteNamesLink(final Composite parent, final IWorkbenchPreferenceContainer container) {
		return createLink(parent, container, PreferenceMessages.ViewPreferencePage_see_note_names_preference_page,
				NoteNamesPreferencePage.ID);
	}

	/* --- interval names link --- */

	static PreferenceLinkArea createIntervalNamesLink(final Composite parent,
			final IWorkbenchPreferenceContainer container) {
		return createLink(parent, container, PreferenceMessages.ViewPreferencePage_see_interval_names_preference_page,
				IntervalNamesPreferencePage.ID);
	}

	/* --- note and interval names link --- */

	static Composite createNotesAndIntervalNamesLink(final Composite parent,
			final IWorkbenchPreferenceContainer container) {

		final Composite linkComposite = new Composite(parent, SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(linkComposite);
		linkComposite.setLayout(createRowLayout());
		linkComposite.setBackground(getBackgroundColor());

		createLink(linkComposite, container,
				PreferenceMessages.ViewPreferencePage_see_note_and_interval_names_preference_page_1,
				NoteNamesPreferencePage.ID);

		createLink(linkComposite, container,
				PreferenceMessages.ViewPreferencePage_see_note_and_interval_names_preference_page_2,
				IntervalNamesPreferencePage.ID);

		return linkComposite;
	}

	/* --- fingering notation, note names and interval names link --- */

	static Composite createFingeringNotesAndIntervalNamesLink(final Composite parent,
			final IWorkbenchPreferenceContainer container) {

		final Composite linkComposite = new Composite(parent, SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(linkComposite);
		linkComposite.setLayout(createRowLayout());
		linkComposite.setBackground(getBackgroundColor());

		createLink(linkComposite, container,
				PreferenceMessages.ViewPreferencePage_see_fingering_note_and_interval_names_preference_page_1,
				FingeringNotationPreferencePage.ID);

		createLink(linkComposite, container,
				PreferenceMessages.ViewPreferencePage_see_fingering_note_and_interval_names_preference_page_2,
				NoteNamesPreferencePage.ID);

		createLink(linkComposite, container,
				PreferenceMessages.ViewPreferencePage_see_fingering_note_and_interval_names_preference_page_3,
				IntervalNamesPreferencePage.ID);

		return linkComposite;
	}

	/* --- chord names link --- */

	static PreferenceLinkArea createChordNamesLink(final Composite parent, final IWorkbenchPreferenceContainer container) {
		return createLink(parent, container, PreferenceMessages.ViewPreferencePage_see_chord_names_preference_page,
				ChordNamesPreferencePage.ID);
	}

	/* --- scale names link --- */

	static PreferenceLinkArea createScaleNamesLink(final Composite parent, final IWorkbenchPreferenceContainer container) {
		return createLink(parent, container, PreferenceMessages.ViewPreferencePage_see_scale_names_preference_page,
				ScaleNamesPreferencePage.ID);
	}

	/* --- chord and scale names link --- */

	static Composite createChordAndScaleNamesLink(final Composite parent, final IWorkbenchPreferenceContainer container) {

		final Composite linkComposite = createLinkComposite(parent);

		createLink(linkComposite, container,
				PreferenceMessages.ViewPreferencePage_see_chord_and_scale_names_preference_page_1,
				ChordNamesPreferencePage.ID);

		createLink(linkComposite, container,
				PreferenceMessages.ViewPreferencePage_see_chord_and_scale_names_preference_page_2,
				ScaleNamesPreferencePage.ID);

		return linkComposite;
	}

	static Composite createMainLinkComposite(final Composite parent) {
		return createMainLinkComposite(parent, 1, 15);
	}

	static Composite createMainLinkComposite(final Composite parent, final int hSpan, final int marginBottom) {
		final Composite outerLinkComposite = new Composite(parent, SWT.NONE);
		GridLayoutFactory.fillDefaults().spacing(0, 0).margins(3, 0).extendedMargins(0, 0, 0, marginBottom)
				.applyTo(outerLinkComposite);
		GridDataFactory.fillDefaults().grab(true, false).span(hSpan, 1).applyTo(outerLinkComposite);

		final Composite linkComposite = new Composite(outerLinkComposite, SWT.NONE);
		linkComposite.setBackground(PreferenceLinkUtil.getBackgroundColor());
		GridLayoutFactory.fillDefaults().spacing(2, 2).margins(5, 5).applyTo(linkComposite);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(linkComposite);

		return linkComposite;
	}

	private static Color getBackgroundColor() {
		return Display.getCurrent().getSystemColor(SWT.COLOR_WHITE);
	}

	private static Composite createLinkComposite(final Composite parent) {
		final Composite linkComposite = new Composite(parent, SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(linkComposite);
		linkComposite.setLayout(createRowLayout());
		linkComposite.setBackground(getBackgroundColor());
		return linkComposite;
	}

	private static RowLayout createRowLayout() {
		final RowLayout rowLayout = new RowLayout(SWT.HORIZONTAL);
		rowLayout.spacing = 0;
		rowLayout.wrap = false;
		rowLayout.marginLeft = 0;
		rowLayout.marginRight = 0;
		rowLayout.marginTop = 0;
		rowLayout.marginBottom = 0;
		return rowLayout;
	}
}
