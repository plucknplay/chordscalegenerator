/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.perspectives;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import com.plucknplay.csg.ui.views.ChordsView;
import com.plucknplay.csg.ui.views.InstrumentsView;
import com.plucknplay.csg.ui.views.ScalesView;

public class SetupPerspective implements IPerspectiveFactory {

	public static final String ID = "com.plucknplay.csg.ui.perspectives.setupPerspective"; //$NON-NLS-1$

	@Override
	public void createInitialLayout(final IPageLayout layout) {

		final IFolderLayout folder = layout.createFolder("left", IPageLayout.LEFT, 0.3f, layout.getEditorArea()); //$NON-NLS-1$
		folder.addView(InstrumentsView.ID);
		folder.addView(ChordsView.ID);
		folder.addView(ScalesView.ID);

		layout.addShowViewShortcut(InstrumentsView.ID);
		layout.addShowViewShortcut(ChordsView.ID);
		layout.addShowViewShortcut(ScalesView.ID);

		layout.addPerspectiveShortcut(ChordsPerspective.ID);
		layout.addPerspectiveShortcut(ScalesPerspective.ID);
	}
}
