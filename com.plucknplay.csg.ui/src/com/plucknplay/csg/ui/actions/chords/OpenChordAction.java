/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions.chords;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.WorkbenchException;

import com.plucknplay.csg.core.model.Chord;
import com.plucknplay.csg.core.model.IntervalContainer;
import com.plucknplay.csg.core.model.sets.Category;
import com.plucknplay.csg.core.model.sets.ChordList;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.IImageKeys;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.actions.ActionMessages;
import com.plucknplay.csg.ui.dialogs.IntervalContainerSelectionDialog;
import com.plucknplay.csg.ui.editors.IntervalContainerEditor;
import com.plucknplay.csg.ui.editors.input.ChordEditorInput;
import com.plucknplay.csg.ui.util.WorkbenchUtil;

public class OpenChordAction extends Action {

	private static final String COMMAND_ID = "com.plucknplay.csg.ui.openChord"; //$NON-NLS-1$

	private final IWorkbenchWindow window;

	public OpenChordAction(final IWorkbenchWindow window) {
		this.window = window;
		setId(COMMAND_ID);
		setActionDefinitionId(COMMAND_ID);
		setText(ActionMessages.OpenChordAction_text);
		setToolTipText(ActionMessages.OpenChordAction_tooltip);
		setImageDescriptor(Activator.getImageDescriptor(IImageKeys.OPEN_CHORD));
	}

	@Override
	public void run() {

		final IntervalContainerSelectionDialog dialog = new IntervalContainerSelectionDialog(window.getShell(),
				IntervalContainer.TYPE_CHORD, ChordList.getInstance().getRootCategory().getAllElements());
		dialog.setImage(Activator.getDefault().getImage(IImageKeys.CHORD));

		if (dialog.open() == Dialog.OK) {
			final Chord chord = (Chord) dialog.getResult()[0];
			try {
				final Category category = ChordList.getInstance().getRootCategory().getCategory(chord);
				WorkbenchUtil.showPerspective(window.getWorkbench(),
						Preferences.PERSPECTIVES_BINDING_ELEMENT_EDITING);
				window.getActivePage().openEditor(new ChordEditorInput(chord, category, false),
						IntervalContainerEditor.ID);
			} catch (final WorkbenchException e) {
			}
		}
	}
}
