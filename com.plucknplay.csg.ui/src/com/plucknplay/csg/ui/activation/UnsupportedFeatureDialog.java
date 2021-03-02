/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.activation;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;

public class UnsupportedFeatureDialog extends Dialog {

	public UnsupportedFeatureDialog(final Shell shell) {
		super(shell);
	}

	@Override
	protected Control createDialogArea(final Composite parent) {

		// composite
		final Composite composite = (Composite) super.createDialogArea(parent);
		GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).extendedMargins(10, 15, 10, 10).spacing(5, 5)
				.applyTo(composite);

		// info image
		final Image infoImage = getShell().getDisplay().getSystemImage(SWT.ICON_INFORMATION);
		final Label imageLabel = new Label(composite, SWT.NONE);
		imageLabel.setImage(infoImage);
		GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.BEGINNING).applyTo(imageLabel);

		// init main composite
		final Composite main = new Composite(composite, SWT.NONE);
		GridLayoutFactory.fillDefaults().applyTo(main);
		GridDataFactory.fillDefaults().indent(8, 0).applyTo(main);

		// explanatory label
		final Label label = new Label(main, SWT.NONE);
		label.setText(NlsUtil.getActivation_unsupported_feature_msg());

		// link to contact form on website
		final String href = NlsUtil.getActivation_buy_url();
		final Link link = new Link(main, SWT.NONE);
		link.setText(NlsUtil.getActivation_buy_msg() + " <a>" + href + "</a>"); //$NON-NLS-1$ //$NON-NLS-2$
		link.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(final Event event) {
				final IWorkbenchBrowserSupport support = PlatformUI.getWorkbench().getBrowserSupport();
				try {
					final IWebBrowser browser = support.getExternalBrowser();
					browser.openURL(new URL(href));
				} catch (final MalformedURLException e) {
				} catch (final PartInitException e) {
				}
			}
		});

		return composite;
	}

	@Override
	protected void createButtonsForButtonBar(final Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
	}

	@Override
	protected void configureShell(final Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(NlsUtil.getActivation_unsupported_feature_title());
	}
}
