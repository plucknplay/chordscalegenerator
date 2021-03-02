/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.figures;

import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.jface.preference.IPreferenceStore;

import com.plucknplay.csg.core.model.Note;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.UIConstants;
import com.plucknplay.csg.ui.model.KeyboardDraft;
import com.plucknplay.csg.ui.util.TooltipUtil;
import com.plucknplay.csg.ui.util.enums.KeySizeMode;

public class KeyboardKeysLayer extends AbstractKeyboardLayer {

	private final XYLayout contentsLayout;

	private HashMap<Note, KeyFigure> keyMap;

	public KeyboardKeysLayer(final KeyboardDraft keyboardDraft) {
		super(keyboardDraft);

		contentsLayout = new XYLayout();
		setLayoutManager(contentsLayout);
		init();
	}

	@Override
	protected void init() {
		removeAll();

		final KeySizeMode keySizeMode = KeySizeMode.valueOf(Activator.getDefault().getPreferenceStore()
				.getString(Preferences.KEYBOARD_VIEW_KEY_SIZE));

		keyMap = new HashMap<Note, KeyFigure>();

		final int x0 = IFigureConstants.KEYBOARD_OFFSET_X;
		final int y0 = IFigureConstants.KEYBOARD_OFFSET_Y;
		final int x1 = IFigureConstants.KEYBOARD_OFFSET_X + IFigureConstants.WHITE_KEY_WIDTH
				- IFigureConstants.BLACK_KEY_WIDTH / 2;

		// (1) create white keys
		int i = 0;
		for (final Iterator<Note> iter = toneRangeIterator(); iter.hasNext();) {
			final Note note = iter.next();
			if (note.hasAccidental()) {
				continue;
			}

			final KeyFigure keyFigure = createKeyFigure(note);
			contentsLayout.setConstraint(keyFigure,
					new Rectangle(x0 + i * IFigureConstants.WHITE_KEY_WIDTH, y0, IFigureConstants.WHITE_KEY_WIDTH + 1,
							keySizeMode.getWhiteKeyHeight(getKeyboardDraft().getKeyNumber())));
			i++;
		}

		// (2) create black keys
		// This has to be done separately since black key figures have to be
		// atop of the white ones.
		i = -1;
		for (final Iterator<Note> iter = toneRangeIterator(); iter.hasNext();) {
			final Note note = iter.next();
			if (!note.hasAccidental()) {
				i++;
				continue;
			}

			final KeyFigure keyFigure = createKeyFigure(note);
			contentsLayout
					.setConstraint(
							keyFigure,
							new Rectangle(x1 + i * IFigureConstants.WHITE_KEY_WIDTH, y0,
									IFigureConstants.BLACK_KEY_WIDTH, keySizeMode.getBlackKeyHeight(getKeyboardDraft()
											.getKeyNumber())));
		}
	}

	private KeyFigure createKeyFigure(final Note note) {
		final KeyFigure keyFigure = new KeyFigure(note, !note.hasAccidental());
		keyFigure.setToolTip(TooltipUtil.getToolTipLabel(note, !showOnlyRelativeNotes()));
		keyMap.put(note, keyFigure);
		add(keyFigure);
		return keyFigure;
	}

	@Override
	protected void drawInput() {

		for (final KeyFigure figure : keyMap.values()) {
			figure.resetBackgroundColor(getKeyboardDraft().isEditable());
		}

		final IPreferenceStore prefs = Activator.getDefault().getPreferenceStore();
		if (prefs.getBoolean(Preferences.SHOW_BLOCKS)
				&& UIConstants.BLOCK_OVERLAY.equals(prefs.getString(Preferences.KEYBOARD_VIEW_BLOCK_PRESENTATION))
				&& getKeyboardDraft().isBlock() && !getKeyboardDraft().isEditable()
				&& !getKeyboardDraft().isModifiedInput()) {

			for (final Note note : getKeyboardDraft().getAbsoluteNotes()) {
				final KeyFigure keyFigure = keyMap.get(note);
				if (keyFigure != null) {
					keyFigure.setBlockColor();
					continue;
				}
			}
		}
	}
}
