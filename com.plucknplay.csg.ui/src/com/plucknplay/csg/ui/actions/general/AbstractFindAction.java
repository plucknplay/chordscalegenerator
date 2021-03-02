/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions.general;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchSite;

public abstract class AbstractFindAction extends Action {

	public static final int MIN_NUMBER_OF_NOTES = 2;

	private final IWorkbenchSite site;

	public AbstractFindAction(final IWorkbenchSite site) {
		this.site = site;
	}

	protected IWorkbenchSite getSite() {
		return site;
	}

	protected Shell getShell() {
		return site.getShell();
	}
}
