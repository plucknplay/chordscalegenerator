/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.util;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

import com.plucknplay.csg.core.model.FretboardPosition;
import com.plucknplay.csg.core.model.Instrument;
import com.plucknplay.csg.core.model.sets.InstrumentList;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.figures.IFigureConstants;
import com.plucknplay.csg.ui.model.TabDraft;

/**
 * Utility class which offers some useful static method for the tab view.
 */
public final class TabViewUtil {

	private static Color color;

	private TabViewUtil() {
	}

	public static int getTabImageWidth() {
		final int s = getStringCount();
		return (int) (12 * (s - 1) * (-0.111 * s + 1.666));
	}

	public static int getColomnOffsetX() {
		return (int) (1.1 * getStringCount() + 5.8);
	}

	public static int getColumnSpacing() {
		return (int) (2.8 * getStringCount() + 26.9);
	}

	private static int getStringCount() {
		if (InstrumentList.getInstance().getCurrentInstrument() == null) {
			return 0;
		}
		return InstrumentList.getInstance().getCurrentInstrument().getStringCount();
	}

	public static void setTooltip(final Label label, final FretboardPosition fbp) {
		if (label == null) {
			return;
		}
		if (fbp == null || fbp.getFret() == -1 || InstrumentList.getInstance().getCurrentInstrument() == null) {
			label.setToolTip(null);
			return;
		}

		Label tooltipLabel = (Label) label.getToolTip();
		if (tooltipLabel == null) {
			tooltipLabel = new Label();
			tooltipLabel.setBackgroundColor(IFigureConstants.TOOLTIP_YELLOW);
			label.setToolTip(tooltipLabel);
		}
		tooltipLabel.setText(" " + getNotesText(fbp) + " "); //$NON-NLS-1$ //$NON-NLS-2$
	}

	private static String getNotesText(final FretboardPosition fbp) {
		return new NotesLabelProvider(true).getText(InstrumentList.getInstance().getCurrentInstrument().getNote(fbp));
	}

	public static void updateRootNote(final TabDraft tabDraft, final Label label, final FretboardPosition fbp) {
		if (tabDraft == null || label == null) {
			return;
		}

		final IPreferenceStore prefs = Activator.getDefault().getPreferenceStore();
		final Instrument currentInstrument = InstrumentList.getInstance().getCurrentInstrument();

		final boolean highlightedRootNote = (prefs.getBoolean(Preferences.TAB_VIEW_HIGHLIGHT_ROOT_NOTE) || tabDraft
				.isEditable())
				&& fbp != null
				&& fbp.getFret() >= 0
				&& currentInstrument != null
				&& tabDraft.isRootNote(currentInstrument.getNote(fbp));

		label.setFont(FontManager.getFont(
				null,
				label,
				"24", -1, IFigureConstants.MAX_TAB_TEXT_HEIGHT, //$NON-NLS-1$
				highlightedRootNote && prefs.getBoolean(Preferences.TAB_VIEW_HIGHLIGHT_ROOT_NOTE_WITH_BOLD_FONT) ? SWT.BOLD
						: SWT.NORMAL));
		label.setBorder(highlightedRootNote && prefs.getBoolean(Preferences.TAB_VIEW_HIGHLIGHT_ROOT_NOTE_WITH_FRAME) ? new LineBorder()
				: null);
		label.setForegroundColor(highlightedRootNote
				&& prefs.getBoolean(Preferences.TAB_VIEW_HIGHLIGHT_ROOT_NOTE_WITH_COLOR) ? getColor(RGBUtil
				.convertStringToRGB(prefs.getString(Preferences.TAB_VIEW_HIGHLIGHT_ROOT_NOTE_COLOR_ID)))
				: ColorConstants.black);
	}

	private static Color getColor(final RGB rgb) {
		if (color != null && color.getRGB().equals(rgb)) {
			return color;
		}
		if (color != null) {
			color.dispose();
		}
		color = new Color(null, rgb);
		return color;
	}
}
