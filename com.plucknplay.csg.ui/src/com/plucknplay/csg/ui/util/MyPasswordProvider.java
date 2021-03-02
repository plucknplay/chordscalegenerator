/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.util;

import javax.crypto.spec.PBEKeySpec;

import org.eclipse.equinox.security.storage.provider.IPreferencesContainer;
import org.eclipse.equinox.security.storage.provider.PasswordProvider;

public class MyPasswordProvider extends PasswordProvider {

	private static final String PASSWORD = "kJHe3-80G#Dnef123*j"; //$NON-NLS-1$

	@Override
	public PBEKeySpec getPassword(final IPreferencesContainer container, final int passwordType) {
		// create master password
		char[] thePassword = PASSWORD.toCharArray();
		try {
			final String user = System.getProperty("user.name"); //$NON-NLS-1$
			if (user != null) {
				thePassword = (PASSWORD + user).toCharArray();
			}
		} catch (final SecurityException e) {
		}
		return new PBEKeySpec(thePassword);
	}
}
