/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui;

import org.eclipse.osgi.util.NLS;

public final class ConstantMessages extends NLS {

	public static String Constants_level_easy;

	public static String Constants_level_hard;

	public static String Constants_level_hell;

	public static String Constants_level_medium;

	public static String Constants_level_very_hard;

	public static String Enum_fingering_mode_custom;

	public static String PreferenceInitializer_default_b_name;

	public static String PreferenceInitializer_default_h_name;

	static {
		NLS.initializeMessages("com.plucknplay.csg.ui.ConstantMessages", ConstantMessages.class);
	}

	private ConstantMessages() {
	}
}
