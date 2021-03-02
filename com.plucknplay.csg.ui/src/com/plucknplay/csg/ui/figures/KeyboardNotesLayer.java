/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.figures;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.progress.UIJob;

import com.plucknplay.csg.core.Constants;
import com.plucknplay.csg.core.model.Factory;
import com.plucknplay.csg.core.model.Note;
import com.plucknplay.csg.core.util.ToneRangeMode;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.UIConstants;
import com.plucknplay.csg.ui.model.KeyboardDraft;
import com.plucknplay.csg.ui.util.RGBUtil;
import com.plucknplay.csg.ui.util.StyleTemp;
import com.plucknplay.csg.ui.util.TooltipUtil;
import com.plucknplay.csg.ui.util.enums.BackgroundColorMode;
import com.plucknplay.csg.ui.util.enums.KeySizeMode;

public class KeyboardNotesLayer extends AbstractKeyboardLayer {

	private static Color highlightColor;

	private List<Map<Note, KeyboardNoteFigure>> figureMapsList;
	private Map<Note, KeyboardNoteFigure> littleWhiteNotesMap;
	private Map<Note, KeyboardNoteFigure> littleBlackNotesMap;
	private Map<Note, KeyboardNoteFigure> bigWhiteNotesMap;
	private Map<Note, KeyboardNoteFigure> bigBlackNotesMap;

	private final XYLayout contentsLayout;

	private String mode;

	private Map<Note, StyleTemp> tempMap;

	public KeyboardNotesLayer(final KeyboardDraft keyboardDraft) {
		super(keyboardDraft);

		contentsLayout = new XYLayout();
		setLayoutManager(contentsLayout);
		init();
	}

	@Override
	protected void init() {
		removeAll();

		final KeySizeMode keySizeMode = KeySizeMode.valueOf(Activator.getDefault().getPreferenceStore()
				.getString(Preferences.KEYBOARD_VIEW_KEY_SIZE));

		// for single notes or intervals with less than 2 chars
		littleWhiteNotesMap = new HashMap<Note, KeyboardNoteFigure>();
		littleBlackNotesMap = new HashMap<Note, KeyboardNoteFigure>();
		// for intervals with more than 2 characters
		bigWhiteNotesMap = new HashMap<Note, KeyboardNoteFigure>();
		// for doubled notes or intervals with more than 2 characters
		bigBlackNotesMap = new HashMap<Note, KeyboardNoteFigure>();

		figureMapsList = new ArrayList<Map<Note, KeyboardNoteFigure>>();
		figureMapsList.add(littleWhiteNotesMap);
		figureMapsList.add(littleBlackNotesMap);
		figureMapsList.add(bigWhiteNotesMap);
		figureMapsList.add(bigBlackNotesMap);

		final int whiteKeyHeight = keySizeMode.getWhiteKeyHeight(getKeyboardDraft().getKeyNumber());
		final int blackKeyHeight = keySizeMode.getBlackKeyHeight(getKeyboardDraft().getKeyNumber());

		final int x01 = IFigureConstants.KEYBOARD_OFFSET_X + IFigureConstants.KEYBOARD_NOTES_OFFSET_X;
		final int y01 = IFigureConstants.KEYBOARD_OFFSET_Y + whiteKeyHeight - IFigureConstants.KEYBOARD_NOTES_OFFSET_Y
				- IFigureConstants.KEYBOARD_NOTES_HEIGHT;
		final int y012 = IFigureConstants.KEYBOARD_OFFSET_Y + whiteKeyHeight - IFigureConstants.KEYBOARD_NOTES_OFFSET_Y
				- IFigureConstants.KEYBOARD_TWO_NOTES_HEIGHT;
		final int x02 = IFigureConstants.KEYBOARD_OFFSET_X + IFigureConstants.WHITE_KEY_WIDTH / 2
				+ IFigureConstants.KEYBOARD_NOTES_OFFSET_X;
		final int y02 = IFigureConstants.KEYBOARD_OFFSET_Y + blackKeyHeight - IFigureConstants.KEYBOARD_NOTES_OFFSET_Y
				- IFigureConstants.KEYBOARD_NOTES_HEIGHT;
		final int y03 = IFigureConstants.KEYBOARD_OFFSET_Y + blackKeyHeight - IFigureConstants.KEYBOARD_NOTES_OFFSET_Y
				- IFigureConstants.KEYBOARD_TWO_NOTES_HEIGHT;

		int i = 0;
		for (final Iterator<Note> iter = toneRangeIterator(); iter.hasNext();) {
			final Note note = iter.next();

			// initialize white keynotes
			if (!note.hasAccidental()) {

				// little note
				final KeyboardNoteFigure littleWhiteNoteFigure = new KeyboardNoteFigure(KeyboardNoteFigure.SIZE_LITTLE,
						note);
				littleWhiteNoteFigure.setVisible(true);
				littleWhiteNoteFigure.setToolTip(TooltipUtil.getToolTipLabel(note, !showOnlyRelativeNotes()));
				littleWhiteNotesMap.put(note, littleWhiteNoteFigure);
				add(littleWhiteNoteFigure);
				contentsLayout.setConstraint(littleWhiteNoteFigure, new Rectangle(x01 + i
						* IFigureConstants.WHITE_KEY_WIDTH, y01, IFigureConstants.KEYBOARD_NOTES_HEIGHT,
						IFigureConstants.KEYBOARD_NOTES_HEIGHT));

				// big note
				final KeyboardNoteFigure bigWhiteNoteFigure = new KeyboardNoteFigure(KeyboardNoteFigure.SIZE_BIG, note);
				bigWhiteNoteFigure.setVisible(false);
				bigWhiteNoteFigure.setToolTip(TooltipUtil.getToolTipLabel(note, !showOnlyRelativeNotes()));
				bigWhiteNotesMap.put(note, bigWhiteNoteFigure);
				add(bigWhiteNoteFigure);
				contentsLayout.setConstraint(bigWhiteNoteFigure, new Rectangle(x01 + i
						* IFigureConstants.WHITE_KEY_WIDTH, y012, IFigureConstants.KEYBOARD_NOTES_HEIGHT,
						IFigureConstants.KEYBOARD_TWO_NOTES_HEIGHT));
				i++;
			}

			// initialize black key notes
			else {
				// little note
				final KeyboardNoteFigure littleBlackNoteFigure = new KeyboardNoteFigure(KeyboardNoteFigure.SIZE_LITTLE,
						note);
				littleBlackNoteFigure.setVisible(true);
				littleBlackNoteFigure.setToolTip(TooltipUtil.getToolTipLabel(note, !showOnlyRelativeNotes()));
				littleBlackNotesMap.put(note, littleBlackNoteFigure);
				add(littleBlackNoteFigure);
				contentsLayout.setConstraint(littleBlackNoteFigure, new Rectangle(x02 + (i - 1)
						* IFigureConstants.WHITE_KEY_WIDTH, y02, IFigureConstants.KEYBOARD_NOTES_HEIGHT,
						IFigureConstants.KEYBOARD_NOTES_HEIGHT));

				// big note
				final KeyboardNoteFigure bigBlackNoteFigure = new KeyboardNoteFigure(KeyboardNoteFigure.SIZE_BIG, note);
				bigBlackNoteFigure.setVisible(false);
				bigBlackNoteFigure.setToolTip(TooltipUtil.getToolTipLabel(note, !showOnlyRelativeNotes()));
				bigBlackNotesMap.put(note, bigBlackNoteFigure);
				add(bigBlackNoteFigure);
				contentsLayout.setConstraint(bigBlackNoteFigure, new Rectangle(x02 + (i - 1)
						* IFigureConstants.WHITE_KEY_WIDTH, y03, IFigureConstants.KEYBOARD_NOTES_HEIGHT,
						IFigureConstants.KEYBOARD_TWO_NOTES_HEIGHT));
			}
		}
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

	@Override
	protected void drawInput() {
		clear();

		// load preferences
		final boolean isPointMode = UIConstants.MODE_POINTS.equals(mode);
		final IPreferenceStore prefs = Activator.getDefault().getPreferenceStore();
		final String notesMode = prefs.getString(Preferences.NOTES_MODE);
		final boolean highlightRootNote = prefs.getBoolean(Preferences.KEYBOARD_VIEW_HIGHLIGHT_ROOT_NOTE);
		final boolean highlightRootNoteWithShape = highlightRootNote
				&& prefs.getBoolean(Preferences.KEYBOARD_VIEW_HIGHLIGHT_ROOT_NOTE_WITH_SHAPE);
		final boolean highlightRootNoteWithColor = highlightRootNote
				&& prefs.getBoolean(Preferences.KEYBOARD_VIEW_HIGHLIGHT_ROOT_NOTE_WITH_COLOR);
		final boolean frame = isPointMode || prefs.getBoolean(Preferences.KEYBOARD_VIEW_FRAME_NOTES_INTERVALS);

		// determine input
		final boolean relativeNoteMode = ToneRangeMode.valueOf(
				prefs.getString(Preferences.KEYBOARD_VIEW_TONE_RANGE_MODE)).showOnlyRelativeNotes();
		final boolean showBlocks = prefs.getBoolean(Preferences.SHOW_BLOCKS);
		final boolean isGriptable = getKeyboardDraft().isGriptable();
		final boolean isBlock = showBlocks && getKeyboardDraft().isBlock();
		final boolean isChordScale = !showBlocks && getKeyboardDraft().isBlock();

		// determine colors
		final Color fgColor = ColorConstants.black;
		final BackgroundColorMode bgColorMode = BackgroundColorMode.valueOf(prefs
				.getString(Preferences.KEYBOARD_VIEW_NOTES_INTERVALS_BACKGROUND));
		final Color bgColor = bgColorMode == BackgroundColorMode.WHITE || isPointMode ? getWhite()
				: UIConstants.MODE_INTERVALS.equals(mode) ? IFigureConstants.YELLOW : IFigureConstants.GREEN;
		highlightColor = new Color(Display.getDefault(), RGBUtil.convertStringToRGB(prefs
				.getString(Preferences.KEYBOARD_VIEW_HIGHLIGHT_ROOT_NOTE_COLOR_ID)));
		final boolean showAdditionalNotesInBlack = prefs
				.getBoolean(Preferences.KEYBOARD_VIEW_SHOW_ADDITIONAL_NOTES_IN_BLACK);
		final Color additionalNotesFgColor = showAdditionalNotesInBlack && frame && !isPointMode
				&& (bgColorMode == BackgroundColorMode.COLORED || getKeyboardDraft().isEmpty()) ? ColorConstants.black
				: IFigureConstants.GREY;

		final String theMode = getKeyboardDraft().isEmpty() ? UIConstants.MODE_NOTES : mode;

		// additional notes
		final boolean additionalNotesOnEmptyKeyboard = prefs
				.getBoolean(Preferences.KEYBOARD_VIEW_SHOW_ADDITIONAL_NOTES_ON_EMPTY_KEYBOARD);
		final boolean additionalNotesForGriptable = prefs
				.getBoolean(Preferences.KEYBOARD_VIEW_SHOW_ADDITIONAL_NOTES_FOR_GRIPTABLE) && !isPointMode;
		final boolean additionalNotesForChordScale = prefs
				.getBoolean(Preferences.KEYBOARD_VIEW_SHOW_ADDITIONAL_NOTES_FOR_CHORD_AND_SCALE) && !isPointMode;
		final boolean additionalNotesForBlock = prefs
				.getBoolean(Preferences.KEYBOARD_VIEW_SHOW_ADDITIONAL_NOTES_FOR_BLOCK) && !isPointMode;
		final boolean showAdditionalChordNotes = prefs
				.getBoolean(Preferences.KEYBOARD_VIEW_SHOW_ADDITIONAL_CHORD_NOTES);
		final boolean showAdditionalBlockNotes = prefs
				.getBoolean(Preferences.KEYBOARD_VIEW_SHOW_ADDITIONAL_BLOCK_NOTES);
		final boolean showBlockOnly = UIConstants.BLOCK_NO_OVERLAY_FRAME.equals(prefs
				.getString(Preferences.KEYBOARD_VIEW_BLOCK_PRESENTATION));

		// draw white notes (first cycle) and black notes (second cycle)
		for (int i = 0; i < 2; i++) {

			final Map<Note, KeyboardNoteFigure> littleNotesMap = i == 0 ? littleWhiteNotesMap : littleBlackNotesMap;
			final Map<Note, KeyboardNoteFigure> bigNotesMap = i == 0 ? bigWhiteNotesMap : bigBlackNotesMap;

			final Set<Entry<Note, KeyboardNoteFigure>> entrySet = littleNotesMap.entrySet();
			for (final Entry<Note, KeyboardNoteFigure> entry : entrySet) {

				final Note absoluteNote = entry.getKey();
				final Note relativeNote = Factory.getInstance().getNote(absoluteNote.getValue());
				final boolean isRootNote = getKeyboardDraft().isRootNote(absoluteNote);

				Color theFgColor = null;
				Color theBgColor = null;

				// main notes
				if (getKeyboardDraft().getAbsoluteNotes().contains(absoluteNote)
						|| getKeyboardDraft().getRelativeNotes().contains(relativeNote)
						&& (relativeNoteMode || isBlock && !showBlockOnly && !getKeyboardDraft().isEditable())) {

					theBgColor = bgColor;
					theFgColor = highlightRootNoteWithColor && isRootNote ? highlightColor : fgColor;

					// set white foreground color for non-framed black keys
					if (!frame && i == 1 && theFgColor != highlightColor) {
						theFgColor = getWhite();
					}
				}

				// additional notes
				else if (!getKeyboardDraft().isEditable()
						&& (getKeyboardDraft().isEmpty() && additionalNotesOnEmptyKeyboard || isGriptable
								&& additionalNotesForGriptable || isChordScale && additionalNotesForChordScale
								|| isBlock && additionalNotesForBlock || getKeyboardDraft().getRelativeNotes()
								.contains(relativeNote)
								&& (isGriptable && showAdditionalChordNotes || isBlock && showAdditionalBlockNotes))) {

					theBgColor = getWhite();
					theFgColor = additionalNotesFgColor;
				}

				// draw note figure
				if (theBgColor != null && theFgColor != null) {

					final KeyboardNoteFigure noteFigure = UIConstants.MODE_NOTES.equals(theMode)
							&& notesMode.equals(Constants.NOTES_MODE_CROSS_AND_B) && i == 1
							|| UIConstants.MODE_INTERVALS.equals(theMode) && getIntervalText(absoluteNote).length() > 2 ? bigNotesMap
							.get(absoluteNote) : littleNotesMap.get(absoluteNote);

					noteFigure.setVisible(true);
					noteFigure.setBackgroundColor(theBgColor);
					noteFigure.setForegroundColor(theFgColor);
					noteFigure.setLabelForegroundColor(theFgColor);
					noteFigure.setOutline(frame);
					noteFigure.setFill(frame);
					noteFigure.setCornerDimensions(isRootNote && highlightRootNoteWithShape ? new Dimension(0, 0)
							: new Dimension(IFigureConstants.KEYBOARD_NOTES_HEIGHT,
									IFigureConstants.KEYBOARD_NOTES_HEIGHT));
					noteFigure.setText(UIConstants.MODE_NOTES.equals(theMode) ? getNoteText(absoluteNote, notesMode)
							: getIntervalText(absoluteNote), theMode);
				}
			}
		}
	}

	/**
	 * Clears this layer.
	 */
	private void clear() {
		setVisible(littleWhiteNotesMap.values(), false);
		setVisible(littleBlackNotesMap.values(), false);
		setVisible(bigWhiteNotesMap.values(), false);
		setVisible(bigBlackNotesMap.values(), false);
	}

	/**
	 * Sets the visible state of all the given note figures.
	 * 
	 * @param figures
	 *            the figures, must not be null
	 * @param visible
	 *            the new visible state
	 */
	private void setVisible(final Collection<KeyboardNoteFigure> figures, final boolean visible) {
		if (figures == null) {
			throw new IllegalArgumentException();
		}

		for (final KeyboardNoteFigure noteFigure : figures) {
			if (noteFigure != null) {
				noteFigure.setVisible(visible);
			}
		}
	}

	/**
	 * Returns the interval text for the given note and the specified root note.
	 * 
	 * @param note
	 *            the note, must not be null
	 * 
	 * @return the interval text for the given note and the specified root note
	 */
	private String getIntervalText(final Note note) {
		if (note == null) {
			throw new IllegalArgumentException();
		}
		return getKeyboardDraft().getIntervalName(note);
	}

	/**
	 * Returns the note text for the given note.
	 * 
	 * @param note
	 *            the note, must not be null
	 * 
	 * @return the note text for the given note
	 */
	private String getNoteText(final Note note, final String notesMode) {
		if (note == null) {
			throw new IllegalArgumentException();
		}

		final String result = notesMode.equals(Constants.NOTES_MODE_CROSS_AND_B) ? note.getRelativeName() : notesMode
				.equals(Constants.NOTES_MODE_ONLY_CROSS) ? note.getRelativeNameAug() : note.getRelativeNameDim();
		return result;
	}

	public void playNote(final boolean noteOn, final Note note) {

		final UIJob uiJob = new UIJob(FigureMessages.KeyboardNotesLayer_play_note_ui_job) {
			@Override
			public IStatus runInUIThread(final IProgressMonitor monitor) {

				// (1) determine figure
				KeyboardNoteFigure noteFigure = null;
				for (final Map<Note, KeyboardNoteFigure> map : figureMapsList) {
					noteFigure = map.get(note);
					if (noteFigure != null && noteFigure.isVisible()) {
						break;
					}
				}
				if (noteFigure == null) {
					return Status.CANCEL_STATUS;
				}

				// (2) set colors and outline
				Color bgColor, fgColor, labelFgColor;
				boolean outline, fill;

				if (noteOn) {

					setStyleTemp(note, noteFigure.getForegroundColor(), noteFigure.getLabelForegroundColor(),
							noteFigure.getBackgroundColor(), noteFigure.getOutline(), noteFigure.getFill());

					fgColor = ColorConstants.black;
					labelFgColor = ColorConstants.black;
					bgColor = IFigureConstants.SOUND_COLOR;
					outline = true;
					fill = true;

				} else {

					final StyleTemp temp = getStyleTemp(note);
					if (temp == null) {
						return Status.CANCEL_STATUS;
					}

					fgColor = temp.getForegroundColor();
					labelFgColor = temp.getLabelForegroundColor();
					bgColor = temp.getBackgroundColor();
					outline = temp.getOutline();
					fill = temp.getFill();

				}

				// (3) draw note figures
				noteFigure.setForegroundColor(fgColor);
				noteFigure.setLabelForegroundColor(labelFgColor);
				noteFigure.setBackgroundColor(bgColor);
				noteFigure.setOutline(outline);
				noteFigure.setFill(fill);

				return Status.OK_STATUS;
			}
		};
		uiJob.schedule();
	}

	private void setStyleTemp(final Note note, final Color fgColor, final Color labelFgColor, final Color bgColor,
			final boolean outline, final boolean fill) {
		if (tempMap == null) {
			tempMap = new HashMap<Note, StyleTemp>();
		}
		tempMap.put(note, new StyleTemp(fgColor, labelFgColor, bgColor, outline, fill));
	}

	private StyleTemp getStyleTemp(final Note note) {
		if (tempMap != null) {
			return tempMap.get(note);
		}
		return null;
	}

	public void drawInputInUIJob() {
		final UIJob uiJob = new UIJob(FigureMessages.KeyboardNotesLayer_draw_input_ui_job) {
			@Override
			public IStatus runInUIThread(final IProgressMonitor monitor) {
				drawInput();
				return Status.OK_STATUS;
			}
		};
		uiJob.schedule();
	}

	private Color getWhite() {
		return getKeyboardDraft().isEditable() ? IFigureConstants.TOOLTIP_YELLOW : ColorConstants.white;
	}
}
