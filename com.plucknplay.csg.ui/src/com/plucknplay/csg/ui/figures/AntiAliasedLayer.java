/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.figures;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Layer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;

import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.Preferences;

public class AntiAliasedLayer extends Layer {

	private boolean antiAliasing = true;

	@Override
	public void paint(final Graphics graphics) {

		final IPreferenceStore prefs = Activator.getDefault().getPreferenceStore();
		final int antiAliasingMode = prefs.getInt(Preferences.GRAPHICS_ANTI_ALIASING_MODE);
		final int textAntiAliasingMode = prefs.getInt(Preferences.GRAPHICS_TEXT_ANTI_ALIASING_MODE);
		final int interpolationMode = prefs.getInt(Preferences.GRAPHICS_INTERPOLATION_MODE);

		graphics.setAntialias(antiAliasing ? antiAliasingMode : SWT.OFF);
		graphics.setTextAntialias(textAntiAliasingMode);
		graphics.setInterpolation(interpolationMode);

		super.paint(graphics);
	}

	protected void setAntiAliasing(final boolean antiAliasing) {
		this.antiAliasing = antiAliasing;
	}
}
