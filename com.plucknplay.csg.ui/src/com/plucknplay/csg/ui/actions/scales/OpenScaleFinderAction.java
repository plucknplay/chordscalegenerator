/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions.scales;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.WorkbenchException;

import com.plucknplay.csg.core.model.Chord;
import com.plucknplay.csg.core.model.Griptable;
import com.plucknplay.csg.core.model.Note;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.IImageKeys;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.actions.ActionMessages;
import com.plucknplay.csg.ui.actions.general.IViewSelectionAction;
import com.plucknplay.csg.ui.util.WorkbenchUtil;
import com.plucknplay.csg.ui.views.ScaleFinderView;

public class OpenScaleFinderAction extends Action implements IViewSelectionAction {

	private static final String COMMAND_ID = "com.plucknplay.csg.ui.openScaleFinder"; //$NON-NLS-1$

	private final IWorkbenchSite site;

	private List<Chord> selectedChords;
	private Griptable selectedGriptable;

	public OpenScaleFinderAction(final IWorkbenchSite site) {
		this.site = site;
		setActionDefinitionId(COMMAND_ID);
		setText(ActionMessages.OpenScaleFinderAction_text);
		setToolTipText(ActionMessages.OpenScaleFinderAction_text);
		setImageDescriptor(Activator.getImageDescriptor(IImageKeys.FIND_SCALES));
		setDisabledImageDescriptor(Activator.getImageDescriptor(IImageKeys.FIND_SCALES_DISABLED));
		setEnabled(false);
	}

	@Override
	public void run() {

		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				try {

					final List<Chord> chords = new ArrayList<Chord>();
					final Set<Note> notes = new HashSet<Note>();

					if (selectedGriptable != null) {
						chords.add(new Chord(selectedGriptable.getChord()));
						notes.addAll(selectedGriptable.getNotes(true));
					}

					else if (selectedChords != null && !selectedChords.isEmpty()) {
						for (final Chord chord : selectedChords) {
							final Chord newChord = new Chord(chord);
							newChord.setRootNote(chord.getRootNote());
							chords.add(newChord);
							notes.addAll(newChord.getNotes());
						}
					}

					// Open Scale Finder View
					if (!chords.isEmpty() && !notes.isEmpty()) {
						final IWorkbenchWindow window = site.getWorkbenchWindow();
						WorkbenchUtil.showPerspective(window.getWorkbench(),
								Preferences.PERSPECTIVES_BINDING_FIND_SCALES);
						final ScaleFinderView view = (ScaleFinderView) window.getActivePage().showView(
								ScaleFinderView.ID);
						view.setInput(chords, notes);
					}

				} catch (final PartInitException e) {
				} catch (final WorkbenchException e) {
				}
			}
		});
	}

	@Override
	public void selectionChanged(final ISelection selection) {

		selectedGriptable = null;
		selectedChords = new ArrayList<Chord>();

		boolean enabled = false;
		if (selection != null && selection instanceof IStructuredSelection) {
			final IStructuredSelection structered = (IStructuredSelection) selection;
			for (final Object obj : structered.toArray()) {
				if (obj instanceof Griptable) {
					selectedGriptable = (Griptable) obj;
					enabled = true;
					break;
				} else if (obj instanceof Chord) {
					selectedChords.add((Chord) obj);
					enabled = true;
				}
			}
		}
		setEnabled(enabled);
	}
}
