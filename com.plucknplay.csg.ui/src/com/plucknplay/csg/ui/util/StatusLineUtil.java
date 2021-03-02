/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.util;

import org.eclipse.jface.action.IContributionManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.action.SubStatusLineManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IViewSite;

import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.IImageKeys;

/**
 * Utility class which offers some useful static methods for the status line.
 */
public final class StatusLineUtil {

	private static final int MARGIN_LEFT = 5;

	private StatusLineUtil() {
	}

	public static void handleTestVersionWarning(final IViewSite viewSite, final String warningText) {
		final IStatusLineManager mgr = viewSite.getActionBars().getStatusLineManager();
		if (LoginUtil.isActivated()) {
			mgr.setMessage(null, null);
			StatusLineUtil.clearStatusLine(viewSite);
		} else {
			mgr.setMessage(Activator.getDefault().getImage(IImageKeys.WARNING), warningText);
			StatusLineUtil.prepareStatusLineForWarning(mgr);
		}
	}

	// --- clear status line --- //

	public static void clearStatusLine(final IViewSite site) {
		clearStatusLine(site.getActionBars().getStatusLineManager());
	}

	public static void clearStatusLine(final IEditorSite site) {
		clearStatusLine(site.getActionBars().getStatusLineManager());
	}

	public static void clearStatusLine(final IStatusLineManager statusLineManager) {
		final CLabel label = getStatusLineCLabel(statusLineManager);
		if (label != null) {
			label.setBackground((Color) null);
			label.setLeftMargin(0);
		}
	}

	// --- prepare status line for warning message --- //

	public static void prepareStatusLineForWarning(final IViewSite site) {
		prepareStatusLineForWarning(site.getActionBars().getStatusLineManager());
	}

	public static void prepareStatusLineForWarning(final IEditorSite site) {
		prepareStatusLineForWarning(site.getActionBars().getStatusLineManager());
	}

	public static void prepareStatusLineForWarning(final IStatusLineManager statusLineManager) {
		final CLabel label = getStatusLineCLabel(statusLineManager);
		if (label != null) {
			label.setBackground(label.getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
			label.setLeftMargin(MARGIN_LEFT);
		}
	}

	// --- prepare status line for warning message --- //

	public static void prepareStatusLineForError(final IViewSite site) {
		prepareStatusLineForError(site.getActionBars().getStatusLineManager());
	}

	public static void prepareStatusLineForError(final IEditorSite site) {
		prepareStatusLineForError(site.getActionBars().getStatusLineManager());
	}

	public static void prepareStatusLineForError(final IStatusLineManager statusLineManager) {
		final CLabel label = getStatusLineCLabel(statusLineManager);
		if (label != null) {
			label.setBackground(label.getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
			label.setLeftMargin(MARGIN_LEFT);
		}
	}

	// --- determine label --- //

	public static CLabel getStatusLineCLabel(final IViewSite site) {
		return getStatusLineCLabel(site.getActionBars().getStatusLineManager());
	}

	public static CLabel getStatusLineCLabel(final IEditorSite site) {
		return getStatusLineCLabel(site.getActionBars().getStatusLineManager());
	}

	public static CLabel getStatusLineCLabel(final IStatusLineManager statusLineManager) {

		// determine status line manager
		StatusLineManager manager = null;
		if (statusLineManager instanceof StatusLineManager) {
			manager = (StatusLineManager) statusLineManager;
		} else if (statusLineManager instanceof SubStatusLineManager) {
			final IContributionManager parent = ((SubStatusLineManager) statusLineManager).getParent();
			if (parent instanceof StatusLineManager) {
				manager = (StatusLineManager) parent;
			}
		}

		// determine first clabel
		if (manager != null) {
			final Control control = manager.getControl();
			if (control != null && control instanceof Composite) {
				for (final Control element : ((Composite) control).getChildren()) {
					if (!(element instanceof CLabel)) {
						continue;
					}
					return (CLabel) element;
				}
			}
		}
		return null;
	}
}
