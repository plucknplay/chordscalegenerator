/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.preferencePages;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.PreferenceLinkArea;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;

import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.IImageKeys;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.UIConstants;
import com.plucknplay.csg.ui.figures.IFigureConstants;
import com.plucknplay.csg.ui.util.MyBooleanFieldEditor;
import com.plucknplay.csg.ui.util.TabFolderLayout;
import com.plucknplay.csg.ui.util.enums.BackgroundColorMode;
import com.plucknplay.csg.ui.util.enums.BarreMode;
import com.plucknplay.csg.ui.util.enums.Position;

public class FretboardViewPreferencePage extends AbstractPreferencePage {

	public static final String ID = "com.plucknplay.csg.ui.views.fretboardViewPreferences"; //$NON-NLS-1$
	public static final String HELP_ID = "fretboard_view_preference_page_context"; //$NON-NLS-1$

	private static final String BACKGROUND_MODE = "background.mode";

	private IPreferenceStore prefs;

	private int index;

	private Group backgroundGroup;
	private Button noBackgroundButton;
	private Button backgroundColorButton;
	private Button backgroundImageButton;
	private Button previousButton;
	private Button nextButton;
	private Label previewLabel;

	private Button showFretNumbersButton;
	private Button grayFretNumbersButton;
	private Button reducedFretNumbersButton;
	private Button fretNumbersTopPositionButton;
	private Button fretNumbersBottomPositionButton;
	private Button fretNumbersArabicButton;
	private Button fretNumbersRomanButton;

	private Button showInlaysButton;
	private Button grayInlaysButton;
	private Button inlaysTopPositionButton;
	private Button inlaysBottomPositionButton;
	private Button inlaysCenterPositionButton;
	private Button circleInlaysButton;
	private Button triangleInlaysButton;

	private Button showMutedStringsButton;
	private Button highlightRootNoteButton;
	private Button highlightRootNoteWithColorButton;
	private Button highlightRootNoteWithShapeButton;

	private Button whitePointsButton;
	private Button blackPointsButton;

	private Button frameFingeringButton;
	private Button whiteFingeringButton;
	private Button blackFingeringButton;
	private Button coloredFingeringButton;
	private Button whiteEmptyStringsButton;

	private Button drawBarreButton;
	private Button barreAsLineButton;
	private Button barreAsArcButton;
	private Button barreAsBarButton;
	private Combo lineWidthCombo;
	private Button sameColorBarButton;
	private Button whiteBarButton;
	private Button blackBarButton;
	private Button showInsideBarButton;
	private Button singleFingeringInBarButton;

	private Button allNotesOnEmptyFretboardButton;
	private Button allNotesForGriptablesButton;
	private Button allNotesForChordsScalesButton;
	private Button allNotesForBlocksButton;
	private Button additionalChordNotesButton;
	private Button additionalBlockNotesButton;
	private Button additionalNotesInBlackButton;
	private Button overlayButton;
	private Button outlineButton;
	private Button onlyBlockButton;

	@Override
	public void init(final IWorkbench workbench) {
		prefs = Activator.getDefault().getPreferenceStore();
	}

	@Override
	public void createControl(final Composite parent) {
		super.createControl(parent);

		// add listener and update enablement
		addListener();
		updateEnablement();
		updateAdditionalNotesButtons();
	}

	@Override
	protected void createFieldEditors() {

		// preference page links
		final Composite linkComposite = PreferenceLinkUtil.createMainLinkComposite(getFieldEditorParent());
		PreferenceLinkUtil.createViewsLink(linkComposite, (IWorkbenchPreferenceContainer) getContainer(), false);
		PreferenceLinkUtil.createChordAndScaleNamesLink(linkComposite, (IWorkbenchPreferenceContainer) getContainer());

		// show info
		final Group infoGroup = new Group(getFieldEditorParent(), SWT.NONE);
		infoGroup.setText(PreferenceMessages.FretboardViewPreferencePage_show_info);
		GridLayoutFactory.fillDefaults().applyTo(infoGroup);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(infoGroup);

		final BooleanFieldEditor showBlockNavigationInfoEditor = new BooleanFieldEditor(
				Preferences.FRETBOARD_VIEW_SHOW_BLOCK_NAVIGATION_INFO,
				PreferenceMessages.FretboardViewPreferencePage_show_block_navigation_info, infoGroup);
		addField(showBlockNavigationInfoEditor);
		GridDataFactory.fillDefaults().indent(5, 5)
				.applyTo(showBlockNavigationInfoEditor.getDescriptionControl(infoGroup));

		// tab folder
		final TabFolder folder = new TabFolder(getFieldEditorParent(), SWT.NONE);
		folder.setLayout(new TabFolderLayout());
		GridDataFactory.fillDefaults().span(3, 1).grab(true, false).applyTo(folder);

		final TabItem item1 = new TabItem(folder, SWT.NONE);
		item1.setText(PreferenceMessages.ViewPreferencePage_tab_item_fingering_notes_intervals_blocks);
		item1.setControl(createFingeringNotesIntervalsPage(folder));

		final TabItem item2 = new TabItem(folder, SWT.NONE);
		item2.setText(PreferenceMessages.FretboardViewPreferencePage_tab_item_fretboard_fret_number);
		item2.setControl(createFretboardPage(folder));

		// set context-sensitive help
		Activator.getDefault().setHelp(getControl(), HELP_ID);
	}

	private Control createFingeringNotesIntervalsPage(final Composite parent) {

		final Composite parentComposite = new Composite(parent, SWT.NONE);
		GridLayoutFactory.fillDefaults().extendedMargins(5, 5, 10, 0).applyTo(parentComposite);

		final Composite composite = new Composite(parentComposite, SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(composite);

		// preference page links
		final Composite linkComposite = PreferenceLinkUtil.createMainLinkComposite(composite);
		PreferenceLinkUtil.createFingeringNotesAndIntervalNamesLink(linkComposite,
				(IWorkbenchPreferenceContainer) getContainer());
		PreferenceLinkUtil.createLink(linkComposite, (IWorkbenchPreferenceContainer) getContainer(),
				PreferenceMessages.ViewPreferencePage_see_settings_for_blocks, BlockPreferencePage.ID);

		// show muted strings
		final MyBooleanFieldEditor showMutedStringsEditor = new MyBooleanFieldEditor(
				Preferences.FRETBOARD_VIEW_SHOW_MUTED_STRINGS,
				PreferenceMessages.FretboardViewPreferencePage_show_muted_strings, composite);
		showMutedStringsButton = showMutedStringsEditor.getButton(composite);
		addField(showMutedStringsEditor);

		createRootNoteSection(composite);
		createPointsGroup(composite);
		createFingeringNotesIntervalsGroup(composite);

		// white empty strings background color
		whiteEmptyStringsButton = new Button(composite, SWT.CHECK);
		whiteEmptyStringsButton.setText(PreferenceMessages.ViewPreferencePage_white_empty_strings_background);
		whiteEmptyStringsButton.setSelection(prefs
				.getBoolean(Preferences.FRETBOARD_VIEW_EMPTY_STRINGS_BACKGROUND_WHITE));
		GridDataFactory.fillDefaults().grab(true, false).applyTo(whiteEmptyStringsButton);

		createBarreGroup(composite);
		createAdditionNotesGroup(composite);
		createBlockGroup(composite);
		createCopyFromBoxButton(composite);

		return parentComposite;
	}

	private void createRootNoteSection(final Composite parent) {

		// highlight root note
		final MyBooleanFieldEditor highlightRootNoteEditor = new MyBooleanFieldEditor(
				Preferences.FRETBOARD_VIEW_HIGHLIGHT_ROOT_NOTE,
				PreferenceMessages.ViewPreferencePage_highlight_root_note, parent);
		highlightRootNoteButton = highlightRootNoteEditor.getButton(parent);
		addField(highlightRootNoteEditor);

		// root note composite
		final Composite rootNoteComposite = new Composite(parent, SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, false).indent(HORIZONTAL_INDENT, 0).applyTo(rootNoteComposite);
		GridLayoutFactory.fillDefaults().numColumns(2).margins(5, 0).extendedMargins(0, 0, 0, 10)
				.applyTo(rootNoteComposite);

		// opposite background color
		highlightRootNoteWithColorButton = new Button(rootNoteComposite, SWT.CHECK);
		highlightRootNoteWithColorButton
				.setText(PreferenceMessages.ViewPreferencePage_highlight_root_note_with_background_color);
		highlightRootNoteWithColorButton.setSelection(prefs
				.getBoolean(Preferences.FRETBOARD_VIEW_HIGHLIGHT_ROOT_NOTE_WITH_COLOR));

		// square shape
		highlightRootNoteWithShapeButton = new Button(rootNoteComposite, SWT.CHECK);
		highlightRootNoteWithShapeButton.setText(PreferenceMessages.ViewPreferencePage_highlight_root_note_with_shape);
		highlightRootNoteWithShapeButton.setSelection(prefs
				.getBoolean(Preferences.FRETBOARD_VIEW_HIGHLIGHT_ROOT_NOTE_WITH_SHAPE));
	}

	private void createPointsGroup(final Composite parent) {

		// points group
		final Group pointSizeGroup = new Group(parent, SWT.NONE);
		pointSizeGroup.setText(PreferenceMessages.PreferencePage_points_settings);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(pointSizeGroup);
		GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).margins(5, 5).applyTo(pointSizeGroup);

		// point background color
		final BackgroundColorMode pointBackground = BackgroundColorMode.valueOf(prefs
				.getString(Preferences.FRETBOARD_VIEW_POINTS_BACKGROUND));
		final Label pointBackgroundLabel = new Label(pointSizeGroup, SWT.NONE);
		pointBackgroundLabel.setText(PreferenceMessages.ViewPreferencePage_background_color + ":"); //$NON-NLS-1$
		GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.TOP).applyTo(pointBackgroundLabel);
		final Composite pointBackgroundComposite = new Composite(pointSizeGroup, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).applyTo(pointBackgroundComposite);
		whitePointsButton = new Button(pointBackgroundComposite, SWT.RADIO);
		whitePointsButton.setText(PreferenceMessages.ViewPreferencePage_white);
		whitePointsButton.setSelection(pointBackground != BackgroundColorMode.BLACK);
		blackPointsButton = new Button(pointBackgroundComposite, SWT.RADIO);
		blackPointsButton.setText(PreferenceMessages.ViewPreferencePage_black);
		blackPointsButton.setSelection(pointBackground == BackgroundColorMode.BLACK);
		pointBackgroundLabel.setFont(whitePointsButton.getFont());
	}

	private void createFingeringNotesIntervalsGroup(final Composite parent) {

		// fingering/notes/intervals group
		final Group fingeringGroup = new Group(parent, SWT.NONE);
		fingeringGroup.setText(PreferenceMessages.ViewPreferencePage_fingering_notes_intervals_settings);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(fingeringGroup);
		GridLayoutFactory.fillDefaults().numColumns(2).margins(5, 5).applyTo(fingeringGroup);

		// frame elements
		frameFingeringButton = new Button(fingeringGroup, SWT.CHECK);
		frameFingeringButton.setText(PreferenceMessages.FretboardViewPreferencePage_frame_fingering_notes_intervals);
		frameFingeringButton.setSelection(prefs.getBoolean(Preferences.FRETBOARD_VIEW_FRAME_FINGERING));
		GridDataFactory.fillDefaults().span(2, 1).grab(true, false).applyTo(frameFingeringButton);

		// fingering background color
		final BackgroundColorMode fingeringBackground = BackgroundColorMode.valueOf(prefs
				.getString(Preferences.FRETBOARD_VIEW_FINGERING_BACKGROUND));
		final Label fingeringBackgroundLabel = new Label(fingeringGroup, SWT.RIGHT);
		fingeringBackgroundLabel.setText(PreferenceMessages.ViewPreferencePage_background_color + ":"); //$NON-NLS-1$
		fingeringBackgroundLabel.setFont(frameFingeringButton.getFont());
		GridDataFactory.fillDefaults().align(SWT.END, SWT.CENTER).applyTo(fingeringBackgroundLabel);
		final Composite fingeringBackgroundComposite = new Composite(fingeringGroup, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(3).equalWidth(false).applyTo(fingeringBackgroundComposite);
		whiteFingeringButton = new Button(fingeringBackgroundComposite, SWT.RADIO);
		whiteFingeringButton.setText(PreferenceMessages.ViewPreferencePage_white);
		whiteFingeringButton.setSelection(fingeringBackground == BackgroundColorMode.WHITE);
		blackFingeringButton = new Button(fingeringBackgroundComposite, SWT.RADIO);
		blackFingeringButton.setText(PreferenceMessages.ViewPreferencePage_black);
		blackFingeringButton.setSelection(fingeringBackground == BackgroundColorMode.BLACK);
		coloredFingeringButton = new Button(fingeringBackgroundComposite, SWT.RADIO);
		coloredFingeringButton.setText(PreferenceMessages.ViewPreferencePage_colored);
		coloredFingeringButton.setSelection(fingeringBackground == BackgroundColorMode.COLORED);
	}

	private void createBarreGroup(final Composite parent) {

		// barre group
		final Group barreGroup = new Group(parent, SWT.NONE);
		barreGroup.setText(PreferenceMessages.ViewPreferencePage_barre_settings);
		GridDataFactory.fillDefaults().indent(0, 10).grab(true, false).applyTo(barreGroup);
		GridLayoutFactory.fillDefaults().margins(5, 5).applyTo(barreGroup);

		// draw barre
		drawBarreButton = new Button(barreGroup, SWT.CHECK);
		drawBarreButton.setText(PreferenceMessages.ViewPreferencePage_draw_barre);
		drawBarreButton.setSelection(prefs.getBoolean(Preferences.FRETBOARD_VIEW_SHOW_BARRE));

		// barre mode
		final BarreMode barreMode = BarreMode.valueOf(prefs.getString(Preferences.FRETBOARD_VIEW_BARRE_MODE));
		final Composite barreComposite = new Composite(barreGroup, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(3).applyTo(barreComposite);
		barreAsLineButton = new Button(barreComposite, SWT.RADIO);
		barreAsLineButton.setText(PreferenceMessages.ViewPreferencePage_line);
		barreAsLineButton.setSelection(barreMode == BarreMode.LINE);
		GridDataFactory.fillDefaults().indent(HORIZONTAL_INDENT, 0).applyTo(barreAsLineButton);
		barreAsArcButton = new Button(barreComposite, SWT.RADIO);
		barreAsArcButton.setText(PreferenceMessages.ViewPreferencePage_arc);
		barreAsArcButton.setSelection(barreMode == BarreMode.ARC);
		barreAsBarButton = new Button(barreComposite, SWT.RADIO);
		barreAsBarButton.setText(PreferenceMessages.ViewPreferencePage_bar);
		barreAsBarButton.setSelection(barreMode == BarreMode.BAR);

		// bar line width and background composite
		final Composite barLineWidthAndBackgroundComposite = new Composite(barreGroup, SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(barLineWidthAndBackgroundComposite);
		GridLayoutFactory.fillDefaults().numColumns(3).applyTo(barLineWidthAndBackgroundComposite);

		// line width
		final Label lineWidthLabel = new Label(barLineWidthAndBackgroundComposite, SWT.NONE);
		lineWidthLabel.setText(PreferenceMessages.ViewPreferencePage_line_width + ":"); //$NON-NLS-1$
		GridDataFactory.fillDefaults().indent(HORIZONTAL_INDENT * 2, 0).align(SWT.RIGHT, SWT.CENTER)
				.applyTo(lineWidthLabel);
		lineWidthCombo = new Combo(barLineWidthAndBackgroundComposite, SWT.READ_ONLY | SWT.DROP_DOWN);
		lineWidthCombo.setItems(new String[] { "1", "2", "3", "4", "5" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		lineWidthCombo.select(prefs.getInt(Preferences.FRETBOARD_VIEW_BARRE_LINE_WIDTH) - 1);
		new Label(barLineWidthAndBackgroundComposite, SWT.NONE);
		lineWidthLabel.setFont(lineWidthCombo.getFont());

		// bar background color
		final BackgroundColorMode barBackground = BackgroundColorMode.valueOf(prefs
				.getString(Preferences.FRETBOARD_VIEW_BARRE_BAR_BACKGROUND));
		final Label barBackgroundLabel = new Label(barLineWidthAndBackgroundComposite, SWT.NONE);
		barBackgroundLabel.setText(PreferenceMessages.ViewPreferencePage_background_color + ":"); //$NON-NLS-1$
		GridDataFactory.fillDefaults().indent(HORIZONTAL_INDENT * 2, 0).align(SWT.RIGHT, SWT.TOP)
				.applyTo(barBackgroundLabel);
		sameColorBarButton = new Button(barLineWidthAndBackgroundComposite, SWT.RADIO);
		sameColorBarButton.setText(PreferenceMessages.FretboardViewPreferencePage_same_as_fingering_notes_intervals);
		sameColorBarButton.setSelection(barBackground == BackgroundColorMode.SAME);
		GridDataFactory.fillDefaults().span(2, 1).applyTo(sameColorBarButton);
		new Label(barLineWidthAndBackgroundComposite, SWT.NONE);
		whiteBarButton = new Button(barLineWidthAndBackgroundComposite, SWT.RADIO);
		whiteBarButton.setText(PreferenceMessages.ViewPreferencePage_white);
		whiteBarButton.setSelection(barBackground == BackgroundColorMode.WHITE);
		blackBarButton = new Button(barLineWidthAndBackgroundComposite, SWT.RADIO);
		blackBarButton.setText(PreferenceMessages.ViewPreferencePage_black);
		blackBarButton.setSelection(barBackground == BackgroundColorMode.BLACK);
		barBackgroundLabel.setFont(whiteBarButton.getFont());

		// show inside bar
		showInsideBarButton = new Button(barreGroup, SWT.CHECK);
		showInsideBarButton.setText(PreferenceMessages.FretboardViewPreferencePage_show_inside_bar);
		showInsideBarButton.setSelection(prefs.getBoolean(Preferences.FRETBOARD_VIEW_BARRE_BAR_SHOW_ELEMENTS_INSIDE));
		GridDataFactory.fillDefaults().indent(HORIZONTAL_INDENT * 2, 0).grab(true, false).applyTo(showInsideBarButton);

		// encircle fingering in bar
		singleFingeringInBarButton = new Button(barreGroup, SWT.CHECK);
		singleFingeringInBarButton.setText(PreferenceMessages.ViewPreferencePage_single_fingering_in_bar);
		singleFingeringInBarButton.setSelection(prefs
				.getBoolean(Preferences.FRETBOARD_VIEW_BARRE_BAR_SHOW_SINGLE_FINGER_NUMBER));
		GridDataFactory.fillDefaults().indent(HORIZONTAL_INDENT * 3, 0).applyTo(singleFingeringInBarButton);
	}

	private void createAdditionNotesGroup(final Composite parent) {

		// additional notes group
		final Group additionalNotesGroup = new Group(parent, SWT.NONE);
		additionalNotesGroup.setText(PreferenceMessages.ViewPreferencePage_additional_notes_settings);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(additionalNotesGroup);
		GridLayoutFactory.fillDefaults().numColumns(2).margins(5, 5).applyTo(additionalNotesGroup);

		// all notes
		new Label(additionalNotesGroup, SWT.LEFT).setText(PreferenceMessages.ViewPreferencePage_show_additional_notes);
		final Composite additionalNotesComposite = new Composite(additionalNotesGroup, SWT.NONE);
		GridDataFactory.fillDefaults().span(1, 2).grab(true, false).applyTo(additionalNotesComposite);
		GridLayoutFactory.fillDefaults().numColumns(2).applyTo(additionalNotesComposite);

		allNotesOnEmptyFretboardButton = new Button(additionalNotesComposite, SWT.CHECK);
		allNotesOnEmptyFretboardButton
				.setText(PreferenceMessages.FretboardViewPreferencePage_show_additional_notes_on_empty_fretboard);
		allNotesOnEmptyFretboardButton.setSelection(prefs
				.getBoolean(Preferences.FRETBOARD_VIEW_SHOW_ADDITIONAL_NOTES_ON_EMPTY_FRETBOARD));

		allNotesForGriptablesButton = new Button(additionalNotesComposite, SWT.CHECK);
		allNotesForGriptablesButton.setText(PreferenceMessages.ViewPreferencePage_show_additional_notes_for_griptable);
		allNotesForGriptablesButton.setSelection(prefs
				.getBoolean(Preferences.FRETBOARD_VIEW_SHOW_ADDITIONAL_NOTES_FOR_GRIPTABLE));

		allNotesForChordsScalesButton = new Button(additionalNotesComposite, SWT.CHECK);
		allNotesForChordsScalesButton
				.setText(PreferenceMessages.ViewPreferencePage_show_additional_notes_for_chords_scales);
		allNotesForChordsScalesButton.setSelection(prefs
				.getBoolean(Preferences.FRETBOARD_VIEW_SHOW_ADDITIONAL_NOTES_FOR_CHORD_AND_SCALE));

		allNotesForBlocksButton = new Button(additionalNotesComposite, SWT.CHECK);
		allNotesForBlocksButton.setText(PreferenceMessages.ViewPreferencePage_show_additional_notes_for_blocks);
		allNotesForBlocksButton.setSelection(prefs
				.getBoolean(Preferences.FRETBOARD_VIEW_SHOW_ADDITIONAL_NOTES_FOR_BLOCK));

		// show additional chord notes
		additionalChordNotesButton = new Button(additionalNotesGroup, SWT.CHECK);
		additionalChordNotesButton.setText(PreferenceMessages.ViewPreferencePage_show_additional_chord_notes);
		additionalChordNotesButton.setSelection(prefs
				.getBoolean(Preferences.FRETBOARD_VIEW_SHOW_ADDITIONAL_CHORD_NOTES));
		GridDataFactory.fillDefaults().span(2, 1).applyTo(additionalChordNotesButton);

		// show additional block notes
		additionalBlockNotesButton = new Button(additionalNotesGroup, SWT.CHECK);
		additionalBlockNotesButton.setText(PreferenceMessages.ViewPreferencePage_show_additional_block_notes);
		additionalBlockNotesButton.setSelection(prefs
				.getBoolean(Preferences.FRETBOARD_VIEW_SHOW_ADDITIONAL_BLOCK_NOTES));
		GridDataFactory.fillDefaults().span(2, 1).applyTo(additionalBlockNotesButton);

		// show additional notes in black
		additionalNotesInBlackButton = new Button(additionalNotesGroup, SWT.CHECK);
		additionalNotesInBlackButton.setText(PreferenceMessages.ViewPreferencePage_show_additional_notes_in_black);
		additionalNotesInBlackButton.setSelection(prefs
				.getBoolean(Preferences.FRETBOARD_VIEW_SHOW_ADDITIONAL_NOTES_IN_BLACK));
		GridDataFactory.fillDefaults().span(2, 1).applyTo(additionalNotesInBlackButton);
	}

	private void createBlockGroup(final Composite parent) {

		// block presentation group
		final Group blockGroup = new Group(parent, SWT.NONE);
		blockGroup.setText(PreferenceMessages.FretboardViewPreferencePage_block_presentation);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(blockGroup);
		GridLayoutFactory.fillDefaults().numColumns(3).margins(5, 5).applyTo(blockGroup);

		final String blockPresentation = prefs.getString(Preferences.FRETBOARD_VIEW_SHOW_BLOCK_PRESENTATION);
		overlayButton = new Button(blockGroup, SWT.RADIO);
		overlayButton.setText(PreferenceMessages.FretboardViewPreferencePage_block_overlay);
		overlayButton.setSelection(UIConstants.BLOCK_OVERLAY.equals(blockPresentation));
		outlineButton = new Button(blockGroup, SWT.RADIO);
		outlineButton.setText(PreferenceMessages.FretboardViewPreferencePage_block_frame);
		outlineButton.setSelection(UIConstants.BLOCK_OUTLINE.equals(blockPresentation));
		onlyBlockButton = new Button(blockGroup, SWT.RADIO);
		onlyBlockButton.setText(PreferenceMessages.ViewPreferencePage_block_no_overlay_frame);
		onlyBlockButton.setSelection(UIConstants.BLOCK_NO_OVERLAY_FRAME.equals(blockPresentation));
	}

	private void createCopyFromBoxButton(final Composite composite) {

		final Composite copyComposite = new Composite(composite, SWT.NONE);
		GridLayoutFactory.fillDefaults().applyTo(copyComposite);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(copyComposite);

		// copy button
		final Button copyButton = new Button(copyComposite, SWT.PUSH);
		copyButton.setText(PreferenceMessages.FretboardViewPreferencePage_copy_box_settings);
		copyButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {

				// muted strings
				showMutedStringsButton.setSelection(prefs.getBoolean(Preferences.BOX_VIEW_SHOW_MUTED_STRINGS));

				// highlight root note
				highlightRootNoteButton.setSelection(prefs.getBoolean(Preferences.BOX_VIEW_HIGHLIGHT_ROOT_NOTE));
				highlightRootNoteWithColorButton.setSelection(prefs
						.getBoolean(Preferences.BOX_VIEW_HIGHLIGHT_ROOT_NOTE_WITH_COLOR));
				highlightRootNoteWithShapeButton.setSelection(prefs
						.getBoolean(Preferences.BOX_VIEW_HIGHLIGHT_ROOT_NOTE_WITH_SHAPE));

				// points background
				final BackgroundColorMode pointsBgMode = BackgroundColorMode.valueOf(prefs
						.getString(Preferences.BOX_VIEW_POINTS_BACKGROUND));
				whitePointsButton.setSelection(pointsBgMode == BackgroundColorMode.WHITE);
				blackPointsButton.setSelection(pointsBgMode == BackgroundColorMode.BLACK);

				// fingering/notes/intervals
				frameFingeringButton.setSelection(prefs.getBoolean(Preferences.BOX_VIEW_FRAME_INSIDE));
				whiteEmptyStringsButton.setSelection(prefs
						.getBoolean(Preferences.BOX_VIEW_EMPTY_STRINGS_BACKGROUND_WHITE));
				final BackgroundColorMode insideBgMode = BackgroundColorMode.valueOf(prefs
						.getString(Preferences.BOX_VIEW_BACKGROUND_INSIDE));
				whiteFingeringButton.setSelection(insideBgMode == BackgroundColorMode.WHITE);
				blackFingeringButton.setSelection(insideBgMode == BackgroundColorMode.BLACK);
				coloredFingeringButton.setSelection(insideBgMode == BackgroundColorMode.COLORED);

				// barre
				drawBarreButton.setSelection(prefs.getBoolean(Preferences.BOX_VIEW_SHOW_BARRE));
				final BarreMode barreMode = BarreMode.valueOf(prefs.getString(Preferences.BOX_VIEW_BARRE_MODE));
				barreAsLineButton.setSelection(barreMode == BarreMode.LINE);
				barreAsArcButton.setSelection(barreMode == BarreMode.ARC);
				barreAsBarButton.setSelection(barreMode == BarreMode.BAR);
				lineWidthCombo.select(prefs.getInt(Preferences.BOX_VIEW_BARRE_LINE_WIDTH) - 1);
				final BackgroundColorMode barBgMode = BackgroundColorMode.valueOf(prefs
						.getString(Preferences.BOX_VIEW_BARRE_BAR_BACKGROUND));
				sameColorBarButton.setSelection(barBgMode == BackgroundColorMode.SAME);
				whiteBarButton.setSelection(barBgMode == BackgroundColorMode.WHITE);
				blackBarButton.setSelection(barBgMode == BackgroundColorMode.BLACK);
				showInsideBarButton.setSelection(prefs.getBoolean(Preferences.BOX_VIEW_BARRE_BAR_SHOW_ELEMENTS_INSIDE));
				singleFingeringInBarButton.setSelection(prefs
						.getBoolean(Preferences.BOX_VIEW_BARRE_BAR_SHOW_SINGLE_FINGER_NUMBER));

				// update resulting selections and enablements
				updateEnablement();
				updateAdditionalNotesButtons();
			}
		});
		GridDataFactory.fillDefaults().applyTo(copyButton);

		// box view settings link
		final PreferenceLinkArea link = PreferenceLinkUtil.createLink(
				PreferenceLinkUtil.createMainLinkComposite(copyComposite, 1, 5),
				(IWorkbenchPreferenceContainer) getContainer(),
				PreferenceMessages.FretboardViewPreferencePage_see_settings_for_box, BoxViewPreferencePage.ID);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(link.getControl());
	}

	private Control createFretboardPage(final Composite parent) {

		final Composite parentComposite = new Composite(parent, SWT.NONE);
		GridLayoutFactory.fillDefaults().extendedMargins(5, 5, 10, 0).applyTo(parentComposite);

		final Composite composite = new Composite(parentComposite, SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(composite);

		// show empty strings twice
		final BooleanFieldEditor showEmptyStringsTwiceEditor = new BooleanFieldEditor(
				Preferences.FRETBOARD_VIEW_SHOW_EMPTY_STRINGS_TWICE,
				PreferenceMessages.FretboardViewPreferencePage_show_empty_strings_twice, composite);
		addField(showEmptyStringsTwiceEditor);

		// frame empty strings
		final BooleanFieldEditor showEmptyStringsFrameEditor = new BooleanFieldEditor(
				Preferences.FRETBOARD_VIEW_SHOW_EMPTY_STRINGS_FRAME,
				PreferenceMessages.FretboardViewPreferencePage_show_empty_strings_frame, composite);
		addField(showEmptyStringsFrameEditor);

		createFretboardBackgroundGroup(composite);
		createInlaysGroup(composite);
		createFretNumbersGroup(composite);

		final SelectionAdapter positionListener = new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				if (e.getSource() == fretNumbersTopPositionButton) {
					final boolean selected = fretNumbersTopPositionButton.getSelection();
					if (selected && inlaysTopPositionButton.getSelection()) {
						inlaysTopPositionButton.setSelection(!selected);
						inlaysBottomPositionButton.setSelection(selected);
					}
				} else if (e.getSource() == fretNumbersBottomPositionButton) {
					final boolean selected = fretNumbersBottomPositionButton.getSelection();
					if (selected && inlaysBottomPositionButton.getSelection()) {
						inlaysTopPositionButton.setSelection(selected);
						inlaysBottomPositionButton.setSelection(!selected);
					}
				} else if (e.getSource() == inlaysTopPositionButton) {
					final boolean selected = inlaysTopPositionButton.getSelection();
					if (selected && fretNumbersTopPositionButton.getSelection()) {
						fretNumbersTopPositionButton.setSelection(!selected);
						fretNumbersBottomPositionButton.setSelection(selected);
					}
				} else if (e.getSource() == inlaysBottomPositionButton) {
					final boolean selected = inlaysBottomPositionButton.getSelection();
					if (selected && fretNumbersBottomPositionButton.getSelection()) {
						fretNumbersTopPositionButton.setSelection(selected);
						fretNumbersBottomPositionButton.setSelection(!selected);
					}
				}
				updateInlayModeButtons();
			}
		};
		fretNumbersTopPositionButton.addSelectionListener(positionListener);
		fretNumbersBottomPositionButton.addSelectionListener(positionListener);
		inlaysTopPositionButton.addSelectionListener(positionListener);
		inlaysBottomPositionButton.addSelectionListener(positionListener);
		inlaysCenterPositionButton.addSelectionListener(positionListener);

		updateInlayModeButtons();

		return parentComposite;
	}

	private void createFretboardBackgroundGroup(final Composite parent) {

		final String backgroundMode = getPreferenceStore().getString(Preferences.FRETBOARD_VIEW_BACKGROUND_MODE);

		backgroundGroup = new Group(parent, SWT.NONE);
		backgroundGroup.setText(PreferenceMessages.FretboardViewPreferencePage_background_mode);
		GridDataFactory.fillDefaults().grab(true, false).indent(0, 10).applyTo(backgroundGroup);
		GridLayoutFactory.fillDefaults().equalWidth(false).numColumns(4).spacing(10, 5).margins(5, 10)
				.applyTo(backgroundGroup);

		final SelectionAdapter bgRadioSelectionListener = new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				updateNavigationButtons(e.widget.getData(BACKGROUND_MODE) != UIConstants.NO_BACKGROUND);
				updatePreviewLabel();
			}
		};

		noBackgroundButton = new Button(backgroundGroup, SWT.RADIO);
		noBackgroundButton.setText(PreferenceMessages.FretboardViewPreferencePage_no_background);
		noBackgroundButton.setSelection(UIConstants.NO_BACKGROUND.equals(backgroundMode));
		noBackgroundButton.setData(BACKGROUND_MODE, UIConstants.NO_BACKGROUND);
		noBackgroundButton.addSelectionListener(bgRadioSelectionListener);
		GridDataFactory.fillDefaults().indent(5, 0).applyTo(noBackgroundButton);

		backgroundColorButton = new Button(backgroundGroup, SWT.RADIO);
		backgroundColorButton.setText(PreferenceMessages.FretboardViewPreferencePage_background_color);
		backgroundColorButton.setSelection(UIConstants.BACKGROUND_COLOR.equals(backgroundMode));
		backgroundColorButton.setData(BACKGROUND_MODE, UIConstants.BACKGROUND_COLOR);
		backgroundColorButton.addSelectionListener(bgRadioSelectionListener);

		backgroundImageButton = new Button(backgroundGroup, SWT.RADIO);
		backgroundImageButton.setText(PreferenceMessages.FretboardViewPreferencePage_background_image);
		backgroundImageButton.setSelection(UIConstants.BACKGROUND_IMAGE.equals(backgroundMode));
		backgroundImageButton.setData(BACKGROUND_MODE, UIConstants.BACKGROUND_IMAGE);
		backgroundImageButton.addSelectionListener(bgRadioSelectionListener);

		final Composite previewComposite = new Composite(backgroundGroup, SWT.NONE);
		previewComposite.setBackground(IFigureConstants.GREY);
		GridDataFactory.fillDefaults().span(1, 2).grab(true, true).applyTo(previewComposite);
		GridLayoutFactory.fillDefaults().margins(5, 5).applyTo(previewComposite);

		previewLabel = new Label(previewComposite, SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(previewLabel);

		index = prefs.getInt(Preferences.FRETBOARD_VIEW_BACKGROUND_INDEX);

		final Composite buttonComposite = new Composite(backgroundGroup, SWT.NONE);
		GridLayoutFactory.fillDefaults().equalWidth(true).numColumns(2).spacing(0, 0).applyTo(buttonComposite);
		GridDataFactory.fillDefaults().span(3, 1).grab(false, false).applyTo(buttonComposite);

		previousButton = new Button(buttonComposite, SWT.PUSH);
		previousButton.setText(PreferenceMessages.FretboardViewPreferencePage_background_previous);
		previousButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				index--;
				updateNavigationButtons(true);
				updatePreviewLabel();
			}
		});
		GridDataFactory.fillDefaults().grab(true, false).applyTo(previousButton);

		nextButton = new Button(buttonComposite, SWT.PUSH);
		nextButton.setText(PreferenceMessages.FretboardViewPreferencePage_background_next);
		nextButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				index++;
				updateNavigationButtons(true);
				updatePreviewLabel();
			}
		});
		GridDataFactory.fillDefaults().grab(true, false).applyTo(nextButton);

		updateNavigationButtons(backgroundMode != UIConstants.NO_BACKGROUND);
		updatePreviewLabel();
	}

	private void createInlaysGroup(final Composite parent) {

		// inlays group
		final Group inlaysGroup = new Group(parent, SWT.NONE);
		inlaysGroup.setText(PreferenceMessages.FretboardViewPreferencePage_inlays);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(inlaysGroup);
		GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).spacing(10, 5).margins(5, 10)
				.applyTo(inlaysGroup);

		// show inlays numbers
		showInlaysButton = new Button(inlaysGroup, SWT.CHECK);
		showInlaysButton.setText(PreferenceMessages.FretboardViewPreferencePage_show_inlays);
		showInlaysButton.setSelection(prefs.getBoolean(Preferences.FRETBOARD_VIEW_SHOW_INLAYS));
		GridDataFactory.fillDefaults().span(2, 1).applyTo(showInlaysButton);

		// inlays in gray color
		grayInlaysButton = new Button(inlaysGroup, SWT.CHECK);
		grayInlaysButton.setText(PreferenceMessages.FretboardViewPreferencePage_gray_inlays);
		grayInlaysButton.setSelection(prefs.getBoolean(Preferences.FRETBOARD_VIEW_INLAYS_GRAY_COLOR));
		GridDataFactory.fillDefaults().span(2, 1).grab(true, false).indent(HORIZONTAL_INDENT, 5)
				.applyTo(grayInlaysButton);

		// inlays position
		final Position inlaysPosition = Position.valueOf(prefs.getString(Preferences.FRETBOARD_VIEW_INLAYS_POSITION));
		final Label inlaysPositionLabel = new Label(inlaysGroup, SWT.RIGHT);
		inlaysPositionLabel.setText(PreferenceMessages.FretboardViewPreferencePage_position + ":"); //$NON-NLS-1$
		GridDataFactory.fillDefaults().indent(HORIZONTAL_INDENT, 5).applyTo(inlaysPositionLabel);

		final Composite inlaysPositionComposite = new Composite(inlaysGroup, SWT.NONE);
		GridDataFactory.fillDefaults().indent(-1, 5).grab(true, false).applyTo(inlaysPositionComposite);
		GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).margins(0, 0).applyTo(inlaysPositionComposite);
		inlaysTopPositionButton = new Button(inlaysPositionComposite, SWT.RADIO);
		inlaysTopPositionButton.setText(PreferenceMessages.FretboardViewPreferencePage_above_fretboard);
		inlaysTopPositionButton.setSelection(inlaysPosition == Position.TOP);
		inlaysBottomPositionButton = new Button(inlaysPositionComposite, SWT.RADIO);
		inlaysBottomPositionButton.setText(PreferenceMessages.FretboardViewPreferencePage_below_fretboard);
		inlaysBottomPositionButton.setSelection(inlaysPosition == Position.BOTTOM);
		inlaysCenterPositionButton = new Button(inlaysPositionComposite, SWT.RADIO);
		inlaysCenterPositionButton.setText(PreferenceMessages.FretboardViewPreferencePage_inside_fretboard);
		inlaysCenterPositionButton.setSelection(inlaysPosition != Position.TOP && inlaysPosition != Position.BOTTOM);
		GridDataFactory.fillDefaults().span(2, 1).applyTo(inlaysCenterPositionButton);
		inlaysPositionLabel.setFont(inlaysTopPositionButton.getFont());

		// inlays shape
		final String inlaysShape = prefs.getString(Preferences.FRETBOARD_VIEW_INLAYS_SHAPE);
		final Label inlaysShapeLabel = new Label(inlaysGroup, SWT.RIGHT);
		inlaysShapeLabel.setText(PreferenceMessages.FretboardViewPreferencePage_inlays_shape + ":"); //$NON-NLS-1$
		GridDataFactory.fillDefaults().indent(HORIZONTAL_INDENT, -1).applyTo(inlaysShapeLabel);

		final Composite inlaysShapeComposite = new Composite(inlaysGroup, SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(inlaysShapeComposite);
		GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).margins(0, 0).applyTo(inlaysShapeComposite);
		circleInlaysButton = new Button(inlaysShapeComposite, SWT.RADIO);
		circleInlaysButton.setText(PreferenceMessages.FretboardViewPreferencePage_circle_inlays);
		circleInlaysButton.setSelection(UIConstants.INLAYS_SHAPE_CIRCLE.equals(inlaysShape));
		triangleInlaysButton = new Button(inlaysShapeComposite, SWT.RADIO);
		triangleInlaysButton.setText(PreferenceMessages.FretboardViewPreferencePage_triangle_inlays);
		triangleInlaysButton.setSelection(!UIConstants.INLAYS_SHAPE_CIRCLE.equals(inlaysShape));
		inlaysShapeLabel.setFont(circleInlaysButton.getFont());

		// add listeners
		showInlaysButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				updateInlaysButtons();
			}
		});
		updateInlaysButtons();
	}

	private void createFretNumbersGroup(final Composite parent) {

		// fret numbers group
		final Group fretNumbersGroup = new Group(parent, SWT.NONE);
		fretNumbersGroup.setText(PreferenceMessages.FretboardViewPreferencePage_fret_numbers);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(fretNumbersGroup);
		GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).spacing(10, 5).margins(5, 10)
				.applyTo(fretNumbersGroup);

		// show fret numbers
		showFretNumbersButton = new Button(fretNumbersGroup, SWT.CHECK);
		showFretNumbersButton.setText(PreferenceMessages.FretboardViewPreferencePage_show_fret_numbers);
		showFretNumbersButton.setSelection(prefs.getBoolean(Preferences.FRETBOARD_VIEW_SHOW_FRET_NUMBERS));
		GridDataFactory.fillDefaults().span(2, 1).applyTo(showFretNumbersButton);

		// fret numbers in gray color
		grayFretNumbersButton = new Button(fretNumbersGroup, SWT.CHECK);
		grayFretNumbersButton.setText(PreferenceMessages.FretboardViewPreferencePage_gray_fret_numbers);
		grayFretNumbersButton.setSelection(prefs.getBoolean(Preferences.FRETBOARD_VIEW_FRET_NUMBERS_GRAY_COLOR));
		GridDataFactory.fillDefaults().span(2, 1).grab(true, false).indent(HORIZONTAL_INDENT, 5)
				.applyTo(grayFretNumbersButton);

		// reduced fret numbers mode
		reducedFretNumbersButton = new Button(fretNumbersGroup, SWT.CHECK);
		reducedFretNumbersButton.setText(PreferenceMessages.FretboardViewPreferencePage_show_reduced_fret_numbers);
		reducedFretNumbersButton.setSelection(prefs.getBoolean(Preferences.FRETBOARD_VIEW_FRET_NUMBERS_REDUCED_MODE));
		GridDataFactory.fillDefaults().span(2, 1).grab(true, false).indent(HORIZONTAL_INDENT, -1)
				.applyTo(reducedFretNumbersButton);

		// fret numbers position
		final Position fretNumbersPosition = Position.valueOf(prefs
				.getString(Preferences.FRETBOARD_VIEW_FRET_NUMBERS_POSITION));
		final Label fretNumbersPositionLabel = new Label(fretNumbersGroup, SWT.RIGHT);
		fretNumbersPositionLabel.setText(PreferenceMessages.FretboardViewPreferencePage_position + ":"); //$NON-NLS-1$
		GridDataFactory.fillDefaults().indent(HORIZONTAL_INDENT, 5).applyTo(fretNumbersPositionLabel);

		final Composite fretNumbersPositionComposite = new Composite(fretNumbersGroup, SWT.NONE);
		GridDataFactory.fillDefaults().indent(-1, 5).grab(true, false).applyTo(fretNumbersPositionComposite);
		GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).margins(0, 0)
				.applyTo(fretNumbersPositionComposite);
		fretNumbersTopPositionButton = new Button(fretNumbersPositionComposite, SWT.RADIO);
		fretNumbersTopPositionButton.setText(PreferenceMessages.FretboardViewPreferencePage_above_fretboard);
		fretNumbersTopPositionButton.setSelection(fretNumbersPosition == Position.TOP);
		fretNumbersBottomPositionButton = new Button(fretNumbersPositionComposite, SWT.RADIO);
		fretNumbersBottomPositionButton.setText(PreferenceMessages.FretboardViewPreferencePage_below_fretboard);
		fretNumbersBottomPositionButton.setSelection(fretNumbersPosition != Position.TOP);
		fretNumbersPositionLabel.setFont(fretNumbersTopPositionButton.getFont());

		// fret numbers numerals
		final String fretNumbersMode = prefs.getString(Preferences.FRETBOARD_VIEW_FRET_NUMBERS_NUMERALS);
		final Label fretNumbersModeLabel = new Label(fretNumbersGroup, SWT.RIGHT);
		fretNumbersModeLabel.setText(PreferenceMessages.ViewPreferencePage_numerals + ":"); //$NON-NLS-1$
		GridDataFactory.fillDefaults().indent(HORIZONTAL_INDENT, -1).applyTo(fretNumbersModeLabel);

		final Composite fretNumbersModeComposite = new Composite(fretNumbersGroup, SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(fretNumbersModeComposite);
		GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).margins(0, 0)
				.applyTo(fretNumbersModeComposite);
		fretNumbersArabicButton = new Button(fretNumbersModeComposite, SWT.RADIO);
		fretNumbersArabicButton.setText(PreferenceMessages.ViewPreferencePage_arabic_numerals);
		fretNumbersArabicButton.setSelection(UIConstants.NUMERALS_MODE_ARABIC.equals(fretNumbersMode));
		fretNumbersRomanButton = new Button(fretNumbersModeComposite, SWT.RADIO);
		fretNumbersRomanButton.setText(PreferenceMessages.ViewPreferencePage_roman_numerals);
		fretNumbersRomanButton.setSelection(!UIConstants.NUMERALS_MODE_ARABIC.equals(fretNumbersMode));
		fretNumbersModeLabel.setFont(fretNumbersArabicButton.getFont());

		// add listeners
		showFretNumbersButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				updateFretNumbersButtons();
			}
		});
		updateFretNumbersButtons();
	}

	/* --- enablement and selection handling --- */

	private void updateEnablement() {
		updateFingeringEnablement();
		updateBarreEnablement();
	}

	private void addListener() {

		highlightRootNoteButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				updateEnablement();
			}
		});

		frameFingeringButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				updateEnablement();
			}
		});

		// barre listener
		final SelectionAdapter barreListener = new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				updateBarreEnablement();
			}
		};
		drawBarreButton.addSelectionListener(barreListener);
		barreAsLineButton.addSelectionListener(barreListener);
		barreAsArcButton.addSelectionListener(barreListener);
		barreAsBarButton.addSelectionListener(barreListener);
		showInsideBarButton.addSelectionListener(barreListener);

		final SelectionAdapter highlightListener = new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				if (!highlightRootNoteWithColorButton.getSelection()
						&& !highlightRootNoteWithShapeButton.getSelection()) {
					if (e.widget == highlightRootNoteWithColorButton) {
						highlightRootNoteWithShapeButton.setSelection(true);
					} else {
						highlightRootNoteWithColorButton.setSelection(true);
					}
				}
			}
		};
		highlightRootNoteWithColorButton.addSelectionListener(highlightListener);
		highlightRootNoteWithShapeButton.addSelectionListener(highlightListener);

		// block listener
		final SelectionAdapter additionalNotesListener = new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				updateAdditionalNotesButtons();
			}
		};
		overlayButton.addSelectionListener(additionalNotesListener);
		outlineButton.addSelectionListener(additionalNotesListener);
		onlyBlockButton.addSelectionListener(additionalNotesListener);
		allNotesForGriptablesButton.addSelectionListener(additionalNotesListener);
		allNotesForBlocksButton.addSelectionListener(additionalNotesListener);
	}

	private void updateFingeringEnablement() {

		final boolean showRootNote = highlightRootNoteButton.getSelection();

		frameFingeringButton.setEnabled(!showRootNote);
		if (showRootNote) {
			frameFingeringButton.setSelection(true);
			if (coloredFingeringButton.getSelection()) {
				coloredFingeringButton.setSelection(false);
				whiteFingeringButton.setSelection(true);
			}
		}

		final boolean frameFingeringEnabled = frameFingeringButton.getSelection();
		whiteFingeringButton.setEnabled(frameFingeringEnabled);
		blackFingeringButton.setEnabled(frameFingeringEnabled);
		coloredFingeringButton.setEnabled(frameFingeringEnabled && !showRootNote);
	}

	private void updateBarreEnablement() {

		// update selections
		final boolean showRootNote = highlightRootNoteButton.getSelection();

		if (showRootNote) {
			// bar color
			sameColorBarButton.setSelection(true);
			whiteBarButton.setSelection(false);
			blackBarButton.setSelection(false);
			// fingering in bar
			showInsideBarButton.setSelection(true);
			// empty string color
			whiteEmptyStringsButton.setSelection(false);
		}
		if (showRootNote) {
			singleFingeringInBarButton.setSelection(false);
		}

		final boolean frameFingering = frameFingeringButton.getSelection();
		if (!frameFingering) {
			barreAsLineButton.setSelection(false);
			barreAsArcButton.setSelection(false);
			barreAsBarButton.setSelection(true);
		}
		final boolean barSelected = barreAsBarButton.getSelection();
		if (barSelected) {
			highlightRootNoteWithColorButton.setSelection(true);
		}

		// update enablement
		final boolean enabled = drawBarreButton.getSelection();
		final boolean enabledLineOrArc = barreAsLineButton.getSelection() || barreAsArcButton.getSelection();
		final boolean enabledBar = barSelected;
		final boolean enabledBarFingering = showInsideBarButton.getSelection();

		barreAsLineButton.setEnabled(enabled && frameFingering);
		barreAsArcButton.setEnabled(enabled && frameFingering);
		barreAsBarButton.setEnabled(enabled);

		lineWidthCombo.setEnabled(enabled && enabledLineOrArc);
		sameColorBarButton.setEnabled(enabled && enabledBar);
		whiteBarButton.setEnabled(enabled && enabledBar);
		blackBarButton.setEnabled(enabled && enabledBar);
		showInsideBarButton.setEnabled(enabled && enabledBar && !showRootNote);
		sameColorBarButton.setEnabled(enabled && enabledBar && !showRootNote);
		whiteBarButton.setEnabled(enabled && enabledBar && !showRootNote);
		blackBarButton.setEnabled(enabled && enabledBar && !showRootNote);
		singleFingeringInBarButton.setEnabled(enabled && enabledBar && enabledBarFingering && !showRootNote);
		highlightRootNoteWithColorButton.setEnabled(showRootNote && !barSelected);
		highlightRootNoteWithShapeButton.setEnabled(showRootNote);

		whiteEmptyStringsButton.setEnabled(!showRootNote);
	}

	private void updateAdditionalNotesButtons() {
		final boolean allNotesForGriptables = allNotesForGriptablesButton.getSelection();
		if (allNotesForGriptables) {
			additionalChordNotesButton.setSelection(true);
		}
		additionalChordNotesButton.setEnabled(!allNotesForGriptables);

		final boolean allNotesForBlocks = allNotesForBlocksButton.getSelection();
		final boolean noOverlay = onlyBlockButton.getSelection();
		if (!noOverlay || allNotesForBlocks) {
			additionalBlockNotesButton.setSelection(true);
		}
		additionalBlockNotesButton.setEnabled(noOverlay && !allNotesForBlocks);
	}

	private void updateNavigationButtons(final boolean enabled) {
		previousButton.setEnabled(enabled && index > 0);
		nextButton.setEnabled(enabled && index < IFigureConstants.FRETBOARD_COLORS.length - 1);
	}

	private void updatePreviewLabel() {
		if (backgroundColorButton.getSelection()) {
			previewLabel.setBackgroundImage(null);
			if (index >= 0) {
				previewLabel.setBackground(IFigureConstants.FRETBOARD_COLORS[index]);
			}
		} else if (backgroundImageButton.getSelection()) {
			if (index >= 0) {
				previewLabel.setBackgroundImage(Activator.getDefault().getImage(IImageKeys.WOOD_PATTERN[index]));
			}
		} else {
			previewLabel.setBackgroundImage(null);
			previewLabel.setBackground(ColorConstants.white);
		}
	}

	private void updateInlaysButtons() {
		final boolean enabled = showInlaysButton.getSelection();
		grayInlaysButton.setEnabled(enabled && !inlaysCenterPositionButton.getSelection());
		inlaysTopPositionButton.setEnabled(enabled);
		inlaysBottomPositionButton.setEnabled(enabled);
		inlaysCenterPositionButton.setEnabled(enabled);
		circleInlaysButton.setEnabled(enabled);
		triangleInlaysButton.setEnabled(enabled && inlaysCenterPositionButton.getSelection());
	}

	private void updateFretNumbersButtons() {
		final boolean enabled = showFretNumbersButton.getSelection();
		grayFretNumbersButton.setEnabled(enabled);
		reducedFretNumbersButton.setEnabled(enabled);
		fretNumbersTopPositionButton.setEnabled(enabled);
		fretNumbersBottomPositionButton.setEnabled(enabled);
		fretNumbersArabicButton.setEnabled(enabled);
		fretNumbersRomanButton.setEnabled(enabled);
	}

	private void updateInlayModeButtons() {
		if (inlaysTopPositionButton.getSelection() || inlaysBottomPositionButton.getSelection()) {
			circleInlaysButton.setSelection(true);
			triangleInlaysButton.setSelection(false);
		}
		if (inlaysCenterPositionButton.getSelection()) {
			grayInlaysButton.setSelection(true);
		}
		final boolean showInlays = showInlaysButton.getSelection();
		final boolean centerInlays = inlaysCenterPositionButton.getSelection();
		triangleInlaysButton.setEnabled(showInlays && centerInlays);
		grayInlaysButton.setEnabled(showInlays && !centerInlays);
	}

	/* --- perform buttons handling --- */

	@Override
	public boolean performOk() {

		// highlight root note
		prefs.setValue(Preferences.FRETBOARD_VIEW_HIGHLIGHT_ROOT_NOTE_WITH_COLOR,
				highlightRootNoteWithColorButton.getSelection());
		prefs.setValue(Preferences.FRETBOARD_VIEW_HIGHLIGHT_ROOT_NOTE_WITH_SHAPE,
				highlightRootNoteWithShapeButton.getSelection());

		// points background
		prefs.setValue(
				Preferences.FRETBOARD_VIEW_POINTS_BACKGROUND,
				whitePointsButton.getSelection() ? BackgroundColorMode.WHITE.toString() : BackgroundColorMode.BLACK
						.toString());

		// fingering frame/background
		prefs.setValue(Preferences.FRETBOARD_VIEW_FRAME_FINGERING, frameFingeringButton.getSelection());
		prefs.setValue(Preferences.FRETBOARD_VIEW_EMPTY_STRINGS_BACKGROUND_WHITE,
				whiteEmptyStringsButton.getSelection());
		prefs.setValue(
				Preferences.FRETBOARD_VIEW_FINGERING_BACKGROUND,
				whiteFingeringButton.getSelection() ? BackgroundColorMode.WHITE.toString() : blackFingeringButton
						.getSelection() ? BackgroundColorMode.BLACK.toString() : BackgroundColorMode.COLORED.toString());

		// barre
		prefs.setValue(Preferences.FRETBOARD_VIEW_SHOW_BARRE, drawBarreButton.getSelection());
		prefs.setValue(Preferences.FRETBOARD_VIEW_BARRE_MODE,
				barreAsLineButton.getSelection() ? BarreMode.LINE.toString()
						: barreAsArcButton.getSelection() ? BarreMode.ARC.toString() : BarreMode.BAR.toString());
		prefs.setValue(Preferences.FRETBOARD_VIEW_BARRE_LINE_WIDTH, lineWidthCombo.getSelectionIndex() + 1);
		prefs.setValue(Preferences.FRETBOARD_VIEW_BARRE_BAR_BACKGROUND,
				whiteBarButton.getSelection() ? BackgroundColorMode.WHITE.toString()
						: blackBarButton.getSelection() ? BackgroundColorMode.BLACK.toString()
								: BackgroundColorMode.SAME.toString());
		prefs.setValue(Preferences.FRETBOARD_VIEW_BARRE_BAR_SHOW_ELEMENTS_INSIDE, showInsideBarButton.getSelection());
		prefs.setValue(Preferences.FRETBOARD_VIEW_BARRE_BAR_SHOW_SINGLE_FINGER_NUMBER,
				singleFingeringInBarButton.getSelection());

		// additional notes
		prefs.setValue(Preferences.FRETBOARD_VIEW_SHOW_ADDITIONAL_NOTES_ON_EMPTY_FRETBOARD,
				allNotesOnEmptyFretboardButton.getSelection());
		prefs.setValue(Preferences.FRETBOARD_VIEW_SHOW_ADDITIONAL_NOTES_FOR_GRIPTABLE,
				allNotesForGriptablesButton.getSelection());
		prefs.setValue(Preferences.FRETBOARD_VIEW_SHOW_ADDITIONAL_NOTES_FOR_CHORD_AND_SCALE,
				allNotesForChordsScalesButton.getSelection());
		prefs.setValue(Preferences.FRETBOARD_VIEW_SHOW_ADDITIONAL_NOTES_FOR_BLOCK,
				allNotesForBlocksButton.getSelection());
		prefs.setValue(Preferences.FRETBOARD_VIEW_SHOW_ADDITIONAL_CHORD_NOTES,
				additionalChordNotesButton.getSelection());
		prefs.setValue(Preferences.FRETBOARD_VIEW_SHOW_ADDITIONAL_BLOCK_NOTES,
				additionalBlockNotesButton.getSelection());
		prefs.setValue(Preferences.FRETBOARD_VIEW_SHOW_ADDITIONAL_NOTES_IN_BLACK,
				additionalNotesInBlackButton.getSelection());

		// block presentation
		prefs.setValue(Preferences.FRETBOARD_VIEW_SHOW_BLOCK_PRESENTATION,
				overlayButton.getSelection() ? UIConstants.BLOCK_OVERLAY
						: outlineButton.getSelection() ? UIConstants.BLOCK_OUTLINE : UIConstants.BLOCK_NO_OVERLAY_FRAME);

		// fretboard background
		if (backgroundColorButton.getSelection()) {
			prefs.setValue(Preferences.FRETBOARD_VIEW_BACKGROUND_MODE, UIConstants.BACKGROUND_COLOR);
			prefs.setValue(Preferences.FRETBOARD_VIEW_BACKGROUND_INDEX, index);
		} else if (backgroundImageButton.getSelection()) {
			prefs.setValue(Preferences.FRETBOARD_VIEW_BACKGROUND_MODE, UIConstants.BACKGROUND_IMAGE);
			prefs.setValue(Preferences.FRETBOARD_VIEW_BACKGROUND_INDEX, index);
		} else {
			prefs.setValue(Preferences.FRETBOARD_VIEW_BACKGROUND_MODE, UIConstants.NO_BACKGROUND);
		}

		// inlays
		prefs.setValue(Preferences.FRETBOARD_VIEW_SHOW_INLAYS, showInlaysButton.getSelection());
		prefs.setValue(Preferences.FRETBOARD_VIEW_INLAYS_GRAY_COLOR, grayInlaysButton.getSelection());
		prefs.setValue(
				Preferences.FRETBOARD_VIEW_INLAYS_POSITION,
				inlaysTopPositionButton.getSelection() ? Position.TOP.toString() : inlaysBottomPositionButton
						.getSelection() ? Position.BOTTOM.toString() : Position.CENTER.toString());
		prefs.setValue(Preferences.FRETBOARD_VIEW_INLAYS_SHAPE,
				circleInlaysButton.getSelection() ? UIConstants.INLAYS_SHAPE_CIRCLE : UIConstants.INLAYS_SHAPE_TRIANGLE);

		// fret numbers
		prefs.setValue(Preferences.FRETBOARD_VIEW_SHOW_FRET_NUMBERS, showFretNumbersButton.getSelection());
		prefs.setValue(Preferences.FRETBOARD_VIEW_FRET_NUMBERS_GRAY_COLOR, grayFretNumbersButton.getSelection());
		prefs.setValue(Preferences.FRETBOARD_VIEW_FRET_NUMBERS_REDUCED_MODE, reducedFretNumbersButton.getSelection());
		prefs.setValue(Preferences.FRETBOARD_VIEW_FRET_NUMBERS_POSITION,
				fretNumbersTopPositionButton.getSelection() ? Position.TOP.toString() : Position.BOTTOM.toString());
		prefs.setValue(Preferences.FRETBOARD_VIEW_FRET_NUMBERS_NUMERALS,
				fretNumbersArabicButton.getSelection() ? UIConstants.NUMERALS_MODE_ARABIC
						: UIConstants.NUMERALS_MODE_ROMAN);

		return super.performOk();
	}

	@Override
	protected void performDefaults() {
		super.performDefaults();

		// highlight root note
		highlightRootNoteWithColorButton.setSelection(prefs
				.getDefaultBoolean(Preferences.FRETBOARD_VIEW_HIGHLIGHT_ROOT_NOTE_WITH_COLOR));
		highlightRootNoteWithShapeButton.setSelection(prefs
				.getDefaultBoolean(Preferences.FRETBOARD_VIEW_HIGHLIGHT_ROOT_NOTE_WITH_SHAPE));

		// points background
		final BackgroundColorMode pointsDefaultBackground = BackgroundColorMode.valueOf(prefs
				.getDefaultString(Preferences.FRETBOARD_VIEW_POINTS_BACKGROUND));
		whitePointsButton.setSelection(pointsDefaultBackground == BackgroundColorMode.WHITE);
		blackPointsButton.setSelection(pointsDefaultBackground == BackgroundColorMode.BLACK);

		// fingering frame/background
		frameFingeringButton.setSelection(prefs.getDefaultBoolean(Preferences.FRETBOARD_VIEW_FRAME_FINGERING));
		whiteEmptyStringsButton.setSelection(prefs
				.getDefaultBoolean(Preferences.FRETBOARD_VIEW_EMPTY_STRINGS_BACKGROUND_WHITE));
		final BackgroundColorMode fingeringDefaultBackground = BackgroundColorMode.valueOf(prefs
				.getDefaultString(Preferences.FRETBOARD_VIEW_FINGERING_BACKGROUND));
		whiteFingeringButton.setSelection(fingeringDefaultBackground == BackgroundColorMode.WHITE);
		blackFingeringButton.setSelection(fingeringDefaultBackground == BackgroundColorMode.BLACK);
		coloredFingeringButton.setSelection(fingeringDefaultBackground == BackgroundColorMode.COLORED);

		// barre
		drawBarreButton.setSelection(prefs.getDefaultBoolean(Preferences.FRETBOARD_VIEW_SHOW_BARRE));
		final BarreMode barreDefaultMode = BarreMode.valueOf(prefs
				.getDefaultString(Preferences.FRETBOARD_VIEW_BARRE_MODE));
		barreAsLineButton.setSelection(barreDefaultMode == BarreMode.LINE);
		barreAsArcButton.setSelection(barreDefaultMode == BarreMode.ARC);
		barreAsBarButton.setSelection(barreDefaultMode == BarreMode.BAR);
		lineWidthCombo.select(prefs.getDefaultInt(Preferences.FRETBOARD_VIEW_BARRE_LINE_WIDTH) - 1);
		final BackgroundColorMode barDefaultBackground = BackgroundColorMode.valueOf(prefs
				.getDefaultString(Preferences.FRETBOARD_VIEW_BARRE_BAR_BACKGROUND));
		sameColorBarButton.setSelection(barDefaultBackground == BackgroundColorMode.SAME);
		whiteBarButton.setSelection(barDefaultBackground == BackgroundColorMode.WHITE);
		blackBarButton.setSelection(barDefaultBackground == BackgroundColorMode.BLACK);
		showInsideBarButton.setSelection(prefs
				.getDefaultBoolean(Preferences.FRETBOARD_VIEW_BARRE_BAR_SHOW_ELEMENTS_INSIDE));
		singleFingeringInBarButton.setSelection(prefs
				.getDefaultBoolean(Preferences.FRETBOARD_VIEW_BARRE_BAR_SHOW_SINGLE_FINGER_NUMBER));

		// additional notes
		allNotesOnEmptyFretboardButton.setSelection(prefs
				.getDefaultBoolean(Preferences.FRETBOARD_VIEW_SHOW_ADDITIONAL_NOTES_ON_EMPTY_FRETBOARD));
		allNotesForGriptablesButton.setSelection(prefs
				.getDefaultBoolean(Preferences.FRETBOARD_VIEW_SHOW_ADDITIONAL_NOTES_FOR_GRIPTABLE));
		allNotesForChordsScalesButton.setSelection(prefs
				.getDefaultBoolean(Preferences.FRETBOARD_VIEW_SHOW_ADDITIONAL_NOTES_FOR_CHORD_AND_SCALE));
		allNotesForBlocksButton.setSelection(prefs
				.getDefaultBoolean(Preferences.FRETBOARD_VIEW_SHOW_ADDITIONAL_NOTES_FOR_BLOCK));
		additionalChordNotesButton.setSelection(prefs
				.getDefaultBoolean(Preferences.FRETBOARD_VIEW_SHOW_ADDITIONAL_CHORD_NOTES));
		additionalBlockNotesButton.setSelection(prefs
				.getDefaultBoolean(Preferences.FRETBOARD_VIEW_SHOW_ADDITIONAL_BLOCK_NOTES));
		additionalNotesInBlackButton.setSelection(prefs
				.getDefaultBoolean(Preferences.FRETBOARD_VIEW_SHOW_ADDITIONAL_NOTES_IN_BLACK));

		// block presentation
		final String blockDefaultPresentation = prefs
				.getDefaultString(Preferences.FRETBOARD_VIEW_SHOW_BLOCK_PRESENTATION);
		overlayButton.setSelection(UIConstants.BLOCK_OVERLAY.equals(blockDefaultPresentation));
		outlineButton.setSelection(UIConstants.BLOCK_OUTLINE.equals(blockDefaultPresentation));
		onlyBlockButton.setSelection(UIConstants.BLOCK_NO_OVERLAY_FRAME.equals(blockDefaultPresentation));

		// fretboard background
		final String defaultBackgroundMode = prefs.getDefaultString(Preferences.FRETBOARD_VIEW_BACKGROUND_MODE);
		noBackgroundButton.setSelection(UIConstants.NO_BACKGROUND.equals(defaultBackgroundMode));
		backgroundColorButton.setSelection(UIConstants.BACKGROUND_COLOR.equals(defaultBackgroundMode));
		backgroundImageButton.setSelection(UIConstants.BACKGROUND_IMAGE.equals(defaultBackgroundMode));
		index = prefs.getDefaultInt(Preferences.FRETBOARD_VIEW_BACKGROUND_INDEX);

		// inlays
		showInlaysButton.setSelection(prefs.getDefaultBoolean(Preferences.FRETBOARD_VIEW_SHOW_INLAYS));
		grayInlaysButton.setSelection(prefs.getDefaultBoolean(Preferences.FRETBOARD_VIEW_INLAYS_GRAY_COLOR));
		final Position inlaysPosition = Position.valueOf(prefs
				.getDefaultString(Preferences.FRETBOARD_VIEW_INLAYS_POSITION));
		inlaysTopPositionButton.setSelection(inlaysPosition == Position.TOP);
		inlaysBottomPositionButton.setSelection(inlaysPosition == Position.BOTTOM);
		inlaysCenterPositionButton.setSelection(inlaysPosition != Position.TOP && inlaysPosition != Position.BOTTOM);
		final String inlaysMode = prefs.getDefaultString(Preferences.FRETBOARD_VIEW_INLAYS_SHAPE);
		circleInlaysButton.setSelection(UIConstants.INLAYS_SHAPE_CIRCLE.equals(inlaysMode));
		triangleInlaysButton.setSelection(!UIConstants.INLAYS_SHAPE_CIRCLE.equals(inlaysMode));

		// fret numbers
		showFretNumbersButton.setSelection(prefs.getDefaultBoolean(Preferences.FRETBOARD_VIEW_SHOW_FRET_NUMBERS));
		grayFretNumbersButton.setSelection(prefs.getDefaultBoolean(Preferences.FRETBOARD_VIEW_FRET_NUMBERS_GRAY_COLOR));
		reducedFretNumbersButton.setSelection(prefs
				.getDefaultBoolean(Preferences.FRETBOARD_VIEW_FRET_NUMBERS_REDUCED_MODE));
		final Position fretNumberPosition = Position.valueOf(prefs
				.getDefaultString(Preferences.FRETBOARD_VIEW_FRET_NUMBERS_POSITION));
		fretNumbersTopPositionButton.setSelection(fretNumberPosition == Position.TOP);
		fretNumbersBottomPositionButton.setSelection(fretNumberPosition != Position.TOP);
		final String fretNumberNumerals = prefs.getDefaultString(Preferences.FRETBOARD_VIEW_FRET_NUMBERS_NUMERALS);
		fretNumbersArabicButton.setSelection(UIConstants.NUMERALS_MODE_ARABIC.equals(fretNumberNumerals));
		fretNumbersRomanButton.setSelection(!UIConstants.NUMERALS_MODE_ARABIC.equals(fretNumberNumerals));

		// update buttons
		updateEnablement();
		updateAdditionalNotesButtons();
		updateNavigationButtons(defaultBackgroundMode != UIConstants.NO_BACKGROUND);
		updatePreviewLabel();
		updateInlaysButtons();
		updateFretNumbersButtons();
		updateInlayModeButtons();
	}
}
