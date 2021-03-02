/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions.chords;

import org.eclipse.ui.IViewPart;

import com.plucknplay.csg.core.model.Chord;
import com.plucknplay.csg.core.model.sets.Category;
import com.plucknplay.csg.core.model.sets.CategoryList;
import com.plucknplay.csg.core.model.sets.ChordList;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.actions.general.AbstractAddElementAction;
import com.plucknplay.csg.ui.activation.NlsUtil;
import com.plucknplay.csg.ui.editors.IntervalContainerEditor;
import com.plucknplay.csg.ui.editors.input.CategorizableEditorInput;
import com.plucknplay.csg.ui.editors.input.ChordEditorInput;
import com.plucknplay.csg.ui.util.LoginUtil;

public class AddChordAction extends AbstractAddElementAction {

	private static final String ACTION_ID = "com.plucknplay.csg.ui.actions.addChordAction"; //$NON-NLS-1$
	private static final String COMMAND_ID = "com.plucknplay.csg.ui.newChord"; //$NON-NLS-1$

	public AddChordAction(final IViewPart view, final boolean popup) {
		super(view);

		setId(ACTION_ID);
		setActionDefinitionId(COMMAND_ID);
		setText(popup ? NlsUtil.getAction_new_chord() : NlsUtil.getAction_add_chord());
		setToolTipText(popup ? NlsUtil.getAction_new_chord() : NlsUtil.getAction_add_chord());
		setImageDescriptor(Activator.getImageDescriptor(NlsUtil.getAction_image_new_chord()));
	}

	@Override
	protected String getEditorID() {
		return IntervalContainerEditor.ID;
	}

	@Override
	protected CategorizableEditorInput getEditorInput(final Category category) {
		return new ChordEditorInput(new Chord(), category, true);
	}

	@Override
	public CategoryList getCategoryList() {
		return ChordList.getInstance();
	}

	@Override
	public void run() {
		if (LoginUtil.isActivated()) {
			super.run();
		} else {
			LoginUtil.showUnsupportedFeatureInformation(getViewPart().getSite().getShell());
		}
	}
}
