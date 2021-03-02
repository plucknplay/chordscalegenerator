/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.preferencePages;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
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
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.PreferenceLinkArea;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;

import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.UIConstants;
import com.plucknplay.csg.ui.util.MyBooleanFieldEditor;
import com.plucknplay.csg.ui.util.OnlyNumbersKeyListener;
import com.plucknplay.csg.ui.util.TabFolderLayout;
import com.plucknplay.csg.ui.util.enums.BackgroundColorMode;
import com.plucknplay.csg.ui.util.enums.BarreMode;
import com.plucknplay.csg.ui.util.enums.BoxViewPresentationMode;
import com.plucknplay.csg.ui.util.enums.FigureSizeMode;
import com.plucknplay.csg.ui.util.enums.Position;

public class BoxViewPreferencePage extends AbstractPreferencePage {

	public static final String ID = "com.plucknplay.csg.ui.views.boxViewPreferences"; //$NON-NLS-1$
	public static final String HELP_ID = "box_view_preference_page_context"; //$NON-NLS-1$

	public static final String BOX_VIEW_SHOW_OUTSIDE_BOX_CHANGED = "chordGenerator.preferences.views.box.view.show.outside.box.changed"; //$NON-NLS-1$

	private static final int MINIMAL_FRET_COUNT_MIN = 3;
	private static final int MINIMAL_FRET_COUNT_MAX = 8;
	private static final int MAXIMAL_UNASSIGNED_FRET_COUNT_MIN = 0;
	private static final int MAXIMAL_UNASSIGNED_FRET_COUNT_MAX = 7;

	private IPreferenceStore prefs;

	// frame section
	private Text minFretNumberText;
	private Text maxNumberOfStartingEmptyFretsText;

	// fret numbers section
	private Group positionGroup;
	private Button topButton;
	private Button bottomButton;
	private Button leftButton;
	private Button rightButton;
	private RadioGroupFieldEditor fretNumbersModeEditor;

	// fingering section
	private Button showMutedStringsButton;
	private Button highlightRootNoteButton;
	private Button highlightRootNoteWithColorButton;
	private Button highlightRootNoteWithShapeButton;
	private Group emptyStringsGroup;
	private Button placeEmptyStringsCloseToFrameButton;
	private Button sameEmptyStringSizeButton;
	private Button smallEmptyStringSizeButton;
	private Button mediumEmptyStringSizeButton;
	private Button largeEmptyStringSizeButton;
	private Button smallPointSizeButton;
	private Button mediumPointSizeButton;
	private Button largePointSizeButton;
	private Button whitePointsButton;
	private Button blackPointsButton;
	private Button showFingeringOutsideButton;
	private Button showNotesOutsideButton;
	private Button showIntervalsOutsideButton;
	private Button frameInsideButton;
	private Button whiteInsideButton;
	private Button blackInsideButton;
	private Button coloredInsideButton;
	private Button frameOutsideButton;
	private Button whiteOutsideButton;
	private Button blackOutsideButton;
	private Button coloredOutsideButton;
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
	private Button whiteEmptyStringButton;

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
	}

	@Override
	protected void createFieldEditors() {

		// preference page links
		final Composite linkComposite = PreferenceLinkUtil.createMainLinkComposite(getFieldEditorParent());
		PreferenceLinkUtil.createViewsLink(linkComposite, (IWorkbenchPreferenceContainer) getContainer(), false);
		PreferenceLinkUtil.createChordAndScaleNamesLink(linkComposite, (IWorkbenchPreferenceContainer) getContainer());

		// presentation mode
		final RadioGroupFieldEditor presentationModeEditor = new RadioGroupFieldEditor(
				Preferences.BOX_VIEW_PRESENTATION_MODE, PreferenceMessages.BoxViewPreferencePage_presentation_mode, 2,
				new String[][] {
						{ PreferenceMessages.BoxViewPreferencePage_horizontal,
								BoxViewPresentationMode.HORIZONTAL.toString() },
						{ PreferenceMessages.BoxViewPreferencePage_vertical,
								BoxViewPresentationMode.VERTICAL.toString() } }, getFieldEditorParent(), true);
		addField(presentationModeEditor);
		GridDataFactory.fillDefaults().applyTo(presentationModeEditor.getRadioBoxControl(getFieldEditorParent()));

		// tab folder
		final TabFolder folder = new TabFolder(getFieldEditorParent(), SWT.NONE);
		folder.setLayout(new TabFolderLayout());
		GridDataFactory.fillDefaults().span(3, 1).grab(true, false).applyTo(folder);

		final TabItem item1 = new TabItem(folder, SWT.NONE);
		item1.setText(PreferenceMessages.BoxViewPreferencePage_tab_item_fingering_notes_intervals);
		item1.setControl(createFingeringNotesIntervalsPage(folder));

		final TabItem item2 = new TabItem(folder, SWT.NONE);
		item2.setText(PreferenceMessages.BoxViewPreferencePage_tab_item_frame);
		item2.setControl(createFramePage(folder));

		final TabItem item3 = new TabItem(folder, SWT.NONE);
		item3.setText(PreferenceMessages.BoxViewPreferencePage_tab_item_fret_number);
		item3.setControl(createFretNumberPage(folder));

		// finally update fonts (layout problem)
		presentationModeEditor.getRadioBoxControl(getFieldEditorParent()).setFont(positionGroup.getFont());

		// set context-sensitive help
		Activator.getDefault().setHelp(getControl(), HELP_ID);
	}

	private Control createFramePage(final Composite parent) {

		final Composite parentComposite = new Composite(parent, SWT.NONE);
		GridLayoutFactory.fillDefaults().extendedMargins(5, 5, 10, 0).applyTo(parentComposite);

		final Composite composite = new Composite(parentComposite, SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(composite);

		// minimal fret number
		final Composite textComposite = new Composite(composite, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(3).equalWidth(false).applyTo(textComposite);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(textComposite);

		final Label minFretNumberLabel = new Label(textComposite, SWT.NONE);
		minFretNumberLabel.setText(PreferenceMessages.BoxViewPreferencePage_min_fret_number);

		minFretNumberText = new Text(textComposite, SWT.BORDER | SWT.SINGLE);
		minFretNumberText.setText("" + prefs.getInt(Preferences.BOX_VIEW_FRAME_MIN_FRET_COUNT)); //$NON-NLS-1$
		minFretNumberText.addKeyListener(new OnlyNumbersKeyListener());
		minFretNumberText.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(final FocusEvent e) {
				if ("".equals(minFretNumberText.getText())) {
					minFretNumberText.setText("0"); //$NON-NLS-1$
				}
				final int currentValue = Integer.parseInt(minFretNumberText.getText());
				if (currentValue < MINIMAL_FRET_COUNT_MIN) {
					minFretNumberText.setText("" + MINIMAL_FRET_COUNT_MIN); //$NON-NLS-1$
				}
				if (currentValue > MINIMAL_FRET_COUNT_MAX) {
					minFretNumberText.setText("" + MINIMAL_FRET_COUNT_MAX); //$NON-NLS-1$
				}
			}
		});
		minFretNumberText.setTextLimit(1);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(minFretNumberText);

		final Label minFretNumberCommentLabel = new Label(textComposite, SWT.NONE);
		minFretNumberCommentLabel.setText("(" + MINIMAL_FRET_COUNT_MIN + ".." //$NON-NLS-1$ //$NON-NLS-2$
				+ MINIMAL_FRET_COUNT_MAX + ")"); //$NON-NLS-1$

		// maximal number of unassigned starting frets
		final Label maxNumberOfStartingEmptyFretsLabel = new Label(textComposite, SWT.NONE);
		maxNumberOfStartingEmptyFretsLabel.setText(PreferenceMessages.BoxViewPreferencePage_max_empty_starting_frets);

		maxNumberOfStartingEmptyFretsText = new Text(textComposite, SWT.BORDER | SWT.SINGLE);
		maxNumberOfStartingEmptyFretsText
				.setText("" + prefs.getInt(Preferences.BOX_VIEW_FRAME_MAX_UNASSIGNED_FRET_COUNT)); //$NON-NLS-1$
		maxNumberOfStartingEmptyFretsText.addKeyListener(new OnlyNumbersKeyListener());
		maxNumberOfStartingEmptyFretsText.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(final FocusEvent e) {
				if ("".equals(maxNumberOfStartingEmptyFretsText.getText())) {
					maxNumberOfStartingEmptyFretsText.setText("0"); //$NON-NLS-1$
				}
				final int currentValue = Integer.parseInt(maxNumberOfStartingEmptyFretsText.getText());
				if (currentValue < MAXIMAL_UNASSIGNED_FRET_COUNT_MIN) {
					maxNumberOfStartingEmptyFretsText.setText("" + MAXIMAL_UNASSIGNED_FRET_COUNT_MIN); //$NON-NLS-1$
				}
				if (currentValue > MAXIMAL_UNASSIGNED_FRET_COUNT_MAX) {
					maxNumberOfStartingEmptyFretsText.setText("" + MAXIMAL_UNASSIGNED_FRET_COUNT_MAX); //$NON-NLS-1$
				}
			}
		});
		maxNumberOfStartingEmptyFretsText.setTextLimit(1);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(maxNumberOfStartingEmptyFretsText);

		final Label maxNumberOfStartingEmptyFretsCommentLabel = new Label(textComposite, SWT.NONE);
		maxNumberOfStartingEmptyFretsCommentLabel.setText("(" + MAXIMAL_UNASSIGNED_FRET_COUNT_MIN //$NON-NLS-1$
				+ ".." + MAXIMAL_UNASSIGNED_FRET_COUNT_MAX + ")"); //$NON-NLS-1$ //$NON-NLS-2$

		// small frets (if possible)
		final BooleanFieldEditor smallFretsEditor = new BooleanFieldEditor(Preferences.BOX_VIEW_FRAME_SMALL_FRETS,
				PreferenceMessages.BoxViewPreferencePage_small_frets, composite);
		addField(smallFretsEditor);

		// highlight nut
		final BooleanFieldEditor highlightNutEditor = new BooleanFieldEditor(Preferences.BOX_VIEW_FRAME_HIGHLIGHT_NUT,
				PreferenceMessages.BoxViewPreferencePage_highlight_nut, composite);
		addField(highlightNutEditor);

		// highlight outer frets
		final BooleanFieldEditor highlightOuterFretsEditor = new BooleanFieldEditor(
				Preferences.BOX_VIEW_FRAME_HIGHLIGHT_OUTER_FRETS,
				PreferenceMessages.BoxViewPreferencePage_highlight_outer_frets, composite);
		addField(highlightOuterFretsEditor);

		// draw doubled strings
		final BooleanFieldEditor drawDoubledStringsEditor = new BooleanFieldEditor(
				Preferences.BOX_VIEW_FRAME_DRAW_DOUBLED_STRINGS,
				PreferenceMessages.BoxViewPreferencePage_draw_doubled_strings, composite);
		addField(drawDoubledStringsEditor);

		// draw fretless frets dotted
		final BooleanFieldEditor drawFretlessFretsDottedEditor = new BooleanFieldEditor(
				Preferences.BOX_VIEW_FRAME_DRAW_FRETLESS_FRETS_DOTTED,
				PreferenceMessages.BoxViewPreferencePage_draw_frets_of_fretless_instruments_dotted, composite);
		addField(drawFretlessFretsDottedEditor);

		// gray lines
		final BooleanFieldEditor grayFrameEditor = new BooleanFieldEditor(Preferences.BOX_VIEW_FRAME_GRAY_COLOR,
				PreferenceMessages.BoxViewPreferencePage_gray_frame, composite);
		addField(grayFrameEditor);

		return parentComposite;
	}

	private Control createFretNumberPage(final Composite parent) {

		final Composite parentComposite = new Composite(parent, SWT.NONE);
		GridLayoutFactory.fillDefaults().extendedMargins(5, 5, 10, 0).applyTo(parentComposite);

		final Composite composite = new Composite(parentComposite, SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(composite);

		final Position horizontalValue = Position.valueOf(prefs
				.getString(Preferences.BOX_VIEW_FRET_NUMBERS_HORIZONTAL_POSITION));
		final Position verticalValue = Position.valueOf(prefs
				.getString(Preferences.BOX_VIEW_FRET_NUMBERS_VERTICAL_POSITION));

		// fret numbers mode
		fretNumbersModeEditor = new RadioGroupFieldEditor(Preferences.BOX_VIEW_FRET_NUMBERS_MODE,
				PreferenceMessages.ViewPreferencePage_numerals, 2, new String[][] {
						{ PreferenceMessages.ViewPreferencePage_arabic_numerals, UIConstants.NUMERALS_MODE_ARABIC },
						{ PreferenceMessages.ViewPreferencePage_roman_numerals, UIConstants.NUMERALS_MODE_ROMAN } },
				composite, true);
		addField(fretNumbersModeEditor);

		// position (horizontal presentation)
		positionGroup = new Group(composite, SWT.NONE);
		positionGroup.setText(PreferenceMessages.BoxViewPreferencePage_position);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(positionGroup);
		GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).margins(5, 5).applyTo(positionGroup);

		final Label horizontalLabel = new Label(positionGroup, SWT.NONE);
		horizontalLabel.setText(PreferenceMessages.BoxViewPreferencePage_horizontal_presentation_mode);
		final Composite horizontalComposite = new Composite(positionGroup, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).applyTo(horizontalComposite);
		topButton = new Button(horizontalComposite, SWT.RADIO);
		topButton.setText(PreferenceMessages.BoxViewPreferencePage_top);
		topButton.setSelection(horizontalValue == Position.TOP);
		bottomButton = new Button(horizontalComposite, SWT.RADIO);
		bottomButton.setText(PreferenceMessages.BoxViewPreferencePage_bottom);
		bottomButton.setSelection(horizontalValue == Position.BOTTOM);
		horizontalLabel.setFont(topButton.getFont());

		final Label verticalLabel = new Label(positionGroup, SWT.NONE);
		verticalLabel.setText(PreferenceMessages.BoxViewPreferencePage_vertical_presentation_mode);
		final Composite verticalComposite = new Composite(positionGroup, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).applyTo(verticalComposite);
		leftButton = new Button(verticalComposite, SWT.RADIO);
		leftButton.setText(PreferenceMessages.BoxViewPreferencePage_left);
		leftButton.setSelection(verticalValue == Position.LEFT);
		rightButton = new Button(verticalComposite, SWT.RADIO);
		rightButton.setText(PreferenceMessages.BoxViewPreferencePage_right);
		rightButton.setSelection(verticalValue == Position.RIGHT);
		verticalLabel.setFont(leftButton.getFont());

		// show fret number for first fret
		final BooleanFieldEditor showFretNumberForFirstFretEditor = new BooleanFieldEditor(
				Preferences.BOX_VIEW_FRET_NUMBERS_VISIBLE_FOR_FIRST_FRET,
				PreferenceMessages.BoxViewPreferencePage_show_fret_number_for_first_fret, composite);
		addField(showFretNumberForFirstFretEditor);

		// frame fret number
		final BooleanFieldEditor frameFretNumberEditor = new BooleanFieldEditor(
				Preferences.BOX_VIEW_FRET_NUMBERS_FRAMED, PreferenceMessages.BoxViewPreferencePage_frame_fret_number,
				composite);
		addField(frameFretNumberEditor);

		// gray fret number
		final BooleanFieldEditor grayFretNumberEditor = new BooleanFieldEditor(
				Preferences.BOX_VIEW_FRET_NUMBERS_GRAY_COLOR,
				PreferenceMessages.BoxViewPreferencePage_gray_fret_number, composite);
		addField(grayFretNumberEditor);

		// update fonts
		fretNumbersModeEditor.getRadioBoxControl(composite).setFont(positionGroup.getFont());

		return parentComposite;
	}

	private Control createFingeringNotesIntervalsPage(final Composite parent) {

		final Composite parentComposite = new Composite(parent, SWT.NONE);
		GridLayoutFactory.fillDefaults().extendedMargins(5, 5, 10, 0).applyTo(parentComposite);

		final Composite composite = new Composite(parentComposite, SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(composite);

		// preference page links
		PreferenceLinkUtil.createFingeringNotesAndIntervalNamesLink(
				PreferenceLinkUtil.createMainLinkComposite(composite), (IWorkbenchPreferenceContainer) getContainer());

		// show muted strings
		final MyBooleanFieldEditor showMutedStringsEditor = new MyBooleanFieldEditor(
				Preferences.BOX_VIEW_SHOW_MUTED_STRINGS, PreferenceMessages.BoxViewPreferencePage_show_muted_strings,
				composite);
		showMutedStringsButton = showMutedStringsEditor.getButton(composite);
		addField(showMutedStringsEditor);

		createRootNoteSection(composite);
		createPointsGroup(composite);
		createFingeringGroup(composite);
		createBarreGroup(composite);
		createEmptyMutedStringsGroup(composite);
		createCopyFromFretboardButton(composite);

		updateTexts();

		return parentComposite;
	}

	private void createRootNoteSection(final Composite parent) {

		// highlight root note
		final MyBooleanFieldEditor highlightRootNoteEditor = new MyBooleanFieldEditor(
				Preferences.BOX_VIEW_HIGHLIGHT_ROOT_NOTE, PreferenceMessages.ViewPreferencePage_highlight_root_note,
				parent);
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
				.getBoolean(Preferences.BOX_VIEW_HIGHLIGHT_ROOT_NOTE_WITH_COLOR));

		// square shape
		highlightRootNoteWithShapeButton = new Button(rootNoteComposite, SWT.CHECK);
		highlightRootNoteWithShapeButton.setText(PreferenceMessages.ViewPreferencePage_highlight_root_note_with_shape);
		highlightRootNoteWithShapeButton.setSelection(prefs
				.getBoolean(Preferences.BOX_VIEW_HIGHLIGHT_ROOT_NOTE_WITH_SHAPE));
	}

	private void createPointsGroup(final Composite parent) {

		// points group
		final Group pointSizeGroup = new Group(parent, SWT.NONE);
		pointSizeGroup.setText(PreferenceMessages.PreferencePage_points_settings);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(pointSizeGroup);
		GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).margins(5, 5).applyTo(pointSizeGroup);

		// point size
		final FigureSizeMode pointSize = FigureSizeMode.valueOf(prefs.getString(Preferences.BOX_VIEW_POINTS_SIZE));
		final Label pointSizeLabel = new Label(pointSizeGroup, SWT.NONE);
		pointSizeLabel.setText(PreferenceMessages.BoxViewPreferencePage_size + ":"); //$NON-NLS-1$
		GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.TOP).applyTo(pointSizeLabel);
		final Composite pointSizeComposite = new Composite(pointSizeGroup, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(3).equalWidth(false).applyTo(pointSizeComposite);
		smallPointSizeButton = new Button(pointSizeComposite, SWT.RADIO);
		smallPointSizeButton.setText(PreferenceMessages.BoxViewPreferencePage_small);
		smallPointSizeButton.setSelection(pointSize == FigureSizeMode.SMALL);
		mediumPointSizeButton = new Button(pointSizeComposite, SWT.RADIO);
		mediumPointSizeButton.setText(PreferenceMessages.BoxViewPreferencePage_medium);
		mediumPointSizeButton.setSelection(pointSize == FigureSizeMode.MEDIUM);
		largePointSizeButton = new Button(pointSizeComposite, SWT.RADIO);
		largePointSizeButton.setText(PreferenceMessages.BoxViewPreferencePage_large);
		largePointSizeButton.setSelection(pointSize == FigureSizeMode.LARGE);
		pointSizeLabel.setFont(smallPointSizeButton.getFont());

		// point background color
		final BackgroundColorMode pointBackground = BackgroundColorMode.valueOf(prefs
				.getString(Preferences.BOX_VIEW_POINTS_BACKGROUND));
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

	private void createFingeringGroup(final Composite parent) {

		// fingering group
		final Group fingeringGroup = new Group(parent, SWT.NONE);
		fingeringGroup.setText(PreferenceMessages.ViewPreferencePage_fingering_notes_intervals_settings);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(fingeringGroup);
		GridLayoutFactory.fillDefaults().numColumns(2).margins(5, 5).applyTo(fingeringGroup);

		// fingering background color
		final Label outsideLabel = new Label(fingeringGroup, SWT.RIGHT);
		outsideLabel.setText(PreferenceMessages.BoxViewPreferencePage_show_outside_box + ":"); //$NON-NLS-1$
		GridDataFactory.fillDefaults().align(SWT.END, SWT.CENTER).applyTo(outsideLabel);
		final Composite outsideComposite = new Composite(fingeringGroup, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(4).equalWidth(false).applyTo(outsideComposite);
		showFingeringOutsideButton = new Button(outsideComposite, SWT.CHECK);
		showFingeringOutsideButton.setText(PreferenceMessages.BoxViewPreferencePage_fingering);
		showFingeringOutsideButton.setSelection(prefs.getBoolean(Preferences.BOX_VIEW_SHOW_FINGERING_OUTSIDE_BOX));
		showNotesOutsideButton = new Button(outsideComposite, SWT.CHECK);
		showNotesOutsideButton.setText(PreferenceMessages.BoxViewPreferencePage_notes);
		showNotesOutsideButton.setSelection(prefs.getBoolean(Preferences.BOX_VIEW_SHOW_NOTES_OUTSIDE_BOX));
		showIntervalsOutsideButton = new Button(outsideComposite, SWT.CHECK);
		showIntervalsOutsideButton.setText(PreferenceMessages.BoxViewPreferencePage_intervals);
		showIntervalsOutsideButton.setSelection(prefs.getBoolean(Preferences.BOX_VIEW_SHOW_INTERVALS_OUTSIDE_BOX));

		final Label sep1 = new Label(fingeringGroup, SWT.HORIZONTAL | SWT.SEPARATOR);
		GridDataFactory.fillDefaults().span(2, 1).grab(true, false).applyTo(sep1);

		// frame inside elements
		frameInsideButton = new Button(fingeringGroup, SWT.CHECK);
		frameInsideButton.setSelection(prefs.getBoolean(Preferences.BOX_VIEW_FRAME_INSIDE));
		GridDataFactory.fillDefaults().span(2, 1).grab(true, false).applyTo(frameInsideButton);
		outsideLabel.setFont(frameInsideButton.getFont());

		// frame notes & intervals
		frameOutsideButton = new Button(fingeringGroup, SWT.CHECK);
		frameOutsideButton.setSelection(prefs.getBoolean(Preferences.BOX_VIEW_FRAME_OUTSIDE));
		GridDataFactory.fillDefaults().span(2, 1).grab(true, false).applyTo(frameOutsideButton);

		final Label sep2 = new Label(fingeringGroup, SWT.HORIZONTAL | SWT.SEPARATOR);
		GridDataFactory.fillDefaults().span(2, 1).grab(true, false).applyTo(sep2);

		// inside background color
		final BackgroundColorMode insideBackground = BackgroundColorMode.valueOf(prefs
				.getString(Preferences.BOX_VIEW_BACKGROUND_INSIDE));
		final Label insideBackgroundLabel = new Label(fingeringGroup, SWT.RIGHT);
		insideBackgroundLabel.setText(PreferenceMessages.ViewPreferencePage_background_color
				+ " (" + PreferenceMessages.BoxViewPreferencePage_inside + "):"); //$NON-NLS-1$ //$NON-NLS-2$
		insideBackgroundLabel.setFont(frameInsideButton.getFont());
		GridDataFactory.fillDefaults().align(SWT.END, SWT.CENTER).applyTo(insideBackgroundLabel);
		final Composite insideBackgroundComposite = new Composite(fingeringGroup, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(3).equalWidth(false).applyTo(insideBackgroundComposite);
		whiteInsideButton = new Button(insideBackgroundComposite, SWT.RADIO);
		whiteInsideButton.setText(PreferenceMessages.ViewPreferencePage_white);
		whiteInsideButton.setSelection(insideBackground == BackgroundColorMode.WHITE);
		blackInsideButton = new Button(insideBackgroundComposite, SWT.RADIO);
		blackInsideButton.setText(PreferenceMessages.ViewPreferencePage_black);
		blackInsideButton.setSelection(insideBackground == BackgroundColorMode.BLACK);
		coloredInsideButton = new Button(insideBackgroundComposite, SWT.RADIO);
		coloredInsideButton.setText(PreferenceMessages.ViewPreferencePage_colored);
		coloredInsideButton.setSelection(insideBackground == BackgroundColorMode.COLORED);

		// outside background color
		final BackgroundColorMode outsideBackground = BackgroundColorMode.valueOf(prefs
				.getString(Preferences.BOX_VIEW_BACKGROUND_OUTSIDE));
		final Label outsideBackgroundLabel = new Label(fingeringGroup, SWT.RIGHT);
		outsideBackgroundLabel.setText(PreferenceMessages.ViewPreferencePage_background_color
				+ " (" + PreferenceMessages.BoxViewPreferencePage_outside + "):"); //$NON-NLS-1$ //$NON-NLS-2$
		GridDataFactory.fillDefaults().align(SWT.END, SWT.CENTER).applyTo(outsideBackgroundLabel);
		final Composite outsideBackgroundComposite = new Composite(fingeringGroup, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(3).applyTo(outsideBackgroundComposite);
		whiteOutsideButton = new Button(outsideBackgroundComposite, SWT.RADIO);
		whiteOutsideButton.setText(PreferenceMessages.ViewPreferencePage_white);
		whiteOutsideButton.setSelection(outsideBackground == BackgroundColorMode.WHITE);
		blackOutsideButton = new Button(outsideBackgroundComposite, SWT.RADIO);
		blackOutsideButton.setText(PreferenceMessages.ViewPreferencePage_black);
		blackOutsideButton.setSelection(outsideBackground == BackgroundColorMode.BLACK);
		coloredOutsideButton = new Button(outsideBackgroundComposite, SWT.RADIO);
		coloredOutsideButton.setText(PreferenceMessages.ViewPreferencePage_colored);
		coloredOutsideButton.setSelection(outsideBackground == BackgroundColorMode.COLORED);
		outsideBackgroundLabel.setFont(whiteOutsideButton.getFont());
	}

	private void createBarreGroup(final Composite parent) {

		// barre group
		final Group barreGroup = new Group(parent, SWT.NONE);
		barreGroup.setText(PreferenceMessages.ViewPreferencePage_barre_settings);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(barreGroup);
		GridLayoutFactory.fillDefaults().margins(5, 5).applyTo(barreGroup);

		// draw barre
		drawBarreButton = new Button(barreGroup, SWT.CHECK);
		drawBarreButton.setText(PreferenceMessages.ViewPreferencePage_draw_barre);
		drawBarreButton.setSelection(prefs.getBoolean(Preferences.BOX_VIEW_SHOW_BARRE));

		// barre mode
		final BarreMode barreMode = BarreMode.valueOf(prefs.getString(Preferences.BOX_VIEW_BARRE_MODE));
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
		lineWidthCombo.select(prefs.getInt(Preferences.BOX_VIEW_BARRE_LINE_WIDTH) - 1);
		new Label(barLineWidthAndBackgroundComposite, SWT.NONE);
		lineWidthLabel.setFont(lineWidthCombo.getFont());

		// bar background color
		final BackgroundColorMode barBackground = BackgroundColorMode.valueOf(prefs
				.getString(Preferences.BOX_VIEW_BARRE_BAR_BACKGROUND));
		final Label barBackgroundLabel = new Label(barLineWidthAndBackgroundComposite, SWT.NONE);
		barBackgroundLabel.setText(PreferenceMessages.ViewPreferencePage_background_color + ":"); //$NON-NLS-1$
		GridDataFactory.fillDefaults().indent(HORIZONTAL_INDENT * 2, 0).align(SWT.RIGHT, SWT.TOP)
				.applyTo(barBackgroundLabel);
		sameColorBarButton = new Button(barLineWidthAndBackgroundComposite, SWT.RADIO);
		sameColorBarButton.setText(PreferenceMessages.BoxViewPreferencePage_same_as_points_and_fingering);
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
		showInsideBarButton.setSelection(prefs.getBoolean(Preferences.BOX_VIEW_BARRE_BAR_SHOW_ELEMENTS_INSIDE));
		GridDataFactory.fillDefaults().indent(HORIZONTAL_INDENT * 2, 0).grab(true, false).applyTo(showInsideBarButton);

		// encircle fingering in bar
		singleFingeringInBarButton = new Button(barreGroup, SWT.CHECK);
		singleFingeringInBarButton.setText(PreferenceMessages.ViewPreferencePage_single_fingering_in_bar);
		singleFingeringInBarButton.setSelection(prefs
				.getBoolean(Preferences.BOX_VIEW_BARRE_BAR_SHOW_SINGLE_FINGER_NUMBER));
		GridDataFactory.fillDefaults().indent(HORIZONTAL_INDENT * 3, 0).applyTo(singleFingeringInBarButton);
	}

	private void createEmptyMutedStringsGroup(final Composite parent) {

		// empty & muted strings group
		emptyStringsGroup = new Group(parent, SWT.NONE);
		emptyStringsGroup.setText(PreferenceMessages.BoxViewPreferencePage_empty_and_muted_strings_settings);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(emptyStringsGroup);
		GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).margins(5, 5).applyTo(emptyStringsGroup);

		// place empty & muted strings close to frame
		placeEmptyStringsCloseToFrameButton = new Button(emptyStringsGroup, SWT.CHECK);
		placeEmptyStringsCloseToFrameButton
				.setText(PreferenceMessages.BoxViewPreferencePage_place_empty_and_muted_strings_close_to_frame);
		placeEmptyStringsCloseToFrameButton.setSelection(prefs
				.getBoolean(Preferences.BOX_VIEW_EMPTY_AND_MUTED_STRINGS_CLOSE_TO_FRAME));
		GridDataFactory.fillDefaults().span(3, 1).applyTo(placeEmptyStringsCloseToFrameButton);

		// white empty strings background color
		whiteEmptyStringButton = new Button(emptyStringsGroup, SWT.CHECK);
		whiteEmptyStringButton.setText(PreferenceMessages.ViewPreferencePage_white_empty_strings_background);
		whiteEmptyStringButton.setSelection(prefs.getBoolean(Preferences.BOX_VIEW_EMPTY_STRINGS_BACKGROUND_WHITE));
		GridDataFactory.fillDefaults().span(3, 1).applyTo(whiteEmptyStringButton);

		// empty & muted strings size
		final FigureSizeMode emptyStringSize = FigureSizeMode.valueOf(prefs
				.getString(Preferences.BOX_VIEW_EMPTY_AND_MUTED_STRINGS_SIZE));
		final Label emptyStringSizeLabel = new Label(emptyStringsGroup, SWT.NONE);
		emptyStringSizeLabel.setText(PreferenceMessages.BoxViewPreferencePage_size + ":"); //$NON-NLS-1$
		GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.TOP).applyTo(emptyStringSizeLabel);
		final Composite emptyAndMutedStringsComposite = new Composite(emptyStringsGroup, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(3).equalWidth(false).applyTo(emptyAndMutedStringsComposite);
		sameEmptyStringSizeButton = new Button(emptyAndMutedStringsComposite, SWT.RADIO);
		sameEmptyStringSizeButton.setText(PreferenceMessages.BoxViewPreferencePage_same_as_points_and_fingering);
		sameEmptyStringSizeButton.setSelection(emptyStringSize == FigureSizeMode.SAME);
		GridDataFactory.fillDefaults().span(3, 1).applyTo(sameEmptyStringSizeButton);
		smallEmptyStringSizeButton = new Button(emptyAndMutedStringsComposite, SWT.RADIO);
		smallEmptyStringSizeButton.setText(PreferenceMessages.BoxViewPreferencePage_small);
		smallEmptyStringSizeButton.setSelection(emptyStringSize == FigureSizeMode.SMALL);
		mediumEmptyStringSizeButton = new Button(emptyAndMutedStringsComposite, SWT.RADIO);
		mediumEmptyStringSizeButton.setText(PreferenceMessages.BoxViewPreferencePage_medium);
		mediumEmptyStringSizeButton.setSelection(emptyStringSize == FigureSizeMode.MEDIUM);
		largeEmptyStringSizeButton = new Button(emptyAndMutedStringsComposite, SWT.RADIO);
		largeEmptyStringSizeButton.setText(PreferenceMessages.BoxViewPreferencePage_large);
		largeEmptyStringSizeButton.setSelection(emptyStringSize == FigureSizeMode.LARGE);
		emptyStringSizeLabel.setFont(sameEmptyStringSizeButton.getFont());
	}

	private void createCopyFromFretboardButton(final Composite composite) {

		final Composite copyComposite = new Composite(composite, SWT.NONE);
		GridLayoutFactory.fillDefaults().applyTo(copyComposite);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(copyComposite);

		// copy button
		final Button copyButton = new Button(copyComposite, SWT.PUSH);
		copyButton.setText(PreferenceMessages.BoxViewPreferencePage_copy_fretboard_settings);
		copyButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {

				// muted strings
				showMutedStringsButton.setSelection(prefs.getBoolean(Preferences.FRETBOARD_VIEW_SHOW_MUTED_STRINGS));

				// highlight root note
				highlightRootNoteButton.setSelection(prefs.getBoolean(Preferences.FRETBOARD_VIEW_HIGHLIGHT_ROOT_NOTE));
				highlightRootNoteWithColorButton.setSelection(prefs
						.getBoolean(Preferences.FRETBOARD_VIEW_HIGHLIGHT_ROOT_NOTE_WITH_COLOR));
				highlightRootNoteWithShapeButton.setSelection(prefs
						.getBoolean(Preferences.FRETBOARD_VIEW_HIGHLIGHT_ROOT_NOTE_WITH_SHAPE));

				// points background
				final BackgroundColorMode pointsBgMode = BackgroundColorMode.valueOf(prefs
						.getString(Preferences.FRETBOARD_VIEW_POINTS_BACKGROUND));
				whitePointsButton.setSelection(pointsBgMode == BackgroundColorMode.WHITE);
				blackPointsButton.setSelection(pointsBgMode == BackgroundColorMode.BLACK);

				// fingering/notes/intervals
				frameInsideButton.setSelection(prefs.getBoolean(Preferences.FRETBOARD_VIEW_FRAME_FINGERING));
				whiteEmptyStringButton.setSelection(prefs
						.getBoolean(Preferences.FRETBOARD_VIEW_EMPTY_STRINGS_BACKGROUND_WHITE));
				final BackgroundColorMode insideBgMode = BackgroundColorMode.valueOf(prefs
						.getString(Preferences.FRETBOARD_VIEW_FINGERING_BACKGROUND));
				whiteInsideButton.setSelection(insideBgMode == BackgroundColorMode.WHITE);
				blackInsideButton.setSelection(insideBgMode == BackgroundColorMode.BLACK);
				coloredInsideButton.setSelection(insideBgMode == BackgroundColorMode.COLORED);

				// barre
				drawBarreButton.setSelection(prefs.getBoolean(Preferences.FRETBOARD_VIEW_SHOW_BARRE));
				final BarreMode barreMode = BarreMode.valueOf(prefs.getString(Preferences.FRETBOARD_VIEW_BARRE_MODE));
				barreAsLineButton.setSelection(barreMode == BarreMode.LINE);
				barreAsArcButton.setSelection(barreMode == BarreMode.ARC);
				barreAsBarButton.setSelection(barreMode == BarreMode.BAR);
				lineWidthCombo.select(prefs.getInt(Preferences.FRETBOARD_VIEW_BARRE_LINE_WIDTH) - 1);
				final BackgroundColorMode barBgMode = BackgroundColorMode.valueOf(prefs
						.getString(Preferences.FRETBOARD_VIEW_BARRE_BAR_BACKGROUND));
				sameColorBarButton.setSelection(barBgMode == BackgroundColorMode.SAME);
				whiteBarButton.setSelection(barBgMode == BackgroundColorMode.WHITE);
				blackBarButton.setSelection(barBgMode == BackgroundColorMode.BLACK);
				showInsideBarButton.setSelection(prefs
						.getBoolean(Preferences.FRETBOARD_VIEW_BARRE_BAR_SHOW_ELEMENTS_INSIDE));
				singleFingeringInBarButton.setSelection(prefs
						.getBoolean(Preferences.FRETBOARD_VIEW_BARRE_BAR_SHOW_SINGLE_FINGER_NUMBER));

				// update resulting selections and enablements
				updateEnablement();
			}
		});
		GridDataFactory.fillDefaults().applyTo(copyButton);

		// fretboard view settings link
		final PreferenceLinkArea link = PreferenceLinkUtil.createLink(
				PreferenceLinkUtil.createMainLinkComposite(copyComposite, 1, 5),
				(IWorkbenchPreferenceContainer) getContainer(),
				PreferenceMessages.BoxViewPreferencePage_see_settings_for_fretboard, FretboardViewPreferencePage.ID);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(link.getControl());
	}

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

		final SelectionAdapter outsideListener = new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				updateTexts();
				updateEnablement();
			}
		};
		showFingeringOutsideButton.addSelectionListener(outsideListener);
		showNotesOutsideButton.addSelectionListener(outsideListener);
		showIntervalsOutsideButton.addSelectionListener(outsideListener);

		frameInsideButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				updateEnablement();
			}
		});

		frameOutsideButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				updateEnablement();
			}
		});

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
	}

	private void updateTexts() {

		final StringBuffer outsideBuf = new StringBuffer(PreferenceMessages.BoxViewPreferencePage_frame + WHITE_SPACE);
		final StringBuffer insideBuf = new StringBuffer(PreferenceMessages.BoxViewPreferencePage_frame + WHITE_SPACE);
		final StringBuffer barBuf = new StringBuffer(PreferenceMessages.BoxViewPreferencePage_show + WHITE_SPACE);

		final boolean outsideFingering = showFingeringOutsideButton.getSelection();
		final boolean outsideNotes = showNotesOutsideButton.getSelection();
		final boolean outsideIntervals = showIntervalsOutsideButton.getSelection();

		// fingering
		if (outsideFingering) {
			outsideBuf.append(PreferenceMessages.BoxViewPreferencePage_fingering);
			if (outsideNotes || outsideIntervals) {
				outsideBuf.append(SEPARATOR);
			}
		} else {
			insideBuf.append(PreferenceMessages.BoxViewPreferencePage_fingering);
			barBuf.append(PreferenceMessages.BoxViewPreferencePage_fingering);
			if (!outsideNotes || !outsideIntervals) {
				insideBuf.append(SEPARATOR);
				barBuf.append(SEPARATOR);
			}
		}

		// notes
		if (outsideNotes) {
			outsideBuf.append(PreferenceMessages.BoxViewPreferencePage_notes);
			if (outsideIntervals) {
				outsideBuf.append(SEPARATOR);
			}
		} else {
			insideBuf.append(PreferenceMessages.BoxViewPreferencePage_notes);
			barBuf.append(PreferenceMessages.BoxViewPreferencePage_notes);
			if (!outsideIntervals) {
				insideBuf.append(SEPARATOR);
				barBuf.append(SEPARATOR);
			}
		}

		// intervals
		if (outsideIntervals) {
			outsideBuf.append(PreferenceMessages.BoxViewPreferencePage_intervals);
		} else {
			insideBuf.append(PreferenceMessages.BoxViewPreferencePage_intervals);
			barBuf.append(PreferenceMessages.BoxViewPreferencePage_intervals);
		}

		// separating space
		if (outsideFingering || outsideNotes || outsideIntervals) {
			outsideBuf.append(WHITE_SPACE);
		}
		if (!outsideFingering || !outsideNotes || !outsideIntervals) {
			insideBuf.append(WHITE_SPACE);
			barBuf.append(WHITE_SPACE);
		}

		outsideBuf.append(PreferenceMessages.BoxViewPreferencePage_outside_the_box);
		insideBuf.append(PreferenceMessages.BoxViewPreferencePage_inside_the_box);
		barBuf.append(PreferenceMessages.BoxViewPreferencePage_inside_the_bar);

		frameOutsideButton.setText(outsideBuf.toString());
		frameInsideButton.setText(insideBuf.toString());
		showInsideBarButton.setText(barBuf.toString());
	}

	private void updateFingeringEnablement() {

		final boolean showRootNote = highlightRootNoteButton.getSelection();
		final boolean atLeastOneOutside = showFingeringOutsideButton.getSelection()
				|| showNotesOutsideButton.getSelection() || showIntervalsOutsideButton.getSelection();
		final boolean atLeastOneInside = !showFingeringOutsideButton.getSelection()
				|| !showNotesOutsideButton.getSelection() || !showIntervalsOutsideButton.getSelection();

		frameInsideButton.setEnabled(atLeastOneInside && !showRootNote);
		frameOutsideButton.setEnabled(atLeastOneOutside);

		if (showRootNote) {
			frameInsideButton.setSelection(true);
			if (coloredInsideButton.getSelection()) {
				coloredInsideButton.setSelection(false);
				whiteInsideButton.setSelection(true);
			}
		}

		final boolean insideEnabled = frameInsideButton.getSelection() && atLeastOneInside;
		whiteInsideButton.setEnabled(insideEnabled);
		blackInsideButton.setEnabled(insideEnabled);
		coloredInsideButton.setEnabled(insideEnabled && !showRootNote);

		final boolean outsideEnabled = frameOutsideButton.getSelection() && atLeastOneOutside;
		whiteOutsideButton.setEnabled(outsideEnabled);
		blackOutsideButton.setEnabled(outsideEnabled);
		coloredOutsideButton.setEnabled(outsideEnabled);
	}

	private void updateBarreEnablement() {

		// update selections
		final boolean showRootNote = highlightRootNoteButton.getSelection();
		final boolean insideFingering = !showFingeringOutsideButton.getSelection();

		if (showRootNote) {
			// bar color
			sameColorBarButton.setSelection(true);
			whiteBarButton.setSelection(false);
			blackBarButton.setSelection(false);
			// fingering in bar
			showInsideBarButton.setSelection(true);
			// empty string color
			whiteEmptyStringButton.setSelection(false);
		}
		if (showRootNote || !insideFingering) {
			singleFingeringInBarButton.setSelection(false);
		}

		final boolean outsideFingering = showFingeringOutsideButton.getSelection();
		final boolean frameFingering = frameInsideButton.getSelection();
		if (!frameFingering && !outsideFingering) {
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

		barreAsLineButton.setEnabled(enabled && (frameFingering || outsideFingering));
		barreAsArcButton.setEnabled(enabled && (frameFingering || outsideFingering));
		barreAsBarButton.setEnabled(enabled);

		lineWidthCombo.setEnabled(enabled && enabledLineOrArc);
		sameColorBarButton.setEnabled(enabled && enabledBar);
		whiteBarButton.setEnabled(enabled && enabledBar);
		blackBarButton.setEnabled(enabled && enabledBar);
		showInsideBarButton.setEnabled(enabled && enabledBar && !showRootNote);
		sameColorBarButton.setEnabled(enabled && enabledBar && !showRootNote);
		whiteBarButton.setEnabled(enabled && enabledBar && !showRootNote);
		blackBarButton.setEnabled(enabled && enabledBar && !showRootNote);
		singleFingeringInBarButton.setEnabled(enabled && enabledBar && enabledBarFingering && !showRootNote
				&& insideFingering);
		highlightRootNoteWithColorButton.setEnabled(showRootNote && !barSelected);
		highlightRootNoteWithShapeButton.setEnabled(showRootNote);

		whiteEmptyStringButton.setEnabled(!showRootNote);
	}

	/* --- perform buttons handling --- */

	@Override
	public boolean performOk() {

		// highlight root note
		prefs.setValue(Preferences.BOX_VIEW_HIGHLIGHT_ROOT_NOTE_WITH_COLOR,
				highlightRootNoteWithColorButton.getSelection());
		prefs.setValue(Preferences.BOX_VIEW_HIGHLIGHT_ROOT_NOTE_WITH_SHAPE,
				highlightRootNoteWithShapeButton.getSelection());

		// points
		prefs.setValue(
				Preferences.BOX_VIEW_POINTS_SIZE,
				smallPointSizeButton.getSelection() ? FigureSizeMode.SMALL.toString() : largePointSizeButton
						.getSelection() ? FigureSizeMode.LARGE.toString() : FigureSizeMode.MEDIUM.toString());
		prefs.setValue(
				Preferences.BOX_VIEW_POINTS_BACKGROUND,
				whitePointsButton.getSelection() ? BackgroundColorMode.WHITE.toString() : BackgroundColorMode.BLACK
						.toString());

		// inside/outside presentation
		final boolean changedFingering = prefs.getBoolean(Preferences.BOX_VIEW_SHOW_FINGERING_OUTSIDE_BOX) != showFingeringOutsideButton
				.getSelection();
		final boolean changedNotes = prefs.getBoolean(Preferences.BOX_VIEW_SHOW_NOTES_OUTSIDE_BOX) != showNotesOutsideButton
				.getSelection();
		final boolean changedIntervals = prefs.getBoolean(Preferences.BOX_VIEW_SHOW_INTERVALS_OUTSIDE_BOX) != showIntervalsOutsideButton
				.getSelection();
		prefs.setValue(Preferences.BOX_VIEW_SHOW_FINGERING_OUTSIDE_BOX, showFingeringOutsideButton.getSelection());
		prefs.setValue(Preferences.BOX_VIEW_SHOW_NOTES_OUTSIDE_BOX, showNotesOutsideButton.getSelection());
		prefs.setValue(Preferences.BOX_VIEW_SHOW_INTERVALS_OUTSIDE_BOX, showIntervalsOutsideButton.getSelection());
		if (changedFingering || changedNotes || changedIntervals) {
			prefs.firePropertyChangeEvent(BOX_VIEW_SHOW_OUTSIDE_BOX_CHANGED, null, null);
		}

		// fingering/notes/intervals
		prefs.setValue(Preferences.BOX_VIEW_FRAME_INSIDE, frameInsideButton.getSelection());
		prefs.setValue(Preferences.BOX_VIEW_FRAME_OUTSIDE, frameOutsideButton.getSelection());
		prefs.setValue(
				Preferences.BOX_VIEW_BACKGROUND_INSIDE,
				whiteInsideButton.getSelection() ? BackgroundColorMode.WHITE.toString() : blackInsideButton
						.getSelection() ? BackgroundColorMode.BLACK.toString() : BackgroundColorMode.COLORED.toString());
		prefs.setValue(
				Preferences.BOX_VIEW_BACKGROUND_OUTSIDE,
				whiteOutsideButton.getSelection() ? BackgroundColorMode.WHITE.toString() : blackOutsideButton
						.getSelection() ? BackgroundColorMode.BLACK.toString() : BackgroundColorMode.COLORED.toString());

		// barre
		prefs.setValue(Preferences.BOX_VIEW_SHOW_BARRE, drawBarreButton.getSelection());
		prefs.setValue(Preferences.BOX_VIEW_BARRE_MODE, barreAsArcButton.getSelection() ? BarreMode.ARC.toString()
				: barreAsBarButton.getSelection() ? BarreMode.BAR.toString() : BarreMode.LINE.toString());
		prefs.setValue(Preferences.BOX_VIEW_BARRE_LINE_WIDTH, lineWidthCombo.getSelectionIndex() + 1);
		prefs.setValue(Preferences.BOX_VIEW_BARRE_BAR_BACKGROUND,
				whiteBarButton.getSelection() ? BackgroundColorMode.WHITE.toString()
						: blackBarButton.getSelection() ? BackgroundColorMode.BLACK.toString()
								: BackgroundColorMode.SAME.toString());
		prefs.setValue(Preferences.BOX_VIEW_BARRE_BAR_SHOW_ELEMENTS_INSIDE, showInsideBarButton.getSelection());
		prefs.setValue(Preferences.BOX_VIEW_BARRE_BAR_SHOW_SINGLE_FINGER_NUMBER,
				singleFingeringInBarButton.getSelection());

		// empty strings
		prefs.setValue(Preferences.BOX_VIEW_EMPTY_AND_MUTED_STRINGS_CLOSE_TO_FRAME,
				placeEmptyStringsCloseToFrameButton.getSelection());
		prefs.setValue(Preferences.BOX_VIEW_EMPTY_STRINGS_BACKGROUND_WHITE, whiteEmptyStringButton.getSelection());
		prefs.setValue(Preferences.BOX_VIEW_EMPTY_AND_MUTED_STRINGS_SIZE,
				smallEmptyStringSizeButton.getSelection() ? FigureSizeMode.SMALL.toString()
						: mediumEmptyStringSizeButton.getSelection() ? FigureSizeMode.MEDIUM.toString()
								: largeEmptyStringSizeButton.getSelection() ? FigureSizeMode.LARGE.toString()
										: FigureSizeMode.SAME.toString());

		// frame
		prefs.setValue(Preferences.BOX_VIEW_FRAME_MIN_FRET_COUNT, Integer.parseInt(minFretNumberText.getText()));
		prefs.setValue(Preferences.BOX_VIEW_FRAME_MAX_UNASSIGNED_FRET_COUNT,
				Integer.parseInt(maxNumberOfStartingEmptyFretsText.getText()));

		// fret numbers
		prefs.setValue(Preferences.BOX_VIEW_FRET_NUMBERS_HORIZONTAL_POSITION,
				topButton.getSelection() ? Position.TOP.toString() : Position.BOTTOM.toString());
		prefs.setValue(Preferences.BOX_VIEW_FRET_NUMBERS_VERTICAL_POSITION,
				leftButton.getSelection() ? Position.LEFT.toString() : Position.RIGHT.toString());

		return super.performOk();
	}

	@Override
	protected void performDefaults() {
		super.performDefaults();

		// highlight root note
		highlightRootNoteWithColorButton.setSelection(prefs
				.getDefaultBoolean(Preferences.BOX_VIEW_HIGHLIGHT_ROOT_NOTE_WITH_COLOR));
		highlightRootNoteWithShapeButton.setSelection(prefs
				.getDefaultBoolean(Preferences.BOX_VIEW_HIGHLIGHT_ROOT_NOTE_WITH_SHAPE));

		// points
		final FigureSizeMode pointSizeValue = FigureSizeMode.valueOf(prefs
				.getDefaultString(Preferences.BOX_VIEW_POINTS_SIZE));
		smallPointSizeButton.setSelection(pointSizeValue == FigureSizeMode.SMALL);
		mediumPointSizeButton.setSelection(pointSizeValue == FigureSizeMode.MEDIUM);
		largePointSizeButton.setSelection(pointSizeValue == FigureSizeMode.LARGE);
		final BackgroundColorMode pointBackgroundValue = BackgroundColorMode.valueOf(prefs
				.getDefaultString(Preferences.BOX_VIEW_POINTS_BACKGROUND));
		whitePointsButton.setSelection(pointBackgroundValue == BackgroundColorMode.WHITE);
		blackPointsButton.setSelection(pointBackgroundValue == BackgroundColorMode.BLACK);

		// fingering/notes/intervals
		showFingeringOutsideButton.setSelection(prefs
				.getDefaultBoolean(Preferences.BOX_VIEW_SHOW_FINGERING_OUTSIDE_BOX));
		showNotesOutsideButton.setSelection(prefs.getDefaultBoolean(Preferences.BOX_VIEW_SHOW_NOTES_OUTSIDE_BOX));
		showIntervalsOutsideButton.setSelection(prefs
				.getDefaultBoolean(Preferences.BOX_VIEW_SHOW_INTERVALS_OUTSIDE_BOX));
		frameInsideButton.setSelection(prefs.getDefaultBoolean(Preferences.BOX_VIEW_FRAME_INSIDE));
		frameOutsideButton.setSelection(prefs.getDefaultBoolean(Preferences.BOX_VIEW_FRAME_OUTSIDE));
		final BackgroundColorMode fingeringBackgroundValue = BackgroundColorMode.valueOf(prefs
				.getDefaultString(Preferences.BOX_VIEW_BACKGROUND_INSIDE));
		whiteInsideButton.setSelection(fingeringBackgroundValue == BackgroundColorMode.WHITE);
		blackInsideButton.setSelection(fingeringBackgroundValue == BackgroundColorMode.BLACK);
		coloredInsideButton.setSelection(fingeringBackgroundValue == BackgroundColorMode.COLORED);
		final BackgroundColorMode notesBackgroundValue = BackgroundColorMode.valueOf(prefs
				.getDefaultString(Preferences.BOX_VIEW_BACKGROUND_OUTSIDE));
		whiteOutsideButton.setSelection(notesBackgroundValue == BackgroundColorMode.WHITE);
		blackOutsideButton.setSelection(notesBackgroundValue == BackgroundColorMode.BLACK);
		coloredOutsideButton.setSelection(notesBackgroundValue == BackgroundColorMode.COLORED);

		// barre
		drawBarreButton.setSelection(prefs.getDefaultBoolean(Preferences.BOX_VIEW_SHOW_BARRE));
		final BarreMode barreMode = BarreMode.valueOf(prefs.getDefaultString(Preferences.BOX_VIEW_BARRE_MODE));
		barreAsLineButton.setSelection(barreMode == BarreMode.LINE);
		barreAsArcButton.setSelection(barreMode == BarreMode.ARC);
		barreAsBarButton.setSelection(barreMode == BarreMode.BAR);
		lineWidthCombo.select(prefs.getDefaultInt(Preferences.BOX_VIEW_BARRE_LINE_WIDTH) - 1);
		final BackgroundColorMode barBackgroundValue = BackgroundColorMode.valueOf(prefs
				.getDefaultString(Preferences.BOX_VIEW_BARRE_BAR_BACKGROUND));
		sameColorBarButton.setSelection(barBackgroundValue == BackgroundColorMode.SAME);
		whiteBarButton.setSelection(barBackgroundValue == BackgroundColorMode.WHITE);
		blackBarButton.setSelection(barBackgroundValue == BackgroundColorMode.BLACK);
		showInsideBarButton.setSelection(prefs.getDefaultBoolean(Preferences.BOX_VIEW_BARRE_BAR_SHOW_ELEMENTS_INSIDE));
		singleFingeringInBarButton.setSelection(prefs
				.getDefaultBoolean(Preferences.BOX_VIEW_BARRE_BAR_SHOW_SINGLE_FINGER_NUMBER));

		// empty strings
		placeEmptyStringsCloseToFrameButton.setSelection(prefs
				.getDefaultBoolean(Preferences.BOX_VIEW_EMPTY_AND_MUTED_STRINGS_CLOSE_TO_FRAME));
		whiteEmptyStringButton.setSelection(prefs
				.getDefaultBoolean(Preferences.BOX_VIEW_EMPTY_STRINGS_BACKGROUND_WHITE));
		final FigureSizeMode emptyStringSizeValue = FigureSizeMode.valueOf(prefs
				.getDefaultString(Preferences.BOX_VIEW_EMPTY_AND_MUTED_STRINGS_SIZE));
		sameEmptyStringSizeButton.setSelection(emptyStringSizeValue == FigureSizeMode.SAME);
		smallEmptyStringSizeButton.setSelection(emptyStringSizeValue == FigureSizeMode.SMALL);
		mediumEmptyStringSizeButton.setSelection(emptyStringSizeValue == FigureSizeMode.MEDIUM);
		largeEmptyStringSizeButton.setSelection(emptyStringSizeValue == FigureSizeMode.LARGE);

		// frame
		minFretNumberText.setText("" + prefs.getDefaultInt(Preferences.BOX_VIEW_FRAME_MIN_FRET_COUNT)); //$NON-NLS-1$
		maxNumberOfStartingEmptyFretsText
				.setText("" + prefs.getDefaultInt(Preferences.BOX_VIEW_FRAME_MAX_UNASSIGNED_FRET_COUNT)); //$NON-NLS-1$

		// fret numbers
		final Position horizontalValue = Position.valueOf(prefs
				.getDefaultString(Preferences.BOX_VIEW_FRET_NUMBERS_HORIZONTAL_POSITION));
		final Position verticalValue = Position.valueOf(prefs
				.getDefaultString(Preferences.BOX_VIEW_FRET_NUMBERS_VERTICAL_POSITION));
		topButton.setSelection(horizontalValue == Position.TOP);
		bottomButton.setSelection(horizontalValue == Position.BOTTOM);
		leftButton.setSelection(verticalValue == Position.LEFT);
		rightButton.setSelection(verticalValue == Position.RIGHT);

		updateEnablement();
	}
}
