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

public class ExpiredKeyDialog extends Dialog {

	private final String key;

	public ExpiredKeyDialog(final Shell shell, final String key) {
		super(shell);
		this.key = key;
	}

	@Override
	protected Control createDialogArea(final Composite parent) {

		// composite
		final Composite composite = (Composite) super.createDialogArea(parent);
		GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).extendedMargins(10, 15, 10, 10).spacing(5, 5)
				.applyTo(composite);

		// info image
		final Image warningImage = getShell().getDisplay().getSystemImage(SWT.ICON_INFORMATION);
		final Label imageLabel = new Label(composite, SWT.NONE);
		imageLabel.setImage(warningImage);
		GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.BEGINNING).applyTo(imageLabel);

		// init main composite
		final Composite main = new Composite(composite, SWT.NONE);
		GridLayoutFactory.fillDefaults().applyTo(main);
		GridDataFactory.fillDefaults().indent(8, 0).applyTo(main);

		// explanatory labels
		final Label label1 = new Label(main, SWT.NONE);
		label1.setText(NlsUtil.getActivation_expired_msg_1());

		final Label label2 = new Label(main, SWT.NONE);
		label2.setText(NlsUtil.getActivation_expired_msg_2());

		// link to reactivation form on website
		final String href = NlsUtil.getActivation_contact_url();
		final Link link = new Link(main, SWT.NONE);
		link.setText("<a>" + href + "</a>"); //$NON-NLS-1$ //$NON-NLS-2$
		link.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(final Event event) {
				final IWorkbenchBrowserSupport support = PlatformUI.getWorkbench().getBrowserSupport();
				try {
					final String url = href + "?key=" + key; //$NON-NLS-1$
					final IWebBrowser browser = support.getExternalBrowser();
					browser.openURL(new URL(url));

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
		newShell.setText(NlsUtil.getActivation_expired_title());
	}
}
