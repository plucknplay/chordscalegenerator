/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.figures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.RoundedRectangle;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;

import com.plucknplay.csg.core.Constants;
import com.plucknplay.csg.core.model.FretboardPosition;
import com.plucknplay.csg.core.model.Interval;
import com.plucknplay.csg.core.model.Note;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.UIConstants;
import com.plucknplay.csg.ui.model.BoxDraft;
import com.plucknplay.csg.ui.model.FretDraft.Barre;
import com.plucknplay.csg.ui.util.ArcConnection;
import com.plucknplay.csg.ui.util.CenterAnchor;
import com.plucknplay.csg.ui.util.FontManager;
import com.plucknplay.csg.ui.util.enums.BackgroundColorMode;
import com.plucknplay.csg.ui.util.enums.BarreMode;
import com.plucknplay.csg.ui.util.enums.BoxViewFigureMode;
import com.plucknplay.csg.ui.util.enums.BoxViewPresentationMode;
import com.plucknplay.csg.ui.util.enums.FigureSizeMode;
import com.plucknplay.csg.ui.util.enums.FingeringMode;

public class BoxAssignmentLayer extends AbstractBoxLayer {

	private final IPreferenceStore prefs;

	private Label startFretLabel;

	private List<Integer> figureSizes;
	private Map<Integer, List<MutedStringFigure>> mutedStringFiguresMap;
	private Map<Integer, List<RoundedRectangle2>> emptyStringPointsMap;
	private Map<FretboardPosition, RoundedRectangle2> pointsMap;
	private Map<FretboardPosition, Label> labelMap;
	private Map<FretboardPosition, BoxAssignmentFigure> boxAssignmentMap;
	private Map<Integer, List<BoxInfoFigure>> fingeringInfoMap;
	private Map<Integer, List<BoxInfoFigure>> notesInfoMap;
	private Map<Integer, List<BoxInfoFigure>> intervalsInfoMap;
	private Map<Barre, RoundedRectangle> barreFigureMap;
	private Map<Barre, ArcConnection> barreConnectionMap;

	private final XYLayout contentsLayout;

	public BoxAssignmentLayer(final BoxDraft boxDraft, final boolean showFingering, final boolean showFingeringOutside,
			final boolean showNotes, final boolean showNotesOutside, final boolean showIntervals,
			final boolean showIntervalsOutside) {

		super(boxDraft, showFingering, showFingeringOutside, showNotes, showNotesOutside, showIntervals,
				showIntervalsOutside);

		prefs = Activator.getDefault().getPreferenceStore();
		contentsLayout = new XYLayout();

		setLayoutManager(contentsLayout);
		init();
	}

	public void init() {
		removeAll();

		figureSizes = new ArrayList<Integer>();
		mutedStringFiguresMap = new HashMap<Integer, List<MutedStringFigure>>();
		emptyStringPointsMap = new HashMap<Integer, List<RoundedRectangle2>>();
		pointsMap = new HashMap<FretboardPosition, RoundedRectangle2>();
		labelMap = new HashMap<FretboardPosition, Label>();
		boxAssignmentMap = new HashMap<FretboardPosition, BoxAssignmentFigure>();
		fingeringInfoMap = new HashMap<Integer, List<BoxInfoFigure>>();
		notesInfoMap = new HashMap<Integer, List<BoxInfoFigure>>();
		intervalsInfoMap = new HashMap<Integer, List<BoxInfoFigure>>();
		barreFigureMap = new HashMap<Barre, RoundedRectangle>();
		barreConnectionMap = new HashMap<Barre, ArcConnection>();

		if (getCurrentInstrument() == null) {
			return;
		}

		final int stringCount = getCurrentInstrument().getStringCount();
		final int fretCount = getBoxDraft().getFretWidth();

		// determine preferences
		final BoxViewPresentationMode presentationMode = BoxViewPresentationMode.valueOf(prefs
				.getString(Preferences.BOX_VIEW_PRESENTATION_MODE));
		final boolean placeEmptyStringsCloseToFrame = prefs
				.getBoolean(Preferences.BOX_VIEW_EMPTY_AND_MUTED_STRINGS_CLOSE_TO_FRAME)
				&& !(getShowNotesInside() || getShowIntervalsInside());
		final Color pointsBgColor = BackgroundColorMode
				.valueOf(prefs.getString(Preferences.BOX_VIEW_POINTS_BACKGROUND)) == BackgroundColorMode.WHITE ? ColorConstants.white
				: ColorConstants.black;

		// determine sizes
		final FigureSizeMode emptyStringSizeMode = FigureSizeMode.valueOf(prefs
				.getString(Preferences.BOX_VIEW_EMPTY_AND_MUTED_STRINGS_SIZE));
		final FigureSizeMode pointSizeMode = FigureSizeMode.valueOf(prefs.getString(Preferences.BOX_VIEW_POINTS_SIZE));
		final int pointWidth = pointSizeMode.getSize();

		if (emptyStringSizeMode == FigureSizeMode.SAME) {
			figureSizes.add(pointWidth);
			if (pointSizeMode != FigureSizeMode.LARGE) {
				figureSizes.add(FigureSizeMode.LARGE.getSize());
			}
		} else {
			figureSizes.add(emptyStringSizeMode.getSize());
		}

		// init start fret label
		startFretLabel = new Label();
		startFretLabel.setForegroundColor(ColorConstants.black);
		startFretLabel.setTextAlignment(PositionConstants.CENTER);

		final Point fretNumberPosition = presentationMode.getFretNumberPosition(getBoxDraft(),
				getShowFingeringInside(), getShowFingeringOutside(), getShowNotesOutside(), getShowNotesInside(),
				getShowIntervalsOutside(), getShowIntervalsInside(), fretCount, stringCount);

		add(startFretLabel);
		final Rectangle rectangle = new Rectangle(fretNumberPosition.x, fretNumberPosition.y,
				presentationMode.getFretNumberWidth(getShowNotesInside(), getShowIntervalsInside()),
				presentationMode.getFretHeight(getShowNotesInside(), getShowIntervalsInside()));
		startFretLabel.setBounds(rectangle);
		contentsLayout.setConstraint(startFretLabel, rectangle);

		// init barre figures
		for (final Barre barre : getBoxDraft().getBarres()) {
			final ArcConnection barreLineConnection = new ArcConnection();
			barreLineConnection.setVisible(false);
			barreLineConnection.setLineStyle(SWT.LINE_SOLID);
			barreLineConnection.setForegroundColor(ColorConstants.black);
			barreLineConnection.setBackgroundColor(ColorConstants.black);
			barreLineConnection.setLineWidth(prefs.getInt(Preferences.BOX_VIEW_BARRE_LINE_WIDTH));
			barreConnectionMap.put(barre, barreLineConnection);
			add(barreLineConnection);

			final RoundedRectangle barreFigure = new RoundedRectangle();
			barreFigure.setForegroundColor(ColorConstants.black);
			barreFigure.setBackgroundColor(ColorConstants.black);
			barreFigure.setLineStyle(SWT.LINE_SOLID);
			barreFigureMap.put(barre, barreFigure);
			add(barreFigure);
		}

		// init points and assignments
		final BoxViewFigureMode insideMode = getShowNotesInside() ? BoxViewFigureMode.NOTE
				: getShowIntervalsInside() ? BoxViewFigureMode.INTERVAL : BoxViewFigureMode.FINGERING;
		final Point boxPosition = presentationMode.getBoxPosition(getBoxDraft(), getShowFingeringInside(),
				getShowFingeringOutside(), getShowNotesOutside(), getShowNotesInside(), getShowIntervalsOutside(),
				getShowIntervalsInside());

		for (int f = 0; f <= fretCount; f++) {

			// init muted and empty string figure maps
			if (f == 0) {
				for (final Integer figureSize : figureSizes) {
					mutedStringFiguresMap.put(figureSize, new ArrayList<MutedStringFigure>());
					emptyStringPointsMap.put(figureSize, new ArrayList<RoundedRectangle2>());
				}
			}

			for (int s = 0; s < stringCount; s++) {

				final FretboardPosition fbp = new FretboardPosition(s, f);
				final Point assignmentPosition = presentationMode.getAssignmentPosition(boxPosition,
						getShowNotesInside(), getShowIntervalsInside(), stringCount, fretCount, s, f);

				if (f == 0) {

					for (final Integer figureSize : figureSizes) {

						// determine bounds
						final Point emptyStringPosition = presentationMode.getEmptyStringPositon(boxPosition,
								getShowNotesInside(), getShowIntervalsInside(), stringCount, fretCount, s, figureSize,
								placeEmptyStringsCloseToFrame);
						final Rectangle bounds = new Rectangle(emptyStringPosition.x, emptyStringPosition.y,
								figureSize, figureSize);

						// muted string figures
						final List<MutedStringFigure> mutedStringFiguresList = mutedStringFiguresMap.get(figureSize);
						final MutedStringFigure mutedStringFigure = new MutedStringFigure(1);
						mutedStringFigure.setVisible(false);
						mutedStringFiguresList.add(mutedStringFigure);
						add(mutedStringFigure);
						mutedStringFigure.setBounds(bounds);
						contentsLayout.setConstraint(mutedStringFigure, bounds);

						// empty string point
						if (getShowFingeringInside() || !getShowInside()) {
							final List<RoundedRectangle2> emptyStringPointsList = emptyStringPointsMap.get(figureSize);
							final RoundedRectangle2 emptyStringPointFigure = createPointFigure(figureSize,
									pointsBgColor);
							emptyStringPointsList.add(emptyStringPointFigure);
							emptyStringPointFigure.setBounds(bounds);
							contentsLayout.setConstraint(emptyStringPointFigure, bounds);
						}
					}

				}

				if (f > 0 || getShowNotesInside() || getShowIntervalsInside()) {

					// create point figure
					final RoundedRectangle2 pointFigure = createPointFigure(pointWidth, pointsBgColor);
					pointsMap.put(fbp, pointFigure);
					final Rectangle pointBounds = new Rectangle(assignmentPosition.x - pointWidth / 2,
							assignmentPosition.y - pointWidth / 2, pointWidth, pointWidth);
					pointFigure.setBounds(pointBounds);
					contentsLayout.setConstraint(pointFigure, pointBounds);

					// create box assignment figures (for the fingering)
					final BoxAssignmentFigure assignmentFigure = new BoxAssignmentFigure(insideMode);
					assignmentFigure.setText(""); //$NON-NLS-1$

					add(assignmentFigure);
					final int boxAssignmentWidth = presentationMode == BoxViewPresentationMode.VERTICAL
							|| insideMode == BoxViewFigureMode.FINGERING ? IFigureConstants.BOX_ASSIGNMENT_SIZE_SMALL
							: IFigureConstants.BOX_ASSIGNMENT_SIZE_BIG;
					final int boxAssignmentHeight = presentationMode == BoxViewPresentationMode.HORIZONTAL
							|| insideMode == BoxViewFigureMode.FINGERING ? IFigureConstants.BOX_ASSIGNMENT_SIZE_SMALL
							: IFigureConstants.BOX_ASSIGNMENT_SIZE_BIG;

					final Rectangle assignmentBounds = new Rectangle(assignmentPosition.x - boxAssignmentWidth / 2,
							assignmentPosition.y - boxAssignmentHeight / 2, boxAssignmentWidth, boxAssignmentHeight);
					assignmentFigure.setBounds(assignmentBounds);
					contentsLayout.setConstraint(assignmentFigure, assignmentBounds);
					boxAssignmentMap.put(fbp, assignmentFigure);
				}

				// finally create label figure
				// NOTE: necessary to detect click positions in search mode
				final Label label = new Label();
				label.setText(""); //$NON-NLS-1$

				add(label);
				final Rectangle labelBounds = new Rectangle(assignmentPosition.x
						- presentationMode.getFretWidth(getShowNotesInside(), getShowIntervalsInside()) / 2,
						assignmentPosition.y
								- presentationMode.getFretHeight(getShowNotesInside(), getShowIntervalsInside()) / 2,
						presentationMode.getFretWidth(getShowNotesInside(), getShowIntervalsInside()),
						presentationMode.getFretHeight(getShowNotesInside(), getShowIntervalsInside()));
				label.setBounds(labelBounds);
				contentsLayout.setConstraint(label, labelBounds);
				labelMap.put(fbp, label);
			}
		}

		// init info bars for fingerings, notes and intervals
		for (int string = 0; string < stringCount; string++) {

			for (final BoxViewFigureMode currentMode : BoxViewFigureMode.values()) {

				if (currentMode == BoxViewFigureMode.FINGERING && !getShowFingeringOutside()) {
					continue;
				}
				if (currentMode == BoxViewFigureMode.NOTE && !getShowNotesOutside()) {
					continue;
				}
				if (currentMode == BoxViewFigureMode.INTERVAL && !getShowIntervalsOutside()) {
					continue;
				}

				final Map<Integer, List<BoxInfoFigure>> map = currentMode == BoxViewFigureMode.NOTE ? notesInfoMap
						: currentMode == BoxViewFigureMode.INTERVAL ? intervalsInfoMap : fingeringInfoMap;
				final List<BoxInfoFigure> list = new ArrayList<BoxInfoFigure>();
				map.put(string, list);

				final Point infoPosition = presentationMode.getInfoPosition(getBoxDraft(), fretCount, stringCount,
						string, getShowFingeringInside(), getShowFingeringOutside(), getShowNotesOutside(),
						getShowNotesInside(), getShowIntervalsOutside(), getShowIntervalsInside(), currentMode);
				int x = infoPosition.x;
				int y = infoPosition.y;

				final int max = getBoxDraft().getMaxAssignmentsNumber(currentMode != BoxViewFigureMode.FINGERING);
				for (int i = 0; i < max; i++) {
					final int index = isHorizontalLeftHanderMode() ? max - i - 1 : i;

					final int width = presentationMode.getInfoBarWidth(getBoxDraft(), getShowNotesInside(),
							getShowIntervalsInside(), index, currentMode);
					final int height = presentationMode.getInfoBarHeight(getBoxDraft(), getShowNotesInside(),
							getShowIntervalsInside(), index, currentMode);

					final BoxInfoFigure boxInfo = new BoxInfoFigure(currentMode, getBoxDraft());
					boxInfo.setVisible(true);
					add(boxInfo);

					final Rectangle infoBounds = new Rectangle(x, y, width + 1, height + 1);
					boxInfo.setBounds(infoBounds);
					contentsLayout.setConstraint(boxInfo, infoBounds);
					list.add(boxInfo);

					if (presentationMode == BoxViewPresentationMode.HORIZONTAL) {
						x += width;
					} else {
						y += height;
					}
				}
			}
		}

		// draw
		drawStartFretLabel();
		drawInsideInfo();
		drawOutsideInfo();
	}

	private RoundedRectangle2 createPointFigure(final int figureSize, final Color bgColor) {
		final RoundedRectangle2 pointFigure = new RoundedRectangle2();
		pointFigure.setCornerDimensions(new Dimension(figureSize, figureSize));
		pointFigure.setForegroundColor(ColorConstants.black);
		pointFigure.setBackgroundColor(bgColor);
		pointFigure.setFill(true);
		pointFigure.setLineStyle(SWT.LINE_SOLID);
		pointFigure.setVisible(false);
		add(pointFigure);
		return pointFigure;
	}

	public void drawStartFretLabel() {
		if (getCurrentInstrument() == null) {
			return;
		}

		final int fretNumber = getBoxDraft().getStartFret();
		final boolean isEditable = getBoxDraft().isEditable();

		final boolean grayColor = prefs.getBoolean(Preferences.BOX_VIEW_FRET_NUMBERS_GRAY_COLOR);
		final String fretNumbersMode = prefs.getString(Preferences.BOX_VIEW_FRET_NUMBERS_MODE);
		final boolean frameFretNumber = isEditable || prefs.getBoolean(Preferences.BOX_VIEW_FRET_NUMBERS_FRAMED);
		final boolean visible = isEditable || fretNumber != 1
				|| prefs.getBoolean(Preferences.BOX_VIEW_FRET_NUMBERS_VISIBLE_FOR_FIRST_FRET);
		final boolean roman = fretNumbersMode.equals(UIConstants.NUMERALS_MODE_ROMAN) && !isEditable;

		final BoxViewPresentationMode presentationMode = BoxViewPresentationMode.valueOf(prefs
				.getString(Preferences.BOX_VIEW_PRESENTATION_MODE));

		// update label font
		startFretLabel.setForegroundColor(grayColor ? IFigureConstants.DARK_GREY : ColorConstants.black);
		startFretLabel.setVisible(visible);
		startFretLabel.setFont(FontManager.getFont(
				null,
				startFretLabel,
				roman ? "XXIII" : "24",
				presentationMode.getFretNumberWidth(getShowNotesInside(), getShowIntervalsInside())
						- (frameFretNumber ? 15 : 0)
						- (!presentationMode.areBigFretsInUse(getShowFingeringInside(), getShowNotesInside(),
								getShowIntervalsInside()) || presentationMode == BoxViewPresentationMode.VERTICAL ? 0
								: 20), -1, SWT.BOLD));
		startFretLabel.setBorder(frameFretNumber ? new LineBorder() : null);

		// update label text
		final String text = roman ? UIConstants.ROMAN_NUMERALS[fretNumber] : fretNumber + ""; //$NON-NLS-1$
		startFretLabel.setText(text);
	}

	/**
	 * Draws the assignment info (fingering, notes, intervals) that are shown
	 * inside the box.
	 */
	private void drawInsideInfo() {

		if (getCurrentInstrument() == null) {
			return;
		}

		final boolean frameInside = prefs.getBoolean(Preferences.BOX_VIEW_FRAME_INSIDE);

		// determine preferences
		final boolean highlightRootNote = prefs.getBoolean(Preferences.BOX_VIEW_HIGHLIGHT_ROOT_NOTE);
		final boolean highlightRootNoteWithColor = highlightRootNote
				&& prefs.getBoolean(Preferences.BOX_VIEW_HIGHLIGHT_ROOT_NOTE_WITH_COLOR);
		final boolean highlightRootNoteWithShape = highlightRootNote
				&& prefs.getBoolean(Preferences.BOX_VIEW_HIGHLIGHT_ROOT_NOTE_WITH_SHAPE);
		final int pointSize = FigureSizeMode.valueOf(prefs.getString(Preferences.BOX_VIEW_POINTS_SIZE)).getSize();

		// determine background colors
		final BoxViewFigureMode insideMode = getShowNotesInside() ? BoxViewFigureMode.NOTE
				: getShowIntervalsInside() ? BoxViewFigureMode.INTERVAL : BoxViewFigureMode.FINGERING;
		final BackgroundColorMode insideBgColorMode = BackgroundColorMode.valueOf(prefs
				.getString(Preferences.BOX_VIEW_BACKGROUND_INSIDE));
		final Color insideBgColor = insideBgColorMode == BackgroundColorMode.BLACK ? ColorConstants.black
				: insideBgColorMode == BackgroundColorMode.COLORED ? insideMode.getColor() : getWhite();
		final BackgroundColorMode pointsBgColorMode = BackgroundColorMode.valueOf(prefs
				.getString(Preferences.BOX_VIEW_POINTS_BACKGROUND));
		final Color pointsBgColor = pointsBgColorMode == BackgroundColorMode.BLACK ? ColorConstants.black : getWhite();
		final BackgroundColorMode emptyStringsBgColorMode = prefs
				.getBoolean(Preferences.BOX_VIEW_EMPTY_STRINGS_BACKGROUND_WHITE) ? BackgroundColorMode.WHITE
				: getShowFingeringInside() ? insideBgColorMode : pointsBgColorMode;
		final Color emptyStringsBgColor = emptyStringsBgColorMode == BackgroundColorMode.WHITE ? getWhite()
				: emptyStringsBgColorMode == BackgroundColorMode.COLORED ? IFigureConstants.RED : ColorConstants.black;

		// set visibility
		setInitialVisibility();

		// store muted strings
		final List<Integer> mutedStrings = new ArrayList<Integer>();
		for (int s = 0; s < getCurrentInstrument().getStringCount(); s++) {
			mutedStrings.add(s);
		}

		// set selected points and assignments visible
		final FingeringMode fingeringMode = FingeringMode.valueOf(prefs.getString(Preferences.GENERAL_FINGERING_MODE));

		final List<FretboardPosition> fretboardPositions = getBoxDraft().getFretboardPositions();
		for (final FretboardPosition fbp : fretboardPositions) {
			mutedStrings.remove(Integer.valueOf(fbp.getString()));

			final boolean isRootNote = getBoxDraft().isRootNote(fbp);

			final FretboardPosition theRelativeFBP = fbp.getFret() == getCurrentInstrument().getCapoFret(
					fbp.getString() + 1) ? new FretboardPosition(fbp.getString(), 0) : new FretboardPosition(
					fbp.getString(), fbp.getFret() - getBoxDraft().getStartFret() + 1);

			// hide labels
			if (getShowFingeringInside() && theRelativeFBP.getFret() != 0) {
				final Label label = labelMap.get(theRelativeFBP);
				if (label != null) {
					label.setVisible(false);
				}
			}

			// draw empty string (inside fingering mode)
			if (theRelativeFBP.getFret() == 0 && (getShowFingeringInside() || !getShowInside())) {

				final FigureSizeMode sizeMode = FigureSizeMode.valueOf(prefs
						.getString(Preferences.BOX_VIEW_EMPTY_AND_MUTED_STRINGS_SIZE));
				final List<RoundedRectangle2> emptyStringPointsList = sizeMode == FigureSizeMode.SAME
						&& getShowFingeringInside() ? emptyStringPointsMap.get(FigureSizeMode.LARGE.getSize())
						: emptyStringPointsMap.get(figureSizes.get(0));

				final RoundedRectangle emptyStringPoint = emptyStringPointsList.get(theRelativeFBP.getString());
				if (emptyStringPoint != null) {
					emptyStringPoint.setVisible(true);

					// set corner dimension
					final int size = emptyStringPoint.getSize().width;
					emptyStringPoint.setCornerDimensions(isRootNote && highlightRootNoteWithShape ? new Dimension(0, 0)
							: new Dimension(size, size));

					// set background color
					emptyStringPoint.setBackgroundColor(emptyStringsBgColor);
					if (isRootNote && highlightRootNoteWithColor) {
						emptyStringPoint
								.setBackgroundColor(emptyStringPoint.getBackgroundColor() == getWhite() ? ColorConstants.black
										: getWhite());
					}
				}
			}

			// draw fingering
			else if (getShowInside()) {

				final BoxAssignmentFigure figure = boxAssignmentMap.get(theRelativeFBP);
				if (figure != null) {

					figure.setVisible(true);
					figure.setFill(true);
					figure.setOutline(true);
					figure.setToolTip(null);

					if (insideMode == BoxViewFigureMode.FINGERING) {

						figure.setText(""
								+ fingeringMode.getValue(getBoxDraft().getAssignment(theRelativeFBP.getFret(),
										theRelativeFBP.getString() + 1)));

					} else if (insideMode == BoxViewFigureMode.NOTE) {

						String text = BoxInfoFigure.HYPHEN;
						Label tooltipLabel = null;
						text = getNotesText(fbp, false);
						tooltipLabel = new Label();
						tooltipLabel.setBackgroundColor(IFigureConstants.TOOLTIP_YELLOW);
						tooltipLabel.setText(" " + getNotesText(fbp, true) + " "); //$NON-NLS-1$ //$NON-NLS-2$
						figure.setText(text);
						figure.setToolTip(tooltipLabel);

					} else {

						String text = BoxInfoFigure.HYPHEN;
						text = getIntervalsText(fbp);
						figure.setText(text);
					}

					// set corner dimension
					figure.setCornerDimensions(isRootNote && highlightRootNoteWithShape ? new Dimension(0, 0)
							: new Dimension(FigureSizeMode.LARGE.getSize(), FigureSizeMode.LARGE.getSize()));

					// set background color
					figure.setBackgroundColor(insideBgColor);
					if (isRootNote && highlightRootNoteWithColor) {
						figure.setBackgroundColor(figure.getBackgroundColor() == getWhite() ? ColorConstants.black
								: getWhite());
					}
					figure.setLabelForegroundColor(figure.getBackgroundColor() == ColorConstants.black && frameInside ? getWhite()
							: ColorConstants.black);
				}

			} else {

				final RoundedRectangle point = pointsMap.get(theRelativeFBP);
				if (point != null) {
					point.setVisible(true);

					// set corner dimension
					point.setCornerDimensions(isRootNote && highlightRootNoteWithShape ? new Dimension(0, 0)
							: new Dimension(pointSize, pointSize));

					// set background color
					point.setBackgroundColor(pointsBgColor);
					if (isRootNote && highlightRootNoteWithColor) {
						point.setBackgroundColor(point.getBackgroundColor() == getWhite() ? ColorConstants.black
								: getWhite());
					}
				}
			}
		}

		// draw muted strings
		if (prefs.getBoolean(Preferences.BOX_VIEW_SHOW_MUTED_STRINGS) && !getBoxDraft().isEditable()) {
			drawMutedStringFigures(mutedStrings);
		}

		// draw barre
		if (prefs.getBoolean(Preferences.BOX_VIEW_SHOW_BARRE) && getBoxDraft().isPotentialGriptable()
				&& getBoxDraft().getShowBarre() && (!getBoxDraft().isEditable() || getShowFingeringInside())) {
			drawBarre(highlightRootNoteWithColor, highlightRootNoteWithShape);
		}
	}

	private void setInitialVisibility() {
		for (final Integer figureSize : figureSizes) {
			for (final MutedStringFigure mutedStringFigure : mutedStringFiguresMap.get(figureSize)) {
				mutedStringFigure.setVisible(false);
			}
			for (final RoundedRectangle2 pointFigure : emptyStringPointsMap.get(figureSize)) {
				pointFigure.setVisible(false);
			}
		}
		for (final RoundedRectangle2 pointFigure : pointsMap.values()) {
			pointFigure.setVisible(false);
		}
		for (final BoxAssignmentFigure boxAssignmentFigure : boxAssignmentMap.values()) {
			boxAssignmentFigure.setVisible(false);
		}
		for (final ArcConnection barreConnection : barreConnectionMap.values()) {
			barreConnection.setVisible(false);
		}
		for (final RoundedRectangle barreFigure : barreFigureMap.values()) {
			barreFigure.setVisible(false);
		}
		for (final Label label : labelMap.values()) {
			label.setVisible(true);
		}
	}

	private void drawMutedStringFigures(final List<Integer> mutedStrings) {

		final FigureSizeMode sizeMode = FigureSizeMode.valueOf(prefs
				.getString(Preferences.BOX_VIEW_EMPTY_AND_MUTED_STRINGS_SIZE));
		final List<MutedStringFigure> mutedStringFiguresList = sizeMode == FigureSizeMode.SAME && getShowFingering()
				&& !getShowFingeringOutside() ? mutedStringFiguresMap.get(FigureSizeMode.LARGE.getSize())
				: mutedStringFiguresMap.get(figureSizes.get(0));

		for (final Integer mutedString : mutedStrings) {
			final MutedStringFigure mutedStringFigure = mutedStringFiguresList.get(mutedString);
			if (mutedStringFigure != null) {
				mutedStringFigure.setVisible(true);
				mutedStringFigure.repaint();
			}
		}
	}

	private void drawBarre(final boolean highlightRootNoteWithColor, final boolean highlightRootNoteWithShape) {

		// determine background color
		final BoxViewFigureMode insideMode = getShowNotesInside() ? BoxViewFigureMode.NOTE
				: getShowIntervalsInside() ? BoxViewFigureMode.INTERVAL : BoxViewFigureMode.FINGERING;
		final BackgroundColorMode pointsgBgColorMode = BackgroundColorMode.valueOf(prefs
				.getString(Preferences.BOX_VIEW_POINTS_BACKGROUND));
		final BackgroundColorMode insideBgColorMode = BackgroundColorMode.valueOf(prefs
				.getString(Preferences.BOX_VIEW_BACKGROUND_INSIDE));
		BackgroundColorMode barBgColorMode = BackgroundColorMode.valueOf(prefs
				.getString(Preferences.BOX_VIEW_BARRE_BAR_BACKGROUND));
		if (BackgroundColorMode.SAME == barBgColorMode) {
			barBgColorMode = getShowInside() ? insideBgColorMode : pointsgBgColorMode;
		}
		final Color bgColor = barBgColorMode == BackgroundColorMode.WHITE ? getWhite()
				: barBgColorMode == BackgroundColorMode.BLACK ? ColorConstants.black : insideMode.getColor();

		// show fingering and/or highlighted root note
		final boolean showInsideBar = prefs.getBoolean(Preferences.BOX_VIEW_BARRE_BAR_SHOW_ELEMENTS_INSIDE);
		final boolean singleFingeringInBar = prefs.getBoolean(Preferences.BOX_VIEW_BARRE_BAR_SHOW_SINGLE_FINGER_NUMBER);

		for (final Barre barre : getBoxDraft().getBarres()) {

			final int f = barre.getRelativeFret();
			final int minString = barre.getMinString() - 1;
			final int maxString = barre.getMaxString() - 1;

			final BarreMode barreMode = BarreMode.valueOf(prefs.getString(Preferences.BOX_VIEW_BARRE_MODE));

			// determine figures
			final FretboardPosition minFbp = new FretboardPosition(minString, f);
			final FretboardPosition maxFbp = new FretboardPosition(maxString, f);
			final IFigure minFigure = getShowInside() ? boxAssignmentMap.get(minFbp) : pointsMap.get(minFbp);
			final IFigure maxFigure = getShowInside() ? boxAssignmentMap.get(maxFbp) : pointsMap.get(maxFbp);

			// determine bounds
			final Rectangle min = (Rectangle) contentsLayout.getConstraint(minFigure);
			final Rectangle max = (Rectangle) contentsLayout.getConstraint(maxFigure);
			final int startX = Math.min(min.x, max.x);
			final int endX = Math.max(min.x, max.x) + min.width;
			final int startY = Math.min(min.y, max.y);
			final int endY = Math.max(min.y, max.y) + min.height;
			final Rectangle bounds = new Rectangle(startX, startY, endX - startX, endY - startY);

			if (BarreMode.BAR == barreMode) {

				// set bar visibile
				final RoundedRectangle barreFigure = barreFigureMap.get(barre);
				barreFigure.setVisible(true);
				barreFigure.setBackgroundColor(bgColor);
				barreFigure.setCornerDimensions(new Dimension(Math.min(min.width, min.height), Math.min(min.width,
						min.height)));
				barreFigure.setBounds(bounds);
				contentsLayout.setConstraint(barreFigure, bounds);

				for (int s = minString; s <= maxString; s++) {

					final FretboardPosition fbp = new FretboardPosition(s, f);
					final FretboardPosition theFBP = new FretboardPosition(fbp.getString(), fbp.getFret()
							+ getBoxDraft().getStartFret() - 1);

					final boolean highlightRootNote = getBoxDraft().isRootNote(theFBP) && highlightRootNoteWithColor;

					if (getShowInside()) {

						final BoxAssignmentFigure figure = boxAssignmentMap.get(fbp);
						if (figure != null && !figure.isVisible()) {
							continue;
						}

						// show text inside bar
						if (figure != null) {
							figure.setVisible(showInsideBar && (!singleFingeringInBar || maxFigure == figure)
									|| highlightRootNote || !getShowFingeringInside());

							final Color theBgColor = highlightRootNote ? figure.getBackgroundColor() : bgColor;
							figure.setLabelForegroundColor(theBgColor == ColorConstants.black ? getWhite()
									: ColorConstants.black);

							figure.setForceMinTextHeight(true);
							figure.setOutline(highlightRootNote);
							figure.setFill(highlightRootNote);
						}

					} else {

						final RoundedRectangle point = pointsMap.get(fbp);
						if (point != null && point.isVisible()) {
							point.setVisible(highlightRootNote && highlightRootNoteWithColor);
						}
					}
				}
			}

			else {

				final BoxViewPresentationMode mode = BoxViewPresentationMode.valueOf(prefs
						.getString(Preferences.BOX_VIEW_PRESENTATION_MODE));
				final boolean leftHander = Activator.getDefault().isLeftHander();
				final ArcConnection barreLineConnection = barreConnectionMap.get(barre);
				final CenterAnchor minFigureAnchor = new CenterAnchor(minFigure);
				final CenterAnchor maxFigureAnchor = new CenterAnchor(maxFigure);

				// init connection
				barreLineConnection.setDepth(BarreMode.LINE == barreMode ? 0
						: (mode == BoxViewPresentationMode.VERTICAL ? mode.getFretHeight(getShowNotesInside(),
								getShowIntervalsInside()) : mode.getFretWidth(getShowNotesInside(),
								getShowIntervalsInside())) / 2 - 3);
				barreLineConnection.setSourceAnchor(leftHander ? maxFigureAnchor : minFigureAnchor);
				barreLineConnection.setTargetAnchor(leftHander ? minFigureAnchor : maxFigureAnchor);
				barreLineConnection.setVisible(true);
			}
		}
	}

	private void drawOutsideInfo() {
		if (getCurrentInstrument() == null) {
			return;
		}

		// map: key = string, value = list of fretboard position
		final Map<Integer, List<FretboardPosition>> assignmentMap = new HashMap<Integer, List<FretboardPosition>>();
		if (getShowFingeringOutside() || getShowNotes() || getShowIntervals()) {
			for (final FretboardPosition fbp : getBoxDraft().getFretboardPositions()) {
				List<FretboardPosition> list = assignmentMap.get(fbp.getString());
				if (list == null) {
					list = new ArrayList<FretboardPosition>();
					assignmentMap.put(fbp.getString(), list);
				}
				list.add(fbp);
			}
		}

		for (int string = 0; string < getCurrentInstrument().getStringCount(); string++) {
			final List<FretboardPosition> fbps = assignmentMap.get(string);

			// fingering
			final List<Integer> assignments = getBoxDraft().getAssignments(string + 1, false);
			final List<BoxInfoFigure> fingeringInfoBoxList = fingeringInfoMap.get(string);
			if (fingeringInfoBoxList != null) {

				final int max = getBoxDraft().getMaxAssignmentsNumber(false);
				for (int i = 0; i < max; i++) {
					final int index = isHorizontalLeftHanderMode() ? max - i - 1 : i;

					final BoxInfoFigure fingeringBox = fingeringInfoBoxList.get(i);
					final Integer assignment = assignments != null && index < assignments.size() ? assignments
							.get(index) : null;

					if (fingeringBox != null) {
						String text = BoxInfoFigure.HYPHEN;
						if (assignment != null) {
							text = Character.toString(FingeringMode.valueOf(
									prefs.getString(Preferences.GENERAL_FINGERING_MODE)).getValue(assignment));
						}
						if ("?".equals(text)) { //$NON-NLS-1$
							text = BoxInfoFigure.HYPHEN;
						}
						fingeringBox.updateText(text);
						fingeringBox.setToolTip(null);
					}
				}
			}

			final int max = getBoxDraft().getMaxAssignmentsNumber(true);

			// notes
			final List<BoxInfoFigure> notesInfoBoxList = notesInfoMap.get(string);
			if (notesInfoBoxList != null) {

				for (int i = 0; i < max; i++) {
					final int index = isHorizontalLeftHanderMode() ? max - i - 1 : i;

					final BoxInfoFigure noteBox = notesInfoBoxList.get(i);
					final FretboardPosition fbp = fbps != null && index < fbps.size() ? fbps.get(index) : null;

					if (noteBox != null) {
						String text = BoxInfoFigure.HYPHEN;
						Label tooltipLabel = null;
						if (fbp != null) {
							text = getNotesText(fbp, false);
							tooltipLabel = new Label();
							tooltipLabel.setBackgroundColor(IFigureConstants.TOOLTIP_YELLOW);
							tooltipLabel.setText(" " + getNotesText(fbp, true) + " "); //$NON-NLS-1$ //$NON-NLS-2$
						}
						noteBox.updateText(text);
						noteBox.setToolTip(tooltipLabel);
					}
				}
			}

			// intervals
			final List<BoxInfoFigure> intervalInfoBoxList = intervalsInfoMap.get(string);
			if (intervalInfoBoxList != null) {

				for (int i = 0; i < max; i++) {
					final int index = isHorizontalLeftHanderMode() ? max - i - 1 : i;

					final BoxInfoFigure intervalBox = intervalInfoBoxList.get(i);
					final FretboardPosition fbp = fbps != null && index < fbps.size() ? fbps.get(index) : null;

					if (intervalBox != null) {
						String text = BoxInfoFigure.HYPHEN;
						if (fbp != null) {
							text = getIntervalsText(fbp);
						}
						intervalBox.updateText(text);
						intervalBox.setToolTip(null);
					}
				}
			}
		}
	}

	private String getNotesText(final FretboardPosition fbp, final boolean absolute) {
		final String notesModeValue = prefs.getString(Preferences.NOTES_MODE);

		final Note note = getCurrentInstrument().getNote(fbp);
		return absolute ? note.getAbsoluteName() : notesModeValue.equals(Constants.NOTES_MODE_CROSS_AND_B) ? note
				.getRelativeName() : notesModeValue.equals(Constants.NOTES_MODE_ONLY_CROSS) ? note.getRelativeNameAug()
				: note.getRelativeNameDim();
	}

	private String getIntervalsText(final FretboardPosition fbp) {
		final Note rootNote = getBoxDraft().getRootNote();
		if (rootNote != null) {
			final Interval interval = rootNote.calcInterval(getCurrentInstrument().getNote(fbp));
			return getBoxDraft().getIntervalName(interval);
		}
		return "?"; //$NON-NLS-1$
	}

	public boolean isStartFretLabel(final Label label) {
		return label == startFretLabel;
	}

	public FretboardPosition getRelativeFretboardPosition(final Label label) {
		for (final Entry<FretboardPosition, Label> entry : labelMap.entrySet()) {
			if (entry.getValue() == label) {
				return entry.getKey();
			}
		}
		return null;
	}

	public FretboardPosition getRelativeFretboardPosition(final BoxAssignmentFigure boxAssignmentFigure) {
		for (final Entry<FretboardPosition, BoxAssignmentFigure> entry : boxAssignmentMap.entrySet()) {
			if (entry.getValue() == boxAssignmentFigure) {
				return entry.getKey();
			}
		}
		return null;
	}

	public BoxAssignmentFigure getAssignmentFigure(final FretboardPosition fretboardPosition) {
		return boxAssignmentMap.get(fretboardPosition);
	}

	private boolean isHorizontalLeftHanderMode() {
		final BoxViewPresentationMode presentationMode = BoxViewPresentationMode.valueOf(prefs
				.getString(Preferences.BOX_VIEW_PRESENTATION_MODE));
		final boolean horizontalLeftHanderMode = presentationMode == BoxViewPresentationMode.HORIZONTAL
				&& Activator.getDefault().isLeftHander();
		return horizontalLeftHanderMode;
	}

	Color getWhite() {
		return getBoxDraft().isEditable() ? IFigureConstants.TOOLTIP_YELLOW : ColorConstants.white;
	}
}
