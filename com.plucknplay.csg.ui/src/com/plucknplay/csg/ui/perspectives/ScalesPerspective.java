/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.perspectives;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import com.plucknplay.csg.ui.views.BoxView;
import com.plucknplay.csg.ui.views.FretboardView;
import com.plucknplay.csg.ui.views.InstrumentsView;
import com.plucknplay.csg.ui.views.KeyboardView;
import com.plucknplay.csg.ui.views.NotesView;
import com.plucknplay.csg.ui.views.ScaleFinderView;
import com.plucknplay.csg.ui.views.ScaleResultsView;
import com.plucknplay.csg.ui.views.ScalesView;
import com.plucknplay.csg.ui.views.TabView;

public class ScalesPerspective implements IPerspectiveFactory {

	public static final String ID = "com.plucknplay.csg.ui.perspectives.scalesPerspective"; //$NON-NLS-1$

	private static final String SCALE_FINDER_FOLDER_ID = "scaleFinder"; //$NON-NLS-1$
	private static final String KEYBOARD_FOLDER_ID = "keyboard"; //$NON-NLS-1$
	private static final String BOX_FOLDER_ID = "box"; //$NON-NLS-1$

	@Override
	public void createInitialLayout(final IPageLayout layout) {

		// add scale finder view + scales view
		final IFolderLayout scaleFinderFolder = layout.createFolder(SCALE_FINDER_FOLDER_ID, IPageLayout.BOTTOM, 0.31f,
				layout.getEditorArea());
		scaleFinderFolder.addView(ScaleFinderView.ID);
		scaleFinderFolder.addView(ScalesView.ID);

		// add fretboard view
		layout.addView(ScaleResultsView.ID, IPageLayout.BOTTOM, 0.69f, SCALE_FINDER_FOLDER_ID);

		// add scale results view
		layout.addView(FretboardView.ID, IPageLayout.RIGHT, 0.3f, SCALE_FINDER_FOLDER_ID);

		// add keyboard view + notes view
		final IFolderLayout folder2 = layout.createFolder(KEYBOARD_FOLDER_ID, IPageLayout.BOTTOM, 0.5f,
				FretboardView.ID);
		folder2.addView(KeyboardView.ID);
		folder2.addView(NotesView.ID);

		// add box view + tab view
		final IFolderLayout folder3 = layout.createFolder(BOX_FOLDER_ID, IPageLayout.RIGHT, 0.75f, ScaleResultsView.ID);
		folder3.addView(BoxView.ID);
		folder3.addView(TabView.ID);

		// add view shortcuts
		layout.addShowViewShortcut(InstrumentsView.ID);
		layout.addShowViewShortcut(ScalesView.ID);
		layout.addShowViewShortcut(FretboardView.ID);
		layout.addShowViewShortcut(KeyboardView.ID);
		layout.addShowViewShortcut(ScaleResultsView.ID);
		layout.addShowViewShortcut(TabView.ID);
		layout.addShowViewShortcut(BoxView.ID);
		layout.addShowViewShortcut(NotesView.ID);
		layout.addShowViewShortcut(ScaleFinderView.ID);

		// add perspective shortcuts
		layout.addPerspectiveShortcut(SetupPerspective.ID);
		layout.addPerspectiveShortcut(ChordsPerspective.ID);

		// add fast views
		layout.addFastView(InstrumentsView.ID);

		// define editor area
		layout.setEditorAreaVisible(false);
	}
}
