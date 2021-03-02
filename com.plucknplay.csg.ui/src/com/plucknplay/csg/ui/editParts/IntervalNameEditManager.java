/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.editParts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.tools.DirectEditManager;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;

import com.plucknplay.csg.core.model.Interval;
import com.plucknplay.csg.core.model.enums.IntervalNamesMode;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.figures.IFigureWithLabel;
import com.plucknplay.csg.ui.listeners.ISimpleChangeListener;
import com.plucknplay.csg.ui.model.BoxDraft;
import com.plucknplay.csg.ui.model.Draft;
import com.plucknplay.csg.ui.util.FontManager;

public class IntervalNameEditManager extends DirectEditManager {

	private boolean bringDown = true;

	private final double scale;
	private final IFigureWithLabel figure;
	private final Draft draft;

	private Font font;

	private final List<String> itemList;
	private final Interval interval;

	public IntervalNameEditManager(final GraphicalEditPart source, final IFigureWithLabel figure, final Draft draft,
			final double scale, final int alignment) {

		super(source, null, new LabelCellEditorLocator(figure.getMainLabel(), new Dimension((int) (90 * scale),
				(int) (30 * scale)), alignment));

		if (draft == null) {
			throw new IllegalArgumentException();
		}

		this.scale = scale;
		this.figure = figure;
		this.draft = draft;

		// determine combo box items
		itemList = new ArrayList<String>();
		final IntervalNamesMode intervalNamesMode = IntervalNamesMode.valueOf(Activator.getDefault()
				.getPreferenceStore().getString(Preferences.INTERVAL_NAMES_MODE));

		interval = intervalNamesMode.getInterval(figure.getText());
		if (interval != null) {

			final List<String> names = intervalNamesMode.getNames(interval, true);

			// remove empty strings
			for (final String name : names) {
				if (!"".equals(name)) {
					itemList.add(name);
				}
			}

			// sort list by interval numbers
			Collections.sort(itemList, new Comparator<String>() {
				@Override
				public int compare(final String s0, final String s1) {
					return getNumber(s0).compareTo(getNumber(s1));
				}

				private Integer getNumber(final String s) {
					final StringBuffer buf = new StringBuffer();
					for (int i = 0; i < s.length(); i++) {
						if (Character.isDigit(s.charAt(i))) {
							buf.append(s.charAt(i));
						}
					}
					return Integer.valueOf(buf.toString());
				}
			});

		} else {
			itemList.add(figure.getText());
		}
	}

	private class MyComboBoxCellEditor extends ComboBoxCellEditor implements ISimpleChangeListener {

		public MyComboBoxCellEditor(final Composite composite, final String[] items, final int style) {
			super(composite, items, style);
			draft.addSimpleChangeListener(this);
		}

		@Override
		protected void keyReleaseOccured(final KeyEvent e) {
			if (e.character == SWT.ESC || e.character == SWT.TAB) {
				applyValueAndDeactivate();
			} else {
				final String string = Character.toString(e.character);
				final String upperCase = string.toUpperCase();
				final String lowerCase = string.toLowerCase();

				for (int i = 0; i < itemList.size(); i++) {
					if (i == ((CCombo) getControl()).getSelectionIndex()) {
						continue;
					}
					final String item = itemList.get(i);
					if (item.contains(upperCase) || item.contains(lowerCase)) {
						setValue(i);
						return;
					}
				}
			}
		}

		@Override
		public void notifyChange(final Object property, final Object value) {
			if (BoxDraft.PROP_FRET_WIDTH_CHANGED.equals(property) || Draft.PROP_ROOT_NOTE_CHANGED.equals(property)) {
				fireCancelEditor();
			} else if (Draft.PROP_EDITABLE_STATE_CHANGED.equals(property)) {
				applyValueAndDeactivate();
			}
		}

		@Override
		public void dispose() {
			draft.removeSimpleChangeListener(this);
			super.dispose();
		}

		private void applyValueAndDeactivate() {
			fireApplyEditorValue();
			deactivate();
		}
	}

	@Override
	protected CellEditor createCellEditorOn(final Composite composite) {

		// create cell editor
		final ComboBoxCellEditor comboBoxCellEditor = new MyComboBoxCellEditor(composite,
				itemList.toArray(new String[itemList.size()]), SWT.READ_ONLY | SWT.DROP_DOWN);

		comboBoxCellEditor.addListener(new ICellEditorListener() {

			@Override
			public void applyEditorValue() {
				final Integer value = (Integer) ((ComboBoxCellEditor) getCellEditor()).getValue();
				if (value != null && value >= 0 && value < itemList.size()) {
					final String name = itemList.get(value);
					draft.setIntervalName(interval, name);
				}
			}

			@Override
			public void cancelEditor() {
			}

			@Override
			public void editorValueChanged(final boolean oldValidState, final boolean newValidState) {
			}
		});

		return comboBoxCellEditor;
	}

	@Override
	protected void initCellEditor() {

		final String text = figure.getText();
		if (text != null && itemList.contains(text)) {
			getCellEditor().setValue(itemList.indexOf(text));
		}
		final Font f = FontManager.getFont(null, figure.getMainLabel(), "bb13", //$NON-NLS-1$
				60, -1, SWT.NORMAL);
		font = new Font(f.getDevice(), f.getFontData()[0].getName(), (int) (scale * f.getFontData()[0].getHeight()),
				f.getFontData()[0].getStyle());
		getCellEditor().getControl().setFont(font);
	}

	@Override
	protected void bringDown() {
		if (bringDown) {
			bringDown = false;
			font.dispose();
			super.bringDown();
		}
	}
}
