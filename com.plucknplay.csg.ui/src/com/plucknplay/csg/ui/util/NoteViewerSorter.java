/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.util;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

import com.plucknplay.csg.core.model.Note;

public class NoteViewerSorter extends ViewerSorter {

	@Override
	public int compare(final Viewer viewer, final Object e1, final Object e2) {
		if (e1 != null && e2 != null && e1 instanceof Note && e2 instanceof Note) {
			return ((Note) e1).compareTo((Note) e2);
		}
		return super.compare(viewer, e1, e2);
	}
}
