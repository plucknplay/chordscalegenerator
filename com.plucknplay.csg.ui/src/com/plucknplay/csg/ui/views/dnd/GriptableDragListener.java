/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.views.dnd;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;

import com.plucknplay.csg.core.model.Chord;
import com.plucknplay.csg.core.model.Griptable;
import com.plucknplay.csg.core.model.sets.ChordList;

public class GriptableDragListener implements DragSourceListener {

	private final TableViewer viewer;

	public GriptableDragListener(final TableViewer viewer) {
		this.viewer = viewer;
	}

	@Override
	public void dragStart(final DragSourceEvent event) {
		event.doit = !viewer.getSelection().isEmpty();
	}

	@Override
	public void dragSetData(final DragSourceEvent event) {
		final IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
		final Object first = selection.getFirstElement();
		final List<Chord> theSelection = new ArrayList<Chord>();
		if (first instanceof Griptable) {
			final Chord chord = ((Griptable) first).getChord();
			final Chord theRealChord = (Chord) ChordList.getInstance().getElement(chord.getName());
			theRealChord.setData(ModelObjectTransfer.DATA_KEY_FQ_NAME, null);
			theSelection.add(chord);
		}
		if (GriptableTransfer.getInstance().isSupportedType(event.dataType)) {
			event.data = theSelection;
		}
	}

	@Override
	public void dragFinished(final DragSourceEvent event) {
		// do nothing
	}
}
