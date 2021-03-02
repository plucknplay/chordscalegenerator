/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.plucknplay.csg.core.model.Interval;
import com.plucknplay.csg.core.model.IntervalContainer;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.IImageKeys;

public class IntervalTableViewerLabelProvider extends LabelProvider implements ITableLabelProvider {

	private boolean addNoteInfo;
	private IntervalContainer intervalContainer;
	private Collection<IntervalContainer> intervalContainers;

	public IntervalTableViewerLabelProvider() {
		addNoteInfo = false;
	}

	public IntervalTableViewerLabelProvider(final IntervalContainer intervalContainer, final boolean addNoteInfo) {
		this();
		this.addNoteInfo = addNoteInfo;
		this.intervalContainer = intervalContainer;
	}

	public IntervalTableViewerLabelProvider(final Collection<IntervalContainer> intervalContainers) {
		this();
		this.intervalContainers = intervalContainers;
	}

	@Override
	public String getColumnText(final Object obj, final int index) {
		return getText(obj);
	}

	@Override
	public Image getColumnImage(final Object obj, final int index) {
		return getImage(obj);
	}

	@Override
	public Image getImage(final Object obj) {
		if (obj instanceof Interval) {
			return Activator.getDefault().getImage(IImageKeys.INTERVAL);
		}
		return super.getImage(obj);
	}

	@Override
	public String getText(final Object obj) {
		if (obj instanceof Interval) {
			final Interval interval = (Interval) obj;
			if (intervalContainers == null) {
				if (intervalContainer == null) {
					return interval.getDefaultName();
				} else {
					final StringBuffer buf = new StringBuffer();
					buf.append(intervalContainer.getIntervalName(interval));
					final int length = buf.length();
					if (addNoteInfo) {
						buf.append(" "); //$NON-NLS-1$
						for (int i = 4; i >= length; i--) {
							buf.append("."); //$NON-NLS-1$
						}
						buf.append(" "); //$NON-NLS-1$
						buf.append(intervalContainer.getRootNote().calcNote(interval).getRelativeName());
					}
					return buf.toString();
				}
			} else {
				final List<String> diffentNames = new ArrayList<String>();
				for (final IntervalContainer intervalContainer2 : intervalContainers) {
					final String name = intervalContainer2.getIntervalName(interval);
					if (!diffentNames.contains(name)) {
						diffentNames.add(name);
					}
				}
				Collections.sort(diffentNames, new Comparator<String>() {
					@Override
					public int compare(final String name1, final String name2) {
						return new Integer(name1.replace("b", "").replace("#", "")).compareTo(new Integer(name2
								.replace("b", "").replace("#", "")));
					}
				});
				final StringBuffer buf = new StringBuffer();
				for (final Iterator<String> iter = diffentNames.iterator(); iter.hasNext();) {
					final String name = iter.next();
					buf.append(name);
					if (iter.hasNext()) {
						buf.append(" / "); //$NON-NLS-1$
					}
				}
				return buf.toString();
			}
		}
		return super.getText(obj);
	}
}
