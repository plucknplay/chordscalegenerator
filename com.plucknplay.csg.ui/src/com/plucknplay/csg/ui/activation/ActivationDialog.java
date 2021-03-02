/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.activation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Random;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;

import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.util.LoginUtil;

public class ActivationDialog extends Dialog {

	public static final Object MODE_START_UP = new Object();
	public static final Object MODE_NORMAL = new Object();

	private static final String HYPHEN = "-";

	private final Object mode;

	private Text text1;
	private Text text2;
	private Text text3;
	private Text text4;
	private MyComposite errorComposite;
	private CLabel errorLabel;
	private Button okButton;
	private final Cursor loadCursor;
	private final Image errorImage;

	private String key;
	private int result;

	/**
	 * The constructor.
	 * 
	 * @param shell
	 *            the shell
	 * @param mode
	 *            must not be null, must be one of the values of MODE_*
	 */
	public ActivationDialog(final Shell shell, final Object mode) {
		super(shell);
		this.mode = mode;
		loadCursor = new Cursor(shell.getDisplay(), SWT.CURSOR_WAIT);
		errorImage = Activator.getDefault().getImage(NlsUtil.getActivation_error_image());
	}

	@Override
	protected Control createDialogArea(final Composite parent) {

		// composite
		final Composite composite = (Composite) super.createDialogArea(parent);
		GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).extendedMargins(10, 15, 10, 10).spacing(5, 5)
				.applyTo(composite);

		// csg logo
		final Label logo = new Label(composite, SWT.NONE);
		logo.setImage(Activator.getDefault().getImage(NlsUtil.getActivation_dialog_image()));
		GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.BEGINNING).applyTo(logo);

		// init main composite
		final Composite main = new Composite(composite, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(8).equalWidth(false).applyTo(main);
		GridDataFactory.fillDefaults().indent(8, 0).applyTo(main);

		// explanatory label
		final Label explanationLabel = new Label(main, SWT.NONE);
		explanationLabel.setText(NlsUtil.getActivation_activate_msg());
		GridDataFactory.fillDefaults().span(8, 1).applyTo(explanationLabel);

		// text fields
		final int textLimit = 4;
		text1 = new Text(main, SWT.SINGLE | SWT.BORDER);
		text1.setTextLimit(textLimit);
		new Label(main, SWT.NONE).setText(HYPHEN);
		text2 = new Text(main, SWT.SINGLE | SWT.BORDER);
		text2.setTextLimit(textLimit);
		new Label(main, SWT.NONE).setText(HYPHEN);
		text3 = new Text(main, SWT.SINGLE | SWT.BORDER);
		text3.setTextLimit(textLimit);
		new Label(main, SWT.NONE).setText(HYPHEN);
		text4 = new Text(main, SWT.SINGLE | SWT.BORDER);
		text4.setTextLimit(textLimit);

		final Label dummyLabel = new Label(main, SWT.NONE);
		dummyLabel.setText(""); //$NON-NLS-1$
		GridDataFactory.fillDefaults().grab(true, false);

		final GC gc = new GC(text1);
		final int width = 8 * gc.getFontMetrics().getAverageCharWidth();
		gc.dispose();

		GridDataFactory.fillDefaults().hint(width, SWT.DEFAULT).applyTo(text1);
		GridDataFactory.fillDefaults().hint(width, SWT.DEFAULT).applyTo(text2);
		GridDataFactory.fillDefaults().hint(width, SWT.DEFAULT).applyTo(text3);
		GridDataFactory.fillDefaults().hint(width, SWT.DEFAULT).applyTo(text4);

		text1.addModifyListener(new TextModifyListener(text2));
		text2.addModifyListener(new TextModifyListener(text3));
		text3.addModifyListener(new TextModifyListener(text4));
		text4.addModifyListener(new TextModifyListener(null));

		text1.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(final KeyEvent e) {
				if (e.character == '-') {
					e.doit = false;
				}
			}
		});
		text1.addVerifyListener(new VerifyListener() {
			@Override
			public void verifyText(final VerifyEvent e) {
				final String text = e.text;
				if (text != null && text.contains(HYPHEN)) {

					final int sep1 = text.indexOf(HYPHEN);
					final int sep2 = sep1 > -1 && sep1 < text.length() - 1 ? text.indexOf(HYPHEN, sep1 + 1) : -1;
					final int sep3 = sep2 > -1 && sep2 < text.length() - 1 ? text.indexOf(HYPHEN, sep2 + 1) : -1;

					if (sep1 > -1) {
						text1.setText(text.substring(0, sep1));
						if (sep2 > -1) {
							text2.setText(text.substring(sep1 + 1, sep2));
							if (sep3 > -1) {
								text3.setText(text.substring(sep2 + 1, sep3));
								text4.setText(text.substring(sep3 + 1));
							} else {
								text3.setText(text.substring(sep2 + 1));
							}
						} else {
							text2.setText(text.substring(sep1 + 1));
						}
					}
					text2.clearSelection();
				}
			}
		});

		// note label
		final Composite noteComposite = new Composite(main, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(2).extendedMargins(-textLimit, 0, 0, 0).applyTo(noteComposite);
		GridDataFactory.fillDefaults().span(8, 1).grab(true, false).applyTo(noteComposite);
		new Label(noteComposite, SWT.LEFT).setImage(Activator.getDefault().getImage(
				NlsUtil.getActivation_warning_image()));
		new Label(noteComposite, SWT.LEFT).setText(NlsUtil.getActivation_activate_note());

		// final explanation including link to website
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
		GridDataFactory.fillDefaults().span(8, 1).applyTo(link);

		// prompt check box
		if (mode == MODE_START_UP) {
			final Button hidePromptButton = new Button(composite, SWT.CHECK);
			hidePromptButton.setText(NlsUtil.getActivation_do_not_prompt());
			hidePromptButton.setSelection(false);
			hidePromptButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(final SelectionEvent e) {
					Activator.getDefault().getPreferenceStore()
							.setValue(Preferences.SHOW_LOGIN_PROMPT, !hidePromptButton.getSelection());
				}
			});
			GridDataFactory.fillDefaults().span(2, 1).indent(0, 15).applyTo(hidePromptButton);
		}

		// error composite
		errorComposite = new MyComposite(composite, SWT.NONE);
		GridLayoutFactory.fillDefaults().extendedMargins(0, 0, 10, 0).spacing(0, 0).applyTo(errorComposite);
		GridDataFactory.fillDefaults().span(2, 1).grab(true, false).applyTo(errorComposite);
		final Label separator = new Label(errorComposite, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(separator);
		errorLabel = new CLabel(errorComposite, SWT.NONE);
		errorLabel.setImage(errorImage);
		errorLabel.setForeground(ColorConstants.red);
		errorComposite.setVisible(false);

		return composite;
	}

	@Override
	protected Control createButtonBar(final Composite parent) {
		final Control control = super.createButtonBar(parent);
		okButton = getButton(OK);
		okButton.setEnabled(false);
		final Button cancelButton = getButton(CANCEL);
		if (mode == MODE_START_UP) {
			cancelButton.setText(NlsUtil.getActivation_later());
		}
		return control;
	}

	@Override
	protected void okPressed() {

		result = -1;

		// modify dialog
		setEnablement(false);
		getShell().setCursor(loadCursor);

		// determine data
		final Random r = new Random();
		final StringBuffer checkBuffer = new StringBuffer("" + (1 + r.nextInt(2))); //$NON-NLS-1$
		for (int i = 0; i < 4; i++) {
			checkBuffer.append(r.nextInt(3));
		}
		final String check = checkBuffer.toString();

		key = text1.getText().toUpperCase() + HYPHEN + text2.getText().toUpperCase() + HYPHEN
				+ text3.getText().toUpperCase() + HYPHEN + text4.getText().toUpperCase();
		final String macInfo = LoginUtil.getHardwareAdressesInfo();
		final String osInfo = LoginUtil.getOperatingSystemInfo();
		final String sourceInfo = LoginUtil.getDownloadSourceInfo();
		final String data = "key=" + key + "&mac=" + macInfo + "&os=" + osInfo + "&source=" + sourceInfo + "&check=" + check; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$

		// check key
		try {
			final URL url = new URL(NlsUtil.getActivation_activate_url());
			final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST"); //$NON-NLS-1$

			// define output stream
			connection.setDoOutput(true);
			final PrintStream out = new PrintStream(connection.getOutputStream());
			out.write(data.getBytes());
			out.flush();
			out.close();

			// define input stream
			final BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			final StringBuffer messageBuffer = new StringBuffer();
			while ((line = input.readLine()) != null) {
				messageBuffer.append(line);
			}
			input.close();

			result = decodeResult(messageBuffer.toString().trim(), check);

		} catch (final MalformedURLException e) {
		} catch (final ProtocolException e) {
		} catch (final IOException e) {
		}

		getShell().setCursor(null);

		// handle result
		if (result == 2) {
			setEnablement(true);
			showInvalidKeyMessage();
		} else if (result == -1) {
			setEnablement(true);
			showErrorMessage();
		} else {
			super.okPressed();
		}
	}

	private void setEnablement(final boolean enabled) {
		text1.setEnabled(enabled);
		text2.setEnabled(enabled);
		text3.setEnabled(enabled);
		text4.setEnabled(enabled);
		getButton(OK).setEnabled(enabled);
		getButton(CANCEL).setEnabled(enabled);
	}

	@Override
	protected void configureShell(final Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(NlsUtil.getActivation_activate_title());
	}

	private void showErrorMessage() {
		showErrorMessageHelper(NlsUtil.getActivation_activate_error_msg());
	}

	private void showInvalidKeyMessage() {
		showErrorMessageHelper(NlsUtil.getActivation_activate_invalid_key_msg());
	}

	private void showErrorMessageHelper(final String message) {
		errorLabel.setImage(errorImage);
		errorLabel.setText(message);
		if (!errorComposite.isVisible()) {
			errorComposite.setVisible(true);
			getShell().pack(true);
		}
	}

	@Override
	public boolean close() {
		loadCursor.dispose();
		return super.close();
	}

	private int decodeResult(final String message, final String check) {
		if (message != null && message.length() == 15) {

			// check validity of message
			for (int i = 0; i < 5; i++) {
				final int currentCheckValue = Integer.valueOf(check.substring(i, i + 1));
				final int currentValue = message.charAt(i);
				if (currentValue % 3 != currentCheckValue) {
					return -1;
				}
			}

			// decode result
			int sum = 0;
			for (int i = 5; i < message.length(); i++) {
				sum += message.charAt(i);
			}

			return sum % 3;
		}
		return -1;
	}

	public int getResult() {
		return result;
	}

	public String getKey() {
		return key;
	}

	private class TextModifyListener implements ModifyListener {

		private final Text successor;

		public TextModifyListener(final Text successor) {
			this.successor = successor;
		}

		@Override
		public void modifyText(final ModifyEvent e) {

			if (okButton != null && !okButton.isDisposed() && text1 != null && !text1.isDisposed() && text2 != null
					&& !text2.isDisposed() && text3 != null && !text3.isDisposed() && text4 != null
					&& !text4.isDisposed()) {

				// set ok button enablement
				okButton.setEnabled(text1.getText().length() == 4 && text2.getText().length() == 4
						&& text3.getText().length() == 4 && text4.getText().length() == 4);

				// jump to successor text if fouth letter was inserted
				if (successor != null) {
					final Text text = (Text) e.widget;
					if (text.getText().length() == 4) {
						successor.setFocus();
					}
				}
			}
		}
	}

	private static class MyComposite extends Composite {

		public MyComposite(final Composite parent, final int style) {
			super(parent, style);
		}

		@Override
		public Point computeSize(final int wHint, final int hHint, final boolean changed) {
			if (!isVisible()) {
				return new Point(0, 0);
			} else {
				return super.computeSize(wHint, hHint, changed);
			}
		}
	}
}
