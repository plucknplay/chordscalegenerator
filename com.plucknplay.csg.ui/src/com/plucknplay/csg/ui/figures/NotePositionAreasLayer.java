/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.figures;

import java.util.Iterator;

import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Rectangle;

import com.plucknplay.csg.core.model.Note;
import com.plucknplay.csg.core.model.NotePosition;
import com.plucknplay.csg.core.model.enums.Accidental;
import com.plucknplay.csg.ui.UIConstants;
import com.plucknplay.csg.ui.model.NotesDraft;
import com.plucknplay.csg.ui.model.NotesDraftUtil;

public class NotePositionAreasLayer extends AbstractNotesLayer {

	private final XYLayout contentsLayout;

	public NotePositionAreasLayer(final NotesDraft notesDraft, final String displayMode) {
		super(notesDraft, displayMode);

		contentsLayout = new XYLayout();
		setLayoutManager(contentsLayout);
		init();
	}

	public void init() {
		removeAll();

		final String mode = getNotesDraft().isEditable() ? UIConstants.DISPLAY_AS_BLOCK : getDisplayMode();
		final int x0 = IFigureConstants.NOTES_OFFSET_X;
		final int y0 = getYOffset() + IFigureConstants.NOTE_LINE_DISTANCE / 4;

		final int width = NotesDraftUtil.getStaffWidth(mode, getNotesDraft());
		final int height = IFigureConstants.NOTE_LINE_DISTANCE / 2;

		int lastPosition = -1;
		for (final Iterator<Note> iter = getNotesDraft().getToneRange().iterator(); iter.hasNext();) {
			final Note currentNote = iter.next();
			final int currentPosition = new NotePosition(currentNote, getNotesDraft().isSharpSignOn()).getPosition();
			if (currentPosition == lastPosition) {
				continue;
			}
			final NotePosition newNotePosition = new NotePosition(currentPosition, Accidental.NONE);
			final NotePositionAreaFigure areaFigure = new NotePositionAreaFigure(newNotePosition, getNotesDraft()
					.isEditable());
			add(areaFigure);
			final Rectangle rectangle = new Rectangle(x0, y0 + getRelativePosition(newNotePosition) * height,
					width + 2, height);
			areaFigure.setBounds(rectangle);
			contentsLayout.setConstraint(areaFigure, rectangle);
			lastPosition = currentPosition;
		}
	}
}
