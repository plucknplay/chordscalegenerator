/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.osgi.framework.Bundle;

import com.plucknplay.csg.ui.Activator;

public class ShowLocationsAction extends Action implements IWorkbenchWindowActionDelegate {

	public static final String COMMAND_ID = "com.plucknplay.csg.util.showLocations";

	private IWorkbenchWindow window;

	@Override
	public void init(final IWorkbenchWindow window) {
		this.window = window;
		setActionDefinitionId(COMMAND_ID);
	}

	@Override
	public void run(final IAction action) {

		final StringBuffer buf = new StringBuffer();

		buf.append("IPath:\n\n");

		buf.append("Location: ");
		buf.append(Platform.getLocation());
		buf.append("\n");

		buf.append("LogFileLocation: ");
		buf.append(Platform.getLogFileLocation());
		buf.append("\n");

		buf.append("StateLocation: ");
		buf.append(Platform.getStateLocation(Activator.getDefault().getBundle()));
		buf.append("\n\n");

		buf.append("Location.URL:\n\n");

		buf.append("UserLocation: ");
		buf.append(Platform.getUserLocation().getURL());
		buf.append("\n");

		buf.append("ConfigurationLocation: ");
		buf.append(Platform.getConfigurationLocation().getURL());
		buf.append("\n");

		buf.append("InstallLocation: ");
		buf.append(Platform.getInstallLocation().getURL());
		buf.append("\n");

		buf.append("InstanceLocation: ");
		buf.append(Platform.getInstanceLocation().getURL());
		buf.append("\n\n");

		buf.append("String:\n\n");

		buf.append("Bundle.Location: ");
		buf.append(Activator.getDefault().getBundle().getLocation());
		buf.append("\n\n");

		final URL entry = Activator.getDefault().getBundle().getEntry("lists/en/chords.xml");
		buf.append("URL: ");
		buf.append(entry);
		buf.append("\n\n");

		final StringBuffer text = new StringBuffer();
		try {
			final InputStream inputStream = FileLocator.openStream(Activator.getDefault().getBundle(), new Path(
					"lists/en/chords.xml"), false);
			final Reader reader = new InputStreamReader(inputStream);
			int counter = 0;
			int data = reader.read();
			while (data != -1 && counter < 300) {
				text.append((char) data);
				data = reader.read();
				counter++;
			}
			reader.close();
		} catch (final IOException e) {
			e.printStackTrace();
		}

		buf.append("File-Content: ");
		buf.append(text.toString());
		buf.append("\n\n");

		final Bundle bundle = com.plucknplay.csg.data.Activator.getDefault().getBundle();
		buf.append("Data-Bundle: ");
		buf.append(bundle);
		buf.append("\n\n");

		if (bundle != null) {
			buf.append("URL: ");
			final URL url = FileLocator.find(bundle, new Path("data/en/"), null);
			buf.append(url);
			buf.append("\n\n");

			buf.append("File-URL: ");
			try {

				final URL fileURL = FileLocator.toFileURL(url);

				buf.append(fileURL);
				buf.append("\n\n");

				buf.append("File-URL Path: ");
				buf.append(fileURL.getPath());
				buf.append("\n\n");

				buf.append("File-URL File: ");
				buf.append(fileURL.getFile());
				buf.append("\n\n");

			} catch (final IOException e) {
			}
		}

		MessageDialog.openInformation(window.getShell(), "Show Locations", buf.toString());
	}

	@Override
	public void selectionChanged(final IAction action, final ISelection selection) {
		// do nothing
	}

	@Override
	public void dispose() {
		// do nothing
	}
}
