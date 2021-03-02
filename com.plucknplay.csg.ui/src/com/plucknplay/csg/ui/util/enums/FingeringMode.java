/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.util.enums;

import org.eclipse.jface.preference.IPreferenceStore;

import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.ConstantMessages;
import com.plucknplay.csg.ui.Preferences;

public enum FingeringMode {

	NUMBERS_T('T', '1', '2', '3', '4', true), NUMBERS('0', '1', '2', '3', '4', true), PIMAQ('p', 'i', 'm', 'a', 'q',
			true), PIMAC('p', 'i', 'm', 'a', 'c', true), TIMAO('T', 'I', 'M', 'A', 'O', true), DZMRK('D', 'Z', 'M',
			'R', 'K', false), CUSTOM() {

		@Override
		public String getBeautifiedName(final boolean putThumbInBraces) {
			return ConstantMessages.Enum_fingering_mode_custom;
		}

		@Override
		public char getValue(final int fingerNumber) {
			final IPreferenceStore prefs = Activator.getDefault().getPreferenceStore();
			final String notation = prefs.getString(Preferences.GENERAL_FINGERING_MODE_CUSTOM_NOTATION);
			if (notation == null || notation.length() != 5) {
				return '?'; //$NON-NLS-1$
			}
			return notation.charAt(fingerNumber);
		}
	};

	private boolean common;

	private char thumb;
	private char indexFinger;
	private char middleFinger;
	private char ringFinger;
	private char littleFinger;

	private FingeringMode() {
		common = true;
	}

	private FingeringMode(final char thumb, final char indexFinger, final char middleFinger, final char ringFinger,
			final char littleFinger, final boolean common) {
		this.thumb = thumb;
		this.indexFinger = indexFinger;
		this.middleFinger = middleFinger;
		this.ringFinger = ringFinger;
		this.littleFinger = littleFinger;
		this.common = common;
	}

	public char getValue(final int fingerNumber) {
		return fingerNumber == 0 ? thumb : fingerNumber == 1 ? indexFinger : fingerNumber == 2 ? middleFinger
				: fingerNumber == 3 ? ringFinger : fingerNumber == 4 ? littleFinger : '?';
	}

	public String getBeautifiedName(final boolean putThumbInBraces) {
		final StringBuffer buf = new StringBuffer();
		if (putThumbInBraces) {
			buf.append("("); //$NON-NLS-1$
		}
		buf.append(thumb);
		if (putThumbInBraces) {
			buf.append(")"); //$NON-NLS-1$
		}
		buf.append(indexFinger);
		buf.append(middleFinger);
		buf.append(ringFinger);
		buf.append(littleFinger);
		return buf.toString();
	}

	public boolean isCommon() {
		return common;
	}
}
