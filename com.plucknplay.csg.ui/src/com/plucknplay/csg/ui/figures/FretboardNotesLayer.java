/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.figures;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.RoundedRectangle;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.progress.UIJob;

import com.plucknplay.csg.core.Constants;
import com.plucknplay.csg.core.model.Factory;
import com.plucknplay.csg.core.model.FretboardPosition;
import com.plucknplay.csg.core.model.Note;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.UIConstants;
import com.plucknplay.csg.ui.model.FretDraft.Barre;
import com.plucknplay.csg.ui.model.FretboardDraft;
import com.plucknplay.csg.ui.util.ArcConnection;
import com.plucknplay.csg.ui.util.CenterAnchor;
import com.plucknplay.csg.ui.util.StyleTemp;
import com.plucknplay.csg.ui.util.enums.BackgroundColorMode;
import com.plucknplay.csg.ui.util.enums.BarreMode;
import com.plucknplay.csg.ui.util.enums.FingeringMode;

public class FretboardNotesLayer extends AbstractFretboardLayer {

	private final IPreferenceStore prefs;

	private final XYLayout contentsLayout;

	private Map<FretboardPosition, Label> labelMap;
	private Map<FretboardPosition, MutedStringFigure> mutedStringFiguresMap;
	private Map<FretboardPosition, FretboardNotesFigure> littleNotesMap;
	private Map<FretboardPosition, FretboardNotesFigure> bigNotesMap;
	private Map<Barre, RoundedRectangle> barreFigureMap;
	private Map<Barre, ArcConnection> barreConnectionMap;

	private Map<FretboardPosition, StyleTemp> tempMap;

	private String mode;
	private String notesModeValue;
	private FingeringMode fingeringMode;

	public FretboardNotesLayer(final FretboardDraft fretboardDraft) {
		super(fretboardDraft);
		prefs = Activator.getDefault().getPreferenceStore();
		contentsLayout = new XYLayout();
		setLayoutManager(contentsLayout);
		init();
	}

	/**
	 * Initializes the layer.
	 */
	private void init() {
		removeAll();

		labelMap = new HashMap<FretboardPosition, Label>();
		mutedStringFiguresMap = new HashMap<FretboardPosition, MutedStringFigure>();
		littleNotesMap = new HashMap<FretboardPosition, FretboardNotesFigure>();
		bigNotesMap = new HashMap<FretboardPosition, FretboardNotesFigure>();
		barreFigureMap = new HashMap<Barre, RoundedRectangle>();
		barreConnectionMap = new HashMap<Barre, ArcConnection>();

		if (getCurrentInstrument() != null) {
			initBarres();
			initCircles();
		}

		fingeringMode = FingeringMode.valueOf(Activator.getDefault().getPreferenceStore()
				.getString(Preferences.GENERAL_FINGERING_MODE));
	}

	/**
	 * Creates the barre figures and poly line connections.
	 */
	private void initBarres() {
		for (final Barre barre : getFretboardDraft().getBarres()) {
			final ArcConnection barreArcConnection = new ArcConnection();
			barreArcConnection.setVisible(false);
			barreArcConnection.setLineStyle(SWT.LINE_SOLID);
			barreArcConnection.setForegroundColor(ColorConstants.black);
			barreArcConnection.setBackgroundColor(ColorConstants.black);
			barreConnectionMap.put(barre, barreArcConnection);
			add(barreArcConnection);

			final RoundedRectangle barreFigure = new RoundedRectangle();
			barreFigure.setForegroundColor(ColorConstants.black);
			barreFigure.setBackgroundColor(ColorConstants.black);
			barreFigure.setLineStyle(SWT.LINE_SOLID);
			barreFigureMap.put(barre, barreFigure);
			add(barreFigure);
		}
	}

	/**
	 * Creates the circles and stores them with their related fretboard
	 * positions.
	 */
	private void initCircles() {
		final boolean[] circleModes = new boolean[] { false, true };

		for (final boolean isCircle : circleModes) {

			final int width = isCircle ? IFigureConstants.NOTES_HEIGHT : IFigureConstants.TWO_NOTES_WIDTH;
			final int x0 = (IFigureConstants.FRET_WIDTH - width) / 2;
			final int y0 = getOffsetY() - IFigureConstants.NOTES_HEIGHT / 2;

			int fretCount = getCurrentInstrument().getFretCount();
			if (prefs.getBoolean(Preferences.FRETBOARD_VIEW_SHOW_EMPTY_STRINGS_TWICE)) {
				fretCount += 1;
			}

			for (int f = 0; f <= fretCount; f++) {
				for (int s = 0; s < getCurrentInstrument().getStringCount(); s++) {

					final FretboardPosition fbp = new FretboardPosition(s, f);
					final FretboardPosition realFbp = new FretboardPosition(s, f == getCurrentInstrument()
							.getFretCount() + 1 ? 0 : f);
					final Note theNote = getCurrentInstrument().getNote(realFbp);
					final FretboardMouseListener mouseListener = new FretboardMouseListener(theNote, realFbp);

					// create label once (for sound mouse listener)
					if (!isCircle) {
						final Label label = new Label();
						if (!getFretboardDraft().isEditable()) {
							label.addMouseListener(mouseListener);
						}
						add(label);
						labelMap.put(fbp, label);
						contentsLayout.setConstraint(label, new Rectangle(getXFretPosition(f), getOffsetY()
								- IFigureConstants.FRET_HEIGHT / 2 + s * IFigureConstants.FRET_HEIGHT,
								IFigureConstants.FRET_WIDTH, IFigureConstants.FRET_HEIGHT));
					}

					// determine bounds
					final Rectangle bounds = new Rectangle(x0 + getXFretPosition(f), y0 + s
							* IFigureConstants.FRET_HEIGHT, width, IFigureConstants.NOTES_HEIGHT);

					// create muted string figure
					if (isCircle && (f == 0 || f == fretCount)) {
						final MutedStringFigure mutedStringFigure = new MutedStringFigure(2);
						mutedStringFigure.setVisible(false);
						mutedStringFiguresMap.put(fbp, mutedStringFigure);
						add(mutedStringFigure);
						contentsLayout.setConstraint(mutedStringFigure, new Rectangle(bounds.x, bounds.y + 1,
								bounds.width, bounds.height));
					}

					// create note figure
					final FretboardNotesFigure noteFigure = new FretboardNotesFigure(isCircle, theNote, realFbp);
					noteFigure.setCornerDimensions(new Dimension(IFigureConstants.NOTES_HEIGHT,
							IFigureConstants.NOTES_HEIGHT));
					noteFigure.setForegroundColor(ColorConstants.black);
					noteFigure.setBackgroundColor(ColorConstants.white);
					noteFigure.setLineStyle(SWT.LINE_SOLID);
					noteFigure.setVisible(!isCircle);
					if (!getFretboardDraft().isEditable()) {
						noteFigure.addMouseListener(mouseListener);
					}
					add(noteFigure);
					contentsLayout.setConstraint(noteFigure, bounds);

					// store note figure
					if (isCircle) {
						littleNotesMap.put(fbp, noteFigure);
					} else {
						bigNotesMap.put(fbp, noteFigure);
					}

					// set tooltip
					final Note note = getCurrentInstrument().getNote(fbp);
					final boolean addOctaveJump = getCurrentInstrument().isDoubledStringWithOctaveJump(s + 1);
					final Label tooltipLabel = new Label();
					tooltipLabel.setBackgroundColor(IFigureConstants.TOOLTIP_YELLOW);
					final StringBuffer buf = new StringBuffer();
					buf.append(" "); //$NON-NLS-1$
					if (!addOctaveJump) {
						buf.append(note.getAbsoluteName());
					} else {
						final Note higherNote = Factory.getInstance().getNote(note.getValue(), note.getLevel() + 1);
						buf.append(note.getAbsoluteNameAug());
						buf.append(" "); //$NON-NLS-1$
						buf.append(higherNote.getAbsoluteNameAug());
						if (note.hasAccidental()) {
							buf.append("/"); //$NON-NLS-1$
							buf.append(note.getAbsoluteNameDim());
							buf.append(" "); //$NON-NLS-1$
							buf.append(higherNote.getAbsoluteNameDim());
						}
					}
					buf.append(" "); //$NON-NLS-1$
					tooltipLabel.setText(buf.toString());
					noteFigure.setToolTip(tooltipLabel);
				}
			}
		}
	}

	/**
	 * Draws the input.
	 */
	private void drawInput() {

		if (getCurrentInstrument() == null) {
			return;
		}
		clear();

		notesModeValue = prefs.getString(Preferences.NOTES_MODE);
		final boolean isPointMode = UIConstants.MODE_POINTS.equals(mode);
		final boolean showMutedStrings = prefs.getBoolean(Preferences.FRETBOARD_VIEW_SHOW_MUTED_STRINGS);
		final boolean showEmptyStringsTwice = prefs.getBoolean(Preferences.FRETBOARD_VIEW_SHOW_EMPTY_STRINGS_TWICE);
		final boolean highlightRootNote = prefs.getBoolean(Preferences.FRETBOARD_VIEW_HIGHLIGHT_ROOT_NOTE);
		final boolean highlightRootNoteWithColor = highlightRootNote
				&& prefs.getBoolean(Preferences.FRETBOARD_VIEW_HIGHLIGHT_ROOT_NOTE_WITH_COLOR);
		final boolean highlightRootNoteWithShape = highlightRootNote
				&& prefs.getBoolean(Preferences.FRETBOARD_VIEW_HIGHLIGHT_ROOT_NOTE_WITH_SHAPE);
		final boolean frame = isPointMode || prefs.getBoolean(Preferences.FRETBOARD_VIEW_FRAME_FINGERING);

		// determine input
		final boolean showBlocks = prefs.getBoolean(Preferences.SHOW_BLOCKS);
		final boolean isGriptable = getFretboardDraft().isGriptable();
		final boolean isBlock = showBlocks && getFretboardDraft().isBlock();
		final boolean isChordScale = !showBlocks && getFretboardDraft().isBlock();
		final boolean isModifiedInput = getFretboardDraft().isModifiedInput();
		final boolean showFingering = UIConstants.MODE_FINGERING.equals(mode)
				&& (isGriptable || isBlock || getFretboardDraft().isEditable() || isModifiedInput);

		// determine colors
		final BackgroundColorMode bgColorMode = BackgroundColorMode.valueOf(prefs
				.getString(isPointMode ? Preferences.FRETBOARD_VIEW_POINTS_BACKGROUND
						: Preferences.FRETBOARD_VIEW_FINGERING_BACKGROUND));
		final Color bgColor = bgColorMode == BackgroundColorMode.BLACK ? ColorConstants.black
				: bgColorMode == BackgroundColorMode.WHITE ? getWhite() : showFingering ? IFigureConstants.RED
						: UIConstants.MODE_INTERVALS.equals(mode) ? IFigureConstants.YELLOW : IFigureConstants.GREEN;
		final boolean whiteEmptyStringsBackground = prefs
				.getBoolean(Preferences.FRETBOARD_VIEW_EMPTY_STRINGS_BACKGROUND_WHITE)
				&& (UIConstants.MODE_FINGERING.equals(mode) || isPointMode);
		final Color emptyStringsBgColor = whiteEmptyStringsBackground ? getWhite() : bgColor;

		// draw additional notes
		if (!getFretboardDraft().isEditable()) {

			final boolean additionalNotesOnEmptyFretboard = prefs
					.getBoolean(Preferences.FRETBOARD_VIEW_SHOW_ADDITIONAL_NOTES_ON_EMPTY_FRETBOARD);
			final boolean additionalNotesForGriptable = !isPointMode
					&& prefs.getBoolean(Preferences.FRETBOARD_VIEW_SHOW_ADDITIONAL_NOTES_FOR_GRIPTABLE);
			final boolean additionalNotesForChordScale = !isPointMode
					&& prefs.getBoolean(Preferences.FRETBOARD_VIEW_SHOW_ADDITIONAL_NOTES_FOR_CHORD_AND_SCALE);
			final boolean additionalNotesForBlock = !isPointMode
					&& prefs.getBoolean(Preferences.FRETBOARD_VIEW_SHOW_ADDITIONAL_NOTES_FOR_BLOCK);
			final boolean showAdditionalChordNotes = prefs
					.getBoolean(Preferences.FRETBOARD_VIEW_SHOW_ADDITIONAL_CHORD_NOTES);
			final boolean showAdditionalBlockNotes = prefs
					.getBoolean(Preferences.FRETBOARD_VIEW_SHOW_ADDITIONAL_BLOCK_NOTES);
			final boolean showBlockOnly = UIConstants.BLOCK_NO_OVERLAY_FRAME.equals(prefs
					.getString(Preferences.FRETBOARD_VIEW_SHOW_BLOCK_PRESENTATION));

			// determine background color
			final boolean showAdditionalNotesInBlack = prefs
					.getBoolean(Preferences.FRETBOARD_VIEW_SHOW_ADDITIONAL_NOTES_IN_BLACK);
			final boolean whiteFingering = bgColorMode == BackgroundColorMode.WHITE || whiteEmptyStringsBackground
					|| bgColorMode == BackgroundColorMode.BLACK && highlightRootNoteWithColor;
			final Color fgColor = showAdditionalNotesInBlack && (!whiteFingering || getFretboardDraft().isEmpty()) ? ColorConstants.black
					: IFigureConstants.GREY;

			// show ALL additional notes
			if (getFretboardDraft().isEmpty() && additionalNotesOnEmptyFretboard || isGriptable
					&& additionalNotesForGriptable || isChordScale && additionalNotesForChordScale || isBlock
					&& additionalNotesForBlock) {

				setVisible(bigNotesMap.values(), true);
				setVisible(littleNotesMap.values(), false);
				setPresentation(bigNotesMap.values(), fgColor, ColorConstants.white, frame, frame);

				// set text
				for (final Entry<FretboardPosition, FretboardNotesFigure> entry : bigNotesMap.entrySet()) {
					final FretboardPosition fbp = entry.getKey();
					final boolean showIntervalText = UIConstants.MODE_INTERVALS.equals(mode)
							&& !getFretboardDraft().isEmpty();
					entry.getValue().setText(
							showIntervalText ? getFretboardDraft().getIntervalName(fbp) : getNotesText(fbp),
							showIntervalText ? UIConstants.MODE_INTERVALS : UIConstants.MODE_NOTES);
				}
			}

			// show all additional chord/scale notes for griptables or blocks
			if (isGriptable && showAdditionalChordNotes || isBlock && showAdditionalBlockNotes) {

				Color bg = getWhite();
				Color fg = fgColor;

				for (final FretboardPosition fbp : getFretboardDraft().getAdditionalNotes()) {
					if (isBlock && showAdditionalBlockNotes && !showBlockOnly) {
						bg = fbp.getFret() == 0 ? emptyStringsBgColor : bgColor;
						if (bg == IFigureConstants.RED) {
							bg = UIConstants.MODE_INTERVALS.equals(mode) ? IFigureConstants.YELLOW
									: IFigureConstants.GREEN;
						}
						fg = ColorConstants.black;
					}
					drawNote(fbp, bg, fg, false, false, false, showEmptyStringsTwice);
				}
			}
		}

		// draw muted strings
		if (showMutedStrings && !getFretboardDraft().isEmpty()) {
			for (int s = 1; s <= getCurrentInstrument().getStringCount(); s++) {
				if (getFretboardDraft().getAssignments(s, true).isEmpty()) {
					drawMutedString(s);
				}
			}
		}

		// draw notes
		final Color fgColor = ColorConstants.black;
		for (final FretboardPosition fbp : getFretboardDraft().getFretboardPositions()) {
			final Color theBgColor = fbp.getFret() == 0 ? emptyStringsBgColor : bgColor;
			drawNote(fbp, theBgColor, fgColor, showFingering, highlightRootNoteWithColor, highlightRootNoteWithShape,
					showEmptyStringsTwice);
		}

		// draw barre
		if (prefs.getBoolean(Preferences.FRETBOARD_VIEW_SHOW_BARRE) && getFretboardDraft().isPotentialGriptable()
				&& getFretboardDraft().getShowBarre()) {
			drawBarre(highlightRootNoteWithColor, highlightRootNoteWithShape);
		}
	}

	private void drawNote(final FretboardPosition fbp, final Color bgColor, final Color fgColor,
			final boolean showFingering, final boolean highlightRootNoteWithColor,
			final boolean highlightRootNoteWithShape, final boolean showEmptyStringsTwice) {
		drawNote(fbp, bgColor, fgColor, showFingering, highlightRootNoteWithColor, highlightRootNoteWithShape);
		if (showEmptyStringsTwice && fbp.getFret() == 0) {
			drawNote(new FretboardPosition(fbp.getString(), getCurrentInstrument().getFretCount() + 1), bgColor,
					fgColor, showFingering, highlightRootNoteWithColor, highlightRootNoteWithShape);
		}
	}

	private void drawNote(final FretboardPosition fbp, final Color bgColor, final Color fgColor,
			final boolean showFingering, final boolean highlightRootNoteWithColor,
			final boolean highlightRootNoteWithShape) {

		final boolean isPointMode = UIConstants.MODE_POINTS.equals(mode);
		final boolean frame = isPointMode
				|| Activator.getDefault().getPreferenceStore().getBoolean(Preferences.FRETBOARD_VIEW_FRAME_FINGERING);

		final boolean isRootNote = getFretboardDraft().isRootNote(fbp);

		Color theBgColor = bgColor;
		if (isRootNote && highlightRootNoteWithColor) {
			theBgColor = bgColor == ColorConstants.black ? getWhite() : ColorConstants.black;
		}
		Color theFgColor = fgColor;
		if (theBgColor == ColorConstants.black && frame) {
			theFgColor = getWhite();
		}

		// set visible
		final boolean useLittle = showFingering || isPointMode;
		setVisible(fbp, useLittle);
		final FretboardNotesFigure notesFigure = useLittle ? littleNotesMap.get(fbp) : bigNotesMap.get(fbp);
		notesFigure.setBackgroundColor(theBgColor);
		notesFigure.setOutline(theBgColor != ColorConstants.black && frame);
		notesFigure.setFill(frame);
		notesFigure.setCornerDimensions(isRootNote && highlightRootNoteWithShape ? new Dimension(0, 0) : new Dimension(
				IFigureConstants.NOTES_HEIGHT, IFigureConstants.NOTES_HEIGHT));
		setForegroundColor(fbp, theFgColor, useLittle);

		// exception empty string
		if ((fbp.getFret() == 0 || fbp.getFret() == getCurrentInstrument().getFretCount() + 1) && showFingering) {
			notesFigure.setOutline(true);
			notesFigure.setFill(true);
			notesFigure.setForegroundColor(ColorConstants.black);
		}

		// set text
		final String text = isPointMode ? "" : showFingering ? getFingeringText(fbp) //$NON-NLS-1$
				: UIConstants.MODE_INTERVALS.equals(mode) ? getFretboardDraft().getIntervalName(fbp)
						: getNotesText(fbp);
		notesFigure.setText(text, showFingering ? UIConstants.MODE_FINGERING
				: UIConstants.MODE_INTERVALS.equals(mode) ? mode : UIConstants.MODE_NOTES);
	}

	private void drawMutedString(final int s) {
		final boolean showEmptyStringsTwice = prefs.getBoolean(Preferences.FRETBOARD_VIEW_SHOW_EMPTY_STRINGS_TWICE);

		final List<FretboardPosition> fbps = new ArrayList<FretboardPosition>();
		fbps.add(new FretboardPosition(s - 1, 0));
		if (showEmptyStringsTwice) {
			fbps.add(new FretboardPosition(s - 1, getCurrentInstrument().getFretCount() + 1));
		}

		for (final FretboardPosition fbp : fbps) {
			final FretboardNotesFigure bigNoteFigure = bigNotesMap.get(fbp);
			if (bigNoteFigure != null) {
				bigNoteFigure.setVisible(false);
			}
			final FretboardNotesFigure littleNoteFigure = littleNotesMap.get(fbp);
			if (littleNoteFigure != null) {
				littleNoteFigure.setVisible(false);
			}
			final MutedStringFigure mutedStringFigure = mutedStringFiguresMap.get(fbp);
			if (mutedStringFigure != null) {
				mutedStringFigure.setVisible(true);
				mutedStringFigure.repaint();
			}
		}
	}

	private void drawBarre(final boolean highlightRootNoteWithColor, final boolean highlightRootNoteWithShape) {

		// determine background color
		final boolean isPointMode = UIConstants.MODE_POINTS.equals(mode);
		final BackgroundColorMode fingeringBgColorMode = BackgroundColorMode.valueOf(prefs
				.getString(isPointMode ? Preferences.FRETBOARD_VIEW_POINTS_BACKGROUND
						: Preferences.FRETBOARD_VIEW_FINGERING_BACKGROUND));

		BackgroundColorMode barBgColorMode = BackgroundColorMode.valueOf(prefs
				.getString(Preferences.FRETBOARD_VIEW_BARRE_BAR_BACKGROUND));
		if (BackgroundColorMode.SAME == barBgColorMode) {
			barBgColorMode = fingeringBgColorMode;
		}
		final Color bgColor = barBgColorMode == BackgroundColorMode.BLACK ? ColorConstants.black
				: barBgColorMode == BackgroundColorMode.WHITE ? getWhite()
						: UIConstants.MODE_FINGERING.equals(mode) ? IFigureConstants.RED : UIConstants.MODE_NOTES
								.equals(mode) ? IFigureConstants.GREEN : IFigureConstants.YELLOW;

		// show fingering and/or highlighted root note
		final boolean showInsideBar = prefs.getBoolean(Preferences.FRETBOARD_VIEW_BARRE_BAR_SHOW_ELEMENTS_INSIDE);
		final boolean singleFingeringInBar = prefs
				.getBoolean(Preferences.FRETBOARD_VIEW_BARRE_BAR_SHOW_SINGLE_FINGER_NUMBER);

		for (final Barre barre : getFretboardDraft().getBarres()) {

			final int f = barre.getRelativeFret();
			final int minString = barre.getMinString() - 1;
			final int maxString = barre.getMaxString() - 1;

			final BarreMode barreMode = BarreMode.valueOf(prefs.getString(Preferences.FRETBOARD_VIEW_BARRE_MODE));

			// determine figures
			final FretboardPosition minFbp = new FretboardPosition(minString, f);
			final FretboardPosition maxFbp = new FretboardPosition(maxString, f);
			final boolean little = UIConstants.MODE_FINGERING.equals(mode) || isPointMode;
			final Map<FretboardPosition, FretboardNotesFigure> figureMap = little ? littleNotesMap : bigNotesMap;
			final IFigure minFigure = figureMap.get(minFbp);
			final IFigure maxFigure = figureMap.get(maxFbp);

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

					final boolean highlightRootNote = getFretboardDraft().isRootNote(fbp) && highlightRootNoteWithColor;

					final FretboardNotesFigure figure = figureMap.get(fbp);
					if (figure != null && !figure.isVisible()) {
						if (little) {
							bigNotesMap.get(fbp).setVisible(false);
						}
						continue;
					}

					final List<FretboardPosition> fretboardPositions = getFretboardDraft().getFretboardPositions();
					if (!fretboardPositions.contains(fbp)) {
						figure.setLabelForegroundColor(bgColor == IFigureConstants.GREEN ? IFigureConstants.GREY_ON_GREEN
								: bgColor == IFigureConstants.YELLOW ? IFigureConstants.GREY_ON_YELLOW
										: IFigureConstants.GREY_ON_RED);
						figure.setOutline(false);
						figure.setFill(false);
						figure.setForceMinTextHeight(true);
						continue;
					}

					// show text inside bar
					if (figure != null) {
						figure.setVisible(showInsideBar && (!singleFingeringInBar || maxFigure == figure)
								|| highlightRootNote || !UIConstants.MODE_FINGERING.equals(mode));

						final Color theBgColor = highlightRootNote ? figure.getBackgroundColor() : bgColor;
						figure.setLabelForegroundColor(theBgColor == ColorConstants.black ? getWhite()
								: ColorConstants.black);

						figure.setForceMinTextHeight(true);
						figure.setForegroundColor(ColorConstants.black);
						figure.setOutline(highlightRootNote);
						figure.setFill(highlightRootNote);
					}
				}
			}

			else {

				final boolean leftHander = Activator.getDefault().isLeftHander();
				final ArcConnection connection = barreConnectionMap.get(barre);
				final CenterAnchor minFigureAnchor = new CenterAnchor(minFigure);
				final CenterAnchor maxFigureAnchor = new CenterAnchor(maxFigure);

				// init connection
				connection.setLineWidth(prefs.getInt(Preferences.FRETBOARD_VIEW_BARRE_LINE_WIDTH));
				connection.setDepth(BarreMode.LINE == barreMode ? 0 : 26);
				connection.setSourceAnchor(leftHander ? maxFigureAnchor : minFigureAnchor);
				connection.setTargetAnchor(leftHander ? minFigureAnchor : maxFigureAnchor);
				connection.setVisible(true);
			}
		}
	}

	/**
	 * Clears this layer.
	 */
	private void clear() {

		// set everything invisble
		for (final ArcConnection barreConnection : barreConnectionMap.values()) {
			barreConnection.setVisible(false);
		}
		for (final RoundedRectangle barreFigure : barreFigureMap.values()) {
			barreFigure.setVisible(false);
		}
		for (final MutedStringFigure mutedStringFigure : mutedStringFiguresMap.values()) {
			mutedStringFigure.setVisible(false);
		}
		clearAllLabels(bigNotesMap.values());
		clearAllLabels(littleNotesMap.values());
		setVisible(bigNotesMap.values(), false);
		setVisible(littleNotesMap.values(), false);
	}

	/**
	 * Clears the text labels of all the given note figures.
	 * 
	 * @param figures
	 *            the note figures, must not be null
	 */
	private void clearAllLabels(final Collection<FretboardNotesFigure> figures) {
		if (figures == null) {
			throw new IllegalArgumentException();
		}
		for (final FretboardNotesFigure fretboardNotesFigure : figures) {
			fretboardNotesFigure.setText("", null); //$NON-NLS-1$
		}
	}

	private String getFingeringText(final FretboardPosition fbp) {
		if (fbp.getFret() == 0 || fbp.getFret() == getCurrentInstrument().getCapoFret(fbp.getString() + 1)
				|| fbp.getFret() == getCurrentInstrument().getFretCount() + 1) {
			return ""; //$NON-NLS-1$
		} else {
			final Integer fingerNumber = getFretboardDraft().getAssignment(fbp.getFret(), fbp.getString() + 1);
			if (fingerNumber == null || fingerNumber < 0 || fingerNumber > 4) {
				return "?"; //$NON-NLS-1$
			}
			return "" + fingeringMode.getValue(fingerNumber.intValue());
		}
	}

	private String getNotesText(final FretboardPosition fp) {
		Note note = getCurrentInstrument().getNote(
				fp.getFret() == getCurrentInstrument().getFretCount() + 1 ? new FretboardPosition(fp.getString(), 0)
						: fp);
		note = Factory.getInstance().getNote(note.getValue());
		return notesModeValue.equals(Constants.NOTES_MODE_CROSS_AND_B) ? note.getRelativeName() : notesModeValue
				.equals(Constants.NOTES_MODE_ONLY_CROSS) ? note.getRelativeNameAug() : note.getRelativeNameDim();
	}

	/**
	 * Sets the mode of the notes layes.
	 * 
	 * @param mode
	 *            the mode, use UIConstants.MODE_*
	 */
	public void setMode(final String mode) {
		this.mode = mode;
		drawInput();
	}

	/**
	 * Sets the visible state of all the given note figures.
	 * 
	 * @param figures
	 *            the figures, must not be null
	 * @param visible
	 *            the new visible state
	 */
	private void setVisible(final Collection<FretboardNotesFigure> figures, final boolean visible) {
		if (figures == null) {
			throw new IllegalArgumentException();
		}

		for (final FretboardNotesFigure noteFigure : figures) {
			if (noteFigure != null) {
				noteFigure.setVisible(visible);
			}
		}
	}

	/**
	 * Sets the visible state for the note figures related to the given
	 * fretboard position. Depend on the passed flag will be either the big or
	 * the little note figure set visible.
	 * 
	 * @param fp
	 *            the fretboard position, must not be null
	 * @param setLittleOneVisible
	 *            true if the litte note figure (circle) shall be set visible,
	 *            or false otherwise
	 */
	private void setVisible(final FretboardPosition fp, final boolean setLittleOneVisible) {
		if (fp == null) {
			throw new IllegalArgumentException();
		}

		final FretboardNotesFigure bigNoteFigure = bigNotesMap.get(fp);
		if (bigNoteFigure != null) {
			bigNoteFigure.setVisible(!setLittleOneVisible);
		}
		final FretboardNotesFigure littleNoteFigure = littleNotesMap.get(fp);
		if (littleNoteFigure != null) {
			littleNoteFigure.setVisible(setLittleOneVisible);
		}
	}

	private void setPresentation(final Collection<FretboardNotesFigure> figures, final Color fgColor,
			final Color bgColor, final boolean outline, final boolean fill) {
		if (figures == null || fgColor == null || bgColor == null) {
			throw new IllegalArgumentException();
		}

		for (final FretboardNotesFigure noteFigure : figures) {
			if (noteFigure != null) {
				noteFigure.setBackgroundColor(bgColor);
				noteFigure.setForegroundColor(fgColor);
				noteFigure.setLabelForegroundColor(fgColor);
				noteFigure.setOutline(outline);
				noteFigure.setFill(fill);
			}
		}
	}

	/**
	 * Sets the foreground color for the note figures related to the given
	 * fretboard position.
	 * 
	 * @param fp
	 *            the fretboard position, must not be null
	 * @param color
	 *            the new foreground color, must not be null
	 * @param setColorForLittleOne
	 *            true if for the litte note figure (circle) shall be set the
	 *            new foreground color, or false otherwise
	 */
	private void setForegroundColor(final FretboardPosition fp, final Color color, final boolean setColorForLittleOne) {
		if (fp == null || color == null) {
			throw new IllegalArgumentException();
		}

		final FretboardNotesFigure noteFigure = setColorForLittleOne ? littleNotesMap.get(fp) : bigNotesMap.get(fp);
		if (noteFigure != null) {
			noteFigure.setForegroundColor(color);
			noteFigure.setLabelForegroundColor(color);
		}
	}

	/**
	 * Refreshs this layer.
	 * 
	 * @param reInitialize
	 *            true if the layer shall be initialized again, false otherwise
	 */
	public void refresh(final boolean reInitialize) {
		if (reInitialize) {
			init();
		}
		drawInput();
	}

	public void playNote(final boolean noteOn, final FretboardPosition fbp) {
		final UIJob uiJob = new UIJob(FigureMessages.FretboardNotesLayer_play_note_ui_job) {

			@Override
			public IStatus runInUIThread(final IProgressMonitor monitor) {

				// (1) determine figure
				final FretboardNotesFigure noteFigure = UIConstants.MODE_FINGERING.equals(mode) ? littleNotesMap
						.get(fbp) : bigNotesMap.get(fbp);
				if (noteFigure == null) {
					return Status.CANCEL_STATUS;
				}

				// (2) set colors and outline
				Color bgColor, fgColor, labelFgColor;
				boolean outline, fill;

				if (noteOn) {

					setStyleTemp(fbp, noteFigure.getForegroundColor(), noteFigure.getLabelForegroundColor(),
							noteFigure.getBackgroundColor(), noteFigure.getOutline(), noteFigure.getFill());

					fgColor = ColorConstants.black;
					labelFgColor = ColorConstants.black;
					bgColor = IFigureConstants.SOUND_COLOR;
					outline = true;
					fill = true;

				} else {

					final StyleTemp temp = getStyleTemp(fbp);
					if (temp == null) {
						return Status.CANCEL_STATUS;
					}

					fgColor = temp.getForegroundColor();
					labelFgColor = temp.getLabelForegroundColor();
					bgColor = temp.getBackgroundColor();
					outline = temp.getOutline();
					fill = temp.getFill();

				}

				// (3) draw note figure
				drawNote(noteFigure, fgColor, labelFgColor, bgColor, outline, fill);

				// (4) draw second empty string note figure
				if (fbp.getFret() == 0) {
					final FretboardPosition fbp2 = new FretboardPosition(fbp.getString(), getCurrentInstrument()
							.getFretCount() + 1);
					final FretboardNotesFigure noteFigure2 = UIConstants.MODE_FINGERING.equals(mode) ? littleNotesMap
							.get(fbp2) : bigNotesMap.get(fbp2);
					if (noteFigure2 != null) {
						drawNote(noteFigure2, fgColor, labelFgColor, bgColor, outline, fill);
					}
				}

				return Status.OK_STATUS;
			}
		};
		uiJob.schedule();
	}

	private void drawNote(final FretboardNotesFigure figure, final Color fgColor, final Color labelFgColor,
			final Color bgColor, final boolean outline, final boolean fill) {

		figure.setForegroundColor(fgColor);
		figure.setLabelForegroundColor(labelFgColor);
		figure.setBackgroundColor(bgColor);
		figure.setOutline(outline);
		figure.setFill(fill);
	}

	private void setStyleTemp(final FretboardPosition fbp, final Color fgColor, final Color labelFgColor,
			final Color bgColor, final boolean outline, final boolean fill) {
		if (tempMap == null) {
			tempMap = new HashMap<FretboardPosition, StyleTemp>();
		}
		tempMap.put(fbp, new StyleTemp(fgColor, labelFgColor, bgColor, outline, fill));
	}

	private StyleTemp getStyleTemp(final FretboardPosition fbp) {
		if (tempMap != null) {
			return tempMap.get(fbp);
		}
		return null;
	}

	public void drawInputInUIJob() {
		final UIJob uiJob = new UIJob(FigureMessages.FretboardNotesLayer_draw_input_ui_job) {
			@Override
			public IStatus runInUIThread(final IProgressMonitor monitor) {
				drawInput();
				return Status.OK_STATUS;
			}
		};
		uiJob.schedule();
	}

	public FretboardPosition getFretboardPosition(final Label label) {
		for (final Entry<FretboardPosition, Label> entry : labelMap.entrySet()) {
			if (entry.getValue() == label) {
				return entry.getKey();
			}
		}
		return null;
	}

	public FretboardPosition getFretboardPosition(final FretboardNotesFigure fretboardNotesFigure) {
		final FretboardPosition fbp = getFretboardPosition(fretboardNotesFigure, littleNotesMap);
		return fbp == null ? getFretboardPosition(fretboardNotesFigure, bigNotesMap) : fbp;
	}

	private FretboardPosition getFretboardPosition(final FretboardNotesFigure fretboardNotesFigure,
			final Map<FretboardPosition, FretboardNotesFigure> map) {
		for (final Entry<FretboardPosition, FretboardNotesFigure> entry : map.entrySet()) {
			if (entry.getValue() == fretboardNotesFigure) {
				return entry.getKey();
			}
		}
		return null;
	}

	public FretboardNotesFigure getFretboardNotesFigure(final FretboardPosition fbp) {
		return littleNotesMap.get(fbp);
	}

	private Color getWhite() {
		return getFretboardDraft().isEditable() ? IFigureConstants.TOOLTIP_YELLOW : ColorConstants.white;
	}
}
