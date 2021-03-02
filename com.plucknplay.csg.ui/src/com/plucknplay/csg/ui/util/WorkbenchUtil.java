/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.util;

import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.WorkbenchException;

import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.UIConstants;

public final class WorkbenchUtil {

	private WorkbenchUtil() {
	}

	/**
	 * Returns the first view found in the given workbench window with the
	 * specified id.
	 * 
	 * @param window
	 *            the workbench window, must not be null
	 * @param id
	 *            the id of the view extension to use, must not be null
	 * 
	 * @return the view, or null if none is found
	 */
	public static IViewPart findView(final IWorkbenchWindow window, final String id) {
		if (window == null || id == null) {
			throw new IllegalArgumentException();
		}

		final IWorkbenchPage activePage = window.getActivePage();
		final IPerspectiveDescriptor currentPerspective = activePage.getPerspective();

		final IPerspectiveDescriptor[] perspectives = activePage.getOpenPerspectives();
		for (final IPerspectiveDescriptor perspective : perspectives) {
			activePage.setPerspective(perspective);
			final IWorkbenchPage[] pages = window.getPages();
			for (final IWorkbenchPage page : pages) {
				final IViewPart viewPart = page.findView(id);
				if (viewPart != null) {
					activePage.setPerspective(currentPerspective);
					return viewPart;
				}
			}
		}
		activePage.setPerspective(currentPerspective);
		return null;
	}

	/**
	 * Shows the specified perspective to the user. The perspective is specified
	 * by its preference entries of the action binding.
	 * 
	 * @param workbench
	 *            the workbench, must not be null
	 * @param prefName
	 *            the preference id, must not be null, must be one of the
	 *            following ids: Preferences.PREF_PREFERENCES_*
	 * @throws WorkbenchException
	 */
	public static void showPerspective(final IWorkbench workbench, final String prefName) throws WorkbenchException {
		if (workbench == null || prefName == null) {
			throw new IllegalArgumentException();
		}

		final String perspectiveId = Activator.getDefault().getPreferenceStore().getString(prefName);
		final IWorkbenchPage activePage = workbench.getActiveWorkbenchWindow().getActivePage();
		if (perspectiveId.equals(UIConstants.NO_PERSPECTIVES_BINDING)
				|| perspectiveId.equals(activePage.getPerspective().getId())) {
			return;
		}

		for (final IPerspectiveDescriptor descriptor : activePage.getOpenPerspectives()) {
			if (descriptor.getId().equals(perspectiveId)) {
				activePage.setPerspective(descriptor);
				return;
			}
		}
		workbench.showPerspective(perspectiveId, workbench.getActiveWorkbenchWindow());
	}
}
