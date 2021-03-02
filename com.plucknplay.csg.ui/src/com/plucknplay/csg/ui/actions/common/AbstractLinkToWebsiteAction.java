/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions.common;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;

public abstract class AbstractLinkToWebsiteAction extends Action {

	private final IWorkbenchWindow window;
	private final String href;

	public AbstractLinkToWebsiteAction(final IWorkbenchWindow window, final String href) {
		this.window = window;
		this.href = href;
	}

	@Override
	public void run() {
		final IWorkbenchBrowserSupport support = window.getWorkbench().getBrowserSupport();
		try {
			final IWebBrowser browser = support.getExternalBrowser();
			browser.openURL(new URL(href));
		} catch (final MalformedURLException e) {
		} catch (final PartInitException e) {
		}
	}
}
