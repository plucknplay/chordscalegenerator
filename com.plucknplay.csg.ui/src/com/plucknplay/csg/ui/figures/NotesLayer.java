/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.figures;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.jface.preference.IPreferenceStore;

import com.plucknplay.csg.core.model.Note;
import com.plucknplay.csg.core.model.NotePosition;
import com.plucknplay.csg.core.model.enums.Accidental;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.UIConstants;
import com.plucknplay.csg.ui.model.NotesDraft;
import com.plucknplay.csg.ui.model.NotesDraftUtil;

public class NotesLayer extends AbstractNotesLayer {

	private final XYLayout contentsLayout;

	private Map<Integer, Integer> positionMapColumn1;
	private Map<Integer, Integer> positionMapColumn2;

	private final Comparator<NotePosition> ascendingSorter = new Comparator<NotePosition>() {

		@Override
		public int compare(final NotePosition arg0, final NotePosition arg1) {
			if (arg0.getPosition() < arg1.getPosition()) {
				return -1;
			}
			if (arg0.getPosition() > arg1.getPosition()) {
				return 1;
			}
			return arg0.getAccidental().compareTo(arg1.getAccidental());
		}
	};

	private final Comparator<NotePosition> descendingSorter = new Comparator<NotePosition>() {
		@Override
		public int compare(final NotePosition arg0, final NotePosition arg1) {
			if (arg0.getPosition() > arg1.getPosition()) {
				return -1;
			}
			if (arg0.getPosition() < arg1.getPosition()) {
				return 1;
			}
			return -1 * arg0.getAccidental().compareTo(arg1.getAccidental());
		}
	};

	public NotesLayer(final NotesDraft notesDraft, final String notesMode) {
		super(notesDraft, notesMode);

		contentsLayout = new XYLayout();
		setLayoutManager(contentsLayout);
		init();
	}

	public void init() {

		// (0) clear all
		removeAll();
		positionMapColumn1 = new HashMap<Integer, Integer>();
		positionMapColumn2 = new HashMap<Integer, Integer>();
		final Set<NotePosition> highlightedNotePositions = getNotesDraft().getHighlightedNotePositions();

		// (1) get all necessary preferences
		final IPreferenceStore prefs = Activator.getDefault().getPreferenceStore();
		final boolean drawHighlightedNPs = prefs.getBoolean(Preferences.SHOW_BLOCKS);
		final String mode = getNotesDraft().isEditable() ? UIConstants.DISPLAY_AS_BLOCK : getDisplayMode();
		final boolean openNotes = prefs.getBoolean(Preferences.NOTES_VIEW_OPEN_NOTE_REPRESENTATION);
		final boolean highlightRootNote = prefs.getBoolean(Preferences.NOTES_VIEW_HIGHLIGHT_ROOT_NOTE);
		final boolean flexibleNoteSpacing = prefs.getBoolean(Preferences.NOTES_VIEW_FLEXIBLE_SPACING);

		int stepWidth = IFigureConstants.ACCIDENTAL_IMAGE_WIDTH + IFigureConstants.NOTE_WIDTH
				+ IFigureConstants.LEDGER_LINE_OFFSET + IFigureConstants.NOTE_ACCIDENTAL_SPACING;
		if (flexibleNoteSpacing && !getNotesDraft().getNotePositions().isEmpty()) {
			final int flexibleStepWidth = (NotesDraftUtil.getStaffWidth(mode, getNotesDraft())
					- IFigureConstants.CLEF_IMAGE_WIDTH - 2 * IFigureConstants.COMMON_NOTE_SPACING)
					/ (getNotesDraft().getNotePositions().size() + 0);
			stepWidth = Math.max(stepWidth, flexibleStepWidth);
		}

		// (2) determine sign positions
		final Map<Integer, Integer> columnSigns = new HashMap<Integer, Integer>();
		final Map<Integer, Integer> column1Signs = new HashMap<Integer, Integer>();
		final Map<Integer, Integer> column2Signs = new HashMap<Integer, Integer>();
		int lastStartSignPos = 0;
		int lastStartSignPos1 = 0;
		int lastStartSignPos2 = 0;
		final List<NotePosition> notePositionsWithSign = new ArrayList<NotePosition>();
		final List<NotePosition> notePositionsWithoutSign = new ArrayList<NotePosition>();
		final List<NotePosition> notePositionsWithSignOnSecondColumn = new ArrayList<NotePosition>();

		if (mode.equals(UIConstants.DISPLAY_AS_BLOCK)) {

			// (2.1) sort note positions in descending order
			final List<NotePosition> notePositions = new ArrayList<NotePosition>(getNotesDraft().getNotePositions());
			Collections.sort(notePositions, descendingSorter);

			// (2.2) separate note positions
			for (final NotePosition notePosition : notePositions) {
				if (notePosition.getAccidental() == Accidental.NONE) {
					notePositionsWithoutSign.add(notePosition);
				} else {
					notePositionsWithSign.add(notePosition);
				}
			}

			// (2.3) determine sign column information
			int currentColumn = 1;
			int currentColumn1 = 1;
			int currentColumn2 = 1;
			for (final NotePosition notePosition : notePositionsWithSign) {
				final int pos = notePosition.getPosition();

				// handle 'all in one' column
				int[] results = checkSignColumns(columnSigns, currentColumn, pos, lastStartSignPos);
				currentColumn = results[0];
				lastStartSignPos = results[1];

				// handle separated column
				boolean isSecondNotesColumnNecessary = false;
				for (final NotePosition notePosition2 : notePositionsWithoutSign) {
					if (notePosition2.getPosition() == pos) {
						isSecondNotesColumnNecessary = true;
						notePositionsWithSignOnSecondColumn.add(notePosition);
						break;
					}
				}

				// check first sign columns
				if (!isSecondNotesColumnNecessary) {
					results = checkSignColumns(column1Signs, currentColumn1, pos, lastStartSignPos1);
					currentColumn1 = results[0];
					lastStartSignPos1 = results[1];
				} else {
					results = checkSignColumns(column2Signs, currentColumn2, pos, lastStartSignPos2);
					currentColumn2 = results[0];
					lastStartSignPos2 = results[1];
				}
			}
		}

		// (3) sort note positions
		final List<NotePosition> notePositions = new ArrayList<NotePosition>(getNotesDraft().getNotePositions());
		Collections.sort(notePositions, mode.equals(UIConstants.DISPLAY_AS_DESC_ARPEGGIO) ? descendingSorter
				: ascendingSorter);

		// (4) determine offsets and reference sets
		final int maxColumnSignNumber = determineMaxColumnNumber(columnSigns);
		final int maxColumn1SignNumber = determineMaxColumnNumber(column1Signs);
		final int maxColumn2SignNumber = determineMaxColumnNumber(column2Signs);
		final Set<NotePosition> firstColumnNotes = new HashSet<NotePosition>(notePositionsWithoutSign);
		final Set<NotePosition> secondColumnNotes = new HashSet<NotePosition>(notePositionsWithSignOnSecondColumn);
		final boolean useSecondColumnForSigns = maxColumn1SignNumber + maxColumn2SignNumber > 7;
		int signNumber1 = maxColumn1SignNumber;
		int signNumber2 = maxColumn2SignNumber;
		int signNumber = maxColumn1SignNumber + maxColumn2SignNumber;
		if (useSecondColumnForSigns) {
			secondColumnNotes.addAll(notePositionsWithSign);
			signNumber1 = 0;
			signNumber2 = maxColumnSignNumber;
			signNumber = maxColumnSignNumber;
		} else {
			firstColumnNotes.addAll(notePositionsWithSign);
			firstColumnNotes.removeAll(notePositionsWithSignOnSecondColumn);
		}

		int x0 = IFigureConstants.NOTE_START_X;
		if (!mode.equals(UIConstants.DISPLAY_AS_BLOCK) && notePositions.size() < 4) {
			x0 += stepWidth / 4;
		}
		final int secondColumnWidth = IFigureConstants.NOTE_WIDTH + IFigureConstants.LEDGER_LINE_OFFSET / 2
				+ IFigureConstants.ACCIDENTAL_ACCIDENTAL_SPACING;
		final int xOffset = (7 - signNumber) / 2 * IFigureConstants.ACCIDENTAL_IMAGE_WIDTH
				+ (secondColumnNotes.isEmpty() ? secondColumnWidth / 2 : 0);
		final int xSignOffset1 = xOffset + x0;
		final int xNoteOffset1 = xSignOffset1 + signNumber1 * IFigureConstants.ACCIDENTAL_IMAGE_WIDTH;
		final int xSignOffset2 = xNoteOffset1 + 2 * IFigureConstants.NOTE_WIDTH + 3 / 2
				* IFigureConstants.LEDGER_LINE_OFFSET + IFigureConstants.ACCIDENTAL_ACCIDENTAL_SPACING;
		final int xNoteOffset2 = xSignOffset2 + signNumber2 * IFigureConstants.ACCIDENTAL_IMAGE_WIDTH;

		// (5) create note position figure for each valid displayable note
		// position
		int x = 0;
		for (final NotePosition notePosition : notePositions) {
			final int pos = notePosition.getPosition();
			final int relativePos = getRelativePosition(notePosition);
			final int yOffset = getYOffset();

			final boolean drawHighlighted = drawHighlightedNPs && highlightedNotePositions.contains(notePosition)
					&& !getNotesDraft().isEditable();

			// create sign figure
			if (notePosition.getAccidental() != Accidental.NONE) {
				final AccidentalFigure signFigure = new AccidentalFigure(notePosition, drawHighlighted);
				add(signFigure);

				int y = yOffset + relativePos * (IFigureConstants.NOTE_LINE_DISTANCE / 2);
				y -= notePosition.getAccidental() == Accidental.SHARP ? 60 : 105;

				int theX = x0 + x;
				if (mode.equals(UIConstants.DISPLAY_AS_BLOCK)) {
					final Map<Integer, Integer> theColumnSigns = useSecondColumnForSigns ? columnSigns
							: firstColumnNotes.contains(notePosition) ? column1Signs : column2Signs;
					final int theMaxColumn = useSecondColumnForSigns ? maxColumnSignNumber : firstColumnNotes
							.contains(notePosition) ? maxColumn1SignNumber : maxColumn2SignNumber;
					theX = firstColumnNotes.contains(notePosition) ? xSignOffset1 : xSignOffset2;
					final Integer theColumn = theColumnSigns.get(pos);
					if (theColumn == null) {
						throw new Error();
					}
					theX += (theMaxColumn - theColumn.intValue()) * IFigureConstants.ACCIDENTAL_IMAGE_WIDTH;
				}

				final Rectangle rectangle = new Rectangle(theX, y, IFigureConstants.ACCIDENTAL_IMAGE_WIDTH,
						IFigureConstants.ACCIDENTAL_IMAGE_HEIGHT);
				signFigure.setBounds(rectangle);
				contentsLayout.setConstraint(signFigure, rectangle);
			}

			final boolean isOnTopOfLine = getNotesDraft().isOnTopOfLine(notePosition);
			// create note figure
			final NotePositionFigure figure = new NotePositionFigure(notePosition, getNotesDraft()
					.getNumberOfLedgerLines(notePosition), getNotesDraft().isAboveStaff(notePosition), isOnTopOfLine);
			final Note note = notePosition.getNote();
			figure.setImage(drawHighlighted, openNotes, (getNotesDraft().isEditable() || highlightRootNote)
					&& getNotesDraft().isRootNote(note));

			// Since notes above/below the staff span from the last/first staff
			// line, it has to be prevented that newly added note figures hide
			// previous ones.
			// Note: They are, strictly speaking, not hidden but unclickable.
			add(figure, getNotesDraft().isAboveStaff(notePosition) ? 0 : -1);

			figure.setBackgroundColor(drawHighlighted ? IFigureConstants.DARK_BLUE : ColorConstants.black);
			figure.setForegroundColor(drawHighlighted ? IFigureConstants.DARK_BLUE : ColorConstants.black);

			// determine offset
			int offset = 0;
			if (mode.equals(UIConstants.DISPLAY_AS_BLOCK)) {
				final Integer potentialPredecessorColumn = firstColumnNotes.contains(notePosition) ? positionMapColumn1
						.get(pos - 1) : positionMapColumn2.get(pos - 1);
				if (potentialPredecessorColumn != null) {
					offset = 1 - potentialPredecessorColumn;
				}
			}

			// determine remaining constraints
			int y1 = yOffset + relativePos * (IFigureConstants.NOTE_LINE_DISTANCE / 2);

			// determine height of notes figure
			// NOTE: height depends on necessary number of ledger lines
			int height = IFigureConstants.NOTE_LINE_DISTANCE + 1;
			final int numberOfLedgerLines = getNotesDraft().getNumberOfLedgerLines(notePosition);
			if (numberOfLedgerLines > 0) {
				height += IFigureConstants.NOTE_LINE_DISTANCE * numberOfLedgerLines;
				if (isOnTopOfLine) {
					height -= IFigureConstants.NOTE_LINE_DISTANCE / 2;
				}
				if (getNotesDraft().isBelowStaff(notePosition)) {
					y1 -= IFigureConstants.NOTE_LINE_DISTANCE * numberOfLedgerLines;
					if (isOnTopOfLine) {
						y1 += IFigureConstants.NOTE_LINE_DISTANCE / 2;
					}
				}
			}

			final int width = IFigureConstants.NOTE_WIDTH + IFigureConstants.LEDGER_LINE_OFFSET;

			int theX = x0 + x + IFigureConstants.ACCIDENTAL_IMAGE_WIDTH + offset * IFigureConstants.NOTE_WIDTH;
			if (mode.equals(UIConstants.DISPLAY_AS_BLOCK)) {
				theX = firstColumnNotes.contains(notePosition) ? xNoteOffset1 : xNoteOffset2;
				theX += offset * IFigureConstants.NOTE_WIDTH;
			}

			// layout figure
			final Rectangle rectangle2 = new Rectangle(theX, y1, width, height);
			figure.setBounds(rectangle2);
			contentsLayout.setConstraint(figure, rectangle2);

			// store offset
			if (firstColumnNotes.contains(notePosition)) {
				positionMapColumn1.put(pos, offset);
			} else {
				positionMapColumn2.put(pos, offset);
			}

			if (!mode.equals(UIConstants.DISPLAY_AS_BLOCK)) {
				x += stepWidth;
			}
		}
	}

	private int[] checkSignColumns(final Map<Integer, Integer> signColumns, final int currentColumn, final int pos,
			final int lastStartSignPosition) {

		int column = currentColumn;
		int lastStartSignPos = lastStartSignPosition;

		if (column == 1) {
			signColumns.put(pos, column);
			lastStartSignPos = pos;
		} else {
			if (pos + 7 > lastStartSignPos) {
				signColumns.put(pos, column);
			} else {
				column = 1;
				signColumns.put(pos, column);
				lastStartSignPos = pos;
			}
		}

		column++;
		if (column > 7) {
			column = 1;
		}

		final int newStartSignPos = checkSignMap(signColumns, pos);
		if (newStartSignPos != -1) {
			column--;
			lastStartSignPos = newStartSignPos;
		}

		final int[] results = new int[2];
		results[0] = column;
		results[1] = lastStartSignPos;

		return results;
	}

	private int checkSignMap(final Map<Integer, Integer> signColumns, final int position) {

		int lastStartSignPosition = -1;

		final int column = signColumns.get(position);
		if (column == 1) {
			return lastStartSignPosition;
		}

		boolean reorganizeMap = false;
		for (final Entry<Integer, Integer> entry : signColumns.entrySet()) {
			final int currentColumn = entry.getValue();
			if (currentColumn == column) {
				final int currentPosition = entry.getKey();
				if (position != currentPosition && position + 7 > currentPosition) {
					reorganizeMap = true;
					break;
				}
			}
		}

		if (reorganizeMap) {
			int lastPos = -1;
			for (int pos = position + 1; pos <= 30; pos++) {
				final Integer currentColumn = signColumns.get(pos);
				if (currentColumn != null) {
					if (lastPos == -1) {
						final int newColumn = currentColumn - 1;
						if (newColumn > 0) {
							signColumns.put(pos, newColumn);
							if (newColumn == 1) {
								lastStartSignPosition = pos;
							}
						} else {
							lastPos = pos;
							continue;
						}
					} else {
						signColumns.put(lastPos, currentColumn + 1);
						break;
					}
				}
			}

			final int newColumn = column - 1;
			signColumns.put(position, newColumn);
			if (newColumn == 1) {
				lastStartSignPosition = position;
			}
		}
		return lastStartSignPosition;
	}

	private int determineMaxColumnNumber(final Map<Integer, Integer> columnSigns) {
		int maxColumn = 0;
		for (final Integer column : columnSigns.values()) {
			if (column > maxColumn) {
				maxColumn = column;
				if (maxColumn == 7) {
					break;
				}
			}
		}
		return maxColumn;
	}

	public void refresh() {

		final Set<NotePosition> highlightedNotePositions = getNotesDraft().getHighlightedNotePositions();

		final IPreferenceStore prefs = Activator.getDefault().getPreferenceStore();
		final boolean drawHighlightedNPs = prefs.getBoolean(Preferences.SHOW_BLOCKS);
		final boolean openNotes = prefs.getBoolean(Preferences.NOTES_VIEW_OPEN_NOTE_REPRESENTATION);
		final boolean highlightRootNote = prefs.getBoolean(Preferences.NOTES_VIEW_HIGHLIGHT_ROOT_NOTE);

		getChildren();
		for (final Object child : getChildren()) {
			if (!(child instanceof NotePositionFigure)) {
				continue;
			}

			final NotePositionFigure figure = (NotePositionFigure) child;
			final NotePosition notePosition = figure.getNotePosition();
			final Note note = notePosition.getNote();

			final boolean drawHighlighted = drawHighlightedNPs && highlightedNotePositions.contains(notePosition)
					&& !getNotesDraft().isEditable();

			figure.setImage(drawHighlighted, openNotes, (getNotesDraft().isEditable() || highlightRootNote)
					&& getNotesDraft().isRootNote(note));
		}
	}
}
