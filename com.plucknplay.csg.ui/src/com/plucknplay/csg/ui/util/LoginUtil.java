/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.equinox.security.storage.StorageException;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.activation.ActivateAction;
import com.plucknplay.csg.ui.activation.NlsUtil;
import com.plucknplay.csg.ui.activation.UnsupportedFeatureDialog;
import com.plucknplay.csg.ui.model.sets.ListManager;

public final class LoginUtil {

	private static final String PROPERTIES_FILE_NAME = ".chordScaleGenerator/config/org.eclipse.equinox.security/secure_storage"; //$NON-NLS-1$

	private static final String HOST = "org.eclipse.equinox.security.preferences.keyLength"; //$NON-NLS-1$
	private static final String USER = "org.eclipse.equinox.security.preferences.salt"; //$NON-NLS-1$

	private static final int MAX_INFO_LENGTH = 20;

	private static ActivateAction activateAction;
	private static IMenuManager menuBar;
	private static boolean checkActivated = true;
	private static boolean isActivated;

	private LoginUtil() {
		isActivated = false;
	}

	public static boolean isActivated() {
		if (checkActivated && !isActivated) {
			final ISecurePreferences securePrefs = getSecurePreferences();
			try {
				if (/*
					 * getHostName().equals(securePrefs.get(HOST, null)) &&
					 */getUserName().equals(securePrefs.get(USER, null))) {
					isActivated = true;
				}
			} catch (final StorageException e) {
			}
			checkActivated = false;
		}
		return isActivated;
	}

	public static void activate() {

		// set flags
		checkActivated = false;
		isActivated = true;

		// store activation related secure preferences
		final ISecurePreferences securePrefs = getSecurePreferences();
		securePrefs.clear();

		try {
			securePrefs.put(HOST, getHostName(), true);
			securePrefs.put(USER, getUserName(), true);
		} catch (final StorageException e) {
		}

		// remove register action
		if (activateAction != null && menuBar != null) {
			menuBar.remove(activateAction.getId());
			menuBar.update(true);
		}

		ListManager.loadChords();
		ListManager.loadScales();

		LoginUtil.showSuccessfulActivationDialog(Display.getDefault().getActiveShell());
	}

	@SuppressWarnings("deprecation")
	private static ISecurePreferences getSecurePreferences() {

		ISecurePreferences preferences = null;
		try {
			final String userHome = System.getProperty("user.home"); //$NON-NLS-1$
			if (userHome != null) {

				// determine location
				final File file = new File(userHome, PROPERTIES_FILE_NAME);
				final URL location = file.toURL();

				// create secure preferences
				preferences = SecurePreferencesFactory.open(location, null);
			}
		} catch (final IOException e) {
		}

		return preferences != null ? preferences : SecurePreferencesFactory.getDefault();
	}

	public static void storeRegisterAction(final ActivateAction action, final IMenuManager menu) {
		activateAction = action;
		menuBar = menu;
	}

	public static void showUnsupportedFeatureInformation(final Shell shell) {
		new UnsupportedFeatureDialog(shell).open();
	}

	public static void showSuccessfulActivationDialog(final Shell shell) {
		MessageDialog.openInformation(shell, NlsUtil.getActivation_successful_title(),
				NlsUtil.getActivation_successful_msg());
	}

	public static String getHardwareAdressesInfo() {
		final StringBuffer buf = new StringBuffer();
		for (final Iterator<String> iter = getHardwareAddresses().iterator(); iter.hasNext();) {
			final String currentAddress = iter.next();
			buf.append(currentAddress);
			if (iter.hasNext()) {
				buf.append(", "); //$NON-NLS-1$
			}
		}
		return buf.toString();
	}

	public static List<String> getHardwareAddresses() {
		final List<String> result = new ArrayList<String>();

		try {
			final Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
			while (e.hasMoreElements()) {
				final NetworkInterface ni = e.nextElement();
				final byte[] hardwareAddress = ni.getHardwareAddress();
				String theHardwareAddress = ""; //$NON-NLS-1$
				if (hardwareAddress != null) {
					for (int i = 0; i < hardwareAddress.length; i++) {
						theHardwareAddress += String.format((i == 0 ? "" : "-") + "%02X", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
								hardwareAddress[i]);
					}
					result.add(theHardwareAddress);
				}
			}
		} catch (final SocketException e) {
		}

		return result;
	}

	private static String getHostName() {
		try {
			return InetAddress.getLocalHost().getCanonicalHostName();
		} catch (final UnknownHostException e) {
			return "UnknownHostName"; //$NON-NLS-1$
		}
	}

	private static String getUserName() {
		try {
			return System.getProperty("user.name"); //$NON-NLS-1$
		} catch (final SecurityException e) {
			return "UnknownUserName"; //$NON-NLS-1$
		}
	}

	public static String getOperatingSystemInfo() {
		String result = System.getProperty("os.name"); //$NON-NLS-1$
		if (result == null || "".equals(result)) { //$NON-NLS-1$
			result = "Unknown"; //$NON-NLS-1$
		}
		while (result.contains(" ")) { //$NON-NLS-1$
			result = result.replaceAll(" ", ""); //$NON-NLS-1$  //$NON-NLS-2$
		}
		return cutString(result, MAX_INFO_LENGTH);
	}

	public static String getDownloadSourceInfo() {
		String result = null;
		InputStream inputStream;
		try {
			inputStream = FileLocator.openStream(Activator.getDefault().getBundle(),
					new Path("plugin.properties"), false); //$NON-NLS-1$
			final Properties prop = new Properties();
			prop.load(inputStream);
			result = prop.getProperty("download.source"); //$NON-NLS-1$
		} catch (final IOException e) {
		}
		if (result == null || "".equals(result)) { //$NON-NLS-1$
			result = "Unknown"; //$NON-NLS-1$
		}
		return cutString(result, MAX_INFO_LENGTH);
	}

	private static String cutString(final String string, final int maxLength) {
		if (string.length() > maxLength) {
			return string.substring(0, maxLength);
		}
		return string;
	}
}
