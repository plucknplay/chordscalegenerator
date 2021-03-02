/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.util;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.draw2d.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.PlatformUI;

public final class FontManager {

	public static final Map<Display, Map<String, Font>> FONT_MAP = new HashMap<Display, Map<String, Font>>();

	private static final String BIG_REFERENCE_NAME = "Gb";
	private static final String SMALL_REFERENCE_NAME = "G#/Ab";

	private FontManager() {
	}

	/**
	 * Returns the font for the given label and restrictions.
	 * 
	 * @param display
	 *            the display
	 * @param label
	 *            the label the name shall be displayed on
	 * @param referenceText
	 *            a reference text
	 * @param maxWidth
	 *            the maximum width in pixels or -1 if this check shall not be
	 *            applied
	 * @param maxHeight
	 *            the maximum hight in pixels or -1 if this check shall not be
	 *            applied
	 * @param fontStyle
	 *            the style of the font, for example SWT.BOLD, SWT.NORMAL or
	 *            SWT.ITALIC
	 * @return the font
	 */
	public static Font getFont(final Display display, final Label label, final String referenceText,
			final int maxWidth, final int maxHeight, final int fontStyle) {
		if (label == null) {
			throw new IllegalArgumentException("Label must not be null.");
		}

		final int textLength = (referenceText != null ? referenceText : label.getText()).length();
		final String key = (textLength <= 2 ? "BIG" : "SMALL") + "_WIDTH_" + maxWidth + "_HEIGHT_" + maxHeight
				+ "_STYLE_" + fontStyle;
		final String reference = referenceText != null ? referenceText : textLength <= 2 ? BIG_REFERENCE_NAME
				: SMALL_REFERENCE_NAME;

		final Map<String, Font> fontRegistry = getFontRegistry(display);

		Font theFont = null;

		theFont = fontRegistry.get(key);

		if (theFont == null) {

			for (int i = 7; i <= 100; i++) {
				final Font font = new Font(null, "Arial", i, fontStyle); //$NON-NLS-1$
				if (maxWidth != -1 && label.getTextUtilities().getStringExtents(reference, font).width > maxWidth
						|| maxHeight != -1
						&& label.getTextUtilities().getStringExtents(reference, font).height > maxHeight) {
					font.dispose();
					theFont = new Font(null, "Arial", i - 1, fontStyle); //$NON-NLS-1$
					fontRegistry.put(key, theFont);
					break;
				}
				font.dispose();
			}
		}

		return theFont;
	}

	private static Map<String, Font> getFontRegistry(final Display display) {

		Display theDisplay = display;
		if (theDisplay == null) {
			theDisplay = PlatformUI.getWorkbench().getDisplay();
		}

		Map<String, Font> fontRegistry = FONT_MAP.get(theDisplay);
		if (fontRegistry == null) {
			fontRegistry = new HashMap<String, Font>();
			FONT_MAP.put(theDisplay, fontRegistry);

			// add dispose listener when internal font registry is created for
			// the first time
			final Display d = theDisplay;
			d.addListener(SWT.Dispose, new Listener() {
				@Override
				public void handleEvent(final Event event) {
					for (final Font f : FONT_MAP.get(d).values()) {
						f.dispose();
					}
				}
			});
		}

		return fontRegistry;
	}
}
