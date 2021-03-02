/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions.chords;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.WorkbenchException;

import com.plucknplay.csg.core.model.Chord;
import com.plucknplay.csg.core.model.sets.ChordList;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.activation.NlsUtil;
import com.plucknplay.csg.ui.editors.IntervalContainerEditor;
import com.plucknplay.csg.ui.editors.input.ChordEditorInput;
import com.plucknplay.csg.ui.util.LoginUtil;
import com.plucknplay.csg.ui.util.WorkbenchUtil;

public class GlobalAddChordAction extends Action {

	private static final String ID = "com.plucknplay.csg.ui.actions.global.add.chord"; //$NON-NLS-1$
	private static final String COMMAND_ID = "com.plucknplay.csg.ui.newChord"; //$NON-NLS-1$

	private final IWorkbenchWindow window;

	public GlobalAddChordAction(final IWorkbenchWindow window) {
		this.window = window;
		setId(ID);
		setActionDefinitionId(COMMAND_ID);
		setText(NlsUtil.getAction_global_add_chord());
		setImageDescriptor(Activator.getImageDescriptor(NlsUtil.getAction_image_new_chord()));
	}

	@Override
	public void run() {
		if (LoginUtil.isActivated()) {
			try {
				WorkbenchUtil.showPerspective(window.getWorkbench(), Preferences.PERSPECTIVES_BINDING_ELEMENT_EDITING);
				window.getActivePage().openEditor(
						new ChordEditorInput(new Chord(), ChordList.getInstance().getRootCategory(), true),
						IntervalContainerEditor.ID);
			} catch (final PartInitException e) {
			} catch (final WorkbenchException e) {
			}
		} else {
			LoginUtil.showUnsupportedFeatureInformation(window.getShell());
		}
	}
}
