/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.util;

import java.util.List;

import org.eclipse.draw2d.TextUtilities;
import org.eclipse.swt.widgets.Button;

public final class LayoutUtil {

	private LayoutUtil() {
	}

	public static int determineMaxButtonWidth(final List<Button> buttons) {
		if (buttons == null || buttons.isEmpty()) {
			throw new IllegalArgumentException();
		}

		int result = 0;
		for (final Button button : buttons) {
			final int width = TextUtilities.INSTANCE.getTextExtents(button.getText(), button.getFont()).width;
			if (width > result) {
				result = width;
			}
		}
		return result + 30;
	}

	public static int determineMinButtonWidth(final List<Button> buttons) {
		if (buttons == null || buttons.isEmpty()) {
			throw new IllegalArgumentException();
		}

		int result = Integer.MAX_VALUE;
		for (final Button button : buttons) {
			final int width = TextUtilities.INSTANCE.getTextExtents(button.getText(), button.getFont()).width;
			if (width < result && width > 0) {
				result = width;
			}
		}
		return result + 30;
	}
}
