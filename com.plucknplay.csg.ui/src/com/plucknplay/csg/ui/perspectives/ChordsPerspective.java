/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.perspectives;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import com.plucknplay.csg.ui.views.BoxView;
import com.plucknplay.csg.ui.views.ChordGenerationView;
import com.plucknplay.csg.ui.views.ChordResultsView;
import com.plucknplay.csg.ui.views.ChordsView;
import com.plucknplay.csg.ui.views.FretboardView;
import com.plucknplay.csg.ui.views.InstrumentsView;
import com.plucknplay.csg.ui.views.KeyboardView;
import com.plucknplay.csg.ui.views.NotesView;
import com.plucknplay.csg.ui.views.TabView;

public class ChordsPerspective implements IPerspectiveFactory {

	public static final String ID = "com.plucknplay.csg.ui.perspectives.chordsPerspective"; //$NON-NLS-1$

	private static final String CHORD_GENERATION_FOLDER_ID = "chordGeneration"; //$NON-NLS-1$
	private static final String FRETBOARD_FOLDER_ID = "fretboard"; //$NON-NLS-1$
	private static final String BOX_FOLDER_ID = "box"; //$NON-NLS-1$

	@Override
	public void createInitialLayout(final IPageLayout layout) {

		// add chord generation view + chords view
		final IFolderLayout chordGenerationFolder = layout.createFolder(CHORD_GENERATION_FOLDER_ID, IPageLayout.BOTTOM,
				0.31f, layout.getEditorArea());
		chordGenerationFolder.addView(ChordGenerationView.ID);
		chordGenerationFolder.addView(ChordsView.ID);

		// add chord results view
		layout.addView(ChordResultsView.ID, IPageLayout.BOTTOM, 0.69f, CHORD_GENERATION_FOLDER_ID);

		// add fretboard view + keyboard view
		final IFolderLayout fretboardFolder = layout.createFolder(FRETBOARD_FOLDER_ID, IPageLayout.RIGHT, 0.3f,
				ChordGenerationView.ID);
		fretboardFolder.addView(FretboardView.ID);
		fretboardFolder.addView(KeyboardView.ID);

		// add box view + tab view
		final IFolderLayout boxFolder = layout.createFolder(BOX_FOLDER_ID, IPageLayout.BOTTOM, 0.5f, FretboardView.ID);
		boxFolder.addView(BoxView.ID);
		boxFolder.addView(TabView.ID);

		// add notes view
		layout.addView(NotesView.ID, IPageLayout.RIGHT, 0.5f, BoxView.ID);

		// add view shortcuts
		layout.addShowViewShortcut(InstrumentsView.ID);
		layout.addShowViewShortcut(ChordsView.ID);
		layout.addShowViewShortcut(ChordGenerationView.ID);
		layout.addShowViewShortcut(ChordResultsView.ID);
		layout.addShowViewShortcut(FretboardView.ID);
		layout.addShowViewShortcut(KeyboardView.ID);
		layout.addShowViewShortcut(TabView.ID);
		layout.addShowViewShortcut(BoxView.ID);
		layout.addShowViewShortcut(NotesView.ID);

		// add perspective shortcuts
		layout.addPerspectiveShortcut(SetupPerspective.ID);
		layout.addPerspectiveShortcut(ScalesPerspective.ID);

		// add fast views
		layout.addFastView(InstrumentsView.ID);

		// define editor area
		layout.setEditorAreaVisible(false);
	}
}
